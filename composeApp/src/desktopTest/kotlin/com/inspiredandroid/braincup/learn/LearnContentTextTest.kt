package com.inspiredandroid.braincup.learn

import androidx.compose.ui.graphics.Color
import com.inspiredandroid.braincup.ui.components.formatMathSymbols
import com.inspiredandroid.braincup.ui.components.learn.FigureRole
import com.inspiredandroid.braincup.ui.components.learn.FigureRoles
import com.inspiredandroid.braincup.ui.components.learn.roles
import com.inspiredandroid.braincup.ui.components.statesItsResult
import com.inspiredandroid.braincup.ui.components.withFormulaColors
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import com.inspiredandroid.braincup.ui.theme.WorkingBlue
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getPluralString
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.getSystemResourceEnvironment
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Content checks that have to read the lesson text.
 *
 * They live here rather than in `commonTest` because the catalog holds `StringResource` keys since
 * the text moved to `strings.xml`, and resolving one needs a resource environment that only a JVM
 * test has. Everything about the catalog's *shape* - ids, ordering, figures, answers - stays in
 * `LearnCatalogTest` alongside the model.
 *
 * These are the guards that catch an authoring slip, so they are worth the awkward home: a step
 * that states its own answer, a figure that draws it, an explanation tinted for a figure the
 * screen never shows.
 */
class LearnContentTextTest {

    private fun StringResource.text(): String = runBlocking { getString(getSystemResourceEnvironment(), this@text) }

    private fun List<StringResource>.texts(): List<String> = map { it.text() }

    /** A run of catalog text: notation reads as authored, words come from the resource. */
    private fun CatalogText.text(): String = when (this) {
        is CatalogText.Value -> text
        is CatalogText.Words -> res.text()
        is CatalogText.Counted -> runBlocking {
            getPluralString(getSystemResourceEnvironment(), res, count, count)
        }
        is CatalogText.Formatted -> runBlocking {
            getString(getSystemResourceEnvironment(), res, *args.toTypedArray())
        }
    }

    @JvmName("catalogTexts")
    private fun List<CatalogText>.texts(): List<String> = map { it.text() }

    /** A step that asks something, as the learner reads it. */
    private data class Question(
        val where: String,
        val formula: String?,
        val prompt: String,
        val explanation: String,
        val answer: String,
    )

    private fun lessonQuestions(): List<Question> = LearnCatalog.allLessons.flatMap { lesson ->
        lesson.steps.mapIndexedNotNull { index, step ->
            when (step) {
                is LessonStep.Choice -> Question(
                    where = "${lesson.id} step $index",
                    formula = step.formula?.text(),
                    prompt = step.question.text(),
                    explanation = step.explanation.text(),
                    answer = step.options[step.correctIndex].text(),
                )
                is LessonStep.Numeric -> Question(
                    where = "${lesson.id} step $index",
                    formula = step.formula?.text(),
                    prompt = step.question.text(),
                    explanation = step.explanation.text(),
                    answer = step.answer,
                )
                else -> null
            }
        }
    }

    @Test
    fun everySubTopicIsNamedAndSummarised() {
        LearnCatalog.allUnits.forEach { unit ->
            assertTrue(unit.title.text().isNotBlank(), "${unit.id} has no title")
            assertTrue(unit.summary.text().isNotBlank(), "${unit.id} has no summary")
            unit.lessons.forEach { lesson ->
                assertTrue(lesson.title.text().isNotBlank(), "${lesson.id} has no title")
                assertTrue(lesson.summary.text().isNotBlank(), "${lesson.id} has no summary")
            }
        }
    }

    /**
     * A question step's formula is the question written out, so it has to actually ask something
     * and must not already contain the answer it is asking for.
     */
    @Test
    fun questionFormulasAskRatherThanTell() {
        lessonQuestions().forEach { q ->
            val formula = q.formula ?: return@forEach
            assertTrue(formula.contains("?"), "${q.where}: '$formula' states instead of asking")
            assertFalse(formula.contains(q.answer), "${q.where}: '$formula' gives away its own answer")
        }
    }

