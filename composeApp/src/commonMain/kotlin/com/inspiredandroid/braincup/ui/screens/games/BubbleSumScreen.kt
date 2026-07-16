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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.bubble_sum_instruction
import com.inspiredandroid.braincup.app.BubbleSumUiState
import com.inspiredandroid.braincup.games.BubbleSumGame
import com.inspiredandroid.braincup.ui.components.LocalIsCompactHeight
import com.inspiredandroid.braincup.ui.components.LocalScaffoldBodyHeight
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

/** Body height assumed when a compact scaffold did not report its own (previews, tooling). */
private val FallbackCompactBodyHeight = 220.dp

/**
 * Longest the arena may get relative to its short edge. The arena takes whatever rectangle the
 * layout leaves it, but bubble size follows the short edge, so an uncapped strip would turn into
 * a corridor of mostly empty space.
 */
private const val MAX_ARENA_ASPECT = 1.8f

/** Bubble drawn in the warning color when there are no live frames (previews, screenshots). */
private const val PlaceholderWarningBubble = 0

@Composable
internal fun ColumnScope.BubbleSumContent(
    uiState: BubbleSumUiState,
    onAnswer: (String) -> Unit,
    liveFrames: StateFlow<List<BubbleSumGame.BubbleFrame>>? = null,
    onArenaSize: (Float, Float) -> Unit = { _, _ -> },
) {
    val compact = LocalIsCompactHeight.current
    val scaffoldBodyHeight = LocalScaffoldBodyHeight.current

    BoxWithConstraints(
        // Compact scaffolds scroll, so they measure this with unbounded height: fillMaxSize would
        // collapse to zero and maxHeight is meaningless. The scaffold reports the real viewport
        // height through the local instead.
        modifier = if (compact) Modifier.fillMaxWidth() else Modifier.fillMaxSize(),
    ) {
        val bodyHeight = if (compact) {
            scaffoldBodyHeight ?: FallbackCompactBodyHeight
        } else {
            maxHeight
        }
        // Stacking on a wide body leaves the arena a short, very wide slot, which the aspect cap
        // then trims back to a strip. Put the pad beside it instead and the arena keeps the full
        // body height. Compact bodies always go side-by-side: stacking needs a bounded height to
        // hand the arena, which the scrolling compact path cannot give.
        if (compact || maxWidth > bodyHeight) {
            BubbleSumSideBySide(
                uiState = uiState,
                onAnswer = onAnswer,
                liveFrames = liveFrames,
                onArenaSize = onArenaSize,
                arenaMaxHeight = bodyHeight - 8.dp,
                dense = compact,
            )
        } else {
            BubbleSumStacked(
                uiState = uiState,
                onAnswer = onAnswer,
                liveFrames = liveFrames,
                onArenaSize = onArenaSize,
            )
        }
    }
}

/** Fits the largest rectangle within [MAX_ARENA_ASPECT] into the space the layout offers. */
private fun arenaSize(maxWidth: Dp, maxHeight: Dp): DpSize = if (maxWidth >= maxHeight) {
    DpSize(minOf(maxWidth, maxHeight * MAX_ARENA_ASPECT), maxHeight)
} else {
    DpSize(maxWidth, minOf(maxHeight, maxWidth * MAX_ARENA_ASPECT))
}

/** Wide body: arena on the left across the full body height, pad column beside it. */
@Composable
private fun BubbleSumSideBySide(
    uiState: BubbleSumUiState,
    onAnswer: (String) -> Unit,
    liveFrames: StateFlow<List<BubbleSumGame.BubbleFrame>>?,
    onArenaSize: (Float, Float) -> Unit,
    arenaMaxHeight: Dp,
    dense: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BoxWithConstraints(
            // fill = false so the box shrinks to the arena instead of holding the whole weighted
            // slot open, which lets the Row center the arena and pad as one group.
            modifier = Modifier.weight(1f, fill = false),
            contentAlignment = Alignment.Center,
        ) {
            val size = arenaSize(maxWidth, arenaMaxHeight)
            BubbleSumArena(
                uiState = uiState,
                liveFrames = liveFrames,
                onArenaSize = onArenaSize,
                modifier = Modifier.size(size),
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            BubbleSumInstruction(dense = dense)
            if (dense) {
                // Short bodies cannot afford NumberPadWithInput's 60dp answer row, so show the
                // typed digits on a single line instead.
                var input by remember(uiState.roundKey) { mutableStateOf("") }
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
            } else {
                BubbleSumNumberPad(uiState = uiState, onAnswer = onAnswer)
            }
        }
    }
}

