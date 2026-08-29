package com.inspiredandroid.braincup.ui.components.learn

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import com.inspiredandroid.braincup.learn.LearnVisual
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.roundToLong
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
    val totalIn = if (visual.merge) revealBeat else 0f

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
        val color = groupColor(groupIndex)
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
        // What the groups came to, in the answer green. The equals sign holding it up is
        // structure and stays chrome, the same split the formula card beside it draws.
        labelRuns(
            runs = listOf("= " to null, "$total" to AnswerInk),
            center = Offset(width / 2f, height * 0.85f),
            factor = 0.16f,
            alpha = totalIn,
        )
    }
}

/** The size the sum a pair of ten-frames adds up to is set at. */
private const val TenFrameSumFactor = 0.13f

/** Ten-frames: fill the first to ten, then spill the rest into the second. */
internal fun VisualScope.drawTenFrame(visual: LearnVisual.TenFrame) {
    val total = visual.filled + visual.added
    val sums = visual.added > 0 && visual.reveal
    val room = captions(if (sums) 1 else 0, TenFrameSumFactor)
    val cell = minOf(width / 12.5f, room.figureBottom / 3.2f)
    val frameWidth = cell * 5
    val frames = if (total > 10) 2 else 1
    // A lone frame is centred on its own rather than sitting where the left one of a pair would.
    val left = if (frames == 1) width / 2f - frameWidth / 2f else width / 2f - frameWidth - cell * 0.35f
    val top = room.centerY - cell

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
    if (!sums) return
    // Each number in the colour of the dots it counts: the frame it started with, the dots that
    // arrived, and what they came to. Printed as one orange run this said the total was another
    // given, while the formula card above it had already turned the same number green.
    labelRuns(
        runs = listOf(
            "${visual.filled}" to Accent,
            " + " to null,
            "${visual.added}" to Accent2,
            " = " to null,
            "$total" to AnswerInk,
        ),
        center = Offset(width / 2f, room.y(0)),
        factor = TenFrameSumFactor,
        alpha = revealBeat,
    )
}

/**
 * The horizontal scale a figure lays its values out on: the pixel span [left]..[right], and the
 * value range that maps onto it.
 *
 * The number line, the inequality ray and the ruler all draw a value against a scale, but they do
 * not agree on it - the ruler starts at zero and takes narrower margins - so the convention is a
 * parameter here rather than a constant baked into one shared axis.
 */
internal class ValueAxis(
    val left: Float,
    val right: Float,
    private val from: Float,
    private val span: Float,
) {
    fun xOf(value: Float): Float = left + (right - left) * (value - from) / span
}

/** The scale [from]..[to] laid across the canvas, inset by the usual side margins. */
private fun VisualScope.valueAxis(
    from: Int,
    to: Int,
    leftFraction: Float = 0.09f,
    rightFraction: Float = 0.91f,
): ValueAxis = ValueAxis(
    left = width * leftFraction,
    right = width * rightFraction,
    from = from.toFloat(),
    span = (to - from).coerceAtLeast(1).toFloat(),
)

/** Half the angle between an arrowhead's two barbs, in radians. */
private const val ArrowSpread = 0.52f

/**
 * The arc one hop draws over the axis at [y], growing from [x0] to [x1], with [text] riding above
 * it once the hop has all but landed.
 *
 * [labelX] is where that text sits. A number line centres it on the arc as it grows; the steps
 * figure pins it to the middle of the full slot instead, so a row of them stays evenly spaced.
 */
