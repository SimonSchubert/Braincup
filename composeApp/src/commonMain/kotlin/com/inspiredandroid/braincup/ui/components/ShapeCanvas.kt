package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import com.inspiredandroid.braincup.games.tools.Figure
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ShapeCanvas(
    figure: Figure,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val path = Path()
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

@Composable
fun ShapeCanvasButton(
    figure: Figure,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val figureShape = GenericShape { size, _ ->
        val cx = size.width / 2f
        val cy = size.height / 2f
        val radians = figure.rotation.toFloat() * (PI.toFloat() / 180f)
        val cosR = cos(radians)
        val sinR = sin(radians)
        figure.shape.paths.forEachIndexed { index, pair ->
            val rawX = size.width * pair.first
            val rawY = size.height * pair.second
            val x: Float
            val y: Float
            if (figure.rotation != 0) {
                x = cx + (rawX - cx) * cosR - (rawY - cy) * sinR
                y = cy + (rawX - cx) * sinR + (rawY - cy) * cosR
            } else {
                x = rawX
                y = rawY
            }
            if (index == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }
    val interactionSource = remember { MutableInteractionSource() }
    ShapeCanvas(
        figure = figure,
        modifier = modifier
            .alpha(if (enabled) 1f else 0.3f)
            .then(if (enabled) Modifier.pointerHoverIcon(PointerIcon.Hand) else Modifier)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            )
            .clip(figureShape)
            .indication(interactionSource, ripple()),
    )
}
