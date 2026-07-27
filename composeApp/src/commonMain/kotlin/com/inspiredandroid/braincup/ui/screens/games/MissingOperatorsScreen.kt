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

@Composable
internal fun ColumnScope.MissingOperatorsContent(
    uiState: MissingOperatorsUiState,
    onAnswer: (String) -> Unit,
    onGiveUp: () -> Unit,
) {
    var enteredOperators by remember(uiState) {
        mutableStateOf(List<Operator?>(uiState.operatorsCount) { null })
    }
    var selectedSlotIndex by remember(uiState) { mutableStateOf(0) }

    @Composable
    fun EquationRow() {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            uiState.numbers.forEachIndexed { index, number ->
                MathText(
                    text = number.toString(),
                    style = MaterialTheme.typography.displaySmall
                )
                if (index < uiState.operatorsCount) {
                    val borderStroke = if (selectedSlotIndex == index) {
                        BorderStroke(2.5.dp, MaterialTheme.colorScheme.primary)
                    } else {
                        BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    }

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = if (selectedSlotIndex == index) {
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                },
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(
                                border = borderStroke,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .hoverHand()
                            .clickable {
                                selectedSlotIndex = index
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        val op = enteredOperators[index]
                        if (op != null) {
                            MathText(text = op.char.toString(), style = MaterialTheme.typography.titleLarge)
                        } else {
                            Text(text = " ", style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
            }
            MathText(
                text = " = ${uiState.targetResult}",
                style = MaterialTheme.typography.displaySmall
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
                                selectedSlotIndex = (selectedSlotIndex + 1).coerceAtMost(uiState.operatorsCount - 1)
                            }
                        }
                    },
                    value = op
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
                GiveUpButton(onGiveUp = onGiveUp)
            }
            Column {
                KeysRow()
            }
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            EquationRow()
            Spacer(Modifier.height(32.dp))
            KeysRow()
            Spacer(Modifier.height(32.dp))
            GiveUpButton(
                onGiveUp = onGiveUp,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
    }
}
