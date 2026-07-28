package com.inspiredandroid.braincup.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.bulls_and_cows_bulls
import braincup.composeapp.generated.resources.bulls_and_cows_cows
import braincup.composeapp.generated.resources.bulls_and_cows_demo_bull
import braincup.composeapp.generated.resources.bulls_and_cows_demo_cow
import braincup.composeapp.generated.resources.bulls_and_cows_demo_miss
import braincup.composeapp.generated.resources.bulls_and_cows_demo_step1
import braincup.composeapp.generated.resources.bulls_and_cows_demo_step2
import braincup.composeapp.generated.resources.bulls_and_cows_demo_step3
import braincup.composeapp.generated.resources.bulls_and_cows_demo_title
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.GamePreviewHost
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import com.inspiredandroid.braincup.ui.theme.WordleAbsent
import com.inspiredandroid.braincup.ui.theme.WordlePresent
import com.inspiredandroid.braincup.ui.theme.numberFontFamily
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

/**
 * Pedagogical demo for Bulls & Cows.
 *
 * Fixed secret for the story: 1356.
 * Phase 1 guess 1234 → 1 bull (1), 1 cow (3), 2 misses (2, 4).
 * Phase 2: bull stays; cow 3 slides into the correct slot and becomes a bull → 1378 (2 bulls).
 *
 * Per-digit colours are tutorial-only; live play still shows counts only.
 * Bull = green, Cow = amber — used on tiles, under-labels, count chips, and caption words
 * so each term maps clearly to a box.
 */
private enum class DemoDigitRole { NEUTRAL, BULL, COW, MISS }

private enum class BullsAndCowsDemoPhase {
    /** Digits visible, still unscored. */
    GUESS,

    /** Roles painted; counts shown. */
    REVEAL,

    /** Labels + definition caption. */
    EXPLAIN,

    /** Cow digit sliding into the bull slot. */
    MOVE,

    /** Improved guess settled (2 bulls). */
    IMPROVED,
}

/** Shared palette: tile face, under-label, count chip, and caption word all use these. */
private val BullColor = SuccessGreen
private val CowColor = WordlePresent
private val MissColor = WordleAbsent

private val GuessDigits = listOf('1', '2', '3', '4')
private val GuessRoles = listOf(
    DemoDigitRole.BULL,
    DemoDigitRole.MISS,
    DemoDigitRole.COW,
    DemoDigitRole.MISS,
)

// After moving cow 3 from index 2 → index 1 (secret 1356).
private val ImprovedDigits = listOf('1', '3', '7', '8')
private val ImprovedRoles = listOf(
    DemoDigitRole.BULL,
    DemoDigitRole.BULL,
    DemoDigitRole.MISS,
    DemoDigitRole.MISS,
)

private const val CowFromIndex = 2
private const val CowToIndex = 1

private const val ResetPauseMillis = 500L
private const val TypeStepMillis = 220L
private const val AfterTypeMillis = 700L
private const val RevealStaggerMillis = 320L
private const val RevealHoldMillis = 1600L
private const val ExplainHoldMillis = 2800L
private const val MoveMillis = 520
private const val ImprovedHoldMillis = 2400L

