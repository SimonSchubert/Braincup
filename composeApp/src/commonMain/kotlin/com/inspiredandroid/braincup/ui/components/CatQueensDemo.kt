package com.inspiredandroid.braincup.ui.components

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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.cat_queens_error_column
import braincup.composeapp.generated.resources.cat_queens_error_touch
import braincup.composeapp.generated.resources.game_cat_queens_desc
import com.inspiredandroid.braincup.ui.icons.CatFace
import com.inspiredandroid.braincup.ui.screens.CatQueensBoardFrame
import com.inspiredandroid.braincup.ui.theme.CatRegionColors
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

private const val CatQueensDemoSize = 4

// idx = row * size + col. The same 4x4 board shown on the Cat Queens menu tile preview: four
// connected colour zones, each holding exactly one solution cat.
//
//   0 0 1 1
//   0 1 1 2
//   3 3 1 2
//   3 3 2 2
//
private val CatQueensDemoRegions = intArrayOf(
    0, 0, 1, 1,
    0, 1, 1, 2,
    3, 3, 1, 2,
    3, 3, 2, 2,
)
private val CatQueensDemoCellSize = 56.dp

// Solution: one cat per row, per column and per zone, with none touching.
// (r0,c2)(r1,c0)(r2,c3)(r3,c1) -> zones 1, 0, 2, 3.
private const val CatQueensCat0 = 2
private const val CatQueensCat1 = 4
private const val CatQueensCat2 = 11
private const val CatQueensCat3 = 13

// Cell 7 = (r1,c3) touches cat 2 = (r0,c2) diagonally. A diagonal neighbour can never share a row or
// column, and its zone differs too, so while cat 2 is the only cat on the board this is a pure
// "touching" conflict, distinct from the line mistake below.
private const val CatQueensTouchMistake = 7

// Cell 12 = (r3,c0) shares column 0 with cat 4 = (r1,c0) but is two rows away (so it does not touch)
// and sits in a different zone: a pure "occupied line" conflict that lights the whole column red.
private const val CatQueensColumnMistake = 12

private const val TapHighlightMillis = 420L
private const val PlaceRestMillis = 560L
private const val ConflictHoldMillis = 1100L
private const val CorrectionRestMillis = 320L
private const val SolvedHoldMillis = 1500L
private const val ResetPauseMillis = 500L

// The lit row/column guide: a valid placement lights the cat's row and column in the accent colour;
// a line violation lights just the shared line in red.
private data class CatQueensLine(val index: Int, val row: Boolean, val col: Boolean, val warn: Boolean)

/**
 * Animated tutorial board for Cat Queens: the fixed 4x4 menu-preview puzzle that solves itself on a
 * loop. Each placed cat lights up its whole row and column, making the "one cat per row and column"
 * rule obvious. Two deliberate mistakes show the rules being enforced: a cat dropped onto an occupied
 * column lights that column red ("one cat per column"), and a cat placed next to another flashes red
 * ("cats cannot touch"). Each is undone, the board completes, holds, then resets and repeats.
 */
