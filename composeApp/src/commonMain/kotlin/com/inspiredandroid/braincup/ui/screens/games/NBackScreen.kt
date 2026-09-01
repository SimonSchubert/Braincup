package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.n_back_level
import braincup.composeapp.generated.resources.n_back_match
import com.inspiredandroid.braincup.app.BoardCommand
import com.inspiredandroid.braincup.app.NBackUiState
import com.inspiredandroid.braincup.games.NBackGame
import com.inspiredandroid.braincup.games.tools.Shape
import com.inspiredandroid.braincup.ui.components.ChunkyCheck
import com.inspiredandroid.braincup.ui.components.ChunkyCross
import com.inspiredandroid.braincup.ui.components.LocalIsCompactHeight
import com.inspiredandroid.braincup.ui.components.PrismPolygon
import com.inspiredandroid.braincup.ui.components.PrismTile
import com.inspiredandroid.braincup.ui.components.hoverHand
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ColumnScope.NBackContent(
    uiState: NBackUiState,
    onAnswer: (String) -> Unit,
) {
    val compact = LocalIsCompactHeight.current

    Spacer(Modifier.weight(1f))
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Hidden, not removed, outside the stream: the lead-in and the result own the arena, and
        // keeping the row's height stops everything below it jumping.
        NBackLevelLabel(
            level = uiState.level,
            compact = compact,
            visible = uiState.phase == NBackGame.Phase.STREAM,
        )
        Spacer(Modifier.height(if (compact) 12.dp else 20.dp))
        NBackArena(uiState = uiState, size = if (compact) 116.dp else 168.dp)
        Spacer(Modifier.height(if (compact) 20.dp else 32.dp))
        NBackMatchButton(
            response = uiState.lastResponse,
            enabled = uiState.phase == NBackGame.Phase.STREAM && !uiState.responded,
            onClick = { onAnswer(BoardCommand.SUBMIT) },
        )
    }
    Spacer(Modifier.weight(1f))
}

/** "3-BACK". The one thing the player has to hold on to, so it sits above the stream. */
@Composable
private fun NBackLevelLabel(level: Int, compact: Boolean, visible: Boolean) {
    Text(
        text = stringResource(Res.string.n_back_level, level),
        style = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        letterSpacing = 3.sp,
        modifier = Modifier.alpha(if (visible) 1f else 0f),
    )
}

@Composable
private fun NBackArena(uiState: NBackUiState, size: Dp) {
    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        when (uiState.phase) {
            // Announces the level before the first item, so the stream never starts cold.
            NBackGame.Phase.LEAD_IN -> NBackBanner(level = uiState.level)
            // Null during the blank; the box keeps its size so nothing jumps.
            NBackGame.Phase.STREAM -> uiState.currentShape?.let {
                PrismPolygon(
                    points = it.paths,
                    face = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxSize().padding(size * 0.06f),
                )
            }
        }
    }
}

@Composable
private fun NBackBanner(level: Int) {
    Text(
        text = stringResource(Res.string.n_back_level, level),
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun NBackMatchButton(
    response: NBackGame.Response?,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    val face = when (response) {
        NBackGame.Response.HIT -> SuccessGreen
        NBackGame.Response.FALSE_ALARM -> scheme.error
        else -> if (enabled) Primary else scheme.surfaceVariant
    }
    val animatedFace by animateColorAsState(targetValue = face, label = "nBackMatchFace")

    PrismTile(
        face = animatedFace,
        onClick = onClick,
        isClickable = enabled,
        modifier = Modifier
            .hoverHand(enabled)
            .widthIn(min = 200.dp),
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center,
        ) {
            // The mark is drawn, not typed, so it matches the brand type and does not rest on
            // colour alone to say whether the tap landed.
            when (response) {
                NBackGame.Response.HIT -> ChunkyCheck(Color.White, Modifier.size(24.dp))
                NBackGame.Response.FALSE_ALARM -> ChunkyCross(Color.White, Modifier.size(24.dp))
                else -> Text(
                    text = stringResource(Res.string.n_back_match),
                    color = if (enabled) Color.White else scheme.onSurfaceVariant,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun NBackStreamPreview() {
    GamePreviewHost {
        NBackContent(
            uiState = NBackUiState(
                level = 3,
                phase = NBackGame.Phase.STREAM,
                currentShape = Shape.STAR,
                blockProgress = 0.4f,
                responded = false,
                lastResponse = null,
            ),
            onAnswer = {},
        )
    }
}