/** Tall body: instruction, arena across the width and the leftover height, pad underneath. */
@Composable
private fun BubbleSumStacked(
    uiState: BubbleSumUiState,
    onAnswer: (String) -> Unit,
    liveFrames: StateFlow<List<BubbleSumGame.BubbleFrame>>?,
    onArenaSize: (Float, Float) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BubbleSumInstruction(dense = false)
        Spacer(Modifier.height(4.dp))
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            contentAlignment = Alignment.Center,
        ) {
            val size = arenaSize(maxWidth, maxHeight)
            BubbleSumArena(
                uiState = uiState,
                liveFrames = liveFrames,
                onArenaSize = onArenaSize,
                modifier = Modifier.size(size),
            )
        }
        BubbleSumNumberPad(uiState = uiState, onAnswer = onAnswer)
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun BubbleSumInstruction(dense: Boolean) {
    Text(
        text = stringResource(Res.string.bubble_sum_instruction),
        style = if (dense) {
            MaterialTheme.typography.labelLarge
        } else {
            MaterialTheme.typography.titleMedium
        },
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun BubbleSumNumberPad(
    uiState: BubbleSumUiState,
    onAnswer: (String) -> Unit,
) {
    key(uiState.roundKey) {
        NumberPadWithInput(onInputChange = { typed ->
            if (uiState.answerLength == typed.length) {
                onAnswer(typed)
            }
        })
    }
}

@Composable
private fun BubbleSumArena(
    uiState: BubbleSumUiState,
    liveFrames: StateFlow<List<BubbleSumGame.BubbleFrame>>?,
    onArenaSize: (Float, Float) -> Unit,
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

    val hasLiveFrames = live.isNotEmpty()

    Canvas(
        modifier = modifier.onSizeChanged {
            onArenaSize(it.width.toFloat(), it.height.toFloat())
        },
    ) {
        // Positions come in short-edge units, so scaling every axis by the short edge fills the
        // arena whatever its shape and keeps bubbles round.
        val shortEdge = min(size.width, size.height)
        val strokeWidth = 2.dp.toPx()

        drawRect(
            color = outlineColor,
            topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
            size = androidx.compose.ui.geometry.Size(
                size.width - strokeWidth,
                size.height - strokeWidth,
            ),
            style = Stroke(width = strokeWidth),
        )
        val radiusPx = BubbleSumGame.BALL_RADIUS * shortEdge
        uiState.bubbles.forEachIndexed { index, bubble ->
            val frame = live.getOrNull(index)
            if (hasLiveFrames && frame == null) return@forEachIndexed
            val center = if (frame != null) {
                Offset(frame.x * shortEdge, frame.y * shortEdge)
            } else {
                // Previews and screenshots run without the motion loop: ring the bubbles around
                // the middle so the arena still reads as a game board.
                val angle = index * (2f * PI.toFloat() / uiState.bubbles.size.coerceAtLeast(1))
                Offset(
                    size.width / 2f + 0.28f * shortEdge * cos(angle),
                    size.height / 2f + 0.28f * shortEdge * sin(angle),
                )
            }
            val phase = frame?.phase ?: if (index == PlaceholderWarningBubble) {
                // Show the warning color in the still frame too, so previews and store shots carry
                // the mechanic the game is built around.
                BubbleSumGame.VisibilityPhase.WARNING
            } else {
                BubbleSumGame.VisibilityPhase.VISIBLE
            }
            val face = when (phase) {
                BubbleSumGame.VisibilityPhase.VISIBLE -> visibleFace
                BubbleSumGame.VisibilityPhase.WARNING -> warningFace
                BubbleSumGame.VisibilityPhase.HIDDEN -> mutedFace
            }
            drawPrismCircle(
                center = center,
                radius = radiusPx,
                face = face,
            )
            if (phase != BubbleSumGame.VisibilityPhase.HIDDEN) {
                // Track the bubble: the arena fills the screen, so a fixed ceiling would leave a
                // big-screen bubble carrying a tiny digit. Only the floor stays, to keep numbers
                // legible when a short body squeezes the arena.
                val fontSize = (radiusPx * 0.9f).coerceAtLeast(12.dp.toPx())
                val baseStyle =
                    if (phase == BubbleSumGame.VisibilityPhase.WARNING) {
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
