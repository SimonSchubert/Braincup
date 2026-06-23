package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.colored_shapes_demo_title
import braincup.composeapp.generated.resources.game_colored_shapes_desc
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.Shape
import com.inspiredandroid.braincup.games.tools.composeColor
import com.inspiredandroid.braincup.ui.localizedName
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import com.inspiredandroid.braincup.ui.theme.numberFontFamily
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import com.inspiredandroid.braincup.games.tools.Color as GameColor

// A fixed example: a red triangle. The shape statement is true (it IS a triangle), the colour
// statement is false (it is red, not blue), so the score is just the shape's points.
private val DemoFigure = Figure(Shape.TRIANGLE, GameColor.RED)
private val ShapeStatement = Shape.TRIANGLE // matches the figure -> counts
private val ColorStatement = GameColor.BLUE // does not match the figure's red -> ignored
private val ColorStatementInk = GameColor.GREEN // the colour the colour-word is printed in
private const val ShapePoints = 3
private const val ColorPoints = 2
private val AnswerOptions = listOf("0", "2", "3", "5")
private const val CorrectAnswer = "3" // only the shape statement is true

private const val ShowMillis = 900L
private const val EvalGapMillis = 1100L
private const val RevealMillis = 900L
private const val SolvedHoldMillis = 1700L
private const val ResetPauseMillis = 500L

/**
 * Animated tutorial for Colored Shapes. Shows one figure and two statements, then evaluates each in
 * turn: the statement whose subject matches the figure is ticked (it counts), the other is crossed
 * out, and finally the correct point total is highlighted. Mirrors ColoredShapesGame.points (add a
 * statement's points only when it matches the displayed figure). Loops on its own, like
 * [LightsOutDemo].
 */
@Composable
fun ColoredShapesDemo(modifier: Modifier = Modifier) {
    // null = not evaluated yet; true/false = whether the statement matches the figure.
    var shapeTrue by remember { mutableStateOf<Boolean?>(null) }
    var colorTrue by remember { mutableStateOf<Boolean?>(null) }
    var revealAnswer by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            shapeTrue = null
            colorTrue = null
            revealAnswer = false
            delay(ResetPauseMillis)

            delay(ShowMillis)
            shapeTrue = true // displayed shape is a triangle -> the statement counts
            delay(EvalGapMillis)
            colorTrue = false // displayed colour is red, not blue -> the statement is ignored
            delay(EvalGapMillis)
            revealAnswer = true
            delay(RevealMillis)
            delay(SolvedHoldMillis)
        }
    }

    val figureSize = if (LocalIsCompactHeight.current) 120.dp else 160.dp

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.colored_shapes_demo_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(16.dp))

        ShapeCanvas(figure = DemoFigure, modifier = Modifier.size(figureSize))
        Spacer(Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            StatementRow(
                text = "${ShapeStatement.localizedName()} = $ShapePoints",
                textColor = MaterialTheme.colorScheme.onSurface,
                counts = shapeTrue,
            )
            StatementRow(
                text = "${ColorStatement.localizedName()} = $ColorPoints",
                textColor = ColorStatementInk.composeColor(),
                counts = colorTrue,
            )
        }
        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AnswerOptions.forEach { value ->
                val isCorrect = revealAnswer && value == CorrectAnswer
                PrismTile(
                    face = if (isCorrect) SuccessGreen else MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(56.dp)
                        .alpha(if (revealAnswer && !isCorrect) 0.4f else 1f),
                    isClickable = false,
                    onClick = {},
                ) {
                    Text(
                        text = value,
                        color = Color.White,
                        fontFamily = numberFontFamily(),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(Res.string.game_colored_shapes_desc),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}

@Composable
private fun StatementRow(text: String, textColor: Color, counts: Boolean?) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.alpha(if (counts == false) 0.4f else 1f),
        )
        Spacer(Modifier.width(10.dp))
        // A tick once a statement is judged to count, a cross once it is judged not to.
        Text(
            text = when (counts) {
                true -> "✓"
                false -> "✗"
                null -> ""
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (counts == true) SuccessGreen else MaterialTheme.colorScheme.error,
        )
    }
}
