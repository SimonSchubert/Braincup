package com.inspiredandroid.braincup.mathlearning

import com.inspiredandroid.braincup.api.UserStorage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MathLearningTest {

    @Test
    fun testTopicsCountAndEntries() {
        assertEquals(8, MathLearningTopic.entries.size)
        val ids = MathLearningTopic.entries.map { it.id }.toSet()
        assertEquals(8, ids.size)

        for (topic in MathLearningTopic.entries) {
            assertNotNull(MathLearningTopic.getById(topic.id))
            assertTrue(topic.lessons.isNotEmpty())
            assertTrue(topic.testQuestions.isNotEmpty())
        }
    }

    @Test
    fun testUserStorageMathLearningPersistence() {
        val storage = UserStorage.forPreview()
        val topic = MathLearningTopic.ARITHMETIC

        assertFalse(storage.isMathTopicPassed(topic.id))
        assertNull(storage.getMathTopicScore(topic.id))

        // Record a failing score
        val failAward = storage.recordMathTopicResult(topic.id, 50, false)
        assertNull(failAward)
        assertFalse(storage.isMathTopicPassed(topic.id))
        assertEquals(50, storage.getMathTopicScore(topic.id))

        // Record a passing score
        val passAward = storage.recordMathTopicResult(topic.id, 100, true)
        assertNotNull(passAward)
        assertEquals(UserStorage.MATH_TOPIC_PASS_XP, passAward.xpGained)
        assertTrue(storage.isMathTopicPassed(topic.id))
        assertEquals(100, storage.getMathTopicScore(topic.id))
        assertNotNull(storage.getMathTopicTimestamp(topic.id))

        // Repeat passing score does not award XP again
        val repeatAward = storage.recordMathTopicResult(topic.id, 100, true)
        assertNull(repeatAward)
    }
}