@Composable
fun CatQueensDemo(modifier: Modifier = Modifier) {
    val n = CatQueensDemoSize
    val gridLineColor = Color(0xFF000000).copy(alpha = 0.15f)
    val borderColor = Color(0xFF1A1A1A)
    val invalidColor = MaterialTheme.colorScheme.error
    val validColor = SuccessGreen
    val accentColor = Primary
    val catPainter = rememberVectorPainter(CatFace)

    // cats = placed; invalid = cats flashing red during a conflict; active = cell being tapped;
    // line = the row/column guide currently lit; errorRes = the rule message shown under the board.
    var cats by remember { mutableStateOf(emptySet<Int>()) }
    var invalid by remember { mutableStateOf(emptySet<Int>()) }
    var active by remember { mutableIntStateOf(-1) }
    var line by remember { mutableStateOf<CatQueensLine?>(null) }
    var errorRes by remember { mutableStateOf<StringResource?>(null) }

    LaunchedEffect(Unit) {
        suspend fun tapPlace(index: Int) {
            line = null
            active = index
            delay(TapHighlightMillis)
            cats = cats + index
            active = -1
            line = CatQueensLine(index, row = true, col = true, warn = false)
            delay(PlaceRestMillis)
        }
        suspend fun mistake(cell: Int, clashesWith: Int, error: StringResource, hint: CatQueensLine?) {
            line = null
            active = cell
            delay(TapHighlightMillis)
            cats = cats + cell
            active = -1
            invalid = setOf(clashesWith, cell)
            line = hint
            errorRes = error
            delay(ConflictHoldMillis)
            cats = cats - cell
            invalid = emptySet()
            line = null
            errorRes = null
            delay(CorrectionRestMillis)
        }
        while (true) {
            cats = emptySet()
            invalid = emptySet()
            active = -1
            line = null
            errorRes = null
            delay(ResetPauseMillis)

            tapPlace(CatQueensCat0)

            // Mistake 1: placed next to the lone cat, it only touches it (no shared row/column/zone),
            // so no line guide lights up.
            mistake(
                cell = CatQueensTouchMistake,
                clashesWith = CatQueensCat0,
                error = Res.string.cat_queens_error_touch,
                hint = null,
            )

            tapPlace(CatQueensCat1)

            // Mistake 2: dropped onto the column the cat above just claimed, lighting it red.
            mistake(
                cell = CatQueensColumnMistake,
                clashesWith = CatQueensCat1,
                error = Res.string.cat_queens_error_column,
                hint = CatQueensLine(CatQueensColumnMistake, row = false, col = true, warn = true),
            )

            tapPlace(CatQueensCat2)
            tapPlace(CatQueensCat3)
            line = null

            delay(SolvedHoldMillis)
        }
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        PrismCard(face = CatQueensBoardFrame, facet = 6.dp) {
            Canvas(modifier = Modifier.size(CatQueensDemoCellSize * n, CatQueensDemoCellSize * n)) {
                val cellW = size.width / n
                val cellH = size.height / n
                val cellSize = Size(cellW, cellH)
                fun topLeft(index: Int) = Offset((index % n) * cellW, (index / n) * cellH)
                val ln = line

                for (index in 0 until n * n) {
                    val color = CatRegionColors[CatQueensDemoRegions[index] % CatRegionColors.size]
                    drawRect(color = color, topLeft = topLeft(index), size = cellSize)
                }

                // Line indicator (band): tint the whole lit row and/or column.
                if (ln != null) {
                    val tint = (if (ln.warn) invalidColor else accentColor).copy(alpha = 0.30f)
                    val r = ln.index / n
                    val c = ln.index % n
                    if (ln.row) drawRect(color = tint, topLeft = Offset(0f, r * cellH), size = Size(size.width, cellH))
                    if (ln.col) drawRect(color = tint, topLeft = Offset(c * cellW, 0f), size = Size(cellW, size.height))
                }

                // Thin lines between every cell, then bold lines along zone boundaries so the zones
                // read clearly even when two neighbouring hues are hard to tell apart.
                for (i in 0..n) {
                    val x = i * cellW
                    drawLine(gridLineColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1.dp.toPx())
                    val y = i * cellH
                    drawLine(gridLineColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.dp.toPx())
                }

                val bold = 3.dp.toPx()
                for (r in 0 until n) {
                    for (c in 0 until n) {
                        val index = r * n + c
                        val region = CatQueensDemoRegions[index]
                        val x0 = c * cellW
                        val y0 = r * cellH
                        val x1 = x0 + cellW
                        val y1 = y0 + cellH
                        if (r == 0 || CatQueensDemoRegions[index - n] != region) {
                            drawLine(borderColor, Offset(x0, y0), Offset(x1, y0), strokeWidth = bold)
                        }
                        if (r == n - 1 || CatQueensDemoRegions[index + n] != region) {
                            drawLine(borderColor, Offset(x0, y1), Offset(x1, y1), strokeWidth = bold)
                        }
                        if (c == 0 || CatQueensDemoRegions[index - 1] != region) {
                            drawLine(borderColor, Offset(x0, y0), Offset(x0, y1), strokeWidth = bold)
                        }
                        if (c == n - 1 || CatQueensDemoRegions[index + 1] != region) {
                            drawLine(borderColor, Offset(x1, y0), Offset(x1, y1), strokeWidth = bold)
                        }
                    }
                }

                // Line indicator (outline): a bold border around the lit strip makes the line pop above
                // the grid, drawn after the cell borders so it stays visible.
                if (ln != null) {
                    val edge = (if (ln.warn) invalidColor else accentColor)
                    val lineStroke = 3.dp.toPx()
                    val r = ln.index / n
                    val c = ln.index % n
                    if (ln.row) {
                        drawRect(
                            color = edge,
                            topLeft = Offset(0f, r * cellH),
                            size = Size(size.width, cellH),
                            style = Stroke(width = lineStroke),
                        )
                    }
                    if (ln.col) {
                        drawRect(
                            color = edge,
                            topLeft = Offset(c * cellW, 0f),
                            size = Size(cellW, size.height),
                            style = Stroke(width = lineStroke),
                        )
                    }
                }

                // The tap target lights up just before its cat pops in.
                val inset = cellW * 0.07f
                val corner = CornerRadius(cellW * 0.2f)
                if (active in 0 until n * n) {
                    val tl = topLeft(active)
                    drawRoundRect(
                        color = accentColor,
                        topLeft = Offset(tl.x + inset, tl.y + inset),
                        size = Size(cellW - 2 * inset, cellH - 2 * inset),
                        cornerRadius = corner,
                        style = Stroke(width = 3.dp.toPx()),
                    )
                }

                // Each cat shows a green ring while it breaks no rule, a red ring and tint when it does.
                val pad = cellW * 0.12f
                val catSize = Size(cellW - 2 * pad, cellH - 2 * pad)
                cats.forEach { index ->
                    val tl = topLeft(index)
                    val isInvalid = index in invalid
                    if (isInvalid) {
                        drawRect(color = invalidColor.copy(alpha = 0.18f), topLeft = tl, size = cellSize)
                    }
                    translate(left = tl.x + pad, top = tl.y + pad) {
                        with(catPainter) { draw(catSize) }
                    }
                    drawRoundRect(
                        color = if (isInvalid) invalidColor else validColor,
                        topLeft = Offset(tl.x + inset, tl.y + inset),
                        size = Size(cellW - 2 * inset, cellH - 2 * inset),
                        cornerRadius = corner,
                        style = Stroke(width = 3.dp.toPx()),
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(errorRes ?: Res.string.game_cat_queens_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = if (errorRes != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (errorRes != null) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}
