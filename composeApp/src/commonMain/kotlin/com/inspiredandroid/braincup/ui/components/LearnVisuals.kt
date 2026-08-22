package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.inspiredandroid.braincup.learn.LearnVisual
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Hand-drawn diagram accompanying a lesson step. Everything is drawn from the two theme colors so
 * the sketches stay legible in light, dark and OLED themes without per-theme assets.
 */
@Composable
fun LearnVisualCanvas(
    visual: LearnVisual,
    modifier: Modifier = Modifier,
    ink: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            when (visual) {
                LearnVisual.NUMBER_LINE -> drawNumberLine(ink)
                LearnVisual.FRACTION_BAR -> drawFractionBar(ink)
                LearnVisual.RULER -> drawRuler(ink)
                LearnVisual.AREA_RECTANGLE -> drawAreaRectangle(ink)
                LearnVisual.RIGHT_TRIANGLE -> drawRightTriangle(ink)
                LearnVisual.CIRCLE -> drawCircleDiagram(ink)
                LearnVisual.ANGLES -> drawAngles(ink)
                LearnVisual.BAR_CHART -> drawBarChart(ink)
                LearnVisual.BALANCE_SCALE -> drawBalanceScale(ink)
                LearnVisual.UNIT_CIRCLE -> drawUnitCircle(ink)
                LearnVisual.PARABOLA -> drawParabola(ink)
                LearnVisual.TANGENT_LINE -> drawTangentLine(ink)
                LearnVisual.AREA_UNDER_CURVE -> drawAreaUnderCurve(ink)
            }
        }
    }
}

private fun DrawScope.axisStroke() = size.minDimension * 0.02f

private fun DrawScope.drawNumberLine(ink: Color) {
    val y = size.height / 2
    val left = size.width * 0.08f
    val right = size.width * 0.92f
    drawLine(ink, Offset(left, y), Offset(right, y), strokeWidth = axisStroke(), cap = StrokeCap.Round)
    repeat(11) { index ->
        val x = left + (right - left) * index / 10f
        val major = index % 5 == 0
        val tick = if (major) size.height * 0.18f else size.height * 0.09f
        drawLine(
            color = if (major) Primary else ink,
            start = Offset(x, y - tick),
            end = Offset(x, y + tick),
            strokeWidth = axisStroke() * if (major) 1.4f else 1f,
            cap = StrokeCap.Round,
        )
    }
}

private fun DrawScope.drawFractionBar(ink: Color) {
    val parts = 4
    val filled = 3
    val barWidth = size.width * 0.84f
    val left = (size.width - barWidth) / 2
    val height = size.height * 0.4f
    val top = (size.height - height) / 2
    val partWidth = barWidth / parts
    repeat(parts) { index ->
        val x = left + partWidth * index
        drawRect(
            color = if (index < filled) Primary else Color.Transparent,
            topLeft = Offset(x, top),
            size = Size(partWidth, height),
        )
        drawRect(
            color = ink,
            topLeft = Offset(x, top),
            size = Size(partWidth, height),
            style = Stroke(width = axisStroke()),
        )
    }
}

private fun DrawScope.drawRuler(ink: Color) {
    val top = size.height * 0.32f
    val height = size.height * 0.36f
    drawRect(ink, Offset(size.width * 0.06f, top), Size(size.width * 0.88f, height), style = Stroke(axisStroke()))
    repeat(21) { index ->
        val x = size.width * 0.06f + size.width * 0.88f * index / 20f
        val long = index % 5 == 0
        drawLine(
            color = if (long) Primary else ink,
            start = Offset(x, top),
            end = Offset(x, top + height * if (long) 0.6f else 0.32f),
            strokeWidth = axisStroke(),
        )
    }
}

private fun DrawScope.drawAreaRectangle(ink: Color) {
    val cols = 6
    val rows = 3
    val cell = minOf(size.width * 0.8f / cols, size.height * 0.7f / rows)
    val left = (size.width - cell * cols) / 2
    val top = (size.height - cell * rows) / 2
    repeat(rows) { row ->
        repeat(cols) { col ->
            drawRect(
                color = Primary.copy(alpha = 0.18f),
                topLeft = Offset(left + col * cell, top + row * cell),
                size = Size(cell, cell),
            )
            drawRect(
                color = ink,
                topLeft = Offset(left + col * cell, top + row * cell),
                size = Size(cell, cell),
                style = Stroke(width = axisStroke() * 0.6f),
            )
        }
    }
    drawRect(ink, Offset(left, top), Size(cell * cols, cell * rows), style = Stroke(axisStroke() * 1.4f))
}

