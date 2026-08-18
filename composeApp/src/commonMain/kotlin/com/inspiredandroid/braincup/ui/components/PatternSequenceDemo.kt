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
import braincup.composeapp.generated.resources.pattern_sequence_demo_rule
import braincup.composeapp.generated.resources.pattern_sequence_prompt
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.GameColor
import com.inspiredandroid.braincup.games.tools.Shape
import com.inspiredandroid.braincup.ui.theme.OnPrimaryContainer
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.PrimaryContainer
import com.inspiredandroid.braincup.ui.theme.PrismFacet
import com.inspiredandroid.braincup.ui.theme.RoundedSlot
import com.inspiredandroid.braincup.ui.theme.SuccessGreenSoft
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

// Distribute-three on shape, one colour throughout: every row holds the same three shapes in a
// shifted order, so the missing corner is the one shape its row has not used yet. Mirrors the
// DISTRIBUTE_THREE rule in MatrixGenerator.
private val DemoColor = GameColor.BLUE
private val DemoShapes = persistentListOf(Shape.CIRCLE, Shape.TRIANGLE, Shape.STAR)
private val Matrix = List(9) { index ->
    Figure(DemoShapes[(index / 3 + index % 3) % 3], DemoColor)
}
private const val AnswerIndex = 8
private val Options = persistentListOf(
    Figure(Shape.CIRCLE, DemoColor),
    Figure(Shape.TRIANGLE, DemoColor), // the answer
    Figure(Shape.STAR, DemoColor),
    Figure(Shape.HEART, DemoColor),
)
private const val CorrectOption = 1

private const val ShowMillis = 700L
private const val RowHighlightMillis = 900L
private const val RowGapMillis = 200L
private const val RevealMillis = 700L
private const val SolvedHoldMillis = 1800L
private const val ResetPauseMillis = 500L

// Every caption the demo cycles through, so the caption line can reserve the tallest one's height.
private val DemoCaptions = persistentListOf(
    Res.string.game_pattern_sequence_desc,
    Res.string.pattern_sequence_demo_rule,
)

/**
 * Animated tutorial for Pattern Sequence. Each row of the matrix lights up in turn to show that
 * the same three shapes recur in a shifted order, then the answer fills the empty corner and the
 * matching option turns green. Loops on its own, like [LightsOutDemo].
 */
@Composable
fun PatternSequenceDemo(modifier: Modifier = Modifier) {
    var highlightedRow by remember { mutableIntStateOf(-1) }
    var solved by remember { mutableStateOf(false) }
    var captionRes by remember { mutableStateOf(Res.string.game_pattern_sequence_desc) }

    LaunchedEffect(Unit) {
        while (true) {
            highlightedRow = -1
            solved = false
            captionRes = Res.string.game_pattern_sequence_desc
            delay(ResetPauseMillis)
            delay(ShowMillis)

            captionRes = Res.string.pattern_sequence_demo_rule
            for (row in 0 until 3) {
                highlightedRow = row
                delay(RowHighlightMillis)
                highlightedRow = -1
                delay(RowGapMillis)
            }

            captionRes = Res.string.game_pattern_sequence_desc
            solved = true
            delay(RevealMillis)
            delay(SolvedHoldMillis)
        }
    }

    val cell = if (LocalIsCompactHeight.current) 34.dp else 42.dp

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.pattern_sequence_prompt),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            for (row in 0 until 3) {
                DemoMatrixRow(row = row, highlighted = row == highlightedRow, solved = solved, cell = cell)
            }
        }
        Spacer(Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Options.forEachIndexed { index, figure ->
                DemoMatrixOption(
                    figure = figure,
                    isCorrect = solved && index == CorrectOption,
                    size = cell + 10.dp,
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        DemoCaption(current = captionRes, all = DemoCaptions)
    }
}

@Composable
private fun DemoMatrixRow(row: Int, highlighted: Boolean, solved: Boolean, cell: Dp) {
    // A soft rounded frame fades in behind the row to mark the three shapes it recycles.
    val highlightAlpha by animateFloatAsState(
        targetValue = if (highlighted) 1f else 0f,
        animationSpec = tween(260),
        label = "rowHighlight",
    )
    Row(
        modifier = Modifier
            .clip(RoundedSlot)
            .background(Primary.copy(alpha = 0.16f * highlightAlpha))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        for (column in 0 until 3) {
            val index = row * 3 + column
            if (index == AnswerIndex && !solved) {
                PrismCard(
                    face = PrimaryContainer,
                    facet = PrismFacet.Cell,
                    modifier = Modifier.size(cell),
                ) {
                    Text(
                        text = "?",
                        style = MaterialTheme.typography.titleMedium,
                        color = OnPrimaryContainer,
                    )
                }
            } else {
                PrismCard(
                    face = if (index == AnswerIndex) SuccessGreenSoft else MaterialTheme.colorScheme.surfaceContainer,
                    facet = PrismFacet.Cell,
                    modifier = Modifier.size(cell),
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        ShapeCanvas(figure = Matrix[index], modifier = Modifier.fillMaxSize().padding(6.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun DemoMatrixOption(figure: Figure, isCorrect: Boolean, size: Dp) {
    PrismTile(
        face = if (isCorrect) SuccessGreenSoft else MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.size(size),
        isClickable = false,
        onClick = {},
    ) {
        ShapeCanvas(figure = figure, modifier = Modifier.fillMaxSize().padding(7.dp))
    }
}
