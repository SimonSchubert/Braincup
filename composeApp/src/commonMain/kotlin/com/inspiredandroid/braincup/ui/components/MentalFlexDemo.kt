package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.game_mental_flex_desc
import braincup.composeapp.generated.resources.game_mental_flex_rule_color
import braincup.composeapp.generated.resources.game_mental_flex_rule_shape
import braincup.composeapp.generated.resources.mental_flex_demo_title
import com.inspiredandroid.braincup.app.AnswerFeedbackState
import com.inspiredandroid.braincup.app.FigureCell
import com.inspiredandroid.braincup.games.MentalFlexGame
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.GameColor
import com.inspiredandroid.braincup.games.tools.Shape
import com.inspiredandroid.braincup.ui.screens.games.FigureCellContent
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

// One fixed board, shown twice. The tiles never change; only the cue above them does, and with it
// the answer. That swap is the whole game, so the tutorial is built to show exactly it.
private val DemoTarget = Figure(Shape.STAR, GameColor.BLUE)

private val DemoCandidates = listOf(
    Figure(Shape.STAR, GameColor.RED), // matches the target's shape
    Figure(Shape.CIRCLE, GameColor.BLUE), // matches the target's color
    Figure(Shape.HEART, GameColor.GREEN), // matches neither
)

// Fixed here, unlike in the game where the pair is redrawn every round: the instructions are
// teaching what a cue IS, so the two forms have to sit still long enough to be compared.
private val DemoExemplars = mapOf(
    // One shape, two colors: the shape is what is held constant.
    MentalFlexGame.Rule.SHAPE to persistentListOf(
        Figure(Shape.HOUSE, GameColor.PURPLE),
        Figure(Shape.HOUSE, GameColor.ORANGE),
    ),
    // Two shapes, one color: the color is.
    MentalFlexGame.Rule.COLOR to persistentListOf(
        Figure(Shape.DIAMOND, GameColor.TURQUOISE),
        Figure(Shape.ARROW, GameColor.TURQUOISE),
    ),
)

private const val ShapeAnswerIndex = 0
private const val ColorAnswerIndex = 1

private const val ReadCueMillis = 1500L
private const val RevealHoldMillis = 1900L
private const val SwitchPauseMillis = 700L

/**
 * Animated tutorial for Mental Flex, and the only place the two cue marks are ever spelled out.
 *
 * The board carries a bare mark, so a player who has not been told what it means cannot start. This
 * screen is where that mapping is taught: the cue is shown [MentalFlexRuleCue] with `showLabel`,
 * beside the tile it selects, and it cycles between the two rules over one unchanging board so the
 * point lands: same three tiles, different mark, different answer.
 */
@Composable
fun MentalFlexDemo(modifier: Modifier = Modifier) {
    var rule by remember { mutableStateOf(MentalFlexGame.Rule.SHAPE) }
    var answerIndex by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        while (true) {
            rule = MentalFlexGame.Rule.SHAPE
            answerIndex = null
            delay(ReadCueMillis)
            answerIndex = ShapeAnswerIndex
            delay(RevealHoldMillis)

            // Same tiles, new mark: the answer moves without the board moving.
            rule = MentalFlexGame.Rule.COLOR
            answerIndex = null
            delay(SwitchPauseMillis)
            delay(ReadCueMillis)
            answerIndex = ColorAnswerIndex
            delay(RevealHoldMillis)
            delay(SwitchPauseMillis)
        }
    }

    val figureSize = if (LocalIsCompactHeight.current) 64.dp else 84.dp
    val tileSize = if (LocalIsCompactHeight.current) 56.dp else 72.dp

    DemoScaffold(
        title = Res.string.mental_flex_demo_title,
        modifier = modifier,
        description = Res.string.game_mental_flex_desc,
    ) {
        // Both marks stay on screen together, the live one labelled, so the pair is learned as a
        // pair rather than one at a time.
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MentalFlexGame.Rule.entries.forEach { candidateRule ->
                val isActive = candidateRule == rule
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    MentalFlexRuleCue(
                        exemplar = DemoExemplars.getValue(candidateRule),
                        label = stringResource(
                            when (candidateRule) {
                                MentalFlexGame.Rule.SHAPE -> Res.string.game_mental_flex_rule_shape
                                MentalFlexGame.Rule.COLOR -> Res.string.game_mental_flex_rule_color
                            },
                        ),
                        modifier = if (isActive) Modifier else Modifier.alpha(0.35f),
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        ShapeCanvas(figure = DemoTarget, modifier = Modifier.size(figureSize))
        Spacer(Modifier.height(16.dp))

        Row {
            DemoCandidates.forEachIndexed { index, figure ->
                val state = when (answerIndex) {
                    null -> AnswerFeedbackState.NORMAL
                    index -> AnswerFeedbackState.CORRECT
                    else -> AnswerFeedbackState.DIMMED
                }
                FigureCellContent(
                    cell = FigureCell(figure, state),
                    onClick = {},
                    modifier = Modifier.size(tileSize).aspectRatio(1f).padding(6.dp),
                )
            }
        }
    }
}
