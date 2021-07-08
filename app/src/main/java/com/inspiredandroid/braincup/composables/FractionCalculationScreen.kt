package com.inspiredandroid.braincup.composables

import android.os.Handler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        NumberPad(false, onInputChange = {
            if (game.isCorrect(it)) {
                Handler().post {

                    answer(it)
                }
                DelayedTask().execute(next)
            }
        })
        Spacer(Modifier.height(32.dp))
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