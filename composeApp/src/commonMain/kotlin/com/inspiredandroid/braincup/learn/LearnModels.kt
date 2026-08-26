package com.inspiredandroid.braincup.learn

/**
 * Content model for the Learn section.
 *
 * The catalog is organised as `topic -> sub-topic -> lessons + test`. A sub-topic is a [LearnUnit],
 * and a topic is a ladder of them ordered easiest first.
 *
 * Step diagrams are declared as [LearnVisual] values carrying the numbers they illustrate, so the
 * picture teaches and the prose stays short.
 *
 * Lesson and quiz bodies are authored in English right here rather than in `strings.xml`: a lesson
 * is a content catalog entry (like a Sudoku puzzle or a matchstick riddle definition), not UI
 * chrome, and a single topic carries far more prose than the resource pipeline is meant to hold.
 * Everything the section shows *around* the content — screen titles, buttons, progress labels,
 * certificate wording — does go through `strings.xml` and stays translatable.
 */

/**
 * One screen inside a lesson. A lesson alternates teaching steps ([Concept], [Worked]) with steps
 * the learner has to answer ([Choice], [Numeric]) so understanding is checked as it is built.
 */
sealed interface LessonStep {
    /** Plain teaching step: an idea, optionally with the formula that captures it and a diagram. */
    data class Concept(
        val body: String,
        val formula: String? = null,
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
        val problem: String,
        val lines: List<String>,
        val result: String,
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
        val question: String,
        val options: List<String>,
        val correctIndex: Int,
        val explanation: String,
        val formula: String? = null,
        val visual: LearnVisual? = null,
    ) : LessonStep {
        init {
            require(correctIndex in options.indices) { "correctIndex out of bounds for $question" }
        }
    }

    /** Free numeric answer, typed on the number pad. [answer] is compared as a trimmed string. */
    data class Numeric(
        val question: String,
        val answer: String,
        val explanation: String,
        val formula: String? = null,
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
    val title: String,
    val summary: String,
    val steps: List<LessonStep>,
)

/** An interactive lesson: a handful of steps the learner walks through in order. */
data class LearnLesson(
    val id: String,
    val unitId: String,
    val title: String,
    val summary: String,
    val steps: List<LessonStep>,
) {
    /** Steps that ask the learner something, used for the "x of y correct" summary. */
    val questionCount: Int = steps.count { it is LessonStep.Choice || it is LessonStep.Numeric }
}

/** One question in a unit test. Unlike a lesson check, the answer is revealed only at the end. */
data class QuizQuestion(
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
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
    val title: String,
    /** One line on what the sub-topic covers, e.g. "Undo the operations, one at a time". */
    val summary: String,
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
    title: String,
    summary: String,
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
