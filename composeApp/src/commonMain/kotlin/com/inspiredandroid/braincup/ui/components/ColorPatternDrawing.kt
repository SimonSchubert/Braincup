package com.inspiredandroid.braincup.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.luminance
import com.inspiredandroid.braincup.games.tools.ColorPattern
import kotlin.math.ceil
import kotlin.math.hypot

/**
 * Textures [clip] with [pattern] so that a figure can be told apart without seeing its hue.
 *
 * The pitch scales with the figure but never drops below [MinPitchPx], so the texture still reads
 * on the small figures of a Pattern Sequence panel. The ink is black or white, whichever stands out
 * from [face], so it survives every palette and theme.
 */
fun DrawScope.drawColorPattern(pattern: ColorPattern, face: Color, clip: Path) {
    if (pattern == ColorPattern.PLAIN) return
    val bounds = clip.getBounds()
    if (bounds.isEmpty) return
    clipPath(clip) { drawPatternIn(pattern, face, bounds) }
}

fun DrawScope.drawColorPattern(pattern: ColorPattern, face: Color, bounds: Rect) {
    if (pattern == ColorPattern.PLAIN || bounds.isEmpty) return
    clipRect(bounds.left, bounds.top, bounds.right, bounds.bottom) { drawPatternIn(pattern, face, bounds) }
}

private fun DrawScope.drawPatternIn(pattern: ColorPattern, face: Color, bounds: Rect) {
    val ink = patternInk(face)
    val pitch = (bounds.minDimension / 4.5f).coerceAtLeast(MinPitchPx)
    val stroke = pitch * 0.34f
    val left = bounds.left
    val top = bounds.top
    val right = bounds.right
    val bottom = bounds.bottom
    when (pattern) {
        ColorPattern.PLAIN -> Unit

        ColorPattern.HORIZONTAL_STRIPES -> {
            var y = top + pitch / 2
            while (y < bottom) {
                drawLine(ink, Offset(left, y), Offset(right, y), stroke)
                y += pitch
            }
        }

        ColorPattern.VERTICAL_STRIPES -> {
            var x = left + pitch / 2
            while (x < right) {
                drawLine(ink, Offset(x, top), Offset(x, bottom), stroke)
                x += pitch
            }
        }

        ColorPattern.DIAGONAL_STRIPES -> drawDiagonals(ink, bounds, pitch * 1.2f, stroke, rising = true)

        ColorPattern.CROSSHATCH -> {
            drawDiagonals(ink, bounds, pitch * 1.2f, stroke * 0.8f, rising = true)
            drawDiagonals(ink, bounds, pitch * 1.2f, stroke * 0.8f, rising = false)
        }

        ColorPattern.DOTS -> {
            val radius = pitch * 0.3f
            var y = top + pitch / 2
            var row = 0
            while (y < bottom + pitch) {
                var x = left + if (row % 2 == 0) pitch / 2 else pitch
                while (x < right + pitch) {
                    drawCircle(ink, radius, Offset(x, y))
                    x += pitch
                }
                y += pitch * 0.87f
                row++
            }
        }

        ColorPattern.CHECKER -> {
            val cell = pitch * 0.8f
            val columns = ceil(bounds.width / cell).toInt()
            val rows = ceil(bounds.height / cell).toInt()
            for (r in 0 until rows) {
                for (c in 0 until columns) {
                    if ((r + c) % 2 == 0) {
                        drawRect(
                            ink,
                            topLeft = Offset(left + c * cell, top + r * cell),
                            size = Size(cell, cell),
                        )
                    }
                }
            }
        }

        ColorPattern.RINGS -> {
            val center = bounds.center
            val reach = hypot(bounds.width, bounds.height) / 2
            var radius = pitch * 0.6f
            while (radius < reach) {
                drawCircle(ink, radius, center, style = Stroke(stroke))
                radius += pitch
            }
        }
    }
}

private fun DrawScope.drawDiagonals(ink: Color, bounds: Rect, pitch: Float, stroke: Float, rising: Boolean) {
    val span = bounds.width + bounds.height
    var offset = -bounds.height
    while (offset < bounds.width + pitch) {
        if (rising) {
            drawLine(
                ink,
                Offset(bounds.left + offset, bounds.bottom),
                Offset(bounds.left + offset + bounds.height, bounds.top),
                stroke,
            )
        } else {
            drawLine(
                ink,
                Offset(bounds.left + offset, bounds.top),
                Offset(bounds.left + offset + bounds.height, bounds.bottom),
                stroke,
            )
        }
        offset += pitch
        if (offset > span) break
    }
}

private fun patternInk(face: Color): Color = if (face.luminance() > 0.35f) {
    Color.Black.copy(alpha = 0.42f)
} else {
    Color.White.copy(alpha = 0.55f)
}

private const val MinPitchPx = 9f
