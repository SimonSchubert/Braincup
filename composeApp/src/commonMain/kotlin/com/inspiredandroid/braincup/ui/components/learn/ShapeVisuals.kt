package com.inspiredandroid.braincup.ui.components.learn

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import com.inspiredandroid.braincup.learn.FlatShapeKind
import com.inspiredandroid.braincup.learn.LearnVisual
import com.inspiredandroid.braincup.learn.QuadKind
import com.inspiredandroid.braincup.learn.Side
import com.inspiredandroid.braincup.learn.SolidKind
import com.inspiredandroid.braincup.learn.TriKind
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

/** The corners of a regular polygon with [sides] sides, flat-bottomed and centred on [center]. */
private fun polygonPoints(sides: Int, center: Offset, radius: Float): List<Offset> {
    val n = sides.coerceAtLeast(3)
    // Rotate so the shape sits on a flat edge for even counts and on a point for odd ones.
    val start = -PI / 2 + if (n % 2 == 0) PI / n else 0.0
    return List(n) { i ->
        val angle = start + i * 2 * PI / n
        Offset(center.x + (radius * cos(angle)).toFloat(), center.y + (radius * sin(angle)).toFloat())
    }
}

/**
 * Corners of each named triangle on a -1..1 box, y down. The scalene one is checked to have no
 * right angle and no two sides alike, so it cannot be misread as either of the others.
 */
private fun trianglePoints(kind: TriKind): List<Offset> = when (kind) {
    TriKind.EQUILATERAL -> listOf(Offset(0f, -0.9f), Offset(0.78f, 0.45f), Offset(-0.78f, 0.45f))
    TriKind.ISOSCELES -> listOf(Offset(0f, -0.95f), Offset(0.62f, 0.55f), Offset(-0.62f, 0.55f))
    TriKind.SCALENE -> listOf(Offset(0.15f, -0.85f), Offset(0.9f, 0.62f), Offset(-0.95f, 0.5f))
}

/** Which sides carry a tick, so equal lengths are marked the way a textbook marks them. */
private fun equalTriangleSides(kind: TriKind): List<Int> = when (kind) {
    TriKind.EQUILATERAL -> listOf(0, 1, 2)
    TriKind.ISOSCELES -> listOf(0, 2)
    TriKind.SCALENE -> emptyList()
}

/**
 * The ghost of the finished shape, under the sides that are still arriving, so a figure never
 * looks broken mid-animation.
 */
private fun VisualScope.ghostOutline(points: List<Offset>) {
    path(closedPath(points), fill = Accent.copy(alpha = 0.12f * progress), outline = faint, width = stroke * 0.7f)
}

/**
 * Draws the closed run of [points] one side at a time, each side growing out of the corner the
 * one before it landed on. This is what makes a polygon count its own sides.
 */
private fun VisualScope.drawSidesInTurn(points: List<Offset>, width: Float = stroke * 1.5f) {
    points.indices.forEach { i ->
        val from = points[i]
        val to = points[(i + 1) % points.size]
        val t = item(i, points.size)
        if (t <= 0f) return@forEach
        line(from, Offset(from.x + (to.x - from.x) * t, from.y + (to.y - from.y) * t), Accent, width)
    }
}

/** A tick drawn across the side [from]..[to] and centred on [at], marking it equal to its group. */
private fun VisualScope.tickMark(
    at: Offset,
    from: Offset,
    to: Offset,
    half: Float,
    color: Color = Accent2,
    alpha: Float = 1f,
) {
    val normal = normalAt(from, to)
    line(
        Offset(at.x + normal.x * half, at.y + normal.y * half),
        Offset(at.x - normal.x * half, at.y - normal.y * half),
        color,
        stroke,
        alpha = alpha,
    )
}

/** The beat the equal-side ticks and parallel chevrons arrive on, once every side is down. */
private val VisualScope.marksAlpha: Float get() = ((progress - 0.75f) * 4f).coerceIn(0f, 1f)

/**
 * A triangle of the kind the step is about. [drawPolygon] can only build the equilateral one, and
 * a question that names two unequal angles needs a shape that does not contradict it.
 */
internal fun VisualScope.drawTriangle(visual: LearnVisual.Triangle) {
    val scale = size.minDimension * 0.34f
    // None of the three sits square on its box - the isosceles reaches 0.95 up and 0.55 down - so
    // the shape is centred on what it actually spans rather than on the box it is drawn in.
    val raw = trianglePoints(visual.kind).map { Offset(it.x * scale, it.y * scale) }
    val shift = placeShape(raw, captions(0))
    val points = raw.map { it + shift }

    ghostOutline(points)
    drawSidesInTurn(points)

    if (!visual.marks) return
    val marks = marksAlpha
    if (marks <= 0f) return
    val tickHalf = scale * 0.075f
    equalTriangleSides(visual.kind).forEach { i ->
        val from = points[i]
        val to = points[(i + 1) % 3]
        val mid = Offset((from.x + to.x) / 2f, (from.y + to.y) / 2f)
        tickMark(mid, from, to, tickHalf, alpha = marks)
    }
}

/**
 * Corners of each named quadrilateral, in order round the shape, on a -1..1 box with y running
 * down the screen. Every one is built so its defining property is visible: the rhombus is a
 * squashed diamond rather than a square, the parallelogram leans, and the trapezium has one pair
 * of parallel sides and no more.
 */
private fun quadPoints(kind: QuadKind): List<Offset> = when (kind) {
    QuadKind.SQUARE -> listOf(Offset(-0.7f, -0.7f), Offset(0.7f, -0.7f), Offset(0.7f, 0.7f), Offset(-0.7f, 0.7f))
    QuadKind.RECTANGLE -> listOf(Offset(-1f, -0.6f), Offset(1f, -0.6f), Offset(1f, 0.6f), Offset(-1f, 0.6f))
    QuadKind.RHOMBUS -> listOf(Offset(0f, -0.8f), Offset(0.95f, 0f), Offset(0f, 0.8f), Offset(-0.95f, 0f))
    QuadKind.PARALLELOGRAM -> listOf(Offset(-0.6f, -0.6f), Offset(1f, -0.6f), Offset(0.6f, 0.6f), Offset(-1f, 0.6f))
    QuadKind.TRAPEZIUM -> listOf(Offset(-0.5f, -0.6f), Offset(0.5f, -0.6f), Offset(1f, 0.6f), Offset(-1f, 0.6f))
    QuadKind.KITE -> listOf(Offset(0f, -0.95f), Offset(0.65f, -0.1f), Offset(0f, 0.9f), Offset(-0.65f, -0.1f))
}

/** Sides that share a length, as tick counts: index 0 gets one tick, index 1 gets two. */
private fun equalSideGroups(kind: QuadKind): List<List<Int>> = when (kind) {
    QuadKind.SQUARE, QuadKind.RHOMBUS -> listOf(listOf(0, 1, 2, 3))
    QuadKind.RECTANGLE, QuadKind.PARALLELOGRAM -> listOf(listOf(0, 2), listOf(1, 3))
    QuadKind.KITE -> listOf(listOf(0, 3), listOf(1, 2))
    QuadKind.TRAPEZIUM -> emptyList()
}

/** Sides that run parallel, as chevron counts, the same way a textbook marks them. */
private fun parallelSideGroups(kind: QuadKind): List<List<Int>> = when (kind) {
    QuadKind.SQUARE, QuadKind.RECTANGLE, QuadKind.RHOMBUS, QuadKind.PARALLELOGRAM ->
        listOf(listOf(0, 2), listOf(1, 3))
    QuadKind.TRAPEZIUM -> listOf(listOf(0, 2))
    QuadKind.KITE -> emptyList()
}

