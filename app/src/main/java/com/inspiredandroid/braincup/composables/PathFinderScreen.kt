package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.ui.core.dp
import androidx.ui.layout.Gravity
import androidx.ui.layout.HeightSpacer
import androidx.ui.layout.Row
import androidx.ui.layout.Spacing
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
            Row(modifier = Gravity.Center) {
                directions.forEach {
                    ShapeCanvas(
                        size = 32.dp,
                        figure = it.getFigure(), modifier = Gravity.Center wraps Spacing(4.dp)
                    )
                }
            }
        }
        HeightSpacer(height = 16.dp)
        val blankFigure = Figure(Shape.SQUARE, Color.GREY_LIGHT)
        val startFigure = Figure(Shape.SQUARE, Color.ORANGE)
        repeat(game.gridSize) { y ->
            Row(modifier = Gravity.Center) {
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
                        modifier = Gravity.Center wraps Spacing(8.dp),
                        onClick = {
                            answer("${index + 1}")
                            DelayedTask().execute(next)
                        })
                }
            }
        }
    }
}
