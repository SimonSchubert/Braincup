package com.inspiredandroid.braincup.composables.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.composables.BaseApp
import com.inspiredandroid.braincup.composables.Input
import com.inspiredandroid.braincup.composables.Subtitle1
import com.inspiredandroid.braincup.composables.TextButton
import com.inspiredandroid.braincup.games.RiddleGame

@Composable
fun RiddleScreen(
    game: RiddleGame,
    answer: (String) -> Unit
) {
    BaseApp {
        Subtitle1(
            text = game.quest,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(16.dp))
        Box(Modifier.widthIn(max = 300.dp)) {
            Input {
                if (game.isCorrect(it)) {
                    answer(it)
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        TextButton(
            text = "Give up", onClick = {
                answer("")
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}