/**
 * A named quadrilateral, drawn side by side with its equal sides ticked and its parallel sides
 * chevroned, so a rhombus reads as a rhombus and not as the square [drawPolygon] would give.
 */
internal fun VisualScope.drawQuadrilateral(visual: LearnVisual.Quadrilateral) {
    val scale = size.minDimension * 0.33f
    val raw = quadPoints(visual.kind).map { Offset(it.x * scale, it.y * scale) }
    val shift = placeShape(raw, captions(0))
    val points = raw.map { it + shift }

    ghostOutline(points)
    drawSidesInTurn(points)

    if (!visual.marks) return
    val marks = marksAlpha
    if (marks <= 0f) return

    val tickHalf = scale * 0.075f
    equalSideGroups(visual.kind).forEachIndexed { group, sides ->
        sides.forEach { i ->
            val from = points[i]
            val to = points[(i + 1) % 4]
            val mid = Offset((from.x + to.x) / 2f, (from.y + to.y) / 2f)
            val along = unitAlong(from, to)
            // A group of two or three ticks spreads out along the side, centred on its midpoint.
            repeat(group + 1) { t ->
                val shift = (t - group / 2f) * stroke * 2.2f
                val at = Offset(mid.x + along.x * shift, mid.y + along.y * shift)
                tickMark(at, from, to, tickHalf, alpha = marks)
            }
        }
    }

    val chevron = scale * 0.09f
    parallelSideGroups(visual.kind).forEachIndexed { group, sides ->
        sides.forEach { i ->
            val from = points[i]
            val to = points[(i + 1) % 4]
            // Chevrons sit a quarter of the way along, clear of the equal-length ticks that take
            // the midpoint; drawn on top of each other the two marks read as a smudge.
            val mid = Offset(from.x + (to.x - from.x) * 0.27f, from.y + (to.y - from.y) * 0.27f)
            val along = unitAlong(from, to)
            val nx = -along.y
            val ny = along.x
            repeat(group + 1) { c ->
                val tip = Offset(mid.x + along.x * c * chevron * 0.9f, mid.y + along.y * c * chevron * 0.9f)
                line(
                    Offset(
                        tip.x - along.x * chevron + nx * chevron * 0.6f,
                        tip.y - along.y * chevron + ny * chevron * 0.6f,
                    ),
                    tip,
                    ink,
                    stroke * 0.9f,
                    alpha = marks,
                )
                line(
                    Offset(
                        tip.x - along.x * chevron - nx * chevron * 0.6f,
                        tip.y - along.y * chevron - ny * chevron * 0.6f,
                    ),
                    tip,
                    ink,
                    stroke * 0.9f,
                    alpha = marks,
                )
            }
        }
    }
}

/**
 * Where each corner of the cyclic quadrilateral sits on the circle, in degrees. The gaps between
 * them are deliberately uneven - 68, 124, 118 and 50 degrees - so the corner angles come out near
 * 121, 84, 59 and 96. A near-square would let the opposite-angle rule be read as a fact about
 * rectangles, which is the one reading the lesson has to rule out.
 */
private val CyclicQuadCorners = listOf(200f, 268f, 32f, 150f)

/**
 * A lopsided quadrilateral inscribed in its circle. The shape is deliberately irregular so that
 * the opposite-angle rule cannot be mistaken for a fact about squares, and the corners land on
 * the circle one at a time so "all four corners sit on it" is the thing the figure shows.
 */
internal fun VisualScope.drawCyclicQuad(visual: LearnVisual.CyclicQuad) {
    val radius = size.minDimension * 0.36f
    val center = Offset(width / 2f, height / 2f)
    val points = CyclicQuadCorners.map { deg -> polar(center, radius, deg, flipY = false) }

    circle(center, radius, outline = faint)

    // A hair thinner than the other polygons draw with, so the sides do not swamp the circle.
    drawSidesInTurn(points, width = stroke * 1.4f)

    val cornersAlpha = ((progress - 0.55f) * 3f).coerceIn(0f, 1f)
    repeat(4) { i ->
        val paired = visual.highlightPair != null && i % 2 == visual.highlightPair
        dot(points[i], stroke * 1.5f, if (paired) Accent2 else Accent, alpha = cornersAlpha)
    }

    val labelsAlpha = ((progress - 0.7f) * 3.4f).coerceIn(0f, 1f)
    repeat(4) { i ->
        val text = visual.angles.getOrNull(i).orEmpty()
        if (text.isEmpty()) return@repeat
        // Pull the label in off the corner so it sits inside the shape rather than on the circle.
        val at = Offset(
            points[i].x + (center.x - points[i].x) * 0.42f,
            points[i].y + (center.y - points[i].y) * 0.42f,
        )
        val paired = visual.highlightPair != null && i % 2 == visual.highlightPair
        label(text, at, if (paired) Accent2 else ink, 0.095f, alpha = labelsAlpha)
    }
}

/** How far past a corner its number stands, as a share of the polygon's radius. */
private const val CornerLabelReach = 0.26f

private const val CornerLabelFactor = 0.09f

/** The size a polygon's and a solid's own captions are set at: a shade over the standard. */
private const val PolygonCaptionFactor = 0.1f

private const val SolidCaptionFactor = 0.1f

/**
 * A real polygon of the size the question asks about, drawn one side at a time with its corners
 * numbered as they arrive — so "how many sides does a pentagon have" answers itself.
 */
internal fun VisualScope.drawPolygon(visual: LearnVisual.Polygon) {
    val sides = visual.drawnSides
    val radius = size.minDimension * 0.34f
    // A polygon with an odd number of sides stands on an edge and points up, so it reaches a full
    // radius over the middle of its circle and about half of one under it. The numbered corners
    // stand outside the shape again. Both are part of what gets centred.
    val raw = polygonPoints(sides, Offset.Zero, radius)
    val half = capHeight(CornerLabelFactor) / 2f
    val extent = if (visual.countCorners) {
        raw + raw.flatMap {
            val mark = it * (1f + CornerLabelReach)
            listOf(Offset(mark.x, mark.y - half), Offset(mark.x, mark.y + half))
        }
    } else {
        raw
    }
    val spanY = extent.maxOf { it.y } - extent.minOf { it.y }
    val room = captionsUnder(if (visual.reveal) 1 else 0, spanY, PolygonCaptionFactor)
    val center = placeShape(extent, room)
    val points = raw.map { it + center }

    ghostOutline(points)
    drawSidesInTurn(points)

    // Corners go on in a pass of their own, once every side is down. Marking each one as its side
    // arrived buried it under the side drawn next, and the corner is the thing being counted.
    if (visual.countCorners) {
        repeat(sides) { i ->
            val from = points[i]
            val cornerAlpha = ((item(i, sides) - 0.6f) * 2.5f).coerceIn(0f, 1f)
            if (cornerAlpha <= 0f) return@repeat
            val outward = Offset(from.x - center.x, from.y - center.y)
            val length = hypot(outward.x, outward.y).coerceAtLeast(1f)
            val at = Offset(
                from.x + outward.x / length * radius * CornerLabelReach,
                from.y + outward.y / length * radius * CornerLabelReach,
            )
            dot(from, stroke * 1.6f, Accent2, alpha = cornerAlpha)
            label("${i + 1}", at, Accent2, CornerLabelFactor, alpha = cornerAlpha)
        }
    }

    if (!visual.reveal) return
    val caption = revealBeat
    val captionY = room.y(0)
    // Each count is written in the colour of the thing it counts: the sides in the accent the shape
    // is drawn in, the corners in the green their dots are marked with. One colour across the whole
    // line leaves the reader working out for themselves which half names which part of the figure.
    if (!visual.countCorners) {
        label(strings.sidesAndCorners, Offset(width / 2f, captionY), Accent, PolygonCaptionFactor, alpha = caption)
        return
    }
    val sidesText = strings.sides
    val cornersText = strings.corners
    val captionStyle = labelStyle(PolygonCaptionFactor)
    val sidesWidth = measure(sidesText, captionStyle).size.width.toFloat()
    val cornersWidth = measure(cornersText, captionStyle).size.width.toFloat()
    val space = size.minDimension * 0.03f
    val captionLeft = width / 2f - (sidesWidth + space + cornersWidth) / 2f
    labelStart(sidesText, Offset(captionLeft, captionY), Accent, PolygonCaptionFactor, alpha = caption)
    labelStart(
        text = cornersText,
        start = Offset(captionLeft + sidesWidth + space, captionY),
        color = Accent2,
        factor = PolygonCaptionFactor,
        alpha = caption,
    )
}

