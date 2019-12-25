package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.ui.core.dp
import androidx.ui.layout.Gravity
import androidx.ui.layout.HeightSpacer
import androidx.ui.layout.MaxWidth
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
        Input(modifier = Gravity.Center wraps MaxWidth(300.dp)) {
            if (game.isCorrect(it)) {
                answer(it)
                DelayedTask().execute(next)
            }
        }
        HeightSpacer(16.dp)
        Button(
            "Give up", onClick = {
                answer("")
                DelayedTask().execute(next)
            },
            modifier = Gravity.Center
        )
    }
}