package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.inspiredandroid.braincup.matchstickriddles.MatchstickRiddle
import com.inspiredandroid.braincup.matchstickriddles.Stick
import com.inspiredandroid.braincup.ui.theme.SuccessGreen

/**
 * A static, non-interactive rendering of a riddle's starting (unsolved) equation, used as a glanceable
 * preview tile on the overview screen. It reuses the same [boardTransform] fit-to-bounds scaling as the
 * interactive board, so it never distorts: a tile whose aspect differs from the puzzle just gets a little
 * extra padding around the centered matchsticks.
 */
@Composable
fun MatchstickBoardPreview(
    riddle: MatchstickRiddle,
    solved: Boolean,
    modifier: Modifier = Modifier,
) {
    val woodBody = Color(0xFFE0A85B)
    val woodSide = Color(0xFFB07E3C)
    val headColor = if (solved) SuccessGreen else Color(0xFF4C9A52)
    Canvas(modifier = modifier.fillMaxSize()) {
        val t = boardTransform(size.width, size.height, riddle)
        riddle.initial.forEach { i ->
            val s = riddle.slots[i]
            drawStick(t.toPx(s.ax, s.ay), t.toPx(s.bx, s.by), t.scale, woodBody, woodSide, headColor)
        }
    }
}

/** Maps normalized board coordinates to canvas pixels (and back), fitting the board centered. */
internal class BoardTransform(val scale: Float, val offsetX: Float, val offsetY: Float) {
    fun toPx(nx: Float, ny: Float) = Offset(offsetX + nx * scale, offsetY + ny * scale)
    fun toNorm(p: Offset): Pair<Float, Float> = (p.x - offsetX) / scale to (p.y - offsetY) / scale
}

internal fun boardTransform(width: Float, height: Float, riddle: MatchstickRiddle): BoardTransform {
    val pad = 0.6f
    val totalW = riddle.boardWidth + 2 * pad
    val totalH = riddle.boardHeight + 2 * pad
    val scale = minOf(width / totalW, height / totalH)
    val offsetX = (width - riddle.boardWidth * scale) / 2f
    val offsetY = (height - riddle.boardHeight * scale) / 2f
    return BoardTransform(scale, offsetX, offsetY)
}

internal fun DrawScope.drawStick(a: Offset, b: Offset, scale: Float, body: Color, side: Color, head: Color) {
    val width = scale * 0.16f
    drawLine(side, a, b, strokeWidth = width * 1.25f, cap = StrokeCap.Round)
    drawLine(body, a, b, strokeWidth = width, cap = StrokeCap.Round)
    drawCircle(head, radius = width * 0.85f, center = a)
}

internal fun DrawScope.drawStickOutline(stick: Stick, t: BoardTransform, color: Color, strong: Boolean) {
    drawLine(
        color,
        t.toPx(stick.ax, stick.ay),
        t.toPx(stick.bx, stick.by),
        strokeWidth = t.scale * (if (strong) 0.16f else 0.10f),
        cap = StrokeCap.Round,
    )
}
