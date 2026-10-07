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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalInspectionMode
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

enum class MascotMood {
    NEUTRAL,
    DELIGHTED,
    SAD,
    APPROVING,
}

internal enum class MascotPart {
    ARM,
    BODY,
    GLASSES,
    GLASSES_SHADOW,
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

private val glassesShapes: List<MascotShape> by lazy {
    mascotShapes.filter {
        it.part == MascotPart.GLASSES || it.part == MascotPart.LENS || it.part == MascotPart.GLASSES_SHADOW
    }
}

private const val BODY_BASE_Y = 645f
private const val BODY_CENTER_X = 340f
private const val BODY_HEIGHT = 630f
private val RIGHT_HEEL = Offset(390f, 815f)
private val GLASSES_HINGE = Offset(60f, 300f)
private const val GLASSES_SLIP_DEGREES = 11f
private const val GLASSES_LIFT_DEGREES = 5f
private const val GLASSES_LIFT_Y = 165f
private val FACE_ANCHOR = Offset(460f, 420f)
private val SHOULDER = Offset(40f, 425f)
private const val ARM_TUCK_DEGREES = 30f
private val ARM_TUCK_OFFSET = Offset(300f, 40f)

private const val BREATH_PERIOD_S = 3.4f
private const val SWAY_PERIOD_S = 5.3f
private const val GLASSES_LAG_S = 0.14f
private const val GLINT_DURATION_S = 0.6f
private const val TAP_DURATION_S = 0.22f
private const val BLINK_DURATION_S = 0.14f

private val FaceFront = Color(0xFFFF9678)
private val FaceSide = Color(0xFFF87B57)
private val Ink = Color(0xFF2B2622)
private val MouthRed = Color(0xFFA82D0C)
private val Tongue = Color(0xFFFF6F5E)

/**
 * Mutable pose of the rig, advanced by [step]. Squash and the glasses are damped springs so
 * that landings jiggle and the glasses overshoot. Squash: positive squashes, negative
 * stretches. Glasses: positive slips them down the nose, negative lifts them onto the forehead.
 */
private class MascotAnimator(seed: Int) {
    private val random = Random(seed)
    var time = 0f
        private set
    var lift = 0f
        private set
    var squash = 0f
        private set
    var glasses = 0f
        private set
    var toeAngle = 0f
        private set
    var glint = -1f
        private set
    var eyeOpenness = 1f
        private set
    var arm = 0f
        private set
    var mood = MascotMood.NEUTRAL
        private set

    private var squashVelocity = 0f
    private var squashTarget = 0f
    private var glassesVelocity = 0f
    private var armVelocity = 0f
    private var armTarget = 0f
    private var glassesTarget = 0f
    private var glassesTargetAtLaunch: Float? = null
    private var liftVelocity = 0f
    private var airborne = false
    private var anticipationLeft = 0f
    private var nextGlintAt = 1.5f
    private var nextFootTapAt = 4f + random.nextFloat() * 3f
    private var nextBlinkAt = 1.2f

    fun snapTo(mood: MascotMood) {
        this.mood = mood
        squash = restSquash(mood)
        squashTarget = squash
        glasses = restGlasses(mood)
        glassesTarget = glasses
        arm = restArm(mood)
        armTarget = arm
    }

    fun setMood(mood: MascotMood) {
        if (mood == this.mood) return
        this.mood = mood
        squashTarget = restSquash(mood)
        armTarget = restArm(mood)
        if (mood == MascotMood.APPROVING) {
            // A nod as the thumb comes up, and the glasses catch the light just after.
            squashVelocity += 6f
            nextGlintAt = time + 0.3f
        }
        if (mood == MascotMood.DELIGHTED) {
            jump()
            glassesTargetAtLaunch = restGlasses(mood)
        } else {
            glassesTarget = restGlasses(mood)
        }
    }

