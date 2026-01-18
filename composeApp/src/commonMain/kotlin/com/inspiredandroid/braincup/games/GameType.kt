package com.inspiredandroid.braincup.games

import braincup.composeapp.generated.resources.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

enum class GameType(
    val displayNameRes: StringResource,
    val id: String,
    val goldScore: Int,
    val silverScore: Int,
    val descriptionRes: StringResource,
    val icon: DrawableResource,
) {
    ANOMALY_PUZZLE(
        displayNameRes = Res.string.game_anomaly_puzzle,
        id = "6",
        goldScore = 17,
        silverScore = 8,
        descriptionRes = Res.string.game_anomaly_puzzle_desc,
        icon = Res.drawable.ic_anomaly_puzzle,
    ),
    MENTAL_CALCULATION(
        displayNameRes = Res.string.game_mental_calculation,
        id = "0",
        goldScore = 16,
        silverScore = 8,
        descriptionRes = Res.string.game_mental_calculation_desc,
        icon = Res.drawable.ic_mental_calculation,
    ),
    COLOR_CONFUSION(
        displayNameRes = Res.string.game_color_confusion,
        id = "1",
        goldScore = 15,
        silverScore = 8,
        descriptionRes = Res.string.game_color_confusion_desc,
        icon = Res.drawable.ic_color_confusion,
    ),
    SHERLOCK_CALCULATION(
        displayNameRes = Res.string.game_sherlock_calculation,
        id = "2",
        goldScore = 7,
        silverScore = 3,
        descriptionRes = Res.string.game_sherlock_calculation_desc,
        icon = Res.drawable.ic_sherlock_calculation,
    ),
    CHAIN_CALCULATION(
        displayNameRes = Res.string.game_chain_calculation,
        id = "3",
        goldScore = 8,
        silverScore = 4,
        descriptionRes = Res.string.game_chain_calculation_desc,
        icon = Res.drawable.ic_chain_calculation,
    ),
    FRACTION_CALCULATION(
        displayNameRes = Res.string.game_fraction_calculation,
        id = "4",
        goldScore = 10,
        silverScore = 4,
        descriptionRes = Res.string.game_fraction_calculation_desc,
        icon = Res.drawable.ic_fraction_calculation,
    ),
    VALUE_COMPARISON(
        displayNameRes = Res.string.game_value_comparison,
        id = "5",
        goldScore = 14,
        silverScore = 4,
        descriptionRes = Res.string.game_value_comparison_desc,
        icon = Res.drawable.ic_value_comparison,
    ),
    PATH_FINDER(
        displayNameRes = Res.string.game_path_finder,
        id = "8",
        goldScore = 14,
        silverScore = 7,
        descriptionRes = Res.string.game_path_finder_desc,
        icon = Res.drawable.ic_path_finder,
    ),
    GRID_SOLVER(
        displayNameRes = Res.string.game_grid_solver,
        id = "9",
        goldScore = 3,
        silverScore = 6,
        descriptionRes = Res.string.game_grid_solver_desc,
        icon = Res.drawable.ic_grid_solver,
    ),
    VISUAL_MEMORY(
        displayNameRes = Res.string.game_visual_memory,
        id = "10",
        goldScore = 9,
        silverScore = 6,
        descriptionRes = Res.string.game_visual_memory_desc,
        icon = Res.drawable.ic_visual_memory,
    ),
}
