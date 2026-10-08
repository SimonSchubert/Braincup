package com.inspiredandroid.braincup.api

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UserStorageMascotContextTest {
    @Test
    fun firstOpenHasNoPreviousDay() {
        assertNull(testStorage().recordAppOpenDay(today = 100))
    }

    @Test
    fun reportsDaysSinceThePreviousOpen() {
        val storage = testStorage()
        storage.recordAppOpenDay(today = 100)
        assertEquals(0, storage.recordAppOpenDay(today = 100))
        assertEquals(4, storage.recordAppOpenDay(today = 104))
    }

    @Test
    fun streakIsAtRiskWhenYesterdaysSessionWasTheLast() {
        val storage = testStorage()
        storage.recordSessionCompleted()
        val completedDay = storage.lastCompletedDayForTest()
        assertFalse(storage.isStreakAtRisk(today = completedDay))
        assertTrue(storage.isStreakAtRisk(today = completedDay + 1))
        assertFalse(storage.isStreakAtRisk(today = completedDay + 2))
    }

    @Test
    fun noStreakIsNeverAtRisk() {
        assertFalse(testStorage().isStreakAtRisk(today = 100))
    }

    private fun UserStorage.lastCompletedDayForTest(): Int {
        val today = (kotlin.time.Clock.System.now().toEpochMilliseconds() / 86400000L).toInt()
        assertTrue(isSessionCompletedToday())
        return today
    }
}
