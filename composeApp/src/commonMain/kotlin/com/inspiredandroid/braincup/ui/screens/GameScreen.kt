package com.inspiredandroid.braincup.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.games.DigitMemoryGame
import com.inspiredandroid.braincup.games.GhostGridGame
import com.inspiredandroid.braincup.games.OrbitTrackerGame
import com.inspiredandroid.braincup.games.SpotTheNewGame
import com.inspiredandroid.braincup.games.VisualMemoryGame
import com.inspiredandroid.braincup.games.minichess.PieceType
import com.inspiredandroid.braincup.games.tools.Calculator
import com.inspiredandroid.braincup.games.tools.composeColor
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.localizedName
import com.inspiredandroid.braincup.ui.theme.LightsOutOffColor
import com.inspiredandroid.braincup.ui.theme.LightsOutOnColor
import com.inspiredandroid.braincup.ui.theme.OnPrimaryContainer
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.PrimaryContainer
import com.inspiredandroid.braincup.ui.theme.SelectedTileFaceDark
import com.inspiredandroid.braincup.ui.theme.SelectedTileFaceLight
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import com.inspiredandroid.braincup.ui.theme.SuccessGreenSoft
import com.inspiredandroid.braincup.ui.theme.UnselectedTileFaceDark
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.ceil
import kotlin.math.sqrt
import kotlin.time.Duration.Companion.milliseconds
import androidx.compose.ui.graphics.Color as ComposeColor

internal val FlashCrowdBlue = ComposeColor(0xFF4285F4)
internal val FlashCrowdYellow = ComposeColor(0xFFFBBC04)

internal val FlashCrowdBlueSide = FlashCrowdBlue.darken(0.7f)
internal val FlashCrowdBlueBottom = FlashCrowdBlue.darken(0.5f)

// Yellow needs a lighter darken than the prism default (0.7/0.5) so the facets read on a light hue.
internal val FlashCrowdYellowSide = FlashCrowdYellow.darken(0.85f)
internal val FlashCrowdYellowBottom = FlashCrowdYellow.darken(0.7f)

private val ChessHaloDeltas: List<Pair<Float, Float>> = listOf(
    -1f to -1f,
    0f to -1f,
    1f to -1f,
    -1f to 0f,
    1f to 0f,
    -1f to 1f,
    0f to 1f,
    1f to 1f,
)

private val ChessOutlineFilter = ColorFilter.tint(ComposeColor.Black)

