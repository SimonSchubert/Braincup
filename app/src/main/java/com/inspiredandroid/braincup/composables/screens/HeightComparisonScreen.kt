package com.inspiredandroid.braincup.composables.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.composables.BaseApp
import com.inspiredandroid.braincup.composables.TextButton
import com.inspiredandroid.braincup.games.ValueComparisonGame

@Composable
fun ValueComparisonScreen(
    game: ValueComparisonGame,
    answer: (String) -> Unit
) {
    BaseApp {
        game.answers.forEachIndexed { index, s ->
            Spacer(Modifier.height(16.dp))
            TextButton(text = s, onClick = {
                answer("${index + 1}")
            }, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}