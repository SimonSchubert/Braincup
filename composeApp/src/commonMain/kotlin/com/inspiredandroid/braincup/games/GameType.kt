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
) {
    MINI_SUDOKU(
        displayNameRes = Res.string.game_mini_sudoku,
        id = "16",
        goldScore = 5,
        silverScore = 2,
        descriptionRes = Res.string.game_mini_sudoku_desc,
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
    ;

    val accentColor: Long get() = category.accentColor
}

fun getGameTypeById(id: String): GameType? = GameType.entries.find { it.id == id }
