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
                LearnVisual.COUNTERS -> drawCounters(ink)
                LearnVisual.PLACE_VALUE_BLOCKS -> drawPlaceValueBlocks(ink)
                LearnVisual.ARRAY_GRID -> drawArrayGrid(ink)
                LearnVisual.FRACTION_BAR -> drawFractionBar(ink)
                LearnVisual.RULER -> drawRuler(ink)
                LearnVisual.CLOCK -> drawClock(ink)
                LearnVisual.COINS -> drawCoins(ink)
                LearnVisual.SHAPES_2D -> drawShapes2d(ink)
                LearnVisual.SOLIDS -> drawSolids(ink)
                LearnVisual.SYMMETRY -> drawSymmetry(ink)
                LearnVisual.COORDINATE_GRID -> drawCoordinateGrid(ink)
                LearnVisual.PICTOGRAM -> drawPictogram(ink)
                LearnVisual.PIE_CHART -> drawPieChart(ink)
                LearnVisual.NORMAL_CURVE -> drawNormalCurve(ink)
                LearnVisual.EXPONENTIAL_CURVE -> drawExponentialCurve(ink)
                LearnVisual.SINE_WAVE -> drawSineWave(ink)
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

private fun DrawScope.drawCounters(ink: Color) {
    // Two groups of dots side by side: the "4 counters and 3 counters" picture.
    val radius = size.minDimension * 0.07f
    val gap = radius * 2.6f
    val rowY = size.height / 2
    repeat(4) { index ->
        drawCircle(Primary, radius, Offset(size.width * 0.14f + gap * index, rowY))
    }
    repeat(3) { index ->
        drawCircle(SuccessGreen, radius, Offset(size.width * 0.62f + gap * index, rowY))
    }
    drawLine(
        color = ink,
        start = Offset(size.width * 0.55f, rowY - gap),
        end = Offset(size.width * 0.55f, rowY + gap),
        strokeWidth = axisStroke(),
        cap = StrokeCap.Round,
    )
}

private fun DrawScope.drawPlaceValueBlocks(ink: Color) {
    // Three ten-rods and seven loose ones: 37 as a picture.
    val cell = minOf(size.width * 0.08f, size.height * 0.16f)
    val top = size.height * 0.2f
    repeat(3) { rod ->
        val x = size.width * 0.1f + rod * cell * 1.6f
        repeat(5) { row ->
            repeat(2) { col ->
                val topLeft = Offset(x + col * cell, top + row * cell)
                drawRect(Primary.copy(alpha = 0.35f), topLeft, Size(cell, cell))
                drawRect(ink, topLeft, Size(cell, cell), style = Stroke(axisStroke() * 0.5f))
            }
        }
    }
    repeat(7) { index ->
        val topLeft = Offset(
            x = size.width * 0.62f + (index % 3) * cell * 1.3f,
            y = top + (index / 3) * cell * 1.3f,
        )
        drawRect(SuccessGreen.copy(alpha = 0.4f), topLeft, Size(cell, cell))
        drawRect(ink, topLeft, Size(cell, cell), style = Stroke(axisStroke() * 0.5f))
    }
}

private fun DrawScope.drawArrayGrid(ink: Color) {
    val cols = 6
    val rows = 4
    val step = minOf(size.width * 0.8f / cols, size.height * 0.72f / rows)
    val left = (size.width - step * (cols - 1)) / 2
    val top = (size.height - step * (rows - 1)) / 2
    val radius = step * 0.24f
    repeat(rows) { row ->
        repeat(cols) { col ->
            drawCircle(Primary, radius, Offset(left + col * step, top + row * step))
        }
    }
    drawRect(
        color = ink,
        topLeft = Offset(left - step * 0.5f, top - step * 0.5f),
        size = Size(step * cols, step * rows),
        style = Stroke(width = axisStroke()),
    )
}

private fun DrawScope.drawClock(ink: Color) {
    val radius = size.minDimension * 0.38f
    val center = Offset(size.width / 2, size.height / 2)
    drawCircle(ink, radius, center, style = Stroke(width = axisStroke() * 1.4f))
    repeat(12) { hour ->
        val angle = hour * PI / 6.0
        val outer = Offset(
            center.x + (radius * 0.92f * sin(angle)).toFloat(),
            center.y - (radius * 0.92f * cos(angle)).toFloat(),
        )
        val inner = Offset(
            center.x + (radius * 0.78f * sin(angle)).toFloat(),
            center.y - (radius * 0.78f * cos(angle)).toFloat(),
        )
        drawLine(ink, inner, outer, strokeWidth = axisStroke(), cap = StrokeCap.Round)
    }
    // Ten past two, so both hands are clearly visible.
    val hourAngle = 2.17 * PI / 6.0
    val minuteAngle = 2.0 * PI / 6.0
    drawLine(
        color = Primary,
        start = center,
        end = Offset(
            center.x + (radius * 0.5f * sin(hourAngle)).toFloat(),
            center.y - (radius * 0.5f * cos(hourAngle)).toFloat(),
        ),
        strokeWidth = axisStroke() * 1.8f,
        cap = StrokeCap.Round,
    )
    drawLine(
        color = SuccessGreen,
        start = center,
        end = Offset(
            center.x + (radius * 0.78f * sin(minuteAngle)).toFloat(),
            center.y - (radius * 0.78f * cos(minuteAngle)).toFloat(),
        ),
        strokeWidth = axisStroke() * 1.3f,
        cap = StrokeCap.Round,
    )
    drawCircle(ink, axisStroke() * 1.6f, center)
}

