package com.inspiredandroid.braincup.ui.components.mascot

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalInspectionMode
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

internal enum class MascotPart {
    BODY,
    GLASSES,
    LENS,
    MOUTH,
    LEFT_LEG,
    RIGHT_LEG,
    LEFT_SHOE,
    RIGHT_SHOE,
}

internal class MascotShapeSource(
    val part: MascotPart,
    val argb: Long,
    val curves: String,
)

private class MascotShape(
    val part: MascotPart,
    val color: Color,
    val subpaths: List<FloatArray>,
)

// Each subpath is a start point followed by cubic segments of three points each.
private val mascotShapes: List<MascotShape> by lazy {
    mascotRigShapes.map { source ->
        val subpaths = source.curves.split("M ").filter { it.isNotBlank() }.map { subpath ->
            subpath.trim().split(' ').map(String::toFloat).toFloatArray()
        }
        MascotShape(source.part, Color(source.argb), subpaths)
    }
}

private const val BODY_BASE_Y = 645f
private const val BODY_CENTER_X = 340f
private const val BODY_HEIGHT = 630f
private val RIGHT_HEEL = Offset(390f, 815f)

private const val BREATH_PERIOD_S = 3.4f
private const val SWAY_PERIOD_S = 5.3f
private const val GLASSES_LAG_S = 0.14f
private const val GLINT_DURATION_S = 0.6f
private const val TAP_DURATION_S = 0.22f

/**
 * Mutable pose of the rig, advanced by [step]. Squash is a damped spring so that landings
 * jiggle like jelly; positive values squash, negative values stretch.
 */
private class MascotAnimator(seed: Int) {
    private val random = Random(seed)
    var time = 0f
        private set
    var lift = 0f
        private set
    var squash = 0f
        private set
    var toeAngle = 0f
        private set
    var glint = -1f
        private set

    private var squashVelocity = 0f
    private var squashTarget = 0f
    private var liftVelocity = 0f
    private var airborne = false
    private var anticipationLeft = 0f
    private var nextGlintAt = 1.5f
    private var nextFootTapAt = 4f + random.nextFloat() * 3f

    fun jump() {
        if (airborne || anticipationLeft > 0f) return
        anticipationLeft = 0.11f
        squashTarget = 0.55f
    }

    fun breath(lagSeconds: Float = 0f) = sin(2f * PI.toFloat() * (time - lagSeconds) / BREATH_PERIOD_S)

    fun sway() = 7f * sin(2f * PI.toFloat() * time / SWAY_PERIOD_S)

    fun step(dt: Float) {
        var remaining = min(dt, 1f / 20f)
        while (remaining > 0f) {
            val h = min(remaining, 1f / 240f)
            integrate(h)
            remaining -= h
        }
        time += dt
        updateGlint()
        updateFootTap()
    }

    private fun integrate(h: Float) {
        if (anticipationLeft > 0f) {
            anticipationLeft -= h
            if (anticipationLeft <= 0f) {
                airborne = true
                liftVelocity = 820f
                squashTarget = 0f
                squashVelocity = -14f
            }
        }
        if (airborne) {
            liftVelocity -= 3600f * h
            lift += liftVelocity * h
            if (lift <= 0f) {
                lift = 0f
                airborne = false
                squashVelocity += 9f
            }
        }
        val stiffness = 320f
        val damping = 11f
        squashVelocity += (stiffness * (squashTarget - squash) - damping * squashVelocity) * h
        squash += squashVelocity * h
    }

    private fun updateGlint() {
        if (time >= nextGlintAt) {
            glint = (time - nextGlintAt) / GLINT_DURATION_S
            if (glint > 1f) {
                glint = -1f
                nextGlintAt = time + 4f + random.nextFloat() * 4f
            }
        }
    }

    // Two quick toe taps on the right foot, as if keeping time.
    private fun updateFootTap() {
        val sinceStart = time - nextFootTapAt
        toeAngle = when {
            sinceStart < 0f -> 0f
            sinceStart < 2 * TAP_DURATION_S -> {
                val phase = (sinceStart % TAP_DURATION_S) / TAP_DURATION_S
                -11f * sin(PI.toFloat() * phase)
            }
            else -> {
                nextFootTapAt = time + 7f + random.nextFloat() * 5f
                0f
            }
        }
    }
}

/**
 * Point-wise deformation for one frame. The body scales from its base so breathing and
 * squash keep the feet planted, and the crown sways more than the base so the brain bends
 * instead of tilting rigidly.
 */
private class MascotDeformer {
    private var bodyBreath = 0f
    private var glassesBreath = 0f
    private var squash = 0f
    private var sway = 0f
    private var lift = 0f
    private var toeCos = 1f
    private var toeSin = 0f
    var x = 0f
        private set
    var y = 0f
        private set

