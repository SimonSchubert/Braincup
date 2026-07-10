package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.Shape
import com.inspiredandroid.braincup.games.tools.composeColor
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.localizedName
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import com.inspiredandroid.braincup.ui.theme.numberFontFamily
import kotlinx.collections.immutable.persistentListOf
import androidx.compose.ui.graphics.Color as ComposeColor

@Composable
internal fun ColumnScope.ColoredShapesContent(
    uiState: ColoredShapesUiState,
    onAnswer: (String) -> Unit,
) {
    val compact = LocalIsCompactHeight.current

    val shape: @Composable (Modifier) -> Unit = { mod ->
        ShapeCanvas(
            figure = uiState.displayedFigure,
            modifier = mod,
        )
    }

    val pointLabels: @Composable ColumnScope.() -> Unit = {
        Text(
            text = "${uiState.answerShape.localizedName()} = ${uiState.shapePoints}",
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = "${uiState.answerColor.localizedName()} = ${uiState.colorPoints}",
            style = MaterialTheme.typography.bodyLarge,
            color = uiState.stringColor.composeColor(),
        )
    }

    @Composable
    fun answerButton(button: AnswerButton) {
        when (button.state) {
            AnswerButtonState.NORMAL -> CircleButton(
                onClick = { onAnswer(button.value) },
                value = button.value,
            )
            AnswerButtonState.WRONG -> PrismTile(
                face = MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier.size(56.dp),
                isClickable = false,
                onClick = {},
            ) {
                Text(
                    button.value,
                    fontFamily = numberFontFamily(),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
            AnswerButtonState.CORRECT -> PrismTile(
                face = SuccessGreen,
                modifier = Modifier.size(56.dp),
                isClickable = false,
                onClick = {},
            ) {
                Text(
                    button.value,
                    fontFamily = numberFontFamily(),
                    color = ComposeColor.White,
                )
            }
            AnswerButtonState.DIMMED -> Box(
                modifier = Modifier.size(56.dp).alpha(0.3f),
                contentAlignment = Alignment.Center,
            ) {
                CircleButton(onClick = {}, value = button.value)
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
            shape(Modifier.size(160.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                pointLabels()
                Spacer(Modifier.height(12.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    uiState.possibleAnswers.chunked(2).forEach { rowButtons ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowButtons.forEach { answerButton(it) }
                        }
                    }
                }
            }
        }
    } else {
        shape(Modifier.size(200.dp).align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(16.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            pointLabels()
        }
        Spacer(Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            uiState.possibleAnswers.forEach { answerButton(it) }
        }
    }
}

@DevicePreviews
@Composable
private fun ColoredShapesContentPreview() {
    GamePreviewHost {
        ColoredShapesContent(
            uiState = ColoredShapesUiState(
                displayedFigure = Figure(Shape.HEART, Color.RED),
                answerShape = Shape.HEART,
                answerColor = Color.BLUE,
                stringColor = Color.BLUE,
                shapePoints = 2,
                colorPoints = 3,
                possibleAnswers = persistentListOf(
                    AnswerButton("2"),
                    AnswerButton("3"),
                    AnswerButton("5"),
                    AnswerButton("6"),
                ),
            ),
            onAnswer = {},
        )
    }
}
