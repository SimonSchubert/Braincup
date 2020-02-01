package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.compose.MutableState
import androidx.compose.state
import androidx.ui.core.Text
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.Button
import androidx.ui.text.TextStyle
import androidx.ui.unit.dp
import androidx.ui.unit.sp

@Composable
fun NumberPad(showOperators: Boolean = false, onInputChange: (String) -> Unit) {
    val input = state { "" }
    Column {
        Row(arrangement = Arrangement.End, modifier = LayoutPadding(16.dp) + LayoutGravity.Center) {
            Text(
                input.value,
                style = TextStyle(color = Color.Black, fontSize = 32.sp),
                modifier = LayoutGravity.Center
            )
            if (input.value.isNotEmpty()) {
                Spacer(LayoutWidth(8.dp))
                Button(
                    "◄", onClick = {
                        input.value = input.value.substring(0, input.value.lastIndex)
                    }, modifier = LayoutGravity.Center
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
                    Padding(3.dp) {}
                }
                NumberPadButton("0", input, onInputChange)
                if (showOperators) {
                    NumberPadButton(")", input, onInputChange)
                } else {
                    Padding(3.dp) {}
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
    Button(value, onClick = {
        input.value += value
        onInputChange(input.value)
    }, modifier = LayoutPadding(4.dp))
}