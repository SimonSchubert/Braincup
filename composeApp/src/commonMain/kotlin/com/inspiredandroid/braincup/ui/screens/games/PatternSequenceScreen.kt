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
import com.inspiredandroid.braincup.ui.theme.OnPrimaryContainer
import com.inspiredandroid.braincup.ui.theme.PrimaryContainer
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ColumnScope.PatternSequenceContent(
    uiState: PatternSequenceUiState,
    onAnswer: (String) -> Unit,
) {
    Text(
        text = stringResource(Res.string.game_what_comes_next),
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.align(Alignment.CenterHorizontally),
    )
    Spacer(Modifier.height(16.dp))

    // Sequence row with "?" at end
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(horizontal = 16.dp),
    ) {
        uiState.sequence.forEach { figure ->
            ShapeCanvas(
                figure = figure,
                modifier = Modifier.size(48.dp),
            )
        }
        PrismCard(
            face = PrimaryContainer,
            modifier = Modifier.size(48.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize(),
            ) {
                Text(
                    text = "?",
                    style = MaterialTheme.typography.headlineMedium,
                    color = OnPrimaryContainer,
                )
            }
        }
    }
    Spacer(Modifier.height(24.dp))

    // 2x2 grid of options
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .widthIn(max = 80.dp * 2),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        uiState.optionRows.forEachIndexed { y, rowOptions ->
            Row {
                rowOptions.forEachIndexed { x, cell ->
                    val index = y * 2 + x
                    FigureCellContent(
                        cell = cell,
                        onClick = { onAnswer(index.toString()) },
                        modifier = Modifier.weight(1f).aspectRatio(1f).padding(8.dp),
                    )
                }
            }
        }
    }
}

@DevicePreviews
@Composable
private fun PatternSequenceContentPreview() {
    val fig = Figure(Shape.CIRCLE, Color.BLUE)
    GamePreviewHost {
        PatternSequenceContent(
            uiState = PatternSequenceUiState(
                sequence = persistentListOf(fig, fig, fig),
                optionRows = persistentListOf(
                    persistentListOf(FigureCell(fig), FigureCell(fig)),
                    persistentListOf(FigureCell(fig), FigureCell(fig)),
                ),
            ),
            onAnswer = {},
        )
    }
}
