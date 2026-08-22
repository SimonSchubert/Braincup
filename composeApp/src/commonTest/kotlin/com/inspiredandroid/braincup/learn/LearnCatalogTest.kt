package com.inspiredandroid.braincup.learn

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LearnCatalogTest {

    @Test
    fun everyTopicHasLessonsAndATest() {
        MathTopic.entries.forEach { topic ->
            assertTrue(LearnCatalog.lessons(topic).isNotEmpty(), "${topic.id} has no lessons")
            assertTrue(LearnCatalog.quiz(topic).total >= 6, "${topic.id} test is too short")
        }
    }

    @Test
    fun lessonIdsAreUniqueAndResolvable() {
        val ids = LearnCatalog.allLessons.map { it.id }
        assertEquals(ids.size, ids.toSet().size, "duplicate lesson id")
        ids.forEach { id -> assertNotNull(LearnCatalog.lessonById(id)) }
        assertNull(LearnCatalog.lessonById("not-a-lesson"))
    }

    @Test
    fun lessonsAreFiledUnderTheirOwnTopic() {
        MathTopic.entries.forEach { topic ->
            LearnCatalog.lessons(topic).forEach { lesson ->
                assertEquals(topic, lesson.topic, "${lesson.id} is filed under the wrong topic")
            }
        }
    }

    @Test
    fun everyLessonAsksAtLeastOneQuestion() {
        LearnCatalog.allLessons.forEach { lesson ->
            assertTrue(lesson.questionCount > 0, "${lesson.id} has no interactive step")
        }
    }

    @Test
    fun quizOptionsAreDistinctAndAnswersInRange() {
        MathTopic.entries.forEach { topic ->
            LearnCatalog.quiz(topic).questions.forEach { question ->
                assertTrue(question.options.size >= 2, "${question.prompt} needs options")
                assertEquals(
                    question.options.size,
                    question.options.toSet().size,
                    "duplicate option in: ${question.prompt}",
                )
                assertTrue(question.correctIndex in question.options.indices)
            }
        }
    }

    @Test
    fun choiceStepOptionsAreDistinct() {
        LearnCatalog.allLessons.forEach { lesson ->
            lesson.steps.filterIsInstance<LessonStep.Choice>().forEach { step ->
                assertEquals(
                    step.options.size,
                    step.options.toSet().size,
                    "duplicate option in: ${step.question}",
                )
            }
        }
    }

    @Test
    fun numericAnswersTolerateFormatting() {
        assertTrue(LearnCatalog.matchesNumericAnswer("6700", "6700"))
        assertTrue(LearnCatalog.matchesNumericAnswer(" 6 700 ", "6700"))
        assertTrue(LearnCatalog.matchesNumericAnswer("6,700", "6700"))
        assertTrue(LearnCatalog.matchesNumericAnswer("30.0", "30"))
        assertFalse(LearnCatalog.matchesNumericAnswer("670", "6700"))
        assertFalse(LearnCatalog.matchesNumericAnswer("", "6700"))
        assertTrue(LearnCatalog.matchesNumericAnswer("0.75", "0.75"))
    }

    @Test
    fun certificateGradeThresholds() {
        assertNull(CertificateGrade.forPercent(59))
        assertEquals(CertificateGrade.BRONZE, CertificateGrade.forPercent(60))
        assertEquals(CertificateGrade.BRONZE, CertificateGrade.forPercent(74))
        assertEquals(CertificateGrade.SILVER, CertificateGrade.forPercent(75))
        assertEquals(CertificateGrade.GOLD, CertificateGrade.forPercent(90))
        assertEquals(CertificateGrade.GOLD, CertificateGrade.forPercent(100))
    }

    @Test
    fun percentOfRoundsDown() {
        assertEquals(0, CertificateGrade.percentOf(0, 8))
        assertEquals(87, CertificateGrade.percentOf(7, 8))
        assertEquals(100, CertificateGrade.percentOf(8, 8))
        assertEquals(0, CertificateGrade.percentOf(1, 0))
    }

    @Test
    fun topicsResolveByIdAndSlug() {
        MathTopic.entries.forEach { topic ->
            assertEquals(topic, MathTopic.byId(topic.id))
            assertEquals(topic, MathTopic.bySlug(topic.urlSlug))
        }
        assertNull(MathTopic.byId("nope"))
    }
}
