package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.ui.components.*
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ColumnScope.ValueComparisonContent(
    uiState: ValueComparisonUiState,
    onAnswer: (String) -> Unit,
) {
    Text(
        text = stringResource(Res.string.game_highest_value),
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.align(Alignment.CenterHorizontally),
    )
    Spacer(Modifier.height(16.dp))

    uiState.answers.forEachIndexed { index, button ->
        val colors = answerTileColors(button.state)
        val contentColor = colors.content
        PrismTile(
            face = colors.face,
            isClickable = colors.isInteractive,
            onClick = { onAnswer((index + 1).toString()) },
            modifier = Modifier
                .padding(4.dp)
                .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                .answerTileState(button.state),
        ) {
            if (button.value.contains("/")) {
                val parts = button.value.split("/")
                FractionText(
                    numerator = parts[0],
                    denominator = parts[1],
                    style = MaterialTheme.typography.bodyLarge,
                    color = contentColor,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                )
            } else {
                MathText(
                    button.value,
                    color = contentColor,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun ValueComparisonContentPreview() {
    GamePreviewHost {
        ValueComparisonContent(
            uiState = ValueComparisonUiState(
                answers = persistentListOf(AnswerButton("3 + 4"), AnswerButton("2 × 5")),
            ),
            onAnswer = {},
        )
    }
}
