package com.inspiredandroid.braincup.games

abstract class Game {
    var answeredAllCorrect = true
    var round = 0
    abstract fun nextRound()
    abstract fun isCorrect(input: String): Boolean
    abstract fun solution(): String
    abstract fun getGameType(): GameType
    abstract fun hint(): String?
}

