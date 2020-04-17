package com.inspiredandroid.braincup.composables

import android.os.Handler
import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.layout.Spacer
import androidx.ui.layout.preferredHeight
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
            modifier = Modifier.gravity(align = Alignment.CenterHorizontally)
        )
        Text(
            "${game.colorPoints} = ${game.answerColor.getName()}", style = TextStyle
                (
                fontSize = 24.sp,
                color = game.stringColor.getComposeColor()
            ),
            modifier = Modifier.gravity(align = Alignment.CenterHorizontally)
        )
        Spacer(Modifier.preferredHeight(32.dp))
        ShapeCanvas(
            size = 96.dp,
            modifier = Modifier.gravity(align = Alignment.CenterHorizontally),
            figure = Figure(
                shape = game.displayedShape,
                color = game.displayedColor
            )
        )
        Spacer(Modifier.preferredHeight(32.dp))
        val numbers = game.getPossibleAnswers()
        NumberRow(numbers) {
            Handler().post {
                answer(it)
            }
            DelayedTask().execute(next)
        }
    }
}
