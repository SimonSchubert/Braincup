package com.inspiredandroid.braincup.ui.components.learn

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import com.inspiredandroid.braincup.learn.LearnVisual
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Dot groups that count themselves and then slide into one pile.
 *
 * The movement is the point: 4 + 3 is not four dots beside three dots, it is four dots and three
 * dots becoming seven, and the animation shows exactly that.
 */
internal fun VisualScope.drawCounters(visual: LearnVisual.Counters) {
    val groups = visual.groups.filter { it > 0 }
    if (groups.isEmpty()) return
    val total = groups.sum()

    // Stage 1: the groups appear apart. Stage 2: they slide together. Stage 3: the total lands.
    val appear = stage(0, 3)
    val slide = if (visual.merge) stage(1, 3) else 0f
    val totalIn = if (visual.merge) stage(2, 3) else 0f

    val perRow = max(5, (total + 1) / 2)
    val radius = minOf(width / (perRow * 3f), height * 0.11f)
    val gap = radius * 2.6f
    val rowY = height * if (visual.merge) 0.44f else 0.5f

    // Where every dot ends up once the groups have merged: one tidy row (or two).
    fun mergedSlot(index: Int): Offset {
        val rows = if (total > perRow) 2 else 1
        val row = if (rows == 1) 0 else index / ((total + 1) / 2)
        val inRow = if (rows == 1) index else index % ((total + 1) / 2)
        val countInRow = if (rows == 1) {
            total
        } else if (row == 0) {
            (total + 1) / 2
        } else {
            total - (total + 1) / 2
        }
        val rowWidth = (countInRow - 1) * gap
        return Offset(
            x = width / 2f - rowWidth / 2f + inRow * gap,
            y = rowY + (row - (rows - 1) / 2f) * gap,
        )
    }

    val groupGap = width * 0.1f
    val groupWidths = groups.map { (it - 1) * gap }
    val totalWidth = groupWidths.sum() + groupGap * (groups.size - 1)
    var cursor = width / 2f - totalWidth / 2f
    var index = 0

    groups.forEachIndexed { groupIndex, count ->
        val startX = cursor
        val color = if (groupIndex % 2 == 0) Accent else Accent2
        repeat(count) { inGroup ->
            val from = Offset(startX + inGroup * gap, rowY)
            val to = mergedSlot(index)
            val eased = slide * slide * (3f - 2f * slide)
            val at = Offset(from.x + (to.x - from.x) * eased, from.y + (to.y - from.y) * eased)
            dot(at, radius, if (slide > 0.9f) Accent else color, alpha = item(index, total).coerceAtLeast(appear))
            index++
        }
        // Each group counts itself while it is still separate.
        label(
            text = count.toString(),
            center = Offset(startX + groupWidths[groupIndex] / 2f, rowY - radius * 2.4f),
            color = color,
            factor = 0.11f,
            alpha = (1f - slide).coerceIn(0f, 1f) * appear,
        )
        cursor += groupWidths[groupIndex] + gap + groupGap

        if (groupIndex < groups.lastIndex) {
            label(
                text = "+",
                center = Offset(cursor - gap - groupGap / 2f, rowY),
                color = ink,
                factor = 0.12f,
                alpha = (1f - slide).coerceIn(0f, 1f) * appear,
            )
        }
    }

    if (visual.merge && visual.reveal) {
        label(
            text = "= $total",
            center = Offset(width / 2f, height * 0.85f),
            color = Accent,
            factor = 0.16f,
            alpha = totalIn,
        )
    }
}

