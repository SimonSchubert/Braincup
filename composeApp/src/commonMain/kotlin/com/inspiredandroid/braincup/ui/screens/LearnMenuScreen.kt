package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.learn_section_certificates
import braincup.composeapp.generated.resources.learn_section_subtitle
import braincup.composeapp.generated.resources.learn_title
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.learn.LearnTopicProgress
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.LearnTopicTile
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.ScreenPreviewHost
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource

/** The Learn section root: every topic, each opening onto its ladder of sub-topics. */
@Composable
fun LearnMenuScreen(
    storage: UserStorage,
    onTopicSelected: (MathTopic) -> Unit,
    onBack: () -> Unit,
) {
    val progress = remember(storage) { storage.getAllLearnTopicProgress().toImmutableList() }
    LearnMenuScreenContent(
        progress = progress,
        onTopicSelected = onTopicSelected,
        onBack = onBack,
    )
}

@Composable
fun LearnMenuScreenContent(
    progress: ImmutableList<LearnTopicProgress>,
    onTopicSelected: (MathTopic) -> Unit,
    onBack: () -> Unit,
) {
    val certificateCount = remember(progress) { progress.sumOf { it.certificates } }
    val certificateTotal = remember(progress) { progress.sumOf { it.unitsTotal } }

    // The grid runs under the gesture/navigation bar, as on the home screen, and holds the last
    // row clear of it with content padding.
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    AppScaffold(
        title = stringResource(Res.string.learn_title),
        onBack = onBack,
        scrollable = false,
        drawUnderNavigationBar = true,
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
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
            item(span = { GridItemSpan(maxLineSpan) }, contentType = "learn_intro") {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(Res.string.learn_section_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = stringResource(
                            Res.string.learn_section_certificates,
                            certificateCount,
                            certificateTotal,
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            items(progress, key = { it.topic.id }, contentType = { "learn_topic" }) { topicProgress ->
                LearnTopicTile(
                    topic = topicProgress.topic,
                    certificates = topicProgress.certificates,
                    unitsTotal = topicProgress.unitsTotal,
                    onClick = onTopicSelected,
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun LearnMenuScreenPreview() {
    ScreenPreviewHost {
        LearnMenuScreenContent(
            progress = MathTopic.entries.map { LearnTopicProgress.empty(it) }.toImmutableList(),
            onTopicSelected = {},
            onBack = {},
        )
    }
}
