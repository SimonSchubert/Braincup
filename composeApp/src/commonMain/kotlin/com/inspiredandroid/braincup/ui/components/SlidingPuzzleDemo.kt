package com.inspiredandroid.braincup.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.game_sliding_puzzle_desc
import braincup.composeapp.generated.resources.sliding_puzzle_demo_title
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import com.inspiredandroid.braincup.ui.theme.numberFontFamily
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

private const val DemoGridSize = 3

// 0 marks the empty cell. The board starts two-and-a-bit moves from solved, in the top-right
// corner, so three single-step slides put 1..8 back in order:
//   1 _ 2      1 2 3
//   4 5 3  ->  4 5 6
//   7 8 6      7 8 _
private val StartTiles = listOf(1, 0, 2, 4, 5, 3, 7, 8, 6)

// Index of the tile that slides into the empty slot on each step (always a neighbour of the
// current gap). 2 slides left, then 3 slides up, then 6 slides up. Mirrors SlidingPuzzleGame.
private val SlideSequence = listOf(2, 5, 8)

private const val SlideMillis = 360
private const val StepGapMillis = 320L
private const val SolvedHoldMillis = 1400L
private const val ResetPauseMillis = 600L

// Finish flourish: once solved, count 1..8 in order, lighting each tile green to confirm the order.
private const val CountStepMillis = 200L
private const val PulseScale = 1.18f

/**
 * Animated tutorial board for Sliding Puzzle: a 3x3 grid that solves itself in three slides. Each
 * step glides a tile into the empty space (mirroring SlidingPuzzleGame.slideTile) until 1..8 are in
 * order, then loops. Plays on its own with no interaction, like [LightsOutDemo].
 */
@Composable
fun SlidingPuzzleDemo(modifier: Modifier = Modifier) {
    var tiles by remember { mutableStateOf(StartTiles) }
    var slidingIndex by remember { mutableIntStateOf(-1) }
    // Direction (cols, rows) the sliding tile travels toward the gap; one component is ±1.
    var slideDir by remember { mutableStateOf(IntOffset.Zero) }
    val slideProgress = remember { Animatable(0f) }
    // Board indices confirmed green during the finish count, and the one currently popping.
    var litCells by remember { mutableStateOf(emptySet<Int>()) }
    var pulseIndex by remember { mutableIntStateOf(-1) }

    LaunchedEffect(Unit) {
        while (true) {
            tiles = StartTiles
            slidingIndex = -1
            litCells = emptySet()
            pulseIndex = -1
            slideProgress.snapTo(0f)
            delay(ResetPauseMillis)

            for (src in SlideSequence) {
                val dst = tiles.indexOf(0)
                slideDir = IntOffset(
                    x = (dst % DemoGridSize) - (src % DemoGridSize),
                    y = (dst / DemoGridSize) - (src / DemoGridSize),
                )
                slidingIndex = src
                slideProgress.snapTo(0f)
                slideProgress.animateTo(1f, tween(SlideMillis))
                tiles = tiles.toMutableList().also {
                    it[dst] = it[src]
                    it[src] = 0
                }
                slidingIndex = -1
                slideProgress.snapTo(0f)
                delay(StepGapMillis)
            }

            // Finish flourish: walk 1..8 in order, lighting each tile green with a travelling pop so
            // it's clear the numbers are now in the correct sequence.
            for (value in 1 until DemoGridSize * DemoGridSize) {
                val cellIndex = tiles.indexOf(value)
                litCells = litCells + cellIndex
                pulseIndex = cellIndex
                delay(CountStepMillis)
            }
            pulseIndex = -1
            delay(SolvedHoldMillis)
        }
    }

    val cell = if (LocalIsCompactHeight.current) 48.dp else 72.dp
    val gap = 4.dp
    val step = cell + gap

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.sliding_puzzle_demo_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(16.dp))

        val emptyFace = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.4f)
        val tileFace = MaterialTheme.colorScheme.primaryContainer
        val tileText = MaterialTheme.colorScheme.onPrimaryContainer
        Column(verticalArrangement = Arrangement.spacedBy(gap)) {
            for (row in 0 until DemoGridSize) {
                Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                    for (col in 0 until DemoGridSize) {
                        val index = row * DemoGridSize + col
                        val isSliding = index == slidingIndex
                        val dx = if (isSliding) step * slideDir.x * slideProgress.value else 0.dp
                        val dy = if (isSliding) step * slideDir.y * slideProgress.value else 0.dp
                        // Once counted in the finish flourish, the tile settles to green; the most
                        // recently counted one pops to draw the eye along the sequence.
                        val isLit = index in litCells
                        val face by animateColorAsState(
                            targetValue = when {
                                tiles[index] == 0 -> emptyFace
                                isLit -> SuccessGreen
                                else -> tileFace
                            },
                            animationSpec = tween(220),
                            label = "slideFace",
                        )
                        val textColor by animateColorAsState(
                            targetValue = if (isLit) Color.White else tileText,
                            animationSpec = tween(220),
                            label = "slideText",
                        )
                        val scale by animateFloatAsState(
                            targetValue = if (index == pulseIndex) PulseScale else 1f,
                            animationSpec = tween(180),
                            label = "slidePulse",
                        )
                        DemoPuzzleCell(
                            label = tiles[index],
                            face = face,
                            textColor = textColor,
                            modifier = Modifier
                                .size(cell)
                                .offset { IntOffset(dx.roundToPx(), dy.roundToPx()) }
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                },
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(Res.string.game_sliding_puzzle_desc),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}

@Composable
private fun DemoPuzzleCell(
    label: Int,
    face: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    // Colors are resolved by the caller so the tile can animate to its solved/green state. Empty
    // (0) tiles draw no number, matching the real SlidingPuzzleCell.
    PrismTile(
        face = face,
        modifier = modifier,
        isClickable = false,
        onClick = {},
    ) {
        if (label != 0) {
            Text(
                text = label.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontFamily = numberFontFamily(),
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center,
            )
        }
    }
}
