package com.inspiredandroid.braincup.learn

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LearnStoreAchievementsTest {

    /**
     * The guard that makes adding a sub-topic a visible piece of store work. A certificate with no
     * store achievement behind it fails silently in every other place: the Play Games id would be
     * a blank string (a deliberate no-op) and Game Center would report an id nobody registered.
     *
     * If this fails after adding a sub-topic, work through the certificates checklist in
     * `media/achievements/README.md` rather than only appending the id here.
     */
    @Test
    fun everySubTopicHasAStoreAchievement() {
        assertEquals(LearnCatalog.allUnits.map { it.id }, LearnStoreAchievements.certifiedUnitIds)
    }

    @Test
    fun gameCenterIdsRoundTrip() {
        for (unitId in LearnStoreAchievements.certifiedUnitIds) {
            val id = LearnStoreAchievements.gameCenterId(unitId)
            assertEquals(unitId, LearnStoreAchievements.unitIdForGameCenterId(id), "round trip for $id")
        }
    }

    /** Store ids are immutable once published, so the derivation is pinned by an example. */
    @Test
    fun idsAreDerivedFromTheUnitId() {
        assertEquals("cert_arithmetic_standard_form", LearnStoreAchievements.storeKey("arithmetic-standard-form"))
        assertEquals(
            "achievement.cert_geometry_circle_theorems",
            LearnStoreAchievements.gameCenterId("geometry-circle-theorems"),
        )
    }

    @Test
    fun anUnknownGameCenterIdMapsToNothing() {
        assertNull(LearnStoreAchievements.unitIdForGameCenterId("achievement.sudoku_sage"))
        assertNull(LearnStoreAchievements.unitIdForGameCenterId("achievement.cert_calculus_limits"))
    }

    /** Ids reach the stores as text, so no unit may carry a character the scheme cannot encode. */
    @Test
    fun idsStayLowercaseSnakeCase() {
        val allowed = Regex("^achievement\\.cert_[a-z0-9_]+$")
        for (unitId in LearnStoreAchievements.certifiedUnitIds) {
            val id = LearnStoreAchievements.gameCenterId(unitId)
            assertEquals(true, allowed.matches(id), "$id is not a usable store id")
        }
    }
}
