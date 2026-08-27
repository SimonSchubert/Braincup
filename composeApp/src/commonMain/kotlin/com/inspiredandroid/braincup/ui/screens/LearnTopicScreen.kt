package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.learn_topic_certificates
import braincup.composeapp.generated.resources.learn_topic_intro
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.learn.LearnCatalog
import com.inspiredandroid.braincup.learn.LearnUnit
import com.inspiredandroid.braincup.learn.LearnUnitProgress
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.LearnGuideButton
import com.inspiredandroid.braincup.ui.components.LearnSubTopicRow
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.ScreenPreviewHost
import com.inspiredandroid.braincup.ui.theme.ContentMaxWidth
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource

/** One topic's ladder of sub-topics, easiest first. Each row opens that sub-topic's lessons. */
@Composable
fun LearnTopicScreen(
    topic: MathTopic,
    storage: UserStorage,
    onUnitSelected: (LearnUnit) -> Unit,
    onGuide: () -> Unit,
    onBack: () -> Unit,
) {
    val progress = remember(storage, topic) { storage.getLearnUnitProgress(topic).toImmutableList() }
    LearnTopicScreenContent(
        topic = topic,
        progress = progress,
        onUnitSelected = onUnitSelected,
        onGuide = onGuide,
        onBack = onBack,
    )
}

@Composable
fun LearnTopicScreenContent(
    topic: MathTopic,
    progress: ImmutableList<LearnUnitProgress>,
    onUnitSelected: (LearnUnit) -> Unit,
    onGuide: () -> Unit,
    onBack: () -> Unit,
) {
    val certificateCount = remember(progress) { progress.count { it.hasCertificate } }

    AppScaffold(
        title = stringResource(topic.titleRes),
        onBack = onBack,
        scrollable = false,
        // The topic's reference guide belongs in the bar rather than among the rows: it is not a
        // rung - nothing is taught by it and no certificate comes out of it.
        actions = { LearnGuideButton(topic = topic, onClick = onGuide) },
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .widthIn(max = ContentMaxWidth),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item(contentType = "topic_intro") {
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
                    Text(
                        text = stringResource(topic.subtitleRes),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = stringResource(Res.string.learn_topic_intro),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = stringResource(
                            Res.string.learn_topic_certificates,
                            certificateCount,
                            progress.size,
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            itemsIndexed(
                items = progress,
                key = { _, item -> item.unit.id },
                contentType = { _, _ -> "learn_subtopic" },
            ) { index, unitProgress ->
                LearnSubTopicRow(
                    unit = unitProgress.unit,
                    position = index + 1,
                    ladderSize = progress.size,
                    hasCertificate = unitProgress.hasCertificate,
                    onClick = onUnitSelected,
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun LearnTopicScreenPreview() {
    ScreenPreviewHost {
        LearnTopicScreenContent(
            topic = MathTopic.GEOMETRY,
            progress = LearnCatalog.units(MathTopic.GEOMETRY)
                .map { LearnUnitProgress.empty(it) }
                .toImmutableList(),
            onUnitSelected = {},
            onGuide = {},
            onBack = {},
        )
    }
}
