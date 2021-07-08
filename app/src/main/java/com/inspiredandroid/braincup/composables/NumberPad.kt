package com.inspiredandroid.braincup.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NumberPad(showOperators: Boolean = false, onInputChange: (String) -> Unit) {
    val input = remember { mutableStateOf("") }
    Column {
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .padding(16.dp)
                .align(CenterHorizontally)
        ) {
            Text(
                input.value,
                style = TextStyle(color = Color.Black, fontSize = 32.sp),
                modifier = Modifier.align(CenterVertically)
            )
            if (input.value.isNotEmpty()) {
                Spacer(Modifier.width(8.dp))

                TextButton(
                    text = "◄",
                    onClick = {
                        input.value = input.value.substring(0, input.value.lastIndex)
                    }, modifier = Modifier.align(CenterVertically)
                )
            }
        }

        Column(
            modifier = Modifier
                .align(CenterHorizontally)
                .width(IntrinsicSize.Min)
        ) {
            Row {
                NumberPadButtonCell("7", input, onInputChange)
                NumberPadButtonCell("8", input, onInputChange)
                NumberPadButtonCell("9", input, onInputChange)
                if (showOperators) {
                    NumberPadButtonCell("/", input, onInputChange)
                }
            }
            Row {
                NumberPadButtonCell("4", input, onInputChange)
                NumberPadButtonCell("5", input, onInputChange)
                NumberPadButtonCell("6", input, onInputChange)
                if (showOperators) {
                    NumberPadButtonCell("*", input, onInputChange)
                }
            }
            Row {
                NumberPadButtonCell("1", input, onInputChange)
                NumberPadButtonCell("2", input, onInputChange)
                NumberPadButtonCell("3", input, onInputChange)
                if (showOperators) {
                    NumberPadButtonCell("-", input, onInputChange)
                }
            }
            Row {
                if (showOperators) {
                    NumberPadButtonCell("(", input, onInputChange)
                    NumberPadButtonCell("0", input, onInputChange)
                    NumberPadButtonCell(")", input, onInputChange)
                    NumberPadButtonCell("+", input, onInputChange)
                } else {
                    Cell {}
                    NumberPadButtonCell("0", input, onInputChange)
                    Cell {}
                }
            }
        }
    }
}

@Composable
fun NumberRow(numbers: List<String>, onInputChange: (String) -> Unit) {
    val input = remember { mutableStateOf("") }
    Row {
        numbers.forEach {
            NumberPadButton(it, input, onInputChange)
        }
    }
}

@Composable
private fun RowScope.Cell(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier.weight(1f, fill = true),
        propagateMinConstraints = true
    ) {
        content()
    }
}

@Composable
private fun RowScope.NumberPadButtonCell(
    value: String,
    input: MutableState<String>,
    onInputChange: (String) -> Unit
) {
    Cell {
        NumberPadButton(value, input, onInputChange)
    }
}

@Composable
private fun NumberPadButton(
    value: String,
    input: MutableState<String>,
    onInputChange: (String) -> Unit
) {
    TextButton(value, onClick = {
        input.value += value
        onInputChange(input.value)
    }, modifier = Modifier.padding(4.dp))
}
