package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.baseline_backspace_24
import org.jetbrains.compose.resources.painterResource
import kotlin.collections.plusAssign

@Composable
fun ColumnScope.NumberPad(
    showOperators: Boolean = false,
    onInputChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .width(IntrinsicSize.Min),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NumberPadButtonCell("7") {
                onInputChange(it)
            }
            NumberPadButtonCell("8") {
                onInputChange(it)
            }
            NumberPadButtonCell("9") {
                onInputChange(it)
            }
            if (showOperators) {
                NumberPadButtonCell("/") {
                    onInputChange(it)
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NumberPadButtonCell("4") {
                onInputChange(it)
            }
            NumberPadButtonCell("5") {
                onInputChange(it)
            }
            NumberPadButtonCell("6") {
                onInputChange(it)
            }
            if (showOperators) {
                NumberPadButtonCell("*") {
                    onInputChange(it)
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NumberPadButtonCell("1") {
                onInputChange(it)
            }
            NumberPadButtonCell("2") {
                onInputChange(it)
            }
            NumberPadButtonCell("3") {
                onInputChange(it)
            }
            if (showOperators) {
                NumberPadButtonCell("-") {
                    onInputChange(it)
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (showOperators) {
                NumberPadButtonCell("(") {
                    onInputChange(it)
                }
                NumberPadButtonCell("0") {
                    onInputChange(it)
                }
                NumberPadButtonCell(")") {
                    onInputChange(it)
                }
                NumberPadButtonCell("+") {
                    onInputChange(it)
                }
            } else {
                EmptyCell()
                NumberPadButtonCell("0") {
                    onInputChange(it)
                }
                EmptyCell()
            }
        }
    }
}

@Composable
fun NumberPadWithInput(
    showOperators: Boolean = false,
    onInputChange: (String) -> Unit,
) {
    var input by remember { mutableStateOf("") }

    Column {
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .padding(12.dp)
                .defaultMinSize(minHeight = 60.dp)
                .align(Alignment.CenterHorizontally),
        ) {
            Text(
                input,
                style = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 32.sp),
                modifier = Modifier.align(Alignment.CenterVertically),
            )
            if (input.isNotEmpty()) {
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        input = input.take(input.lastIndex)
                    },
                    modifier = Modifier.align(Alignment.CenterVertically),
                ) {
                    Icon(
                        painterResource(Res.drawable.baseline_backspace_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }

        NumberPad(
            showOperators = showOperators,
            onInputChange = {
                input += it
                onInputChange(input)
            },
        )
    }
}

@Composable
private fun RowScope.NumberPadButtonCell(
    value: String,
    onClick: (String) -> Unit,
) {
    Box(
        modifier = Modifier.weight(1f, fill = true),
        contentAlignment = Alignment.Center,
    ) {
        CircleButton(
            onClick = { onClick(value) },
            value = value,
        )
    }
}

@Composable
private fun RowScope.EmptyCell() {
    Box(modifier = Modifier.weight(1f, fill = true))
}
