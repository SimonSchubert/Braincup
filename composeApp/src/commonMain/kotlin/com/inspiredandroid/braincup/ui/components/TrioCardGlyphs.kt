package com.inspiredandroid.braincup.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.app.TrioUiState
import com.inspiredandroid.braincup.games.TrioFill
import com.inspiredandroid.braincup.games.TrioGame
import com.inspiredandroid.braincup.games.TrioShape
import com.inspiredandroid.braincup.ui.theme.SelectedTileFaceDark
import com.inspiredandroid.braincup.ui.theme.SelectedTileFaceLight
import com.inspiredandroid.braincup.ui.theme.SuccessGreenSoft
import com.inspiredandroid.braincup.ui.theme.UnselectedTileFaceDark
import com.inspiredandroid.braincup.ui.theme.isDarkColorScheme
import kotlin.math.min

@Composable
fun TrioCardGlyphs(
    shape: TrioShape,
    count: Int,
    fill: TrioFill,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val n = count.coerceIn(1, 3)
    BoxWithConstraints(modifier.padding(horizontal = 6.dp, vertical = 8.dp)) {
        val gap = 3.dp
        val glyph = minOf(maxHeight, (maxWidth - gap * (n - 1)) / n)
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(gap, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(n) {
                Canvas(modifier = Modifier.size(glyph)) {
                    drawTrioShape(shape, fill, color)
                }
            }
        }
    }
}

private fun DrawScope.drawTrioShape(shape: TrioShape, fill: TrioFill, color: Color) {
    val pad = min(size.width, size.height) * 0.08f
    val path = trioShapePath(shape, size, pad)
    val stroke = Stroke(
        width = (min(size.width, size.height) * 0.12f).coerceIn(2.5f, 5f),
        cap = StrokeCap.Round,
    )
    when (fill) {
        TrioFill.SOLID -> drawPath(path, color)
        TrioFill.OUTLINE -> drawPath(path, color, style = stroke)
        TrioFill.STRIPED -> {
            clipPath(path) {
                val gap = min(size.width, size.height) * 0.22f
                var x = -size.height
                while (x < size.width + size.height) {
                    drawLine(
                        color = color,
                        start = Offset(x, size.height),
                        end = Offset(x + size.height, 0f),
                        strokeWidth = gap * 0.45f,
                        cap = StrokeCap.Butt,
                    )
                    x += gap
                }
            }
            drawPath(path, color, style = stroke)
        }
    }
}

private fun trioShapePath(shape: TrioShape, size: Size, pad: Float): Path {
    val left = pad
    val top = pad
    val right = size.width - pad
    val bottom = size.height - pad
    return when (shape) {
        TrioShape.CIRCLE -> Path().apply { addOval(Rect(left, top, right, bottom)) }
        TrioShape.SQUARE -> Path().apply { addRect(Rect(left, top, right, bottom)) }
        TrioShape.TRIANGLE -> Path().apply {
            moveTo((left + right) / 2f, top)
            lineTo(right, bottom)
            lineTo(left, bottom)
            close()
        }
    }
}

@Composable
fun TrioCardTile(
    card: TrioUiState.Card,
    locked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDark = isDarkColorScheme
    val selectedFace = if (isDark) SelectedTileFaceDark else SelectedTileFaceLight
    val unselectedFace = if (isDark) {
        UnselectedTileFaceDark
    } else {
        MaterialTheme.colorScheme.surfaceContainer
    }
    val targetFace = when (card.feedback) {
        TrioGame.CardFeedback.CORRECT -> SuccessGreenSoft
        TrioGame.CardFeedback.WRONG -> MaterialTheme.colorScheme.errorContainer
        TrioGame.CardFeedback.SELECTED -> selectedFace
        TrioGame.CardFeedback.DIMMED,
        TrioGame.CardFeedback.NONE,
        -> unselectedFace
    }
    val face by animateColorAsState(
        targetValue = targetFace,
        animationSpec = tween(220),
        label = "trioTileFace",
    )
    val interactive = !locked && card.feedback != TrioGame.CardFeedback.DIMMED
    val tileModifier = if (card.feedback == TrioGame.CardFeedback.DIMMED) {
        modifier.alpha(0.3f)
    } else {
        modifier
    }
    PrismTile(
        face = face,
        modifier = tileModifier.hoverHand(interactive),
        isClickable = interactive,
        isSelected = card.feedback == TrioGame.CardFeedback.CORRECT ||
            card.feedback == TrioGame.CardFeedback.WRONG,
        onClick = onClick,
    ) {
        TrioCardGlyphs(
            shape = card.shape,
            count = card.count,
            fill = card.fill,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
