package com.inspiredandroid.braincup.composables

import android.os.Handler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.DelayedTask
import com.inspiredandroid.braincup.games.RiddleGame

@Composable
fun RiddleScreen(
    game: RiddleGame,
    answer: (String) -> Unit,
    next: () -> Unit
) {
    BaseApp {
        Subtitle1(
            text = game.quest,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(16.dp))
        Box(Modifier.widthIn(max = 300.dp)) {
            Input {
                if (game.isCorrect(it)) {
                    Handler().post {
                        answer(it)
                    }
                    DelayedTask().execute(next)
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        TextButton(
            text = "Give up", onClick = {
                Handler().post {

                    answer("")
                }
                DelayedTask().execute(next)
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}