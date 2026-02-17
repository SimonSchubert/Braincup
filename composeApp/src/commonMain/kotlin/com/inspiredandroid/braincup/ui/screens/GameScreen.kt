package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.button_backspace
import braincup.composeapp.generated.resources.button_done
import braincup.composeapp.generated.resources.button_give_up
import braincup.composeapp.generated.resources.game_fill_grid
import braincup.composeapp.generated.resources.game_follow_directions
import braincup.composeapp.generated.resources.game_goal
import braincup.composeapp.generated.resources.game_highest_value
import braincup.composeapp.generated.resources.game_tap_matching_colors
import braincup.composeapp.generated.resources.game_tap_numbers
import braincup.composeapp.generated.resources.game_what_comes_next
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.games.GhostGridGame
import com.inspiredandroid.braincup.games.VisualMemoryGame
import com.inspiredandroid.braincup.games.tools.Calculator
import com.inspiredandroid.braincup.ui.components.CircleButton
import com.inspiredandroid.braincup.ui.components.GameScaffold
import com.inspiredandroid.braincup.ui.components.NumberPad
import com.inspiredandroid.braincup.ui.components.NumberPadWithInput
import com.inspiredandroid.braincup.ui.components.ShapeCanvas
import com.inspiredandroid.braincup.ui.components.ShapeCanvasButton
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import org.jetbrains.compose.resources.stringResource

