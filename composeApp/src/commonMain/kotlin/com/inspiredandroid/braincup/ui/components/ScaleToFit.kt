package com.inspiredandroid.braincup.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import kotlin.math.roundToInt

/**
 * Measures [content] at the width it asks for and shrinks the whole thing uniformly when that is
 * wider than the space available.
 *
 * For a row that reads as one unit - an equation, a sequence - neither of the usual answers works.
 * Wrapping it onto a second line breaks the left-to-right reading, and letting it run off the edge
 * hides part of the puzzle: Missing Operators grows to five numbers and four slots at round 10,
 * which is wider than a phone. Scaling keeps the line intact and keeps the proportions between the
 * numbers, the slots and the gaps; the cost is that a longer row draws smaller and its tap targets
 * shrink with it, so this suits content whose length is bounded and known.
 *
 * The content is scaled through its layer, so it stays crisp and stays clickable at the scaled
 * position. The reported size is the scaled one, which lets the parent centre it as usual.
 */
@Composable
fun ScaleToFit(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        val placeables = measurables.map {
            it.measure(constraints.copy(minWidth = 0, maxWidth = Constraints.Infinity))
        }
        val contentWidth = placeables.maxOfOrNull { it.width } ?: 0
        val contentHeight = placeables.maxOfOrNull { it.height } ?: 0

        val scale = if (contentWidth > constraints.maxWidth && contentWidth > 0) {
            constraints.maxWidth / contentWidth.toFloat()
        } else {
            1f
        }

        val width = (contentWidth * scale).roundToInt().coerceAtMost(constraints.maxWidth)
        val height = (contentHeight * scale).roundToInt()
        layout(width, height) {
            placeables.forEach { placeable ->
                placeable.placeWithLayer(0, 0) {
                    scaleX = scale
                    scaleY = scale
                    transformOrigin = TransformOrigin(0f, 0f)
                }
            }
        }
    }
}
