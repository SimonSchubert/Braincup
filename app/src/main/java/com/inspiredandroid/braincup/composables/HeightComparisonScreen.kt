package com.inspiredandroid.braincup.composables

import android.os.Handler
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.DelayedTask
import com.inspiredandroid.braincup.games.ValueComparisonGame

@Composable
fun ValueComparisonScreen(
    game: ValueComparisonGame,
    answer: (String) -> Unit,
    next: () -> Unit
) {
    BaseApp {
        game.answers.forEachIndexed { index, s ->
            Spacer(Modifier.height(16.dp))
            TextButton(text = s, onClick = {
                Handler().post {

                    answer("${index + 1}")
                }
                DelayedTask().execute(next)
            }, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}