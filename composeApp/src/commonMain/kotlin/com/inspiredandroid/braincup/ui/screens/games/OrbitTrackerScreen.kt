package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.games.OrbitTrackerGame
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ColumnScope.OrbitTrackerContent(
    uiState: OrbitTrackerUiState,
    onAnswer: (String) -> Unit,
    livePositions: StateFlow<List<Pair<Float, Float>>>? = null,
) {
    val isHighlighting = uiState.phase == OrbitTrackerGame.Phase.HIGHLIGHTING
    val isAnswering = uiState.phase == OrbitTrackerGame.Phase.ANSWERING
    val isMoving = uiState.phase == OrbitTrackerGame.Phase.MOVING

    // The instruction header depends only on the phase, which is constant while the balls
    // animate. Keeping it in its own composable lets it skip the 60fps recomposition the moving
    // balls trigger, so the AnimatedContent and string lookups only run on a real phase change.
    OrbitTrackerInstruction(uiState.phase)
    Spacer(Modifier.height(8.dp))

    val errorColor = MaterialTheme.colorScheme.error
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    val outlineColor = MaterialTheme.colorScheme.outline
    val primaryColor = Primary

    val compact = LocalIsCompactHeight.current
    val canvasSizeModifier = remember(compact) {
        if (compact) {
            Modifier
                .padding(horizontal = 24.dp)
                .heightIn(max = 240.dp)
                .aspectRatio(1f)
        } else {
            Modifier
                .padding(horizontal = 24.dp)
                .widthIn(max = 400.dp)
                .aspectRatio(1f)
        }
    }
    Box(
        modifier = canvasSizeModifier
            .align(Alignment.CenterHorizontally),
    ) {
        // Positions are collected only in this leaf so MOVING frames do not recompose
        // instructions or the surrounding GameScreen tree.
        OrbitTrackerArena(
            uiState = uiState,
            livePositions = livePositions,
            isHighlighting = isHighlighting,
            isAnswering = isAnswering,
            isMoving = isMoving,
            errorColor = errorColor,
            onSurfaceVariantColor = onSurfaceVariantColor,
            outlineColor = outlineColor,
            primaryColor = primaryColor,
            onAnswer = onAnswer,
        )
    }
}

@Composable
private fun OrbitTrackerArena(
    uiState: OrbitTrackerUiState,
    livePositions: StateFlow<List<Pair<Float, Float>>>?,
    isHighlighting: Boolean,
    isAnswering: Boolean,
    isMoving: Boolean,
    errorColor: androidx.compose.ui.graphics.Color,
    onSurfaceVariantColor: androidx.compose.ui.graphics.Color,
    outlineColor: androidx.compose.ui.graphics.Color,
    primaryColor: androidx.compose.ui.graphics.Color,
    onAnswer: (String) -> Unit,
) {
    val emptyPositions = remember { MutableStateFlow(emptyList<Pair<Float, Float>>()) }
    val live by (livePositions ?: emptyPositions).collectAsStateWithLifecycle()
    val staticPositions = remember(uiState.balls) { uiState.balls.map { it.x to it.y } }
    // Prefer live frame positions during MOVING; fall back to uiState for other phases / previews.
    val positions: List<Pair<Float, Float>> =
        if (isMoving && live.isNotEmpty()) live else staticPositions
    val positionsForTap by rememberUpdatedState(positions)
    val onAnswerState by rememberUpdatedState(onAnswer)

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .hoverHand(isAnswering)
            // Key only on phase so MOVING frames do not restart the pointer coroutine.
            .pointerInput(uiState.phase) {
                if (!isAnswering) return@pointerInput
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.type == PointerEventType.Press) {
                            val position = event.changes.firstOrNull()?.position ?: continue
                            val normalizedX = position.x / size.width
                            val normalizedY = position.y / size.height
                            val ballRadius = 0.04f

                            var closestIndex = -1
                            var closestDist = Float.MAX_VALUE
                            positionsForTap.forEachIndexed { index, (bx, by) ->
                                val dx = bx - normalizedX
                                val dy = by - normalizedY
                                val dist = kotlin.math.sqrt(dx * dx + dy * dy)
                                if (dist < ballRadius * 3 && dist < closestDist) {
                                    closestDist = dist
                                    closestIndex = index
                                }
                            }
                            if (closestIndex >= 0) {
                                onAnswerState(closestIndex.toString())
                            }
                        }
                    }
                }
            },
    ) {
        drawRect(
            color = outlineColor,
            style = Stroke(width = 2.dp.toPx()),
        )

        val ballRadiusPx = 0.04f * size.width
        val isGameOver = uiState.phase == OrbitTrackerGame.Phase.GAME_OVER

        uiState.balls.forEachIndexed { index, ball ->
            val (px, py) = positions.getOrElse(index) { ball.x to ball.y }
            val center = Offset(px * size.width, py * size.height)

            val color = when {
                ball.feedback == OrbitTrackerUiState.BallFeedback.CORRECT_SELECTED && isGameOver -> SuccessGreen
                ball.feedback == OrbitTrackerUiState.BallFeedback.CORRECT_SELECTED -> primaryColor
                ball.feedback == OrbitTrackerUiState.BallFeedback.WRONG_SELECTED -> errorColor
                ball.feedback == OrbitTrackerUiState.BallFeedback.MISSED -> SuccessGreen
                isHighlighting && ball.isTarget -> primaryColor
                else -> onSurfaceVariantColor
            }

            drawPrismCircle(
                center = center,
                radius = ballRadiusPx,
                face = color,
            )

            if (ball.feedback == OrbitTrackerUiState.BallFeedback.MISSED) {
                drawCircle(
                    color = SuccessGreen,
                    radius = ballRadiusPx + 3.dp.toPx(),
                    center = center,
                    style = Stroke(width = 2.dp.toPx()),
                )
            }

            if (ball.isSelected && ball.feedback == OrbitTrackerUiState.BallFeedback.NONE) {
                drawCircle(
                    color = primaryColor,
                    radius = ballRadiusPx + 3.dp.toPx(),
                    center = center,
                    style = Stroke(width = 2.dp.toPx()),
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.OrbitTrackerInstruction(phase: OrbitTrackerGame.Phase) {
    val instructionText = when (phase) {
        OrbitTrackerGame.Phase.HIGHLIGHTING -> stringResource(Res.string.game_remember_targets)
        OrbitTrackerGame.Phase.ANSWERING -> stringResource(Res.string.game_tap_original_targets)
        else -> ""
    }
    AnimatedContent(
        targetState = instructionText,
        transitionSpec = {
            fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(150))
        },
        modifier = Modifier.align(Alignment.CenterHorizontally),
        label = "orbitTrackerInstruction",
    ) { text ->
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            minLines = 1,
        )
    }
}

@DevicePreviews
@Composable
private fun OrbitTrackerContentPreview() {
    GamePreviewHost {
        OrbitTrackerContent(
            uiState = OrbitTrackerUiState(
                balls = persistentListOf(
                    OrbitTrackerUiState.BallState(0.3f, 0.4f, true, false, OrbitTrackerUiState.BallFeedback.NONE),
                    OrbitTrackerUiState.BallState(0.6f, 0.5f, false, false, OrbitTrackerUiState.BallFeedback.NONE),
                    OrbitTrackerUiState.BallState(0.5f, 0.7f, true, true, OrbitTrackerUiState.BallFeedback.NONE),
                ),
                phase = OrbitTrackerGame.Phase.ANSWERING,
                targetCount = 2,
                selectedCount = 1,
            ),
            onAnswer = {},
        )
    }
}
