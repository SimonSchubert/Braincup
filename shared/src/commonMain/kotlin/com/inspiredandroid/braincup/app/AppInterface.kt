package com.inspiredandroid.braincup.app

import com.inspiredandroid.braincup.games.*

interface AppInterface {
    fun showMainMenu(
        title: String,
        description: String,
        games: List<GameType>,
        callback: (GameType) -> Unit
    )

    fun showInstructions(title: String, description: String, start: (Long) -> Unit)
    fun showMentalCalculation(
        game: MentalCalculationGame,
        answer: (String) -> Unit,
        next: (Long) -> Unit
    )

    fun showColorConfusion(
        game: ColorConfusionGame,
        answer: (String) -> Unit,
        next: (Long) -> Unit
    )

    fun showSherlockCalculation(
        game: SherlockCalculationGame,
        answer: (String) -> Unit,
        next: (Long) -> Unit
    )

    fun showChainCalculation(
        game: ChainCalculationGame,
        answer: (String) -> Unit,
        next: (Long) -> Unit
    )

    fun showCorrectAnswerFeedback()
    fun showWrongAnswerFeedback()
    fun showFinishFeedback(rank: String, plays: Int, random: () -> Unit)
}