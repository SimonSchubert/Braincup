package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Canvas
import androidx.ui.foundation.Clickable
import androidx.ui.graphics.Paint
import androidx.ui.graphics.Path
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.layout.RowScope.gravity
import androidx.ui.layout.padding
import androidx.ui.layout.preferredSize
import androidx.ui.material.ripple.ripple
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
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
    Container(
        width = size, height = size,
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.preferredSize(width = size, height = size), onCanvas = {
            val paint = Paint()
            paint.color = figure.color.getComposeColor()
            paint.isAntiAlias = true
            val path = Path()
            figure.shape.getPaths().forEachIndexed { index, pair ->
                val x = this.size.width.value * pair.first
                val y = this.size.height.value * pair.second
                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }
            path.close()

            if (figure.rotation != 0) {
                save()
                translate(
                    this.size.width.value / 2f,
                    this.size.height.value / 2f
                )
                rotate(figure.rotation.toFloat())
                translate(
                    -this.size.width.value / 2f,
                    -this.size.height.value / 2f
                )
                drawPath(path, paint)
                restore()
            } else {
                drawPath(path, paint)
            }
        })
    }
}

@Composable
fun ShapeCanvasButton(
    size: Dp,
    figure: Figure,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Clickable(onClick = onClick, modifier = Modifier.ripple()) {
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
        listOf(
            Figure(
                shape = Shape.T,
                color = com.inspiredandroid.braincup.games.tools.Color.GREEN
            )
        ).forEach { figure ->
            ShapeCanvasButton(
                size = 48.dp,
                modifier = Modifier.gravity(align = Alignment.CenterVertically) + Modifier.padding(8.dp),
                figure = figure,
                onClick = {})
        }
    }
}