package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.inspiredandroid.braincup.ui.theme.HeadCountDoor
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
private const val HouseDepth = 0.025f
private const val PersonHeight = 0.3f
private const val QueueSpacing = 0.22f
private const val RevealPersonHeight = 0.18f
private const val RevealRowRise = 0.21f
private const val RevealPerRow = 5

/**
 * The house and the people crossing its doorstep.
 *
 * One move is drawn at [walkProgress] 0..1: [entering] people queue in from one side and vanish
 * into the door, [leaving] people step out of it and walk off the other side. A batch walks at one
 * pace in single file, so the last of them reaches the door exactly at 1.
 *
 * With [revealedOccupants] set the roof lifts and the front wall is drawn as a cutaway with that
 * many people standing inside, which is how the answer is shown: as the house the player was
 * keeping count of, not as a bare number.
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
            drawHouse(roofLift = RoofLift, isCutaway = true)
            drawOccupants(revealedOccupants)
        } else {
            drawHouse(roofLift = 0f, isCutaway = false)
        }
        drawEntering(entering, entersFromLeft, walkProgress)
        drawLeaving(leaving, leavesToRight = entersFromLeft, walkProgress)
    }
}

/** Every part sits [HouseDepth] above [GroundY], so its extrusion is what lands on the ground. */
private fun DrawScope.drawHouse(roofLift: Float, isCutaway: Boolean) {
    val w = size.width
    val h = size.height
    val depth = HouseDepth * h
    val base = GroundY * h - depth
    drawPrismPolygon(
        points = chamferRect(WallLeft * w, WallTop * h, WallRight * w, base, cut = depth * 1.5f),
        face = HeadCountWall,
        depth = depth,
    )
    if (!isCutaway) {
        listOf(0.39f, 0.56f).forEach { left ->
            drawPrismPolygon(
                points = chamferRect(left * w, 0.52f * h, (left + 0.05f) * w, 0.62f * h, cut = depth * 0.6f),
                face = HeadCountWindow,
                depth = depth * 0.4f,
            )
        }
        drawPrismPolygon(
            points = chamferRect((0.5f - DoorHalfWidth) * w, DoorTop * h, (0.5f + DoorHalfWidth) * w, base, cut = depth),
            face = HeadCountDoor,
            depth = depth * 0.4f,
        )
    }
    val eaves = (WallTop - roofLift + 0.02f) * h
    drawPrismPolygon(
        points = listOf(
            Offset((WallLeft - RoofOverhang) * w, eaves),
            Offset(0.5f * w, (RoofPeak - roofLift) * h),
            Offset((WallRight + RoofOverhang) * w, eaves),
        ),
        face = HeadCountRoof,
        depth = depth,
    )
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
        withAlpha(fadeNearDoor(doorX - x, h)) {
            drawWalker(x = if (fromLeft) x else w - x, colorIndex = j)
        }
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
        withAlpha(fadeNearDoor(x - doorX, h)) {
            drawWalker(x = if (leavesToRight) x else w - x, colorIndex = j + 2)
        }
    }
}

/** People melt into the doorway rather than popping, so a batch never seems to lose someone. */
private fun fadeNearDoor(distance: Float, height: Float): Float = (distance / (PersonHeight * height * 0.35f)).coerceIn(0f, 1f)

private fun DrawScope.drawWalker(x: Float, colorIndex: Int) {
    val h = size.height
    val personHeight = PersonHeight * h
    val bob = abs(sin(x / (personHeight * 0.45f) * PI.toFloat())) * personHeight * 0.04f
    drawPerson(
        centerX = x,
        feetY = GroundY * h - bob,
        height = personHeight,
        color = HeadCountPeople[colorIndex % HeadCountPeople.size],
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
    val depth = height * 0.07f
    val base = feetY - depth
    val bodyWidth = height * 0.48f
    val bodyTop = base - height * 0.55f
    drawPrismPolygon(
        points = chamferRect(centerX - bodyWidth / 2, bodyTop, centerX + bodyWidth / 2, base, cut = bodyWidth * 0.2f),
        face = color,
        depth = depth,
    )
    val headSize = height * 0.3f
    val headBottom = bodyTop - height * 0.06f
    drawPrismPolygon(
        points = chamferRect(centerX - headSize / 2, headBottom - headSize, centerX + headSize / 2, headBottom, cut = headSize * 0.3f),
        face = color,
        depth = depth,
    )
}

/** The prism silhouette: a rectangle with its top-right and bottom-left corners cut. */
private fun chamferRect(left: Float, top: Float, right: Float, bottom: Float, cut: Float): List<Offset> = listOf(
    Offset(left, top),
    Offset(right - cut, top),
    Offset(right, top + cut),
    Offset(right, bottom),
    Offset(left + cut, bottom),
    Offset(left, bottom - cut),
)

/** A prism is drawn back to front, so it has to fade as one layer or its back shows through. */
private inline fun DrawScope.withAlpha(alpha: Float, block: DrawScope.() -> Unit) {
    if (alpha >= 1f) {
        block()
        return
    }
    if (alpha <= 0f) return
    drawContext.canvas.saveLayer(Rect(Offset.Zero, size), Paint().apply { this.alpha = alpha })
    block()
    drawContext.canvas.restore()
}
