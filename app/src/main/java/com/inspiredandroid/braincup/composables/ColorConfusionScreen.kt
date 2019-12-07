package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.core.Draw
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.core.sp
import androidx.ui.graphics.Paint
import androidx.ui.graphics.Path
import androidx.ui.layout.Container
import androidx.ui.layout.Gravity
import androidx.ui.layout.HeightSpacer
import androidx.ui.material.MaterialTheme
import androidx.ui.text.TextStyle
import com.inspiredandroid.braincup.DelayedTask
import com.inspiredandroid.braincup.games.ColorConfusionGame
import com.inspiredandroid.braincup.games.tools.getName
import com.inspiredandroid.braincup.games.tools.getPaths
import com.inspiredandroid.braincup.getComposeColor

@Composable
fun ColorConfusionScreen(
    game: ColorConfusionGame,
    answer: (String) -> Unit,
    next: () -> Unit
) {
    BaseApp {
        Text(
            "${game.shapePoints} = ${game.answerShape.getName()}", style =
            (+MaterialTheme.typography()).h5,
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
        Container(
            width = 96.dp, height = 96.dp,
            modifier = Gravity.Center
        ) {
            Draw { canvas, parentSize ->
                val paint = Paint()
                paint.color = game.displayedColor.getComposeColor()
                paint.isAntiAlias = true
                val path = Path()
                game.displayedShape.getPaths().forEachIndexed { index, pair ->
                    val x = parentSize.width.value * pair.first
                    val y = parentSize.width.value * pair.second
                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                }
                path.close()
                canvas.drawPath(path, paint)
            }
        }
        HeightSpacer(32.dp)
        val numbers = listOf(
            0,
            game.shapePoints,
            game.colorPoints,
            game.shapePoints + game.colorPoints
        ).sorted().map { it.toString() }
        NumberRow(numbers) {
            answer(it)
            DelayedTask().execute(next)
        }
    }
}
