package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
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
import com.inspiredandroid.braincup.ui.theme.PrismFacet
import com.inspiredandroid.braincup.ui.theme.PrismShade
import com.inspiredandroid.braincup.ui.theme.RailYardClosedRail
import com.inspiredandroid.braincup.ui.theme.RailYardGround
import com.inspiredandroid.braincup.ui.theme.RailYardRail
import com.inspiredandroid.braincup.ui.theme.RailYardSwitchFace
import com.inspiredandroid.braincup.ui.theme.RailYardSwitchInk
import com.inspiredandroid.braincup.ui.theme.RailYardTunnel
import com.inspiredandroid.braincup.ui.theme.RailYardTunnelStone
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import kotlin.math.hypot
import kotlin.math.max

/**
 * The yard on a prism board: tracks, switches, the tunnel, stations and trains, drawn in one canvas.
 *
 * Everything that stands on the board is a prism, a flat face pushed out down-right like the rest
 * of the app. The tracks a switch currently sends trains along are raised the same way and the
 * branches it does not are left flat, so the live route reads from depth before colour.
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
                val reach = max(geometry.switchSize * 1.4f, 30.dp.toPx())
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

    PrismCard(face = RailYardGround, facet = PrismFacet.Board, modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize().then(tapModifier)) {
            val geometry = BoardGeometry(size, uiState, this)
            uiState.tracks.filter { !it.isOpen }.forEach { drawTrack(it, geometry) }
            uiState.tracks.filter { it.isOpen }.forEach { drawTrack(it, geometry) }
            uiState.switches.forEach { drawSwitch(it, geometry, isPressed = it.nodeIndex == pressedSwitch) }
            // Trains ride over the switches so one is never lost under a key, and slip under the
            // tunnel and the stations so they come out of one and disappear into the other.
            trains().forEach { drawTrain(it, geometry, faces.getValue(it.color), patterns.getValue(it.color)) }
            drawTunnel(geometry.toPx(uiState.tunnel), geometry)
            uiState.stations.forEach {
                drawStation(it, geometry, faces.getValue(it.color), patterns.getValue(it.color))
            }
        }
    }
}

/**
 * Board pixels for the game's unit square. Pieces are sized off whichever is tighter, a column per
 * station across or a row per level down, so a short board shrinks them before they collide and a
 * tablet's big board grows them instead of leaving them lost in it.
 */
private class BoardGeometry(size: Size, uiState: RailYardUiState, density: Density) {
    private val inset: Float
    private val innerWidth: Float
    private val innerHeight: Float
    val unit: Float
    val switchSize: Float
    val depth: Float

    init {
        with(density) {
            val columns = max(uiState.stations.size, 3)
            val rows = (uiState.switches.map { it.position.y } + uiState.stations.map { it.position.y } + uiState.tunnel.y)
                .distinct().size
            unit = minOf(size.width / (columns + 1.6f), size.height / (rows * 1.3f), MaxUnit.toPx())
            inset = unit * 0.55f
            switchSize = unit * 0.5f
            depth = max(unit * 0.07f, PrismFacet.Cell.toPx())
        }
        innerWidth = size.width - 2 * inset
        innerHeight = size.height - 2 * inset
    }

    fun toPx(point: RailYardGame.Point): Offset = Offset(inset + point.x * innerWidth, inset + point.y * innerHeight)
}

private val MaxUnit = 96.dp

private fun rect(center: Offset, size: Size): List<Offset> {
    val left = center.x - size.width / 2f
    val top = center.y - size.height / 2f
    return listOf(
        Offset(left, top),
        Offset(left + size.width, top),
        Offset(left + size.width, top + size.height),
        Offset(left, top + size.height),
    )
}

private fun polygonPath(points: List<Offset>): Path = Path().apply {
    moveTo(points.first().x, points.first().y)
    for (point in points.drop(1)) lineTo(point.x, point.y)
    close()
}

/**
 * A convex [face] polygon extruded [depth] down-right: the back copy, then the side quad of every
 * edge that faces down or right, then the front. The same build as [PrismPolygon], for a canvas.
 */
