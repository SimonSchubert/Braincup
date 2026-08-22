package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.mathlearning.MathLearningTopic
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.MathText
import com.inspiredandroid.braincup.ui.components.PrismCard
import com.inspiredandroid.braincup.ui.components.PrismTrophy
import com.inspiredandroid.braincup.ui.components.hoverHand
import com.inspiredandroid.braincup.ui.theme.ContentMaxWidth
import com.inspiredandroid.braincup.ui.theme.MedalGold
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.PrismFacet
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import org.jetbrains.compose.resources.stringResource

@Composable
fun MathLearningTestScreen(
    topic: MathLearningTopic,
    storage: UserStorage,
    onViewCertificate: (MathLearningTopic) -> Unit,
    onBack: () -> Unit,
) {
    val questions = topic.testQuestions
    var userAnswers by remember { mutableStateOf(IntArray(questions.size) { -1 }) }
    var isSubmitted by remember { mutableStateOf(false) }

    val correctCount = remember(isSubmitted, userAnswers) {
        if (!isSubmitted) 0
        else questions.indices.count { idx -> userAnswers[idx] == questions[idx].correctIndex }
    }
    val scorePercentage = remember(correctCount, questions.size) {
        if (questions.isEmpty()) 0 else (correctCount * 100) / questions.size
    }
    val passed = scorePercentage >= 66

    LaunchedEffect(isSubmitted) {
        if (isSubmitted) {
            storage.recordMathTopicResult(topic.id, scorePercentage, passed)
        }
    }

    AppScaffold(
        title = stringResource(Res.string.math_learning_test_title, stringResource(topic.titleRes)),
        onBack = onBack,
        scrollable = false,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = ContentMaxWidth)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Spacer(Modifier.height(8.dp))

            if (isSubmitted) {
                // Test Results Card
                PrismCard(
                    face = if (passed) SuccessGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                    facet = PrismFacet.Cell,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        if (passed) {
                            PrismTrophy(tint = MedalGold, modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = stringResource(Res.string.math_learning_test_passed_title),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen,
                            )
                        } else {
                            Text(
                                text = stringResource(Res.string.math_learning_test_failed_title),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = stringResource(
                                Res.string.math_learning_test_score,
                                correctCount,
                                questions.size,
                                scorePercentage,
                            ),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = if (passed) {
                                stringResource(Res.string.math_learning_test_passed_msg, stringResource(topic.titleRes))
                            } else {
                                stringResource(Res.string.math_learning_test_failed_msg)
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )

                        if (passed) {
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = { onViewCertificate(topic) },
                                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                                modifier = Modifier.hoverHand(),
                            ) {
                                Text(
                                    text = stringResource(Res.string.math_learning_view_certificate),
                                    color = Color.White,
                                )
                            }
                        }
                    }
                }
            }

            // Questions list
            questions.forEachIndexed { qIdx, question ->
                PrismCard(
                    face = MaterialTheme.colorScheme.surface,
                    facet = PrismFacet.Cell,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(Res.string.math_learning_question_progress, qIdx + 1, questions.size),
                            style = MaterialTheme.typography.labelMedium,
                            color = Primary,
                            fontWeight = FontWeight.Bold,
                        )

                        Spacer(Modifier.height(6.dp))

                        MathText(
                            text = stringResource(question.questionRes),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )

                        Spacer(Modifier.height(12.dp))

                        question.options.forEachIndexed { optIdx, optionText ->
                            val isSelected = userAnswers[qIdx] == optIdx
                            val isCorrect = question.correctIndex == optIdx

                            val buttonColor = when {
                                isSubmitted && isCorrect -> SuccessGreen
                                isSubmitted && isSelected && !isCorrect -> MaterialTheme.colorScheme.error
                                isSelected -> Primary
                                else -> MaterialTheme.colorScheme.surfaceContainer
                            }

                            Button(
                                onClick = {
                                    if (!isSubmitted) {
                                        val newAnswers = userAnswers.copyOf()
                                        newAnswers[qIdx] = optIdx
                                        userAnswers = newAnswers
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .hoverHand(),
                            ) {
                                Text(
                                    text = optionText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isSelected || (isSubmitted && isCorrect)) Color.White else MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        }
                    }
                }
            }

            if (!isSubmitted) {
                val allAnswered = userAnswers.none { it == -1 }
                Button(
                    onClick = { isSubmitted = true },
                    enabled = allAnswered,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(topic.accentColor)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .hoverHand(),
                ) {
                    Text(
                        text = stringResource(Res.string.math_learning_submit_test),
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            } else {
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
