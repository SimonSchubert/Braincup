package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.app.RailYardUiState
import com.inspiredandroid.braincup.games.RailYardGame
import com.inspiredandroid.braincup.games.tools.ColorPattern
import com.inspiredandroid.braincup.games.tools.GameColor
import com.inspiredandroid.braincup.games.tools.composeColor
import com.inspiredandroid.braincup.games.tools.visiblePattern
import com.inspiredandroid.braincup.ui.theme.ErrorRed
import com.inspiredandroid.braincup.ui.theme.RailYardClosedRail
import com.inspiredandroid.braincup.ui.theme.RailYardClosedSleeper
import com.inspiredandroid.braincup.ui.theme.RailYardFrame
import com.inspiredandroid.braincup.ui.theme.RailYardGround
import com.inspiredandroid.braincup.ui.theme.RailYardRail
import com.inspiredandroid.braincup.ui.theme.RailYardSleeper
import com.inspiredandroid.braincup.ui.theme.RailYardSwitchFace
import com.inspiredandroid.braincup.ui.theme.RailYardSwitchInk
import com.inspiredandroid.braincup.ui.theme.RailYardTunnel
import com.inspiredandroid.braincup.ui.theme.RailYardTunnelStone
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min

/**
 * The yard: tracks, switches, the tunnel, stations and trains, drawn in one canvas.
 *
 * [trains] is read inside the draw phase, so the motion loop's frames repaint the canvas without
 * recomposing anything. Positions arrive in the game's unit square and are stretched to the board;
 * every track runs along an axis, so the stretch never bends one.
 *
 * A switch takes the press itself rather than the release, and anywhere near it counts: the player
 * is racing a train to it.
 */
@Composable
fun RailYardBoard(
    uiState: RailYardUiState,
    trains: () -> List<RailYardGame.TrainFrame>,
    modifier: Modifier = Modifier,
    onSwitchTap: ((Int) -> Unit)? = null,
    pressedSwitch: Int? = null,
) {
    val faces = GameColor.entries.associateWith { it.composeColor() }
    val patterns = GameColor.entries.associateWith { it.visiblePattern() }
    val currentState by rememberUpdatedState(uiState)
    val currentOnSwitchTap by rememberUpdatedState(onSwitchTap)

    val tapModifier = if (onSwitchTap == null) {
        Modifier
    } else {
        Modifier.pointerInput(Unit) {
            awaitEachGesture {
                val down = awaitFirstDown()
                val geometry = BoardGeometry(Size(size.width.toFloat(), size.height.toFloat()), currentState, this)
                val reach = max(geometry.switchRadius * 1.8f, 30.dp.toPx())
                val nearest = currentState.switches
                    .map { it to geometry.toPx(it.position) }
                    .minByOrNull { (_, center) -> (center - down.position).getDistance() }
                if (nearest != null && (nearest.second - down.position).getDistance() <= reach) {
                    down.consume()
                    currentOnSwitchTap?.invoke(nearest.first.nodeIndex)
                }
            }
        }
    }

    Canvas(modifier = modifier.then(tapModifier)) {
        val geometry = BoardGeometry(size, uiState, this)
        drawGround()
        uiState.tracks.sortedBy { it.isOpen }.forEach { drawTrack(it, geometry) }
        uiState.switches.forEach { drawSwitch(it, geometry, isPressed = it.nodeIndex == pressedSwitch) }
        // Trains ride over the switches so one is never lost under a disc, and slip under the
        // tunnel and the stations so they come out of one and disappear into the other.
        trains().forEach { drawTrain(it, geometry, faces.getValue(it.color), patterns.getValue(it.color)) }
        drawTunnel(geometry.toPx(uiState.tunnel), geometry.unit)
        uiState.stations.forEach {
            drawStation(it, geometry, faces.getValue(it.color), patterns.getValue(it.color))
        }
    }
}

/** Board pixels for the game's unit square, sized off the narrowest column the map can have. */
private class BoardGeometry(size: Size, uiState: RailYardUiState, density: Density) {
    private val inset: Float
    private val innerWidth: Float
    private val innerHeight: Float
    val unit: Float
    val switchRadius: Float

    init {
        with(density) {
            val columns = max(uiState.stations.size, 3)
            unit = min(size.width / (columns + 0.6f), 52.dp.toPx())
            inset = unit * 0.55f
            switchRadius = unit * 0.32f
        }
        innerWidth = size.width - 2 * inset
        innerHeight = size.height - 2 * inset
    }

