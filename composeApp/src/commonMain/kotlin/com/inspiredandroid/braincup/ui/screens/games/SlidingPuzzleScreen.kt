package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
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
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ColumnScope.SlidingPuzzleContent(
    uiState: SlidingPuzzleUiState,
    onAnswer: (String) -> Unit,
    onGiveUp: () -> Unit,
) {
    val n = uiState.gridSize
    val compact = LocalIsCompactHeight.current
    val cellSize = squareTileSize(n, compact)

    val board: @Composable () -> Unit = {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            for (row in 0 until n) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (col in 0 until n) {
                        val index = row * n + col
                        val tile = uiState.tiles[index]
                        SlidingPuzzleCell(
                            label = tile,
                            size = cellSize,
                            onClick = { if (tile != 0) onAnswer(index.toString()) },
                        )
                    }
                }
            }
        }
    }

    if (compact) {
        CompactGameRow {
            board()
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                LevelHeader(uiState.level)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(Res.string.moves_label, uiState.moves),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(8.dp))
                GiveUpButton(onGiveUp = onGiveUp)
            }
        }
    } else {
        LevelHeader(uiState.level, Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(Res.string.moves_label, uiState.moves),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(16.dp))
        Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            board()
        }
        Spacer(Modifier.height(16.dp))
        GiveUpButton(
            onGiveUp = onGiveUp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
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
