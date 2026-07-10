package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.games.tools.Calculator
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.theme.annotateNumbers
import com.inspiredandroid.braincup.ui.theme.numberFontFamily
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ColumnScope.SherlockCalculationContent(
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
                text = annotateNumbers(stringResource(Res.string.game_goal, uiState.result)),
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
                        checkAnswer()
                    }
                },
                onBackspace = {
                    if (!showingSolution && expressionTokens.isNotEmpty()) {
                        val lastToken = expressionTokens.removeAt(expressionTokens.lastIndex)
                        if (lastToken is ExpressionToken.NumberToken) {
                            usedNumberIndices.remove(lastToken.originalIndex)
                        }
                        checkAnswer()
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
                        checkAnswer()
                    }
                },
                modifier = mod,
            )
        }

        if (compact) {
            // Weight the goal/expression side so it only takes leftover space after the
            // numbers/operators pad measures to its intrinsic width. Without weight,
            // ExpressionRow's fillMaxWidth() expands the left column to the full Row
            // width and squeezes the pad off-screen.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
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
                    // No extra horizontal padding — the Row already insets 16.dp and the
                    // pad must keep an intrinsic width so it is not clipped.
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
            numbersRow(centered.padding(horizontal = 16.dp))
            Spacer(Modifier.height(12.dp))
            operatorRow(centered.padding(horizontal = 16.dp))
            Spacer(Modifier.height(16.dp))
            giveUpButton(centered)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ExpressionRow(
    tokens: ImmutableList<ExpressionToken>,
    onTokenClick: (Int) -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        // fillMaxWidth so the empty-state hint + backspace center; in compact layout the
        // parent Column is weight-constrained so this no longer steals the pad's space.
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
                                fontFamily = numberFontFamily(),
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
        modifier = modifier,
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
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = numberFontFamily(),
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
        modifier = modifier,
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

@DevicePreviews
@Composable
private fun SherlockCalculationContentPreview() {
    GamePreviewHost {
        SherlockCalculationContent(
            uiState = SherlockCalculationUiState(
                result = 42,
                numbers = persistentListOf(4, 9, 3, 7, 2),
            ),
            onAnswer = {},
            onGiveUp = {},
        )
    }
}
