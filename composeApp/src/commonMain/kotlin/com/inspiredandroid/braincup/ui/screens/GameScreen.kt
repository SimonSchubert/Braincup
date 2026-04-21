package com.inspiredandroid.braincup.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.games.GhostGridGame
import com.inspiredandroid.braincup.games.OrbitTrackerGame
import com.inspiredandroid.braincup.games.VisualMemoryGame
import com.inspiredandroid.braincup.games.tools.Calculator
import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.localizedName
import com.inspiredandroid.braincup.ui.theme.OnPrimaryContainer
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.PrimaryContainer
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.graphics.Color as ComposeColor

private val FlashCrowdBlue = ComposeColor(0xFF4285F4)
private val FlashCrowdYellow = ComposeColor(0xFFFBBC04)

@Composable
fun GameScreen(
    gameUiState: GameUiState,
    timeRemaining: Long,
    onAnswer: (String) -> Unit,
    onGiveUp: () -> Unit,
    onBack: () -> Unit,
) {
    GameScaffold(onBack = onBack) {
        if (gameUiState !is VisualMemoryUiState && gameUiState !is GhostGridUiState && gameUiState !is OrbitTrackerUiState) {
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
            is OrbitTrackerUiState -> OrbitTrackerContent(gameUiState, onAnswer)
            is FlashCrowdUiState -> FlashCrowdContent(gameUiState, onAnswer)
        }

        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun ColumnScope.MentalCalculationContent(
    uiState: MentalCalculationUiState,
    onAnswer: (String) -> Unit,
) {
    MathText(
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
    MathText(
        text = "${uiState.calculation} = ?",
        style = MaterialTheme.typography.displaySmall,
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(horizontal = 16.dp),
    )
    Spacer(Modifier.height(16.dp))
    NumberPadWithInput(onInputChange = { input ->
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
        text = "${uiState.answerShape.localizedName()} = ${uiState.shapePoints}",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.align(Alignment.CenterHorizontally),
    )
    Text(
        text = "${uiState.answerColor.localizedName()} = ${uiState.colorPoints}",
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

        TextButton(
            onClick = onGiveUp,
            enabled = !showingSolution,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .alpha(if (showingSolution) 0f else 1f)
                .hoverHand(),
        ) {
            Text(stringResource(Res.string.button_give_up))
        }
    }
}

@Composable
private fun ColumnScope.FractionCalculationContent(
    uiState: FractionCalculationUiState,
    onAnswer: (String) -> Unit,
    onGiveUp: () -> Unit,
) {
    Row(
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val parts = uiState.calculation.split(" * ")
        parts.forEachIndexed { index, part ->
            val fraction = part.removeSurrounding("(", ")")
            val fractionParts = fraction.split("/")
            if (fractionParts.size == 2) {
                FractionText(
                    numerator = fractionParts[0],
                    denominator = fractionParts[1],
                    style = MaterialTheme.typography.displaySmall,
                )
            } else {
                Text(part, style = MaterialTheme.typography.displaySmall)
            }

            if (index < parts.size - 1) {
                Text("\u00D7", style = MaterialTheme.typography.displaySmall)
            }
        }
    }
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
            .hoverHand(),
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
                .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                .hoverHand(),
        ) {
            if (answer.contains("/")) {
                val parts = answer.split("/")
                FractionText(
                    numerator = parts[0],
                    denominator = parts[1],
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            } else {
                MathText(answer)
            }
        }
    }
}

@Composable
private fun ColumnScope.GridSolverContent(
    uiState: GridSolverUiState,
    onAnswer: (String) -> Unit,
) {
    val totalCells = uiState.gridSize * uiState.gridSize
    // Key on uiState so state resets each new round — grid size can grow (3→4), and pre-filled
    // clues differ every round. Without the key, the previous round's inputs persist and can
    // index out of bounds on a larger grid.
    var inputs by remember(uiState) {
        mutableStateOf(uiState.initialValues.map { it?.toString() ?: "" })
    }
    var currentIndex by remember(uiState) {
        mutableStateOf(uiState.initialValues.indexOfFirst { it == null }.coerceAtLeast(0))
    }

    Text(
        text = stringResource(Res.string.game_fill_grid),
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(horizontal = 16.dp),
    )
    Spacer(Modifier.height(16.dp))

    val showingSolution = uiState.solutionValues != null
    Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
        for (rowIndex in 0 until uiState.gridSize) {
            Row {
                for (colIndex in 0 until uiState.gridSize) {
                    val cellIndex = rowIndex * uiState.gridSize + colIndex
                    val isInitialValue = uiState.initialValues[cellIndex] != null
                    val isSolutionCell = showingSolution && !isInitialValue
                    val isCurrentCell = cellIndex == currentIndex && !showingSolution
                    val cellValue = when {
                        isSolutionCell -> uiState.solutionValues[cellIndex].toString()
                        else -> inputs[cellIndex]
                    }
                    val isInteractive = !isInitialValue && !showingSolution
                    Card(
                        onClick = {
                            if (isInteractive) {
                                currentIndex = cellIndex
                            }
                        },
                        enabled = isInteractive,
                        modifier = Modifier
                            .padding(4.dp)
                            .size(48.dp)
                            .hoverHand(isInteractive),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                isSolutionCell -> SuccessGreen.copy(alpha = 0.15f)
                                isCurrentCell -> MaterialTheme.colorScheme.secondaryContainer
                                isInitialValue -> MaterialTheme.colorScheme.surfaceVariant
                                else -> MaterialTheme.colorScheme.surface
                            },
                        ),
                        border = if (isSolutionCell) BorderStroke(2.dp, SuccessGreen) else null,
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            Text(
                                text = cellValue.ifEmpty { "?" },
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isInitialValue) {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
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
                        containerColor = PrimaryContainer,
                    ),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Text(
                            text = "=${uiState.resultsY[rowIndex]}",
                            style = MaterialTheme.typography.titleSmall,
                            color = OnPrimaryContainer,
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
                        containerColor = PrimaryContainer,
                    ),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Text(
                            text = "=$sum",
                            style = MaterialTheme.typography.titleSmall,
                            color = OnPrimaryContainer,
                        )
                    }
                }
            }
        }
    }

    Spacer(Modifier.height(24.dp))

    NumberPad(onInputChange = { input ->
        if (input.isNotEmpty() && !showingSolution) {
            val newInputs = inputs.toMutableList()
            newInputs[currentIndex] = input.last().toString()
            inputs = newInputs

            if (newInputs.all { it.isNotEmpty() }) {
                onAnswer(newInputs.joinToString(","))
            } else {
                // Advance to the next editable cell that's still empty — skip clues and any
                // cells the user has already filled.
                for (i in 1..totalCells) {
                    val checkIndex = (currentIndex + i) % totalCells
                    if (uiState.initialValues[checkIndex] == null && newInputs[checkIndex].isEmpty()) {
                        currentIndex = checkIndex
                        break
                    }
                }
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
                                modifier = Modifier.hoverHand(),
                                selected = true,
                                onClick = { onTokenClick(index) },
                                label = { Text(token.displayValue) },
                            )
                        }
                        is ExpressionToken.OperatorToken -> {
                            AssistChip(
                                modifier = Modifier.hoverHand(),
                                onClick = { onTokenClick(index) },
                                label = { MathText(token.displayValue) },
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.width(8.dp))
        IconButton(
            modifier = Modifier.hoverHand(),
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
                    .hoverHand(),
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
    val operators = remember { listOf("+", "-", "*", "/", "(", ")") }
    FlowRow(
        modifier = modifier.padding(horizontal = 16.dp),
        maxItemsInEachRow = 3,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        operators.forEach { operator ->
            CircleButton(
                onClick = { onOperatorClick(operator) },
                value = operator.replace("*", "\u00D7").replace("/", "\u00F7"),
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
    val progressColor = Primary

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
            color = Primary,
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

    val showAnswerOptions = uiState.phase == VisualMemoryGame.Phase.ANSWERING ||
        uiState.phase == VisualMemoryGame.Phase.GAME_OVER
    AnimatedVisibility(
        visible = showAnswerOptions,
        enter = fadeIn(animationSpec = tween(250)),
        exit = fadeOut(animationSpec = tween(200)),
        modifier = Modifier.align(Alignment.CenterHorizontally),
    ) {
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
                VisualMemoryUiState.CellType.CURRENT_TARGET -> PrimaryContainer
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
                        color = OnPrimaryContainer,
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
    val cellColor = when (cell.type) {
        GhostGridUiState.CellType.ACTIVE -> Primary
        GhostGridUiState.CellType.TAPPED -> Primary
        GhostGridUiState.CellType.WRONG -> MaterialTheme.colorScheme.errorContainer
        GhostGridUiState.CellType.MISSED -> SuccessGreen.copy(alpha = 0.15f)
        GhostGridUiState.CellType.INACTIVE -> MaterialTheme.colorScheme.surfaceVariant
    }
    val border = if (cell.type == GhostGridUiState.CellType.MISSED) {
        BorderStroke(2.dp, SuccessGreen)
    } else {
        null
    }
    Card(
        onClick = onClick,
        enabled = isClickable,
        modifier = modifier.hoverHand(isClickable),
        colors = CardDefaults.cardColors(
            containerColor = cellColor,
            disabledContainerColor = cellColor,
        ),
        border = border,
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
                containerColor = PrimaryContainer,
            ),
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
        textAlign = TextAlign.Center,
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

    Button(
        onClick = { onAnswer("submit") },
        enabled = !uiState.isSubmitted,
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .alpha(if (uiState.isSubmitted) 0f else 1f)
            .hoverHand(),
    ) {
        Text(stringResource(Res.string.button_done))
    }
}

@Composable
private fun ColorConfusionCell(
    cell: ColorConfusionUiState.Cell,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val targetContainerColor = when (cell.feedback) {
        ColorConfusionUiState.CellFeedback.CORRECT_SELECTED -> SuccessGreen.copy(alpha = 0.15f)
        ColorConfusionUiState.CellFeedback.WRONG_SELECTED -> MaterialTheme.colorScheme.errorContainer
        ColorConfusionUiState.CellFeedback.MISSED -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.surface
    }
    val containerColor by animateColorAsState(
        targetValue = targetContainerColor,
        animationSpec = tween(250),
        label = "colorConfusionContainer",
    )
    val border = when {
        cell.feedback == ColorConfusionUiState.CellFeedback.CORRECT_SELECTED -> BorderStroke(2.dp, SuccessGreen)
        cell.feedback == ColorConfusionUiState.CellFeedback.MISSED -> BorderStroke(2.dp, MaterialTheme.colorScheme.error)
        cell.isSelected && cell.feedback == ColorConfusionUiState.CellFeedback.NONE -> BorderStroke(2.dp, Primary)
        else -> null
    }

    val isInteractive = cell.feedback == ColorConfusionUiState.CellFeedback.NONE
    Card(
        onClick = onClick,
        enabled = isInteractive,
        modifier = modifier.hoverHand(isInteractive),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            disabledContainerColor = containerColor,
        ),
        border = border,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                text = cell.word.localizedName(),
                style = MaterialTheme.typography.titleMedium,
                color = cell.fontColor.composeColor,
            )
        }
    }
}

// --- Orbit Tracker Game Composables ---

@Composable
private fun ColumnScope.OrbitTrackerContent(
    uiState: OrbitTrackerUiState,
    onAnswer: (String) -> Unit,
) {
    val isHighlighting = uiState.phase == OrbitTrackerGame.Phase.HIGHLIGHTING
    val isAnswering = uiState.phase == OrbitTrackerGame.Phase.ANSWERING

    val instructionText = when {
        isHighlighting -> stringResource(Res.string.game_remember_targets)
        isAnswering -> stringResource(Res.string.game_tap_original_targets)
        else -> ""
    }
    AnimatedContent(
        targetState = instructionText,
        transitionSpec = {
            fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(150))
        },
        modifier = Modifier.align(Alignment.CenterHorizontally),
        label = "orbitTrackerInstruction",
    ) { text ->
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            minLines = 1,
        )
    }
    Spacer(Modifier.height(8.dp))

    val errorColor = MaterialTheme.colorScheme.error
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    val outlineColor = MaterialTheme.colorScheme.outline
    val primaryColor = Primary

    Box(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .widthIn(max = 400.dp)
            .aspectRatio(1f)
            .align(Alignment.CenterHorizontally),
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(uiState.phase) {
                    if (!isAnswering) return@pointerInput
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            if (event.type == PointerEventType.Press) {
                                val position = event.changes.firstOrNull()?.position ?: continue
                                val normalizedX = position.x / size.width
                                val normalizedY = position.y / size.height
                                val ballRadius = 0.04f

                                // Find the closest ball within tap range
                                var closestIndex = -1
                                var closestDist = Float.MAX_VALUE
                                uiState.balls.forEachIndexed { index, ball ->
                                    val dx = ball.x - normalizedX
                                    val dy = ball.y - normalizedY
                                    val dist = kotlin.math.sqrt(dx * dx + dy * dy)
                                    if (dist < ballRadius * 3 && dist < closestDist) {
                                        closestDist = dist
                                        closestIndex = index
                                    }
                                }
                                if (closestIndex >= 0) {
                                    onAnswer(closestIndex.toString())
                                }
                            }
                        }
                    }
                },
        ) {
            // Draw arena border
            drawRect(
                color = outlineColor,
                style = Stroke(width = 2.dp.toPx()),
            )

            val ballRadiusPx = 0.04f * size.width

            uiState.balls.forEach { ball ->
                val cx = ball.x * size.width
                val cy = ball.y * size.height
                val center = Offset(cx, cy)

                val isGameOver = uiState.phase == OrbitTrackerGame.Phase.GAME_OVER

                val color = when {
                    // Feedback states
                    ball.feedback == OrbitTrackerUiState.BallFeedback.CORRECT_SELECTED && isGameOver -> SuccessGreen
                    ball.feedback == OrbitTrackerUiState.BallFeedback.CORRECT_SELECTED -> primaryColor
                    ball.feedback == OrbitTrackerUiState.BallFeedback.WRONG_SELECTED -> errorColor
                    ball.feedback == OrbitTrackerUiState.BallFeedback.MISSED -> SuccessGreen
                    // Highlight phase: targets are blue
                    isHighlighting && ball.isTarget -> primaryColor
                    // Default dark grey
                    else -> onSurfaceVariantColor
                }

                drawCircle(
                    color = color,
                    radius = ballRadiusPx,
                    center = center,
                )

                // Draw outline for missed targets
                if (ball.feedback == OrbitTrackerUiState.BallFeedback.MISSED) {
                    drawCircle(
                        color = SuccessGreen,
                        radius = ballRadiusPx + 3.dp.toPx(),
                        center = center,
                        style = Stroke(width = 2.dp.toPx()),
                    )
                }

                // Draw selection ring during answering
                if (ball.isSelected && ball.feedback == OrbitTrackerUiState.BallFeedback.NONE) {
                    drawCircle(
                        color = primaryColor,
                        radius = ballRadiusPx + 3.dp.toPx(),
                        center = center,
                        style = Stroke(width = 2.dp.toPx()),
                    )
                }
            }
        }
    }
}