@Composable
fun BullsAndCowsDemo(modifier: Modifier = Modifier) {
    var phase by remember { mutableStateOf(BullsAndCowsDemoPhase.GUESS) }
    var visibleCount by remember { mutableIntStateOf(0) }
    var roles by remember { mutableStateOf(List(4) { DemoDigitRole.NEUTRAL }) }
    var digits by remember { mutableStateOf(GuessDigits) }
    var showLabels by remember { mutableStateOf(false) }
    var bulls by remember { mutableIntStateOf(0) }
    var cows by remember { mutableIntStateOf(0) }
    var showCounts by remember { mutableStateOf(false) }
    // 0 = cow at from-index, 1 = cow at to-index. Only used during MOVE.
    val cowSlide = remember { Animatable(0f) }
    // Hide the miss being replaced while the cow flies in.
    var hideTargetSlot by remember { mutableStateOf(false) }
    // Hide the cow's origin slot while it is mid-flight (overlay draws it).
    var hideCowOrigin by remember { mutableStateOf(false) }
    var loop by remember { mutableIntStateOf(0) }

    LaunchedEffect(loop) {
        // Reset
        phase = BullsAndCowsDemoPhase.GUESS
        visibleCount = 0
        roles = List(4) { DemoDigitRole.NEUTRAL }
        digits = GuessDigits
        showLabels = false
        bulls = 0
        cows = 0
        showCounts = false
        hideTargetSlot = false
        hideCowOrigin = false
        cowSlide.snapTo(0f)
        delay(ResetPauseMillis)

        // Type-in 1234
        for (i in 1..4) {
            visibleCount = i
            delay(TypeStepMillis)
        }
        delay(AfterTypeMillis)

        // Reveal roles left → right; labels appear with each role so text maps to boxes.
        phase = BullsAndCowsDemoPhase.REVEAL
        showLabels = true
        for (i in GuessRoles.indices) {
            roles = roles.toMutableList().also { it[i] = GuessRoles[i] }
            delay(RevealStaggerMillis)
        }
        bulls = 1
        cows = 1
        showCounts = true
        delay(RevealHoldMillis)

        // Explain bull vs cow
        phase = BullsAndCowsDemoPhase.EXPLAIN
        delay(ExplainHoldMillis)

        // Move cow 3 into the correct place
        phase = BullsAndCowsDemoPhase.MOVE
        showLabels = false
        hideTargetSlot = true
        hideCowOrigin = true
        cowSlide.snapTo(0f)
        cowSlide.animateTo(1f, tween(durationMillis = MoveMillis))

        // Settled improved guess — show Bull labels on both greens.
        phase = BullsAndCowsDemoPhase.IMPROVED
        digits = ImprovedDigits
        roles = ImprovedRoles
        hideTargetSlot = false
        hideCowOrigin = false
        cowSlide.snapTo(0f)
        bulls = 2
        cows = 0
        showLabels = true
        delay(ImprovedHoldMillis)

        loop++
    }

    val bullWord = stringResource(Res.string.bulls_and_cows_demo_bull)
    val cowWord = stringResource(Res.string.bulls_and_cows_demo_cow)
    val caption = coloredCaption(phase = phase, bullWord = bullWord, cowWord = cowWord)
    val reserveCaptions = persistentListOf(
        stringResource(Res.string.bulls_and_cows_demo_step1),
        stringResource(Res.string.bulls_and_cows_demo_step2),
        stringResource(Res.string.bulls_and_cows_demo_step3),
    )

    val compact = LocalIsCompactHeight.current
    val tileSize = if (compact) 40.dp else 48.dp
    val spacing = 6.dp
    val density = LocalDensity.current
    val slidePx = with(density) { (tileSize + spacing).toPx() }
    // Cow slides left by one slot (from index 2 → 1).
    val cowOffsetX = -(cowSlide.value * slidePx)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.bulls_and_cows_demo_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        DemoCaptionAnnotated(
            text = caption,
            reserveTexts = reserveCaptions,
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .widthIn(max = 340.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box {
                Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
                    repeat(4) { index ->
                        val visible = index < visibleCount
                        val isFlyingOrigin = hideCowOrigin && index == CowFromIndex
                        val isReplacedTarget = hideTargetSlot && index == CowToIndex
                        val digit = when {
                            !visible || isFlyingOrigin || isReplacedTarget -> null
                            else -> digits[index]
                        }
                        val role = if (digit == null && !isReplacedTarget) {
                            DemoDigitRole.NEUTRAL
                        } else if (isReplacedTarget) {
                            DemoDigitRole.NEUTRAL
                        } else {
                            roles[index]
                        }
                        val label = when {
                            !showLabels || digit == null -> null
                            role == DemoDigitRole.BULL ->
                                stringResource(Res.string.bulls_and_cows_demo_bull)
                            role == DemoDigitRole.COW ->
                                stringResource(Res.string.bulls_and_cows_demo_cow)
                            role == DemoDigitRole.MISS ->
                                stringResource(Res.string.bulls_and_cows_demo_miss)
                            else -> null
                        }
                        DemoDigitColumn(
                            digit = digit,
                            role = role,
                            label = label,
                            tileSize = tileSize,
                        )
                    }
                }

                // Flying cow overlay during MOVE — turns bull-green as it settles into place.
                if (phase == BullsAndCowsDemoPhase.MOVE) {
                    val originX = with(density) {
                        ((tileSize + spacing) * CowFromIndex).toPx()
                    }
                    val flyingRole =
                        if (cowSlide.value >= 0.85f) DemoDigitRole.BULL else DemoDigitRole.COW
                    Box(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    x = (originX + cowOffsetX).roundToInt(),
                                    y = 0,
                                )
                            },
                    ) {
                        DemoDigitTile(
                            digit = '3',
                            role = flyingRole,
                            tileSize = tileSize,
                        )
                    }
                }
            }

            // Always reserve chip width so the tiles do not jump when counts appear.
            Spacer(Modifier.width(12.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.graphicsLayer { alpha = if (showCounts) 1f else 0f },
            ) {
                CountChip(
                    label = stringResource(Res.string.bulls_and_cows_bulls, bulls),
                    color = BullColor,
                )
                CountChip(
                    label = stringResource(Res.string.bulls_and_cows_cows, cows),
                    color = CowColor,
                )
            }
        }
    }
}

