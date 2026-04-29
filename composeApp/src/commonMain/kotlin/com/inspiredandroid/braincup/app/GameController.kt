package com.inspiredandroid.braincup.app

import androidx.navigation.NavController
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.games.*
import com.inspiredandroid.braincup.games.minichess.ChessAi
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
    private var stopwatchRunning = false
    private var inSessionMode = false
    private var miniChessAiJob: Job? = null

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
    }

    init {
        storage.migrateStreakIfNeeded()
        _sessionStreak.value = storage.getSessionStreak()
        _sessionState.value = storage.getOrCreateTodaySession { generateSessionGameIds() }
        _totalXp.value = storage.getTotalXp()
        refreshDerivedStorageState()
    }

    private fun refreshDerivedStorageState() {
        _highscores.value = GameType.entries.associate { it.id to storage.getHighScore(it.id) }
        _unlockedAchievementCount.value = storage.getUnlockedAchievements().size
    }

    fun dispose() {
        scope.cancel()
    }

    private fun generateSessionGameIds(): List<String> = GameType.entries
        .shuffled()
        .take(UserStorage.SESSION_GAME_COUNT)
        .map { it.id }

    fun navigateToMainMenu() {
        val currentState = _gameState.value
        if (currentState is GameState.Active) {
            (currentState.game as? VisualMemoryGame)?.cancelCountdown()
            (currentState.game as? GhostGridGame)?.cancelShowSequence()
            (currentState.game as? OrbitTrackerGame)?.cancelAnimation()
            if (currentState.game is MiniChessGame) cancelMiniChessAi()
        }
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

    fun navigateToScoreboard(gameType: GameType) {
        navController.navigate(Scoreboard(gameType.id))
    }

    fun navigateToAchievements() {
        navController.navigate(Achievements)
    }

    fun startGame(gameType: GameType) {
        points = 0

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

        startTime = Clock.System.now().toEpochMilliseconds()
        _timeRemaining.value = GAME_TIME_MILLIS

        val game = createGame(gameType)
        if (game.adaptiveDifficulty) {
            game.round = storage.getLastRound(gameType.id)
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

        if (game is SherlockCalculationGame) {
            val currentUiState = _gameUiState.value as? SherlockCalculationUiState ?: return
            _gameUiState.value = currentUiState.copy(solutionTokens = game.solutionTokens)
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
        val randomGame = GameType.entries.random()
        navigateToInstructions(randomGame)
    }

    fun playAgain(gameType: GameType) {
        navigateToInstructions(gameType)
    }

    private fun startTimer() {
        scope.launch {
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
        GameType.SCHULTE_TABLE -> SchulteTableGame()
        GameType.VISUAL_MEMORY -> VisualMemoryGame()
        GameType.PATTERN_SEQUENCE -> PatternSequenceGame()
        GameType.GHOST_GRID -> GhostGridGame()
        GameType.COLOR_CONFUSION -> ColorConfusionGame()
        GameType.ORBIT_TRACKER -> OrbitTrackerGame()
        GameType.FLASH_CROWD -> FlashCrowdGame()
        GameType.MINI_CHESS -> MiniChessGame()
    }

    private fun startVisualMemoryGame(gameType: GameType) {
        val game = VisualMemoryGame()
        if (game.adaptiveDifficulty) {
            game.round = storage.getLastRound(gameType.id)
        }
        game.nextRound()

        _gameState.value = GameState.Active(gameType, game)
        navController.navigate(Playing(gameType.id))
        game.startCountdown(scope) { emitGameUiState(game) }
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
                },
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
                },
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
            _gameUiState.value = currentUiState.copy(solutionValues = game.flatSolution())
            scope.launch {
                delay(1500.milliseconds)
                proceedAfterInlineFeedback(currentState.gameType, game)
            }
        }
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
        stopwatchRunning = true
        scope.launch {
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
            game.round = storage.getLastRound(gameType.id)
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
            game.round = storage.getLastRound(gameType.id)
        }
        game.nextRound()

        _gameState.value = GameState.Active(gameType, game)
        navController.navigate(Playing(gameType.id))
        game.startHighlightAndMove(scope) { emitOrbitTrackerUiState(game) }
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
                    game.startHighlightAndMove(scope) { emitOrbitTrackerUiState(game) }
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
        (game as? GhostGridGame)?.cancelShowSequence()
        (game as? OrbitTrackerGame)?.cancelAnimation()
        _gameUiState.value = null
        _gameState.value = GameState.Idle

        val scoreResult = storage.putScore(gameType.id, points)
        val highscore = storage.getHighScore(gameType.id)
        if (game.adaptiveDifficulty) {
            storage.putLastRound(gameType.id, game.round - 3)
        }
        _totalXp.value = storage.getTotalXp()
        refreshDerivedStorageState()

        if (inSessionMode) {
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
            return
        }

        navController.navigate(
            Finish(
                gameTypeId = gameType.id,
                score = points,
                isNewHighscore = scoreResult.newHighscore,
                answeredAllCorrect = game.answeredAllCorrect,
                highscore = highscore,
                xpGained = scoreResult.xpGained,
                totalXpAfter = storage.getTotalXp(),
            ),
        ) {
            popUpTo(MainMenu)
        }
    }

    private fun emitGameUiState(game: VisualMemoryGame) {
        _gameUiState.value = game.toUiState()
    }

    private fun List<List<FigureCell>>.withFeedbackStates(
        wrongIndex: Int?,
        correctIndex: Int,
        columnsPerRow: Int,
    ): List<List<FigureCell>> = mapIndexed { y, row ->
        row.mapIndexed { x, cell ->
            val flatIndex = y * columnsPerRow + x
            cell.copy(
                state = when (flatIndex) {
                    wrongIndex -> FigureCellState.WRONG
                    correctIndex -> FigureCellState.CORRECT
                    else -> FigureCellState.DIMMED
                },
            )
        }
    }

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
}
