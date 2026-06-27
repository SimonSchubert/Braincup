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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.normalsudoku.NormalSudokuPuzzles
import com.inspiredandroid.braincup.normalsudoku.SudokuDifficulty
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.ColorPrismCell
import com.inspiredandroid.braincup.ui.components.PrismTile
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
                // Puzzles in a tier must be played in order: only completed puzzles and the next
                // unsolved one are unlocked; everything past it stays locked, so you can't skip
                // ahead. This also keeps the completed set a contiguous prefix, which is exactly
                // what the incremental tier achievement counts and restores.
                val firstUnsolved = list.indexOfFirst { it.id !in completed }
                item(span = { GridItemSpan(maxLineSpan) }, key = "header-${difficulty.name}") {
                    DifficultyHeader(
                        label = stringResource(difficulty.labelRes()),
                        completed = tierCompleted,
                        total = list.size,
                    )
                }
                items(list, key = { it.id }, span = { GridItemSpan(1) }) { puzzle ->
                    val index = list.indexOf(puzzle)
                    val isCompleted = puzzle.id in completed
                    // Already-solved puzzles stay replayable; only not-yet-solved ones past the
                    // next playable puzzle are locked (handles legacy non-prefix completion too).
                    val isLocked = !isCompleted && firstUnsolved != -1 && index > firstUnsolved
                    PuzzleTile(
                        indexInTier = index + 1,
                        isCompleted = isCompleted,
                        isLocked = isLocked,
                        hasProgress = !isCompleted && !isLocked &&
                            storage.getNormalSudokuProgress(puzzle.id) != null,
                        onClick = { if (!isLocked) onPuzzleSelected(puzzle.id) },
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
    indexInTier: Int,
    isCompleted: Boolean,
    isLocked: Boolean,
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
        isClickable = !isLocked,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .alpha(if (isLocked) 0.5f else 1f),
        onClick = onClick,
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            if (isLocked) {
                ChunkyLock(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp),
                )
            } else {
                Text(
                    text = stringResource(Res.string.normal_sudoku_puzzle_label, indexInTier),
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor,
                    fontWeight = FontWeight.SemiBold,
                )
            }
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

/**
 * A hand-drawn padlock that matches the chunky check above, marking a puzzle still locked
 * behind an earlier one in its tier.
 */
@Composable
private fun ChunkyLock(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val stroke = minOf(w, h) * 0.15f
        val shackleW = w * 0.42f
        val shackleLeft = (w - shackleW) / 2f
        // Shackle: the top semicircle; its endpoints tuck behind the body.
        drawArc(
            color = color,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(shackleLeft, h * 0.10f),
            size = Size(shackleW, h * 0.70f),
            style = Stroke(width = stroke, cap = StrokeCap.Round),
        )
        // Body.
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.20f, h * 0.45f),
            size = Size(w * 0.60f, h * 0.45f),
            cornerRadius = CornerRadius(w * 0.12f, w * 0.12f),
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
