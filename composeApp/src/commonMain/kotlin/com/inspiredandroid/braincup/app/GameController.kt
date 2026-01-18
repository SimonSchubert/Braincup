package com.inspiredandroid.braincup.app

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
    val storage: UserStorage = UserStorage(),
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _currentScreen = MutableStateFlow<Screen>(Screen.MainMenu)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

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
            GameType.MENTAL_CALCULATION,
            GameType.SHERLOCK_CALCULATION,
            GameType.CHAIN_CALCULATION,
            GameType.FRACTION_CALCULATION,
            GameType.VALUE_COMPARISON,
            GameType.GRID_SOLVER,
        )
    }

    init {
        storage.putAppOpen()
    }

    fun navigateToMainMenu() {
        _currentScreen.value = Screen.MainMenu
    }

    fun navigateToInstructions(gameType: GameType) {
        _currentScreen.value = Screen.Instructions(gameType)
    }

    fun navigateToScoreboard(gameType: GameType) {
        _currentScreen.value = Screen.Scoreboard(gameType)
    }

    fun navigateToAchievements() {
        _currentScreen.value = Screen.Achievements
    }

    fun startGame(gameType: GameType) {
        startTime = Clock.System.now().toEpochMilliseconds()
        plays++
        points = 0
        _timeRemaining.value = GAME_TIME_MILLIS

        val game = createGame(gameType)
        game.nextRound()
        game.round++

        _currentScreen.value = Screen.Playing(gameType, game)
        startTimer()
    }

    fun submitAnswer(answer: String) {
        val currentState = _currentScreen.value
        if (currentState !is Screen.Playing) return

        val game = currentState.game
        val input = answer.trim()
        val isCorrect = game.isCorrect(input)

        if (isCorrect) {
            points++
            _currentScreen.value = Screen.AnswerFeedback(
                gameType = currentState.gameType,
                game = game,
                isCorrect = true,
                message = game.hint(),
            )
        } else {
            game.answeredAllCorrect = false
            _currentScreen.value = Screen.AnswerFeedback(
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
        val currentState = _currentScreen.value
        if (currentState !is Screen.Playing) return

        val game = currentState.game
        game.answeredAllCorrect = false
        _currentScreen.value = Screen.AnswerFeedback(
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
        val currentState = _currentScreen.value
        if (currentState !is Screen.AnswerFeedback) return

        val game = currentState.game
        val currentTime = Clock.System.now().toEpochMilliseconds()
        val elapsed = currentTime - startTime

        if (elapsed > GAME_TIME_MILLIS) {
            finishGame(currentState.gameType, game)
        } else {
            game.nextRound()
            game.round++
            _currentScreen.value = Screen.Playing(currentState.gameType, game)
        }
    }

    private fun finishGame(gameType: GameType, game: Game) {
        if (game.answeredAllCorrect) {
            points++
        }

        val newHighscore = storage.putScore(gameType.id, points)

        _currentScreen.value = Screen.Finish(
            gameType = gameType,
            score = points,
            isNewHighscore = newHighscore,
            answeredAllCorrect = game.answeredAllCorrect,
        )
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
    }
}
