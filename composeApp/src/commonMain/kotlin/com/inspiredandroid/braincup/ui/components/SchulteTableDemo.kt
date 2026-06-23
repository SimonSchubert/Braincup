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
import braincup.composeapp.generated.resources.game_schulte_instruction
import braincup.composeapp.generated.resources.schulte_demo_title
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.numberFontFamily
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

private const val DemoGridSize = 3

// A fixed scramble of 1..9 indexed by board position, so the demo plays out the same every loop
// and reads as a real (non-sorted) Schulte board.
private val DemoNumbers = listOf(3, 7, 1, 8, 5, 9, 2, 6, 4)

private const val HighlightMillis = 450L
private const val StepGapMillis = 120L
private const val SolvedHoldMillis = 1300L
private const val ResetPauseMillis = 400L

/**
 * Animated tutorial board for Schulte Table: a 3x3 grid of shuffled numbers that taps itself in
 * ascending order (1..9), pulsing the next number in [Primary] before sinking it into the tapped
 * state, then loops. No interaction needed; it plays on its own like [GhostGridDemo].
 */
@Composable
fun SchulteTableDemo(modifier: Modifier = Modifier) {
    // Numbers <= tappedCount have been tapped; activeNumber is the one pulsing right now (0 = none).
    var tappedCount by remember { mutableStateOf(0) }
    var activeNumber by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            tappedCount = 0
            activeNumber = 0
            delay(ResetPauseMillis)

            for (n in 1..DemoGridSize * DemoGridSize) {
                activeNumber = n
                delay(HighlightMillis)
                tappedCount = n
                activeNumber = 0
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
            text = stringResource(Res.string.schulte_demo_title),
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
                        val number = DemoNumbers[y * DemoGridSize + x]
                        val isTapped = number <= tappedCount
                        val isActive = number == activeNumber
                        DemoNumberTile(
                            number = number,
                            isTapped = isTapped,
                            isActive = isActive,
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
            text = stringResource(Res.string.game_schulte_instruction),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}

@Composable
private fun DemoNumberTile(
    number: Int,
    isTapped: Boolean,
    isActive: Boolean,
    modifier: Modifier = Modifier,
) {
    // Match the real SchulteCell colors so the demo reads as actual gameplay, plus a Primary
    // pulse on the number being tapped to spell out the ascending order.
    val face = when {
        isActive -> Primary
        isTapped -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surfaceContainer
    }
    val textColor = when {
        isActive -> Color.White
        isTapped -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        else -> MaterialTheme.colorScheme.onSurface
    }
    PrismTile(
        face = face,
        modifier = modifier,
        isClickable = false,
        isSelected = isTapped,
        onClick = {},
    ) {
        Text(
            text = number.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontFamily = numberFontFamily(),
            fontWeight = FontWeight.Bold,
            color = textColor,
            textAlign = TextAlign.Center,
        )
    }
}
