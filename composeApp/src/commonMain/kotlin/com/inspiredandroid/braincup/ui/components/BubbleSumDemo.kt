package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.bubble_sum_demo_blink
import braincup.composeapp.generated.resources.bubble_sum_demo_correct
import braincup.composeapp.generated.resources.bubble_sum_demo_title
import com.inspiredandroid.braincup.ui.theme.FlashCrowdYellow
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import com.inspiredandroid.braincup.ui.theme.numberFontFamily
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private const val Radius = 0.068f
private const val DemoSpeed = 0.14f
private const val FrameDelayMillis = 16L
private const val FrameDeltaSeconds = FrameDelayMillis / 1000f
private const val VisibleMillis = 300L
private const val WarningMillis = 1500L
private const val HiddenMillis = 900L
private const val IntroMillis = 700L
private const val RevealHoldMillis = 1600L

/**
 * Only this bubble's number warns and hides. The real game staggers the blink so numbers never
 * all vanish at once, and a single blinking bubble also keeps the rest readable as an example.
 */
private const val BlinkingBubble = 0

private enum class BubbleDemoPhase { INTRO, BLINK, REVEAL }
private enum class DemoNumberState { VISIBLE, WARNING, HIDDEN }

private class DemoBubble(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val value: Int,
) {
    fun copy() = DemoBubble(x, y, vx, vy, value)
}

private fun bubble(x: Float, y: Float, angleDeg: Float, value: Int): DemoBubble {
    val angle = angleDeg / 180f * PI.toFloat()
    return DemoBubble(x, y, cos(angle) * DemoSpeed, sin(angle) * DemoSpeed, value)
}

private val InitialBubbles = listOf(
    bubble(0.28f, 0.32f, 40f, 3),
    bubble(0.68f, 0.30f, 140f, 7),
    bubble(0.50f, 0.68f, 260f, 2),
)

private val DemoSum = InitialBubbles.sumOf { it.value }

private fun step(current: List<DemoBubble>, delta: Float): List<DemoBubble> {
    val bubbles = current.map { it.copy() }
    for (b in bubbles) {
        b.x += b.vx * delta
        b.y += b.vy * delta
        if (b.x - Radius < 0f) {
            b.x = Radius
            b.vx = -b.vx
        }
        if (b.x + Radius > 1f) {
            b.x = 1f - Radius
            b.vx = -b.vx
        }
        if (b.y - Radius < 0f) {
            b.y = Radius
            b.vy = -b.vy
        }
        if (b.y + Radius > 1f) {
            b.y = 1f - Radius
            b.vy = -b.vy
        }
    }
    for (i in bubbles.indices) {
        for (j in i + 1 until bubbles.size) {
            val a = bubbles[i]
            val b = bubbles[j]
            val dx = b.x - a.x
            val dy = b.y - a.y
            val dist = sqrt(dx * dx + dy * dy)
            val minDist = Radius * 2
            if (dist < minDist && dist > 0.0001f) {
                val nx = dx / dist
                val ny = dy / dist
                val dvx = a.vx - b.vx
                val dvy = a.vy - b.vy
                val dvDotN = dvx * nx + dvy * ny
                if (dvDotN > 0) {
                    a.vx -= dvDotN * nx
                    a.vy -= dvDotN * ny
                    b.vx += dvDotN * nx
                    b.vy += dvDotN * ny
                }
                val overlap = (minDist - dist) / 2f
                a.x -= overlap * nx
                a.y -= overlap * ny
                b.x += overlap * nx
                b.y += overlap * ny
            }
        }
    }
    for (b in bubbles) {
        val speed = sqrt(b.vx * b.vx + b.vy * b.vy)
        if (speed > 0.0001f) {
            b.vx = b.vx / speed * DemoSpeed
            b.vy = b.vy / speed * DemoSpeed
        }
    }
    return bubbles
}