@Composable
fun GameScreen(
    gameUiState: GameUiState,
    timeRemaining: Long,
    onAnswer: (String) -> Unit,
    onGiveUp: () -> Unit,
    onBack: () -> Unit,
) {
    GameScaffold(onBack = onBack) {
        if (gameUiState !is VisualMemoryUiState && gameUiState !is GhostGridUiState) {
            TimeProgressIndicator(
                progress = timeRemaining / 60000f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }

        Spacer(Modifier.weight(1f))

        when (gameUiState) {
            is MentalCalculationUiState -> MentalCalculationContent(gameUiState, onAnswer)
            is ChainCalculationUiState -> ChainCalculationContent(gameUiState, onAnswer)
            is FractionCalculationUiState -> FractionCalculationContent(gameUiState, onAnswer, onGiveUp)
            is ColoredShapesUiState -> ColoredShapesContent(gameUiState, onAnswer)
            is SherlockCalculationUiState -> SherlockCalculationContent(gameUiState, onAnswer, onGiveUp)
            is ValueComparisonUiState -> ValueComparisonContent(gameUiState, onAnswer)
            is AnomalyPuzzleUiState -> AnomalyPuzzleContent(gameUiState, onAnswer)
            is PathFinderUiState -> PathFinderContent(gameUiState, onAnswer)
            is GridSolverUiState -> GridSolverContent(gameUiState, onAnswer)
            is PatternSequenceUiState -> PatternSequenceContent(gameUiState, onAnswer)
            is VisualMemoryUiState -> VisualMemoryContent(gameUiState, onAnswer)
            is GhostGridUiState -> GhostGridContent(gameUiState, onAnswer)
            is ColorConfusionUiState -> ColorConfusionContent(gameUiState, onAnswer)
        }

        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun ColumnScope.MentalCalculationContent(
    uiState: MentalCalculationUiState,
    onAnswer: (String) -> Unit,
) {
    Text(
        text = uiState.calculation,
        style = MaterialTheme.typography.displaySmall,
        modifier = Modifier.align(Alignment.CenterHorizontally),
    )
    Spacer(Modifier.height(16.dp))
    NumberPadWithInput(onInputChange = { input ->
        if (uiState.answerLength == input.length) {
            onAnswer(input)
        }
    })
}

@Composable
private fun ColumnScope.ChainCalculationContent(
    uiState: ChainCalculationUiState,
    onAnswer: (String) -> Unit,
) {
    Text(
        text = "${uiState.calculation} = ?",
        style = MaterialTheme.typography.displaySmall,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(horizontal = 16.dp),
    )
    Spacer(Modifier.height(16.dp))
    NumberPad(onInputChange = { input ->
        if (input.toIntOrNull() == uiState.answer) {
            onAnswer(input)
        }
    })
}

@Composable
private fun ColumnScope.ColoredShapesContent(
    uiState: ColoredShapesUiState,
    onAnswer: (String) -> Unit,
) {
    // Show actual shape with color
    ShapeCanvas(
        figure = uiState.displayedFigure,
        modifier = Modifier.size(200.dp).align(Alignment.CenterHorizontally),
    )
    Spacer(Modifier.height(16.dp))

    // Point assignments
    Text(
        text = "${uiState.answerShape.displayName} = ${uiState.shapePoints}",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.align(Alignment.CenterHorizontally),
    )
    Text(
        text = "${uiState.answerColor.displayName} = ${uiState.colorPoints}",
        style = MaterialTheme.typography.bodyLarge,
        color = uiState.stringColor.composeColor,
        modifier = Modifier.align(Alignment.CenterHorizontally),
    )
    Spacer(Modifier.height(16.dp))

    // Answer buttons
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.align(Alignment.CenterHorizontally),
    ) {
        uiState.possibleAnswers.forEach { button ->
            when (button.state) {
                AnswerButtonState.NORMAL -> CircleButton(
                    onClick = { onAnswer(button.value) },
                    value = button.value,
                )
                AnswerButtonState.WRONG -> Card(
                    modifier = Modifier.size(56.dp),
                    shape = androidx.compose.foundation.shape.CircleShape,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(button.value, color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
                AnswerButtonState.CORRECT -> Card(
                    modifier = Modifier.size(56.dp),
                    shape = androidx.compose.foundation.shape.CircleShape,
                    colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.15f)),
                    border = BorderStroke(2.dp, SuccessGreen),
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(button.value)
                    }
                }
                AnswerButtonState.DIMMED -> Box(
                    modifier = Modifier.size(56.dp).alpha(0.3f),
                    contentAlignment = Alignment.Center,
                ) {
                    CircleButton(onClick = {}, value = button.value)
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.SherlockCalculationContent(
    uiState: SherlockCalculationUiState,
    onAnswer: (String) -> Unit,
    onGiveUp: () -> Unit,
) {
    // Goal display
    Text(
        text = stringResource(Res.string.game_goal, uiState.result),
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.align(Alignment.CenterHorizontally),
    )
    Spacer(Modifier.height(16.dp))

    // Use key to reset state when uiState.result changes (new round)
    key(uiState.result, uiState.solutionTokens) {
        var usedNumberIndices by remember { mutableStateOf(emptySet<Int>()) }
        var expressionTokens by remember { mutableStateOf(emptyList<ExpressionToken>()) }

        val showingSolution = uiState.solutionTokens != null

        fun checkAnswer() {
            val expr = expressionTokens.joinToString("") { it.displayValue }
            try {
                if (Calculator.calculate(expr).toInt() == uiState.result) {
                    onAnswer(expr)
                }
            } catch (_: Exception) {
                // expression not yet valid
            }
        }

        // Expression row
        ExpressionRow(
            tokens = if (showingSolution) {
                uiState.solutionTokens
            } else {
                expressionTokens
            },
            onTokenClick = { tokenIndex ->
                if (!showingSolution) {
                    val token = expressionTokens[tokenIndex]
                    expressionTokens = expressionTokens.toMutableList().apply { removeAt(tokenIndex) }
                    if (token is ExpressionToken.NumberToken) {
                        usedNumberIndices = usedNumberIndices - token.originalIndex
                    }
                }
            },
            onBackspace = {
                if (!showingSolution && expressionTokens.isNotEmpty()) {
                    val lastToken = expressionTokens.last()
                    expressionTokens = expressionTokens.dropLast(1)
                    if (lastToken is ExpressionToken.NumberToken) {
                        usedNumberIndices = usedNumberIndices - lastToken.originalIndex
                    }
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(16.dp))

        // Available numbers — always visible, disabled when showing solution
        AvailableNumbersRow(
            numbers = uiState.numbers,
            usedIndices = if (showingSolution) uiState.numbers.indices.toSet() else usedNumberIndices,
            onNumberClick = { value, index ->
                expressionTokens = expressionTokens + ExpressionToken.NumberToken(value, index)
                usedNumberIndices = usedNumberIndices + index
                checkAnswer()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(12.dp))

        // Operators — always visible, non-interactive when showing solution
        OperatorRow(
            onOperatorClick = { operator ->
                if (!showingSolution) {
                    expressionTokens = expressionTokens + ExpressionToken.OperatorToken(operator)
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(16.dp))

        // Give up button — hidden when showing solution
        if (!showingSolution) {
            TextButton(
                onClick = onGiveUp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .pointerHoverIcon(PointerIcon.Hand),
            ) {
                Text(stringResource(Res.string.button_give_up))
            }
        }
    }
}

@Composable
private fun ColumnScope.FractionCalculationContent(
    uiState: FractionCalculationUiState,
    onAnswer: (String) -> Unit,
    onGiveUp: () -> Unit,
) {
    Text(
        text = uiState.calculation,
        style = MaterialTheme.typography.displaySmall,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(horizontal = 16.dp),
    )
    Spacer(Modifier.height(16.dp))
    NumberPadWithInput(onInputChange = { input ->
        if (input == uiState.answerString || input.length >= 4) {
            onAnswer(input)
        }
    })
    Spacer(Modifier.height(16.dp))
    TextButton(
        onClick = onGiveUp,
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .pointerHoverIcon(PointerIcon.Hand),
    ) {
        Text(stringResource(Res.string.button_give_up))
    }
}

@Composable
private fun FigureCellContent(
    cell: FigureCell,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (cell.state) {
        FigureCellState.NORMAL -> ShapeCanvasButton(figure = cell.figure, onClick = onClick, modifier = modifier)
        FigureCellState.WRONG -> Card(modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
            ShapeCanvas(figure = cell.figure, modifier = Modifier.fillMaxSize().padding(8.dp))
        }
        FigureCellState.CORRECT -> Card(modifier, colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.15f)), border = BorderStroke(2.dp, SuccessGreen)) {
            ShapeCanvas(figure = cell.figure, modifier = Modifier.fillMaxSize().padding(8.dp))
        }
        FigureCellState.DIMMED -> ShapeCanvas(figure = cell.figure, modifier = modifier.alpha(0.3f))
    }
}

@Composable
private fun AnomalyPuzzleContent(
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

@Composable
private fun ColumnScope.PathFinderContent(
    uiState: PathFinderUiState,
    onAnswer: (String) -> Unit,
) {
    Text(
        text = stringResource(Res.string.game_follow_directions),
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.align(Alignment.CenterHorizontally),
    )
    Spacer(Modifier.height(8.dp))
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        uiState.directionFigures.forEach {
            ShapeCanvas(
                figure = it,
                modifier = Modifier.size(32.dp),
            )
        }
    }
    Spacer(Modifier.height(16.dp))

    // 4x4 grid
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .widthIn(max = 64.dp * 4),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        uiState.grid.forEachIndexed { y, cells ->
            Row {
                cells.forEachIndexed { x, cell ->
                    val index = y * 4 + x + 1
                    FigureCellContent(
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

@Composable
private fun ColumnScope.ValueComparisonContent(
    uiState: ValueComparisonUiState,
    onAnswer: (String) -> Unit,
) {
    Text(
        text = stringResource(Res.string.game_highest_value),
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.align(Alignment.CenterHorizontally),
    )
    Spacer(Modifier.height(16.dp))

    uiState.answers.forEachIndexed { index, answer ->
        Button(
            onClick = { onAnswer((index + 1).toString()) },
            modifier = Modifier
                .padding(4.dp)
                .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp),
        ) {
            Text(answer)
        }
    }
}

@Composable
private fun ColumnScope.GridSolverContent(
    uiState: GridSolverUiState,
    onAnswer: (String) -> Unit,
) {
    val totalCells = uiState.gridSize * uiState.gridSize
    var inputs by remember { mutableStateOf(List(totalCells) { "" }) }
    var currentIndex by remember { mutableStateOf(0) }

    Text(
        text = stringResource(Res.string.game_fill_grid),
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(horizontal = 16.dp),
    )
    Spacer(Modifier.height(16.dp))

    // Display the grid with row and column sums (hidden answers)
    Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
        for (rowIndex in 0 until uiState.gridSize) {
            Row {
                for (colIndex in 0 until uiState.gridSize) {
                    val cellIndex = rowIndex * uiState.gridSize + colIndex
                    val isCurrentCell = cellIndex == currentIndex
                    val cellValue = inputs[cellIndex]
                    Card(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(48.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrentCell) {
                                MaterialTheme.colorScheme.secondaryContainer
                            } else {
                                MaterialTheme.colorScheme.surface
                            },
                        ),
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            Text(
                                text = cellValue.ifEmpty { "?" },
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    }
                }
                // Row sum
                Card(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(48.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Text(
                            text = "=${uiState.resultsY[rowIndex]}",
                            style = MaterialTheme.typography.titleSmall,
                        )
                    }
                }
            }
        }
        // Column sums row
        Row {
            uiState.resultsX.forEach { sum ->
                Card(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(48.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Text(
                            text = "=$sum",
                            style = MaterialTheme.typography.titleSmall,
                        )
                    }
                }
            }
        }
    }

    Spacer(Modifier.height(24.dp))

    NumberPad(onInputChange = { input ->
        if (input.isNotEmpty()) {
            val newInputs = inputs.toMutableList()
            newInputs[currentIndex] = input.last().toString()
            inputs = newInputs

            if (currentIndex < totalCells - 1) {
                currentIndex++
            } else {
                // All cells filled, submit answer
                val answer = inputs.joinToString(",")
                onAnswer(answer)
            }
        }
    })
}

// --- Sherlock Calculation Helper Composables ---

@Composable
private fun ExpressionRow(
    tokens: List<ExpressionToken>,
    onTokenClick: (Int) -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (tokens.isEmpty()) {
            Text(
                text = stringResource(Res.string.game_tap_numbers),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                tokens.forEachIndexed { index, token ->
                    when (token) {
                        is ExpressionToken.NumberToken -> {
                            FilterChip(
                                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                                selected = true,
                                onClick = { onTokenClick(index) },
                                label = { Text(token.displayValue) },
                            )
                        }
                        is ExpressionToken.OperatorToken -> {
                            AssistChip(
                                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                                onClick = { onTokenClick(index) },
                                label = { Text(token.displayValue) },
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.width(8.dp))
        IconButton(
            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
            onClick = onBackspace,
            enabled = tokens.isNotEmpty(),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Backspace,
                contentDescription = stringResource(Res.string.button_backspace),
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AvailableNumbersRow(
    numbers: List<Int>,
    usedIndices: Set<Int>,
    onNumberClick: (value: Int, index: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        numbers.forEachIndexed { index, value ->
            val isUsed = index in usedIndices
            FilledTonalButton(
                onClick = { onNumberClick(value, index) },
                enabled = !isUsed,
                modifier = Modifier
                    .size(56.dp)
                    .pointerHoverIcon(PointerIcon.Hand),
                contentPadding = PaddingValues(0.dp),
            ) {
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

@Composable
private fun OperatorRow(
    onOperatorClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val operators = listOf("+", "-", "*", "/", "(", ")")
    FlowRow(
        modifier = modifier.padding(horizontal = 16.dp),
        maxItemsInEachRow = 3,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        operators.forEach { operator ->
            CircleButton(
                onClick = { onOperatorClick(operator) },
                value = operator,
            )
        }
    }
}

@Composable
private fun TimeProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val progressColor = MaterialTheme.colorScheme.primary

    Canvas(
        modifier = modifier.height(12.dp),
    ) {
        val cornerRadius = CornerRadius(size.height / 2, size.height / 2)

        // Draw track (background)
        drawRoundRect(
            color = trackColor,
            size = size,
            cornerRadius = cornerRadius,
        )

        // Draw progress
        val progressWidth = size.width * progress.coerceIn(0f, 1f)
        if (progressWidth > 0f) {
            drawRoundRect(
                color = progressColor,
                size = Size(progressWidth, size.height),
                cornerRadius = cornerRadius,
            )
        }
    }
}

// --- Visual Memory Game Composables ---

@Composable
private fun ColumnScope.VisualMemoryContent(
    uiState: VisualMemoryUiState,
    onAnswer: (String) -> Unit,
) {
    Spacer(Modifier.height(8.dp))

    // Countdown during memorization phase
    if (uiState.phase == VisualMemoryGame.Phase.MEMORIZING) {
        Text(
            text = "${uiState.countdown}",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
    Spacer(Modifier.height(16.dp))

    // 3x3 Grid
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .widthIn(max = 80.dp * 3),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        uiState.cells.chunked(3).forEach { rowCells ->
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

    Spacer(Modifier.height(24.dp))

    // Answer options (visible during answer and game-over phases)
    if (uiState.phase == VisualMemoryGame.Phase.ANSWERING || uiState.phase == VisualMemoryGame.Phase.GAME_OVER) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .widthIn(max = 72.dp * 3),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            uiState.answerOptions.chunked(3).forEach { rowOptions ->
                Row {
                    rowOptions.forEach { option ->
                        if (option.isWrong) {
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                ),
                            ) {
                                ShapeCanvas(
                                    figure = option.figure,
                                    modifier = Modifier.fillMaxSize().padding(8.dp),
                                )
                            }
                        } else {
                            ShapeCanvasButton(
                                figure = option.figure,
                                onClick = { onAnswer(option.figureIndex.toString()) },
                                enabled = option.enabled,
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
    }
}

@Composable
private fun VisualMemoryCell(
    cell: VisualMemoryUiState.CellState,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = when (cell.type) {
                VisualMemoryUiState.CellType.CURRENT_TARGET -> MaterialTheme.colorScheme.primaryContainer
                VisualMemoryUiState.CellType.HIDDEN -> MaterialTheme.colorScheme.surfaceVariant
                VisualMemoryUiState.CellType.EMPTY -> MaterialTheme.colorScheme.surfaceVariant
                VisualMemoryUiState.CellType.MEMORIZING -> MaterialTheme.colorScheme.surface
                VisualMemoryUiState.CellType.REVEALED -> MaterialTheme.colorScheme.surface
                VisualMemoryUiState.CellType.WRONG -> MaterialTheme.colorScheme.errorContainer
            },
        ),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            when (cell.type) {
                VisualMemoryUiState.CellType.CURRENT_TARGET -> {
                    Text(
                        text = "?",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
                VisualMemoryUiState.CellType.MEMORIZING,
                VisualMemoryUiState.CellType.REVEALED,
                VisualMemoryUiState.CellType.WRONG,
                -> {
                    if (cell.figure != null) {
                        ShapeCanvas(
                            figure = cell.figure,
                            modifier = Modifier.fillMaxSize().padding(8.dp),
                        )
                    }
                }
                else -> {}
            }
        }
    }
}

// --- Ghost Grid Game Composables ---

@Composable
private fun ColumnScope.GhostGridContent(
    uiState: GhostGridUiState,
    onAnswer: (String) -> Unit,
) {
    // NxN Grid
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .widthIn(max = 72.dp * uiState.gridSize),
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
    Card(
        onClick = onClick,
        enabled = isClickable,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = when (cell.type) {
                GhostGridUiState.CellType.ACTIVE -> MaterialTheme.colorScheme.primary
                GhostGridUiState.CellType.TAPPED -> MaterialTheme.colorScheme.primary
                GhostGridUiState.CellType.WRONG -> MaterialTheme.colorScheme.errorContainer
                GhostGridUiState.CellType.MISSED -> SuccessGreen.copy(alpha = 0.15f)
                GhostGridUiState.CellType.INACTIVE -> MaterialTheme.colorScheme.surfaceVariant
            },
            disabledContainerColor = when (cell.type) {
                GhostGridUiState.CellType.ACTIVE -> MaterialTheme.colorScheme.primary
                GhostGridUiState.CellType.TAPPED -> MaterialTheme.colorScheme.primary
                GhostGridUiState.CellType.WRONG -> MaterialTheme.colorScheme.errorContainer
                GhostGridUiState.CellType.MISSED -> SuccessGreen.copy(alpha = 0.15f)
                GhostGridUiState.CellType.INACTIVE -> MaterialTheme.colorScheme.surfaceVariant
            },
        ),
        border = if (cell.type == GhostGridUiState.CellType.MISSED) {
            BorderStroke(2.dp, SuccessGreen)
        } else {
            null
        },
    ) {
        Box(Modifier.fillMaxSize())
    }
}

// --- Pattern Sequence Game Composables ---

@Composable
private fun ColumnScope.PatternSequenceContent(
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
        Card(
            modifier = Modifier.size(48.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize(),
            ) {
                Text(
                    text = "?",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
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

// --- Color Confusion Game Composables ---

@Composable
private fun ColumnScope.ColorConfusionContent(
    uiState: ColorConfusionUiState,
    onAnswer: (String) -> Unit,
) {
    Text(
        text = stringResource(Res.string.game_tap_matching_colors),
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.align(Alignment.CenterHorizontally),
    )
    Spacer(Modifier.height(16.dp))

    // 3x3 Grid
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .widthIn(max = 100.dp * 3),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        uiState.cells.chunked(3).forEachIndexed { y, rowCells ->
            Row {
                rowCells.forEachIndexed { x, cell ->
                    val index = y * 3 + x
                    ColorConfusionCell(
                        cell = cell,
                        onClick = {
                            if (!uiState.isSubmitted) {
                                onAnswer(index.toString())
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(4.dp),
                    )
                }
            }
        }
    }

    Spacer(Modifier.height(16.dp))

    if (!uiState.isSubmitted) {
        Button(
            onClick = { onAnswer("submit") },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .pointerHoverIcon(PointerIcon.Hand),
        ) {
            Text(stringResource(Res.string.button_done))
        }
    }
}

@Composable
private fun ColorConfusionCell(
    cell: ColorConfusionUiState.Cell,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor = when (cell.feedback) {
        ColorConfusionUiState.CellFeedback.CORRECT_SELECTED -> SuccessGreen.copy(alpha = 0.15f)
        ColorConfusionUiState.CellFeedback.WRONG_SELECTED -> MaterialTheme.colorScheme.errorContainer
        ColorConfusionUiState.CellFeedback.MISSED -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.surface
    }
    val border = when {
        cell.feedback == ColorConfusionUiState.CellFeedback.CORRECT_SELECTED -> BorderStroke(2.dp, SuccessGreen)
        cell.feedback == ColorConfusionUiState.CellFeedback.MISSED -> BorderStroke(2.dp, MaterialTheme.colorScheme.error)
        cell.isSelected && cell.feedback == ColorConfusionUiState.CellFeedback.NONE -> BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        else -> null
    }

    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = border,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                text = cell.word,
                style = MaterialTheme.typography.titleMedium,
                color = cell.fontColor.composeColor,
            )
        }
    }
}
