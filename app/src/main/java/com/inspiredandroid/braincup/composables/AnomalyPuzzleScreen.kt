package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.ui.core.dp
import androidx.ui.layout.Column
import androidx.ui.layout.Gravity
import androidx.ui.layout.Row
import androidx.ui.layout.Spacing
import androidx.ui.tooling.preview.Preview
import com.inspiredandroid.braincup.DelayedTask
import com.inspiredandroid.braincup.games.AnomalyPuzzleGame
import com.inspiredandroid.braincup.getComposeColor

@Composable
fun AnomalyPuzzleScreen(
    game: AnomalyPuzzleGame,
    answer: (String) -> Unit,
    next: () -> Unit
) {
    BaseApp {
        Column {
            var count = 0
            val chunkSize = when {
                game.figures.size >= 16 -> 4
                game.figures.size >= 9 -> 3
                else -> 2
            }
            game.figures.chunked(chunkSize).forEach {
                Row {
                    it.forEach {
                        val index = count
                        ShapeCanvasButton(
                            size = 48.dp,
                            modifier = Gravity.Center wraps Spacing(8.dp),
                            shape = it.shape,
                            color = it.color.getComposeColor(),
                            rotation = it.rotation,
                            onClick = {
                                answer("${index + 1}")
                                DelayedTask().execute(next)
                            })
                        count++
                    }
                }
            }
        }
    }
}
