package com.inspiredandroid.braincup.ui.components

import androidx.compose.animation.core.Animatable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.game_sliding_puzzle_desc
import braincup.composeapp.generated.resources.sliding_puzzle_demo_title
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

    LaunchedEffect(Unit) {
        while (true) {
            tiles = StartTiles
            slidingIndex = -1
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

        Column(verticalArrangement = Arrangement.spacedBy(gap)) {
            for (row in 0 until DemoGridSize) {
                Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                    for (col in 0 until DemoGridSize) {
                        val index = row * DemoGridSize + col
                        val isSliding = index == slidingIndex
                        val dx = if (isSliding) step * slideDir.x * slideProgress.value else 0.dp
                        val dy = if (isSliding) step * slideDir.y * slideProgress.value else 0.dp
                        DemoPuzzleCell(
                            label = tiles[index],
                            modifier = Modifier
                                .size(cell)
                                .offset { IntOffset(dx.roundToPx(), dy.roundToPx()) },
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
private fun DemoPuzzleCell(label: Int, modifier: Modifier = Modifier) {
    // Match the real SlidingPuzzleCell colors so the demo reads as gameplay.
    val isEmpty = label == 0
    val face = if (isEmpty) {
        MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.4f)
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }
    PrismTile(
        face = face,
        modifier = modifier,
        isClickable = false,
        onClick = {},
    ) {
        if (!isEmpty) {
            Text(
                text = label.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontFamily = numberFontFamily(),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
            )
        }
    }
}
