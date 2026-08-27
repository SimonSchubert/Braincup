package com.inspiredandroid.braincup.ui.components.learn

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import com.inspiredandroid.braincup.learn.LearnVisual
import com.inspiredandroid.braincup.learn.phaseCount
import com.inspiredandroid.braincup.ui.components.hoverHand
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import com.inspiredandroid.braincup.ui.theme.displayFontFamily
import com.inspiredandroid.braincup.ui.theme.numberFontFamily
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

private val EntranceSpec = tween<Float>(durationMillis = 1100, easing = FastOutSlowInEasing)

/** How long a finished phase stays on screen before a two-phase figure moves on. */
private const val PhaseHoldMillis = 3000L

/**
 * A value the learner has just put forward, so a figure drawn on a scale can point at it.
 *
 * Deliberately not part of [LearnVisual]: which numbers a figure is about is content, but what the
 * learner just guessed is screen state, and the same authored figure is shown before and after.
 */
data class VisualAnswer(val value: Int, val correct: Boolean)

/**
 * The animated diagram for one lesson step.
 *
 * Everything is drawn from the theme colours so the sketches stay legible in light, dark and OLED
 * without per-theme assets. The whole figure animates in on first appearance — dots slide together,
 * bars grow, sides draw on — and tapping it replays that animation, because the movement is often
 * the explanation.
 *
 * A figure with more than one phase ([LearnVisual.phaseCount]) plays them in turn, holding each
 * finished phase for [PhaseHoldMillis] before the next, and loops.
 */
@Composable
fun LearnVisualCanvas(
    visual: LearnVisual,
    modifier: Modifier = Modifier,
    ink: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    answer: VisualAnswer? = null,
    paper: Color = MaterialTheme.colorScheme.surfaceVariant,
) {
    // Previews and screenshot tests never advance the clock, so an entrance that starts at zero
    // renders them as an empty panel. Under inspection the figure starts finished instead.
    val inspecting = LocalInspectionMode.current
    val progress = remember(visual, inspecting) { Animatable(if (inspecting) 1f else 0f) }
    var phase by remember(visual) { mutableIntStateOf(0) }
    // Bumped by a tap. Restarting the whole effect is what makes replay work on a looping figure:
    // a second coroutine racing the loop would leave the two fighting over the same Animatable.
    var replayKey by remember(visual) { mutableIntStateOf(0) }
    val phaseCount = visual.phaseCount

    LaunchedEffect(visual, inspecting, replayKey) {
        if (inspecting) return@LaunchedEffect
        phase = 0
        while (true) {
            progress.snapTo(0f)
            progress.animateTo(1f, EntranceSpec)
            if (phaseCount <= 1) break
            delay(PhaseHoldMillis)
            phase = (phase + 1) % phaseCount
        }
    }

    val measurer = rememberTextMeasurer()
    val wrongColor = MaterialTheme.colorScheme.error
    val interactions = remember { MutableInteractionSource() }
    // Resolved out here because both are @Composable and the draw block is not.
    val numberFont = numberFontFamily()
    val displayFont = displayFontFamily()

    Box(
        modifier = modifier
            .hoverHand()
            .clickable(interactionSource = interactions, indication = null) { replayKey++ },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val scope = VisualScope(
                draw = this,
                progress = progress.value,
                ink = ink,
                measurer = measurer,
                numberFont = numberFont,
                displayFont = displayFont,
                paper = paper,
                answer = answer,
                wrongColor = wrongColor,
                phase = phase,
            )
            scope.draw(visual)
        }
    }
}

/**
 * Everything a figure needs while drawing: the canvas, how far the entrance animation has run,
 * the ink colour, and a text measurer for the labels that make a diagram self-explanatory.
 */