private fun VisualScope.hopArc(
    x0: Float,
    x1: Float,
    y: Float,
    t: Float,
    text: String,
    labelX: Float = (x0 + x1) / 2f,
    alpha: Float = 1f,
    /**
     * The working colour by default, because on a number line a hop is a step applied to a given.
     * A sequence has no given to step from - every term is one of the same kind of thing - so
     * [drawSteps] passes the plain accent and keeps blue meaning what it means everywhere else.
     */
    color: Color = Accent2,
    /**
     * How far short of the landing point the arrowhead stops.
     *
     * A number line draws a marker dot where a hop lands, and the head was being drawn at that
     * dot's centre: on a one-step hop the whole head disappeared under the disc and all that
     * showed were two stubs poking out from behind it. Backing the head off by the dot's radius
     * leaves it pointing at the marker instead of buried in it. A figure that lands on a bare tick
     * passes nothing and keeps the head on the end of the arc.
     */
    tipClearance: Float = 0f,
) {
    val control = Offset((x0 + x1) / 2f, y - height * HopArcRise)
    val arc = Path().apply {
        moveTo(x0, y)
        quadraticTo(control.x, control.y, x1, y)
    }
    path(arc, fill = null, outline = color, width = stroke * 1.2f, alpha = alpha)
    // The arc ends in an arrowhead at the value it lands on. Drawn without one, a hop back is the
    // same upward curve in the same place as a hop forward, and the minus sign on the label is the
    // only thing saying the count goes the other way - which is a lot to hang on one glyph in a
    // figure whose whole job is to show the movement.
    if (abs(x1 - x0) > stroke) {
        // The quadratic's tangent at its end is the step from the control point to the end.
        val along = unitAlong(control, Offset(x1, y))
        // Never more than a third of the hop, so a short one still reads as an arrow rather than
        // as a head floating halfway along its own arc.
        val backoff = min(tipClearance, abs(x1 - x0) * 0.33f)
        val tip = Offset(x1 - along.x * backoff, y - along.y * backoff)
        val barb = stroke * 2.6f
        listOf(ArrowSpread, -ArrowSpread).forEach { angle ->
            val c = cos(angle)
            val s = sin(angle)
            // The barb sweeps backwards off the tip, so it is the reversed tangent, turned.
            line(
                from = tip,
                to = Offset(
                    tip.x - barb * (along.x * c - along.y * s),
                    tip.y - barb * (along.x * s + along.y * c),
                ),
                color = color,
                width = stroke * 1.2f,
                alpha = alpha,
            )
        }
    }
    if (t > 0.9f) {
        label(text, Offset(labelX, y - height * HopLabelRise), color, HopLabelFactor)
    }
}

/** How far over its axis a hop's arc is pulled, and where the step it counts rides, as shares of
 * the panel height. A figure that draws hops reserves both above its axis. */
private const val HopArcRise = 0.34f

private const val HopLabelRise = 0.26f

private const val HopLabelFactor = 0.1f

/** Font size of the in-between tick numbers, relative to the canvas, and the gap they need. */
private const val MinorLabelFactor = 0.085f
private const val MinorLabelGap = 1.35f

/**
 * Font size of a called-out value on the axis. Larger than [MinorLabelFactor] and drawn bold, so
 * it is also the label the fit test has to be measured against.
 */
private const val AccentLabelFactor = 0.11f

/** Font size of a tick the every-fifth rule numbers. */
private const val MajorLabelFactor = 0.1f

