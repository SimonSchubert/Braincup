package com.inspiredandroid.braincup.composables

import android.os.Handler
import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.layout.Spacer
import androidx.ui.layout.preferredHeight
import androidx.ui.unit.dp
import com.inspiredandroid.braincup.DelayedTask
import com.inspiredandroid.braincup.games.FractionCalculationGame

@Composable
fun FractionCalculationScreen(
    game: FractionCalculationGame,
    answer: (String) -> Unit,
    next: () -> Unit
) {
    BaseApp {
        Headline4(
            text = game.calculation,
            modifier = Modifier.gravity(align = Alignment.CenterHorizontally)
        )
        NumberPad(false, onInputChange = {
            if (game.isCorrect(it)) {
                Handler().post {

                    answer(it)
                }
                DelayedTask().execute(next)
            }
        })
        Spacer(Modifier.preferredHeight(32.dp))
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