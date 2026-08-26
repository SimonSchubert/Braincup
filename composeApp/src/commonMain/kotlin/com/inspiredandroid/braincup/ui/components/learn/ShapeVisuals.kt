package com.inspiredandroid.braincup.ui.components.learn

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.inspiredandroid.braincup.learn.LearnVisual
import com.inspiredandroid.braincup.learn.Side
import com.inspiredandroid.braincup.learn.SolidKind
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
 * A real polygon of the size the question asks about, drawn one side at a time with its corners
 * numbered as they arrive — so "how many sides does a pentagon have" answers itself.
 */
internal fun VisualScope.drawPolygon(visual: LearnVisual.Polygon) {
    val sides = visual.sides.coerceAtLeast(3)
    val radius = size.minDimension * 0.34f
    val center = Offset(width / 2f, height * 0.46f)
    val points = polygonPoints(sides, center, radius)

    // Ghost of the finished shape, so the figure never looks broken mid-animation.
    val outlinePath = Path().apply {
        moveTo(points[0].x, points[0].y)
        points.drop(1).forEach { lineTo(it.x, it.y) }
        close()
    }
    path(outlinePath, fill = Accent.copy(alpha = 0.12f * progress), outline = faint, width = stroke * 0.7f)

    repeat(sides) { i ->
        val from = points[i]
        val to = points[(i + 1) % sides]
        val t = item(i, sides)
        if (t <= 0f) return@repeat
        line(from, Offset(from.x + (to.x - from.x) * t, from.y + (to.y - from.y) * t), Accent, stroke * 1.5f)
    }

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
                from.x + outward.x / length * radius * 0.26f,
                from.y + outward.y / length * radius * 0.26f,
            )
            dot(from, stroke * 1.6f, Accent2, alpha = cornerAlpha)
            label("${i + 1}", at, Accent2, 0.09f, alpha = cornerAlpha)
        }
    }

    if (!revealing(visual.reveal)) return
    label(
        text = "$sides sides, $sides corners",
        center = Offset(width / 2f, height * 0.93f),
        color = Accent,
        factor = 0.1f,
        alpha = stage(2, 3),
    )
}

/** One solid, drawn in the way that makes its faces and edges countable. */
internal fun VisualScope.drawSolid(visual: LearnVisual.Solid) {
    val s = size.minDimension * 0.34f
    val center = Offset(width / 2f, height * 0.48f)
    val depth = s * 0.42f
    val outline = ink.copy(alpha = progress)

    when (visual.kind) {
        SolidKind.CUBE, SolidKind.PRISM -> {
            val w = if (visual.kind == SolidKind.PRISM) s * 1.35f else s
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
            draw.drawCircle(Accent.copy(alpha = 0.18f * progress), s * 0.9f, center)
            draw.drawCircle(outline, s * 0.9f, center, style = Stroke(width = stroke * 1.2f))
            draw.drawOval(
                color = faint.copy(alpha = faint.alpha * progress),
                topLeft = Offset(center.x - s * 0.9f, center.y - s * 0.28f),
                size = Size(s * 1.8f, s * 0.56f),
                style = Stroke(width = stroke * 0.7f),
            )
        }

        SolidKind.CYLINDER -> {
            val w = s * 1.3f
            val ellipse = s * 0.34f
            val top = center.y - s * 0.85f
            val bottom = center.y + s * 0.85f
            draw.drawOval(
                Accent.copy(alpha = 0.18f * progress),
                Offset(center.x - w / 2f, top),
                Size(w, ellipse),
            )
            draw.drawOval(outline, Offset(center.x - w / 2f, top), Size(w, ellipse), style = Stroke(stroke * 1.2f))
            draw.drawOval(
                outline,
                Offset(center.x - w / 2f, bottom - ellipse),
                Size(w, ellipse),
                style = Stroke(stroke * 1.2f),
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
            draw.drawOval(
                outline,
                Offset(center.x - w / 2f, baseY - ellipse),
                Size(w, ellipse),
                style = Stroke(stroke * 1.2f),
            )
        }
    }

    val name = when (visual.kind) {
        SolidKind.CUBE -> "cube"
        SolidKind.SPHERE -> "sphere"
        SolidKind.CYLINDER -> "cylinder"
        SolidKind.CONE -> "cone"
        SolidKind.PRISM -> "prism"
    }
    val counts = when (visual.kind) {
        SolidKind.CUBE -> "6 faces · 12 edges · 8 corners"
        SolidKind.SPHERE -> "no flat faces, no edges"
        SolidKind.CYLINDER -> "2 flat faces · 1 curved"
        SolidKind.CONE -> "1 flat face · 1 point"
        SolidKind.PRISM -> "same cross-section throughout"
    }
    if (revealing(visual.reveal)) {
        label(
            text = if (visual.counts) counts else name,
            center = Offset(width / 2f, height * 0.93f),
            color = Accent,
            factor = 0.1f,
            alpha = stage(2, 3),
        )
    }
}

/** A shape with its fold lines swinging in one at a time. */
internal fun VisualScope.drawSymmetry(visual: LearnVisual.Symmetry) {
    val center = Offset(width / 2f, height * 0.46f)
    val radius = size.minDimension * 0.33f

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
        val shape = Path().apply {
            moveTo(points[0].x, points[0].y)
            points.drop(1).forEach { lineTo(it.x, it.y) }
            close()
        }
        path(shape, Accent.copy(alpha = 0.15f), ink, stroke * 1.2f)
        repeat(visual.lines) { i ->
            val angle = -PI / 2 + i * PI / visual.lines
            val from = Offset(
                center.x - (radius * 1.12f * cos(angle)).toFloat(),
                center.y - (radius * 1.12f * sin(angle)).toFloat(),
            )
            val to = Offset(
                center.x + (radius * 1.12f * cos(angle)).toFloat(),
                center.y + (radius * 1.12f * sin(angle)).toFloat(),
            )
            line(from, to, Accent2, stroke * 1.1f, dashed = true, alpha = item(i, visual.lines))
        }
    }

    if (!revealing(visual.reveal)) return
    label(
        text = "${visual.lines} line${if (visual.lines == 1) "" else "s"} of symmetry",
        center = Offset(width / 2f, height * 0.93f),
        color = Accent2,
        factor = 0.1f,
        alpha = stage(2, 3),
    )
}

