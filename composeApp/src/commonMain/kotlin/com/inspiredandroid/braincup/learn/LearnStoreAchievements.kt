package com.inspiredandroid.braincup.learn

/**
 * Store identity of the per-certificate achievements on Play Games and Game Center.
 *
 * Every Learn sub-topic certificate has one store achievement. Unlike the per-game gold medals,
 * whose store ids were derived from their display titles, these are derived from the [LearnUnit]
 * id: store ids are immutable, so a title that is reworded later can never drift away from the id
 * it was named after.
 *
 * There is deliberately no matching `UserStorage.Achievements` entry. Certificates already have a
 * home in the Learn section, so these exist only to show up in the two stores, and to give the
 * store a record to restore a certificate from after a reinstall.
 */
object LearnStoreAchievements {

    /** Prefix Game Center ids carry, matching the per-game medals and the Sudoku tiers. */
    private const val GAME_CENTER_PREFIX = "achievement."

    /**
     * Every unit id that has a store achievement, in catalog order.
     *
     * Spelled out rather than derived from [LearnCatalog] so that adding a sub-topic fails
     * `LearnStoreAchievementsTest` instead of silently shipping a certificate no store knows
     * about. Adding one here is step one of the checklist in `media/achievements/README.md`.
     */
    val certifiedUnitIds: List<String> = listOf(
        "arithmetic-counting",
        "arithmetic-multiplication",
        "arithmetic-fractions",
        "arithmetic-decimals",
        "arithmetic-negatives",
        "arithmetic-ratio",
        "arithmetic-percent",
        "arithmetic-standard-form",
        "arithmetic-surds",
        "arithmetic-bounds",
        "geometry-flat-shapes",
        "geometry-solid-shapes",
        "geometry-angles",
        "geometry-quadrilaterals",
        "geometry-symmetry",
        "geometry-perimeter-and-area",
        "geometry-pythagoras",
        "geometry-circles",
        "geometry-volume",
        "geometry-similarity",
        "geometry-transformations",
        "geometry-circle-theorems",
    )

    /** The shared part of both stores' ids, e.g. "arithmetic-surds" -> "cert_arithmetic_surds". */
    fun storeKey(unitId: String): String = "cert_" + unitId.replace('-', '_')

    /** Game Center id for a unit, e.g. "achievement.cert_arithmetic_surds". */
    fun gameCenterId(unitId: String): String = GAME_CENTER_PREFIX + storeKey(unitId)

    /**
     * Inverse of [gameCenterId], for the restore path. Unknown ids (a retired sub-topic, or an
     * achievement from some other family) return null.
     */
    fun unitIdForGameCenterId(id: String): String? = certifiedUnitIds.firstOrNull { gameCenterId(it) == id }
}
