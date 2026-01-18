package com.inspiredandroid.braincup.api

import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.games.GameType
import com.russhwolf.settings.Settings
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.StringResource
import kotlin.time.Clock
import kotlin.time.Instant

class UserStorage(
    private val settings: Settings = Settings(),
) {
    enum class Achievements(
        val titleRes: StringResource,
        val descriptionRes: StringResource,
    ) {
        MEDAL_BRONZE(
            titleRes = Res.string.achievement_bronze_medal,
            descriptionRes = Res.string.achievement_bronze_medal_desc,
        ),
        MEDAL_SILVER(
            titleRes = Res.string.achievement_silver_medal,
            descriptionRes = Res.string.achievement_silver_medal_desc,
        ),
        MEDAL_GOLD(
            titleRes = Res.string.achievement_gold_medal,
            descriptionRes = Res.string.achievement_gold_medal_desc,
        ),
        SCORES_10(
            titleRes = Res.string.achievement_10_points,
            descriptionRes = Res.string.achievement_10_points_desc,
        ),
        SCORES_100(
            titleRes = Res.string.achievement_100_points,
            descriptionRes = Res.string.achievement_100_points_desc,
        ),
        SCORES_1000(
            titleRes = Res.string.achievement_1000_points,
            descriptionRes = Res.string.achievement_1000_points_desc,
        ),
        SCORES_10000(
            titleRes = Res.string.achievement_10000_points,
            descriptionRes = Res.string.achievement_10000_points_desc,
        ),
        APP_OPEN_3(
            titleRes = Res.string.achievement_3_day_streak,
            descriptionRes = Res.string.achievement_3_day_streak_desc,
        ),
        APP_OPEN_7(
            titleRes = Res.string.achievement_7_day_streak,
            descriptionRes = Res.string.achievement_7_day_streak_desc,
        ),
        APP_OPEN_30(
            titleRes = Res.string.achievement_30_day_streak,
            descriptionRes = Res.string.achievement_30_day_streak_desc,
        ),
    }

    private val scoreAchievements =
        listOf(
            Achievements.SCORES_10,
            Achievements.SCORES_100,
            Achievements.SCORES_1000,
            Achievements.SCORES_10000,
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
        listOf(Achievements.APP_OPEN_3, Achievements.APP_OPEN_7, Achievements.APP_OPEN_30)

    fun getUnlockedAchievements(): MutableList<Achievements> = settings
        .getStringOrNull(KEY_UNLOCKED_ACHIEVEMENTS)
        ?.split(",")
        ?.filter { it.isNotEmpty() }
        ?.map {
            Achievements.valueOf(it)
        }?.toMutableList() ?: mutableListOf()

    private fun unlockAchievement(achievement: Achievements) {
        val unlockedAchievements = getUnlockedAchievements()
        unlockedAchievements.add(achievement)
        settings.putString(KEY_UNLOCKED_ACHIEVEMENTS, unlockedAchievements.joinToString(","))
    }

    fun getAppOpenCount(): Int = settings.getIntOrNull(KEY_APP_OPEN_COMBO) ?: 0

    fun putAppOpen() {
        val appOpenDay = settings.getIntOrNull(KEY_APP_OPEN_DAY) ?: -1
        val todayDay = (Clock.System.now().toEpochMilliseconds() / 86400000L).toInt()
        if (appOpenDay < todayDay) {
            val appOpenCombo =
                if (appOpenDay == todayDay - 1) {
                    settings.getInt(KEY_APP_OPEN_COMBO, 0)
                } else {
                    0
                }
            settings.putInt(KEY_APP_OPEN_COMBO, appOpenCombo + 1)
            settings.putInt(KEY_APP_OPEN_DAY, todayDay)

            val unlockedAchievements = getUnlockedAchievements()
            appOpenAchievements.forEach {
                if (!unlockedAchievements.contains(it) && hasAppOpenAchievement(it, appOpenCombo)) {
                    unlockAchievement(it)
                }
            }
        }
    }

    fun hasAppOpenAchievement(
        achievement: Achievements,
        appOpenDay: Int,
    ): Boolean = when (achievement) {
        Achievements.APP_OPEN_3 -> appOpenDay >= 3
        Achievements.APP_OPEN_7 -> appOpenDay >= 7
        Achievements.APP_OPEN_30 -> appOpenDay >= 30
        else -> true
    }

    private fun getHighscoreKey(gameId: String): String = "game_${gameId}_highscore"

    private fun getScoresKey(gameId: String): String = "game_${gameId}_scores"

    fun getHighScore(gameId: String): Int = settings.getInt(getHighscoreKey(gameId), 0)

    fun putScore(
        gameId: String,
        score: Int,
    ): Boolean {
        val newHighscore = score > getHighScore(gameId)
        if (newHighscore) {
            settings.putInt(getHighscoreKey(gameId), score)
        }
        val scoresRaw = settings.getString(getScoresKey(gameId), "")
        settings.putString(
            getScoresKey(gameId),
            "${Clock.System.now().toEpochMilliseconds()}/$score,$scoresRaw",
        )
        val updatedTotalScore = getTotalScore() + score
        settings.putInt(KEY_TOTAL_SCORE, updatedTotalScore)

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

    fun getTotalScore(): Int = settings.getIntOrNull(KEY_TOTAL_SCORE) ?: 0

    private fun hasTotalScore(
        achievement: Achievements,
        totalScore: Int,
    ): Boolean = when (achievement) {
        Achievements.SCORES_10 -> totalScore >= 10
        Achievements.SCORES_100 -> totalScore >= 100
        Achievements.SCORES_1000 -> totalScore >= 1_000
        Achievements.SCORES_10000 -> totalScore >= 10_000
        else -> true
    }

    private fun hasMedalForAllGames(achievement: Achievements): Boolean = GameType.entries.all {
        val highscore = getHighScore(it.id)
        when (achievement) {
            Achievements.MEDAL_BRONZE -> highscore > 0
            Achievements.MEDAL_SILVER -> highscore >= it.silverScore
            Achievements.MEDAL_GOLD -> highscore >= it.goldScore
            else -> true
        }
    }

    fun getScores(gameId: String): List<Pair<String, List<Int>>> {
        val scoresRaw = settings.getStringOrNull(getScoresKey(gameId)) ?: return listOf()
        return scoresRaw
            .split(",")
            .filterNot { it.isEmpty() }
            .groupBy {
                val parts = it.split("/")
                val timeInMillis = parts[0].toLongOrNull() ?: 0L
                val date = Instant.fromEpochMilliseconds(timeInMillis).toLocalDateTime(TimeZone.UTC)
                "${date.day} ${date.month.name} ${date.year}"
            }.map {
                Pair(it.key, it.value.map { score -> score.split("/")[1].toIntOrNull() ?: 0 })
            }
    }
}
