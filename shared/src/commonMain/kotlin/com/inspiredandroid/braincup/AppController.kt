package com.inspiredandroid.braincup

import com.inspiredandroid.braincup.games.*

/**
 * Controls the flow through the app and games.
 */
class AppController(private val page: Interface) {

    var startTime = 0L
    var points = 0
    private val GAME_TIME_MILLIS = 60 * 1000
    var isCorrect = false
    var plays = 0

    companion object {
        val list = listOf(Game.MENTAL_CALCULATION, Game.COLOR_CONFUSION, Game.SHERLOCK_CALCULATION)
    }

    interface Interface {
        fun showMainMenu(title: String, description: String, games: List<Game>, callback: (Game) -> Unit)
        fun showInstructions(title: String, description: String, start: (Long) -> Unit)
        fun showMentalCalculation(
            game: MentalCalculation,
            answer: (String) -> Unit,
            next: (Long) -> Unit
        )

        fun showColorConfusion(
            game: ColorConfusion,
            answer: (String) -> Unit,
            next: (Long) -> Unit
        )

        fun showSherlockCalculation(
            game: SherlockCalculation,
            answer: (String) -> Unit,
            next: (Long) -> Unit
        )

        fun showCorrectAnswerFeedback()
        fun showWrongAnswerFeedback()
        fun showFinishFeedback(rank: String, plays: Int, random: () -> Unit)
    }

    fun start() {
        page.showMainMenu("Braincup", "Improve your memory and focus.", list) { game ->
            startGame(game)
        }
    }

    private fun startGame(game: Game) {
        page.showInstructions(game.getName(), game.getDescription()) {
            startTime = it
            plays++
            when (game) {
                Game.COLOR_CONFUSION -> nextRound(ColorConfusion())
                Game.MENTAL_CALCULATION -> nextRound(MentalCalculation())
                Game.SHERLOCK_CALCULATION -> nextRound(SherlockCalculation())
            }
        }
    }

    private fun nextRound(game: GameMode) {
        game.nextRound()

        val answer: (String) -> Unit = {
            val input = it.trim()
            if (input == "quit" || input == "exit" || input == ":q") {
                start()
            } else {
                isCorrect = game.isCorrect(input)
                if (isCorrect) {
                    page.showCorrectAnswerFeedback()
                    points++
                } else {
                    page.showWrongAnswerFeedback()
                }
            }
        }
        val next: (Long) -> Unit = {
            println("$startTime / $it / $GAME_TIME_MILLIS")
            if (it - startTime > GAME_TIME_MILLIS) {
                Api.postScore(1, points) { rank ->
                    page.showFinishFeedback(rank, plays) {
                        startGame(list.random())
                    }
                }
            } else {
                nextRound(game)
            }
        }

        when (game) {
            is ColorConfusion -> page.showColorConfusion(game, answer, next)
            is MentalCalculation -> page.showMentalCalculation(game, answer, next)
            is SherlockCalculation -> page.showSherlockCalculation(game, answer, next)
        }
    }
}