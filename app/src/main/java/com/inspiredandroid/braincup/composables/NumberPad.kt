package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.compose.MutableState
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.Text
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.text.TextStyle
import androidx.ui.unit.dp
import androidx.ui.unit.sp

@Composable
fun NumberPad(showOperators: Boolean = false, onInputChange: (String) -> Unit) {
    val input = state { "" }
    Column {
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.padding(16.dp) + Modifier.gravity(align = Alignment.CenterHorizontally)
        ) {
            Text(
                input.value,
                style = TextStyle(color = Color.Black, fontSize = 32.sp),
                modifier = Modifier.gravity(align = Alignment.CenterVertically)
            )
            if (input.value.isNotEmpty()) {
                Spacer(Modifier.preferredWidth(8.dp))

                TextButton(
                    text = "◄",
                    onClick = {
                        input.value = input.value.substring(0, input.value.lastIndex)
                    }, modifier = Modifier.gravity(align = Alignment.CenterVertically)
                )
            }
        }
        Table(
            columns = if (showOperators) {
                4
            } else {
                3
            }, columnWidth = {
                TableColumnWidth.Wrap
            }) {
            tableRow {
                NumberPadButton("7", input, onInputChange)
                NumberPadButton("8", input, onInputChange)
                NumberPadButton("9", input, onInputChange)
                if (showOperators) {
                    NumberPadButton("/", input, onInputChange)
                }
            }
            tableRow {
                NumberPadButton("4", input, onInputChange)
                NumberPadButton("5", input, onInputChange)
                NumberPadButton("6", input, onInputChange)
                if (showOperators) {
                    NumberPadButton("*", input, onInputChange)
                }
            }
            tableRow {
                NumberPadButton("1", input, onInputChange)
                NumberPadButton("2", input, onInputChange)
                NumberPadButton("3", input, onInputChange)
                if (showOperators) {
                    NumberPadButton("-", input, onInputChange)
                }
            }
            tableRow {
                if (showOperators) {
                    NumberPadButton("(", input, onInputChange)
                } else {
                    Box {}
                }
                NumberPadButton("0", input, onInputChange)
                if (showOperators) {
                    NumberPadButton(")", input, onInputChange)
                } else {
                    Box {}
                }
                if (showOperators) {
                    NumberPadButton("+", input, onInputChange)
                }
            }
        }
    }
}

@Composable
fun NumberRow(numbers: List<String>, onInputChange: (String) -> Unit) {
    val input = state { "" }
    Row {
        numbers.forEach {
            NumberPadButton(it, input, onInputChange)
        }
    }
}

@Composable
fun NumberPadButton(value: String, input: MutableState<String>, onInputChange: (String) -> Unit) {
    TextButton(value, onClick = {
        input.value += value
        onInputChange(input.value)
    }, modifier = Modifier.padding(4.dp))
}