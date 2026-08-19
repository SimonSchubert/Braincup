package com.inspiredandroid.braincup.games.iqtest

import com.inspiredandroid.braincup.games.matrix.MatrixGenerator

/**
 * The fixed item plan every attempt follows.
 *
 * A fixed plan is what makes a raw-score norm table valid: every attempt faces the same ladder of
 * difficulties, so two raw scores are directly comparable. Adapting item difficulty to the player
 * would break that, which is why the ramp here is a constant and not derived from performance.
 */
object IqTestBlueprint {

    const val ITEM_COUNT = 30

    const val TIME_LIMIT_MILLIS = 15 * 60 * 1000L

    private val TIER_BY_ITEM = intArrayOf(
        0, 0, 0, 1, 1, 1, 1, 2, 2, 2,
        2, 2, 3, 3, 3, 3, 3, 4, 4, 4,
        4, 4, 5, 5, 5, 5, 5, 6, 6, 6,
    )

    fun tierFor(itemIndex: Int): Int = TIER_BY_ITEM[itemIndex]

    fun itemCountForTier(tier: Int): Int = TIER_BY_ITEM.count { it == tier }

    /**
     * Raw score a player who only guesses is expected to reach, given that each tier hands out a
     * different number of options. Everything at or below this carries no signal, which is where
     * [IqScoring] puts its floor.
     */
    val chanceLevel: Double
        get() = (0 until ITEM_COUNT).sumOf { 1.0 / MatrixGenerator.optionCountFor(tierFor(it)) }
}
