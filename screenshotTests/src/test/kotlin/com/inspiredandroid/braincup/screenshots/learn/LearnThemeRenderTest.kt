package com.inspiredandroid.braincup.screenshots.learn

import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.learn.LearnCatalog
import com.inspiredandroid.braincup.learn.LearnUnit
import com.inspiredandroid.braincup.learn.LearnUnitProgress
import com.inspiredandroid.braincup.learn.LessonStep
import com.inspiredandroid.braincup.ui.screens.LearnCertificateScreenContent
import com.inspiredandroid.braincup.ui.screens.LearnLessonScreenContent
import com.inspiredandroid.braincup.ui.screens.LearnQuizScreenContent
import com.inspiredandroid.braincup.ui.screens.LearnUnitScreenContent
import com.inspiredandroid.braincup.ui.screens.LessonScreenState
import com.inspiredandroid.braincup.ui.screens.QuizScreenState
import kotlinx.collections.immutable.persistentSetOf
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.setResourceReaderAndroidContext
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * The dark and OLED sample.
 *
 * Learn's figures are drawn from theme colours rather than from assets, so a contrast problem
 * shows up in the ink or the accents rather than in the layout, and one sub-topic per figure
 * family is enough to find it. `arithmetic-fractions` covers the two-accent number figures,
 * `geometry-circle-theorems` the line-art shapes, and `arithmetic-counting` gets the OLED pass
 * because its counters are the densest block of colour in the section.
 *
 * Only resting states are rendered here; the answered states are a light-theme concern about
 * wording, and they are covered in full by [LearnUnitRenderTest].
 */
@RunWith(Parameterized::class)
class LearnThemeRenderTest(
    private val label: String,
    private val unitId: String,
    private val theme: LearnTheme,
) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun cases(): List<Array<Any>> = listOf(
            arrayOf("arithmetic-fractions-dark", "arithmetic-fractions", LearnTheme.DARK),
            arrayOf("geometry-circle-theorems-dark", "geometry-circle-theorems", LearnTheme.DARK),
            arrayOf("arithmetic-counting-oled", "arithmetic-counting", LearnTheme.OLED),
        )
    }

    @get:Rule
    val paparazzi = learnPaparazzi()

    private lateinit var unit: LearnUnit

    @OptIn(ExperimentalResourceApi::class)
    @Before
    fun setup() {
        unit = LearnCatalog.unitById(unitId) ?: error("unknown unit $unitId ($label)")
        setResourceReaderAndroidContext(paparazzi.context)
    }

    @Test
    fun unitScreen() {
        paparazzi.learnSnap("00_unit_fresh", theme) {
            LearnUnitScreenContent(
                progress = LearnUnitProgress.empty(unit),
                completedLessonIds = persistentSetOf(),
                onLessonSelected = {},
                onTakeTest = {},
                onViewCertificate = {},
                onBack = {},
            )
        }
    }

    @Test
    fun lessons() {
        unit.lessons.forEachIndexed { lessonIndex, lesson ->
            lesson.steps.forEachIndexed { stepIndex, step ->
                val revealed = (step as? LessonStep.Worked)?.lines?.size ?: 0
                paparazzi.learnSnap("1${lessonIndex + 1}${stepIndex + 1}_${step.kindTag()}", theme) {
                    LearnLessonScreenContent(
                        lesson = lesson,
                        nextLessonId = null,
                        xpGained = 0,
                        onLessonCompleted = {},
                        onNextLesson = {},
                        onBack = {},
                        onTakeTest = {},
                        initialState = LessonScreenState(
                            stepIndex = stepIndex,
                            revealedLines = revealed,
                        ),
                    )
                }
            }
        }
    }

    @Test
    fun test() {
        unit.quiz.questions.indices.forEach { index ->
            paparazzi.learnSnap("3${index + 1}_question", theme) {
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
    fun resultAndCertificate() {
        val total = unit.quiz.total
        paparazzi.learnSnap("41_result_pass_review", theme, heightPx = LearnTallPx) {
            LearnQuizScreenContent(
                unit = unit,
                result = UserStorage.LearnQuizResult(
                    unit = unit,
                    correct = total,
                    total = total,
                    earnedCertificate = true,
                    xpAward = UserStorage.XpAward(25, null),
                ),
                onSubmit = {},
                onViewCertificate = {},
                onDone = {},
                onBack = {},
                initialState = QuizScreenState(
                    questionIndex = total,
                    answers = unit.quiz.questions.map { it.correctIndex },
                    showReview = true,
                ),
            )
        }
        paparazzi.learnSnap("50_certificate", theme) {
            LearnCertificateScreenContent(
                unit = unit,
                earnedEpochDay = 20_000,
                onDone = {},
                onBack = {},
            )
        }
    }
}
