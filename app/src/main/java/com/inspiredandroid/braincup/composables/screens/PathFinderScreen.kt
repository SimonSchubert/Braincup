package com.inspiredandroid.braincup.composables.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.composables.BaseApp
import com.inspiredandroid.braincup.composables.ShapeCanvas
import com.inspiredandroid.braincup.composables.ShapeCanvasButton
import com.inspiredandroid.braincup.games.PathFinderGame
import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.Shape
import com.inspiredandroid.braincup.games.tools.getFigure

@Composable
fun PathFinderScreen(
    game: PathFinderGame,
    answer: (String) -> Unit
) {
    BaseApp {
        game.directions.chunked(game.gridSize + 2).forEach { directions ->
            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                directions.forEach {
                    ShapeCanvas(
                        size = 32.dp,
                        figure = it.getFigure(),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(
                                4.dp
                            )
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
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
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(8.dp),
                        onClick = {
                            answer("${index + 1}")
                        })
                }
            }
        }
    }
}
