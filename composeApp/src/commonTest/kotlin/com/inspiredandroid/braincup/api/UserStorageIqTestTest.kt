package com.inspiredandroid.braincup.api

import com.inspiredandroid.braincup.games.iqtest.IqScoring
import com.russhwolf.settings.MapSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UserStorageIqTestTest {

    private fun storage() = testStorage()

    @Test
    fun historyStartsEmpty() {
        val storage = storage()
        assertTrue(storage.getIqTestResults().isEmpty())
        assertNull(storage.getBestIqTestRawScore())
    }

    @Test
    fun resultsRoundTripNewestFirst() {
        val storage = storage()
        storage.putIqTestResult(seed = 11L, rawScore = 14, durationSeconds = 500)
        storage.putIqTestResult(seed = 22L, rawScore = 21, durationSeconds = 640)

        val history = storage.getIqTestResults()
        assertEquals(2, history.size)
        assertEquals(21, history[0].rawScore)
        assertEquals(22L, history[0].seed)
        assertEquals(640, history[0].durationSeconds)
        assertEquals(14, history[1].rawScore)
        assertEquals(IqScoring.iqFor(21), history[0].iq)
        assertEquals(21, storage.getBestIqTestRawScore())
    }

    /** The per-game score history grows without bound; this one must not. */
    @Test
    fun historyIsCappedAtTheLimit() {
        val storage = storage()
        repeat(UserStorage.IQ_TEST_HISTORY_LIMIT + 7) { attempt ->
            storage.putIqTestResult(seed = attempt.toLong(), rawScore = attempt, durationSeconds = 60)
        }
        val history = storage.getIqTestResults()
        assertEquals(UserStorage.IQ_TEST_HISTORY_LIMIT, history.size)
        // The oldest attempts fall off, so the newest raw score leads.
        assertEquals(UserStorage.IQ_TEST_HISTORY_LIMIT + 6, history[0].rawScore)
    }

    @Test
    fun personalBestOnlyFlagsAnImprovement() {
        val storage = storage()
        assertTrue(storage.putIqTestResult(seed = 1L, rawScore = 12, durationSeconds = 60).isPersonalBest)
        assertFalse(storage.putIqTestResult(seed = 2L, rawScore = 12, durationSeconds = 60).isPersonalBest)
        assertFalse(storage.putIqTestResult(seed = 3L, rawScore = 9, durationSeconds = 60).isPersonalBest)
        assertTrue(storage.putIqTestResult(seed = 4L, rawScore = 13, durationSeconds = 60).isPersonalBest)
    }

    @Test
    fun everyAttemptAwardsCompletionXp() {
        val storage = storage()
        val before = storage.getTotalXp()
        val award = storage.putIqTestResult(seed = 1L, rawScore = 3, durationSeconds = 60)
        assertEquals(UserStorage.IQ_TEST_COMPLETION_XP, award.xpGained)
        assertEquals(before + UserStorage.IQ_TEST_COMPLETION_XP, storage.getTotalXp())
    }

    @Test
    fun finishingUnlocksCompletionAndHighScoreOnlyPastTheThreshold() {
        val storage = storage()
        storage.putIqTestResult(seed = 1L, rawScore = 10, durationSeconds = 60)
        assertTrue(UserStorage.Achievements.IQ_TEST_COMPLETED in storage.getUnlockedAchievements())
        assertFalse(UserStorage.Achievements.IQ_TEST_HIGH in storage.getUnlockedAchievements())

        val highRaw = (0..30).first { IqScoring.iqFor(it) >= UserStorage.IQ_TEST_HIGH_ACHIEVEMENT_IQ }
        storage.putIqTestResult(seed = 2L, rawScore = highRaw, durationSeconds = 60)
        assertTrue(UserStorage.Achievements.IQ_TEST_HIGH in storage.getUnlockedAchievements())
    }

    @Test
    fun bothAchievementsAppearInDisplayOrder() {
        assertTrue(UserStorage.Achievements.IQ_TEST_COMPLETED in UserStorage.Achievements.displayOrder)
        assertTrue(UserStorage.Achievements.IQ_TEST_HIGH in UserStorage.Achievements.displayOrder)
        assertEquals(
            UserStorage.Achievements.entries.size,
            UserStorage.Achievements.displayOrder.size,
            "every achievement must be reachable from the achievements screen",
        )
    }

    /** The history is capped, so the best has to outlive the attempts that fall off the end. */
    @Test
    fun bestSurvivesAttemptsFallingOffTheHistory() {
        val storage = storage()
        storage.putIqTestResult(seed = 1L, rawScore = 26, durationSeconds = 60)
        repeat(UserStorage.IQ_TEST_HISTORY_LIMIT) { attempt ->
            storage.putIqTestResult(seed = attempt.toLong(), rawScore = 18, durationSeconds = 60)
        }

        assertTrue(storage.getIqTestResults().none { it.rawScore == 26 }, "the record should have aged out")
        assertEquals(26, storage.getBestIqTestRawScore())
    }

    /** A worse score must not read as a personal best just because the old best aged out. */
    @Test
    fun personalBestDoesNotFireAgainAfterTheBestAgesOut() {
        val storage = storage()
        storage.putIqTestResult(seed = 1L, rawScore = 26, durationSeconds = 60)
        repeat(UserStorage.IQ_TEST_HISTORY_LIMIT) { attempt ->
            storage.putIqTestResult(seed = attempt.toLong(), rawScore = 18, durationSeconds = 60)
        }

        assertFalse(storage.putIqTestResult(seed = 99L, rawScore = 25, durationSeconds = 60).isPersonalBest)
        assertTrue(storage.putIqTestResult(seed = 100L, rawScore = 27, durationSeconds = 60).isPersonalBest)
    }

    /** An install written before the best key existed still reports its real best. */
    @Test
    fun bestFallsBackToHistoryWhenTheKeyIsAbsent() {
        val settings = MapSettings()
        val storage = testStorage(settings)
        storage.putIqTestResult(seed = 1L, rawScore = 19, durationSeconds = 60)
        settings.remove(settings.keys.first { it.endsWith(UserStorage.KEY_IQ_TEST_BEST_RAW) })

        assertEquals(19, storage.getBestIqTestRawScore())
    }

    /** A later version may append a field; this build has to keep reading the rows it wrote. */
    @Test
    fun recordsWithAnAppendedFieldStillParse() {
        val settings = MapSettings()
        val storage = testStorage(settings)
        storage.putIqTestResult(seed = 1L, rawScore = 12, durationSeconds = 60)
        val key = settings.keys.first { it.endsWith(UserStorage.KEY_IQ_TEST_RESULTS) }
        settings.putString(key, "1755000000000/7/22/640/somethingnew,1754000000000/8/18/720")

        val history = storage.getIqTestResults()
        assertEquals(listOf(7L, 8L), history.map { it.seed })
        assertEquals(22, history[0].rawScore)
        assertEquals(640, history[0].durationSeconds)
    }

    /** Tolerating extra fields must not start accepting truncated ones. */
    @Test
    fun recordsMissingAFieldAreStillSkipped() {
        val settings = MapSettings()
        val storage = testStorage(settings)
        storage.putIqTestResult(seed = 1L, rawScore = 12, durationSeconds = 60)
        val key = settings.keys.first { it.endsWith(UserStorage.KEY_IQ_TEST_RESULTS) }
        settings.putString(key, "1755000000000/7/22,1754000000000/8/18/720")

        assertEquals(listOf(8L), storage.getIqTestResults().map { it.seed })
    }

    @Test
    fun garbledHistoryIsSkippedRatherThanCrashing() {
        val settings = MapSettings()
        val storage = testStorage(settings)
        storage.putIqTestResult(seed = 1L, rawScore = 12, durationSeconds = 60)
        val key = settings.keys.first { it.endsWith(UserStorage.KEY_IQ_TEST_RESULTS) }
        settings.putString(key, "not-a-record,1/2/3/4,also/bad")
        assertEquals(listOf(2L), storage.getIqTestResults().map { it.seed })
    }
}