/**
 * How far each solid reaches above and below the point it is drawn around, in units of its size.
 *
 * Only the first three are drawn symmetrically about that point. A triangular prism is set back
 * and up from it by half its size, so centring the point rather than the solid leaves the prism
 * sitting a third of its own height high while the caption stays at the foot of the panel.
 */
private fun solidSpan(kind: SolidKind): Pair<Float, Float> = when (kind) {
    SolidKind.CUBE, SolidKind.PRISM -> -0.71f to 0.71f
    SolidKind.SPHERE -> -0.9f to 0.9f
    SolidKind.CYLINDER -> -0.85f to 0.85f
    SolidKind.CONE -> -0.9f to 0.8f
    SolidKind.TRIANGULAR_PRISM -> -1.375f to 0.375f
    SolidKind.PYRAMID -> -0.95f to 0.7f
}

/** One solid, drawn in the way that makes its faces and edges countable. */
internal fun VisualScope.drawSolid(visual: LearnVisual.Solid) {
    val s = size.minDimension * 0.34f
    val (up, down) = solidSpan(visual.kind)
    val room = captionsUnder(if (visual.reveal) 1 else 0, (down - up) * s, SolidCaptionFactor)
    val center = Offset(width / 2f, room.centerY - (up + down) / 2f * s)
    val depth = s * 0.42f
    val outline = ink.copy(alpha = progress)
    // Every solid is tinted the same, so a cube and a cylinder read as the same kind of object.
    // The cube and the cone paint their faces at a flat 0.18 rather than fading in with this one.
    val face = Accent.copy(alpha = 0.18f * progress)

    when (visual.kind) {
        SolidKind.CUBE, SolidKind.PRISM -> {
            val w = if (visual.kind == SolidKind.PRISM) s * 1.7f else s
            val left = center.x - w / 2f - depth / 2f
            val top = center.y - s / 2f + depth / 2f
            box(Offset(left, top), Size(w, s), Accent.copy(alpha = 0.18f), outline)
            box(Offset(left + depth, top - depth), Size(w, s), null, outline)
            listOf(
                Offset(left, top) to Offset(left + depth, top - depth),
                Offset(left + w, top) to Offset(left + w + depth, top - depth),
                Offset(left, top + s) to Offset(left + depth, top + s - depth),
                Offset(left + w, top + s) to Offset(left + w + depth, top + s - depth),
            ).forEach { (from, to) -> line(from, to, outline, stroke * 0.9f) }
        }

        SolidKind.SPHERE -> {
            circle(center, s * 0.9f, fill = face, outline = null)
            circle(center, s * 0.9f, outline = outline, width = stroke * 1.2f)
            oval(
                topLeft = Offset(center.x - s * 0.9f, center.y - s * 0.28f),
                size = Size(s * 1.8f, s * 0.56f),
                outline = faint.copy(alpha = faint.alpha * progress),
                width = stroke * 0.7f,
            )
        }

        SolidKind.CYLINDER -> {
            val w = s * 1.3f
            val ellipse = s * 0.34f
            val top = center.y - s * 0.85f
            val bottom = center.y + s * 0.85f
            oval(
                topLeft = Offset(center.x - w / 2f, top),
                size = Size(w, ellipse),
                fill = face,
                outline = null,
            )
            oval(Offset(center.x - w / 2f, top), Size(w, ellipse), outline = outline, width = stroke * 1.2f)
            oval(
                topLeft = Offset(center.x - w / 2f, bottom - ellipse),
                size = Size(w, ellipse),
                outline = outline,
                width = stroke * 1.2f,
            )
            listOf(center.x - w / 2f, center.x + w / 2f).forEach { x ->
                line(Offset(x, top + ellipse / 2f), Offset(x, bottom - ellipse / 2f), outline, stroke * 1.2f)
            }
        }

        SolidKind.CONE -> {
            val w = s * 1.2f
            val ellipse = s * 0.32f
            val apex = Offset(center.x, center.y - s * 0.9f)
            val baseY = center.y + s * 0.8f
            val cone = Path().apply {
                moveTo(apex.x, apex.y)
                lineTo(center.x + w / 2f, baseY - ellipse / 2f)
                lineTo(center.x - w / 2f, baseY - ellipse / 2f)
                close()
            }
            path(cone, Accent.copy(alpha = 0.18f), outline, stroke * 1.2f)
            oval(
                topLeft = Offset(center.x - w / 2f, baseY - ellipse),
                size = Size(w, ellipse),
                outline = outline,
                width = stroke * 1.2f,
            )
        }

        SolidKind.TRIANGULAR_PRISM -> {
            val half = s * 0.85f
            val tall = s * 1.25f
            // Longer than it is deep, and set down more than it is set back: a prism drawn on the
            // cube's short 45-degree offset comes out square-on and reads as a pyramid.
            val runX = s * 1.15f
            val runY = s * 0.5f
            val baseY = center.y + tall / 2f - runY / 2f
            val left = center.x - half - runX / 2f
            // The triangle the prism is named for faces the reader; the same triangle set back and
            // up is what makes the cross-section visibly the same all the way through.
            val front = listOf(
                Offset(left, baseY),
                Offset(left + half, baseY - tall),
                Offset(left + half * 2f, baseY),
            )
            val back = front.map { Offset(it.x + runX, it.y - runY) }
            // Only the back bottom-left corner is hidden, so its three edges are the dashed ones.
            line(back[0], back[1], faint, stroke * 0.8f, dashed = true)
            line(back[0], back[2], faint, stroke * 0.8f, dashed = true)
            line(front[0], back[0], faint, stroke * 0.8f, dashed = true)
            line(back[1], back[2], outline, stroke * 0.9f)
            line(front[1], back[1], outline, stroke * 0.9f)
            line(front[2], back[2], outline, stroke * 0.9f)
            path(closedPath(front), Accent.copy(alpha = 0.18f), outline, stroke * 1.2f)
        }

        SolidKind.PYRAMID -> {
            val half = s * 0.95f
            val baseY = center.y + s * 0.7f
            // The base is a square seen at an angle, so it is drawn as a parallelogram; the apex
            // sits over its middle rather than over a front corner.
            val frontLeft = Offset(center.x - half - depth / 2f, baseY)
            val frontRight = Offset(center.x + half - depth / 2f, baseY)
            val backRight = Offset(frontRight.x + depth, baseY - depth)
            val backLeft = Offset(frontLeft.x + depth, baseY - depth)
            val apex = Offset(center.x, center.y - s * 0.95f)
            // The two hidden edges go on faint, the way a textbook dashes them in.
            line(backLeft, frontLeft, faint, stroke * 0.8f, dashed = true)
            line(backLeft, backRight, faint, stroke * 0.8f, dashed = true)
            line(apex, backLeft, faint, stroke * 0.8f, dashed = true)
            path(closedPath(listOf(apex, frontRight, frontLeft)), Accent.copy(alpha = 0.18f), outline, stroke * 1.2f)
            line(frontRight, backRight, outline, stroke * 1.1f)
            line(apex, backRight, outline, stroke * 1.1f)
        }
    }

    val table = if (visual.counts) strings.solidCounts else strings.solidNames
    if (visual.reveal) {
        label(
            text = table.getValue(visual.kind),
            center = Offset(width / 2f, room.y(0)),
            color = Accent,
            factor = SolidCaptionFactor,
            alpha = revealBeat,
        )
    }
}

