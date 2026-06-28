package com.inspiredandroid.braincup.api

import com.inspiredandroid.braincup.matchstickriddles.MatchstickRiddles
import com.russhwolf.settings.MapSettings
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Covers the incremental Matchstick Riddles set-completion achievement: progress is reported per
 * solve, the achievement unlocks once every riddle is solved, and reinstall restore seeds the local
 * solved set from the store's step count. Mirrors [UserStorageSudokuAchievementsTest].
 */
class UserStorageMatchstickAchievementsTest {

    private val allIds: List<String> = MatchstickRiddles.all.map { it.id }

    private fun solvedCount(storage: UserStorage): Int = storage.getSolvedMatchstickRiddleIds().count { it in allIds }

    @BeforeTest
    fun clearBridge() {
        PlayGamesBridge.onMatchstickRiddlesProgress = null
    }

    @AfterTest
    fun resetBridge() {
        PlayGamesBridge.onMatchstickRiddlesProgress = null
    }

    @Test
    fun reportsRisingProgressAndUnlocksWhenAllSolved() {
        val storage = UserStorage(MapSettings())
        val reported = mutableListOf<Int>()
        PlayGamesBridge.onMatchstickRiddlesProgress = { count -> reported += count }

        allIds.forEachIndexed { index, id ->
            storage.markMatchstickRiddleSolved(id)
            assertEquals(index + 1, reported.last())
        }

        assertTrue(storage.getUnlockedAchievements().contains(UserStorage.Achievements.MATCHSTICK_MASTER))
    }

    @Test
    fun doesNotUnlockBeforeEveryRiddleIsSolved() {
        val storage = UserStorage(MapSettings())
        allIds.dropLast(1).forEach { storage.markMatchstickRiddleSolved(it) }
        assertFalse(storage.getUnlockedAchievements().contains(UserStorage.Achievements.MATCHSTICK_MASTER))
    }

    @Test
    fun reMarkingSameRiddleDoesNotDoubleCountOrReReport() {
        val storage = UserStorage(MapSettings())
        val reported = mutableListOf<Int>()
        PlayGamesBridge.onMatchstickRiddlesProgress = { count -> reported += count }

        val id = allIds.first()
        storage.markMatchstickRiddleSolved(id)
        storage.markMatchstickRiddleSolved(id)
        assertEquals(1, solvedCount(storage))
        assertEquals(listOf(1), reported)
    }

    @Test
    fun restoreSeedsSolvedSetAndIsIdempotent() {
        val storage = UserStorage(MapSettings())

        assertTrue(storage.restoreMatchstickRiddlesProgressIfHigher(4))
        assertEquals(4, solvedCount(storage))
        assertEquals(allIds.take(4).toSet(), storage.getSolvedMatchstickRiddleIds())
        // Partial restore must not unlock the achievement.
        assertFalse(storage.getUnlockedAchievements().contains(UserStorage.Achievements.MATCHSTICK_MASTER))

        // Equal or lower remote counts are no-ops.
        assertFalse(storage.restoreMatchstickRiddlesProgressIfHigher(4))
        assertFalse(storage.restoreMatchstickRiddlesProgressIfHigher(3))
        assertEquals(4, solvedCount(storage))
    }

    @Test
    fun restoreToFullSetUnlocksAchievement() {
        val storage = UserStorage(MapSettings())
        assertTrue(storage.restoreMatchstickRiddlesProgressIfHigher(MatchstickRiddles.count))
        assertTrue(storage.getUnlockedAchievements().contains(UserStorage.Achievements.MATCHSTICK_MASTER))
    }

    @Test
    fun restoreClampsACountLargerThanTheCatalog() {
        val storage = UserStorage(MapSettings())
        // A store total left over from a larger past catalog must not overshoot the current riddles.
        assertTrue(storage.restoreMatchstickRiddlesProgressIfHigher(MatchstickRiddles.count + 5))
        assertEquals(MatchstickRiddles.count, solvedCount(storage))
        assertTrue(storage.getUnlockedAchievements().contains(UserStorage.Achievements.MATCHSTICK_MASTER))
    }
}
