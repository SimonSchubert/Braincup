package com.inspiredandroid.braincup.composables

import android.os.Handler
import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.layout.Spacer
import androidx.ui.layout.preferredHeight
import androidx.ui.unit.dp
import com.inspiredandroid.braincup.DelayedTask
import com.inspiredandroid.braincup.games.SherlockCalculationGame

@Composable
fun SherlockCalculationScreen(
    game: SherlockCalculationGame,
    answer: (String) -> Unit,
    next: () -> Unit
) {
    BaseApp {
        Headline3(
            text = "Goal: ${game.result}",
            modifier = Modifier.gravity(align = Alignment.CenterHorizontally)
        )
        Headline5(
            text = "Numbers: ${game.getNumbersString()}",
            modifier = Modifier.gravity(align = Alignment.CenterHorizontally)
        )
        NumberPad(true, onInputChange = {
            if (game.isCorrect(it)) {
                Handler().post {

                    answer(it)
                }
                DelayedTask().execute(next)
            }
        })
        Spacer(Modifier.preferredHeight(32.dp))
        TextButton(text = "Give up", onClick = {
            Handler().post {

                answer("")
            }
            DelayedTask().execute(next)
        }, modifier = Modifier.gravity(align = Alignment.CenterHorizontally))
    }
}
