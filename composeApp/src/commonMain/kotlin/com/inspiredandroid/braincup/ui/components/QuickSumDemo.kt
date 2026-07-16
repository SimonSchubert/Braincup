package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.quick_sum_demo_flash
import braincup.composeapp.generated.resources.quick_sum_demo_title
import braincup.composeapp.generated.resources.quick_sum_demo_total
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import com.inspiredandroid.braincup.ui.theme.numberFontFamily
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.delay

private val DemoTerms = listOf(3, 7, 2)
private val DemoSum = DemoTerms.sum()

// Slower than the real game's opening tier: a tutorial is read, not raced.
private const val TermVisibleMillis = 800L
private const val TermGapMillis = 260L
private const val IntroMillis = 600L
private const val RevealHoldMillis = 1800L

private enum class QuickSumDemoPhase { INTRO, FLASH, REVEAL }

@Composable
fun QuickSumDemo(modifier: Modifier = Modifier) {
    var phase by remember { mutableStateOf(QuickSumDemoPhase.INTRO) }
    var index by remember { mutableIntStateOf(0) }
    var showing by remember { mutableStateOf(false) }
    var loop by remember { mutableIntStateOf(0) }
    val outlineColor = MaterialTheme.colorScheme.outline

    LaunchedEffect(loop) {
        phase = QuickSumDemoPhase.INTRO
        index = 0
        showing = false
        delay(IntroMillis)

        phase = QuickSumDemoPhase.FLASH
        for (i in DemoTerms.indices) {
            index = i
            showing = true
            delay(TermVisibleMillis)
            // The blank gap is the mechanic, not a pause: without it two equal terms in a row
            // would read as one number that never changed.
            showing = false
            delay(TermGapMillis)
        }

        phase = QuickSumDemoPhase.REVEAL
        delay(RevealHoldMillis)
        loop++
    }

    val caption = when (phase) {
        QuickSumDemoPhase.INTRO -> Res.string.quick_sum_demo_title
        QuickSumDemoPhase.FLASH -> Res.string.quick_sum_demo_flash
        QuickSumDemoPhase.REVEAL -> Res.string.quick_sum_demo_total
    }
    val captions = persistentListOf(
        Res.string.quick_sum_demo_title,
        Res.string.quick_sum_demo_flash,
        Res.string.quick_sum_demo_total,
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DemoCaption(
            current = caption,
            all = captions,
            emphasis = persistentSetOf(Res.string.quick_sum_demo_total),
        )
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .fillMaxWidth(0.85f)
                .aspectRatio(1f),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {
                drawRect(color = outlineColor, style = Stroke(width = 2.dp.toPx()))
            }
            when (phase) {
                QuickSumDemoPhase.REVEAL -> Text(
                    text = "= $DemoSum",
                    style = MaterialTheme.typography.displaySmall,
                    fontFamily = numberFontFamily(),
                    color = SuccessGreen,
                    fontWeight = FontWeight.Bold,
                )
                else -> if (showing) {
                    Text(
                        text = DemoTerms[index].toString(),
                        style = MaterialTheme.typography.displayMedium,
                        fontFamily = numberFontFamily(),
                        color = Primary,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            DemoTerms.indices.forEach { i ->
                val lit = phase == QuickSumDemoPhase.REVEAL || (phase == QuickSumDemoPhase.FLASH && i <= index)
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(if (lit) Primary else Primary.copy(alpha = 0.25f)),
                )
            }
        }
    }
}
