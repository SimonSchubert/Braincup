package com.inspiredandroid.braincup.api

import com.inspiredandroid.braincup.settings
import com.soywiz.klock.DateTime

class UserStorage {

    private fun getHighscoreKey(gameId: String): String {
        return "game_${gameId}_highscore"
    }

    private fun getScoresKey(gameId: String): String {
        return "game_${gameId}_scores"
    }

    fun getHighScore(gameId: String): Int {
        return settings?.getInt(getHighscoreKey(gameId)) ?: 0
    }

    fun putScore(gameId: String, score: Int): Boolean {
        val newHighscore = score > getHighScore(gameId)
        if (newHighscore) {
            settings?.putInt(getHighscoreKey(gameId), score)
        }
        val scoresRaw = settings?.getString(getScoresKey(gameId))
        settings?.putString(
            getScoresKey(gameId),
            "${DateTime.now().unixMillis}/$score,$scoresRaw"
        )
        return newHighscore
    }

    fun getScores(gameId: String): List<Pair<String, List<Int>>> {
        // "1570791440000/4,1570791440000/7,1570791440000/12,1560791440000/8,1560791440000/10,1560791440000/1"
        val scoresRaw = settings?.getString(getScoresKey(gameId)) ?: return listOf()
        return scoresRaw.split(",").filterNot { it.isEmpty() }.groupBy {
            val parts = it.split("/")
            val timeInMillis = parts[0].toDoubleOrNull() ?: 0.0
            DateTime(timeInMillis).format("dd MMMM yyyy")
        }.map {
            Pair(it.key, it.value.map { it.split("/")[1].toIntOrNull() ?: 0 })
        }
    }
}