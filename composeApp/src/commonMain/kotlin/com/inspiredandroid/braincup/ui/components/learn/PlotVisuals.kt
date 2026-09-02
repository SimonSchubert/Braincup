package com.inspiredandroid.braincup.ui.components.learn

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import com.inspiredandroid.braincup.learn.Curve
import com.inspiredandroid.braincup.learn.LearnVisual
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin

private const val X_MIN = -3f
private const val X_MAX = 3f
private const val Y_MIN = -3f
private const val Y_MAX = 3f

private fun Curve.valueAt(x: Float): Float? = when (this) {
    is Curve.Linear -> m * x + c
    is Curve.Quadratic -> a * x * x + b * x + c
    is Curve.Exponential -> base.pow(x)
    is Curve.Sine -> amplitude * if (cosine) cos(frequency * x) else sin(frequency * x)
    Curve.Logarithm -> if (x <= 0f) null else ln(x.toDouble()).toFloat()
}

/** The radius a plotted point is marked with, in hairlines, and the size its name is set at. */
private const val MarkerRadius = 2f

private const val MarkerLabelFactor = 0.085f

/** The size the numbers along the axes are set at. Smaller than a marker's name, which they defer to. */
private const val AxisNumberFactor = 0.07f

/**
 * A curve on axes, plus whatever the step is really about: the points it names, the tangent whose
 * gradient is the derivative, or the area the integral accumulates.
 */
