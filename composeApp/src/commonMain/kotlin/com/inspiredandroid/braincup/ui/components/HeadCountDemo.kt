package com.inspiredandroid.braincup.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.head_count_demo_answer
import braincup.composeapp.generated.resources.head_count_demo_both
import braincup.composeapp.generated.resources.head_count_demo_in
import braincup.composeapp.generated.resources.head_count_demo_out
import braincup.composeapp.generated.resources.head_count_demo_title
import com.inspiredandroid.braincup.games.HeadCountGame
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.StringResource

private class DemoStep(val move: HeadCountGame.Move, val caption: StringResource)

private val DemoSteps = listOf(
    DemoStep(HeadCountGame.Move(entering = 2, leaving = 0, entersFromLeft = true), Res.string.head_count_demo_in),
    DemoStep(HeadCountGame.Move(entering = 0, leaving = 1, entersFromLeft = true), Res.string.head_count_demo_out),
    DemoStep(HeadCountGame.Move(entering = 2, leaving = 1, entersFromLeft = false), Res.string.head_count_demo_both),
)
private val DemoOccupants = DemoSteps.sumOf { it.move.entering - it.move.leaving }

// Slower than the real game's opening tier: a tutorial is read, not raced.
private const val WalkMillis = 1500
private const val StillMillis = 500L
private const val IntroMillis = 1200L
private const val RevealHoldMillis = 2200L

@Composable
fun HeadCountDemo(modifier: Modifier = Modifier) {
    var stepIndex by remember { mutableIntStateOf(-1) }
    var isRevealed by remember { mutableStateOf(false) }
    var loop by remember { mutableIntStateOf(0) }
    val walk = remember { Animatable(0f) }

    LaunchedEffect(loop) {
        stepIndex = -1
        isRevealed = false
        walk.snapTo(0f)
        delay(IntroMillis)
        for (i in DemoSteps.indices) {
            walk.snapTo(0f)
            stepIndex = i
            walk.animateTo(1f, tween(WalkMillis, easing = LinearEasing))
            delay(StillMillis)
        }
        isRevealed = true
        delay(RevealHoldMillis)
        loop++
    }

    val move = DemoSteps.getOrNull(stepIndex)?.move?.takeIf { !isRevealed }
    val caption = when {
        isRevealed -> Res.string.head_count_demo_answer
        stepIndex < 0 -> Res.string.head_count_demo_title
        else -> DemoSteps[stepIndex].caption
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DemoCaption(
            current = caption,
            all = persistentListOf(
                Res.string.head_count_demo_title,
                Res.string.head_count_demo_in,
                Res.string.head_count_demo_out,
                Res.string.head_count_demo_both,
                Res.string.head_count_demo_answer,
            ),
            emphasis = persistentSetOf(Res.string.head_count_demo_answer),
        )
        Spacer(Modifier.height(8.dp))
        HeadCountHouse(
            entering = move?.entering ?: 0,
            leaving = move?.leaving ?: 0,
            entersFromLeft = move?.entersFromLeft ?: true,
            walkProgress = walk.value,
            revealedOccupants = if (isRevealed) DemoOccupants else null,
            modifier = Modifier
                .widthIn(max = 320.dp)
                .fillMaxWidth(0.9f)
                .aspectRatio(HeadCountSceneAspect),
        )
    }
}
