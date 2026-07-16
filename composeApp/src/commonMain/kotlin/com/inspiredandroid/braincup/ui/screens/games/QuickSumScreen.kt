package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.games.QuickSumGame
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.graphics.Color as ComposeColor

/** Height of the flash slot. Fixed so the layout never shifts as terms appear and clear. */
private val FlashSlotHeight = 96.dp

@Composable
internal fun ColumnScope.QuickSumContent(
    uiState: QuickSumUiState,
    onAnswer: (String) -> Unit,
) {
    Spacer(Modifier.weight(1f))
    when (uiState.phase) {
        QuickSumGame.Phase.FLASHING -> QuickSumFlashingContent(uiState)
        QuickSumGame.Phase.ANSWER -> QuickSumAnswerContent(uiState, onAnswer)
    }
    Spacer(Modifier.weight(1f))
}

@Composable
private fun QuickSumPhaseLabel(text: String, accent: ComposeColor) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = accent,
        letterSpacing = 3.sp,
    )
}

@Composable
private fun QuickSumFlashingContent(uiState: QuickSumUiState) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        QuickSumPhaseLabel(
            text = stringResource(Res.string.quick_sum_watch),
            accent = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(20.dp))
        Box(
            modifier = Modifier.height(FlashSlotHeight),
            contentAlignment = Alignment.Center,
        ) {
            // Null during the blank gap between terms; the slot keeps its height so the number
            // does not jump as the sequence steps.
            uiState.currentTerm?.let { term ->
                MathText(
                    text = term.toString(),
                    style = MaterialTheme.typography.displayLarge,
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        QuickSumProgressDots(index = uiState.termIndex, count = uiState.termCount)
    }
}

@Composable
private fun QuickSumProgressDots(index: Int, count: Int) {
    val accent = MaterialTheme.colorScheme.primary
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(count) { i ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (i <= index) accent else accent.copy(alpha = 0.25f)),
            )
        }
    }
}

@Composable
private fun QuickSumAnswerContent(
    uiState: QuickSumUiState,
    onAnswer: (String) -> Unit,
) {
    val reveal = uiState.revealedSum
    val revealColor = when (uiState.answerResult) {
        QuickSumGame.AnswerResult.CORRECT -> SuccessGreen
        QuickSumGame.AnswerResult.WRONG -> MaterialTheme.colorScheme.error
        null -> ComposeColor.Unspecified
    }
    val onInputChange: (String) -> Unit = { typed ->
        if (reveal == null && typed.length == uiState.answerLength) onAnswer(typed)
    }

    if (LocalIsCompactHeight.current) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                QuickSumPhaseLabel(
                    text = stringResource(Res.string.quick_sum_instruction),
                    accent = MaterialTheme.colorScheme.tertiary,
                )
                if (reveal != null) {
                    Spacer(Modifier.height(12.dp))
                    MathText(
                        text = reveal,
                        style = MaterialTheme.typography.displayMedium,
                        color = revealColor,
                    )
                }
            }
            if (reveal == null) {
                Column { NumberPadWithInput(onInputChange = onInputChange) }
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            QuickSumPhaseLabel(
                text = stringResource(Res.string.quick_sum_instruction),
                accent = MaterialTheme.colorScheme.tertiary,
            )
            if (reveal != null) {
                Spacer(Modifier.height(20.dp))
                MathText(
                    text = reveal,
                    style = MaterialTheme.typography.displayLarge,
                    color = revealColor,
                )
            } else {
                Spacer(Modifier.height(16.dp))
                NumberPadWithInput(onInputChange = onInputChange)
            }
        }
    }
}

@DevicePreviews
@Composable
private fun QuickSumFlashingPreview() {
    GamePreviewHost {
        QuickSumContent(
            uiState = QuickSumUiState(
                phase = QuickSumGame.Phase.FLASHING,
                currentTerm = 7,
                termIndex = 1,
                termCount = 4,
                answerLength = 2,
                revealedSum = null,
                answerResult = null,
            ),
            onAnswer = {},
        )
    }
}

@DevicePreviews
@Composable
private fun QuickSumAnswerPreview() {
    GamePreviewHost {
        QuickSumContent(
            uiState = QuickSumUiState(
                phase = QuickSumGame.Phase.ANSWER,
                currentTerm = null,
                termIndex = 3,
                termCount = 4,
                answerLength = 2,
                revealedSum = null,
                answerResult = null,
            ),
            onAnswer = {},
        )
    }
}