internal fun VisualScope.drawPlot(visual: LearnVisual.Plot) {
    // Every name on this figure is set off its own marker rather than off the panel, so the plate
    // it sits on clears the disc it belongs to.
    val markerGap = MarkerRadius * stroke + labelGap * 0.4f
    // What a marker is allowed to print. A root, a turning point and a point's coordinates are
    // values the learner is asked to read off, so on a question figure they animate in as usual
    // and then say nothing. The curve, the grid and the discs themselves are the situation.
    val named = if (visual.reveal) revealBeat else 0f
    // One unit across has to measure one unit up, or the figure lies about every gradient it
    // draws: on a panel two and a half times wider than it is tall, a line of gradient 1 came out
    // at 22 degrees and a learner counting the picture rather than the squares read 2 as 1. So the
    // grid takes the largest *square* six-by-six it can, centred, and the panel keeps the slack.
    // Tighter insets than a figure that captions itself: the grid is square, so its size is the
    // panel's height and every percent held back off the top and bottom is a percent off the
    // drawing. What the labels need is side room, and the square leaves plenty of that.
    val room = frame(left = 0.05f, top = 0.05f, right = 0.95f, bottom = 0.95f)
    val span = min(room.width, room.height)
    val rect = Rect(
        left = room.center.x - span / 2f,
        top = room.center.y - span / 2f,
        right = room.center.x + span / 2f,
        bottom = room.center.y + span / 2f,
    )
    val cell = span / (X_MAX - X_MIN)
    fun px(x: Float) = rect.left + cell * (x - X_MIN)
    fun py(y: Float) = rect.bottom - cell * (y - Y_MIN)

    // Grid and axes first, so the curve reads as drawn on top of them.
    var g = X_MIN
    while (g <= X_MAX) {
        line(Offset(px(g), rect.top), Offset(px(g), rect.bottom), faint.copy(alpha = 0.18f), stroke * 0.5f)
        line(Offset(rect.left, py(g)), Offset(rect.right, py(g)), faint.copy(alpha = 0.18f), stroke * 0.5f)
        g += 1f
    }
    line(Offset(rect.left, py(0f)), Offset(rect.right, py(0f)), ink, stroke)
    line(Offset(px(0f), rect.top), Offset(px(0f), rect.bottom), ink, stroke)

    // Numbered, because "what are the coordinates of the marked point?" against a bare grid is a
    // counting exercise before it is a reading one, and counting is not what the question is
    // about. Zero is left off: it would sit in the corner both axes already meet at.
    val numberOut = capHeight(AxisNumberFactor) * 0.9f + stroke
    // Darker than the grid they sit against: these are meant to be read, not merely sensed, and
    // the `faint` the gridlines take put them at the edge of legible on a phone.
    val axisInk = ink.copy(alpha = 0.65f)
    listOf(-3, -2, -1, 1, 2, 3).forEach { n ->
        label(n.toString(), Offset(px(n.toFloat()), py(0f) + numberOut * 1.4f), axisInk, AxisNumberFactor, bold = false)
        label(n.toString(), Offset(px(0f) - numberOut * 1.4f, py(n.toFloat())), axisInk, AxisNumberFactor, bold = false)
    }
    // The letters go where the numbers are not: x out in the side margin the square grid frees up,
    // y on the far side of its own axis from its numbers.
    label("x", Offset(rect.right + numberOut * 1.7f, py(0f)), axisInk, 0.075f, bold = false)
    label("y", Offset(px(0f) + numberOut * 1.5f, rect.top - numberOut * 0.2f), axisInk, 0.075f, bold = false)

    fun pathFor(curve: Curve, upTo: Float): Path {
        val path = Path()
        var started = false
        var i = 0
        val steps = 240
        // Clipped to the grid rather than a unit past it: a line left to run to y = 4 stopped in
        // the margin above the squares, which reads as a segment that ends rather than a line.
        while (i <= (steps * upTo).toInt()) {
            val x = X_MIN + (X_MAX - X_MIN) * i / steps.toFloat()
            val y = curve.valueAt(x)
            if (y == null || y < Y_MIN || y > Y_MAX) {
                started = false
            } else {
                val point = Offset(px(x), py(y))
                if (!started) {
                    path.moveTo(point.x, point.y)
                    started = true
                } else {
                    path.lineTo(point.x, point.y)
                }
            }
            i++
        }
        return path
    }

    visual.second?.let { path(pathFor(it, progress), null, Accent2, stroke * 1.3f) }
    visual.curve?.let { path(pathFor(it, progress), null, Accent, stroke * 1.6f) }

    if (visual.areaTo != null && visual.curve != null) {
        val fill = Path().apply {
            moveTo(px(0f), py(0f))
            var x = 0f
            val end = visual.areaTo * progress
            while (x <= end) {
                val y = visual.curve.valueAt(x) ?: break
                lineTo(px(x), py(y.coerceIn(Y_MIN, Y_MAX)))
                x += 0.05f
            }
            lineTo(px(visual.areaTo * progress), py(0f))
            close()
        }
        path(fill, Accent.copy(alpha = 0.3f), null)
    }

    visual.tangentAt?.let { at ->
        val curve = visual.curve ?: return@let
        val y = curve.valueAt(at) ?: return@let
        val h = 0.01f
        val slope = ((curve.valueAt(at + h) ?: y) - (curve.valueAt(at - h) ?: y)) / (2 * h)
        val dx = 1.3f
        val reveal = revealBeat
        line(
            Offset(px(at - dx * reveal), py(y - slope * dx * reveal)),
            Offset(px(at + dx * reveal), py(y + slope * dx * reveal)),
            Accent2,
            stroke * 1.4f,
        )
        dot(Offset(px(at), py(y)), stroke * 2f, Accent2, alpha = reveal)
        label(
            text = strings.gradientTemplate.fillIn(formatDecimal(slope.toDouble())),
            center = Offset(width * 0.7f, rect.top + height * 0.08f),
            color = Accent2,
            factor = 0.09f,
            alpha = reveal,
        )
    }

    if (visual.markRoots) {
        val quadratic = visual.curve as? Curve.Quadratic
        if (quadratic != null) {
            val disc = quadratic.b * quadratic.b - 4 * quadratic.a * quadratic.c
            if (disc >= 0f) {
                val sqrt = kotlin.math.sqrt(disc.toDouble()).toFloat()
                listOf((-quadratic.b - sqrt) / (2 * quadratic.a), (-quadratic.b + sqrt) / (2 * quadratic.a))
                    .filter { it in X_MIN..X_MAX }
                    .forEach { root ->
                        dot(Offset(px(root), py(0f)), MarkerRadius * stroke, Accent2, alpha = named)
                        chipOutside(
                            text = formatDecimal(root.toDouble()),
                            at = Offset(px(root), py(0f)),
                            outward = Offset(0f, 1f),
                            color = Accent2,
                            factor = MarkerLabelFactor,
                            alpha = named,
                            gap = markerGap,
                        )
                    }
            }
        }
    }

    if (visual.markVertex) {
        val quadratic = visual.curve as? Curve.Quadratic
        if (quadratic != null && abs(quadratic.a) > 0.0001f) {
            val vx = -quadratic.b / (2 * quadratic.a)
            // Computed directly: a quadratic always has a value, unlike the nullable log curve.
            val vy = quadratic.a * vx * vx + quadratic.b * vx + quadratic.c
            if (vx in X_MIN..X_MAX && vy in Y_MIN..Y_MAX) {
                dot(Offset(px(vx), py(vy)), MarkerRadius * stroke, Accent2, alpha = named)
                chipOutside(
                    text = "(${formatDecimal(vx.toDouble())}, ${formatDecimal(vy.toDouble())})",
                    at = Offset(px(vx), py(vy)),
                    outward = Offset(0f, 1f),
                    color = Accent2,
                    factor = MarkerLabelFactor,
                    alpha = named,
                    gap = markerGap,
                )
            }
        }
    }

    visual.points.forEach { point ->
        val at = Offset(px(point.x), py(point.y))
        // The disc marks a place the question points at, so it is drawn either way; only its
        // name is withheld, because a printed coordinate is the answer to "where is this?".
        dot(at, MarkerRadius * stroke, Accent2, alpha = revealBeat)
        point.label?.let {
            chipOutside(it, at, Offset(0f, -1f), Accent2, MarkerLabelFactor, named, markerGap)
        }
    }
}

