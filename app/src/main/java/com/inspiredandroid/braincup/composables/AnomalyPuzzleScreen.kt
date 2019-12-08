package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.ui.core.Draw
import androidx.ui.core.dp
import androidx.ui.foundation.Clickable
import androidx.ui.graphics.Paint
import androidx.ui.graphics.Path
import androidx.ui.layout.*
import com.inspiredandroid.braincup.DelayedTask
import com.inspiredandroid.braincup.games.AnomalyPuzzleGame
import com.inspiredandroid.braincup.games.tools.getPaths
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
            val chunkSize = if (game.figures.size >= 9) {
                3
            } else {
                2
            }
            game.figures.chunked(chunkSize).forEach {
                Row {
                    it.forEach {
                        val index = count
                        Clickable(onClick = {
                            answer("${index + 1}")
                            DelayedTask().execute(next)
                        }) {
                            Container(
                                width = 48.dp, height = 48.dp,
                                modifier = Gravity.Center wraps Spacing(8.dp)
                            ) {
                                Draw { canvas, parentSize ->
                                    val paint = Paint()
                                    paint.color = it.color.getComposeColor()
                                    paint.isAntiAlias = true
                                    val path = Path()
                                    it.shape.getPaths().forEachIndexed { index, pair ->
                                        val x = parentSize.width.value * pair.first
                                        val y = parentSize.height.value * pair.second
                                        if (index == 0) {
                                            path.moveTo(x, y)
                                        } else {
                                            path.lineTo(x, y)
                                        }
                                    }
                                    path.close()

                                    if(it.rotation != 0) {
                                        canvas.translate(
                                            parentSize.width.value / 2f,
                                            parentSize.height.value / 2f
                                        )
                                        canvas.rotate(it.rotation.toFloat())
                                        canvas.translate(
                                            -parentSize.width.value / 2f,
                                            -parentSize.height.value / 2f
                                        )
                                    }
                                    canvas.drawPath(path, paint)
                                }
                            }
                        }
                        count++
                    }
                }
            }
        }
    }
}
