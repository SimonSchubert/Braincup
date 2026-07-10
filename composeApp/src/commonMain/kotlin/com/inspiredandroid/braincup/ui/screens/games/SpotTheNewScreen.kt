package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.games.SpotTheNewGame
import com.inspiredandroid.braincup.games.tools.Animal
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.theme.SpotTheNewColors
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.painterResource
import kotlin.math.ceil
import kotlin.math.sqrt

private const val SpotTheNewFadeMillis = 420

@Composable
internal fun ColumnScope.SpotTheNewContent(
    uiState: SpotTheNewUiState,
    onAnswer: (String) -> Unit,
) {
    // Each round fades out, then the next round fades in. Keying on the round number means the
    // in-place game-over reveal (same round, only tile colors change) is not faded.
    AnimatedContent(
        targetState = uiState,
        contentKey = { it.round },
        transitionSpec = {
            fadeIn(animationSpec = tween(SpotTheNewFadeMillis, delayMillis = SpotTheNewFadeMillis)) togetherWith
                fadeOut(animationSpec = tween(SpotTheNewFadeMillis))
        },
        modifier = Modifier.align(Alignment.CenterHorizontally),
        label = "spotTheNewRound",
    ) { state ->
        // Only the fully settled, visible round accepts taps, so the outgoing grid can't be
        // tapped against the already-advanced game state during the fade.
        val settled = transition.currentState == EnterExitState.Visible &&
            transition.targetState == EnterExitState.Visible
        SpotTheNewGrid(
            uiState = state,
            interactive = settled,
            onAnswer = onAnswer,
        )
    }
}

@Composable
private fun SpotTheNewGrid(
    uiState: SpotTheNewUiState,
    interactive: Boolean,
    onAnswer: (String) -> Unit,
) {
    val count = uiState.displayedCount.coerceAtLeast(1)
    val columns = ceil(sqrt(count.toDouble())).toInt().coerceAtLeast(1)
    val clickable = interactive && uiState.phase == SpotTheNewGame.Phase.ANSWERING
    val maxTileWidth = if (LocalIsCompactHeight.current) 56.dp else 72.dp

    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .widthIn(max = maxTileWidth * columns),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        uiState.cells.chunked(columns).forEach { rowCells ->
            Row {
                rowCells.forEach { cell ->
                    SpotTheNewTile(
                        cell = cell,
                        clickable = clickable,
                        onAnswer = onAnswer,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(4.dp),
                    )
                }
                // Pad the final short row so its tiles keep the same square size.
                repeat(columns - rowCells.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun SpotTheNewTile(
    cell: SpotTheNewUiState.CellState,
    clickable: Boolean,
    onAnswer: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val face = when (cell.type) {
        // Strong red (not the pale errorContainer) so the tile the player got wrong is unmistakable.
        SpotTheNewGame.CellType.WRONG -> SpotTheNewColors.wrongFace()
        SpotTheNewGame.CellType.CORRECT -> SpotTheNewColors.highlightFace()
        SpotTheNewGame.CellType.NORMAL -> SpotTheNewColors.normalFace()
    }
    PrismTile(
        face = face,
        modifier = modifier,
        isClickable = clickable,
        onClick = { onAnswer(cell.index.toString()) },
    ) {
        Image(
            painter = painterResource(cell.animal.resource),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
        )
    }
}

@GameDevicePreviews
@Composable
private fun SpotTheNewContentPreview() {
    GamePreviewHost {
        SpotTheNewContent(
            uiState = SpotTheNewUiState(
                round = 1,
                phase = SpotTheNewGame.Phase.ANSWERING,
                displayedCount = 3,
                cells = persistentListOf(
                    SpotTheNewUiState.CellState(Animal.FISH, 0, SpotTheNewGame.CellType.NORMAL),
                    SpotTheNewUiState.CellState(Animal.CRAB, 1, SpotTheNewGame.CellType.NORMAL),
                    SpotTheNewUiState.CellState(Animal.TURTLE, 2, SpotTheNewGame.CellType.NORMAL),
                ),
            ),
            onAnswer = {},
        )
    }
}
