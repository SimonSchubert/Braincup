package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.ui.core.dp
import androidx.ui.layout.Gravity
import androidx.ui.layout.HeightSpacer
import androidx.ui.material.Button
import com.inspiredandroid.braincup.DelayedTask
import com.inspiredandroid.braincup.games.HeightComparisonGame

@Composable
fun HeightComparisonScreen(
    game: HeightComparisonGame,
    answer: (String) -> Unit,
    next: () -> Unit
) {
    BaseApp {
        game.answers.forEachIndexed { index, s ->
            HeightSpacer(16.dp)
            Button(text = s, onClick = {
                answer("${index + 1}")
                DelayedTask().execute(next)
            }, modifier = Gravity.Center)
        }
    }
}