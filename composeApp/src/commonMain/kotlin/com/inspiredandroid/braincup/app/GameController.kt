package com.inspiredandroid.braincup.app

import androidx.navigation.NavController
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.games.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Clock

class GameController(
    private val navController: NavController,
    val storage: UserStorage = UserStorage(),
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _gameState = MutableStateFlow<GameState>(GameState.Idle)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _timeRemaining = MutableStateFlow(GAME_TIME_MILLIS)
    val timeRemaining: StateFlow<Long> = _timeRemaining.asStateFlow()

    private val _gameUiState = MutableStateFlow<GameUiState?>(null)
    val gameUiState: StateFlow<GameUiState?> = _gameUiState.asStateFlow()

    private var startTime = 0L
    private var points = 0

    companion object {
        const val GAME_TIME_MILLIS = 60 * 1_000L
    }

    init {
        storage.putAppOpen()
    }

    fun navigateToMainMenu() {
        val currentState = _gameState.value
        if (currentState is GameState.Active) {
            (currentState.game as? VisualMemoryGame)?.cancelCountdown()
            (currentState.game as? GhostGridGame)?.cancelShowSequence()
            (currentState.game as? OrbitTrackerGame)?.cancelAnimation()
        }
        _gameUiState.value = null
        _gameState.value = GameState.Idle
        navController.popBackStack(MainMenu, inclusive = false)
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

        startTime = Clock.System.now().toEpochMilliseconds()
        _timeRemaining.value = GAME_TIME_MILLIS

        val game = createGame(gameType)
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

        val input = answer.trim()
        val isCorrect = game.isCorrect(input)

        if (isCorrect) {
            points++
            _gameState.value = GameState.Feedback(
                gameType = currentState.gameType,
                game = game,
                isCorrect = true,
                message = game.hint(),
            )
        } else {
            game.answeredAllCorrect = false
            _gameState.value = GameState.Feedback(
                gameType = currentState.gameType,
                game = game,
                isCorrect = false,
                message = game.solution(),
            )
        }

        scope.launch {
            delay(1_000)
            proceedAfterFeedback()
        }
    }

    fun giveUp() {
        val currentState = _gameState.value
        if (currentState !is GameState.Active) return

        val game = currentState.game
        game.answeredAllCorrect = false

        if (game is SherlockCalculationGame) {
            val currentUiState = _gameUiState.value as? SherlockCalculationUiState ?: return
            _gameUiState.value = currentUiState.copy(solutionTokens = game.solutionTokens)
            scope.launch {
                delay(1_000)
                proceedAfterInlineFeedback(currentState.gameType, game)
            }
            return
        }

        _gameState.value = GameState.Feedback(
            gameType = currentState.gameType,
            game = game,
            isCorrect = false,
            message = game.solution(),
        )

        scope.launch {
            delay(1_000)
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
                delay(100)
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
        GameType.GRID_SOLVER -> GridSolverGame()
        GameType.VISUAL_MEMORY -> VisualMemoryGame()
        GameType.PATTERN_SEQUENCE -> PatternSequenceGame()
        GameType.GHOST_GRID -> GhostGridGame()
        GameType.COLOR_CONFUSION -> ColorConfusionGame()
        GameType.ORBIT_TRACKER -> OrbitTrackerGame()
        GameType.FLASH_CROWD -> FlashCrowdGame()
    }

    private fun startVisualMemoryGame(gameType: GameType) {
        val game = VisualMemoryGame()
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
                message = game.hint(),
            )
            scope.launch {
                delay(1_000)
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
                delay(1_000)
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
                message = game.hint(),
            )
            scope.launch {
                delay(1_000)
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
                delay(1_000)
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
                message = game.hint(),
            )
            scope.launch {
                delay(1_000)
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
                delay(1_000)
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
                message = game.hint(),
            )
            scope.launch {
                delay(1_000)
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
                delay(1_000)
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
                delay(1_000)
                proceedAfterInlineFeedback(currentState.gameType, game)
            }
        } else {
            val index = input.toIntOrNull() ?: return
            game.toggleCell(index)
            _gameUiState.value = game.toUiState()
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
            delay(1_000)
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
                    delay(1000)
                    game.nextRound()
                    _gameState.value = GameState.Active(currentState.gameType, game)
                    game.startShowSequence(scope) { emitGhostGridUiState(game) }
                }
            }
            GhostGridGame.SubmitResult.Wrong -> {
                emitGhostGridUiState(game)
                scope.launch {
                    delay(2000)
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
            OrbitTrackerGame.SubmitResult.CorrectContinue -> emitOrbitTrackerUiState(game)
            OrbitTrackerGame.SubmitResult.RoundComplete -> {
                points++
                _gameState.value = GameState.Feedback(
                    gameType = currentState.gameType,
                    game = game,
                    isCorrect = true,
                    message = null,
                )
                scope.launch {
                    delay(1000)
                    game.nextRound()
                    _gameState.value = GameState.Active(currentState.gameType, game)
                    game.startHighlightAndMove(scope) { emitOrbitTrackerUiState(game) }
                }
            }
            OrbitTrackerGame.SubmitResult.Wrong -> {
                emitOrbitTrackerUiState(game)
                scope.launch {
                    delay(2000)
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
        when (game.submitAnswer(answer)) {
            VisualMemoryGame.SubmitResult.CorrectContinue -> emitGameUiState(game)
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
                    delay(2000)
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

        val newHighscore = storage.putScore(gameType.id, points)
        val highscore = storage.getHighScore(gameType.id)

        navController.navigate(
            Finish(
                gameTypeId = gameType.id,
                score = points,
                isNewHighscore = newHighscore,
                answeredAllCorrect = game.answeredAllCorrect,
                highscore = highscore,
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
}
