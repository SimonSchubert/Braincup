package com.inspiredandroid.braincup.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.simon_says_demo_repeat
import braincup.composeapp.generated.resources.simon_says_demo_title
import braincup.composeapp.generated.resources.simon_says_demo_watch
import com.inspiredandroid.braincup.games.SimonSaysGame
import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.games.tools.composeColor
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

// Roughly matches the real game's flash pacing (SimonSaysGame.FLASH_MILLIS).
private const val FlashOnMillis = 700L
private const val FlashOffMillis = 250L
private const val ReplayStepMillis = 500L

// Hand-picked sequence with a deliberate repeat (GREEN appears twice) so the tutorial visibly
// demonstrates that repeats are legal: only the most recently tapped pad stays highlighted, not
// every pad tapped so far.
private val DemoSequence = listOf(Color.GREEN, Color.RED, Color.GREEN, Color.YELLOW)

private val DemoCaptions = persistentListOf(
    Res.string.simon_says_demo_watch,
    Res.string.simon_says_demo_repeat,
)

/**
 * Animated tutorial board for Simon Says. The point it has to get across is the asymmetry: the
 * "watch" phase flashes a single new pad, then the "repeat" phase walks the whole sequence back
 * from the start, so the player sees that they owe more taps than they were shown. Loops with one
 * more step added each time -- unlike [GhostGridDemo], which shows a single fixed sequence once.
 * No interaction needed; it plays on its own.
 */
@Composable
fun SimonSaysDemo(modifier: Modifier = Modifier) {
    var activeColor by remember { mutableStateOf<Color?>(null) }
    var tappedCount by remember { mutableStateOf(0) }
    var repeating by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            for (roundLength in 1..DemoSequence.size) {
                repeating = false
                tappedCount = 0
                activeColor = null
                delay(500)

                // Watch phase: only the pad this round adds, never the ones already seen.
                activeColor = DemoSequence[roundLength - 1]
                delay(FlashOnMillis)
                activeColor = null
                delay(FlashOffMillis)
                delay(400)

                // Repeat phase: walk the *whole* sequence, one pad at a time, leaving the most
                // recent one lit -- this is the half the player owes back from memory.
                repeating = true
                for (i in 0 until roundLength) {
                    tappedCount = i + 1
                    delay(ReplayStepMillis)
                }
                delay(1200)
            }
        }
    }

    val litColor = if (repeating) DemoSequence.getOrNull(tappedCount - 1) else activeColor
    val cellMax = if (LocalIsCompactHeight.current) 72.dp else 96.dp

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.simon_says_demo_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(16.dp))

        SimonDisc(modifier = Modifier.widthIn(max = cellMax * 2)) { index, quadrant, padModifier ->
            val color = SimonSaysGame.PADS[index]
            DemoSimonPad(
                color = color,
                lit = color == litColor,
                quadrant = quadrant,
                modifier = padModifier,
            )
        }
        Spacer(Modifier.height(16.dp))

        DemoCaption(
            current = if (repeating) Res.string.simon_says_demo_repeat else Res.string.simon_says_demo_watch,
            all = DemoCaptions,
        )
    }
}

@Composable
private fun DemoSimonPad(
    color: Color,
    lit: Boolean,
    quadrant: SimonQuadrant,
    modifier: Modifier = Modifier,
) {
    val baseColor = color.composeColor()
    val animatedColor by animateColorAsState(
        targetValue = simonPadColor(baseColor, lit),
        animationSpec = SimonPadColorSpec,
        label = "simonDemoPadColor",
    )
    val shape = remember(quadrant) { simonQuadrantShape(quadrant) }

    Box(
        modifier = modifier
            .clip(shape)
            .background(animatedColor),
    )
}
