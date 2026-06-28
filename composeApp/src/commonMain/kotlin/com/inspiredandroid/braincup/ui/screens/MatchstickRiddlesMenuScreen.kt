package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.matchstick_riddles_title
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.matchstickriddles.MatchstickRiddle
import com.inspiredandroid.braincup.matchstickriddles.MatchstickRiddles
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.ChunkyCheck
import com.inspiredandroid.braincup.ui.components.ChunkyLock
import com.inspiredandroid.braincup.ui.components.MatchstickBoardPreview
import com.inspiredandroid.braincup.ui.components.PrismTile
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import org.jetbrains.compose.resources.stringResource

private val TileHeight = 72.dp

@Composable
fun MatchstickRiddlesMenuScreen(
    storage: UserStorage,
    onRiddleSelected: (riddleId: String) -> Unit,
    onBack: () -> Unit,
) {
    val solved = remember(MatchstickRiddles.all) { storage.getSolvedMatchstickRiddleIds() }
    MatchstickRiddlesMenuScreenContent(
        solved = solved,
        onRiddleSelected = onRiddleSelected,
        onBack = onBack,
    )
}

@Composable
fun MatchstickRiddlesMenuScreenContent(
    solved: Set<String>,
    onRiddleSelected: (riddleId: String) -> Unit,
    onBack: () -> Unit,
) {
    val riddles = remember { MatchstickRiddles.all }
    // Riddles must be played in catalog order: only completed ones and the next unsolved one are
    // unlocked; the one after that is shown locked as a teaser; everything past that stays hidden.
    val firstUnsolved = remember(riddles, solved) { riddles.indexOfFirst { it.id !in solved } }

    AppScaffold(
        title = stringResource(Res.string.matchstick_riddles_title),
        onBack = onBack,
        scrollable = false,
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 280.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(riddles, key = { it.id }) { riddle ->
                val index = riddles.indexOf(riddle)
                val isSolved = riddle.id in solved
                val isLocked = !isSolved && firstUnsolved != -1 && index > firstUnsolved
                RiddleTile(
                    riddle = riddle,
                    isSolved = isSolved,
                    isLocked = isLocked,
                    onClick = { if (!isLocked) onRiddleSelected(riddle.id) },
                )
            }
        }
    }
}

@Composable
private fun RiddleTile(
    riddle: MatchstickRiddle,
    isSolved: Boolean,
    isLocked: Boolean,
    onClick: () -> Unit,
) {
    PrismTile(
        face = MaterialTheme.colorScheme.surfaceVariant,
        isClickable = !isLocked,
        modifier = Modifier
            .fillMaxWidth()
            .height(TileHeight)
            .alpha(if (isLocked) 0.5f else 1f),
        onClick = onClick,
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (isLocked) {
                ChunkyLock(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp),
                )
            } else {
                MatchstickBoardPreview(
                    riddle = riddle,
                    solved = isSolved,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            if (isSolved) {
                ChunkyCheck(
                    color = SuccessGreen,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(14.dp),
                )
            }
        }
    }
}
