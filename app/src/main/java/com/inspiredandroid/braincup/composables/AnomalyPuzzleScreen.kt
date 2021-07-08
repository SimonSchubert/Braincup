package com.inspiredandroid.braincup.composables

import android.os.Handler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.DelayedTask
import com.inspiredandroid.braincup.games.AnomalyPuzzleGame

@Composable
fun AnomalyPuzzleScreen(
    game: AnomalyPuzzleGame,
    answer: (String) -> Unit,
    next: () -> Unit
) {
    BaseApp {
        val chunkSize = when {
            game.figures.size >= 16 -> 4
            game.figures.size >= 9 -> 3
            else -> 2
        }
        game.figures.chunked(chunkSize).forEachIndexed { y, figures ->
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = CenterVertically
            ) {
                figures.forEachIndexed { x, figure ->
                    val index = y * chunkSize + x
                    ShapeCanvasButton(
                        size = 48.dp,
                        modifier = Modifier
                            .padding(8.dp),
                        figure = figure,
                        onClick = {
                            Handler().post {
                                answer("${index + 1}")
                            }
                            DelayedTask().execute(next)
                        })
                }
            }
        }
    }
}
