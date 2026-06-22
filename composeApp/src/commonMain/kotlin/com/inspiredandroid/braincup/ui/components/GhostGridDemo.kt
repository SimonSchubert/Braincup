package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.ghost_grid_demo_repeat
import braincup.composeapp.generated.resources.ghost_grid_demo_title
import braincup.composeapp.generated.resources.ghost_grid_demo_watch
import com.inspiredandroid.braincup.ui.theme.Primary
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

private const val DemoGridSize = 3

// Mirrors the real game's flash timings closely enough to read as the same thing.
private const val FlashOnMillis = 700L
private const val FlashOffMillis = 250L
private const val ReplayStepMillis = 500L

// A hand-picked sequence on the 3x3 board (clockwise diamond); the demo numbers these tiles 1..4
// so the order is spelled out instead of relying on memory.
private val DemoSequence = listOf(1, 5, 7, 3)

/**
 * Animated tutorial board for Ghost Grid: a 4x4 grid that flashes a fixed sequence one tile at a
 * time (the "watch" phase), then lights the same tiles back in order (the "repeat" phase), then
 * loops. No interaction needed; it plays on its own like [ChessMoveDemo].
 */
@Composable
fun GhostGridDemo(modifier: Modifier = Modifier) {
    // Position currently flashing during the watch phase, or -1 when nothing is lit.
    var activePosition by remember { mutableStateOf(-1) }
    // How many of the sequence's tiles are lit during the repeat phase.
    var tappedCount by remember { mutableStateOf(0) }
    var repeating by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            repeating = false
            tappedCount = 0
            activePosition = -1
            delay(500)

            // Watch phase: flash each tile in order.
            for (position in DemoSequence) {
                activePosition = position
                delay(FlashOnMillis)
                activePosition = -1
                delay(FlashOffMillis)
            }
            delay(400)

            // Repeat phase: re-light the same tiles one by one and leave them lit.
            repeating = true
            for (index in DemoSequence.indices) {
                tappedCount = index + 1
                delay(ReplayStepMillis)
            }
            delay(1200)
        }
    }

    val tappedSet = remember(tappedCount) { DemoSequence.take(tappedCount).toSet() }
    val cellMax = if (LocalIsCompactHeight.current) 56.dp else 72.dp

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.ghost_grid_demo_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier.widthIn(max = cellMax * DemoGridSize),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            for (y in 0 until DemoGridSize) {
                Row {
                    for (x in 0 until DemoGridSize) {
                        val position = y * DemoGridSize + x
                        val isTapped = position in tappedSet
                        val lit = position == activePosition || isTapped
                        // Order within the sequence (0-based), or -1 for tiles not in the sequence.
                        val order = DemoSequence.indexOf(position)
                        DemoGridTile(
                            face = if (lit) Primary else MaterialTheme.colorScheme.surfaceVariant,
                            isSelected = isTapped,
                            // Reveal the order number only while the tile is lit (orange), not on gray tiles.
                            label = if (order >= 0 && lit) "${order + 1}" else null,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(4.dp),
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(
                if (repeating) Res.string.ghost_grid_demo_repeat else Res.string.ghost_grid_demo_watch,
            ),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}

@Composable
private fun DemoGridTile(
    face: Color,
    isSelected: Boolean,
    label: String?,
    modifier: Modifier = Modifier,
) {
    PrismTile(
        face = face,
        modifier = modifier,
        isClickable = false,
        isSelected = isSelected,
        onClick = {},
    ) {
        if (label != null) {
            // The label only ever shows on a lit (orange) tile, so white reads cleanly.
            Text(
                text = label,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
