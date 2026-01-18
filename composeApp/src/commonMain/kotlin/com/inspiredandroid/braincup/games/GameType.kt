package com.inspiredandroid.braincup.games

import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.ic_anomaly_puzzle
import braincup.composeapp.generated.resources.ic_chain_calculation
import braincup.composeapp.generated.resources.ic_color_confusion
import braincup.composeapp.generated.resources.ic_fraction_calculation
import braincup.composeapp.generated.resources.ic_grid_solver
import braincup.composeapp.generated.resources.ic_mental_calculation
import braincup.composeapp.generated.resources.ic_path_finder
import braincup.composeapp.generated.resources.ic_sherlock_calculation
import braincup.composeapp.generated.resources.ic_value_comparison
import org.jetbrains.compose.resources.DrawableResource

enum class GameType(
    val displayName: String,
    val id: String,
    val goldScore: Int,
    val silverScore: Int,
    val description: String,
    val icon: DrawableResource,
) {
    ANOMALY_PUZZLE(
        displayName = "Anomaly puzzle",
        id = "6",
        goldScore = 17,
        silverScore = 8,
        description = "Find the outstanding figure. Take the color and shape of the figure into account.",
        icon = Res.drawable.ic_anomaly_puzzle,
    ),
    MENTAL_CALCULATION(
        displayName = "Mental calculation",
        id = "0",
        goldScore = 16,
        silverScore = 8,
        description = "Follow the mathematical expressions. Use the result as the base for the next calculation.",
        icon = Res.drawable.ic_mental_calculation,
    ),
    COLOR_CONFUSION(
        displayName = "Color confusion",
        id = "1",
        goldScore = 15,
        silverScore = 8,
        description = "Sum up the points of the correct statements.",
        icon = Res.drawable.ic_color_confusion,
    ),
    SHERLOCK_CALCULATION(
        displayName = "Sherlock calculation",
        id = "2",
        goldScore = 7,
        silverScore = 3,
        description = "Find out how to get the goal by only using the given numbers and the following operators: + - * / ( ).",
        icon = Res.drawable.ic_sherlock_calculation,
    ),
    CHAIN_CALCULATION(
        displayName = "Chain calculation",
        id = "3",
        goldScore = 8,
        silverScore = 4,
        description = "Follow the mathematical expressions.",
        icon = Res.drawable.ic_chain_calculation,
    ),
    FRACTION_CALCULATION(
        displayName = "Fraction calculation",
        id = "4",
        goldScore = 10,
        silverScore = 4,
        description = "Solve the fractions.",
        icon = Res.drawable.ic_fraction_calculation,
    ),
    VALUE_COMPARISON(
        displayName = "Value comparison",
        id = "5",
        goldScore = 14,
        silverScore = 4,
        description = "Pick the mathematical formula with the highest result.",
        icon = Res.drawable.ic_value_comparison,
    ),
    PATH_FINDER(
        displayName = "Path finder",
        id = "8",
        goldScore = 14,
        silverScore = 7,
        description = "Start at the marked position and follow the arrow instructions to find the destination.",
        icon = Res.drawable.ic_path_finder,
    ),
    GRID_SOLVER(
        displayName = "Grid solver",
        id = "9",
        goldScore = 3,
        silverScore = 6,
        description = "Fill in the missing numbers so that the sum of each row and column is equal to the numbers given.",
        icon = Res.drawable.ic_grid_solver,
    ),
}
