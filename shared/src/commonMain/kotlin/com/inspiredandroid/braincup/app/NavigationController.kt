package com.inspiredandroid.braincup.app

import com.inspiredandroid.braincup.api.Api
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.challenge.ChallengeData
import com.inspiredandroid.braincup.challenge.RiddleChallengeData
import com.inspiredandroid.braincup.challenge.SherlockCalculationChallengeData
import com.inspiredandroid.braincup.gCoroutineScope
import com.inspiredandroid.braincup.games.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Clock

/**
 * Controls the flow through the app and games.
 */
class NavigationController(private val app: NavigationInterface) {

    private val GAME_TIME_MILLIS = 60 * 1_000
    var startTime = 0L
    var points = 0
    var plays = 0
    var state = AppState.START
    val storage = UserStorage()

    companion object {
        val games = listOf(
            GameType.ANOMALY_PUZZLE,
            GameType.PATH_FINDER,
            GameType.MENTAL_CALCULATION,
            GameType.SHERLOCK_CALCULATION,
            GameType.CHAIN_CALCULATION,
            GameType.FRACTION_CALCULATION,
            GameType.VALUE_COMPARISON,
            GameType.COLOR_CONFUSION,
            GameType.GRID_SOLVER
        )
    }

    fun start(
        state: AppState = AppState.START,
        gameType: GameType? = null,
        challengeData: ChallengeData? = null
    ) {
        storage.putAppOpen()
        when (state) {
            AppState.START -> startMenu()
            AppState.INSTRUCTIONS -> gameType?.let { startInstructions(it) }
            AppState.SCOREBOARD -> gameType?.let { startScoreboard(it) }
            AppState.CREATE_CHALLENGE -> startCreateChallenge()
            AppState.CHALLENGE -> challengeData?.let { startChallenge(it) }
            else -> throw IllegalArgumentException("App state not allowed")
        }
    }

    private fun startMenu() {
        state = AppState.START
        app.showMainMenu(
            "Braincup", "Train your math skills, memory and focus.",
            games, { game ->
                startInstructions(game)
            }, { game ->
                startScoreboard(game)
            }, {
                startAchievements()
            }, {
                startCreateChallenge()
            }, storage, storage.getTotalScore(), storage.getAppOpenCount()
        )
    }

    private fun startCreateChallenge() {
        state = AppState.CREATE_CHALLENGE
        app.showCreateChallengeMenu(listOf(GameType.SHERLOCK_CALCULATION, GameType.RIDDLE)) {
            when (it) {
                GameType.SHERLOCK_CALCULATION -> app.showCreateSherlockCalculationChallenge(
                    "Create challenge",
                    ""
                )
                GameType.RIDDLE -> app.showCreateRiddleChallenge("Create challenge")
                else -> throw IllegalArgumentException("Game type not allowed")
            }
        }
    }

    private fun startChallenge(challengeData: ChallengeData) {
        app.showInstructions(
            challengeData.gameType,
            challengeData.gameType.getName(),
            challengeData.gameType.getDescription(false),
            showChallengeInfo = true,
            hasSecret = challengeData.challengeSecret.isNotEmpty()
        ) {
            when (challengeData) {
                is SherlockCalculationChallengeData -> {
                    val game = SherlockCalculationGame()
                    game.result = challengeData.goal
                    game.numbers.addAll(challengeData.numbers)
                    app.showSherlockCalculation(game, challengeData.getTitle()) { answer ->
                        val input = answer.trim()
                        if (game.isCorrect(input)) {
                            app.showCorrectChallengeAnswerFeedback(
                                answer,
                                challengeData.challengeSecret,
                                challengeData.url
                            )
                        } else {
                            app.showWrongChallengeAnswerFeedback(challengeData.url)
                        }
                    }
                }
                is RiddleChallengeData -> {
                    val game = RiddleGame()
                    game.quest = challengeData.description
                    game.answers.addAll(challengeData.answers)
                    app.showRiddle(game, challengeData.getTitle()) { answer ->
                        val input = answer.trim()
                        if (game.isCorrect(input)) {
                            app.showCorrectChallengeAnswerFeedback(
                                answer,
                                challengeData.challengeSecret,
                                challengeData.url
                            )
                        } else {
                            app.showWrongChallengeAnswerFeedback(challengeData.url)
                        }
                    }
                }
                else -> {}
            }
        }
    }

