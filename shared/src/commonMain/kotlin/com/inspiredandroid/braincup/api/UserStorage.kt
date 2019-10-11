package com.inspiredandroid.braincup.api

import com.soywiz.klock.DateTime

class UserStorage {

    fun getHighScore(gameId: String): Int {
        return settings.getInt("game_${gameId}_highscore")
    }

    fun putScore(gameId: String, score: Int): Boolean {
        val newHighscore = score > getHighScore(gameId)
        if (newHighscore) {
            settings.putInt("game_${gameId}_highscore", score)
        }
        val scoresRaw = settings.getString("game_${gameId}_scores")
        settings.putString(
            "game_${gameId}_scores",
            "$scoresRaw,${DateTime.now().unixMillis}/$score"
        )
        return newHighscore
    }

    fun getScores(gameId: String): List<Pair<String, List<Int>>> {
        // "1570791440000/4,1570791440000/7,1570791440000/12,1560791440000/8,1560791440000/10,1560791440000/1"
        val scoresRaw = settings.getString("game_${gameId}_scores")
        return scoresRaw.split(",").filterNot { it.isEmpty() }.groupBy {
            val parts = it.split("/")
            val timeInMillis = parts[0].toLong()
            DateTime(timeInMillis).format("dd MMMM yyyy")
        }.map {
            Pair(it.key, it.value.map { it.split("/")[1].toInt() })
        }
    }
}