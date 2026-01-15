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

@Composable
fun NumberPad(
    showOperators: Boolean = false,
    onInputChange: (String) -> Unit
) {
    var input by remember { mutableStateOf("") }

    Column {
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .padding(12.dp)
                .defaultMinSize(minHeight = 60.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                input,
                style = TextStyle(color = Color.Black, fontSize = 32.sp),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            if (input.isNotEmpty()) {
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        input = input.substring(0, input.lastIndex)
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Text("\u232B", style = TextStyle(fontSize = 24.sp))
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(IntrinsicSize.Min)
        ) {
            Row {
                NumberPadButtonCell("7") { input += it; onInputChange(input) }
                NumberPadButtonCell("8") { input += it; onInputChange(input) }
                NumberPadButtonCell("9") { input += it; onInputChange(input) }
                if (showOperators) {
                    NumberPadButtonCell("/") { input += it; onInputChange(input) }
                }
            }
            Row {
                NumberPadButtonCell("4") { input += it; onInputChange(input) }
                NumberPadButtonCell("5") { input += it; onInputChange(input) }
                NumberPadButtonCell("6") { input += it; onInputChange(input) }
                if (showOperators) {
                    NumberPadButtonCell("*") { input += it; onInputChange(input) }
                }
            }
            Row {
                NumberPadButtonCell("1") { input += it; onInputChange(input) }
                NumberPadButtonCell("2") { input += it; onInputChange(input) }
                NumberPadButtonCell("3") { input += it; onInputChange(input) }
                if (showOperators) {
                    NumberPadButtonCell("-") { input += it; onInputChange(input) }
                }
            }
            Row {
                if (showOperators) {
                    NumberPadButtonCell("(") { input += it; onInputChange(input) }
                    NumberPadButtonCell("0") { input += it; onInputChange(input) }
                    NumberPadButtonCell(")") { input += it; onInputChange(input) }
                    NumberPadButtonCell("+") { input += it; onInputChange(input) }
                } else {
                    EmptyCell()
                    NumberPadButtonCell("0") { input += it; onInputChange(input) }
                    EmptyCell()
                }
            }
        }
    }
}

@Composable
fun NumberRow(numbers: List<String>, onInputChange: (String) -> Unit) {
    var input by remember { mutableStateOf("") }
    Row {
        numbers.forEach { number ->
            Button(
                onClick = {
                    input += number
                    onInputChange(input)
                },
                modifier = Modifier
                    .padding(4.dp)
                    .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            ) {
                Text(number)
            }
        }
    }
}

@Composable
private fun RowScope.NumberPadButtonCell(
    value: String,
    onClick: (String) -> Unit
) {
    Box(
        modifier = Modifier.weight(1f, fill = true),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = { onClick(value) },
            modifier = Modifier
                .padding(4.dp)
                .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
        ) {
            Text(value)
        }
    }
}

@Composable
private fun RowScope.EmptyCell() {
    Box(modifier = Modifier.weight(1f, fill = true))
}
