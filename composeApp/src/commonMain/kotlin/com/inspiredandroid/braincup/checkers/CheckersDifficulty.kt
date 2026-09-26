package com.inspiredandroid.braincup.checkers

enum class CheckersDifficulty(
    val depth: Int,
    /** Probability per move of playing a random legal move instead of the calculated best. Never
     *  rolled when a capture is forced, since the rules already narrow the choice there. */
    val blunderChance: Double,
) {
    EASY(depth = 2, blunderChance = 0.3),
    MEDIUM(depth = 4, blunderChance = 0.0),
    HARD(depth = 7, blunderChance = 0.0),
}
