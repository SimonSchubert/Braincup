package com.inspiredandroid.braincup.app

import androidx.navigation.NavController
import braincup.composeapp.generated.resources.Res
import com.inspiredandroid.braincup.api.PlayGamesBridge
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.games.*
import com.inspiredandroid.braincup.games.minichess.ChessAi
import com.inspiredandroid.braincup.games.wordle.WordleGame
import com.inspiredandroid.braincup.games.wordle.WordleLanguage
import com.inspiredandroid.braincup.games.wordle.WordleLanguages
import com.inspiredandroid.braincup.games.wordle.deviceLanguageTag
import com.inspiredandroid.braincup.normalchess.NormalChessDifficulty
import com.inspiredandroid.braincup.normalchess.NormalChessMode
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class GameController(
    private val navController: NavController,
    val storage: UserStorage = UserStorage(),
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _gameState = MutableStateFlow<GameState>(GameState.Idle)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _timeRemaining = MutableStateFlow(GAME_TIME_MILLIS)
    val timeRemaining: StateFlow<Long> = _timeRemaining.asStateFlow()

    private val _elapsedTime = MutableStateFlow(0L)
    val elapsedTime: StateFlow<Long> = _elapsedTime.asStateFlow()

    /**
     * Lightweight Orbit Tracker ball positions updated every animation frame during MOVING.
     * Phase / selection / feedback still flow through [gameUiState] so the full game tree does
     * not recompose at 60fps.
     */
    private val _orbitBallPositions = MutableStateFlow<List<Pair<Float, Float>>>(emptyList())
    val orbitBallPositions: StateFlow<List<Pair<Float, Float>>> = _orbitBallPositions.asStateFlow()

    private val _gameUiState = MutableStateFlow<GameUiState?>(null)
    val gameUiState: StateFlow<GameUiState?> = _gameUiState.asStateFlow()

    private val _sessionState = MutableStateFlow<UserStorage.SessionState?>(null)
    val sessionState: StateFlow<UserStorage.SessionState?> = _sessionState.asStateFlow()

    private val _sessionStreak = MutableStateFlow(0)
    val sessionStreak: StateFlow<Int> = _sessionStreak.asStateFlow()

    private val _lastCompletedSession = MutableStateFlow<SessionResult?>(null)
    val lastCompletedSession: StateFlow<SessionResult?> = _lastCompletedSession.asStateFlow()

    private val _intermediateCorrectEvents = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val intermediateCorrectEvents: SharedFlow<Unit> = _intermediateCorrectEvents.asSharedFlow()

    private var startTime = 0L
    private var points = 0
    /** Adaptive start round for the current session; used as difficulty bonus on finish. */
    private var sessionStartRound = 0
    private var stopwatchRunning = false
    private var inSessionMode = false
    val isInSessionMode: Boolean get() = inSessionMode
    private var miniChessAiJob: Job? = null
    private var flagsTimerJob: Job? = null
    private var timerJob: Job? = null
    private var stopwatchJob: Job? = null

    private var timersPaused = false
    private var pausedTimerKind: TimerKind? = null
    private var pausedRemainingMillis: Long = 0L
    private var pausedElapsedMillis: Long = 0L

    private enum class TimerKind { GAME_COUNTDOWN, FLAGS_COUNTDOWN, STOPWATCH }

    private data class WordleWordLists(val answers: List<String>, val guesses: Set<String>)

    /** Wordle word lists keyed by language tag; each bundled file is read at most once. */
    private val wordListCache = mutableMapOf<String, WordleWordLists>()
    private var wordleScoreRecorded = false

    private val _totalXp = MutableStateFlow(0)
    val totalXp: StateFlow<Int> = _totalXp.asStateFlow()

    private val _highscores = MutableStateFlow<Map<String, Int>>(emptyMap())
    val highscores: StateFlow<Map<String, Int>> = _highscores.asStateFlow()

    private val _unlockedAchievementCount = MutableStateFlow(0)
    val unlockedAchievementCount: StateFlow<Int> = _unlockedAchievementCount.asStateFlow()

    data class SessionResult(
        val gameIds: List<String>,
        val scores: List<Int>,
        val streakBefore: Int,
        val streakAfter: Int,
        val xpGained: Int,
        val totalXpAfter: Int,
        val levelChange: UserStorage.LevelChange?,
    )

    companion object {
        const val GAME_TIME_MILLIS = 60 * 1_000L
        const val FLAGS_ROUND_TIME_MILLIS = FlagsGame.ROUND_TIME_MILLIS

        // Sentinel inputs the Wordle keyboard sends through the shared onAnswer(String) channel;
        // anything else is treated as a single typed letter.
        const val WORDLE_ENTER = "ENTER"
        const val WORDLE_DELETE = "DEL"
        private const val WORDLE_CLEAR_PREFIX = "CLEAR"

        fun wordleClearAt(index: Int): String = "$WORDLE_CLEAR_PREFIX$index"
    }

    init {
        storage.migrateStreakIfNeeded()
        _sessionStreak.value = storage.getSessionStreak()
        _sessionState.value = storage.getOrCreateTodaySession { generateSessionGameIds() }
        _totalXp.value = storage.getTotalXp()
        refreshDerivedStorageState()
        PlayGamesBridge.onTotalXpRestored = { restored -> _totalXp.value = restored }
    }

    private fun refreshDerivedStorageState() {
        _highscores.value = GameType.entries.associate { it.id to storage.getHighScore(it.id) }
        _unlockedAchievementCount.value = storage.getUnlockedAchievements().size
    }

    fun dispose() {
        PlayGamesBridge.onTotalXpRestored = null
        scope.cancel()
    }

    private fun generateSessionGameIds(): List<String> {
        val eligibleByCategory = GameType.entries
            .filterNot {
                it == GameType.LIGHTS_OUT ||
                    it == GameType.SLIDING_PUZZLE ||
                    it == GameType.SHIKAKU ||
                    it == GameType.NURIKABE ||
                    it == GameType.CAT_QUEENS ||
                    it == GameType.KNOT ||
                    it == GameType.SOLO_CHESS ||
                    it == GameType.MINI_CHESS ||
                    it == GameType.WORDLE
            }
            .filterNot { storage.isColorblindPaletteEnabled() && it.requiresColorVision }
            .groupBy { it.category.name } // one bucket per GameCategory
            .mapValues { (_, games) -> games.map { it.id } }
        // Draw one game per category via a per-category shuffle bag so games rotate without
        // repeating until the category's pool is exhausted; then randomize the play order.
        return storage.drawDailySessionGameIds(eligibleByCategory).shuffled()
    }

    fun navigateToMainMenu() {
        val currentState = _gameState.value
        if (currentState is GameState.Active) {
            (currentState.game as? VisualMemoryGame)?.cancelCountdown()
            (currentState.game as? SpotTheNewGame)?.cancelCountdown()
            (currentState.game as? GhostGridGame)?.cancelShowSequence()
            (currentState.game as? OrbitTrackerGame)?.cancelAnimation()
            (currentState.game as? DigitMemoryGame)?.cancelShowing()
            if (currentState.game is MiniChessGame) cancelMiniChessAi()
            if (currentState.game is FlagsGame) cancelFlagsTimer()
        }
        timersPaused = false
        pausedTimerKind = null
        stopwatchRunning = false
        stopwatchJob?.cancel()
        stopwatchJob = null
        cancelTimer()
        _gameUiState.value = null
        _gameState.value = GameState.Idle
        inSessionMode = false
        _sessionState.value = storage.getOrCreateTodaySession { generateSessionGameIds() }
        navController.popBackStack(MainMenu, inclusive = false)
    }

    fun startDailySession() {
        if (storage.isSessionCompletedToday()) return
        val session = storage.getOrCreateTodaySession { generateSessionGameIds() }
        _sessionState.value = session
        inSessionMode = true
        navController.navigate(SessionInterstitial)
    }

    fun playNextSessionGame() {
        val session = _sessionState.value ?: return
        val gameId = session.gameIds.getOrNull(session.currentIndex) ?: return
        val gameType = getGameTypeById(gameId) ?: return
        startGame(gameType)
    }

    fun navigateToInstructions(gameType: GameType) {
        navController.navigate(Instructions(gameType.id))
    }

    fun showLeaderboard(gameType: GameType) {
        PlayGamesBridge.onShowLeaderboard?.invoke(gameType)
    }

    fun showBrainCup() {
        PlayGamesBridge.onShowBrainCup?.invoke()
    }

    fun navigateToScoreboard(gameType: GameType) {
        navController.navigate(Scoreboard(gameType.id))
    }

    fun navigateToAchievements() {
        navController.navigate(Achievements)
    }

    fun navigateToSettings() {
        navController.navigate(Settings)
    }

    fun navigateToNormalSudokuMenu() {
        navController.navigate(NormalSudokuMenu)
    }

    fun navigateToNormalSudokuPlay(puzzleId: String) {
        navController.navigate(NormalSudokuPlay(puzzleId))
    }

    fun navigateToNormalChessMenu() {
        navController.navigate(NormalChessMenu)
    }

    fun navigateToNormalChessPlay(mode: NormalChessMode, difficulty: NormalChessDifficulty) {
        navController.navigate(NormalChessPlay(mode = mode.name, difficulty = difficulty.name))
    }

    fun navigateToMatchstickRiddlesMenu() {
        navController.navigate(MatchstickRiddlesMenu)
    }

    fun navigateToMatchstickRiddlesPlay(riddleId: String) {
        navController.navigate(MatchstickRiddlesPlay(riddleId))
    }

    fun startGame(gameType: GameType) {
        points = 0
        sessionStartRound = 0

        // Visual Memory and Ghost Grid have special handling (no timer, round-based)
        if (gameType == GameType.VISUAL_MEMORY) {
            startVisualMemoryGame(gameType)
            return
        }
        if (gameType == GameType.GHOST_GRID) {
            startGhostGridGame(gameType)
            return
        }
        if (gameType == GameType.ORBIT_TRACKER) {
            startOrbitTrackerGame(gameType)
            return
        }
        if (gameType == GameType.SCHULTE_TABLE) {
            startSchulteTableGame(gameType)
            return
        }
        if (gameType == GameType.MINI_CHESS) {
            startMiniChessGame(gameType)
            return
        }
        if (gameType == GameType.LIGHTS_OUT) {
            startLightsOutGame(gameType)
            return
        }
        if (gameType == GameType.SLIDING_PUZZLE) {
            startSlidingPuzzleGame(gameType)
            return
        }
        if (gameType == GameType.SHIKAKU) {
            startShikakuGame(gameType)
            return
        }
        if (gameType == GameType.NURIKABE) {
            startNurikabeGame(gameType)
            return
        }
        if (gameType == GameType.CAT_QUEENS) {
            startCatQueensGame(gameType)
            return
        }
        if (gameType == GameType.KNOT) {
            startKnotGame(gameType)
            return
        }
        if (gameType == GameType.SOLO_CHESS) {
            startSoloChessGame(gameType)
            return
        }
        if (gameType == GameType.FLAGS) {
            startFlagsGame(gameType)
            return
        }
        if (gameType == GameType.DIGIT_MEMORY) {
            startDigitMemoryGame(gameType)
            return
        }
        if (gameType == GameType.SPOT_THE_NEW) {
            startSpotTheNewGame(gameType)
            return
        }
        if (gameType == GameType.WORDLE) {
            startWordleGame(gameType)
            return
        }

        startTime = Clock.System.now().toEpochMilliseconds()
        _timeRemaining.value = GAME_TIME_MILLIS

        val game = createGame(gameType)
        if (game.adaptiveDifficulty) {
            sessionStartRound = storage.getLastRound(gameType.id)
            game.round = sessionStartRound
        }
        game.nextRound()

        _gameState.value = GameState.Active(gameType, game)
        _gameUiState.value = game.toUiState()
        navController.navigate(Playing(gameType.id))
        startTimer()
    }

    fun submitAnswer(answer: String) {
        val currentState = _gameState.value
        if (currentState !is GameState.Active) return

        val game = currentState.game
        if (game is VisualMemoryGame) {
            handleVisualMemoryAnswer(game, answer)
            return
        }
        if (game is GhostGridGame) {
            handleGhostGridAnswer(currentState, game, answer)
            return
        }
        if (game is AnomalyPuzzleGame) {
            handleAnomalyPuzzleAnswer(currentState, game, answer.trim())
            return
        }
        if (game is PatternSequenceGame) {
            handlePatternSequenceAnswer(currentState, game, answer.trim())
            return
        }
        if (game is PathFinderGame) {
            handlePathFinderAnswer(currentState, game, answer.trim())
            return
        }
        if (game is ColoredShapesGame) {
            handleColoredShapesAnswer(currentState, game, answer.trim())
            return
        }
        if (game is ColorConfusionGame) {
            handleColorConfusionAnswer(currentState, game, answer.trim())
            return
        }
        if (game is OrbitTrackerGame) {
            handleOrbitTrackerAnswer(currentState, game, answer.trim())
            return
        }
        if (game is FlashCrowdGame) {
            handleFlashCrowdAnswer(currentState, game, answer.trim())
            return
        }
        if (game is MiniSudokuGame) {
            handleMiniSudokuAnswer(currentState, game, answer.trim())
            return
        }
        if (game is WordleGame) {
            handleWordleAnswer(currentState, game, answer)
            return
        }
        if (game is LightsOutGame) {
            handleLightsOutAnswer(currentState, game, answer.trim())
            return
        }
        if (game is SlidingPuzzleGame) {
            handleSlidingPuzzleAnswer(currentState, game, answer.trim())
            return
        }
        if (game is ShikakuGame) {
            handleShikakuAnswer(currentState, game, answer.trim())
            return
        }
        if (game is NurikabeGame) {
            handleNurikabeAnswer(currentState, game, answer.trim())
            return
        }
        if (game is CatQueensGame) {
            handleCatQueensAnswer(currentState, game, answer.trim())
            return
        }
        if (game is KnotGame) {
            handleKnotAnswer(currentState, game, answer.trim())
            return
        }
        if (game is SoloChessGame) {
            handleSoloChessAnswer(currentState, game, answer.trim())
            return
        }
        if (game is SchulteTableGame) {
            handleSchulteTableAnswer(currentState, game, answer.trim())
            return
        }
        if (game is ValueComparisonGame) {
            handleValueComparisonAnswer(currentState, game, answer.trim())
            return
        }
        if (game is MiniChessGame) {
            handleMiniChessAnswer(currentState, game, answer.trim())
            return
        }
        if (game is FlagsGame) {
            handleFlagsAnswer(currentState, game, answer.trim())
            return
        }
        if (game is DigitMemoryGame) {
            handleDigitMemoryAnswer(currentState, game, answer.trim())
            return
        }
        if (game is SpotTheNewGame) {
            handleSpotTheNewAnswer(game, answer.trim())
            return
        }

        val input = answer.trim()
        val isCorrect = game.isCorrect(input)

        if (isCorrect) {
            points++
            _gameState.value = GameState.Feedback(
                gameType = currentState.gameType,
                game = game,
                isCorrect = true,
                message = game.hint()?.let { FeedbackMessage.Plain(it) },
            )
        } else {
            game.answeredAllCorrect = false
            _gameState.value = GameState.Feedback(
                gameType = currentState.gameType,
                game = game,
                isCorrect = false,
                message = game.solutionMessage(),
            )
        }

        scope.launch {
            delay(1.seconds)
            proceedAfterFeedback()
        }
    }

    fun giveUp() {
        val currentState = _gameState.value
        if (currentState !is GameState.Active) return

        val game = currentState.game
        game.answeredAllCorrect = false

        if (game is MiniChessGame) {
            cancelMiniChessAi()
            game.markGiveUp()
            finishCurrentGame(currentState.gameType, game)
            return
        }
        if (game is WordleGame) {
            game.giveUp()
            points = 0
            _gameUiState.value = game.toUiState()
            recordWordleScore(currentState.gameType)
            return
        }
        if (game is LightsOutGame) {
            points = 0
            finishCurrentGame(currentState.gameType, game)
            return
        }
        if (game is SlidingPuzzleGame) {
            points = 0
            finishCurrentGame(currentState.gameType, game)
            return
        }
        if (game is ShikakuGame) {
            points = 0
            finishCurrentGame(currentState.gameType, game)
            return
        }
        if (game is NurikabeGame) {
            points = 0
            finishCurrentGame(currentState.gameType, game)
            return
        }
        if (game is CatQueensGame) {
            points = 0
            finishCurrentGame(currentState.gameType, game)
            return
        }
        if (game is KnotGame) {
            points = 0
            finishCurrentGame(currentState.gameType, game)
            return
        }
        if (game is SoloChessGame) {
            points = 0
            finishCurrentGame(currentState.gameType, game)
            return
        }

        if (game is SherlockCalculationGame) {
            val currentUiState = _gameUiState.value as? SherlockCalculationUiState ?: return
            _gameUiState.value = currentUiState.copy(solutionTokens = game.solutionTokens.toImmutableList())
            scope.launch {
                delay(1.seconds)
                proceedAfterInlineFeedback(currentState.gameType, game)
            }
            return
        }

        _gameState.value = GameState.Feedback(
            gameType = currentState.gameType,
            game = game,
            isCorrect = false,
            message = game.solutionMessage(),
        )

        scope.launch {
            delay(1.seconds)
            proceedAfterFeedback()
        }
    }

    private fun proceedAfterFeedback() {
        val currentState = _gameState.value
        if (currentState !is GameState.Feedback) return

        val game = currentState.game
        val currentTime = Clock.System.now().toEpochMilliseconds()
        val elapsed = currentTime - startTime

        if (elapsed > GAME_TIME_MILLIS) {
            finishGame(currentState.gameType, game)
        } else {
            game.nextRound()
            _gameState.value = GameState.Active(currentState.gameType, game)
            _gameUiState.value = game.toUiState()
        }
    }

    private fun finishGame(gameType: GameType, game: Game) {
        if (game.answeredAllCorrect) {
            points++
        }
        finishCurrentGame(gameType, game)
    }

    fun playRandomGame() {
        val randomGame = GameType.entries
            .filterNot { storage.isColorblindPaletteEnabled() && it.requiresColorVision }
            .filterNot { it == GameType.WORDLE && !WordleLanguages.isAvailable() }
            .random()
        navigateToInstructions(randomGame)
    }

    fun playAgain(gameType: GameType) {
        // Skip the instructions screen and jump straight back into the game. For level-based
        // games this starts the next level (getLastRound was bumped on solve); after a give-up
        // it replays the same level.
        startGame(gameType)
    }

    /** Play Again from the Wordle result screen, or Continue during a daily challenge. */
    fun wordleFinishedAction() {
        if (inSessionMode) {
            continueWordleInDailyChallenge()
        } else {
            restartWordleInPlace()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = scope.launch {
            while (true) {
                val currentTime = Clock.System.now().toEpochMilliseconds()
                val elapsed = currentTime - startTime
                val remaining = (GAME_TIME_MILLIS - elapsed).coerceAtLeast(0)
                _timeRemaining.value = remaining

                if (remaining <= 0) break
                delay(100.milliseconds)
            }
        }
    }

    private fun cancelTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    /** Pauses countdown/stopwatch timers and in-game memorize phases while a modal is open. */
    fun pauseTimers() {
        if (timersPaused) return
        val state = _gameState.value as? GameState.Active ?: return

        timersPaused = true
        when {
            flagsTimerJob != null -> {
                pausedTimerKind = TimerKind.FLAGS_COUNTDOWN
                pausedRemainingMillis = _timeRemaining.value
                cancelFlagsTimer()
            }
            stopwatchRunning -> {
                pausedTimerKind = TimerKind.STOPWATCH
                pausedElapsedMillis = _elapsedTime.value
                stopwatchRunning = false
                stopwatchJob?.cancel()
                stopwatchJob = null
            }
            timerJob != null -> {
                pausedTimerKind = TimerKind.GAME_COUNTDOWN
                pausedRemainingMillis = _timeRemaining.value
                cancelTimer()
            }
        }

        when (val game = state.game) {
            is VisualMemoryGame ->
                if (game.phase == VisualMemoryGame.Phase.MEMORIZING) game.pauseCountdown()
            is SpotTheNewGame ->
                if (game.phase == SpotTheNewGame.Phase.MEMORIZING) game.pauseCountdown()
            is DigitMemoryGame ->
                if (game.phase == DigitMemoryGame.Phase.SHOWING) game.pauseShowing()
        }
    }

    /** Resumes timers paused by [pauseTimers]; no-op if nothing was paused. */
    fun resumeTimers() {
        if (!timersPaused) return
        timersPaused = false
        val state = _gameState.value as? GameState.Active ?: return

        when (pausedTimerKind) {
            TimerKind.GAME_COUNTDOWN -> {
                startTime = Clock.System.now().toEpochMilliseconds() -
                    (GAME_TIME_MILLIS - pausedRemainingMillis)
                _timeRemaining.value = pausedRemainingMillis
                startTimer()
            }
            TimerKind.FLAGS_COUNTDOWN -> {
                val game = state.game as? FlagsGame ?: return
                startTime = Clock.System.now().toEpochMilliseconds() -
                    (FLAGS_ROUND_TIME_MILLIS - pausedRemainingMillis)
                _timeRemaining.value = pausedRemainingMillis
                startFlagsRoundTimer(state.gameType, game)
            }
            TimerKind.STOPWATCH -> {
                startTime = Clock.System.now().toEpochMilliseconds() - pausedElapsedMillis
                _elapsedTime.value = pausedElapsedMillis
                startStopwatch()
            }
            null -> Unit
        }
        pausedTimerKind = null

        when (val game = state.game) {
            is VisualMemoryGame ->
                if (game.phase == VisualMemoryGame.Phase.MEMORIZING) {
                    game.resumeCountdown(scope) { emitGameUiState(game) }
                }
            is SpotTheNewGame ->
                if (game.phase == SpotTheNewGame.Phase.MEMORIZING) {
                    game.resumeCountdown(scope) { _gameUiState.value = game.toUiState() }
                }
            is DigitMemoryGame ->
                if (game.phase == DigitMemoryGame.Phase.SHOWING) {
                    game.resumeShowing(scope) { _gameUiState.value = game.toUiState() }
                }
        }
    }

    private fun createGame(gameType: GameType): Game = when (gameType) {
        GameType.COLORED_SHAPES -> ColoredShapesGame()
        GameType.MENTAL_CALCULATION -> MentalCalculationGame()
        GameType.SHERLOCK_CALCULATION -> SherlockCalculationGame()
        GameType.CHAIN_CALCULATION -> ChainCalculationGame()
        GameType.VALUE_COMPARISON -> ValueComparisonGame()
        GameType.FRACTION_CALCULATION -> FractionCalculationGame()
        GameType.ANOMALY_PUZZLE -> AnomalyPuzzleGame()
        GameType.PATH_FINDER -> PathFinderGame()
        GameType.MINI_SUDOKU -> MiniSudokuGame()
        GameType.LIGHTS_OUT -> LightsOutGame()
        GameType.SLIDING_PUZZLE -> SlidingPuzzleGame()
        GameType.SHIKAKU -> ShikakuGame()
        GameType.NURIKABE -> NurikabeGame()
        GameType.CAT_QUEENS -> CatQueensGame()
        GameType.KNOT -> KnotGame()
        GameType.SOLO_CHESS -> SoloChessGame()
        GameType.SCHULTE_TABLE -> SchulteTableGame()
        GameType.VISUAL_MEMORY -> VisualMemoryGame()
        GameType.PATTERN_SEQUENCE -> PatternSequenceGame()
        GameType.GHOST_GRID -> GhostGridGame()
        GameType.COLOR_CONFUSION -> ColorConfusionGame()
        GameType.ORBIT_TRACKER -> OrbitTrackerGame()
        GameType.FLASH_CROWD -> FlashCrowdGame()
        GameType.MINI_CHESS -> MiniChessGame()
        GameType.FLAGS -> FlagsGame()
        GameType.DIGIT_MEMORY -> DigitMemoryGame()
        GameType.SPOT_THE_NEW -> SpotTheNewGame()
        // Wordle needs an async-loaded, locale-specific word list, so it is built in startWordleGame.
        GameType.WORDLE -> error("WordleGame is created in startWordleGame")
    }

    private fun startVisualMemoryGame(gameType: GameType) {
        val game = VisualMemoryGame()
        game.nextRound()

        _gameState.value = GameState.Active(gameType, game)
        navController.navigate(Playing(gameType.id))
        game.startCountdown(scope) { emitGameUiState(game) }
    }

    private fun startSpotTheNewGame(gameType: GameType) {
        val game = SpotTheNewGame()
        game.startMemorizing()

        _gameState.value = GameState.Active(gameType, game)
        _gameUiState.value = game.toUiState()
        navController.navigate(Playing(gameType.id))
        // Memorize phase runs for a fixed countdown, then the answering rounds begin.
        // No global timer: the game runs until a wrong tap.
        game.startMemorizeCountdown(scope) { _gameUiState.value = game.toUiState() }
    }

    private fun handleSpotTheNewAnswer(game: SpotTheNewGame, answer: String) {
        if (game.phase != SpotTheNewGame.Phase.ANSWERING) return
        when (game.submitAnswer(answer)) {
            SpotTheNewGame.SubmitResult.Correct -> {
                points++
                _gameUiState.value = game.toUiState()
                _intermediateCorrectEvents.tryEmit(Unit)
            }
            SpotTheNewGame.SubmitResult.PoolExhausted -> {
                // Player beat every unique combo; count the final tap and finish gracefully.
                points++
                finishCurrentGame(GameType.SPOT_THE_NEW, game)
            }
            SpotTheNewGame.SubmitResult.Wrong -> {
                _gameUiState.value = game.toUiState()
                scope.launch {
                    delay(2.seconds)
                    finishCurrentGame(GameType.SPOT_THE_NEW, game)
                }
            }
        }
    }

    private fun handleAnomalyPuzzleAnswer(
        currentState: GameState.Active,
        game: AnomalyPuzzleGame,
        input: String,
    ) {
        if (game.isCorrect(input)) {
            points++
            _gameState.value = GameState.Feedback(
                gameType = currentState.gameType,
                game = game,
                isCorrect = true,
                message = game.hint()?.let { FeedbackMessage.Plain(it) },
            )
            scope.launch {
                delay(1.seconds)
                proceedAfterFeedback()
            }
        } else {
            game.answeredAllCorrect = false
            val wrongIndex = input.toIntOrNull()?.minus(1)
            val currentUiState = _gameUiState.value as? AnomalyPuzzleUiState ?: return
            _gameUiState.value = currentUiState.copy(
                rows = currentUiState.rows.withFeedbackStates(wrongIndex, game.resultIndex, currentUiState.columnsPerRow),
            )
            scope.launch {
                delay(1.seconds)
                proceedAfterInlineFeedback(currentState.gameType, game)
            }
        }
    }

    private fun handlePatternSequenceAnswer(
        currentState: GameState.Active,
        game: PatternSequenceGame,
        input: String,
    ) {
        if (game.isCorrect(input)) {
            points++
            _gameState.value = GameState.Feedback(
                gameType = currentState.gameType,
                game = game,
                isCorrect = true,
                message = game.hint()?.let { FeedbackMessage.Plain(it) },
            )
            scope.launch {
                delay(1.seconds)
                proceedAfterFeedback()
            }
        } else {
            game.answeredAllCorrect = false
            val wrongIndex = input.toIntOrNull()
            val currentUiState = _gameUiState.value as? PatternSequenceUiState ?: return
            _gameUiState.value = currentUiState.copy(
                optionRows = currentUiState.optionRows.withFeedbackStates(wrongIndex, game.correctOptionIndex, 2),
            )
            scope.launch {
                delay(1.seconds)
                proceedAfterInlineFeedback(currentState.gameType, game)
            }
        }
    }

    private fun handlePathFinderAnswer(
        currentState: GameState.Active,
        game: PathFinderGame,
        input: String,
    ) {
        if (game.isCorrect(input)) {
            points++
            _gameState.value = GameState.Feedback(
                gameType = currentState.gameType,
                game = game,
                isCorrect = true,
                message = game.hint()?.let { FeedbackMessage.Plain(it) },
            )
            scope.launch {
                delay(1.seconds)
                proceedAfterFeedback()
            }
        } else {
            game.answeredAllCorrect = false
            val wrongIndex = input.toIntOrNull()?.minus(1)
            val currentUiState = _gameUiState.value as? PathFinderUiState ?: return
            _gameUiState.value = currentUiState.copy(
                grid = currentUiState.grid.withFeedbackStates(wrongIndex, game.correctIndex, 4),
            )
            scope.launch {
                delay(1.seconds)
                proceedAfterInlineFeedback(currentState.gameType, game)
            }
        }
    }

    private fun handleColoredShapesAnswer(
        currentState: GameState.Active,
        game: ColoredShapesGame,
        input: String,
    ) {
        if (game.isCorrect(input)) {
            points++
            _gameState.value = GameState.Feedback(
                gameType = currentState.gameType,
                game = game,
                isCorrect = true,
                message = game.hint()?.let { FeedbackMessage.Plain(it) },
            )
            scope.launch {
                delay(1.seconds)
                proceedAfterFeedback()
            }
        } else {
            game.answeredAllCorrect = false
            val correctAnswer = game.points()
            val currentUiState = _gameUiState.value as? ColoredShapesUiState ?: return
            _gameUiState.value = currentUiState.copy(
                possibleAnswers = currentUiState.possibleAnswers.map { button ->
                    button.copy(
                        state = when (button.value) {
                            input -> AnswerButtonState.WRONG
                            correctAnswer -> AnswerButtonState.CORRECT
                            else -> AnswerButtonState.DIMMED
                        },
                    )
                }.toImmutableList(),
            )
            scope.launch {
                delay(1.seconds)
                proceedAfterInlineFeedback(currentState.gameType, game)
            }
        }
    }

    private fun handleValueComparisonAnswer(
        currentState: GameState.Active,
        game: ValueComparisonGame,
        input: String,
    ) {
        if (game.isCorrect(input)) {
            points++
            _gameState.value = GameState.Feedback(
                gameType = currentState.gameType,
                game = game,
                isCorrect = true,
                message = game.hint()?.let { FeedbackMessage.Plain(it) },
            )
            scope.launch {
                delay(1.seconds)
                proceedAfterFeedback()
            }
        } else {
            game.answeredAllCorrect = false
            val selectedIndex = (input.toIntOrNull() ?: return) - 1
            val correctIndex = game.resultIndex
            val currentUiState = _gameUiState.value as? ValueComparisonUiState ?: return
            _gameUiState.value = currentUiState.copy(
                answers = currentUiState.answers.mapIndexed { i, button ->
                    button.copy(
                        state = when (i) {
                            selectedIndex -> AnswerButtonState.WRONG
                            correctIndex -> AnswerButtonState.CORRECT
                            else -> AnswerButtonState.DIMMED
                        },
                    )
                }.toImmutableList(),
            )
            scope.launch {
                delay(1.seconds)
                proceedAfterInlineFeedback(currentState.gameType, game)
            }
        }
    }

    private fun handleColorConfusionAnswer(
        currentState: GameState.Active,
        game: ColorConfusionGame,
        input: String,
    ) {
        if (input == "submit") {
            val correct = game.submit()
            _gameUiState.value = game.toUiState()
            if (correct) {
                points++
            }
            scope.launch {
                delay(1.seconds)
                proceedAfterInlineFeedback(currentState.gameType, game)
            }
        } else {
            val index = input.toIntOrNull() ?: return
            game.toggleCell(index)
            _gameUiState.value = game.toUiState()
        }
    }

    private fun handleMiniSudokuAnswer(
        currentState: GameState.Active,
        game: MiniSudokuGame,
        input: String,
    ) {
        if (game.isCorrect(input)) {
            points++
            _gameState.value = GameState.Feedback(
                gameType = currentState.gameType,
                game = game,
                isCorrect = true,
                message = game.hint()?.let { FeedbackMessage.Plain(it) },
            )
            scope.launch {
                delay(1.seconds)
                proceedAfterFeedback()
            }
        } else {
            game.answeredAllCorrect = false
            val currentUiState = _gameUiState.value as? MiniSudokuUiState ?: return
            _gameUiState.value = currentUiState.copy(solutionValues = game.flatSolution().toImmutableList())
            scope.launch {
                delay(1500.milliseconds)
                proceedAfterInlineFeedback(currentState.gameType, game)
            }
        }
    }

    private fun startLightsOutGame(gameType: GameType) {
        val level = storage.getLastRound(gameType.id).coerceAtLeast(1)
        // The puzzle has no concept of a "wrong" answer, so the per-round no-mistakes
        // bonus message on the finish screen wouldn't make sense here.
        val game = LightsOutGame(level = level).apply { answeredAllCorrect = false }
        game.nextRound()
        _gameState.value = GameState.Active(gameType, game)
        _gameUiState.value = game.toUiState()
        navController.navigate(Playing(gameType.id))
    }

    private fun startSlidingPuzzleGame(gameType: GameType) {
        val level = storage.getLastRound(gameType.id).coerceAtLeast(1)
        // The puzzle has no concept of a "wrong" answer, so the per-round no-mistakes
        // bonus message on the finish screen wouldn't make sense here.
        val game = SlidingPuzzleGame(level = level).apply { answeredAllCorrect = false }
        game.nextRound()
        _gameState.value = GameState.Active(gameType, game)
        _gameUiState.value = game.toUiState()
        navController.navigate(Playing(gameType.id))
    }

    private fun handleLightsOutAnswer(
        currentState: GameState.Active,
        game: LightsOutGame,
        input: String,
    ) {
        val index = input.toIntOrNull() ?: return
        val solved = game.press(index)
        _gameUiState.value = game.toUiState()
        if (solved) {
            points = game.level
            storage.putLastRound(currentState.gameType.id, game.level + 1)
            _gameState.value = GameState.Feedback(
                gameType = currentState.gameType,
                game = game,
                isCorrect = true,
                message = null,
            )
            scope.launch {
                delay(700.milliseconds)
                finishCurrentGame(currentState.gameType, game)
            }
        }
    }

    private fun handleSlidingPuzzleAnswer(
        currentState: GameState.Active,
        game: SlidingPuzzleGame,
        input: String,
    ) {
        val index = input.toIntOrNull() ?: return
        val solved = game.slideTile(index)
        _gameUiState.value = game.toUiState()
        if (solved) {
            points = game.level
            storage.putLastRound(currentState.gameType.id, game.level + 1)
            _gameState.value = GameState.Feedback(
                gameType = currentState.gameType,
                game = game,
                isCorrect = true,
                message = null,
            )
            scope.launch {
                delay(700.milliseconds)
                finishCurrentGame(currentState.gameType, game)
            }
        }
    }

    private fun startShikakuGame(gameType: GameType) {
        val level = storage.getLastRound(gameType.id).coerceAtLeast(1)
        // The puzzle has no concept of a "wrong" answer, so the per-round no-mistakes
        // bonus message on the finish screen wouldn't make sense here.
        val game = ShikakuGame(level = level).apply { answeredAllCorrect = false }
        game.nextRound()
        _gameState.value = GameState.Active(gameType, game)
        _gameUiState.value = game.toUiState()
        navController.navigate(Playing(gameType.id))
    }

    private fun handleShikakuAnswer(
        currentState: GameState.Active,
        game: ShikakuGame,
        input: String,
    ) {
        // The UI encodes drawing/erasing over the shared onAnswer(String) channel:
        //   "draw:r1,c1,r2,c2" commits a rectangle, "del:r,c" removes the one under a cell.
        val solved = when {
            input.startsWith("draw:") -> {
                val parts = input.removePrefix("draw:").split(",").mapNotNull { it.toIntOrNull() }
                if (parts.size != 4) return
                game.commitRectangle(parts[0], parts[1], parts[2], parts[3])
            }
            input.startsWith("del:") -> {
                val parts = input.removePrefix("del:").split(",").mapNotNull { it.toIntOrNull() }
                if (parts.size != 2) return
                game.deleteRectangleAt(parts[0], parts[1])
            }
            else -> return
        }
        _gameUiState.value = game.toUiState()
        if (solved) {
            points = game.level
            storage.putLastRound(currentState.gameType.id, game.level + 1)
            _gameState.value = GameState.Feedback(
                gameType = currentState.gameType,
                game = game,
                isCorrect = true,
                message = null,
            )
            scope.launch {
                delay(700.milliseconds)
                finishCurrentGame(currentState.gameType, game)
            }
        }
    }

    private fun startNurikabeGame(gameType: GameType) {
        val level = storage.getLastRound(gameType.id).coerceAtLeast(1)
        // The puzzle has no concept of a "wrong" answer, so the per-round no-mistakes
        // bonus message on the finish screen wouldn't make sense here.
        val game = NurikabeGame(level = level).apply { answeredAllCorrect = false }
        game.nextRound()
        _gameState.value = GameState.Active(gameType, game)
        _gameUiState.value = game.toUiState()
        navController.navigate(Playing(gameType.id))
    }

    private fun handleNurikabeAnswer(
        currentState: GameState.Active,
        game: NurikabeGame,
        input: String,
    ) {
        // The UI encodes painting over the shared onAnswer(String) channel:
        //   "toggle:idx" flips one cell, "paint:<0|1>:idx,idx,..." sets a whole stroke.
        val solved = when {
            input.startsWith("toggle:") -> {
                val index = input.removePrefix("toggle:").toIntOrNull() ?: return
                game.toggleWall(index)
            }
            input.startsWith("paint:") -> {
                val rest = input.removePrefix("paint:")
                val separator = rest.indexOf(':')
                if (separator < 0) return
                val wall = rest.substring(0, separator) == "1"
                val cells = rest.substring(separator + 1).split(",").mapNotNull { it.toIntOrNull() }
                if (cells.isEmpty()) return
                game.setWalls(cells, wall)
            }
            else -> return
        }
        _gameUiState.value = game.toUiState()
        if (solved) {
            points = game.level
            storage.putLastRound(currentState.gameType.id, game.level + 1)
            _gameState.value = GameState.Feedback(
                gameType = currentState.gameType,
                game = game,
                isCorrect = true,
                message = null,
            )
            scope.launch {
                delay(700.milliseconds)
                finishCurrentGame(currentState.gameType, game)
            }
        }
    }

    private fun startCatQueensGame(gameType: GameType) {
        val level = storage.getLastRound(gameType.id).coerceAtLeast(1)
        // The puzzle has no concept of a "wrong" answer, so the per-round no-mistakes
        // bonus message on the finish screen wouldn't make sense here.
        val game = CatQueensGame(level = level).apply { answeredAllCorrect = false }
        game.nextRound()
        _gameState.value = GameState.Active(gameType, game)
        _gameUiState.value = game.toUiState()
        navController.navigate(Playing(gameType.id))
    }

    private fun handleCatQueensAnswer(
        currentState: GameState.Active,
        game: CatQueensGame,
        input: String,
    ) {
        // The UI sends the tapped cell index over the shared onAnswer(String) channel.
        val index = input.toIntOrNull() ?: return
        val solved = game.toggle(index)
        _gameUiState.value = game.toUiState()
        if (solved) {
            points = game.level
            storage.putLastRound(currentState.gameType.id, game.level + 1)
            _gameState.value = GameState.Feedback(
                gameType = currentState.gameType,
                game = game,
                isCorrect = true,
                message = null,
            )
            scope.launch {
                delay(700.milliseconds)
                finishCurrentGame(currentState.gameType, game)
            }
        }
    }

    private fun startKnotGame(gameType: GameType) {
        val level = storage.getLastRound(gameType.id).coerceAtLeast(1)
        // The puzzle has no concept of a "wrong" answer, so the per-round no-mistakes
        // bonus message on the finish screen wouldn't make sense here.
        val game = KnotGame(level = level).apply { answeredAllCorrect = false }
        game.nextRound()
        _gameState.value = GameState.Active(gameType, game)
        _gameUiState.value = game.toUiState()
        navController.navigate(Playing(gameType.id))
    }

    private fun handleKnotAnswer(
        currentState: GameState.Active,
        game: KnotGame,
        input: String,
    ) {
        // The UI encodes drawing/erasing over the shared onAnswer(String) channel:
        //   "path:<color>:idx,idx,..." sets a color's drawn path, "clear:<color>" removes it.
        val solved = when {
            input.startsWith("path:") -> {
                val rest = input.removePrefix("path:")
                val separator = rest.indexOf(':')
                if (separator < 0) return
                val color = rest.substring(0, separator).toIntOrNull() ?: return
                val cells = rest.substring(separator + 1).split(",").mapNotNull { it.toIntOrNull() }
                if (cells.isEmpty()) return
                game.setPath(color, cells)
            }
            input.startsWith("clear:") -> {
                val color = input.removePrefix("clear:").toIntOrNull() ?: return
                game.clearPath(color)
            }
            else -> return
        }
        _gameUiState.value = game.toUiState()
        if (solved) {
            points = game.level
            storage.putLastRound(currentState.gameType.id, game.level + 1)
            _gameState.value = GameState.Feedback(
                gameType = currentState.gameType,
                game = game,
                isCorrect = true,
                message = null,
            )
            scope.launch {
                delay(700.milliseconds)
                finishCurrentGame(currentState.gameType, game)
            }
        }
    }

    private fun startSoloChessGame(gameType: GameType) {
        val level = storage.getLastRound(gameType.id).coerceAtLeast(1)
        // The puzzle has no concept of a "wrong" answer, so the per-round no-mistakes
        // bonus message on the finish screen wouldn't make sense here.
        val game = SoloChessGame(level = level).apply { answeredAllCorrect = false }
        game.nextRound()
        _gameState.value = GameState.Active(gameType, game)
        _gameUiState.value = game.toUiState()
        navController.navigate(Playing(gameType.id))
    }

    private fun handleSoloChessAnswer(
        currentState: GameState.Active,
        game: SoloChessGame,
        input: String,
    ) {
        // The UI sends taps and the restart action over the shared onAnswer(String) channel:
        //   "tap:<index>" selects/captures, "restart" resets the level after a dead-end.
        val solved = when {
            input == "restart" -> {
                game.restart()
                false
            }
            input.startsWith("tap:") -> {
                val index = input.removePrefix("tap:").toIntOrNull() ?: return
                game.tap(index)
            }
            else -> return
        }
        _gameUiState.value = game.toUiState()
        if (solved) {
            points = game.level
            storage.putLastRound(currentState.gameType.id, game.level + 1)
            _gameState.value = GameState.Feedback(
                gameType = currentState.gameType,
                game = game,
                isCorrect = true,
                message = null,
            )
            scope.launch {
                delay(700.milliseconds)
                finishCurrentGame(currentState.gameType, game)
            }
        }
    }

    private fun startWordleGame(gameType: GameType) {
        val language = WordleLanguages.resolve(deviceLanguageTag())
        if (language == null) {
            // The tile is hidden for unsupported locales, so this is just a safety net.
            navigateToMainMenu()
            return
        }
        points = 0
        wordleScoreRecorded = false
        scope.launch {
            if (!launchWordleGame(gameType, language, navigateToPlaying = true)) {
                navigateToMainMenu()
            }
        }
    }

    private fun restartWordleInPlace() {
        val currentState = _gameState.value as? GameState.Active ?: return
        if (currentState.gameType != GameType.WORDLE) return
        val language = WordleLanguages.resolve(deviceLanguageTag()) ?: return
        points = 0
        wordleScoreRecorded = false
        scope.launch {
            launchWordleGame(currentState.gameType, language, navigateToPlaying = false)
        }
    }

    private fun continueWordleInDailyChallenge() {
        if (_gameState.value !is GameState.Active) return
        _gameUiState.value = null
        _gameState.value = GameState.Idle
        advanceDailyChallenge()
    }

    private suspend fun launchWordleGame(
        gameType: GameType,
        language: WordleLanguage,
        navigateToPlaying: Boolean,
    ): Boolean {
        val lists = loadWordleLists(language)
        if (lists.answers.isEmpty()) return false
        val game = WordleGame(language, lists.answers.random(), lists.guesses)
        game.nextRound()
        _gameState.value = GameState.Active(gameType, game)
        _gameUiState.value = game.toUiState()
        if (navigateToPlaying) {
            navController.navigate(Playing(gameType.id))
        }
        return true
    }

    private suspend fun loadWordleLists(language: WordleLanguage): WordleWordLists {
        wordListCache[language.tag]?.let { return it }
        val answers = loadWordleFile(language, language.answersPath)
        val guesses = loadWordleFile(language, language.guessesPath).toMutableSet()
        guesses.addAll(answers)
        val lists = WordleWordLists(answers = answers, guesses = guesses)
        wordListCache[language.tag] = lists
        return lists
    }

    private suspend fun loadWordleFile(language: WordleLanguage, path: String): List<String> = try {
        Res.readBytes(path)
            .decodeToString()
            .lineSequence()
            .map { it.trim().uppercase() }
            .filter { word ->
                word.length == language.wordLength && word.all { it in language.alphabet }
            }
            .distinct()
            .toList()
    } catch (_: Exception) {
        emptyList()
    }

    private fun handleWordleAnswer(
        currentState: GameState.Active,
        game: WordleGame,
        input: String,
    ) {
        val guessSubmitted = when {
            input == WORDLE_ENTER -> game.submitGuess()
            input == WORDLE_DELETE -> {
                game.backspace()
                false
            }
            input.startsWith(WORDLE_CLEAR_PREFIX) -> {
                input.removePrefix(WORDLE_CLEAR_PREFIX).toIntOrNull()?.let { game.clearFrom(it) }
                false
            }
            else -> input.firstOrNull()?.let { game.typeLetter(it) } ?: false
        }
        _gameUiState.value = game.toUiState()
        if (guessSubmitted && game.finished) {
            points = game.score
            recordWordleScore(currentState.gameType)
        }
    }

    private fun advanceDailyChallenge() {
        storage.appendSessionScore(points)
        val updated = storage.getOrCreateTodaySession { generateSessionGameIds() }
        _sessionState.value = updated
        if (updated.currentIndex >= updated.gameIds.size) {
            val streakBefore = _sessionStreak.value
            val completion = storage.recordSessionCompleted()
            _sessionStreak.value = completion.newStreak
            val totalXpAfter = storage.getTotalXp()
            _totalXp.value = totalXpAfter
            refreshDerivedStorageState()
            val sessionXpGained = updated.scores.sum() + completion.xpGained
            val totalXpBefore = totalXpAfter - sessionXpGained
            val levelBefore = UserStorage.levelForXp(totalXpBefore)
            val levelAfter = UserStorage.levelForXp(totalXpAfter)
            val sessionLevelChange = if (levelAfter > levelBefore) {
                UserStorage.LevelChange(
                    oldLevel = levelBefore,
                    newLevel = levelAfter,
                    totalXpBefore = totalXpBefore,
                    totalXpAfter = totalXpAfter,
                )
            } else {
                null
            }
            _lastCompletedSession.value = SessionResult(
                gameIds = updated.gameIds,
                scores = updated.scores,
                streakBefore = streakBefore,
                streakAfter = completion.newStreak,
                xpGained = sessionXpGained,
                totalXpAfter = totalXpAfter,
                levelChange = sessionLevelChange,
            )
            navController.navigate(SessionComplete) {
                popUpTo(MainMenu)
            }
        } else {
            navController.navigate(SessionInterstitial) {
                popUpTo(MainMenu)
            }
        }
    }

    /** Persist the Wordle result while staying on the board; the player leaves via Back. */
    private fun recordWordleScore(gameType: GameType) {
        if (wordleScoreRecorded) return
        wordleScoreRecorded = true
        storage.putScore(gameType.id, points)
        _totalXp.value = storage.getTotalXp()
        refreshDerivedStorageState()
    }

    private fun startSchulteTableGame(gameType: GameType) {
        val game = SchulteTableGame()
        game.nextRound()

        startTime = Clock.System.now().toEpochMilliseconds()
        _elapsedTime.value = 0L

        _gameState.value = GameState.Active(gameType, game)
        _gameUiState.value = game.toUiState()
        navController.navigate(Playing(gameType.id))
        startStopwatch()
    }

    private fun startStopwatch() {
        stopwatchJob?.cancel()
        stopwatchRunning = true
        stopwatchJob = scope.launch {
            while (stopwatchRunning && _gameState.value is GameState.Active) {
                _elapsedTime.value = Clock.System.now().toEpochMilliseconds() - startTime
                delay(100.milliseconds)
            }
        }
    }

    private fun handleSchulteTableAnswer(
        currentState: GameState.Active,
        game: SchulteTableGame,
        input: String,
    ) {
        val index = input.toIntOrNull() ?: return
        when (game.tapCell(index)) {
            SchulteTableGame.TapResult.Correct -> {
                _gameUiState.value = game.toUiState()
            }
            SchulteTableGame.TapResult.Complete -> {
                val elapsedMillis = Clock.System.now().toEpochMilliseconds() - startTime
                stopwatchRunning = false
                _elapsedTime.value = elapsedMillis
                // Deciseconds (1/10s), rounded; e.g. 24_300 ms → 243.
                points = ((elapsedMillis + 50) / 100).toInt().coerceAtLeast(1)
                _gameUiState.value = game.toUiState()
                scope.launch {
                    delay(500.milliseconds)
                    finishCurrentGame(currentState.gameType, game)
                }
            }
            SchulteTableGame.TapResult.Wrong -> {
                game.answeredAllCorrect = false
                _gameUiState.value = game.toUiState()
                scope.launch {
                    delay(500.milliseconds)
                    game.clearWrongTap()
                    _gameUiState.value = game.toUiState()
                }
            }
        }
    }

    private fun handleFlashCrowdAnswer(
        currentState: GameState.Active,
        game: FlashCrowdGame,
        input: String,
    ) {
        val isCorrect = game.isCorrect(input)
        if (isCorrect) {
            points++
        } else {
            game.answeredAllCorrect = false
        }
        _gameState.value = GameState.Feedback(
            gameType = currentState.gameType,
            game = game,
            isCorrect = isCorrect,
            message = null,
        )
        scope.launch {
            delay(1.seconds)
            proceedAfterFeedback()
        }
    }

    private fun proceedAfterInlineFeedback(gameType: GameType, game: Game) {
        val elapsed = Clock.System.now().toEpochMilliseconds() - startTime
        if (elapsed > GAME_TIME_MILLIS) {
            finishGame(gameType, game)
        } else {
            game.nextRound()
            _gameState.value = GameState.Active(gameType, game)
            _gameUiState.value = game.toUiState()
        }
    }

    private fun startGhostGridGame(gameType: GameType) {
        val game = GhostGridGame()
        if (game.adaptiveDifficulty) {
            sessionStartRound = storage.getLastRound(gameType.id)
            game.round = sessionStartRound
        }
        game.nextRound()

        _gameState.value = GameState.Active(gameType, game)
        navController.navigate(Playing(gameType.id))
        game.startShowSequence(scope) { emitGhostGridUiState(game) }
    }

    private fun handleGhostGridAnswer(
        currentState: GameState.Active,
        game: GhostGridGame,
        answer: String,
    ) {
        if (game.phase != GhostGridGame.Phase.ANSWERING) return
        when (game.submitAnswer(answer)) {
            GhostGridGame.SubmitResult.CorrectContinue -> emitGhostGridUiState(game)
            GhostGridGame.SubmitResult.RoundComplete -> {
                points++
                _gameState.value = GameState.Feedback(
                    gameType = currentState.gameType,
                    game = game,
                    isCorrect = true,
                    message = null,
                )
                scope.launch {
                    delay(1.seconds)
                    game.nextRound()
                    _gameState.value = GameState.Active(currentState.gameType, game)
                    game.startShowSequence(scope) { emitGhostGridUiState(game) }
                }
            }
            GhostGridGame.SubmitResult.Wrong -> {
                emitGhostGridUiState(game)
                scope.launch {
                    delay(2.seconds)
                    finishGhostGridGame(game)
                }
            }
        }
    }

    private fun finishGhostGridGame(game: GhostGridGame) {
        finishCurrentGame(GameType.GHOST_GRID, game)
    }

    private fun emitGhostGridUiState(game: GhostGridGame) {
        _gameUiState.value = game.toUiState()
    }

    private fun startOrbitTrackerGame(gameType: GameType) {
        val game = OrbitTrackerGame()
        if (game.adaptiveDifficulty) {
            sessionStartRound = storage.getLastRound(gameType.id)
            game.round = sessionStartRound
        }
        game.nextRound()

        _gameState.value = GameState.Active(gameType, game)
        navController.navigate(Playing(gameType.id))
        startOrbitTrackerAnimation(game)
    }

    private fun startOrbitTrackerAnimation(game: OrbitTrackerGame) {
        game.startHighlightAndMove(
            scope = scope,
            onPhaseChanged = { emitOrbitTrackerUiState(game) },
            onFrame = { emitOrbitTrackerFrame(game) },
        )
    }

    private fun handleOrbitTrackerAnswer(
        currentState: GameState.Active,
        game: OrbitTrackerGame,
        input: String,
    ) {
        val index = input.toIntOrNull() ?: return
        when (game.selectBall(index)) {
            OrbitTrackerGame.SubmitResult.CorrectContinue -> {
                emitOrbitTrackerUiState(game)
                _intermediateCorrectEvents.tryEmit(Unit)
            }
            OrbitTrackerGame.SubmitResult.RoundComplete -> {
                points++
                _gameState.value = GameState.Feedback(
                    gameType = currentState.gameType,
                    game = game,
                    isCorrect = true,
                    message = null,
                )
                scope.launch {
                    delay(1.seconds)
                    game.nextRound()
                    _gameState.value = GameState.Active(currentState.gameType, game)
                    startOrbitTrackerAnimation(game)
                }
            }
            OrbitTrackerGame.SubmitResult.Wrong -> {
                emitOrbitTrackerUiState(game)
                scope.launch {
                    delay(2.seconds)
                    finishOrbitTrackerGame(game)
                }
            }
        }
    }

    private fun finishOrbitTrackerGame(game: OrbitTrackerGame) {
        finishCurrentGame(GameType.ORBIT_TRACKER, game)
    }

    private fun emitOrbitTrackerUiState(game: OrbitTrackerGame) {
        _gameUiState.value = game.toUiState()
        emitOrbitTrackerFrame(game)
    }

    private fun emitOrbitTrackerFrame(game: OrbitTrackerGame) {
        _orbitBallPositions.value = game.balls.map { it.x to it.y }
    }

    private fun handleVisualMemoryAnswer(game: VisualMemoryGame, answer: String) {
        if (game.phase != VisualMemoryGame.Phase.ANSWERING) return
        when (game.submitAnswer(answer)) {
            VisualMemoryGame.SubmitResult.CorrectContinue -> {
                emitGameUiState(game)
                _intermediateCorrectEvents.tryEmit(Unit)
            }
            VisualMemoryGame.SubmitResult.RoundComplete -> {
                points++
                game.startCountdown(scope) { emitGameUiState(game) }
            }
            VisualMemoryGame.SubmitResult.GameComplete -> {
                points++
                finishVisualMemoryGame(game)
            }
            VisualMemoryGame.SubmitResult.Wrong -> {
                emitGameUiState(game)
                scope.launch {
                    delay(2.seconds)
                    finishVisualMemoryGame(game)
                }
            }
        }
    }

    private fun finishVisualMemoryGame(game: VisualMemoryGame) {
        finishCurrentGame(GameType.VISUAL_MEMORY, game)
    }

    private fun finishCurrentGame(gameType: GameType, game: Game) {
        (game as? VisualMemoryGame)?.cancelCountdown()
        (game as? SpotTheNewGame)?.cancelCountdown()
        (game as? GhostGridGame)?.cancelShowSequence()
        (game as? OrbitTrackerGame)?.cancelAnimation()
        (game as? DigitMemoryGame)?.cancelShowing()
        if (game is FlagsGame) cancelFlagsTimer()
        cancelTimer()
        _gameUiState.value = null
        _gameState.value = GameState.Idle

        val baseScore = points
        val difficultyBonus = gameType.difficultyBonus(
            startRound = sessionStartRound,
            baseScore = baseScore,
            adaptiveDifficulty = game.adaptiveDifficulty,
        )
        val totalScore = baseScore + difficultyBonus

        val scoreResult = storage.putScore(gameType.id, totalScore)
        val highscore = storage.getHighScore(gameType.id)
        if (game.adaptiveDifficulty) {
            storage.putLastRound(gameType.id, game.round - 3)
        }
        _totalXp.value = storage.getTotalXp()
        refreshDerivedStorageState()

        if (inSessionMode) {
            advanceDailyChallenge()
            return
        }

        navController.navigate(
            Finish(
                gameTypeId = gameType.id,
                score = totalScore,
                isNewHighscore = scoreResult.newHighscore,
                answeredAllCorrect = game.answeredAllCorrect,
                highscore = highscore,
                xpGained = scoreResult.xpGained,
                totalXpAfter = storage.getTotalXp(),
                difficultyBonus = difficultyBonus,
            ),
        ) {
            popUpTo(MainMenu)
        }
    }

    private fun emitGameUiState(game: VisualMemoryGame) {
        _gameUiState.value = game.toUiState()
    }

    private fun startDigitMemoryGame(gameType: GameType) {
        startTime = Clock.System.now().toEpochMilliseconds()
        _timeRemaining.value = GAME_TIME_MILLIS

        val game = DigitMemoryGame()
        game.nextRound()

        _gameState.value = GameState.Active(gameType, game)
        navController.navigate(Playing(gameType.id))
        startTimer()
        game.startShowing(scope) { _gameUiState.value = game.toUiState() }
    }

    private fun handleDigitMemoryAnswer(
        currentState: GameState.Active,
        game: DigitMemoryGame,
        input: String,
    ) {
        when (game.phase) {
            DigitMemoryGame.Phase.SOLVING -> {
                if (game.submitMath(input)) {
                    game.advanceToRecall()
                    _gameUiState.value = game.toUiState()
                } else {
                    // Wrong math forfeits the round: flash the answer, then start a fresh memorize
                    // round at the same difficulty (no recall, no point).
                    _gameUiState.value = game.toUiState()
                    scope.launch {
                        delay(1.seconds)
                        advanceDigitMemory(currentState.gameType, game, advanceDifficulty = false)
                    }
                }
            }
            DigitMemoryGame.Phase.RECALL -> {
                // Only a correct recall makes the next sequence longer; a wrong recall (like a wrong
                // math answer) replays at the same length with a fresh sequence.
                val correct = game.submitRecall(input)
                if (correct) points++
                _gameUiState.value = game.toUiState()
                scope.launch {
                    delay(1.seconds)
                    advanceDigitMemory(currentState.gameType, game, advanceDifficulty = correct)
                }
            }
            DigitMemoryGame.Phase.SHOWING -> Unit // ignore input while memorizing
        }
    }

    private fun advanceDigitMemory(gameType: GameType, game: DigitMemoryGame, advanceDifficulty: Boolean) {
        if (_gameState.value !is GameState.Active) return
        val elapsed = Clock.System.now().toEpochMilliseconds() - startTime
        if (elapsed > GAME_TIME_MILLIS) {
            finishCurrentGame(gameType, game)
        } else {
            if (advanceDifficulty) game.nextRound() else game.repeatRound()
            _gameState.value = GameState.Active(gameType, game)
            game.startShowing(scope) { _gameUiState.value = game.toUiState() }
        }
    }

    private fun ImmutableList<ImmutableList<FigureCell>>.withFeedbackStates(
        wrongIndex: Int?,
        correctIndex: Int,
        columnsPerRow: Int,
    ): ImmutableList<ImmutableList<FigureCell>> = mapIndexed { y, row ->
        row.mapIndexed { x, cell ->
            val flatIndex = y * columnsPerRow + x
            cell.copy(
                state = when (flatIndex) {
                    wrongIndex -> FigureCellState.WRONG
                    correctIndex -> FigureCellState.CORRECT
                    else -> FigureCellState.DIMMED
                },
            )
        }.toImmutableList()
    }.toImmutableList()

    private fun startMiniChessGame(gameType: GameType) {
        val game = MiniChessGame(difficultyDepth = storage.getMiniChessDifficulty())
        game.nextRound()
        _gameState.value = GameState.Active(gameType, game)
        _gameUiState.value = game.toUiState()
        navController.navigate(Playing(gameType.id))
    }

    private fun handleMiniChessAnswer(
        currentState: GameState.Active,
        game: MiniChessGame,
        input: String,
    ) {
        if (input == "restart" || input == "reset") {
            cancelMiniChessAi()
            // Any score from the just-finished round was already recorded in
            // handleMiniChessRoundOver, so reset the per-attempt counters before either
            // restoring the initial position or rolling a fresh scenario.
            points = 0
            game.answeredAllCorrect = true
            if (input == "reset") game.resetScenario() else game.restartScenario()
            _gameUiState.value = game.toUiState()
            return
        }
        if (game.phase != MiniChessGame.Phase.PLAYER_TURN) return
        val move = game.parseMove(input) ?: return
        val result = game.applyPlayerMove(move)
        _gameUiState.value = game.toUiState()

        when (result) {
            MiniChessGame.PlayerMoveResult.RoundOver ->
                handleMiniChessRoundOver(currentState, game)
            MiniChessGame.PlayerMoveResult.AiToMove ->
                scheduleMiniChessAi(currentState, game)
        }
    }

    private fun scheduleMiniChessAi(
        currentState: GameState.Active,
        game: MiniChessGame,
    ) {
        miniChessAiJob?.cancel()
        miniChessAiJob = scope.launch {
            val started = Clock.System.now().toEpochMilliseconds()
            val ai = ChessAi(game.aiDepth())
            val move = withContext(Dispatchers.Default) { ai.bestMove(game.board) }
                ?: return@launch
            // Enforce a minimum think time so the CPU's response always feels deliberate,
            // even when alpha-beta returns instantly on shallow positions.
            val elapsed = Clock.System.now().toEpochMilliseconds() - started
            val minThinkMs = 800L
            if (elapsed < minThinkMs) delay((minThinkMs - elapsed).milliseconds)
            game.applyAiMove(move)
            _gameUiState.value = game.toUiState()
            if (game.phase == MiniChessGame.Phase.ROUND_OVER) {
                handleMiniChessRoundOver(currentState, game)
            }
        }
    }

    private fun handleMiniChessRoundOver(
        currentState: GameState.Active,
        game: MiniChessGame,
    ) {
        when (game.outcome) {
            MiniChessOutcome.PLAYER_WIN -> points = game.winPoints()
            MiniChessOutcome.PLAYER_LOSS, MiniChessOutcome.DRAW -> points = 0
            null -> return
        }
        // Chess has no per-round bonus; suppress the "extra point for making no mistakes"
        // message on the finish screen (used as fallback if the user navigates back).
        game.answeredAllCorrect = false

        // Record the score immediately so it counts even if the player taps Back instead of
        // Play Again. The finish screen is bypassed for chess — Play Again resets the board
        // in place rather than going through it.
        storage.putScore(currentState.gameType.id, points)
        _totalXp.value = storage.getTotalXp()
        refreshDerivedStorageState()
    }

    private fun cancelMiniChessAi() {
        miniChessAiJob?.cancel()
        miniChessAiJob = null
    }

    private fun startFlagsGame(gameType: GameType) {
        val game = FlagsGame().apply { answeredAllCorrect = false }
        game.nextRound()
        _gameState.value = GameState.Active(gameType, game)
        _gameUiState.value = buildFlagsUiState(gameType, game)
        navController.navigate(Playing(gameType.id))
        startFlagsRoundTimer(gameType, game)
    }

    private fun buildFlagsUiState(
        gameType: GameType,
        game: FlagsGame,
        overrideAnswers: ImmutableList<AnswerButton>? = null,
    ): FlagsUiState {
        val base = game.toUiState() as FlagsUiState
        return base.copy(
            possibleAnswers = overrideAnswers ?: base.possibleAnswers,
            currentScore = points,
            bestScore = storage.getHighScore(gameType.id),
        )
    }

    private fun startFlagsRoundTimer(gameType: GameType, game: FlagsGame) {
        flagsTimerJob?.cancel()
        cancelTimer()
        startTime = Clock.System.now().toEpochMilliseconds()
        _timeRemaining.value = FLAGS_ROUND_TIME_MILLIS
        flagsTimerJob = scope.launch {
            while (true) {
                val elapsed = Clock.System.now().toEpochMilliseconds() - startTime
                val remaining = (FLAGS_ROUND_TIME_MILLIS - elapsed).coerceAtLeast(0)
                _timeRemaining.value = remaining
                if (remaining <= 0) {
                    finishCurrentGame(gameType, game)
                    return@launch
                }
                delay(100.milliseconds)
            }
        }
    }

    private fun cancelFlagsTimer() {
        flagsTimerJob?.cancel()
        flagsTimerJob = null
    }

    private fun handleFlagsAnswer(
        currentState: GameState.Active,
        game: FlagsGame,
        input: String,
    ) {
        val correctAnswer = game.correctCountry
        val currentUiState = _gameUiState.value as? FlagsUiState ?: return
        val isCorrect = game.isCorrect(input)

        // Freeze the timer during feedback so the 1s delay isn't counted against the player
        // (correct case) or the timeout doesn't race the game-over transition (wrong case).
        cancelFlagsTimer()

        if (isCorrect) {
            points++
            _intermediateCorrectEvents.tryEmit(Unit)
            val highlightedAnswers = currentUiState.possibleAnswers.map { button ->
                button.copy(
                    state = when (button.value) {
                        input -> AnswerButtonState.CORRECT
                        else -> AnswerButtonState.DIMMED
                    },
                )
            }.toImmutableList()
            _gameUiState.value = buildFlagsUiState(currentState.gameType, game, highlightedAnswers)
            scope.launch {
                delay(1.seconds)
                if (_gameState.value !is GameState.Active) return@launch
                if (game.isComplete()) {
                    finishCurrentGame(currentState.gameType, game)
                    return@launch
                }
                game.nextRound()
                _gameState.value = GameState.Active(currentState.gameType, game)
                _gameUiState.value = buildFlagsUiState(currentState.gameType, game)
                startFlagsRoundTimer(currentState.gameType, game)
            }
        } else {
            game.answeredAllCorrect = false
            val highlightedAnswers = currentUiState.possibleAnswers.map { button ->
                button.copy(
                    state = when (button.value) {
                        input -> AnswerButtonState.WRONG
                        correctAnswer -> AnswerButtonState.CORRECT
                        else -> AnswerButtonState.DIMMED
                    },
                )
            }.toImmutableList()
            _gameUiState.value = buildFlagsUiState(currentState.gameType, game, highlightedAnswers)
            scope.launch {
                delay(1.seconds)
                finishCurrentGame(currentState.gameType, game)
            }
        }
    }
}
