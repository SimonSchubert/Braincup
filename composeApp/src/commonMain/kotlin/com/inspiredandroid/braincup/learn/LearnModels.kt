package com.inspiredandroid.braincup.learn

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import org.jetbrains.compose.resources.PluralStringResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Content model for the Learn section.
 *
 * The catalog is organised as `topic -> sub-topic -> lessons + test`. A sub-topic is a [LearnUnit],
 * and a topic is a ladder of them ordered easiest first.
 *
 * Step diagrams are declared as [LearnVisual] values carrying the numbers they illustrate, so the
 * picture teaches and the prose stays short.
 *
 * Every word the section shows is a [StringResource]: the lessons were authored in English in
 * these files until 2026-08-28, which meant a learner who set the app to German got a German
 * frame around English lessons. The catalog now holds keys and `strings.xml` holds the text, so
 * a lesson translates like the rest of the app.
 *
 * What stays here is what is not language, as a [CatalogText.Value]: a formula, an option that is
 * a number, a worked line that is an equation. Those read the same in every language and have to
 * keep agreeing with the figure drawn beside them, so they are written in place. So are a step's
 * [LessonStep.Numeric.answer], compared against what the learner types rather than shown, and the
 * ids and slugs that progress and web addresses are recorded against.
 *
 * A sentence the catalog repeats with different numbers is one template, filled at the call site:
 * [CatalogText.Counted] against a `<plurals>` when it counts a noun ("8 litres", the shape guide's
 * "6 sides · 6 corners"), [CatalogText.Formatted] when it does not ("enlargement by 0.5"). Writing
 * such a sentence out once per number is the same sentence many times over, and no fixed string
 * can inflect its noun.
 *
 * `scripts/check_localizations.py` enforces all of this: an option carrying a number, a sentence
 * stored twice with different numbers, or one text under two keys all fail the check.
 *
 * What is left in `strings.xml` with numbers in it is prose that talks about them - "the frame
 * holds ten and {a:7} are filled, so {b:3} squares are still empty". Those did not become
 * templates: a number in a lesson sentence is either the step's arithmetic or a fact being taught
 * ("angles on a straight line add to 180"), nothing in the text tells them apart, and templating
 * the wrong one would let a later edit silently make the maths wrong. Changing such a step means
 * changing its sentence and re-translating it; `./gradlew learnNumberCoupling` says which
 * sentences a step is tied to, and the localization check fails for any locale left behind.
 *
 * Fields that are always prose - [LearnUnit.title], a lesson's summary, a [LessonStep.Concept]
 * body - hold a [StringResource] directly, because there is no choice to express.
 *
 * Keys are derived from where a thing sits in the catalog - `learn_<lesson>_s3_body` is the third
 * step of that lesson - so a step that moves takes its key with it and nothing has to be renamed.
 */

/**
 * A run of text a lesson shows.
 *
 * [Value] is notation - "-11", "3/4", "12 + {b:4} = ?" - and stays in the catalog beside the
 * figure it has to agree with. It reads the same in every language, so translating it would be
 * work with nothing to gain and one way to go wrong: an option list is picked by position and its
 * numbers are drawn on the diagram next to it, so a changed digit is a broken question.
 *
 * [Words] is language, and lives in `strings.xml` like the rest of the app.
 *
 * Fields that are always one or the other do not use this: a `body` or an `explanation` is always
 * [Words] and holds a [StringResource] directly. This exists for the four that go either way -
 * a question's options, a formula, and a worked example's problem and result - where four numbers
 * are the common case and four phrases the exception.
 */
@Immutable
sealed interface CatalogText {
    data class Value(val text: String) : CatalogText

    data class Words(val res: StringResource) : CatalogText

    /**
     * A phrase whose form follows a number the catalog already knows: "8 litres", "1 litre",
     * "6 sides · 6 corners".
     *
     * Ten polygons whose only difference is their side count are one sentence, not ten, and a
     * language that inflects the noun cannot write that sentence from a fixed string anyway.
     */
    data class Counted(val res: PluralStringResource, val count: Int) : CatalogText

    /**
     * A sentence the catalog fills values into: "enlargement by 2", "3 euro 50 cents".
     *
     * The same reason as [Counted] without a count to agree with. The values stay here beside the
     * figure; only the sentence around them is translated, and a language that puts them in a
     * different order can.
     */
    data class Formatted(val res: StringResource, val args: List<String>) : CatalogText
}

/** Notation, written where it is read: `math("3/4")`. */
fun math(text: String): CatalogText.Value = CatalogText.Value(text)

/** A translated run: `words(Res.string.learn_..._o1)`. */
fun words(res: StringResource): CatalogText.Words = CatalogText.Words(res)

/** A translated run that counts something: `counted(Res.plurals.learn_opt_litres, 8)`. */
fun counted(res: PluralStringResource, count: Int): CatalogText.Counted = CatalogText.Counted(res, count)

/** A translated sentence with values in it: `filled(Res.string.learn_opt_enlargement_by, "0.5")`. */
fun filled(res: StringResource, vararg args: String): CatalogText.Formatted = CatalogText.Formatted(res, args.toList())

/** Options that are all notation, which most are: `mathOptions("14", "15", "16", "17")`. */
fun mathOptions(vararg values: String): List<CatalogText> = values.map(::math)

/** Options that are all phrases. */
fun wordOptions(vararg res: StringResource): List<CatalogText> = res.map(::words)

/** The run as the learner reads it. */
@Composable
fun CatalogText.resolve(): String = when (this) {
    is CatalogText.Value -> text
    is CatalogText.Words -> stringResource(res)
    is CatalogText.Counted -> pluralStringResource(res, count, count)
    is CatalogText.Formatted -> stringResource(res, *args.toTypedArray())
}

