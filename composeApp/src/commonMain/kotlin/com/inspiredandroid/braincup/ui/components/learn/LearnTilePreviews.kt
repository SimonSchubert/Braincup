package com.inspiredandroid.braincup.ui.components.learn

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.ui.components.ColorPrismCell
import com.inspiredandroid.braincup.ui.components.MathText
import com.inspiredandroid.braincup.ui.components.OperatorIcons
import com.inspiredandroid.braincup.ui.components.PrismCard
import com.inspiredandroid.braincup.ui.components.PrismPolygon
import com.inspiredandroid.braincup.ui.theme.LightColorScheme
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.PrismFacet
import com.inspiredandroid.braincup.ui.theme.PrismSlot
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

/*
 * The still sketches that stand in for a grade band or a topic on its menu tile.
 *
 * The lesson diagrams in LearnVisualCanvas animate and caption themselves, because there the
 * movement is the explanation. A menu tile is a different job: it sits in the same grid as the
 * mini-game tiles, so these are built from the same prism parts, hold three or four shapes at
 * most, carry no explanatory text, and never move.
 */

/** Tile previews always render on the band's pastel accent, so colours are set explicitly. */
private val PreviewInk = LightColorScheme.onSurface

/** Neutral grey for the structural parts of a sketch: beams, fulcrums, axes. */
private val PreviewStructure = Color(0xFF6B7280)

private val TilePreviewPadding = 24.dp

@Composable
internal fun TopicTilePreview(topic: MathTopic) {
    when (topic) {
        MathTopic.ARITHMETIC -> OperatorGridPreview()
        MathTopic.GEOMETRY -> PentagonPreview()
    }
}

private val RungBarHeights = listOf(0.4f, 0.7f, 1f)

/**
 * The badge on a sub-topic row: how far up the ladder this rung sits, as a three-bar meter.
 *
 * A figure rather than a numeral, so the row reads at a glance and the badge belongs to the same
 * family as the topic sketches. Three bars whatever the ladder's length: the row order already
 * carries the exact position, and a bar per rung turns into hairlines on the longer ladders.
 */
@Composable
internal fun SubTopicRowPreview(position: Int, ladderSize: Int, modifier: Modifier = Modifier) {
    val lit = rungBarsLit(position, ladderSize)
    Row(
        modifier = modifier.padding(12.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        RungBarHeights.forEachIndexed { index, height ->
            ColorPrismCell(
                face = if (index < lit) Primary else PreviewStructure.copy(alpha = 0.3f),
                modifier = Modifier.weight(1f).fillMaxHeight(height),
            )
        }
    }
}

/** The rung's band of the climb, rounded up so the last rung of any ladder fills the meter. */
private fun rungBarsLit(position: Int, ladderSize: Int): Int {
    val rungs = maxOf(ladderSize, position, 1)
    return ceil(position * RungBarHeights.size / rungs.toDouble()).toInt().coerceIn(1, RungBarHeights.size)
}

/** The square drawing area every sketch gets, inset the same way the mini-game previews are. */
@Composable
private fun PreviewBox(
    padding: Dp = TilePreviewPadding,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f)
            .padding(padding),
        contentAlignment = Alignment.Center,
        content = content,
    )
}

// --- Topics --------------------------------------------------------------------------------

private val ArithmeticOperators = listOf("+", "-", "×", "÷")

@Composable
private fun OperatorGridPreview() {
    PreviewBox(padding = 22.dp) {
        Column(modifier = Modifier.fillMaxWidth()) {
            ArithmeticOperators.chunked(2).forEach { row ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    row.forEach { operator ->
                        PrismCard(
                            face = Primary,
                            facet = PrismFacet.Cell,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp),
                        ) {
                            Icon(
                                imageVector = OperatorIcons.getValue(operator),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.fillMaxSize().padding(4.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

private const val RulerTicks = 5

private val PentagonPoints = regularPolygonPoints(sides = 5)

@Composable
private fun PentagonPreview() {
    PreviewBox {
        PrismPolygon(
            points = PentagonPoints,
            face = Primary,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

private val BarChartHeights = listOf(0.45f, 0.75f, 0.3f, 1f)

/** The unknown, in the same slot shape the number pad drops an answer into. */
/** A curve with the tangent that measures its slope: the derivative, in one picture. */
// --- Drawing helpers -----------------------------------------------------------------------

private const val ArchSamples = 24

private const val UnitCircleDegrees = 52f

/** The corners of a regular polygon with [sides] sides, point up, normalized to the unit square. */
private fun regularPolygonPoints(sides: Int): ImmutableList<Pair<Float, Float>> {
    val step = 2f * PI.toFloat() / sides
    val start = -PI.toFloat() / 2f
    return List(sides) { index ->
        val angle = start + step * index
        (0.5f + cos(angle) * 0.5f) to (0.5f + sin(angle) * 0.5f)
    }.toImmutableList()
}

/**
 * A point of the plot, from x in -1..1 and y in 0..1, inset by [inset] so a thick stroke drawn
 * through it still lands inside the canvas.
 */
private fun DrawScope.plotPoint(x: Float, y: Float, inset: Float): Offset {
    val floor = size.height - inset
    return Offset(
        x = inset + (x + 1f) / 2f * (size.width - inset * 2f),
        y = floor - y * (floor - inset),
    )
}

/** The arch y = 1 - x², as an open polyline or as a closed dome sitting on the axis. */
private fun DrawScope.archPath(inset: Float, close: Boolean): Path {
    val points = List(ArchSamples + 1) { index ->
        val x = -1f + 2f * index / ArchSamples
        plotPoint(x, 1f - x * x, inset)
    }
    return Path().apply {
        moveTo(points.first().x, points.first().y)
        points.drop(1).forEach { lineTo(it.x, it.y) }
        if (close) close()
    }
}

private fun DrawScope.drawPlotAxes(stroke: Float) {
    val axis = PreviewStructure.copy(alpha = 0.35f)
    val width = stroke * 0.5f
    drawLine(
        color = axis,
        start = Offset(0f, size.height - width / 2f),
        end = Offset(size.width, size.height - width / 2f),
        strokeWidth = width,
    )
    drawLine(
        color = axis,
        start = Offset(width / 2f, 0f),
        end = Offset(width / 2f, size.height),
        strokeWidth = width,
    )
}
