package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.layout.*
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
    val compact = LocalIsCompactHeight.current

    val board: @Composable () -> Unit = {
        SquareTileBoard(uiState.gridSize, compact) { index, cellSize ->
            LightsOutCell(
                on = uiState.cells[index],
                size = cellSize,
                onClick = { onAnswer(index.toString()) },
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
