package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.baseline_backspace_24
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.games.tools.Operator
import com.inspiredandroid.braincup.ui.components.*
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun ColumnScope.MissingOperatorsContent(
    uiState: MissingOperatorsUiState,
    onAnswer: (String) -> Unit,
    onGiveUp: () -> Unit,
) {
    var typedOperators by remember(uiState) { mutableStateOf(emptyList<Operator>()) }

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
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (index < typedOperators.size) {
                            val opSymbol = typedOperators[index].char.toString()
                            MathText(text = opSymbol, style = MaterialTheme.typography.titleLarge)
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
                        if (typedOperators.size < uiState.operatorsCount) {
                            typedOperators = typedOperators + Operator.entries.first { it.char.toString() == op }
                            if (typedOperators.size == uiState.operatorsCount) {
                                val answerStr = typedOperators.map { it.char }.joinToString("")
                                onAnswer(answerStr)
                            }
                        }
                    },
                    value = op
                )
            }

            PrismTile(
                face = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .size(56.dp)
                    .hoverHand(typedOperators.isNotEmpty()),
                isClickable = typedOperators.isNotEmpty(),
                onClick = {
                    if (typedOperators.isNotEmpty()) {
                        typedOperators = typedOperators.dropLast(1)
                    }
                },
            ) {
                Icon(
                    painterResource(Res.drawable.baseline_backspace_24),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp),
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