    @Test
    fun everyStepAndQuestionExplains() {
        lessonQuestions().forEach { q ->
            assertTrue(q.explanation.isNotBlank(), "${q.where} explains nothing")
        }
        LearnCatalog.allUnits.forEach { unit ->
            unit.quiz.questions.forEach { question ->
                val prompt = question.prompt.text()
                val explanation = question.explanation.text()
                assertTrue(explanation.isNotBlank(), "${unit.id}: '$prompt' explains nothing")
                assertFalse(
                    explanation.contains("{a:") || explanation.contains("{b:"),
                    "${unit.id}: '$prompt' tints its explanation, but the test review draws no figure",
                )
            }
        }
    }

    /**
     * A test question may not be answerable by reading its own figure.
     *
     * Lessons are deliberately out of scope: a lesson step's figure works alongside prose teaching
     * the method, and showing the hops there is the teaching.
     */
    @Test
    fun testFiguresDoNotDrawTheirOwnAnswer() {
        LearnCatalog.allUnits.forEach { unit ->
            unit.quiz.questions.forEach { question ->
                val prompt = question.prompt.text()
                if (prompt in COUNTING_IS_THE_METHOD) return@forEach
                val readable = question.visual?.readableValues() ?: return@forEach
                val options = question.options.texts()
                val drawn = options.filter { it in readable }
                assertFalse(
                    drawn == listOf(options[question.correctIndex]),
                    "${unit.id}: the figure for '$prompt' draws its own answer",
                )
            }
        }
    }

    /**
     * Text naming a fraction the figure draws has to tag it the colour that figure draws it in.
     *
     * Only the fractions the figure actually draws are checked, and only where they are written
     * out in full, because "3/4" as a run of text says which bar it means and nothing else does.
     */
    @Test
    fun fractionTextMatchesTheBarItNames() {
        fun taggedRuns(text: String, tag: Char): List<String> = Regex("""\{$tag:([^\}]*)\}""").findAll(text).map { it.groupValues[1] }.toList()

        fun namesFraction(text: String, fraction: String): Boolean = Regex("(?<![0-9/])" + Regex.escape(fraction) + "(?![0-9/])").containsMatchIn(text)

        fun check(where: String, visual: LearnVisual?, texts: List<String>) {
            val fraction = visual as? LearnVisual.Fraction ?: return
            val second = fraction.plus ?: fraction.compare ?: return
            val given = "${fraction.numerator}/${fraction.denominator}"
            val working = "${second.first}/${second.second}"
            if (given == working) return
            texts.forEach { text ->
                val inWorking = taggedRuns(text, 'b').any { namesFraction(it, working) }
                assertTrue(
                    !namesFraction(text, working) || inWorking,
                    "$where: the figure draws $working in the working colour, but '$text' does not tag it {b:}",
                )
                assertFalse(
                    taggedRuns(text, 'b').any { namesFraction(it, given) },
                    "$where: '$text' tags $given as working, but the figure draws it as the given",
                )
            }
        }

        LearnCatalog.allLessons.forEach { lesson ->
            lesson.steps.forEachIndexed { index, step ->
                val where = "${lesson.id} step $index"
                when (step) {
                    is LessonStep.Concept ->
                        check(where, step.visual, listOfNotNull(step.formula?.text(), step.body.text()))
                    is LessonStep.Worked ->
                        check(where, step.visual, listOf(step.problem.text()) + step.lines.texts())
                    is LessonStep.Choice -> check(
                        where,
                        step.visual,
                        listOfNotNull(step.formula?.text(), step.question.text(), step.explanation.text()),
                    )
                    is LessonStep.Numeric -> check(
                        where,
                        step.visual,
                        listOfNotNull(step.formula?.text(), step.question.text(), step.explanation.text()),
                    )
                }
            }
        }
        LearnCatalog.allUnits.forEach { unit ->
            unit.quiz.questions.forEach { question ->
                val prompt = question.prompt.text()
                check("${unit.id}: '$prompt'", question.visual, listOf(prompt))
            }
        }
    }

