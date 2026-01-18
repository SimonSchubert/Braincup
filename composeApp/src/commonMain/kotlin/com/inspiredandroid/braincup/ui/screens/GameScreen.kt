package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.games.*
import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.Shape
import com.inspiredandroid.braincup.games.tools.getFigure
import com.inspiredandroid.braincup.games.tools.getName
import com.inspiredandroid.braincup.ui.components.GameScaffold
import com.inspiredandroid.braincup.ui.components.NumberPad
import com.inspiredandroid.braincup.ui.components.OptionButton
import com.inspiredandroid.braincup.ui.components.ShapeCanvas
import com.inspiredandroid.braincup.ui.components.ShapeCanvasButton
import com.inspiredandroid.braincup.ui.components.toComposeColor

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
    onBack: () -> Unit
) {
    GameScaffold(onBack = onBack) {

        TimeProgressIndicator(
            progress = timeRemaining / 60000f,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Spacer(Modifier.weight(1f))

        when (game) {
            is MentalCalculationGame -> MentalCalculationContent(game, onAnswer)
            is ChainCalculationGame -> ChainCalculationContent(game, onAnswer)
            is ColorConfusionGame -> ColorConfusionContent(game, onAnswer)
            is SherlockCalculationGame -> SherlockCalculationContent(game, onAnswer, onGiveUp)
            is FractionCalculationGame -> FractionCalculationContent(game, onAnswer)
            is AnomalyPuzzleGame -> AnomalyPuzzleContent(game, onAnswer)
            is PathFinderGame -> PathFinderContent(game, onAnswer)
            is ValueComparisonGame -> ValueComparisonContent(game, onAnswer)
            is RiddleGame -> RiddleContent(game, onAnswer)
            is GridSolverGame -> GridSolverContent(game, onAnswer)
        }

        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun ColumnScope.MentalCalculationContent(
    game: MentalCalculationGame,
    onAnswer: (String) -> Unit
) {
    Text(
        text = game.calculation,
        style = MaterialTheme.typography.displaySmall,
        modifier = Modifier.align(Alignment.CenterHorizontally)
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
    onAnswer: (String) -> Unit
) {
    Text(
        text = "${game.calculation} = ?",
        style = MaterialTheme.typography.displaySmall,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(horizontal = 16.dp)
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
    onAnswer: (String) -> Unit
) {
    // Show actual shape with color
    ShapeCanvas(
        size = 96.dp,
        figure = Figure(game.displayedShape, game.displayedColor),
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )
    Spacer(Modifier.height(16.dp))

    // Point assignments
    Text(
        text = "${game.shapePoints} = ${game.answerShape.getName()}",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )
    Text(
        text = "${game.colorPoints} = ${game.answerColor.getName()}",
        style = MaterialTheme.typography.bodyLarge,
        color = game.stringColor.toComposeColor(),
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )
    Spacer(Modifier.height(16.dp))

    // Answer buttons
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.align(Alignment.CenterHorizontally)
    ) {
        game.getPossibleAnswers().forEach { answer ->
            Button(onClick = { onAnswer(answer) }) {
                Text(answer)
            }
        }
    }
}

@Composable
private fun ColumnScope.SherlockCalculationContent(
    game: SherlockCalculationGame,
    onAnswer: (String) -> Unit,
    onGiveUp: () -> Unit
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
            text = "Goal: ${game.result}",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
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
            modifier = Modifier.align(Alignment.CenterHorizontally)
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
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(12.dp))

        // Operators
        OperatorRow(
            onOperatorClick = { operator ->
                expressionTokens = expressionTokens + ExpressionToken.OperatorToken(operator)
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(16.dp))

        // Give up button
        TextButton(
            onClick = onGiveUp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Give Up")
        }
    }
}

@Composable
private fun ColumnScope.FractionCalculationContent(
    game: FractionCalculationGame,
    onAnswer: (String) -> Unit
) {
    Text(
        text = game.calculation,
        style = MaterialTheme.typography.displaySmall,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(horizontal = 16.dp)
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
    onAnswer: (String) -> Unit
) {
    val chunkSize = when {
        game.figures.size >= 16 -> 4
        game.figures.size >= 9 -> 3
        else -> 2
    }

    game.figures.chunked(chunkSize).forEachIndexed { y, figures ->
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            figures.forEachIndexed { x, figure ->
                val index = y * chunkSize + x
                ShapeCanvasButton(
                    size = 48.dp,
                    modifier = Modifier.padding(8.dp),
                    figure = figure,
                    onClick = { onAnswer("${index + 1}") }
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.PathFinderContent(
    game: PathFinderGame,
    onAnswer: (String) -> Unit
) {
    Text(
        text = "Follow the directions:",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )
    Spacer(Modifier.height(8.dp))
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        game.directions.forEach {
            ShapeCanvas(32.dp,
                it.getFigure()
            )
        }
    }
    Spacer(Modifier.height(16.dp))

    // 4x4 grid with orange start position and grey cells
    val startFigure = Figure(Shape.SQUARE, Color.ORANGE)
    val blankFigure = Figure(Shape.SQUARE, Color.GREY_LIGHT)

    for (row in 0 until 4) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            for (col in 0 until 4) {
                val index = row * 4 + col + 1
                val isStart = row == game.startY && col == game.startX
                ShapeCanvasButton(
                    size = 56.dp,
                    figure = if (isStart) startFigure else blankFigure,
                    onClick = { onAnswer(index.toString()) },
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.ValueComparisonContent(
    game: ValueComparisonGame,
    onAnswer: (String) -> Unit
) {
    Text(
        text = "Which has the highest value?",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )
    Spacer(Modifier.height(16.dp))

    game.answers.forEachIndexed { index, answer ->
        OptionButton(
            text = answer,
            onClick = { onAnswer((index + 1).toString()) }
        )
    }
}

@Composable
private fun ColumnScope.RiddleContent(
    game: RiddleGame,
    onAnswer: (String) -> Unit
) {
    Text(
        text = game.quest,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(horizontal = 16.dp)
    )
    Spacer(Modifier.height(16.dp))

    var answer by remember { mutableStateOf("") }
    OutlinedTextField(
        value = answer,
        onValueChange = { answer = it },
        label = { Text("Your answer") },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = { onAnswer(answer) }
        ),
        modifier = Modifier.padding(horizontal = 16.dp)
    )
    Spacer(Modifier.height(8.dp))
    Button(
        onClick = { onAnswer(answer) },
        modifier = Modifier.align(Alignment.CenterHorizontally)
    ) {
        Text("Submit")
    }
}

@Composable
private fun ColumnScope.GridSolverContent(
    game: GridSolverGame,
    onAnswer: (String) -> Unit
) {
    val totalCells = game.size() * game.size()
    var inputs by remember { mutableStateOf(List(totalCells) { "" }) }
    var currentIndex by remember { mutableStateOf(0) }

    Text(
        text = "Fill the grid so rows and columns match the sums",
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(horizontal = 16.dp)
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
                            containerColor = if (isCurrentCell)
                                MaterialTheme.colorScheme.secondaryContainer
                            else
                                MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = cellValue.ifEmpty { "?" },
                                style = MaterialTheme.typography.titleMedium
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
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "=${game.resultsY[rowIndex]}",
                            style = MaterialTheme.typography.titleSmall
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
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "=$sum",
                            style = MaterialTheme.typography.titleSmall
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
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (tokens.isEmpty()) {
            Text(
                text = "Tap numbers to build expression",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tokens.forEachIndexed { index, token ->
                    when (token) {
                        is ExpressionToken.NumberToken -> {
                            FilterChip(
                                selected = true,
                                onClick = { onTokenClick(index) },
                                label = { Text(token.displayValue) }
                            )
                        }
                        is ExpressionToken.OperatorToken -> {
                            AssistChip(
                                onClick = { onTokenClick(index) },
                                label = { Text(token.displayValue) },
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.width(8.dp))
        IconButton(
            onClick = onBackspace,
            enabled = tokens.isNotEmpty()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Backspace,
                contentDescription = "Backspace"
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
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        numbers.forEachIndexed { index, value ->
            val isUsed = index in usedIndices
            FilledTonalButton(
                onClick = { onNumberClick(value, index) },
                enabled = !isUsed,
                modifier = Modifier.size(56.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun OperatorRow(
    onOperatorClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val operators = listOf("+", "-", "*", "/", "(", ")")
    Row(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        operators.forEach { operator ->
            OutlinedButton(
                onClick = { onOperatorClick(operator) },
                modifier = Modifier.size(48.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = operator,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun TimeProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val progressColor = MaterialTheme.colorScheme.primary

    Canvas(
        modifier = modifier.height(12.dp)
    ) {
        val cornerRadius = CornerRadius(size.height / 2, size.height / 2)

        // Draw track (background)
        drawRoundRect(
            color = trackColor,
            size = size,
            cornerRadius = cornerRadius
        )

        // Draw progress
        val progressWidth = size.width * progress.coerceIn(0f, 1f)
        if (progressWidth > 0f) {
            drawRoundRect(
                color = progressColor,
                size = Size(progressWidth, size.height),
                cornerRadius = cornerRadius
            )
        }
    }
}
