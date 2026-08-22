package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.mathlearning.MathLearningTopic
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.PrismCard
import com.inspiredandroid.braincup.ui.components.PrismTrophy
import com.inspiredandroid.braincup.ui.theme.ContentMaxWidth
import com.inspiredandroid.braincup.ui.theme.MedalGold
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.PrismFacet
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun MathLearningCertificateScreen(
    topic: MathLearningTopic,
    storage: UserStorage,
    onBack: () -> Unit,
) {
    val score = remember(storage, topic.id) { storage.getMathTopicScore(topic.id) ?: 100 }
    val timestamp = remember(storage, topic.id) { storage.getMathTopicTimestamp(topic.id) }
    val dateStr = remember(timestamp) {
        if (timestamp != null) {
            val dt = Instant.fromEpochMilliseconds(timestamp).toLocalDateTime(TimeZone.UTC)
            "${dt.year}-${dt.month.number.toString().padStart(2, '0')}-${dt.day.toString().padStart(2, '0')}"
        } else ""
    }

    val activeAccount = remember(storage) {
        val snapshot = storage.accounts.snapshot.value
        snapshot.accounts.firstOrNull { it.id == snapshot.activeId }
    }
    val recipientName = activeAccount?.name ?: stringResource(Res.string.certificate_recipient_default)

    AppScaffold(
        title = stringResource(Res.string.certificate_title),
        onBack = onBack,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = ContentMaxWidth)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Certificate Card Frame
            PrismCard(
                face = MaterialTheme.colorScheme.surface,
                facet = PrismFacet.Cell,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .border(
                            border = BorderStroke(2.dp, Color(topic.accentColor)),
                            shape = MaterialTheme.shapes.medium,
                        )
                        .background(Color(topic.accentColor).copy(alpha = 0.05f))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.ic_mascot),
                            contentDescription = null,
                            modifier = Modifier.height(90.dp),
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = stringResource(Res.string.certificate_header),
                            style = MaterialTheme.typography.labelLarge,
                            color = Primary,
                            fontWeight = FontWeight.Bold,
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = stringResource(Res.string.certificate_title),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                        )

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = stringResource(Res.string.certificate_presented_to),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = recipientName,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Primary,
                            textAlign = TextAlign.Center,
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = stringResource(Res.string.certificate_body),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface,
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            text = stringResource(topic.titleRes),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(topic.accentColor),
                            textAlign = TextAlign.Center,
                        )

                        Spacer(Modifier.height(20.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column {
                                Text(
                                    text = stringResource(Res.string.certificate_score_label, score),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                if (dateStr.isNotEmpty()) {
                                    Text(
                                        text = dateStr,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }

                            PrismTrophy(
                                tint = MedalGold,
                                modifier = Modifier.size(44.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
