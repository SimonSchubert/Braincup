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
import com.inspiredandroid.braincup.games.RevealResult
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.theme.PrismSlot
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import com.inspiredandroid.braincup.ui.theme.SuccessGreenSoft
import org.jetbrains.compose.resources.stringResource

/** Height of the flash slot. Fixed so the layout never shifts as terms appear and clear. */
private val FlashSlotHeight = 136.dp
private val CompactFlashSlotHeight = 88.dp

/**
 * The flashed term is set well above `displayLarge`, which the rest of the app tops out at. It is
 * on screen for well under a second, it is the only thing on that screen, and its size is also
 * half of what tells a flashed term apart from the total revealed a second later.
 */
private val FlashFontSize = 100.sp
private val CompactFlashFontSize = 64.sp

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
private fun QuickSumFlashingContent(uiState: QuickSumUiState) {
    val compact = LocalIsCompactHeight.current
    val fontSize = if (compact) CompactFlashFontSize else FlashFontSize
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PhaseLabel(
            text = stringResource(Res.string.quick_sum_watch),
            accent = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(20.dp))
        Box(
            modifier = Modifier.height(if (compact) CompactFlashSlotHeight else FlashSlotHeight),
            contentAlignment = Alignment.Center,
        ) {
            // Null during the blank gap between terms; the slot keeps its height so the number
            // does not jump as the sequence steps.
            uiState.currentTerm?.let { term ->
                MathText(
                    text = term.toString(),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = fontSize,
                        lineHeight = fontSize * 1.1f,
                    ),
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
    val onInputChange: (String) -> Unit = { typed ->
        if (reveal == null && typed.length == uiState.answerLength) onAnswer(typed)
    }

    if (LocalIsCompactHeight.current) {
        CompactGameRow {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PhaseLabel(
                    text = stringResource(Res.string.quick_sum_instruction),
                    accent = MaterialTheme.colorScheme.tertiary,
                )
                if (reveal != null) {
                    Spacer(Modifier.height(12.dp))
                    QuickSumTotalCard(
                        total = reveal,
                        correct = uiState.answerResult == RevealResult.CORRECT,
                        compact = true,
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
            PhaseLabel(
                text = stringResource(Res.string.quick_sum_instruction),
                accent = MaterialTheme.colorScheme.tertiary,
            )
            if (reveal != null) {
                Spacer(Modifier.height(20.dp))
                QuickSumTotalCard(
                    total = reveal,
                    correct = uiState.answerResult == RevealResult.CORRECT,
                    compact = false,
                )
            } else {
                Spacer(Modifier.height(16.dp))
                NumberPadWithInput(onInputChange = onInputChange)
            }
        }
    }
}

/**
 * The revealed total, boxed and tinted.
 *
 * The reveal lands about a second after the last flashed term, and while both were a bare number
 * centred on an empty screen players read the total as one more term to add. The card gives the
 * answer an edge and a coloured face the flash phase never has, and the leading `=` says it is the
 * end of a sum rather than another number to add to one.
 */
@Composable
private fun QuickSumTotalCard(
    total: String,
    correct: Boolean,
    compact: Boolean,
) {
    Surface(
        shape = PrismSlot,
        color = if (correct) SuccessGreenSoft else MaterialTheme.colorScheme.errorContainer,
    ) {
        MathText(
            text = "= $total",
            style = if (compact) {
                MaterialTheme.typography.displayMedium
            } else {
                MaterialTheme.typography.displayLarge
            },
            color = if (correct) SuccessGreen else MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.padding(horizontal = 28.dp, vertical = if (compact) 10.dp else 16.dp),
        )
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

@DevicePreviews
@Composable
private fun QuickSumRevealPreview() {
    GamePreviewHost {
        QuickSumContent(
            uiState = QuickSumUiState(
                phase = QuickSumGame.Phase.ANSWER,
                currentTerm = null,
                termIndex = 3,
                termCount = 4,
                answerLength = 2,
                revealedSum = "16",
                answerResult = RevealResult.CORRECT,
            ),
            onAnswer = {},
        )
    }
}
