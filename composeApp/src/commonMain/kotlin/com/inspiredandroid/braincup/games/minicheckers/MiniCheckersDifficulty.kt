package com.inspiredandroid.braincup.games.minicheckers

import com.inspiredandroid.braincup.checkers.CheckersDifficulty

/** [combinationMoves] is the length of the shortest forcing combination a scenario is built
 *  around. Hard adds men on both sides so the line is harder to spot among the alternatives. */
enum class MiniCheckersDifficulty(
    val cpu: CheckersDifficulty,
    val playerPieces: IntRange,
    val cpuPieces: IntRange,
    val combinationMoves: IntRange,
) {
    NORMAL(
        cpu = CheckersDifficulty.MEDIUM,
        playerPieces = 3..4,
        cpuPieces = 2..4,
        combinationMoves = 3..3,
    ),
    HARD(
        cpu = CheckersDifficulty.HARD,
        playerPieces = 3..5,
        cpuPieces = 3..5,
        combinationMoves = 4..MINI_CHECKERS_MAX_COMBINATION,
    ),
}