/** Ten-frames: fill the first to ten, then spill the rest into the second. */
internal fun VisualScope.drawTenFrame(visual: LearnVisual.TenFrame) {
    val total = visual.filled + visual.added
    val cell = minOf(width / 12.5f, height / 3.2f)
    val frameWidth = cell * 5
    val left = width / 2f - frameWidth - cell * 0.35f
    val top = height / 2f - cell

    fun frameAt(frameIndex: Int): Offset = Offset(left + frameIndex * (frameWidth + cell * 0.7f), top)

    val frames = if (total > 10) 2 else 1
    repeat(frames) { f ->
        val origin = frameAt(f)
        repeat(2) { row ->
            repeat(5) { col ->
                box(
                    topLeft = Offset(origin.x + col * cell, origin.y + row * cell),
                    size = Size(cell, cell),
                    fill = null,
                    outline = faint,
                )
            }
        }
    }

    repeat(total.coerceAtMost(20)) { i ->
        val f = i / 10
        val within = i % 10
        val origin = frameAt(f)
        val center = Offset(
            origin.x + (within % 5) * cell + cell / 2f,
            origin.y + (within / 5) * cell + cell / 2f,
        )
        // The dots that complete the first ten arrive in the accent colour so the split is visible.
        val fromSecondGroup = i >= visual.filled
        dot(
            center = center,
            radius = cell * 0.32f,
            color = if (fromSecondGroup) Accent2 else Accent,
            alpha = item(i, total),
        )
    }

    if (!visual.reveal) return
    label(
        text = "${visual.filled} + ${visual.added} = $total",
        center = Offset(width / 2f, height * 0.9f),
        color = Accent,
        factor = 0.13f,
        alpha = stage(2, 3),
    )
}

/** A number line, with an optional hop counting on from a start value. */
internal fun VisualScope.drawNumberLine(visual: LearnVisual.NumberLine) {
    val span = (visual.to - visual.from).coerceAtLeast(1)
    val left = width * 0.09f
    val right = width * 0.91f
    val axisY = height * 0.62f
    fun xOf(value: Float): Float = left + (right - left) * (value - visual.from) / span

    line(Offset(left, axisY), Offset(right, axisY), ink, stroke)

    val step = visual.tickStep.coerceAtLeast(1)
    var value = visual.from
    while (value <= visual.to) {
        val x = xOf(value.toFloat())
        val major = (value - visual.from) % (step * 5) == 0 || step > 1
        line(
            Offset(x, axisY - height * (if (major) 0.06f else 0.035f)),
            Offset(x, axisY + height * (if (major) 0.06f else 0.035f)),
            if (major) ink else faint,
            stroke * 0.8f,
        )
        if (major) {
            label(value.toString(), Offset(x, axisY + height * 0.16f), ink, 0.1f, alpha = 1f, bold = false)
        }
        value += step
    }

    val start = visual.start ?: return
    if (visual.jump == 0) {
        dot(Offset(xOf(start.toFloat()), axisY), stroke * 2.2f, Accent)
        label(start.toString(), Offset(xOf(start.toFloat()), axisY - height * 0.2f), Accent, 0.12f)
        return
    }

    // Hops arrive one after another so the learner can count them.
    val perHop = visual.jump / visual.hops
    repeat(visual.hops) { hop ->
        val fromValue = start + perHop * hop
        val toValue = fromValue + perHop
        val t = item(hop, visual.hops)
        if (t <= 0f) return@repeat
        val x0 = xOf(fromValue.toFloat())
        val x1 = xOf(fromValue + (toValue - fromValue) * t)
        val arc = Path().apply {
            moveTo(x0, axisY)
            quadraticTo((x0 + x1) / 2f, axisY - height * 0.34f, x1, axisY)
        }
        path(arc, fill = null, outline = Accent, width = stroke * 1.2f)
        if (t > 0.9f) {
            label(
                text = (if (perHop >= 0) "+" else "") + perHop,
                center = Offset((x0 + x1) / 2f, axisY - height * 0.26f),
                color = Accent,
                factor = 0.1f,
            )
        }
    }
    dot(Offset(xOf(start.toFloat()), axisY), stroke * 2f, ink)
    val landed = start + visual.jump
    if (visual.reveal) {
        dot(Offset(xOf(landed.toFloat()), axisY), stroke * 2.4f, Accent, alpha = progress)
        label(
            text = landed.toString(),
            center = Offset(xOf(landed.toFloat()), axisY - height * 0.2f),
            color = Accent,
            factor = 0.13f,
            alpha = stage(2, 3),
        )
    }
}

