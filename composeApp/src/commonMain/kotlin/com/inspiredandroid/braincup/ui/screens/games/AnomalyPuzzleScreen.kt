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
import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.Shape
import com.inspiredandroid.braincup.ui.components.*
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun AnomalyPuzzleContent(
    uiState: AnomalyPuzzleUiState,
    onAnswer: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .widthIn(max = 80.dp * uiState.columnsPerRow),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        uiState.rows.forEachIndexed { y, cells ->
            Row {
                cells.forEachIndexed { x, cell ->
                    val index = y * uiState.columnsPerRow + x
                    FigureCellContent(
                        cell = cell,
                        onClick = { onAnswer("${index + 1}") },
                        modifier = Modifier.weight(1f).aspectRatio(1f).padding(8.dp),
                    )
                }
            }
        }
    }
}

@GameDevicePreviews
@Composable
private fun AnomalyPuzzleContentPreview() {
    val fig = Figure(Shape.SQUARE, Color.BLUE)
    GamePreviewHost {
        AnomalyPuzzleContent(
            uiState = AnomalyPuzzleUiState(
                rows = persistentListOf(
                    persistentListOf(FigureCell(fig), FigureCell(fig), FigureCell(fig)),
                    persistentListOf(FigureCell(fig), FigureCell(Figure(Shape.CIRCLE, Color.RED)), FigureCell(fig)),
                    persistentListOf(FigureCell(fig), FigureCell(fig), FigureCell(fig)),
                ),
                columnsPerRow = 3,
            ),
            onAnswer = {},
        )
    }
}
