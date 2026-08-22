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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.TextUnit
import com.inspiredandroid.braincup.learn.LearnVisual
import com.inspiredandroid.braincup.ui.components.hoverHand
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import kotlinx.coroutines.launch

private val EntranceSpec = tween<Float>(durationMillis = 1100, easing = FastOutSlowInEasing)

/**
 * The animated diagram for one lesson step.
 *
 * Everything is drawn from the theme colours so the sketches stay legible in light, dark and OLED
 * without per-theme assets. The whole figure animates in on first appearance — dots slide together,
 * bars grow, sides draw on — and tapping it replays that animation, because the movement is often
 * the explanation.
 */
@Composable
fun LearnVisualCanvas(
    visual: LearnVisual,
    modifier: Modifier = Modifier,
    ink: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    val progress = remember(visual) { Animatable(0f) }
    LaunchedEffect(visual) { progress.animateTo(1f, EntranceSpec) }
    val measurer = rememberTextMeasurer()
    val replayScope = rememberCoroutineScope()
    val interactions = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .hoverHand()
            .clickable(interactionSource = interactions, indication = null) {
                replayScope.launch {
                    progress.snapTo(0f)
                    progress.animateTo(1f, EntranceSpec)
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val scope = VisualScope(this, progress.value, ink, measurer)
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
) {
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
    )

    fun measure(text: String, style: TextStyle): TextLayoutResult = measurer.measure(text, style)

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
            topLeft = Offset(center.x - measured.size.width / 2f, center.y - measured.size.height / 2f),
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
        draw.drawText(measured, topLeft = Offset(start.x, start.y - measured.size.height / 2f))
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

    fun box(topLeft: Offset, size: Size, fill: Color? = null, outline: Color? = ink, alpha: Float = 1f) {
        if (alpha <= 0.01f) return
        fill?.let { draw.drawRect(it.copy(alpha = it.alpha * alpha), topLeft, size) }
        outline?.let {
            draw.drawRect(it.copy(alpha = it.alpha * alpha), topLeft, size, style = Stroke(width = stroke))
        }
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
        is LearnVisual.PlaceValue -> drawPlaceValue(visual)
        is LearnVisual.DecimalGrid -> drawDecimalGrid(visual)
        is LearnVisual.ArrayDots -> drawArrayDots(visual)
        is LearnVisual.Fraction -> drawFraction(visual)
        is LearnVisual.Coins -> drawCoins(visual)
        is LearnVisual.Ruler -> drawRuler(visual)
        is LearnVisual.Clock -> drawClock(visual)
        is LearnVisual.Steps -> drawSteps(visual)
        is LearnVisual.Polygon -> drawPolygon(visual)
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