private fun DrawScope.drawPrism(points: List<Offset>, face: Color, depth: Float) {
    val front = polygonPath(points)
    translate(depth, depth) { drawPath(front, face.darken(PrismShade.Bottom)) }
    val center = Offset(points.sumOf { it.x.toDouble() }.toFloat(), points.sumOf { it.y.toDouble() }.toFloat()) / points.size.toFloat()
    val side = face.darken(PrismShade.Side)
    for (i in points.indices) {
        val a = points[i]
        val b = points[(i + 1) % points.size]
        val outward = (a + b) / 2f - center
        if (outward.x + outward.y <= 0f) continue
        drawPath(polygonPath(listOf(a, b, b + Offset(depth, depth), a + Offset(depth, depth))), side)
    }
    drawPath(front, face)
}

/**
 * A raised track is the same band drawn three times, back, side and front, each a step further
 * up-left: the stroke equivalent of [drawPrism]'s extrusion.
 */
private fun DrawScope.drawTrack(track: RailYardUiState.Track, geometry: BoardGeometry) {
    val path = chamferedPath(track.points.map(geometry::toPx), cut = geometry.unit * 0.3f)
    val stroke = Stroke(width = geometry.unit * 0.2f, cap = StrokeCap.Butt, join = StrokeJoin.Miter)
    if (!track.isOpen) {
        drawPath(path, RailYardClosedRail, style = stroke)
        return
    }
    val depth = geometry.depth
    translate(depth, depth) { drawPath(path, RailYardRail.darken(PrismShade.Bottom), style = stroke) }
    translate(depth / 2f, depth / 2f) { drawPath(path, RailYardRail.darken(PrismShade.Side), style = stroke) }
    drawPath(path, RailYardRail, style = stroke)
}

/** A polyline with each inner corner cut at 45 degrees, the prism silhouette's own corner. */
private fun chamferedPath(points: List<Offset>, cut: Float): Path {
    val path = Path()
    path.moveTo(points.first().x, points.first().y)
    for (i in 1 until points.lastIndex) {
        val previous = points[i - 1]
        val corner = points[i]
        val next = points[i + 1]
        val length = minOf(cut, (corner - previous).getDistance() / 2f, (next - corner).getDistance() / 2f)
        val entry = corner - (corner - previous).unit() * length
        val exit = corner + (next - corner).unit() * length
        path.lineTo(entry.x, entry.y)
        path.lineTo(exit.x, exit.y)
    }
    path.lineTo(points.last().x, points.last().y)
    return path
}

private fun Offset.unit(): Offset {
    val length = hypot(x, y)
    return if (length == 0f) Offset.Zero else Offset(x / length, y / length)
}

/** Two cars with a gap between them, so a train never reads as just another block. */
private fun DrawScope.drawTrain(
    frame: RailYardGame.TrainFrame,
    geometry: BoardGeometry,
    face: Color,
    pattern: ColorPattern,
) {
    val center = geometry.toPx(RailYardGame.Point(frame.x, frame.y))
    val carLength = geometry.unit * 0.32f
    val carWidth = geometry.unit * 0.36f
    val gap = geometry.unit * 0.06f
    val along = if (frame.isHorizontal) Offset(1f, 0f) else Offset(0f, 1f)
    val carSize = if (frame.isHorizontal) Size(carLength, carWidth) else Size(carWidth, carLength)
    for (sign in listOf(-1f, 1f)) {
        val front = rect(center + along * (sign * (carLength + gap) / 2f), carSize)
        drawPrism(front, face, geometry.depth)
        drawColorPattern(pattern, face, polygonPath(front))
    }
}

