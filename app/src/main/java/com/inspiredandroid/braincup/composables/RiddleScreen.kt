package com.inspiredandroid.braincup.composables

import android.os.Handler
import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.layout.Spacer
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.preferredWidthIn
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
        Subtitle1(
            text = game.quest,
            modifier = Modifier.gravity(align = Alignment.CenterHorizontally)
        )
        Spacer(Modifier.preferredHeight(16.dp))
        Box(Modifier.preferredWidthIn(maxWidth = 300.dp)) {
            Input {
                if (game.isCorrect(it)) {
                    Handler().post {
                        answer(it)
                    }
                    DelayedTask().execute(next)
                }
            }
        }
        Spacer(Modifier.preferredHeight(16.dp))
        TextButton(
            text = "Give up", onClick = {
                Handler().post {

                    answer("")
                }
                DelayedTask().execute(next)
            },
            modifier = Modifier.gravity(align = Alignment.CenterHorizontally)
        )
    }
}