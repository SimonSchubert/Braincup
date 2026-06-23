package com.inspiredandroid.braincup.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.game_wordle_desc
import braincup.composeapp.generated.resources.wordle_demo_title
import com.inspiredandroid.braincup.ui.theme.WordleAbsent
import com.inspiredandroid.braincup.ui.theme.WordleCorrect
import com.inspiredandroid.braincup.ui.theme.WordlePresent
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

private enum class DemoLetterState { NEUTRAL, ABSENT, PRESENT, CORRECT }

private data class DemoTile(val char: Char, val state: DemoLetterState)

// Hidden word: BRAIN. The first guess BREAD scores every tile state at once (B,R green;
// A yellow/present; E,D grey/absent); the winning guess BRAIN turns all green.
private val GuessRow = listOf(
    DemoTile('B', DemoLetterState.CORRECT),
    DemoTile('R', DemoLetterState.CORRECT),
    DemoTile('E', DemoLetterState.ABSENT),
    DemoTile('A', DemoLetterState.PRESENT),
    DemoTile('D', DemoLetterState.ABSENT),
)
private val WinRow = listOf(
    DemoTile('B', DemoLetterState.CORRECT),
    DemoTile('R', DemoLetterState.CORRECT),
    DemoTile('A', DemoLetterState.CORRECT),
    DemoTile('I', DemoLetterState.CORRECT),
    DemoTile('N', DemoLetterState.CORRECT),
)

private const val FlipStaggerMillis = 300L
private const val BetweenRowsMillis = 900L
private const val SolvedHoldMillis = 1800L
private const val ResetPauseMillis = 600L

/**
 * Animated tutorial for Wordle: one scored guess that reveals every tile colour (green = right
 * spot, yellow = wrong spot, grey = not in the word), then the winning guess flips all green. Tiles
 * reveal left-to-right with a flip, matching the real board. Loops on its own, like [LightsOutDemo].
 * The colour legend below it on the instructions screen spells out the meaning.
 */
@Composable
fun WordleDemo(modifier: Modifier = Modifier) {
    // How many tiles have been revealed (flipped to their colour) in each row.
    var revealedGuess by remember { mutableIntStateOf(0) }
    var revealedWin by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            revealedGuess = 0
            revealedWin = 0
            delay(ResetPauseMillis)

            repeat(GuessRow.size) {
                revealedGuess++
                delay(FlipStaggerMillis)
            }
            delay(BetweenRowsMillis)
            repeat(WinRow.size) {
                revealedWin++
                delay(FlipStaggerMillis)
            }
            delay(SolvedHoldMillis)
        }
    }

    val tile = if (LocalIsCompactHeight.current) 40.dp else 48.dp

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.wordle_demo_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            DemoWordleRow(tiles = GuessRow, revealed = revealedGuess, tileSize = tile)
            DemoWordleRow(tiles = WinRow, revealed = revealedWin, tileSize = tile)
        }
        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(Res.string.game_wordle_desc),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}

@Composable
private fun DemoWordleRow(tiles: List<DemoTile>, revealed: Int, tileSize: androidx.compose.ui.unit.Dp) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        tiles.forEachIndexed { index, tile ->
            DemoWordleTile(tile = tile, isRevealed = index < revealed, size = tileSize)
        }
    }
}

@Composable
private fun DemoWordleTile(
    tile: DemoTile,
    isRevealed: Boolean,
    size: androidx.compose.ui.unit.Dp,
) {
    // Classic Wordle flip: the tile rotates to edge-on (scaleY 0) at the halfway point, and only
    // there does its letter and scored colour appear. Tiles start empty, so the characters animate
    // in together with each box's flip.
    val flip by animateFloatAsState(
        targetValue = if (isRevealed) 1f else 0f,
        animationSpec = tween(durationMillis = 360),
        label = "wordleFlip",
    )
    val revealedNow = flip >= 0.5f
    val state = if (revealedNow) tile.state else DemoLetterState.NEUTRAL
    val face = when (state) {
        DemoLetterState.NEUTRAL -> MaterialTheme.colorScheme.surfaceVariant
        DemoLetterState.ABSENT -> WordleAbsent
        DemoLetterState.PRESENT -> WordlePresent
        DemoLetterState.CORRECT -> WordleCorrect
    }
    PrismCard(
        face = face,
        modifier = Modifier
            .size(size)
            // 1 -> 0 (edge-on) at the midpoint -> 1, so the face swaps while the tile is edge-on.
            .graphicsLayer { scaleY = kotlin.math.abs(flip - 0.5f) * 2f },
    ) {
        if (revealedNow) {
            Text(
                text = tile.char.toString(),
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
        }
    }
}