    private fun startAchievements() {
        state = AppState.ACHIEVEMENTS
        app.showAchievements(
            UserStorage.Achievements.values().sorted(),
            storage.getUnlockedAchievements()
        )
    }

    private fun startScoreboard(gameType: GameType) {
        state = AppState.SCOREBOARD
        app.showScoreboard(
            gameType,
            storage.getHighScore(gameType.getId()),
            storage.getScores(gameType.getId())
        )
    }

    private fun startInstructions(gameType: GameType) {
        state = AppState.INSTRUCTIONS
        app.showInstructions(gameType, gameType.getName(), gameType.getDescription()) {
            state = AppState.GAME
            startTime = Clock.systemUTC().millis()
            plays++
            points = 0
            val game = when (gameType) {
                GameType.COLOR_CONFUSION -> ColorConfusionGame()
                GameType.MENTAL_CALCULATION -> MentalCalculationGame()
                GameType.SHERLOCK_CALCULATION -> SherlockCalculationGame()
                GameType.CHAIN_CALCULATION -> ChainCalculationGame()
                GameType.VALUE_COMPARISON -> ValueComparisonGame()
                GameType.FRACTION_CALCULATION -> FractionCalculationGame()
                GameType.ANOMALY_PUZZLE -> AnomalyPuzzleGame()
                GameType.PATH_FINDER -> PathFinderGame()
                GameType.RIDDLE -> RiddleGame()
                GameType.GRID_SOLVER -> GridSolverGame()
            }
            nextRound(game)
        }
    }

    private fun nextRound(game: Game) {
        if (state != AppState.GAME) {
            return
        }
        game.nextRound()
        game.round++

        val answer: (String) -> Unit = { answer ->
            gCoroutineScope.launch {

                val input = answer.trim()
                if (game.isCorrect(input)) {
                    app.showCorrectAnswerFeedback(game.getGameType(), game.hint())
                    points++
                } else {
                    app.showWrongAnswerFeedback(game.getGameType(), game.solution())
                    game.answeredAllCorrect = false
                }


                delay(1_000)

                val currentTime = Clock.systemUTC().millis()
                if (currentTime - startTime > GAME_TIME_MILLIS) {
                    if (game.answeredAllCorrect) {
                        points++
                    }
                    Api.postScore(
                        game.getGameType().getId(),
                        points
                    ) { score: String, newHighscore: Boolean ->
                        app.showFinishFeedback(
                            game.getGameType(),
                            score,
                            newHighscore,
                            game.answeredAllCorrect,
                            plays,
                            { startInstructions(games.random()) },
                            { startInstructions(game.getGameType()) })
                    }
                } else {
                    nextRound(game)
                }
            }
        }

        when (game) {
            is ColorConfusionGame -> app.showColorConfusion(game, answer)
            is MentalCalculationGame -> app.showMentalCalculation(game, answer)
            is SherlockCalculationGame -> app.showSherlockCalculation(
                game,
                game.getName(),
                answer
            )
            is ChainCalculationGame -> app.showChainCalculation(game, answer)
            is ValueComparisonGame -> app.showValueComparison(game, answer)
            is FractionCalculationGame -> app.showFractionCalculation(game, answer)
            is AnomalyPuzzleGame -> app.showAnomalyPuzzle(game, answer)
            is PathFinderGame -> app.showPathFinder(game, answer)
            is GridSolverGame -> app.showGridSolver(game, answer)
        }
    }
}