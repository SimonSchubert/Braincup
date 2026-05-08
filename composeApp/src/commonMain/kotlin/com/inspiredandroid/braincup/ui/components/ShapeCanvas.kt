package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.withTransform
import com.inspiredandroid.braincup.games.tools.Figure

@Composable
fun ShapeCanvas(
    figure: Figure,
    modifier: Modifier = Modifier,
) {
    val path = remember { Path() }
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            path.reset()
            figure.shape.paths.forEachIndexed { index, pair ->
                val x = this.size.width * pair.first
                val y = this.size.height * pair.second
                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }
            path.close()

            if (figure.rotation != 0) {
                withTransform({
                    rotate(figure.rotation.toFloat())
                }) {
                    drawPath(path, color = figure.color.composeColor)
                }
            } else {
                drawPath(path, color = figure.color.composeColor)
            }
        }
    }
}
