package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.ui.layout.LayoutGravity
import androidx.ui.layout.LayoutHeight
import androidx.ui.layout.Spacer
import androidx.ui.material.Button
import androidx.ui.unit.dp
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
            Spacer(LayoutHeight(16.dp))
            Button(text = s, onClick = {
                answer("${index + 1}")
                DelayedTask().execute(next)
            }, modifier = LayoutGravity.Center)
        }
    }
}