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

    private var startTime = 0L
    private var points = 0
    private var plays = 0

    companion object {
        const val GAME_TIME_MILLIS = 60 * 1_000L

        val games = listOf(
            GameType.ANOMALY_PUZZLE,
            GameType.PATH_FINDER,
            GameType.COLOR_CONFUSION,
            GameType.VISUAL_MEMORY,
            GameType.MENTAL_CALCULATION,
            GameType.SHERLOCK_CALCULATION,
            GameType.CHAIN_CALCULATION,
            GameType.FRACTION_CALCULATION,
            GameType.VALUE_COMPARISON,
            GameType.GRID_SOLVER,
        )

        fun getGameTypeById(id: String): GameType? = GameType.entries.find { it.id == id }
    }

    init {
        storage.putAppOpen()
    }

    fun navigateToMainMenu() {
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
        // Visual Memory has special handling (no timer, round-based)
        if (gameType == GameType.VISUAL_MEMORY) {
            startVisualMemoryGame(gameType)
            return
        }

        startTime = Clock.System.now().toEpochMilliseconds()
        plays++
        points = 0
        _timeRemaining.value = GAME_TIME_MILLIS

        val game = createGame(gameType)
        game.nextRound()
        game.round++

        _gameState.value = GameState.Active(gameType, game)
        navController.navigate(Playing(gameType.id))
        startTimer()
    }

    fun submitAnswer(answer: String) {
        val currentState = _gameState.value
        if (currentState !is GameState.Active) return

        val game = currentState.game
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
            game.round++
            _gameState.value = GameState.Active(currentState.gameType, game)
        }
    }

    private fun finishGame(gameType: GameType, game: Game) {
        if (game.answeredAllCorrect) {
            points++
        }

        val newHighscore = storage.putScore(gameType.id, points)

        _gameState.value = GameState.Idle
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

    fun playRandomGame() {
        val randomGame = games.random()
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
    }

    private fun startVisualMemoryGame(gameType: GameType) {
        startTime = Clock.System.now().toEpochMilliseconds()
        plays++
        points = 0

        val game = VisualMemoryGame()
        game.nextRound()
        game.round++

        _gameState.value = GameState.Active(gameType, game)
        navController.navigate(Playing(gameType.id))
        // No timer for visual memory - game is round-based, not time-based
    }

    fun submitVisualMemoryAnswer(answer: String) {
        val currentState = _gameState.value
        if (currentState !is GameState.Active) return

        val game = currentState.game as? VisualMemoryGame ?: return
        val isCorrect = game.isCorrect(answer)

        if (isCorrect) {
            // Advance to next shape in this round
            game.advanceGuess()

            if (game.isRoundComplete()) {
                // All shapes in this round identified
                points++

                if (game.isGameComplete()) {
                    // Win - completed all 9 rounds
                    finishVisualMemoryGame(game)
                } else {
                    // Proceed to next round
                    game.nextRound()
                    game.round++
                    _gameState.value = GameState.Active(
                        gameType = GameType.VISUAL_MEMORY,
                        game = game,
                        stateVersion = game.round.toLong(),
                    )
                }
            } else {
                // More shapes to identify in this round - trigger UI update
                _gameState.value = GameState.Active(
                    gameType = GameType.VISUAL_MEMORY,
                    game = game,
                    stateVersion = game.round * 100L + game.currentGuessIndex,
                )
            }
        } else {
            // Wrong answer - game over
            game.answeredAllCorrect = false
            finishVisualMemoryGame(game)
        }
    }

    private fun finishVisualMemoryGame(game: VisualMemoryGame) {
        val newHighscore = storage.putScore(GameType.VISUAL_MEMORY.id, points)

        _gameState.value = GameState.Idle
        navController.navigate(
            Finish(
                gameTypeId = GameType.VISUAL_MEMORY.id,
                score = points,
                isNewHighscore = newHighscore,
                answeredAllCorrect = game.answeredAllCorrect,
            ),
        ) {
            popUpTo(MainMenu)
        }
    }
}
