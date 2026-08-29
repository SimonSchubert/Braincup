package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.games.MentalFlexGame
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.GameColor
import com.inspiredandroid.braincup.games.tools.Shape
import com.inspiredandroid.braincup.ui.components.*
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun ColumnScope.MentalFlexContent(
    uiState: MentalFlexUiState,
    onAnswer: (String) -> Unit,
) {
    val compact = LocalIsCompactHeight.current

    MentalFlexRuleCue(uiState.cueExemplar, Modifier.align(Alignment.CenterHorizontally))

    Spacer(Modifier.height(if (compact) 10.dp else 16.dp))

    // The target is drawn bare rather than on a PrismTile, so it never reads as one more thing to
    // tap alongside the candidates below it.
    ShapeCanvas(
        figure = uiState.target,
        modifier = Modifier
            .size(if (compact) 56.dp else 76.dp)
            .align(Alignment.CenterHorizontally),
    )

    Spacer(Modifier.height(if (compact) 12.dp else 20.dp))

    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .widthIn(max = 78.dp * uiState.columnsPerRow)
            .align(Alignment.CenterHorizontally),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        uiState.rows.forEachIndexed { y, cells ->
            Row {
                cells.forEachIndexed { x, cell ->
                    val index = y * uiState.columnsPerRow + x
                    FigureCellContent(
                        cell = cell,
                        onClick = { onAnswer("${index + 1}") },
                        modifier = Modifier.weight(1f).aspectRatio(1f).padding(6.dp),
                    )
                }
                // A final short row (6 tiles in 3 columns divides evenly, but a future count may
                // not) keeps its tiles the same size as the rows above instead of stretching.
                repeat(uiState.columnsPerRow - cells.size) {
                    Spacer(Modifier.weight(1f).aspectRatio(1f).padding(6.dp))
                }
            }
        }
    }
}

@DevicePreviews
@Composable
private fun MentalFlexContentPreview() {
    GamePreviewHost {
        MentalFlexContent(
            uiState = MentalFlexUiState(
                rule = MentalFlexGame.Rule.SHAPE,
                cueExemplar = persistentListOf(
                    Figure(Shape.HOUSE, GameColor.PURPLE),
                    Figure(Shape.HOUSE, GameColor.GREEN),
                ),
                target = Figure(Shape.STAR, GameColor.BLUE),
                rows = persistentListOf(
                    persistentListOf(
                        FigureCell(Figure(Shape.STAR, GameColor.RED)),
                        FigureCell(Figure(Shape.CIRCLE, GameColor.BLUE)),
                        FigureCell(Figure(Shape.HEART, GameColor.GREEN)),
                    ),
                    persistentListOf(
                        FigureCell(Figure(Shape.DIAMOND, GameColor.ORANGE)),
                        FigureCell(Figure(Shape.HOUSE, GameColor.PURPLE)),
                        FigureCell(Figure(Shape.ARROW, GameColor.TURQUOISE)),
                    ),
                ),
                columnsPerRow = 3,
            ),
            onAnswer = {},
        )
    }
}
