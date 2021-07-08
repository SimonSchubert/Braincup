package com.inspiredandroid.braincup.composables

import android.os.Handler
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.inspiredandroid.braincup.DelayedTask
import com.inspiredandroid.braincup.games.MentalCalculationGame

@Composable
fun MentalCalculationScreen(
    game: MentalCalculationGame,
    answer: (String) -> Unit,
    next: () -> Unit
) {
    BaseApp {
        Headline3(
            game.calculation,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        NumberPad(onInputChange = {
            if (game.getNumberLength() == it.length) {
                Handler().post {
                    answer(it)
                }
                DelayedTask().execute(next)
            }
        })
    }
}