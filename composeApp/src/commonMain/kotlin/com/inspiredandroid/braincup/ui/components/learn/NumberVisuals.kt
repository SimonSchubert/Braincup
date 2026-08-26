package com.inspiredandroid.braincup.ui.components.learn

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
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

    if (visual.merge && revealing(visual.reveal)) {
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
    val frames = if (total > 10) 2 else 1
    // A lone frame is centred on its own rather than sitting where the left one of a pair would.
    val left = if (frames == 1) width / 2f - frameWidth / 2f else width / 2f - frameWidth - cell * 0.35f
    val top = height / 2f - cell

    fun frameAt(frameIndex: Int): Offset = Offset(left + frameIndex * (frameWidth + cell * 0.7f), top)

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

    // Nothing was added, so there is no sum to state: a frame filled to seven captioning itself
    // "7 + 0 = 7" reads as a puzzle of its own rather than as the seven dots on show.
    if (visual.added == 0 || !revealing(visual.reveal)) return
    label(
        text = "${visual.filled} + ${visual.added} = $total",
        center = Offset(width / 2f, height * 0.9f),
        color = Accent,
        factor = 0.13f,
        alpha = stage(2, 3),
    )
}

/** Font size of the in-between tick numbers, relative to the canvas, and the gap they need. */
private const val MinorLabelFactor = 0.085f
private const val MinorLabelGap = 1.35f

