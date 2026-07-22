package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.app.PrismClearUiState
import com.inspiredandroid.braincup.games.PrismTileType
import com.inspiredandroid.braincup.games.tools.composeColor

/**
 * Public review card for a Prism Clear level (board + label). Used by Paparazzi gallery
 * screenshots; keeps rendering out of the `internal` play screen.
 */
@Composable
fun PrismClearLevelCard(
    uiState: PrismClearUiState,
    modifier: Modifier = Modifier,
    cellSize: Dp = 28.dp,
) {
    Column(
        modifier = modifier.padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Level ${uiState.level}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = "${uiState.rows}×${uiState.cols}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            for (row in 0 until uiState.rows) {
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    for (col in 0 until uiState.cols) {
                        val ordinal = uiState.cells[row * uiState.cols + col]
                        val face = if (ordinal == null) {
                            MaterialTheme.colorScheme.surfaceContainerHighest
                        } else {
                            PrismTileType.entries[ordinal].color.composeColor()
                        }
                        PrismTile(
                            face = face,
                            modifier = Modifier.size(cellSize),
                            isSelected = false,
                            isClickable = false,
                            onClick = {},
                        ) {}
                    }
                }
            }
        }
    }
}
