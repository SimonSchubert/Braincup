package com.inspiredandroid.braincup.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.n_back_demo_match
import braincup.composeapp.generated.resources.n_back_demo_watch
import braincup.composeapp.generated.resources.n_back_match
import com.inspiredandroid.braincup.games.tools.Shape
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.PrismSlot
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

/**
 * A 2-back stream. Item 4 repeats item 2, so it is the match; item 5 repeats item 4, one step back
 * rather than two, so it is the lure the tutorial deliberately walks past without tapping.
 */
private val DemoSequence = listOf(
    Shape.TRIANGLE,
    Shape.CIRCLE,
    Shape.STAR,
    Shape.CIRCLE,
    Shape.CIRCLE,
    Shape.HEART,
)
private const val DemoN = 2

// Slow, and lingering on the match: a tutorial is read, not raced.
private const val ShapeVisibleMillis = 900L
private const val ShapeGapMillis = 450L
private const val IntroMillis = 700L
private const val LoopHoldMillis = 1400L

@Composable
fun NBackDemo(modifier: Modifier = Modifier) {
    var index by remember { mutableIntStateOf(-1) }
    var showing by remember { mutableStateOf(false) }
    var matched by remember { mutableStateOf(false) }
    var loop by remember { mutableIntStateOf(0) }

    LaunchedEffect(loop) {
        index = -1
        showing = false
        matched = false
        delay(IntroMillis)

        for (i in DemoSequence.indices) {
            index = i
            showing = true
            // The tap lands while the item is on screen, the way a player would answer it.
            matched = i >= DemoN && DemoSequence[i] == DemoSequence[i - DemoN]
            delay(ShapeVisibleMillis)
            // The blank gap is the mechanic: without it two equal shapes in a row would read as one.
            showing = false
            matched = false
            delay(ShapeGapMillis)
        }

        delay(LoopHoldMillis)
        loop++
    }

    val caption = if (index >= DemoN) Res.string.n_back_demo_match else Res.string.n_back_demo_watch
    val captions = persistentListOf(
        Res.string.n_back_demo_watch,
        Res.string.n_back_demo_match,
    )

    val cell = if (LocalIsCompactHeight.current) 84.dp else 112.dp

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DemoCaption(
            current = caption,
            all = captions,
            emphasis = persistentSetOf(Res.string.n_back_demo_match),
        )
        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier.height(cell),
            contentAlignment = Alignment.Center,
        ) {
            if (showing && index >= 0) {
                PrismPolygon(
                    points = DemoSequence[index].paths,
                    face = if (matched) SuccessGreen else Primary,
                    modifier = Modifier.size(cell),
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        DemoMatchButton(lit = matched)
    }
}

/** The Match button as the arena draws it, lighting only on the item that is really 2 back. */
@Composable
private fun DemoMatchButton(lit: Boolean) {
    val face by animateColorAsState(
        targetValue = if (lit) SuccessGreen else MaterialTheme.colorScheme.surfaceVariant,
        label = "nBackDemoMatchFace",
    )
    val label by animateColorAsState(
        targetValue = if (lit) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "nBackDemoMatchLabel",
    )
    Box(
        modifier = Modifier
            .clip(PrismSlot)
            .background(face)
            .padding(horizontal = 24.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(Res.string.n_back_match),
            color = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}
