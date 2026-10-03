package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.game_flock_howto
import com.inspiredandroid.braincup.app.AnswerFeedbackState
import com.inspiredandroid.braincup.app.FlockUiState
import com.inspiredandroid.braincup.games.FlockGame
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.theme.ErrorRed
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import org.jetbrains.compose.resources.stringResource
import kotlin.math.abs

/**
 * The flock alone in a tall swipe surface. The surface is much larger than the flock so a swipe
 * can start anywhere near it, and the answer fires the moment the finger has travelled far enough
 * rather than on release, so the reading is a decision time and not a decision-plus-lift time.
 */
@Composable
internal fun ColumnScope.FlockContent(
    uiState: FlockUiState,
    onAnswer: (String) -> Unit,
) {
    val compact = LocalIsCompactHeight.current
    val isAwaitingNextTrial = uiState.feedback != AnswerFeedbackState.NORMAL
    val currentOnAnswer by rememberUpdatedState(onAnswer)
    val currentIsAwaitingNextTrial by rememberUpdatedState(isAwaitingNextTrial)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(if (compact) 200.dp else 320.dp)
            .pointerInput(Unit) {
                awaitSwipes { direction ->
                    if (!currentIsAwaitingNextTrial) currentOnAnswer(direction.name)
                }
            },
    ) {
        FlockRow(
            target = uiState.target,
            flankers = uiState.flankers,
            feedback = uiState.feedback,
            modifier = Modifier.padding(horizontal = 24.dp).widthIn(max = 380.dp),
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(if (compact) 28.dp else 36.dp)
            .align(Alignment.CenterHorizontally),
    ) {
        when (uiState.feedback) {
            AnswerFeedbackState.CORRECT -> ChunkyCheck(SuccessGreen, Modifier.fillMaxSize())
            AnswerFeedbackState.WRONG -> ChunkyCross(ErrorRed, Modifier.fillMaxSize())
            else -> Unit
        }
    }

    Spacer(Modifier.height(if (compact) 8.dp else 16.dp))

    BoardInstructionLine(
        text = stringResource(Res.string.game_flock_howto),
        isError = false,
        modifier = Modifier.align(Alignment.CenterHorizontally).padding(horizontal = 24.dp),
    )
}

private suspend fun PointerInputScope.awaitSwipes(onSwipe: (FlockGame.Direction) -> Unit) {
    val threshold = 24.dp.toPx()
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        var travelled = Offset.Zero
        while (true) {
            val change = awaitPointerEvent().changes.firstOrNull { it.id == down.id } ?: break
            if (!change.pressed) break
            travelled += change.positionChange()
            // Consumed so a compact layout's scrolling column cannot take a vertical swipe.
            change.consume()
            if (travelled.getDistance() >= threshold) {
                onSwipe(travelled.toDirection())
                break
            }
        }
    }
}

private fun Offset.toDirection(): FlockGame.Direction = if (abs(x) >= abs(y)) {
    if (x > 0) FlockGame.Direction.RIGHT else FlockGame.Direction.LEFT
} else {
    if (y > 0) FlockGame.Direction.DOWN else FlockGame.Direction.UP
}

@DevicePreviews
@Composable
private fun FlockContentPreview() {
    GamePreviewHost {
        FlockContent(
            uiState = FlockUiState(
                target = FlockGame.Direction.LEFT,
                flankers = FlockGame.Direction.RIGHT,
                feedback = AnswerFeedbackState.NORMAL,
            ),
            onAnswer = {},
        )
    }
}
