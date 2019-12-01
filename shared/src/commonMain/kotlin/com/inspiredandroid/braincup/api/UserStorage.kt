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
        MEDAL_GOLD,
        SCORES_10,
        SCORES_100,
        SCORES_1000,
        SCORES_10000,
        APP_OPEN_7,
        APP_OPEN_30,
        APP_OPEN_356,
    }

    private val scoreAchievements = listOf(
        Achievements.SCORES_10,
        Achievements.SCORES_100,
        Achievements.SCORES_1000,
        Achievements.SCORES_10000
    )

    companion object {
        const val KEY_APP_OPEN_COMBO = "app_open_combo"
        const val KEY_APP_OPEN_DAY = "app_open_day"
        const val KEY_UNLOCKED_ACHIEVEMENTS = "unlocked_achievements"
        const val KEY_TOTAL_SCORE = "total_score"
    }

    private val medalAchievements =
        listOf(Achievements.MEDAL_BRONZE, Achievements.MEDAL_SILVER, Achievements.MEDAL_GOLD)

    private val appOpenAchievements =
        listOf(Achievements.APP_OPEN_7, Achievements.APP_OPEN_30, Achievements.APP_OPEN_356)

    fun getUnlockedAchievements(): MutableList<Achievements> {
        return settings?.getString(KEY_UNLOCKED_ACHIEVEMENTS)?.split(",")?.filter { it.isNotEmpty() }?.map {
            Achievements.valueOf(
                it
            )
        }?.toMutableList() ?: mutableListOf()
    }

    private fun unlockAchievement(achievement: Achievements) {
        val unlockedAchievements = getUnlockedAchievements()
        unlockedAchievements.add(achievement)
        settings?.putString(KEY_UNLOCKED_ACHIEVEMENTS, unlockedAchievements.joinToString(","))
    }

    fun getAppOpen(): Int {
        return settings?.getInt(KEY_APP_OPEN_COMBO, -1) ?: -1
    }

    fun putAppOpen() {
        val appOpenDay = settings?.getInt(KEY_APP_OPEN_DAY, -1) ?: -1
        val dateTime = DateTime.now()
        val todayDay = (dateTime.unixMillis / 86400000L).toInt()
        if (appOpenDay < todayDay) {
            val appOpenCombo = if (appOpenDay == todayDay - 1) {
                settings?.getInt(KEY_APP_OPEN_COMBO, 0) ?: 0
            } else {
                0
            }
            settings?.putInt(KEY_APP_OPEN_COMBO, appOpenCombo + 1)
            settings?.putInt(KEY_APP_OPEN_DAY, todayDay)

            val unlockedAchievements = getUnlockedAchievements()
            appOpenAchievements.forEach {
                if (!unlockedAchievements.contains(it) && hasAppOpenAchievement(it, appOpenCombo)) {
                    unlockAchievement(it)
                }
            }
        }
    }

    fun hasAppOpenAchievement(achievement: Achievements, appOpenDay: Int): Boolean {
        return when (achievement) {
            Achievements.APP_OPEN_7 -> {
                appOpenDay >= 7
            }
            Achievements.APP_OPEN_30 -> {
                appOpenDay >= 30
            }
            Achievements.APP_OPEN_356 -> {
                appOpenDay >= 356
            }
            else -> true
        }
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
        val updatedTotalScore = getTotalScore() + score
        settings?.putInt(KEY_TOTAL_SCORE, updatedTotalScore)

        val unlockedAchievements = getUnlockedAchievements()
        medalAchievements.forEach {
            if (!unlockedAchievements.contains(it) && hasMedalForAllGames(it)) {
                unlockAchievement(it)
            }
        }
        scoreAchievements.forEach {
            if (!unlockedAchievements.contains(it) && hasTotalScore(it, updatedTotalScore)) {
                unlockAchievement(it)
            }
        }

        return newHighscore
    }

    fun getTotalScore(): Int {
        return settings?.getInt(KEY_TOTAL_SCORE) ?: 0
    }

    private fun hasTotalScore(achievement: Achievements, totalScore: Int): Boolean {
        return GameType.values().all {
            when (achievement) {
                Achievements.SCORES_10 -> {
                    totalScore >= 10
                }
                Achievements.SCORES_100 -> {
                    totalScore >= 100
                }
                Achievements.SCORES_1000 -> {
                    totalScore >= 1_000
                }
                Achievements.SCORES_10000 -> {
                    totalScore >= 10_000
                }
                else -> true
            }
        }
    }

    private fun hasMedalForAllGames(achievement: Achievements): Boolean {
        return GameType.values().all {
            val heighscore = getHighScore(it.getId())
            when (achievement) {
                Achievements.MEDAL_BRONZE -> {
                    heighscore > 0
                }
                Achievements.MEDAL_SILVER -> {
                    heighscore >= it.getScoreTable()[1]
                }
                Achievements.MEDAL_GOLD -> {
                    heighscore >= it.getScoreTable()[0]
                }
                else -> true
            }
        }
    }

    fun getScores(gameId: String): List<Pair<String, List<Int>>> {
        // val scoresRaw = "1570791440000/80,1570791440000/7,1570791440000/12,1560791440000/8,1560791440000/10,1560791440000/1"
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