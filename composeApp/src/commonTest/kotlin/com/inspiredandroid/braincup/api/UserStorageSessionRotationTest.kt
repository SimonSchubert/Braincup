package com.inspiredandroid.braincup.api

import com.russhwolf.settings.MapSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Covers the daily-challenge per-category shuffle-bag rotation in
 * [UserStorage.drawDailySessionGameIds]: one game per category, every eligible game used once
 * before any repeat, no back-to-back repeat at the cycle boundary, and games that become
 * ineligible (e.g. after toggling the color-blind palette) are never drawn.
 */
class UserStorageSessionRotationTest {

    private fun storage() = UserStorage(MapSettings())

    @Test
    fun drawsOneGamePerCategoryInOrder() {
        val eligible = linkedMapOf(
            "MEMORY" to listOf("a", "b", "c"),
            "MATH" to listOf("x", "y"),
        )
        val picks = storage().drawDailySessionGameIds(eligible)
        assertEquals(2, picks.size)
        assertTrue(picks[0] in eligible.getValue("MEMORY"))
        assertTrue(picks[1] in eligible.getValue("MATH"))
    }

    @Test
    fun cyclesThroughAllGamesBeforeRepeating() {
        val storage = storage()
        val games = listOf("a", "b", "c", "d")
        val eligible = mapOf("CAT" to games)

        val cycle1 = (1..4).map { storage.drawDailySessionGameIds(eligible).single() }
        assertEquals(games.toSet(), cycle1.toSet(), "first cycle should cover every game once")
        assertEquals(4, cycle1.toSet().size, "no repeats within a cycle")

        val cycle2 = (1..4).map { storage.drawDailySessionGameIds(eligible).single() }
        assertEquals(games.toSet(), cycle2.toSet(), "second cycle should cover every game once")
        assertTrue(cycle1.last() != cycle2.first(), "no back-to-back repeat across the cycle boundary")
    }

    @Test
    fun neverDrawsAGameThatBecameIneligible() {
        val storage = storage()
        storage.drawDailySessionGameIds(mapOf("CAT" to listOf("a", "b", "c")))

        // "a" is now ineligible; it must never be picked even though it may still sit in the bag.
        val reduced = mapOf("CAT" to listOf("b", "c"))
        repeat(6) {
            val pick = storage.drawDailySessionGameIds(reduced).single()
            assertTrue(pick == "b" || pick == "c", "ineligible game must be skipped, got $pick")
        }
    }

    @Test
    fun rotationSurvivesReloadFromSettings() {
        val settings = MapSettings()
        val eligible = mapOf("CAT" to listOf("a", "b", "c"))

        // Draw across separate UserStorage instances backed by the same settings; the bag must
        // persist so the full pool is still covered exactly once before repeating.
        val drawn = (1..3).map { UserStorage(settings).drawDailySessionGameIds(eligible).single() }
        assertEquals(setOf("a", "b", "c"), drawn.toSet())
    }
}