private fun DrawScope.drawRightTriangle(ink: Color) {
    val left = size.width * 0.2f
    val bottom = size.height * 0.82f
    val width = size.width * 0.5f
    val height = size.height * 0.62f
    val a = Offset(left, bottom)
    val b = Offset(left + width, bottom)
    val c = Offset(left, bottom - height)
    val path = Path().apply {
        moveTo(a.x, a.y)
        lineTo(b.x, b.y)
        lineTo(c.x, c.y)
        close()
    }
    drawPath(path, Primary.copy(alpha = 0.15f))
    drawPath(path, ink, style = Stroke(width = axisStroke() * 1.4f))
    // Right-angle marker in the corner.
    val marker = minOf(width, height) * 0.16f
    drawRect(
        color = Primary,
        topLeft = Offset(a.x, a.y - marker),
        size = Size(marker, marker),
        style = Stroke(width = axisStroke()),
    )
}

private fun DrawScope.drawCircleDiagram(ink: Color) {
    val radius = size.minDimension * 0.36f
    val center = Offset(size.width / 2, size.height / 2)
    drawCircle(Primary.copy(alpha = 0.15f), radius, center)
    drawCircle(ink, radius, center, style = Stroke(width = axisStroke() * 1.4f))
    drawLine(Primary, center, Offset(center.x + radius, center.y), strokeWidth = axisStroke() * 1.4f, cap = StrokeCap.Round)
    drawLine(
        color = SuccessGreen,
        start = Offset(center.x - radius, center.y),
        end = Offset(center.x + radius, center.y),
        strokeWidth = axisStroke(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f)),
    )
    drawCircle(ink, radius = axisStroke() * 1.6f, center = center)
}

private fun DrawScope.drawAngles(ink: Color) {
    val origin = Offset(size.width * 0.5f, size.height * 0.78f)
    val arm = size.minDimension * 0.62f
    drawLine(ink, Offset(origin.x - arm, origin.y), Offset(origin.x + arm, origin.y), strokeWidth = axisStroke() * 1.4f, cap = StrokeCap.Round)
    val angle = 55.0 * PI / 180.0
    val tip = Offset(origin.x + (arm * cos(angle)).toFloat(), origin.y - (arm * sin(angle)).toFloat())
    drawLine(Primary, origin, tip, strokeWidth = axisStroke() * 1.4f, cap = StrokeCap.Round)
    val sweepRadius = arm * 0.32f
    drawArc(
        color = SuccessGreen,
        startAngle = -55f,
        sweepAngle = 55f,
        useCenter = false,
        topLeft = Offset(origin.x - sweepRadius, origin.y - sweepRadius),
        size = Size(sweepRadius * 2, sweepRadius * 2),
        style = Stroke(width = axisStroke()),
    )
}

private fun DrawScope.drawBarChart(ink: Color) {
    val values = listOf(0.45f, 0.75f, 0.3f, 0.95f, 0.6f)
    val baseline = size.height * 0.85f
    val chartHeight = size.height * 0.66f
    val slot = size.width * 0.84f / values.size
    val barWidth = slot * 0.6f
    values.forEachIndexed { index, value ->
        val height = chartHeight * value
        val x = size.width * 0.08f + slot * index + (slot - barWidth) / 2
        drawRect(Primary, Offset(x, baseline - height), Size(barWidth, height))
    }
    drawLine(ink, Offset(size.width * 0.06f, baseline), Offset(size.width * 0.94f, baseline), strokeWidth = axisStroke() * 1.2f)
}

private fun DrawScope.drawBalanceScale(ink: Color) {
    val center = Offset(size.width / 2, size.height * 0.34f)
    val beam = size.width * 0.34f
    drawLine(ink, Offset(center.x - beam, center.y), Offset(center.x + beam, center.y), strokeWidth = axisStroke() * 1.6f, cap = StrokeCap.Round)
    drawLine(ink, center, Offset(center.x, size.height * 0.82f), strokeWidth = axisStroke() * 1.6f, cap = StrokeCap.Round)
    drawLine(
        ink,
        Offset(center.x - beam * 0.4f, size.height * 0.86f),
        Offset(center.x + beam * 0.4f, size.height * 0.86f),
        strokeWidth = axisStroke() * 1.6f,
        cap = StrokeCap.Round,
    )
    val panWidth = size.width * 0.22f
    val panHeight = size.height * 0.16f
    listOf(center.x - beam, center.x + beam).forEach { x ->
        drawLine(ink, Offset(x, center.y), Offset(x, center.y + panHeight * 0.5f), strokeWidth = axisStroke())
        drawRect(
            color = Primary,
            topLeft = Offset(x - panWidth / 2, center.y + panHeight * 0.5f),
            size = Size(panWidth, panHeight),
        )
    }
}

