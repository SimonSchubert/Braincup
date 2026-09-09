package com.inspiredandroid.braincup.reversi

enum class ReversiDifficulty(
    val depth: Int,
    /** When false the AI scores a position by discs and corners alone, so it takes the most discs
     *  available now. That is the beginner's mistake in Reversi, and it is what keeps Normal
     *  beatable without making it play nonsense. */
    val usePositionalEval: Boolean,
    /** Probability per move of playing a random legal move instead of the calculated best. */
    val blunderChance: Double,
    /** Empty squares at or below which the search abandons its depth limit and plays the rest of
     *  the game out exactly. */
    val exactSolveEmpties: Int,
) {
    NORMAL(depth = 2, usePositionalEval = false, blunderChance = 0.2, exactSolveEmpties = 0),
    HARD(depth = 6, usePositionalEval = true, blunderChance = 0.0, exactSolveEmpties = 12),
}
