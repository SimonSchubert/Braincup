package com.inspiredandroid.braincup.normalchess

enum class NormalChessDifficulty(
    val depth: Int,
    /** When false, the AI evaluates the static position at the depth horizon instead of
     *  following capture chains — so it doesn't see recaptures and will trade away pieces
     *  it shouldn't. Used to make Easy genuinely casual. */
    val useQuiescence: Boolean,
    /** Probability per move that the AI picks a random legal move instead of the calculated
     *  best. Adds beginner-like blunders to Easy without throwing the game outright. */
    val blunderChance: Double,
) {
    EASY(depth = 1, useQuiescence = false, blunderChance = 0.3),
    MEDIUM(depth = 3, useQuiescence = true, blunderChance = 0.0),
    HARD(depth = 4, useQuiescence = true, blunderChance = 0.0),
}
