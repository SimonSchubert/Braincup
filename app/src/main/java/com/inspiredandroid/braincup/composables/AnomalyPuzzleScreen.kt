package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.ui.core.dp
import androidx.ui.layout.Gravity
import androidx.ui.layout.Row
import androidx.ui.layout.Spacing
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
            Row {
                figures.forEachIndexed { x, figure ->
                    val index = y * chunkSize + x
                    ShapeCanvasButton(
                        size = 48.dp,
                        modifier = Gravity.Center wraps Spacing(8.dp),
                        figure = figure,
                        onClick = {
                            answer("${index + 1}")
                            DelayedTask().execute(next)
                        })
                }
            }
        }
    }
}
