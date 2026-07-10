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
internal fun ColumnScope.SchulteTableContent(
    uiState: SchulteTableUiState,
    onAnswer: (String) -> Unit,
) {
    val n = uiState.gridSize
    val cellSize = when (n) {
        4 -> 64.dp
        5 -> 56.dp
        6 -> 48.dp
        else -> 42.dp
    }

    Text(
        text = stringResource(Res.string.game_schulte_instruction),
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(horizontal = 16.dp),
    )
    Spacer(Modifier.height(16.dp))

    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.align(Alignment.CenterHorizontally),
    ) {
        for (row in 0 until n) {
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                for (col in 0 until n) {
                    val index = row * n + col
                    SchulteCell(
                        cell = uiState.cells[index],
                        size = cellSize,
                        onClick = { onAnswer(index.toString()) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SchulteCell(
    cell: SchulteTableUiState.CellState,
    size: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit,
) {
    val face = when (cell.type) {
        SchulteTableUiState.CellType.NORMAL -> MaterialTheme.colorScheme.surfaceContainer
        SchulteTableUiState.CellType.TAPPED -> MaterialTheme.colorScheme.surfaceVariant
        SchulteTableUiState.CellType.WRONG -> MaterialTheme.colorScheme.errorContainer
    }
    val textColor = when (cell.type) {
        SchulteTableUiState.CellType.TAPPED -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        SchulteTableUiState.CellType.WRONG -> MaterialTheme.colorScheme.onErrorContainer
        SchulteTableUiState.CellType.NORMAL -> MaterialTheme.colorScheme.onSurface
    }
    val isInteractive = cell.type == SchulteTableUiState.CellType.NORMAL
    PrismTile(
        face = face,
        modifier = Modifier
            .size(size)
            .hoverHand(isInteractive),
        isClickable = isInteractive,
        isSelected = cell.type == SchulteTableUiState.CellType.TAPPED,
        onClick = onClick,
    ) {
        Text(
            text = cell.number.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontFamily = numberFontFamily(),
            fontWeight = FontWeight.Bold,
            color = textColor,
            textAlign = TextAlign.Center,
        )
    }
}

@DevicePreviews
@Composable
private fun SchulteTableContentPreview() {
    GamePreviewHost {
        SchulteTableContent(
            uiState = SchulteTableUiState(
                gridSize = 3,
                cells = persistentListOf(
                    SchulteTableUiState.CellState(1, SchulteTableUiState.CellType.NORMAL),
                    SchulteTableUiState.CellState(5, SchulteTableUiState.CellType.NORMAL),
                    SchulteTableUiState.CellState(3, SchulteTableUiState.CellType.NORMAL),
                    SchulteTableUiState.CellState(8, SchulteTableUiState.CellType.NORMAL),
                    SchulteTableUiState.CellState(2, SchulteTableUiState.CellType.NORMAL),
                    SchulteTableUiState.CellState(7, SchulteTableUiState.CellType.NORMAL),
                    SchulteTableUiState.CellState(4, SchulteTableUiState.CellType.NORMAL),
                    SchulteTableUiState.CellState(9, SchulteTableUiState.CellType.NORMAL),
                    SchulteTableUiState.CellState(6, SchulteTableUiState.CellType.NORMAL),
                ),
            ),
            onAnswer = {},
        )
    }
}
