package com.inspiredandroid.braincup.app

import androidx.navigation.NavController
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.games.*
import com.inspiredandroid.braincup.games.AnomalyPuzzleGame
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
        }
        _gameUiState.value = null
        _gameState.value = GameState.Idle
        navController.navigate(MainMenu) {
            popUpTo(MainMenu) { inclusive = true }
        }
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

        // Visual Memory has special handling (no timer, round-based)
        if (gameType == GameType.VISUAL_MEMORY) {
            startVisualMemoryGame(gameType)
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
        if (game is AnomalyPuzzleGame) {
            handleAnomalyPuzzleAnswer(currentState, game, answer.trim())
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
        GameType.COLOR_CONFUSION -> ColorConfusionGame()
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
                wrongAnswerIndex = wrongIndex,
                correctAnswerIndex = game.resultIndex,
            )
            scope.launch {
                delay(1_000)
                proceedAfterAnomalyFeedback(currentState.gameType, game)
            }
        }
    }

    private fun proceedAfterAnomalyFeedback(gameType: GameType, game: Game) {
        val elapsed = Clock.System.now().toEpochMilliseconds() - startTime
        if (elapsed > GAME_TIME_MILLIS) {
            finishGame(gameType, game)
        } else {
            game.nextRound()
            _gameState.value = GameState.Active(gameType, game)
            _gameUiState.value = game.toUiState()
        }
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
        _gameUiState.value = null
        _gameState.value = GameState.Idle

        val newHighscore = storage.putScore(gameType.id, points)

        navController.navigate(
            Finish(
                gameTypeId = gameType.id,
                score = points,
                isNewHighscore = newHighscore,
                answeredAllCorrect = game.answeredAllCorrect,
            ),
        ) {
            popUpTo(MainMenu)
        }
    }

    private fun emitGameUiState(game: VisualMemoryGame) {
        _gameUiState.value = game.toUiState()
    }
}
