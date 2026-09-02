package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.game_color_confusion_howto
import com.inspiredandroid.braincup.app.AnswerFeedbackState
import com.inspiredandroid.braincup.app.ColorConfusionUiState
import com.inspiredandroid.braincup.app.ColorSwatchCell
import com.inspiredandroid.braincup.games.ColorConfusionGame
import com.inspiredandroid.braincup.games.tools.GameColor
import com.inspiredandroid.braincup.games.tools.composeColor
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.localizedName
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource

/**
 * The word alone above a fixed row of ink swatches. Nothing else is on screen: the task is over in
 * under a second, and anything else to look at would be measured as part of it.
 */
@Composable
internal fun ColumnScope.ColorConfusionContent(
    uiState: ColorConfusionUiState,
    onAnswer: (String) -> Unit,
) {
    val compact = LocalIsCompactHeight.current

    // Big, because the ink is the signal and a large glyph carries more of it. Uppercase is how
    // the task is printed, and it also keeps every word the same visual weight.
    Text(
        text = uiState.word.localizedName().uppercase(),
        style = if (compact) MaterialTheme.typography.displaySmall else MaterialTheme.typography.displayMedium,
        fontWeight = FontWeight.Bold,
        color = uiState.ink.composeColor(),
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(horizontal = 24.dp),
    )

    Spacer(Modifier.height(if (compact) 24.dp else 40.dp))

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .widthIn(max = 84.dp * uiState.swatches.size)
            .align(Alignment.CenterHorizontally),
    ) {
        uiState.swatches.forEachIndexed { index, swatch ->
            ColorSwatchTile(
                swatch = swatch,
                markSize = if (compact) 22.dp else 28.dp,
                // The whole row stops taking taps for the feedback beat, not just the swatch that
                // was tapped: the trial is already decided and the game ignores the input anyway.
                isEnabled = !uiState.isAwaitingNextTrial,
                onClick = { onAnswer("${index + 1}") },
                modifier = Modifier.weight(1f).aspectRatio(1f).padding(4.dp),
            )
        }
    }

    Spacer(Modifier.height(if (compact) 16.dp else 26.dp))

    BoardInstructionLine(
        text = stringResource(Res.string.game_color_confusion_howto),
        isError = false,
        modifier = Modifier.align(Alignment.CenterHorizontally).padding(horizontal = 24.dp),
    )
}

/**
 * A swatch keeps its own colour whatever happened, because the colour is what it means: recolouring
 * the tapped one green or red would make the row briefly lie about which answer is which. The
 * verdict is a tick or a cross drawn over it instead.
 */
@Composable
private fun ColorSwatchTile(
    swatch: ColorSwatchCell,
    markSize: Dp,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PrismTile(
        face = swatch.color.composeColor(),
        isClickable = isEnabled,
        isSelected = swatch.state != AnswerFeedbackState.NORMAL,
        onClick = if (isEnabled) onClick else ({}),
        modifier = modifier.hoverHand(isEnabled),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            when (swatch.state) {
                AnswerFeedbackState.CORRECT -> ChunkyCheck(Color.White, Modifier.size(markSize))
                AnswerFeedbackState.WRONG -> ChunkyCross(Color.White, Modifier.size(markSize))
                else -> Unit
            }
        }
    }
}

@DevicePreviews
@Composable
private fun ColorConfusionContentPreview() {
    GamePreviewHost {
        ColorConfusionContent(
            uiState = ColorConfusionUiState(
                word = GameColor.GREEN,
                ink = GameColor.RED,
                swatches = ColorConfusionGame.RESPONSE_COLORS
                    .map { ColorSwatchCell(it) }
                    .toImmutableList(),
                isAwaitingNextTrial = false,
            ),
            onAnswer = {},
        )
    }
}