    /**
     * No formula in the catalog prints its answer in the colour meaning "what you were handed".
     *
     * It is easy to author a step that states a result and never notice the card and the figure
     * have gone out of step.
     */
    @Test
    fun noCatalogFormulaPrintsItsResultAsAGiven() {
        val offenders = LearnCatalog.allLessons.flatMap { lesson ->
            lesson.steps.mapIndexedNotNull { index, step ->
                // A worked step states its problem in the same card a teaching step states its
                // formula in, so it is held to the same rule. It was skipped here while the only
                // thing that could colour a result was the punctuation, and a worked problem
                // almost always ends in "= ?" - which this test passes over anyway.
                val formula = when (step) {
                    is LessonStep.Concept -> step.formula
                    is LessonStep.Choice -> step.formula
                    is LessonStep.Numeric -> step.formula
                    is LessonStep.Worked -> step.problem
                }
                val visual = step.visual
                val text = formula?.text() ?: return@mapIndexedNotNull null
                if (text.contains('?')) return@mapIndexedNotNull null
                val runs = text.runColors(visual?.roles()).filter { it.first.isNotBlank() }
                val last = runs.lastOrNull { it.first.any(Char::isDigit) } ?: return@mapIndexedNotNull null
                val statesAResult = text.formatMathSymbols(fractionSlash = true).statesItsResult()
                if (statesAResult && last.second != SuccessGreen) {
                    "${lesson.id} step $index: '$text' ends '${last.first}' in ${last.second}"
                } else {
                    null
                }
            }
        }
        assertTrue(offenders.isEmpty(), "formulas printing their result as a given:\n" + offenders.joinToString("\n"))
    }

    @Test
    fun everyShapeAndSectionInTheShapeGuideIsWritten() {
        ShapeGuide.sections.forEach { section ->
            assertTrue(section.title.text().isNotBlank(), "${section.id} has no title")
            assertTrue(section.blurb.text().isNotBlank(), "${section.id} has no blurb")
            section.entries.forEach { shape ->
                assertTrue(shape.name.text().isNotBlank(), "${shape.id} has no name")
                assertTrue(shape.fact.text().isNotBlank(), "${shape.id} has no fact")
            }
        }
    }

    @Test
    fun everyRuleAndSectionInTheRulesGuideIsWritten() {
        RulesGuide.sections.forEach { section ->
            assertTrue(section.title.text().isNotBlank(), "${section.id} has no title")
            assertTrue(section.blurb.text().isNotBlank(), "${section.id} has no blurb")
            section.entries.forEach { rule ->
                assertTrue(rule.rule.text().isNotBlank(), "${rule.id} has no rule")
                assertTrue(rule.meaning.text().isNotBlank(), "${rule.id} has no meaning")
                assertTrue(rule.example?.text()?.isBlank() != true, "${rule.id} has an empty example")
            }
        }
    }

    /**
     * A negative *number* after the multiply sign has to be bracketed. `MathText` spaces out any
     * minus standing after a letter, and `x` is a letter, so "5 x -3" would come out as "5 x - 3"
     * and read as a subtraction. A lone minus - the sign table's "- x - = +" - wants that spacing.
     */
    @Test
    fun negativesAfterMultiplyAreBracketed() {
        val loose = Regex("""x\s+-[0-9a-zA-Z]""")
        RulesGuide.sections.flatMap { it.entries }.forEach { rule ->
            listOfNotNull(rule.rule.text(), rule.example?.text()).forEach { text ->
                assertTrue(
                    !loose.containsMatchIn(text),
                    "${rule.id} needs brackets round the negative in \"$text\"",
                )
            }
        }
    }

