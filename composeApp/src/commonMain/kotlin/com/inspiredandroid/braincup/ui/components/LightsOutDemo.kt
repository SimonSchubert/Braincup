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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.game_lights_out_desc
import braincup.composeapp.generated.resources.lights_out_demo_title
import com.inspiredandroid.braincup.ui.theme.LightsOutOffColor
import com.inspiredandroid.braincup.ui.theme.LightsOutOnColor
import com.inspiredandroid.braincup.ui.theme.Primary
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

private const val DemoGridSize = 3

// Bit i set = light i is on. Start with every light on except the two middle-edge cells (1 and 7):
// a symmetric pattern that clears in three taps.
//   0 1 2     X . X
//   3 4 5  =  X X X
//   6 7 8     X . X
private const val StartMask = 0b1_0111_1101

// The three presses that switch everything off: 1 (top edge), 7 (bottom edge), 4 (center). Presses
// commute, so the order only affects how the animation reads (it walks through a plus shape).
private val TapSequence = listOf(1, 7, 4)

private const val PressHighlightMillis = 480L
private const val StepGapMillis = 260L
private const val SolvedHoldMillis = 1300L
private const val ResetPauseMillis = 500L

/**
 * Animated tutorial board for Lights Out: a 3x3 grid that solves itself in three taps. Each step
 * pulses the cell about to be pressed in [Primary], then flips that cell and its four orthogonal
 * neighbours (the real game's rule) until every light is off, then loops. Plays on its own with no
 * interaction, like [SchulteTableDemo].
 */
@Composable
fun LightsOutDemo(modifier: Modifier = Modifier) {
    var lights by remember { mutableIntStateOf(StartMask) }
    var activeIndex by remember { mutableIntStateOf(-1) }

    LaunchedEffect(Unit) {
        while (true) {
            lights = StartMask
            activeIndex = -1
            delay(ResetPauseMillis)

            for (index in TapSequence) {
                activeIndex = index
                delay(PressHighlightMillis)
                lights = pressLights(lights, index)
                activeIndex = -1
                delay(StepGapMillis)
            }
            delay(SolvedHoldMillis)
        }
    }

    val cellMax = if (LocalIsCompactHeight.current) 56.dp else 72.dp

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.lights_out_demo_title),
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
                        val index = y * DemoGridSize + x
                        DemoLightCell(
                            on = (lights shr index) and 1 == 1,
                            isActive = index == activeIndex,
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
            text = stringResource(Res.string.game_lights_out_desc),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}

// Flip cell [index] and its orthogonal neighbours, mirroring LightsOutGame.applyPress.
private fun pressLights(mask: Int, index: Int): Int {
    var result = mask xor (1 shl index)
    val r = index / DemoGridSize
    val c = index % DemoGridSize
    if (r > 0) result = result xor (1 shl (index - DemoGridSize))
    if (r < DemoGridSize - 1) result = result xor (1 shl (index + DemoGridSize))
    if (c > 0) result = result xor (1 shl (index - 1))
    if (c < DemoGridSize - 1) result = result xor (1 shl (index + 1))
    return result
}

@Composable
private fun DemoLightCell(
    on: Boolean,
    isActive: Boolean,
    modifier: Modifier = Modifier,
) {
    // Match the real LightsOutCell colors (amber on / dark off) so the demo reads as gameplay, with
    // a Primary pulse marking the cell being pressed this step.
    val face = when {
        isActive -> Primary
        on -> LightsOutOnColor
        else -> LightsOutOffColor
    }
    PrismTile(
        face = face,
        modifier = modifier,
        isClickable = false,
        isSelected = !on && !isActive,
        onClick = {},
    ) {}
}
