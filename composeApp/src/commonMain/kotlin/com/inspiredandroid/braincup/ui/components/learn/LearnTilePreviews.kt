package com.inspiredandroid.braincup.ui.components.learn

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.ui.components.ColorPrismCell
import com.inspiredandroid.braincup.ui.components.OperatorIcons
import com.inspiredandroid.braincup.ui.components.PrismCard
import com.inspiredandroid.braincup.ui.components.PrismPolygon
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.PrismFacet
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

private val TrianglePoints = regularPolygonPoints(sides = 3)
private val SquarePoints = regularPolygonPoints(sides = 4)
private val PentagonPoints = regularPolygonPoints(sides = 5)

/** The shapes the guide button holds, in the order they read best from left to right. */
private val BadgeShapes = listOf(TrianglePoints, SquarePoints, PentagonPoints)

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

/**
 * The shapes inside the shape guide button: a triangle, a square and a pentagon.
 *
 * Three rather than one, because the button opens a chart of them all and a single shape would
 * read as a lesson about that shape. They are drawn from the same prism parts as every other
 * sketch in the section, at button size.
 */
@Composable
internal fun ShapeGuideGlyphs(
    modifier: Modifier = Modifier,
    face: Color = Color.White,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BadgeShapes.forEach { shape ->
            PrismPolygon(
                points = shape,
                face = face,
                // Upright rather than on a point: a diamond beside a triangle reads as a second
                // odd shape instead of as the square everyone knows.
                rotationDegrees = if (shape === SquarePoints) 45f else 0f,
                // Hairline facet: the default extrusion is a tenth of the glyph at this size and
                // turns the shapes into smudges.
                facet = 1.dp,
                modifier = Modifier.fillMaxHeight().aspectRatio(1f),
            )
        }
    }
}

/** The operators inside the rules guide button, in the order the rules guide opens with. */
private val GuideOperators = listOf("+", "-", "×")

/**
 * The operators inside the rules guide button, drawn as the same chunky glyphs the arithmetic
 * topic tile is built from, so the two buttons in the section are a pair.
 */
@Composable
internal fun RulesGuideGlyphs(
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GuideOperators.forEach { operator ->
            Icon(
                imageVector = OperatorIcons.getValue(operator),
                contentDescription = null,
                tint = tint,
                modifier = Modifier.fillMaxHeight().aspectRatio(1f),
            )
        }
    }
}

/** The corners of a regular polygon with [sides] sides, point up, normalized to the unit square. */
private fun regularPolygonPoints(sides: Int): ImmutableList<Pair<Float, Float>> {
    val step = 2f * PI.toFloat() / sides
    val start = -PI.toFloat() / 2f
    return List(sides) { index ->
        val angle = start + step * index
        (0.5f + cos(angle) * 0.5f) to (0.5f + sin(angle) * 0.5f)
    }.toImmutableList()
}
