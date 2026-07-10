package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.ui.components.*

@Composable
internal fun ColumnScope.ChainCalculationContent(
    uiState: ChainCalculationUiState,
    onAnswer: (String) -> Unit,
    onGiveUp: () -> Unit,
) {
    val onInputChange: (String) -> Unit = { input ->
        if (input.toIntOrNull() == uiState.answer) {
            onAnswer(input)
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
                MathText(
                    text = "${uiState.calculation} = ${input.ifEmpty { "?" }}",
                    style = MaterialTheme.typography.displaySmall,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(8.dp))
                GiveUpButton(onGiveUp = onGiveUp)
            }
            Column {
                NumberPad(onInputChange = { typed ->
                    val next = input + typed
                    input = next
                    if (next.toIntOrNull() == uiState.answer) {
                        onAnswer(next)
                    }
                })
            }
        }
    } else {
        MathText(
            text = "${uiState.calculation} = ?",
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )
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
private fun ChainCalculationContentPreview() {
    GamePreviewHost {
        ChainCalculationContent(
            uiState = ChainCalculationUiState(calculation = "3 + 4 × 2", answer = 11),
            onAnswer = {},
            onGiveUp = {},
        )
    }
}
