package com.inspiredandroid.braincup.api

import com.inspiredandroid.braincup.learn.CertificateGrade
import com.inspiredandroid.braincup.learn.LearnCatalog
import com.inspiredandroid.braincup.learn.MathTopic
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UserStorageLearnTest {

    @Test
    fun completingALessonAwardsXpOnce() {
        val storage = UserStorage.forPreview()
        val lessonId = LearnCatalog.lessons(MathTopic.ARITHMETIC).first().id

        val first = storage.completeLearnLesson(lessonId)
        assertEquals(UserStorage.LEARN_LESSON_XP, first.xpGained)
        assertTrue(storage.isLearnLessonCompleted(lessonId))

        val replay = storage.completeLearnLesson(lessonId)
        assertEquals(0, replay.xpGained)
        assertEquals(UserStorage.LEARN_LESSON_XP, storage.getTotalXp())
    }

    @Test
    fun lessonProgressCountsOnlyItsOwnTopic() {
        val storage = UserStorage.forPreview()
        storage.completeLearnLesson(LearnCatalog.lessons(MathTopic.ALGEBRA).first().id)

        assertEquals(1, storage.getCompletedLearnLessonCount(MathTopic.ALGEBRA))
        assertEquals(0, storage.getCompletedLearnLessonCount(MathTopic.GEOMETRY))
    }

    @Test
    fun failingTheTestStoresNoCertificate() {
        val storage = UserStorage.forPreview()
        val result = storage.recordLearnQuizResult(MathTopic.GEOMETRY, correct = 4, total = 8)

        assertEquals(50, result.percent)
        assertNull(result.grade)
        assertNull(storage.getLearnCertificate(MathTopic.GEOMETRY))
        assertEquals(0, result.xpAward.xpGained)
        assertFalse(UserStorage.Achievements.LEARN_FIRST_CERTIFICATE in storage.getUnlockedAchievements())
    }

    @Test
    fun passingStoresACertificateAndUnlocksTheAchievement() {
        val storage = UserStorage.forPreview()
        val result = storage.recordLearnQuizResult(MathTopic.GEOMETRY, correct = 6, total = 8)

        assertEquals(75, result.percent)
        assertEquals(CertificateGrade.SILVER, result.grade)
        assertTrue(result.isNewBest)
        assertEquals(UserStorage.learnCertificateXp(CertificateGrade.SILVER), result.xpAward.xpGained)

        val stored = storage.getLearnCertificate(MathTopic.GEOMETRY)
        assertNotNull(stored)
        assertEquals(CertificateGrade.SILVER, stored.grade)
        assertTrue(UserStorage.Achievements.LEARN_FIRST_CERTIFICATE in storage.getUnlockedAchievements())
    }

    @Test
    fun improvingATierPaysOnlyTheDifference() {
        val storage = UserStorage.forPreview()
        storage.recordLearnQuizResult(MathTopic.DATA, correct = 5, total = 8) // 62% → Bronze
        val xpAfterBronze = storage.getTotalXp()

        val gold = storage.recordLearnQuizResult(MathTopic.DATA, correct = 8, total = 8)
        assertEquals(CertificateGrade.GOLD, gold.grade)
        assertEquals(
            UserStorage.learnCertificateXp(CertificateGrade.GOLD) -
                UserStorage.learnCertificateXp(CertificateGrade.BRONZE),
            gold.xpAward.xpGained,
        )
        assertEquals(UserStorage.learnCertificateXp(CertificateGrade.GOLD), storage.getTotalXp())
        assertTrue(xpAfterBronze < storage.getTotalXp())
    }

    @Test
    fun aWorseRetakeKeepsTheBestCertificate() {
        val storage = UserStorage.forPreview()
        storage.recordLearnQuizResult(MathTopic.CALCULUS, correct = 8, total = 8)
        val xpAfterGold = storage.getTotalXp()

        val retake = storage.recordLearnQuizResult(MathTopic.CALCULUS, correct = 5, total = 8)
        assertFalse(retake.isNewBest)
        assertEquals(0, retake.xpAward.xpGained)
        assertEquals(100, storage.getLearnCertificate(MathTopic.CALCULUS)?.percent)
        assertEquals(xpAfterGold, storage.getTotalXp())
    }

    @Test
    fun certificatesInEveryTopicUnlockTheScholarAchievement() {
        val storage = UserStorage.forPreview()
        MathTopic.entries.dropLast(1).forEach {
            storage.recordLearnQuizResult(it, correct = 8, total = 8)
        }
        assertFalse(UserStorage.Achievements.LEARN_ALL_CERTIFICATES in storage.getUnlockedAchievements())

        storage.recordLearnQuizResult(MathTopic.entries.last(), correct = 8, total = 8)
        assertTrue(UserStorage.Achievements.LEARN_ALL_CERTIFICATES in storage.getUnlockedAchievements())
        assertEquals(MathTopic.entries.size, storage.getLearnCertificateCount())
    }

    @Test
    fun topicProgressReflectsLessonsAndCertificate() {
        val storage = UserStorage.forPreview()
        storage.completeLearnLesson(LearnCatalog.lessons(MathTopic.TRIGONOMETRY).first().id)
        storage.recordLearnQuizResult(MathTopic.TRIGONOMETRY, correct = 8, total = 8)

        val progress = storage.getLearnTopicProgress(MathTopic.TRIGONOMETRY)
        assertEquals(1, progress.lessonsCompleted)
        assertEquals(LearnCatalog.lessons(MathTopic.TRIGONOMETRY).size, progress.lessonsTotal)
        assertEquals(CertificateGrade.GOLD, progress.grade)
        assertTrue(progress.hasCertificate)
        assertFalse(progress.allLessonsDone)

        assertEquals(MathTopic.entries.size, storage.getAllLearnTopicProgress().size)
    }
}
