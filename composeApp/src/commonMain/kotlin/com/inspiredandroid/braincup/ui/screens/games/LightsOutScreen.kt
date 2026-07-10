package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.theme.LightsOutOffColor
import com.inspiredandroid.braincup.ui.theme.LightsOutOnColor
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ColumnScope.LightsOutContent(
    uiState: LightsOutUiState,
    onAnswer: (String) -> Unit,
    onGiveUp: () -> Unit,
) {
    val n = uiState.gridSize
    val compact = LocalIsCompactHeight.current
    val cellSize = when {
        compact -> if (n <= 4) 48.dp else 40.dp
        n == 3 -> 72.dp
        n == 4 -> 60.dp
        else -> 52.dp
    }

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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            board()
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(Res.string.level_label, uiState.level),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
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
        Text(
            text = stringResource(Res.string.level_label, uiState.level),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
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
private fun LightsOutCell(
    on: Boolean,
    size: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit,
) {
    PrismTile(
        face = if (on) LightsOutOnColor else LightsOutOffColor,
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
