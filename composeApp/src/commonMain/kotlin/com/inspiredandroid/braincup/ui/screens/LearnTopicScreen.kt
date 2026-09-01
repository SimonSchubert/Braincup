package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource

/**
 * One rung to a row on a phone, several to a line on a tablet or a desktop window. Wide enough for
 * the longest sub-topic title to sit beside its difficulty chip without wrapping to a third line.
 */
private val SubTopicCellMinWidth = 300.dp

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
    // The age bands this topic actually covers, in ladder order, so the row meter can say which of
    // them a rung sits at rather than how far down the list it happens to be.
    val bands = remember(progress) { progress.map { it.unit.level }.distinct() }

    // The ladder runs under the gesture/navigation bar, as on the home screen, and holds the last
    // rung clear of it with content padding.
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    AppScaffold(
        title = stringResource(topic.titleRes),
        onBack = onBack,
        scrollable = false,
        // The topic's reference guide belongs in the bar rather than among the rows: it is not a
        // rung - nothing is taught by it and no certificate comes out of it.
        actions = { LearnGuideButton(topic = topic, onClick = onGuide) },
        drawUnderNavigationBar = true,
    ) {
        LazyVerticalGrid(
            // A ladder still reads in order when it wraps, and a wide window fits three rungs to a
            // line instead of stretching one across the whole screen.
            columns = GridCells.Adaptive(minSize = SubTopicCellMinWidth),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = 8.dp + bottomInset,
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }, contentType = "topic_intro") {
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
            ) { _, unitProgress ->
                LearnSubTopicRow(
                    unit = unitProgress.unit,
                    band = bands.indexOf(unitProgress.unit.level) + 1,
                    bands = bands.size,
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
