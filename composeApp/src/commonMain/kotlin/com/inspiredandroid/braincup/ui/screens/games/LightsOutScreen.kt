package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.theme.LightsOutOffColor
import com.inspiredandroid.braincup.ui.theme.LightsOutOffColorDark
import com.inspiredandroid.braincup.ui.theme.LightsOutOnColor
import com.inspiredandroid.braincup.ui.theme.isDarkColorScheme
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun ColumnScope.LightsOutContent(
    uiState: LightsOutUiState,
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
                        LightsOutCell(
                            on = uiState.cells[index],
                            size = cellSize,
                            onClick = { onAnswer(index.toString()) },
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
                MovesLabel(uiState.moves)
                Spacer(Modifier.height(8.dp))
                GiveUpButton(onGiveUp = onGiveUp)
            }
        }
    } else {
        LevelHeader(uiState.level, Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(4.dp))
        MovesLabel(uiState.moves, Modifier.align(Alignment.CenterHorizontally))
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
private fun LightsOutCell(
    on: Boolean,
    size: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit,
) {
    val offColor = if (isDarkColorScheme) LightsOutOffColorDark else LightsOutOffColor
    PrismTile(
        face = if (on) LightsOutOnColor else offColor,
        modifier = Modifier
            .size(size)
            .hoverHand(),
        isSelected = !on,
        onClick = onClick,
    ) {}
}

@DevicePreviews
@Composable
private fun LightsOutContentPreview() {
    GamePreviewHost {
        LightsOutContent(
            uiState = LightsOutUiState(
                gridSize = 3,
                cells = persistentListOf(true, false, true, false, true, false, true, false, true),
                moves = 0,
                level = 1,
            ),
            onAnswer = {},
            onGiveUp = {},
        )
    }
}
