package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.theme.numberFontFamily

@Composable
internal fun ColumnScope.FractionCalculationContent(
    uiState: FractionCalculationUiState,
    onAnswer: (String) -> Unit,
    onGiveUp: () -> Unit,
) {
    val onInputChange: (String) -> Unit = { input ->
        if (input == uiState.answerString || input.length >= 4) {
            onAnswer(input)
        }
    }
    val expression: @Composable () -> Unit = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val parts = uiState.calculation.split(" * ")
            parts.forEachIndexed { index, part ->
                val fraction = part.removeSurrounding("(", ")")
                val fractionParts = fraction.split("/")
                if (fractionParts.size == 2) {
                    FractionText(
                        numerator = fractionParts[0],
                        denominator = fractionParts[1],
                        style = MaterialTheme.typography.displaySmall,
                    )
                } else {
                    Text(
                        part,
                        style = MaterialTheme.typography.displaySmall,
                        fontFamily = numberFontFamily(),
                    )
                }

                if (index < parts.size - 1) {
                    Text(
                        "\u00D7",
                        style = MaterialTheme.typography.displaySmall,
                        fontFamily = numberFontFamily(),
                    )
                }
            }
        }
    }

    if (LocalIsCompactHeight.current) {
        var input by remember(uiState.calculation) { mutableStateOf("") }
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
                expression()
                if (input.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "= $input",
                        style = MaterialTheme.typography.headlineSmall,
                        fontFamily = numberFontFamily(),
                    )
                }
                Spacer(Modifier.height(8.dp))
                GiveUpButton(onGiveUp = onGiveUp)
            }
            Column {
                NumberPad(onInputChange = { typed ->
                    val next = input + typed
                    input = next
                    if (next == uiState.answerString || next.length >= 4) {
                        onAnswer(next)
                    }
                })
            }
        }
    } else {
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 16.dp),
        ) {
            expression()
        }
        Spacer(Modifier.height(16.dp))
        NumberPadWithInput(onInputChange = onInputChange)
        Spacer(Modifier.height(16.dp))
        GiveUpButton(
            onGiveUp = onGiveUp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}

@GameDevicePreviews
@Composable
private fun FractionCalculationContentPreview() {
    GamePreviewHost {
        FractionCalculationContent(
            uiState = FractionCalculationUiState(calculation = "1/2 + 1/4", answerString = "3/4"),
            onAnswer = {},
            onGiveUp = {},
        )
    }
}