private fun DrawScope.drawCoins(ink: Color) {
    // Three coins of different value, largest first, the way you count them.
    val y = size.height / 2
    val radii = listOf(size.minDimension * 0.22f, size.minDimension * 0.17f, size.minDimension * 0.13f)
    var x = size.width * 0.26f
    radii.forEachIndexed { index, radius ->
        val center = Offset(x, y)
        drawCircle(if (index == 0) Primary.copy(alpha = 0.35f) else SuccessGreen.copy(alpha = 0.3f), radius, center)
        drawCircle(ink, radius, center, style = Stroke(width = axisStroke() * 1.2f))
        drawCircle(ink, radius * 0.62f, center, style = Stroke(width = axisStroke() * 0.6f))
        x += radius + radii.getOrElse(index + 1) { radius } + size.width * 0.05f
    }
}

private fun DrawScope.drawShapes2d(ink: Color) {
    val cell = minOf(size.width * 0.28f, size.height * 0.5f)
    val y = size.height / 2
    val triangleCenter = Offset(size.width * 0.22f, y)
    val triangle = Path().apply {
        moveTo(triangleCenter.x, triangleCenter.y - cell / 2)
        lineTo(triangleCenter.x + cell / 2, triangleCenter.y + cell / 2)
        lineTo(triangleCenter.x - cell / 2, triangleCenter.y + cell / 2)
        close()
    }
    drawPath(triangle, Primary.copy(alpha = 0.2f))
    drawPath(triangle, ink, style = Stroke(width = axisStroke() * 1.2f))

    drawRect(
        color = SuccessGreen.copy(alpha = 0.2f),
        topLeft = Offset(size.width * 0.5f - cell / 2, y - cell / 2),
        size = Size(cell, cell),
    )
    drawRect(
        color = ink,
        topLeft = Offset(size.width * 0.5f - cell / 2, y - cell / 2),
        size = Size(cell, cell),
        style = Stroke(width = axisStroke() * 1.2f),
    )

    val pentagonCenter = Offset(size.width * 0.78f, y)
    val pentagon = Path()
    repeat(5) { index ->
        val angle = -PI / 2 + index * 2 * PI / 5
        val point = Offset(
            pentagonCenter.x + (cell / 2 * cos(angle)).toFloat(),
            pentagonCenter.y + (cell / 2 * sin(angle)).toFloat(),
        )
        if (index == 0) pentagon.moveTo(point.x, point.y) else pentagon.lineTo(point.x, point.y)
    }
    pentagon.close()
    drawPath(pentagon, Primary.copy(alpha = 0.2f))
    drawPath(pentagon, ink, style = Stroke(width = axisStroke() * 1.2f))
}

