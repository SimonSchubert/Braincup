package com.inspiredandroid.braincup.app

import com.inspiredandroid.braincup.games.*

interface AppInterface {
    fun showMainMenu(
        title: String,
        description: String,
        games: List<GameType>,
        instructions: (GameType) -> Unit,
        score: (GameType) -> Unit,
        achievements: () -> Unit
    )

    fun showInstructions(title: String, description: String, start: () -> Unit)
    fun showMentalCalculation(
        game: MentalCalculationGame,
        answer: (String) -> Unit,
        next: () -> Unit
    )

    fun showColorConfusion(
        game: ColorConfusionGame,
        answer: (String) -> Unit,
        next: () -> Unit
    )

    fun showSherlockCalculation(
        game: SherlockCalculationGame,
        answer: (String) -> Unit,
        next: () -> Unit
    )

    fun showChainCalculation(
        game: ChainCalculationGame,
        answer: (String) -> Unit,
        next: () -> Unit
    )

    fun showHeightComparison(
        game: HeightComparisonGame,
        answer: (String) -> Unit,
        next: () -> Unit
    )

    fun showFractionCalculation(
        game: FractionCalculationGame,
        answer: (String) -> Unit,
        next: () -> Unit
    )

    fun showCorrectAnswerFeedback()
    fun showWrongAnswerFeedback(solution: String)
    fun showFinishFeedback(
        rank: String,
        newHighscore: Boolean,
        answeredAllCorrect: Boolean,
        plays: Int,
        random: () -> Unit
    )

    fun showScoreboard(game: GameType, highscore: Int, scores: List<Pair<String, List<Int>>>)
    fun showAchievements()
}