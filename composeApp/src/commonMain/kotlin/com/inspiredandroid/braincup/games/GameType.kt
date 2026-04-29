package com.inspiredandroid.braincup.games

import braincup.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource

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
        goldScore = 5,
        silverScore = 2,
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
    PATH_FINDER(
        displayNameRes = Res.string.game_path_finder,
        id = "8",
        goldScore = 14,
        silverScore = 7,
        descriptionRes = Res.string.game_path_finder_desc,
        category = GameCategory.LOGIC,
    ),
    ANOMALY_PUZZLE(
        displayNameRes = Res.string.game_anomaly_puzzle,
        id = "6",
        goldScore = 17,
        silverScore = 8,
        descriptionRes = Res.string.game_anomaly_puzzle_desc,
        category = GameCategory.LOGIC,
    ),
    GHOST_GRID(
        displayNameRes = Res.string.game_ghost_grid,
        id = "12",
        goldScore = 8,
        silverScore = 5,
        descriptionRes = Res.string.game_ghost_grid_desc,
        category = GameCategory.MEMORY,
    ),
    VISUAL_MEMORY(
        displayNameRes = Res.string.game_visual_memory,
        id = "10",
        goldScore = 9,
        silverScore = 6,
        descriptionRes = Res.string.game_visual_memory_desc,
        category = GameCategory.MEMORY,
    ),
    COLORED_SHAPES(
        displayNameRes = Res.string.game_colored_shapes,
        id = "1",
        goldScore = 15,
        silverScore = 8,
        descriptionRes = Res.string.game_colored_shapes_desc,
        category = GameCategory.LOGIC,
    ),
    SHERLOCK_CALCULATION(
        displayNameRes = Res.string.game_sherlock_calculation,
        id = "2",
        goldScore = 7,
        silverScore = 3,
        descriptionRes = Res.string.game_sherlock_calculation_desc,
        category = GameCategory.MATH,
    ),
    MENTAL_CALCULATION(
        displayNameRes = Res.string.game_mental_calculation,
        id = "0",
        goldScore = 16,
        silverScore = 8,
        descriptionRes = Res.string.game_mental_calculation_desc,
        category = GameCategory.MATH,
    ),
    CHAIN_CALCULATION(
        displayNameRes = Res.string.game_chain_calculation,
        id = "3",
        goldScore = 8,
        silverScore = 4,
        descriptionRes = Res.string.game_chain_calculation_desc,
        category = GameCategory.MATH,
    ),
    FRACTION_CALCULATION(
        displayNameRes = Res.string.game_fraction_calculation,
        id = "4",
        goldScore = 12,
        silverScore = 4,
        descriptionRes = Res.string.game_fraction_calculation_desc,
        category = GameCategory.MATH,
    ),
    VALUE_COMPARISON(
        displayNameRes = Res.string.game_value_comparison,
        id = "5",
        goldScore = 14,
        silverScore = 4,
        descriptionRes = Res.string.game_value_comparison_desc,
        category = GameCategory.MATH,
    ),
    PATTERN_SEQUENCE(
        displayNameRes = Res.string.game_pattern_sequence,
        id = "11",
        goldScore = 10,
        silverScore = 5,
        descriptionRes = Res.string.game_pattern_sequence_desc,
        category = GameCategory.PERCEPTION,
    ),
    COLOR_CONFUSION(
        displayNameRes = Res.string.game_color_confusion,
        id = "13",
        goldScore = 12,
        silverScore = 6,
        descriptionRes = Res.string.game_color_confusion_desc,
        category = GameCategory.PERCEPTION,
    ),
    ORBIT_TRACKER(
        displayNameRes = Res.string.game_orbit_tracker,
        id = "14",
        goldScore = 8,
        silverScore = 4,
        descriptionRes = Res.string.game_orbit_tracker_desc,
        category = GameCategory.MEMORY,
    ),
    FLASH_CROWD(
        displayNameRes = Res.string.game_flash_crowd,
        id = "15",
        goldScore = 18,
        silverScore = 12,
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
    ;

    val accentColor: Long get() = category.accentColor

    /** Format a stored score for display. Time-based scores are stored as deciseconds (1/10s)
     *  and render as "12.3s". */
    fun formatScore(score: Int): String = if (lowerScoreIsBetter) {
        "${score / 10}.${score % 10}s"
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
}

fun getGameTypeById(id: String): GameType? = GameType.entries.find { it.id == id }