/** Unit squares filling a rectangle, labelled with area, perimeter, or both. */
internal fun VisualScope.drawAreaGrid(visual: LearnVisual.AreaGrid) {
    val cols = visual.cols.coerceAtLeast(1)
    val rows = visual.rows.coerceAtLeast(1)
    val cell = min(width * 0.66f / cols, height * 0.56f / rows)
    val left = width / 2f - cell * cols / 2f
    val top = height * 0.44f - cell * rows / 2f

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
    box(Offset(left, top), Size(cell * cols, cell * rows), null, ink)

    label("$cols ${visual.unit}", Offset(left + cell * cols / 2f, top - height * 0.09f), ink, 0.09f)
    label("$rows ${visual.unit}", Offset(left - width * 0.09f, top + cell * rows / 2f), ink, 0.09f)

    if (!revealing(visual.reveal)) return
    val texts = buildList {
        if (visual.showArea) add("area = ${cols * rows} sq ${visual.unit}")
        if (visual.showPerimeter) add("perimeter = ${2 * (cols + rows)} ${visual.unit}")
    }
    texts.forEachIndexed { i, text ->
        label(
            text = text,
            center = Offset(width / 2f, height * (0.88f + i * 0.09f)),
            color = if (i == 0 && visual.showArea) Accent else Accent2,
            factor = 0.095f,
            alpha = stage(2, 3),
        )
    }
}