    private fun restSquash(mood: MascotMood) = if (mood == MascotMood.SAD) 0.16f else 0f

    private fun restArm(mood: MascotMood) = if (mood == MascotMood.APPROVING) 1f else 0f

    private fun restGlasses(mood: MascotMood) = when (mood) {
        MascotMood.NEUTRAL, MascotMood.APPROVING -> 0f
        MascotMood.DELIGHTED -> -1f
        MascotMood.SAD -> 1f
    }

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
        updateBlink()
    }

    private fun integrate(h: Float) {
        if (anticipationLeft > 0f) {
            anticipationLeft -= h
            if (anticipationLeft <= 0f) {
                airborne = true
                liftVelocity = 820f
                squashTarget = restSquash(mood)
                squashVelocity = -14f
                glassesTargetAtLaunch?.let { glassesTarget = it }
                glassesTargetAtLaunch = null
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
        squashVelocity += (320f * (squashTarget - squash) - 11f * squashVelocity) * h
        squash += squashVelocity * h
        glassesVelocity += (150f * (glassesTarget - glasses) - 10f * glassesVelocity) * h
        glasses += glassesVelocity * h
        armVelocity += (170f * (armTarget - arm) - 11f * armVelocity) * h
        arm += armVelocity * h
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

    // Two quick toe taps on the right foot, as if keeping time. Only a relaxed mascot taps.
    private fun updateFootTap() {
        val sinceStart = time - nextFootTapAt
        toeAngle = when {
            sinceStart < 0f -> 0f
            sinceStart < 2 * TAP_DURATION_S && mood == MascotMood.NEUTRAL -> {
                val phase = (sinceStart % TAP_DURATION_S) / TAP_DURATION_S
                -11f * sin(PI.toFloat() * phase)
            }
            else -> {
                nextFootTapAt = time + 7f + random.nextFloat() * 5f
                0f
            }
        }
    }

    private fun updateBlink() {
        val sinceStart = time - nextBlinkAt
        eyeOpenness = when {
            sinceStart < 0f -> 1f
            sinceStart < BLINK_DURATION_S -> 1f - 0.9f * sin(PI.toFloat() * sinceStart / BLINK_DURATION_S)
            else -> {
                nextBlinkAt = time + 2.2f + random.nextFloat() * 2.5f
                1f
            }
        }
    }
}

/**
 * Point-wise deformation for one frame. The body scales from its base so breathing and
 * squash keep the feet planted, and the crown sways more than the base so the brain bends
 * instead of tilting rigidly. The glasses ride the body, then turn about the ear hinge.
 */
private class MascotDeformer {
    private var bodyBreath = 0f
    private var glassesBreath = 0f
    private var squash = 0f
    private var sway = 0f
    private var lift = 0f
    private var toeCos = 1f
    private var toeSin = 0f
    private var glassesCos = 1f
    private var glassesSin = 0f
    private var glassesLift = 0f
    private var armCos = 1f
    private var armSin = 0f
    private var armTuck = 0f
    var bodyScaleX = 1f
        private set
    var bodyScaleY = 1f
        private set
    var x = 0f
        private set
    var y = 0f
        private set

    fun update(animator: MascotAnimator) {
        bodyBreath = animator.breath()
        glassesBreath = animator.breath(GLASSES_LAG_S)
        squash = animator.squash
        sway = animator.sway()
        lift = animator.lift
        val toeRadians = animator.toeAngle * PI.toFloat() / 180f
        toeCos = cos(toeRadians)
        toeSin = sin(toeRadians)
        val g = animator.glasses
        val glassesDegrees = if (g >= 0f) GLASSES_SLIP_DEGREES * g else GLASSES_LIFT_DEGREES * g
        glassesCos = cos(glassesDegrees * PI.toFloat() / 180f)
        glassesSin = sin(glassesDegrees * PI.toFloat() / 180f)
        glassesLift = if (g < 0f) GLASSES_LIFT_Y * g else 0f
        armTuck = 1f - animator.arm
        armCos = cos(ARM_TUCK_DEGREES * armTuck * PI.toFloat() / 180f)
        armSin = sin(ARM_TUCK_DEGREES * armTuck * PI.toFloat() / 180f)
        bodyScaleX = 1f - 0.007f * bodyBreath + 0.09f * squash
        bodyScaleY = 1f + 0.016f * bodyBreath - 0.13f * squash
    }

    fun map(part: MascotPart, px: Float, py: Float) {
        when (part) {
            MascotPart.BODY, MascotPart.MOUTH -> mapBody(px, py, bodyBreath)
            // Tucked, the arm sits wholly behind the body; it swings out about the shoulder.
            MascotPart.ARM -> {
                val dx = px - SHOULDER.x
                val dy = py - SHOULDER.y
                mapBody(
                    SHOULDER.x + dx * armCos - dy * armSin + ARM_TUCK_OFFSET.x * armTuck,
                    SHOULDER.y + dx * armSin + dy * armCos + ARM_TUCK_OFFSET.y * armTuck,
                    bodyBreath,
                )
            }
            MascotPart.GLASSES, MascotPart.LENS, MascotPart.GLASSES_SHADOW -> {
                mapBody(px, py, glassesBreath)
                val dx = x - GLASSES_HINGE.x
                val dy = y - GLASSES_HINGE.y
                x = GLASSES_HINGE.x + dx * glassesCos - dy * glassesSin
                y = GLASSES_HINGE.y + dx * glassesSin + dy * glassesCos + glassesLift
            }
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

private class MascotPaths(shapeCount: Int) {
    val shapes = List(shapeCount) { Path() }
    val lensClip = Path()
    val faceFill = Path()
    val faceFrontClip = Path()
    val faceSideClip = Path()
    val scratch = Path()
}

/**
 * The brain mascot, alive: it breathes, its crown sways, its sunglasses catch the light and
 * it taps a foot now and then. Tapping it makes it hop. A [mood] change plays as a reaction:
 * delighted hops and lifts the glasses to show wide eyes, sad slumps and lets them slip, and
 * approving nods and swings a thumbs up out from behind its head.
 * Inspection mode draws the settled pose of [mood] without animating.
 */
@Composable
fun Mascot(
    modifier: Modifier = Modifier,
    mood: MascotMood = MascotMood.NEUTRAL,
    seed: Int = 0,
) {
    val inInspection = LocalInspectionMode.current
    val animator = remember(seed) { MascotAnimator(seed) }
    val deformer = remember { MascotDeformer() }
    val paths = remember { MascotPaths(mascotShapes.size) }
    var frameNanos by remember { mutableLongStateOf(0L) }

    if (inInspection) {
        remember(mood) { animator.snapTo(mood) }
    } else {
        LaunchedEffect(mood) { animator.setMood(mood) }
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

    val tapModifier = if (inInspection) {
        Modifier
    } else {
        Modifier.pointerInput(animator) { detectTapGestures { animator.jump() } }
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
            drawMascot(animator, deformer, paths)
        }
    }
}

private fun DrawScope.drawMascot(animator: MascotAnimator, deformer: MascotDeformer, paths: MascotPaths) {
    val glassesMoved = abs(animator.glasses) > 0.002f
    // The traced art has no face behind the glasses, so paint one in once they move.
    if (glassesMoved) drawFaceBehindGlasses(deformer, paths)
    val mouth = when {
        animator.glasses < -0.35f -> MascotMouth.GRIN
        animator.glasses > 0.35f -> MascotMouth.FROWN
        animator.mood == MascotMood.APPROVING && animator.arm > 0.4f -> MascotMouth.SMILE
        else -> MascotMouth.TRACED
    }
    var faceDrawn = false
    paths.lensClip.rewind()
    mascotShapes.forEachIndexed { index, shape ->
        val isGlasses = shape.part == MascotPart.GLASSES || shape.part == MascotPart.LENS
        if (isGlasses && !faceDrawn) {
            faceDrawn = true
            withFaceTransform(deformer) {
                if (glassesMoved) drawEyes(animator)
                drawMouth(mouth)
            }
        }
        if (shape.part == MascotPart.ARM && animator.arm < 0.01f) return@forEachIndexed
        if (shape.part == MascotPart.MOUTH && mouth != MascotMouth.TRACED) return@forEachIndexed
        val path = paths.shapes[index]
        buildPath(path, shape, deformer, shape.part)
        drawPath(path, shape.color)
        if (shape.part == MascotPart.LENS) paths.lensClip.addPath(path)
    }
    if (animator.glint >= 0f) drawGlint(paths.lensClip, animator.glint)
}

private fun DrawScope.drawFaceBehindGlasses(deformer: MascotDeformer, paths: MascotPaths) {
    paths.faceFill.rewind()
    for (shape in glassesShapes) {
        buildPath(paths.scratch, shape, deformer, MascotPart.BODY)
        paths.faceFill.addPath(paths.scratch)
    }
    // The front panel meets the side of the head along this line, sampled above and below
    // the glasses.
    paths.faceFrontClip.setPolygon(deformer, 193f, 200f, 235f, 480f, 668f, 480f, 668f, 200f)
    paths.faceSideClip.setPolygon(deformer, 0f, 200f, 193f, 200f, 235f, 480f, 0f, 480f)
    // The outline stroke closes hairline gaps where the fill meets the body's antialiased edge.
    clipPath(paths.faceFrontClip) {
        drawPath(paths.faceFill, FaceFront)
        drawPath(paths.faceFill, FaceFront, style = faceSeamStroke)
    }
    clipPath(paths.faceSideClip) {
        drawPath(paths.faceFill, FaceSide)
        drawPath(paths.faceFill, FaceSide, style = faceSeamStroke)
    }
}

private val faceSeamStroke = Stroke(width = 6f)

private fun Path.setPolygon(deformer: MascotDeformer, vararg points: Float) {
    rewind()
    for (i in points.indices step 2) {
        deformer.map(MascotPart.BODY, points[i], points[i + 1])
        if (i == 0) moveTo(deformer.x, deformer.y) else lineTo(deformer.x, deformer.y)
    }
    close()
}

// Features are authored in rest-pose coordinates and follow the body's local scale.
private inline fun DrawScope.withFaceTransform(deformer: MascotDeformer, block: DrawScope.() -> Unit) {
    deformer.map(MascotPart.BODY, FACE_ANCHOR.x, FACE_ANCHOR.y)
    translate(deformer.x - FACE_ANCHOR.x, deformer.y - FACE_ANCHOR.y) {
        scale(deformer.bodyScaleX, deformer.bodyScaleY, pivot = FACE_ANCHOR) { block() }
    }
}

private fun DrawScope.drawEyes(animator: MascotAnimator) {
    // Fading in with the glasses keeps a sliver of eye from flickering at a lens edge.
    val alpha = ((abs(animator.glasses) - 0.1f) / 0.35f).coerceIn(0f, 1f)
    if (alpha <= 0f) return
    val openness = animator.eyeOpenness
    if (animator.glasses < 0f) {
        drawEye(Offset(335f, 345f), 30f, 40f, openness, 0f, 0f, alpha)
        drawEye(Offset(585f, 330f), 32f, 42f, openness, 0f, 0f, alpha)
        drawBrow(Offset(300f, 272f), Offset(360f, 262f), alpha)
        drawBrow(Offset(550f, 252f), Offset(615f, 250f), alpha)
    } else {
        drawEye(Offset(335f, 318f), 27f, 32f, openness, 26f, 8f, alpha)
        drawEye(Offset(585f, 300f), 28f, 34f, openness, 10f, 28f, alpha)
        drawBrow(Offset(300f, 262f), Offset(362f, 244f), alpha)
        drawBrow(Offset(552f, 236f), Offset(612f, 252f), alpha)
    }
}

private enum class MascotMouth {
    TRACED,
    GRIN,
    FROWN,
    SMILE,
}

private val mouthStroke = Stroke(width = 15f, cap = StrokeCap.Round)

private fun DrawScope.drawMouth(mouth: MascotMouth) {
    when (mouth) {
        MascotMouth.GRIN -> {
            drawPath(grinPath, MouthRed)
            drawPath(tonguePath, Tongue)
        }
        MascotMouth.FROWN -> drawPath(frownPath, MouthRed, style = mouthStroke)
        MascotMouth.SMILE -> drawPath(smilePath, MouthRed, style = mouthStroke)
        MascotMouth.TRACED -> Unit
    }
}

/** [lidLeft] and [lidRight] lower the upper lid at each corner, which is what reads as sad. */
private fun DrawScope.drawEye(
    center: Offset,
    radiusX: Float,
    radiusY: Float,
    openness: Float,
    lidLeft: Float,
    lidRight: Float,
    alpha: Float,
) {
    val eye = Rect(center.x - radiusX, center.y - radiusY * openness, center.x + radiusX, center.y + radiusY * openness)
    val lid = Path().apply {
        moveTo(eye.left - 5f, center.y - radiusY + lidLeft)
        lineTo(eye.right + 5f, center.y - radiusY + lidRight)
        lineTo(eye.right + 5f, eye.bottom + 5f)
        lineTo(eye.left - 5f, eye.bottom + 5f)
        close()
    }
    clipPath(lid) {
        drawOval(Ink, topLeft = eye.topLeft, size = eye.size, alpha = alpha)
        if (openness > 0.5f) {
            val highlightY = if (lidLeft + lidRight > 0f) center.y + radiusY * 0.1f else center.y - radiusY * 0.35f
            drawCircle(
                Color.White,
                radius = radiusX * 0.33f,
                center = Offset(center.x - radiusX * 0.28f, highlightY),
                alpha = alpha,
            )
        }
    }
}

private fun DrawScope.drawBrow(from: Offset, to: Offset, alpha: Float) {
    drawLine(Ink, from, to, strokeWidth = 16f, cap = StrokeCap.Round, alpha = alpha)
}

private val grinPath = Path().apply {
    moveTo(415f, 485f)
    quadraticTo(458f, 488f, 500f, 478f)
    quadraticTo(495f, 540f, 455f, 540f)
    quadraticTo(418f, 540f, 415f, 485f)
    close()
}

private val tonguePath = Path().apply {
    moveTo(432f, 525f)
    quadraticTo(458f, 512f, 485f, 522f)
    quadraticTo(470f, 540f, 455f, 540f)
    quadraticTo(440f, 540f, 432f, 525f)
    close()
}

private val smilePath = Path().apply {
    moveTo(424f, 500f)
    quadraticTo(462f, 526f, 496f, 494f)
}

private val frownPath = Path().apply {
    moveTo(425f, 572f)
    quadraticTo(458f, 545f, 492f, 568f)
}

private fun buildPath(path: Path, shape: MascotShape, deformer: MascotDeformer, mapAs: MascotPart) {
    path.rewind()
    for (points in shape.subpaths) {
        deformer.map(mapAs, points[0], points[1])
        path.moveTo(deformer.x, deformer.y)
        var i = 2
        while (i < points.size) {
            deformer.map(mapAs, points[i], points[i + 1])
            val x1 = deformer.x
            val y1 = deformer.y
            deformer.map(mapAs, points[i + 2], points[i + 3])
            val x2 = deformer.x
            val y2 = deformer.y
            deformer.map(mapAs, points[i + 4], points[i + 5])
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
