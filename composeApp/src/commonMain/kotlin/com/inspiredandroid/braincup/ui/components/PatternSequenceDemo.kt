package com.inspiredandroid.braincup.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.game_pattern_sequence_desc
import braincup.composeapp.generated.resources.game_what_comes_next
import braincup.composeapp.generated.resources.pattern_sequence_demo_unit
import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.Shape
import com.inspiredandroid.braincup.ui.theme.OnPrimaryContainer
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.PrimaryContainer
import com.inspiredandroid.braincup.ui.theme.SuccessGreenSoft
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

// A simple shape cycle in one colour: circle, triangle, circle, triangle, … so the repeating unit is
// "circle, triangle" and the next figure is a circle. Mirrors PatternSequenceGame's SHAPE_CYCLE.
private val DemoColor = Color.BLUE
private const val CycleLength = 2
private val Sequence = listOf(
    Figure(Shape.CIRCLE, DemoColor),
    Figure(Shape.TRIANGLE, DemoColor),
    Figure(Shape.CIRCLE, DemoColor),
    Figure(Shape.TRIANGLE, DemoColor),
)
private val Answer = Figure(Shape.CIRCLE, DemoColor)
private val Options = listOf(
    Figure(Shape.TRIANGLE, DemoColor),
    Figure(Shape.CIRCLE, DemoColor), // the answer
    Figure(Shape.HEART, DemoColor),
    Figure(Shape.STAR, DemoColor),
)
private const val CorrectOption = 1

private const val ShowMillis = 700L
private const val UnitHighlightMillis = 950L
private const val UnitGapMillis = 250L
private const val RevealMillis = 700L
private const val SolvedHoldMillis = 1600L
private const val ResetPauseMillis = 500L

// Every caption the demo cycles through, so the caption line can reserve the tallest one's height.
private val DemoCaptions = listOf(
    Res.string.game_pattern_sequence_desc,
    Res.string.pattern_sequence_demo_unit,
)

/**
 * Animated tutorial for Pattern Sequence. The sequence is drawn in cycle-sized groups; the demo
 * highlights each repeating "circle, triangle" unit in turn to make the pattern obvious, then the
 * matching option lights up green and fills the blank. Mirrors PatternSequenceGame (find the
 * repeating unit and pick the next figure). Loops on its own, like [LightsOutDemo].
 */
@Composable
fun PatternSequenceDemo(modifier: Modifier = Modifier) {
    // Which repeating unit (group of [CycleLength]) is currently framed, or -1 for none.
    var highlightedUnit by remember { mutableIntStateOf(-1) }
    var solved by remember { mutableStateOf(false) }
    var captionRes by remember { mutableStateOf(Res.string.game_pattern_sequence_desc) }

    LaunchedEffect(Unit) {
        while (true) {
            highlightedUnit = -1
            solved = false
            captionRes = Res.string.game_pattern_sequence_desc
            delay(ResetPauseMillis)
            delay(ShowMillis)

            // Frame each repeating unit to spell out the pattern.
            captionRes = Res.string.pattern_sequence_demo_unit
            for (unit in 0 until Sequence.size / CycleLength) {
                highlightedUnit = unit
                delay(UnitHighlightMillis)
                highlightedUnit = -1
                delay(UnitGapMillis)
            }

            // Reveal: the next figure continues the cycle (another circle).
            captionRes = Res.string.game_pattern_sequence_desc
            solved = true
            delay(RevealMillis)
            delay(SolvedHoldMillis)
        }
    }

    val cell = if (LocalIsCompactHeight.current) 38.dp else 44.dp

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.game_what_comes_next),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(16.dp))

        // Wider gaps between cycle groups so the repeating unit reads even without the highlight.
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            for (unit in 0 until Sequence.size / CycleLength) {
                CycleGroup(
                    figures = Sequence.subList(unit * CycleLength, unit * CycleLength + CycleLength),
                    highlighted = unit == highlightedUnit,
                    cell = cell,
                )
            }
            // The "?" card that fills with the answer once it is revealed.
            PrismCard(face = if (solved) SuccessGreenSoft else PrimaryContainer, modifier = Modifier.size(cell)) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    if (solved) {
                        ShapeCanvas(figure = Answer, modifier = Modifier.fillMaxSize().padding(6.dp))
                    } else {
                        Text(
                            text = "?",
                            style = MaterialTheme.typography.headlineMedium,
                            color = OnPrimaryContainer,
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))

        // 2x2 options; the correct one turns green when revealed.
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            for (rowIndex in 0 until 2) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (colIndex in 0 until 2) {
                        val index = rowIndex * 2 + colIndex
                        DemoOptionCell(
                            figure = Options[index],
                            isCorrect = solved && index == CorrectOption,
                            size = cell + 16.dp,
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        DemoCaption(current = captionRes, all = DemoCaptions)
    }
}

@Composable
private fun CycleGroup(figures: List<Figure>, highlighted: Boolean, cell: Dp) {
    // A soft rounded frame fades in behind the unit's figures to mark the repeating group.
    val highlightAlpha by animateFloatAsState(
        targetValue = if (highlighted) 1f else 0f,
        animationSpec = tween(260),
        label = "unitHighlight",
    )
    val frame = Primary.copy(alpha = 0.16f * highlightAlpha)
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(frame)
            .padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        figures.forEach { figure ->
            ShapeCanvas(figure = figure, modifier = Modifier.size(cell))
        }
    }
}

@Composable
private fun DemoOptionCell(figure: Figure, isCorrect: Boolean, size: Dp) {
    PrismTile(
        face = if (isCorrect) SuccessGreenSoft else MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.size(size),
        isClickable = false,
        onClick = {},
    ) {
        ShapeCanvas(figure = figure, modifier = Modifier.fillMaxSize().padding(8.dp))
    }
}
