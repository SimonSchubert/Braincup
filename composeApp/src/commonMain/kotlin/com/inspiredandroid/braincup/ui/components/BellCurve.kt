package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.numberFontFamily
import kotlin.math.exp

/**
 * The normal distribution of IQ scores with the player's own score marked.
 *
 * Shading the area to the left of the marker is the point of the chart: it turns an abstract number
 * into the share of people it sits above, which is what a percentile actually means.
 */
@Composable
fun BellCurve(
    score: Int,
    modifier: Modifier = Modifier,
    minScore: Int = 55,
    maxScore: Int = 145,
    ticks: List<Int> = listOf(70, 85, 100, 115, 130),
) {
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val fillColor = Primary
    val axisColor = MaterialTheme.colorScheme.outlineVariant
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val markerColor = MaterialTheme.colorScheme.onSurface
    val textMeasurer = rememberTextMeasurer()
    val labelStyle = MaterialTheme.typography.labelSmall.copy(
        fontFamily = numberFontFamily(),
        color = labelColor,
    )
    val markerStyle = MaterialTheme.typography.labelLarge.copy(
        fontFamily = numberFontFamily(),
        color = markerColor,
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(CurveHeight),
    ) {
        val labelHeight = textMeasurer.measure("100", labelStyle).size.height.toFloat()
        val markerHeight = textMeasurer.measure("100", markerStyle).size.height.toFloat()
        val plotTop = markerHeight + MarkerLabelGap.toPx()
        val plotBottom = size.height - labelHeight - AxisLabelGap.toPx()
        val plotHeight = (plotBottom - plotTop).coerceAtLeast(1f)

        fun xFor(value: Float): Float = (value - minScore) / (maxScore - minScore).toFloat() * size.width

        val curve = Path().apply {
            moveTo(0f, plotBottom)
            for (step in 0..CurveSteps) {
                val value = minScore + (maxScore - minScore) * step / CurveSteps.toFloat()
                val z = (value - 100f) / 15f
                lineTo(xFor(value), plotBottom - exp(-0.5f * z * z) * plotHeight)
            }
            lineTo(size.width, plotBottom)
            close()
        }

        drawPath(curve, trackColor)
        val markerX = xFor(score.coerceIn(minScore, maxScore).toFloat())
        clipRect(right = markerX) {
            drawPath(curve, fillColor)
        }

        drawLine(
            color = axisColor,
            start = Offset(0f, plotBottom),
            end = Offset(size.width, plotBottom),
            strokeWidth = AxisStroke.toPx(),
        )

        for (tick in ticks) {
            val x = xFor(tick.toFloat())
            drawLine(
                color = axisColor,
                start = Offset(x, plotBottom),
                end = Offset(x, plotBottom + TickLength.toPx()),
                strokeWidth = AxisStroke.toPx(),
            )
            drawCenteredText(textMeasurer, tick.toString(), labelStyle, x, plotBottom + AxisLabelGap.toPx())
        }

        drawLine(
            color = markerColor,
            start = Offset(markerX, plotTop),
            end = Offset(markerX, plotBottom),
            strokeWidth = MarkerStroke.toPx(),
        )
        drawCircle(
            color = markerColor,
            radius = MarkerDotRadius.toPx(),
            center = Offset(markerX, plotTop),
            style = Stroke(width = MarkerStroke.toPx()),
        )
        drawCenteredText(textMeasurer, score.toString(), markerStyle, markerX, 0f)
    }
}

/** Keeps a label inside the canvas when the marker sits at either edge. */
private fun DrawScope.drawCenteredText(
    textMeasurer: TextMeasurer,
    text: String,
    style: TextStyle,
    centerX: Float,
    top: Float,
) {
    val measured = textMeasurer.measure(text, style)
    val left = (centerX - measured.size.width / 2f).coerceIn(0f, size.width - measured.size.width)
    drawText(measured, topLeft = Offset(left, top))
}

private val CurveHeight = 148.dp
private val TickLength = 4.dp
private val AxisLabelGap = 8.dp
private val MarkerLabelGap = 6.dp
private val AxisStroke = 1.dp
private val MarkerStroke = 2.dp
private val MarkerDotRadius = 4.dp
private const val CurveSteps = 160
