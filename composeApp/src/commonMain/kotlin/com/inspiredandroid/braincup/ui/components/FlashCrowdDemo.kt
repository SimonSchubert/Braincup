package com.inspiredandroid.braincup.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.flash_crowd_demo_answer
import braincup.composeapp.generated.resources.flash_crowd_demo_glance
import braincup.composeapp.generated.resources.flash_crowd_demo_title
import braincup.composeapp.generated.resources.game_flash_crowd_blue
import braincup.composeapp.generated.resources.game_flash_crowd_which_more
import braincup.composeapp.generated.resources.game_flash_crowd_yellow
import com.inspiredandroid.braincup.ui.theme.FlashCrowdBlue
import com.inspiredandroid.braincup.ui.theme.FlashCrowdYellow
import com.inspiredandroid.braincup.ui.theme.FlashCrowdYellowBottom
import com.inspiredandroid.braincup.ui.theme.FlashCrowdYellowSide
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.numberFontFamily
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import kotlin.math.max

private const val GlanceMillis = 900L
private const val FadeMillis = 200L
private const val CountTickMillis = 180L
private const val CountHoldMillis = 800L
private const val PickPauseMillis = 450L
private const val AnswerHoldMillis = 900L
private const val ResetPauseMillis = 400L

private enum class FlashDemoPhase { GLANCE, COUNT, ANSWER }

// Fixed (x, y, radius) layouts in the 0..1 canvas, mirroring GameTile's FlashCrowdPreview dots.
// Yellow clearly outnumbers Blue (6 vs 10) so the teaching answer is unambiguous; positions are
// spread out enough that the prism discs never meaningfully overlap. Listed roughly top-to-bottom
// so the count reveal sweeps downward.
private val BlueDots = listOf(
    Triple(0.25f, 0.22f, 0.055f),
    Triple(0.62f, 0.20f, 0.05f),
    Triple(0.78f, 0.45f, 0.055f),
    Triple(0.30f, 0.50f, 0.05f),
    Triple(0.55f, 0.68f, 0.055f),
    Triple(0.22f, 0.80f, 0.05f),
)

private val YellowDots = listOf(
    Triple(0.20f, 0.18f, 0.05f),
    Triple(0.45f, 0.15f, 0.045f),
    Triple(0.72f, 0.20f, 0.05f),
    Triple(0.30f, 0.42f, 0.045f),
    Triple(0.58f, 0.40f, 0.05f),
    Triple(0.85f, 0.42f, 0.045f),
    Triple(0.15f, 0.62f, 0.05f),
    Triple(0.45f, 0.65f, 0.045f),
    Triple(0.72f, 0.65f, 0.05f),
    Triple(0.50f, 0.85f, 0.045f),
)

// Every caption the demo cycles through, so the caption line can reserve the tallest one's height.
private val DemoCaptions = listOf(
    Res.string.flash_crowd_demo_glance,
    Res.string.game_flash_crowd_which_more,
    Res.string.flash_crowd_demo_answer,
)

/**
 * Animated tutorial for Flash Crowd, in the self-playing style of [OrbitTrackerDemo]. It loops four
 * steps: (1) the dots flash on each side as they do in the game (Yellow has more), (2) both clusters
 * count themselves out in parallel so Blue freezes at 6 while Yellow keeps ticking to 10, (3) the
 * Blue/Yellow tiles appear and the Yellow tile auto-presses, then it repeats. No interaction needed;
 * it plays on its own and reuses the real game's [drawPrismCircle] dots and Flash Crowd colors.
 */