/** The corners of a [points]-pointed star, tip up, alternating between the two radii. */
private fun starPoints(center: Offset, radius: Float, points: Int): List<Offset> {
    // 0.382 is the pentagram ratio: the inner corners a five-pointed star drawn in one stroke
    // actually lands on. A rounder value gives the blunt star of a sticker sheet.
    val inner = radius * 0.382f
    return List(points * 2) { i ->
        val r = if (i % 2 == 0) radius else inner
        val angle = -PI / 2 + i * PI / points
        Offset(center.x + (r * cos(angle)).toFloat(), center.y + (r * sin(angle)).toFloat())
    }
}

/**
 * The flat shapes no polygon figure can build, each drawn the way it is defined: the oval on the
 * two different widths that separate it from a circle, the semicircle growing out of the diameter
 * it was cut along, and the star one point at a time.
 */
internal fun VisualScope.drawFlatShape(visual: LearnVisual.FlatShape) {
    val center = Offset(width / 2f, height / 2f)
    val radius = size.minDimension * 0.36f

    when (visual.kind) {
        FlatShapeKind.OVAL -> {
            val w = radius * 2.4f
            val h = radius * 1.5f
            val topLeft = Offset(center.x - w / 2f, center.y - h / 2f)
            oval(topLeft, Size(w, h), fill = Accent.copy(alpha = 0.14f), outline = null)
            oval(topLeft, Size(w, h), outline = ink, width = stroke * 1.3f)
            line(
                Offset(center.x - w / 2f * progress, center.y),
                Offset(center.x + w / 2f * progress, center.y),
                Accent,
                stroke * 1.2f,
            )
            line(
                Offset(center.x, center.y - h / 2f * progress),
                Offset(center.x, center.y + h / 2f * progress),
                Accent2,
                stroke * 1.2f,
            )
            dot(center, stroke * 1.3f, ink)
        }

        FlatShapeKind.SEMICIRCLE -> {
            val r = radius * 1.2f
            val flat = center.y + r * 0.4f
            val base = Offset(center.x, flat)
            arc(
                center = base,
                radius = r,
                startAngle = 180f,
                sweepAngle = 180f * progress,
                fill = Accent.copy(alpha = 0.14f),
                outline = ink,
                width = stroke * 1.3f,
            )
            // The cut edge is the whole point of the shape, so it is inked in the accent rather
            // than left as one more stretch of outline.
            line(Offset(center.x - r, flat), Offset(center.x + r, flat), Accent, stroke * 1.4f)
            dot(base, stroke * 1.3f, ink)
        }

        FlatShapeKind.STAR -> {
            val points = starPoints(center, radius * 1.15f, 5)
            ghostOutline(points)
            drawSidesInTurn(points)
        }
    }
}

/** How far past the shape a fold line runs, as a share of its radius. */
private const val FoldLineReach = 1.12f

/** A shape with its fold lines swinging in one at a time. */
internal fun VisualScope.drawSymmetry(visual: LearnVisual.Symmetry) {
    val radius = size.minDimension * 0.33f
    // The fold lines run out past a polygon, so they are what the figure actually spans; a
    // rectangle's stop on its own edges. Either way the shape and the count under it make one
    // block, centred together.
    val spanY = if (visual.rectangle) {
        radius * 1.05f
    } else {
        val corners = polygonPoints(visual.sides.coerceAtLeast(3), Offset.Zero, radius)
        maxOf(corners.maxOf { it.y } - corners.minOf { it.y }, radius * 2f * FoldLineReach)
    }
    val room = captionsUnder(if (visual.reveal) 1 else 0, spanY)
    val center = Offset(width / 2f, room.centerY)

    if (visual.rectangle) {
        val w = radius * 1.7f
        val h = radius * 1.05f
        box(Offset(center.x - w / 2f, center.y - h / 2f), Size(w, h), Accent.copy(alpha = 0.15f), ink)
        val lines = listOf(
            Offset(center.x, center.y - h / 2f) to Offset(center.x, center.y + h / 2f),
            Offset(center.x - w / 2f, center.y) to Offset(center.x + w / 2f, center.y),
        )
        lines.take(visual.lines).forEachIndexed { i, (from, to) ->
            val t = item(i, visual.lines)
            line(from, to, Accent2, stroke * 1.2f, dashed = true, alpha = t)
        }
    } else {
        val sides = visual.sides.coerceAtLeast(3)
        val points = polygonPoints(sides, center, radius)
        path(closedPath(points), Accent.copy(alpha = 0.15f), ink, stroke * 1.2f)
        repeat(visual.lines) { i ->
            val angle = -PI / 2 + i * PI / visual.lines
            val from = Offset(
                center.x - (radius * FoldLineReach * cos(angle)).toFloat(),
                center.y - (radius * FoldLineReach * sin(angle)).toFloat(),
            )
            val to = Offset(
                center.x + (radius * FoldLineReach * cos(angle)).toFloat(),
                center.y + (radius * FoldLineReach * sin(angle)).toFloat(),
            )
            line(from, to, Accent2, stroke * 1.1f, dashed = true, alpha = item(i, visual.lines))
        }
    }

    if (!visual.reveal) return
    label(
        text = strings.symmetryLines,
        center = Offset(width / 2f, room.y(0)),
        color = Accent2,
        factor = CaptionFactor,
        alpha = revealBeat,
    )
}

/** The size the two counts on an area grid's sides are set at. */
private const val AreaCountFactor = 0.09f

/**
 * Unit squares filling a rectangle, labelled with area, perimeter, or both.
 *
 * The two counts are the same annotation on two sides of one figure, so they keep the same clear
 * space from it, and the grid is centred with them rather than on its own - the count down the
 * side is part of the drawing, not something hanging off its left edge.
 */
