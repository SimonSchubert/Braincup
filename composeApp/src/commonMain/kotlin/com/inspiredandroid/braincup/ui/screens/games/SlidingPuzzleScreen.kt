package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.theme.numberFontFamily
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun ColumnScope.SlidingPuzzleContent(
    uiState: SlidingPuzzleUiState,
    onAnswer: (String) -> Unit,
    onGiveUp: () -> Unit,
) {
    val compact = LocalIsCompactHeight.current

    val board: @Composable () -> Unit = {
        SquareTileBoard(uiState.gridSize, compact) { index, cellSize ->
            val tile = uiState.tiles[index]
            SlidingPuzzleCell(
                label = tile,
                size = cellSize,
                onClick = { if (tile != 0) onAnswer(index.toString()) },
            )
        }
    }

    LevelPuzzleLayout(
        level = uiState.level,
        headerGap = 4.dp,
        board = board,
        actions = { GiveUpButton(onGiveUp = onGiveUp) },
    ) {
        MovesLabel(uiState.moves, Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
private fun SlidingPuzzleCell(
    label: Int,
    size: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit,
) {
    val isEmpty = label == 0
    val containerColor = if (isEmpty) {
        MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.4f)
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }
    PrismTile(
        face = containerColor,
        isClickable = !isEmpty,
        modifier = Modifier
            .size(size)
            .hoverHand(!isEmpty),
        onClick = onClick,
    ) {
        if (!isEmpty) {
            Text(
                text = label.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontFamily = numberFontFamily(),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@DevicePreviews
@Composable
private fun SlidingPuzzleContentPreview() {
    GamePreviewHost {
        SlidingPuzzleContent(
            uiState = SlidingPuzzleUiState(
                gridSize = 3,
                tiles = persistentListOf(1, 2, 3, 4, 0, 5, 7, 8, 6),
                moves = 0,
                level = 1,
            ),
            onAnswer = {},
            onGiveUp = {},
        )
    }
}
