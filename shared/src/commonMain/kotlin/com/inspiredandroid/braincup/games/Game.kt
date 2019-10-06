package com.inspiredandroid.braincup.games

abstract class Game {
    abstract fun nextRound()
    abstract fun isCorrect(input: String): Boolean
}

