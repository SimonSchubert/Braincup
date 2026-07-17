package com.inspiredandroid.braincup.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.n_back_demo_recall
import braincup.composeapp.generated.resources.n_back_demo_stream
import com.inspiredandroid.braincup.games.tools.Shape
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.delay

// A short sequence; the demo asks for position 1, so the answer is the first shape (triangle).
private val DemoSequence = listOf(Shape.TRIANGLE, Shape.CIRCLE, Shape.STAR)
private val DemoAnswer = Shape.TRIANGLE

// Only the shapes that were shown appear as options, so the choice is easy to follow.
private val DemoOptions = listOf(Shape.CIRCLE, Shape.STAR, Shape.TRIANGLE)

// Slow, and lingering on the answer: a tutorial is read, not raced.
private const val ShapeVisibleMillis = 850L
private const val ShapeGapMillis = 350L
private const val QuestionHoldMillis = 1600L
private const val RevealHoldMillis = 2600L
private const val IntroMillis = 600L

private enum class NBackDemoPhase { FLASH, QUESTION, REVEAL }

@Composable
fun NBackDemo(modifier: Modifier = Modifier) {
    var phase by remember { mutableStateOf(NBackDemoPhase.FLASH) }
    var index by remember { mutableIntStateOf(-1) }
    var showing by remember { mutableStateOf(false) }
    var loop by remember { mutableIntStateOf(0) }

    LaunchedEffect(loop) {
        phase = NBackDemoPhase.FLASH
        index = -1
        showing = false
        delay(IntroMillis)

        for (i in DemoSequence.indices) {
            index = i
            showing = true
            delay(ShapeVisibleMillis)
            // The blank gap is the mechanic: without it two equal shapes in a row would read as one.
            showing = false
            delay(ShapeGapMillis)
        }

        // Show the question first so it can be read, then light up the answer.
        phase = NBackDemoPhase.QUESTION
        delay(QuestionHoldMillis)
        phase = NBackDemoPhase.REVEAL
        delay(RevealHoldMillis)
        loop++
    }

    val caption = when (phase) {
        NBackDemoPhase.FLASH -> Res.string.n_back_demo_stream
        else -> Res.string.n_back_demo_recall
    }
    val captions = persistentListOf(
        Res.string.n_back_demo_stream,
        Res.string.n_back_demo_recall,
    )

    val cell = if (LocalIsCompactHeight.current) 92.dp else 124.dp
    val option = if (LocalIsCompactHeight.current) 48.dp else 60.dp

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DemoCaption(
            current = caption,
            all = captions,
            emphasis = persistentSetOf(Res.string.n_back_demo_recall),
        )
        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier.height(cell),
            contentAlignment = Alignment.Center,
        ) {
            if (phase == NBackDemoPhase.FLASH) {
                if (showing) {
                    PrismPolygon(
                        points = DemoSequence[index].paths,
                        face = Primary,
                        modifier = Modifier.size(cell),
                    )
                }
            } else {
                // Only the shapes that were shown, so the source of the options is obvious.
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DemoOptions.forEach { shape ->
                        val revealed = phase == NBackDemoPhase.REVEAL && shape == DemoAnswer
                        val face by animateColorAsState(
                            targetValue = if (revealed) SuccessGreen else Primary,
                            label = "nBackDemoOption",
                        )
                        val background by animateColorAsState(
                            targetValue = if (revealed) {
                                SuccessGreen.copy(alpha = 0.18f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                            label = "nBackDemoOptionBg",
                        )
                        Box(
                            modifier = Modifier
                                .size(option)
                                .clip(RoundedCornerShape(12.dp))
                                .background(background),
                            contentAlignment = Alignment.Center,
                        ) {
                            PrismPolygon(
                                points = shape.paths,
                                face = face,
                                modifier = Modifier.size(option).padding(option * 0.22f),
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        // Position dots while flashing so each shape's slot is visible; hidden once choosing.
        Box(modifier = Modifier.height(8.dp), contentAlignment = Alignment.Center) {
            if (phase == NBackDemoPhase.FLASH) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    DemoSequence.indices.forEach { i ->
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (i <= index) Primary else Primary.copy(alpha = 0.25f)),
                        )
                    }
                }
            }
        }
    }
}
