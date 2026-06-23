package com.inspiredandroid.braincup.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.chain_calculation_demo_title
import braincup.composeapp.generated.resources.fraction_calculation_demo_title
import braincup.composeapp.generated.resources.game_chain_calculation_desc
import braincup.composeapp.generated.resources.game_fraction_calculation_desc
import braincup.composeapp.generated.resources.game_goal
import braincup.composeapp.generated.resources.game_highest_value
import braincup.composeapp.generated.resources.game_mental_calculation_desc
import braincup.composeapp.generated.resources.game_sherlock_calculation_desc
import braincup.composeapp.generated.resources.game_value_comparison_desc
import braincup.composeapp.generated.resources.mental_calculation_demo_keep
import braincup.composeapp.generated.resources.mental_calculation_demo_title
import braincup.composeapp.generated.resources.sherlock_calculation_demo_title
import braincup.composeapp.generated.resources.value_comparison_demo_title
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import com.inspiredandroid.braincup.ui.theme.numberFontFamily
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

private const val ShowMillis = 1300L
private const val SolvedHoldMillis = 1900L
private const val ResetPauseMillis = 500L

/**
 * Shared scaffold for the calculation games' instructions. They are text/number based, so instead of
 * a board animation each one shows a worked example: the prompt holds for a beat, then the answer is
 * revealed (in green). Loops on its own, like [LightsOutDemo]. [content] receives whether the answer
 * is currently revealed.
 */
@Composable
private fun MathExampleDemo(
    title: StringResource,
    caption: StringResource,
    modifier: Modifier = Modifier,
    content: @Composable (revealed: Boolean) -> Unit,
) {
    var revealed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        while (true) {
            revealed = false
            delay(ResetPauseMillis)
            delay(ShowMillis)
            revealed = true
            delay(SolvedHoldMillis)
        }
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(24.dp))
        content(revealed)
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(caption),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}

/** The answer half of an "expression = answer" line: shows "?" then fades in the green result. */
@Composable
private fun RevealedAnswer(answer: String, revealed: Boolean) {
    val alpha by animateFloatAsState(
        targetValue = if (revealed) 1f else 0.6f,
        animationSpec = tween(260),
        label = "answerReveal",
    )
    Text(
        text = if (revealed) answer else "?",
        style = MaterialTheme.typography.displaySmall.copy(fontFamily = numberFontFamily()),
        color = if (revealed) SuccessGreen else MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.graphicsLayer { this.alpha = alpha },
    )
}

// Mental Calculation chains calculations: the result of one becomes the starting number of the next.
// The demo plays a normal first calculation, then shows one follow-up calculation at a time, each
// led by the previous result (in the accent colour, with a pulse) so it is clear it is remembered.
private data class MentalLine(val lead: Int, val op: String, val result: Int, val carried: Boolean)
private val MentalLines = listOf(
    MentalLine(lead = 8, op = "+ 5", result = 13, carried = false),
    MentalLine(lead = 13, op = "* 2", result = 26, carried = true),
    MentalLine(lead = 26, op = "- 7", result = 19, carried = true),
)

private const val InsertDelayMillis = 750L
private const val StepShowMillis = 1000L
private const val StepRevealMillis = 1100L
private const val MentalLoopHoldMillis = 1600L

@Composable
fun MentalCalculationDemo(modifier: Modifier = Modifier) {
    var stepIndex by remember { mutableIntStateOf(0) }
    var revealed by remember { mutableStateOf(false) }
    // For a carried step the operation shows first, then the remembered number is inserted.
    var leadInserted by remember { mutableStateOf(true) }
    // Pulses the leading number as it is inserted.
    val carryPulse = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        while (true) {
            stepIndex = 0
            revealed = false
            leadInserted = true
            carryPulse.snapTo(1f)
            delay(ResetPauseMillis)

            for (i in MentalLines.indices) {
                stepIndex = i
                revealed = false
                if (MentalLines[i].carried) {
                    // Show "× 2 = ?" first, then drop in the remembered number after a beat.
                    leadInserted = false
                    delay(InsertDelayMillis)
                    leadInserted = true
                    carryPulse.snapTo(1.35f)
                    carryPulse.animateTo(1f, tween(380))
                } else {
                    leadInserted = true
                }
                delay(StepShowMillis)
                revealed = true
                delay(StepRevealMillis)
            }
            delay(MentalLoopHoldMillis)
        }
    }

    val line = MentalLines[stepIndex]
    val numberStyle = MaterialTheme.typography.headlineMedium.copy(fontFamily = numberFontFamily())

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.mental_calculation_demo_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(24.dp))

        // A single calculation at a time. The leading number is the result carried from the last one;
        // for carried steps its slot stays reserved and the number drops in after a short delay.
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Right-align the lead and left-align the result so both numbers hug the operators; the
            // reserved slack sits on the outer edges instead of between the symbols.
            Box(modifier = Modifier.width(40.dp), contentAlignment = Alignment.CenterEnd) {
                if (!line.carried || leadInserted) {
                    Text(
                        text = line.lead.toString(),
                        style = numberStyle,
                        fontWeight = FontWeight.Bold,
                        color = if (line.carried) Primary else MaterialTheme.colorScheme.onSurface,
                        modifier = if (line.carried) {
                            Modifier.graphicsLayer { scaleX = carryPulse.value; scaleY = carryPulse.value }
                        } else {
                            Modifier
                        },
                    )
                }
            }
            MathText(text = "${line.op} =", style = MaterialTheme.typography.headlineMedium)
            Box(modifier = Modifier.width(40.dp), contentAlignment = Alignment.CenterStart) {
                Text(
                    text = if (revealed) line.result.toString() else "?",
                    style = numberStyle,
                    fontWeight = FontWeight.Bold,
                    color = if (revealed) Primary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.height(20.dp))

        // Spell out that the leading number is the previous result.
        Text(
            text = if (line.carried) {
                stringResource(Res.string.mental_calculation_demo_keep, line.lead)
            } else {
                stringResource(Res.string.game_mental_calculation_desc)
            },
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}

@Composable
fun ChainCalculationDemo(modifier: Modifier = Modifier) {
    MathExampleDemo(
        title = Res.string.chain_calculation_demo_title,
        caption = Res.string.game_chain_calculation_desc,
        modifier = modifier,
    ) { revealed ->
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            // 8 x 3 resolves before the + (see Calculator), so 5 + 24 = 29.
            MathText(text = "5 + 8 * 3 =", style = MaterialTheme.typography.displaySmall)
            RevealedAnswer(answer = "29", revealed = revealed)
        }
    }
}