    fun update(animator: MascotAnimator?) {
        if (animator == null) {
            bodyBreath = 0f
            glassesBreath = 0f
            squash = 0f
            sway = 0f
            lift = 0f
            toeCos = 1f
            toeSin = 0f
            return
        }
        bodyBreath = animator.breath()
        glassesBreath = animator.breath(GLASSES_LAG_S)
        squash = animator.squash
        sway = animator.sway()
        lift = animator.lift
        val radians = animator.toeAngle * PI.toFloat() / 180f
        toeCos = cos(radians)
        toeSin = sin(radians)
    }

    fun map(part: MascotPart, px: Float, py: Float) {
        when (part) {
            MascotPart.BODY, MascotPart.MOUTH -> mapBody(px, py, bodyBreath)
            MascotPart.GLASSES, MascotPart.LENS -> mapBody(px, py, glassesBreath)
            MascotPart.RIGHT_SHOE -> {
                val dx = px - RIGHT_HEEL.x
                val dy = py - RIGHT_HEEL.y
                x = RIGHT_HEEL.x + dx * toeCos - dy * toeSin
                y = RIGHT_HEEL.y + dx * toeSin + dy * toeCos - lift
            }
            MascotPart.LEFT_LEG, MascotPart.RIGHT_LEG, MascotPart.LEFT_SHOE -> {
                x = px
                y = py - lift
            }
        }
    }

    private fun mapBody(px: Float, py: Float, breath: Float) {
        val height = ((BODY_BASE_Y - py) / BODY_HEIGHT).coerceAtLeast(0f)
        val scaleY = 1f + 0.016f * breath - 0.13f * squash
        val scaleX = 1f - 0.007f * breath + 0.09f * squash
        x = BODY_CENTER_X + (px - BODY_CENTER_X) * scaleX + sway * height * height
        y = BODY_BASE_Y - (BODY_BASE_Y - py) * scaleY - lift
    }
}

/**
 * The brain mascot, alive: it breathes, its crown sways, its sunglasses catch the light and
 * it taps a foot now and then. Tapping it makes it hop. Inspection mode draws the rest pose,
 * which matches the `ic_mascot` drawable.
 */
@Composable
fun Mascot(
    modifier: Modifier = Modifier,
    seed: Int = 0,
) {
    val inInspection = LocalInspectionMode.current
    val animator = remember(seed) { if (inInspection) null else MascotAnimator(seed) }
    val deformer = remember { MascotDeformer() }
    val paths = remember { mascotShapes.map { Path() } }
    val lensClip = remember { Path() }
    var frameNanos by remember { mutableLongStateOf(0L) }

    if (animator != null) {
        LaunchedEffect(animator) {
            var last = withFrameNanos { it }
            while (true) {
                withFrameNanos { now ->
                    animator.step((now - last) / 1_000_000_000f)
                    last = now
                    frameNanos = now
                }
            }
        }
    }

    val tapModifier = if (animator != null) {
        Modifier.pointerInput(animator) { detectTapGestures { animator.jump() } }
    } else {
        Modifier
    }

    Canvas(
        modifier = modifier
            .aspectRatio(MASCOT_VIEWPORT_WIDTH / MASCOT_VIEWPORT_HEIGHT)
            .then(tapModifier),
    ) {
        // Reading the frame state is what redraws the canvas every frame.
        frameNanos
        deformer.update(animator)
        scale(size.width / MASCOT_VIEWPORT_WIDTH, pivot = Offset.Zero) {
            lensClip.rewind()
            mascotShapes.forEachIndexed { index, shape ->
                val path = paths[index]
                buildPath(path, shape, deformer)
                drawPath(path, shape.color)
                if (shape.part == MascotPart.LENS) lensClip.addPath(path)
            }
            val glint = animator?.glint ?: -1f
            if (glint >= 0f) drawGlint(lensClip, glint)
        }
    }
}

private fun buildPath(path: Path, shape: MascotShape, deformer: MascotDeformer) {
    path.rewind()
    for (points in shape.subpaths) {
        deformer.map(shape.part, points[0], points[1])
        path.moveTo(deformer.x, deformer.y)
        var i = 2
        while (i < points.size) {
            deformer.map(shape.part, points[i], points[i + 1])
            val x1 = deformer.x
            val y1 = deformer.y
            deformer.map(shape.part, points[i + 2], points[i + 3])
            val x2 = deformer.x
            val y2 = deformer.y
            deformer.map(shape.part, points[i + 4], points[i + 5])
            path.cubicTo(x1, y1, x2, y2, deformer.x, deformer.y)
            i += 6
        }
    }
}

private val glintBrush = Brush.horizontalGradient(
    0f to Color.Transparent,
    0.5f to Color.White.copy(alpha = 0.55f),
    1f to Color.Transparent,
    startX = -36f,
    endX = 36f,
)

private fun DrawScope.drawGlint(lensClip: Path, progress: Float) {
    val eased = 1f - (1f - progress) * (1f - progress)
    val centerX = 200f + eased * 540f
    clipPath(lensClip) {
        rotate(degrees = 22f, pivot = Offset(centerX, 345f)) {
            translate(left = centerX, top = 0f) {
                drawRect(
                    brush = glintBrush,
                    topLeft = Offset(-36f, 150f),
                    size = Size(72f, 400f),
                )
            }
        }
    }
}
