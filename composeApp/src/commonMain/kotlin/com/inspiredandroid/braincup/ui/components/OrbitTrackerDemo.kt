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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.game_remember_targets
import braincup.composeapp.generated.resources.game_tap_original_targets
import braincup.composeapp.generated.resources.orbit_tracker_demo_correct
import braincup.composeapp.generated.resources.orbit_tracker_demo_title
import braincup.composeapp.generated.resources.orbit_tracker_demo_watch
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// Normalized 0..1 arena. These mirror OrbitTrackerGame exactly so the demo moves with the same
// physics as the real game: BALL_RADIUS, a per-second speed, and a 16ms frame.
private const val Radius = 0.04f
private const val DemoSpeed = 0.15f
private const val FrameDelayMillis = 16L
private const val FrameDeltaSeconds = FrameDelayMillis / 1000f

private const val HighlightMillis = 1300L
private const val MoveDurationMillis = 4200L
private const val AnswerStartPauseMillis = 500L
private const val RingHoldMillis = 380L
private const val ConfirmGapMillis = 260L
private const val DoneHoldMillis = 1300L

// Targets flash on/off roughly every quarter second while moving, so the viewer can follow them.
private const val BlinkHalfPeriodFrames = 16

private enum class OrbitDemoPhase { HIGHLIGHT, MOVE, ANSWER, DONE }

private class DemoBall(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val isTarget: Boolean,
) {
    fun copy() = DemoBall(x, y, vx, vy, isTarget)
}

private fun ball(x: Float, y: Float, angleDeg: Float, isTarget: Boolean): DemoBall {
    val angle = angleDeg / 180f * PI.toFloat()
    return DemoBall(x, y, cos(angle) * DemoSpeed, sin(angle) * DemoSpeed, isTarget)
}

// A fixed 6-ball layout with 2 targets and hand-picked headings, so the demo plays out the same
// every loop. Velocities all start at DemoSpeed; the real game's re-normalization keeps them there.
private val InitialBalls = listOf(
    ball(0.22f, 0.26f, 35f, isTarget = true),
    ball(0.72f, 0.28f, 150f, isTarget = false),
    ball(0.45f, 0.58f, 320f, isTarget = true),
    ball(0.80f, 0.72f, 210f, isTarget = false),
    ball(0.30f, 0.80f, 280f, isTarget = false),
    ball(0.60f, 0.46f, 120f, isTarget = false),
)

private val TargetIndices = InitialBalls.mapIndexedNotNull { index, ball -> index.takeIf { ball.isTarget } }

