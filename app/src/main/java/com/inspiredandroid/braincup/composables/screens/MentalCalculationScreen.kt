package com.inspiredandroid.braincup.composables.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.inspiredandroid.braincup.composables.BaseApp
import com.inspiredandroid.braincup.composables.Headline3
import com.inspiredandroid.braincup.composables.NumberPad
import com.inspiredandroid.braincup.games.MentalCalculationGame

@Composable
fun MentalCalculationScreen(
    game: MentalCalculationGame,
    answer: (String) -> Unit
) {
    BaseApp {
        Headline3(
            game.calculation,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        NumberPad(onInputChange = {
            if (game.getNumberLength() == it.length) {
                answer(it)
            }
        })
    }
}