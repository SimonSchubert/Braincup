package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.learn_shape_guide_intro
import braincup.composeapp.generated.resources.learn_shape_guide_title
import com.inspiredandroid.braincup.learn.ShapeGuide
import com.inspiredandroid.braincup.learn.resolve
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.GuideSectionHeader
import com.inspiredandroid.braincup.ui.components.PrismCard
import com.inspiredandroid.braincup.ui.components.learn.LearnVisualCanvas
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.ScreenPreviewHost
import org.jetbrains.compose.resources.stringResource

/**
 * Two shapes to a row on the narrowest phone, and no more than that at any width, because the
 * cells hold a drawn figure rather than an icon.
 */
private val ShapeCellMinWidth = 150.dp

/**
 * Every cell the same height, so the grid reads as a chart rather than as a ragged list. The
 * figure takes whatever the name and the fact leave it, which is what keeps a three-line fact from
 * pushing its row taller than its neighbours.
 */
private val ShapeCellHeight = 208.dp

/**
 * The shape guide: every named shape in the curriculum, drawn, in one scroll.
 *
 * A reference rather than a lesson. The sub-topics teach these a few at a time and in teaching
 * order, which is no help to a learner who just wants the name of the five-sided one back. Every
 * figure here is the same animated one the lessons use, so tapping a shape draws it again.
 */
@Composable
fun LearnShapeGuideScreen(onBack: () -> Unit) {
    AppScaffold(
        title = stringResource(Res.string.learn_shape_guide_title),
        onBack = onBack,
        scrollable = false,
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = ShapeCellMinWidth),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(key = "intro", span = { GridItemSpan(maxLineSpan) }, contentType = "intro") {
                Text(
                    text = stringResource(Res.string.learn_shape_guide_intro, ShapeGuide.shapeCount),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            ShapeGuide.sections.forEach { section ->
                item(key = section.id, span = { GridItemSpan(maxLineSpan) }, contentType = "section") {
                    GuideSectionHeader(section)
                }
                items(
                    items = section.entries,
                    key = { it.id },
                    contentType = { "shape" },
                ) { shape ->
                    ShapeCell(shape)
                }
            }
        }
    }
}

@Composable
private fun ShapeCell(shape: ShapeGuide.Entry) {
    PrismCard(
        face = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth().height(ShapeCellHeight),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LearnVisualCanvas(
                visual = shape.visual,
                modifier = Modifier.fillMaxWidth().weight(1f),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(shape.name),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = shape.fact.resolve(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@DevicePreviews
@Composable
private fun LearnShapeGuideScreenPreview() {
    ScreenPreviewHost {
        LearnShapeGuideScreen(onBack = {})
    }
}
