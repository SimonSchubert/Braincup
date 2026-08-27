package com.inspiredandroid.braincup.app

import androidx.compose.runtime.Immutable
import androidx.navigation.NavController
import braincup.composeapp.generated.resources.Res
import com.inspiredandroid.braincup.api.PlayGamesBridge
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.app.BoardCommand.intAndIntsArg
import com.inspiredandroid.braincup.app.BoardCommand.intArg
import com.inspiredandroid.braincup.app.BoardCommand.intsArg
import com.inspiredandroid.braincup.games.*
import com.inspiredandroid.braincup.games.minichess.ChessAi
import com.inspiredandroid.braincup.games.tools.GameColor
import com.inspiredandroid.braincup.games.wordle.WordleGame
import com.inspiredandroid.braincup.games.wordle.WordleLanguage
import com.inspiredandroid.braincup.games.wordle.WordleLanguages
import com.inspiredandroid.braincup.learn.LearnUnit
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.locale.AppLocale
import com.inspiredandroid.braincup.normalchess.NormalChessDifficulty
import com.inspiredandroid.braincup.normalchess.NormalChessMode
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
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
import kotlin.time.Duration
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

    /**
     * Lightweight Bubble Sum frames (position + visibility) updated every animation frame.
     * Values and answer length stay on [gameUiState] so only the arena leaf recomposes at 60fps.
     */
    private val _bubbleSumFrames =
        MutableStateFlow<List<BubbleSumGame.BubbleFrame>>(emptyList())
    val bubbleSumFrames: StateFlow<List<BubbleSumGame.BubbleFrame>> =
        _bubbleSumFrames.asStateFlow()

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

    /**
     * One-shot Simon Says pad tones: fired when a pad is flashed during SHOWING and when the
     * player taps a pad during ANSWERING. GameColor is the pad that should sound, not the round score.
     */
    private val _simonPadSoundEvents = MutableSharedFlow<GameColor>(extraBufferCapacity = 4)
    val simonPadSoundEvents: SharedFlow<GameColor> = _simonPadSoundEvents.asSharedFlow()

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

    /** Same stay-on-board scoring pattern as Wordle: record once, then Continue to finish / session. */
    private var bullsAndCowsScoreRecorded = false

    /** Captured when the score is recorded so Continue can open Finish without re-awarding XP. */
    private var bullsAndCowsScoreResult: UserStorage.ScoreResult? = null

    private val _totalXp = MutableStateFlow(0)
    val totalXp: StateFlow<Int> = _totalXp.asStateFlow()

    private val _highscores = MutableStateFlow<Map<String, Int>>(emptyMap())
    val highscores: StateFlow<Map<String, Int>> = _highscores.asStateFlow()

    private val _unlockedAchievementCount = MutableStateFlow(0)
    val unlockedAchievementCount: StateFlow<Int> = _unlockedAchievementCount.asStateFlow()

    private val _storageRevision = MutableStateFlow(0)
    val storageRevision: StateFlow<Int> = _storageRevision.asStateFlow()

    @Immutable
    data class SessionResult(
        val gameIds: ImmutableList<String>,
        val scores: ImmutableList<Int>,
        val streakBefore: Int,
        val streakAfter: Int,
        val xpGained: Int,
        val totalXpAfter: Int,
        val levelChange: UserStorage.LevelChange?,
    )

    companion object {
        const val GAME_TIME_MILLIS = 60 * 1_000L
        const val FLAGS_ROUND_TIME_MILLIS = FlagsGame.ROUND_TIME_MILLIS

        /** How long the correct shape stays revealed after a recall before the next trial. */
        private const val NBACK_REVEAL_HOLD_MILLIS = 900L

        /** Pause with wrong operator slots marked red before the first correct reveal. */
        private const val MISSING_OPS_FEEDBACK_INITIAL_MS = 700L

        /** Delay between revealing each wrong operator one-by-one. */
        private const val MISSING_OPS_FEEDBACK_STEP_MS = 650L

        /** Hold after every correct operator is shown before advancing the round. */
        private const val MISSING_OPS_FEEDBACK_HOLD_MS = 1200L
    }

    init {
        storage.migrateStreakIfNeeded()
        storage.seedHighScoresFromUnlockedGold()
        storage.unlockGoldForQualifyingHighScores()
        _sessionStreak.value = storage.getSessionStreak()
        _sessionState.value = storage.getOrCreateTodaySession { generateSessionGameIds() }
        _totalXp.value = storage.getTotalXp()
        refreshDerivedStorageState()
        PlayGamesBridge.onTotalXpRestored = { applyRestoredStoreProgress() }
        PlayGamesBridge.onStoreProgressRestored = { applyRestoredStoreProgress() }
    }

    private fun refreshDerivedStorageState() {
        _highscores.value = GameType.entries.associate { it.id to storage.getHighScore(it.id) }
        _unlockedAchievementCount.value = storage.getUnlockedAchievements().size
    }

    fun dispose() {
        PlayGamesBridge.onTotalXpRestored = null
        PlayGamesBridge.onStoreProgressRestored = null
        scope.cancel()
    }

    private fun applyRestoredStoreProgress() {
        storage.seedHighScoresFromUnlockedGold()
        storage.unlockGoldForQualifyingHighScores()
        _totalXp.value = storage.getTotalXp()
        refreshDerivedStorageState()
        _storageRevision.value += 1
    }

    private fun generateSessionGameIds(): List<String> {
        val eligibleByCategory = GameType.entries
            .filterNot {
                it.usesLevelLabel ||
                    it == GameType.MINI_CHESS ||
                    it == GameType.WORDLE ||
                    it == GameType.BULLS_AND_COWS
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
            cancelPendingJobs(currentState.game)
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

    fun navigateToAccounts() {
        navController.navigate(Accounts)
    }

    fun navigateToLicenses() {
        navController.navigate(Licenses)
    }

    fun navigateToLanguage() {
        navController.navigate(Language)
    }

    fun reloadAfterAccountSwitch() {
        storage.migrateStreakIfNeeded()
        storage.seedHighScoresFromUnlockedGold()
        storage.unlockGoldForQualifyingHighScores()
        _sessionStreak.value = storage.getSessionStreak()
        _sessionState.value = storage.getOrCreateTodaySession { generateSessionGameIds() }
        _totalXp.value = storage.getTotalXp()
        refreshDerivedStorageState()
        _storageRevision.value += 1
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

    fun navigateToPegSolitaire() {
        navController.navigate(PegSolitaire)
    }

    fun navigateToLearnMenu() {
        navController.navigate(LearnMenu)
    }

    fun navigateToLearnTopic(topic: MathTopic) {
        navController.navigate(LearnTopicDetail(topic.id))
    }

    fun navigateToLearnUnit(unit: LearnUnit) {
        navController.navigate(LearnUnitDetail(unit.id))
    }

    /** Open the reference guide [topic] carries: shapes for Geometry, rules for Arithmetic. */
    fun navigateToLearnGuide(topic: MathTopic) {
        navController.navigate(
            when (topic) {
                MathTopic.GEOMETRY -> LearnShapeGuide
                MathTopic.ARITHMETIC -> LearnRulesGuide
            },
        )
    }

    /**
     * Open a lesson. [replaceCurrent] is used by "Next lesson" so walking a whole unit does not
     * pile lessons up on the back stack — going back always lands on the unit screen.
     */
    fun navigateToLearnLesson(lessonId: String, replaceCurrent: Boolean = false) {
        navController.navigate(LearnLessonPlay(lessonId)) {
            if (replaceCurrent) {
                popUpTo<LearnLessonPlay> { inclusive = true }
            }
        }
    }

    /** Return to a unit's screen from any of its lesson, test or certificate screens. */
    fun popToLearnUnit(unit: LearnUnit) {
        navController.popBackStack(LearnUnitDetail(unit.id), inclusive = false)
    }

    fun navigateToLearnTest(unit: LearnUnit) {
        navController.navigate(LearnTest(unit.id))
    }

    fun navigateToLearnCertificate(unit: LearnUnit) {
        navController.navigate(LearnCertificate(unit.id))
    }

    fun startGame(gameType: GameType) {
        points = 0
        sessionStartRound = 0

        // Every level puzzle resumes at its stored level through the same path.
        if (gameType.usesLevelLabel) {
            startLevelGame(gameType)
            return
        }

        // Games that own their setup (reveal timers, animation loops, async word list) start
        // themselves; the rest take the generic timed-round path.
        when (gameType) {
            GameType.VISUAL_MEMORY -> startVisualMemoryGame(gameType)
            GameType.GHOST_GRID -> startGhostGridGame(gameType)
            GameType.SIMON_SAYS -> startSimonSaysGame(gameType)
            GameType.ORBIT_TRACKER -> startOrbitTrackerGame(gameType)
            GameType.SCHULTE_TABLE -> startSchulteTableGame(gameType)
            GameType.MINI_CHESS -> startMiniChessGame(gameType)
            GameType.FLAGS -> startFlagsGame(gameType)
            GameType.DIGIT_MEMORY,
            GameType.QUICK_SUM,
            GameType.N_BACK,
            -> startRevealRoundGame(gameType)
            GameType.BUBBLE_SUM -> startBubbleSumGame(gameType)
            GameType.SPOT_THE_NEW -> startSpotTheNewGame(gameType)
            GameType.WORDLE -> startWordleGame(gameType)
            GameType.BULLS_AND_COWS -> startBullsAndCowsGame(gameType)
            else -> startTimedRoundGame(gameType)
        }
    }

    /** The default flow: a fixed-length timed run of generated rounds. */
    private fun startTimedRoundGame(gameType: GameType) {
        startTime = Clock.System.now().toEpochMilliseconds()
        _timeRemaining.value = GAME_TIME_MILLIS

        val game = createGame(gameType)
        resumeAdaptiveDifficulty(gameType, game)
        game.nextRound()

        _gameState.value = GameState.Active(gameType, game)
        emitUiState(game)
        navController.navigate(Playing(gameType.id))
        startTimer()
    }

    /**
     * Pick the ramp back up where the last run left it, so a returning player never replays the
     * easiest rounds. The start round is also what [GameType.difficultyBonus] scores on finish.
     */
    private fun resumeAdaptiveDifficulty(gameType: GameType, game: Game) {
        if (!game.adaptiveDifficulty) return
        sessionStartRound = storage.getLastRound(gameType.id)
        game.round = sessionStartRound
    }

    fun submitAnswer(answer: String) {
        val currentState = _gameState.value
        if (currentState !is GameState.Active) return

        val game = currentState.game
        when (game) {
            is VisualMemoryGame -> handleVisualMemoryAnswer(game, answer)
            is GhostGridGame -> handleGhostGridAnswer(currentState, game, answer)
            is SimonSaysGame -> handleSimonSaysAnswer(currentState, game, answer)
            is AnomalyPuzzleGame -> handleAnomalyPuzzleAnswer(currentState, game, answer.trim())
            is PatternSequenceGame -> handlePatternSequenceAnswer(currentState, game, answer.trim())
            is PathFinderGame -> handlePathFinderAnswer(currentState, game, answer.trim())
            is ColoredShapesGame -> handleColoredShapesAnswer(currentState, game, answer.trim())
            is ColorConfusionGame -> handleColorConfusionAnswer(currentState, game, answer.trim())
            is OrbitTrackerGame -> handleOrbitTrackerAnswer(currentState, game, answer.trim())
            is FlashCrowdGame -> handleFlashCrowdAnswer(currentState, game, answer.trim())
            is MiniSudokuGame -> handleMiniSudokuAnswer(currentState, game, answer.trim())
            is WordleGame -> handleWordleAnswer(currentState, game, answer)
            is LightsOutGame -> handleLightsOutAnswer(currentState, game, answer.trim())
            is SlidingPuzzleGame -> handleSlidingPuzzleAnswer(currentState, game, answer.trim())
            is TowerOfHanoiGame -> handleTowerOfHanoiAnswer(currentState, game, answer.trim())
            is ShikakuGame -> handleShikakuAnswer(currentState, game, answer.trim())
            is NurikabeGame -> handleNurikabeAnswer(currentState, game, answer.trim())
            is CatQueensGame -> handleCatQueensAnswer(currentState, game, answer.trim())
            is KnotGame -> handleKnotAnswer(currentState, game, answer.trim())
            is SoloChessGame -> handleSoloChessAnswer(currentState, game, answer.trim())
            is PrismClearGame -> handlePrismClearAnswer(currentState, game, answer.trim())
            is SchulteTableGame -> handleSchulteTableAnswer(currentState, game, answer.trim())
            is ValueComparisonGame -> handleValueComparisonAnswer(currentState, game, answer.trim())
            is MissingOperatorsGame -> handleMissingOperatorsAnswer(currentState, game, answer.trim())
            is MiniChessGame -> handleMiniChessAnswer(currentState, game, answer.trim())
            is FlagsGame -> handleFlagsAnswer(currentState, game, answer.trim())
            is DigitMemoryGame -> handleDigitMemoryAnswer(currentState, game, answer.trim())
            is QuickSumGame -> handleQuickSumAnswer(currentState, game, answer.trim())
            is NBackGame -> handleNBackAnswer(currentState, game, answer.trim())
            is SpotTheNewGame -> handleSpotTheNewAnswer(game, answer.trim())
            is BullsAndCowsGame -> handleBullsAndCowsAnswer(currentState, game, answer)
            is TrioGame -> handleTrioAnswer(currentState, game, answer.trim())
            else -> submitGenericAnswer(currentState, game, answer)
        }
    }

    /** Correct/incorrect plus a one-second feedback beat, for games without bespoke handling. */
    private fun submitGenericAnswer(currentState: GameState.Active, game: Game, answer: String) {
        val input = answer.trim()

        // Stop continuous motion while the feedback screen is up; proceedAfterFeedback restarts it.
        (game as? BubbleSumGame)?.cancelTimedPhase()

        if (game.isCorrect(input)) {
            onCorrectAnswer(currentState, game)
        } else {
            game.answeredAllCorrect = false
            showFeedbackScreen(currentState.gameType, game, isCorrect = false, message = game.solutionMessage())
        }
    }

    /**
     * The one correct-answer beat every round-based game shares: bank the point, show the feedback
     * screen with the game's hint, then move on.
     */
    private fun onCorrectAnswer(currentState: GameState.Active, game: Game) {
        points++
        showFeedbackScreen(
            gameType = currentState.gameType,
            game = game,
            isCorrect = true,
            message = game.hint()?.let { FeedbackMessage.Plain(it) },
        )
    }

    /** Hold the feedback screen for a beat, then start the next round or finish the run. */
    private fun showFeedbackScreen(
        gameType: GameType,
        game: Game,
        isCorrect: Boolean,
        message: FeedbackMessage?,
    ) {
        _gameState.value = GameState.Feedback(
            gameType = gameType,
            game = game,
            isCorrect = isCorrect,
            message = message,
        )
        scope.launch {
            delay(1.seconds)
            proceedAfterFeedback()
        }
    }

    /**
     * The answer flow for games that mark a wrong answer on their own board instead of on the
     * feedback screen: a correct answer takes the shared [onCorrectAnswer] beat, a wrong one
     * recolors the live UI state through [markWrong] and holds it for [wrongFeedback] before the
     * next round. [markWrong] returns null when the input cannot be mapped onto the board, which
     * leaves the round untouched.
     */
    private inline fun <reified S : GameUiState> handleAnswerWithBoardFeedback(
        currentState: GameState.Active,
        game: Game,
        input: String,
        wrongFeedback: Duration = 1.seconds,
        markWrong: (S) -> S?,
    ) {
        if (game.isCorrect(input)) {
            onCorrectAnswer(currentState, game)
            return
        }
        game.answeredAllCorrect = false
        val current = _gameUiState.value as? S ?: return
        _gameUiState.value = markWrong(current) ?: return
        scheduleNextRound(currentState.gameType, game, after = wrongFeedback)
    }

    /**
     * The keyboard flow both guess-a-secret boards share. Returns whether a guess was submitted:
     * Wordle submits by itself once the row is full, so that answer has to come back from [type]
     * as well as from the ENTER key.
     */
    private fun applyKeyboardInput(
        input: String,
        type: (Char) -> Boolean,
        backspace: () -> Unit,
        clearAt: (Int) -> Unit,
        submit: () -> Boolean,
    ): Boolean {
        when (input) {
            KeyboardCommand.ENTER -> return submit()
            KeyboardCommand.DELETE -> {
                backspace()
                return false
            }
        }
        val clearIndex = KeyboardCommand.clearIndexOf(input)
        if (clearIndex != null) {
            clearAt(clearIndex)
            return false
        }
        return input.firstOrNull()?.let(type) ?: false
    }

    /** Leave the board feedback up for [after], then advance the round (or finish the run). */
    private fun scheduleNextRound(gameType: GameType, game: Game, after: Duration) {
        scope.launch {
            delay(after)
            proceedAfterInlineFeedback(gameType, game)
        }
    }

    fun giveUp() {
        val currentState = _gameState.value
        if (currentState !is GameState.Active) return

        val gameType = currentState.gameType
        val game = currentState.game
        game.answeredAllCorrect = false

        when (game) {
            // Games that end the whole attempt on a give-up.
            is MiniChessGame -> {
                game.markGiveUp()
                finishCurrentGame(gameType, game)
            }
            is LevelGame -> {
                points = 0
                finishCurrentGame(gameType, game)
            }
            // Boards that stay up with the answer revealed; the player leaves them by hand.
            is WordleGame -> {
                game.giveUp()
                points = 0
                emitUiState(game)
                recordWordleScore(gameType)
            }
            is BullsAndCowsGame -> {
                game.giveUp()
                points = 0
                emitUiState(game)
                recordBullsAndCowsScore(gameType)
            }
            // Games that reveal the solution on their own board before the next round.
            is SherlockCalculationGame -> {
                val ui = _gameUiState.value as? SherlockCalculationUiState ?: return
                _gameUiState.value = ui.copy(solutionTokens = game.solutionTokens.toImmutableList())
                scheduleNextRound(gameType, game, after = 1.seconds)
            }
            is MissingOperatorsGame -> {
                val ui = _gameUiState.value as? MissingOperatorsUiState ?: return
                if (ui.correctOperators != null) return
                _gameUiState.value = ui.copy(
                    submittedOperators = null,
                    correctOperators = game.correctOperators.toImmutableList(),
                    feedbackRevealedSlots = persistentSetOf(),
                )
                scope.launch {
                    revealMissingOperatorsSequentially(
                        gameType = gameType,
                        game = game,
                        indicesToReveal = game.correctOperators.indices.toList(),
                    )
                }
            }
            // Everyone else: the feedback screen shows the solution, then the round moves on.
            else -> showFeedbackScreen(gameType, game, isCorrect = false, message = game.solutionMessage())
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
            emitUiState(game)
            if (game is BubbleSumGame) {
                startBubbleSumMotion(game)
            }
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
            continueStayOnBoardInDailyChallenge()
        } else {
            restartWordleInPlace()
        }
    }

    /** Continue from the Bulls & Cows result board: finish screen (medal/XP), or next daily game. */
    fun bullsAndCowsFinishedAction() {
        if (inSessionMode) {
            continueStayOnBoardInDailyChallenge()
        } else {
            navigateToBullsAndCowsFinish()
        }
    }

    /** Leave the result board for the standard finish screen without recording score again. */
    private fun navigateToBullsAndCowsFinish() {
        val currentState = _gameState.value as? GameState.Active ?: return
        if (currentState.gameType != GameType.BULLS_AND_COWS) return
        val game = currentState.game as? BullsAndCowsGame ?: return
        val gameType = currentState.gameType
        val score = points
        val scoreResult = bullsAndCowsScoreResult
            ?: UserStorage.ScoreResult(newHighscore = false, xpGained = 0, levelChange = null)
        val highscore = storage.getHighScore(gameType.id)

        _gameUiState.value = null
        _gameState.value = GameState.Idle
        bullsAndCowsScoreRecorded = false
        bullsAndCowsScoreResult = null

        navController.navigate(
            Finish(
                gameTypeId = gameType.id,
                score = score,
                isNewHighscore = scoreResult.newHighscore,
                answeredAllCorrect = game.won,
                highscore = highscore,
                xpGained = scoreResult.xpGained,
                totalXpAfter = storage.getTotalXp(),
            ),
        ) {
            popUpTo(MainMenu)
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

        state.pausablePhase()?.pauseTimedPhase()
    }

    /** The running memorize/flash phase of the current game, if it has one to hold. */
    private fun GameState.Active.pausablePhase(): PausableTimedPhaseGame? = (game as? PausableTimedPhaseGame)?.takeIf { it.isTimedPhaseActive }

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

        state.pausablePhase()?.resumeTimedPhase(scope) { emitUiState(state.game) }
    }

    private fun createGame(gameType: GameType): Game = when (gameType) {
        GameType.COLORED_SHAPES -> ColoredShapesGame()
        GameType.MENTAL_CALCULATION -> MentalCalculationGame()
        GameType.SHERLOCK_CALCULATION -> SherlockCalculationGame()
        GameType.CHAIN_CALCULATION -> ChainCalculationGame()
        GameType.MISSING_OPERATORS -> MissingOperatorsGame()
        GameType.VALUE_COMPARISON -> ValueComparisonGame()
        GameType.FRACTION_CALCULATION -> FractionCalculationGame()
        GameType.ANOMALY_PUZZLE -> AnomalyPuzzleGame()
        GameType.PATH_FINDER -> PathFinderGame()
        GameType.MINI_SUDOKU -> MiniSudokuGame()
        // Level puzzles are normally built at the stored level by startGame; this covers the
        // instructions/preview path, which only ever needs the first one.
        GameType.LIGHTS_OUT,
        GameType.SLIDING_PUZZLE,
        GameType.TOWER_OF_HANOI,
        GameType.SHIKAKU,
        GameType.NURIKABE,
        GameType.CAT_QUEENS,
        GameType.KNOT,
        GameType.SOLO_CHESS,
        GameType.PRISM_CLEAR,
        -> createLevelGame(gameType, level = 1)
        GameType.SCHULTE_TABLE -> SchulteTableGame()
        GameType.VISUAL_MEMORY -> VisualMemoryGame()
        GameType.PATTERN_SEQUENCE -> PatternSequenceGame()
        GameType.GHOST_GRID -> GhostGridGame()
        GameType.SIMON_SAYS -> SimonSaysGame()
        GameType.COLOR_CONFUSION -> ColorConfusionGame()
        GameType.ORBIT_TRACKER -> OrbitTrackerGame()
        GameType.BUBBLE_SUM -> BubbleSumGame()
        GameType.FLASH_CROWD -> FlashCrowdGame()
        GameType.MINI_CHESS -> MiniChessGame()
        GameType.FLAGS -> FlagsGame()
        GameType.DIGIT_MEMORY -> DigitMemoryGame()
        GameType.QUICK_SUM -> QuickSumGame()
        GameType.N_BACK -> NBackGame()
        GameType.SPOT_THE_NEW -> SpotTheNewGame()
        GameType.BULLS_AND_COWS -> BullsAndCowsGame()
        GameType.TRIO -> TrioGame()
        // Wordle needs an async-loaded, locale-specific word list, so it is built in startWordleGame.
        GameType.WORDLE -> error("WordleGame is created in startWordleGame")
    }

    /** The one place that knows how to build each level puzzle at a given level. */
    private fun createLevelGame(gameType: GameType, level: Int): LevelGame = when (gameType) {
        GameType.LIGHTS_OUT -> LightsOutGame(level = level)
        GameType.SLIDING_PUZZLE -> SlidingPuzzleGame(level = level)
        GameType.TOWER_OF_HANOI -> TowerOfHanoiGame(level = level)
        GameType.SHIKAKU -> ShikakuGame(level = level)
        GameType.NURIKABE -> NurikabeGame(level = level)
        GameType.CAT_QUEENS -> CatQueensGame(level = level)
        GameType.KNOT -> KnotGame(level = level)
        GameType.SOLO_CHESS -> SoloChessGame(level = level)
        GameType.PRISM_CLEAR -> PrismClearGame(level = level)
        else -> error("${gameType.name} is not a level game")
    }

    private fun startBullsAndCowsGame(gameType: GameType) {
        points = 0
        bullsAndCowsScoreRecorded = false
        bullsAndCowsScoreResult = null
        val game = BullsAndCowsGame()
        game.nextRound()
        _gameState.value = GameState.Active(gameType, game)
        emitUiState(game)
        navController.navigate(Playing(gameType.id))
    }

    private fun handleBullsAndCowsAnswer(
        currentState: GameState.Active,
        game: BullsAndCowsGame,
        input: String,
    ) {
        val guessSubmitted = applyKeyboardInput(
            input = input,
            type = { digit ->
                game.typeDigit(digit)
                false
            },
            backspace = game::backspace,
            clearAt = game::removeAt,
            submit = game::submitGuess,
        )
        emitUiState(game)
        if (guessSubmitted && game.finished) {
            // Score is guesses used (lower is better): gold ≤3, silver ≤6, bronze ≤12.
            // Stay on the board so the player can embrace the win (or see the secret).
            points = if (game.won) game.guessesUsed else 0
            recordBullsAndCowsScore(currentState.gameType)
        }
    }

    private fun startVisualMemoryGame(gameType: GameType) {
        val game = VisualMemoryGame()
        game.nextRound()

        _gameState.value = GameState.Active(gameType, game)
        navController.navigate(Playing(gameType.id))
        game.startCountdown(scope) { emitUiState(game) }
    }

    private fun startSpotTheNewGame(gameType: GameType) {
        val game = SpotTheNewGame()
        game.startMemorizing()

        _gameState.value = GameState.Active(gameType, game)
        emitUiState(game)
        navController.navigate(Playing(gameType.id))
        // Memorize phase runs for a fixed countdown, then the answering rounds begin.
        // No global timer: the game runs until a wrong tap.
        game.startMemorizeCountdown(scope) { emitUiState(game) }
    }

    private fun handleSpotTheNewAnswer(game: SpotTheNewGame, answer: String) {
        if (game.phase != SpotTheNewGame.Phase.ANSWERING) return
        when (game.submitAnswer(answer)) {
            SpotTheNewGame.SubmitResult.Correct -> {
                points++
                emitUiState(game)
                _intermediateCorrectEvents.tryEmit(Unit)
            }
            SpotTheNewGame.SubmitResult.PoolExhausted -> {
                // Player beat every unique combo; count the final tap and finish gracefully.
                points++
                finishCurrentGame(GameType.SPOT_THE_NEW, game)
            }
            SpotTheNewGame.SubmitResult.Wrong -> {
                emitUiState(game)
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
    ) = handleAnswerWithBoardFeedback<AnomalyPuzzleUiState>(currentState, game, input) { ui ->
        ui.copy(
            rows = ui.rows.withFeedbackStates(
                wrongIndex = input.toIntOrNull()?.minus(1),
                correctIndex = game.resultIndex,
                columnsPerRow = ui.columnsPerRow,
            ),
        )
    }

    private fun handlePatternSequenceAnswer(
        currentState: GameState.Active,
        game: PatternSequenceGame,
        input: String,
    ) = handleAnswerWithBoardFeedback<PatternSequenceUiState>(currentState, game, input) { ui ->
        ui.copy(
            optionRows = ui.optionRows.withFeedbackStates(
                wrongIndex = input.toIntOrNull(),
                correctIndex = game.problem.correctOptionIndex,
                columnsPerRow = ui.optionColumns,
            ),
        )
    }

    private fun handlePathFinderAnswer(
        currentState: GameState.Active,
        game: PathFinderGame,
        input: String,
    ) = handleAnswerWithBoardFeedback<PathFinderUiState>(currentState, game, input) { ui ->
        ui.copy(
            grid = ui.grid.withFeedbackStates(
                wrongIndex = input.toIntOrNull()?.minus(1),
                correctIndex = game.correctIndex,
                columnsPerRow = 4,
            ),
        )
    }

    private fun handleColoredShapesAnswer(
        currentState: GameState.Active,
        game: ColoredShapesGame,
        input: String,
    ) = handleAnswerWithBoardFeedback<ColoredShapesUiState>(currentState, game, input) { ui ->
        ui.copy(
            possibleAnswers = ui.possibleAnswers.withFeedbackStates(
                wrongValue = input,
                correctValue = game.points(),
            ),
        )
    }

    private fun handleValueComparisonAnswer(
        currentState: GameState.Active,
        game: ValueComparisonGame,
        input: String,
    ) = handleAnswerWithBoardFeedback<ValueComparisonUiState>(currentState, game, input) { ui ->
        val selectedIndex = input.toIntOrNull()?.minus(1) ?: return@handleAnswerWithBoardFeedback null
        ui.copy(
            answers = ui.answers.withFeedbackStates(
                wrongIndex = selectedIndex,
                correctIndex = game.resultIndex,
            ),
        )
    }

    private fun handleMissingOperatorsAnswer(
        currentState: GameState.Active,
        game: MissingOperatorsGame,
        input: String,
    ) {
        val currentUiState = _gameUiState.value as? MissingOperatorsUiState ?: return
        // Ignore further input while wrong-answer / give-up feedback is showing.
        if (currentUiState.correctOperators != null) return

        if (game.isCorrect(input)) {
            onCorrectAnswer(currentState, game)
            return
        }

        game.answeredAllCorrect = false
        val submitted = game.parseOperators(input) ?: return
        val correct = game.correctOperators
        val wrongIndices = submitted.indices.filter { i ->
            submitted[i] != correct[i]
        }
        _gameUiState.value = currentUiState.copy(
            submittedOperators = submitted.toImmutableList(),
            correctOperators = correct.toImmutableList(),
            feedbackRevealedSlots = persistentSetOf(),
        )
        scope.launch {
            // Hold on red wrongs, then flip each incorrect slot to the correct operator
            // one at a time so the solution is easy to follow.
            revealMissingOperatorsSequentially(
                gameType = currentState.gameType,
                game = game,
                indicesToReveal = wrongIndices,
            )
        }
    }

    private suspend fun revealMissingOperatorsSequentially(
        gameType: GameType,
        game: MissingOperatorsGame,
        indicesToReveal: List<Int>,
    ) {
        delay(MISSING_OPS_FEEDBACK_INITIAL_MS.milliseconds)
        indicesToReveal.forEachIndexed { step, index ->
            val feedbackState = _gameUiState.value as? MissingOperatorsUiState ?: return
            if (feedbackState.correctOperators == null) return
            _gameUiState.value = feedbackState.copy(
                feedbackRevealedSlots = (feedbackState.feedbackRevealedSlots + index)
                    .toImmutableSet(),
            )
            if (step < indicesToReveal.lastIndex) {
                delay(MISSING_OPS_FEEDBACK_STEP_MS.milliseconds)
            }
        }
        delay(MISSING_OPS_FEEDBACK_HOLD_MS.milliseconds)
        proceedAfterInlineFeedback(gameType, game)
    }

    private fun handleTrioAnswer(
        currentState: GameState.Active,
        game: TrioGame,
        input: String,
    ) {
        val index = input.toIntOrNull() ?: return
        when (game.tap(index)) {
            TrioGame.TapResult.Toggled -> {
                emitUiState(game)
            }
            TrioGame.TapResult.Correct -> {
                points++
                emitUiState(game)
                scheduleNextRound(currentState.gameType, game, after = 700.milliseconds)
            }
            TrioGame.TapResult.Wrong -> {
                emitUiState(game)
                scope.launch {
                    delay(500.milliseconds)
                    if (_gameState.value is GameState.Active) {
                        game.clearSelection()
                        emitUiState(game)
                    }
                }
            }
            TrioGame.TapResult.Ignored -> Unit
        }
    }

    private fun handleColorConfusionAnswer(
        currentState: GameState.Active,
        game: ColorConfusionGame,
        input: String,
    ) {
        if (input == BoardCommand.SUBMIT) {
            val correct = game.submit()
            emitUiState(game)
            if (correct) {
                points++
            }
            scheduleNextRound(currentState.gameType, game, after = 1.seconds)
        } else {
            val index = input.toIntOrNull() ?: return
            game.toggleCell(index)
            emitUiState(game)
        }
    }

    private fun handleMiniSudokuAnswer(
        currentState: GameState.Active,
        game: MiniSudokuGame,
        input: String,
    ) = handleAnswerWithBoardFeedback<MiniSudokuUiState>(
        currentState = currentState,
        game = game,
        input = input,
        wrongFeedback = 1500.milliseconds,
    ) { ui ->
        ui.copy(solutionValues = game.flatSolution().toImmutableList())
    }

    /**
     * Shared flow for every [LevelGame] board: apply the move the screen encoded, republish the
     * board, and bank the level when that move solved it. [apply] returns null when the command
     * is not one this board understands, which leaves the round untouched.
     */
    private fun handleLevelBoardAnswer(
        currentState: GameState.Active,
        game: LevelGame,
        apply: () -> Boolean?,
    ) {
        val solved = apply() ?: return
        emitUiState(game)
        if (solved) {
            onLevelSolved(currentState, game)
        }
    }

    private fun handleLightsOutAnswer(
        currentState: GameState.Active,
        game: LightsOutGame,
        input: String,
    ) = handleLevelBoardAnswer(currentState, game) {
        input.toIntOrNull()?.let { game.press(it) }
    }

    private fun handleSlidingPuzzleAnswer(
        currentState: GameState.Active,
        game: SlidingPuzzleGame,
        input: String,
    ) = handleLevelBoardAnswer(currentState, game) {
        input.toIntOrNull()?.let { game.slideTile(it) }
    }

    private fun handleTowerOfHanoiAnswer(
        currentState: GameState.Active,
        game: TowerOfHanoiGame,
        input: String,
    ) = handleLevelBoardAnswer(currentState, game) {
        input.toIntOrNull()?.let { game.tapPeg(it) }
    }

    private fun handleShikakuAnswer(
        currentState: GameState.Active,
        game: ShikakuGame,
        input: String,
    ) = handleLevelBoardAnswer(currentState, game) {
        val drawn = input.intsArg(BoardCommand.DRAW)?.takeIf { it.size == 4 }
        val deleted = input.intsArg(BoardCommand.DELETE)?.takeIf { it.size == 2 }
        when {
            drawn != null -> game.commitRectangle(drawn[0], drawn[1], drawn[2], drawn[3])
            deleted != null -> game.deleteRectangleAt(deleted[0], deleted[1])
            else -> null
        }
    }

    private fun handleNurikabeAnswer(
        currentState: GameState.Active,
        game: NurikabeGame,
        input: String,
    ) = handleLevelBoardAnswer(currentState, game) {
        val toggled = input.intArg(BoardCommand.TOGGLE)
        val painted = input.intAndIntsArg(BoardCommand.PAINT)
        when {
            toggled != null -> game.toggleWall(toggled)
            painted != null -> game.setWalls(painted.second, wall = painted.first == 1)
            else -> null
        }
    }

    private fun handleCatQueensAnswer(
        currentState: GameState.Active,
        game: CatQueensGame,
        input: String,
    ) = handleLevelBoardAnswer(currentState, game) {
        input.toIntOrNull()?.let { game.toggle(it) }
    }

    private fun handleKnotAnswer(
        currentState: GameState.Active,
        game: KnotGame,
        input: String,
    ) = handleLevelBoardAnswer(currentState, game) {
        val drawn = input.intAndIntsArg(BoardCommand.PATH)
        val cleared = input.intArg(BoardCommand.CLEAR)
        when {
            drawn != null -> game.setPath(drawn.first, drawn.second)
            cleared != null -> game.clearPath(cleared)
            else -> null
        }
    }

    private fun handleSoloChessAnswer(
        currentState: GameState.Active,
        game: SoloChessGame,
        input: String,
    ) = handleLevelBoardAnswer(currentState, game) {
        if (input == BoardCommand.RESTART) {
            // A dead-end level is restarted in place, so this changes the board without solving it.
            game.restart()
            false
        } else {
            input.intArg(BoardCommand.TAP)?.let { game.tap(it) }
        }
    }

    /**
     * Resume a [LevelGame] at the stored level. [answeredAllCorrect] is cleared because these
     * puzzles have no concept of a "wrong" answer, so the per-round no-mistakes bonus message
     * on the finish screen wouldn't make sense.
     */
    private fun startLevelGame(gameType: GameType, navigate: Boolean = true) {
        val level = storage.getLastRound(gameType.id).coerceAtLeast(1)
        val game = createLevelGame(gameType, level).apply { answeredAllCorrect = false }
        game.nextRound()
        _gameState.value = GameState.Active(gameType, game)
        emitUiState(game)
        if (navigate) {
            navController.navigate(Playing(gameType.id))
        }
    }

    /** Bank the cleared level as the score, unlock the next one, flash correct, then finish. */
    private fun onLevelSolved(currentState: GameState.Active, game: LevelGame) {
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

    private fun handlePrismClearAnswer(
        currentState: GameState.Active,
        game: PrismClearGame,
        input: String,
    ) {
        val tapped = input.intArg(BoardCommand.TAP)
        val swapped = input.intsArg(BoardCommand.SWAP)?.takeIf { it.size == 2 }
        val result = when {
            input == BoardCommand.RESTART -> {
                game.restart()
                PrismClearGame.PrismClearResult.Updated
            }
            input == BoardCommand.UNDO -> {
                game.undo()
                PrismClearGame.PrismClearResult.Updated
            }
            tapped != null -> game.tap(tapped)
            swapped != null -> game.trySwap(swapped[0], swapped[1])
            else -> return
        }
        when (result) {
            PrismClearGame.PrismClearResult.Updated,
            PrismClearGame.PrismClearResult.Rejected,
            -> emitUiState(game)
            PrismClearGame.PrismClearResult.Solved -> onPrismClearSolved(currentState, game)
        }
    }

    /**
     * Let clear/fall animation play on the board, then a brief correct flash, then either the next
     * catalog level in place or the finish screen when the last level is cleared.
     */
    private fun onPrismClearSolved(
        currentState: GameState.Active,
        game: PrismClearGame,
    ) {
        val ui = game.toUiState()
        _gameUiState.value = ui
        // Stay in Active so the Playing route keeps rendering the board during the pop animation.
        points = game.level
        storage.putLastRound(currentState.gameType.id, game.level + 1)
        scope.launch {
            // Match PrismClearScreen: SwapMillis + (PopMillis + FallMillis) per cascade wave.
            val swapMs = if (ui.swapFromIndex != null) 200 else 0
            val animMs = (swapMs + ui.clearWaves.size * (240 + 280) + 100).coerceAtLeast(350)
            delay(animMs.milliseconds)
            _gameState.value = GameState.Feedback(
                gameType = currentState.gameType,
                game = game,
                isCorrect = true,
                message = null,
            )
            delay(700.milliseconds)
            val gameType = currentState.gameType
            // Catalog complete (or session mode): full finish flow with XP + finish screen.
            if (inSessionMode || game.level >= PrismClearLevels.COUNT) {
                finishCurrentGame(gameType, game)
                return@launch
            }
            // Intermediate clear: record score/XP for this level, then jump straight into the next.
            storage.putScore(gameType.id, points)
            _totalXp.value = storage.getTotalXp()
            refreshDerivedStorageState()
            points = 0
            startLevelGame(gameType, navigate = false)
        }
    }

    private fun startWordleGame(gameType: GameType) {
        val language = WordleLanguages.resolve(AppLocale.currentTag())
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
        val language = WordleLanguages.resolve(AppLocale.currentTag()) ?: return
        points = 0
        wordleScoreRecorded = false
        scope.launch {
            launchWordleGame(currentState.gameType, language, navigateToPlaying = false)
        }
    }

    private fun continueStayOnBoardInDailyChallenge() {
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
        emitUiState(game)
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
        val guessSubmitted = applyKeyboardInput(
            input = input,
            type = game::typeLetter,
            backspace = game::backspace,
            clearAt = game::clearFrom,
            submit = game::submitGuess,
        )
        emitUiState(game)
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

    /** Persist the Bulls & Cows result while staying on the board; leave via Continue / Back. */
    private fun recordBullsAndCowsScore(gameType: GameType) {
        if (bullsAndCowsScoreRecorded) return
        bullsAndCowsScoreRecorded = true
        bullsAndCowsScoreResult = storage.putScore(gameType.id, points)
        _totalXp.value = storage.getTotalXp()
        refreshDerivedStorageState()
    }

    private fun startSchulteTableGame(gameType: GameType) {
        val game = SchulteTableGame()
        game.nextRound()

        startTime = Clock.System.now().toEpochMilliseconds()
        _elapsedTime.value = 0L

        _gameState.value = GameState.Active(gameType, game)
        emitUiState(game)
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
                emitUiState(game)
            }
            SchulteTableGame.TapResult.Complete -> {
                val elapsedMillis = Clock.System.now().toEpochMilliseconds() - startTime
                stopwatchRunning = false
                _elapsedTime.value = elapsedMillis
                // Deciseconds (1/10s), rounded; e.g. 24_300 ms → 243.
                points = ((elapsedMillis + 50) / 100).toInt().coerceAtLeast(1)
                emitUiState(game)
                scope.launch {
                    delay(500.milliseconds)
                    finishCurrentGame(currentState.gameType, game)
                }
            }
            SchulteTableGame.TapResult.Wrong -> {
                game.answeredAllCorrect = false
                emitUiState(game)
                scope.launch {
                    delay(500.milliseconds)
                    game.clearWrongTap()
                    emitUiState(game)
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
        // No solution message: the answer is the dot count the player was asked to compare.
        showFeedbackScreen(currentState.gameType, game, isCorrect = isCorrect, message = null)
    }

    private fun proceedAfterInlineFeedback(gameType: GameType, game: Game) {
        val elapsed = Clock.System.now().toEpochMilliseconds() - startTime
        if (elapsed > GAME_TIME_MILLIS) {
            finishGame(gameType, game)
        } else {
            game.nextRound()
            _gameState.value = GameState.Active(gameType, game)
            emitUiState(game)
        }
    }

    private fun startGhostGridGame(gameType: GameType) {
        val game = GhostGridGame()
        resumeAdaptiveDifficulty(gameType, game)
        game.nextRound()

        _gameState.value = GameState.Active(gameType, game)
        navController.navigate(Playing(gameType.id))
        game.startShowSequence(scope) { emitUiState(game) }
    }

    private fun handleGhostGridAnswer(
        currentState: GameState.Active,
        game: GhostGridGame,
        answer: String,
    ) {
        if (game.phase != GhostGridGame.Phase.ANSWERING) return
        handleSequenceSubmit(currentState, game, game.submitAnswer(answer)) {
            game.startShowSequence(scope) { emitUiState(game) }
        }
    }

    /**
     * The shared submit flow for the survival games that report through [SequenceSubmitResult]:
     * keep taking taps, bank the round and open the next one through [startNextRound], or end the
     * run on a mistake. The board is republished at every step, including right before the
     * feedback beat and right after the new round is generated — otherwise it keeps showing marks
     * from one tap (or one round) ago while the next sequence is already being flashed.
     */
    private fun handleSequenceSubmit(
        currentState: GameState.Active,
        game: Game,
        result: SequenceSubmitResult,
        startNextRound: () -> Unit,
    ) {
        emitUiState(game)
        when (result) {
            SequenceSubmitResult.CorrectContinue -> Unit
            SequenceSubmitResult.RoundComplete -> {
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
                    emitUiState(game)
                    _gameState.value = GameState.Active(currentState.gameType, game)
                    startNextRound()
                }
            }
            SequenceSubmitResult.Wrong -> {
                scope.launch {
                    delay(2.seconds)
                    finishCurrentGame(currentState.gameType, game)
                }
            }
        }
    }

    private fun startSimonSaysGame(gameType: GameType) {
        val game = SimonSaysGame()
        // adaptiveDifficulty is false, so no storage.getLastRound() resume block here.
        game.nextRound()
        // Paint the fresh (all dark) board before the lead-in delay. Without this _gameUiState
        // still holds the previous Simon session's final board, so replaying flashes the old
        // game-over marks for a moment before the first pad lights.
        emitUiState(game)

        _gameState.value = GameState.Active(gameType, game)
        navController.navigate(Playing(gameType.id))
        startSimonSaysShow(game)
    }

    private fun startSimonSaysShow(game: SimonSaysGame) {
        game.startShowNewPad(scope) {
            emitUiState(game)
            val idx = game.currentShowIndex
            if (idx >= 0) {
                _simonPadSoundEvents.tryEmit(game.sequence[idx])
            }
        }
    }

    private fun handleSimonSaysAnswer(
        currentState: GameState.Active,
        game: SimonSaysGame,
        answer: String,
    ) {
        if (game.phase != SimonSaysGame.Phase.ANSWERING) return
        // Tone for the pad the player pressed, including wrong taps (classic Simon behaviour).
        GameColor.entries.find { it.name == answer }?.let { _simonPadSoundEvents.tryEmit(it) }
        handleSequenceSubmit(currentState, game, game.submitAnswer(answer)) {
            startSimonSaysShow(game)
        }
    }

    private fun startOrbitTrackerGame(gameType: GameType) {
        val game = OrbitTrackerGame()
        resumeAdaptiveDifficulty(gameType, game)
        game.nextRound()

        _gameState.value = GameState.Active(gameType, game)
        navController.navigate(Playing(gameType.id))
        startOrbitTrackerAnimation(game)
    }

    private fun startOrbitTrackerAnimation(game: OrbitTrackerGame) {
        game.startHighlightAndMove(
            scope = scope,
            onPhaseChanged = {
                emitUiState(game)
                emitOrbitTrackerFrame(game)
            },
            onFrame = { emitOrbitTrackerFrame(game) },
        )
    }

    private fun handleOrbitTrackerAnswer(
        currentState: GameState.Active,
        game: OrbitTrackerGame,
        input: String,
    ) {
        val index = input.toIntOrNull() ?: return
        val result = game.selectBall(index)
        if (result == SequenceSubmitResult.CorrectContinue) {
            _intermediateCorrectEvents.tryEmit(Unit)
        }
        handleSequenceSubmit(currentState, game, result) {
            startOrbitTrackerAnimation(game)
        }
        emitOrbitTrackerFrame(game)
    }

    private fun emitOrbitTrackerFrame(game: OrbitTrackerGame) {
        _orbitBallPositions.value = game.balls.map { it.x to it.y }
    }

    private fun handleVisualMemoryAnswer(game: VisualMemoryGame, answer: String) {
        if (game.phase != VisualMemoryGame.Phase.ANSWERING) return
        when (game.submitAnswer(answer)) {
            VisualMemoryGame.SubmitResult.CorrectContinue -> {
                emitUiState(game)
                _intermediateCorrectEvents.tryEmit(Unit)
            }
            VisualMemoryGame.SubmitResult.RoundComplete -> {
                points++
                game.startCountdown(scope) { emitUiState(game) }
            }
            VisualMemoryGame.SubmitResult.GameComplete -> {
                points++
                finishCurrentGame(GameType.VISUAL_MEMORY, game)
            }
            VisualMemoryGame.SubmitResult.Wrong -> {
                emitUiState(game)
                scope.launch {
                    delay(2.seconds)
                    finishCurrentGame(GameType.VISUAL_MEMORY, game)
                }
            }
        }
    }

    /** Republish the board from the game's own state; the single way UI state is produced. */
    private fun emitUiState(game: Game) {
        _gameUiState.value = game.toUiState()
    }

    private fun finishCurrentGame(gameType: GameType, game: Game) {
        cancelPendingJobs(game)
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

        val maxLevelReached = game is PrismClearGame &&
            points > 0 &&
            game.level >= PrismClearLevels.COUNT

        navController.navigate(
            Finish(
                gameTypeId = gameType.id,
                score = totalScore,
                isNewHighscore = scoreResult.newHighscore,
                answeredAllCorrect = game.answeredAllCorrect,
                highscore = highscore,
                xpGained = scoreResult.xpGained,
                totalXpAfter = storage.getTotalXp(),
                adaptiveStartRoundCredit = difficultyBonus,
                isFinalCatalogLevel = maxLevelReached,
            ),
        ) {
            popUpTo(MainMenu)
        }
    }

    /** Start for the reveal-round games: the 60s clock, then the first reveal. */
    private fun startRevealRoundGame(gameType: GameType) {
        startTime = Clock.System.now().toEpochMilliseconds()
        _timeRemaining.value = GAME_TIME_MILLIS

        val game = createGame(gameType)
        check(game is RevealRoundGame) { "${gameType.name} is not a reveal-round game" }
        game.nextRound()

        _gameState.value = GameState.Active(gameType, game)
        navController.navigate(Playing(gameType.id))
        startTimer()
        game.startTimedPhase(scope) { emitUiState(game) }
    }

    /**
     * Open the next reveal round, or finish once the 60s are up. A correct answer advances the
     * ramp; a wrong one replays the same difficulty so the pace never runs ahead of the player.
     */
    private fun <G> advanceRevealRound(
        gameType: GameType,
        game: G,
        advanceDifficulty: Boolean,
    ) where G : Game, G : RevealRoundGame {
        if (_gameState.value !is GameState.Active) return
        if (Clock.System.now().toEpochMilliseconds() - startTime > GAME_TIME_MILLIS) {
            // finishGame, not finishCurrentGame: these games are scored out of correct answers, so
            // they earn the flawless-run bonus point the finish screen announces.
            finishGame(gameType, game)
            return
        }
        if (advanceDifficulty) game.nextRound() else game.repeatRound()
        _gameState.value = GameState.Active(gameType, game)
        game.startTimedPhase(scope) { emitUiState(game) }
    }

    private fun startBubbleSumGame(gameType: GameType) {
        startTime = Clock.System.now().toEpochMilliseconds()
        _timeRemaining.value = GAME_TIME_MILLIS

        val game = BubbleSumGame()
        resumeAdaptiveDifficulty(gameType, game)
        game.nextRound()

        _gameState.value = GameState.Active(gameType, game)
        emitUiState(game)
        navController.navigate(Playing(gameType.id))
        startTimer()
        startBubbleSumMotion(game)
    }

    private fun startBubbleSumMotion(game: BubbleSumGame) {
        emitBubbleSumFrame(game)
        game.startMotion(scope) { emitBubbleSumFrame(game) }
    }

    /**
     * The arena is laid out to fill whatever space the screen has, so only the UI knows its shape.
     * Reported from the canvas; the game rescales its bounds and positions to match.
     */
    fun setBubbleSumArenaSize(widthPx: Float, heightPx: Float) {
        val game = (_gameState.value as? GameState.Active)?.game as? BubbleSumGame ?: return
        game.setArenaSize(widthPx, heightPx)
        emitBubbleSumFrame(game)
    }

    private fun emitBubbleSumFrame(game: BubbleSumGame) {
        _bubbleSumFrames.value = game.frames()
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
                    emitUiState(game)
                } else {
                    // Wrong math forfeits the round: flash the answer, then start a fresh memorize
                    // round at the same difficulty (no recall, no point).
                    emitUiState(game)
                    scope.launch {
                        delay(1.seconds)
                        advanceRevealRound(currentState.gameType, game, advanceDifficulty = false)
                    }
                }
            }
            DigitMemoryGame.Phase.RECALL -> {
                // Only a correct recall makes the next sequence longer; a wrong recall (like a wrong
                // math answer) replays at the same length with a fresh sequence.
                val correct = game.submitRecall(input)
                if (correct) points++
                emitUiState(game)
                scope.launch {
                    delay(1.seconds)
                    advanceRevealRound(currentState.gameType, game, advanceDifficulty = correct)
                }
            }
            DigitMemoryGame.Phase.SHOWING -> Unit // ignore input while memorizing
        }
    }

    private fun handleQuickSumAnswer(
        currentState: GameState.Active,
        game: QuickSumGame,
        input: String,
    ) {
        // Input during the flash is ignored: the pad is not shown yet.
        if (game.phase != QuickSumGame.Phase.ANSWER) return

        // Only a correct total advances the ramp; a wrong one replays the same tier with fresh
        // terms, so the pace never runs ahead of the player.
        val correct = game.submitSum(input)
        if (correct) points++
        emitUiState(game)
        scope.launch {
            delay(1.seconds)
            advanceRevealRound(currentState.gameType, game, advanceDifficulty = correct)
        }
    }

    private fun handleNBackAnswer(currentState: GameState.Active, game: NBackGame, input: String) {
        // Only a tap during recall counts; ignore anything while shapes are still flashing or revealing.
        if (game.phase != NBackGame.Phase.RECALL || game.recallResult != null) return

        val correct = game.submitRecall(input)
        if (correct) {
            points++
            _intermediateCorrectEvents.tryEmit(Unit)
        }
        emitUiState(game)
        scope.launch {
            delay(NBACK_REVEAL_HOLD_MILLIS)
            advanceRevealRound(currentState.gameType, game, advanceDifficulty = correct)
        }
    }

    // Marking up a board after an answer: the picked cell/button goes red, the right one green,
    // everything else dims. One family of helpers so every game reveals an answer the same way.

    private fun <T : FeedbackCell<T>> ImmutableList<ImmutableList<T>>.withFeedbackStates(
        wrongIndex: Int?,
        correctIndex: Int,
        columnsPerRow: Int,
    ): ImmutableList<ImmutableList<T>> = mapIndexed { y, row ->
        row.mapIndexed { x, cell ->
            val flatIndex = y * columnsPerRow + x
            cell.withState(
                when (flatIndex) {
                    wrongIndex -> FigureCellState.WRONG
                    correctIndex -> FigureCellState.CORRECT
                    else -> FigureCellState.DIMMED
                },
            )
        }.toImmutableList()
    }.toImmutableList()

    private fun ImmutableList<AnswerButton>.withFeedbackStates(
        wrongIndex: Int?,
        correctIndex: Int,
    ): ImmutableList<AnswerButton> = mapIndexed { index, button ->
        button.copy(
            state = when (index) {
                wrongIndex -> AnswerButtonState.WRONG
                correctIndex -> AnswerButtonState.CORRECT
                else -> AnswerButtonState.DIMMED
            },
        )
    }.toImmutableList()

    /**
     * Value-keyed variant for games whose buttons carry the answer itself. Passing the correct
     * value as [wrongValue] marks a right answer: it lights green and the rest dim.
     */
    private fun ImmutableList<AnswerButton>.withFeedbackStates(
        wrongValue: String?,
        correctValue: String,
    ): ImmutableList<AnswerButton> = map { button ->
        button.copy(
            state = when (button.value) {
                correctValue -> AnswerButtonState.CORRECT
                wrongValue -> AnswerButtonState.WRONG
                else -> AnswerButtonState.DIMMED
            },
        )
    }.toImmutableList()

    private fun startMiniChessGame(gameType: GameType) {
        val game = MiniChessGame(difficultyDepth = storage.getMiniChessDifficulty())
        game.nextRound()
        _gameState.value = GameState.Active(gameType, game)
        emitUiState(game)
        navController.navigate(Playing(gameType.id))
    }

    private fun handleMiniChessAnswer(
        currentState: GameState.Active,
        game: MiniChessGame,
        input: String,
    ) {
        if (input == BoardCommand.RESTART || input == BoardCommand.RESET) {
            cancelMiniChessAi()
            // Any score from the just-finished round was already recorded in
            // handleMiniChessRoundOver, so reset the per-attempt counters before either
            // restoring the initial position or rolling a fresh scenario.
            points = 0
            game.answeredAllCorrect = true
            if (input == BoardCommand.RESET) game.resetScenario() else game.restartScenario()
            emitUiState(game)
            return
        }
        if (game.phase != MiniChessGame.Phase.PLAYER_TURN) return
        val move = game.parseMove(input) ?: return
        val result = game.applyPlayerMove(move)
        emitUiState(game)

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
            emitUiState(game)
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

    /**
     * Stops any coroutine a game left running (reveal timers, animation frames, AI search).
     * Every exit path out of a live game goes through here so no single path can forget one.
     */
    private fun cancelPendingJobs(game: Game) {
        (game as? TimedPhaseGame)?.cancelTimedPhase()
        // The chess search and the Flags round clock are owned by the controller, not the game.
        if (game is MiniChessGame) cancelMiniChessAi()
        if (game is FlagsGame) cancelFlagsTimer()
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
        } else {
            game.answeredAllCorrect = false
        }
        // A correct pick lights green on its own; a wrong one goes red next to the right answer.
        val markedAnswers = currentUiState.possibleAnswers.withFeedbackStates(
            wrongValue = input,
            correctValue = correctAnswer,
        )
        _gameUiState.value = buildFlagsUiState(currentState.gameType, game, markedAnswers)

        scope.launch {
            delay(1.seconds)
            // One wrong flag ends the run; otherwise play on until the country pool is used up.
            if (!isCorrect) {
                finishCurrentGame(currentState.gameType, game)
                return@launch
            }
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
    }
}
