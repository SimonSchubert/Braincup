package com.inspiredandroid.braincup.learn

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LearnCatalogTest {

    private companion object {
        /**
         * How many question figures in the not-yet-reworked sub-topics still caption their own
         * answer. A ratchet, not a target: lower it as topics are reworked, never raise it.
         */
        const val RATCHET = 50
    }

    @Test
    fun everyTopicHasSubTopicsWithLessonsAndATest() {
        MathTopic.entries.forEach { topic ->
            val units = LearnCatalog.units(topic)
            assertTrue(units.isNotEmpty(), "${topic.id} has no sub-topics")
            units.forEach { unit ->
                assertTrue(unit.lessons.isNotEmpty(), "${unit.id} has no lessons")
                assertTrue(unit.quiz.total >= 6, "${unit.id} test is too short")
                assertTrue(unit.title.isNotBlank(), "${unit.id} has no title")
                assertTrue(unit.summary.isNotBlank(), "${unit.id} has no summary")
            }
        }
    }

    /** A topic's ladder is meant to be walked top to bottom, so it may never step backwards. */
    @Test
    fun subTopicsAreOrderedEasiestFirst() {
        MathTopic.entries.forEach { topic ->
            val levels = LearnCatalog.units(topic).map { it.level.ordinal }
            assertEquals(levels.sorted(), levels, "${topic.id} ladder is out of order")
        }
    }

    /** Sub-topic slugs are the web address inside a topic, so they must not collide there. */
    @Test
    fun subTopicSlugsAreUniqueWithinTheirTopic() {
        MathTopic.entries.forEach { topic ->
            val units = LearnCatalog.units(topic)
            val slugs = units.map { it.urlSlug }
            assertEquals(slugs.size, slugs.toSet().size, "${topic.id} has a duplicate slug")
            units.forEach { unit ->
                assertEquals(unit, LearnCatalog.unitBySlug(topic, unit.urlSlug))
                assertTrue(unit.id.startsWith("${topic.id}-"), "${unit.id} is not filed under its topic")
            }
        }
        assertNull(LearnCatalog.unitBySlug(MathTopic.ALGEBRA, "not-a-sub-topic"))
    }

    @Test
    fun unitIdsAreUniqueAndResolvable() {
        val ids = LearnCatalog.allUnits.map { it.id }
        assertEquals(ids.size, ids.toSet().size, "duplicate unit id")
        LearnCatalog.allUnits.forEach { unit ->
            assertEquals(unit, LearnCatalog.unitById(unit.id))
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

    /**
     * Figures that can caption themselves with the value they work out. The rest of the variants
     * only ever draw a situation, so their inherited `reveal` says nothing about the answer and
     * counting them would drown the check in false positives.
     */
    private fun LearnVisual.canCaptionItsResult(): Boolean = when (this) {
        is LearnVisual.Counters, is LearnVisual.TenFrame, is LearnVisual.NumberLine,
        is LearnVisual.PlaceValue, is LearnVisual.DecimalGrid, is LearnVisual.ArrayDots,
        is LearnVisual.Coins, is LearnVisual.Ruler, is LearnVisual.Polygon, is LearnVisual.Solid,
        is LearnVisual.Symmetry, is LearnVisual.RightTriangle, is LearnVisual.CircleFigure,
        is LearnVisual.AngleFigure, is LearnVisual.BarChart, is LearnVisual.PieChart,
        is LearnVisual.Pictogram, is LearnVisual.Tally, is LearnVisual.UnitCircleFigure,
        is LearnVisual.Inequality, is LearnVisual.Fraction, is LearnVisual.RatioBar,
        -> true
        // An area grid captions itself only when it was asked for a total.
        is LearnVisual.AreaGrid -> showArea || showPerimeter
        else -> false
    }

    private fun questionVisuals(units: List<LearnUnit>): List<Pair<String, LearnVisual>> = units.flatMap { it.lessons }.flatMap { lesson ->
        lesson.steps.mapNotNull { step ->
            val visual = when (step) {
                is LessonStep.Choice -> step.visual
                is LessonStep.Numeric -> step.visual
                else -> null
            }
            visual?.let { lesson.id to it }
        }
    } + units.flatMap { unit ->
        unit.quiz.questions.mapNotNull { q -> q.visual?.let { unit.id to it } }
    }

    /**
     * A question must not caption its figure with the answer it is asking for.
     *
     * Held strictly on the sub-topics already reworked into hand-authored ladders, whether that is
     * a whole topic ([reworked]) or the ones done so far inside a topic still being worked through
     * ([reworkedUnits]). The rest still carry their original grade-slice content and are only kept
     * from getting worse; move a topic across as its last sub-topic lands.
     */
    @Test
    fun questionFiguresDoNotCaptionTheirAnswer() {
        val reworked = setOf(MathTopic.ALGEBRA, MathTopic.ARITHMETIC)
        // The sub-topics already done inside a topic still being worked through. Empty while
        // arithmetic holds a place in [reworked] on its own: every one of its sub-topics is
        // hand-authored now, so any new one joins them under the strict rule.
        val reworkedUnits = emptySet<String>()
        val (done, pending) = LearnCatalog.allUnits.partition {
            it.topic in reworked || it.id in reworkedUnits
        }

        questionVisuals(done).forEach { (owner, visual) ->
            assertFalse(
                visual.canCaptionItsResult() && visual.reveal,
                "$owner: a question figure captions its own answer",
            )
        }

        // A ratchet, not a target: this number may only ever go down, and reaches 0 when the last
        // sub-topic is reworked and every unit is held strictly above.
        val leaking = questionVisuals(pending).count { it.second.canCaptionItsResult() && it.second.reveal }
        assertTrue(leaking <= RATCHET, "not-yet-reworked sub-topics got more spoiling figures, not fewer: $leaking")
    }

    /**
     * A question step's formula is the question written out, so it has to actually ask something
     * and must not already contain the answer it is asking for.
     */
    @Test
    fun questionFormulasAskRatherThanTell() {
        LearnCatalog.allLessons.forEach { lesson ->
            lesson.steps.forEach { step ->
                val (formula, answer) = when (step) {
                    is LessonStep.Choice -> step.formula to step.options[step.correctIndex]
                    is LessonStep.Numeric -> step.formula to step.answer
                    else -> null to ""
                }
                if (formula == null) return@forEach
                assertTrue(formula.contains("?"), "${lesson.id}: '$formula' states instead of asking")
                assertFalse(
                    formula.contains(answer),
                    "${lesson.id}: '$formula' gives away its own answer",
                )
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
    fun onlyAFlawlessRunEarnsTheCertificate() {
        assertTrue(Certificate.isEarnedBy(8, 8))
        assertFalse(Certificate.isEarnedBy(7, 8))
        assertFalse(Certificate.isEarnedBy(0, 8))
        assertFalse(Certificate.isEarnedBy(0, 0))
        assertFalse(Certificate.isEarnedBy(1, 0))
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
