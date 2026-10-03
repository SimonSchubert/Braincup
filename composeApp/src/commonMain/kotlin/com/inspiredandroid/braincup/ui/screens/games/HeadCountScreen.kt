package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.games.HeadCountGame
import com.inspiredandroid.braincup.games.RevealResult
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.theme.PrismSlot
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import com.inspiredandroid.braincup.ui.theme.SuccessGreenSoft
import org.jetbrains.compose.resources.stringResource

private val HouseMaxWidth = 420.dp
private val CompactHouseHeight = 150.dp

/**
 * Share of a step the walk takes. The rest is a still beat with nobody moving, so one batch never
 * runs into the next and each move reads as its own event.
 */
private const val WalkFraction = 0.8f

@Composable
internal fun ColumnScope.HeadCountContent(
    uiState: HeadCountUiState,
    onAnswer: (String) -> Unit,
) {
    Spacer(Modifier.weight(1f))
    when (uiState.phase) {
        HeadCountGame.Phase.WATCHING -> HeadCountWatchingContent(uiState)
        HeadCountGame.Phase.ANSWER -> HeadCountAnswerContent(uiState, onAnswer)
    }
    Spacer(Modifier.weight(1f))
}

@Composable
private fun HeadCountWatchingContent(uiState: HeadCountUiState) {
    val walk = remember { Animatable(0f) }
    LaunchedEffect(uiState.moveKey) {
        walk.snapTo(0f)
        walk.animateTo(
            targetValue = 1f,
            animationSpec = tween((uiState.stepMillis * WalkFraction).toInt(), easing = LinearEasing),
        )
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PhaseLabel(
            text = stringResource(Res.string.head_count_watch),
            accent = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(20.dp))
        HeadCountHouse(
            entering = uiState.entering,
            leaving = uiState.leaving,
            entersFromLeft = uiState.entersFromLeft,
            walkProgress = walk.value,
            modifier = Modifier.houseSize(),
        )
        Spacer(Modifier.height(20.dp))
        HeadCountProgressDots(index = uiState.moveIndex, count = uiState.moveCount)
    }
}

@Composable
private fun Modifier.houseSize(): Modifier = if (LocalIsCompactHeight.current) {
    height(CompactHouseHeight).aspectRatio(HeadCountSceneAspect)
} else {
    widthIn(max = HouseMaxWidth).fillMaxWidth().padding(horizontal = 16.dp).aspectRatio(HeadCountSceneAspect)
}

@Composable
private fun HeadCountProgressDots(index: Int, count: Int) {
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
private fun HeadCountAnswerContent(
    uiState: HeadCountUiState,
    onAnswer: (String) -> Unit,
) {
    val reveal = uiState.revealedCount
    val onInputChange: (String) -> Unit = { typed ->
        if (reveal == null && typed.isNotEmpty()) onAnswer(typed)
    }
    val correct = uiState.answerResult == RevealResult.CORRECT

    if (LocalIsCompactHeight.current) {
        CompactGameRow {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PhaseLabel(
                    text = stringResource(Res.string.head_count_instruction),
                    accent = MaterialTheme.colorScheme.tertiary,
                )
                if (reveal != null) {
                    Spacer(Modifier.height(12.dp))
                    HeadCountRevealCard(count = reveal, correct = correct, compact = true)
                }
            }
            if (reveal == null) {
                Column { NumberPadWithInput(onInputChange = onInputChange) }
            } else {
                HeadCountRevealedHouse(count = reveal)
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PhaseLabel(
                text = stringResource(Res.string.head_count_instruction),
                accent = MaterialTheme.colorScheme.tertiary,
            )
            if (reveal != null) {
                Spacer(Modifier.height(20.dp))
                HeadCountRevealedHouse(count = reveal)
                Spacer(Modifier.height(20.dp))
                HeadCountRevealCard(count = reveal, correct = correct, compact = false)
            } else {
                Spacer(Modifier.height(16.dp))
                NumberPadWithInput(onInputChange = onInputChange)
            }
        }
    }
}

@Composable
private fun HeadCountRevealedHouse(count: Int) {
    HeadCountHouse(
        entering = 0,
        leaving = 0,
        entersFromLeft = true,
        walkProgress = 0f,
        revealedOccupants = count,
        modifier = Modifier.houseSize(),
    )
}

@Composable
private fun HeadCountRevealCard(
    count: Int,
    correct: Boolean,
    compact: Boolean,
) {
    Surface(
        shape = PrismSlot,
        color = if (correct) SuccessGreenSoft else MaterialTheme.colorScheme.errorContainer,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 20.dp),
        ) {
            FeedbackMark(
                kind = if (correct) FeedbackMarkKind.CORRECT else FeedbackMarkKind.WRONG,
                modifier = Modifier.size(if (compact) 28.dp else 36.dp),
            )
            MathText(
                text = count.toString(),
                style = if (compact) {
                    MaterialTheme.typography.displayMedium
                } else {
                    MaterialTheme.typography.displayLarge
                },
                color = if (correct) SuccessGreen else MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.padding(start = 16.dp, end = 28.dp, top = if (compact) 10.dp else 16.dp, bottom = if (compact) 10.dp else 16.dp),
            )
        }
    }
}

@DevicePreviews
@Composable
private fun HeadCountWatchingPreview() {
    GamePreviewHost {
        HeadCountContent(
            uiState = HeadCountUiState(
                phase = HeadCountGame.Phase.WATCHING,
                moveKey = 2,
                entering = 2,
                leaving = 1,
                entersFromLeft = true,
                stepMillis = 1200,
                moveIndex = 1,
                moveCount = 6,
                revealedCount = null,
                answerResult = null,
            ),
            onAnswer = {},
        )
    }
}

@DevicePreviews
@Composable
private fun HeadCountAnswerPreview() {
    GamePreviewHost {
        HeadCountContent(
            uiState = HeadCountUiState(
                phase = HeadCountGame.Phase.ANSWER,
                moveKey = 6,
                entering = 0,
                leaving = 0,
                entersFromLeft = true,
                stepMillis = 1200,
                moveIndex = 5,
                moveCount = 6,
                revealedCount = null,
                answerResult = null,
            ),
            onAnswer = {},
        )
    }
}

@DevicePreviews
@Composable
private fun HeadCountRevealPreview() {
    GamePreviewHost {
        HeadCountContent(
            uiState = HeadCountUiState(
                phase = HeadCountGame.Phase.ANSWER,
                moveKey = 6,
                entering = 0,
                leaving = 0,
                entersFromLeft = true,
                stepMillis = 1200,
                moveIndex = 5,
                moveCount = 6,
                revealedCount = 7,
                answerResult = RevealResult.CORRECT,
            ),
            onAnswer = {},
        )
    }
}
