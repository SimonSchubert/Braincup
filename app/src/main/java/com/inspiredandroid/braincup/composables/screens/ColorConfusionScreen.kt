package com.inspiredandroid.braincup.composables.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inspiredandroid.braincup.composables.BaseApp
import com.inspiredandroid.braincup.composables.Headline5
import com.inspiredandroid.braincup.composables.NumberRow
import com.inspiredandroid.braincup.composables.ShapeCanvas
import com.inspiredandroid.braincup.games.ColorConfusionGame
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.getName
import com.inspiredandroid.braincup.getComposeColor

@Composable
fun ColorConfusionScreen(
    game: ColorConfusionGame,
    answer: (String) -> Unit
) {
    BaseApp {
        Headline5(
            text = "${game.shapePoints} = ${game.answerShape.getName()}",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            "${game.colorPoints} = ${game.answerColor.getName()}", style = TextStyle
                (
                fontSize = 24.sp,
                color = game.stringColor.getComposeColor()
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(32.dp))
        ShapeCanvas(
            size = 96.dp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            figure = Figure(
                shape = game.displayedShape,
                color = game.displayedColor
            )
        )
        Spacer(Modifier.height(32.dp))
        val numbers = game.getPossibleAnswers()
        NumberRow(numbers) {
            answer(it)
        }
    }
}
