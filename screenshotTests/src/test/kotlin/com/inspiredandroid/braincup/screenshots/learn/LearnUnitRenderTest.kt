package com.inspiredandroid.braincup.screenshots.learn

import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.learn.LearnCatalog
import com.inspiredandroid.braincup.learn.LearnLesson
import com.inspiredandroid.braincup.learn.LearnUnit
import com.inspiredandroid.braincup.learn.LearnUnitProgress
import com.inspiredandroid.braincup.learn.LessonStep
import com.inspiredandroid.braincup.ui.screens.LearnCertificateScreenContent
import com.inspiredandroid.braincup.ui.screens.LearnLessonScreenContent
import com.inspiredandroid.braincup.ui.screens.LearnQuizScreenContent
import com.inspiredandroid.braincup.ui.screens.LearnUnitScreenContent
import com.inspiredandroid.braincup.ui.screens.LessonAnswer
import com.inspiredandroid.braincup.ui.screens.LessonScreenState
import com.inspiredandroid.braincup.ui.screens.QuizScreenState
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableSet
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.setResourceReaderAndroidContext
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Every screen of one sub-topic, in the order a learner meets them.
 *
 * Parameterized over the whole catalog, so the run covers all 22 sub-topics: 66 lessons, 396
 * steps and 132 test questions, plus the states a still frame can only reach by seeding the
 * screen (see [LearnRenderHarness][LearnPhone]).
 *
 * The work is split across eight test methods rather than gathered into one, because each method
 * is a fork boundary for Gradle's `forkEvery`, and Paparazzi's native image buffers need the JVM
 * recycled every few hundred renders.
 */
