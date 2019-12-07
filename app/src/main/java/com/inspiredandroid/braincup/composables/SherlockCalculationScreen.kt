package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.layout.Gravity
import androidx.ui.layout.HeightSpacer
import androidx.ui.material.Button
import androidx.ui.material.MaterialTheme
import com.inspiredandroid.braincup.DelayedTask
import com.inspiredandroid.braincup.games.SherlockCalculationGame

@Composable
fun SherlockCalculationScreen(
    game: SherlockCalculationGame,
    answer: (String) -> Unit,
    next: () -> Unit
) {
    BaseApp {
        Text(
            "Goal: ${game.result}",
            style = (+MaterialTheme.typography()).h3,
            modifier = Gravity.Center
        )
        Text(
            "Numbers: ${game.getNumbersString()}",
            style = (+MaterialTheme.typography()).h5, modifier = Gravity.Center
        )
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
