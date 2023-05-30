package com.inspiredandroid.braincup.composables.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.composables.BaseApp
import com.inspiredandroid.braincup.composables.ShapeCanvasButton
import com.inspiredandroid.braincup.games.AnomalyPuzzleGame
import kotlinx.coroutines.launch

@Composable
fun AnomalyPuzzleScreen(
    game: AnomalyPuzzleGame,
    answer: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
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
                            scope.launch {
                                answer("${index + 1}")
                            }
                        })
                }
            }
        }
    }
}
