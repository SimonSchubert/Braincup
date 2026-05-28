package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.normalsudoku.NormalSudokuPuzzle
import com.inspiredandroid.braincup.normalsudoku.NormalSudokuPuzzles
import com.inspiredandroid.braincup.normalsudoku.SudokuDifficulty
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.ColorPrismCell
import com.inspiredandroid.braincup.ui.components.PrismTile
import com.inspiredandroid.braincup.ui.components.hoverHand
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun NormalSudokuMenuScreen(
    storage: UserStorage,
    onPuzzleSelected: (puzzleId: String) -> Unit,
    onBack: () -> Unit,
) {
    val puzzles = remember { NormalSudokuPuzzles.all }
    val completed = remember(puzzles) { storage.getCompletedNormalSudokuIds() }
    val grouped = remember(puzzles) { SudokuDifficulty.entries.map { it to puzzles.filter { p -> p.difficulty == it } } }

    AppScaffold(
        title = stringResource(Res.string.normal_sudoku_title),
        onBack = onBack,
        scrollable = false,
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 96.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            grouped.forEach { (difficulty, list) ->
                val tierCompleted = list.count { it.id in completed }
                item(span = { GridItemSpan(maxLineSpan) }, key = "header-${difficulty.name}") {
                    DifficultyHeader(
                        label = stringResource(difficulty.labelRes()),
                        completed = tierCompleted,
                        total = list.size,
                    )
                }
                items(list, key = { it.id }, span = { GridItemSpan(1) }) { puzzle ->
                    PuzzleTile(
                        puzzle = puzzle,
                        indexInTier = list.indexOf(puzzle) + 1,
                        isCompleted = puzzle.id in completed,
                        hasProgress = puzzle.id !in completed &&
                            storage.getNormalSudokuProgress(puzzle.id) != null,
                        onClick = { onPuzzleSelected(puzzle.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun DifficultyHeader(label: String, completed: Int, total: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Primary,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = stringResource(Res.string.normal_sudoku_section_progress, completed, total),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun PuzzleTile(
    puzzle: NormalSudokuPuzzle,
    indexInTier: Int,
    isCompleted: Boolean,
    hasProgress: Boolean,
    onClick: () -> Unit,
) {
    val face = when {
        isCompleted -> SuccessGreen
        hasProgress -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = when {
        isCompleted -> Color.White
        else -> MaterialTheme.colorScheme.onSurface
    }
    PrismTile(
        face = face,
        modifier = Modifier
            .hoverHand()
            .fillMaxWidth()
            .height(72.dp),
        onClick = onClick,
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = stringResource(Res.string.normal_sudoku_puzzle_label, indexInTier),
                style = MaterialTheme.typography.titleMedium,
                color = contentColor,
                fontWeight = FontWeight.SemiBold,
            )
            if (isCompleted) {
                ChunkyCheck(
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(14.dp),
                )
            } else if (hasProgress) {
                ColorPrismCell(
                    face = Primary,
                    facet = 2.dp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(9.dp),
                )
            }
        }
    }
}

/**
 * A bold, round-capped checkmark drawn by hand so its weight matches the chunky Bungee
 * typography and prism tiles, rather than the thin Material vector icon.
 */
@Composable
private fun ChunkyCheck(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val stroke = (minOf(w, h) * 0.22f)
        val elbow = Offset(w * 0.40f, h * 0.78f)
        drawLine(
            color = color,
            start = Offset(w * 0.08f, h * 0.50f),
            end = elbow,
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = elbow,
            end = Offset(w * 0.92f, h * 0.20f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
    }
}

private fun SudokuDifficulty.labelRes(): StringResource = when (this) {
    SudokuDifficulty.BEGINNER -> Res.string.normal_sudoku_difficulty_beginner
    SudokuDifficulty.EASY -> Res.string.normal_sudoku_difficulty_easy
    SudokuDifficulty.MEDIUM -> Res.string.normal_sudoku_difficulty_medium
    SudokuDifficulty.HARD -> Res.string.normal_sudoku_difficulty_hard
    SudokuDifficulty.EXPERT -> Res.string.normal_sudoku_difficulty_expert
}
