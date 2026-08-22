package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.mathlearning.MathLearningTopic
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.PrismCard
import com.inspiredandroid.braincup.ui.components.PrismTrophy
import com.inspiredandroid.braincup.ui.components.hoverHand
import com.inspiredandroid.braincup.ui.theme.ContentMaxWidth
import com.inspiredandroid.braincup.ui.theme.MedalGold
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.PrismFacet
import org.jetbrains.compose.resources.stringResource

@Composable
fun MathLearningMenuScreen(
    storage: UserStorage,
    onSelectTopicLesson: (MathLearningTopic) -> Unit,
    onSelectTopicTest: (MathLearningTopic) -> Unit,
    onViewCertificate: (MathLearningTopic) -> Unit,
    onBack: () -> Unit,
) {
    val topics = remember { MathLearningTopic.entries }

    AppScaffold(
        title = stringResource(Res.string.math_learning_title),
        onBack = onBack,
        scrollable = false,
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = ContentMaxWidth)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
        ) {
            items(topics, key = { it.id }) { topic ->
                val passed = remember(storage, topic.id) { storage.isMathTopicPassed(topic.id) }
                val score = remember(storage, topic.id) { storage.getMathTopicScore(topic.id) }

                MathTopicCard(
                    topic = topic,
                    passed = passed,
                    scorePercentage = score,
                    onStartLesson = { onSelectTopicLesson(topic) },
                    onTakeTest = { onSelectTopicTest(topic) },
                    onViewCertificate = { onViewCertificate(topic) },
                )
            }
        }
    }
}

@Composable
private fun MathTopicCard(
    topic: MathLearningTopic,
    passed: Boolean,
    scorePercentage: Int?,
    onStartLesson: () -> Unit,
    onTakeTest: () -> Unit,
    onViewCertificate: () -> Unit,
) {
    PrismCard(
        face = MaterialTheme.colorScheme.surface,
        facet = PrismFacet.Cell,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f),
                ) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .background(Color(topic.accentColor), shape = MaterialTheme.shapes.extraSmall),
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = stringResource(topic.titleRes),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }

                if (passed && scorePercentage != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PrismTrophy(tint = MedalGold, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = stringResource(Res.string.math_learning_passed, scorePercentage),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = stringResource(topic.descriptionRes),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = onStartLesson,
                    modifier = Modifier.weight(1f).hoverHand(),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                ) {
                    Text(
                        text = stringResource(Res.string.math_learning_start_lesson),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                    )
                }

                Button(
                    onClick = onTakeTest,
                    modifier = Modifier.weight(1f).hoverHand(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(topic.accentColor)),
                ) {
                    Text(
                        text = stringResource(Res.string.math_learning_take_test),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                    )
                }

                if (passed) {
                    OutlinedButton(
                        onClick = onViewCertificate,
                        modifier = Modifier.hoverHand(),
                    ) {
                        PrismTrophy(tint = MedalGold, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}
