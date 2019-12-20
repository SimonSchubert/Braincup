package com.inspiredandroid.braincup.games

class RiddleGame : Game() {

    internal var description = ""
    internal var answers = mutableListOf<String>()

    override fun nextRound() {
    }

    override fun isCorrect(input: String): Boolean {
        return answers.contains(input.trim())
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