package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.anomaly_demo_color
import braincup.composeapp.generated.resources.anomaly_demo_correct
import braincup.composeapp.generated.resources.anomaly_demo_shape
import braincup.composeapp.generated.resources.anomaly_demo_title
import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.Shape
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.SuccessGreenSoft
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

private const val DemoGridSize = 2

// Round 1 teaches the colour dimension: four identical stars sharing a shape, one in a different
// colour. Round 2 teaches the shape dimension: four figures sharing a colour, one with a different
// shape. The odd cell moves between rounds so it reads like a real puzzle.
private const val Round1OddIndex = 1
private const val Round2OddIndex = 2

private val Round1Figures: List<Figure> = List(DemoGridSize * DemoGridSize) { index ->
    if (index == Round1OddIndex) Figure(Shape.STAR, Color.YELLOW) else Figure(Shape.STAR, Color.BLUE)
}
private val Round2Figures: List<Figure> = List(DemoGridSize * DemoGridSize) { index ->
    if (index == Round2OddIndex) Figure(Shape.TRIANGLE, Color.GREEN) else Figure(Shape.SQUARE, Color.GREEN)
}

private const val ScanMillis = 900L
private const val TapPulseMillis = 450L
private const val SolvedHoldMillis = 1300L
private const val RoundRestMillis = 500L
private const val LoopEndHoldMillis = 1400L
private const val ResetPauseMillis = 400L

private enum class AnomalyTileState { NORMAL, CORRECT, DIMMED }

// Every caption the demo cycles through, so the caption line can reserve the tallest one's height.
private val DemoCaptions = listOf(
    Res.string.anomaly_demo_color,
    Res.string.anomaly_demo_shape,
    Res.string.anomaly_demo_correct,
)

/**
 * Animated tutorial board for Anomaly Puzzle: a fixed 3x3 grid of figures that all share a trait
 * except one. It plays two rounds on a loop, first finding the odd colour then the odd shape: the
 * board shows, the anomaly pulses in [Primary] as if tapped, then flips to the green "correct" state
 * while the rest dim, mirroring the real game's feedback. No interaction needed; it plays on its own
 * like [SchulteTableDemo].
 */
@Composable
fun AnomalyPuzzleDemo(modifier: Modifier = Modifier) {
    var figures by remember { mutableStateOf(Round1Figures) }
    var activeIndex by remember { mutableIntStateOf(-1) }
    var correctIndex by remember { mutableIntStateOf(-1) }
    var dimmed by remember { mutableStateOf(false) }
    var captionRes by remember { mutableStateOf(Res.string.anomaly_demo_color) }

    LaunchedEffect(Unit) {
        suspend fun playRound(roundFigures: List<Figure>, oddIndex: Int, roundCaption: StringResource) {
            figures = roundFigures
            correctIndex = -1
            activeIndex = -1
            dimmed = false
            captionRes = roundCaption
            delay(ScanMillis)

            activeIndex = oddIndex
            delay(TapPulseMillis)

            activeIndex = -1
            correctIndex = oddIndex
            dimmed = true
            captionRes = Res.string.anomaly_demo_correct
            delay(SolvedHoldMillis)
        }

        while (true) {
            figures = Round1Figures
            correctIndex = -1
            activeIndex = -1
            dimmed = false
            captionRes = Res.string.anomaly_demo_color
            delay(ResetPauseMillis)

            playRound(Round1Figures, Round1OddIndex, Res.string.anomaly_demo_color)
            delay(RoundRestMillis)
            playRound(Round2Figures, Round2OddIndex, Res.string.anomaly_demo_shape)
            delay(LoopEndHoldMillis)
        }
    }

    val cellMax = if (LocalIsCompactHeight.current) 56.dp else 72.dp

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.anomaly_demo_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier.widthIn(max = cellMax * DemoGridSize),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            for (y in 0 until DemoGridSize) {
                Row {
                    for (x in 0 until DemoGridSize) {
                        val index = y * DemoGridSize + x
                        val state = when {
                            index == correctIndex -> AnomalyTileState.CORRECT
                            dimmed -> AnomalyTileState.DIMMED
                            else -> AnomalyTileState.NORMAL
                        }
                        AnomalyFigureTile(
                            figure = figures[index],
                            state = state,
                            isActive = index == activeIndex,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(4.dp),
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
private fun AnomalyFigureTile(
    figure: Figure,
    state: AnomalyTileState,
    isActive: Boolean,
    modifier: Modifier = Modifier,
) {
    // Match the real FigureCellContent colors so the demo reads as actual gameplay, plus a Primary
    // pulse on the anomaly as it is "tapped" before it settles into the green correct state.
    val face = when {
        isActive -> Primary
        state == AnomalyTileState.CORRECT -> SuccessGreenSoft
        else -> MaterialTheme.colorScheme.surfaceContainer
    }
    val tileModifier = if (state == AnomalyTileState.DIMMED) modifier.alpha(0.3f) else modifier
    PrismTile(
        face = face,
        modifier = tileModifier,
        isClickable = false,
        isSelected = state != AnomalyTileState.NORMAL,
        onClick = {},
    ) {
        ShapeCanvas(
            figure = figure,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
        )
    }
}
