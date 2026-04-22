package com.inspiredandroid.braincup.api

import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.games.GameType
import com.russhwolf.settings.Settings
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
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
        const val KEY_TOTAL_APP_OPENS = "total_app_opens"
        const val KEY_AUDIO_MUTED = "audio_muted"
        const val KEY_SESSION_DAY = "session_day"
        const val KEY_SESSION_GAME_IDS = "session_game_ids"
        const val KEY_SESSION_SCORES = "session_scores"
        const val KEY_SESSION_INDEX = "session_index"
        const val KEY_LAST_COMPLETED_SESSION_DAY = "last_completed_session_day"
        const val KEY_STREAK_MIGRATED_V2 = "streak_migrated_v2"
        const val KEY_TOTAL_XP = "total_xp"
        const val KEY_XP_SEEDED = "xp_seeded_v1"
        const val SESSION_GAME_COUNT = 5
        const val SESSION_COMPLETION_XP = 50

        fun levelForXp(xp: Int): Int {
            if (xp <= 0) return 1
            var level = 1
            while (xpThresholdForLevel(level + 1) <= xp) level++
            return level
        }

        fun xpThresholdForLevel(level: Int): Int {
            val n = (level - 1).coerceAtLeast(0)
            return 50 * n * n
        }

        fun xpSpanForLevel(level: Int): Int = xpThresholdForLevel(level + 1) - xpThresholdForLevel(level)

        fun xpIntoLevel(xp: Int): Int = (xp - xpThresholdForLevel(levelForXp(xp))).coerceAtLeast(0)

        val MILESTONE_TITLES: List<MilestoneTitle> = listOf(
            MilestoneTitle(1, Res.string.title_novice),
            MilestoneTitle(5, Res.string.title_apprentice),
            MilestoneTitle(10, Res.string.title_scholar),
            MilestoneTitle(20, Res.string.title_sage),
            MilestoneTitle(30, Res.string.title_master),
            MilestoneTitle(50, Res.string.title_grandmaster),
        )

        fun currentTitleRes(level: Int): StringResource = MILESTONE_TITLES.lastOrNull { it.level <= level }?.titleRes ?: Res.string.title_novice

        fun isMilestoneLevel(level: Int): Boolean = MILESTONE_TITLES.any { it.level == level }
    }

    data class MilestoneTitle(val level: Int, val titleRes: StringResource)

    data class LevelChange(
        val oldLevel: Int,
        val newLevel: Int,
        val totalXpBefore: Int,
        val totalXpAfter: Int,
    ) {
        val isMilestone: Boolean
            get() = (oldLevel + 1..newLevel).any { isMilestoneLevel(it) }
    }

    fun getTotalXp(): Int {
        if (!settings.getBoolean(KEY_XP_SEEDED, false)) {
            val seed = getTotalScore()
            settings.putInt(KEY_TOTAL_XP, seed)
            settings.putBoolean(KEY_XP_SEEDED, true)
            return seed
        }
        return settings.getInt(KEY_TOTAL_XP, 0)
    }

    fun getLevel(): Int = levelForXp(getTotalXp())

    private fun addXp(amount: Int): LevelChange? {
        if (amount <= 0) return null
        val before = getTotalXp()
        val after = before + amount
        settings.putInt(KEY_TOTAL_XP, after)
        val oldLevel = levelForXp(before)
        val newLevel = levelForXp(after)
        return if (newLevel > oldLevel) {
            LevelChange(oldLevel, newLevel, before, after)
        } else {
            null
        }
    }

    fun isAudioMuted(): Boolean = settings.getBoolean(KEY_AUDIO_MUTED, false)

    fun setAudioMuted(muted: Boolean) {
        settings.putBoolean(KEY_AUDIO_MUTED, muted)
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

    fun getSessionStreak(): Int = settings.getIntOrNull(KEY_APP_OPEN_COMBO) ?: 0

    fun incrementAndGetTotalAppOpens(): Int {
        val count = settings.getInt(KEY_TOTAL_APP_OPENS, 0) + 1
        settings.putInt(KEY_TOTAL_APP_OPENS, count)
        return count
    }

    fun migrateStreakIfNeeded() {
        if (settings.getBoolean(KEY_STREAK_MIGRATED_V2, false)) return
        settings.putInt(KEY_APP_OPEN_COMBO, 0)
        settings.remove(KEY_APP_OPEN_DAY)
        settings.putBoolean(KEY_STREAK_MIGRATED_V2, true)
    }

    fun hasStreakAchievement(
        achievement: Achievements,
        streak: Int,
    ): Boolean = when (achievement) {
        Achievements.APP_OPEN_3 -> streak >= 3
        Achievements.APP_OPEN_7 -> streak >= 7
        Achievements.APP_OPEN_30 -> streak >= 30
        else -> true
    }

    private fun todayEpochDay(): Int = (Clock.System.now().toEpochMilliseconds() / 86400000L).toInt()

    fun getOrCreateTodaySession(generateGameIds: () -> List<String>): SessionState {
        val today = todayEpochDay()
        val storedDay = settings.getIntOrNull(KEY_SESSION_DAY) ?: -1
        if (storedDay != today) {
            val ids = generateGameIds()
            settings.putInt(KEY_SESSION_DAY, today)
            settings.putString(KEY_SESSION_GAME_IDS, ids.joinToString(","))
            settings.putString(KEY_SESSION_SCORES, "")
            settings.putInt(KEY_SESSION_INDEX, 0)
            return SessionState(today, ids, emptyList(), 0)
        }
        val ids = settings.getString(KEY_SESSION_GAME_IDS, "")
            .split(",")
            .filter { it.isNotEmpty() }
        val scores = settings.getString(KEY_SESSION_SCORES, "")
            .split(",")
            .filter { it.isNotEmpty() }
            .mapNotNull { it.toIntOrNull() }
        val index = settings.getInt(KEY_SESSION_INDEX, 0)
        return SessionState(today, ids, scores, index)
    }

    fun appendSessionScore(score: Int) {
        val scoresRaw = settings.getString(KEY_SESSION_SCORES, "")
        val updated = if (scoresRaw.isEmpty()) score.toString() else "$scoresRaw,$score"
        settings.putString(KEY_SESSION_SCORES, updated)
        settings.putInt(KEY_SESSION_INDEX, settings.getInt(KEY_SESSION_INDEX, 0) + 1)
    }

    fun isSessionCompletedToday(): Boolean {
        val today = todayEpochDay()
        return settings.getIntOrNull(KEY_LAST_COMPLETED_SESSION_DAY) == today
    }

    data class SessionCompletionResult(
        val newStreak: Int,
        val xpGained: Int,
        val levelChange: LevelChange?,
    )

    fun recordSessionCompleted(): SessionCompletionResult {
        val today = todayEpochDay()
        val lastCompleted = settings.getIntOrNull(KEY_LAST_COMPLETED_SESSION_DAY) ?: -1
        if (lastCompleted == today) {
            return SessionCompletionResult(getSessionStreak(), 0, null)
        }

        val newStreak = if (lastCompleted == today - 1) getSessionStreak() + 1 else 1
        settings.putInt(KEY_APP_OPEN_COMBO, newStreak)
        settings.putInt(KEY_LAST_COMPLETED_SESSION_DAY, today)

        val unlockedAchievements = getUnlockedAchievements()
        appOpenAchievements.forEach {
            if (!unlockedAchievements.contains(it) && hasStreakAchievement(it, newStreak)) {
                unlockAchievement(it)
            }
        }

        val levelChange = addXp(SESSION_COMPLETION_XP)
        return SessionCompletionResult(newStreak, SESSION_COMPLETION_XP, levelChange)
    }

    data class SessionState(
        val epochDay: Int,
        val gameIds: List<String>,
        val scores: List<Int>,
        val currentIndex: Int,
    )

    private fun getHighscoreKey(gameId: String): String = "game_${gameId}_highscore"

    private fun getScoresKey(gameId: String): String = "game_${gameId}_scores"

    private fun getLastRoundKey(gameId: String): String = "game_${gameId}_last_round"

    fun getHighScore(gameId: String): Int = settings.getInt(getHighscoreKey(gameId), 0)

    fun getLastRound(gameId: String): Int = settings.getInt(getLastRoundKey(gameId), 0)

    fun putLastRound(gameId: String, round: Int) {
        settings.putInt(getLastRoundKey(gameId), round.coerceAtLeast(0))
    }

    data class ScoreResult(
        val newHighscore: Boolean,
        val xpGained: Int,
        val levelChange: LevelChange?,
    )

    fun putScore(
        gameId: String,
        score: Int,
    ): ScoreResult {
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

        val levelChange = addXp(score)
        return ScoreResult(newHighscore, score, levelChange)
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

    data class ScoreGroup(val day: Int, val month: Int, val year: Int, val scores: List<Int>)

    fun getScores(gameId: String): List<ScoreGroup> {
        val scoresRaw = settings.getStringOrNull(getScoresKey(gameId)) ?: return listOf()
        return scoresRaw
            .split(",")
            .filterNot { it.isEmpty() }
            .groupBy {
                val parts = it.split("/")
                val timeInMillis = parts[0].toLongOrNull() ?: 0L
                val date = Instant.fromEpochMilliseconds(timeInMillis).toLocalDateTime(TimeZone.UTC)
                Triple(date.day, date.month.number, date.year)
            }.map { (key, values) ->
                ScoreGroup(key.first, key.second, key.third, values.map { it.split("/")[1].toIntOrNull() ?: 0 })
            }
    }
}
