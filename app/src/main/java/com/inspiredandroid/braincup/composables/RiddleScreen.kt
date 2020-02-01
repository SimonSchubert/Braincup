package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.ui.layout.*
import androidx.ui.material.Button
import androidx.ui.unit.dp
import com.inspiredandroid.braincup.DelayedTask
import com.inspiredandroid.braincup.games.RiddleGame

@Composable
fun RiddleScreen(
    game: RiddleGame,
    answer: (String) -> Unit,
    next: () -> Unit
) {
    BaseApp {
        Subtitle1(text = game.quest, modifier = LayoutGravity.Center)
        Spacer(LayoutHeight(16.dp))
        Container(LayoutWidth.Max(300.dp)) {
            Input {
                if (game.isCorrect(it)) {
                    answer(it)
                    DelayedTask().execute(next)
                }
            }
        }
        Spacer(LayoutHeight(16.dp))
        Button(
            "Give up", onClick = {
                answer("")
                DelayedTask().execute(next)
            },
            modifier = LayoutGravity.Center
        )
    }
}