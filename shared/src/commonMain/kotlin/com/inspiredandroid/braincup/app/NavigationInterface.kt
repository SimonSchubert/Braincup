package com.inspiredandroid.braincup.app

import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.games.*

interface NavigationInterface {
    fun showMainMenu(
        title: String,
        description: String,
        games: List<GameType>,
        showInstructions: (GameType) -> Unit,
        showScore: (GameType) -> Unit,
        showAchievements: () -> Unit,
        createChallenge: () -> Unit,
        storage: UserStorage,
        totalScore: Int,
        appOpenCount: Int
    )

    fun showInstructions(
        gameType: GameType,
        title: String,
        description: String,
        showChallengeInfo: Boolean = false,
        hasSecret: Boolean = false,
        start: () -> Unit
    )

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
        title: String,
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

    fun showAnomalyPuzzle(
        game: AnomalyPuzzleGame,
        answer: (String) -> Unit,
        next: () -> Unit
    )

    fun showRiddle(
        game: RiddleGame,
        title: String,
        answer: (String) -> Unit,
        next: () -> Unit
    )

    fun showCorrectChallengeAnswerFeedback(solution: String, secret: String, url: String)
    fun showWrongChallengeAnswerFeedback(url: String)
    fun showCorrectAnswerFeedback(gameType: GameType, hint: String?)
    fun showWrongAnswerFeedback(gameType: GameType, solution: String)
    fun showFinishFeedback(
        gameType: GameType,
        rank: String,
        newHighscore: Boolean,
        answeredAllCorrect: Boolean,
        plays: Int,
        random: () -> Unit,
        again: () -> Unit
    )

    fun showScoreboard(gameType: GameType, highscore: Int, scores: List<Pair<String, List<Int>>>)
    fun showAchievements(
        allAchievements: List<UserStorage.Achievements>,
        unlockedAchievements: List<UserStorage.Achievements>
    )

    fun showCreateChallengeMenu(games: List<GameType>, answer: (GameType) -> Unit)
    fun showCreateSherlockCalculationChallenge(title: String, description: String)
    fun showCreateRiddleChallenge(title: String)
}