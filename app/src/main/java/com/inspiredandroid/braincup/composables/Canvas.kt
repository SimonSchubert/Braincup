package com.inspiredandroid.braincup.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.Shape
import com.inspiredandroid.braincup.games.tools.getPaths
import com.inspiredandroid.braincup.getComposeColor

@Composable
fun ShapeCanvas(
    size: Dp,
    figure: Figure,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(size)
    ) {
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
                    drawPath(path, color = figure.color.getComposeColor())
                }
            } else {
                drawPath(path, color = figure.color.getComposeColor())
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
        modifier = Modifier
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true)
            )
    ) {
        ShapeCanvas(
            size = size,
            figure = figure,
            modifier = modifier
        )
    }
}

@Preview
@Composable
fun PreviewShapes() {
    Column {
        Shape.values().map { shape ->
            Figure(
                shape = shape,
                color = Color.GREEN
            )
        }.forEach { figure ->
            ShapeCanvasButton(
                size = 48.dp,
                modifier = Modifier
                    .align(CenterHorizontally)
                    .padding(8.dp),
                figure = figure,
                onClick = {})
        }
    }
}