private fun DrawScope.drawUnitCircle(ink: Color) {
    val radius = size.minDimension * 0.36f
    val center = Offset(size.width / 2, size.height / 2)
    drawLine(ink, Offset(center.x - radius * 1.35f, center.y), Offset(center.x + radius * 1.35f, center.y), strokeWidth = axisStroke())
    drawLine(ink, Offset(center.x, center.y - radius * 1.35f), Offset(center.x, center.y + radius * 1.35f), strokeWidth = axisStroke())
    drawCircle(ink, radius, center, style = Stroke(width = axisStroke() * 1.4f))
    val angle = 52.0 * PI / 180.0
    val point = Offset(center.x + (radius * cos(angle)).toFloat(), center.y - (radius * sin(angle)).toFloat())
    drawLine(Primary, center, point, strokeWidth = axisStroke() * 1.4f, cap = StrokeCap.Round)
    drawLine(SuccessGreen, Offset(point.x, center.y), point, strokeWidth = axisStroke(), cap = StrokeCap.Round)
    drawLine(SuccessGreen, center, Offset(point.x, center.y), strokeWidth = axisStroke(), cap = StrokeCap.Round)
    drawCircle(Primary, radius = axisStroke() * 2f, center = point)
}

/** Shared plotting frame for the calculus sketches: axes plus a curve sampled across the width. */
private fun DrawScope.plotFrame(ink: Color): Rect {
    val rect = Rect(
        left = size.width * 0.12f,
        top = size.height * 0.12f,
        right = size.width * 0.88f,
        bottom = size.height * 0.84f,
    )
    drawLine(ink, Offset(rect.left, rect.bottom), Offset(rect.right, rect.bottom), strokeWidth = axisStroke())
    drawLine(ink, Offset(rect.left, rect.bottom), Offset(rect.left, rect.top), strokeWidth = axisStroke())
    return rect
}

/** y = x² mapped into [rect] with x running 0..1 from the left edge. */
private fun curvePoint(rect: Rect, t: Float): Offset = Offset(
    x = rect.left + rect.width * t,
    y = rect.bottom - rect.height * t * t,
)

private fun curvePath(rect: Rect): Path = Path().apply {
    val start = curvePoint(rect, 0f)
    moveTo(start.x, start.y)
    for (step in 1..40) {
        val point = curvePoint(rect, step / 40f)
        lineTo(point.x, point.y)
    }
}

private fun DrawScope.drawParabola(ink: Color) {
    val rect = plotFrame(ink)
    // Draw both halves so the U shape reads as a parabola rather than a single rising arm.
    val path = Path()
    for (step in 0..40) {
        val t = -1f + 2f * step / 40f
        val point = Offset(
            x = rect.left + rect.width * (t + 1f) / 2f,
            y = rect.bottom - rect.height * t * t,
        )
        if (step == 0) path.moveTo(point.x, point.y) else path.lineTo(point.x, point.y)
    }
    drawPath(path, Primary, style = Stroke(width = axisStroke() * 1.6f, cap = StrokeCap.Round))
}

private fun DrawScope.drawTangentLine(ink: Color) {
    val rect = plotFrame(ink)
    drawPath(curvePath(rect), Primary, style = Stroke(width = axisStroke() * 1.6f, cap = StrokeCap.Round))
    // Tangent to y = x² at t = 0.6 has slope 2t in curve space; extend it both ways.
    val t = 0.6f
    val point = curvePoint(rect, t)
    val slope = 2f * t * rect.height / rect.width
    val dx = rect.width * 0.3f
    drawLine(
        color = SuccessGreen,
        start = Offset(point.x - dx, point.y + slope * dx),
        end = Offset(point.x + dx, point.y - slope * dx),
        strokeWidth = axisStroke() * 1.4f,
        cap = StrokeCap.Round,
    )
    drawCircle(SuccessGreen, radius = axisStroke() * 2f, center = point)
}

private fun DrawScope.drawAreaUnderCurve(ink: Color) {
    val rect = plotFrame(ink)
    val fill = Path().apply {
        moveTo(rect.left, rect.bottom)
        for (step in 0..40) {
            val point = curvePoint(rect, step / 40f * 0.8f)
            lineTo(point.x, point.y)
        }
        lineTo(rect.left + rect.width * 0.8f, rect.bottom)
        close()
    }
    drawPath(fill, Primary.copy(alpha = 0.25f))
    drawPath(curvePath(rect), Primary, style = Stroke(width = axisStroke() * 1.6f, cap = StrokeCap.Round))
    drawLine(
        color = SuccessGreen,
        start = Offset(rect.left + rect.width * 0.8f, rect.bottom),
        end = curvePoint(rect, 0.8f),
        strokeWidth = axisStroke(),
        cap = StrokeCap.Round,
    )
}
