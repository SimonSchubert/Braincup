package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.digit_memory_demo_title
import braincup.composeapp.generated.resources.digit_memory_memorize
import braincup.composeapp.generated.resources.digit_memory_recall
import braincup.composeapp.generated.resources.digit_memory_solve
import braincup.composeapp.generated.resources.game_digit_memory_desc
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import com.inspiredandroid.braincup.ui.theme.numberFontFamily
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

private const val Sequence = "417"
private const val Equation = "5 + 3 ="
private const val EquationAnswer = "8"

private const val MemorizeMillis = 2200L
private const val SolvePauseMillis = 700L
private const val SolveAnswerMillis = 1100L
private const val TypeMillis = 420L
private const val RecallHoldMillis = 700L
private const val SolvedHoldMillis = 1500L
private const val ResetPauseMillis = 500L

private enum class DemoPhase { MEMORIZE, SOLVE, RECALL }

/**
 * Animated tutorial for Digit Memory's three steps: a short sequence is shown to memorize, a quick
 * distraction sum is solved, then the sequence is typed back and flips green when correct. Mirrors
 * DigitMemoryGame's SHOWING → SOLVING → RECALL phases. Loops on its own, like [LightsOutDemo].
 */
@Composable
fun DigitMemoryDemo(modifier: Modifier = Modifier) {
    var phase by remember { mutableStateOf(DemoPhase.MEMORIZE) }
    var solveAnswered by remember { mutableStateOf(false) }
    // How many digits have been typed back during the recall phase.
    var typed by remember { mutableIntStateOf(0) }
    var recallCorrect by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            phase = DemoPhase.MEMORIZE
            solveAnswered = false
            typed = 0
            recallCorrect = false
            delay(ResetPauseMillis)

            delay(MemorizeMillis)

            phase = DemoPhase.SOLVE
            delay(SolvePauseMillis)
            solveAnswered = true
            delay(SolveAnswerMillis)

            phase = DemoPhase.RECALL
            repeat(Sequence.length) {
                typed++
                delay(TypeMillis)
            }
            delay(RecallHoldMillis)
            recallCorrect = true
            delay(SolvedHoldMillis)
        }
    }

    val accent = MaterialTheme.colorScheme.primary
    val slot = if (LocalIsCompactHeight.current) 36.dp else 44.dp

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.digit_memory_demo_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(
                when (phase) {
                    DemoPhase.MEMORIZE -> Res.string.digit_memory_memorize
                    DemoPhase.SOLVE -> Res.string.digit_memory_solve
                    DemoPhase.RECALL -> Res.string.digit_memory_recall
                },
            ),
            style = MaterialTheme.typography.labelLarge,
            color = accent,
            letterSpacing = 3.sp,
        )
        Spacer(Modifier.height(20.dp))

        // Reserve a stable height so the layout doesn't jump between the slot row and the sum.
        Box(
            modifier = Modifier.height(slot * 1.3f + 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            when (phase) {
                DemoPhase.SOLVE -> Text(
                    text = "$Equation ${if (solveAnswered) EquationAnswer else "?"}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontFamily = numberFontFamily(),
                    fontWeight = FontWeight.Bold,
                    color = if (solveAnswered) SuccessGreen else MaterialTheme.colorScheme.onSurface,
                )
                else -> {
                    val display = if (phase == DemoPhase.MEMORIZE) Sequence else Sequence.take(typed)
                    DigitMemorySlots(
                        length = Sequence.length,
                        value = display,
                        accent = accent,
                        revealColor = if (recallCorrect) SuccessGreen else null,
                        slotWidth = slot,
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(Res.string.game_digit_memory_desc),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}
