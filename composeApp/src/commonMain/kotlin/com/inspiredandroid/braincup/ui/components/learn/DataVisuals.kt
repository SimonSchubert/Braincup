package com.inspiredandroid.braincup.ui.components.learn

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
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
        box(
            topLeft = Offset(x, baseline - barHeight),
            size = Size(barWidth, barHeight),
            fill = if (highlighted) Accent2 else Accent,
            outline = null,
        )
        if (visual.reveal) {
            label(
                text = value.toString(),
                center = Offset(x + barWidth / 2f, baseline - barHeight - height * 0.06f),
                color = if (highlighted) Accent2 else Accent,
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
        val reveal = revealBeat
        line(
            Offset(width * 0.06f, y),
            Offset(width * 0.06f + (width * 0.88f) * reveal, y),
            Accent2,
            stroke * 1.3f,
            dashed = true,
        )
        // Parked over the shortest bar and laid on the panel colour. A fixed fraction of the width
        // put the reading straight on top of whichever bar happened to end near the mean, and its
        // value label sits exactly in the band the mean line runs through.
        val shortest = values.indices.minByOrNull { values[it] } ?: 0
        chipLabel(
            text = strings.meanValueTemplate.fillIn(formatDecimal(mean.toDouble())),
            center = Offset(width * 0.08f + slot * shortest + slot / 2f, y - height * 0.09f),
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
    val center = Offset(width / 2f, height * 0.46f)
    val palette = listOf(Accent, Accent2, ink.copy(alpha = 0.45f), Accent.copy(alpha = 0.5f))

    var start = -90f
    shares.forEachIndexed { index, share ->
        val sweep = 360f * share / total
        val t = item(index, shares.size)
        arc(
            center = center,
            radius = diameter / 2f,
            startAngle = start,
            sweepAngle = sweep * t,
            fill = palette[index % palette.size],
            outline = null,
        )
        if (t > 0.85f && visual.reveal) {
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
    circle(center, diameter / 2f, outline = ink)

    visual.labels.forEachIndexed { index, text ->
        label(
            text = text,
            center = Offset(width / 2f, height * (0.9f + index * 0.075f)),
            color = palette[index % palette.size],
            factor = 0.08f,
            alpha = revealBeat,
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
            arc(
                center = Offset(width * 0.18f + step * symbolIndex, y),
                radius = radius,
                startAngle = 90f,
                sweepAngle = 180f,
                fill = Accent,
                outline = null,
            )
        }
        if (visual.reveal) {
            label(
                text = "= ${(count * visual.unitValue).roundToInt()}",
                center = Offset(width * 0.86f, y),
                color = Accent,
                factor = 0.09f,
                alpha = item(rowIndex, rows.size),
            )
        }
    }

    label(
        text = strings.symbolKeyTemplate.fillIn(visual.unitValue),
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
    label(strings.mean, Offset(rect.left + rect.width / 2f, rect.bottom + height * 0.09f), Accent2, 0.08f, bold = false)

    listOf(-band, band).forEach { sd ->
        val x = rect.left + rect.width * (3f + sd) / 6f
        line(Offset(x, rect.bottom), Offset(x, rect.bottom - height * 0.04f), ink, stroke)
        label(
            text = strings.standardDeviationsTemplate.fillIn((if (sd > 0) "+" else "") + sd),
            center = Offset(x, rect.bottom + height * 0.09f),
            color = faint,
            factor = 0.075f,
            bold = false,
        )
    }

    visual.percent?.let {
        label(it, Offset(rect.left + rect.width / 2f, rect.bottom - rect.height * 0.4f), ink, 0.12f, alpha = revealBeat)
    }
}

/** Two overlapping sets: the picture behind every conditional probability. */
internal fun VisualScope.drawSetDiagram(visual: LearnVisual.SetDiagram) {
    val radius = size.minDimension * 0.3f
    val y = height * 0.46f
    val leftCenter = Offset(width / 2f - radius * 0.55f, y)
    val rightCenter = Offset(width / 2f + radius * 0.55f, y)

    circle(leftCenter, radius, fill = Accent.copy(alpha = 0.25f * progress), outline = null)
    circle(rightCenter, radius, fill = Accent2.copy(alpha = 0.25f * progress), outline = null)
    circle(leftCenter, radius, outline = ink)
    circle(rightCenter, radius, outline = ink)

    // Each count takes the colour of the ring it sits in. The overlap belongs to both rings, so it
    // takes the ink instead of picking a side.
    label(visual.aOnly.toString(), Offset(leftCenter.x - radius * 0.45f, y), Accent, 0.11f, alpha = stage(0, 3))
    label(visual.both.toString(), Offset(width / 2f, y), ink, 0.12f, alpha = stage(1, 3))
    label(visual.bOnly.toString(), Offset(rightCenter.x + radius * 0.45f, y), Accent2, 0.11f, alpha = stage(0, 3))

    label(visual.aLabel, Offset(leftCenter.x - radius * 0.4f, y - radius * 1.2f), Accent, 0.085f)
    label(visual.bLabel, Offset(rightCenter.x + radius * 0.4f, y - radius * 1.2f), Accent2, 0.085f)
}