internal class VisualScope(
    val draw: DrawScope,
    val progress: Float,
    val ink: Color,
    val measurer: TextMeasurer,
    /** The face notation takes: digits, operators, and the lone letters that stand for variables. */
    val numberFont: FontFamily,
    /** The face words take, so a caption matches the lesson prose printed under the figure. */
    val displayFont: FontFamily,
    /** The panel the figure is drawn on, for [chipLabel] to lay a caption over its own marks. */
    val paper: Color,
    /** Null while the question is open, or on a step that asks nothing. */
    val answer: VisualAnswer? = null,
    val wrongColor: Color = Color.Red,
    /** Which phase of a multi-phase figure is playing. Always 0 for the single-phase majority. */
    val phase: Int = 0,
) {
    /** The colour a marked value takes: the accent when it is right, the error colour when not. */
    val resultColor: Color get() = if (answer?.correct == false) wrongColor else Accent

    val size: Size get() = draw.size
    val width: Float get() = draw.size.width
    val height: Float get() = draw.size.height

    /** Hairline width that keeps figures consistent whatever the canvas size. */
    val stroke: Float get() = draw.size.minDimension * 0.018f

    val faint: Color get() = ink.copy(alpha = 0.3f)

    /**
     * Progress of one stage of a multi-stage animation, as its own 0..1 ramp. Stages let a figure
     * count itself before it moves, or grow its bars before the mean line arrives.
     */
    fun stage(index: Int, count: Int): Float {
        if (count <= 1) return progress
        val span = 1f / count
        return ((progress - index * span) / span).coerceIn(0f, 1f)
    }

    /**
     * The last of three beats: the one a figure's result arrives on, once it has drawn itself and
     * marked what the step is about. Thirty figures reach for this same beat, always meaning this.
     */
    val revealBeat: Float get() = stage(2, 3)

    /** Progress across [count] items that appear one after another, with a little overlap. */
    fun item(index: Int, count: Int): Float {
        if (count <= 0) return progress
        val span = 1f / (count + 1f)
        return ((progress - index * span) / (span * 2f)).coerceIn(0f, 1f)
    }

    fun fontSize(factor: Float = 0.1f): TextUnit = with(draw) { (size.minDimension * factor).toSp() }

    fun labelStyle(
        color: Color = ink,
        factor: Float = 0.1f,
        bold: Boolean = true,
    ) = TextStyle(
        color = color,
        fontSize = fontSize(factor),
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        // Text drawn onto a Canvas inherits nothing from MaterialTheme, so without this every
        // label falls back to the platform's own face. Words take the display face here; [annotate]
        // spans the notation over to the number face.
        fontFamily = displayFont,
    )

    /**
     * A label split so its notation reads in the number face and its words in the display face.
     *
     * Two or more letters together are a word, and a word stays on-brand: "4 SIDES, 4 CORNERS",
     * "2 LINES OF SYMMETRY", "MEAN". Everything else is notation, lone variables included, so
     * "x + 5" and "90 + 90 = 180" hold one face across the expression instead of changing it at
     * every sign. This is the same split [com.inspiredandroid.braincup.ui.theme.annotateNumbers]
     * makes for "Level 4", done here against explicit families because the canvas has none to
     * inherit.
     */
    fun annotate(text: String): AnnotatedString {
        val isWord = BooleanArray(text.length)
        var i = 0
        while (i < text.length) {
            if (!text[i].isLetter()) {
                i++
                continue
            }
            val start = i
            while (i < text.length && text[i].isLetter()) i++
            if (i - start >= 2) for (j in start until i) isWord[j] = true
        }
        return buildAnnotatedString {
            var from = 0
            while (from < text.length) {
                val word = isWord[from]
                var to = from
                while (to < text.length && isWord[to] == word) to++
                val run = text.substring(from, to)
                if (word) append(run) else withStyle(SpanStyle(fontFamily = numberFont)) { append(run) }
                from = to
            }
        }
    }

    /**
     * Lays [text] out exactly as [label] will draw it. The figures measure captions to place them,
     * so this has to go through [annotate] too or the width they reserve stops matching the ink.
     */
    fun measure(text: String, style: TextStyle): TextLayoutResult = measurer.measure(annotate(text), style)

    /**
     * The left edge a label of [textWidth] should actually be drawn at, having asked for [x].
     *
     * A figure labels the thing it is pointing at, and the outermost of those - a ruler's last
     * tick, a number line's last value - sits close enough to the edge that a centred label runs
     * off the canvas and is clipped. Nudging it inside is always better than losing half of it.
     */
    private fun insideCanvas(x: Float, textWidth: Int): Float = x.coerceIn(0f, (width - textWidth).coerceAtLeast(0f))

    /** Draw [text] centred on [center], fading in with [alpha]. */
    fun label(
        text: String,
        center: Offset,
        color: Color = ink,
        factor: Float = 0.1f,
        alpha: Float = 1f,
        bold: Boolean = true,
    ) {
        if (alpha <= 0.01f || text.isEmpty()) return
        val style = labelStyle(color.copy(alpha = color.alpha * alpha), factor, bold)
        val measured = measure(text, style)
        draw.drawText(
            measured,
            topLeft = Offset(
                insideCanvas(center.x - measured.size.width / 2f, measured.size.width),
                center.y - measured.size.height / 2f,
            ),
        )
    }

    /** Draw [text] with its left edge at [start], vertically centred. */
    fun labelStart(
        text: String,
        start: Offset,
        color: Color = ink,
        factor: Float = 0.1f,
        alpha: Float = 1f,
        bold: Boolean = true,
    ) {
        if (alpha <= 0.01f || text.isEmpty()) return
        val style = labelStyle(color.copy(alpha = color.alpha * alpha), factor, bold)
        val measured = measure(text, style)
        draw.drawText(
            measured,
            topLeft = Offset(insideCanvas(start.x, measured.size.width), start.y - measured.size.height / 2f),
        )
    }

    /**
     * Draw [text] centred on [center], on a patch of the panel's own colour.
     *
     * For the annotations that are anchored to the figure's geometry rather than placed in clear
     * space - a mean line's reading, a root sitting on an axis, a plotted point's name - where the
     * thing they name is exactly what they would otherwise be drawn on top of.
     */
    fun chipLabel(
        text: String,
        center: Offset,
        color: Color = ink,
        factor: Float = 0.1f,
        alpha: Float = 1f,
        bold: Boolean = true,
    ) {
        if (alpha <= 0.01f || text.isEmpty()) return
        val measured = measure(text, labelStyle(color, factor, bold))
        val padding = measured.size.height * 0.16f
        val left = insideCanvas(center.x - measured.size.width / 2f, measured.size.width)
        box(
            topLeft = Offset(left - padding, center.y - measured.size.height / 2f - padding),
            size = Size(measured.size.width + padding * 2f, measured.size.height + padding * 2f),
            fill = paper,
            outline = null,
            alpha = alpha,
        )
        label(text, center, color, factor, alpha, bold)
    }

    fun line(
        from: Offset,
        to: Offset,
        color: Color = ink,
        width: Float = stroke,
        dashed: Boolean = false,
        alpha: Float = 1f,
    ) {
        if (alpha <= 0.01f) return
        draw.drawLine(
            color = color.copy(alpha = color.alpha * alpha),
            start = from,
            end = to,
            strokeWidth = width,
            cap = StrokeCap.Round,
            pathEffect = if (dashed) PathEffect.dashPathEffect(floatArrayOf(width * 3, width * 3)) else null,
        )
    }

    fun dot(center: Offset, radius: Float, color: Color = Primary, alpha: Float = 1f) {
        if (alpha <= 0.01f) return
        draw.drawCircle(color.copy(alpha = color.alpha * alpha), radius, center)
    }

    fun box(
        topLeft: Offset,
        size: Size,
        fill: Color? = null,
        outline: Color? = ink,
        alpha: Float = 1f,
        width: Float = stroke,
    ) {
        if (alpha <= 0.01f) return
        fill?.let { draw.drawRect(it.copy(alpha = it.alpha * alpha), topLeft, size) }
        outline?.let {
            draw.drawRect(it.copy(alpha = it.alpha * alpha), topLeft, size, style = Stroke(width = width))
        }
    }

    /** A circle, filled, outlined or both, in the same shape as [box]. */
    fun circle(
        center: Offset,
        radius: Float,
        fill: Color? = null,
        outline: Color? = ink,
        alpha: Float = 1f,
        width: Float = stroke,
    ) {
        if (alpha <= 0.01f) return
        fill?.let { draw.drawCircle(it.copy(alpha = it.alpha * alpha), radius, center) }
        outline?.let {
            draw.drawCircle(it.copy(alpha = it.alpha * alpha), radius, center, style = Stroke(width = width))
        }
    }

    /** An ellipse in the box [topLeft]..[size], for the lids and shadows a solid is drawn from. */
    fun oval(
        topLeft: Offset,
        size: Size,
        fill: Color? = null,
        outline: Color? = ink,
        alpha: Float = 1f,
        width: Float = stroke,
    ) {
        if (alpha <= 0.01f) return
        fill?.let { draw.drawOval(it.copy(alpha = it.alpha * alpha), topLeft, size) }
        outline?.let {
            draw.drawOval(it.copy(alpha = it.alpha * alpha), topLeft, size, style = Stroke(width = width))
        }
    }

    /**
     * An arc of the circle at [center]. A [fill] closes back through the centre, so it draws the
     * wedge a pie slice or a swept angle needs; an [outline] leaves it open, which is the arc a
     * circumference or an angle mark is drawn with.
     */
    fun arc(
        center: Offset,
        radius: Float,
        startAngle: Float,
        sweepAngle: Float,
        fill: Color? = null,
        outline: Color? = ink,
        alpha: Float = 1f,
        width: Float = stroke,
    ) {
        if (alpha <= 0.01f) return
        val topLeft = Offset(center.x - radius, center.y - radius)
        val box = Size(radius * 2f, radius * 2f)
        fill?.let {
            draw.drawArc(it.copy(alpha = it.alpha * alpha), startAngle, sweepAngle, true, topLeft, box)
        }
        outline?.let {
            draw.drawArc(
                it.copy(alpha = it.alpha * alpha),
                startAngle,
                sweepAngle,
                false,
                topLeft,
                box,
                style = Stroke(width = width),
            )
        }
    }

    /** The outline of a shape, closed back to [points] first corner. */
    fun closedPath(points: List<Offset>): Path = Path().apply {
        moveTo(points[0].x, points[0].y)
        points.drop(1).forEach { lineTo(it.x, it.y) }
        close()
    }

    /**
     * The point [radius] away from [center] at [degrees], measured anticlockwise from east.
     *
     * [flipY] says which way up the figure works. Screen y grows downward, so a figure drawing a
     * mathematical angle wants the default and one laying out corners in screen space wants
     * `flipY = false`. There is no majority to default to: the shape figures use one convention
     * and the number-line and plot figures the other.
     */
    fun polar(center: Offset, radius: Float, degrees: Float, flipY: Boolean = true): Offset {
        val radians = degrees * PI.toFloat() / 180f
        val dy = radius * sin(radians)
        return Offset(center.x + radius * cos(radians), if (flipY) center.y - dy else center.y + dy)
    }

    /** The unit vector pointing along the side [from]..[to]. */
    fun unitAlong(from: Offset, to: Offset): Offset {
        val dx = to.x - from.x
        val dy = to.y - from.y
        val length = hypot(dx, dy).coerceAtLeast(1f)
        return Offset(dx / length, dy / length)
    }

    /** The unit normal of the side [from]..[to], for the ticks and chevrons drawn across it. */
    fun normalAt(from: Offset, to: Offset): Offset {
        val along = unitAlong(from, to)
        return Offset(along.y, -along.x)
    }

    fun path(path: Path, fill: Color? = null, outline: Color? = ink, width: Float = stroke, alpha: Float = 1f) {
        if (alpha <= 0.01f) return
        fill?.let { draw.drawPath(path, it.copy(alpha = it.alpha * alpha)) }
        outline?.let {
            draw.drawPath(path, it.copy(alpha = it.alpha * alpha), style = Stroke(width = width, cap = StrokeCap.Round))
        }
    }

    /** A rectangle inset from the canvas edges, leaving room for labels. */
    fun frame(
        left: Float = 0.1f,
        top: Float = 0.1f,
        right: Float = 0.9f,
        bottom: Float = 0.9f,
    ) = Rect(width * left, height * top, width * right, height * bottom)
}

