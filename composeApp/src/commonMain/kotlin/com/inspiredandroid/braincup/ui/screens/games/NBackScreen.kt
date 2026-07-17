package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.NBackUiState
import com.inspiredandroid.braincup.games.NBackGame
import com.inspiredandroid.braincup.games.tools.Shape
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource

private enum class OptionState { NORMAL, CORRECT, WRONG, DIMMED }

@Composable
internal fun ColumnScope.NBackContent(
    uiState: NBackUiState,
    onAnswer: (String) -> Unit,
) {
    val compact = LocalIsCompactHeight.current

    // The tapped shape, so a wrong pick can flash red; reset when a fresh trial clears the reveal.
    var tapped by remember { mutableStateOf<Shape?>(null) }
    LaunchedEffect(uiState.revealAnswer) {
        if (uiState.revealAnswer == null) tapped = null
    }

    Spacer(Modifier.weight(1f))
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (uiState.phase) {
            NBackGame.Phase.MEMORIZE -> {
                NBackStimulus(
                    shape = uiState.currentShape,
                    size = if (compact) 120.dp else 168.dp,
                )
                Spacer(Modifier.height(if (compact) 16.dp else 24.dp))
                NBackDots(current = uiState.showIndex, count = uiState.sequenceLength)
            }
            NBackGame.Phase.RECALL -> {
                NBackPrompt(position = uiState.askPosition + 1)
                Spacer(Modifier.height(if (compact) 12.dp else 24.dp))
                NBackPalette(
                    uiState = uiState,
                    tapped = tapped,
                    optionSize = if (compact) 60.dp else 76.dp,
                    onTap = { shape ->
                        tapped = shape
                        onAnswer(shape.name)
                    },
                )
            }
        }
    }
    Spacer(Modifier.weight(1f))
}

@Composable
private fun NBackPrompt(position: Int) {
    val text = stringResource(Res.string.n_back_prompt, position)
    val accent = MaterialTheme.colorScheme.primary
    // Emphasize the position number inside the localized sentence so it stands out.
    val annotated = buildAnnotatedString {
        val match = Regex("\\d+").find(text)
        if (match == null) {
            append(text)
        } else {
            append(text.substring(0, match.range.first))
            withStyle(SpanStyle(color = accent, fontWeight = FontWeight.Bold, fontSize = 1.6.em)) {
                append(match.value)
            }
            append(text.substring(match.range.last + 1))
        }
    }
    Text(
        text = annotated,
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
    )
}

@Composable
private fun NBackStimulus(shape: Shape?, size: Dp) {
    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        // Null during the blank gap; the box keeps its size so nothing jumps.
        shape?.let {
            PrismPolygon(
                points = it.paths,
                face = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun NBackDots(current: Int, count: Int) {
    val accent = MaterialTheme.colorScheme.primary
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(count) { i ->
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (i <= current) accent else accent.copy(alpha = 0.25f)),
            )
        }
    }
}

@Composable
private fun NBackPalette(
    uiState: NBackUiState,
    tapped: Shape?,
    optionSize: Dp,
    onTap: (Shape) -> Unit,
) {
    val revealed = uiState.revealAnswer != null
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        uiState.options.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { shape ->
                    val state = when {
                        !revealed -> OptionState.NORMAL
                        shape == uiState.revealAnswer -> OptionState.CORRECT
                        shape == tapped && uiState.recallResult == NBackGame.RecallResult.WRONG -> OptionState.WRONG
                        else -> OptionState.DIMMED
                    }
                    NBackOption(
                        shape = shape,
                        state = state,
                        enabled = !revealed,
                        size = optionSize,
                        onClick = { onTap(shape) },
                    )
                }
            }
        }
    }
}

@Composable
private fun NBackOption(
    shape: Shape,
    state: OptionState,
    enabled: Boolean,
    size: Dp,
    onClick: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val pressed by interaction.collectIsPressedAsState()
    val active = enabled && (hovered || pressed)

    val face = when (state) {
        OptionState.CORRECT -> SuccessGreen
        OptionState.WRONG -> scheme.error
        OptionState.DIMMED -> scheme.onSurfaceVariant.copy(alpha = 0.4f)
        OptionState.NORMAL -> scheme.primary
    }
    val background = when (state) {
        OptionState.CORRECT -> SuccessGreen.copy(alpha = 0.16f)
        OptionState.WRONG -> scheme.error.copy(alpha = 0.16f)
        // Lift the tile toward the accent on hover so it clearly reads as clickable.
        else -> if (active) scheme.primary.copy(alpha = 0.16f) else scheme.surfaceVariant
    }
    val border = when {
        state == OptionState.CORRECT -> BorderStroke(3.dp, SuccessGreen)
        state == OptionState.WRONG -> BorderStroke(3.dp, scheme.error)
        active -> BorderStroke(2.dp, scheme.primary)
        else -> null
    }
    // Grow slightly on hover, dip on press, for tactile click feedback.
    val scale by animateFloatAsState(
        targetValue = if (pressed) {
            0.92f
        } else if (enabled && hovered) {
            1.07f
        } else {
            1f
        },
        label = "nBackOptionScale",
    )

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = background,
        border = border,
        modifier = Modifier
            .size(size)
            .scale(scale)
            .pointerHoverIcon(if (enabled) PointerIcon.Hand else PointerIcon.Default)
            .clickable(
                interactionSource = interaction,
                indication = LocalIndication.current,
                enabled = enabled,
                onClick = onClick,
            ),
    ) {
        PrismPolygon(
            points = shape.paths,
            face = face,
            modifier = Modifier.fillMaxSize().padding(size * 0.22f),
        )
    }
}

@DevicePreviews
@Composable
private fun NBackRecallPreview() {
    GamePreviewHost {
        NBackContent(
            uiState = NBackUiState(
                phase = NBackGame.Phase.RECALL,
                currentShape = null,
                showIndex = 3,
                sequenceLength = 4,
                askPosition = 2,
                options = NBackGame.PALETTE.toImmutableList(),
                revealAnswer = null,
                recallResult = null,
            ),
            onAnswer = {},
        )
    }
}
