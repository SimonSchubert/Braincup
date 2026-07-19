package com.inspiredandroid.braincup.games

import androidx.compose.runtime.Composable
import braincup.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

enum class GameType(
    val displayNameRes: StringResource,
    val id: String,
    val goldScore: Int,
    val silverScore: Int,
    val descriptionRes: StringResource,
    val category: GameCategory,
    /** When true, score is a time (seconds). Lower beats higher; thresholds are upper bounds. */
    val lowerScoreIsBetter: Boolean = false,
) {
    MINI_SUDOKU(
        displayNameRes = Res.string.game_mini_sudoku,
        id = "16",
        goldScore = 6,
        silverScore = 3,
        descriptionRes = Res.string.game_mini_sudoku_desc,
        category = GameCategory.LOGIC,
    ),
    MINI_CHESS(
        displayNameRes = Res.string.game_mini_chess,
        id = "18",
        // Win points scale with chosen difficulty: Easy=5, Medium=10, Hard=15. The medal
        // tiers below map directly to that: any win = bronze, Medium win = silver, Hard
        // win = gold. See MiniChessGame.winPointsForDepth.
        goldScore = 15,
        silverScore = 10,
        descriptionRes = Res.string.game_mini_chess_desc,
        category = GameCategory.LOGIC,
    ),
    SOLO_CHESS(
        displayNameRes = Res.string.game_solo_chess,
        id = "29",
        // Score = highest level solved. Bronze = any solve, silver = level 5, gold = level 10.
        goldScore = 10,
        silverScore = 5,
        descriptionRes = Res.string.game_solo_chess_desc,
        category = GameCategory.LOGIC,
    ),
    LIGHTS_OUT(
        displayNameRes = Res.string.game_lights_out,
        id = "19",
        // Score = highest level solved. Bronze = any solve, silver = level 5, gold = level 10.
        goldScore = 10,
        silverScore = 5,
        descriptionRes = Res.string.game_lights_out_desc,
        category = GameCategory.LOGIC,
    ),
    SLIDING_PUZZLE(
        displayNameRes = Res.string.game_sliding_puzzle,
        id = "20",
        // Score = highest level solved. Bronze = any solve, silver = level 5, gold = level 10.
        goldScore = 10,
        silverScore = 5,
        descriptionRes = Res.string.game_sliding_puzzle_desc,
        category = GameCategory.LOGIC,
    ),
    SHIKAKU(
        displayNameRes = Res.string.game_shikaku,
        id = "25",
        // Score = highest level solved. Bronze = any solve, silver = level 5, gold = level 10.
        goldScore = 10,
        silverScore = 5,
        descriptionRes = Res.string.game_shikaku_desc,
        category = GameCategory.LOGIC,
    ),
    NURIKABE(
        displayNameRes = Res.string.game_nurikabe,
        id = "26",
        // Score = highest level solved. Bronze = any solve, silver = level 5, gold = level 10.
        goldScore = 10,
        silverScore = 5,
        descriptionRes = Res.string.game_nurikabe_desc,
        category = GameCategory.LOGIC,
    ),
    CAT_QUEENS(
        displayNameRes = Res.string.game_cat_queens,
        id = "27",
        // Score = highest level solved. Bronze = any solve, silver = level 5, gold = level 10.
        goldScore = 10,
        silverScore = 5,
        descriptionRes = Res.string.game_cat_queens_desc,
        category = GameCategory.LOGIC,
    ),
    KNOT(
        displayNameRes = Res.string.game_knot,
        id = "28",
        // Score = highest level solved. Bronze = any solve, silver = level 5, gold = level 10.
        goldScore = 10,
        silverScore = 5,
        descriptionRes = Res.string.game_knot_desc,
        category = GameCategory.LOGIC,
    ),
    TOWER_OF_HANOI(
        displayNameRes = Res.string.game_tower_of_hanoi,
        id = "30",
        // Score = highest level solved. Bronze = any solve, silver = level 2 (4 disks),
        // gold = level 4 (6 disks, the violet one; 63 moves optimal).
        goldScore = 4,
        silverScore = 2,
        descriptionRes = Res.string.game_tower_of_hanoi_desc,
        category = GameCategory.LOGIC,
    ),
    PATH_FINDER(
        displayNameRes = Res.string.game_path_finder,
        id = "8",
        goldScore = 15,
        silverScore = 8,
        descriptionRes = Res.string.game_path_finder_desc,
        category = GameCategory.LOGIC,
    ),
    ANOMALY_PUZZLE(
        displayNameRes = Res.string.game_anomaly_puzzle,
        id = "6",
        goldScore = 18,
        silverScore = 9,
        descriptionRes = Res.string.game_anomaly_puzzle_desc,
        category = GameCategory.LOGIC,
    ),
    GHOST_GRID(
        displayNameRes = Res.string.game_ghost_grid,
        id = "12",
        goldScore = 9,
        silverScore = 6,
        descriptionRes = Res.string.game_ghost_grid_desc,
        category = GameCategory.MEMORY,
    ),
    VISUAL_MEMORY(
        displayNameRes = Res.string.game_visual_memory,
        id = "10",
        goldScore = 11,
        silverScore = 7,
        descriptionRes = Res.string.game_visual_memory_desc,
        category = GameCategory.MEMORY,
    ),
    COLORED_SHAPES(
        displayNameRes = Res.string.game_colored_shapes,
        id = "1",
        goldScore = 17,
        silverScore = 9,
        descriptionRes = Res.string.game_colored_shapes_desc,
        category = GameCategory.LOGIC,
    ),
    SHERLOCK_CALCULATION(
        displayNameRes = Res.string.game_sherlock_calculation,
        id = "2",
        goldScore = 9,
        silverScore = 4,
        descriptionRes = Res.string.game_sherlock_calculation_desc,
        category = GameCategory.MATH,
    ),
    MENTAL_CALCULATION(
        displayNameRes = Res.string.game_mental_calculation,
        id = "0",
        goldScore = 18,
        silverScore = 9,
        descriptionRes = Res.string.game_mental_calculation_desc,
        category = GameCategory.MATH,
    ),
    BUBBLE_SUM(
        displayNameRes = Res.string.game_bubble_sum,
        id = "31",
        // Score = rounds summed correctly in 60s. Initial thresholds; tune after playtest.
        goldScore = 12,
        silverScore = 6,
        descriptionRes = Res.string.game_bubble_sum_desc,
        category = GameCategory.MATH,
    ),
    QUICK_SUM(
        displayNameRes = Res.string.game_quick_sum,
        id = "32",
        // Score = sequences summed correctly in 60s. Flash time is ~4s at every tier, so the
        // pacing matches DIGIT_MEMORY; its thresholds are borrowed. Tune after playtest.
        goldScore = 7,
        silverScore = 4,
        descriptionRes = Res.string.game_quick_sum_desc,
        category = GameCategory.MATH,
    ),
    CHAIN_CALCULATION(
        displayNameRes = Res.string.game_chain_calculation,
        id = "3",
        goldScore = 10,
        silverScore = 5,
        descriptionRes = Res.string.game_chain_calculation_desc,
        category = GameCategory.MATH,
    ),
    FRACTION_CALCULATION(
        displayNameRes = Res.string.game_fraction_calculation,
        id = "4",
        goldScore = 14,
        silverScore = 6,
        descriptionRes = Res.string.game_fraction_calculation_desc,
        category = GameCategory.MATH,
    ),
    VALUE_COMPARISON(
        displayNameRes = Res.string.game_value_comparison,
        id = "5",
        goldScore = 16,
        silverScore = 6,
        descriptionRes = Res.string.game_value_comparison_desc,
        category = GameCategory.MATH,
    ),
    PATTERN_SEQUENCE(
        displayNameRes = Res.string.game_pattern_sequence,
        id = "11",
        goldScore = 12,
        silverScore = 6,
        descriptionRes = Res.string.game_pattern_sequence_desc,
        category = GameCategory.PERCEPTION,
    ),
    COLOR_CONFUSION(
        displayNameRes = Res.string.game_color_confusion,
        id = "13",
        goldScore = 13,
        silverScore = 7,
        descriptionRes = Res.string.game_color_confusion_desc,
        category = GameCategory.PERCEPTION,
    ),
    ORBIT_TRACKER(
        displayNameRes = Res.string.game_orbit_tracker,
        id = "14",
        goldScore = 10,
        silverScore = 5,
        descriptionRes = Res.string.game_orbit_tracker_desc,
        category = GameCategory.MEMORY,
    ),
    FLASH_CROWD(
        displayNameRes = Res.string.game_flash_crowd,
        id = "15",
        goldScore = 20,
        silverScore = 13,
        descriptionRes = Res.string.game_flash_crowd_desc,
        category = GameCategory.PERCEPTION,
    ),
    SCHULTE_TABLE(
        displayNameRes = Res.string.game_schulte_table,
        id = "17",
        // Score = deciseconds (tenths of a second) to complete the 4×4 table; lower is better.
        // 80 = 8.0s gold, 150 = 15.0s silver.
        goldScore = 80,
        silverScore = 150,
        descriptionRes = Res.string.game_schulte_table_desc,
        category = GameCategory.PERCEPTION,
        lowerScoreIsBetter = true,
    ),
    FLAGS(
        displayNameRes = Res.string.game_flags,
        id = "21",
        // Score = rounds completed. Initial thresholds; tune after playtest.
        goldScore = 32,
        silverScore = 16,
        descriptionRes = Res.string.game_flags_desc,
        category = GameCategory.PERCEPTION,
    ),
    DIGIT_MEMORY(
        displayNameRes = Res.string.game_digit_memory,
        id = "22",
        // Score = sequences recalled in 60s. Initial thresholds; tune after playtest.
        goldScore = 7,
        silverScore = 4,
        descriptionRes = Res.string.game_digit_memory_desc,
        category = GameCategory.MEMORY,
    ),
    SPOT_THE_NEW(
        displayNameRes = Res.string.game_spot_the_new,
        id = "23",
        // Score = rounds survived. Initial thresholds; tune after playtest.
        goldScore = 14,
        silverScore = 7,
        descriptionRes = Res.string.game_spot_the_new_desc,
        category = GameCategory.MEMORY,
    ),
    N_BACK(
        displayNameRes = Res.string.game_n_back,
        id = "33",
        // Score = correct hits in 60s; perfect play is ~4 blocks x 3 hits.
        // Initial thresholds; tune after playtest.
        goldScore = 10,
        silverScore = 5,
        descriptionRes = Res.string.game_n_back_desc,
        category = GameCategory.MEMORY,
    ),
    WORDLE(
        displayNameRes = Res.string.game_wordle,
        id = "24",
        // Score = 7 - guesses used on a win (1 guess = 6 … 6 guesses = 1), 0 on a loss.
        // Gold = solved in ≤2 guesses, silver = solved in ≤4, bronze = any solve.
        goldScore = 5,
        silverScore = 3,
        descriptionRes = Res.string.game_wordle_desc,
        category = GameCategory.LOGIC,
    ),
    SIMON_SAYS(
        displayNameRes = Res.string.game_simon_says,
        id = "34",
        // Score = rounds survived. Cumulative full-replay-every-round design is closer to Spot The
        // New's difficulty curve than Ghost Grid's; initial thresholds borrowed from there, tune
        // after playtest.
        goldScore = 14,
        silverScore = 7,
        descriptionRes = Res.string.game_simon_says_desc,
        category = GameCategory.MEMORY,
    ),
    ;

    /** URL path segment for web navigation, e.g. CAT_QUEENS → "CatQueens". */
    val urlSlug: String
        get() = name.split('_').joinToString("") { part ->
            part.lowercase().replaceFirstChar { it.uppercase() }
        }

    companion object {
        /** Games in the order shown on the main menu (by category, then enum order within category). */
        val displayOrder: List<GameType> = entries.sortedBy { it.category.ordinal }

        fun fromUrlSlug(slug: String): GameType? = entries.find { it.urlSlug == slug }
    }

    val accentColor: Long get() = category.accentColor

    /** Whether this game has a Play Games leaderboard wired up. */
    val hasLeaderboard: Boolean get() = this == FLAGS

    /** Hidden from menus and daily challenges while the color-blind palette is on —
     *  the mechanic depends on naming specific hues and remains unfair under any palette. */
    val requiresColorVision: Boolean
        get() = this == COLOR_CONFUSION || this == COLORED_SHAPES || this == KNOT

    /** Games whose score is the highest level reached, not a count of correct answers.
     *  UI shows "Level N" / "Play next level" instead of "Score: N" / "Play Again". */
    val usesLevelLabel: Boolean
        get() = this == LIGHTS_OUT ||
            this == SLIDING_PUZZLE ||
            this == SHIKAKU ||
            this == NURIKABE ||
            this == CAT_QUEENS ||
            this == KNOT ||
            this == SOLO_CHESS ||
            this == TOWER_OF_HANOI

    /** Numeric part of a score (time-based stored as deciseconds → "12.3"; count-based → "42").
     *  UI code should prefer [formattedScore] / [secondsTemplate] to attach the localized unit. */
    fun formatScore(score: Int): String = if (lowerScoreIsBetter) {
        "${score / 10}.${score % 10}"
    } else {
        score.toString()
    }

    /** Whether [score] meets-or-beats [threshold] under this game's scoring direction.
     *  Threshold ≤ 1 is the "bronze / any score recorded" tier. */
    fun meetsScore(score: Int, threshold: Int): Boolean {
        if (score <= 0) return false
        if (threshold <= 1) return true
        return if (lowerScoreIsBetter) score <= threshold else score >= threshold
    }

    /**
     * Bonus added to the base (correct-answer) score when a session started mid adaptive ramp.
     * Zero when not adaptive, lower-is-better, or no correct answers this run — so resume alone
     * never awards free points toward medals.
     */
    fun difficultyBonus(startRound: Int, baseScore: Int, adaptiveDifficulty: Boolean): Int {
        if (!adaptiveDifficulty || lowerScoreIsBetter || baseScore <= 0) return 0
        return startRound.coerceAtLeast(0)
    }
}

fun getGameTypeById(id: String): GameType? = GameType.entries.find { it.id == id }

/** Localized score display (adds the seconds unit for time-based games). */
@Composable
fun GameType.formattedScore(score: Int): String {
    val raw = formatScore(score)
    return if (lowerScoreIsBetter) stringResource(Res.string.format_seconds, raw) else raw
}

/** Pre-resolved seconds template for use in non-Composable lambdas (e.g. joinToString).
 *  Substitute `%1$s` with the numeric value from [GameType.formatScore]. */
@Composable
fun secondsTemplate(): String = stringResource(Res.string.format_seconds, "%1\$s")
