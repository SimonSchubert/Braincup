package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.app.MentalRotationsUiState
import com.inspiredandroid.braincup.games.CUBE_HALF_WIDTH
import com.inspiredandroid.braincup.games.CUBE_SIDE_HEIGHT
import com.inspiredandroid.braincup.games.CUBE_TOP_HEIGHT
import com.inspiredandroid.braincup.ui.theme.MentalRotationsCubeEdge
import com.inspiredandroid.braincup.ui.theme.MentalRotationsCubeLeft
import com.inspiredandroid.braincup.ui.theme.MentalRotationsCubeRight
import com.inspiredandroid.braincup.ui.theme.MentalRotationsCubeTop
import kotlin.math.max
import kotlin.math.min

/**
 * The reference and the candidate side by side.
 *
 * Both are drawn at one shared scale, derived from whichever figure needs the most room. Scaling
 * each panel to its own box would draw the same solid at two different cube sizes, which is both a
 * cue the player can read instead of rotating, and a needless obstacle to comparing them.
 */
@Composable
fun MentalRotationsPair(
    reference: MentalRotationsUiState.Figure,
    candidate: MentalRotationsUiState.Figure,
    modifier: Modifier = Modifier,
    spacing: Dp = 12.dp,
) {
    val boxWidth = max(reference.width, candidate.width)
    val boxHeight = max(reference.height, candidate.height)
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val panel = Modifier.weight(1f).fillMaxHeight()
        MentalRotationsFigure(reference, boxWidth, boxHeight, panel)
        MentalRotationsFigure(candidate, boxWidth, boxHeight, panel)
    }
}

/**
 * A projected Mental Rotations figure, centred in whatever space [modifier] gives it.
 *
 * The drawing scales itself to the canvas, so the caller owns the sizing. That matters on the game
 * screen, where the figures share a column with the answer buttons and so have to be bounded by
 * height rather than deriving a square from the available width.
 *
 * [boxWidth]/[boxHeight] is the space the drawing scales to fit, in the projection's own units.
 * Pass a box big enough for a set of figures to draw them all at one scale; the default is the
 * figure's own bounds, which fills the canvas.
 */
@Composable
fun MentalRotationsFigure(
    figure: MentalRotationsUiState.Figure,
    boxWidth: Float = figure.width,
    boxHeight: Float = figure.height,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        drawIsometricFigure(figure, boxWidth, boxHeight)
    }
}

/**
 * Draw a projected figure, scaled so [boxWidth] x [boxHeight] fits the canvas and centred in it.
 *
 * The game hands over cubes already flattened and already sorted back to front, so this only has
 * to fit them to the box and paint three parallelograms per cube in order. Later cubes overpaint
 * the ones they occlude, which is all the depth handling an isometric view needs.
 */
fun DrawScope.drawIsometricFigure(
    figure: MentalRotationsUiState.Figure,
    boxWidth: Float = figure.width,
    boxHeight: Float = figure.height,
) {
    if (figure.cubes.isEmpty() || boxWidth <= 0f || boxHeight <= 0f) return

    val scale = min(size.width / boxWidth, size.height / boxHeight) * FIT_MARGIN
    // Centre the figure itself, not the shared box, so a small figure still sits in the middle.
    val offsetX = (size.width - figure.width * scale) / 2f
    val offsetY = (size.height - figure.height * scale) / 2f

    val halfWidth = CUBE_HALF_WIDTH * scale
    val topHeight = CUBE_TOP_HEIGHT * scale
    val sideHeight = CUBE_SIDE_HEIGHT * scale
    val strokeWidth = (scale * EDGE_STROKE).coerceAtLeast(0.5f)

    figure.cubes.forEach { cube ->
        drawCube(
            cx = offsetX + cube.x * scale,
            cy = offsetY + cube.y * scale,
            halfWidth = halfWidth,
            topHeight = topHeight,
            sideHeight = sideHeight,
            strokeWidth = strokeWidth,
        )
    }
}

/**
 * One unit cube, as its three visible faces. [cx]/[cy] is the topmost vertex; the top face is the
 * rhombus below it, and the two side faces hang off that rhombus's lower edges.
 */
private fun DrawScope.drawCube(
    cx: Float,
    cy: Float,
    halfWidth: Float,
    topHeight: Float,
    sideHeight: Float,
    strokeWidth: Float,
) {
    val top = Offset(cx, cy)
    val left = Offset(cx - halfWidth, cy + topHeight / 2f)
    val right = Offset(cx + halfWidth, cy + topHeight / 2f)
    val middle = Offset(cx, cy + topHeight)

    drawFace(facePath(top, left, middle, right), MentalRotationsCubeTop, strokeWidth)
    drawFace(
        facePath(left, Offset(left.x, left.y + sideHeight), Offset(middle.x, middle.y + sideHeight), middle),
        MentalRotationsCubeLeft,
        strokeWidth,
    )
    drawFace(
        facePath(middle, Offset(middle.x, middle.y + sideHeight), Offset(right.x, right.y + sideHeight), right),
        MentalRotationsCubeRight,
        strokeWidth,
    )
}

private fun DrawScope.drawFace(path: Path, fill: androidx.compose.ui.graphics.Color, strokeWidth: Float) {
    drawPath(path, fill)
    drawPath(path, MentalRotationsCubeEdge, style = Stroke(width = strokeWidth))
}

private fun facePath(a: Offset, b: Offset, c: Offset, d: Offset) = Path().apply {
    moveTo(a.x, a.y)
    lineTo(b.x, b.y)
    lineTo(c.x, c.y)
    lineTo(d.x, d.y)
    close()
}

private const val FIT_MARGIN = 0.88f
private const val EDGE_STROKE = 0.045f
