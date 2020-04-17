package com.inspiredandroid.braincup.composables

import android.os.Handler
import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.layout.Spacer
import androidx.ui.layout.preferredHeight
import androidx.ui.unit.dp
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
            Spacer(Modifier.preferredHeight(16.dp))
            TextButton(text = s, onClick = {
                Handler().post {

                    answer("${index + 1}")
                }
                DelayedTask().execute(next)
            }, modifier = Modifier.gravity(align = Alignment.CenterHorizontally))
        }
    }
}