package com.inspiredandroid.braincup.ui.components.learn

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.inspiredandroid.braincup.learn.LearnVisual
import kotlin.math.exp
import kotlin.math.roundToInt

/** Bars growing to their values, with the mean sliding in afterwards when it is the point. */
internal fun VisualScope.drawBarChart(visual: LearnVisual.BarChart) {
    val values = visual.values
    if (values.isEmpty()) return
    val maxValue = values.max().coerceAtLeast(1)
    val baseline = height * 0.78f
    val chartTop = height * 0.14f
    val chartHeight = baseline - chartTop
    val slot = width * 0.84f / values.size
    val barWidth = slot * 0.58f

    if (visual.gridStep > 0) {
        var g = visual.gridStep
        while (g <= maxValue) {
            val y = baseline - chartHeight * g / maxValue
            line(Offset(width * 0.06f, y), Offset(width * 0.94f, y), faint, stroke * 0.5f)
            label(g.toString(), Offset(width * 0.035f, y), faint, 0.075f, bold = false)
            g += visual.gridStep
        }
    }

    values.forEachIndexed { index, value ->
        val t = item(index, values.size)
        val barHeight = chartHeight * value / maxValue * t
        val x = width * 0.08f + slot * index + (slot - barWidth) / 2f
        val highlighted = index in visual.highlight
        draw.drawRect(
            color = if (highlighted) Accent2 else Accent,
            topLeft = Offset(x, baseline - barHeight),
            size = Size(barWidth, barHeight),
        )
        if (revealing(visual.reveal)) {
            label(
                text = value.toString(),
                center = Offset(x + barWidth / 2f, baseline - barHeight - height * 0.06f),
                color = ink,
                factor = 0.085f,
                alpha = t,
            )
        }
        visual.labels.getOrNull(index)?.let {
            label(it, Offset(x + barWidth / 2f, baseline + height * 0.08f), faint, 0.075f, bold = false)
        }
    }

    line(Offset(width * 0.06f, baseline), Offset(width * 0.94f, baseline), ink, stroke)

    if (visual.showMean) {
        val mean = values.sum().toFloat() / values.size
        val y = baseline - chartHeight * mean / maxValue
        val reveal = stage(2, 3)
        line(
            Offset(width * 0.06f, y),
            Offset(width * 0.06f + (width * 0.88f) * reveal, y),
            Accent2,
            stroke * 1.3f,
            dashed = true,
        )
        label(
            text = "mean " + formatDecimal(mean.toDouble()),
            center = Offset(width * 0.78f, y - height * 0.07f),
            color = Accent2,
            factor = 0.09f,
            alpha = reveal,
        )
    }
}

/** Pie slices sweeping out in turn, each labelled with its share of the whole. */
internal fun VisualScope.drawPieChart(visual: LearnVisual.PieChart) {
    val shares = visual.shares.filter { it > 0 }
    if (shares.isEmpty()) return
    val total = shares.sum().toFloat()
    val diameter = size.minDimension * 0.68f
    val topLeft = Offset(width / 2f - diameter / 2f, height * 0.46f - diameter / 2f)
    val center = Offset(width / 2f, height * 0.46f)
    val palette = listOf(Accent, Accent2, ink.copy(alpha = 0.45f), Accent.copy(alpha = 0.5f))

    var start = -90f
    shares.forEachIndexed { index, share ->
        val sweep = 360f * share / total
        val t = item(index, shares.size)
        draw.drawArc(
            color = palette[index % palette.size],
            startAngle = start,
            sweepAngle = sweep * t,
            useCenter = true,
            topLeft = topLeft,
            size = Size(diameter, diameter),
        )
        if (t > 0.85f && revealing(visual.reveal)) {
            val mid = (start + sweep / 2f) * kotlin.math.PI.toFloat() / 180f
            val at = Offset(
                center.x + (diameter * 0.3f * kotlin.math.cos(mid)),
                center.y + (diameter * 0.3f * kotlin.math.sin(mid)),
            )
            val percent = (share / total * 100).roundToInt()
            label("$percent%", at, ink, 0.085f)
        }
        start += sweep
    }
    draw.drawCircle(ink, diameter / 2f, center, style = Stroke(width = stroke))

    visual.labels.forEachIndexed { index, text ->
        label(
            text = text,
            center = Offset(width / 2f, height * (0.9f + index * 0.075f)),
            color = palette[index % palette.size],
            factor = 0.08f,
            alpha = stage(2, 3),
        )
    }
}

