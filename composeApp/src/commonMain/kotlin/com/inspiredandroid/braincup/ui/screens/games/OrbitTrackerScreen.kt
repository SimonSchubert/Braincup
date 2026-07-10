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
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.games.OrbitTrackerGame
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import kotlin.math.sqrt

@Composable
internal fun ColumnScope.OrbitTrackerContent(
    uiState: OrbitTrackerUiState,
    onAnswer: (String) -> Unit,
) {
    val isHighlighting = uiState.phase == OrbitTrackerGame.Phase.HIGHLIGHTING
    val isAnswering = uiState.phase == OrbitTrackerGame.Phase.ANSWERING

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
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .hoverHand(isAnswering)
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

                                // Find the closest ball within tap range
                                var closestIndex = -1
                                var closestDist = Float.MAX_VALUE
                                uiState.balls.forEachIndexed { index, ball ->
                                    val dx = ball.x - normalizedX
                                    val dy = ball.y - normalizedY
                                    val dist = kotlin.math.sqrt(dx * dx + dy * dy)
                                    if (dist < ballRadius * 3 && dist < closestDist) {
                                        closestDist = dist
                                        closestIndex = index
                                    }
                                }
                                if (closestIndex >= 0) {
                                    onAnswer(closestIndex.toString())
                                }
                            }
                        }
                    }
                },
        ) {
            // Draw arena border
            drawRect(
                color = outlineColor,
                style = Stroke(width = 2.dp.toPx()),
            )

            val ballRadiusPx = 0.04f * size.width

            uiState.balls.forEach { ball ->
                val cx = ball.x * size.width
                val cy = ball.y * size.height
                val center = Offset(cx, cy)

                val isGameOver = uiState.phase == OrbitTrackerGame.Phase.GAME_OVER

                val color = when {
                    // Feedback states
                    ball.feedback == OrbitTrackerUiState.BallFeedback.CORRECT_SELECTED && isGameOver -> SuccessGreen
                    ball.feedback == OrbitTrackerUiState.BallFeedback.CORRECT_SELECTED -> primaryColor
                    ball.feedback == OrbitTrackerUiState.BallFeedback.WRONG_SELECTED -> errorColor
                    ball.feedback == OrbitTrackerUiState.BallFeedback.MISSED -> SuccessGreen
                    // Highlight phase: targets are blue
                    isHighlighting && ball.isTarget -> primaryColor
                    // Default dark grey
                    else -> onSurfaceVariantColor
                }

                drawPrismCircle(
                    center = center,
                    radius = ballRadiusPx,
                    face = color,
                )

                // Draw outline for missed targets
                if (ball.feedback == OrbitTrackerUiState.BallFeedback.MISSED) {
                    drawCircle(
                        color = SuccessGreen,
                        radius = ballRadiusPx + 3.dp.toPx(),
                        center = center,
                        style = Stroke(width = 2.dp.toPx()),
                    )
                }

                // Draw selection ring during answering
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

@GameDevicePreviews
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