// --- Flash Crowd Game Composables ---

@Composable
private fun ColumnScope.FlashCrowdContent(
    uiState: FlashCrowdUiState,
    onAnswer: (String) -> Unit,
) {
    key(uiState.roundKey) {
        var showingDots by remember { mutableStateOf(true) }
        var visible by remember { mutableStateOf(true) }
        val alpha by animateFloatAsState(
            targetValue = if (visible) 1f else 0f,
            animationSpec = tween(200),
            label = "flashCrowdAlpha",
        )

        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(750)
            visible = false
            kotlinx.coroutines.delay(200)
            showingDots = false
            visible = true
        }

        if (showingDots) {
            FlashCrowdDotsRow(
                uiState,
                FlashCrowdBlue,
                FlashCrowdYellow,
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .alpha(alpha),
            )
        } else {
            Text(
                text = stringResource(Res.string.game_flash_crowd_which_more),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .alpha(alpha),
            )
            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .widthIn(max = 400.dp)
                    .align(Alignment.CenterHorizontally)
                    .alpha(alpha),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Button(
                    onClick = { onAnswer("left") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FlashCrowdBlue,
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .hoverHand(),
                ) {
                    Text(
                        text = stringResource(Res.string.game_flash_crowd_blue),
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
                Button(
                    onClick = { onAnswer("right") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FlashCrowdYellow,
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .hoverHand(),
                ) {
                    Text(
                        text = stringResource(Res.string.game_flash_crowd_yellow),
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            }
        }
    }
}

@Composable
private fun FlashCrowdDotsRow(
    uiState: FlashCrowdUiState,
    blueColor: androidx.compose.ui.graphics.Color,
    yellowColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .widthIn(max = 400.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Canvas(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f),
        ) {
            uiState.leftDots.forEach { dot ->
                drawCircle(
                    color = blueColor,
                    radius = dot.radius * size.width,
                    center = Offset(dot.x * size.width, dot.y * size.height),
                )
            }
        }
        Canvas(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f),
        ) {
            uiState.rightDots.forEach { dot ->
                drawCircle(
                    color = yellowColor,
                    radius = dot.radius * size.width,
                    center = Offset(dot.x * size.width, dot.y * size.height),
                )
            }
        }
    }
}