private fun DrawScope.drawSolids(ink: Color) {
    val stroke = Stroke(width = axisStroke() * 1.2f)
    // Cube drawn as a front face plus an offset back face.
    val side = minOf(size.width * 0.26f, size.height * 0.42f)
    val depth = side * 0.4f
    val cubeLeft = size.width * 0.08f
    val cubeTop = size.height * 0.34f
    drawRect(Primary.copy(alpha = 0.18f), Offset(cubeLeft, cubeTop), Size(side, side))
    drawRect(ink, Offset(cubeLeft, cubeTop), Size(side, side), style = stroke)
    drawRect(ink, Offset(cubeLeft + depth, cubeTop - depth), Size(side, side), style = stroke)
    listOf(
        Offset(cubeLeft, cubeTop) to Offset(cubeLeft + depth, cubeTop - depth),
        Offset(cubeLeft + side, cubeTop) to Offset(cubeLeft + side + depth, cubeTop - depth),
        Offset(cubeLeft, cubeTop + side) to Offset(cubeLeft + depth, cubeTop + side - depth),
        Offset(cubeLeft + side, cubeTop + side) to Offset(cubeLeft + side + depth, cubeTop + side - depth),
    ).forEach { (from, to) -> drawLine(ink, from, to, strokeWidth = axisStroke()) }

    // Cylinder: two ellipses joined by straight sides.
    val cylinderCenterX = size.width * 0.62f
    val cylinderWidth = side * 0.9f
    val ellipseHeight = side * 0.3f
    val cylinderTop = size.height * 0.3f
    val cylinderBottom = size.height * 0.74f
    drawOval(
        color = SuccessGreen.copy(alpha = 0.2f),
        topLeft = Offset(cylinderCenterX - cylinderWidth / 2, cylinderTop),
        size = Size(cylinderWidth, ellipseHeight),
    )
    drawOval(
        color = ink,
        topLeft = Offset(cylinderCenterX - cylinderWidth / 2, cylinderTop),
        size = Size(cylinderWidth, ellipseHeight),
        style = stroke,
    )
    drawOval(
        color = ink,
        topLeft = Offset(cylinderCenterX - cylinderWidth / 2, cylinderBottom - ellipseHeight),
        size = Size(cylinderWidth, ellipseHeight),
        style = stroke,
    )
    listOf(cylinderCenterX - cylinderWidth / 2, cylinderCenterX + cylinderWidth / 2).forEach { x ->
        drawLine(
            ink,
            Offset(x, cylinderTop + ellipseHeight / 2),
            Offset(x, cylinderBottom - ellipseHeight / 2),
            strokeWidth = axisStroke() * 1.2f,
        )
    }

    // Cone: a triangle sitting on an ellipse.
    val coneCenterX = size.width * 0.88f
    val coneWidth = side * 0.7f
    val cone = Path().apply {
        moveTo(coneCenterX, cylinderTop)
        lineTo(coneCenterX + coneWidth / 2, cylinderBottom - ellipseHeight / 2)
        lineTo(coneCenterX - coneWidth / 2, cylinderBottom - ellipseHeight / 2)
        close()
    }
    drawPath(cone, Primary.copy(alpha = 0.18f))
    drawPath(cone, ink, style = stroke)
    drawOval(
        color = ink,
        topLeft = Offset(coneCenterX - coneWidth / 2, cylinderBottom - ellipseHeight),
        size = Size(coneWidth, ellipseHeight),
        style = stroke,
    )
}

private fun DrawScope.drawSymmetry(ink: Color) {
    // A shape and its mirror image either side of a dashed fold line.
    val axis = size.width / 2
    val top = size.height * 0.22f
    val bottom = size.height * 0.78f
    val reach = size.width * 0.34f
    listOf(-1f, 1f).forEach { direction ->
        val wing = Path().apply {
            moveTo(axis, top)
            lineTo(axis + reach * direction, size.height * 0.42f)
            lineTo(axis + reach * 0.55f * direction, bottom)
            lineTo(axis, bottom * 0.94f)
            close()
        }
        drawPath(wing, Primary.copy(alpha = 0.2f))
        drawPath(wing, ink, style = Stroke(width = axisStroke() * 1.2f))
    }
    drawLine(
        color = SuccessGreen,
        start = Offset(axis, size.height * 0.1f),
        end = Offset(axis, size.height * 0.9f),
        strokeWidth = axisStroke() * 1.2f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f)),
    )
}

private fun DrawScope.drawCoordinateGrid(ink: Color) {
    val rect = Rect(size.width * 0.1f, size.height * 0.1f, size.width * 0.9f, size.height * 0.9f)
    val faint = ink.copy(alpha = 0.28f)
    repeat(5) { index ->
        val t = index / 4f
        drawLine(faint, Offset(rect.left, rect.top + rect.height * t), Offset(rect.right, rect.top + rect.height * t), strokeWidth = axisStroke() * 0.6f)
        drawLine(faint, Offset(rect.left + rect.width * t, rect.top), Offset(rect.left + rect.width * t, rect.bottom), strokeWidth = axisStroke() * 0.6f)
    }
    val centerY = rect.top + rect.height / 2
    val centerX = rect.left + rect.width / 2
    drawLine(ink, Offset(rect.left, centerY), Offset(rect.right, centerY), strokeWidth = axisStroke() * 1.2f)
    drawLine(ink, Offset(centerX, rect.top), Offset(centerX, rect.bottom), strokeWidth = axisStroke() * 1.2f)
    drawLine(
        color = Primary,
        start = Offset(rect.left, rect.bottom - rect.height * 0.15f),
        end = Offset(rect.right, rect.top + rect.height * 0.1f),
        strokeWidth = axisStroke() * 1.6f,
        cap = StrokeCap.Round,
    )
}

