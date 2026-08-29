package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.learn_certificate_view
import braincup.composeapp.generated.resources.learn_lesson_finish
import braincup.composeapp.generated.resources.learn_quiz_correct_answer
import braincup.composeapp.generated.resources.learn_quiz_failed
import braincup.composeapp.generated.resources.learn_quiz_hide_review
import braincup.composeapp.generated.resources.learn_quiz_passed
import braincup.composeapp.generated.resources.learn_quiz_progress
import braincup.composeapp.generated.resources.learn_quiz_result_title
import braincup.composeapp.generated.resources.learn_quiz_review
import braincup.composeapp.generated.resources.learn_quiz_score
import braincup.composeapp.generated.resources.learn_quiz_your_answer
import braincup.composeapp.generated.resources.learn_test_title
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.learn.Certificate
import com.inspiredandroid.braincup.learn.LearnCatalog
import com.inspiredandroid.braincup.learn.LearnQuiz
import com.inspiredandroid.braincup.learn.LearnUnit
import com.inspiredandroid.braincup.learn.QuizQuestion
import com.inspiredandroid.braincup.learn.isNotation
import com.inspiredandroid.braincup.learn.resolve
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.CertificateMedal
import com.inspiredandroid.braincup.ui.components.PrimaryActionButton
import com.inspiredandroid.braincup.ui.components.PrismCard
import com.inspiredandroid.braincup.ui.components.ProgressDots
import com.inspiredandroid.braincup.ui.components.TextPrismButton
import com.inspiredandroid.braincup.ui.components.XpGainedChip
import com.inspiredandroid.braincup.ui.components.learn.LearnContentWidth
import com.inspiredandroid.braincup.ui.components.learn.LearnFigurePanel
import com.inspiredandroid.braincup.ui.components.learn.LearnFormulaCard
import com.inspiredandroid.braincup.ui.components.learn.LearnOptionState
import com.inspiredandroid.braincup.ui.components.learn.LearnOptionTile
import com.inspiredandroid.braincup.ui.components.learn.LearnResultColumn
import com.inspiredandroid.braincup.ui.components.learn.LearnStepColumn
import com.inspiredandroid.braincup.ui.components.learn.LearnText
import com.inspiredandroid.braincup.ui.components.learn.roles
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.ScreenPreviewHost
import com.inspiredandroid.braincup.ui.theme.LearnWrongContainer
import com.inspiredandroid.braincup.ui.theme.LearnWrongFace
import com.inspiredandroid.braincup.ui.theme.OnLearnWrongContainer
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import com.inspiredandroid.braincup.ui.theme.SuccessGreenOnContainer
import org.jetbrains.compose.resources.stringResource

@Composable
fun LearnQuizScreen(
    unit: LearnUnit,
    storage: UserStorage,
    onViewCertificate: () -> Unit,
    onPassed: () -> Unit,
    onDone: () -> Unit,
    onBack: () -> Unit,
) {
    var result by remember(unit) { mutableStateOf<UserStorage.LearnQuizResult?>(null) }

    LearnQuizScreenContent(
        unit = unit,
        result = result,
        onSubmit = { correct -> result = storage.recordLearnQuizResult(unit, correct, unit.quiz.total) },
        onViewCertificate = onViewCertificate,
        onPassed = onPassed,
        onDone = onDone,
        onBack = onBack,
    )
}

/**
 * Where a test opens.
 *
 * The app always starts a test at [Start]; nothing in the running app passes anything else. It
 * exists so a preview or a screenshot render can open the screen part-way through, which is the
 * only way to see a later question, the result, or the review list a learner has to answer their
 * way into.
 */
@Immutable
data class QuizScreenState(
    val questionIndex: Int = 0,
    /** The option picked per question, -1 for unanswered. Shorter lists are padded with -1. */
    val answers: List<Int> = emptyList(),
    /** Whether the result screen opens with its review list already unfolded. */
    val showReview: Boolean = false,
) {
    companion object {
        val Start = QuizScreenState()
    }
}