internal fun VisualScope.drawAreaGrid(visual: LearnVisual.AreaGrid) {
    val cols = visual.cols.coerceAtLeast(1)
    val rows = visual.rows.coerceAtLeast(1)
    val texts = buildList {
        if (visual.reveal && visual.showArea) add(strings.areaTemplate.fillIn(cols * rows, visual.unit))
        if (visual.reveal && visual.showPerimeter) add(strings.perimeterTemplate.fillIn(2 * (cols + rows), visual.unit))
    }
    val room = captions(texts.size)

    val colText = "$cols ${visual.unit}".trim()
    val rowText = "$rows ${visual.unit}".trim()
    val sideBand = labelBand(rowText, AreaCountFactor)
    val topBand = labelBand(AreaCountFactor)

    val cell = min((width * 0.94f - sideBand) / cols, (room.figureBottom * 0.92f - topBand) / rows)
    val gridWidth = cell * cols
    val gridHeight = cell * rows
    // Both counts belong to the block that gets centred: one adds its width across, the other its
    // height down, and the grid takes what is left.
    val left = (width - sideBand - gridWidth) / 2f + sideBand
    val top = room.centerY - (topBand + gridHeight) / 2f + topBand

    repeat(rows) { r ->
        repeat(cols) { c ->
            box(
                topLeft = Offset(left + c * cell, top + r * cell),
                size = Size(cell, cell),
                fill = Accent.copy(alpha = 0.22f),
                outline = faint,
                alpha = item(r * cols + c, rows * cols),
            )
        }
    }
    // The outline is the perimeter, so it takes the colour of the caption that measures it.
    box(Offset(left, top), Size(gridWidth, gridHeight), null, if (visual.showPerimeter) Accent2 else ink)

    labelAbove(colText, Offset(left + gridWidth / 2f, top), ink, AreaCountFactor)
    labelLeftOf(rowText, Offset(left, top + gridHeight / 2f), ink, AreaCountFactor)

    texts.forEachIndexed { i, text ->
        label(
            text = text,
            center = Offset(width / 2f, room.y(i)),
            color = if (i == 0 && visual.showArea) Accent else Accent2,
            factor = CaptionFactor,
            alpha = revealBeat,
        )
    }
}

/** The size a right triangle's side readings are set at. */
private const val SideLabelFactor = 0.1f

/** A right triangle to scale, optionally with the three squares of Pythagoras growing off it. */
internal fun VisualScope.drawRightTriangle(visual: LearnVisual.RightTriangle) {
    val a = visual.a.coerceAtLeast(1).toFloat()
    val b = visual.b.coerceAtLeast(1).toFloat()
    val c = hypot(a, b)
    val hypLabel = if (visual.unknown == Side.HYPOTENUSE) "?" else formatDecimal(c.toDouble())
    val aLabel = if (visual.unknown == Side.A) "?" else visual.a.toString()
    val bLabel = if (visual.unknown == Side.B) "?" else visual.b.toString()
    // Without the squares a side label sits outside the triangle, which is where there is room.
    // With them, outside is the middle of a square that carries its own number, so the labels move
    // inside the triangle instead - the one part of the figure that is always empty - and the
    // shape gets the whole panel.
    val outside = visual.labels && !visual.showSquares
    val under = if (outside) labelBand(SideLabelFactor) else 0f
    val beside = if (outside) labelBand(bLabel, SideLabelFactor) else 0f
    // The hypotenuse reading stands off the slope, so on a flat triangle it reaches above the apex
    // and on a steep one past the far corner. A band each way is what keeps it on the panel.
    val over = if (outside) labelBand(SideLabelFactor) else 0f
    val alongside = if (outside) labelBand(hypLabel, SideLabelFactor) else 0f
    // With the squares drawn, the figure reaches legB to the left of the corner and legA below it,
    // so the whole thing spans (a + b) units in both directions. Reserving (a + a) across and
    // (b + b) down measured a shape that is not this one: a 12-9 triangle came out at 40% of the
    // size it could be, a 144px island in a 410px canvas, with its side labels printed on top of
    // the square labels.
    val unit = if (visual.showSquares) {
        min(width, height) * 0.86f / (a + b)
    } else {
        min((width * 0.94f - beside - alongside) / a, (height * 0.94f - under - over) / b)
    }
    val legA = a * unit
    val legB = b * unit
    // Where the hypotenuse reading actually lands. The allowance above sized the triangle, but
    // centring on the allowance rather than the reading left the figure low and off to the left by
    // the difference. Both are measured out from the right-angle corner.
    val slope = hypot(legA, legB).coerceAtLeast(1f)
    val outward = Offset(legB / slope, -legA / slope)
    val hypWide = if (outside) alongside - labelGap else 0f
    val hypCap = capHeight(SideLabelFactor)
    val hypPush = if (outside) labelGap + outward.x * hypWide / 2f - outward.y * hypCap / 2f else 0f
    val reachRight = maxOf(legA, legA / 2f + outward.x * hypPush + hypWide / 2f)
    val reachUp = maxOf(legB, legB / 2f - outward.y * hypPush + hypCap / 2f)
    val corner = if (visual.showSquares) {
        // Centre the whole span - square, triangle, square - rather than the triangle alone.
        Offset(width / 2f - (legA - legB) / 2f, height / 2f - (legA - legB) / 2f)
    } else {
        // Centre what is actually drawn: the triangle *and* the readings standing off its sides.
        // Hanging the labels off a centred triangle is what pushed the base label past the foot of
        // the panel while a third of the panel sat empty above the apex.
        Offset(
            (width - beside - reachRight) / 2f + beside,
            (height - reachUp - under) / 2f + reachUp,
        )
    }
    val right = Offset(corner.x + legA, corner.y)
    val top = Offset(corner.x, corner.y - legB)

    if (visual.showSquares) {
        // Square on each leg, then on the hypotenuse: the theorem as an area statement.
        box(
            topLeft = Offset(corner.x, corner.y),
            size = Size(legA, legA),
            fill = Accent.copy(alpha = 0.25f),
            outline = faint,
            alpha = stage(0, 3),
        )
        box(
            topLeft = Offset(corner.x - legB, corner.y - legB),
            size = Size(legB, legB),
            fill = Accent2.copy(alpha = 0.25f),
            outline = faint,
            alpha = stage(1, 3),
        )
        label(
            "${(a * a).roundToInt()}",
            Offset(corner.x + legA / 2f, corner.y + legA / 2f),
            Accent,
            0.1f,
            alpha = stage(0, 3),
        )
        label(
            "${(b * b).roundToInt()}",
            Offset(corner.x - legB / 2f, corner.y - legB / 2f),
            Accent2,
            0.1f,
            alpha = stage(1, 3),
        )
    }

    val triangle = Path().apply {
        moveTo(corner.x, corner.y)
        lineTo(right.x, right.y)
        lineTo(top.x, top.y)
        close()
    }
    path(triangle, Accent.copy(alpha = 0.14f), ink, stroke * 1.3f)

    val marker = min(legA, legB) * 0.16f
    box(Offset(corner.x, corner.y - marker), Size(marker, marker), null, Accent2)

    if (visual.labels) {
        // The interior is above the base and to the right of the upright, so the same two calls
        // read a side from inside the triangle or from outside it depending on which side of it
        // there is room on. Both keep the one clear space every label on a figure keeps.
        val onBase = Offset(corner.x + legA / 2f, corner.y)
        val onUpright = Offset(corner.x, corner.y - legB / 2f)
        if (outside) {
            labelBelow(aLabel, onBase, ink, SideLabelFactor)
            labelLeftOf(bLabel, onUpright, ink, SideLabelFactor)
        } else {
            labelAbove(aLabel, onBase, ink, SideLabelFactor)
            labelRightOf(bLabel, onUpright, ink, SideLabelFactor)
        }
        // The hypotenuse reading stands off the slope itself, on the empty side of it, so it keeps
        // its distance whatever shape the triangle is - a fixed nudge up and to the right had it
        // touching the slope on a steep triangle and adrift from it on a flat one.
        labelOutside(
            text = hypLabel,
            at = Offset((right.x + top.x) / 2f, (right.y + top.y) / 2f),
            outward = outward,
            color = Accent,
            factor = SideLabelFactor,
            alpha = revealBeat,
        )
    }

    visual.angle?.let { degrees ->
        val sweep = degrees.toFloat() * progress
        val arcRadius = min(legA, legB) * 0.42f
        // The wedge at the far end of the base, between the base running back to the right angle
        // and the hypotenuse climbing away from it. Canvas angles start at three o'clock and grow
        // clockwise on a downward y-axis, so the base is 180 and the hypotenuse is 180 + degrees:
        // the sweep is positive. Negative swept the same wedge below the base instead, drawing the
        // angle outside the triangle it belongs to - and left the reading where it had always
        // been, because that was already placed up the bisector of the wedge drawn the right way.
        arc(
            center = right,
            radius = arcRadius,
            startAngle = 180f,
            sweepAngle = sweep,
            outline = Accent2,
        )
        // Up the bisector of that same wedge, so the reading stays inside the angle it names
        // however wide the angle opens. A fixed nudge back along the base only ever pointed at
        // one shape of triangle.
        label(
            text = "$degrees",
            center = polar(right, arcRadius * 1.7f, 180f + degrees / 2f, flipY = false),
            color = Accent2,
            factor = 0.09f,
        )
    }
}

