package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.GameUiState

abstract class Game {
    var answeredAllCorrect = true
    var round = 0

    abstract fun nextRound()

    abstract fun isCorrect(input: String): Boolean

    abstract fun solution(): String

    abstract fun hint(): String?

    abstract fun toUiState(): GameUiState
}