@Composable
fun GameScreen(
    gameUiState: GameUiState,
    timeRemaining: Long,
    elapsedTime: Long = 0L,
    onAnswer: (String) -> Unit,
    onGiveUp: () -> Unit,
    onBack: () -> Unit,
) {
    val progressBar: (@Composable () -> Unit)? = when {
        gameUiState is VisualMemoryUiState &&
            gameUiState.phase == VisualMemoryGame.Phase.MEMORIZING -> {
            val round = gameUiState.round
            val bar: @Composable () -> Unit = {
                val totalMillis = VisualMemoryGame.memorizeDurationMillis(round)
                var progress by remember(round) { mutableStateOf(1f) }
                LaunchedEffect(round) {
                    val startNanos = withFrameNanos { it }
                    while (progress > 0f) {
                        val nowNanos = withFrameNanos { it }
                        val elapsedMillis = (nowNanos - startNanos) / 1_000_000f
                        progress = (1f - elapsedMillis / totalMillis).coerceAtLeast(0f)
                    }
                }
                TimeProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
            bar
        }
        gameUiState is SpotTheNewUiState &&
            gameUiState.phase == SpotTheNewGame.Phase.MEMORIZING -> {
            val bar: @Composable () -> Unit = {
                val totalMillis = SpotTheNewGame.MEMORIZE_MILLIS.toFloat()
                var progress by remember { mutableStateOf(1f) }
                LaunchedEffect(Unit) {
                    val startNanos = withFrameNanos { it }
                    while (progress > 0f) {
                        val nowNanos = withFrameNanos { it }
                        val elapsedMillis = (nowNanos - startNanos) / 1_000_000f
                        progress = (1f - elapsedMillis / totalMillis).coerceAtLeast(0f)
                    }
                }
                TimeProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
            bar
        }
        gameUiState is SpotTheNewUiState -> null
        gameUiState is VisualMemoryUiState ||
            gameUiState is GhostGridUiState ||
            gameUiState is OrbitTrackerUiState ||
            gameUiState is MiniChessUiState ||
            gameUiState is LightsOutUiState ||
            gameUiState is SlidingPuzzleUiState -> null
        gameUiState is SchulteTableUiState -> {
            val bar: @Composable () -> Unit = {
                StopwatchDisplay(
                    elapsedMillis = elapsedTime,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
            bar
        }
        gameUiState is FlagsUiState -> {
            val bar: @Composable () -> Unit = {
                TimeProgressIndicator(
                    progress = timeRemaining / GameController.FLAGS_ROUND_TIME_MILLIS.toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
            bar
        }
        else -> {
            val bar: @Composable () -> Unit = {
                TimeProgressIndicator(
                    progress = timeRemaining / 60000f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
            bar
        }
    }
    GameScaffold(
        onBack = onBack,
        progressBar = progressBar,
        fillContent = gameUiState is FlagsUiState,
    ) {
        // Force LTR for gameplay content: math expressions, digit sequences, directional
        // arrows, and asymmetric shapes carry semantic meaning that breaks under RTL
        // mirroring. Bidi text rendering inside Text composables is unaffected.
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            when (gameUiState) {
                is MentalCalculationUiState -> MentalCalculationContent(gameUiState, onAnswer)
                is ChainCalculationUiState -> ChainCalculationContent(gameUiState, onAnswer, onGiveUp)
                is FractionCalculationUiState -> FractionCalculationContent(gameUiState, onAnswer, onGiveUp)
                is ColoredShapesUiState -> ColoredShapesContent(gameUiState, onAnswer)
                is SherlockCalculationUiState -> SherlockCalculationContent(gameUiState, onAnswer, onGiveUp)
                is ValueComparisonUiState -> ValueComparisonContent(gameUiState, onAnswer)
                is AnomalyPuzzleUiState -> AnomalyPuzzleContent(gameUiState, onAnswer)
                is PathFinderUiState -> PathFinderContent(gameUiState, onAnswer)
                is MiniSudokuUiState -> MiniSudokuContent(gameUiState, onAnswer)
                is LightsOutUiState -> LightsOutContent(gameUiState, onAnswer, onGiveUp)
                is SlidingPuzzleUiState -> SlidingPuzzleContent(gameUiState, onAnswer, onGiveUp)
                is SchulteTableUiState -> SchulteTableContent(gameUiState, onAnswer)
                is PatternSequenceUiState -> PatternSequenceContent(gameUiState, onAnswer)
                is VisualMemoryUiState -> VisualMemoryContent(gameUiState, onAnswer)
                is SpotTheNewUiState -> SpotTheNewContent(gameUiState, onAnswer)
                is GhostGridUiState -> GhostGridContent(gameUiState, onAnswer)
                is ColorConfusionUiState -> ColorConfusionContent(gameUiState, onAnswer)
                is OrbitTrackerUiState -> OrbitTrackerContent(gameUiState, onAnswer)
                is FlashCrowdUiState -> FlashCrowdContent(gameUiState, onAnswer)
                is MiniChessUiState -> MiniChessContent(gameUiState, onAnswer)
                is FlagsUiState -> FlagsContent(gameUiState, onAnswer)
                is DigitMemoryUiState -> DigitMemoryContent(gameUiState, onAnswer)
            }
        }
    }
}

@Composable
private fun ColumnScope.MentalCalculationContent(
    uiState: MentalCalculationUiState,
    onAnswer: (String) -> Unit,
) {
    if (LocalIsCompactHeight.current) {
        var input by remember(uiState.calculation) { mutableStateOf("") }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MathText(
                text = "${uiState.calculation} = ${input.ifEmpty { "?" }}",
                style = MaterialTheme.typography.displaySmall,
            )
            Column {
                NumberPad(onInputChange = { typed ->
                    val next = input + typed
                    input = next
                    if (uiState.answerLength == next.length) {
                        onAnswer(next)
                    }
                })
            }
        }
    } else {
        MathText(
            text = uiState.calculation,
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(16.dp))
        NumberPadWithInput(onInputChange = { typed ->
            if (uiState.answerLength == typed.length) {
                onAnswer(typed)
            }
        })
    }
}

@Composable
private fun ColumnScope.ChainCalculationContent(
    uiState: ChainCalculationUiState,
    onAnswer: (String) -> Unit,
    onGiveUp: () -> Unit,
) {
    val onInputChange: (String) -> Unit = { input ->
        if (input.toIntOrNull() == uiState.answer) {
            onAnswer(input)
        }
    }
    if (LocalIsCompactHeight.current) {
        var input by remember(uiState.calculation) { mutableStateOf("") }
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
                MathText(
                    text = "${uiState.calculation} = ${input.ifEmpty { "?" }}",
                    style = MaterialTheme.typography.displaySmall,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(8.dp))
                GiveUpButton(onGiveUp = onGiveUp)
            }
            Column {
                NumberPad(onInputChange = { typed ->
                    val next = input + typed
                    input = next
                    if (next.toIntOrNull() == uiState.answer) {
                        onAnswer(next)
                    }
                })
            }
        }
    } else {
        MathText(
            text = "${uiState.calculation} = ?",
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(16.dp))
        NumberPadWithInput(onInputChange = onInputChange)
        Spacer(Modifier.height(16.dp))
        GiveUpButton(
            onGiveUp = onGiveUp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}

@Composable
private fun ColumnScope.ColoredShapesContent(
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
                Text(button.value, color = MaterialTheme.colorScheme.onErrorContainer)
            }
            AnswerButtonState.CORRECT -> PrismTile(
                face = SuccessGreen,
                modifier = Modifier.size(56.dp),
                isClickable = false,
                onClick = {},
            ) {
                Text(button.value, color = ComposeColor.White)
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

@Composable
private fun ColumnScope.SherlockCalculationContent(
    uiState: SherlockCalculationUiState,
    onAnswer: (String) -> Unit,
    onGiveUp: () -> Unit,
) {
    val compact = LocalIsCompactHeight.current

    // Use key to reset state when uiState.result changes (new round)
    key(uiState.result, uiState.solutionTokens) {
        val usedNumberIndices = remember { mutableStateSetOf<Int>() }
        val expressionTokens = remember { mutableStateListOf<ExpressionToken>() }

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

        val goalText: @Composable (Modifier) -> Unit = { mod ->
            Text(
                text = stringResource(Res.string.game_goal, uiState.result),
                style = MaterialTheme.typography.headlineMedium,
                modifier = mod,
            )
        }

        val expressionRow: @Composable (Modifier) -> Unit = { mod ->
            ExpressionRow(
                tokens = if (showingSolution) {
                    uiState.solutionTokens
                } else {
                    expressionTokens.toImmutableList()
                },
                onTokenClick = { tokenIndex ->
                    if (!showingSolution) {
                        val token = expressionTokens.removeAt(tokenIndex)
                        if (token is ExpressionToken.NumberToken) {
                            usedNumberIndices.remove(token.originalIndex)
                        }
                    }
                },
                onBackspace = {
                    if (!showingSolution && expressionTokens.isNotEmpty()) {
                        val lastToken = expressionTokens.removeAt(expressionTokens.lastIndex)
                        if (lastToken is ExpressionToken.NumberToken) {
                            usedNumberIndices.remove(lastToken.originalIndex)
                        }
                    }
                },
                modifier = mod,
            )
        }

        val giveUpButton: @Composable (Modifier) -> Unit = { mod ->
            GiveUpButton(
                onGiveUp = onGiveUp,
                modifier = mod.alpha(if (showingSolution) 0f else 1f),
                isClickable = !showingSolution,
            )
        }

        val numbersRow: @Composable (Modifier) -> Unit = { mod ->
            AvailableNumbersRow(
                numbers = uiState.numbers,
                usedIndices = if (showingSolution) {
                    uiState.numbers.indices.toImmutableSet()
                } else {
                    usedNumberIndices.toImmutableSet()
                },
                onNumberClick = { value, index ->
                    expressionTokens.add(ExpressionToken.NumberToken(value, index))
                    usedNumberIndices.add(index)
                    checkAnswer()
                },
                modifier = mod,
            )
        }

        val operatorRow: @Composable (Modifier) -> Unit = { mod ->
            OperatorRow(
                onOperatorClick = { operator ->
                    if (!showingSolution) {
                        expressionTokens.add(ExpressionToken.OperatorToken(operator))
                    }
                },
                modifier = mod,
            )
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
                    goalText(Modifier)
                    Spacer(Modifier.height(8.dp))
                    expressionRow(Modifier)
                    Spacer(Modifier.height(8.dp))
                    giveUpButton(Modifier)
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    numbersRow(Modifier)
                    operatorRow(Modifier)
                }
            }
        } else {
            val centered = Modifier.align(Alignment.CenterHorizontally)
            goalText(centered)
            Spacer(Modifier.height(16.dp))
            expressionRow(centered)
            Spacer(Modifier.height(16.dp))
            numbersRow(centered)
            Spacer(Modifier.height(12.dp))
            operatorRow(centered)
            Spacer(Modifier.height(16.dp))
            giveUpButton(centered)
        }
    }
}

@Composable
private fun ColumnScope.FractionCalculationContent(
    uiState: FractionCalculationUiState,
    onAnswer: (String) -> Unit,
    onGiveUp: () -> Unit,
) {
    val onInputChange: (String) -> Unit = { input ->
        if (input == uiState.answerString || input.length >= 4) {
            onAnswer(input)
        }
    }
    val expression: @Composable () -> Unit = {
        Row(
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
    }

    if (LocalIsCompactHeight.current) {
        var input by remember(uiState.calculation) { mutableStateOf("") }
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
                expression()
                if (input.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "= $input",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                }
                Spacer(Modifier.height(8.dp))
                GiveUpButton(onGiveUp = onGiveUp)
            }
            Column {
                NumberPad(onInputChange = { typed ->
                    val next = input + typed
                    input = next
                    if (next == uiState.answerString || next.length >= 4) {
                        onAnswer(next)
                    }
                })
            }
        }
    } else {
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 16.dp),
        ) {
            expression()
        }
        Spacer(Modifier.height(16.dp))
        NumberPadWithInput(onInputChange = onInputChange)
        Spacer(Modifier.height(16.dp))
        GiveUpButton(
            onGiveUp = onGiveUp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}

@Composable
private fun PathFinderCell(
    cell: FigureCell,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val face = when (cell.state) {
        FigureCellState.WRONG -> MaterialTheme.colorScheme.errorContainer
        FigureCellState.CORRECT -> SuccessGreenSoft
        else -> cell.figure.color.composeColor()
    }
    val isClickable = cell.state == FigureCellState.NORMAL
    val cellModifier = if (cell.state == FigureCellState.DIMMED) modifier.alpha(0.3f) else modifier
    PrismTile(
        face = face,
        modifier = cellModifier,
        isClickable = isClickable,
        isSelected = cell.state == FigureCellState.DIMMED,
        onClick = onClick,
    ) {}
}

@Composable
private fun FigureCellContent(
    cell: FigureCell,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (cell.state) {
        FigureCellState.NORMAL -> PrismTile(
            face = MaterialTheme.colorScheme.surfaceContainer,
            modifier = modifier,
            onClick = onClick,
        ) {
            ShapeCanvas(figure = cell.figure, modifier = Modifier.fillMaxSize().padding(8.dp))
        }
        FigureCellState.WRONG -> PrismTile(
            face = MaterialTheme.colorScheme.errorContainer,
            modifier = modifier,
            isClickable = false,
            onClick = {},
        ) {
            ShapeCanvas(figure = cell.figure, modifier = Modifier.fillMaxSize().padding(8.dp))
        }
        FigureCellState.CORRECT -> PrismTile(
            face = SuccessGreenSoft,
            modifier = modifier,
            isClickable = false,
            onClick = {},
        ) {
            ShapeCanvas(figure = cell.figure, modifier = Modifier.fillMaxSize().padding(8.dp))
        }
        FigureCellState.DIMMED -> PrismTile(
            face = MaterialTheme.colorScheme.surfaceContainer,
            modifier = modifier.alpha(0.3f),
            isClickable = false,
            isSelected = true,
            onClick = {},
        ) {
            ShapeCanvas(figure = cell.figure, modifier = Modifier.fillMaxSize().padding(8.dp))
        }
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

    uiState.answers.forEachIndexed { index, button ->
        val faceColor = when (button.state) {
            AnswerButtonState.WRONG -> MaterialTheme.colorScheme.errorContainer
            AnswerButtonState.CORRECT -> SuccessGreen
            else -> Primary
        }
        val contentColor = when (button.state) {
            AnswerButtonState.WRONG -> MaterialTheme.colorScheme.onErrorContainer
            else -> ComposeColor.White
        }
        val isInteractive = button.state == AnswerButtonState.NORMAL
        PrismTile(
            face = faceColor,
            isClickable = isInteractive,
            onClick = { onAnswer((index + 1).toString()) },
            modifier = Modifier
                .padding(4.dp)
                .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                .then(if (button.state == AnswerButtonState.DIMMED) Modifier.alpha(0.3f) else Modifier)
                .then(if (isInteractive) Modifier.hoverHand() else Modifier),
        ) {
            if (button.value.contains("/")) {
                val parts = button.value.split("/")
                FractionText(
                    numerator = parts[0],
                    denominator = parts[1],
                    style = MaterialTheme.typography.bodyLarge,
                    color = contentColor,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                )
            } else {
                MathText(
                    button.value,
                    color = contentColor,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                )
            }
        }
    }
}

private val SudokuOuterFrame = 4.dp
private val SudokuBlockSeparator = 4.dp
private val SudokuCellSeparator = 2.dp
private val SudokuCellSize = 48.dp

@Composable
private fun ColumnScope.MiniSudokuContent(
    uiState: MiniSudokuUiState,
    onAnswer: (String) -> Unit,
) {
    val showingSolution = uiState.solutionValues != null

    // Reset state each round — grid size can grow 4→6 mid-session, so previous inputs
    // would index out of bounds on the new grid.
    var inputs by remember(uiState) { mutableStateOf(uiState.initialValues) }
    var selectedIndex by remember(uiState) {
        mutableStateOf(uiState.initialValues.indexOfFirst { it == null }.coerceAtLeast(0))
    }

    val onDigit: (Int) -> Unit = { digit ->
        if (selectedIndex in inputs.indices && uiState.initialValues[selectedIndex] == null) {
            val updated = inputs.toMutableList().apply { this[selectedIndex] = digit }
            inputs = updated.toImmutableList()
            if (updated.all { it != null }) {
                onAnswer(updated.joinToString(",") { it.toString() })
            } else {
                selectedIndex = nextEmptyCell(selectedIndex, updated, uiState.initialValues)
            }
        }
    }

    if (LocalIsCompactHeight.current) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SudokuGrid(
                uiState = uiState,
                inputs = inputs,
                selectedIndex = selectedIndex,
                showingSolution = showingSolution,
                onCellClick = { selectedIndex = it },
            )
            SudokuDigitPadGrid(
                gridSize = uiState.gridSize,
                columns = 2,
                enabled = !showingSolution,
                onDigit = onDigit,
            )
        }
    } else {
        Text(
            text = stringResource(Res.string.game_sudoku_instruction),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(16.dp))

        SudokuGrid(
            uiState = uiState,
            inputs = inputs,
            selectedIndex = selectedIndex,
            showingSolution = showingSolution,
            onCellClick = { selectedIndex = it },
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(Modifier.height(24.dp))

        SudokuDigitPad(
            gridSize = uiState.gridSize,
            enabled = !showingSolution,
            onDigit = onDigit,
        )
    }
}

@Composable
private fun SudokuDigitPadGrid(
    gridSize: Int,
    columns: Int,
    enabled: Boolean,
    onDigit: (Int) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        (1..gridSize).chunked(columns).forEach { rowDigits ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                rowDigits.forEach { digit ->
                    PrismTile(
                        face = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier
                            .size(SudokuCellSize)
                            .hoverHand(enabled)
                            .alpha(if (enabled) 1f else 0.6f),
                        isClickable = enabled,
                        onClick = { onDigit(digit) },
                    ) {
                        Text(
                            text = digit.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            }
        }
    }
}

/** Find the next still-empty user-editable cell after [from], wrapping around. */
private fun nextEmptyCell(from: Int, inputs: List<Int?>, initialValues: List<Int?>): Int {
    val total = inputs.size
    for (step in 1..total) {
        val idx = (from + step) % total
        if (initialValues[idx] == null && inputs[idx] == null) return idx
    }
    return from
}

@Composable
private fun SudokuGrid(
    uiState: MiniSudokuUiState,
    inputs: ImmutableList<Int?>,
    selectedIndex: Int,
    showingSolution: Boolean,
    onCellClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val n = uiState.gridSize
    val gridLineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)

    Box(modifier = modifier.background(gridLineColor)) {
        Column(modifier = Modifier.padding(SudokuOuterFrame)) {
            for (row in 0 until n) {
                Row {
                    for (col in 0 until n) {
                        val index = row * n + col
                        val isClue = uiState.initialValues[index] != null
                        val isSolution = showingSolution && !isClue
                        val value = when {
                            isSolution -> uiState.solutionValues!![index]
                            else -> inputs[index]
                        }
                        SudokuCell(
                            value = value?.toString().orEmpty(),
                            isClue = isClue,
                            isSelected = index == selectedIndex && !showingSolution,
                            isSolution = isSolution,
                            onClick = { onCellClick(index) },
                            modifier = Modifier.padding(
                                end = gapAfter(col, n, uiState.blockCols),
                                bottom = gapAfter(row, n, uiState.blockRows),
                            ),
                        )
                    }
                }
            }
        }
    }
}

private fun gapAfter(index: Int, total: Int, blockSize: Int): androidx.compose.ui.unit.Dp = when {
    index == total - 1 -> 0.dp
    (index + 1) % blockSize == 0 -> SudokuBlockSeparator
    else -> SudokuCellSeparator
}

@Composable
private fun SudokuCell(
    value: String,
    isClue: Boolean,
    isSelected: Boolean,
    isSolution: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isInteractive = !isClue && !isSolution
    val containerColor = when {
        isSolution -> SuccessGreen.copy(alpha = 0.22f)
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }
    PrismTile(
        face = containerColor,
        isClickable = isInteractive,
        isSelected = isSelected,
        modifier = modifier
            .size(SudokuCellSize)
            .hoverHand(isInteractive),
        onClick = onClick,
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isSolution) SuccessGreen else MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ColumnScope.SudokuDigitPad(
    gridSize: Int,
    enabled: Boolean,
    onDigit: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.align(Alignment.CenterHorizontally),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (digit in 1..gridSize) {
            PrismTile(
                face = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .size(SudokuCellSize)
                    .hoverHand(enabled)
                    .alpha(if (enabled) 1f else 0.6f),
                isClickable = enabled,
                onClick = { onDigit(digit) },
            ) {
                Text(
                    text = digit.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = OnPrimaryContainer,
                )
            }
        }
    }
}

@Composable
private fun StopwatchDisplay(elapsedMillis: Long, modifier: Modifier = Modifier) {
    val seconds = elapsedMillis / 1000
    val tenths = (elapsedMillis % 1000) / 100
    Text(
        text = stringResource(Res.string.format_seconds, "$seconds.$tenths"),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = modifier.wrapContentWidth(Alignment.CenterHorizontally),
    )
}

@Composable
private fun ColumnScope.SchulteTableContent(
    uiState: SchulteTableUiState,
    onAnswer: (String) -> Unit,
) {
    val n = uiState.gridSize
    val cellSize = when (n) {
        4 -> 64.dp
        5 -> 56.dp
        6 -> 48.dp
        else -> 42.dp
    }

    Text(
        text = stringResource(Res.string.game_schulte_instruction),
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(horizontal = 16.dp),
    )
    Spacer(Modifier.height(16.dp))

    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.align(Alignment.CenterHorizontally),
    ) {
        for (row in 0 until n) {
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                for (col in 0 until n) {
                    val index = row * n + col
                    SchulteCell(
                        cell = uiState.cells[index],
                        size = cellSize,
                        onClick = { onAnswer(index.toString()) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SchulteCell(
    cell: SchulteTableUiState.CellState,
    size: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit,
) {
    val face = when (cell.type) {
        SchulteTableUiState.CellType.NORMAL -> MaterialTheme.colorScheme.surfaceContainer
        SchulteTableUiState.CellType.TAPPED -> MaterialTheme.colorScheme.surfaceVariant
        SchulteTableUiState.CellType.WRONG -> MaterialTheme.colorScheme.errorContainer
    }
    val textColor = when (cell.type) {
        SchulteTableUiState.CellType.TAPPED -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        SchulteTableUiState.CellType.WRONG -> MaterialTheme.colorScheme.onErrorContainer
        SchulteTableUiState.CellType.NORMAL -> MaterialTheme.colorScheme.onSurface
    }
    val isInteractive = cell.type == SchulteTableUiState.CellType.NORMAL
    PrismTile(
        face = face,
        modifier = Modifier
            .size(size)
            .hoverHand(isInteractive),
        isClickable = isInteractive,
        isSelected = cell.type == SchulteTableUiState.CellType.TAPPED,
        onClick = onClick,
    ) {
        Text(
            text = cell.number.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = textColor,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ColumnScope.LightsOutContent(
    uiState: LightsOutUiState,
    onAnswer: (String) -> Unit,
    onGiveUp: () -> Unit,
) {
    val n = uiState.gridSize
    val compact = LocalIsCompactHeight.current
    val cellSize = when {
        compact -> if (n <= 4) 48.dp else 40.dp
        n == 3 -> 72.dp
        n == 4 -> 60.dp
        else -> 52.dp
    }

    val board: @Composable () -> Unit = {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            for (row in 0 until n) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (col in 0 until n) {
                        val index = row * n + col
                        LightsOutCell(
                            on = uiState.cells[index],
                            size = cellSize,
                            onClick = { onAnswer(index.toString()) },
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
            board()
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(Res.string.level_label, uiState.level),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(Res.string.moves_label, uiState.moves),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(8.dp))
                GiveUpButton(onGiveUp = onGiveUp)
            }
        }
    } else {
        Text(
            text = stringResource(Res.string.level_label, uiState.level),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(Res.string.moves_label, uiState.moves),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(16.dp))
        Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            board()
        }
        Spacer(Modifier.height(16.dp))
        GiveUpButton(
            onGiveUp = onGiveUp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}

@Composable
private fun LightsOutCell(
    on: Boolean,
    size: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit,
) {
    PrismTile(
        face = if (on) LightsOutOnColor else LightsOutOffColor,
        modifier = Modifier
            .size(size)
            .hoverHand(),
        isSelected = !on,
        onClick = onClick,
    ) {}
}

@Composable
private fun ColumnScope.SlidingPuzzleContent(
    uiState: SlidingPuzzleUiState,
    onAnswer: (String) -> Unit,
    onGiveUp: () -> Unit,
) {
    val n = uiState.gridSize
    val compact = LocalIsCompactHeight.current
    val cellSize = when {
        compact -> if (n <= 4) 48.dp else 40.dp
        n == 3 -> 72.dp
        n == 4 -> 60.dp
        else -> 52.dp
    }

    val board: @Composable () -> Unit = {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            for (row in 0 until n) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (col in 0 until n) {
                        val index = row * n + col
                        val tile = uiState.tiles[index]
                        SlidingPuzzleCell(
                            label = tile,
                            size = cellSize,
                            onClick = { if (tile != 0) onAnswer(index.toString()) },
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
            board()
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(Res.string.level_label, uiState.level),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(Res.string.moves_label, uiState.moves),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(8.dp))
                GiveUpButton(onGiveUp = onGiveUp)
            }
        }
    } else {
        Text(
            text = stringResource(Res.string.level_label, uiState.level),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(Res.string.moves_label, uiState.moves),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(16.dp))
        Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            board()
        }
        Spacer(Modifier.height(16.dp))
        GiveUpButton(
            onGiveUp = onGiveUp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}

@Composable
private fun SlidingPuzzleCell(
    label: Int,
    size: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit,
) {
    val isEmpty = label == 0
    val containerColor = if (isEmpty) {
        MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.4f)
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }
    PrismTile(
        face = containerColor,
        isClickable = !isEmpty,
        modifier = Modifier
            .size(size)
            .hoverHand(!isEmpty),
        onClick = onClick,
    ) {
        if (!isEmpty) {
            Text(
                text = label.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
            )
        }
    }
}

// --- Sherlock Calculation Helper Composables ---

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ExpressionRow(
    tokens: ImmutableList<ExpressionToken>,
    onTokenClick: (Int) -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (tokens.isEmpty()) {
            Text(
                text = stringResource(Res.string.game_tap_numbers),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterVertically),
            )
        } else {
            tokens.forEachIndexed { index, token ->
                when (token) {
                    is ExpressionToken.NumberToken -> {
                        PrismTile(
                            face = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier
                                .defaultMinSize(40.dp, 40.dp)
                                .hoverHand(),
                            isSelected = true,
                            onClick = { onTokenClick(index) },
                        ) {
                            Text(
                                token.displayValue,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 12.dp),
                            )
                        }
                    }
                    is ExpressionToken.OperatorToken -> {
                        PrismTile(
                            face = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .defaultMinSize(40.dp, 40.dp)
                                .hoverHand(),
                            isSelected = true,
                            onClick = { onTokenClick(index) },
                        ) {
                            val operatorIcon = OperatorIcons[token.displayValue]
                            if (operatorIcon != null) {
                                Icon(
                                    imageVector = operatorIcon,
                                    contentDescription = token.displayValue,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                        .padding(horizontal = 6.dp)
                                        .size(28.dp),
                                )
                            } else {
                                MathText(
                                    token.displayValue,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
        PrismTile(
            face = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .padding(start = 4.dp)
                .size(40.dp)
                .hoverHand(tokens.isNotEmpty()),
            isClickable = tokens.isNotEmpty(),
            onClick = onBackspace,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Backspace,
                contentDescription = stringResource(Res.string.button_backspace),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AvailableNumbersRow(
    numbers: ImmutableList<Int>,
    usedIndices: ImmutableSet<Int>,
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
            PrismTile(
                face = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .size(56.dp)
                    .hoverHand(!isUsed),
                isClickable = !isUsed,
                isSelected = isUsed,
                onClick = { onNumberClick(value, index) },
            ) {
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}

private val SherlockOperators = listOf("+", "-", "*", "/", "(", ")")

@Composable
private fun OperatorRow(
    onOperatorClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.padding(horizontal = 16.dp),
        maxItemsInEachRow = 3,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SherlockOperators.forEach { operator ->
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
    PrismProgressBar(
        progress = progress,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
        fillColor = Primary,
        modifier = modifier.height(12.dp),
    )
}

// --- Visual Memory Game Composables ---

private const val VisualMemoryTransitionMillis = 250

private val CellTypesShowingShape = setOf(
    VisualMemoryUiState.CellType.MEMORIZING,
    VisualMemoryUiState.CellType.REVEALED,
    VisualMemoryUiState.CellType.WRONG,
)

@Composable
private fun ColumnScope.DigitMemoryContent(
    uiState: DigitMemoryUiState,
    onAnswer: (String) -> Unit,
) {
    Spacer(Modifier.weight(1f))
    when (uiState.phase) {
        DigitMemoryGame.Phase.SHOWING -> {
            // SHOWING and RECALL share the slot-box visual; only the math step looks different.
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                DigitMemoryPhaseLabel(
                    text = stringResource(Res.string.digit_memory_memorize),
                    accent = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.height(20.dp))
                DigitSlots(
                    length = uiState.sequenceLength,
                    value = uiState.sequence,
                    accent = MaterialTheme.colorScheme.primary,
                    revealColor = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                )
            }
        }
        DigitMemoryGame.Phase.SOLVING -> DigitMemorySolvingContent(uiState, onAnswer)
        DigitMemoryGame.Phase.RECALL -> {
            val result = uiState.recallResult
            val revealColor = when (result) {
                DigitMemoryGame.RecallResult.CORRECT -> SuccessGreen
                DigitMemoryGame.RecallResult.WRONG -> MaterialTheme.colorScheme.error
                null -> null
            }
            DigitMemoryInputArea(
                label = stringResource(Res.string.digit_memory_recall),
                accent = MaterialTheme.colorScheme.primary,
                expectedLength = uiState.sequenceLength,
                revealed = result != null,
                onAnswer = onAnswer,
                display = { typed, onRemoveAt ->
                    DigitSlots(
                        length = uiState.sequenceLength,
                        value = if (result != null) uiState.sequence else typed,
                        accent = MaterialTheme.colorScheme.primary,
                        revealColor = revealColor,
                        onRemoveAt = if (result == null) onRemoveAt else null,
                        modifier = if (LocalIsCompactHeight.current) {
                            Modifier.widthIn(max = 320.dp)
                        } else {
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        },
                    )
                },
            )
        }
    }
    Spacer(Modifier.weight(1f))
}

/** Small colored header that names the current phase (MEMORIZE / SOLVE / RECALL). */
@Composable
private fun DigitMemoryPhaseLabel(text: String, accent: ComposeColor) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = accent,
        letterSpacing = 3.sp,
    )
}

/**
 * A single row of per-digit boxes that shrink to fit the available width (always one line). Empty
 * boxes act as input slots that fill as the player types; the next slot to fill is highlighted with
 * [accent]. When [onRemoveAt] is set, tapping a filled box deletes that digit. When [revealColor] is
 * set the whole sequence is shown in that color (used for the memorize phase and the recall reveal).
 */
@Composable
private fun DigitSlots(
    length: Int,
    value: String,
    accent: ComposeColor,
    revealColor: ComposeColor?,
    onRemoveAt: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
        val spacing = 6.dp
        val maxSlot = 48.dp
        val slotWidth = ((maxWidth - spacing * (length - 1)) / length).coerceIn(18.dp, maxSlot)
        val slotHeight = slotWidth * 1.3f
        val fontSize = (slotWidth.value * 0.46f).sp
        val shape = RoundedCornerShape(10.dp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally),
        ) {
            repeat(length) { index ->
                val digit = value.getOrNull(index)?.toString() ?: ""
                val isActive = revealColor == null && index == value.length
                val borderColor = when {
                    revealColor != null -> revealColor
                    isActive -> accent
                    digit.isNotEmpty() -> accent.copy(alpha = 0.5f)
                    else -> MaterialTheme.colorScheme.outlineVariant
                }
                val textColor = revealColor ?: MaterialTheme.colorScheme.onSurface
                val clickable = onRemoveAt != null && digit.isNotEmpty()
                Box(
                    modifier = Modifier
                        .size(width = slotWidth, height = slotHeight)
                        .clip(shape)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .border(BorderStroke(2.dp, borderColor), shape)
                        .then(
                            if (clickable) Modifier.clickable { onRemoveAt!!(index) } else Modifier,
                        )
                        .hoverHand(clickable),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = digit,
                        style = MaterialTheme.typography.headlineSmall,
                        fontSize = fontSize,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                    )
                }
            }
        }
    }
}

/**
 * The distraction math step. Reuses the standard [NumberPadWithInput] (same input UI as Mental
 * Calculation) so the typed answer shows in the familiar input row with backspace; the equation
 * itself stays "a + b = ?". On a wrong answer the card flips to show the correct result.
 */
@Composable
private fun DigitMemorySolvingContent(
    uiState: DigitMemoryUiState,
    onAnswer: (String) -> Unit,
) {
    val reveal = uiState.revealedMathAnswer
    val onInputChange: (String) -> Unit = { typed ->
        if (reveal == null && typed.length == uiState.answerLength) onAnswer(typed)
    }
    if (LocalIsCompactHeight.current) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                DigitMemoryPhaseLabel(
                    text = stringResource(Res.string.digit_memory_solve),
                    accent = MaterialTheme.colorScheme.tertiary,
                )
                Spacer(Modifier.height(12.dp))
                EquationCard(problem = uiState.problem, answer = reveal)
            }
            if (reveal == null) {
                Column { NumberPadWithInput(onInputChange = onInputChange) }
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DigitMemoryPhaseLabel(
                text = stringResource(Res.string.digit_memory_solve),
                accent = MaterialTheme.colorScheme.tertiary,
            )
            Spacer(Modifier.height(20.dp))
            EquationCard(problem = uiState.problem, answer = reveal)
            if (reveal == null) {
                Spacer(Modifier.height(16.dp))
                NumberPadWithInput(onInputChange = onInputChange)
            }
        }
    }
}

/** The distraction math problem shown as an equation in a colored card. [answer] is non-null only
 *  while revealing the correct result after a wrong attempt. */
@Composable
private fun EquationCard(problem: String, answer: String?) {
    val isError = answer != null
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isError) {
            MaterialTheme.colorScheme.errorContainer
        } else {
            MaterialTheme.colorScheme.tertiaryContainer
        },
    ) {
        MathText(
            text = "$problem = ${answer ?: "?"}",
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center,
            color = if (isError) {
                MaterialTheme.colorScheme.onErrorContainer
            } else {
                MaterialTheme.colorScheme.onTertiaryContainer
            },
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 18.dp),
        )
    }
}

/**
 * Shared input area for the solving/recall phases: a phase label, a phase-specific [display] that
 * receives the current typed value plus a callback to delete the digit at a given index (used by the
 * recall slots), and a number pad. The pad is hidden while a reveal is showing.
 */
@Composable
private fun DigitMemoryInputArea(
    label: String,
    accent: ComposeColor,
    expectedLength: Int,
    revealed: Boolean,
    onAnswer: (String) -> Unit,
    display: @Composable (typed: String, onRemoveAt: (Int) -> Unit) -> Unit,
) {
    var typed by remember { mutableStateOf("") }
    val onDigit: (String) -> Unit = { digit ->
        if (!revealed) {
            val next = typed + digit
            typed = next
            if (next.length == expectedLength) onAnswer(next)
        }
    }
    val onRemoveAt: (Int) -> Unit = { index ->
        if (!revealed && index < typed.length) {
            typed = typed.removeRange(index, index + 1)
        }
    }
    if (LocalIsCompactHeight.current) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                DigitMemoryPhaseLabel(label, accent)
                Spacer(Modifier.height(12.dp))
                display(typed, onRemoveAt)
            }
            if (!revealed) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    NumberPad(onInputChange = onDigit)
                }
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DigitMemoryPhaseLabel(label, accent)
            Spacer(Modifier.height(20.dp))
            display(typed, onRemoveAt)
            if (!revealed) {
                Spacer(Modifier.height(24.dp))
                NumberPad(onInputChange = onDigit)
            }
        }
    }
}

@Composable
private fun ColumnScope.VisualMemoryContent(
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

// --- Spot the New Game Composables ---

private const val SpotTheNewFadeMillis = 420

@Composable
private fun ColumnScope.SpotTheNewContent(
    uiState: SpotTheNewUiState,
    onAnswer: (String) -> Unit,
) {
    // Each round fades out, then the next round fades in. Keying on the round number means the
    // in-place game-over reveal (same round, only tile colors change) is not faded.
    AnimatedContent(
        targetState = uiState,
        contentKey = { it.round },
        transitionSpec = {
            fadeIn(animationSpec = tween(SpotTheNewFadeMillis, delayMillis = SpotTheNewFadeMillis)) togetherWith
                fadeOut(animationSpec = tween(SpotTheNewFadeMillis))
        },
        modifier = Modifier.align(Alignment.CenterHorizontally),
        label = "spotTheNewRound",
    ) { state ->
        // Only the fully settled, visible round accepts taps, so the outgoing grid can't be
        // tapped against the already-advanced game state during the fade.
        val settled = transition.currentState == EnterExitState.Visible &&
            transition.targetState == EnterExitState.Visible
        SpotTheNewGrid(
            uiState = state,
            interactive = settled,
            onAnswer = onAnswer,
        )
    }
}

@Composable
private fun SpotTheNewGrid(
    uiState: SpotTheNewUiState,
    interactive: Boolean,
    onAnswer: (String) -> Unit,
) {
    val count = uiState.displayedCount.coerceAtLeast(1)
    val columns = ceil(sqrt(count.toDouble())).toInt().coerceAtLeast(1)
    val clickable = interactive && uiState.phase == SpotTheNewGame.Phase.ANSWERING
    val maxTileWidth = if (LocalIsCompactHeight.current) 56.dp else 72.dp

    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .widthIn(max = maxTileWidth * columns),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        uiState.cells.chunked(columns).forEach { rowCells ->
            Row {
                rowCells.forEach { cell ->
                    SpotTheNewTile(
                        cell = cell,
                        clickable = clickable,
                        onAnswer = onAnswer,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(4.dp),
                    )
                }
                // Pad the final short row so its tiles keep the same square size.
                repeat(columns - rowCells.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun SpotTheNewTile(
    cell: SpotTheNewUiState.CellState,
    clickable: Boolean,
    onAnswer: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val face = when (cell.type) {
        SpotTheNewGame.CellType.WRONG -> MaterialTheme.colorScheme.errorContainer
        SpotTheNewGame.CellType.CORRECT -> PrimaryContainer
        SpotTheNewGame.CellType.NORMAL -> MaterialTheme.colorScheme.surfaceContainer
    }
    PrismTile(
        face = face,
        modifier = modifier,
        isClickable = clickable,
        onClick = { onAnswer(cell.index.toString()) },
    ) {
        Image(
            painter = painterResource(cell.animal.resource),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
        )
    }
}

// --- Ghost Grid Game Composables ---

@Composable
private fun ColumnScope.GhostGridContent(
    uiState: GhostGridUiState,
    onAnswer: (String) -> Unit,
) {
    val cellMax = if (LocalIsCompactHeight.current) 56.dp else 72.dp
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .widthIn(max = cellMax * uiState.gridSize)
            .align(Alignment.CenterHorizontally),
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
    val face = when (cell.type) {
        GhostGridUiState.CellType.ACTIVE -> Primary
        GhostGridUiState.CellType.TAPPED -> Primary
        GhostGridUiState.CellType.WRONG -> MaterialTheme.colorScheme.errorContainer
        GhostGridUiState.CellType.MISSED -> SuccessGreenSoft
        GhostGridUiState.CellType.INACTIVE -> MaterialTheme.colorScheme.surfaceVariant
    }
    PrismTile(
        face = face,
        modifier = modifier.hoverHand(isClickable),
        isClickable = isClickable,
        isSelected = cell.type == GhostGridUiState.CellType.TAPPED,
        onClick = onClick,
    ) {}
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

// --- Color Confusion Game Composables ---

@Composable
private fun ColumnScope.ColorConfusionContent(
    uiState: ColorConfusionUiState,
    onAnswer: (String) -> Unit,
) {
    val compact = LocalIsCompactHeight.current
    val cellMax = if (compact) 72.dp else 100.dp

    // 3x3 Grid
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .widthIn(max = cellMax * 3)
            .align(Alignment.CenterHorizontally),
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

    Spacer(Modifier.height(if (compact) 8.dp else 16.dp))

    PrismTile(
        face = Primary,
        isClickable = !uiState.isSubmitted,
        onClick = { onAnswer("submit") },
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .defaultMinSize(minWidth = 96.dp, minHeight = 48.dp)
            .alpha(if (uiState.isSubmitted) 0f else 1f)
            .hoverHand(),
    ) {
        Text(
            stringResource(Res.string.button_done),
            color = ComposeColor.White,
        )
    }
}

@Composable
private fun ColorConfusionCell(
    cell: ColorConfusionUiState.Cell,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDark = isSystemInDarkTheme()
    val selectedFace = if (isDark) SelectedTileFaceDark else SelectedTileFaceLight
    val unselectedFace = if (isDark) UnselectedTileFaceDark else MaterialTheme.colorScheme.surfaceContainer
    val targetContainerColor = when {
        cell.feedback == ColorConfusionUiState.CellFeedback.CORRECT_SELECTED -> SuccessGreenSoft
        cell.feedback == ColorConfusionUiState.CellFeedback.WRONG_SELECTED -> MaterialTheme.colorScheme.errorContainer
        cell.feedback == ColorConfusionUiState.CellFeedback.MISSED -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
        cell.isSelected -> selectedFace
        else -> unselectedFace
    }
    val containerColor by animateColorAsState(
        targetValue = targetContainerColor,
        animationSpec = tween(250),
        label = "colorConfusionContainer",
    )

    val isInteractive = cell.feedback == ColorConfusionUiState.CellFeedback.NONE
    // Only sink the tile for finalized feedback states. Sinking the mid-play selection would dim
    // the bright selected face to its side color and erase the very contrast that distinguishes
    // selected from unselected in bright sunlight.
    val isLockedIn = cell.feedback == ColorConfusionUiState.CellFeedback.CORRECT_SELECTED ||
        cell.feedback == ColorConfusionUiState.CellFeedback.WRONG_SELECTED
    PrismTile(
        face = containerColor,
        modifier = modifier.hoverHand(isInteractive),
        isClickable = isInteractive,
        isSelected = isLockedIn,
        onClick = onClick,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                text = cell.word.localizedName(),
                style = MaterialTheme.typography.titleMedium,
                color = cell.fontColor.composeColor(),
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

    val canvasSizeModifier = if (LocalIsCompactHeight.current) {
        Modifier
            .padding(horizontal = 24.dp)
            .heightIn(max = 240.dp)
            .aspectRatio(1f)
    } else {
        Modifier
            .padding(horizontal = 24.dp)
            .widthIn(max = 400.dp)
            .aspectRatio(1f)
    }
    Box(
        modifier = canvasSizeModifier
            .align(Alignment.CenterHorizontally),
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .hoverHand(isAnswering)
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

                drawPrismCircle(
                    center = center,
                    radius = ballRadiusPx,
                    face = color,
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
        val alpha = animateFloatAsState(
            targetValue = if (visible) 1f else 0f,
            animationSpec = tween(200),
            label = "flashCrowdAlpha",
        )

        LaunchedEffect(Unit) {
            delay(750.milliseconds)
            visible = false
            delay(200.milliseconds)
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
                    .graphicsLayer { this.alpha = alpha.value },
            )
        } else {
            Text(
                text = stringResource(Res.string.game_flash_crowd_which_more),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .graphicsLayer { this.alpha = alpha.value },
            )
            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .widthIn(max = 400.dp)
                    .align(Alignment.CenterHorizontally)
                    .graphicsLayer { this.alpha = alpha.value },
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                PrismTile(
                    face = FlashCrowdBlue,
                    onClick = { onAnswer("left") },
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .hoverHand(),
                ) {
                    Text(
                        text = stringResource(Res.string.game_flash_crowd_blue),
                        style = MaterialTheme.typography.titleLarge,
                        color = ComposeColor.White,
                    )
                }
                PrismTile(
                    face = FlashCrowdYellow,
                    side = FlashCrowdYellowSide,
                    bottom = FlashCrowdYellowBottom,
                    onClick = { onAnswer("right") },
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .hoverHand(),
                ) {
                    Text(
                        text = stringResource(Res.string.game_flash_crowd_yellow),
                        style = MaterialTheme.typography.titleLarge,
                        color = ComposeColor.Black,
                    )
                }
            }
        }
    }
}

@Composable
private fun FlashCrowdDotsRow(
    uiState: FlashCrowdUiState,
    blueColor: ComposeColor,
    yellowColor: ComposeColor,
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
                drawPrismCircle(
                    center = Offset(dot.x * size.width, dot.y * size.height),
                    radius = dot.radius * size.width,
                    face = blueColor,
                )
            }
        }
        Canvas(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f),
        ) {
            uiState.rightDots.forEach { dot ->
                drawPrismCircle(
                    center = Offset(dot.x * size.width, dot.y * size.height),
                    radius = dot.radius * size.width,
                    face = yellowColor,
                    side = FlashCrowdYellowSide,
                    bottom = FlashCrowdYellowBottom,
                )
            }
        }
    }
}

private val MiniChessLightSquare = ComposeColor(0xFFEEEED2)
private val MiniChessDarkSquare = ComposeColor(0xFF769656)
private val MiniChessSelected = ComposeColor(0xFFB9CAFF)
private val MiniChessLastMove = ComposeColor(0x66FFD54F)
private val MiniChessLegalDot = ComposeColor(0x66000000)
private val MiniChessCaptureTint = ComposeColor(0x55E53935)
private val MiniChessDrawDot = ComposeColor(0xCCFBC02D)
private val MiniChessDrawTint = ComposeColor(0x66FBC02D)
private val MiniChessCheckTint = ComposeColor(0x88E53935)
private val MiniChessWarning = ComposeColor(0xFFE65100)

@Composable
private fun ColumnScope.MiniChessContent(
    uiState: MiniChessUiState,
    onAnswer: (String) -> Unit,
) {
    var selectedFrom by remember(uiState.cells, uiState.legalMovesByFrom) {
        mutableStateOf<Int?>(null)
    }
    val highlights = uiState.legalMovesByFrom[selectedFrom].orEmpty()
    val drawHighlights = uiState.stalematingMovesByFrom[selectedFrom].orEmpty()
    val interactive = uiState.outcome == null && !uiState.isAiThinking
    val selectedHasDrawMove = drawHighlights.isNotEmpty()
    val movesLeft = uiState.halfMoveCap - uiState.halfMoveCount
    val movesNearCap = uiState.outcome == null && movesLeft in 1..6

    val statusBox: @Composable () -> Unit = {
        Box(
            modifier = Modifier.height(20.dp),
            contentAlignment = Alignment.Center,
        ) {
            when {
                uiState.outcome != null -> Unit
                uiState.isAiThinking -> Text(
                    text = stringResource(Res.string.mini_chess_thinking),
                    style = MaterialTheme.typography.bodyMedium,
                )
                selectedHasDrawMove -> Row(verticalAlignment = Alignment.CenterVertically) {
                    ColorPrismCell(
                        face = MiniChessDrawDot,
                        facet = 1.5.dp,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(Res.string.mini_chess_draw_warning),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MiniChessWarning,
                        fontWeight = FontWeight.Bold,
                    )
                }
                else -> Text(
                    text = stringResource(
                        Res.string.mini_chess_move_counter,
                        uiState.halfMoveCount,
                        uiState.halfMoveCap,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (movesNearCap) MiniChessWarning else MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (movesNearCap) FontWeight.Bold else FontWeight.Normal,
                )
            }
        }
    }

    val board: @Composable () -> Unit = {
        Column {
            for (row in 4 downTo 0) {
                Row {
                    for (col in 0..4) {
                        val index = row * 5 + col
                        val cell = uiState.cells[index]
                        val showCheckRing = cell.pieceType == PieceType.KING &&
                            (
                                (cell.isWhite && uiState.whiteInCheck) ||
                                    (!cell.isWhite && uiState.blackInCheck)
                                )
                        MiniChessCellView(
                            cell = cell,
                            isLight = (row + col) % 2 == 0,
                            isSelected = selectedFrom == index,
                            isLegalTarget = index in highlights,
                            isStalemateTarget = index in drawHighlights,
                            isLastMove = index == uiState.lastMoveFromIndex || index == uiState.lastMoveToIndex,
                            showCheckRing = showCheckRing,
                            enabled = interactive,
                            onClick = {
                                val from = selectedFrom
                                if (from != null && uiState.legalMovesByFrom[from]?.contains(index) == true) {
                                    onAnswer("$from>$index")
                                    selectedFrom = null
                                } else if (uiState.legalMovesByFrom.containsKey(index)) {
                                    selectedFrom = index
                                } else {
                                    selectedFrom = null
                                }
                            },
                        )
                    }
                }
            }
        }
    }

    val outcomeAndActions: @Composable ColumnScope.() -> Unit = {
        if (uiState.outcome == null) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DefaultButton(
                    onClick = { onAnswer("reset") },
                    value = stringResource(Res.string.mini_chess_reset),
                )
                DefaultButton(
                    onClick = { onAnswer("restart") },
                    value = stringResource(Res.string.mini_chess_restart),
                )
            }
        } else {
            Text(
                text = when (uiState.outcome) {
                    MiniChessOutcome.PLAYER_WIN -> stringResource(Res.string.mini_chess_round_won)
                    MiniChessOutcome.PLAYER_LOSS -> stringResource(Res.string.mini_chess_round_lost)
                    MiniChessOutcome.DRAW -> stringResource(Res.string.mini_chess_round_draw)
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = when (uiState.outcome) {
                    MiniChessOutcome.PLAYER_WIN -> SuccessGreen
                    else -> MaterialTheme.colorScheme.onSurface
                },
            )
            if (uiState.outcome == MiniChessOutcome.PLAYER_WIN) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(Res.string.mini_chess_xp_gained, uiState.pointsForWin),
                    style = MaterialTheme.typography.bodyMedium,
                    color = SuccessGreen,
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DefaultButton(
                    onClick = { onAnswer("reset") },
                    value = stringResource(Res.string.mini_chess_reset),
                )
                DefaultButton(
                    onClick = { onAnswer("restart") },
                    value = stringResource(Res.string.mini_chess_restart),
                )
            }
        }
    }

    if (LocalIsCompactHeight.current) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            board()
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                statusBox()
                Spacer(Modifier.height(12.dp))
                outcomeAndActions()
            }
        }
    } else {
        Column(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(8.dp))
            statusBox()
            Spacer(Modifier.height(8.dp))
            board()
            Spacer(Modifier.height(12.dp))
            outcomeAndActions()
        }
    }
}

@Composable
private fun MiniChessCellView(
    cell: MiniChessCell,
    isLight: Boolean,
    isSelected: Boolean,
    isLegalTarget: Boolean,
    isStalemateTarget: Boolean,
    isLastMove: Boolean,
    showCheckRing: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val baseColor = if (isLight) MiniChessLightSquare else MiniChessDarkSquare
    Box(
        modifier = Modifier
            .size(56.dp)
            .background(if (isSelected) MiniChessSelected else baseColor)
            .clickable(enabled = enabled, onClick = onClick)
            .hoverHand(enabled),
        contentAlignment = Alignment.Center,
    ) {
        if (isLastMove && !isSelected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(MiniChessLastMove),
            )
        }
        // King-in-check tint: solid red over the whole square so the player can spot the
        // attacked king at a glance.
        if (showCheckRing) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(MiniChessCheckTint),
            )
        }
        // Capture tint: red square highlight when the move takes an opposing piece. Skipped
        // for stalemate moves — those use the orange dot instead so the indicator matches
        // the legend in the warning text.
        if (isLegalTarget && cell.pieceType != null && !isStalemateTarget) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(MiniChessCaptureTint),
            )
        }
        cell.pieceType?.let { type ->
            Box(
                modifier = Modifier.size(52.dp),
                contentAlignment = Alignment.Center,
            ) {
                ChessPieceIcon(type = type, isWhite = cell.isWhite)
            }
        }
        // Move indicators rendered last so they sit on top of any piece silhouette.
        if (isLegalTarget) {
            when {
                isStalemateTarget -> ColorPrismCell(
                    face = MiniChessDrawDot,
                    side = ComposeColor.Black.copy(alpha = 0.55f),
                    bottom = ComposeColor.Black.copy(alpha = 0.55f),
                    modifier = Modifier.size(20.dp),
                )
                cell.pieceType == null -> ColorPrismCell(
                    face = MiniChessLegalDot,
                    facet = 1.5.dp,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Composable
private fun ChessPieceIcon(type: PieceType, isWhite: Boolean) {
    val painter = painterResource(chessPieceResource(type))
    val fill = ColorFilter.tint(if (isWhite) ComposeColor.White else ComposeColor.Black)

    Canvas(modifier = Modifier.size(44.dp)) {
        if (isWhite) {
            for ((dx, dy) in ChessHaloDeltas) {
                translate(left = dx, top = dy) {
                    with(painter) { draw(size = this@Canvas.size, colorFilter = ChessOutlineFilter) }
                }
            }
        }
        with(painter) { draw(size = size, colorFilter = fill) }
    }
}

private fun chessPieceResource(type: PieceType) = when (type) {
    PieceType.KING -> Res.drawable.ic_chess_king
    PieceType.QUEEN -> Res.drawable.ic_chess_queen
    PieceType.ROOK -> Res.drawable.ic_chess_rook
    PieceType.BISHOP -> Res.drawable.ic_chess_bishop
    PieceType.KNIGHT -> Res.drawable.ic_chess_knight
    PieceType.PAWN -> Res.drawable.ic_chess_pawn
}

@Composable
private fun ColumnScope.FlagsContent(
    uiState: FlagsUiState,
    onAnswer: (String) -> Unit,
) {
    val compact = LocalIsCompactHeight.current
    val flagSize = if (compact) 120.dp else 180.dp

    if (compact) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(flagResource(uiState.countrySlug)),
                    contentDescription = null,
                    modifier = Modifier.size(flagSize),
                )
                Spacer(Modifier.height(8.dp))
                FlagsScoreRow(uiState.currentScore, uiState.bestScore)
            }
            FlagAnswerButtons(
                uiState = uiState,
                onAnswer = onAnswer,
                modifier = Modifier
                    .weight(1f, fill = false)
                    .widthIn(max = 280.dp),
            )
        }
    } else {
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            val numAnswers = uiState.possibleAnswers.size
            val buttonsApprox = (52.dp * numAnswers) +
                (8.dp * (numAnswers - 1).coerceAtLeast(0))
            val headerApprox = 72.dp
            val minBreathingRoom = 16.dp
            val dynamicFlagSize = (maxHeight - buttonsApprox - headerApprox - minBreathingRoom)
                .coerceIn(80.dp, flagSize)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                FlagsScoreRow(
                    currentScore = uiState.currentScore,
                    bestScore = uiState.bestScore,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
                Text(
                    text = stringResource(Res.string.game_flags_question),
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(Modifier.weight(1f))
                Image(
                    painter = painterResource(flagResource(uiState.countrySlug)),
                    contentDescription = null,
                    modifier = Modifier.size(dynamicFlagSize),
                )
                Spacer(Modifier.weight(1f))
                FlagAnswerButtons(
                    uiState = uiState,
                    onAnswer = onAnswer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .widthIn(max = 480.dp),
                )
            }
        }
    }
}