/**
 * Colour-codes every occurrence of [bullWord] / [cowWord] so caption terms match tile colours.
 */
@Composable
private fun coloredCaption(
    phase: BullsAndCowsDemoPhase,
    bullWord: String,
    cowWord: String,
): AnnotatedString {
    val body = MaterialTheme.colorScheme.onSurfaceVariant
    val bullStyle = SpanStyle(color = BullColor, fontWeight = FontWeight.Bold)
    val cowStyle = SpanStyle(color = CowColor, fontWeight = FontWeight.Bold)
    val plain = SpanStyle(color = body, fontWeight = FontWeight.Normal)

    return when (phase) {
        BullsAndCowsDemoPhase.GUESS,
        BullsAndCowsDemoPhase.REVEAL,
        -> buildAnnotatedString {
            withStyle(plain) { append("1 ") }
            withStyle(bullStyle) { append(bullWord) }
            withStyle(plain) { append(", 1 ") }
            withStyle(cowStyle) { append(cowWord) }
            withStyle(plain) { append(", 2 misses.") }
        }
        BullsAndCowsDemoPhase.EXPLAIN -> buildAnnotatedString {
            withStyle(bullStyle) { append(bullWord) }
            withStyle(plain) { append(" = right place.\n") }
            withStyle(cowStyle) { append(cowWord) }
            withStyle(plain) { append(" = right digit, wrong place.") }
        }
        BullsAndCowsDemoPhase.MOVE,
        BullsAndCowsDemoPhase.IMPROVED,
        -> buildAnnotatedString {
            withStyle(plain) { append("Keep the ") }
            withStyle(bullStyle) { append(bullWord) }
            withStyle(plain) { append(".\nMove the ") }
            withStyle(cowStyle) { append(cowWord) }
            withStyle(plain) { append(" — it becomes a ") }
            withStyle(bullStyle) { append(bullWord) }
            withStyle(plain) { append(".") }
        }
    }
}

/**
 * Like [DemoCaptionText] but for [AnnotatedString] (coloured Bull / Cow words).
 */
@Composable
private fun DemoCaptionAnnotated(
    text: AnnotatedString,
    reserveTexts: kotlinx.collections.immutable.ImmutableList<String>,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        reserveTexts.forEach { reserve ->
            Text(
                text = reserve,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer { alpha = 0f },
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun DemoDigitColumn(
    digit: Char?,
    role: DemoDigitRole,
    label: String?,
    tileSize: Dp,
) {
    val roleColor = role.color()
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        DemoDigitTile(digit = digit, role = role, tileSize = tileSize)
        // Reserve label height so the board does not jiggle when labels appear.
        Box(
            modifier = Modifier
                .height(22.dp)
                .width(tileSize + 4.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (label != null && roleColor != null) {
                // Chip matches the tile face so the label is the same colour family as the box.
                Surface(
                    shape = MaterialTheme.shapes.extraSmall,
                    color = roleColor.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, roleColor.copy(alpha = 0.65f)),
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = roleColor,
                        maxLines = 1,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun DemoDigitTile(
    digit: Char?,
    role: DemoDigitRole,
    tileSize: Dp,
) {
    val targetFace = role.color() ?: MaterialTheme.colorScheme.surfaceVariant
    val face by animateColorAsState(
        targetValue = targetFace,
        animationSpec = tween(durationMillis = 280),
        label = "bullsCowsTileFace",
    )
    val textColor = when (role) {
        DemoDigitRole.NEUTRAL -> MaterialTheme.colorScheme.onSurface
        else -> Color.White
    }

    PrismCard(
        face = face,
        modifier = Modifier
            .size(tileSize)
            .graphicsLayer { alpha = if (digit == null) 0.35f else 1f },
    ) {
        if (digit != null) {
            Text(
                text = digit.toString(),
                color = textColor,
                style = MaterialTheme.typography.headlineSmall,
                fontFamily = numberFontFamily(),
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun CountChip(label: String, color: Color) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.18f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.65f)),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

private fun DemoDigitRole.color(): Color? = when (this) {
    DemoDigitRole.NEUTRAL -> null
    DemoDigitRole.BULL -> BullColor
    DemoDigitRole.COW -> CowColor
    DemoDigitRole.MISS -> MissColor
}

@DevicePreviews
@Composable
private fun BullsAndCowsDemoPreview() {
    GamePreviewHost {
        BullsAndCowsDemo()
    }
}
