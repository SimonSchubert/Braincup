package com.inspiredandroid.braincup.games

abstract class Game {
    var answeredAllCorrect = true
    abstract fun nextRound()
    abstract fun isCorrect(input: String): Boolean
    abstract fun solution(): String
    abstract fun getGameType(): GameType
}

