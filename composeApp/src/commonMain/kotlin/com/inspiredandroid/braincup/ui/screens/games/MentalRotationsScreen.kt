package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.games.*
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.theme.*
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ColumnScope.MentalRotationsContent(
    uiState: MentalRotationsUiState,
    onAnswer: (String) -> Unit,
) {
    Text(
        text = stringResource(Res.string.game_mental_rotations_question),
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.align(Alignment.CenterHorizontally),
    )
    Spacer(Modifier.height(16.dp))

    // Bounded by height, not by width: the figures share this column with the answer buttons, so a
    // square derived from the available width would grow tall enough to sit underneath them.
    MentalRotationsPair(
        reference = uiState.reference,
        candidate = uiState.candidate,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .widthIn(max = 480.dp)
            .fillMaxWidth()
            .height(if (LocalIsCompactHeight.current) 170.dp else 230.dp)
            .align(Alignment.CenterHorizontally),
    )

    Spacer(Modifier.height(32.dp))

    Row(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .widthIn(max = 400.dp)
            .align(Alignment.CenterHorizontally),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        uiState.answers.forEach { button ->
            val label = when (button.value) {
                MentalRotationsGame.ANSWER_SAME -> stringResource(Res.string.game_mental_rotations_same)
                else -> stringResource(Res.string.game_mental_rotations_mirrored)
            }
            val face = when (button.state) {
                AnswerButtonState.WRONG -> MaterialTheme.colorScheme.errorContainer
                AnswerButtonState.CORRECT -> SuccessGreen
                else -> Primary
            }
            val contentColor = when (button.state) {
                AnswerButtonState.WRONG -> MaterialTheme.colorScheme.onErrorContainer
                else -> Color.White
            }
            val isInteractive = button.state == AnswerButtonState.NORMAL
            PrismTile(
                face = face,
                isClickable = isInteractive,
                onClick = { onAnswer(button.value) },
                modifier = Modifier
                    .weight(1f)
                    .height(72.dp)
                    .then(if (button.state == AnswerButtonState.DIMMED) Modifier.alpha(0.3f) else Modifier)
                    .then(if (isInteractive) Modifier.hoverHand() else Modifier),
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor,
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun MentalRotationsContentPreview() {
    val figure = listOf(
        Cube(0, 0, 0),
        Cube(1, 0, 0),
        Cube(2, 0, 0),
        Cube(2, 1, 0),
        Cube(2, 2, 0),
        Cube(2, 2, 1),
        Cube(2, 2, 2),
    )
    GamePreviewHost {
        MentalRotationsContent(
            uiState = MentalRotationsUiState(
                roundKey = 1,
                reference = figure.toProjection(),
                candidate = mirror(figure).toProjection(),
                answers = persistentListOf(
                    AnswerButton(MentalRotationsGame.ANSWER_SAME),
                    AnswerButton(MentalRotationsGame.ANSWER_MIRRORED),
                ),
            ),
            onAnswer = {},
        )
    }
}