/** A number line, with an optional hop counting on from a start value. */
internal fun VisualScope.drawNumberLine(visual: LearnVisual.NumberLine) {
    val axis = valueAxis(visual.from, visual.to)
    val left = axis.left
    val right = axis.right
    fun xOf(value: Float): Float = axis.xOf(value)

    val step = visual.tickStep.coerceAtLeast(1)
    val ticks = (visual.from..visual.to step step).toList()

    // Every tick is numbered when the numbers fit side by side. A step that asks the learner to
    // land on a value is only answerable if they can read that value off the line, and the every
    // fifth tick rule leaves the four ticks in between blank. Crowded lines fall back to it.
    // Measured at the widest label the line can actually draw, not the narrowest. A called-out
    // value is set at [AccentLabelFactor] and bold, so measuring the minor style let crowded lines
    // through the test and then ran "14" into "15", and "-20" into "-18".
    val widestStyle = labelStyle(AccentLabelFactor, bold = true)
    val widest = ticks.maxOfOrNull { measure(it.toString(), widestStyle).size.width } ?: 0
    val spacing = if (ticks.size > 1) (right - left) / (ticks.size - 1) else right - left
    val labelEveryTick = widest * MinorLabelGap <= spacing

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
    val landed = if (start != null && visual.reveal) start + travel else null

    // Whatever the learner just put forward is called out on the scale itself: in the accent when
    // it is right, in the error colour when it is not, because seeing where the number they picked
    // actually sits is the correction.
    val marked = answer?.value?.takeIf { it in visual.from..visual.to }

    // The same map the line of text under the figure colours itself from, so the two cannot drift:
    // if the figure marks -4 green then "-4 is 4 left of 0" prints its -4 green as well.
    // Built once per figure by the canvas; this runs inside the draw block.

    /**
     * What a value on the axis is, in colour: a given the question hands you, or the answer. Null
     * for an ordinary tick.
     *
     * A value a hop touches down on along the way is deliberately not coloured. The working is the
     * movement - the arcs and what they are labelled - and calling out every place the movement
     * pauses turns a three-colour code back into a scatter of highlights. That is why this asks
     * [FigureRoles] only about the two roles the axis carries: the hops are the working, and they
     * are drawn on the arcs rather than on the scale.
     */
    fun roleColor(value: Int): Color? = when {
        value == marked && value != start -> resultColor
        value == start -> Accent
        figureRoles.roleOf(value.toString()) == FigureRole.ANSWER && value == landed -> SuccessGreen
        else -> null
    }

    // The values a comparison question is choosing between. They carry no role - a candidate is
    // not a given and not yet an answer - so they take the ordinary ink and only claim the size
    // and the tick height, which is what makes them readable on a line too crowded to number
    // every tick.
    val candidates = visual.compare.filter { it in visual.from..visual.to }.toSet()

    // A candidate is set at the called-out size, and four of them on one line will not always
    // clear each other: -1 and 0 are neighbouring ticks, and side by side they print as "-10".
    // Each number takes the first row it fits in, left to right, so a crowded pair steps down a
    // line instead of overprinting. Worked out before anything is drawn, because how many rows it
    // comes to is part of how much room the figure needs under its axis.
    val candidateStyle = labelStyle(AccentLabelFactor, bold = true)
    // A whole digit of clearance rather than the hairline [labelPadding] a plain label gets. Two
    // candidates only need to *touch* to be misread: "-1" and "0" set a few pixels apart print as
    // "-10", which is one of the other options on the very step this exists for.
    val candidateGap = measure("0", candidateStyle).size.width.toFloat()
    val candidateRows = mutableMapOf<Int, Int>()
    run {
        val rowRight = mutableListOf<Float>()
        candidates.filter { roleColor(it) == null }.sorted().forEach { value ->
            val x = xOf(value.toFloat())
            val half = measure(value.toString(), candidateStyle).size.width / 2f
            var row = rowRight.indexOfFirst { x - half > it + candidateGap }
            if (row < 0) {
                rowRight.add(0f)
                row = rowRight.lastIndex
            }
            rowRight[row] = x + half
            candidateRows[value] = row
        }
    }

    // The axis sits where it has to for everything hung off it to be centred: hops and their step
    // labels reach over it, tick numbers and any stacked candidates reach under it. Pinned to
    // 0.62 of the height, a line with no hops on it drew itself entirely in the bottom half of
    // the panel with a third of it empty above.
    val hops = start != null && travel != 0
    val overAxis = if (hops) {
        height * HopLabelRise + capHeight(HopLabelFactor) / 2f
    } else {
        height * 0.06f
    }
    val underAxis = height * (0.16f + (candidateRows.values.maxOrNull() ?: 0) * 0.13f) +
        capHeight(AccentLabelFactor) / 2f
    val axisY = (height - overAxis - underAxis) / 2f + overAxis

    line(Offset(left, axisY), Offset(right, axisY), ink, stroke)

    fun isAccented(value: Int) = roleColor(value) != null || value in candidates

    // A called-out value is set larger and bold, so it reaches into the space its neighbours would
    // use, and `tickStep > 1` numbers every tick without consulting the fit test at all. Rather
    // than let the two collide, the plain neighbour gives way: an axis reading "-20-18" is worse
    // than one that starts at -18, and the called-out number is the one the step is about.
    val accentSpans = ticks.filter { isAccented(it) }.map { value ->
        val x = xOf(value.toFloat())
        val half = measure(value.toString(), widestStyle).size.width / 2f
        (x - half) to (x + half)
    }
    val labelPadding = stroke * 2f

    fun clearOfAccents(x: Float, width: Int): Boolean {
        val half = width / 2f
        return accentSpans.none { (lo, hi) ->
            x - half < hi + labelPadding && x + half > lo - labelPadding
        }
    }

    ticks.forEach { value ->
        val x = xOf(value.toFloat())
        val isLanding = value == landed && value != start
        val isMarked = value == marked && value != start
        val accentColor = roleColor(value)
        val accented = isAccented(value)
        val major = (value - visual.from) % (step * 5) == 0 || step > 1
        val tall = major || accented
        line(
            Offset(x, axisY - height * (if (tall) 0.06f else 0.035f)),
            Offset(x, axisY + height * (if (tall) 0.06f else 0.035f)),
            when {
                accentColor != null -> accentColor
                accented || major -> ink
                else -> faint
            },
            stroke * 0.8f,
        )
        when {
            // The total arrives with the last hop, so its number fades in on the same beat.
            accentColor != null -> label(
                text = value.toString(),
                center = Offset(x, axisY + height * 0.16f),
                color = accentColor,
                factor = AccentLabelFactor,
                alpha = if (isLanding) revealBeat else 1f,
            )

            // Candidates are numbered together below, because there can be four of them and two
            // sitting on neighbouring ticks have to stack rather than run into each other.
            value in candidates -> Unit

            major -> {
                val majorStyle = labelStyle(MajorLabelFactor, bold = false)
                if (clearOfAccents(x, measure(value.toString(), majorStyle).size.width)) {
                    label(value.toString(), Offset(x, axisY + height * 0.16f), ink, MajorLabelFactor, bold = false)
                }
            }

            labelEveryTick -> {
                val style = labelStyle(MinorLabelFactor, bold = false)
                if (clearOfAccents(x, measure(value.toString(), style).size.width)) {
                    label(
                        text = value.toString(),
                        center = Offset(x, axisY + height * 0.15f),
                        color = ink.copy(alpha = 0.7f),
                        factor = MinorLabelFactor,
                        bold = false,
                    )
                }
            }
        }
    }

    // Each candidate also sits on the line, not only under it, so the set being compared reads as
    // points on the scale rather than as four numbers that happen to be printed nearby.
    candidates.forEach { value ->
        dot(Offset(xOf(value.toFloat()), axisY), stroke * 1.5f, ink)
    }

    candidateRows.forEach { (value, row) ->
        label(
            text = value.toString(),
            center = Offset(xOf(value.toFloat()), axisY + height * (0.16f + row * 0.13f)),
            color = ink,
            factor = AccentLabelFactor,
        )
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
        hopArc(
            x0 = xOf(fromValue.toFloat()),
            x1 = xOf(fromValue + perHop * t),
            y = axisY,
            t = t,
            text = (if (perHop >= 0) "+" else "") + perHop,
            // The largest marker a hop can land on, so the head clears every one of them.
            tipClearance = stroke * 2.4f,
        )
    }
    dot(Offset(xOf(start.toFloat()), axisY), stroke * 2f, Accent)
    if (landed != null) {
        dot(Offset(xOf(landed.toFloat()), axisY), stroke * 2.4f, SuccessGreen, alpha = progress)
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
    val axis = valueAxis(visual.from, visual.to)
    val left = axis.left
    val right = axis.right
    val axisY = height * 0.55f
    fun xOf(value: Float): Float = axis.xOf(value)

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
        circle(Offset(boundaryX, axisY), radius, outline = ink, width = stroke * 1.2f)
    }

    if (visual.reveal) {
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
private const val PlaceValueNameFactor = 0.12f

internal fun VisualScope.drawPlaceValue(visual: LearnVisual.PlaceValue) {
    val second = visual.plus ?: visual.compare
    val numbers = listOfNotNull(visual.tens to visual.ones, second)

    // Width in cells, so one cell size can satisfy both directions at once.
    fun widthInCells(number: Pair<Int, Int>): Float {
        val rods = if (number.first > 0) (number.first - 1) * RodStride + 1f else 0f
        val ones = if (number.second > 0) (number.second - 1) * OneStride + 1f else 0f
        return rods + (if (number.first > 0 && number.second > 0) RodsToOnesGap else 0f) + ones
    }

    // Only two numbers say what they are, so only two numbers need the strip to say it in. A
    // lone number was reserving it anyway, which left a quarter of the panel empty under blocks
    // that were never going to be captioned.
    val named = numbers.size > 1
    val strip = if (named) labelBand(PlaceValueNameFactor) else 0f
    val gaps = NumberGap * (numbers.size - 1)
    val cell = minOf(
        width * 0.94f / (numbers.sumOf { widthInCells(it).toDouble() }.toFloat() + gaps),
        (height * 0.96f - strip) / 10f,
    )
    val rodHeight = cell * 10f
    // Blocks and the strip under them are centred as one block, so a number too wide for full
    // height rods sits in the middle of the panel instead of hanging off the bottom of it.
    val blocksBottom = (height - rodHeight - strip) / 2f + rodHeight

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
        if (named) {
            labelBelow(
                text = (tens * 10 + ones).toString(),
                at = Offset(left + widths[index] / 2f, blocksBottom),
                color = color,
                factor = PlaceValueNameFactor,
                alpha = revealBeat,
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
        !visual.reveal -> return
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
private const val DecimalOfFactor = 0.12f

private const val DecimalNameFactor = 0.13f

internal fun VisualScope.drawDecimalGrid(visual: LearnVisual.DecimalGrid) {
    val second = visual.plus ?: visual.compare
    val sum = visual.plus?.takeIf { visual.reveal }?.let { visual.value + it }
    val values = listOfNotNull(visual.value, second, sum)
    // A percentage of an amount needs a band above the square for the amount it is taken of, so
    // the square gives some height back. On its own it keeps the room it always had.
    val ofAmount = visual.of?.takeIf { second == null }
    val named = visual.reveal
    val comparePlaces = visual.compareDecimals?.takeIf { visual.compare != null }
    // What the square is a percentage of stands over it and what it works out to sits under it,
    // so both come off the room the squares have before the squares are sized, and the three
    // together are what gets centred.
    val aboveBand = if (ofAmount != null) labelBand(DecimalOfFactor) else 0f
    val belowBand = if (named) labelBand(DecimalNameFactor) else 0f
    val gridSize = minOf(height * 0.96f - aboveBand - belowBand, width / (values.size * 1.5f))
    val cell = gridSize / 10f
    val gap = gridSize * 0.4f
    val totalWidth = values.size * gridSize + (values.size - 1) * gap
    var left = width / 2f - totalWidth / 2f
    val top = (height - aboveBand - gridSize - belowBand) / 2f + aboveBand

    values.forEachIndexed { gridIndex, value ->
        val shaded = (value * 100).roundToInt().coerceIn(0, 100)
        // The total is shaded in both colours, so the tenths that arrived second stay recognisable
        // inside it and the sum reads as the two parts pushed together.
        val carried = if (gridIndex == 2) (visual.value * 100).roundToInt().coerceIn(0, 100) else shaded
        val color = if (gridIndex == 1) Accent2 else Accent
        // The shaded part goes down as unbroken colour with the rules laid over the top of it, so
        // what the eye reads is one region of one square rather than a hundred little boxes. Every
        // fifth rule is drawn heavier, which is what makes 0.35 legible at a glance as three rows
        // and half of the next instead of something to be counted cell by cell.
        repeat(shaded) { i ->
            box(
                topLeft = Offset(left + (i % 10) * cell, top + (i / 10) * cell),
                size = Size(cell, cell),
                fill = (if (i < carried) color else Accent2).copy(alpha = 0.92f),
                outline = null,
                alpha = item(i, shaded.coerceAtLeast(1)),
            )
        }
        // The rules are hairlines under the colour rather than a lattice over it: a hundred cells
        // outlined at equal weight is the brightest thing on the panel and buries the one thing the
        // figure is about. Every fifth rule is drawn up, which is what turns 0.35 into three rows
        // and half of the next at a glance instead of something to count cell by cell.
        repeat(9) { i ->
            val at = (i + 1) * cell
            val quarter = i == 4
            val rule = ink.copy(alpha = if (quarter) 0.34f else 0.16f)
            val weight = stroke * if (quarter) 0.55f else 0.35f
            line(Offset(left + at, top), Offset(left + at, top + gridSize), rule, weight)
            line(Offset(left, top + at), Offset(left + gridSize, top + at), rule, weight)
        }
        box(
            topLeft = Offset(left, top),
            size = Size(gridSize, gridSize),
            outline = ink.copy(alpha = 0.7f),
            width = stroke * 0.8f,
        )
        // What the square is a percentage of is part of the question, so it stands over the grid
        // whether or not the figure may give the answer away. The part it works out to waits.
        if (ofAmount != null) {
            labelAbove(
                text = strings.percentOfTemplate.fillIn(formatDecimal(value * 100), ofAmount),
                at = Offset(left + gridSize / 2f, top),
                color = ink,
                factor = DecimalOfFactor,
            )
        }
        if (named) {
            labelBelow(
                text = when {
                    ofAmount != null -> "= " + formatDecimal(value * ofAmount)
                    // The step that says a percentage and a decimal are one number has to show
                    // both of them, or it is only ever showing one of the two.
                    visual.percent -> formatDecimal(value * 100) + "% = " + formatDecimal(value)
                    // The step about a nought on the end has to caption the square with the nought
                    // on the end, or the picture contradicts the line of notation under it.
                    gridIndex == 1 && comparePlaces != null -> formatPlaces(value, comparePlaces)
                    else -> formatDecimal(value)
                },
                at = Offset(left + gridSize / 2f, top + gridSize),
                // A caption the figure worked out for itself takes the answer green, exactly as
                // the bottom bar of a fraction sum does: the third square of a sum, and the "= 16"
                // a percentage comes to. In the accent these read as another given, while the
                // "0.4 + 0.35 = 0.75" card above had already printed the same number green.
                color = if (gridIndex == 2 && visual.plus != null || ofAmount != null) {
                    AnswerInk
                } else {
                    color
                },
                factor = DecimalNameFactor,
                alpha = if (ofAmount != null) revealBeat else 1f,
            )
        }
        // The sign sits in the gap the square just left behind it. A plus is part of what the step
        // is asking, so it is always there; a comparison sign is the answer, so it waits for the
        // figure to be allowed it, exactly as the base-ten blocks do.
        val sign = when {
            gridIndex == 0 -> null
            visual.plus != null -> if (gridIndex == 1) "+" else "="
            !visual.reveal -> null
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
 * The counts are bracketed against the dots they count rather than left floating beside them: a
 * dimension rule down the rows and another across the columns, each label in the colour of the
 * dots it names. That is what makes a split array readable - the two brackets measure their own
 * block, in their own colour - and it is the same annotation whether the array is being built up
 * or shared out.
 *
 * A remainder is drawn as the row it would have started and could not fill, under the array and
 * outside the bracket, because that is exactly what a remainder is.
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
    val split = visual.bandSplit
    val lane = if (split != null) 0.6f else 0f

    val style = labelStyle(RowLabelFactor)
    val bands = if (split != null) {
        listOf(
            RowBand(strings.rows, 0, split - 1, Accent),
            RowBand(strings.moreTemplate.fillIn(rows - split), split, rows - 1, Accent2),
        )
    } else {
        listOf(RowBand(strings.rows, 0, rows - 1, Accent))
    }
    // The labels are measured before anything is placed: the row counts sit in a gutter of their
    // own width, so the dots are sized to the room that leaves rather than to the whole panel.
    val gutter = bands.maxOf { measure(it.text, style).size.width }.toFloat()
    val colText = strings.inEachTemplate.fillIn(cols)
    val colSize = measure(colText, style).size

    // Everything is counted in strides between dot centres plus what hangs off the edges: bracket
    // and label to the left, a dot's radius all round, and the unfilled row under a remainder.
    val across = ArrayLabelGap + ArrayRuleGap + ArrayDot + (cols - 1) + ArrayDot
    val down = ArrayTopRule + (rows - 1 + lane) + (if (leftover > 0) ArrayRemainderDrop else 0f) + ArrayDot
    val step = minOf(
        (width * 0.96f - gutter) / across,
        (height * 0.96f - colSize.height / 2f) / down,
    ).coerceAtLeast(1f)
    val radius = step * ArrayDot
    val gridWidth = step * (cols - 1)
    val gridHeight = step * (rows - 1 + lane)
    // Brackets, dots and remainder are centred as one block. Centring the dots by themselves and
    // letting the annotation hang off the left is what leaves a figure sitting off to one side.
    val left = (width - gutter - step * across) / 2f + gutter + step * (ArrayLabelGap + ArrayRuleGap + ArrayDot)
    val top = (height - colSize.height / 2f - step * down) / 2f + colSize.height / 2f + step * ArrayTopRule
    fun rowY(r: Int): Float = top + step * (r + if (split != null && r >= split) lane else 0f)

    val dots = rows * cols + leftover
    repeat(rows) { r ->
        repeat(cols) { c ->
            dot(
                center = Offset(left + c * step, rowY(r)),
                radius = radius,
                color = if (split != null && r >= split) Accent2 else Accent,
                alpha = item(r * cols + c, dots),
            )
        }
    }
    repeat(leftover) { i ->
        dot(
            center = Offset(left + i * step, top + gridHeight + step * ArrayRemainderDrop),
            radius = radius,
            color = Accent2,
            alpha = item(rows * cols + i, dots),
        )
    }

    val annotation = stage(0, 3)
    bands.forEach { band ->
        val ruleX = left - radius - step * ArrayRuleGap
        val from = rowY(band.first) - radius
        val to = rowY(band.last) + radius
        dimension(Offset(ruleX, from), Offset(ruleX, to), band.color, step * ArrayTick, annotation)
        val measured = measure(band.text, style)
        label(
            text = band.text,
            center = Offset(ruleX - step * ArrayLabelGap - measured.size.width / 2f, (from + to) / 2f),
            color = band.color,
            factor = RowLabelFactor,
            alpha = annotation,
        )
    }

    // The column count breaks its rule in the middle rather than sitting above it: a panel this
    // shape runs out of height long before it runs out of width, and a stacked label costs a whole
    // stride of dot size. Where the array is too narrow for that, the rule simply stays away.
    //
    // Rows are the first number of the product and columns the second, so "4 rows" takes the given
    // and "7 in each" the working, matching "4 x 7" on the card below. A split array is a different
    // figure: there the two row bands carry the two colours and the column count runs through both,
    // so it is chrome and takes the ink.
    val spanLeft = left - radius
    val spanRight = left + gridWidth + radius
    val ruleY = top - step * ArrayTopRule
    val colColor = if (split != null) ink else Accent2
    val breakHalf = colSize.width / 2f + step * ArrayLabelGap
    if ((spanRight - spanLeft) / 2f - breakHalf > step * ArrayTick) {
        dimension(Offset(spanLeft, ruleY), Offset(spanRight, ruleY), colColor, step * ArrayTick, annotation, breakHalf)
    }
    label(colText, Offset((spanLeft + spanRight) / 2f, ruleY), colColor, RowLabelFactor, alpha = annotation)
}

/** One bracketed run of rows: the whole array, or one side of a split. */
private class RowBand(val text: String, val first: Int, val last: Int, val color: Color)

/**
 * A dimension rule with a tick turned across each end, hairline and half-lit so it reads as the
 * measurement of the dots rather than as another thing drawn on the panel. [breakHalf] opens a gap
 * in the middle for a label to sit in the rule instead of above it.
 */
private fun VisualScope.dimension(
    from: Offset,
    to: Offset,
    color: Color,
    tick: Float,
    alpha: Float,
    breakHalf: Float = 0f,
) {
    val tone = color.copy(alpha = color.alpha * 0.55f)
    val hairline = stroke * 0.5f
    val vertical = from.x == to.x
    val across = if (vertical) Offset(tick, 0f) else Offset(0f, tick)
    if (breakHalf > 0f) {
        val mid = Offset((from.x + to.x) / 2f, (from.y + to.y) / 2f)
        val gap = if (vertical) Offset(0f, breakHalf) else Offset(breakHalf, 0f)
        line(from, mid - gap, tone, hairline, alpha = alpha)
        line(mid + gap, to, tone, hairline, alpha = alpha)
    } else {
        line(from, to, tone, hairline, alpha = alpha)
    }
    line(from - across, from + across, tone, hairline, alpha = alpha)
    line(to - across, to + across, tone, hairline, alpha = alpha)
}

/** Font size of an array's row and column counts, relative to the canvas. */
private const val RowLabelFactor = 0.09f

/** An array's dot radius and its clearances, as fractions of the stride between dot centres. */
private const val ArrayDot = 0.3f
private const val ArrayTopRule = 0.95f
private const val ArrayRuleGap = 0.45f
private const val ArrayLabelGap = 0.4f
private const val ArrayRemainderDrop = 1.3f
private const val ArrayTick = 0.2f

/**
 * One fraction bar, two stacked so a comparison is unmissable, or the three of a sum.
 *
 * A bar names itself only when the step allows it: "3/4" written beside the shading is the answer
 * to "how much is shaded", and on a comparison it does the comparing for the learner. What the
 * figure always shows is the amount, which is the part they are meant to read.
 */
private const val FractionNameFactor = 0.12f

internal fun VisualScope.drawFraction(visual: LearnVisual.Fraction) {
    val second = visual.plus ?: visual.compare
    // Only pieces of the same size add up, so a sum bar is drawn for a matching cut and no other.
    val sum = visual.plus
        ?.takeIf { it.second == visual.denominator && visual.reveal }
        ?.let { (visual.numerator + it.first) to visual.denominator }
    val bars = listOfNotNull(visual.numerator to visual.denominator, second, sum)

    val named = visual.reveal
    val signs = if (visual.plus != null) width * 0.08f else 0f
    // Reserved only when a bar is actually going to name itself, and only as wide as the widest
    // name it will print. A flat sixth of the panel, held back on every figure, was blank space to
    // the right of bars with nothing to say - and it pushed them off centre by half of that.
    val names = if (named) {
        bars.maxOf { (numerator, denominator) -> labelBand("$numerator/$denominator", FractionNameFactor) }
    } else {
        0f
    }
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
            labelRightOf(
                text = "$numerator/$denominator",
                at = Offset(left + barWidth, top + barHeight / 2f),
                // The bottom bar of a sum is what the two above it come to, so its name takes the
                // answer green - the same green the "3/5 + 1/5 = 4/5" card under the figure prints
                // its total in. In the accent it read as a third given rather than as the result.
                color = if (barIndex == 2) AnswerInk else color,
                factor = FractionNameFactor,
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
 * captions itself "2/5" and teaches exactly the wrong thing. The runs are equal partners, each in
 * its own [groupColor], and the counts sit over their own runs so the formula beside the figure can
 * be read straight off it.
 *
 * Equal partners is why the third run is a third colour rather than the answer green: 3 : 2 : 1 has
 * no answer in it, and every part of the split is the same kind of thing as the other two.
 */
private const val RatioCountFactor = 0.13f

internal fun VisualScope.drawRatioBar(visual: LearnVisual.RatioBar) {
    val parts = visual.parts.filter { it > 0 }
    if (parts.isEmpty()) return
    val cells = parts.sum()
    val scale = visual.scale.coerceAtLeast(1)

    val barWidth = width * 0.84f
    val left = width / 2f - barWidth / 2f
    val barHeight = height * 0.28f
    // The counts over the runs and the amounts under them are the same annotation on two sides of
    // one bar, so they take one size and one clear space, and the bar is centred with them rather
    // than on its own. Set at 0.13 above and 0.12 below, 0.14 of the height up and 0.16 down, the
    // pair sat at visibly different distances from the bar they both belong to.
    val counted = visual.reveal
    val aboveBand = if (counted) labelBand(RatioCountFactor) else 0f
    val belowBand = if (counted && visual.total != null) labelBand(RatioCountFactor) else 0f
    val top = (height - aboveBand - barHeight - belowBand) / 2f + aboveBand
    val cellWidth = barWidth / cells

    var placed = 0
    parts.forEachIndexed { index, part ->
        val color = groupColor(index)
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

    if (!visual.reveal) return

    // Each run is counted over its own colour, and says what it is worth underneath when the step
    // is sharing an amount out rather than naming a ratio.
    var seen = 0
    parts.forEachIndexed { index, part ->
        val color = groupColor(index)
        val centreX = left + cellWidth * (seen + part / 2f)
        labelAbove(
            text = (part * scale).toString(),
            at = Offset(centreX, top),
            color = color,
            factor = RatioCountFactor,
            alpha = stage(1, 2),
        )
        visual.total?.let { total ->
            labelBelow(
                text = (total * part / cells).toString(),
                at = Offset(centreX, top + barHeight),
                color = color,
                factor = RatioCountFactor,
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
        circle(
            center = center,
            radius = radius,
            fill = (if (index % 2 == 0) Accent else Accent2).copy(alpha = 0.35f * alpha),
            outline = ink.copy(alpha = alpha),
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
    // A ruler reads from zero and takes narrower margins than the number line does.
    val axis = valueAxis(from = 0, to = span, leftFraction = 0.08f, rightFraction = 0.92f)
    val left = axis.left
    val right = axis.right
    val top = height * 0.46f
    val rulerHeight = height * 0.3f
    fun xOf(value: Float) = axis.xOf(value)

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
    // The reading names the bar laid along the ruler, so it is written in that bar's colour.
    label(
        text = "${visual.length} ${visual.unit}",
        center = Offset(xOf(visual.length / 2f), top - height * 0.16f),
        color = Accent,
        factor = 0.1f,
        alpha = revealBeat,
    )
}

/** A clock face with both hands sweeping to the time. */
internal fun VisualScope.drawClock(visual: LearnVisual.Clock) {
    val radius = size.minDimension * 0.4f
    val center = Offset(width / 2f, height / 2f)
    circle(center, radius, outline = ink, width = stroke * 1.3f)
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

/** The size a sequence's terms are set at. */
private const val StepTermFactor = 0.11f

/** Terms of a sequence with the step between them called out. */
internal fun VisualScope.drawSteps(visual: LearnVisual.Steps) {
    val terms = visual.terms
    if (terms.size < 2) return
    // Bounded by the height as well as the width: on a wide canvas a purely width-derived slot
    // draws terms as discs taller than the figure itself.
    val slot = minOf(width * 0.86f / terms.size, height * 0.62f)
    val left = width / 2f - slot * (terms.size - 1) / 2f
    // Hops and their steps reach over the line, the terms sit under it: the line goes wherever
    // centres the two. See [drawNumberLine], which is the same figure at a shorter stride.
    val overLine = height * HopLabelRise + capHeight(HopLabelFactor) / 2f
    val underLine = height * 0.17f + capHeight(StepTermFactor) / 2f
    val y = (height - overLine - underLine) / 2f + overLine

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
        val value = term.toDouble()
        label(formatDecimal(value), Offset(x, y + height * 0.17f), ink, StepTermFactor, alpha = alpha, bold = false)

        if (index < terms.lastIndex) {
            val next = terms[index + 1].toDouble()
            val t = item(index, terms.size)
            if (t <= 0f) return@forEachIndexed
            val stepLabel = if (visual.multiply) {
                "x" + formatDecimal(next / value)
            } else {
                (if (next - value >= 0) "+" else "") + formatDecimal(next - value)
            }
            hopArc(
                x0 = x,
                x1 = x + slot * t,
                y = y,
                t = t,
                text = stepLabel,
                // Pinned to the middle of the whole slot, so a row of labels stays evenly spaced.
                labelX = x + slot / 2f,
                alpha = t,
                color = Accent,
            )
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
        alpha = revealBeat,
    )
}

/** Trim a double to the shortest sensible label: 2.0 -> "2", 0.25 -> "0.25". */
/**
 * The value written to a fixed number of places, trailing noughts and all. [formatDecimal] drops
 * them, which is right everywhere except the one step whose point is that 0.4 and 0.40 are the
 * same number written twice.
 */
internal fun formatPlaces(value: Double, places: Int): String {
    if (places <= 0) return value.roundToLong().toString()
    var scale = 1L
    repeat(places) { scale *= 10 }
    val scaled = (abs(value) * scale).roundToLong()
    val sign = if (value < 0 && scaled != 0L) "-" else ""
    return sign + (scaled / scale) + "." + (scaled % scale).toString().padStart(places, '0')
}

internal fun formatDecimal(value: Double): String {
    // Whole values answer first, because a term can be far larger than a ratio: 52000 scaled by
    // the hundred the rounding below works in is still an Int, but it does not have to be.
    if (abs(value - value.roundToLong()) < 0.001) return value.roundToLong().toString()
    val rounded = (value * 100).roundToInt() / 100.0
    if (abs(rounded - rounded.roundToInt()) < 0.001) return rounded.roundToInt().toString()
    val text = rounded.toString()
    return if (text.endsWith("0")) text.dropLast(1) else text
}