// One physics frame, copied verbatim from OrbitTrackerGame.updateBallPositions: integrate, bounce
// off walls, resolve approaching ball-to-ball collisions with overlap separation, then re-normalize
// each speed back to DemoSpeed. Works on fresh copies so reassigning the list recomposes the Canvas.
private fun step(current: List<DemoBall>, delta: Float): List<DemoBall> {
    val balls = current.map { it.copy() }

    for (ball in balls) {
        ball.x += ball.vx * delta
        ball.y += ball.vy * delta

        if (ball.x - Radius < 0f) {
            ball.x = Radius
            ball.vx = -ball.vx
        }
        if (ball.x + Radius > 1f) {
            ball.x = 1f - Radius
            ball.vx = -ball.vx
        }
        if (ball.y - Radius < 0f) {
            ball.y = Radius
            ball.vy = -ball.vy
        }
        if (ball.y + Radius > 1f) {
            ball.y = 1f - Radius
            ball.vy = -ball.vy
        }
    }

    for (i in balls.indices) {
        for (j in i + 1 until balls.size) {
            val a = balls[i]
            val b = balls[j]
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

    for (ball in balls) {
        val speed = sqrt(ball.vx * ball.vx + ball.vy * ball.vy)
        if (speed > 0.0001f) {
            ball.vx = ball.vx / speed * DemoSpeed
            ball.vy = ball.vy / speed * DemoSpeed
        }
    }

    return balls
}

// Every caption the demo cycles through, so the caption line can reserve the tallest one's height.
private val DemoCaptions = listOf(
    Res.string.game_remember_targets,
    Res.string.orbit_tracker_demo_watch,
    Res.string.game_tap_original_targets,
    Res.string.orbit_tracker_demo_correct,
)

/**
 * Animated tutorial for Orbit Tracker: a small arena that highlights two target balls, drifts all of
 * them around with the real game's physics (wall and ball-to-ball collisions) while the targets blink
 * so they can be followed, then "taps" the originals (selection ring, then green) once they stop, and
 * loops. No interaction needed; it plays on its own like [SchulteTableDemo], reusing the real game's
 * [drawPrismCircle] balls and colors.
 */
@Composable
fun OrbitTrackerDemo(modifier: Modifier = Modifier) {
    var phase by remember { mutableStateOf(OrbitDemoPhase.HIGHLIGHT) }
    var balls by remember { mutableStateOf(InitialBalls) }
    var ringIndex by remember { mutableIntStateOf(-1) }
    var confirmed by remember { mutableStateOf(emptySet<Int>()) }
    var blinkOn by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            balls = InitialBalls
            ringIndex = -1
            confirmed = emptySet()
            blinkOn = true
            phase = OrbitDemoPhase.HIGHLIGHT
            delay(HighlightMillis)

            phase = OrbitDemoPhase.MOVE
            val frames = (MoveDurationMillis / FrameDelayMillis).toInt()
            for (frame in 0 until frames) {
                balls = step(balls, FrameDeltaSeconds)
                blinkOn = (frame / BlinkHalfPeriodFrames) % 2 == 0
                delay(FrameDelayMillis)
            }

            phase = OrbitDemoPhase.ANSWER
            delay(AnswerStartPauseMillis)
            for (index in TargetIndices) {
                ringIndex = index
                delay(RingHoldMillis)
                confirmed = confirmed + index
                ringIndex = -1
                delay(ConfirmGapMillis)
            }

            phase = OrbitDemoPhase.DONE
            delay(DoneHoldMillis)
        }
    }

    val arenaMax = if (LocalIsCompactHeight.current) 200.dp else 260.dp
    val outlineColor = MaterialTheme.colorScheme.outline
    val idleColor = MaterialTheme.colorScheme.onSurfaceVariant

    val captionRes = when (phase) {
        OrbitDemoPhase.HIGHLIGHT -> Res.string.game_remember_targets
        OrbitDemoPhase.MOVE -> Res.string.orbit_tracker_demo_watch
        OrbitDemoPhase.ANSWER -> Res.string.game_tap_original_targets
        OrbitDemoPhase.DONE -> Res.string.orbit_tracker_demo_correct
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.orbit_tracker_demo_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(16.dp))

        Canvas(
            modifier = Modifier
                .widthIn(max = arenaMax)
                .fillMaxWidth()
                .aspectRatio(1f),
        ) {
            drawRect(color = outlineColor, style = Stroke(width = 2.dp.toPx()))

            val ballRadiusPx = Radius * size.width
            balls.forEachIndexed { index, ball ->
                val center = Offset(ball.x * size.width, ball.y * size.height)
                val showTarget = ball.isTarget &&
                    (phase == OrbitDemoPhase.HIGHLIGHT || (phase == OrbitDemoPhase.MOVE && blinkOn))
                val color = when {
                    index in confirmed -> SuccessGreen
                    showTarget -> Primary
                    else -> idleColor
                }
                drawPrismCircle(center = center, radius = ballRadiusPx, face = color)

                if (index == ringIndex) {
                    drawCircle(
                        color = Primary,
                        radius = ballRadiusPx + 3.dp.toPx(),
                        center = center,
                        style = Stroke(width = 2.dp.toPx()),
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        DemoCaption(current = captionRes, all = DemoCaptions)
    }
}
