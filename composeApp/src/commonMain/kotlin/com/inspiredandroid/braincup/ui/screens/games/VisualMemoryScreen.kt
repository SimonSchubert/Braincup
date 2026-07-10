package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.games.VisualMemoryGame
import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.Shape
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.theme.PrimaryContainer
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

private const val VisualMemoryTransitionMillis = 250

private val CellTypesShowingShape = setOf(
    VisualMemoryUiState.CellType.MEMORIZING,
    VisualMemoryUiState.CellType.REVEALED,
    VisualMemoryUiState.CellType.WRONG,
)

@Composable
internal fun ColumnScope.VisualMemoryContent(
    uiState: VisualMemoryUiState,
    onAnswer: (String) -> Unit,
) {
    val showAnswerOptions = uiState.phase == VisualMemoryGame.Phase.ANSWERING ||
        uiState.phase == VisualMemoryGame.Phase.GAME_OVER

    if (LocalIsCompactHeight.current) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            VisualMemoryGrid(
                cells = uiState.cells,
                modifier = Modifier.widthIn(max = 200.dp),
            )
            VisualMemoryAnswerOptions(
                options = uiState.answerOptions,
                visible = showAnswerOptions,
                onAnswer = onAnswer,
                modifier = Modifier.widthIn(max = 180.dp),
            )
        }
    } else {
        VisualMemoryGrid(
            cells = uiState.cells,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 24.dp)
                .widthIn(max = 80.dp * 3),
        )

        Spacer(Modifier.height(24.dp))

        VisualMemoryAnswerOptions(
            options = uiState.answerOptions,
            visible = showAnswerOptions,
            onAnswer = onAnswer,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 24.dp)
                .widthIn(max = 72.dp * 3),
        )
    }
}

@Composable
private fun VisualMemoryGrid(
    cells: ImmutableList<VisualMemoryUiState.CellState>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        cells.chunked(3).forEach { rowCells ->
            Row {
                rowCells.forEach { cell ->
                    VisualMemoryCell(
                        cell = cell,
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
private fun VisualMemoryAnswerOptions(
    options: ImmutableList<VisualMemoryUiState.AnswerOption>,
    visible: Boolean,
    onAnswer: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val alpha = animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(VisualMemoryTransitionMillis),
        label = "visualMemoryAnswerOptions",
    )
    Column(
        modifier = modifier.graphicsLayer { this.alpha = alpha.value },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        options.chunked(3).forEach { rowOptions ->
            Row {
                rowOptions.forEach { option ->
                    VisualMemoryAnswerOption(
                        option = option,
                        onAnswer = onAnswer,
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
private fun VisualMemoryAnswerOption(
    option: VisualMemoryUiState.AnswerOption,
    onAnswer: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val face = if (option.isWrong) {
        MaterialTheme.colorScheme.errorContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainer
    }
    val cellModifier = if (option.enabled || option.isWrong) modifier else modifier.alpha(0.3f)
    PrismTile(
        face = face,
        modifier = cellModifier,
        isClickable = option.enabled && !option.isWrong,
        onClick = { onAnswer(option.figureIndex.toString()) },
    ) {
        ShapeCanvas(
            figure = option.figure,
            modifier = Modifier.fillMaxSize().padding(8.dp),
        )
    }
}

@Composable
private fun VisualMemoryCell(
    cell: VisualMemoryUiState.CellState,
    modifier: Modifier = Modifier,
) {
    val targetContainerColor = when (cell.type) {
        VisualMemoryUiState.CellType.CURRENT_TARGET -> PrimaryContainer
        VisualMemoryUiState.CellType.HIDDEN,
        VisualMemoryUiState.CellType.EMPTY,
        -> MaterialTheme.colorScheme.surfaceVariant
        VisualMemoryUiState.CellType.MEMORIZING,
        VisualMemoryUiState.CellType.REVEALED,
        -> MaterialTheme.colorScheme.surfaceContainer
        VisualMemoryUiState.CellType.WRONG -> MaterialTheme.colorScheme.errorContainer
    }
    val containerColor by animateColorAsState(
        targetValue = targetContainerColor,
        animationSpec = tween(VisualMemoryTransitionMillis),
        label = "visualMemoryCellContainer",
    )
    val showShape = cell.figure != null && cell.type in CellTypesShowingShape
    val showQuestion = cell.type == VisualMemoryUiState.CellType.CURRENT_TARGET
    val shapeAlpha = animateFloatAsState(
        targetValue = if (showShape) 1f else 0f,
        animationSpec = tween(VisualMemoryTransitionMillis),
        label = "visualMemoryCellShape",
    )
    val questionAlpha = animateFloatAsState(
        targetValue = if (showQuestion) 1f else 0f,
        animationSpec = tween(VisualMemoryTransitionMillis),
        label = "visualMemoryCellQuestion",
    )
    val questionVisible by remember { derivedStateOf { questionAlpha.value > 0f } }
    val shapeVisible by remember { derivedStateOf { shapeAlpha.value > 0f } }
    // Hold the last figure so the fade-out keeps drawing it after the cell type
    // transitions to HIDDEN, where cell.figure is null.
    var rememberedFigure by remember { mutableStateOf(cell.figure) }
    SideEffect {
        if (cell.figure != null) rememberedFigure = cell.figure
    }

    val isPocket = cell.type == VisualMemoryUiState.CellType.HIDDEN ||
        cell.type == VisualMemoryUiState.CellType.EMPTY
    PrismTile(
        face = containerColor,
        modifier = modifier,
        isClickable = false,
        isSelected = isPocket,
        onClick = {},
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            if (questionVisible) {
                Text(
                    text = "?",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.graphicsLayer { alpha = questionAlpha.value },
                )
            }
            val figure = rememberedFigure
            if (shapeVisible && figure != null) {
                ShapeCanvas(
                    figure = figure,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .graphicsLayer { alpha = shapeAlpha.value },
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun VisualMemoryContentPreview() {
    val fig = Figure(Shape.SQUARE, Color.RED)
    GamePreviewHost {
        VisualMemoryContent(
            uiState = VisualMemoryUiState(
                round = 1,
                phase = VisualMemoryGame.Phase.MEMORIZING,
                countdown = 0,
                cells = persistentListOf(
                    VisualMemoryUiState.CellState(VisualMemoryUiState.CellType.MEMORIZING, fig),
                    VisualMemoryUiState.CellState(VisualMemoryUiState.CellType.EMPTY, null),
                    VisualMemoryUiState.CellState(VisualMemoryUiState.CellType.EMPTY, null),
                    VisualMemoryUiState.CellState(VisualMemoryUiState.CellType.MEMORIZING, fig),
                ),
                answerOptions = persistentListOf(),
                currentTargetFigure = null,
            ),
            onAnswer = {},
        )
    }
}