/** A circle showing only the measurement the step is about. */
/** The size an angle's reading is drawn at, small enough to sit inside a narrow wedge. */
private const val AngleLabelFactor = 0.09f

/**
 * An angle's reading, placed up the bisector from [vertex] until the arms stop running through it.
 *
 * A wedge of half-angle [halfAngle] is `2 d sin(halfAngle)` wide at distance `d` from its vertex,
 * so the reading is pushed out until that opening clears its own width. Placing it at a fixed
 * fraction of the radius instead is what put "80" on top of the two arms it was measuring and let
 * a chord cut through "40".
 *
 * A narrow wedge never opens far enough to hold the reading, and [limit] stops it drifting off
 * towards the far side of the circle looking for room - a reading that ends up beside the opposite
 * vertex has stopped naming its own angle. Those are the only ones that get a patch of panel
 * behind them: everywhere else the plate would cut a hole in the arms, and the arms are the figure.
 *
 * Both angles here are bisected by the vertical, which is why the offset is a plain subtraction.
 */
private fun VisualScope.angleReading(
    text: String,
    vertex: Offset,
    halfAngle: Float,
    arcRadius: Float,
    limit: Float,
    color: Color,
    alpha: Float,
) {
    val measured = measure(text, labelStyle(AngleLabelFactor, bold = true))
    val opening = sin(halfAngle * PI.toFloat() / 180f).coerceAtLeast(0.05f)
    val needed = (measured.size.width / 2f + measured.size.height * 0.45f) / opening
    val at = Offset(vertex.x, vertex.y - needed.coerceIn(arcRadius + measured.size.height * 0.7f, limit))
    if (needed > limit) {
        chipLabel(text, at, color, AngleLabelFactor, alpha)
    } else {
        label(text, at, color, AngleLabelFactor, alpha)
    }
}

internal fun VisualScope.drawCircleFigure(visual: LearnVisual.CircleFigure) {
    val radius = size.minDimension * 0.36f
    val center = Offset(width / 2f, height / 2f)

    if (visual.fillArea) {
        circle(center, radius * progress, fill = Accent.copy(alpha = 0.3f * progress), outline = null)
    }
    circle(center, radius, outline = ink, width = stroke * 1.3f)

    if (visual.sweepCircumference) {
        arc(
            center = center,
            radius = radius,
            startAngle = -90f,
            sweepAngle = 360f * progress,
            outline = Accent,
            width = stroke * 2f,
        )
    }
    if (visual.showDiameter) {
        line(
            Offset(center.x - radius, center.y),
            Offset(center.x - radius + radius * 2 * progress, center.y),
            Accent2,
            stroke * 1.4f,
        )
        visual.radius?.let {
            labelBelow("d = ${it * 2}", center, Accent2, 0.1f, alpha = revealBeat)
        }
    }
    if (visual.showRadius && !visual.showDiameter) {
        line(center, Offset(center.x + radius * progress, center.y), Accent, stroke * 1.4f)
        visual.radius?.let {
            labelAbove("r = $it", Offset(center.x + radius / 2f, center.y), Accent, 0.1f)
        }
    }
    visual.centreAngle?.let { degrees ->
        // Centre angle and the angle at the circumference standing on the same arc.
        //
        // Both angles are marked with an arc and both readings sit on a chip. Without the arcs the
        // figure named two angles it never drew, and without the chips each number was painted in
        // its own arms' colour on top of those arms, so "80" merged into the strokes it sat on and
        // a chord cut straight through "40". The whole point of the figure is one angle against
        // the other, so both have to be legible at a glance.
        val half = degrees / 2f
        val left = polar(center, radius, -90f - half, flipY = false)
        val right = polar(center, radius, -90f + half, flipY = false)
        val bottom = Offset(center.x, center.y + radius)

        line(center, left, Accent, stroke * 1.2f, alpha = stage(0, 3))
        line(center, right, Accent, stroke * 1.2f, alpha = stage(0, 3))
        arc(
            center = center,
            radius = radius * 0.22f,
            startAngle = -90f - half,
            sweepAngle = degrees.toFloat(),
            outline = Accent,
            width = stroke * 0.9f,
            alpha = stage(0, 3),
        )
        angleReading(
            text = "$degrees",
            vertex = center,
            halfAngle = half,
            arcRadius = radius * 0.22f,
            limit = radius * 0.70f,
            color = Accent,
            alpha = stage(0, 3),
        )

        line(bottom, left, Accent2, stroke * 1.1f, alpha = stage(1, 3))
        line(bottom, right, Accent2, stroke * 1.1f, alpha = stage(1, 3))
        if (visual.reveal) {
            // The inscribed angle is half the centre angle, and the same vertical bisects it.
            arc(
                center = bottom,
                radius = radius * 0.30f,
                startAngle = -90f - half / 2f,
                sweepAngle = half,
                outline = Accent2,
                width = stroke * 0.9f,
                alpha = revealBeat,
            )
            angleReading(
                text = "${half.roundToInt()}",
                vertex = bottom,
                halfAngle = half / 2f,
                arcRadius = radius * 0.30f,
                // Tighter than the centre's, so the reading stays in the half of the circle its
                // own vertex is in rather than climbing towards the centre angle's.
                limit = radius * 0.62f,
                color = Accent2,
                alpha = revealBeat,
            )
        }
    }
    dot(center, stroke * 1.4f, ink)
}

/** How far out along its bisector a wedge's reading sits, as a share of the arm. */
private const val WedgeLabelReach = 0.3f * 1.6f

