package com.inspiredandroid.braincup.composables

import android.os.Handler
import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
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
            modifier = Modifier.gravity(align = Alignment.CenterHorizontally)
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