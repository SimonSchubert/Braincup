package com.inspiredandroid.braincup.games

class RiddleGame : Game() {

    var quest = ""
    internal var answers = mutableListOf<String>()

    override fun nextRound() {
    }

    override fun isCorrect(input: String): Boolean {
        return answers.any { it.lowercase() == input.trim().lowercase() }
    }

    override fun solution(): String {
        return answers.firstOrNull() ?: ""
    }

    override fun getGameType(): GameType {
        return GameType.RIDDLE
    }

    override fun hint(): String? {
        return ""
    }

}