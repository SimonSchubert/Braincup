package com.inspiredandroid.brainup

class Gamemaster(val page: Interface) {

    var startTime = 0L
    var points = 0
    val GAME_TIME_SECONDS = 10
    var right = false
    var plays = 0

    companion object {
        val list = listOf(Game.MENTAL_CALCULATION, Game.COLOR_CONFUSION)
    }

    interface Interface {
        fun showMainMenu(title: String, description: String, games: List<Game>, callback: (Game) -> Unit)
        fun showInstructions(title: String, description: String, start: (Long) -> Unit)
        fun showMentalCalculation(
            game: MentalCalculation,
            title: String,
            showValue: Boolean,
            answer: (String) -> Unit,
            next: (Long) -> Unit
        )

        fun showColorConfusion(
            round: ColorConfusion.Round,
            title: String,
            answer: (String) -> Unit,
            next: (Long) -> Unit
        )

        fun showCorrectAnswerFeedback(title: String)
        fun showWrongAnswerFeedback(title: String)
        fun showFinishFeedback(rank: String, title: String, plays: Int, random: () -> Unit)
    }

    fun start() {
        page.showMainMenu("Braincup", "Improve your memory and focus.", list) { game ->
            startGame(game)
        }
    }

    private fun startGame(game: Game) {
        when (game) {
            Game.COLOR_CONFUSION -> page.showInstructions(game.getName(), game.getDescription()) {
                startColorConfusion(ColorConfusion(), game.getName())
                startTime = it
                plays++
            }
            Game.MENTAL_CALCULATION -> page.showInstructions(game.getName(), game.getDescription()) {
                startMentalCalculation(MentalCalculation(), game.getName(), true)
                startTime = it
                plays++
            }
        }
    }

    private fun startMentalCalculation(game: MentalCalculation, title: String, showValue: Boolean) {
        page.showMentalCalculation(game, title, showValue, {
            right = game.number.toString() == it
            if (right) {
                page.showCorrectAnswerFeedback(title)
                points++
            } else {
                page.showWrongAnswerFeedback(title)
            }
        }, {
            if (it - startTime > GAME_TIME_SECONDS * 1000) {
                Api.postScore(1, points) { rank ->
                    page.showFinishFeedback(rank, title, plays) {
                        startGame(list.random())
                    }
                }
            } else {
                startMentalCalculation(game, title, !right)
            }
        })
    }

    private fun startColorConfusion(game: ColorConfusion, title: String) {
        val round = game.nextRound()
        page.showColorConfusion(round, title, {
            if (round.isCorrect(it)) {
                page.showCorrectAnswerFeedback(title)
                points++
            } else {
                page.showWrongAnswerFeedback(title)
            }
        }, {
            if (it - startTime > GAME_TIME_SECONDS * 1000) {
                Api.postScore(1, points) { rank ->
                    page.showFinishFeedback(rank, title, plays) {
                        startGame(Gamemaster.list.random())
                    }
                }
            } else {
                startColorConfusion(game, title)
            }
        })
    }
}