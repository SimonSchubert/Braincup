package com.inspiredandroid.braincup.ui.components.learn

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
        MathTopic.ALGEBRA -> UnknownSlotPreview()
    }
}

/**
 * The badge on a sub-topic row: which age band this rung is taught at, as a meter.
 *
 * A figure rather than a numeral, so the row reads at a glance and the badge belongs to the same
 * family as the topic sketches.
 *
 * One bar per band the topic covers, lit up to [band]. It used to be three bars filled by the
 * rung's *position* in the ladder, cut into thirds, which put a different reading beside identical
 * age text: Fractions and Decimals both say "Ages 8-11" and showed one bar and two. Two signals on
 * one row disagreeing is worse than one signal, and the age band is the one the learner acts on.
 */
@Composable
internal fun SubTopicRowPreview(band: Int, bands: Int, modifier: Modifier = Modifier) {
    val total = bands.coerceAtLeast(1)
    Row(
        modifier = modifier.padding(10.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        repeat(total) { index ->
            // Shortest to tallest across whatever number of bands the topic has.
            val height = if (total == 1) 1f else 0.4f + 0.6f * (index / (total - 1f))
            ColorPrismCell(
                face = if (index < band) Primary else PreviewStructure.copy(alpha = 0.3f),
                modifier = Modifier.weight(1f).fillMaxHeight(height),
            )
        }
    }
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

/**
 * The unknown, on a card of its own.
 *
 * One letter rather than a grid of them: what separates algebra from the arithmetic tile beside it
 * is not that it has more symbols but that one of them stands for a number you have not been told.
 */
@Composable
private fun UnknownSlotPreview() {
    PreviewBox(padding = 22.dp) {
        PrismCard(
            face = Primary,
            facet = PrismFacet.Cell,
            modifier = Modifier.fillMaxSize(0.78f),
        ) {
            Text(
                text = "x",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
            )
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