/** A number line, with an optional hop counting on from a start value. */
internal fun VisualScope.drawNumberLine(visual: LearnVisual.NumberLine) {
    val span = (visual.to - visual.from).coerceAtLeast(1)
    val left = width * 0.09f
    val right = width * 0.91f
    val axisY = height * 0.62f
    fun xOf(value: Float): Float = left + (right - left) * (value - visual.from) / span

    line(Offset(left, axisY), Offset(right, axisY), ink, stroke)

    val step = visual.tickStep.coerceAtLeast(1)
    val ticks = (visual.from..visual.to step step).toList()

    // Every tick is numbered when the numbers fit side by side. A step that asks the learner to
    // land on a value is only answerable if they can read that value off the line, and the every
    // fifth tick rule leaves the four ticks in between blank. Crowded lines fall back to it.
    val minorStyle = labelStyle(ink, MinorLabelFactor, bold = false)
    val widestMinor = ticks.maxOfOrNull { measure(it.toString(), minorStyle).size.width } ?: 0
    val spacing = if (ticks.size > 1) (right - left) / (ticks.size - 1) else right - left
    val labelEveryTick = widestMinor * MinorLabelGap <= spacing

    // The two values the step is about are called out on the axis itself rather than floated above
    // it: the number counted on from, and the one landed on. Now that every tick is numbered, a
    // second copy of the total hovering over the last hop only collides with the hop labels.
    val start = visual.start
    // A two-phase figure shows its second hop on the second beat, so a step that teaches a move
    // and its opposite demonstrates both without needing two figures.
    val jump = if (phase == 1) visual.thenJump ?: visual.jump else visual.jump
    // Authored hops of different sizes replace the even split, so a figure can bridge through ten
    // the way the lesson teaches it: 15 - 8 is -5 down to ten and then -3.
    val hopSteps = if (visual.hopSteps.isNotEmpty() && phase == 0) {
        visual.hopSteps
    } else {
        val count = visual.hops.coerceAtLeast(1)
        List(count) { jump / count }
    }
    val travel = hopSteps.sum()
    val landed = if (start != null && revealing(visual.reveal)) start + travel else null

    // Whatever the learner just put forward is called out on the scale itself: in the accent when
    // it is right, in the error colour when it is not, because seeing where the number they picked
    // actually sits is the correction.
    val marked = answer?.value?.takeIf { it in visual.from..visual.to }

    ticks.forEach { value ->
        val x = xOf(value.toFloat())
        val isLanding = value == landed && value != start
        val isMarked = value == marked && value != start
        val accented = isMarked || value == start || value == landed
        val accentColor = if (isMarked) resultColor else Accent
        val major = (value - visual.from) % (step * 5) == 0 || step > 1
        val tall = major || accented
        line(
            Offset(x, axisY - height * (if (tall) 0.06f else 0.035f)),
            Offset(x, axisY + height * (if (tall) 0.06f else 0.035f)),
            when {
                accented -> accentColor
                major -> ink
                else -> faint
            },
            stroke * 0.8f,
        )
        when {
            // The total arrives with the last hop, so its number fades in on the same beat.
            accented -> label(
                text = value.toString(),
                center = Offset(x, axisY + height * 0.16f),
                color = accentColor,
                factor = 0.11f,
                alpha = if (isLanding) stage(2, 3) else 1f,
            )

            major -> label(value.toString(), Offset(x, axisY + height * 0.16f), ink, 0.1f, bold = false)
            labelEveryTick -> label(
                text = value.toString(),
                center = Offset(x, axisY + height * 0.15f),
                color = ink.copy(alpha = 0.7f),
                factor = MinorLabelFactor,
                bold = false,
            )
        }
    }

    // The learner's own marker goes on last on every path out of here, so an arc never lands on
    // top of the value it is pointing at.
    fun markAnswer() {
        if (marked != null && marked != start) {
            dot(Offset(xOf(marked.toFloat()), axisY), stroke * 2.2f, resultColor)
        }
    }

    if (start == null) {
        markAnswer()
        return
    }
    if (travel == 0) {
        dot(Offset(xOf(start.toFloat()), axisY), stroke * 2.2f, Accent)
        markAnswer()
        return
    }

    // Hops arrive one after another so the learner can count them.
    var hopFrom = start
    hopSteps.forEachIndexed { index, perHop ->
        val fromValue = hopFrom
        hopFrom += perHop
        val t = item(index, hopSteps.size)
        if (t <= 0f) return@forEachIndexed
        val x0 = xOf(fromValue.toFloat())
        val x1 = xOf(fromValue + perHop * t)
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
    if (landed != null) {
        dot(Offset(xOf(landed.toFloat()), axisY), stroke * 2.4f, Accent, alpha = progress)
    }
    markAnswer()
}

/**
 * An inequality as the set of numbers that satisfy it: the axis, a marker on the boundary, and a
 * ray sweeping out over everything that works.
 *
 * The boundary is drawn hollow for a strict inequality and filled for "or equal", because that
 * ring is the entire visible difference between `x > 3` and `x >= 3`.
 */
internal fun VisualScope.drawInequality(visual: LearnVisual.Inequality) {
    val span = (visual.to - visual.from).coerceAtLeast(1)
    val left = width * 0.09f
    val right = width * 0.91f
    val axisY = height * 0.55f
    fun xOf(value: Float): Float = left + (right - left) * (value - visual.from) / span

    line(Offset(left, axisY), Offset(right, axisY), ink, stroke)

    for (value in visual.from..visual.to) {
        val x = xOf(value.toFloat())
        line(Offset(x, axisY - height * 0.05f), Offset(x, axisY + height * 0.05f), faint, stroke * 0.8f)
        label(value.toString(), Offset(x, axisY + height * 0.17f), ink, 0.085f, bold = false)
    }

    // The ray sweeps out from the boundary so the shaded side is read as a consequence of it.
    val boundaryX = xOf(visual.value.toFloat())
    val endX = if (visual.greater) right else left
    val sweep = stage(1, 2)
    if (sweep > 0f) {
        val tipX = boundaryX + (endX - boundaryX) * sweep
        line(Offset(boundaryX, axisY), Offset(tipX, axisY), Accent, stroke * 2.4f)
        val head = stroke * 3f
        val direction = if (visual.greater) -1f else 1f
        line(Offset(tipX, axisY), Offset(tipX + head * direction, axisY - head), Accent, stroke * 1.6f)
        line(Offset(tipX, axisY), Offset(tipX + head * direction, axisY + head), Accent, stroke * 1.6f)
    }

    val radius = stroke * 2.4f
    if (visual.orEqual) {
        dot(Offset(boundaryX, axisY), radius, Accent)
    } else {
        // A ring, not a disc over a guessed background: the figure has no background to match.
        draw.drawCircle(ink, radius, Offset(boundaryX, axisY), style = Stroke(width = stroke * 1.2f))
    }

    if (revealing(visual.reveal)) {
        val sign = when {
            visual.greater && visual.orEqual -> "\u2265"
            visual.greater -> ">"
            visual.orEqual -> "\u2264"
            else -> "<"
        }
        label(
            text = "x $sign ${visual.value}",
            center = Offset(width / 2f, height * 0.2f),
            color = resultColor,
            factor = 0.13f,
            alpha = stage(1, 2),
        )
    }
}

/** Base-ten rods and unit cubes: the tens stack up, then the loose ones arrive. */
/**
 * Base-ten blocks: rods of ten, loose ones, and a second number beside the first when the step is
 * about two of them.
 *
 * Everything is sized from the room actually available rather than from a fixed cell, because nine
 * rods beside nine ones has to fit the same panel three beside seven does, and a rod that overruns
 * the figure teaches nothing. The bottom strip is kept clear for the labels, so nothing is ever
 * captioned across the blocks.
 */
internal fun VisualScope.drawPlaceValue(visual: LearnVisual.PlaceValue) {
    val second = visual.plus ?: visual.compare
    val numbers = listOfNotNull(visual.tens to visual.ones, second)

    // Width in cells, so one cell size can satisfy both directions at once.
    fun widthInCells(number: Pair<Int, Int>): Float {
        val rods = if (number.first > 0) (number.first - 1) * RodStride + 1f else 0f
        val ones = if (number.second > 0) (number.second - 1) * OneStride + 1f else 0f
        return rods + (if (number.first > 0 && number.second > 0) RodsToOnesGap else 0f) + ones
    }

    val labelBand = height * 0.24f
    val blocksBottom = height - labelBand
    val gaps = NumberGap * (numbers.size - 1)
    val cell = minOf(width * 0.94f / (numbers.sumOf { widthInCells(it).toDouble() }.toFloat() + gaps), blocksBottom / 10f)
    val rodHeight = cell * 10f

    val widths = numbers.map { widthInCells(it) * cell }
    var cursor = width / 2f - (widths.sum() + gaps * cell) / 2f
    val lefts = widths.map { numberWidth ->
        cursor.also { cursor += numberWidth + NumberGap * cell }
    }

    val blocks = numbers.sumOf { it.first + it.second }
    var placed = 0

    numbers.forEachIndexed { index, number ->
        val (tens, ones) = number
        // One number is coloured by place, so its rods and its loose ones are the two halves of
        // "37 = 30 + 7" and the formula can be read straight off the blocks. Two numbers are
        // coloured by number instead, because then the colour has to say which pile is meant.
        val color = if (index == 0) Accent else Accent2
        val onesColor = if (numbers.size == 1) Accent2 else color
        val left = lefts[index]
        repeat(tens) { rod ->
            val alpha = item(placed++, blocks)
            repeat(10) { segment ->
                box(
                    topLeft = Offset(left + rod * RodStride * cell, blocksBottom - rodHeight + segment * cell),
                    size = Size(cell, cell),
                    fill = color.copy(alpha = 0.4f),
                    outline = ink,
                    alpha = alpha,
                )
            }
        }
        val onesLeft = left + (if (tens > 0) (tens - 1) * RodStride * cell + cell + RodsToOnesGap * cell else 0f)
        repeat(ones) { one ->
            box(
                topLeft = Offset(onesLeft + one * OneStride * cell, blocksBottom - cell),
                size = Size(cell, cell),
                fill = onesColor.copy(alpha = 0.55f),
                outline = ink,
                alpha = item(placed++, blocks),
            )
        }
        // With two numbers on the panel each says what it is, right under its own blocks, so the
        // options can name them.
        if (numbers.size > 1) {
            label(
                text = (tens * 10 + ones).toString(),
                center = Offset(left + widths[index] / 2f, blocksBottom + labelBand * 0.55f),
                color = color,
                factor = 0.12f,
                alpha = stage(2, 3),
            )
        }
    }

    // A lone number is never captioned: the step's own formula already says what it adds up to,
    // and the rods and the ones carry its two halves in their own colours.
    if (second == null) return

    // The sign sits between the two piles. A plus is part of what the step is asking, so it is
    // always there; a comparison sign is the answer, so it waits for the figure to be allowed it.
    val sign = when {
        visual.plus != null -> "+"
        !revealing(visual.reveal) -> return
        else -> {
            val first = visual.tens * 10 + visual.ones
            val other = second.first * 10 + second.second
            if (first > other) {
                ">"
            } else if (first < other) {
                "<"
            } else {
                "="
            }
        }
    }
    label(
        text = sign,
        center = Offset(lefts[0] + widths[0] + NumberGap * cell / 2f, blocksBottom - rodHeight / 2f),
        color = ink,
        factor = 0.2f,
    )
}

/** Cells of horizontal advance between one rod and the next, between ones, and between the two. */
private const val RodStride = 1.7f
private const val OneStride = 1.35f
private const val RodsToOnesGap = 0.8f

/** Cells of clear space between two numbers laid side by side, where their sign goes. */
private const val NumberGap = 2.4f

/**
 * Hundred-squares shaded to a decimal: one on its own, two side by side to compare, or the two of
 * a sum with their total arriving beside them.
 *
 * A square names itself only when the step allows it. Naming is the whole answer to "write this as
 * a decimal", and on a comparison it hands over the reasoning the learner was asked to do; the
 * shading is what the figure is for, and that is always on show.
 */
internal fun VisualScope.drawDecimalGrid(visual: LearnVisual.DecimalGrid) {
    val second = visual.plus ?: visual.compare
    val sum = visual.plus?.takeIf { revealing(visual.reveal) }?.let { visual.value + it }
    val values = listOfNotNull(visual.value, second, sum)
    // A percentage of an amount needs a band above the square for the amount it is taken of, so
    // the square gives some height back. On its own it keeps the room it always had.
    val ofAmount = visual.of?.takeIf { second == null }
    val sharing = ofAmount != null
    val gridSize = minOf(if (sharing) height * 0.56f else height * 0.72f, width / (values.size * 1.5f))
    val cell = gridSize / 10f
    val gap = gridSize * 0.4f
    val totalWidth = values.size * gridSize + (values.size - 1) * gap
    var left = width / 2f - totalWidth / 2f
    val top = (if (sharing) height * 0.52f else height * 0.44f) - gridSize / 2f
    val named = revealing(visual.reveal)

    values.forEachIndexed { gridIndex, value ->
        val shaded = (value * 100).roundToInt().coerceIn(0, 100)
        // The total is shaded in both colours, so the tenths that arrived second stay recognisable
        // inside it and the sum reads as the two parts pushed together.
        val carried = if (gridIndex == 2) (visual.value * 100).roundToInt().coerceIn(0, 100) else shaded
        val color = if (gridIndex == 1) Accent2 else Accent
        repeat(100) { i ->
            val row = i / 10
            val col = i % 10
            val on = i < shaded
            val alpha = if (on) item(i, shaded.coerceAtLeast(1)) else 1f
            box(
                topLeft = Offset(left + col * cell, top + row * cell),
                size = Size(cell, cell),
                fill = if (on) (if (i < carried) color else Accent2).copy(alpha = 0.75f) else null,
                outline = faint,
                alpha = alpha,
            )
        }
        box(Offset(left, top), Size(gridSize, gridSize), fill = null, outline = ink)
        // What the square is a percentage of is part of the question, so it stands over the grid
        // whether or not the figure may give the answer away. The part it works out to waits.
        if (ofAmount != null) {
            label(
                text = "${formatDecimal(value * 100)}% of $ofAmount",
                center = Offset(left + gridSize / 2f, top - height * 0.13f),
                color = ink,
                factor = 0.12f,
            )
        }
        if (named) {
            label(
                text = when {
                    ofAmount != null -> "= " + formatDecimal(value * ofAmount)
                    // The step that says a percentage and a decimal are one number has to show
                    // both of them, or it is only ever showing one of the two.
                    visual.percent -> formatDecimal(value * 100) + "% = " + formatDecimal(value)
                    else -> formatDecimal(value)
                },
                center = Offset(left + gridSize / 2f, top + gridSize + height * 0.12f),
                color = color,
                factor = 0.13f,
                alpha = if (sharing) stage(2, 3) else 1f,
            )
        }
        // The sign sits in the gap the square just left behind it. A plus is part of what the step
        // is asking, so it is always there; a comparison sign is the answer, so it waits for the
        // figure to be allowed it, exactly as the base-ten blocks do.
        val sign = when {
            gridIndex == 0 -> null
            visual.plus != null -> if (gridIndex == 1) "+" else "="
            !revealing(visual.reveal) -> null
            visual.value > value -> ">"
            visual.value < value -> "<"
            else -> "="
        }
        if (sign != null) {
            label(
                text = sign,
                center = Offset(left - gap / 2f, top + gridSize / 2f),
                color = ink,
                factor = 0.16f,
            )
        }
        left += gridSize + gap
    }
}

/**
 * Rows and columns of dots, filled row by row, so a times fact becomes something you can count.
 *
 * A split array is drawn as two blocks with a lane between them, in the two group colours, which
 * is the whole of "break it into facts you already know". Leftover dots stand apart to the right,
 * because a remainder is precisely what would not go into the rows.
 *
 * The array never writes its own sum underneath. Every step that shows one gives that sum as a
 * formula in the card directly below the figure, so a caption only says the same line twice. What
 * the picture is for is the situation - this many rows, this many in each - which is the half the
 * formula cannot show.
 */
internal fun VisualScope.drawArrayDots(visual: LearnVisual.ArrayDots) {
    val rows = visual.rows.coerceAtLeast(1)
    val cols = visual.cols.coerceAtLeast(1)
    val leftover = visual.leftover.coerceAtLeast(0)
    // A split that takes every row or none of them is not a split, and drawing the lane anyway
    // would promise a second block that never comes.
    val split = visual.split?.takeIf { it in 1 until rows }
    val lane = if (split != null) 0.6f else 0f

    // The labels are measured before anything is placed, because the row count sits in a gutter of
    // its own width: the dots are sized to the room that leaves, not to the whole panel.
    val style = labelStyle(ink, RowLabelFactor)
    val rowTexts = if (split != null) {
        listOf("$split rows", "${rows - split} more")
    } else {
        listOf("$rows rows")
    }
    val gutter = rowTexts.maxOf { measure(it, style).size.width }.toFloat()
    val colText = "$cols in each"
    val colHeight = measure(colText, style).size.height.toFloat()

    // Sizing counts the strides between dot centres plus everything that hangs off the edges: the
    // gap across to the gutter, a dot's radius, and the two columns a remainder stands in.
    val trailing = if (leftover > 0) LeftoverStride + 1f + DotFactor else DotFactor
    val across = GutterStride + (cols - 1) + trailing
    val down = ColLabelStride + DotFactor + (rows - 1 + lane)
    val step = minOf(
        (width * 0.96f - gutter) / across,
        (height * 0.96f - colHeight / 2f) / down,
    ).coerceAtLeast(1f)
    val gridWidth = step * (cols - 1)
    val gridHeight = step * (rows - 1 + lane)
    // Gutter, dots and remainder are then centred as one block. Centring the dots by themselves and
    // letting the row count hang off the left is what leaves a figure sitting off to one side.
    val left = (width - gutter - step * across) / 2f + gutter + step * GutterStride
    val top = (height - colHeight / 2f - step * down) / 2f + colHeight / 2f + step * ColLabelStride
    fun rowY(r: Int): Float = top + step * (r + if (split != null && r >= split) lane else 0f)

    repeat(rows) { r ->
        repeat(cols) { c ->
            dot(
                center = Offset(left + c * step, rowY(r)),
                radius = step * DotFactor,
                color = if (split != null && r >= split) Accent2 else Accent,
                alpha = item(r * cols + c, rows * cols + leftover),
            )
        }
    }
    repeat(leftover) { i ->
        dot(
            center = Offset(left + gridWidth + step * (LeftoverStride + i % 2), rowY(i / 2)),
            radius = step * DotFactor,
            color = Accent2,
            alpha = item(rows * cols + i, rows * cols + leftover),
        )
    }

    // The row counts share the right edge of the gutter instead of each centring on its own width,
    // so a split array's two labels line up under one another.
    fun rowLabel(text: String, y: Float, color: Color) {
        val measured = measure(text, style)
        val center = Offset(left - step * GutterStride - measured.size.width / 2f, y)
        label(text, center, color, RowLabelFactor, alpha = stage(0, 3))
    }
    if (split != null) {
        rowLabel(rowTexts[0], (rowY(0) + rowY(split - 1)) / 2f, Accent)
        rowLabel(rowTexts[1], (rowY(split) + rowY(rows - 1)) / 2f, Accent2)
    } else {
        rowLabel(rowTexts[0], top + gridHeight / 2f, Accent2)
    }
    label(colText, Offset(left + gridWidth / 2f, top - step * ColLabelStride), Accent2, RowLabelFactor, alpha = stage(0, 3))
}

/** Font size of an array's row and column counts, relative to the canvas. */
private const val RowLabelFactor = 0.09f

/** An array's dot radius and its clearances, as fractions of the stride between dot centres. */
private const val DotFactor = 0.26f
private const val GutterStride = 0.7f
private const val ColLabelStride = 1.1f
private const val LeftoverStride = 1.4f

/**
 * One fraction bar, two stacked so a comparison is unmissable, or the three of a sum.
 *
 * A bar names itself only when the step allows it: "3/4" written beside the shading is the answer
 * to "how much is shaded", and on a comparison it does the comparing for the learner. What the
 * figure always shows is the amount, which is the part they are meant to read.
 */
internal fun VisualScope.drawFraction(visual: LearnVisual.Fraction) {
    val second = visual.plus ?: visual.compare
    // Only pieces of the same size add up, so a sum bar is drawn for a matching cut and no other.
    val sum = visual.plus
        ?.takeIf { it.second == visual.denominator && revealing(visual.reveal) }
        ?.let { (visual.numerator + it.first) to visual.denominator }
    val bars = listOfNotNull(visual.numerator to visual.denominator, second, sum)

    val signs = if (visual.plus != null) width * 0.08f else 0f
    val names = width * 0.15f
    val barWidth = width * 0.94f - signs - names
    val left = width * 0.03f + signs
    val barHeight = height * if (bars.size > 2) {
        0.18f
    } else if (bars.size > 1) {
        0.24f
    } else {
        0.34f
    }
    val gap = height * if (bars.size > 2) 0.09f else 0.16f
    var top = height * 0.5f - (bars.size * barHeight + (bars.size - 1) * gap) / 2f
    val named = revealing(visual.reveal)

    bars.forEachIndexed { barIndex, (numerator, denominator) ->
        val parts = denominator.coerceAtLeast(1)
        val partWidth = barWidth / parts
        val color = if (barIndex == 1) Accent2 else Accent
        repeat(parts) { i ->
            val filled = i < numerator
            // The total keeps each half in the colour it arrived in, so the sum is visibly the two
            // bars above it pushed together rather than a fresh shading of its own.
            val pieceColor = if (barIndex == 2 && i >= visual.numerator) Accent2 else color
            box(
                topLeft = Offset(left + partWidth * i, top),
                size = Size(partWidth, barHeight),
                fill = if (filled) pieceColor.copy(alpha = 0.8f) else null,
                outline = ink,
                alpha = if (filled) item(i, numerator.coerceAtLeast(1)) else 1f,
            )
        }
        if (named) {
            label(
                text = "$numerator/$denominator",
                center = Offset(left + barWidth + names / 2f, top + barHeight / 2f),
                color = color,
                factor = 0.12f,
            )
        }
        // A sum reads down the page like a column addition, with its signs in a column of their own.
        if (barIndex > 0 && visual.plus != null) {
            label(
                text = if (barIndex == 1) "+" else "=",
                center = Offset(signs / 2f, top + barHeight / 2f),
                color = ink,
                factor = 0.15f,
            )
        }
        top += barHeight + gap
    }
}

/**
 * A ratio as one bar cut into its parts, each run in its own colour.
 *
 * Deliberately not a fraction bar. Nothing here is shaded against an unshaded remainder, because
 * the mistake this figure exists to head off is reading 2 : 3 as two fifths: a fraction bar
 * captions itself "2/5" and teaches exactly the wrong thing. The two runs are equal partners in
 * the two group colours, and the counts sit over their own runs so the formula beside the figure
 * can be read straight off it.
 */
internal fun VisualScope.drawRatioBar(visual: LearnVisual.RatioBar) {
    val parts = visual.parts.filter { it > 0 }
    if (parts.isEmpty()) return
    val cells = parts.sum()
    val scale = visual.scale.coerceAtLeast(1)

    val barWidth = width * 0.84f
    val left = width / 2f - barWidth / 2f
    val barHeight = height * 0.28f
    val top = height * 0.5f - barHeight / 2f
    val cellWidth = barWidth / cells

    var placed = 0
    parts.forEachIndexed { index, part ->
        val color = if (index % 2 == 0) Accent else Accent2
        repeat(part) {
            val cell = placed++
            box(
                topLeft = Offset(left + cellWidth * cell, top),
                size = Size(cellWidth, barHeight),
                fill = color.copy(alpha = 0.75f),
                outline = ink,
                alpha = item(cell, cells),
            )
        }
    }

    // The finer cut arrives after the bar is out, so equivalence reads as the same bar divided
    // again rather than as a second diagram that has to be matched up with the first.
    if (scale > 1) {
        val fine = stage(1, 2)
        val fineWidth = barWidth / (cells * scale)
        repeat(cells * scale) { i ->
            if (i == 0 || i % scale == 0) return@repeat
            val x = left + fineWidth * i
            line(Offset(x, top), Offset(x, top + barHeight), ink, stroke * 0.6f, alpha = fine)
        }
    }

    if (!revealing(visual.reveal)) return

    // Each run is counted over its own colour, and says what it is worth underneath when the step
    // is sharing an amount out rather than naming a ratio.
    var seen = 0
    parts.forEachIndexed { index, part ->
        val color = if (index % 2 == 0) Accent else Accent2
        val centreX = left + cellWidth * (seen + part / 2f)
        label(
            text = (part * scale).toString(),
            center = Offset(centreX, top - height * 0.14f),
            color = color,
            factor = 0.13f,
            alpha = stage(1, 2),
        )
        visual.total?.let { total ->
            label(
                text = (total * part / cells).toString(),
                center = Offset(centreX, top + barHeight + height * 0.16f),
                color = color,
                factor = 0.12f,
                alpha = stage(1, 2),
            )
        }
        seen += part
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
        if (alpha > 0.5f && revealing(visual.reveal)) {
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
    if (!revealing(visual.reveal)) return
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
    // Bounded by the height as well as the width: on a wide canvas a purely width-derived slot
    // draws terms as discs taller than the figure itself.
    val slot = minOf(width * 0.86f / terms.size, height * 0.62f)
    val left = width / 2f - slot * (terms.size - 1) / 2f
    val y = height * 0.62f

    // Drawn in the number line's own language, because it is the same idea at a longer stride: the
    // terms sit on a line and the jumps arc over it. Translucent discs behind the numbers turned
    // the whole figure muddy on a dark panel and told the learner nothing.
    val ends = slot * 0.4f
    line(Offset(left - ends, y), Offset(left + slot * (terms.size - 1) + ends, y), ink, stroke)

    terms.forEachIndexed { index, term ->
        val x = left + slot * index
        val alpha = item(index, terms.size)
        line(
            Offset(x, y - height * 0.05f),
            Offset(x, y + height * 0.05f),
            ink,
            stroke * 0.8f,
            alpha = alpha,
        )
        label(term.toString(), Offset(x, y + height * 0.17f), ink, 0.11f, alpha = alpha, bold = false)

        if (index < terms.lastIndex) {
            val next = terms[index + 1]
            val t = item(index, terms.size)
            if (t <= 0f) return@forEachIndexed
            val x1 = x + slot * t
            val arc = Path().apply {
                moveTo(x, y)
                quadraticTo((x + x1) / 2f, y - height * 0.34f, x1, y)
            }
            path(arc, fill = null, outline = Accent, width = stroke * 1.2f, alpha = t)
            if (t > 0.9f) {
                val stepLabel = if (visual.multiply) {
                    "x" + formatDecimal(next.toDouble() / term)
                } else {
                    (if (next - term >= 0) "+" else "") + (next - term)
                }
                label(stepLabel, Offset(x + slot / 2f, y - height * 0.26f), Accent, 0.1f)
            }
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

    if (!revealing(visual.reveal)) return
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