@Composable
fun LearnQuizScreenContent(
    unit: LearnUnit,
    result: UserStorage.LearnQuizResult?,
    onSubmit: (correctCount: Int) -> Unit,
    onViewCertificate: () -> Unit,
    onDone: () -> Unit,
    onBack: () -> Unit,
    /**
     * Fired once, when a finished test turns out to have earned its certificate. A test never
     * says which answer was right while it is being taken, so this is the only moment in it that
     * the device has anything to confirm.
     */
    onPassed: () -> Unit = {},
    initialState: QuizScreenState = QuizScreenState.Start,
) {
    val quiz = unit.quiz
    // One entry per question, filled in as the learner answers; -1 means not answered yet.
    val answers = remember(unit.id) {
        mutableStateListOf<Int>().apply {
            repeat(quiz.total) { add(initialState.answers.getOrElse(it) { -1 }) }
        }
    }
    var questionIndex by remember(unit.id) { mutableIntStateOf(initialState.questionIndex) }

    val submitted = questionIndex >= quiz.total
    val correctCount = quiz.questions.indices.count { answers[it] == quiz.questions[it].correctIndex }

    // The result is recorded once, when the last question is answered.
    LaunchedEffect(unit.id, submitted) {
        if (!submitted) return@LaunchedEffect
        onSubmit(correctCount)
        if (Certificate.isEarnedBy(correctCount, quiz.total)) onPassed()
    }

    AppScaffold(
        title = stringResource(Res.string.learn_test_title, stringResource(unit.topic.titleRes)),
        onBack = onBack,
        scrollable = false,
    ) {
        if (submitted) {
            QuizResultContent(
                quiz = quiz,
                answers = answers,
                correctCount = correctCount,
                result = result,
                initiallyShowingReview = initialState.showReview,
                onViewCertificate = onViewCertificate,
                onDone = onDone,
                modifier = Modifier.weight(1f),
            )
            return@AppScaffold
        }

        val question = quiz.questions[questionIndex]

        Text(
            text = stringResource(Res.string.learn_quiz_progress, questionIndex + 1, quiz.total),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(8.dp))
        ProgressDots(
            currentIndex = questionIndex,
            total = quiz.total,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        LearnStepColumn {
            question.visual?.let { visual ->
                LearnFigurePanel(visual, modifier = Modifier.padding(bottom = 12.dp))
            }
            // Notation goes in a card, exactly as a lesson step's formula does; a prose question
            // stays plain text, exactly as a lesson step's question does. The test was printing
            // both as loose text, so an equation read as a caption on the figure above it.
            //
            // Which is which comes off the catalog's own types. Deciding it from the characters
            // put "A 3-4-5 triangle is enlarged by 4" and "A 90 euro coat is 30% off" on the
            // formula card in the number face, because a hyphen and a per-cent sign look like
            // operators to a reader that has only the string.
            val prompt = question.prompt.resolve()
            if (question.prompt.isNotation) {
                // The figure is on the screen directly above, so the prompt takes its roles from
                // it. A test figure is drawn with `reveal = false`, which leaves its answer set
                // empty, so this can colour the given and the working without ever handing over
                // the number being asked for.
                LearnFormulaCard(prompt, roles = question.visual?.roles())
            } else {
                LearnText(
                    text = prompt,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(max = LearnContentWidth),
                    notation = false,
                )
            }
            Spacer(Modifier.height(20.dp))
            question.options.map { it.resolve() }.forEachIndexed { index, option ->
                // Every option stays IDLE: answers are revealed only at the end of a test, so
                // nothing here may hint at which one was right.
                LearnOptionTile(
                    label = option,
                    state = LearnOptionState.IDLE,
                    onClick = {
                        answers[questionIndex] = index
                        questionIndex++
                    },
                    notation = question.options[index].isNotation,
                )
                Spacer(Modifier.height(8.dp))
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun QuizResultContent(
    quiz: LearnQuiz,
    answers: List<Int>,
    correctCount: Int,
    result: UserStorage.LearnQuizResult?,
    initiallyShowingReview: Boolean,
    onViewCertificate: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showReview by remember { mutableStateOf(initiallyShowingReview) }
    val earnedCertificate = result?.earnedCertificate ?: Certificate.isEarnedBy(correctCount, quiz.total)

    LearnResultColumn(
        title = stringResource(Res.string.learn_quiz_result_title),
        score = stringResource(Res.string.learn_quiz_score, correctCount, quiz.total),
        scoreStyle = MaterialTheme.typography.titleMedium,
        modifier = modifier,
    ) {
        Spacer(Modifier.height(16.dp))

        if (earnedCertificate) {
            CertificateMedal(modifier = Modifier.size(56.dp))
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.learn_quiz_passed),
                style = MaterialTheme.typography.titleMedium,
                color = SuccessGreen,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        } else {
            Text(
                text = stringResource(Res.string.learn_quiz_failed),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }

        val xpGained = result?.xpAward?.xpGained ?: 0
        if (xpGained > 0) {
            Spacer(Modifier.height(16.dp))
            XpGainedChip(xpGained = xpGained)
        }

        Spacer(Modifier.height(20.dp))
        TextPrismButton(
            onClick = { showReview = !showReview },
            value = stringResource(
                if (showReview) Res.string.learn_quiz_hide_review else Res.string.learn_quiz_review,
            ),
        )

        if (showReview) {
            Spacer(Modifier.height(12.dp))
            quiz.questions.forEachIndexed { index, question ->
                ReviewCard(
                    question = question,
                    givenIndex = answers.getOrElse(index) { -1 },
                    modifier = Modifier.widthIn(max = LearnContentWidth).fillMaxWidth().padding(bottom = 8.dp),
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        if (earnedCertificate) {
            PrimaryActionButton(
                onClick = onViewCertificate,
                value = stringResource(Res.string.learn_certificate_view),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
        }
        PrimaryActionButton(
            onClick = onDone,
            value = stringResource(Res.string.learn_lesson_finish),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ReviewCard(
    question: QuizQuestion,
    givenIndex: Int,
    modifier: Modifier = Modifier,
) {
    val isCorrect = givenIndex == question.correctIndex
    // The miss is the card the learner opened the review to find, so it is the one called out.
    // This used to be the other way round: the questions they got right took the loud container
    // and the single wrong one was left in the plain surface tone, which is the quietest card on
    // a screen whose whole purpose is the wrong answer.
    val (face, ink) = if (isCorrect) {
        MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        LearnWrongContainer to OnLearnWrongContainer
    }
    PrismCard(
        face = face,
        modifier = modifier,
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            LearnText(
                text = question.prompt.resolve(),
                style = MaterialTheme.typography.titleSmall,
                color = ink,
                roleColors = true,
                notation = question.prompt.isNotation,
            )
            Spacer(Modifier.height(6.dp))
            if (!isCorrect) {
                Text(
                    text = stringResource(
                        Res.string.learn_quiz_your_answer,
                        question.options.getOrNull(givenIndex)?.resolve() ?: "-",
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    // Pinned with the card it sits on: colorScheme.error is a pale pink under
                    // Material You and this container is paler still.
                    color = LearnWrongFace,
                )
            }
            // Green whether or not they got it: green means "the answer" everywhere in the section,
            // and the answer is the answer. Printing it in the card's own ink after a miss made the
            // one line the learner most needs the quietest thing on the card.
            Text(
                text = stringResource(
                    Res.string.learn_quiz_correct_answer,
                    question.options[question.correctIndex].resolve(),
                ),
                style = MaterialTheme.typography.bodySmall,
                // Green means "the answer" everywhere in the section. On the pale miss card the
                // full-strength tone washes out, so that one takes the darkened sibling.
                color = if (isCorrect) SuccessGreen else SuccessGreenOnContainer,
            )
            Spacer(Modifier.height(4.dp))
            LearnText(
                text = question.explanation.resolve(),
                style = MaterialTheme.typography.bodySmall,
                color = ink.copy(alpha = 0.85f),
                notation = question.explanation.isNotation,
            )
        }
    }
}

@DevicePreviews
@Composable
private fun LearnQuizScreenPreview() {
    ScreenPreviewHost {
        LearnQuizScreenContent(
            unit = LearnCatalog.allUnits.first(),
            result = null,
            onSubmit = {},
            onViewCertificate = {},
            onDone = {},
            onBack = {},
        )
    }
}