/** Base-ten rods and unit cubes: the tens stack up, then the loose ones arrive. */
internal fun VisualScope.drawPlaceValue(visual: LearnVisual.PlaceValue) {
    val cell = minOf(width / (visual.tens * 2.6f + visual.ones.coerceAtMost(9) * 1.5f + 4f), height * 0.14f)
    val rodWidth = cell
    val rodHeight = cell * 10
    val top = height * 0.52f - rodHeight / 2f
    val totalWidth = visual.tens * rodWidth * 1.7f + visual.ones * cell * 1.35f + width * 0.06f
    var x = width / 2f - totalWidth / 2f

    repeat(visual.tens) { i ->
        val alpha = item(i, visual.tens + visual.ones)
        repeat(10) { seg ->
            box(
                topLeft = Offset(x, top + seg * cell),
                size = Size(rodWidth, cell),
                fill = Accent.copy(alpha = 0.4f),
                outline = ink,
                alpha = alpha,
            )
        }
        x += rodWidth * 1.7f
    }
    x += width * 0.06f
    repeat(visual.ones) { i ->
        val alpha = item(visual.tens + i, visual.tens + visual.ones)
        box(
            topLeft = Offset(x, top + rodHeight - cell),
            size = Size(cell, cell),
            fill = Accent2.copy(alpha = 0.45f),
            outline = ink,
            alpha = alpha,
        )
        x += cell * 1.35f
    }

    if (!visual.reveal) return
    label(
        text = "${visual.tens * 10} + ${visual.ones} = ${visual.tens * 10 + visual.ones}",
        center = Offset(width / 2f, height * 0.94f),
        color = Accent,
        factor = 0.12f,
        alpha = stage(2, 3),
    )
}

/** Hundred-squares shaded to a decimal, side by side when two are being compared. */
internal fun VisualScope.drawDecimalGrid(visual: LearnVisual.DecimalGrid) {
    val values = listOfNotNull(visual.value, visual.compare)
    val gridSize = minOf(height * 0.72f, width / (values.size * 1.5f))
    val cell = gridSize / 10f
    val gap = gridSize * 0.4f
    val totalWidth = values.size * gridSize + (values.size - 1) * gap
    var left = width / 2f - totalWidth / 2f

    values.forEachIndexed { gridIndex, value ->
        val top = height * 0.44f - gridSize / 2f
        val shaded = (value * 100).roundToInt().coerceIn(0, 100)
        val color = if (gridIndex == 0) Accent else Accent2
        repeat(100) { i ->
            val row = i / 10
            val col = i % 10
            val on = i < shaded
            val alpha = if (on) item(i, shaded.coerceAtLeast(1)) else 1f
            box(
                topLeft = Offset(left + col * cell, top + row * cell),
                size = Size(cell, cell),
                fill = if (on) color.copy(alpha = 0.75f) else null,
                outline = faint,
                alpha = alpha,
            )
        }
        box(Offset(left, top), Size(gridSize, gridSize), fill = null, outline = ink)
        if (visual.reveal) {
            label(
                text = formatDecimal(value),
                center = Offset(left + gridSize / 2f, top + gridSize + height * 0.12f),
                color = color,
                factor = 0.13f,
            )
        }
        left += gridSize + gap
    }
}

/** Rows and columns of dots, filled row by row, so a times fact becomes something you can count. */
internal fun VisualScope.drawArrayDots(visual: LearnVisual.ArrayDots) {
    val rows = visual.rows.coerceAtLeast(1)
    val cols = visual.cols.coerceAtLeast(1)
    val step = minOf(width * 0.62f / cols, height * 0.6f / rows)
    val gridWidth = step * (cols - 1)
    val gridHeight = step * (rows - 1)
    val left = width * 0.52f - gridWidth / 2f
    val top = height * 0.48f - gridHeight / 2f

    repeat(rows) { r ->
        repeat(cols) { c ->
            dot(
                center = Offset(left + c * step, top + r * step),
                radius = step * 0.26f,
                color = Accent,
                alpha = item(r * cols + c, rows * cols),
            )
        }
    }

    label("$rows rows", Offset(left - step * 1.5f, top + gridHeight / 2f), Accent2, 0.09f, alpha = stage(0, 3))
    label("$cols in each", Offset(left + gridWidth / 2f, top - step * 1.1f), Accent2, 0.09f, alpha = stage(0, 3))
    if (!visual.reveal) return
    label(
        text = "$rows x $cols = ${rows * cols}",
        center = Offset(width / 2f, top + gridHeight + step * 1.4f),
        color = Accent,
        factor = 0.13f,
        alpha = stage(2, 3),
    )
}

