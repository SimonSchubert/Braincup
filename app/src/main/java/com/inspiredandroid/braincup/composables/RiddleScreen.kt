package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.EditorModel
import androidx.ui.core.TextField
import androidx.ui.core.dp
import androidx.ui.foundation.shape.border.Border
import androidx.ui.foundation.shape.border.DrawBorder
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.Container
import androidx.ui.layout.Gravity
import androidx.ui.layout.HeightSpacer
import androidx.ui.layout.Size
import androidx.ui.material.Button
import com.inspiredandroid.braincup.DelayedTask
import com.inspiredandroid.braincup.games.RiddleGame

@Composable
fun RiddleScreen(
    game: RiddleGame,
    answer: (String) -> Unit,
    next: () -> Unit
) {
    BaseApp {
        Subtitle1(text = game.quest, modifier = Gravity.Center)
        HeightSpacer(32.dp)
        val state = +state { EditorModel("") }

        Container(
            modifier = Gravity.Center wraps Size(250.dp, 48.dp)
        ) {
            DrawBorder(shape = RoundedCornerShape(4.dp), border = Border(Color.Black, 1.dp))
            TextField(value = state.value, onValueChange = {
                state.value = it
                if (game.isCorrect(it.text)) {
                    answer(it.text)
                    DelayedTask().execute(next)
                }
            }, modifier = Size(234.dp, 48.dp))
        }
        HeightSpacer(height = 16.dp)

        Button(
            "Give up", onClick = {
                answer("")
                DelayedTask().execute(next)
            },
            modifier = Gravity.Center
        )
    }
}