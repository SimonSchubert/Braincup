package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.inspiredandroid.braincup.ui.theme.HeadCountDoor
import com.inspiredandroid.braincup.ui.theme.HeadCountGround
import com.inspiredandroid.braincup.ui.theme.HeadCountPeople
import com.inspiredandroid.braincup.ui.theme.HeadCountRoof
import com.inspiredandroid.braincup.ui.theme.HeadCountWall
import com.inspiredandroid.braincup.ui.theme.HeadCountWindow
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

/** Width over height of the scene; callers size it with `aspectRatio`. */
const val HeadCountSceneAspect = 2f

private const val GroundY = 0.86f
private const val WallTop = 0.44f
private const val WallLeft = 0.33f
private const val WallRight = 0.67f
private const val RoofOverhang = 0.04f
private const val RoofPeak = 0.18f
private const val RoofLift = 0.16f
private const val DoorHalfWidth = 0.045f
private const val DoorTop = 0.62f
private const val PersonHeight = 0.3f
private const val QueueSpacing = 0.22f
private const val RevealPersonHeight = 0.18f
private const val RevealRowRise = 0.21f
private const val RevealedHouseAlpha = 0.3f
private const val RevealPerRow = 5

/**
 * The house and the people crossing its doorstep.
 *
 * One move is drawn at [walkProgress] 0..1: [entering] people queue in from one side and vanish
 * into the door, [leaving] people step out of it and walk off the other side. A batch walks at one
 * pace in single file, so the last of them reaches the door exactly at 1.
 *
 * With [revealedOccupants] set the roof lifts, the house fades and that many people show through
 * it, which is how the answer is shown: as the house the player was keeping count of, not as a
 * bare number.
 */
@Composable
fun HeadCountHouse(
    entering: Int,
    leaving: Int,
    entersFromLeft: Boolean,
    walkProgress: Float,
    modifier: Modifier = Modifier,
    revealedOccupants: Int? = null,
) {
    Canvas(modifier = modifier.clipToBounds()) {
        if (revealedOccupants != null) {
            drawHouse(roofLift = RoofLift, alpha = RevealedHouseAlpha)
            drawOccupants(revealedOccupants)
        } else {
            drawHouse(roofLift = 0f, alpha = 1f)
        }
        drawEntering(entering, entersFromLeft, walkProgress)
        drawLeaving(leaving, leavesToRight = entersFromLeft, walkProgress)
    }
}

private fun DrawScope.drawHouse(roofLift: Float, alpha: Float) {
    val w = size.width
    val h = size.height
    drawLine(
        color = HeadCountGround,
        start = Offset(0f, GroundY * h),
        end = Offset(w, GroundY * h),
        strokeWidth = h * 0.012f,
    )
    drawRect(
        color = HeadCountWall,
        topLeft = Offset(WallLeft * w, WallTop * h),
        size = Size((WallRight - WallLeft) * w, (GroundY - WallTop) * h),
        alpha = alpha,
    )
    val window = Size(0.05f * w, 0.1f * h)
    listOf(0.39f, 0.56f).forEach { left ->
        drawRect(color = HeadCountWindow, topLeft = Offset(left * w, 0.52f * h), size = window, alpha = alpha)
    }
    drawRoundRect(
        alpha = alpha,
        color = HeadCountDoor,
        topLeft = Offset((0.5f - DoorHalfWidth) * w, DoorTop * h),
        size = Size(DoorHalfWidth * 2 * w, (GroundY - DoorTop) * h),
        cornerRadius = CornerRadius(DoorHalfWidth * w * 0.5f),
    )
    val roofBase = (WallTop - roofLift) * h
    val roof = Path().apply {
        moveTo((WallLeft - RoofOverhang) * w, roofBase + 0.02f * h)
        lineTo(0.5f * w, (RoofPeak - roofLift) * h)
        lineTo((WallRight + RoofOverhang) * w, roofBase + 0.02f * h)
        close()
    }
    drawPath(roof, HeadCountRoof, alpha = alpha)
}

private fun DrawScope.drawEntering(count: Int, fromLeft: Boolean, progress: Float) {
    if (count <= 0) return
    val w = size.width
    val h = size.height
    val doorX = 0.5f * w
    val startX = PersonHeight * h * 0.4f
    val spacing = QueueSpacing * h
    val travel = doorX - startX + (count - 1) * spacing
    repeat(count) { j ->
        val x = startX - j * spacing + progress * travel
        if (x >= doorX) return@repeat
        drawWalker(
            x = if (fromLeft) x else w - x,
            alpha = fadeNearDoor(doorX - x, h),
            colorIndex = j,
        )
    }
}

private fun DrawScope.drawLeaving(count: Int, leavesToRight: Boolean, progress: Float) {
    if (count <= 0) return
    val w = size.width
    val h = size.height
    val doorX = 0.5f * w
    val endX = w + PersonHeight * h * 0.4f
    val spacing = QueueSpacing * h
    val travel = endX - doorX + (count - 1) * spacing
    repeat(count) { j ->
        val x = doorX + progress * travel - j * spacing
        if (x <= doorX) return@repeat
        drawWalker(
            x = if (leavesToRight) x else w - x,
            alpha = fadeNearDoor(x - doorX, h),
            colorIndex = j + 2,
        )
    }
}

/** People melt into the doorway rather than popping, so a batch never seems to lose someone. */
private fun fadeNearDoor(distance: Float, height: Float): Float = (distance / (PersonHeight * height * 0.35f)).coerceIn(0f, 1f)

private fun DrawScope.drawWalker(x: Float, alpha: Float, colorIndex: Int) {
    val h = size.height
    val personHeight = PersonHeight * h
    val bob = abs(sin(x / (personHeight * 0.45f) * PI.toFloat())) * personHeight * 0.04f
    drawPerson(
        centerX = x,
        feetY = GroundY * h - bob,
        height = personHeight,
        color = HeadCountPeople[colorIndex % HeadCountPeople.size].copy(alpha = alpha),
    )
}

/** Up to two rows inside the walls, apart from each other so every person can be counted. */
private fun DrawScope.drawOccupants(count: Int) {
    val w = size.width
    val h = size.height
    val personHeight = RevealPersonHeight * h
    val spacing = (WallRight - WallLeft) * w / (RevealPerRow + 0.5f)
    val front = count.coerceAtMost(RevealPerRow)
    val back = count - front
    listOf(1 to back, 0 to front).forEach { (row, inRow) ->
        val rowWidth = (inRow - 1) * spacing
        repeat(inRow) { i ->
            drawPerson(
                centerX = 0.5f * w - rowWidth / 2 + i * spacing,
                feetY = (GroundY - 0.02f - row * RevealRowRise) * h,
                height = personHeight,
                color = HeadCountPeople[(i + row * RevealPerRow) % HeadCountPeople.size],
            )
        }
    }
}

private fun DrawScope.drawPerson(centerX: Float, feetY: Float, height: Float, color: Color) {
    val headRadius = height * 0.17f
    drawCircle(color = color, radius = headRadius, center = Offset(centerX, feetY - height + headRadius))
    val bodyWidth = height * 0.5f
    val bodyTop = feetY - height * 0.6f
    drawRoundRect(
        color = color,
        topLeft = Offset(centerX - bodyWidth / 2, bodyTop),
        size = Size(bodyWidth, feetY - bodyTop),
        cornerRadius = CornerRadius(bodyWidth * 0.4f),
    )
}
