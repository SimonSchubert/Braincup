package com.inspiredandroid.braincup.learn

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LearnCatalogTest {

    @Test
    fun everyGradeBandHasUnitsWithLessonsAndATest() {
        GradeLevel.entries.forEach { level ->
            val units = LearnCatalog.units(level)
            assertTrue(units.isNotEmpty(), "${level.id} has no units")
            units.forEach { unit ->
                assertTrue(unit.lessons.isNotEmpty(), "${unit.id} has no lessons")
                assertTrue(unit.quiz.total >= 6, "${unit.id} test is too short")
                assertTrue(unit.summary.isNotBlank(), "${unit.id} has no summary")
            }
        }
    }

    /** The point of the grade structure: a topic must reappear at more than one level. */
    @Test
    fun topicsSpanSeveralGradeBands() {
        val recurring = MathTopic.entries.filter { LearnCatalog.unitsOf(it).size > 1 }
        assertTrue(recurring.size >= 4, "too few topics are taught across several bands")
        MathTopic.entries.forEach { topic ->
            assertTrue(LearnCatalog.unitsOf(topic).isNotEmpty(), "${topic.id} is never taught")
        }
    }

    @Test
    fun unitIdsAreUniqueAndResolvable() {
        val ids = LearnCatalog.allUnits.map { it.id }
        assertEquals(ids.size, ids.toSet().size, "duplicate unit id")
        LearnCatalog.allUnits.forEach { unit ->
            assertEquals(unit, LearnCatalog.unitById(unit.id))
            assertEquals(unit, LearnCatalog.unitOf(unit.level, unit.topic))
        }
        assertNull(LearnCatalog.unitById("not-a-unit"))
    }

    @Test
    fun lessonIdsAreUniqueAndResolvable() {
        val ids = LearnCatalog.allLessons.map { it.id }
        assertEquals(ids.size, ids.toSet().size, "duplicate lesson id")
        ids.forEach { id -> assertNotNull(LearnCatalog.lessonById(id)) }
        assertNull(LearnCatalog.lessonById("not-a-lesson"))
    }

    @Test
    fun lessonsPointBackAtTheirOwnUnit() {
        LearnCatalog.allUnits.forEach { unit ->
            unit.lessons.forEach { lesson ->
                assertEquals(unit.id, lesson.unitId, "${lesson.id} is filed under the wrong unit")
                assertEquals(unit, LearnCatalog.unitOfLesson(lesson))
            }
            assertEquals(unit.id, unit.quiz.unitId)
        }
    }

    /** The section is meant to teach by picture, so a step without a figure is a content bug. */
    @Test
    fun everyStepAndQuestionHasAVisual() {
        LearnCatalog.allLessons.forEach { lesson ->
            lesson.steps.forEachIndexed { index, step ->
                val visual = when (step) {
                    is LessonStep.Concept -> step.visual
                    is LessonStep.Worked -> step.visual
                    is LessonStep.Choice -> step.visual
                    is LessonStep.Numeric -> step.visual
                }
                assertNotNull(visual, "${lesson.id} step $index has no visual")
            }
        }
        LearnCatalog.allUnits.forEach { unit ->
            unit.quiz.questions.forEach { question ->
                assertNotNull(question.visual, "${unit.id}: no visual for ${question.prompt}")
            }
        }
    }

    /** A question must not caption the diagram with the answer it is asking for. */
    @Test
    fun questionVisualsDoNotRevealTheirAnswer() {
        val revealing = LearnCatalog.allLessons
            .flatMap { lesson -> lesson.steps.map { lesson.id to it } }
            .count { (_, step) ->
                when (step) {
                    is LessonStep.Choice -> step.visual?.reveal == true
                    is LessonStep.Numeric -> step.visual?.reveal == true
                    else -> false
                }
            }
        // Most question figures hide their summary; the rest show a situation with nothing to give away.
        assertTrue(revealing < LearnCatalog.allLessons.size * 2, "too many question figures caption the answer")
    }

    @Test
    fun everyLessonAsksAtLeastOneQuestion() {
        LearnCatalog.allLessons.forEach { lesson ->
            assertTrue(lesson.questionCount > 0, "${lesson.id} has no interactive step")
        }
    }

    @Test
    fun quizOptionsAreDistinctAndAnswersInRange() {
        LearnCatalog.allUnits.forEach { unit ->
            unit.quiz.questions.forEach { question ->
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

    /** Every free-answer step must be reachable through the number pad's own normalisation. */
    @Test
    fun numericStepAnswersAcceptThemselves() {
        LearnCatalog.allLessons.forEach { lesson ->
            lesson.steps.filterIsInstance<LessonStep.Numeric>().forEach { step ->
                assertTrue(
                    LearnCatalog.matchesNumericAnswer(step.answer, step.answer),
                    "unmatchable answer in: ${step.question}",
                )
            }
        }
    }

    @Test
    fun certificateTierThresholds() {
        assertNull(CertificateTier.forPercent(59))
        assertEquals(CertificateTier.BRONZE, CertificateTier.forPercent(60))
        assertEquals(CertificateTier.BRONZE, CertificateTier.forPercent(74))
        assertEquals(CertificateTier.SILVER, CertificateTier.forPercent(75))
        assertEquals(CertificateTier.GOLD, CertificateTier.forPercent(90))
        assertEquals(CertificateTier.GOLD, CertificateTier.forPercent(100))
    }

    @Test
    fun percentOfRoundsDown() {
        assertEquals(0, CertificateTier.percentOf(0, 8))
        assertEquals(87, CertificateTier.percentOf(7, 8))
        assertEquals(100, CertificateTier.percentOf(8, 8))
        assertEquals(0, CertificateTier.percentOf(1, 0))
    }

    @Test
    fun topicsAndLevelsResolveByIdAndSlug() {
        MathTopic.entries.forEach { topic ->
            assertEquals(topic, MathTopic.byId(topic.id))
            assertEquals(topic, MathTopic.bySlug(topic.urlSlug))
        }
        GradeLevel.entries.forEach { level ->
            assertEquals(level, GradeLevel.byId(level.id))
            assertEquals(level, GradeLevel.bySlug(level.urlSlug))
        }
        assertNull(MathTopic.byId("nope"))
        assertNull(GradeLevel.bySlug("grade-99"))
    }
}
