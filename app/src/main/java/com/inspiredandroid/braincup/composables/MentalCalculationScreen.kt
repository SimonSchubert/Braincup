package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.layout.Gravity
import androidx.ui.material.MaterialTheme
import com.inspiredandroid.braincup.DelayedTask
import com.inspiredandroid.braincup.games.MentalCalculationGame

@Composable
fun MentalCalculationScreen(
    game: MentalCalculationGame,
    answer: (String) -> Unit,
    next: () -> Unit
) {
    BaseApp {
        Text(
            game.calculation, style = (+MaterialTheme.typography()).h3,
            modifier = Gravity.Center
        )
        NumberPad(onInputChange = {
            if (game.getNumberLength() == it.length) {
                answer(it)
                DelayedTask().execute(next)
            }
        })
    }
}