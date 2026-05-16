package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/** Renders an arbitrary normalized polygon with the same 3D faceted look as [PrismCard]. */
@Composable
fun PrismPolygon(
    points: ImmutableList<Pair<Float, Float>>,
    face: Color,
    modifier: Modifier = Modifier,
    rotationDegrees: Float = 0f,
    side: Color? = null,
    bottom: Color? = null,
    facet: Dp? = null,
) {
    val resolvedSide = remember(face, side) { side ?: face.darken(0.7f) }
    val resolvedBottom = remember(face, bottom) { bottom ?: face.darken(0.5f) }
    Spacer(
        modifier = modifier.drawWithCache {
            if (points.size < 3) return@drawWithCache onDrawBehind { }

            val w = size.width
            val h = size.height
            val d = facet?.toPx() ?: (min(w, h) * 0.08f).coerceAtMost(10.dp.toPx())
            val availW = w - d
            val availH = h - d
            val theta = (rotationDegrees * PI / 180.0).toFloat()
            val cosT = cos(theta)
            val sinT = sin(theta)

            val front = List(points.size) { i ->
                val (nx, ny) = points[i]
                val rx = (nx - 0.5f) * cosT - (ny - 0.5f) * sinT + 0.5f
                val ry = (nx - 0.5f) * sinT + (ny - 0.5f) * cosT + 0.5f
                Offset(rx * availW, ry * availH)
            }

            val polygonPath = Path().apply { buildPolygon(front) }

            var sumX = 0f
            var sumY = 0f
            for (p in front) {
                sumX += p.x
                sumY += p.y
            }
            val cx = sumX / front.size
            val cy = sumY / front.size

            val sidePaths = mutableListOf<Path>()
            for (i in front.indices) {
                val j = (i + 1) % front.size
                val p1 = front[i]
                val p2 = front[j]
                val ex = p2.x - p1.x
                val ey = p2.y - p1.y
                val toMidX = (p1.x + p2.x) * 0.5f - cx
                val toMidY = (p1.y + p2.y) * 0.5f - cy
                val flip = -ey * toMidX + ex * toMidY < 0f
                val nx = if (flip) ey else -ey
                val ny = if (flip) -ex else ex
                if ((nx + ny) * d > 0f) {
                    sidePaths.add(
                        Path().apply {
                            moveTo(p1.x, p1.y)
                            lineTo(p2.x, p2.y)
                            lineTo(p2.x + d, p2.y + d)
                            lineTo(p1.x + d, p1.y + d)
                            close()
                        },
                    )
                }
            }

            onDrawBehind {
                translate(d, d) { drawPath(polygonPath, resolvedBottom) }
                for (p in sidePaths) drawPath(p, resolvedSide)
                drawPath(polygonPath, face)
            }
        },
    )
}

/** Prism-styled disc. Front and back are true circles; only the down-right facet ring is polygonal. */
fun DrawScope.drawPrismCircle(
    center: Offset,
    radius: Float,
    face: Color,
    side: Color? = null,
    bottom: Color? = null,
    depth: Float = radius * 0.22f,
) {
    if (radius <= 0f) return
    val resolvedSide = side ?: face.darken(0.7f)
    val resolvedBottom = bottom ?: face.darken(0.5f)

    translate(depth, depth) { drawCircle(resolvedBottom, radius, center) }

    val segments = (radius * 1.5f).toInt().coerceIn(8, 32)
    val step = (2f * PI.toFloat()) / segments
    val path = Path()
    var c1 = cos(0f)
    var s1 = sin(0f)
    for (i in 0 until segments) {
        val a2 = (i + 1) * step
        val c2 = cos(a2)
        val s2 = sin(a2)
        if (c1 + s1 + c2 + s2 > 0f) {
            val x1 = center.x + c1 * radius
            val y1 = center.y + s1 * radius
            val x2 = center.x + c2 * radius
            val y2 = center.y + s2 * radius
            path.reset()
            path.moveTo(x1, y1)
            path.lineTo(x2, y2)
            path.lineTo(x2 + depth, y2 + depth)
            path.lineTo(x1 + depth, y1 + depth)
            path.close()
            drawPath(path, resolvedSide)
        }
        c1 = c2
        s1 = s2
    }

    drawCircle(face, radius, center)
}

private fun Path.buildPolygon(points: List<Offset>) {
    reset()
    moveTo(points[0].x, points[0].y)
    for (i in 1 until points.size) lineTo(points[i].x, points[i].y)
    close()
}