    fun toPx(point: RailYardGame.Point): Offset = Offset(inset + point.x * innerWidth, inset + point.y * innerHeight)
}

private fun DrawScope.drawGround() {
    val corner = CornerRadius(14.dp.toPx())
    drawRoundRect(RailYardFrame, cornerRadius = corner)
    val frame = 4.dp.toPx()
    drawRoundRect(
        RailYardGround,
        topLeft = Offset(frame, frame),
        size = Size(size.width - 2 * frame, size.height - 2 * frame),
        cornerRadius = CornerRadius(corner.x - frame),
    )
}

private fun DrawScope.drawTrack(track: RailYardUiState.Track, geometry: BoardGeometry) {
    val path = roundedPath(track.points.map(geometry::toPx), cornerRadius = geometry.unit * 0.35f)
    val gauge = geometry.unit * 0.2f
    val sleeper = if (track.isOpen) RailYardSleeper else RailYardClosedSleeper
    val rail = if (track.isOpen) RailYardRail else RailYardClosedRail
    drawPath(
        path,
        sleeper,
        style = Stroke(
            width = gauge * 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(gauge * 0.45f, gauge * 0.55f)),
        ),
    )
    drawPath(path, rail, style = Stroke(width = gauge * 1.25f, join = StrokeJoin.Round))
    drawPath(path, sleeper, style = Stroke(width = gauge * 0.75f, join = StrokeJoin.Round))
}

/** A polyline with each inner corner swapped for a quarter curve, the way a track bends. */
private fun roundedPath(points: List<Offset>, cornerRadius: Float): Path {
    val path = Path()
    path.moveTo(points.first().x, points.first().y)
    for (i in 1 until points.lastIndex) {
        val previous = points[i - 1]
        val corner = points[i]
        val next = points[i + 1]
        val radius = minOf(cornerRadius, (corner - previous).getDistance() / 2f, (next - corner).getDistance() / 2f)
        val entry = corner - (corner - previous).unit() * radius
        val exit = corner + (next - corner).unit() * radius
        path.lineTo(entry.x, entry.y)
        path.quadraticTo(corner.x, corner.y, exit.x, exit.y)
    }
    path.lineTo(points.last().x, points.last().y)
    return path
}

private fun Offset.unit(): Offset {
    val length = hypot(x, y)
    return if (length == 0f) Offset.Zero else Offset(x / length, y / length)
}

private fun DrawScope.drawTrain(
    frame: RailYardGame.TrainFrame,
    geometry: BoardGeometry,
    face: Color,
    pattern: ColorPattern,
) {
    val center = geometry.toPx(RailYardGame.Point(frame.x, frame.y))
    val length = geometry.unit * 0.66f
    val width = geometry.unit * 0.4f
    val carSize = if (frame.isHorizontal) Size(length, width) else Size(width, length)
    val bounds = Rect(center - Offset(carSize.width / 2f, carSize.height / 2f), carSize)
    val corner = CornerRadius(width * 0.3f)
    drawRoundRect(face.darken(0.45f), bounds.topLeft + Offset(0f, width * 0.12f), bounds.size, corner)
    drawRoundRect(face, bounds.topLeft, bounds.size, corner)
    drawColorPattern(pattern, face, Path().apply { addRoundRect(RoundRect(bounds, corner)) })
    drawRoundRect(face.darken(0.4f), bounds.topLeft, bounds.size, corner, style = Stroke(1.5.dp.toPx()))
}

private fun DrawScope.drawTunnel(mouth: Offset, unit: Float) {
    val width = unit * 0.9f
    val height = unit * 0.62f
    val stone = unit * 0.12f
    val arch = Path().apply {
        moveTo(mouth.x - width / 2f, mouth.y + height * 0.35f)
        lineTo(mouth.x - width / 2f, mouth.y - height * 0.15f)
        quadraticTo(mouth.x - width / 2f, mouth.y - height * 0.65f, mouth.x, mouth.y - height * 0.65f)
        quadraticTo(mouth.x + width / 2f, mouth.y - height * 0.65f, mouth.x + width / 2f, mouth.y - height * 0.15f)
        lineTo(mouth.x + width / 2f, mouth.y + height * 0.35f)
        close()
    }
    drawPath(arch, RailYardTunnelStone, style = Stroke(width = stone * 2f, join = StrokeJoin.Round))
    drawPath(arch, RailYardTunnel)
}