/** Rows of symbols — halves included — with the key that says what one symbol is worth. */
internal fun VisualScope.drawPictogram(visual: LearnVisual.Pictogram) {
    val rows = visual.rows
    if (rows.isEmpty()) return
    val step = width * 0.15f
    val radius = step * 0.3f
    val rowGap = height * 0.68f / rows.size

    rows.forEachIndexed { rowIndex, count ->
        val y = height * 0.18f + rowGap * rowIndex
        var drawn = 0f
        var symbolIndex = 0
        while (drawn + 1f <= count) {
            dot(
                center = Offset(width * 0.18f + step * symbolIndex, y),
                radius = radius,
                color = Accent,
                alpha = item(rowIndex * 5 + symbolIndex, rows.size * 5),
            )
            drawn += 1f
            symbolIndex++
        }
        if (count - drawn > 0.4f) {
            // A half symbol is half the value, and looks it.
            draw.drawArc(
                color = Accent,
                startAngle = 90f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(width * 0.18f + step * symbolIndex - radius, y - radius),
                size = Size(radius * 2, radius * 2),
            )
        }
        if (revealing(visual.reveal)) {
            label(
                text = "= ${(count * visual.unitValue).roundToInt()}",
                center = Offset(width * 0.86f, y),
                color = Accent2,
                factor = 0.09f,
                alpha = item(rowIndex, rows.size),
            )
        }
    }

    label(
        text = "1 symbol = ${visual.unitValue}",
        center = Offset(width / 2f, height * 0.94f),
        color = ink,
        factor = 0.085f,
        bold = false,
    )
}

/** The bell curve with its middle band shading in. */
internal fun VisualScope.drawNormalCurve(visual: LearnVisual.NormalCurve) {
    val rect = frame(left = 0.1f, top = 0.16f, right = 0.9f, bottom = 0.78f)
    fun pointAt(t: Float): Offset {
        val z = -3f + 6f * t
        val h = exp(-0.5 * z * z).toFloat()
        return Offset(rect.left + rect.width * t, rect.bottom - rect.height * h)
    }

    val band = visual.shadeSd.coerceIn(1, 3)
    val from = (3f - band) / 6f
    val to = (3f + band) / 6f
    val shaded = Path().apply {
        moveTo(rect.left + rect.width * from, rect.bottom)
        var t = from
        while (t <= from + (to - from) * progress) {
            val p = pointAt(t)
            lineTo(p.x, p.y)
            t += 0.01f
        }
        lineTo(rect.left + rect.width * (from + (to - from) * progress), rect.bottom)
        close()
    }
    path(shaded, Accent.copy(alpha = 0.3f), null)

    val curve = Path()
    for (i in 0..80) {
        val p = pointAt(i / 80f)
        if (i == 0) curve.moveTo(p.x, p.y) else curve.lineTo(p.x, p.y)
    }
    path(curve, null, Accent, stroke * 1.5f)

    line(Offset(rect.left, rect.bottom), Offset(rect.right, rect.bottom), ink, stroke)
    line(Offset(rect.left + rect.width / 2f, rect.bottom), pointAt(0.5f), Accent2, stroke, dashed = true)
    label("mean", Offset(rect.left + rect.width / 2f, rect.bottom + height * 0.09f), Accent2, 0.08f, bold = false)

    listOf(-band, band).forEach { sd ->
        val x = rect.left + rect.width * (3f + sd) / 6f
        line(Offset(x, rect.bottom), Offset(x, rect.bottom - height * 0.04f), ink, stroke)
        label(
            text = (if (sd > 0) "+" else "") + sd + " sd",
            center = Offset(x, rect.bottom + height * 0.09f),
            color = faint,
            factor = 0.075f,
            bold = false,
        )
    }

    visual.percent?.let {
        label(it, Offset(rect.left + rect.width / 2f, rect.bottom - rect.height * 0.4f), ink, 0.12f, alpha = stage(2, 3))
    }
}

/** Two overlapping sets: the picture behind every conditional probability. */
internal fun VisualScope.drawSetDiagram(visual: LearnVisual.SetDiagram) {
    val radius = size.minDimension * 0.3f
    val y = height * 0.46f
    val leftCenter = Offset(width / 2f - radius * 0.55f, y)
    val rightCenter = Offset(width / 2f + radius * 0.55f, y)

    draw.drawCircle(Accent.copy(alpha = 0.25f * progress), radius, leftCenter)
    draw.drawCircle(Accent2.copy(alpha = 0.25f * progress), radius, rightCenter)
    draw.drawCircle(ink, radius, leftCenter, style = Stroke(width = stroke))
    draw.drawCircle(ink, radius, rightCenter, style = Stroke(width = stroke))

    label(visual.aOnly.toString(), Offset(leftCenter.x - radius * 0.45f, y), ink, 0.11f, alpha = stage(0, 3))
    label(visual.both.toString(), Offset(width / 2f, y), Accent, 0.12f, alpha = stage(1, 3))
    label(visual.bOnly.toString(), Offset(rightCenter.x + radius * 0.45f, y), ink, 0.11f, alpha = stage(0, 3))

    label(visual.aLabel, Offset(leftCenter.x - radius * 0.4f, y - radius * 1.2f), Accent, 0.085f)
    label(visual.bLabel, Offset(rightCenter.x + radius * 0.4f, y - radius * 1.2f), Accent2, 0.085f)
}
