package com.inspiredandroid.braincup.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.ui.theme.PuzzleSlateFrame
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * The four quadrant wedges of a Simon disc, matching `SimonSaysGame.PADS` order
 * (GREEN, RED, YELLOW, BLUE) 1:1 with (TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT).
 */
internal enum class SimonQuadrant { TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT }

internal val SimonQuadrants = listOf(
    SimonQuadrant.TOP_LEFT,
    SimonQuadrant.TOP_RIGHT,
    SimonQuadrant.BOTTOM_LEFT,
    SimonQuadrant.BOTTOM_RIGHT,
)

/**
 * A quarter-circle pie-slice shape for one pad of the 2x2 board: a straight edge in from the
 * shared center, a 90-degree arc around the outside, a straight edge back to center. Together the
 * four quadrants read as a single inscribed circle, like the physical Simon toy.
 */
internal fun simonQuadrantShape(quadrant: SimonQuadrant) = GenericShape { size, _ ->
    val w = size.width // cells are square (aspectRatio(1f)), so width == height
    when (quadrant) {
        SimonQuadrant.TOP_LEFT -> {
            moveTo(w, w)
            lineTo(w, 0f)
            arcTo(Rect(0f, 0f, 2 * w, 2 * w), startAngleDegrees = -90f, sweepAngleDegrees = -90f, forceMoveTo = false)
            close()
        }
        SimonQuadrant.TOP_RIGHT -> {
            moveTo(0f, w)
            lineTo(0f, 0f)
            arcTo(Rect(-w, 0f, w, 2 * w), startAngleDegrees = -90f, sweepAngleDegrees = 90f, forceMoveTo = false)
            close()
        }
        SimonQuadrant.BOTTOM_LEFT -> {
            moveTo(w, 0f)
            lineTo(0f, 0f)
            arcTo(Rect(0f, -w, 2 * w, w), startAngleDegrees = 180f, sweepAngleDegrees = -90f, forceMoveTo = false)
            close()
        }
        SimonQuadrant.BOTTOM_RIGHT -> {
            moveTo(0f, 0f)
            lineTo(w, 0f)
            arcTo(Rect(-w, -w, w, w), startAngleDegrees = 0f, sweepAngleDegrees = 90f, forceMoveTo = false)
            close()
        }
    }
}

/**
 * The lit and unlit faces of a pad. `darken` scales the channels, so the unlit face keeps the
 * pad's hue but at a fraction of its brightness; taking it well below half is what makes an
 * unlit pad read as off rather than merely dull, and the white lift makes the lit face read as
 * glowing rather than just "the normal color". Shared so the board, the tutorial and the menu
 * tile can never drift apart on what "lit" looks like.
 */
internal fun simonPadColor(base: Color, lit: Boolean): Color = if (lit) lerp(base, Color.White, 0.18f) else base.darken(0.28f)

/**
 * Pads snap between lit and unlit rather than easing. The default spring smears a pad's fade-out
 * across the gap between flashes, which reads as the previous pad still being active.
 */
internal val SimonPadColorSpec = tween<Color>(durationMillis = 110)

/**
 * Diameter of a circular overlay (a feedback mark) inside a wedge, as a fraction of the pad.
 * Paired with [simonWedgeAlignment]: together they keep the circle clear of the arc, which clips
 * anything that spills past it.
 */
internal const val SimonWedgeMarkFraction = 0.26f

/**
 * Where to anchor a [SimonWedgeMarkFraction]-sized overlay inside a wedge. Pushed out along the
 * bisector toward the quarter disc's centroid so the mark sits on the fat part of the wedge and
 * clear of the hub, but not so far that the circle crosses the arc: at bias b the circle's center
 * is 1.414 * (1 - (0.37 * (1 + b) + 0.13)) of the pad from the disc center, which plus the 0.13
 * radius has to stay under 1.
 */
internal fun simonWedgeAlignment(quadrant: SimonQuadrant): Alignment {
    val (x, y) = when (quadrant) {
        SimonQuadrant.TOP_LEFT -> -0.25f to -0.25f
        SimonQuadrant.TOP_RIGHT -> 0.25f to -0.25f
        SimonQuadrant.BOTTOM_LEFT -> -0.25f to 0.25f
        SimonQuadrant.BOTTOM_RIGHT -> 0.25f to 0.25f
    }
    return BiasAlignment(horizontalBias = x, verticalBias = y)
}

