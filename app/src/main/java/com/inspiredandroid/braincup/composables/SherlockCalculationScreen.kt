package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.ui.core.dp
import androidx.ui.layout.Gravity
import androidx.ui.layout.HeightSpacer
import androidx.ui.material.Button
import com.inspiredandroid.braincup.DelayedTask
import com.inspiredandroid.braincup.games.SherlockCalculationGame

@Composable
fun SherlockCalculationScreen(
    game: SherlockCalculationGame,
    answer: (String) -> Unit,
    next: () -> Unit
) {
    BaseApp {
        Headline3(text = "Goal: ${game.result}", modifier = Gravity.Center)
        Headline5(text = "Numbers: ${game.getNumbersString()}", modifier = Gravity.Center)
        NumberPad(true, onInputChange = {
            if (game.isCorrect(it)) {
                answer(it)
                DelayedTask().execute(next)
            }
        })
        HeightSpacer(32.dp)
        Button("Give up", onClick = {
            answer("")
            DelayedTask().execute(next)
        }, modifier = Gravity.Center)
    }
}