private fun DrawScope.drawPictogram(ink: Color) {
    // Three rows of symbols, the last row ending in a half symbol.
    val counts = listOf(4f, 2.5f, 3f)
    val step = size.width * 0.17f
    val radius = step * 0.3f
    counts.forEachIndexed { row, count ->
        val y = size.height * (0.24f + row * 0.26f)
        var drawn = 0f
        while (drawn + 1f <= count) {
            drawCircle(Primary, radius, Offset(size.width * 0.16f + step * drawn, y))
            drawn += 1f
        }
        if (count - drawn > 0f) {
            drawArc(
                color = Primary,
                startAngle = 90f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(size.width * 0.16f + step * drawn - radius, y - radius),
                size = Size(radius * 2, radius * 2),
            )
        }
    }
    drawLine(
        color = ink,
        start = Offset(size.width * 0.09f, size.height * 0.12f),
        end = Offset(size.width * 0.09f, size.height * 0.88f),
        strokeWidth = axisStroke(),
    )
}

private fun DrawScope.drawPieChart(ink: Color) {
    val diameter = size.minDimension * 0.72f
    val topLeft = Offset((size.width - diameter) / 2, (size.height - diameter) / 2)
    val slices = listOf(140f to Primary, 110f to SuccessGreen, 110f to ink.copy(alpha = 0.35f))
    var start = -90f
    slices.forEach { (sweep, color) ->
        drawArc(color, start, sweep, useCenter = true, topLeft = topLeft, size = Size(diameter, diameter))
        start += sweep
    }
    drawArc(
        color = ink,
        startAngle = 0f,
        sweepAngle = 360f,
        useCenter = false,
        topLeft = topLeft,
        size = Size(diameter, diameter),
        style = Stroke(width = axisStroke() * 1.2f),
    )
}

/** Bell curve on [rect], with x running -3..3 standard deviations. */
private fun bellPoint(rect: Rect, t: Float): Offset {
    val z = -3f + 6f * t
    val height = kotlin.math.exp(-0.5 * z * z).toFloat()
    return Offset(rect.left + rect.width * t, rect.bottom - rect.height * height)
}

private fun DrawScope.drawNormalCurve(ink: Color) {
    val rect = plotFrame(ink)
    val fill = Path().apply {
        moveTo(rect.left + rect.width / 3f, rect.bottom)
        for (step in 0..20) {
            val point = bellPoint(rect, 1f / 3f + step / 20f / 3f)
            lineTo(point.x, point.y)
        }
        lineTo(rect.left + rect.width * 2f / 3f, rect.bottom)
        close()
    }
    drawPath(fill, Primary.copy(alpha = 0.25f))
    val curve = Path()
    for (step in 0..60) {
        val point = bellPoint(rect, step / 60f)
        if (step == 0) curve.moveTo(point.x, point.y) else curve.lineTo(point.x, point.y)
    }
    drawPath(curve, Primary, style = Stroke(width = axisStroke() * 1.6f, cap = StrokeCap.Round))
    drawLine(
        color = SuccessGreen,
        start = Offset(rect.left + rect.width / 2, rect.bottom),
        end = bellPoint(rect, 0.5f),
        strokeWidth = axisStroke(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f)),
    )
}

private fun DrawScope.drawExponentialCurve(ink: Color) {
    val rect = plotFrame(ink)
    val curve = Path()
    for (step in 0..40) {
        val t = step / 40f
        val point = Offset(
            x = rect.left + rect.width * t,
            y = rect.bottom - rect.height * (kotlin.math.exp(3.0 * t).toFloat() - 1f) / (kotlin.math.exp(3.0).toFloat() - 1f),
        )
        if (step == 0) curve.moveTo(point.x, point.y) else curve.lineTo(point.x, point.y)
    }
    drawPath(curve, Primary, style = Stroke(width = axisStroke() * 1.6f, cap = StrokeCap.Round))
    drawLine(
        color = SuccessGreen,
        start = Offset(rect.left, rect.bottom),
        end = Offset(rect.right, rect.top + rect.height * 0.55f),
        strokeWidth = axisStroke(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f)),
    )
}

private fun DrawScope.drawSineWave(ink: Color) {
    val rect = Rect(size.width * 0.08f, size.height * 0.14f, size.width * 0.92f, size.height * 0.86f)
    val midY = rect.top + rect.height / 2
    drawLine(ink, Offset(rect.left, midY), Offset(rect.right, midY), strokeWidth = axisStroke())
    drawLine(ink, Offset(rect.left, rect.top), Offset(rect.left, rect.bottom), strokeWidth = axisStroke())
    val wave = Path()
    for (step in 0..80) {
        val t = step / 80f
        val point = Offset(
            x = rect.left + rect.width * t,
            y = midY - (rect.height / 2 * 0.85f * sin(2 * PI * 2 * t)).toFloat(),
        )
        if (step == 0) wave.moveTo(point.x, point.y) else wave.lineTo(point.x, point.y)
    }
    drawPath(wave, Primary, style = Stroke(width = axisStroke() * 1.6f, cap = StrokeCap.Round))
}