@Composable
private fun FlagsScoreRow(
    currentScore: Int,
    bestScore: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.game_flags_score, currentScore),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (bestScore > 0) {
            Text(
                text = stringResource(Res.string.game_flags_best, bestScore),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun FlagAnswerButtons(
    uiState: FlagsUiState,
    onAnswer: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        uiState.possibleAnswers.forEach { button ->
            FlagAnswerButton(button = button, onAnswer = onAnswer)
        }
    }
}

@Composable
private fun FlagAnswerButton(
    button: AnswerButton,
    onAnswer: (String) -> Unit,
) {
    val label = stringResource(countryNameRes(button.value))
    val face = when (button.state) {
        AnswerButtonState.NORMAL, AnswerButtonState.DIMMED -> Primary
        AnswerButtonState.WRONG -> MaterialTheme.colorScheme.errorContainer
        AnswerButtonState.CORRECT -> SuccessGreen
    }
    val textColor = when (button.state) {
        AnswerButtonState.WRONG -> MaterialTheme.colorScheme.onErrorContainer
        else -> ComposeColor.White
    }
    val isClickable = button.state == AnswerButtonState.NORMAL
    val containerModifier = Modifier
        .fillMaxWidth()
        .defaultMinSize(minHeight = 52.dp)
        .hoverHand(isClickable)
        .alpha(if (button.state == AnswerButtonState.DIMMED) 0.4f else 1f)

    PrismTile(
        face = face,
        modifier = containerModifier,
        isClickable = isClickable,
        onClick = { onAnswer(button.value) },
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
        )
    }
}