// Fraction Calculation worked example for (3/4) × (8/3) = 2. The animation: the × between the two
// fractions moves inside, becoming a numerator (3 × 8) and a denominator (4 × 3); each multiplies
// down into a single number one at a time (24 and 12); then the resulting 24/12 divides to 2.
private enum class FractionPhase { SPLIT, COMBINED, NUM_MERGED, DEN_MERGED, RESULT }

private const val FractionSplitMillis = 1200L
private const val FractionCombineMillis = 1000L
private const val FractionNumMergeMillis = 1000L
private const val FractionDenMergeMillis = 1000L
private const val FractionResultMillis = 1700L
private const val FractionResetMillis = 500L

@Composable
fun FractionCalculationDemo(modifier: Modifier = Modifier) {
    var phase by remember { mutableStateOf(FractionPhase.SPLIT) }
    // Each part pops as its two numbers multiply into one.
    val numPulse = remember { Animatable(1f) }
    val denPulse = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        suspend fun Animatable<Float, *>.pop() {
            snapTo(1.3f)
            animateTo(1f, tween(340))
        }
        while (true) {
            phase = FractionPhase.SPLIT
            numPulse.snapTo(1f)
            denPulse.snapTo(1f)
            delay(FractionResetMillis)
            delay(FractionSplitMillis)
            phase = FractionPhase.COMBINED
            delay(FractionCombineMillis)
            phase = FractionPhase.NUM_MERGED
            numPulse.pop()
            delay(FractionNumMergeMillis)
            phase = FractionPhase.DEN_MERGED
            denPulse.pop()
            delay(FractionDenMergeMillis)
            phase = FractionPhase.RESULT
            delay(FractionResultMillis)
        }
    }

    val fractionStyle = MaterialTheme.typography.displaySmall

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.fraction_calculation_demo_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(24.dp))

        Crossfade(targetState = phase == FractionPhase.SPLIT, label = "fractionSplit") { isSplit ->
            if (isSplit) {
                // Two separate fractions with the multiplier between them.
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    FractionText(numerator = "3", denominator = "4", style = fractionStyle)
                    MathText(text = "*", style = fractionStyle)
                    FractionText(numerator = "8", denominator = "3", style = fractionStyle)
                }
            } else {
                // One fraction; the × has moved inside, and each part merges to a single number.
                val numMerged = phase != FractionPhase.COMBINED
                val denMerged = phase == FractionPhase.DEN_MERGED || phase == FractionPhase.RESULT
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    BarFraction(
                        numerator = {
                            FractionPart(
                                pair = "3" to "8",
                                merged = if (numMerged) "24" else null,
                                scale = numPulse.value,
                                style = fractionStyle,
                            )
                        },
                        denominator = {
                            FractionPart(
                                pair = "4" to "3",
                                merged = if (denMerged) "12" else null,
                                scale = denPulse.value,
                                style = fractionStyle,
                            )
                        },
                    )
                    AnimatedVisibility(visible = phase == FractionPhase.RESULT) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            MathText(text = "=", style = fractionStyle)
                            Text(
                                text = "2",
                                style = fractionStyle.copy(fontFamily = numberFontFamily()),
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen,
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(Res.string.game_fraction_calculation_desc),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}

/** A fraction with composable numerator/denominator slots and a centred bar (like [FractionText]). */
@Composable
private fun BarFraction(
    numerator: @Composable () -> Unit,
    denominator: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier.width(IntrinsicSize.Min),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        numerator()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(MaterialTheme.colorScheme.onSurface),
        )
        denominator()
    }
}

