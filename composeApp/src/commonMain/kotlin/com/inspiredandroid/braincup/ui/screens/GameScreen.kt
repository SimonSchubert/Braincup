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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.button_backspace
import braincup.composeapp.generated.resources.button_give_up
import braincup.composeapp.generated.resources.game_fill_grid
import braincup.composeapp.generated.resources.game_follow_directions
import braincup.composeapp.generated.resources.game_goal
import braincup.composeapp.generated.resources.game_highest_value
import braincup.composeapp.generated.resources.game_tap_numbers
import braincup.composeapp.generated.resources.game_what_comes_next
import com.inspiredandroid.braincup.games.*
import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.Shape
import com.inspiredandroid.braincup.ui.components.CircleButton
import com.inspiredandroid.braincup.ui.components.GameScaffold
import com.inspiredandroid.braincup.ui.components.NumberPad
import com.inspiredandroid.braincup.ui.components.ShapeCanvas
import com.inspiredandroid.braincup.ui.components.ShapeCanvasButton
import org.jetbrains.compose.resources.stringResource

/**
 * Represents a token in the Sherlock Calculation expression builder.
 */
sealed class ExpressionToken(val displayValue: String) {
    data class NumberToken(val value: Int, val originalIndex: Int) : ExpressionToken(value.toString())
    data class OperatorToken(val operator: String) : ExpressionToken(operator)
}

