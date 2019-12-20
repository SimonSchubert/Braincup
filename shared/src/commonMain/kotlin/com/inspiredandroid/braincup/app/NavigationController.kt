package com.inspiredandroid.braincup.app

import com.inspiredandroid.braincup.api.Api
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.games.*
import com.soywiz.klock.DateTime

/**
 * Controls the flow through the app and games.
 */
class NavigationController(private val app: NavigationInterface) {

    private val GAME_TIME_MILLIS = 60 * 1000
    var startTime = 0.0
    var points = 0
    var plays = 0
    var state = AppState.START
    val storage = UserStorage()

    companion object {
        val games = listOf(
            GameType.ANOMALY_PUZZLE,
            GameType.MENTAL_CALCULATION,
            GameType.SHERLOCK_CALCULATION,
            GameType.CHAIN_CALCULATION,
            GameType.FRACTION_CALCULATION,
            GameType.HEIGHT_COMPARISON,
            GameType.COLOR_CONFUSION
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
        app.showCreateChallenge("Create challenge", "")
    }

    private fun startChallenge(challengeData: ChallengeData) {
        when (challengeData) {
            is SherlockCalculationChallengeData -> {
                app.showInstructions(
                    GameType.SHERLOCK_CALCULATION,
                    GameType.SHERLOCK_CALCULATION.getName(),
                    GameType.SHERLOCK_CALCULATION.getDescription(false),
                    showChallengeInfo = true
                ) {
                    val game = SherlockCalculationGame()
                    game.result = challengeData.goal
                    game.numbers.addAll(challengeData.numbers)
                    app.showSherlockCalculation(game, { answer ->
                        val input = answer.trim()
                        if (game.isCorrect(input)) {
                            app.showCorrectChallengeAnswerFeedback(answer, challengeData.getUrl())
                        } else {
                            app.showWrongChallengeAnswerFeedback(challengeData.getUrl())
                        }
                    }, {})
                }
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
            startTime = DateTime.now().unixMillis
            plays++
            points = 0
            val game = when (gameType) {
                GameType.COLOR_CONFUSION -> ColorConfusionGame()
                GameType.MENTAL_CALCULATION -> MentalCalculationGame()
                GameType.SHERLOCK_CALCULATION -> SherlockCalculationGame()
                GameType.CHAIN_CALCULATION -> ChainCalculationGame()
                GameType.HEIGHT_COMPARISON -> HeightComparisonGame()
                GameType.FRACTION_CALCULATION -> FractionCalculationGame()
                GameType.ANOMALY_PUZZLE -> AnomalyPuzzleGame()
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
            val input = answer.trim()
            if (game.isCorrect(input)) {
                app.showCorrectAnswerFeedback(game.getGameType(), game.hint())
                points++
            } else {
                app.showWrongAnswerFeedback(game.getGameType(), game.solution())
                game.answeredAllCorrect = false
            }
        }
        val next: () -> Unit = {
            val currentTime = DateTime.now().unixMillis
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

        when (game) {
            is ColorConfusionGame -> app.showColorConfusion(game, answer, next)
            is MentalCalculationGame -> app.showMentalCalculation(game, answer, next)
            is SherlockCalculationGame -> app.showSherlockCalculation(game, answer, next)
            is ChainCalculationGame -> app.showChainCalculation(game, answer, next)
            is HeightComparisonGame -> app.showHeightComparison(game, answer, next)
            is FractionCalculationGame -> app.showFractionCalculation(game, answer, next)
            is AnomalyPuzzleGame -> app.showAnomalyPuzzle(game, answer, next)
        }
    }
}