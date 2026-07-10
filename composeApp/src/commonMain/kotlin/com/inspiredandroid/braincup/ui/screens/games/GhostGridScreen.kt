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
import com.inspiredandroid.braincup.games.GhostGridGame
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.SuccessGreenSoft
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun ColumnScope.GhostGridContent(
    uiState: GhostGridUiState,
    onAnswer: (String) -> Unit,
) {
    val cellMax = if (LocalIsCompactHeight.current) 56.dp else 72.dp
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .widthIn(max = cellMax * uiState.gridSize)
            .align(Alignment.CenterHorizontally),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        uiState.cells.chunked(uiState.gridSize).forEachIndexed { y, rowCells ->
            Row {
                rowCells.forEachIndexed { x, cell ->
                    val position = y * uiState.gridSize + x
                    GhostGridCell(
                        cell = cell,
                        onClick = {
                            if (uiState.phase == GhostGridGame.Phase.ANSWERING) {
                                onAnswer(position.toString())
                            }
                        },
                        isClickable = uiState.phase == GhostGridGame.Phase.ANSWERING,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(4.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun GhostGridCell(
    cell: GhostGridUiState.CellState,
    onClick: () -> Unit,
    isClickable: Boolean,
    modifier: Modifier = Modifier,
) {
    val face = when (cell.type) {
        GhostGridUiState.CellType.ACTIVE -> Primary
        GhostGridUiState.CellType.TAPPED -> Primary
        GhostGridUiState.CellType.WRONG -> MaterialTheme.colorScheme.errorContainer
        GhostGridUiState.CellType.MISSED -> SuccessGreenSoft
        GhostGridUiState.CellType.INACTIVE -> MaterialTheme.colorScheme.surfaceVariant
    }
    PrismTile(
        face = face,
        modifier = modifier.hoverHand(isClickable),
        isClickable = isClickable,
        isSelected = cell.type == GhostGridUiState.CellType.TAPPED,
        onClick = onClick,
    ) {}
}

@DevicePreviews
@Composable
private fun GhostGridContentPreview() {
    GamePreviewHost {
        GhostGridContent(
            uiState = GhostGridUiState(
                gridSize = 3,
                round = 1,
                phase = GhostGridGame.Phase.SHOWING,
                cells = persistentListOf(
                    GhostGridUiState.CellState(GhostGridUiState.CellType.ACTIVE),
                    GhostGridUiState.CellState(GhostGridUiState.CellType.INACTIVE),
                    GhostGridUiState.CellState(GhostGridUiState.CellType.INACTIVE),
                    GhostGridUiState.CellState(GhostGridUiState.CellType.INACTIVE),
                    GhostGridUiState.CellState(GhostGridUiState.CellType.ACTIVE),
                    GhostGridUiState.CellState(GhostGridUiState.CellType.INACTIVE),
                    GhostGridUiState.CellState(GhostGridUiState.CellType.INACTIVE),
                    GhostGridUiState.CellState(GhostGridUiState.CellType.INACTIVE),
                    GhostGridUiState.CellState(GhostGridUiState.CellType.ACTIVE),
                ),
                sequenceLength = 3,
                tappedCount = 0,
            ),
            onAnswer = {},
        )
    }
}
