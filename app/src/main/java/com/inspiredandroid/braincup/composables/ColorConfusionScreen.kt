package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.core.sp
import androidx.ui.layout.Gravity
import androidx.ui.layout.HeightSpacer
import androidx.ui.text.TextStyle
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
            modifier = Gravity.Center
        )
        Text(
            "${game.colorPoints} = ${game.answerColor.getName()}", style = TextStyle
                (
                fontSize = 24.sp,
                color = game.stringColor.getComposeColor()
            ),
            modifier = Gravity.Center
        )
        HeightSpacer(32.dp)
        ShapeCanvas(
            size = 96.dp,
            modifier = Gravity.Center,
            figure = Figure(
                shape = game.displayedShape,
                color = game.displayedColor
            )
        )
        HeightSpacer(32.dp)
        val numbers = game.getPossibleAnswers()
        NumberRow(numbers) {
            answer(it)
            DelayedTask().execute(next)
        }
    }
}
