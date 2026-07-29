package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.games.tools.Operator
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.theme.SuccessGreen

@Composable
internal fun ColumnScope.MissingOperatorsContent(
    uiState: MissingOperatorsUiState,
    onAnswer: (String) -> Unit,
    onGiveUp: () -> Unit,
) {
    val isFeedback = uiState.correctOperators != null
    var enteredOperators by remember(uiState.numbers, uiState.targetResult, uiState.operatorsCount) {
        mutableStateOf(List<Operator?>(uiState.operatorsCount) { null })
    }
    var selectedSlotIndex by remember(uiState.numbers, uiState.targetResult, uiState.operatorsCount) {
        mutableStateOf(0)
    }

    @Composable
    fun EquationRow() {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            uiState.numbers.forEachIndexed { index, number ->
                MathText(
                    text = number.toString(),
                    style = MaterialTheme.typography.displaySmall,
                )
                if (index < uiState.operatorsCount) {
                    OperatorSlot(
                        index = index,
                        uiState = uiState,
                        enteredOperators = enteredOperators,
                        selectedSlotIndex = selectedSlotIndex,
                        isFeedback = isFeedback,
                        onSelect = {
                            if (!isFeedback) {
                                selectedSlotIndex = index
                            }
                        },
                    )
                }
            }
            MathText(
                text = " = ${uiState.targetResult}",
                style = MaterialTheme.typography.displaySmall,
            )
        }
    }

    @Composable
    fun KeysRow() {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val ops = listOf("+", "-", "*", "/")
            ops.forEach { op ->
                CircleButton(
                    onClick = {
                        if (isFeedback) return@CircleButton
                        val selectedOp = Operator.entries.first { it.char.toString() == op }
                        enteredOperators = enteredOperators.toMutableList().apply {
                            this[selectedSlotIndex] = selectedOp
                        }

                        if (enteredOperators.all { it != null }) {
                            val answerStr = enteredOperators.map { it?.char }.joinToString("")
                            onAnswer(answerStr)
                        } else {
                            val nextEmptyIndex = enteredOperators.indexOfFirst { it == null }
                            if (nextEmptyIndex != -1) {
                                selectedSlotIndex = nextEmptyIndex
                            } else {
                                selectedSlotIndex =
                                    (selectedSlotIndex + 1).coerceAtMost(uiState.operatorsCount - 1)
                            }
                        }
                    },
                    value = op,
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                EquationRow()
                Spacer(Modifier.height(16.dp))
                if (!isFeedback) {
                    GiveUpButton(onGiveUp = onGiveUp)
                }
            }
            Column {
                KeysRow()
            }
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
        ) {
            EquationRow()
            Spacer(Modifier.height(32.dp))
            KeysRow()
            Spacer(Modifier.height(32.dp))
            if (!isFeedback) {
                GiveUpButton(
                    onGiveUp = onGiveUp,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
            }
        }
    }
}

@Composable
private fun OperatorSlot(
    index: Int,
    uiState: MissingOperatorsUiState,
    enteredOperators: List<Operator?>,
    selectedSlotIndex: Int,
    isFeedback: Boolean,
    onSelect: () -> Unit,
) {
    val submitted = uiState.submittedOperators?.getOrNull(index)
    val correct = uiState.correctOperators?.getOrNull(index)
    val entered = enteredOperators.getOrNull(index)

    val wasAlreadyCorrect = submitted != null && correct != null && submitted == correct
    val isRevealedCorrect = isFeedback &&
        (index in uiState.revealedCorrectIndices || wasAlreadyCorrect)
    val isWrongPending = isFeedback &&
        submitted != null &&
        correct != null &&
        submitted != correct &&
        index !in uiState.revealedCorrectIndices

    val displayOp: Operator? = when {
        isRevealedCorrect -> correct
        isFeedback && submitted != null -> submitted
        isFeedback -> null
        else -> entered
    }

    val backgroundColor = when {
        isWrongPending -> MaterialTheme.colorScheme.errorContainer
        isRevealedCorrect -> SuccessGreen.copy(alpha = 0.2f)
        selectedSlotIndex == index && !isFeedback ->
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val borderStroke = when {
        isWrongPending -> BorderStroke(2.5.dp, MaterialTheme.colorScheme.error)
        isRevealedCorrect -> BorderStroke(2.5.dp, SuccessGreen)
        selectedSlotIndex == index && !isFeedback ->
            BorderStroke(2.5.dp, MaterialTheme.colorScheme.primary)
        else -> BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    }

    val textColor = when {
        isWrongPending -> MaterialTheme.colorScheme.onErrorContainer
        isRevealedCorrect -> SuccessGreen
        else -> LocalContentColor.current
    }

    Box(
        modifier = Modifier
            .size(56.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp),
            )
            .border(
                border = borderStroke,
                shape = RoundedCornerShape(8.dp),
            )
            .then(
                if (!isFeedback) {
                    Modifier
                        .hoverHand()
                        .clickable(onClick = onSelect)
                } else {
                    Modifier
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (displayOp != null) {
            MathText(
                text = displayOp.char.toString(),
                style = MaterialTheme.typography.headlineLarge,
                color = textColor,
            )
        } else {
            Text(text = " ", style = MaterialTheme.typography.headlineLarge)
        }
    }
}
