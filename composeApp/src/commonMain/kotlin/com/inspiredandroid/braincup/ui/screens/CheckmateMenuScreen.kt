package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.checkmate_mate_in
import braincup.composeapp.generated.resources.checkmate_stats
import braincup.composeapp.generated.resources.checkmate_title
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.checkmate.CheckmatePuzzle
import com.inspiredandroid.braincup.checkmate.CheckmatePuzzles
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.ChunkyCheck
import com.inspiredandroid.braincup.ui.components.PrismTile
import com.inspiredandroid.braincup.ui.components.hoverHand
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.ScreenPreviewHost
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun CheckmateMenuScreen(
    storage: UserStorage,
    onPuzzleSelected: (puzzleId: String) -> Unit,
    onBack: () -> Unit,
) {
    val puzzles by produceState(CheckmatePuzzles.cached?.toImmutableList()) {
        value = CheckmatePuzzles.load().toImmutableList()
    }
    val solved = remember { storage.getSolvedCheckmateIds().toImmutableSet() }
    val streak = remember { storage.getCheckmateStreak() }
    val best = remember { storage.getCheckmateBestStreak() }
    CheckmateMenuScreenContent(
        puzzles = puzzles,
        solved = solved,
        streak = streak,
        best = best,
        onPuzzleSelected = onPuzzleSelected,
        onBack = onBack,
    )
}

@Composable
fun CheckmateMenuScreenContent(
    puzzles: ImmutableList<CheckmatePuzzle>?,
    solved: ImmutableSet<String>,
    streak: Int,
    best: Int,
    onPuzzleSelected: (puzzleId: String) -> Unit,
    onBack: () -> Unit,
) {
    AppScaffold(
        title = stringResource(Res.string.checkmate_title),
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
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = stringResource(Res.string.checkmate_stats, solved.size, streak, best),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                )
            }
            items(puzzles.orEmpty(), key = { it.id }) { puzzle ->
                PuzzleTile(
                    puzzle = puzzle,
                    isSolved = puzzle.id in solved,
                    onClick = { onPuzzleSelected(puzzle.id) },
                )
            }
        }
    }
}

@Composable
private fun PuzzleTile(
    puzzle: CheckmatePuzzle,
    isSolved: Boolean,
    onClick: () -> Unit,
) {
    val contentColor = if (isSolved) Color.White else MaterialTheme.colorScheme.onSurface
    PrismTile(
        face = if (isSolved) SuccessGreen else MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .hoverHand(),
        onClick = onClick,
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = puzzle.number.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor,
                )
                Text(
                    text = pluralStringResource(Res.plurals.checkmate_mate_in, puzzle.mateIn, puzzle.mateIn),
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.8f),
                )
            }
            if (isSolved) {
                ChunkyCheck(
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(14.dp),
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun CheckmateMenuScreenPreview() {
    ScreenPreviewHost {
        CheckmateMenuScreenContent(
            puzzles = CheckmatePuzzle.parse(
                "UMLhc|6k1/1r3ppp/p1q5/3p4/P1n5/6QP/5PP1/1R4K1|w|3|uB JB 5B SC BC|-1|1308|F>JB\n" +
                    "NSBCR|8/1Q3pk1/2p5/4n3/3R3N/6BP/r3q1PK/8|b|2|ct nt 02|-1|1308|F>jD",
            ).toImmutableList(),
            solved = persistentSetOf("UMLhc"),
            streak = 1,
            best = 1,
            onPuzzleSelected = {},
            onBack = {},
        )
    }
}
