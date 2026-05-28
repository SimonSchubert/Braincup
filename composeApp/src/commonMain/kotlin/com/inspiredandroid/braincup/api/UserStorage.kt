package com.inspiredandroid.braincup.api

import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.games.getGameTypeById
import com.inspiredandroid.braincup.ui.theme.ThemeMode
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
        GOLD_MINI_SUDOKU(Res.string.achievement_gold_mini_sudoku, Res.string.achievement_gold_mini_sudoku_desc),
        GOLD_MINI_CHESS(Res.string.achievement_gold_mini_chess, Res.string.achievement_gold_mini_chess_desc),
        GOLD_LIGHTS_OUT(Res.string.achievement_gold_lights_out, Res.string.achievement_gold_lights_out_desc),
        GOLD_SLIDING_PUZZLE(Res.string.achievement_gold_sliding_puzzle, Res.string.achievement_gold_sliding_puzzle_desc),
        GOLD_PATH_FINDER(Res.string.achievement_gold_path_finder, Res.string.achievement_gold_path_finder_desc),
        GOLD_ANOMALY_PUZZLE(Res.string.achievement_gold_anomaly_puzzle, Res.string.achievement_gold_anomaly_puzzle_desc),
        GOLD_GHOST_GRID(Res.string.achievement_gold_ghost_grid, Res.string.achievement_gold_ghost_grid_desc),
        GOLD_VISUAL_MEMORY(Res.string.achievement_gold_visual_memory, Res.string.achievement_gold_visual_memory_desc),
        GOLD_COLORED_SHAPES(Res.string.achievement_gold_colored_shapes, Res.string.achievement_gold_colored_shapes_desc),
        GOLD_SHERLOCK_CALCULATION(Res.string.achievement_gold_sherlock_calculation, Res.string.achievement_gold_sherlock_calculation_desc),
        GOLD_MENTAL_CALCULATION(Res.string.achievement_gold_mental_calculation, Res.string.achievement_gold_mental_calculation_desc),
        GOLD_CHAIN_CALCULATION(Res.string.achievement_gold_chain_calculation, Res.string.achievement_gold_chain_calculation_desc),
        GOLD_FRACTION_CALCULATION(Res.string.achievement_gold_fraction_calculation, Res.string.achievement_gold_fraction_calculation_desc),
        GOLD_VALUE_COMPARISON(Res.string.achievement_gold_value_comparison, Res.string.achievement_gold_value_comparison_desc),
        GOLD_PATTERN_SEQUENCE(Res.string.achievement_gold_pattern_sequence, Res.string.achievement_gold_pattern_sequence_desc),
        GOLD_COLOR_CONFUSION(Res.string.achievement_gold_color_confusion, Res.string.achievement_gold_color_confusion_desc),
        GOLD_ORBIT_TRACKER(Res.string.achievement_gold_orbit_tracker, Res.string.achievement_gold_orbit_tracker_desc),
        GOLD_FLASH_CROWD(Res.string.achievement_gold_flash_crowd, Res.string.achievement_gold_flash_crowd_desc),
        GOLD_SCHULTE_TABLE(Res.string.achievement_gold_schulte_table, Res.string.achievement_gold_schulte_table_desc),
        GOLD_DIGIT_MEMORY(Res.string.achievement_gold_digit_memory, Res.string.achievement_gold_digit_memory_desc),
        GOLD_SPOT_THE_NEW(Res.string.achievement_gold_spot_the_new, Res.string.achievement_gold_spot_the_new_desc),
        TOTAL_SCORE_10K(Res.string.achievement_mind_marathoner, Res.string.achievement_mind_marathoner_desc),
        STREAK_30(Res.string.achievement_iron_streak, Res.string.achievement_iron_streak_desc),
        ;

        companion object {
            fun forGameGold(gameType: GameType): Achievements? = entries.firstOrNull { it.name == "GOLD_${gameType.name}" }
        }
    }

    companion object {
        const val TOTAL_SCORE_10K_TARGET = 10_000
        const val STREAK_30_TARGET = 30

        const val KEY_APP_OPEN_COMBO = "app_open_combo"
        const val KEY_APP_OPEN_DAY = "app_open_day"
        const val KEY_UNLOCKED_ACHIEVEMENTS = "unlocked_achievements"
        const val KEY_TOTAL_SCORE = "total_score"
        const val KEY_TOTAL_APP_OPENS = "total_app_opens"
        const val KEY_AUDIO_MUTED = "audio_muted"
        const val KEY_COLORBLIND_PALETTE = "colorblind_palette"
        const val KEY_HAPTIC_ENABLED = "haptic_enabled"
        const val KEY_SESSION_DAY = "session_day"
        const val KEY_SESSION_GAME_IDS = "session_game_ids"
        const val KEY_SESSION_SCORES = "session_scores"
        const val KEY_SESSION_INDEX = "session_index"
        const val KEY_LAST_COMPLETED_SESSION_DAY = "last_completed_session_day"
        const val KEY_STREAK_MIGRATED_V2 = "streak_migrated_v2"
        const val KEY_TOTAL_XP = "total_xp"
        const val KEY_XP_SEEDED = "xp_seeded_v1"
        const val KEY_MINI_CHESS_DIFFICULTY = "mini_chess_difficulty"
        const val KEY_THEME_MODE = "theme_mode"
        const val KEY_NORMAL_SUDOKU_COMPLETED = "normal_sudoku_completed"
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

    /**
     * Raise the per-game high score to [remoteScore] if it beats the current local value
     * under the game's scoring direction (e.g. restoring from a Play Games leaderboard on a
     * fresh install). Total XP is restored separately via [restoreTotalXpIfHigher], so this
     * intentionally does not award XP.
     *
     * Returns true if the local value was changed.
     */
    fun restoreHighScoreIfHigher(gameId: String, remoteScore: Int): Boolean {
        if (remoteScore <= 0) return false
        val gameType = getGameTypeById(gameId)
        val current = getHighScore(gameId)
        val isBetter = if (gameType?.lowerScoreIsBetter == true) {
            current == 0 || remoteScore < current
        } else {
            remoteScore > current
        }
        if (!isBetter) return false
        settings.putInt(getHighscoreKey(gameId), remoteScore)
        if (gameType != null && gameType.meetsScore(remoteScore, gameType.goldScore)) {
            Achievements.forGameGold(gameType)?.let { unlockAchievement(it) }
        }
        return true
    }

    /**
     * Raise local XP to [remoteXp] if it's higher than the current value (e.g. restoring
     * progress from the Brain Cup leaderboard on a fresh install).
     *
     * Returns true if the local value was changed.
     */
    fun restoreTotalXpIfHigher(remoteXp: Int): Boolean {
        if (remoteXp <= 0) return false
        val current = if (settings.getBoolean(KEY_XP_SEEDED, false)) {
            settings.getInt(KEY_TOTAL_XP, 0)
        } else {
            getTotalScore()
        }
        if (remoteXp <= current) return false
        settings.putInt(KEY_TOTAL_XP, remoteXp)
        settings.putBoolean(KEY_XP_SEEDED, true)
        if (remoteXp >= TOTAL_SCORE_10K_TARGET) {
            unlockAchievement(Achievements.TOTAL_SCORE_10K)
        }
        return true
    }

    private fun addXp(amount: Int): LevelChange? {
        if (amount <= 0) return null
        val before = getTotalXp()
        val after = before + amount
        settings.putInt(KEY_TOTAL_XP, after)
        PlayGamesBridge.onSubmitTotalXp?.invoke(after)
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

    fun isColorblindPaletteEnabled(): Boolean = settings.getBoolean(KEY_COLORBLIND_PALETTE, false)

    fun setColorblindPaletteEnabled(enabled: Boolean) {
        settings.putBoolean(KEY_COLORBLIND_PALETTE, enabled)
    }

    fun isHapticEnabled(): Boolean = settings.getBoolean(KEY_HAPTIC_ENABLED, true)

    fun setHapticEnabled(enabled: Boolean) {
        settings.putBoolean(KEY_HAPTIC_ENABLED, enabled)
    }

    /** Selected app theme. Defaults to [ThemeMode.SYSTEM]. */
    fun getThemeMode(): ThemeMode = runCatching { ThemeMode.valueOf(settings.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name)) }
        .getOrDefault(ThemeMode.SYSTEM)

    fun setThemeMode(mode: ThemeMode) {
        settings.putString(KEY_THEME_MODE, mode.name)
    }

    /** Mini Chess AI search depth chosen on the instructions screen. Defaults to 3 (Medium). */
    fun getMiniChessDifficulty(): Int = settings.getInt(KEY_MINI_CHESS_DIFFICULTY, 3)

    fun setMiniChessDifficulty(depth: Int) {
        settings.putInt(KEY_MINI_CHESS_DIFFICULTY, depth)
    }

    private fun normalSudokuProgressKey(id: String): String = "normal_sudoku_progress_$id"

    fun getCompletedNormalSudokuIds(): Set<String> = settings
        .getStringOrNull(KEY_NORMAL_SUDOKU_COMPLETED)
        ?.split(",")
        ?.filter { it.isNotEmpty() }
        ?.toSet()
        ?: emptySet()

    fun markNormalSudokuCompleted(id: String) {
        val current = getCompletedNormalSudokuIds()
        if (id in current) return
        val updated = current + id
        settings.putString(KEY_NORMAL_SUDOKU_COMPLETED, updated.joinToString(","))
        settings.remove(normalSudokuProgressKey(id))
    }

    /** Resume state: 81-char board where '0' = empty user cell, '1'..'9' = entered digit. */
    fun getNormalSudokuProgress(id: String): String? = settings.getStringOrNull(normalSudokuProgressKey(id))?.takeIf { it.length == 81 }

    fun saveNormalSudokuProgress(id: String, board: String) {
        if (board.length != 81) return
        settings.putString(normalSudokuProgressKey(id), board)
    }

    fun clearNormalSudokuProgress(id: String) {
        settings.remove(normalSudokuProgressKey(id))
    }

    fun getUnlockedAchievements(): MutableList<Achievements> = settings
        .getStringOrNull(KEY_UNLOCKED_ACHIEVEMENTS)
        ?.split(",")
        ?.filter { it.isNotEmpty() }
        ?.mapNotNull { name -> runCatching { Achievements.valueOf(name) }.getOrNull() }
        ?.toMutableList()
        ?: mutableListOf()

    private fun unlockAchievement(achievement: Achievements) {
        val unlockedAchievements = getUnlockedAchievements()
        if (unlockedAchievements.contains(achievement)) return
        unlockedAchievements.add(achievement)
        settings.putString(KEY_UNLOCKED_ACHIEVEMENTS, unlockedAchievements.joinToString(",") { it.name })
    }

    /** Merge unlocked achievements from an external source (e.g. Play Games sync on the playStore flavor). */
    fun restoreUnlockedAchievements(achievements: Set<Achievements>) {
        if (achievements.isEmpty()) return
        val current = getUnlockedAchievements().toMutableSet()
        val before = current.size
        current.addAll(achievements)
        if (current.size > before) {
            settings.putString(KEY_UNLOCKED_ACHIEVEMENTS, current.joinToString(",") { it.name })
        }
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
        PlayGamesBridge.onStreak?.invoke(newStreak)

        if (newStreak >= STREAK_30_TARGET) {
            unlockAchievement(Achievements.STREAK_30)
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
        val gameType = getGameTypeById(gameId)
        val previousHighscore = getHighScore(gameId)
        val newHighscore = if (gameType?.lowerScoreIsBetter == true) {
            score > 0 && (previousHighscore == 0 || score < previousHighscore)
        } else {
            score > previousHighscore
        }
        if (newHighscore) {
            settings.putInt(getHighscoreKey(gameId), score)
        }
        val scoresRaw = settings.getString(getScoresKey(gameId), "")
        settings.putString(
            getScoresKey(gameId),
            "${Clock.System.now().toEpochMilliseconds()}/$score,$scoresRaw",
        )

        // For time-based games, awarding XP equal to the time would punish faster players.
        // Map the result to a fixed-bandwidth XP bracket instead.
        val xpAward = if (gameType?.lowerScoreIsBetter == true) {
            xpForTimeScore(gameType, score)
        } else {
            score
        }
        val updatedTotalScore = getTotalScore() + xpAward
        settings.putInt(KEY_TOTAL_SCORE, updatedTotalScore)

        PlayGamesBridge.onTotalScore?.invoke(updatedTotalScore)
        if (gameType != null) {
            PlayGamesBridge.onSubmitScore?.invoke(gameType, score)
        }
        if (gameType != null && gameType.meetsScore(score, gameType.goldScore)) {
            PlayGamesBridge.onGoldMedal?.invoke(gameType)
            Achievements.forGameGold(gameType)?.let { unlockAchievement(it) }
        }
        if (updatedTotalScore >= TOTAL_SCORE_10K_TARGET) {
            unlockAchievement(Achievements.TOTAL_SCORE_10K)
        }

        val levelChange = addXp(xpAward)
        return ScoreResult(newHighscore, xpAward, levelChange)
    }

    /** XP for a time-based result. Tiered to match the typical per-session XP yield of
     *  points-based games (whose averages across the catalog are ~11 for gold, ~6 for silver). */
    private fun xpForTimeScore(gameType: GameType, score: Int): Int {
        if (score <= 0) return 0
        return when {
            gameType.meetsScore(score, gameType.goldScore) -> 12
            gameType.meetsScore(score, gameType.silverScore) -> 6
            else -> 3
        }
    }

    fun getTotalScore(): Int = settings.getIntOrNull(KEY_TOTAL_SCORE) ?: 0

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
