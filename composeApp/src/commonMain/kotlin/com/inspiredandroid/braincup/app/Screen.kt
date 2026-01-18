package com.inspiredandroid.braincup.app

import com.inspiredandroid.braincup.games.Game
import com.inspiredandroid.braincup.games.GameType

sealed class Screen {
    data object MainMenu : Screen()

    data class Instructions(
        val gameType: GameType,
    ) : Screen()

    data class Playing(
        val gameType: GameType,
        val game: Game,
    ) : Screen()

    data class AnswerFeedback(
        val gameType: GameType,
        val game: Game,
        val isCorrect: Boolean,
        val message: String?,
    ) : Screen()

    data class Finish(
        val gameType: GameType,
        val score: Int,
        val isNewHighscore: Boolean,
        val answeredAllCorrect: Boolean,
    ) : Screen()

    data class Scoreboard(
        val gameType: GameType,
    ) : Screen()

    data object Achievements : Screen()
}
