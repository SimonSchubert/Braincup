package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.games.SimonSaysGame
import com.inspiredandroid.braincup.games.tools.composeColor
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun ColumnScope.SimonSaysContent(
    uiState: SimonSaysUiState,
    onAnswer: (String) -> Unit,
) {
    val cellMax = if (LocalIsCompactHeight.current) 96.dp else 140.dp
    val isClickable = uiState.phase == SimonSaysGame.Phase.ANSWERING

    SimonDisc(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .widthIn(max = cellMax * 2)
            .align(Alignment.CenterHorizontally),
    ) { index, quadrant, padModifier ->
        val pad = uiState.pads[index]
        SimonPad(
            pad = pad,
            quadrant = quadrant,
            isClickable = isClickable,
            onClick = { onAnswer(pad.color.name) },
            modifier = padModifier,
        )
    }
}

@Composable
private fun SimonPad(
    pad: SimonSaysUiState.PadState,
    quadrant: SimonQuadrant,
    isClickable: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val baseColor = pad.color.composeColor()
    // The board *is* four colors, so game-over feedback can't be another color the way it is in
    // Ghost Grid (whose tiles are neutral) -- a red "wrong" tint next to the red pad, or a green
    // "missed" tint next to the green pad, is unreadable. Lit/dim carries the state and a ✓/✗
    // mark carries the verdict, both independent of hue.
    val isLit = when (pad.type) {
        SimonSaysUiState.CellType.ACTIVE,
        SimonSaysUiState.CellType.TAPPED,
        SimonSaysUiState.CellType.MISSED,
        -> true
        SimonSaysUiState.CellType.WRONG,
        SimonSaysUiState.CellType.INACTIVE,
        -> false
    }
    val animatedColor by animateColorAsState(
        targetValue = simonPadColor(baseColor, isLit),
        animationSpec = SimonPadColorSpec,
        label = "simonPadColor",
    )

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.92f else 1f, label = "simonPadScale")

    val shape = remember(quadrant) { simonQuadrantShape(quadrant) }

    Box(
        modifier = modifier
            .scale(scale)
            .clip(shape)
            .background(animatedColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = isClickable,
                onClick = onClick,
            )
            .hoverHand(isClickable),
    ) {
        val mark = when (pad.type) {
            SimonSaysUiState.CellType.WRONG -> SimonMark.WRONG
            SimonSaysUiState.CellType.MISSED -> SimonMark.MISSED
            else -> null
        }
        if (mark != null) {
            SimonFeedbackMark(
                mark = mark,
                modifier = Modifier
                    .align(simonWedgeAlignment(quadrant))
                    .fillMaxWidth(SimonWedgeMarkFraction)
                    .aspectRatio(1f),
            )
        }
    }
}

private enum class SimonMark { WRONG, MISSED }

/**
 * A ✗ or ✓ drawn on a surface-colored disc. The disc guarantees contrast whatever hue the pad
 * underneath happens to be, and the glyph shape (not its color) is what tells the two apart.
 */
@Composable
private fun SimonFeedbackMark(mark: SimonMark, modifier: Modifier = Modifier) {
    val strokeColor = when (mark) {
        SimonMark.WRONG -> MaterialTheme.colorScheme.error
        SimonMark.MISSED -> SuccessGreen
    }
    val backdrop = MaterialTheme.colorScheme.surface
    Canvas(modifier = modifier.clip(CircleShape).background(backdrop)) {
        val s = size.minDimension
        val stroke = Stroke(width = s * 0.14f, cap = StrokeCap.Round)
        val inset = s * 0.3f
        when (mark) {
            SimonMark.WRONG -> {
                drawLine(strokeColor, Offset(inset, inset), Offset(s - inset, s - inset), stroke.width, stroke.cap)
                drawLine(strokeColor, Offset(s - inset, inset), Offset(inset, s - inset), stroke.width, stroke.cap)
            }
            SimonMark.MISSED -> {
                drawLine(strokeColor, Offset(inset, s * 0.52f), Offset(s * 0.44f, s - inset), stroke.width, stroke.cap)
                drawLine(strokeColor, Offset(s * 0.44f, s - inset), Offset(s - inset, inset), stroke.width, stroke.cap)
            }
        }
    }
}

@DevicePreviews
@Composable
private fun SimonSaysContentPreview() {
    GamePreviewHost {
        SimonSaysContent(
            uiState = SimonSaysUiState(
                round = 1,
                phase = SimonSaysGame.Phase.ANSWERING,
                pads = SimonSaysGame.PADS.mapIndexed { index, color ->
                    SimonSaysUiState.PadState(
                        color = color,
                        type = if (index == 0) SimonSaysUiState.CellType.TAPPED else SimonSaysUiState.CellType.INACTIVE,
                    )
                }.toImmutableList(),
                sequenceLength = 3,
                tappedCount = 1,
            ),
            onAnswer = {},
        )
    }
}