private fun DrawScope.drawStation(
    station: RailYardUiState.Station,
    geometry: BoardGeometry,
    face: Color,
    pattern: ColorPattern,
) {
    val center = geometry.toPx(station.position)
    val bodyWidth = geometry.unit * 0.78f
    val bodyHeight = geometry.unit * 0.5f
    val roofHeight = geometry.unit * 0.3f
    val body = Rect(
        Offset(center.x - bodyWidth / 2f, center.y - bodyHeight * 0.35f),
        Size(bodyWidth, bodyHeight),
    )
    val roof = Path().apply {
        moveTo(body.left - bodyWidth * 0.1f, body.top)
        lineTo(center.x, body.top - roofHeight)
        lineTo(body.right + bodyWidth * 0.1f, body.top)
        close()
    }
    val corner = CornerRadius(bodyWidth * 0.08f)
    val shade = face.darken(0.45f)

    drawRoundRect(shade, body.topLeft + Offset(0f, bodyHeight * 0.14f), body.size, corner)
    drawRoundRect(face, body.topLeft, body.size, corner)
    drawColorPattern(pattern, face, body)
    drawPath(roof, shade)
    drawPath(roof, face.darken(0.25f), style = Stroke(1.5.dp.toPx(), join = StrokeJoin.Round))

    val doorWidth = bodyWidth * 0.26f
    drawRoundRect(
        shade,
        topLeft = Offset(center.x - doorWidth / 2f, body.bottom - bodyHeight * 0.55f),
        size = Size(doorWidth, bodyHeight * 0.55f),
        cornerRadius = CornerRadius(doorWidth * 0.3f),
    )

    val mark = when (station.flash) {
        RailYardGame.StationFlash.NONE -> return
        RailYardGame.StationFlash.CORRECT -> SuccessGreen
        RailYardGame.StationFlash.WRONG -> ErrorRed
    }
    val badgeCenter = Offset(body.right, body.top - roofHeight * 0.4f)
    val badgeRadius = geometry.unit * 0.22f
    drawCircle(Color.White, badgeRadius * 1.15f, badgeCenter)
    drawCircle(mark, badgeRadius, badgeCenter)
    val ink = Stroke(width = badgeRadius * 0.32f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val r = badgeRadius * 0.5f
    val glyph = Path().apply {
        if (station.flash == RailYardGame.StationFlash.CORRECT) {
            moveTo(badgeCenter.x - r, badgeCenter.y)
            lineTo(badgeCenter.x - r * 0.25f, badgeCenter.y + r * 0.7f)
            lineTo(badgeCenter.x + r, badgeCenter.y - r * 0.6f)
        } else {
            moveTo(badgeCenter.x - r * 0.75f, badgeCenter.y - r * 0.75f)
            lineTo(badgeCenter.x + r * 0.75f, badgeCenter.y + r * 0.75f)
            moveTo(badgeCenter.x + r * 0.75f, badgeCenter.y - r * 0.75f)
            lineTo(badgeCenter.x - r * 0.75f, badgeCenter.y + r * 0.75f)
        }
    }
    drawPath(glyph, Color.White, style = ink)
}

private fun DrawScope.drawSwitch(switch: RailYardUiState.Switch, geometry: BoardGeometry, isPressed: Boolean) {
    val center = geometry.toPx(switch.position)
    val radius = geometry.switchRadius * if (isPressed) 0.88f else 1f
    drawPrismCircle(center = center, radius = radius, face = RailYardSwitchFace)

    // A chevron towards the branch the switch is set to. The tracks already show it, but the
    // chevron is what the eye lands on when it is looking at the switch it is about to tap.
    val direction = if (switch.turnsRight) 1f else -1f
    val reach = radius * 0.42f
    val chevron = Path().apply {
        moveTo(center.x - direction * reach * 0.5f, center.y - reach)
        lineTo(center.x + direction * reach * 0.7f, center.y)
        lineTo(center.x - direction * reach * 0.5f, center.y + reach)
    }
    drawPath(
        chevron,
        RailYardSwitchInk,
        style = Stroke(width = radius * 0.26f, cap = StrokeCap.Round, join = StrokeJoin.Round),
    )
}
