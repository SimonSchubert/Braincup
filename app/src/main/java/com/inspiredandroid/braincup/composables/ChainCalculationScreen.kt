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
import com.inspiredandroid.braincup.games.ChainCalculationGame

@Composable
fun ChainCalculationScreen(
    game: ChainCalculationGame,
    answer: (String) -> Unit,
    next: () -> Unit
) {
    BaseApp {
        Text(
            game.calculation, style = (+MaterialTheme.typography()).h4,
            modifier = Gravity.Center
        )
        NumberPad(false, onInputChange = {
            if (game.isCorrect(it)) {
                answer(it)
                DelayedTask().execute(next)
            }
        })
        HeightSpacer(32.dp)
        Button(
            "Give up", onClick = {
                answer("")
                DelayedTask().execute(next)
            },
            modifier = Gravity.Center
        )
    }
}