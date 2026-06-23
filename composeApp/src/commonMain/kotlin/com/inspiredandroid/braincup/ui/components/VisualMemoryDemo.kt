package com.inspiredandroid.braincup.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.game_visual_memory_desc
import braincup.composeapp.generated.resources.visual_memory_demo_title
import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.Shape
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

private const val DemoGridSize = 3

// Three figures placed on a 3x3 grid (by cell index), recalled in a scrambled order. Mirrors
// VisualMemoryGame: memorize the placements, then identify each hidden figure.
private val Placements = mapOf(
    1 to Figure(Shape.STAR, Color.YELLOW),
    3 to Figure(Shape.HEART, Color.RED),
    8 to Figure(Shape.TRIANGLE, Color.BLUE),
)
private val RecallOrder = listOf(3, 1, 8)
private val AnswerFigures = listOf(
    Figure(Shape.HEART, Color.RED),
    Figure(Shape.TRIANGLE, Color.BLUE),
    Figure(Shape.STAR, Color.YELLOW),
)

private const val MemorizeMillis = 2200L
private const val HidePauseMillis = 600L
private const val TargetMillis = 800L
private const val PickMillis = 420L
private const val RevealMillis = 600L
private const val SolvedHoldMillis = 1500L
private const val ResetPauseMillis = 500L
private const val TransitionMillis = 260

private enum class CellPhase { EMPTY, MEMORIZING, HIDDEN, TARGET, REVEALED }

/**
 * Animated tutorial for Visual Memory. Three figures are shown on a 3x3 grid to memorize, then all
 * hidden; one cell at a time lights up with a "?", and the matching figure is picked from the row of
 * options and revealed back in place. Mirrors VisualMemoryGame's memorize→answer flow. Loops on its
 * own, like [LightsOutDemo].
 */
@Composable
fun VisualMemoryDemo(modifier: Modifier = Modifier) {
    var memorizing by remember { mutableStateOf(true) }
    var targetCell by remember { mutableIntStateOf(-1) }
    var revealedCells by remember { mutableStateOf(emptySet<Int>()) }
    // The figure index in [AnswerFigures] currently being "tapped", and those already used.
    var pickedOption by remember { mutableIntStateOf(-1) }
    var usedOptions by remember { mutableStateOf(emptySet<Int>()) }

    LaunchedEffect(Unit) {
        while (true) {
            memorizing = true
            targetCell = -1
            revealedCells = emptySet()
            pickedOption = -1
            usedOptions = emptySet()
            delay(ResetPauseMillis)

            delay(MemorizeMillis)
            memorizing = false // hide every figure
            delay(HidePauseMillis)

            for (cell in RecallOrder) {
                targetCell = cell
                delay(TargetMillis)
                val optionIndex = AnswerFigures.indexOf(Placements.getValue(cell))
                pickedOption = optionIndex
                delay(PickMillis)
                revealedCells = revealedCells + cell
                usedOptions = usedOptions + optionIndex
                targetCell = -1
                pickedOption = -1
                delay(RevealMillis)
            }
            delay(SolvedHoldMillis)
        }
    }

    val cell = if (LocalIsCompactHeight.current) 48.dp else 60.dp

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.visual_memory_demo_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            for (row in 0 until DemoGridSize) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (col in 0 until DemoGridSize) {
                        val index = row * DemoGridSize + col
                        val figure = Placements[index]
                        val phase = when {
                            figure == null -> CellPhase.EMPTY
                            memorizing -> CellPhase.MEMORIZING
                            index == targetCell -> CellPhase.TARGET
                            index in revealedCells -> CellPhase.REVEALED
                            else -> CellPhase.HIDDEN
                        }
                        DemoMemoryCell(figure = figure, phase = phase, size = cell)
                    }
                }
            }
        }
        Spacer(Modifier.height(20.dp))

        // The answer row: pick the figure that belongs in the highlighted cell.
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AnswerFigures.forEachIndexed { index, figure ->
                DemoMemoryOption(
                    figure = figure,
                    isPicked = index == pickedOption,
                    isUsed = index in usedOptions,
                    size = cell,
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(Res.string.game_visual_memory_desc),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}

@Composable
private fun DemoMemoryCell(figure: Figure?, phase: CellPhase, size: Dp) {
    val targetColor = when (phase) {
        CellPhase.TARGET -> MaterialTheme.colorScheme.primaryContainer
        CellPhase.EMPTY, CellPhase.HIDDEN -> MaterialTheme.colorScheme.surfaceVariant
        CellPhase.MEMORIZING, CellPhase.REVEALED -> MaterialTheme.colorScheme.surfaceContainer
    }
    val face by animateColorAsState(targetColor, tween(TransitionMillis), label = "memoryCell")
    val showShape = figure != null && (phase == CellPhase.MEMORIZING || phase == CellPhase.REVEALED)
    val shapeAlpha by animateFloatAsState(
        targetValue = if (showShape) 1f else 0f,
        animationSpec = tween(TransitionMillis),
        label = "memoryShape",
    )
    val isPocket = phase == CellPhase.HIDDEN || phase == CellPhase.EMPTY
    PrismTile(
        face = face,
        modifier = Modifier.size(size),
        isClickable = false,
        isSelected = isPocket,
        onClick = {},
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            if (phase == CellPhase.TARGET) {
                Text(
                    text = "?",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            if (figure != null && shapeAlpha > 0f) {
                ShapeCanvas(
                    figure = figure,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .graphicsLayer { alpha = shapeAlpha },
                )
            }
        }
    }
}

@Composable
private fun DemoMemoryOption(figure: Figure, isPicked: Boolean, isUsed: Boolean, size: Dp) {
    PrismTile(
        face = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier
            .size(size)
            .alpha(if (isUsed) 0.3f else 1f),
        isClickable = false,
        isSelected = isPicked,
        onClick = {},
    ) {
        ShapeCanvas(
            figure = figure,
            modifier = Modifier.fillMaxSize().padding(8.dp),
        )
    }
}