/** A stone block with a dark arched mouth; the tracks begin underneath it. */
private fun DrawScope.drawTunnel(mouth: Offset, geometry: BoardGeometry) {
    val unit = geometry.unit
    val blockSize = Size(unit * 1.0f, unit * 0.62f)
    val blockCenter = mouth + Offset(0f, -unit * 0.12f)
    drawPrism(rect(blockCenter, blockSize), RailYardTunnelStone, geometry.depth)

    val openingWidth = unit * 0.56f
    val bottom = blockCenter.y + blockSize.height / 2f
    val top = blockCenter.y - blockSize.height * 0.32f
    val cut = openingWidth * 0.3f
    val left = mouth.x - openingWidth / 2f
    val right = mouth.x + openingWidth / 2f
    drawPath(
        polygonPath(
            listOf(
                Offset(left, bottom),
                Offset(left, top + cut),
                Offset(left + cut, top),
                Offset(right - cut, top),
                Offset(right, top + cut),
                Offset(right, bottom),
            ),
        ),
        RailYardTunnel,
    )
}

/** A prism wall and roof with a flat door, the same house Head Count is built from. */
private fun DrawScope.drawStation(
    station: RailYardUiState.Station,
    geometry: BoardGeometry,
    face: Color,
    pattern: ColorPattern,
) {
    val center = geometry.toPx(station.position)
    val unit = geometry.unit
    val depth = geometry.depth
    val wallWidth = unit * 0.72f
    val wallHeight = unit * 0.46f
    val roofHeight = unit * 0.3f
    val wall = rect(center + Offset(0f, wallHeight * 0.15f), Size(wallWidth, wallHeight))
    val wallTop = wall[0].y
    val roof = listOf(
        Offset(wall[0].x - wallWidth * 0.1f, wallTop),
        Offset(center.x, wallTop - roofHeight),
        Offset(wall[1].x + wallWidth * 0.1f, wallTop),
    )

    drawPrism(wall, face, depth)
    drawColorPattern(pattern, face, polygonPath(wall))
    drawPrism(roof, face.darken(0.8f), depth)

    val doorWidth = wallWidth * 0.26f
    val doorHeight = wallHeight * 0.58f
    drawRect(
        face.darken(PrismShade.Bottom),
        topLeft = Offset(center.x - doorWidth / 2f, wall[2].y - doorHeight),
        size = Size(doorWidth, doorHeight),
    )

    val mark = when (station.flash) {
        RailYardGame.StationFlash.NONE -> return
        RailYardGame.StationFlash.CORRECT -> SuccessGreen
        RailYardGame.StationFlash.WRONG -> ErrorRed
    }
    val badgeRadius = unit * 0.2f
    val badgeCenter = Offset(wall[1].x, wallTop - roofHeight * 0.5f)
    drawPrismCircle(center = badgeCenter, radius = badgeRadius, face = mark)
    val glyph = Size(badgeRadius * 1.2f, badgeRadius * 1.2f)
    val glyphTopLeft = badgeCenter - Offset(glyph.width / 2f, glyph.height / 2f)
    if (station.flash == RailYardGame.StationFlash.CORRECT) {
        drawChunkyCheck(Color.White, glyphTopLeft, glyph)
    } else {
        drawChunkyCross(Color.White, glyphTopLeft, glyph)
    }
}

/**
 * A prism key with a chunky chevron towards the branch it is set to. Pressed, the face sinks into
 * its facet the way a [PrismTile] does.
 */
private fun DrawScope.drawSwitch(switch: RailYardUiState.Switch, geometry: BoardGeometry, isPressed: Boolean) {
    val size = geometry.switchSize
    val fullDepth = geometry.depth * 1.6f
    val shift = if (isPressed) fullDepth * 0.7f else 0f
    val center = geometry.toPx(switch.position) - Offset(fullDepth / 2f, fullDepth / 2f) + Offset(shift, shift)
    drawPrism(rect(center, Size(size, size)), RailYardSwitchFace, fullDepth - shift)

    val direction = if (switch.turnsRight) 1f else -1f
    val stroke = size * 0.15f
    val tip = center + Offset(direction * size * 0.16f, 0f)
    val back = center.x - direction * size * 0.14f
    drawLine(RailYardSwitchInk, Offset(back, center.y - size * 0.26f), tip, strokeWidth = stroke, cap = StrokeCap.Round)
    drawLine(RailYardSwitchInk, tip, Offset(back, center.y + size * 0.26f), strokeWidth = stroke, cap = StrokeCap.Round)
}
