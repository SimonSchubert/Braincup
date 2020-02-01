package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.ui.core.Text
import androidx.ui.layout.LayoutGravity
import androidx.ui.layout.LayoutHeight
import androidx.ui.layout.Spacer
import androidx.ui.text.TextStyle
import androidx.ui.unit.dp
import androidx.ui.unit.sp
import com.inspiredandroid.braincup.DelayedTask
import com.inspiredandroid.braincup.games.ColorConfusionGame
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.getName
import com.inspiredandroid.braincup.getComposeColor

@Composable
fun ColorConfusionScreen(
    game: ColorConfusionGame,
    answer: (String) -> Unit,
    next: () -> Unit
) {
    BaseApp {
        Headline5(
            text = "${game.shapePoints} = ${game.answerShape.getName()}",
            modifier = LayoutGravity.Center
        )
        Text(
            "${game.colorPoints} = ${game.answerColor.getName()}", style = TextStyle
                (
                fontSize = 24.sp,
                color = game.stringColor.getComposeColor()
            ),
            modifier = LayoutGravity.Center
        )
        Spacer(LayoutHeight(32.dp))
        ShapeCanvas(
            size = 96.dp,
            modifier = LayoutGravity.Center,
            figure = Figure(
                shape = game.displayedShape,
                color = game.displayedColor
            )
        )
        Spacer(LayoutHeight(32.dp))
        val numbers = game.getPossibleAnswers()
        NumberRow(numbers) {
            answer(it)
            DelayedTask().execute(next)
        }
    }
}