@Composable
fun FlashCrowdDemo(modifier: Modifier = Modifier) {
    var phase by remember { mutableStateOf(FlashDemoPhase.GLANCE) }
    var dotsVisible by remember { mutableStateOf(true) }
    var blueCount by remember { mutableIntStateOf(0) }
    var yellowCount by remember { mutableIntStateOf(0) }
    var yellowSelected by remember { mutableStateOf(false) }

    val dotsAlpha by animateFloatAsState(
        targetValue = if (dotsVisible) 1f else 0f,
        animationSpec = tween(FadeMillis.toInt()),
        label = "flashCrowdDemoDots",
    )

    LaunchedEffect(Unit) {
        while (true) {
            // 1. Show the dots as they flashed in the game.
            phase = FlashDemoPhase.GLANCE
            blueCount = 0
            yellowCount = 0
            yellowSelected = false
            dotsVisible = true
            delay(GlanceMillis)
            dotsVisible = false
            delay(FadeMillis)

            // 2. Count both sides in parallel: Blue freezes at 6 while Yellow keeps ticking to 10.
            phase = FlashDemoPhase.COUNT
            dotsVisible = true
            val ticks = max(BlueDots.size, YellowDots.size)
            for (i in 1..ticks) {
                if (i <= BlueDots.size) blueCount = i
                if (i <= YellowDots.size) yellowCount = i
                delay(CountTickMillis)
            }
            delay(CountHoldMillis)
            dotsVisible = false
            delay(FadeMillis)

            // 3. Press the button for the side that had more.
            phase = FlashDemoPhase.ANSWER
            delay(PickPauseMillis)
            yellowSelected = true
            delay(AnswerHoldMillis)

            // 4. Repeat.
            delay(ResetPauseMillis)
        }
    }

    val compact = LocalIsCompactHeight.current
    val rowMax = if (compact) 220.dp else 280.dp
    val contentHeight = if (compact) 170.dp else 210.dp
    val idleColor = MaterialTheme.colorScheme.onSurfaceVariant

    val captionRes = when (phase) {
        FlashDemoPhase.GLANCE -> Res.string.flash_crowd_demo_glance
        FlashDemoPhase.COUNT -> Res.string.game_flash_crowd_which_more
        FlashDemoPhase.ANSWER -> Res.string.flash_crowd_demo_answer
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.flash_crowd_demo_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(contentHeight),
            contentAlignment = Alignment.Center,
        ) {
            when (phase) {
                FlashDemoPhase.GLANCE, FlashDemoPhase.COUNT -> DotsContent(
                    counting = phase == FlashDemoPhase.COUNT,
                    blueCount = blueCount,
                    yellowCount = yellowCount,
                    dotsAlpha = dotsAlpha,
                    idleColor = idleColor,
                    rowMax = rowMax,
                )
                FlashDemoPhase.ANSWER -> QuestionContent(
                    yellowSelected = yellowSelected,
                    rowMax = rowMax,
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        DemoCaption(current = captionRes, all = DemoCaptions)
    }
}

@Composable
private fun DotsContent(
    counting: Boolean,
    blueCount: Int,
    yellowCount: Int,
    dotsAlpha: Float,
    idleColor: Color,
    rowMax: Dp,
) {
    // When not counting (the glance), every dot is lit in full color; while counting, only the
    // dots already reached light up and the rest stay dimmed, so the tally reads off the board.
    val blueLit = if (counting) blueCount else BlueDots.size
    val yellowLit = if (counting) yellowCount else YellowDots.size

    Column(
        modifier = Modifier
            .widthIn(max = rowMax)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DotCluster(
                dots = BlueDots,
                litCount = blueLit,
                showRing = counting,
                fullFace = FlashCrowdBlue,
                fullSide = null,
                fullBottom = null,
                idleColor = idleColor,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .graphicsLayer { alpha = dotsAlpha },
            )
            DotCluster(
                dots = YellowDots,
                litCount = yellowLit,
                showRing = counting,
                fullFace = FlashCrowdYellow,
                fullSide = FlashCrowdYellowSide,
                fullBottom = FlashCrowdYellowBottom,
                idleColor = idleColor,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .graphicsLayer { alpha = dotsAlpha },
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TallyText(
                text = if (counting) blueCount.toString() else "",
                color = FlashCrowdBlue,
                modifier = Modifier.weight(1f),
            )
            TallyText(
                text = if (counting) yellowCount.toString() else "",
                color = FlashCrowdYellowBottom,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun TallyText(text: String, color: Color, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall,
        fontFamily = numberFontFamily(),
        fontWeight = FontWeight.Bold,
        color = color,
        textAlign = TextAlign.Center,
        modifier = modifier,
    )
}

@Composable
private fun DotCluster(
    dots: List<Triple<Float, Float, Float>>,
    litCount: Int,
    showRing: Boolean,
    fullFace: Color,
    fullSide: Color?,
    fullBottom: Color?,
    idleColor: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        dots.forEachIndexed { index, triple ->
            val (x, y, r) = triple
            val center = Offset(x * size.width, y * size.height)
            val radius = r * size.width
            val lit = index < litCount
            drawPrismCircle(
                center = center,
                radius = radius,
                face = if (lit) fullFace else idleColor,
                side = if (lit) fullSide else null,
                bottom = if (lit) fullBottom else null,
            )
            if (showRing && lit && index == litCount - 1) {
                drawCircle(
                    color = Primary,
                    radius = radius + 3.dp.toPx(),
                    center = center,
                    style = Stroke(width = 2.dp.toPx()),
                )
            }
        }
    }
}

@Composable
private fun QuestionContent(
    yellowSelected: Boolean,
    rowMax: Dp,
) {
    Column(
        modifier = Modifier
            .widthIn(max = rowMax)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.game_flash_crowd_which_more),
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PrismTile(
                face = FlashCrowdBlue,
                isClickable = false,
                onClick = {},
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp),
            ) {
                Text(
                    text = stringResource(Res.string.game_flash_crowd_blue),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                )
            }
            PrismTile(
                face = FlashCrowdYellow,
                side = FlashCrowdYellowSide,
                bottom = FlashCrowdYellowBottom,
                isClickable = false,
                isSelected = yellowSelected,
                onClick = {},
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp),
            ) {
                Text(
                    text = stringResource(Res.string.game_flash_crowd_yellow),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black,
                )
            }
        }
    }
}
