package com.inspiredandroid.braincup.api

import com.inspiredandroid.braincup.learn.CertificateTier
import com.inspiredandroid.braincup.learn.GradeLevel
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

    private fun unit(level: GradeLevel, topic: MathTopic): LearnUnit = requireNotNull(LearnCatalog.unitOf(level, topic)) { "missing unit $level/$topic" }

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
        val middle = unit(GradeLevel.GRADES_6_8, MathTopic.ARITHMETIC)
        val early = unit(GradeLevel.GRADES_1_2, MathTopic.ARITHMETIC)
        storage.completeLearnLesson(middle.lessons.first().id)

        assertEquals(1, storage.getCompletedLearnLessonCount(middle))
        assertEquals(0, storage.getCompletedLearnLessonCount(early))
    }

    @Test
    fun failingTheTestStoresNoCertificate() {
        val storage = UserStorage.forPreview()
        val target = unit(GradeLevel.GRADES_3_5, MathTopic.GEOMETRY)
        val result = storage.recordLearnQuizResult(target, correct = 4, total = 8)

        assertEquals(50, result.percent)
        assertNull(result.tier)
        assertNull(storage.getLearnCertificate(target))
        assertEquals(0, result.xpAward.xpGained)
        assertFalse(UserStorage.Achievements.LEARN_FIRST_CERTIFICATE in storage.getUnlockedAchievements())
    }

    @Test
    fun passingStoresACertificateAndUnlocksTheAchievement() {
        val storage = UserStorage.forPreview()
        val target = unit(GradeLevel.GRADES_3_5, MathTopic.GEOMETRY)
        val result = storage.recordLearnQuizResult(target, correct = 6, total = 8)

        assertEquals(75, result.percent)
        assertEquals(CertificateTier.SILVER, result.tier)
        assertTrue(result.isNewBest)
        assertEquals(UserStorage.learnCertificateXp(CertificateTier.SILVER), result.xpAward.xpGained)

        val stored = storage.getLearnCertificate(target)
        assertNotNull(stored)
        assertEquals(CertificateTier.SILVER, stored.tier)
        assertTrue(UserStorage.Achievements.LEARN_FIRST_CERTIFICATE in storage.getUnlockedAchievements())
    }

    @Test
    fun improvingATierPaysOnlyTheDifference() {
        val storage = UserStorage.forPreview()
        val target = unit(GradeLevel.GRADES_6_8, MathTopic.DATA)
        storage.recordLearnQuizResult(target, correct = 5, total = 8) // 62% -> Bronze
        val xpAfterBronze = storage.getTotalXp()

        val gold = storage.recordLearnQuizResult(target, correct = 8, total = 8)
        assertEquals(CertificateTier.GOLD, gold.tier)
        assertEquals(
            UserStorage.learnCertificateXp(CertificateTier.GOLD) -
                UserStorage.learnCertificateXp(CertificateTier.BRONZE),
            gold.xpAward.xpGained,
        )
        assertEquals(UserStorage.learnCertificateXp(CertificateTier.GOLD), storage.getTotalXp())
        assertTrue(xpAfterBronze < storage.getTotalXp())
    }

    @Test
    fun aWorseRetakeKeepsTheBestCertificate() {
        val storage = UserStorage.forPreview()
        val target = unit(GradeLevel.GRADES_11_12, MathTopic.CALCULUS)
        storage.recordLearnQuizResult(target, correct = 8, total = 8)
        val xpAfterGold = storage.getTotalXp()

        val retake = storage.recordLearnQuizResult(target, correct = 5, total = 8)
        assertFalse(retake.isNewBest)
        assertEquals(0, retake.xpAward.xpGained)
        assertEquals(100, storage.getLearnCertificate(target)?.percent)
        assertEquals(xpAfterGold, storage.getTotalXp())
    }

    @Test
    fun certifyingAWholeBandUnlocksTheGradeAchievement() {
        val storage = UserStorage.forPreview()
        val band = LearnCatalog.units(GradeLevel.GRADES_1_2)
        band.dropLast(1).forEach { storage.recordLearnQuizResult(it, correct = 8, total = 8) }
        assertFalse(UserStorage.Achievements.LEARN_GRADE_CERTIFICATES in storage.getUnlockedAchievements())

        storage.recordLearnQuizResult(band.last(), correct = 8, total = 8)
        assertTrue(UserStorage.Achievements.LEARN_GRADE_CERTIFICATES in storage.getUnlockedAchievements())
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
        val target = unit(GradeLevel.GRADES_9_10, MathTopic.TRIGONOMETRY)
        storage.completeLearnLesson(target.lessons.first().id)
        storage.recordLearnQuizResult(target, correct = 8, total = 8)

        val progress = storage.getLearnUnitProgress(target)
        assertEquals(1, progress.lessonsCompleted)
        assertEquals(target.lessons.size, progress.lessonsTotal)
        assertEquals(CertificateTier.GOLD, progress.tier)
        assertTrue(progress.hasCertificate)
        assertFalse(progress.allLessonsDone)

        assertEquals(
            LearnCatalog.units(GradeLevel.GRADES_9_10).size,
            storage.getLearnUnitProgress(GradeLevel.GRADES_9_10).size,
        )
    }

    @Test
    fun gradeProgressRollsUpItsUnits() {
        val storage = UserStorage.forPreview()
        val band = LearnCatalog.units(GradeLevel.GRADES_1_2)
        storage.completeLearnLesson(band.first().lessons.first().id)
        storage.recordLearnQuizResult(band.first(), correct = 6, total = 8)

        val all = storage.getAllLearnGradeProgress()
        assertEquals(GradeLevel.entries.size, all.size)

        val early = all.first { it.level == GradeLevel.GRADES_1_2 }
        assertEquals(1, early.lessonsCompleted)
        assertEquals(band.sumOf { it.lessons.size }, early.lessonsTotal)
        assertEquals(1, early.certificates)
        assertEquals(band.size, early.unitsTotal)
        assertEquals(CertificateTier.SILVER, early.bestTier)
        assertFalse(early.allCertificatesEarned)
    }
}