@Composable
fun BubbleSumDemo(modifier: Modifier = Modifier) {
    var phase by remember { mutableStateOf(BubbleDemoPhase.INTRO) }
    var bubbles by remember { mutableStateOf(InitialBubbles.map { it.copy() }) }
    var numberState by remember { mutableStateOf(DemoNumberState.VISIBLE) }
    var loop by remember { mutableIntStateOf(0) }
    val textMeasurer = rememberTextMeasurer()
    val outlineColor = MaterialTheme.colorScheme.outline
    val mutedFace = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
    val digitStyle = TextStyle(
        color = Color.White,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = numberFontFamily(),
    )
    val warningDigitStyle = digitStyle.copy(color = Color(0xFF1A1A1A))

    LaunchedEffect(loop) {
        bubbles = InitialBubbles.map { it.copy() }
        phase = BubbleDemoPhase.INTRO
        numberState = DemoNumberState.VISIBLE
        val introFrames = (IntroMillis / FrameDelayMillis).toInt()
        repeat(introFrames) {
            bubbles = step(bubbles, FrameDeltaSeconds)
            delay(FrameDelayMillis)
        }
        phase = BubbleDemoPhase.BLINK
        // One cycle only: the point lands as soon as the number warns and goes, and holding the
        // loop longer just delays the sum the demo is there to show.
        numberState = DemoNumberState.VISIBLE
        val onFrames = (VisibleMillis / FrameDelayMillis).toInt()
        repeat(onFrames) {
            bubbles = step(bubbles, FrameDeltaSeconds)
            delay(FrameDelayMillis)
        }
        numberState = DemoNumberState.WARNING
        val warnFrames = (WarningMillis / FrameDelayMillis).toInt()
        repeat(warnFrames) {
            bubbles = step(bubbles, FrameDeltaSeconds)
            delay(FrameDelayMillis)
        }
        numberState = DemoNumberState.HIDDEN
        val offFrames = (HiddenMillis / FrameDelayMillis).toInt()
        repeat(offFrames) {
            bubbles = step(bubbles, FrameDeltaSeconds)
            delay(FrameDelayMillis)
        }
        phase = BubbleDemoPhase.REVEAL
        numberState = DemoNumberState.VISIBLE
        val revealFrames = (RevealHoldMillis / FrameDelayMillis).toInt()
        repeat(revealFrames) {
            bubbles = step(bubbles, FrameDeltaSeconds)
            delay(FrameDelayMillis)
        }
        loop++
    }

    val caption = when (phase) {
        BubbleDemoPhase.INTRO -> Res.string.bubble_sum_demo_title
        BubbleDemoPhase.BLINK -> Res.string.bubble_sum_demo_blink
        BubbleDemoPhase.REVEAL -> Res.string.bubble_sum_demo_correct
    }
    val captions = persistentListOf(
        Res.string.bubble_sum_demo_title,
        Res.string.bubble_sum_demo_blink,
        Res.string.bubble_sum_demo_correct,
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DemoCaption(
            current = caption,
            all = captions,
            emphasis = persistentSetOf(Res.string.bubble_sum_demo_correct),
        )
        Spacer(Modifier.height(8.dp))
        Canvas(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .fillMaxWidth(0.85f)
                .aspectRatio(1f),
        ) {
            drawRect(color = outlineColor, style = Stroke(width = 2.dp.toPx()))
            val radiusPx = Radius * size.width
            bubbles.forEachIndexed { index, bubble ->
                val center = Offset(bubble.x * size.width, bubble.y * size.height)
                val state = if (index == BlinkingBubble) {
                    numberState
                } else {
                    DemoNumberState.VISIBLE
                }
                val face = when {
                    phase == BubbleDemoPhase.REVEAL -> SuccessGreen
                    state == DemoNumberState.VISIBLE -> Primary
                    state == DemoNumberState.WARNING -> FlashCrowdYellow
                    else -> mutedFace
                }
                drawPrismCircle(center = center, radius = radiusPx, face = face)
                if (state != DemoNumberState.HIDDEN) {
                    val style =
                        if (state == DemoNumberState.WARNING) warningDigitStyle else digitStyle
                    val measured = textMeasurer.measure(
                        text = bubble.value.toString(),
                        style = style,
                    )
                    drawText(
                        textLayoutResult = measured,
                        topLeft = Offset(
                            center.x - measured.size.width / 2f,
                            center.y - measured.size.height / 2f,
                        ),
                    )
                }
            }
        }
        if (phase == BubbleDemoPhase.REVEAL) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "= $DemoSum",
                style = MaterialTheme.typography.titleLarge,
                fontFamily = numberFontFamily(),
                color = SuccessGreen,
                fontWeight = FontWeight.Bold,
            )
        } else {
            Spacer(Modifier.height(8.dp))
            Text(
                text = " ",
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}
