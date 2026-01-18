package com.inspiredandroid.braincup.games

class RiddleGame : Game() {
    var quest = ""
    internal var answers = mutableListOf<String>()

    override fun nextRound() {
    }

    override fun isCorrect(input: String): Boolean = answers.any { it.lowercase() == input.trim().lowercase() }

    override fun solution(): String = answers.firstOrNull() ?: ""

    override fun getGameType(): GameType = GameType.RIDDLE

    override fun hint(): String? = ""
}
