package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.learn_certificate_view
import braincup.composeapp.generated.resources.learn_lesson_finish
import braincup.composeapp.generated.resources.learn_quiz_correct_answer
import braincup.composeapp.generated.resources.learn_quiz_failed
import braincup.composeapp.generated.resources.learn_quiz_hide_review
import braincup.composeapp.generated.resources.learn_quiz_improved
import braincup.composeapp.generated.resources.learn_quiz_passed
import braincup.composeapp.generated.resources.learn_quiz_progress
import braincup.composeapp.generated.resources.learn_quiz_result_title
import braincup.composeapp.generated.resources.learn_quiz_review
import braincup.composeapp.generated.resources.learn_quiz_score
import braincup.composeapp.generated.resources.learn_quiz_your_answer
import braincup.composeapp.generated.resources.learn_test_title
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.learn.CertificateGrade
import com.inspiredandroid.braincup.learn.LearnCatalog
import com.inspiredandroid.braincup.learn.LearnQuiz
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.learn.QuizQuestion
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.CertificateMedal
import com.inspiredandroid.braincup.ui.components.MathText
import com.inspiredandroid.braincup.ui.components.PrimaryActionButton
import com.inspiredandroid.braincup.ui.components.PrismCard
import com.inspiredandroid.braincup.ui.components.PrismTile
import com.inspiredandroid.braincup.ui.components.ProgressDots
import com.inspiredandroid.braincup.ui.components.TextPrismButton
import com.inspiredandroid.braincup.ui.components.XpGainedChip
import com.inspiredandroid.braincup.ui.components.hoverHand
import com.inspiredandroid.braincup.ui.components.labelRes
import com.inspiredandroid.braincup.ui.components.medalColor
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.ScreenPreviewHost
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import org.jetbrains.compose.resources.stringResource

@Composable
fun LearnQuizScreen(
    topic: MathTopic,
    storage: UserStorage,
    onViewCertificate: () -> Unit,
    onDone: () -> Unit,
    onBack: () -> Unit,
) {
    val quiz = remember(topic) { LearnCatalog.quiz(topic) }
    var result by remember(topic) { mutableStateOf<UserStorage.LearnQuizResult?>(null) }

    LearnQuizScreenContent(
        quiz = quiz,
        result = result,
        onSubmit = { correct -> result = storage.recordLearnQuizResult(topic, correct, quiz.total) },
        onViewCertificate = onViewCertificate,
        onDone = onDone,
        onBack = onBack,
    )
}

@Composable
fun LearnQuizScreenContent(
    quiz: LearnQuiz,
    result: UserStorage.LearnQuizResult?,
    onSubmit: (correctCount: Int) -> Unit,
    onViewCertificate: () -> Unit,
    onDone: () -> Unit,
    onBack: () -> Unit,
) {
    // One entry per question, filled in as the learner answers; -1 means not answered yet.
    val answers = remember(quiz.topic) { mutableStateListOf<Int>().apply { repeat(quiz.total) { add(-1) } } }
    var questionIndex by remember(quiz.topic) { mutableIntStateOf(0) }

    val submitted = questionIndex >= quiz.total
    val correctCount = quiz.questions.indices.count { answers[it] == quiz.questions[it].correctIndex }

    // The result is recorded once, when the last question is answered.
    LaunchedEffect(quiz.topic, submitted) {
        if (submitted) onSubmit(correctCount)
    }

    AppScaffold(
        title = stringResource(Res.string.learn_test_title, stringResource(quiz.topic.titleRes)),
        onBack = onBack,
        scrollable = false,
    ) {
        if (submitted) {
            QuizResultContent(
                quiz = quiz,
                answers = answers,
                correctCount = correctCount,
                result = result,
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

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = question.prompt,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 480.dp),
            )
            Spacer(Modifier.height(20.dp))
            question.options.forEachIndexed { index, option ->
                // No right/wrong feedback during the test — answers are revealed only at the end.
                PrismTile(
                    face = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .widthIn(max = 480.dp)
                        .fillMaxWidth()
                        .hoverHand(),
                    onClick = {
                        answers[questionIndex] = index
                        questionIndex++
                    },
                ) {
                    MathText(
                        text = option,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                    )
                }
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
    onViewCertificate: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showReview by remember { mutableStateOf(false) }
    val percent = result?.percent ?: CertificateGrade.percentOf(correctCount, quiz.total)
    val grade = result?.grade ?: CertificateGrade.forPercent(percent)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.learn_quiz_result_title),
            style = MaterialTheme.typography.headlineSmall,
            color = Primary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.learn_quiz_score, correctCount, quiz.total, percent),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))

        if (grade != null) {
            CertificateMedal(grade = grade, modifier = Modifier.size(56.dp))
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(grade.labelRes()),
                style = MaterialTheme.typography.titleMedium,
                color = grade.medalColor(),
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(
                    if (result?.isNewBest == true) Res.string.learn_quiz_improved else Res.string.learn_quiz_passed,
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = SuccessGreen,
                textAlign = TextAlign.Center,
            )
        } else {
            Text(
                text = stringResource(Res.string.learn_quiz_failed, CertificateGrade.PASS_PERCENT),
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
                    modifier = Modifier.widthIn(max = 480.dp).fillMaxWidth().padding(bottom = 8.dp),
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        if (grade != null) {
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
    PrismCard(
        face = if (isCorrect) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        modifier = modifier,
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                text = question.prompt,
                style = MaterialTheme.typography.titleSmall,
            )
            Spacer(Modifier.height(6.dp))
            if (!isCorrect) {
                Text(
                    text = stringResource(
                        Res.string.learn_quiz_your_answer,
                        question.options.getOrElse(givenIndex) { "—" },
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Text(
                text = stringResource(
                    Res.string.learn_quiz_correct_answer,
                    question.options[question.correctIndex],
                ),
                style = MaterialTheme.typography.bodySmall,
                color = if (isCorrect) SuccessGreen else Color.Unspecified,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = question.explanation,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@DevicePreviews
@Composable
private fun LearnQuizScreenPreview() {
    ScreenPreviewHost {
        LearnQuizScreenContent(
            quiz = LearnCatalog.quiz(MathTopic.ARITHMETIC),
            result = null,
            onSubmit = {},
            onViewCertificate = {},
            onDone = {},
            onBack = {},
        )
    }
}
