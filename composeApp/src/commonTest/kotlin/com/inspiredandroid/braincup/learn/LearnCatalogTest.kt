package com.inspiredandroid.braincup.learn

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LearnCatalogTest {

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
        assertNull(LearnCatalog.unitBySlug(MathTopic.ARITHMETIC, "not-a-sub-topic"))
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
        is LearnVisual.PlaceValue, is LearnVisual.DecimalGrid,
        is LearnVisual.Coins, is LearnVisual.Ruler, is LearnVisual.Polygon, is LearnVisual.Solid,
        is LearnVisual.Symmetry, is LearnVisual.CircleFigure,
        is LearnVisual.AngleFigure, is LearnVisual.BarChart, is LearnVisual.PieChart,
        is LearnVisual.Pictogram, is LearnVisual.Tally, is LearnVisual.UnitCircleFigure,
        is LearnVisual.Inequality, is LearnVisual.Fraction, is LearnVisual.RatioBar,
        -> true
        // An area grid captions itself only when it was asked for a total.
        is LearnVisual.AreaGrid -> showArea || showPerimeter
        // A right triangle writes all three sides only when it is labelled and nothing is marked
        // unknown; `unknown` already replaces the side being asked for with a question mark. It
        // has no `reveal` of its own, so these two are the whole of its answer-hiding.
        is LearnVisual.RightTriangle -> labels && unknown == null
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
     * Held over the whole catalog. It used to spare the sub-topics still carrying their original
     * grade-slice content, capping them with a ratchet that could only go down; that reached zero
     * when the last of Geometry was reworked, so there is nothing left to spare.
     */
    @Test
    fun questionFiguresDoNotCaptionTheirAnswer() {
        questionVisuals(LearnCatalog.allUnits).forEach { (owner, visual) ->
            assertFalse(
                visual.canCaptionItsResult() && visual.reveal,
                "$owner: a question figure captions its own answer",
            )
        }
    }

    /**
     * The two test questions whose figure is meant to be counted.
     *
     * "How many dots are here?" is answered by counting the array and nothing else, so a figure
     * that comes to the answer is the question rather than a leak. Every other test question has
     * to pose its problem and then leave it alone.
     */
    private val countingIsTheMethod = setOf(
        "How many dots are here?",
        "A tray holds 8 rows of 6 buns. How many buns?",
    )

    /**
     * What a learner can take straight off a figure without answering the question: the totals it
     * counts out, the values it lands on, and the counts it prints in words.
     *
     * Only the variants that count out a whole number are listed, because that is the shape an
     * option is written in. The rest either draw a situation with no total in it - a shape, a
     * solid, an angle - or resolve to a decimal or a fraction, where matching an option by its
     * text would be a coincidence rather than a check.
     */
    private fun LearnVisual.readableValues(): Set<String> = when (this) {
        is LearnVisual.Counters -> setOf("${groups.sum()}")
        // Two frames filling to fifteen have already done the regrouping the question asks for.
        is LearnVisual.TenFrame -> setOf("${filled + added}")
        // Hops are the working, and the working ends on the answer whether or not it is labelled:
        // the tick it lands on can be read off an axis that numbers every tick.
        is LearnVisual.NumberLine -> {
            val travel = if (hopSteps.isNotEmpty()) hopSteps.sum() else jump
            buildSet {
                start?.let { add("${it + travel}") }
                // The second phase of a two-phase line replaces the whole hop, so it lands
                // somewhere else again.
                start?.let { s -> thenJump?.let { add("${s + it}") } }
                addAll(compare.map { it.toString() })
            }
        }
        is LearnVisual.PlaceValue -> buildSet {
            add("${tens * 10 + ones}")
            compare?.let { (t, o) -> add("${t * 10 + o}") }
            plus?.let { (t, o) -> add("${t * 10 + o}") }
        }
        // An array prints its own row count in words, which for a division is the answer, and its
        // remainder sits under it in the shape the option is written in.
        is LearnVisual.ArrayDots -> buildSet {
            add("${rows * cols + leftover}")
            add("$rows")
            if (leftover > 0) add("$rows r $leftover")
        }
        // The cell count only. A grid's side labels are the dimensions the question already
        // states, and counting them as "drawn" made a 3 x 3 grid look like it was offering a
        // choice between 3 and 9 rather than answering with the 9.
        is LearnVisual.AreaGrid -> setOf("${cols * rows}")
        // A ladder is drawn to be continued, so its terms are what it hands over, not where it
        // stops - but a term that is itself an option has been handed over all the same.
        is LearnVisual.Steps -> terms.map { it.toString() }.toSet()
        is LearnVisual.Coins -> setOf("${values.sum()}")
        is LearnVisual.Tally -> setOf("$count")
        else -> emptySet()
    }

    /**
     * A test figure may pose its question but may not answer it.
     *
     * `questionFiguresDoNotCaptionTheirAnswer` checks that a figure does not *label* the value it
     * works out. It cannot see one that simply *draws* it, which is how a 6 x 9 array of countable
     * dots, a number line hopping to its landing tick and a 3 x 3 area grid all sat in tests with
     * the answer already on the panel.
     *
     * A figure drawing several of the options is showing the field rather than the answer - which
     * is exactly what a "which of these is largest" question needs - so only a figure that comes
     * to the correct option and to none of the others is a leak.
     *
     * Lessons are deliberately out of scope. A lesson step's figure works alongside prose that is
     * teaching the method, and showing the hops there is the teaching; a test is the one place the
     * learner is meant to supply the whole of the answer.
     */
    @Test
    fun testFiguresDoNotDrawTheirOwnAnswer() {
        LearnCatalog.allUnits.forEach { unit ->
            unit.quiz.questions.forEach { question ->
                if (question.prompt in countingIsTheMethod) return@forEach
                val readable = question.visual?.readableValues() ?: return@forEach
                val drawn = question.options.filter { it in readable }
                assertFalse(
                    drawn == listOf(question.options[question.correctIndex]),
                    "${unit.id}: the figure for '${question.prompt}' draws its own answer",
                )
            }
        }
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

    /**
     * A typed answer has to be typeable. The lesson keypad is ten digits and a backspace - no
     * decimal point, no minus - so a step answered "0.75" or "-5" cannot be answered at all. Those
     * questions ask as a [LessonStep.Choice] instead.
     */
    @Test
    fun typedAnswersFitTheKeypad() {
        LearnCatalog.allLessons.forEach { lesson ->
            lesson.steps.filterIsInstance<LessonStep.Numeric>().forEach { step ->
                assertTrue(
                    step.answer.isNotEmpty() && step.answer.all { it.isDigit() },
                    "${lesson.id}: '${step.answer}' cannot be typed on the number pad",
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

    /**
     * Every answer explanation has to say something, and a test explanation may not lean on the
     * figure's accent colours.
     *
     * The two places an explanation renders are not alike. A lesson's `FeedbackCard` sits under
     * the figure that posed the question, so `{a:}` / `{b:}` tinting there points at colours the
     * learner can still see. A test's `ReviewCard` draws no figure at all - only the prompt, the
     * answers and this line - so a tint in a quiz explanation refers to nothing.
     */
    @Test
    fun everyStepAndQuestionExplains() {
        LearnCatalog.allLessons.forEach { lesson ->
            lesson.steps.forEach { step ->
                val explanation = when (step) {
                    is LessonStep.Choice -> step.explanation
                    is LessonStep.Numeric -> step.explanation
                    else -> return@forEach
                }
                assertTrue(explanation.isNotBlank(), "${lesson.id}: a question step explains nothing")
            }
        }
        LearnCatalog.allUnits.forEach { unit ->
            unit.quiz.questions.forEach { question ->
                assertTrue(
                    question.explanation.isNotBlank(),
                    "${unit.id}: '${question.prompt}' explains nothing",
                )
                assertFalse(
                    question.explanation.contains("{a:") || question.explanation.contains("{b:"),
                    "${unit.id}: '${question.prompt}' tints its explanation, but the test review draws no figure",
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