/**
 * How far a pad is extruded down-right, as a fraction of the pad's width. Relative rather than a
 * fixed dp so the 45dp pads of the menu tile and the 140dp pads of the board keep one proportion.
 */
private const val SimonPadDepthFraction = 0.06f

private const val SimonArcSegments = 16

private val SimonBodyColor = PuzzleSlateFrame

/** The wedge as a polygon, so it can be extruded by [drawPrismPolygon] like every other prism. */
private fun simonWedgePoints(quadrant: SimonQuadrant, w: Float): List<Offset> {
    val (center, startDegrees) = when (quadrant) {
        SimonQuadrant.TOP_LEFT -> Offset(w, w) to 180f
        SimonQuadrant.TOP_RIGHT -> Offset(0f, w) to 270f
        SimonQuadrant.BOTTOM_LEFT -> Offset(w, 0f) to 90f
        SimonQuadrant.BOTTOM_RIGHT -> Offset(0f, 0f) to 0f
    }
    return listOf(center) + List(SimonArcSegments + 1) { i ->
        val radians = (startDegrees + 90f * i / SimonArcSegments) * PI.toFloat() / 180f
        Offset(center.x + w * cos(radians), center.y + w * sin(radians))
    }
}

/**
 * Paints one extruded pad. [sink] runs from 0 (raised) to 1 (pressed flush), the same press the
 * prism tiles make. Drawing is not clipped: the extrusion has to spill into the gap below and to
 * the right, where the next pad (drawn later) or the disc body covers it.
 */
internal fun Modifier.simonPadSurface(
    quadrant: SimonQuadrant,
    face: Color,
    sink: Float = 0f,
    depth: Dp? = null,
): Modifier = drawWithCache {
    val depth = depth?.toPx() ?: (size.width * SimonPadDepthFraction)
    val points = simonWedgePoints(quadrant, size.width)
    onDrawBehind {
        val shift = depth * sink
        translate(shift, shift) { drawPrismPolygon(points, face, depth - shift) }
    }
}

/**
 * The 2x2 board of wedges seated in a slate body with a raised center cap, like the physical toy.
 * Shared by the live game, the instructions demo and the menu tile so all three stay identical.
 *
 * [pad] receives the index into `SimonSaysGame.PADS`, its quadrant, and the modifier the wedge
 * must apply (weight/aspect/padding), so callers only decide how a wedge is painted, normally via
 * [simonPadSurface].
 */
@Composable
internal fun SimonDisc(
    modifier: Modifier = Modifier,
    bodyColor: Color = SimonBodyColor,
    depth: Dp? = null,
    pad: @Composable (index: Int, quadrant: SimonQuadrant, padModifier: Modifier) -> Unit,
) {
    Box(modifier = modifier) {
        Canvas(Modifier.matchParentSize()) {
            val padDepth = depth?.toPx() ?: (size.width / 2f * SimonPadDepthFraction)
            // Centred half a pad-depth down-right and grown by the diagonal reach of the
            // extrusion, so the body encloses the pads' sides as well as their faces and still
            // shows a rim all the way round.
            drawPrismCircle(
                center = center + Offset(padDepth / 2f, padDepth / 2f),
                radius = size.width * 0.53f + padDepth * 0.71f,
                face = bodyColor,
                depth = padDepth,
            )
        }
        Column {
            for (row in 0 until 2) {
                Row {
                    for (col in 0 until 2) {
                        val index = row * 2 + col
                        pad(
                            index,
                            SimonQuadrants[index],
                            Modifier.weight(1f).aspectRatio(1f).padding(2.dp),
                        )
                    }
                }
            }
        }
        // Sized off the board rather than a fixed dp so the cap keeps its proportion when the
        // board shrinks below its max width on narrow screens.
        Canvas(Modifier.matchParentSize()) {
            drawPrismCircle(
                center = center,
                radius = size.width * 0.09f,
                face = bodyColor,
                depth = depth?.toPx() ?: (size.width * 0.09f * 0.22f),
            )
        }
    }
}