/** One fraction bar, or two stacked so the comparison is unmissable. */
internal fun VisualScope.drawFraction(visual: LearnVisual.Fraction) {
    val bars = listOfNotNull(
        visual.numerator to visual.denominator,
        visual.compare,
    )
    val barWidth = width * 0.78f
    val left = width / 2f - barWidth / 2f
    val barHeight = if (bars.size > 1) height * 0.24f else height * 0.34f
    val gap = height * 0.16f
    var top = height * 0.5f - (bars.size * barHeight + (bars.size - 1) * gap) / 2f

    bars.forEachIndexed { barIndex, (numerator, denominator) ->
        val parts = denominator.coerceAtLeast(1)
        val partWidth = barWidth / parts
        val color = if (barIndex == 0) Accent else Accent2
        repeat(parts) { i ->
            val filled = i < numerator
            box(
                topLeft = Offset(left + partWidth * i, top),
                size = Size(partWidth, barHeight),
                fill = if (filled) color.copy(alpha = 0.8f) else null,
                outline = ink,
                alpha = if (filled) item(i, numerator.coerceAtLeast(1)) else 1f,
            )
        }
        label(
            text = "$numerator/$denominator",
            center = Offset(left + barWidth + width * 0.06f, top + barHeight / 2f),
            color = color,
            factor = 0.12f,
        )
        top += barHeight + gap
    }
}

/** Coins counted on one at a time, with the running total climbing beneath them. */
internal fun VisualScope.drawCoins(visual: LearnVisual.Coins) {
    val values = visual.values
    if (values.isEmpty()) return
    val maxValue = values.max().toFloat()
    val baseRadius = minOf(width / (values.size * 2.6f), height * 0.24f)
    var x = width / 2f - (values.size - 1) * baseRadius * 1.25f
    var running = 0

    values.forEachIndexed { index, value ->
        val radius = baseRadius * (0.62f + 0.38f * (value / maxValue))
        val alpha = item(index, values.size)
        val center = Offset(x, height * 0.42f)
        draw.drawCircle(
            color = (if (index % 2 == 0) Accent else Accent2).copy(alpha = 0.35f * alpha),
            radius = radius,
            center = center,
        )
        draw.drawCircle(
            color = ink.copy(alpha = alpha),
            radius = radius,
            center = center,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke),
        )
        label("$value", center, ink, 0.095f, alpha = alpha)
        running += value
        if (alpha > 0.5f && visual.reveal) {
            label(
                text = running.toString() + visual.currency,
                center = Offset(x, height * 0.78f),
                color = Accent,
                factor = 0.1f,
                alpha = (alpha - 0.5f) * 2f,
            )
        }
        x += baseRadius * 2.5f
    }
}

/** A ruler with the object being measured growing along it from zero. */
internal fun VisualScope.drawRuler(visual: LearnVisual.Ruler) {
    val span = visual.span.coerceAtLeast(1)
    val left = width * 0.08f
    val right = width * 0.92f
    val top = height * 0.46f
    val rulerHeight = height * 0.3f
    fun xOf(value: Float) = left + (right - left) * value / span

    box(Offset(left, top), Size(right - left, rulerHeight), fill = null, outline = ink)
    for (i in 0..span) {
        val x = xOf(i.toFloat())
        line(Offset(x, top), Offset(x, top + rulerHeight * 0.42f), ink, stroke * 0.7f)
        label(i.toString(), Offset(x, top + rulerHeight * 0.72f), faint, 0.085f, bold = false)
    }

    // The measured object grows from zero, so the reading is the length, not the end position.
    val shown = visual.length * progress
    box(
        topLeft = Offset(left, top - height * 0.24f),
        size = Size(xOf(shown) - left, height * 0.16f),
        fill = Accent.copy(alpha = 0.75f),
        outline = ink,
    )
    if (!visual.reveal) return
    label(
        text = "${visual.length} ${visual.unit}",
        center = Offset(xOf(visual.length / 2f), top - height * 0.16f),
        color = ink,
        factor = 0.1f,
        alpha = stage(2, 3),
    )
}