internal val Accent: Color get() = Primary
internal val Accent2: Color get() = SuccessGreen

private fun VisualScope.draw(visual: LearnVisual) {
    when (visual) {
        is LearnVisual.Counters -> drawCounters(visual)
        is LearnVisual.TenFrame -> drawTenFrame(visual)
        is LearnVisual.NumberLine -> drawNumberLine(visual)
        is LearnVisual.Inequality -> drawInequality(visual)
        is LearnVisual.PlaceValue -> drawPlaceValue(visual)
        is LearnVisual.DecimalGrid -> drawDecimalGrid(visual)
        is LearnVisual.ArrayDots -> drawArrayDots(visual)
        is LearnVisual.Fraction -> drawFraction(visual)
        is LearnVisual.RatioBar -> drawRatioBar(visual)
        is LearnVisual.Coins -> drawCoins(visual)
        is LearnVisual.Ruler -> drawRuler(visual)
        is LearnVisual.Clock -> drawClock(visual)
        is LearnVisual.Steps -> drawSteps(visual)
        is LearnVisual.Polygon -> drawPolygon(visual)
        is LearnVisual.Triangle -> drawTriangle(visual)
        is LearnVisual.Quadrilateral -> drawQuadrilateral(visual)
        is LearnVisual.CyclicQuad -> drawCyclicQuad(visual)
        is LearnVisual.Solid -> drawSolid(visual)
        is LearnVisual.Symmetry -> drawSymmetry(visual)
        is LearnVisual.AreaGrid -> drawAreaGrid(visual)
        is LearnVisual.RightTriangle -> drawRightTriangle(visual)
        is LearnVisual.CircleFigure -> drawCircleFigure(visual)
        is LearnVisual.AngleFigure -> drawAngleFigure(visual)
        is LearnVisual.Balance -> drawBalance(visual)
        is LearnVisual.BarChart -> drawBarChart(visual)
        is LearnVisual.PieChart -> drawPieChart(visual)
        is LearnVisual.Pictogram -> drawPictogram(visual)
        is LearnVisual.NormalCurve -> drawNormalCurve(visual)
        is LearnVisual.SetDiagram -> drawSetDiagram(visual)
        is LearnVisual.Tally -> drawTally(visual)
        is LearnVisual.Plot -> drawPlot(visual)
        is LearnVisual.UnitCircleFigure -> drawUnitCircle(visual)
    }
}