/**
 * One screen inside a lesson. A lesson alternates teaching steps ([Concept], [Worked]) with steps
 * the learner has to answer ([Choice], [Numeric]) so understanding is checked as it is built.
 */
sealed interface LessonStep {
    /** Plain teaching step: an idea, optionally with the formula that captures it and a diagram. */
    data class Concept(
        val body: CatalogText,
        val formula: CatalogText? = null,
        val visual: LearnVisual? = null,
    ) : LessonStep

    /**
     * A solved example the learner walks through one line at a time before seeing [result].
     *
     * When [problem] is an equation ending in `= ?` the answer takes the place of the question
     * mark once the last line is out, the way a question step's formula resolves, so write
     * [result] bare: "38", not "25 + 13 = 38". A [problem] asked in words has no question mark to
     * land on and answers on a line of its own, where [result] can be a phrase like "15c change".
     */
    data class Worked(
        val problem: CatalogText,
        val lines: List<CatalogText>,
        val result: CatalogText,
        val visual: LearnVisual? = null,
    ) : LessonStep

    /**
     * Multiple choice check. [explanation] is shown once the learner gets there.
     *
     * [formula] is the question as the learner would write it down, e.g. "12 + 4 = ?". When a step
     * has one it leads, and [question] drops to a supporting line underneath: the sum is what is
     * being asked, and the prose only says how to read the picture.
     */
    data class Choice(
        val question: CatalogText,
        val options: List<CatalogText>,
        val correctIndex: Int,
        val explanation: CatalogText,
        val formula: CatalogText? = null,
        val visual: LearnVisual? = null,
    ) : LessonStep {
        init {
            require(correctIndex in options.indices) { "correctIndex out of bounds for $question" }
        }
    }

    /** Free numeric answer, typed on the number pad. [answer] is compared as a trimmed string. */
    data class Numeric(
        val question: CatalogText,
        /** Compared against what the learner types, so this one stays a value, not a resource. */
        val answer: String,
        val explanation: CatalogText,
        val formula: CatalogText? = null,
        val visual: LearnVisual? = null,
    ) : LessonStep
}

/**
 * A lesson as the content files declare it, before [learnUnit] stamps it with the unit it belongs
 * to. Authoring one of these instead of a [LearnLesson] keeps the unit id out of every
 * single lesson literal.
 */
data class LessonSpec(
    val id: String,
    val title: StringResource,
    val summary: StringResource,
    val steps: List<LessonStep>,
)

/** An interactive lesson: a handful of steps the learner walks through in order. */
data class LearnLesson(
    val id: String,
    val unitId: String,
    val title: StringResource,
    val summary: StringResource,
    val steps: List<LessonStep>,
) {
    /** Steps that ask the learner something, used for the "x of y correct" summary. */
    val questionCount: Int = steps.count { it is LessonStep.Choice || it is LessonStep.Numeric }
}

/** One question in a unit test. Unlike a lesson check, the answer is revealed only at the end. */
data class QuizQuestion(
    val prompt: CatalogText,
    val options: List<CatalogText>,
    val correctIndex: Int,
    val explanation: CatalogText,
    val visual: LearnVisual? = null,
) {
    init {
        require(correctIndex in options.indices) { "correctIndex out of bounds for $prompt" }
    }
}

/** The test that ends a unit and, when passed, awards the certificate. */
data class LearnQuiz(
    val unitId: String,
    val questions: List<QuizQuestion>,
) {
    val total: Int = questions.size
}

/**
 * One sub-topic of a [MathTopic]: the lessons that teach it and the test that certifies it. A
 * topic is a ladder of these, ordered easiest first, and this is the level progress and
 * certificates are recorded against, so [id] must stay stable once shipped.
 *
 * [level] is where the sub-topic sits in school. It is a hint shown to the learner ("Ages 11-14")
 * and the key the ladder is ordered by; it is not a navigation layer.
 */
data class LearnUnit(
    val id: String,
    val topic: MathTopic,
    /** Sub-topic name shown on its row, e.g. "Linear equations". */
    val title: StringResource,
    /** One line on what the sub-topic covers, e.g. "Undo the operations, one at a time". */
    val summary: StringResource,
    val level: GradeLevel,
    /** Last path segment of the sub-topic's web address, unique inside its topic. */
    val urlSlug: String,
    val lessons: List<LearnLesson>,
    val quiz: LearnQuiz,
)

/**
 * Builds a sub-topic, attaching its id to every lesson and to the test. The id is derived from the
 * topic and [urlSlug] so the two can never drift apart.
 */
fun learnUnit(
    topic: MathTopic,
    urlSlug: String,
    title: StringResource,
    summary: StringResource,
    level: GradeLevel,
    lessons: List<LessonSpec>,
    questions: List<QuizQuestion>,
): LearnUnit {
    val unitId = "${topic.id}-$urlSlug"
    return LearnUnit(
        id = unitId,
        topic = topic,
        title = title,
        summary = summary,
        level = level,
        urlSlug = urlSlug,
        lessons = lessons.map { LearnLesson(it.id, unitId, it.title, it.summary, it.steps) },
        quiz = LearnQuiz(unitId, questions),
    )
}

/**
 * The rule for certifying a unit test. A certificate is all-or-nothing: only a flawless run earns
 * one, so there is no grade to report and nothing anywhere shows a percentage. A test that falls
 * short simply gets retaken.
 */
object Certificate {
    fun isEarnedBy(correct: Int, total: Int): Boolean = total > 0 && correct == total
}
