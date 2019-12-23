package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.ui.layout.Gravity
import com.inspiredandroid.braincup.DelayedTask
import com.inspiredandroid.braincup.games.MentalCalculationGame

@Composable
fun MentalCalculationScreen(
    game: MentalCalculationGame,
    answer: (String) -> Unit,
    next: () -> Unit
) {
    BaseApp {
        Headline3(game.calculation, modifier = Gravity.Center)
        NumberPad(onInputChange = {
            if (game.getNumberLength() == it.length) {
                answer(it)
                DelayedTask().execute(next)
            }
        })
    }
}