package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.GamePreviewHost
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

private enum class BullsAndCowsDemoPhase { STEP1, STEP2, STEP3 }

@Composable
fun BullsAndCowsDemo(modifier: Modifier = Modifier) {
    var phase by remember { mutableStateOf(BullsAndCowsDemoPhase.STEP1) }
    var typedDigits by remember { mutableStateOf("") }
    var loop by remember { mutableIntStateOf(0) }

    LaunchedEffect(loop) {
        // Step 1: Animate typing "1234"
        phase = BullsAndCowsDemoPhase.STEP1
        typedDigits = ""
        delay(600)
        val guess1 = "1234"
        for (i in guess1.indices) {
            typedDigits = guess1.take(i + 1)
            delay(300)
        }
        delay(1200)

        // Step 2: Show Bulls / Cows result badge for guess "1234" (1 Bull, 2 Cows)
        phase = BullsAndCowsDemoPhase.STEP2
        delay(2500)

        // Step 3: Crack the code! Enter winning guess "1356" with "4 Bulls" victory
        phase = BullsAndCowsDemoPhase.STEP3
        typedDigits = ""
        delay(600)
        val winningGuess = "1356"
        for (i in winningGuess.indices) {
            typedDigits = winningGuess.take(i + 1)
            delay(250)
        }
        delay(2000)

        loop++
    }

    val caption = when (phase) {
        BullsAndCowsDemoPhase.STEP1 -> Res.string.bulls_and_cows_demo_step1
        BullsAndCowsDemoPhase.STEP2 -> Res.string.bulls_and_cows_demo_step2
        BullsAndCowsDemoPhase.STEP3 -> Res.string.bulls_and_cows_demo_step3
    }

    val captions = persistentListOf(
        Res.string.bulls_and_cows_demo_step1,
        Res.string.bulls_and_cows_demo_step2,
        Res.string.bulls_and_cows_demo_step3,
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.bulls_and_cows_demo_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        DemoCaption(
            current = caption,
            all = captions,
            emphasis = persistentSetOf(Res.string.bulls_and_cows_demo_step3),
        )

        Spacer(Modifier.height(16.dp))

        // Show a brief demonstration row representing a guess / feedback
        Row(
            modifier = Modifier
                .widthIn(max = 340.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val slotsValue = when (phase) {
                BullsAndCowsDemoPhase.STEP1 -> typedDigits
                BullsAndCowsDemoPhase.STEP2 -> "1234"
                BullsAndCowsDemoPhase.STEP3 -> typedDigits
            }

            DigitMemorySlots(
                length = 4,
                value = slotsValue,
                accent = MaterialTheme.colorScheme.primary,
                revealColor = if (phase == BullsAndCowsDemoPhase.STEP3 && slotsValue.length == 4) com.inspiredandroid.braincup.ui.theme.SuccessGreen else null,
                modifier = Modifier.width(180.dp),
            )

            Box(
                modifier = Modifier.width(110.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                when (phase) {
                    BullsAndCowsDemoPhase.STEP1 -> {
                        // Empty spacer or typing indicator
                    }
                    BullsAndCowsDemoPhase.STEP2 -> {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = com.inspiredandroid.braincup.ui.theme.SuccessGreen.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, com.inspiredandroid.braincup.ui.theme.SuccessGreen.copy(alpha = 0.5f)),
                            ) {
                                Text(
                                    text = "1 Bull",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = com.inspiredandroid.braincup.ui.theme.SuccessGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                )
                            }

                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                            ) {
                                Text(
                                    text = "2 Cows",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                )
                            }
                        }
                    }
                    BullsAndCowsDemoPhase.STEP3 -> {
                        if (slotsValue.length == 4) {
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = com.inspiredandroid.braincup.ui.theme.SuccessGreen.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, com.inspiredandroid.braincup.ui.theme.SuccessGreen.copy(alpha = 0.5f)),
                            ) {
                                Text(
                                    text = "4 Bulls!",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = com.inspiredandroid.braincup.ui.theme.SuccessGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@DevicePreviews
@Composable
private fun BullsAndCowsDemoPreview() {
    GamePreviewHost {
        BullsAndCowsDemo()
    }
}
