package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.ui.core.Dp
import androidx.ui.core.Draw
import androidx.ui.core.Modifier
import androidx.ui.core.dp
import androidx.ui.foundation.Clickable
import androidx.ui.graphics.Paint
import androidx.ui.graphics.Path
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.layout.Gravity
import androidx.ui.layout.Spacing
import androidx.ui.material.ripple.Ripple
import androidx.ui.tooling.preview.Preview
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.Shape
import com.inspiredandroid.braincup.games.tools.getPaths
import com.inspiredandroid.braincup.getComposeColor

@Composable
fun ShapeCanvas(
    size: Dp,
    figure: Figure,
    modifier: Modifier = Modifier.None
) {
    Container(
        width = size, height = size,
        modifier = modifier
    ) {
        Draw { canvas, parentSize ->
            val paint = Paint()
            paint.color = figure.color.getComposeColor()
            paint.isAntiAlias = true
            val path = Path()
            figure.shape.getPaths().forEachIndexed { index, pair ->
                val x = parentSize.width.value * pair.first
                val y = parentSize.height.value * pair.second
                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }
            path.close()

            if (figure.rotation != 0) {
                canvas.translate(
                    parentSize.width.value / 2f,
                    parentSize.height.value / 2f
                )
                canvas.rotate(figure.rotation.toFloat())
                canvas.translate(
                    -parentSize.width.value / 2f,
                    -parentSize.height.value / 2f
                )
            }
            canvas.drawPath(path, paint)
        }
    }
}

@Composable
fun ShapeCanvasButton(
    size: Dp,
    figure: Figure,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.None
) {
    Ripple(bounded = false) {
        Clickable(onClick = onClick) {
            ShapeCanvas(
                size = size,
                figure = figure,
                modifier = modifier
            )
        }
    }
}

@Preview
@Composable
fun PreviewShapes() {
    Column {
        listOf(
            Figure(
                shape = Shape.T,
                color = com.inspiredandroid.braincup.games.tools.Color.GREEN
            )
        ).forEach { figure ->
            ShapeCanvasButton(
                size = 48.dp,
                modifier = Gravity.Center wraps Spacing(8.dp),
                figure = figure,
                onClick = {})
        }
    }
}