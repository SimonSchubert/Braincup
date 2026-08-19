package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer

/** A bold, round-capped checkmark matching the chunky tile typography. */
@Composable
fun ChunkyCheck(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val stroke = minOf(w, h) * 0.22f
        val elbow = Offset(w * 0.40f, h * 0.78f)
        drawLine(color, Offset(w * 0.08f, h * 0.50f), elbow, strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color, elbow, Offset(w * 0.92f, h * 0.20f), strokeWidth = stroke, cap = StrokeCap.Round)
    }
}

/** A bold chevron pointing right at [rotationDegrees] 0, and down at 90. */
@Composable
fun ChunkyChevron(
    color: Color,
    modifier: Modifier = Modifier,
    rotationDegrees: Float = 0f,
) {
    Canvas(modifier.graphicsLayer { rotationZ = rotationDegrees }) {
        val w = size.width
        val h = size.height
        val stroke = minOf(w, h) * 0.18f
        val tip = Offset(w * 0.68f, h * 0.50f)
        drawLine(color, Offset(w * 0.34f, h * 0.18f), tip, strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color, tip, Offset(w * 0.34f, h * 0.82f), strokeWidth = stroke, cap = StrokeCap.Round)
    }
}

/** A hand-drawn padlock marking a puzzle still locked behind an earlier one in its tier. */
@Composable
fun ChunkyLock(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val stroke = minOf(w, h) * 0.15f
        val shackleW = w * 0.42f
        val shackleLeft = (w - shackleW) / 2f
        drawArc(
            color = color,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(shackleLeft, h * 0.10f),
            size = Size(shackleW, h * 0.70f),
            style = Stroke(width = stroke, cap = StrokeCap.Round),
        )
        drawRect(
            color = color,
            topLeft = Offset(w * 0.20f, h * 0.45f),
            size = Size(w * 0.60f, h * 0.45f),
        )
    }
}