/** An angle sweeping open to its size, with the partner that completes it when asked for. */
internal fun VisualScope.drawAngleFigure(visual: LearnVisual.AngleFigure) {
    val arm = size.minDimension * 0.62f
    val degrees = visual.degrees.coerceIn(1, 179)
    // What the angle is a part of: a straight line, or the whole turn about the point.
    val whole = if (visual.wholeTurn) 360f else 180f
    val partner = (visual.supplement || visual.wholeTurn) && visual.reveal
    val sweepRadius = arm * WedgeLabelReach / 1.6f

    // Everything an angle draws stands on its baseline, so the baseline is placed from how far
    // above it the figure actually reaches - which is the arm on a sharp angle and, once the arm
    // has swung most of the way round, the reading sitting up the bisector instead. Pinning the
    // baseline to 0.74 of the height left every one of these with a quarter of the panel empty
    // underneath it.
    val radians = degrees * PI.toFloat() / 180f
    // The reading's own half-height: what it needs over the arm's tip is half a line of it,
    // not a whole label band. A band put a fifth of the panel of nothing above a wide angle.
    val readingBand = capHeight(WedgeFactor) / 2f
    val rise = maxOf(
        arm * sin(radians),
        arm * WedgeLabelReach * sin(radians / 2f) + readingBand,
        if (partner) arm * WedgeLabelReach * cos(radians / 2f) + readingBand else 0f,
    )
    // Nothing reaches below the baseline except a whole turn: its circle closes underneath, and
    // the reading for the part of the turn that is left sits down there with it. Every other
    // figure leaves this at zero and the placement is the one it always was.
    val drop = if (visual.wholeTurn) {
        maxOf(sweepRadius, if (partner) arm * WedgeLabelReach + readingBand else 0f)
    } else {
        0f
    }
    // The drawing is as tall as it reaches either side of its baseline, and the sum under it goes
    // directly below that: centred on the panel as one block, rather than the figure centred in
    // the room above a caption pinned to the foot, which left a sharp angle high and the sum
    // stranded under a band of empty panel.
    val room = captionsUnder(if (partner) 1 else 0, rise + drop)
    val origin = Offset(width * 0.5f, room.centerY + (rise - drop) / 2f)

    line(Offset(origin.x - arm, origin.y), Offset(origin.x + arm, origin.y), ink, stroke * 1.2f)

    val swept = degrees * progress
    val angle = swept * PI / 180.0
    val tip = Offset(origin.x + (arm * cos(PI - angle)).toFloat(), origin.y - (arm * sin(PI - angle)).toFloat())

    // Both parts of a straight line are drawn on one radius, so together they read as the single
    // half turn the caption adds them up to. Two radii made the pair look like two unrelated
    // angles that happened to share an arm.
    val otherSweep = (whole - swept).coerceAtLeast(0f)

    // The turn itself, before anything is measured off it. A step asking what is left of a full
    // turn needs the whole turn on the panel: with only the angle's own arc drawn there is
    // nothing on the screen for "the rest of it" to be the rest of, and a 170 degree sweep just
    // reads as a half circle that has overshot.
    if (visual.wholeTurn) {
        arc(center = origin, radius = sweepRadius, startAngle = 0f, sweepAngle = 360f, outline = faint)
    }

    arc(
        center = origin,
        radius = sweepRadius,
        startAngle = 180f,
        sweepAngle = swept,
        outline = Accent,
    )
    if (partner) {
        arc(
            center = origin,
            radius = sweepRadius,
            startAngle = 180f + swept,
            sweepAngle = otherSweep,
            outline = Accent2,
        )
    }

    // The arm goes on after the arcs, not before. It is the same colour as the first of them, so
    // drawn first it had the arc end blending into it instead of stopping cleanly against it.
    line(origin, tip, Accent, stroke * 1.4f)

    // The angle's own reading is a given on every step but one - on "the angle is 130, what is
    // the other one?" it is the 130 - so it does not follow `reveal`. The single step that asks
    // for this number itself turns it off in the catalog instead. See `AngleFigure.labels`.
    if (visual.labels) {
        labelWedge("$degrees", origin, 180f, swept, sweepRadius * 1.6f, Accent, stage(1, 3))
    }

    if (partner) {
        val other = whole.toInt() - degrees
        labelWedge(
            text = "$other",
            origin = origin,
            startDegrees = 180f + swept,
            sweepDegrees = otherSweep,
            radius = sweepRadius * 1.6f,
            color = Accent2,
            alpha = revealBeat,
        )
        // Each number in the sum takes the colour of the arc it counts.
        labelRuns(
            runs = listOf(
                "$degrees" to Accent,
                " + " to null,
                "$other" to Accent2,
                " = " to null,
                // What the sum comes to, in the answer green - the same green the "a + b = 180"
                // card under this figure prints its 180 in. Left in the structure ink, the one
                // number the two halves are being added up to was the only value on the step
                // whose colour said nothing.
                "${whole.toInt()}" to AnswerInk,
            ),
            center = Offset(width / 2f, room.y(0)),
            color = ink,
            factor = CaptionFactor,
            alpha = revealBeat,
        )
    }
}

/** The size an angle figure's readings are set at. */
private const val WedgeFactor = 0.1f

/**
 * An angle's number, set on the bisector of its wedge and far enough out that both arms have
 * opened clear of it. Pinning it to a fixed corner of the figure drew it straight through the arm
 * at some angles, and the arm crossing the number is the one thing the figure must not do.
 */
private fun VisualScope.labelWedge(
    text: String,
    origin: Offset,
    startDegrees: Float,
    sweepDegrees: Float,
    radius: Float,
    color: Color,
    alpha: Float,
) {
    val middle = (startDegrees + sweepDegrees / 2f) * PI / 180.0
    label(
        text = text,
        center = Offset(
            origin.x + (radius * cos(middle)).toFloat(),
            origin.y + (radius * sin(middle)).toFloat(),
        ),
        color = color,
        factor = WedgeFactor,
        alpha = alpha,
    )
}

/** An equation as a balance, with the same blocks lifted off both pans. */
/** The size an algebra rectangle's side and cell readings are set at. */
private const val AlgebraLabelFactor = 0.095f

/**
 * How much longer x is drawn than a one.
 *
 * Not to scale, and deliberately not a whole number of ones: the length of x is the thing the
 * figure must not claim to know, and a side drawn exactly three squares long says it is 3.
 */
private const val AlgebraXLength = 2.6f

