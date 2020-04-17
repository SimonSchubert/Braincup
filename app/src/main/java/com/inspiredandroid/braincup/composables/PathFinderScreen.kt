package com.inspiredandroid.braincup.composables

import android.os.Handler
import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.unit.dp
import com.inspiredandroid.braincup.DelayedTask
import com.inspiredandroid.braincup.games.PathFinderGame
import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.Shape
import com.inspiredandroid.braincup.games.tools.getFigure

@Composable
fun PathFinderScreen(
    game: PathFinderGame,
    answer: (String) -> Unit,
    next: () -> Unit
) {
    BaseApp {
        game.directions.chunked(game.gridSize + 2).forEach { directions ->
            Row(modifier = Modifier.gravity(align = Alignment.CenterHorizontally)) {
                directions.forEach {
                    ShapeCanvas(
                        size = 32.dp,
                        figure = it.getFigure(),
                        modifier = Modifier.gravity(align = Alignment.CenterVertically) + Modifier.padding(
                            4.dp
                        )
                    )
                }
            }
        }
        Spacer(Modifier.preferredHeight(16.dp))
        val blankFigure = Figure(Shape.SQUARE, Color.GREY_LIGHT)
        val startFigure = Figure(Shape.SQUARE, Color.ORANGE)
        repeat(game.gridSize) { y ->
            Row {
                repeat(game.gridSize) { x ->
                    val index = y * game.gridSize + x
                    val figure = if (index == game.startX) {
                        startFigure
                    } else {
                        blankFigure
                    }
                    ShapeCanvasButton(
                        size = 48.dp,
                        figure = figure,
                        modifier = Modifier.gravity(align = Alignment.CenterVertically) + Modifier.padding(
                            8.dp
                        ),
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
