package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.games.DigitMemoryGame
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.graphics.Color as ComposeColor

@Composable
internal fun ColumnScope.DigitMemoryContent(
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
                DigitMemorySlots(
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
                    DigitMemorySlots(
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

@Composable
private fun DigitMemoryPhaseLabel(text: String, accent: ComposeColor) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = accent,
        letterSpacing = 3.sp,
    )
}

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

@GameDevicePreviews
@Composable
private fun DigitMemoryContentPreview() {
    GamePreviewHost {
        DigitMemoryContent(
            uiState = DigitMemoryUiState(
                phase = DigitMemoryGame.Phase.SHOWING,
                sequence = "4829",
                sequenceLength = 4,
                problem = "3 + 4",
                answerLength = 1,
                revealedMathAnswer = null,
                recallResult = null,
            ),
            onAnswer = {},
        )
    }
}