/** A right triangle to scale, optionally with the three squares of Pythagoras growing off it. */
internal fun VisualScope.drawRightTriangle(visual: LearnVisual.RightTriangle) {
    val a = visual.a.coerceAtLeast(1).toFloat()
    val b = visual.b.coerceAtLeast(1).toFloat()
    val c = hypot(a, b)
    val room = if (visual.showSquares) 0.3f else 0.62f
    val unit = min(width * room / (a + if (visual.showSquares) a else 0f), height * room / (b + if (visual.showSquares) b else 0f))
    val legA = a * unit
    val legB = b * unit
    val corner = Offset(
        width / 2f - legA / 2f + if (visual.showSquares) legA * 0.1f else 0f,
        height * (if (visual.showSquares) 0.46f else 0.62f) + legB / 2f,
    )
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
        val hypLabel = if (visual.unknown == Side.HYPOTENUSE) "?" else formatDecimal(c.toDouble())
        val aLabel = if (visual.unknown == Side.A) "?" else visual.a.toString()
        val bLabel = if (visual.unknown == Side.B) "?" else visual.b.toString()
        label(aLabel, Offset(corner.x + legA / 2f, corner.y + height * 0.07f), ink, 0.1f)
        label(bLabel, Offset(corner.x - width * 0.05f, corner.y - legB / 2f), ink, 0.1f)
        label(
            text = hypLabel,
            center = Offset((right.x + top.x) / 2f + width * 0.045f, (right.y + top.y) / 2f - height * 0.03f),
            color = Accent,
            factor = 0.1f,
            alpha = stage(2, 3),
        )
    }

    visual.angle?.let { degrees ->
        val sweep = degrees.toFloat() * progress
        val arcRadius = min(legA, legB) * 0.42f
        draw.drawArc(
            color = Accent2,
            startAngle = 180f,
            sweepAngle = -sweep,
            useCenter = false,
            topLeft = Offset(right.x - arcRadius, right.y - arcRadius),
            size = Size(arcRadius * 2, arcRadius * 2),
            style = Stroke(width = stroke),
        )
        label("$degrees", Offset(right.x - arcRadius * 1.5f, right.y - arcRadius * 0.5f), Accent2, 0.09f)
    }
}

/** A circle showing only the measurement the step is about. */
internal fun VisualScope.drawCircleFigure(visual: LearnVisual.CircleFigure) {
    val radius = size.minDimension * 0.36f
    val center = Offset(width / 2f, height * 0.47f)

    if (visual.fillArea) {
        draw.drawCircle(Accent.copy(alpha = 0.3f * progress), radius * progress, center)
    }
    draw.drawCircle(ink, radius, center, style = Stroke(width = stroke * 1.3f))

    if (visual.sweepCircumference) {
        draw.drawArc(
            color = Accent,
            startAngle = -90f,
            sweepAngle = 360f * progress,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = stroke * 2f),
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
            label("d = ${it * 2}", Offset(center.x, center.y + height * 0.1f), Accent2, 0.1f, alpha = stage(2, 3))
        }
    }
    if (visual.showRadius && !visual.showDiameter) {
        line(center, Offset(center.x + radius * progress, center.y), Accent, stroke * 1.4f)
        visual.radius?.let {
            label("r = $it", Offset(center.x + radius / 2f, center.y - height * 0.08f), Accent, 0.1f)
        }
    }
    visual.centreAngle?.let { degrees ->
        // Centre angle and the angle at the circumference standing on the same arc.
        val half = degrees / 2f
        val left = Offset(
            center.x + (radius * cos((-90 - degrees / 2.0) * PI / 180)).toFloat(),
            center.y + (radius * sin((-90 - degrees / 2.0) * PI / 180)).toFloat(),
        )
        val right = Offset(
            center.x + (radius * cos((-90 + degrees / 2.0) * PI / 180)).toFloat(),
            center.y + (radius * sin((-90 + degrees / 2.0) * PI / 180)).toFloat(),
        )
        val bottom = Offset(center.x, center.y + radius)
        line(center, left, Accent, stroke * 1.2f, alpha = stage(0, 3))
        line(center, right, Accent, stroke * 1.2f, alpha = stage(0, 3))
        label("$degrees", Offset(center.x, center.y - height * 0.08f), Accent, 0.1f, alpha = stage(0, 3))
        line(bottom, left, Accent2, stroke * 1.1f, alpha = stage(1, 3))
        line(bottom, right, Accent2, stroke * 1.1f, alpha = stage(1, 3))
        if (revealing(visual.reveal)) {
            label(
                text = "${half.roundToInt()}",
                center = Offset(center.x, center.y + radius * 0.68f),
                color = Accent2,
                factor = 0.1f,
                alpha = stage(2, 3),
            )
        }
    }
    dot(center, stroke * 1.4f, ink)
}