@Composable
fun GameScreen(
    game: Game,
    timeRemaining: Long,
    onAnswer: (String) -> Unit,
    onGiveUp: () -> Unit,
    onBack: () -> Unit,
) {
    GameScaffold(onBack = onBack) {
        TimeProgressIndicator(
            progress = timeRemaining / 60000f,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        )

        Spacer(Modifier.weight(1f))

        when (game) {
            is AnomalyPuzzleGame -> AnomalyPuzzleContent(game, onAnswer)
            is PathFinderGame -> PathFinderContent(game, onAnswer)
            is ColorConfusionGame -> ColorConfusionContent(game, onAnswer)
            is VisualMemoryGame -> VisualMemoryContent(game, onAnswer)
            is MentalCalculationGame -> MentalCalculationContent(game, onAnswer)
            is SherlockCalculationGame -> SherlockCalculationContent(game, onAnswer, onGiveUp)
            is ChainCalculationGame -> ChainCalculationContent(game, onAnswer)
            is FractionCalculationGame -> FractionCalculationContent(game, onAnswer)
            is ValueComparisonGame -> ValueComparisonContent(game, onAnswer)
            is GridSolverGame -> GridSolverContent(game, onAnswer)
            is PatternSequenceGame -> PatternSequenceContent(game, onAnswer)
        }

        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun ColumnScope.MentalCalculationContent(
    game: MentalCalculationGame,
    onAnswer: (String) -> Unit,
) {
    Text(
        text = game.calculation,
        style = MaterialTheme.typography.displaySmall,
        modifier = Modifier.align(Alignment.CenterHorizontally),
    )
    Spacer(Modifier.height(16.dp))
    NumberPad(onInputChange = { input ->
        if (game.getNumberLength() == input.length) {
            onAnswer(input)
        }
    })
}

@Composable
private fun ColumnScope.ChainCalculationContent(
    game: ChainCalculationGame,
    onAnswer: (String) -> Unit,
) {
    Text(
        text = "${game.calculation} = ?",
        style = MaterialTheme.typography.displaySmall,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(horizontal = 16.dp),
    )
    Spacer(Modifier.height(16.dp))
    NumberPad(onInputChange = { input ->
        if (game.isCorrect(input)) {
            onAnswer(input)
        }
    })
}

@Composable
private fun ColumnScope.ColorConfusionContent(
    game: ColorConfusionGame,
    onAnswer: (String) -> Unit,
) {
    // Show actual shape with color
    ShapeCanvas(
        figure = Figure(game.displayedShape, game.displayedColor),
        modifier = Modifier.size(200.dp).align(Alignment.CenterHorizontally),
    )
    Spacer(Modifier.height(16.dp))

    // Point assignments
    Text(
        text = "${game.answerShape.displayName} = ${game.shapePoints}",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.align(Alignment.CenterHorizontally),
    )
    Text(
        text = "${game.answerColor.displayName} = ${game.colorPoints}",
        style = MaterialTheme.typography.bodyLarge,
        color = game.stringColor.composeColor,
        modifier = Modifier.align(Alignment.CenterHorizontally),
    )
    Spacer(Modifier.height(16.dp))

    // Answer buttons
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.align(Alignment.CenterHorizontally),
    ) {
        game.possibleAnswers.forEach { answer ->
            CircleButton(onClick = { onAnswer(answer) }, value = answer)
        }
    }
}

@Composable
private fun ColumnScope.SherlockCalculationContent(
    game: SherlockCalculationGame,
    onAnswer: (String) -> Unit,
    onGiveUp: () -> Unit,
) {
    // Use key to reset state when game.result changes (new round)
    key(game.result) {
        var usedNumberIndices by remember { mutableStateOf(emptySet<Int>()) }
        var expressionTokens by remember { mutableStateOf(emptyList<ExpressionToken>()) }

        fun checkAnswer() {
            val expr = expressionTokens.joinToString("") { it.displayValue }
            if (game.isCorrect(expr)) {
                onAnswer(expr)
            }
        }

        // Goal display
        Text(
            text = stringResource(Res.string.game_goal, game.result),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(16.dp))

        // Expression row
        ExpressionRow(
            tokens = expressionTokens,
            onTokenClick = { tokenIndex ->
                val token = expressionTokens[tokenIndex]
                expressionTokens = expressionTokens.toMutableList().apply { removeAt(tokenIndex) }
                if (token is ExpressionToken.NumberToken) {
                    usedNumberIndices = usedNumberIndices - token.originalIndex
                }
            },
            onBackspace = {
                if (expressionTokens.isNotEmpty()) {
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

        // Available numbers
        AvailableNumbersRow(
            numbers = game.numbers,
            usedIndices = usedNumberIndices,
            onNumberClick = { value, index ->
                expressionTokens = expressionTokens + ExpressionToken.NumberToken(value, index)
                usedNumberIndices = usedNumberIndices + index
                checkAnswer()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(12.dp))

        // Operators
        OperatorRow(
            onOperatorClick = { operator ->
                expressionTokens = expressionTokens + ExpressionToken.OperatorToken(operator)
            },
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(16.dp))

        // Give up button
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

@Composable
private fun ColumnScope.FractionCalculationContent(
    game: FractionCalculationGame,
    onAnswer: (String) -> Unit,
) {
    Text(
        text = game.calculation,
        style = MaterialTheme.typography.displaySmall,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(horizontal = 16.dp),
    )
    Spacer(Modifier.height(16.dp))
    NumberPad(onInputChange = { input ->
        if (game.isCorrect(input) || input.length >= 4) {
            onAnswer(input)
        }
    })
}

@Composable
private fun ColumnScope.AnomalyPuzzleContent(
    game: AnomalyPuzzleGame,
    onAnswer: (String) -> Unit,
) {
    val chunkSize = when {
        game.figures.size >= 16 -> 4
        game.figures.size >= 9 -> 3
        else -> 2
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .widthIn(max = 80.dp * chunkSize),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        game.figures.chunked(chunkSize).forEachIndexed { y, figures ->
            Row {
                figures.forEachIndexed { x, figure ->
                    val index = y * chunkSize + x
                    ShapeCanvasButton(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(8.dp),
                        figure = figure,
                        onClick = { onAnswer("${index + 1}") },
                    )
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.PathFinderContent(
    game: PathFinderGame,
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
        game.directions.forEach {
            ShapeCanvas(
                figure = it.figure,
                modifier = Modifier.size(32.dp),
            )
        }
    }
    Spacer(Modifier.height(16.dp))

    // 4x4 grid with orange start position and grey cells
    val startFigure = Figure(Shape.SQUARE, Color.ORANGE)
    val blankFigure = Figure(Shape.SQUARE, Color.GREY_LIGHT)

    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .widthIn(max = 64.dp * 4),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        for (row in 0 until 4) {
            Row {
                for (col in 0 until 4) {
                    val index = row * 4 + col + 1
                    val isStart = row == game.startY && col == game.startX
                    ShapeCanvasButton(
                        figure = if (isStart) startFigure else blankFigure,
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
    game: ValueComparisonGame,
    onAnswer: (String) -> Unit,
) {
    Text(
        text = stringResource(Res.string.game_highest_value),
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.align(Alignment.CenterHorizontally),
    )
    Spacer(Modifier.height(16.dp))

    game.answers.forEachIndexed { index, answer ->
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
    game: GridSolverGame,
    onAnswer: (String) -> Unit,
) {
    val totalCells = game.size() * game.size()
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
    Spacer(Modifier.height(8.dp))

    // Display the grid with row and column sums (hidden answers)
    Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
        game.entries.forEachIndexed { rowIndex, row ->
            Row {
                row.forEachIndexed { colIndex, _ ->
                    val cellIndex = rowIndex * game.size() + colIndex
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
                            text = "=${game.resultsY[rowIndex]}",
                            style = MaterialTheme.typography.titleSmall,
                        )
                    }
                }
            }
        }
        // Column sums row
        Row {
            game.resultsX.forEach { sum ->
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

    Spacer(Modifier.height(8.dp))

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
    game: VisualMemoryGame,
    onAnswer: (String) -> Unit,
) {
    val memorizeDurationSeconds = (VisualMemoryGame.MEMORIZE_DURATION_MILLIS / 1000).toInt()

    // Key on round to reset state when round changes
    key(game.round) {
        var countdown by remember { mutableStateOf(memorizeDurationSeconds) }
        var phase by remember { mutableStateOf(game.phase) }

        // Countdown timer during memorization phase
        LaunchedEffect(game.round) {
            countdown = memorizeDurationSeconds
            phase = VisualMemoryGame.Phase.MEMORIZING

            while (countdown > 0) {
                kotlinx.coroutines.delay(1000L)
                countdown--
            }

            // Transition to answer phase
            game.startAnswerPhase()
            phase = VisualMemoryGame.Phase.ANSWERING
        }

        // Round indicator
        Spacer(Modifier.height(8.dp))

        // Phase indicator with countdown or target shape
        if (phase == VisualMemoryGame.Phase.MEMORIZING) {
            Text(
                text = "$countdown",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
        Spacer(Modifier.height(16.dp))

        // 3x3 Grid - key on currentGuessIndex to recompose when guess advances
        key(game.currentGuessIndex) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .widthIn(max = 80.dp * 3),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                for (row in 0 until 3) {
                    Row {
                        for (col in 0 until 3) {
                            val position = row * 3 + col
                            VisualMemoryCell(
                                game = game,
                                position = position,
                                phase = phase,
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

        Spacer(Modifier.height(24.dp))

        // Answer options (only visible during answer phase)
        if (phase == VisualMemoryGame.Phase.ANSWERING) {
            // 3x3 grid of answer options (shuffled) - key on currentGuessIndex
            key(game.currentGuessIndex) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .widthIn(max = 72.dp * 3),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    game.shuffledAnswerOptions.chunked(3).forEach { rowFigures ->
                        Row {
                            rowFigures.forEach { figure ->
                                // Find the original index in availableFigures for the answer
                                val figureIndex = game.availableFigures.indexOf(figure)
                                val isAlreadyGuessed = game.isFigureRevealed(figureIndex)
                                ShapeCanvasButton(
                                    figure = figure,
                                    onClick = { onAnswer(figureIndex.toString()) },
                                    enabled = !isAlreadyGuessed,
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
}

@Composable
private fun VisualMemoryCell(
    game: VisualMemoryGame,
    position: Int,
    phase: VisualMemoryGame.Phase,
    modifier: Modifier = Modifier,
) {
    val figure = game.getFigureAt(position)
    val hasPlacedFigure = game.hasPlacedFigure(position)
    val isEmptyCell = !hasPlacedFigure
    // Current target shows "?" during answer phase
    val isCurrentTarget = phase == VisualMemoryGame.Phase.ANSWERING &&
        position == game.getCurrentTargetPosition()
    // Hidden cells have a figure, are not revealed, and are not the current target
    val isHiddenCell = phase == VisualMemoryGame.Phase.ANSWERING &&
        hasPlacedFigure &&
        position !in game.revealedPositions &&
        !isCurrentTarget

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = when {
                isCurrentTarget -> MaterialTheme.colorScheme.primaryContainer
                isHiddenCell -> MaterialTheme.colorScheme.surfaceVariant
                isEmptyCell -> MaterialTheme.colorScheme.surfaceVariant
                else -> MaterialTheme.colorScheme.surface
            },
        ),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            when {
                isCurrentTarget -> {
                    Text(
                        text = "?",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
                figure != null -> {
                    ShapeCanvas(
                        figure = figure,
                        modifier = Modifier.fillMaxSize().padding(8.dp),
                    )
                }
            }
        }
    }
}

// --- Pattern Sequence Game Composables ---

@Composable
private fun ColumnScope.PatternSequenceContent(
    game: PatternSequenceGame,
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
        game.sequence.forEach { figure ->
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
        game.options.chunked(2).forEachIndexed { y, rowOptions ->
            Row {
                rowOptions.forEachIndexed { x, figure ->
                    val index = y * 2 + x
                    ShapeCanvasButton(
                        figure = figure,
                        onClick = { onAnswer(index.toString()) },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(8.dp),
                    )
                }
            }
        }
    }
}
