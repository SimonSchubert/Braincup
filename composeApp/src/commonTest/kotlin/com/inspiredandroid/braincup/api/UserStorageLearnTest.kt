package com.inspiredandroid.braincup.api

import com.inspiredandroid.braincup.learn.LearnCatalog
import com.inspiredandroid.braincup.learn.LearnUnit
import com.inspiredandroid.braincup.learn.MathTopic
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UserStorageLearnTest {

    private fun unit(topic: MathTopic, slug: String): LearnUnit = requireNotNull(LearnCatalog.unitBySlug(topic, slug)) { "missing sub-topic $topic/$slug" }

    private val firstUnit = LearnCatalog.allUnits.first()

    @Test
    fun completingALessonAwardsXpOnce() {
        val storage = UserStorage.forPreview()
        val lessonId = firstUnit.lessons.first().id

        val first = storage.completeLearnLesson(lessonId)
        assertEquals(UserStorage.LEARN_LESSON_XP, first.xpGained)
        assertTrue(storage.isLearnLessonCompleted(lessonId))

        val replay = storage.completeLearnLesson(lessonId)
        assertEquals(0, replay.xpGained)
        assertEquals(UserStorage.LEARN_LESSON_XP, storage.getTotalXp())
    }

    /** The same topic exists at several levels, so progress must not leak between them. */
    @Test
    fun lessonProgressCountsOnlyItsOwnUnit() {
        val storage = UserStorage.forPreview()
        val middle = unit(MathTopic.ARITHMETIC, "percent")
        val early = unit(MathTopic.ARITHMETIC, "counting")
        storage.completeLearnLesson(middle.lessons.first().id)

        assertEquals(1, storage.getCompletedLearnLessonCount(middle))
        assertEquals(0, storage.getCompletedLearnLessonCount(early))
    }

    /** One wrong answer is enough to miss out: the certificate is all-or-nothing. */
    @Test
    fun anythingShortOfPerfectStoresNoCertificate() {
        val storage = UserStorage.forPreview()
        val target = unit(MathTopic.GEOMETRY, "angles-and-symmetry")
        val result = storage.recordLearnQuizResult(target, correct = 7, total = 8)

        assertFalse(result.earnedCertificate)
        assertNull(storage.getLearnCertificate(target))
        assertEquals(0, result.xpAward.xpGained)
        assertFalse(UserStorage.Achievements.LEARN_FIRST_CERTIFICATE in storage.getUnlockedAchievements())
    }

    @Test
    fun aFlawlessRunStoresACertificateAndUnlocksTheAchievement() {
        val storage = UserStorage.forPreview()
        val target = unit(MathTopic.GEOMETRY, "angles-and-symmetry")
        val result = storage.recordLearnQuizResult(target, correct = 8, total = 8)

        assertTrue(result.earnedCertificate)
        assertEquals(UserStorage.LEARN_CERTIFICATE_XP, result.xpAward.xpGained)

        assertNotNull(storage.getLearnCertificate(target))
        assertTrue(UserStorage.Achievements.LEARN_FIRST_CERTIFICATE in storage.getUnlockedAchievements())
    }

    @Test
    fun retakingACertifiedTestPaysNothingMore() {
        val storage = UserStorage.forPreview()
        val target = unit(MathTopic.ARITHMETIC, "decimals")
        storage.recordLearnQuizResult(target, correct = 8, total = 8)
        val xpAfterCertificate = storage.getTotalXp()
        assertEquals(UserStorage.LEARN_CERTIFICATE_XP, xpAfterCertificate)

        val perfectAgain = storage.recordLearnQuizResult(target, correct = 8, total = 8)
        assertTrue(perfectAgain.earnedCertificate)
        assertEquals(0, perfectAgain.xpAward.xpGained)

        // A failed retake leaves the certificate it was earned with in place.
        val slip = storage.recordLearnQuizResult(target, correct = 5, total = 8)
        assertFalse(slip.earnedCertificate)
        assertNotNull(storage.getLearnCertificate(target))
        assertEquals(xpAfterCertificate, storage.getTotalXp())
    }

    @Test
    fun certifyingAWholeTopicUnlocksTheTopicAchievement() {
        val storage = UserStorage.forPreview()
        val ladder = LearnCatalog.units(MathTopic.GEOMETRY)
        ladder.dropLast(1).forEach { storage.recordLearnQuizResult(it, correct = 8, total = 8) }
        assertFalse(UserStorage.Achievements.LEARN_TOPIC_CERTIFICATES in storage.getUnlockedAchievements())

        storage.recordLearnQuizResult(ladder.last(), correct = 8, total = 8)
        assertTrue(UserStorage.Achievements.LEARN_TOPIC_CERTIFICATES in storage.getUnlockedAchievements())
        assertFalse(UserStorage.Achievements.LEARN_ALL_CERTIFICATES in storage.getUnlockedAchievements())
    }

    @Test
    fun certificatesInEveryUnitUnlockTheScholarAchievement() {
        val storage = UserStorage.forPreview()
        LearnCatalog.allUnits.dropLast(1).forEach {
            storage.recordLearnQuizResult(it, correct = 8, total = 8)
        }
        assertFalse(UserStorage.Achievements.LEARN_ALL_CERTIFICATES in storage.getUnlockedAchievements())

        storage.recordLearnQuizResult(LearnCatalog.allUnits.last(), correct = 8, total = 8)
        assertTrue(UserStorage.Achievements.LEARN_ALL_CERTIFICATES in storage.getUnlockedAchievements())
        assertEquals(LearnCatalog.totalUnitCount, storage.getLearnCertificateCount())
    }

    @Test
    fun unitProgressReflectsLessonsAndCertificate() {
        val storage = UserStorage.forPreview()
        val target = unit(MathTopic.GEOMETRY, "flat-shapes")
        storage.completeLearnLesson(target.lessons.first().id)
        storage.recordLearnQuizResult(target, correct = 8, total = 8)

        val progress = storage.getLearnUnitProgress(target)
        assertEquals(1, progress.lessonsCompleted)
        assertEquals(target.lessons.size, progress.lessonsTotal)
        assertTrue(progress.hasCertificate)
        assertFalse(progress.allLessonsDone)

        assertEquals(
            LearnCatalog.units(MathTopic.GEOMETRY).size,
            storage.getLearnUnitProgress(MathTopic.GEOMETRY).size,
        )
    }

    @Test
    fun topicProgressRollsUpItsSubTopics() {
        val storage = UserStorage.forPreview()
        val ladder = LearnCatalog.units(MathTopic.ARITHMETIC)
        storage.completeLearnLesson(ladder.first().lessons.first().id)
        storage.recordLearnQuizResult(ladder.first(), correct = 8, total = 8)

        val all = storage.getAllLearnTopicProgress()
        assertEquals(MathTopic.entries.size, all.size)

        val arithmetic = all.first { it.topic == MathTopic.ARITHMETIC }
        assertEquals(1, arithmetic.lessonsCompleted)
        assertEquals(ladder.sumOf { it.lessons.size }, arithmetic.lessonsTotal)
        assertEquals(1, arithmetic.certificates)
        assertEquals(ladder.size, arithmetic.unitsTotal)
        assertFalse(arithmetic.allCertificatesEarned)
    }
}
