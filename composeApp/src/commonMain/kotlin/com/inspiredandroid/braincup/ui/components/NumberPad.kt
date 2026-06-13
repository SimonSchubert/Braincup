package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.baseline_backspace_24
import com.inspiredandroid.braincup.ui.theme.numberFontFamily
import org.jetbrains.compose.resources.painterResource
import kotlin.collections.plusAssign

/** Layout order for [NumberPad]. When true the keypad shows 1-2-3 on the top row (phone style);
 *  when false it shows 7-8-9 on top (calculator style). Provided from the app's settings. */
val LocalNumberPadAscending = staticCompositionLocalOf { false }

@Composable
fun ColumnScope.NumberPad(
    showOperators: Boolean = false,
    onInputChange: (String) -> Unit,
) {
    // Number rows from top to bottom in the default (calculator) layout. Each row pairs its
    // digits with the operator anchored to that row position (top to bottom: / * -).
    val numberRows = listOf(
        Triple("7", "8", "9") to "/",
        Triple("4", "5", "6") to "*",
        Triple("1", "2", "3") to "-",
    )
    // Phone style flips the digits while the operators stay anchored to their row position.
    val orderedRows = if (LocalNumberPadAscending.current) {
        numberRows.map { it.first }.reversed().zip(numberRows.map { it.second })
    } else {
        numberRows.map { it.first to it.second }
    }

    Column(
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .width(IntrinsicSize.Min),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        orderedRows.forEach { (digits, operator) ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NumberPadButtonCell(digits.first) {
                    onInputChange(it)
                }
                NumberPadButtonCell(digits.second) {
                    onInputChange(it)
                }
                NumberPadButtonCell(digits.third) {
                    onInputChange(it)
                }
                if (showOperators) {
                    NumberPadButtonCell(operator) {
                        onInputChange(it)
                    }
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
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                fontFamily = numberFontFamily(),
                modifier = Modifier.align(Alignment.CenterVertically),
            )
            if (input.isNotEmpty()) {
                Spacer(Modifier.width(8.dp))
                PrismTile(
                    face = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.CenterVertically)
                        .hoverHand(),
                    onClick = {
                        input = input.take(input.lastIndex)
                    },
                ) {
                    Icon(
                        painterResource(Res.drawable.baseline_backspace_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp),
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
