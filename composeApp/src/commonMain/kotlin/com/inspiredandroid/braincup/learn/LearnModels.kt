package com.inspiredandroid.braincup.learn

/**
 * Content model for the Learn section.
 *
 * The catalog is organised as `grade band -> topic -> lessons + test`. One (band, topic) pair is a
 * [LearnUnit]: the same topic appears in several bands, teaching harder material each time.
 *
 * Lesson and quiz bodies are authored in English right here rather than in `strings.xml`: a lesson
 * is a content catalog entry (like a Sudoku puzzle or a matchstick riddle definition), not UI
 * chrome, and a single band carries far more prose than the resource pipeline is meant to hold.
 * Everything the section shows *around* the content — screen titles, buttons, progress labels,
 * certificate wording — does go through `strings.xml` and stays translatable.
 */

/** An optional diagram drawn next to a step, rendered by `LearnVisualCanvas`. */
enum class LearnVisual {
    NUMBER_LINE,
    COUNTERS,
    PLACE_VALUE_BLOCKS,
    ARRAY_GRID,
    FRACTION_BAR,
    RULER,
    CLOCK,
    COINS,
    AREA_RECTANGLE,
    SHAPES_2D,
    SOLIDS,
    SYMMETRY,
    RIGHT_TRIANGLE,
    CIRCLE,
    ANGLES,
    COORDINATE_GRID,
    BAR_CHART,
    PICTOGRAM,
    PIE_CHART,
    NORMAL_CURVE,
    BALANCE_SCALE,
    PARABOLA,
    EXPONENTIAL_CURVE,
    UNIT_CIRCLE,
    SINE_WAVE,
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

/**
 * A lesson as the content files declare it, before [learnUnit] stamps it with the unit it belongs
 * to. Authoring one of these instead of a [LearnLesson] keeps the band and topic out of every
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
 * One topic at one grade band: the lessons that teach it and the test that certifies it. This is
 * what progress and certificates are recorded against, so [id] must stay stable once shipped.
 */
data class LearnUnit(
    val level: GradeLevel,
    val topic: MathTopic,
    /** What this band adds to the topic, e.g. "Counting on, first sums, tens and ones". */
    val summary: String,
    val lessons: List<LearnLesson>,
    val quiz: LearnQuiz,
) {
    val id: String = idFor(level, topic)

    companion object {
        fun idFor(level: GradeLevel, topic: MathTopic): String = "${level.id}-${topic.id}"
    }
}

/** Builds a unit, attaching its id to every lesson and to the test. */
fun learnUnit(
    level: GradeLevel,
    topic: MathTopic,
    summary: String,
    lessons: List<LessonSpec>,
    questions: List<QuizQuestion>,
): LearnUnit {
    val unitId = LearnUnit.idFor(level, topic)
    return LearnUnit(
        level = level,
        topic = topic,
        summary = summary,
        lessons = lessons.map { LearnLesson(it.id, unitId, it.title, it.summary, it.steps) },
        quiz = LearnQuiz(unitId, questions),
    )
}

/**
 * Certificate tier awarded for a unit test, by percentage of correct answers. Anything below
 * [PASS_PERCENT] earns no certificate and the test can simply be retaken.
 */
enum class CertificateTier(val minPercent: Int) {
    BRONZE(60),
    SILVER(75),
    GOLD(90),
    ;

    companion object {
        const val PASS_PERCENT = 60

        /** The highest tier reached by [percent], or null when the test was not passed. */
        fun forPercent(percent: Int): CertificateTier? = entries.lastOrNull { percent >= it.minPercent }

        fun percentOf(correct: Int, total: Int): Int = if (total <= 0) 0 else (correct * 100) / total
    }
}
