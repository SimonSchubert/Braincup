package com.inspiredandroid.braincup.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.game_mental_rotations_mirrored
import braincup.composeapp.generated.resources.game_mental_rotations_same
import braincup.composeapp.generated.resources.mental_rotations_demo_mirrored
import braincup.composeapp.generated.resources.mental_rotations_demo_same
import braincup.composeapp.generated.resources.mental_rotations_demo_title
import braincup.composeapp.generated.resources.mental_rotations_demo_turn
import com.inspiredandroid.braincup.games.Cube
import com.inspiredandroid.braincup.games.LatticeRotation
import com.inspiredandroid.braincup.games.mirror
import com.inspiredandroid.braincup.games.toProjection
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

private const val TurnMillis = 1600L
private const val AnswerHoldMillis = 1400L
private const val FadeMillis = 250L
private const val ResetPauseMillis = 400L

private enum class RotationDemoPhase { TURN, ANSWER }

/** A chiral staircase: its mirror is genuinely unreachable by rotation, which is the whole lesson. */
private val DemoFigure = listOf(
    Cube(0, 0, 0),
    Cube(1, 0, 0),
    Cube(2, 0, 0),
    Cube(2, 1, 0),
    Cube(2, 2, 0),
    Cube(2, 2, 1),
)

// A quarter turn, so the "same" candidate is visibly turned rather than a giveaway copy.
private val DemoTurn = LatticeRotation.all[4]

private val DemoCaptions = persistentListOf(
    Res.string.mental_rotations_demo_turn,
    Res.string.mental_rotations_demo_same,
    Res.string.mental_rotations_demo_mirrored,
)

/**
 * Animated tutorial for Mental Rotations. It loops two rounds, one of each answer: a turned copy
 * that lines up, then the mirror image that never can. Both rounds pause on the figures before the
 * matching button lights up, so the caption explains the judgement rather than just naming it.
 */
@Composable
fun MentalRotationsDemo(modifier: Modifier = Modifier) {
    var phase by remember { mutableStateOf(RotationDemoPhase.TURN) }
    var showMirrored by remember { mutableStateOf(false) }
    var figuresVisible by remember { mutableStateOf(true) }

    val figuresAlpha by animateFloatAsState(
        targetValue = if (figuresVisible) 1f else 0f,
        animationSpec = tween(FadeMillis.toInt()),
        label = "mentalRotationsDemoFigures",
    )

    LaunchedEffect(Unit) {
        while (true) {
            for (mirrored in listOf(false, true)) {
                showMirrored = mirrored
                phase = RotationDemoPhase.TURN
                figuresVisible = true
                delay(TurnMillis)

                phase = RotationDemoPhase.ANSWER
                delay(AnswerHoldMillis)

                figuresVisible = false
                delay(FadeMillis + ResetPauseMillis)
            }
        }
    }

    val compact = LocalIsCompactHeight.current
    val rowMax = if (compact) 220.dp else 280.dp
    val contentHeight = if (compact) 190.dp else 230.dp

    val candidate = remember(showMirrored) {
        val base = if (showMirrored) mirror(DemoFigure) else DemoFigure
        base.map(DemoTurn::apply).toProjection()
    }
    val reference = remember { DemoFigure.toProjection() }

    val captionRes = when {
        phase == RotationDemoPhase.TURN -> Res.string.mental_rotations_demo_turn
        showMirrored -> Res.string.mental_rotations_demo_mirrored
        else -> Res.string.mental_rotations_demo_same
    }

    DemoScaffold(
        title = Res.string.mental_rotations_demo_title,
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(contentHeight),
            contentAlignment = Alignment.Center,
        ) {
            MentalRotationsPair(
                reference = reference,
                candidate = candidate,
                modifier = Modifier
                    .widthIn(max = rowMax)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .graphicsLayer { alpha = figuresAlpha },
                spacing = 8.dp,
            )
        }
        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .widthIn(max = rowMax)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DemoAnswerTile(
                label = stringResource(Res.string.game_mental_rotations_same),
                lit = phase == RotationDemoPhase.ANSWER && !showMirrored,
                modifier = Modifier.weight(1f),
            )
            DemoAnswerTile(
                label = stringResource(Res.string.game_mental_rotations_mirrored),
                lit = phase == RotationDemoPhase.ANSWER && showMirrored,
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(16.dp))

        DemoCaption(current = captionRes, all = DemoCaptions)
    }
}

@Composable
private fun DemoAnswerTile(label: String, lit: Boolean, modifier: Modifier = Modifier) {
    PrismTile(
        face = if (lit) SuccessGreen else Primary,
        isClickable = false,
        isSelected = lit,
        onClick = {},
        modifier = modifier.height(56.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
        )
    }
}
