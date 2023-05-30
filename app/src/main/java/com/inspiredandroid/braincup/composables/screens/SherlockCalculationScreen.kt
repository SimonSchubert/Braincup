package com.inspiredandroid.braincup.composables.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.composables.BaseApp
import com.inspiredandroid.braincup.composables.Headline3
import com.inspiredandroid.braincup.composables.Headline5
import com.inspiredandroid.braincup.composables.NumberPad
import com.inspiredandroid.braincup.composables.TextButton
import com.inspiredandroid.braincup.games.SherlockCalculationGame

@Composable
fun SherlockCalculationScreen(
    game: SherlockCalculationGame,
    answer: (String) -> Unit
) {
    BaseApp {
        Headline3(
            text = "Goal: ${game.result}",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Headline5(
            text = "Numbers: ${game.getNumbersString()}",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        NumberPad(true, onInputChange = {
            if (game.isCorrect(it)) {
                answer(it)
            }
        })
        Spacer(Modifier.height(32.dp))
        TextButton(text = "Give up", onClick = {
            answer("")
        }, modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}