/** A clock face with both hands sweeping to the time. */
internal fun VisualScope.drawClock(visual: LearnVisual.Clock) {
    val radius = size.minDimension * 0.4f
    val center = Offset(width / 2f, height / 2f)
    draw.drawCircle(
        color = ink,
        radius = radius,
        center = center,
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke * 1.3f),
    )
    repeat(12) { i ->
        val angle = i * PI / 6.0
        val at = Offset(
            center.x + (radius * 0.82f * sin(angle)).toFloat(),
            center.y - (radius * 0.82f * cos(angle)).toFloat(),
        )
        label(if (i == 0) "12" else i.toString(), at, faint, 0.085f, bold = false)
    }

    val minuteAngle = visual.minute / 60.0 * 2 * PI * progress
    val hourAngle = ((visual.hour % 12) + visual.minute / 60.0) / 12.0 * 2 * PI * progress
    line(
        center,
        Offset(
            center.x + (radius * 0.48f * sin(hourAngle)).toFloat(),
            center.y - (radius * 0.48f * cos(hourAngle)).toFloat(),
        ),
        Accent,
        stroke * 2f,
    )
    line(
        center,
        Offset(
            center.x + (radius * 0.7f * sin(minuteAngle)).toFloat(),
            center.y - (radius * 0.7f * cos(minuteAngle)).toFloat(),
        ),
        Accent2,
        stroke * 1.4f,
    )
    dot(center, stroke * 1.6f, ink)
}

/** Terms of a sequence with the step between them called out. */
internal fun VisualScope.drawSteps(visual: LearnVisual.Steps) {
    val terms = visual.terms
    if (terms.size < 2) return
    val slot = width * 0.86f / terms.size
    val left = width * 0.07f + slot / 2f
    val y = height * 0.56f

    terms.forEachIndexed { index, term ->
        val x = left + slot * index
        val alpha = item(index, terms.size)
        draw.drawCircle(Accent.copy(alpha = 0.18f * alpha), slot * 0.36f, Offset(x, y))
        label(term.toString(), Offset(x, y), ink, 0.12f, alpha = alpha)

        if (index < terms.lastIndex) {
            val next = terms[index + 1]
            val midX = x + slot / 2f
            val arc = Path().apply {
                moveTo(x + slot * 0.36f, y - slot * 0.1f)
                quadraticTo(midX, y - slot * 0.72f, x + slot - slot * 0.36f, y - slot * 0.1f)
            }
            val t = item(index, terms.size)
            path(arc, fill = null, outline = Accent2, width = stroke, alpha = t)
            val stepLabel = if (visual.multiply) {
                "x" + formatDecimal(next.toDouble() / term)
            } else {
                (if (next - term >= 0) "+" else "") + (next - term)
            }
            label(stepLabel, Offset(midX, y - slot * 0.62f), Accent2, 0.095f, alpha = t)
        }
    }
}

/** Tally marks in gates of five, drawn in the order you would actually write them. */
internal fun VisualScope.drawTally(visual: LearnVisual.Tally) {
    val count = visual.count.coerceAtLeast(0)
    val gates = (count + 4) / 5
    val gateWidth = minOf(width * 0.78f / gates.coerceAtLeast(1), width * 0.22f)
    val markHeight = height * 0.4f
    val top = height * 0.28f
    val left = width / 2f - (gates * gateWidth) / 2f

    repeat(count) { i ->
        val gate = i / 5
        val within = i % 5
        val x = left + gate * gateWidth + gateWidth * (0.12f + within.coerceAtMost(4) * 0.16f)
        val alpha = item(i, count)
        if (within == 4) {
            // The fifth mark strikes through the four before it.
            line(
                Offset(left + gate * gateWidth + gateWidth * 0.06f, top + markHeight),
                Offset(left + gate * gateWidth + gateWidth * 0.66f, top),
                Accent,
                stroke * 1.2f,
                alpha = alpha,
            )
        } else {
            line(Offset(x, top), Offset(x, top + markHeight), ink, stroke * 1.2f, alpha = alpha)
        }
    }

    if (!visual.reveal) return
    label(
        text = count.toString(),
        center = Offset(width / 2f, height * 0.86f),
        color = Accent,
        factor = 0.14f,
        alpha = stage(2, 3),
    )
}

/** Trim a double to the shortest sensible label: 2.0 -> "2", 0.25 -> "0.25". */
internal fun formatDecimal(value: Double): String {
    val rounded = (value * 100).roundToInt() / 100.0
    if (abs(rounded - rounded.roundToInt()) < 0.001) return rounded.roundToInt().toString()
    val text = rounded.toString()
    return if (text.endsWith("0")) text.dropLast(1) else text
}