/** An angle sweeping open to its size, with its partner on the straight line when asked for. */
internal fun VisualScope.drawAngleFigure(visual: LearnVisual.AngleFigure) {
    val origin = Offset(width * 0.5f, height * 0.74f)
    val arm = size.minDimension * 0.62f
    val degrees = visual.degrees.coerceIn(1, 179)

    line(Offset(origin.x - arm, origin.y), Offset(origin.x + arm, origin.y), ink, stroke * 1.2f)

    val swept = degrees * progress
    val angle = swept * PI / 180.0
    val tip = Offset(origin.x + (arm * cos(PI - angle)).toFloat(), origin.y - (arm * sin(PI - angle)).toFloat())
    line(origin, tip, Accent, stroke * 1.4f)

    val sweepRadius = arm * 0.3f
    draw.drawArc(
        color = Accent,
        startAngle = 180f,
        sweepAngle = swept,
        useCenter = false,
        topLeft = Offset(origin.x - sweepRadius, origin.y - sweepRadius),
        size = Size(sweepRadius * 2, sweepRadius * 2),
        style = Stroke(width = stroke),
    )
    labelWedge("$degrees", origin, 180f, swept, sweepRadius * 1.6f, Accent, stage(1, 3))

    if (visual.supplement && revealing(visual.reveal)) {
        val other = 180 - degrees
        val otherRadius = arm * 0.44f
        draw.drawArc(
            color = Accent2,
            startAngle = 180f + swept,
            sweepAngle = (180f - swept).coerceAtLeast(0f),
            useCenter = false,
            topLeft = Offset(origin.x - otherRadius, origin.y - otherRadius),
            size = Size(otherRadius * 2, otherRadius * 2),
            style = Stroke(width = stroke),
        )
        labelWedge(
            text = "$other",
            origin = origin,
            startDegrees = 180f + swept,
            sweepDegrees = (180f - swept).coerceAtLeast(0f),
            radius = otherRadius * 1.35f,
            color = Accent2,
            alpha = stage(2, 3),
        )
        label(
            text = "$degrees + $other = 180",
            center = Offset(width / 2f, height * 0.94f),
            color = ink,
            factor = 0.095f,
            alpha = stage(2, 3),
        )
    }
}

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
        factor = 0.1f,
        alpha = alpha,
    )
}

/** An equation as a balance, with the same blocks lifted off both pans. */
internal fun VisualScope.drawBalance(visual: LearnVisual.Balance) {
    val center = Offset(width / 2f, height * 0.3f)
    val beam = width * 0.33f
    line(Offset(center.x - beam, center.y), Offset(center.x + beam, center.y), ink, stroke * 1.6f)
    line(center, Offset(center.x, height * 0.8f), ink, stroke * 1.6f)
    line(
        Offset(center.x - beam * 0.35f, height * 0.83f),
        Offset(center.x + beam * 0.35f, height * 0.83f),
        ink,
        stroke * 1.6f,
    )

    val blockSize = min(width * 0.055f, height * 0.14f)
    val removeProgress = stage(2, 3)

    fun drawPan(cx: Float, xBlocks: Int, ones: Int, removed: Int) {
        val panY = center.y + height * 0.1f
        line(Offset(cx, center.y), Offset(cx, panY), ink, stroke)
        line(Offset(cx - blockSize * 2.4f, panY), Offset(cx + blockSize * 2.4f, panY), ink, stroke * 1.3f)

        var x = cx - (xBlocks + ones) * blockSize * 0.65f
        repeat(xBlocks) {
            box(
                topLeft = Offset(x, panY - blockSize * 1.5f),
                size = Size(blockSize * 1.1f, blockSize * 1.4f),
                fill = Accent.copy(alpha = 0.6f),
                outline = ink,
            )
            label("x", Offset(x + blockSize * 0.55f, panY - blockSize * 0.8f), ink, 0.075f)
            x += blockSize * 1.3f
        }
        repeat(ones) { i ->
            // Blocks being taken from both sides fade out together.
            val fading = i >= ones - removed
            box(
                topLeft = Offset(x, panY - blockSize * 1.2f),
                size = Size(blockSize, blockSize),
                fill = Accent2.copy(alpha = 0.55f),
                outline = ink,
                alpha = if (fading) 1f - removeProgress else 1f,
            )
            x += blockSize * 1.2f
        }
    }

    drawPan(center.x - beam, visual.leftX, visual.leftOnes, visual.remove)
    drawPan(center.x + beam, 0, visual.rightOnes, visual.remove)

    if (visual.remove > 0) {
        label(
            text = "take ${visual.remove} from both sides",
            center = Offset(width / 2f, height * 0.94f),
            color = Accent2,
            factor = 0.085f,
            alpha = removeProgress,
        )
    }
}
