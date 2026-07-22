package com.inspiredandroid.braincup.ui.components

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.game_prism_clear_desc
import com.inspiredandroid.braincup.games.PrismTileType
import com.inspiredandroid.braincup.games.tools.composeColor
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

private const val DemoCols = 6
private val PrismClearDemoCellSize = 36.dp

/** Frame = flat board of type ordinals (or -1 empty), selected index, hold ms. */
private data class PrismClearDemoFrame(
    val cells: List<Int>,
    val selected: Int?,
    val holdMillis: Long,
)

// Worked-example loop on a 2×6 crop (level-1 height packing).
private val DemoFrames = listOf(
    PrismClearDemoFrame(
        cells = listOf(0, 2, 2, -1, -1, -1, 2, 0, 1, 0, 1, 1),
        selected = null,
        holdMillis = 900,
    ),
    PrismClearDemoFrame(
        cells = listOf(0, 2, 2, -1, -1, -1, 2, 0, 1, 0, 1, 1),
        selected = 0,
        holdMillis = 600,
    ),
    PrismClearDemoFrame(
        cells = listOf(2, 2, 2, -1, -1, -1, 0, 0, 1, 0, 1, 1),
        selected = null,
        holdMillis = 500,
    ),
    PrismClearDemoFrame(
        cells = listOf(-1, -1, -1, -1, -1, -1, 0, 0, 1, 0, 1, 1),
        selected = null,
        holdMillis = 700,
    ),
    PrismClearDemoFrame(
        cells = listOf(-1, -1, -1, -1, -1, -1, 0, 0, 1, 0, 1, 1),
        selected = 8,
        holdMillis = 600,
    ),
    PrismClearDemoFrame(
        cells = listOf(-1, -1, -1, -1, -1, -1, 0, 0, 0, 1, 1, 1),
        selected = null,
        holdMillis = 500,
    ),
    PrismClearDemoFrame(
        cells = List(12) { -1 },
        selected = null,
        holdMillis = 1400,
    ),
)

/**
 * Animated tutorial for Prism Clear: the design worked example plays on a loop,
 * showing select-then-swap and cascading clears with no refill. Colored tiles only.
 */
@Composable
fun PrismClearDemo(modifier: Modifier = Modifier) {
    var frameIndex by remember { mutableIntStateOf(0) }
    val frame = DemoFrames[frameIndex]

    LaunchedEffect(Unit) {
        while (true) {
            for (i in DemoFrames.indices) {
                frameIndex = i
                delay(DemoFrames[i].holdMillis)
            }
            delay(400)
        }
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            for (row in 0 until 2) {
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    for (col in 0 until DemoCols) {
                        val index = row * DemoCols + col
                        val ordinal = frame.cells[index]
                        val selected = frame.selected == index
                        val face = if (ordinal < 0) {
                            MaterialTheme.colorScheme.surfaceContainerHighest
                        } else {
                            PrismTileType.entries[ordinal].color.composeColor()
                        }
                        PrismTile(
                            face = face,
                            modifier = Modifier.size(PrismClearDemoCellSize),
                            isSelected = selected,
                            isClickable = false,
                            onClick = {},
                        ) {}
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(Res.string.game_prism_clear_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
    }
}
