package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.background
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
import com.inspiredandroid.braincup.mathlearning.MathLearningTopic
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.MathText
import com.inspiredandroid.braincup.ui.components.PrismCard
import com.inspiredandroid.braincup.ui.components.hoverHand
import com.inspiredandroid.braincup.ui.theme.ContentMaxWidth
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.PrismFacet
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import org.jetbrains.compose.resources.stringResource

@Composable
fun MathLearningLessonScreen(
    topic: MathLearningTopic,
    onFinishLessonTakeTest: () -> Unit,
    onBack: () -> Unit,
) {
    var stepIndex by remember { mutableStateOf(0) }
    val currentStep = topic.lessons.getOrNull(stepIndex) ?: topic.lessons.first()

    // Practice question state for current step
    var selectedOption by remember(stepIndex) { mutableStateOf<Int?>(null) }
    var isChecked by remember(stepIndex) { mutableStateOf(false) }

    AppScaffold(
        title = stringResource(Res.string.math_learning_lesson_title, stringResource(topic.titleRes)),
        onBack = onBack,
        scrollable = false,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Progress indicators
            Row(
                modifier = Modifier.widthIn(max = ContentMaxWidth).fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                topic.lessons.forEachIndexed { idx, _ ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .background(
                                color = if (idx <= stepIndex) Color(topic.accentColor) else MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.small,
                            ),
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .widthIn(max = ContentMaxWidth)
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {
                // Lesson Step Title & Explanation
                Text(
                    text = stringResource(currentStep.titleRes),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = stringResource(currentStep.explanationRes),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                // Formula block if present
                currentStep.formula?.let { formulaText ->
                    Spacer(Modifier.height(16.dp))
                    PrismCard(
                        face = Color(topic.accentColor).copy(alpha = 0.15f),
                        facet = PrismFacet.Cell,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            MathText(
                                text = formulaText,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }

                // Practice Question
                currentStep.practiceQuestion?.let { practice ->
                    Spacer(Modifier.height(24.dp))

                    Text(
                        text = stringResource(Res.string.math_learning_practice_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                    )

                    Spacer(Modifier.height(8.dp))

                    PrismCard(
                        face = MaterialTheme.colorScheme.surface,
                        facet = PrismFacet.Cell,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            MathText(
                                text = stringResource(practice.questionRes),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                            )

                            Spacer(Modifier.height(12.dp))

                            practice.options.forEachIndexed { optionIdx, optionText ->
                                val isSelected = selectedOption == optionIdx
                                val isCorrect = optionIdx == practice.correctIndex

                                val buttonColor = when {
                                    isChecked && isCorrect -> SuccessGreen
                                    isChecked && isSelected && !isCorrect -> MaterialTheme.colorScheme.error
                                    isSelected -> Primary
                                    else -> MaterialTheme.colorScheme.surfaceContainer
                                }

                                Button(
                                    onClick = {
                                        if (!isChecked) {
                                            selectedOption = optionIdx
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
                                        color = if (isSelected || (isChecked && isCorrect)) Color.White else MaterialTheme.colorScheme.onSurface,
                                    )
                                }
                            }

                            if (!isChecked && selectedOption != null) {
                                Spacer(Modifier.height(12.dp))
                                Button(
                                    onClick = { isChecked = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                                    modifier = Modifier.fillMaxWidth().hoverHand(),
                                ) {
                                    Text(
                                        text = stringResource(Res.string.math_learning_check_answer),
                                        color = Color.White,
                                    )
                                }
                            }

                            if (isChecked) {
                                Spacer(Modifier.height(12.dp))
                                val isRight = selectedOption == practice.correctIndex
                                Text(
                                    text = if (isRight) {
                                        stringResource(Res.string.math_learning_correct)
                                    } else {
                                        stringResource(Res.string.math_learning_incorrect)
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (isRight) SuccessGreen else MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold,
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = stringResource(practice.explanationRes),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
            }

            // Navigation Footer
            Row(
                modifier = Modifier
                    .widthIn(max = ContentMaxWidth)
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                if (stepIndex < topic.lessons.size - 1) {
                    Button(
                        onClick = { stepIndex++ },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        modifier = Modifier.fillMaxWidth().hoverHand(),
                    ) {
                        Text(
                            text = stringResource(Res.string.math_learning_next_step),
                            color = Color.White,
                        )
                    }
                } else {
                    Button(
                        onClick = onFinishLessonTakeTest,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(topic.accentColor)),
                        modifier = Modifier.fillMaxWidth().hoverHand(),
                    ) {
                        Text(
                            text = stringResource(Res.string.math_learning_finish_lesson),
                            color = Color.White,
                        )
                    }
                }
            }
        }
    }
}
