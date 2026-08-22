package com.inspiredandroid.braincup.learn

/**
 * Content model for the Learn section.
 *
 * Lesson and quiz bodies are authored in English right here rather than in `strings.xml`: a lesson
 * is a content catalog entry (like a Sudoku puzzle or a matchstick riddle definition), not UI
 * chrome, and a single topic carries far more prose than the resource pipeline is meant to hold.
 * Everything the section shows *around* the content — screen titles, buttons, progress labels,
 * certificate wording — does go through `strings.xml` and stays translatable.
 */

/** An optional diagram drawn next to a step, rendered by `LearnVisualCanvas`. */
enum class LearnVisual {
    NUMBER_LINE,
    FRACTION_BAR,
    RULER,
    AREA_RECTANGLE,
    RIGHT_TRIANGLE,
    CIRCLE,
    ANGLES,
    BAR_CHART,
    BALANCE_SCALE,
    UNIT_CIRCLE,
    PARABOLA,
    TANGENT_LINE,
    AREA_UNDER_CURVE,
}

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

    /** A solved example the learner walks through one line at a time before seeing [result]. */
    data class Worked(
        val problem: String,
        val lines: List<String>,
        val result: String,
        val visual: LearnVisual? = null,
    ) : LessonStep

    /** Multiple choice check. [explanation] is shown after answering, right or wrong. */
    data class Choice(
        val question: String,
        val options: List<String>,
        val correctIndex: Int,
        val explanation: String,
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
        val visual: LearnVisual? = null,
    ) : LessonStep
}

/** An interactive lesson: a handful of steps the learner walks through in order. */
data class LearnLesson(
    val id: String,
    val topic: MathTopic,
    val title: String,
    val summary: String,
    val steps: List<LessonStep>,
) {
    /** Steps that ask the learner something, used for the "x of y correct" summary. */
    val questionCount: Int = steps.count { it is LessonStep.Choice || it is LessonStep.Numeric }
}

/** One question in a topic test. Unlike a lesson check, the answer is revealed only at the end. */
data class QuizQuestion(
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
) {
    init {
        require(correctIndex in options.indices) { "correctIndex out of bounds for $prompt" }
    }
}

/** The test that ends a topic and, when passed, awards the certificate. */
data class LearnQuiz(
    val topic: MathTopic,
    val questions: List<QuizQuestion>,
) {
    val total: Int = questions.size
}

/**
 * Certificate tier awarded for a topic test, by percentage of correct answers. Anything below
 * [PASS_PERCENT] earns no certificate and the test can simply be retaken.
 */
enum class CertificateGrade(val minPercent: Int) {
    BRONZE(60),
    SILVER(75),
    GOLD(90),
    ;

    companion object {
        const val PASS_PERCENT = 60

        /** The highest tier reached by [percent], or null when the test was not passed. */
        fun forPercent(percent: Int): CertificateGrade? = entries.lastOrNull { percent >= it.minPercent }

        fun percentOf(correct: Int, total: Int): Int = if (total <= 0) 0 else (correct * 100) / total
    }
}