@RunWith(Parameterized::class)
class LearnUnitRenderTest(
    private val unitId: String,
) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun units(): List<Array<Any>> = LearnCatalog.allUnits.map { arrayOf<Any>(it.id) }

        /** What one lesson pays out. Only the size of the chip on screen depends on it. */
        private const val LESSON_XP = 10

        /** What a flawless test pays out. */
        private const val QUIZ_XP = 25

        /** A fixed day so the certificate's date never drifts between runs. */
        private const val EARNED_EPOCH_DAY = 20_000
    }

    @get:Rule
    val paparazzi = learnPaparazzi()

    private lateinit var unit: LearnUnit

    @OptIn(ExperimentalResourceApi::class)
    @Before
    fun setup() {
        unit = LearnCatalog.unitById(unitId) ?: error("unknown unit $unitId")
        setResourceReaderAndroidContext(paparazzi.context)
    }

    // ---------------------------------------------------------------- the sub-topic screen

    @Test
    fun unitScreen() {
        paparazzi.learnSnap("00_unit_fresh") {
            LearnUnitScreenContent(
                progress = LearnUnitProgress.empty(unit),
                completedLessonIds = persistentSetOf(),
                onLessonSelected = {},
                onTakeTest = {},
                onViewCertificate = {},
                onBack = {},
            )
        }
        // Finished and certified: the lesson rows tick and the certificate card appears above them.
        paparazzi.learnSnap("01_unit_complete") {
            LearnUnitScreenContent(
                progress = LearnUnitProgress(unit, unit.lessons.size, EARNED_EPOCH_DAY),
                completedLessonIds = unit.lessons.map { it.id }.toImmutableSet(),
                onLessonSelected = {},
                onTakeTest = {},
                onViewCertificate = {},
                onBack = {},
            )
        }
    }

    // ---------------------------------------------------------------- the three lessons

    @Test
    fun lesson1() = renderLesson(0)

    @Test
    fun lesson2() = renderLesson(1)

    @Test
    fun lesson3() = renderLesson(2)

    /**
     * Every step of one lesson at rest, plus the states a learner answers their way into.
     *
     * A miss renders only on the first question of each kind in the lesson: the struck-through
     * option and the retry note are a layout to check once, while every correct answer carries a
     * different explanation and so is rendered in full. The miss is rendered at two attempts,
     * because that is where "Show me the answer" joins the retry note, and the reveal it leads to
     * gets a frame of its own.
     */
    private fun renderLesson(lessonIndex: Int) {
        val lesson = unit.lessons.getOrNull(lessonIndex) ?: return
        var choiceMissShown = false
        var numericMissShown = false

        lesson.steps.forEachIndexed { stepIndex, step ->
            val prefix = "1${lessonIndex + 1}${stepIndex + 1}_${step.kindTag()}"

            when (step) {
                is LessonStep.Concept -> snapLesson(lesson, prefix, LessonScreenState(stepIndex = stepIndex))

                is LessonStep.Worked -> {
                    // The lines are turned over one at a time; both ends of that are worth seeing.
                    snapLesson(
                        lesson,
                        "${prefix}_unrevealed",
                        LessonScreenState(stepIndex = stepIndex),
                    )
                    snapLesson(
                        lesson,
                        prefix,
                        LessonScreenState(stepIndex = stepIndex, revealedLines = step.lines.size),
                    )
                }

                is LessonStep.Choice -> {
                    snapLesson(lesson, prefix, LessonScreenState(stepIndex = stepIndex))
                    if (!choiceMissShown) {
                        choiceMissShown = true
                        val missed = step.wrongOptions(2)
                        snapLesson(
                            lesson,
                            "${prefix}_missed",
                            LessonScreenState(
                                stepIndex = stepIndex,
                                answer = LessonAnswer.Missed(missed),
                            ),
                        )
                        snapLesson(
                            lesson,
                            "${prefix}_revealed",
                            LessonScreenState(
                                stepIndex = stepIndex,
                                answer = LessonAnswer.Revealed(missed),
                            ),
                        )
                    }
                    snapLesson(
                        lesson,
                        "${prefix}_correct",
                        LessonScreenState(
                            stepIndex = stepIndex,
                            answer = LessonAnswer.Correct(firstTry = true),
                        ),
                    )
                }

                is LessonStep.Numeric -> {
                    snapLesson(lesson, prefix, LessonScreenState(stepIndex = stepIndex))
                    if (!numericMissShown) {
                        numericMissShown = true
                        val missed = step.wrongTypedTwice()
                        snapLesson(
                            lesson,
                            "${prefix}_missed",
                            LessonScreenState(
                                stepIndex = stepIndex,
                                answer = LessonAnswer.Missed(missed),
                            ),
                        )
                        snapLesson(
                            lesson,
                            "${prefix}_revealed",
                            LessonScreenState(
                                stepIndex = stepIndex,
                                answer = LessonAnswer.Revealed(missed),
                            ),
                        )
                    }
                    snapLesson(
                        lesson,
                        "${prefix}_correct",
                        LessonScreenState(
                            stepIndex = stepIndex,
                            answer = LessonAnswer.Correct(firstTry = true),
                            typedAnswer = step.answer,
                        ),
                    )
                }
            }
        }
    }

    private fun snapLesson(lesson: LearnLesson, name: String, state: LessonScreenState) {
        val siblings = unit.lessons
        val nextLessonId = siblings.getOrNull(siblings.indexOfFirst { it.id == lesson.id } + 1)?.id
        paparazzi.learnSnap(name) {
            LearnLessonScreenContent(
                lesson = lesson,
                nextLessonId = nextLessonId,
                xpGained = 0,
                onLessonCompleted = {},
                onNextLesson = {},
                onBack = {},
                onTakeTest = {},
                initialState = state,
            )
        }
    }

    // ---------------------------------------------------------------- end of a lesson

    @Test
    fun lessonComplete() {
        // Mid-unit: the result offers the next lesson.
        unit.lessons.firstOrNull()?.let { lesson ->
            paparazzi.learnSnap("21_lesson1_complete") {
                LearnLessonScreenContent(
                    lesson = lesson,
                    nextLessonId = unit.lessons.getOrNull(1)?.id,
                    xpGained = LESSON_XP,
                    onLessonCompleted = {},
                    onNextLesson = {},
                    onBack = {},
                    onTakeTest = {},
                    initialState = LessonScreenState(
                        stepIndex = lesson.steps.size,
                        correctCount = lesson.questionCount,
                    ),
                )
            }
        }
        // Last lesson: the result hands straight over to the unit test instead.
        unit.lessons.lastOrNull()?.let { lesson ->
            paparazzi.learnSnap("23_lesson3_complete_takes_test") {
                LearnLessonScreenContent(
                    lesson = lesson,
                    nextLessonId = null,
                    xpGained = LESSON_XP,
                    onLessonCompleted = {},
                    onNextLesson = {},
                    onBack = {},
                    onTakeTest = {},
                    initialState = LessonScreenState(
                        stepIndex = lesson.steps.size,
                        // One dropped, so the "x of y" line is not always a perfect score.
                        correctCount = (lesson.questionCount - 1).coerceAtLeast(0),
                    ),
                )
            }
        }
    }

    // ---------------------------------------------------------------- the unit test

    @Test
    fun test() {
        unit.quiz.questions.indices.forEach { index ->
            paparazzi.learnSnap("3${index + 1}_question") {
                LearnQuizScreenContent(
                    unit = unit,
                    result = null,
                    onSubmit = {},
                    onViewCertificate = {},
                    onDone = {},
                    onBack = {},
                    initialState = QuizScreenState(questionIndex = index),
                )
            }
        }
    }

    @Test
    fun testResult() {
        val quiz = unit.quiz
        val allCorrect = quiz.questions.map { it.correctIndex }
        // Two misses, spread across the list so the review shows both card colours interleaved.
        val missedIndices = setOf(1, quiz.total - 2).filter { it in quiz.questions.indices }.toSet()
        val withMisses = quiz.questions.mapIndexed { index, question ->
            if (index in missedIndices) {
                wrongIndexFor(question.correctIndex, question.options.size)
            } else {
                question.correctIndex
            }
        }

        paparazzi.learnSnap("40_result_pass") {
            QuizResult(allCorrect, correct = quiz.total, showReview = false)
        }
        // Tall: six review cards do not fit a phone, and every explanation has to be readable.
        paparazzi.learnSnap("41_result_pass_review", heightPx = LearnTallPx) {
            QuizResult(allCorrect, correct = quiz.total, showReview = true)
        }
        paparazzi.learnSnap("42_result_failed_review", heightPx = LearnTallPx) {
            QuizResult(withMisses, correct = quiz.total - missedIndices.size, showReview = true)
        }
    }

    @androidx.compose.runtime.Composable
    private fun QuizResult(answers: List<Int>, correct: Int, showReview: Boolean) {
        val total = unit.quiz.total
        val passed = correct == total
        LearnQuizScreenContent(
            unit = unit,
            result = UserStorage.LearnQuizResult(
                unit = unit,
                correct = correct,
                total = total,
                earnedCertificate = passed,
                xpAward = UserStorage.XpAward(if (passed) QUIZ_XP else 0, null),
            ),
            onSubmit = {},
            onViewCertificate = {},
            onDone = {},
            onBack = {},
            initialState = QuizScreenState(
                questionIndex = total,
                answers = answers,
                showReview = showReview,
            ),
        )
    }

    // ---------------------------------------------------------------- the certificate

    @Test
    fun certificate() {
        paparazzi.learnSnap("50_certificate") {
            LearnCertificateScreenContent(
                unit = unit,
                earnedEpochDay = EARNED_EPOCH_DAY,
                onDone = {},
                onBack = {},
            )
        }
    }
}
