package com.inspiredandroid.braincup.app

import com.inspiredandroid.braincup.api.Api
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.games.*
import com.soywiz.klock.DateTime

/**
 * Controls the flow through the app and games.
 */
class AppController(private val app: AppInterface) {

    private val GAME_TIME_MILLIS = 60 * 1000
    var startTime = 0.0
    var points = 0
    var isCorrect = false
    var plays = 0
    var state = AppState.START

    companion object {
        val games = listOf(
            GameType.MENTAL_CALCULATION,
            GameType.COLOR_CONFUSION,
            GameType.SHERLOCK_CALCULATION,
            GameType.CHAIN_CALCULATION,
            GameType.FRACTION_CALCULATION,
            GameType.HEIGHT_COMPARISON
        )
    }

    fun start() {
        state = AppState.START
        app.showMainMenu(
            "Braincup", "Train your math skills, memory and focus.",
            games, { game ->
                startGame(game)
            }, { game ->
                val storage = UserStorage()
                app.showScoreboard(
                    game,
                    storage.getHighScore(game.getId()),
                    storage.getScores(game.getId())
                )
            })
    }

    private fun startGame(gameType: GameType) {
        app.showInstructions(gameType.getName(), gameType.getDescription()) {
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
            }
            nextRound(game)
        }
    }

    private fun nextRound(game: Game) {
        if (state != AppState.GAME) {
            return
        }
        game.nextRound()

        val answer: (String) -> Unit = { answer ->
            val input = answer.trim()
            isCorrect = game.isCorrect(input)
            if (isCorrect) {
                app.showCorrectAnswerFeedback()
                points++
            } else {
                app.showWrongAnswerFeedback(game.solution())
            }
        }
        val next: () -> Unit = {
            val currentTime = DateTime.now().unixMillis
            if (currentTime - startTime > GAME_TIME_MILLIS) {
                Api.postScore(
                    game.getGameType().getId(),
                    points
                ) { score: String, newHighscore: Boolean ->
                    app.showFinishFeedback(score, newHighscore, plays) {
                        startGame(games.random())
                    }
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
        }
    }
}