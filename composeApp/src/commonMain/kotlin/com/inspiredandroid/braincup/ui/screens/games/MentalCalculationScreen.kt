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

@Composable
internal fun ColumnScope.MentalCalculationContent(
    uiState: MentalCalculationUiState,
    onAnswer: (String) -> Unit,
) {
    if (LocalIsCompactHeight.current) {
        var input by remember(uiState.calculation) { mutableStateOf("") }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MathText(
                text = "${uiState.calculation} = ${input.ifEmpty { "?" }}",
                style = MaterialTheme.typography.displaySmall,
            )
            Column {
                NumberPad(onInputChange = { typed ->
                    val next = input + typed
                    input = next
                    if (uiState.answerLength == next.length) {
                        onAnswer(next)
                    }
                })
            }
        }
    } else {
        MathText(
            text = uiState.calculation,
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(16.dp))
        NumberPadWithInput(onInputChange = { typed ->
            if (uiState.answerLength == typed.length) {
                onAnswer(typed)
            }
        })
    }
}

@GameDevicePreviews
@Composable
private fun MentalCalculationContentPreview() {
    GamePreviewHost {
        MentalCalculationContent(
            uiState = MentalCalculationUiState(calculation = "12 + 7", answerLength = 2),
            onAnswer = {},
        )
    }
}
