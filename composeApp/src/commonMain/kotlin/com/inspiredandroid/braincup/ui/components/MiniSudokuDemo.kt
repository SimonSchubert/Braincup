package com.inspiredandroid.braincup.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.game_mini_sudoku_desc
import braincup.composeapp.generated.resources.mini_sudoku_demo_box
import braincup.composeapp.generated.resources.mini_sudoku_demo_column
import braincup.composeapp.generated.resources.mini_sudoku_demo_complete
import braincup.composeapp.generated.resources.mini_sudoku_demo_mistake
import braincup.composeapp.generated.resources.mini_sudoku_demo_title
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import com.inspiredandroid.braincup.ui.theme.numberFontFamily
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

private const val DemoGridSize = 4 // the real game's first grid is 4x4 with 2x2 blocks
private const val DemoBlock = 2

// A complete, valid 4x4 solution; the demo only ever leaves one cell (cell 5) open.
private val Solution = listOf(
    1, 2, 3, 4,
    3, 4, 1, 2,
    2, 1, 4, 3,
    4, 3, 2, 1,
)

// The open cell sits where row 1 and the top-left box meet, so filling it completes both. The
// mistake puts a 1 right next to the 1 already in that row (a single clashing cell). Correct = 4.
private const val OpenCell = 5
private const val ConflictCell = 6
private const val WrongValue = 1

// The row, column and 2x2 box that the filled-in number belongs to (cell indices). The check walks
// each group in value order 1..4, not in position order.
private val AddedRow = listOf(4, 5, 6, 7)
private val AddedColumn = listOf(1, 5, 9, 13)
private val AddedBox = listOf(0, 1, 4, 5)

private const val FillPauseMillis = 500L
private const val MistakeHoldMillis = 2000L
private const val FixPauseMillis = 500L
private const val StepMillis = 260L
private const val GroupHoldMillis = 600L
private const val GroupGapMillis = 300L
private const val SolvedHoldMillis = 1200L
private const val ResetPauseMillis = 600L

private val OuterFrame = 4.dp
private val BlockSeparator = 4.dp
private val CellSeparator = 2.dp

// Every caption the demo cycles through, so the caption line can reserve the tallest one's height.
private val DemoCaptions = listOf(
    Res.string.game_mini_sudoku_desc,
    Res.string.mini_sudoku_demo_mistake,
    Res.string.mini_sudoku_demo_complete,
    Res.string.mini_sudoku_demo_column,
    Res.string.mini_sudoku_demo_box,
)

/**
 * Animated tutorial for Mini Sudoku on a 4x4 grid with a single open cell. The demo first tries a
 * duplicate (a 1 beside the 1 already in that row, with that one clashing cell highlighted), then
 * fills the right number. It validates only the groups the new number joins: it walks its row, its
 * column and its 2x2 box, counting 1..4 (lighting each value's cell in turn green). Mirrors
 * MiniSudokuGame's uniqueness rule. Loops on its own, like [LightsOutDemo].
 */
@Composable
fun MiniSudokuDemo(modifier: Modifier = Modifier) {
    var values by remember { mutableStateOf<List<Int?>>(emptyList()) }
    var mistakeCell by remember { mutableIntStateOf(-1) }
    var conflictCell by remember { mutableIntStateOf(-1) }
    var litCells by remember { mutableStateOf(emptySet<Int>()) }
    var captionRes by remember { mutableStateOf(Res.string.game_mini_sudoku_desc) }

    LaunchedEffect(Unit) {
        // Light a group's cells one at a time in value order (1, 2, 3, 4), then hold and clear.
        suspend fun checkGroup(cells: List<Int>) {
            for (cell in cells.sortedBy { Solution[it] }) {
                litCells = litCells + cell
                delay(StepMillis)
            }
            delay(GroupHoldMillis)
            litCells = emptySet()
            delay(GroupGapMillis)
        }

        while (true) {
            values = Solution.mapIndexed { index, v -> if (index == OpenCell) null else v }
            mistakeCell = -1
            conflictCell = -1
            litCells = emptySet()
            captionRes = Res.string.game_mini_sudoku_desc
            delay(ResetPauseMillis)

            // 1) Try a duplicate 1 in the open cell, beside the 1 already in its row.
            delay(FillPauseMillis)
            values = values.toMutableList().also { it[OpenCell] = WrongValue }
            mistakeCell = OpenCell
            conflictCell = ConflictCell
            captionRes = Res.string.mini_sudoku_demo_mistake
            delay(MistakeHoldMillis)

            // 2) Fix it with the right number.
            mistakeCell = -1
            conflictCell = -1
            values = values.toMutableList().also { it[OpenCell] = Solution[OpenCell] }
            captionRes = Res.string.game_mini_sudoku_desc
            delay(FixPauseMillis)

            // 3) Validate only the groups the new number joined (row, column, box), counting 1..4.
            captionRes = Res.string.mini_sudoku_demo_complete
            checkGroup(AddedRow)
            captionRes = Res.string.mini_sudoku_demo_column
            checkGroup(AddedColumn)
            captionRes = Res.string.mini_sudoku_demo_box
            checkGroup(AddedBox)

            delay(SolvedHoldMillis)
        }
    }

    val cell = if (LocalIsCompactHeight.current) 40.dp else 48.dp
    val gridLineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
    val surface = MaterialTheme.colorScheme.surface
    val litColor = lerp(surface, SuccessGreen, 0.45f)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.mini_sudoku_demo_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(16.dp))

        Box(Modifier.background(gridLineColor)) {
            Column(Modifier.padding(OuterFrame)) {
                for (row in 0 until DemoGridSize) {
                    Row {
                        for (col in 0 until DemoGridSize) {
                            val index = row * DemoGridSize + col
                            val targetFace = when {
                                index == mistakeCell -> MaterialTheme.colorScheme.error
                                index == conflictCell -> MaterialTheme.colorScheme.errorContainer
                                index in litCells -> litColor
                                else -> surface
                            }
                            // Animate so each number lights up smoothly as the check reaches it.
                            val face by animateColorAsState(targetFace, tween(220), label = "sudokuCell")
                            val valueColor = when {
                                index == mistakeCell -> MaterialTheme.colorScheme.onError
                                index == conflictCell -> MaterialTheme.colorScheme.onErrorContainer
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                            DemoSudokuCell(
                                value = values.getOrNull(index),
                                face = face,
                                valueColor = valueColor,
                                size = cell,
                                modifier = Modifier.padding(
                                    end = gapAfter(col),
                                    bottom = gapAfter(row),
                                ),
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        DemoCaption(current = captionRes, all = DemoCaptions)
    }
}

private fun gapAfter(index: Int): Dp = when {
    index == DemoGridSize - 1 -> 0.dp
    (index + 1) % DemoBlock == 0 -> BlockSeparator
    else -> CellSeparator
}

@Composable
private fun DemoSudokuCell(
    value: Int?,
    face: Color,
    valueColor: Color,
    size: Dp,
    modifier: Modifier = Modifier,
) {
    PrismTile(
        face = face,
        isClickable = false,
        modifier = modifier.size(size),
        onClick = {},
    ) {
        if (value != null) {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontFamily = numberFontFamily(),
                fontWeight = FontWeight.Bold,
                color = valueColor,
                textAlign = TextAlign.Center,
            )
        }
    }
}
