package com.inspiredandroid.braincup.api

import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.games.getId
import com.inspiredandroid.braincup.games.getScoreTable
import com.inspiredandroid.braincup.settings
import com.soywiz.klock.DateTime

class UserStorage {

    enum class Achievements {
        MEDAL_BRONZE,
        MEDAL_SILVER,
        MEDAL_GOLD
    }

    fun getAchievements(): MutableList<Achievements> {
        return settings?.getString("unlocked_achievements")?.split(",")?.filter { it.isNotEmpty() }?.map {
            Achievements.valueOf(
                it
            )
        }?.toMutableList() ?: mutableListOf()
    }

    private fun unlockAchievement(achievement: Achievements) {
        val achievements = getAchievements()
        achievements.add(achievement)
        settings?.putString("unlocked_achievements", achievements.joinToString(","))
    }

    private fun getTotalScore(): Int {
        return settings?.getInt("total_score") ?: 0
    }

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
        settings?.putInt("total_score", getTotalScore() + score)

        val achievements = getAchievements()
        val medals =
            listOf(Achievements.MEDAL_BRONZE, Achievements.MEDAL_SILVER, Achievements.MEDAL_GOLD)
        medals.forEach {
            if (!achievements.contains(it)) {
                if (hasMedalForAllGames(it)) {
                    unlockAchievement(it)
                }
            }
        }

        return newHighscore
    }

    private fun hasMedalForAllGames(achievement: Achievements): Boolean {
        return GameType.values().all {
            when (achievement) {
                Achievements.MEDAL_BRONZE -> {
                    getHighScore(it.getId()) > 0
                }
                Achievements.MEDAL_SILVER -> {
                    getHighScore(it.getId()) > it.getScoreTable()[1]
                }
                Achievements.MEDAL_GOLD -> {
                    getHighScore(it.getId()) > it.getScoreTable()[0]
                }
            }
        }
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