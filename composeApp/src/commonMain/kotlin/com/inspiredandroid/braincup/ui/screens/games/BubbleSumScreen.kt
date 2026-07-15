package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.bubble_sum_instruction
import com.inspiredandroid.braincup.app.BubbleSumUiState
import com.inspiredandroid.braincup.games.BubbleSumGame
import com.inspiredandroid.braincup.ui.components.LocalIsCompactHeight
import com.inspiredandroid.braincup.ui.components.NumberPad
import com.inspiredandroid.braincup.ui.components.NumberPadWithInput
import com.inspiredandroid.braincup.ui.components.drawPrismCircle
import com.inspiredandroid.braincup.ui.theme.FlashCrowdYellow
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.numberFontFamily
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.stringResource
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
internal fun ColumnScope.BubbleSumContent(
    uiState: BubbleSumUiState,
    onAnswer: (String) -> Unit,
    liveFrames: StateFlow<List<BubbleSumGame.BubbleFrame>>? = null,
) {
    val compact = LocalIsCompactHeight.current
    if (compact) {
        // Landscape / short height: arena + pad side-by-side. Size the arena as a square that
        // fits the available height so weight(1f) cannot stretch it into a wide rectangle
        // (that used to clip bubbles — radius was scaled to width while height was short).
        var input by remember(uiState.roundKey) { mutableStateOf("") }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Scrollable compact columns pass unbounded height — never rely on fillMaxHeight.
            // Cap the square so weight cannot stretch the arena into a wide strip.
            BoxWithConstraints(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                val side = minOf(maxWidth, 220.dp)
                BubbleSumArena(
                    uiState = uiState,
                    liveFrames = liveFrames,
                    modifier = Modifier.size(side),
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(Res.string.bubble_sum_instruction),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = input.ifEmpty { "?" },
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = numberFontFamily(),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(6.dp))
                NumberPad(onInputChange = { typed ->
                    val next = input + typed
                    input = next
                    if (uiState.answerLength == next.length) {
                        onAnswer(next)
                    }
                })
            }
        }
    } else {
        // fillContent layout: arena uses remaining height, pad sits tight underneath.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(Res.string.bubble_sum_instruction),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                val side = minOf(maxWidth, maxHeight)
                BubbleSumArena(
                    uiState = uiState,
                    liveFrames = liveFrames,
                    modifier = Modifier.size(side),
                )
            }
            key(uiState.roundKey) {
                NumberPadWithInput(onInputChange = { typed ->
                    if (uiState.answerLength == typed.length) {
                        onAnswer(typed)
                    }
                })
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun BubbleSumArena(
    uiState: BubbleSumUiState,
    liveFrames: StateFlow<List<BubbleSumGame.BubbleFrame>>?,
    modifier: Modifier = Modifier,
) {
    val emptyFrames = remember { MutableStateFlow(emptyList<BubbleSumGame.BubbleFrame>()) }
    val live by (liveFrames ?: emptyFrames).collectAsStateWithLifecycle()
    val textMeasurer = rememberTextMeasurer()
    val outlineColor = MaterialTheme.colorScheme.outline
    val mutedFace = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
    val visibleFace = Primary
    val warningFace = FlashCrowdYellow
    val digitStyle = TextStyle(
        color = Color.White,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = numberFontFamily(),
    )
    val warningDigitStyle = digitStyle.copy(color = Color(0xFF1A1A1A))

    // Prefer live frames; fall back to centered placeholders for previews/screenshots.
    val frames: List<BubbleSumGame.BubbleFrame> = if (live.isNotEmpty()) {
        live
    } else {
        uiState.bubbles.mapIndexed { index, _ ->
            val angle = index * (2f * PI.toFloat() / uiState.bubbles.size.coerceAtLeast(1))
            BubbleSumGame.BubbleFrame(
                x = 0.5f + 0.28f * cos(angle),
                y = 0.5f + 0.28f * sin(angle),
                phase = BubbleSumGame.VisibilityPhase.VISIBLE,
            )
        }
    }

    Canvas(modifier = modifier) {
        // Physics is unit-square; map into the largest centered square so non-square layouts
        // never stretch positions or inflate radius past the short edge.
        val side = min(size.width, size.height)
        val originX = (size.width - side) / 2f
        val originY = (size.height - side) / 2f

        drawRect(
            color = outlineColor,
            topLeft = Offset(originX, originY),
            size = androidx.compose.ui.geometry.Size(side, side),
            style = Stroke(width = 2.dp.toPx()),
        )
        val radiusPx = BubbleSumGame.BALL_RADIUS * side
        uiState.bubbles.forEachIndexed { index, bubble ->
            val frame = frames.getOrNull(index) ?: return@forEachIndexed
            val center = Offset(
                originX + frame.x * side,
                originY + frame.y * side,
            )
            val face = when (frame.phase) {
                BubbleSumGame.VisibilityPhase.VISIBLE -> visibleFace
                BubbleSumGame.VisibilityPhase.WARNING -> warningFace
                BubbleSumGame.VisibilityPhase.HIDDEN -> mutedFace
            }
            drawPrismCircle(
                center = center,
                radius = radiusPx,
                face = face,
            )
            if (frame.phase != BubbleSumGame.VisibilityPhase.HIDDEN) {
                val fontSize = (radiusPx * 0.9f).coerceIn(12.dp.toPx(), 22.dp.toPx())
                val baseStyle =
                    if (frame.phase == BubbleSumGame.VisibilityPhase.WARNING) {
                        warningDigitStyle
                    } else {
                        digitStyle
                    }
                val style = baseStyle.copy(fontSize = fontSize.toSp())
                val measured = textMeasurer.measure(
                    text = bubble.value.toString(),
                    style = style,
                )
                drawText(
                    textLayoutResult = measured,
                    topLeft = Offset(
                        center.x - measured.size.width / 2f,
                        center.y - measured.size.height / 2f,
                    ),
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun BubbleSumContentPreview() {
    GamePreviewHost {
        BubbleSumContent(
            uiState = BubbleSumUiState(
                bubbles = persistentListOf(
                    BubbleSumUiState.BubbleState(3),
                    BubbleSumUiState.BubbleState(7),
                    BubbleSumUiState.BubbleState(2),
                ),
                answerLength = 2,
                roundKey = 1,
            ),
            onAnswer = {},
        )
    }
}
