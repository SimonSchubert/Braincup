package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.Dp
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.getPaths

@Composable
fun ShapeCanvas(
    size: Dp,
    figure: Figure,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.size(size)) {
        Canvas(modifier = Modifier.size(size)) {
            val path = Path()
            figure.shape.getPaths().forEachIndexed { index, pair ->
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
                    drawPath(path, color = figure.color.toComposeColor())
                }
            } else {
                drawPath(path, color = figure.color.toComposeColor())
            }
        }
    }
}

@Composable
fun ShapeCanvasButton(
    size: Dp,
    figure: Figure,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier.clickable(
            onClick = onClick,
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        )
    ) {
        ShapeCanvas(
            size = size,
            figure = figure,
            modifier = modifier
        )
    }
}