/** The unit circle with the radius swinging to its angle and its sine and cosine legs dropping. */
internal fun VisualScope.drawUnitCircle(visual: LearnVisual.UnitCircleFigure) {
    val radius = size.minDimension * 0.34f
    val center = Offset(width * 0.5f, height * 0.5f)

    line(Offset(center.x - radius * 1.35f, center.y), Offset(center.x + radius * 1.35f, center.y), ink, stroke)
    line(Offset(center.x, center.y - radius * 1.35f), Offset(center.x, center.y + radius * 1.35f), ink, stroke)
    circle(center, radius, outline = ink, width = stroke * 1.2f)

    val degrees = visual.degrees * progress
    val point = polar(center, radius, degrees)

    arc(
        center = center,
        radius = radius * 0.35f,
        startAngle = 0f,
        sweepAngle = -degrees,
        fill = Accent.copy(alpha = 0.3f),
        outline = null,
    )
    line(center, point, Accent, stroke * 1.4f)

    if (visual.showCos) {
        line(center, Offset(point.x, center.y), Accent2, stroke * 1.2f, alpha = revealBeat)
        if (visual.reveal) {
            label(
                text = strings.cosTemplate.fillIn(formatDecimal(cos(visual.degrees * PI / 180.0))),
                center = Offset(center.x, center.y + radius * 1.62f),
                color = Accent2,
                factor = 0.085f,
                alpha = revealBeat,
            )
        }
    }
    if (visual.showSin) {
        line(Offset(point.x, center.y), point, Accent2, stroke * 1.2f, alpha = revealBeat)
        if (visual.reveal) {
            label(
                text = strings.sinTemplate.fillIn(formatDecimal(sin(visual.degrees * PI / 180.0))),
                center = Offset(center.x, center.y - radius * 1.62f),
                color = Accent2,
                factor = 0.085f,
                alpha = revealBeat,
            )
        }
    }

    // After the legs: the sine leg ends exactly on this point and would otherwise cover it.
    dot(point, stroke * 2f, Accent)

    label(
        text = visual.label ?: strings.degreesTemplate.fillIn(visual.degrees.toFloat().roundToInt()),
        center = Offset(center.x + radius * 1.05f, center.y - radius * 0.9f),
        color = Accent,
        factor = 0.095f,
        alpha = stage(1, 3),
    )
}
