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
import com.inspiredandroid.braincup.ui.theme.GroupPlum
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import com.inspiredandroid.braincup.ui.theme.WorkingBlue
import com.inspiredandroid.braincup.ui.theme.displayFontFamily
import com.inspiredandroid.braincup.ui.theme.numberFontFamily
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

private val EntranceSpec = tween<Float>(durationMillis = 1100, easing = FastOutSlowInEasing)

/** How long a finished phase stays on screen before a two-phase figure moves on. */
private const val PhaseHoldMillis = 3000L

/**
 * How many laid-out labels one figure's measurer keeps.
 *
 * The default is eight, and a figure routinely draws more than that - a number line labels every
 * tick - so the labels evicted each other and every frame of the entrance re-laid all of them.
 */
private const val LabelCacheSize = 64

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
 *
 * [inspectionPhase] picks which of those phases a preview or a screenshot render freezes on. The
 * loop never runs under inspection, so without it the second half of a two-phase figure -- the
 * return hop of a `NumberLine(thenJump = ...)` -- could never be seen anywhere but on a device.
 */
@Composable
fun LearnVisualCanvas(
    visual: LearnVisual,
    modifier: Modifier = Modifier,
    ink: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    answer: VisualAnswer? = null,
    paper: Color = MaterialTheme.colorScheme.surfaceVariant,
    inspectionPhase: Int = 0,
) {
    // Previews and screenshot tests never advance the clock, so an entrance that starts at zero
    // renders them as an empty panel. Under inspection the figure starts finished instead.
    val inspecting = LocalInspectionMode.current
    val progress = remember(visual, inspecting) { Animatable(if (inspecting) 1f else 0f) }
    var phase by remember(visual) { mutableIntStateOf(if (inspecting) inspectionPhase else 0) }
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

    val measurer = rememberTextMeasurer(cacheSize = LabelCacheSize)
    val wrongColor = MaterialTheme.colorScheme.error
    val interactions = remember { MutableInteractionSource() }
    // Resolved out here because all three are @Composable and the draw block is not.
    val numberFont = numberFontFamily()
    val displayFont = displayFontFamily()
    val strings = learnVisualStrings(visual)
    // A figure draws the same handful of labels on every frame, and splitting one into its word
    // and notation runs is a scan plus a builder each time. Cached per figure, so the split is
    // paid once per distinct string rather than once per label per frame.
    val annotations = remember(visual, numberFont) { mutableMapOf<String, AnnotatedString>() }
    // Three sets built from the figure, and the figure does not change between frames.
    val figureRoles = remember(visual) { visual.roles() }

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
                annotations = annotations,
                figureRoles = figureRoles,
                paper = paper,
                strings = strings,
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
    /** Memoised [annotate] results, so a repeated label is split once rather than once a frame. */
    private val annotations: MutableMap<String, AnnotatedString>,
    /** Which values this figure treats as given, working and answer. Built once per figure. */
    val figureRoles: FigureRoles,
    /** The panel the figure is drawn on, for [chipLabel] to lay a caption over its own marks. */
    val paper: Color,
    /** The words a figure captions itself with, looked up before the canvas opened. */
    val strings: LearnVisualStrings,
    /** Null while the question is open, or on a step that asks nothing. */
    val answer: VisualAnswer? = null,
    val wrongColor: Color = Color.Red,
    /** Which phase of a multi-phase figure is playing. Always 0 for the single-phase majority. */
    val phase: Int = 0,
) {
    /**
     * The colour a value the learner put forward takes on the scale.
     *
     * Green once it is right, and green nowhere else: the same green the option tile turns and the
     * same green the solved formula prints, so the answer reads as one thing in three places.
     * Before anything is answered the figure is still only pointing at a value, so it uses the
     * accent; a miss takes the error colour.
     */
    val resultColor: Color
        get() = when {
            answer == null -> Accent
            answer.correct -> SuccessGreen
            else -> wrongColor
        }

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

    /**
     * The style a label is *measured* in, which is deliberately not the colour it is drawn in.
     *
     * Colour used to be baked in here, and the entrance fades a label up by multiplying its alpha
     * every frame, so the style differed on every frame and the measurer's cache could never hit:
     * each label paid a full paragraph layout, sixty times a second, for the whole 1100ms entrance.
     * The colour is handed to `drawText` instead, which resolves it as
     * `color.takeOrElse { style.color }.modulate(alpha)` - exactly what baking it in produced - so
     * the ink is unchanged while the layout is now the same object every frame.
     */
    fun labelStyle(
        factor: Float = 0.1f,
        bold: Boolean = true,
    ) = TextStyle(
        color = ink,
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
    fun annotate(text: String): AnnotatedString = annotations.getOrPut(text) { buildAnnotation(text) }

    private fun buildAnnotation(text: String): AnnotatedString {
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

    /**
     * Draw a caption whose parts carry different colours as one centred line.
     *
     * A figure that sums two of its own parts should say so in their colours - "90 + 90 = 180"
     * with each number matching the arc it counts - and a single-colour caption cannot. Each run
     * pairs its text with a colour, or null to take the caption's own [color].
     */
    fun labelRuns(
        runs: List<Pair<String, Color?>>,
        center: Offset,
        color: Color = ink,
        factor: Float = 0.1f,
        alpha: Float = 1f,
        bold: Boolean = true,
    ) {
        if (alpha <= 0.01f || runs.isEmpty()) return
        // The one label kind whose fade cannot move to `drawText`: each run carries its own colour
        // as a span, and drawText's alpha only reaches the base colour, not the spans. Three call
        // sites, so it keeps baking the fade into the string and stays a per-frame layout.
        val style = labelStyle(factor, bold)
        val text = buildAnnotatedString {
            runs.forEach { (piece, tint) ->
                val resolved = (tint ?: color).let { it.copy(alpha = it.alpha * alpha) }
                withStyle(SpanStyle(color = resolved)) { append(annotate(piece)) }
            }
        }
        val measured = measurer.measure(text, style)
        draw.drawText(
            measured,
            topLeft = Offset(
                insideCanvas(center.x - measured.size.width / 2f, measured.size.width),
                center.y - measured.size.height / 2f,
            ),
        )
    }

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
        val measured = measure(text, labelStyle(factor, bold))
        draw.drawText(
            measured,
            color = color,
            alpha = alpha,
            topLeft = Offset(
                insideCanvas(center.x - measured.size.width / 2f, measured.size.width),
                center.y - measured.size.height / 2f,
            ),
        )
    }

    /**
     * The clear space a label keeps from whatever it names.
     *
     * One measure, whichever side of the figure the label is on. A panel is two and a half times
     * wider than it is tall, so a count set `height * 0.09` above a grid and its partner set
     * `width * 0.09` beside it are nowhere near the same distance out - the side one lands three
     * times further away, and the pair stops reading as two labels on one figure.
     */
    val labelGap: Float get() = size.minDimension * 0.05f

    /**
     * How tall the glyphs in a label of [factor] actually stand.
     *
     * A text layout is taller than its ink: the line box carries the room a descender needs under
     * the baseline and an accent needs over the cap, and a figure's labels are digits and capitals
     * that use neither. Spacing against the box instead of the ink is what left a number sitting
     * above a shape looking further from it than the same number beside it.
     */
    fun capHeight(factor: Float): Float = size.minDimension * factor * 0.72f

    /**
     * How much room a label of [factor] needs above or below the thing it names, [gap] included.
     * A figure adds this to its own extent so the block it centres is the drawing *and* its
     * labels, not the drawing on its own with the labels hanging off an edge.
     */
    fun labelBand(factor: Float = 0.1f, gap: Float = labelGap): Float = gap + capHeight(factor)

    /** The same, for a label set beside what it names: [gap] plus how wide the words run. */
    fun labelBand(text: String, factor: Float = 0.1f, bold: Boolean = true, gap: Float = labelGap): Float = gap + measure(text, labelStyle(factor, bold)).size.width

    /**
     * Draw [text] just outside [at], pushed along the unit vector [outward] until its glyphs
     * stand [gap] clear of the point.
     *
     * The push is the label's own half-extent in that direction, so a number set beside a sloping
     * side keeps exactly the clear space a number set square above a flat one does. Its ink is
     * centred across the direction too, rather than its line box, which is what keeps a label
     * beside a figure level with the thing it names.
     */
    fun labelOutside(
        text: String,
        at: Offset,
        outward: Offset,
        color: Color = ink,
        factor: Float = 0.1f,
        alpha: Float = 1f,
        gap: Float = labelGap,
        bold: Boolean = true,
    ) {
        if (alpha <= 0.01f || text.isEmpty()) return
        val measured = measure(text, labelStyle(factor, bold))
        val cap = capHeight(factor)
        val push = gap + abs(outward.x) * measured.size.width / 2f + abs(outward.y) * cap / 2f
        // [label] centres the line box; the glyphs sit high in it, so this puts the ink where the
        // caller asked for it instead.
        val inkShift = measured.size.height / 2f - measured.firstBaseline + cap / 2f
        label(
            text = text,
            center = Offset(at.x + outward.x * push, at.y + outward.y * push + inkShift),
            color = color,
            factor = factor,
            alpha = alpha,
            bold = bold,
        )
    }

    /** Draw [text] centred over [at], its glyphs standing [gap] clear of it. */
    fun labelAbove(
        text: String,
        at: Offset,
        color: Color = ink,
        factor: Float = 0.1f,
        alpha: Float = 1f,
        gap: Float = labelGap,
        bold: Boolean = true,
    ) = labelOutside(text, at, Offset(0f, -1f), color, factor, alpha, gap, bold)

    /** Draw [text] centred under [at], its glyphs standing [gap] clear of it. */
    fun labelBelow(
        text: String,
        at: Offset,
        color: Color = ink,
        factor: Float = 0.1f,
        alpha: Float = 1f,
        gap: Float = labelGap,
        bold: Boolean = true,
    ) = labelOutside(text, at, Offset(0f, 1f), color, factor, alpha, gap, bold)

    /** Draw [text] to the left of [at], its right edge [gap] clear of it. */
    fun labelLeftOf(
        text: String,
        at: Offset,
        color: Color = ink,
        factor: Float = 0.1f,
        alpha: Float = 1f,
        gap: Float = labelGap,
        bold: Boolean = true,
    ) = labelOutside(text, at, Offset(-1f, 0f), color, factor, alpha, gap, bold)

    /** Draw [text] to the right of [at], its left edge [gap] clear of it. */
    fun labelRightOf(
        text: String,
        at: Offset,
        color: Color = ink,
        factor: Float = 0.1f,
        alpha: Float = 1f,
        gap: Float = labelGap,
        bold: Boolean = true,
    ) = labelOutside(text, at, Offset(1f, 0f), color, factor, alpha, gap, bold)

    /**
     * The strip at the foot of the canvas a figure prints its own captions in, and the room that
     * leaves the drawing above them.
     *
     * Every family used to pin its caption to a fraction of the height of its own choosing - 0.86,
     * 0.88, 0.9, 0.93, 0.94 - and centre its shape on another one. Two things followed. The same
     * shape sat somewhere else depending on whether the step let it caption itself, and every
     * uncaptioned figure left the band it would have used as empty panel underneath. Measuring the
     * strip up from the bottom instead means a captioned figure and a bare one are the same
     * drawing in the same place, and the shape has the rest of the canvas to be centred in.
     */
    fun captions(lines: Int, factor: Float = CaptionFactor): CaptionStrip {
        if (lines <= 0) return CaptionStrip(height, 0f, height)
        val strip = captionMetrics(lines, factor)
        val top = height - labelGap - strip.block
        return CaptionStrip(top - labelGap, strip.step, top + strip.firstOffset)
    }

    /**
     * The same, for a figure whose drawing is [shapeHeight] tall however much room it is given.
     *
     * Those are laid out the other way round: the drawing, a gap and the caption lines make one
     * block, centred on the panel together. A shape drawn at a fixed share of the panel and then
     * centred in the room above a foot-pinned caption ends up sitting high, with the caption
     * stranded at the bottom and the space between them wider than either margin.
     *
     * Deliberately not an overload of [captions]. `captions(1, spanY)` would have bound `spanY` to
     * that one's `factor` - both take `(Int, Float)` once the default is applied, and the shorter
     * signature wins - which set a caption at a hundred times its size and left the whole figure
     * off the panel. The compiler has nothing to warn about; the name is the only guard.
     */
    fun captionsUnder(lines: Int, shapeHeight: Float, factor: Float = CaptionFactor): CaptionStrip {
        if (lines <= 0) return CaptionStrip(height, 0f, height)
        val strip = captionMetrics(lines, factor)
        val top = (height - (shapeHeight + labelGap + strip.block)) / 2f + shapeHeight + labelGap
        return CaptionStrip(top - labelGap, strip.step, top + strip.firstOffset, shapeHeight)
    }

    /**
     * How tall a block of [lines] captions at [factor] is, and how to place one whose ink starts
     * at a given y.
     *
     * Measured in ink rather than in line boxes. A line box carries a descender's worth of room
     * under the baseline that a caption in capitals never uses, so a strip sized by line boxes
     * reserved half again what it drew and left every captioned figure sitting high by the
     * difference.
     */
    private fun captionMetrics(lines: Int, factor: Float): CaptionMetrics {
        val cap = capHeight(factor)
        val leading = labelGap * 0.5f
        // The line box's own metrics, which are the font's and so the same whatever the words are.
        val sample = measure("0", labelStyle(factor))
        val inkShift = sample.size.height / 2f - sample.firstBaseline + cap / 2f
        return CaptionMetrics(
            block = lines * cap + (lines - 1) * leading,
            step = cap + leading,
            firstOffset = cap / 2f - inkShift,
        )
    }

    /**
     * How far a shape spanning [points] has to move for its own extent - grown by [pad] for
     * whatever is drawn around it - to sit in the middle of the room [room] leaves.
     *
     * A shape is not its circumcircle. A triangle drawn point-up on a circle of radius r reaches r
     * above that circle's middle and only half of r below it, so centring the circle leaves the
     * triangle itself a quarter of its own height high on the panel.
     */
    fun placeShape(points: List<Offset>, room: CaptionStrip, pad: Float = 0f): Offset {
        val minX = points.minOf { it.x } - pad
        val maxX = points.maxOf { it.x } + pad
        val minY = points.minOf { it.y } - pad
        val maxY = points.maxOf { it.y } + pad
        return Offset(width / 2f - (minX + maxX) / 2f, room.centerY - (minY + maxY) / 2f)
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
        val measured = measure(text, labelStyle(factor, bold))
        draw.drawText(
            measured,
            color = color,
            alpha = alpha,
            topLeft = Offset(insideCanvas(start.x, measured.size.width), start.y - measured.size.height / 2f),
        )
    }

    /**
     * Draw [text] on its own patch of panel, standing [gap] clear of [at] on the side [outward]
     * points to.
     *
     * [chipLabel] centres its plate on the point, so a chip nudged off a marker by a fixed
     * fraction of the panel still laid part of that plate over the marker - every plotted point
     * came out as a half disc with its own name sitting on the missing half. Here the offset is
     * the plate's own half-extent, so the two never overlap whatever size either is.
     */
    fun chipOutside(
        text: String,
        at: Offset,
        outward: Offset,
        color: Color = ink,
        factor: Float = 0.1f,
        alpha: Float = 1f,
        gap: Float = labelGap,
        bold: Boolean = true,
    ) {
        if (alpha <= 0.01f || text.isEmpty()) return
        val measured = measure(text, labelStyle(factor, bold))
        val padding = measured.size.height * ChipPadding
        val push = gap +
            abs(outward.x) * (measured.size.width / 2f + padding) +
            abs(outward.y) * (measured.size.height / 2f + padding)
        chipLabel(text, Offset(at.x + outward.x * push, at.y + outward.y * push), color, factor, alpha, bold)
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
        val measured = measure(text, labelStyle(factor, bold))
        val padding = measured.size.height * ChipPadding
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

/** The size a figure's own caption is set at, unless the family asks for another. */
internal const val CaptionFactor = 0.095f

/** How much panel a chip keeps round its text, as a share of the text's own height. */
private const val ChipPadding = 0.16f

/**
 * The shape of a caption block before it is told where to start: how tall it stands, how far
 * apart its lines are, and where the first line's centre falls relative to the top of its ink.
 */
private class CaptionMetrics(val block: Float, val step: Float, val firstOffset: Float)

/**
 * Where a figure's captions go, and where the drawing above them has to stop.
 *
 * Built by [VisualScope.captions]. A figure centres its shape on `figureBottom / 2` and prints
 * line `i` at `y(i)`, so the whole thing - shape and captions together - sits centred on the
 * panel however many lines the step allows it.
 */
internal class CaptionStrip(
    /** The lowest a figure may draw: the top of the caption block, less its clearance. */
    val figureBottom: Float,
    private val lineHeight: Float,
    private val firstY: Float,
    /** Set only by the overload that lays the drawing and its captions out as one block. */
    private val shapeHeight: Float? = null,
) {
    /** Where the [index]th caption line is centred. */
    fun y(index: Int): Float = firstY + lineHeight * index

    /**
     * Where the middle of the drawing goes: the middle of its own band when the figure has said
     * how tall it is, and the middle of the whole room when it will fill whatever it is given.
     */
    val centerY: Float get() = shapeHeight?.let { figureBottom - it / 2f } ?: (figureBottom / 2f)
}

internal val Accent: Color get() = Primary
internal val Accent2: Color get() = WorkingBlue
internal val Accent3: Color get() = GroupPlum

/**
 * The colour of the [index]th group in a figure that draws its subject as several runs.
 *
 * One list rather than a parity test in each figure. Every one of these was written as
 * `if (index % 2 == 0) Accent else Accent2` back when nothing in the catalog had a third group, so
 * the day one arrived it was painted the same colour as the first and nothing complained.
 */
internal fun groupColor(index: Int): Color = GroupColors[index % GroupColors.size]

private val GroupColors: List<Color> get() = listOf(Accent, Accent2, Accent3)

/**
 * What a figure's own arithmetic comes to, for the figures that write their sum out.
 *
 * The same green the solved formula prints its `{c:}` in and the correct option tile turns, so a
 * value reads as the answer in the picture and in the words beside it rather than only in one of
 * them. [VisualScope.resultColor] is the other half of this: that one colours a value the learner
 * put forward, this one a total the figure works out itself.
 */
internal val AnswerInk: Color get() = SuccessGreen

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
        is LearnVisual.FlatShape -> drawFlatShape(visual)
        is LearnVisual.Symmetry -> drawSymmetry(visual)
        is LearnVisual.AreaGrid -> drawAreaGrid(visual)
        is LearnVisual.RightTriangle -> drawRightTriangle(visual)
        is LearnVisual.CircleFigure -> drawCircleFigure(visual)
        is LearnVisual.AngleFigure -> drawAngleFigure(visual)
        is LearnVisual.AlgebraRect -> drawAlgebraRect(visual)
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
