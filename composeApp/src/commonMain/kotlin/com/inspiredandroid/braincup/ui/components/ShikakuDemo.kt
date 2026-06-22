package com.inspiredandroid.braincup.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.game_shikaku_desc
import com.inspiredandroid.braincup.ui.screens.ShikakuBoardFrame
import com.inspiredandroid.braincup.ui.screens.ShikakuCellColor
import com.inspiredandroid.braincup.ui.theme.CatRegionColors
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.numberFontFamily
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

private const val ShikakuCols = 4
private const val ShikakuRows = 4
private val ShikakuCellSize = 56.dp

// How long a rectangle takes to grow out from its number, then the beat it rests before the next one.
private const val GrowMillis = 480
private const val CommitRestMillis = 360L
private const val SolvedHoldMillis = 1400L
private const val ResetPauseMillis = 500L

private data class ShikakuRect(
    val top: Int,
    val left: Int,
    val bottom: Int,
    val right: Int,
    val clueRow: Int,
    val clueCol: Int,
) {
    val area: Int get() = (bottom - top + 1) * (right - left + 1)
}

// A fixed 4x4 puzzle whose rectangles partition the whole grid; each holds exactly its one clue.
private val ShikakuSolution = listOf(
    ShikakuRect(top = 0, left = 0, bottom = 0, right = 2, clueRow = 0, clueCol = 0),
    ShikakuRect(top = 0, left = 3, bottom = 2, right = 3, clueRow = 0, clueCol = 3),
    ShikakuRect(top = 1, left = 0, bottom = 3, right = 1, clueRow = 1, clueCol = 0),
    ShikakuRect(top = 1, left = 2, bottom = 2, right = 2, clueRow = 1, clueCol = 2),
    ShikakuRect(top = 3, left = 2, bottom = 3, right = 3, clueRow = 3, clueCol = 2),
)

private fun lerp(a: Float, b: Float, t: Float) = a + (b - a) * t

/**
 * Animated tutorial board for Shikaku: a fixed 4x4 puzzle that solves itself on a loop. One rectangle
 * at a time grows out from its number (orange drag-preview) and then commits to a coloured region with
 * a bold border, mirroring the real board. Once the grid is partitioned it holds, resets, and repeats.
 */
@Composable
fun ShikakuDemo(modifier: Modifier = Modifier) {
    val cellColor = ShikakuCellColor
    val gridLineColor = Color(0xFF1A1A1A).copy(alpha = 0.2f)
    val borderColor = Color(0xFF1A1A1A)
    val clueColor = Color(0xFF1A1A1A)
    val previewColor = Primary
    val numberFont = numberFontFamily()
    val textMeasurer = rememberTextMeasurer()

    // committed = rectangles already placed; activeIndex = the one currently growing (-1 = none).
    var committed by remember { mutableStateOf(listOf<ShikakuRect>()) }
    var activeIndex by remember { mutableStateOf(-1) }
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            committed = emptyList()
            activeIndex = -1
            progress.snapTo(0f)
            delay(ResetPauseMillis)
            ShikakuSolution.forEachIndexed { index, _ ->
                activeIndex = index
                progress.snapTo(0f)
                progress.animateTo(1f, tween(GrowMillis))
                committed = ShikakuSolution.subList(0, index + 1)
                activeIndex = -1
                delay(CommitRestMillis)
            }
            delay(SolvedHoldMillis)
        }
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        PrismCard(face = ShikakuBoardFrame, facet = 6.dp) {
            Canvas(modifier = Modifier.size(ShikakuCellSize * ShikakuCols, ShikakuCellSize * ShikakuRows)) {
                val cellW = size.width / ShikakuCols
                val cellH = size.height / ShikakuRows

                // Build a fill rect from grid-edge coordinates (in cell units, where right/bottom are exclusive).
                fun edgeTopLeft(left: Float, top: Float) = Offset(left * cellW, top * cellH)
                fun edgeSize(left: Float, top: Float, right: Float, bottom: Float) = Size((right - left) * cellW, (bottom - top) * cellH)

                drawRect(color = cellColor)

                // Region fills first so grid lines and borders draw on top, matching the real board.
                committed.forEachIndexed { idx, rect ->
                    drawRect(
                        color = CatRegionColors[idx % CatRegionColors.size].copy(alpha = 0.65f),
                        topLeft = edgeTopLeft(rect.left.toFloat(), rect.top.toFloat()),
                        size = edgeSize(rect.left.toFloat(), rect.top.toFloat(), (rect.right + 1).toFloat(), (rect.bottom + 1).toFloat()),
                    )
                }

                for (c in 0..ShikakuCols) {
                    val x = c * cellW
                    drawLine(gridLineColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1.5.dp.toPx())
                }
                for (r in 0..ShikakuRows) {
                    val y = r * cellH
                    drawLine(gridLineColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.5.dp.toPx())
                }

                committed.forEach { rect ->
                    drawRect(
                        color = borderColor,
                        topLeft = edgeTopLeft(rect.left.toFloat(), rect.top.toFloat()),
                        size = edgeSize(rect.left.toFloat(), rect.top.toFloat(), (rect.right + 1).toFloat(), (rect.bottom + 1).toFloat()),
                        style = Stroke(width = 3.dp.toPx()),
                    )
                }

                // The growing rectangle expands from its clue cell outward to the full bounds.
                val ai = activeIndex
                if (ai in ShikakuSolution.indices) {
                    val rect = ShikakuSolution[ai]
                    val t = progress.value
                    val left = lerp(rect.clueCol.toFloat(), rect.left.toFloat(), t)
                    val top = lerp(rect.clueRow.toFloat(), rect.top.toFloat(), t)
                    val right = lerp((rect.clueCol + 1).toFloat(), (rect.right + 1).toFloat(), t)
                    val bottom = lerp((rect.clueRow + 1).toFloat(), (rect.bottom + 1).toFloat(), t)
                    drawRect(
                        color = previewColor.copy(alpha = 0.25f),
                        topLeft = edgeTopLeft(left, top),
                        size = edgeSize(left, top, right, bottom),
                    )
                    drawRect(
                        color = previewColor,
                        topLeft = edgeTopLeft(left, top),
                        size = edgeSize(left, top, right, bottom),
                        style = Stroke(width = 3.dp.toPx()),
                    )
                }

                val clueStyle = TextStyle(
                    color = clueColor,
                    fontSize = (cellH * 0.42f).toSp(),
                    fontFamily = numberFont,
                    fontWeight = FontWeight.Bold,
                )
                ShikakuSolution.forEach { rect ->
                    val measured = textMeasurer.measure(AnnotatedString(rect.area.toString()), style = clueStyle)
                    val centerX = rect.clueCol * cellW + cellW / 2f
                    val centerY = rect.clueRow * cellH + cellH / 2f
                    drawText(
                        measured,
                        topLeft = Offset(centerX - measured.size.width / 2f, centerY - measured.size.height / 2f),
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(Res.string.game_shikaku_desc),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}