internal fun VisualScope.drawAlgebraRect(visual: LearnVisual.AlgebraRect) {
    val leftX = visual.leftX.coerceAtLeast(0)
    val leftOnes = visual.leftOnes.coerceAtLeast(0)
    val topX = visual.topX.coerceAtLeast(0)
    val topOnes = visual.topOnes.coerceAtLeast(0)
    if ((leftX + leftOnes) == 0 || (topX + topOnes) == 0) return

    // A band is one run of a side: its length, and whether that length is x-es or ones. Each is
    // pure, so a cell is always exactly one kind of term - x², an x-term, or a number.
    class Band(val span: Float, val xs: Int, val ones: Int) {
        val label: String get() = when {
            ones > 0 -> ones.toString()
            xs == 1 -> "x"
            else -> "${xs}x"
        }
    }

    val cols = buildList {
        if (topX > 0) add(Band(topX * AlgebraXLength, topX, 0))
        if (topOnes > 0) add(Band(topOnes.toFloat(), 0, topOnes))
    }
    val rows = buildList {
        if (leftX > 0) add(Band(leftX * AlgebraXLength, leftX, 0))
        if (leftOnes > 0) add(Band(leftOnes.toFloat(), 0, leftOnes))
    }

    val totalW = cols.sumOf { it.span.toDouble() }.toFloat()
    val totalH = rows.sumOf { it.span.toDouble() }.toFloat()
    val sideBand = labelBand(rows.maxOf { it.label }, AlgebraLabelFactor)
    val topBand = labelBand(AlgebraLabelFactor)
    val scale = min((width * 0.9f - sideBand) / totalW, (height * 0.84f - topBand) / totalH)
    val w = totalW * scale
    val h = totalH * scale
    val left = (width - sideBand - w) / 2f + sideBand
    val top = (height - topBand - h) / 2f + topBand

    var y = top
    rows.forEachIndexed { r, row ->
        var x = left
        val rowHeight = row.span * scale
        cols.forEachIndexed { c, col ->
            val colWidth = col.span * scale
            // Three kinds of term, three tints, so the picture shows at a glance that the middle
            // cells of a quadratic are the same kind of thing as each other and not as the corners.
            val tint = when {
                row.xs > 0 && col.xs > 0 -> Accent
                row.xs > 0 || col.xs > 0 -> Accent2
                else -> Accent3
            }
            box(
                topLeft = Offset(x, y),
                size = Size(colWidth, rowHeight),
                fill = tint.copy(alpha = 0.18f),
                outline = faint,
                alpha = item(r * cols.size + c, rows.size * cols.size),
            )
            val text = when {
                row.xs > 0 && col.xs > 0 -> (row.xs * col.xs).let { if (it == 1) "x²" else "${it}x²" }
                row.xs > 0 -> (row.xs * col.ones).let { if (it == 1) "x" else "${it}x" }
                col.xs > 0 -> (row.ones * col.xs).let { if (it == 1) "x" else "${it}x" }
                else -> (row.ones * col.ones).toString()
            }
            // What each piece comes to is worked out by the figure, so it is the answer green -
            // and on a question figure it is simply not said, leaving the split to be multiplied.
            label(
                text = text,
                center = Offset(x + colWidth / 2f, y + rowHeight / 2f),
                color = AnswerInk,
                factor = AlgebraLabelFactor,
                alpha = if (visual.reveal) revealBeat else 0f,
            )
            x += colWidth
        }
        y += rowHeight
    }

    box(Offset(left, top), Size(w, h), null, ink)

    // The sides are what the question hands over, so they carry the given colour - and on a
    // question figure they are withheld with everything else, because for a factorising question
    // the sides *are* the answer.
    if (!visual.reveal) return
    var cx = left
    cols.forEach {
        labelAbove(it.label, Offset(cx + it.span * scale / 2f, top), Accent, AlgebraLabelFactor)
        cx += it.span * scale
    }
    var cy = top
    rows.forEach {
        labelLeftOf(it.label, Offset(left, cy + it.span * scale / 2f), Accent, AlgebraLabelFactor)
        cy += it.span * scale
    }
}

internal fun VisualScope.drawBalance(visual: LearnVisual.Balance) {
    // How many of each kind fit across a pan, and where counting them out stops being a picture
    // of a number and becomes a worse way of writing one.
    val xPerRow = 4
    val onesPerRow = 5
    val maxCountedOnes = onesPerRow * 3

    fun rowsFor(xs: Int, ones: Int): Int {
        val xRows = (xs + xPerRow - 1) / xPerRow
        val oneRows = if (ones > maxCountedOnes) 1 else (ones + onesPerRow - 1) / onesPerRow
        return (xRows + oneRows).coerceAtLeast(1)
    }

    val center = Offset(width / 2f, height * 0.17f)
    val beam = width * 0.32f
    val panY = center.y + height * 0.50f
    val rows = maxOf(rowsFor(visual.leftX, visual.leftOnes), rowsFor(visual.rightX, visual.rightOnes))

    // Sized so the widest row stays inside its pan and the tallest stack still clears the beam.
    // Laying a pan out and hoping is what put nine blocks through the beam and off the panel.
    val byWidth = width * 0.28f / (onesPerRow * 1.2f)
    val byHeight = (panY - center.y - labelGap) / (rows * 1.55f)
    val blockSize = min(min(byWidth, byHeight), height * 0.13f)
    val removeProgress = revealBeat
    val plate = blockSize * (onesPerRow * 1.2f) / 2f + blockSize * 0.2f

    line(Offset(center.x - beam, center.y), Offset(center.x + beam, center.y), ink, stroke * 1.6f)
    line(center, Offset(center.x, height * 0.88f), ink, stroke * 1.6f)
    line(
        Offset(center.x - beam * 0.35f, height * 0.9f),
        Offset(center.x + beam * 0.35f, height * 0.9f),
        ink,
        stroke * 1.6f,
    )

    fun drawPan(cx: Float, xBlocks: Int, ones: Int, removed: Int, removedX: Int) {
        line(Offset(cx, center.y), Offset(cx, panY), ink, stroke)
        line(Offset(cx - plate, panY), Offset(cx + plate, panY), ink, stroke * 1.3f)

        // Rows stack upward off the pan, the x-blocks underneath because they are the taller piece.
        val xRows = (xBlocks + xPerRow - 1) / xPerRow
        repeat(xBlocks) { i ->
            val row = i / xPerRow
            val inRow = i % xPerRow
            val n = if (row == xRows - 1) xBlocks - row * xPerRow else xPerRow
            val rowWidth = n * blockSize * 1.3f - blockSize * 0.2f
            val fading = i >= xBlocks - removedX
            val alpha = if (fading) 1f - removeProgress else 1f
            val left = cx - rowWidth / 2f + inRow * blockSize * 1.3f
            val top = panY - blockSize * 1.4f - row * blockSize * 1.55f
            box(Offset(left, top), Size(blockSize * 1.1f, blockSize * 1.4f), Accent.copy(alpha = 0.6f), ink, alpha)
            label("x", Offset(left + blockSize * 0.55f, top + blockSize * 0.7f), ink, 0.07f, alpha)
        }

        val onesBase = panY - xRows * blockSize * 1.55f
        if (ones > maxCountedOnes) {
            // A pan holding forty-two ones is a number, not a picture of one.
            val w = blockSize * (onesPerRow * 1.2f) * 0.8f
            box(
                topLeft = Offset(cx - w / 2f, onesBase - blockSize * 1.2f),
                size = Size(w, blockSize * 1.2f),
                fill = Accent2.copy(alpha = 0.55f),
                outline = ink,
            )
            label(ones.toString(), Offset(cx, onesBase - blockSize * 0.6f), ink, 0.08f)
            return
        }
        val oneRows = (ones + onesPerRow - 1) / onesPerRow
        repeat(ones) { i ->
            val row = i / onesPerRow
            val inRow = i % onesPerRow
            val n = if (row == oneRows - 1) ones - row * onesPerRow else onesPerRow
            val rowWidth = n * blockSize * 1.2f - blockSize * 0.2f
            val fading = i >= ones - removed
            box(
                topLeft = Offset(
                    cx - rowWidth / 2f + inRow * blockSize * 1.2f,
                    onesBase - blockSize - row * blockSize * 1.2f,
                ),
                size = Size(blockSize, blockSize),
                fill = Accent2.copy(alpha = 0.55f),
                outline = ink,
                alpha = if (fading) 1f - removeProgress else 1f,
            )
        }
    }

    drawPan(center.x - beam, visual.leftX, visual.leftOnes, visual.remove, visual.removeX)
    drawPan(center.x + beam, visual.rightX, visual.rightOnes, visual.remove, visual.removeX)

    if (visual.remove > 0) {
        label(
            text = strings.takeFromBothSidesTemplate.fillIn(visual.remove),
            center = Offset(width / 2f, height * 0.97f),
            color = Accent2,
            factor = 0.08f,
            alpha = removeProgress,
        )
    }
}