    /**
     * Every number a figure names is printed in that figure's colour by the text beside it.
     *
     * This is the general form of [fractionTextMatchesTheBarItNames], and it exists because the
     * two halves of the colour code were built separately and quietly disagreed. A formula card
     * infers roles from punctuation, so a line with no equals sign in it - "-4 is 4 left of 0",
     * "0.5 euro = 50 cents", and 35 more that are translated prose rather than notation - had every
     * number fall through to the given colour while the figure above was drawing them in three.
     * Nothing failed; the screen was just wrong.
     *
     * `LearnVisual.roles()` closed it by making the figure the source of truth, and this holds it
     * closed: a new lesson whose text contradicts its own picture fails here rather than shipping.
     *
     * A figure that returns an empty [FigureRoles] is skipped, which is what makes the map safe to
     * extend one family at a time.
     */
    @Test
    fun textTakesItsColoursFromTheFigureBesideIt() {
        val expected = mapOf(
            FigureRole.GIVEN to Primary,
            FigureRole.WORKING to WorkingBlue,
            FigureRole.ANSWER to SuccessGreen,
        )
        val offenders = mutableListOf<String>()

        fun check(where: String, visual: LearnVisual?, texts: List<String>) {
            val roles = visual?.roles() ?: return
            if (roles.isEmpty) return
            texts.forEach { text ->
                // A line that states its own result is out of scope, and deliberately so: it names
                // its answer itself, [withFormulaColors] gives it the green, and the figure is not
                // allowed to name a second one. That matters for the handful of steps that teach an
                // inverse - "0.75 - 0.4 = 0.35" is drawn by the figure for 0.35 + 0.4 - where the
                // picture and the line genuinely disagree about which number is the answer, and the
                // line is the one that wins. `noCatalogFormulaPrintsItsResultAsAGiven` holds those.
                if (text.formatMathSymbols(fractionSlash = true).statesItsResult()) return@forEach
                // Rendered exactly as the screen renders it, roles and all, so this compares the
                // real output rather than a second opinion about what it should be.
                text.runColors(roles).forEach { (run, color) ->
                    val role = roles.roleOf(run.trim()) ?: return@forEach
                    val want = expected.getValue(role)
                    if (color != want) {
                        offenders += "$where: '$text' prints '$run' in $color, " +
                            "but the figure draws it as ${role.name.lowercase()} ($want)"
                    }
                }
            }
        }

        LearnCatalog.allLessons.forEach { lesson ->
            lesson.steps.forEachIndexed { index, step ->
                val where = "${lesson.id} step $index"
                when (step) {
                    is LessonStep.Concept -> check(where, step.visual, listOfNotNull(step.formula?.text()))
                    is LessonStep.Worked -> check(where, step.visual, listOf(step.problem.text()))
                    is LessonStep.Choice -> check(where, step.visual, listOfNotNull(step.formula?.text()))
                    is LessonStep.Numeric -> check(where, step.visual, listOfNotNull(step.formula?.text()))
                }
            }
        }
        LearnCatalog.allUnits.forEach { unit ->
            unit.quiz.questions.forEach { question ->
                val prompt = question.prompt.text()
                if (question.prompt.isNotation) check("${unit.id}: '$prompt'", question.visual, listOf(prompt))
            }
        }
        assertTrue(offenders.isEmpty(), "text disagreeing with its figure:\n" + offenders.joinToString("\n"))
    }

    /**
     * A figure that works a value out marks it in the answer green, never the given orange.
     *
     * The companion to the test above, from the figure's side: it catches the family whose caption
     * states its own sum. Three of them printed the total in the accent - the ten frame's
     * "6 + 7 = 13", the counters' "= 13", the decimal grids' third square - so the picture called
     * its answer a given while the card above it had already turned the same number green.
     */
    @Test
    fun figuresMarkWhatTheyWorkOutAsTheAnswer() {
        val offenders = LearnCatalog.allLessons.flatMap { lesson ->
            lesson.steps.mapIndexedNotNull { index, step ->
                val visual = step.visual ?: return@mapIndexedNotNull null
                val roles = visual.roles()
                val clash = roles.answer.filter { it in roles.given }
                if (clash.isEmpty()) null else "${lesson.id} step $index: $visual calls $clash both"
            }
        }
        assertTrue(offenders.isEmpty(), "figures with a value in two roles:\n" + offenders.joinToString("\n"))
    }

    /** The colour each run of a formula card is printed in, in order. */
    private fun String.runColors(roles: FigureRoles? = null): List<Pair<String, Color>> {
        val colored = formatMathSymbols(fractionSlash = true)
            .withFormulaColors(structure = STRUCTURE, roles = roles)
        return colored.spanStyles.map { colored.text.substring(it.start, it.end) to it.item.color }
    }

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

    private companion object {
        /** Test prompts whose whole method is counting what the figure shows. */
        val COUNTING_IS_THE_METHOD = setOf(
            "How many dots are here?",
            "A tray holds 8 rows of 6 buns. How many buns?",
        )

        /** The flat grey `MathText` prints a formula's operators and equals signs in. */
        val STRUCTURE = Color(0xFF666666)
    }
}
