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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.game_nurikabe_desc
import com.inspiredandroid.braincup.ui.theme.NurikabeBoardFrame
import com.inspiredandroid.braincup.ui.theme.NurikabeIslandColor
import com.inspiredandroid.braincup.ui.theme.NurikabeSeaColor
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.PrismFacet
import com.inspiredandroid.braincup.ui.theme.PuzzleGridInk
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import com.inspiredandroid.braincup.ui.theme.SuccessGreenSoft
import com.inspiredandroid.braincup.ui.theme.numberFontFamily
import com.inspiredandroid.braincup.ui.theme.puzzleGridLine
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

private const val NurikabeCols = 4
private const val NurikabeRows = 4
private val NurikabeCellSize = 56.dp

// One cell of the current stroke is painted every PaintCellMillis, then the stroke commits and rests.
private const val PaintCellMillis = 110L
private const val StrokeCommitRest = 460L
private const val SolvedHoldMillis = 1500L
private const val ResetPauseMillis = 500L

// idx = row * cols + col. A fixed 4x4 puzzle with varied island shapes (matches the Shikaku demo size):
//
//   3 . ~ 2      3 = L-tromino  (0,0)(0,1)(1,0)
//   . ~ ~ .      2 = domino     (0,3)(1,3)
//   ~ ~ 1 ~      1 = single     (2,2)
//   1 ~ ~ ~      1 = single     (3,0)
//                ~ = the winding, single-connected sea (9 cells, no 2x2 pool)
//
private val NurikabeClues = mapOf(0 to 3, 3 to 2, 10 to 1, 12 to 1)

// Island cells, grouped, so a stroke can reveal a whole island at once.
private val IslandCorner = setOf(0, 1, 4) // the size-3 L
private val IslandTopRight = setOf(3, 7) // size-2 domino
private val IslandCenter = setOf(10) // size-1
private val IslandBottomLeft = setOf(12) // size-1

private data class NurikabeStroke(
    val paint: List<Int>,
    val satisfied: Set<Int>,
)

// Three strokes that solve the puzzle as one continuous sea snake. After each stroke, every island
// whose surrounding sea is now complete turns green. The final sea is one connected region with no 2x2 pool.
private val NurikabeStrokes = listOf(
    // Snake down from the top-centre, hugging the L: encloses the size-3 L.
    NurikabeStroke(paint = listOf(2, 6, 5, 9, 8), satisfied = IslandCorner),
    // Reach across the bottom: seals off the lone 1.
    NurikabeStroke(paint = listOf(13, 14), satisfied = IslandCorner + IslandBottomLeft),
    // Close the right edge: the domino and the centre 1 complete the board.
    NurikabeStroke(
        paint = listOf(15, 11),
        satisfied = IslandCorner + IslandBottomLeft + IslandTopRight + IslandCenter,
    ),
)

/**
 * Animated tutorial board for Nurikabe: a fixed 5x5 puzzle that solves itself on a loop. Sea cells are
 * painted stroke by stroke (orange-outlined preview, then settled to the sea colour); as each stroke
 * seals off an island, that island flashes green, mirroring the real board. Then it holds and repeats.
 */
@Composable
fun NurikabeDemo(modifier: Modifier = Modifier) {
    val islandColor = NurikabeIslandColor
    val seaColor = NurikabeSeaColor
    val gridLineColor = puzzleGridLine(alpha = 0.4f)
    val clueColor = PuzzleGridInk
    val satisfiedFill = SuccessGreenSoft
    val satisfiedColor = SuccessGreen
    val previewColor = Primary
    val numberFont = numberFontFamily()
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    // Clue glyphs are static (fixed values on a fixed-size board), so measure each distinct value
    // once instead of re-measuring on every animation frame inside the Canvas. The colour is applied
    // at draw time so a clue can still flip to green when its island is satisfied.
    val clueLayouts = remember(textMeasurer, numberFont, density) {
        val cellPx = with(density) { NurikabeCellSize.toPx() }
        val clueStyle = TextStyle(
            fontSize = with(density) { (cellPx * 0.42f).toSp() },
            fontFamily = numberFont,
            fontWeight = FontWeight.Bold,
        )
        NurikabeClues.values.distinct().associateWith { value ->
            textMeasurer.measure(AnnotatedString(value.toString()), style = clueStyle)
        }
    }

    // walls = committed sea; painting = cells of the current stroke being previewed; satisfied = green islands.
    var walls by remember { mutableStateOf(emptySet<Int>()) }
    var painting by remember { mutableStateOf(emptySet<Int>()) }
    var satisfied by remember { mutableStateOf(emptySet<Int>()) }

    LaunchedEffect(Unit) {
        while (true) {
            walls = emptySet()
            painting = emptySet()
            satisfied = emptySet()
            delay(ResetPauseMillis)
            for (stroke in NurikabeStrokes) {
                var p = emptySet<Int>()
                for (cell in stroke.paint) {
                    p = p + cell
                    painting = p
                    delay(PaintCellMillis)
                }
                walls = walls + stroke.paint
                painting = emptySet()
                satisfied = stroke.satisfied
                delay(StrokeCommitRest)
            }
            delay(SolvedHoldMillis)
        }
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        PrismCard(face = NurikabeBoardFrame, facet = PrismFacet.Board) {
            Canvas(modifier = Modifier.size(NurikabeCellSize * NurikabeCols, NurikabeCellSize * NurikabeRows)) {
                val cellW = size.width / NurikabeCols
                val cellH = size.height / NurikabeRows

                fun cellTopLeft(index: Int) = Offset((index % NurikabeCols) * cellW, (index / NurikabeCols) * cellH)
                val cellSize = Size(cellW, cellH)

                drawRect(color = islandColor)

                satisfied.forEach { index ->
                    drawRect(color = satisfiedFill, topLeft = cellTopLeft(index), size = cellSize)
                }

                walls.forEach { index ->
                    drawRect(color = seaColor, topLeft = cellTopLeft(index), size = cellSize)
                }

                // Live stroke preview: each painted cell turns sea-coloured with an orange outline.
                painting.forEach { index ->
                    drawRect(color = seaColor.copy(alpha = 0.55f), topLeft = cellTopLeft(index), size = cellSize)
                    drawRect(
                        color = previewColor,
                        topLeft = cellTopLeft(index),
                        size = cellSize,
                        style = Stroke(width = 2.dp.toPx()),
                    )
                }

                for (c in 0..NurikabeCols) {
                    val x = c * cellW
                    drawLine(gridLineColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1.5.dp.toPx())
                }
                for (r in 0..NurikabeRows) {
                    val y = r * cellH
                    drawLine(gridLineColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.5.dp.toPx())
                }

                NurikabeClues.forEach { (index, value) ->
                    val color = if (index in satisfied) satisfiedColor else clueColor
                    val measured = clueLayouts.getValue(value)
                    val centerX = (index % NurikabeCols) * cellW + cellW / 2f
                    val centerY = (index / NurikabeCols) * cellH + cellH / 2f
                    drawText(
                        measured,
                        color = color,
                        topLeft = Offset(centerX - measured.size.width / 2f, centerY - measured.size.height / 2f),
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(Res.string.game_nurikabe_desc),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}