/**
 * One numerator/denominator part. Before merging it shows the two numbers with a × between them (as
 * a Row, so it always lays out on one line); once [merged] is set it shows that single number,
 * scaled by [scale] for a brief pop.
 */
@Composable
private fun FractionPart(
    pair: Pair<String, String>,
    merged: String?,
    scale: Float,
    style: androidx.compose.ui.text.TextStyle,
) {
    val numberStyle = style.copy(fontFamily = numberFontFamily())
    val popModifier = Modifier.graphicsLayer { scaleX = scale; scaleY = scale }
    if (merged != null) {
        Text(text = merged, style = numberStyle, modifier = popModifier)
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(text = pair.first, style = numberStyle)
            Text(text = "×", style = numberStyle)
            Text(text = pair.second, style = numberStyle)
        }
    }
}

@Composable
fun SherlockCalculationDemo(modifier: Modifier = Modifier) {
    MathExampleDemo(
        title = Res.string.sherlock_calculation_demo_title,
        caption = Res.string.game_sherlock_calculation_desc,
        modifier = modifier,
    ) { revealed ->
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(Res.string.game_goal, 21),
                style = MaterialTheme.typography.headlineMedium.copy(fontFamily = numberFontFamily()),
            )
            Spacer(Modifier.height(16.dp))
            // The available numbers; the example combines them to hit the goal.
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("3", "7").forEach { number ->
                    PrismTile(
                        face = MaterialTheme.colorScheme.surfaceContainer,
                        modifier = Modifier.size(44.dp),
                        isClickable = false,
                        onClick = {},
                    ) {
                        Text(
                            text = number,
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = numberFontFamily(),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Box(modifier = Modifier.height(40.dp), contentAlignment = Alignment.Center) {
                if (revealed) {
                    MathText(
                        text = "3 * 7 = 21",
                        style = MaterialTheme.typography.headlineSmall,
                        color = SuccessGreen,
                    )
                } else {
                    Text(
                        text = "?",
                        style = MaterialTheme.typography.headlineSmall.copy(fontFamily = numberFontFamily()),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

private data class ComparisonOption(val expression: String, val value: Int, val isLargest: Boolean)

private val ComparisonOptions = listOf(
    ComparisonOption("4 * 3", value = 12, isLargest = false),
    ComparisonOption("7 + 6", value = 13, isLargest = false),
    ComparisonOption("2 * 8", value = 16, isLargest = true), // the answer
)

private const val ComparisonShowMillis = 700L
private const val ComparisonRevealStepMillis = 750L
private const val ComparisonBeforeHighlightMillis = 600L
private const val ComparisonHoldMillis = 1900L

@Composable
fun ValueComparisonDemo(modifier: Modifier = Modifier) {
    // How many option results have been worked out so far, and whether the winner is highlighted.
    var revealedCount by remember { mutableIntStateOf(0) }
    var highlighted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            revealedCount = 0
            highlighted = false
            delay(ResetPauseMillis)
            delay(ComparisonShowMillis)
            // Work out each option's value one by one so the comparison is clear.
            for (i in ComparisonOptions.indices) {
                revealedCount = i + 1
                delay(ComparisonRevealStepMillis)
            }
            delay(ComparisonBeforeHighlightMillis)
            highlighted = true
            delay(ComparisonHoldMillis)
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.value_comparison_demo_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(Res.string.game_highest_value),
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier.widthIn(max = 260.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ComparisonOptions.forEachIndexed { index, option ->
                ComparisonButton(
                    option = option,
                    resultVisible = index < revealedCount,
                    isWinner = highlighted && option.isLargest,
                    dimmed = highlighted && !option.isLargest,
                )
            }
        }
        Spacer(Modifier.height(20.dp))

        Text(
            text = stringResource(Res.string.game_value_comparison_desc),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}

@Composable
private fun ComparisonButton(
    option: ComparisonOption,
    resultVisible: Boolean,
    isWinner: Boolean,
    dimmed: Boolean,
) {
    val face by animateColorAsState(
        targetValue = if (isWinner) SuccessGreen else Primary,
        animationSpec = tween(300),
        label = "comparisonFace",
    )
    val alpha by animateFloatAsState(
        targetValue = if (dimmed) 0.45f else 1f,
        animationSpec = tween(300),
        label = "comparisonAlpha",
    )
    PrismTile(
        face = face,
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .alpha(alpha),
        isClickable = false,
        onClick = {},
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            MathText(text = option.expression, color = Color.White)
            // Each option's value is filled in so it is clear which is highest.
            AnimatedVisibility(
                visible = resultVisible,
                enter = fadeIn(tween(260)) + scaleIn(tween(260), initialScale = 0.6f),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    MathText(text = "=", color = Color.White)
                    Text(
                        text = option.value.toString(),
                        style = MaterialTheme.typography.titleMedium.copy(fontFamily = numberFontFamily()),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }
            }
        }
    }
}
