package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.ui.layout.LayoutGravity
import androidx.ui.layout.LayoutHeight
import androidx.ui.layout.Spacer
import androidx.ui.material.Button
import androidx.ui.unit.dp
import com.inspiredandroid.braincup.DelayedTask
import com.inspiredandroid.braincup.games.ChainCalculationGame

@Composable
fun ChainCalculationScreen(
    game: ChainCalculationGame,
    answer: (String) -> Unit,
    next: () -> Unit
) {
    BaseApp {
        Headline4(text = game.calculation, modifier = LayoutGravity.Center)
        NumberPad(false, onInputChange = {
            if (game.isCorrect(it)) {
                answer(it)
                DelayedTask().execute(next)
            }
        })
        Spacer(LayoutHeight(32.dp))
        Button(
            "Give up", onClick = {
                answer("")
                DelayedTask().execute(next)
            },
            modifier = LayoutGravity.Center
        )
    }
}