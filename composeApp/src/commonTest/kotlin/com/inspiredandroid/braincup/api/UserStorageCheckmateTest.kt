package com.inspiredandroid.braincup.api

import com.inspiredandroid.braincup.checkmate.CheckmatePuzzles
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UserStorageCheckmateTest {

    @Test
    fun firstGoSolvesCountAndBuildTheStreak() {
        val storage = UserStorage.forPreview()
        storage.recordCheckmateResult("a", solved = true, firstGo = true)
        storage.recordCheckmateResult("b", solved = true, firstGo = true)
        assertEquals(setOf("a", "b"), storage.getSolvedCheckmateIds())
        assertEquals(2, storage.getCheckmateStreak())
        assertEquals(2, storage.getCheckmateBestStreak())
    }

    @Test
    fun aMissBreaksTheStreakButKeepsTheBest() {
        val storage = UserStorage.forPreview()
        storage.recordCheckmateResult("a", solved = true, firstGo = true)
        storage.recordCheckmateResult("b", solved = false, firstGo = true)
        assertEquals(0, storage.getCheckmateStreak())
        assertEquals(1, storage.getCheckmateBestStreak())
    }

    @Test
    fun aSolveAfterARestartCountsButDoesNotExtendTheStreak() {
        val storage = UserStorage.forPreview()
        storage.recordCheckmateResult("a", solved = true, firstGo = false)
        assertEquals(setOf("a"), storage.getSolvedCheckmateIds())
        assertEquals(0, storage.getCheckmateStreak())
        storage.recordCheckmateResult("b", solved = true, firstGo = true)
        storage.recordCheckmateResult("b", solved = true, firstGo = true)
        assertEquals(1, storage.getCheckmateStreak())
    }

    @Test
    fun solvingTheLastPuzzleUnlocksMateMaster() {
        val storage = UserStorage.forPreview()
        repeat(CheckmatePuzzles.COUNT - 1) { storage.recordCheckmateResult("p$it", solved = true, firstGo = false) }
        assertFalse(UserStorage.Achievements.CHECKMATE_MASTER in storage.getUnlockedAchievements())
        storage.recordCheckmateResult("last", solved = true, firstGo = false)
        assertTrue(UserStorage.Achievements.CHECKMATE_MASTER in storage.getUnlockedAchievements())
    }
}
