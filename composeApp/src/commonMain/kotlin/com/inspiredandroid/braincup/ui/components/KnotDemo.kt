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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.game_knot_desc
import com.inspiredandroid.braincup.ui.theme.CatRegionColors
import com.inspiredandroid.braincup.ui.theme.KnotBoardFrame
import com.inspiredandroid.braincup.ui.theme.KnotCellColor
import com.inspiredandroid.braincup.ui.theme.PrismFacet
import com.inspiredandroid.braincup.ui.theme.puzzleGridLine
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

private const val KnotCols = 4
private const val KnotRows = 4
private val KnotCellSize = 56.dp

// One cell of the current path is revealed every DrawCellMillis, then the path commits and rests.
private const val DrawCellMillis = 120L
private const val PathCommitRest = 420L
private const val SolvedHoldMillis = 1500L
private const val ResetPauseMillis = 500L

private data class KnotDemoPath(val color: Int, val cells: List<Int>)

// A fixed 4x4 puzzle (same as the tile preview): three colored paths that together cover every cell.
private val KnotDemoPaths = listOf(
    KnotDemoPath(color = 0, cells = listOf(0, 4, 8, 12, 13, 14, 15, 11)),
    KnotDemoPath(color = 1, cells = listOf(1, 5, 9, 10)),
    KnotDemoPath(color = 2, cells = listOf(2, 3, 7, 6)),
)

/**
 * Animated tutorial board for Knot: a fixed 4x4 puzzle that solves itself on a loop. The colored
 * endpoint dots are shown from the start; one path at a time is drawn cell by cell from its dot to
 * its partner, then settles, mirroring the real board. Once every cell is covered it holds and repeats.
 */
@Composable
fun KnotDemo(modifier: Modifier = Modifier) {
    val cellColor = KnotCellColor
    val gridLineColor = puzzleGridLine()

    // committed = paths already fully drawn; activeColor/drawing = the path currently being drawn.
    var committed by remember { mutableStateOf(listOf<KnotDemoPath>()) }
    var activeColor by remember { mutableStateOf(-1) }
    var drawing by remember { mutableStateOf(listOf<Int>()) }

    LaunchedEffect(Unit) {
        while (true) {
            committed = emptyList()
            activeColor = -1
            drawing = emptyList()
            delay(ResetPauseMillis)
            for (path in KnotDemoPaths) {
                activeColor = path.color
                var partial = listOf<Int>()
                for (cell in path.cells) {
                    partial = partial + cell
                    drawing = partial
                    delay(DrawCellMillis)
                }
                committed = committed + path
                activeColor = -1
                drawing = emptyList()
                delay(PathCommitRest)
            }
            delay(SolvedHoldMillis)
        }
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        PrismCard(face = KnotBoardFrame, facet = PrismFacet.Board) {
            Canvas(modifier = Modifier.size(KnotCellSize * KnotCols, KnotCellSize * KnotRows)) {
                val cellW = size.width / KnotCols
                val cellH = size.height / KnotRows
                fun center(cell: Int) = Offset((cell % KnotCols + 0.5f) * cellW, (cell / KnotCols + 0.5f) * cellH)

                drawRect(color = cellColor)

                for (i in 0..KnotCols) {
                    drawLine(gridLineColor, Offset(i * cellW, 0f), Offset(i * cellW, size.height), strokeWidth = 1.5.dp.toPx())
                }
                for (i in 0..KnotRows) {
                    drawLine(gridLineColor, Offset(0f, i * cellH), Offset(size.width, i * cellH), strokeWidth = 1.5.dp.toPx())
                }

                val stroke = minOf(cellW, cellH) * 0.34f
                val dotRadius = minOf(cellW, cellH) * 0.30f

                committed.forEach { path ->
                    val color = CatRegionColors[path.color % CatRegionColors.size]
                    for (i in 1 until path.cells.size) {
                        drawLine(color, center(path.cells[i - 1]), center(path.cells[i]), strokeWidth = stroke, cap = StrokeCap.Round)
                    }
                }

                if (activeColor >= 0 && drawing.size >= 2) {
                    val color = CatRegionColors[activeColor % CatRegionColors.size]
                    for (i in 1 until drawing.size) {
                        drawLine(color, center(drawing[i - 1]), center(drawing[i]), strokeWidth = stroke, cap = StrokeCap.Round)
                    }
                }

                // Endpoint dots are always visible so the player sees the pairs before any path is drawn.
                KnotDemoPaths.forEach { path ->
                    val color = CatRegionColors[path.color % CatRegionColors.size]
                    drawCircle(color, radius = dotRadius, center = center(path.cells.first()))
                    drawCircle(color, radius = dotRadius, center = center(path.cells.last()))
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(Res.string.game_knot_desc),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}
