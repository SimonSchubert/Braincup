package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.Shape
import com.inspiredandroid.braincup.ui.components.*
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ColumnScope.PathFinderContent(
    uiState: PathFinderUiState,
    onAnswer: (String) -> Unit,
) {
    val compact = LocalIsCompactHeight.current

    val instructionText: @Composable () -> Unit = {
        Text(
            text = stringResource(Res.string.game_follow_directions),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }

    val directions: @Composable () -> Unit = {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            uiState.directionFigures.forEach {
                ShapeCanvas(
                    figure = it,
                    modifier = Modifier.size(if (compact) 24.dp else 32.dp),
                )
            }
        }
    }

    val grid: @Composable (Modifier) -> Unit = { mod ->
        Column(
            modifier = mod,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            uiState.grid.forEachIndexed { y, cells ->
                Row {
                    cells.forEachIndexed { x, cell ->
                        val index = y * 4 + x + 1
                        PathFinderCell(
                            cell = cell,
                            onClick = { onAnswer(index.toString()) },
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

    if (compact) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                instructionText()
                Spacer(Modifier.height(8.dp))
                directions()
            }
            grid(Modifier.widthIn(max = 48.dp * 4))
        }
    } else {
        instructionText()
        Spacer(Modifier.height(8.dp))
        directions()
        Spacer(Modifier.height(16.dp))
        grid(
            Modifier
                .padding(horizontal = 24.dp)
                .widthIn(max = 64.dp * 4),
        )
    }
}

@GameDevicePreviews
@Composable
private fun PathFinderContentPreview() {
    val fig = Figure(Shape.TRIANGLE, Color.GREEN)
    GamePreviewHost {
        PathFinderContent(
            uiState = PathFinderUiState(
                directionFigures = persistentListOf(fig, fig),
                grid = persistentListOf(
                    persistentListOf(FigureCell(fig), FigureCell(fig)),
                    persistentListOf(FigureCell(fig), FigureCell(fig)),
                ),
            ),
            onAnswer = {},
        )
    }
}
