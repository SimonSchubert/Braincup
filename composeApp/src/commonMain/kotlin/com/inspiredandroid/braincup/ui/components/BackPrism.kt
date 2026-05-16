package com.inspiredandroid.braincup.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

private val BackPrismFacet = 6.dp
private val BackPrismPressShift = 3.dp

@Composable
fun BackPrism(
    color: Color,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val side = remember(color) { color.darken(0.7f) }
    val bottom = remember(color) { color.darken(0.5f) }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val shiftDp by animateDpAsState(
        targetValue = if (isPressed) BackPrismPressShift else 0.dp,
        animationSpec = tween(durationMillis = 120),
    )
    val faceColor = if (isPressed) side else color

    val silhouettePath = remember { Path() }
    val faceAndSidePath = remember { Path() }
    val frontPath = remember { Path() }

    Box(
        modifier = modifier
            .size(48.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClickLabel = contentDescription,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(32.dp)) {
            val w = size.width
            val h = size.height
            val f = BackPrismFacet.toPx()
            val s = shiftDp.toPx()
            val d = f - s

            // Face (front triangle), shifts diagonally by (s, s) when pressed.
            val faceTopX = w - d
            val faceTopY = s
            val faceTipX = s
            val faceTipY = (h - f) / 2f + s
            val faceBottomX = w - d
            val faceBottomY = h - d

            // Outer chamfer vertices (fixed): mirror PrismShape's (w, f) and (f, h) corners.
            val topChamferX = w
            val topChamferY = f
            val tipChamferX = f
            val tipChamferY = (h + f) / 2f
            val outerBottomX = w
            val outerBottomY = h

            // Silhouette: chamfered pentagon (face + extruded right edge + extruded bottom hypotenuse).
            silhouettePath.reset()
            silhouettePath.apply {
                moveTo(faceTopX, faceTopY)
                lineTo(topChamferX, topChamferY)
                lineTo(outerBottomX, outerBottomY)
                lineTo(tipChamferX, tipChamferY)
                lineTo(faceTipX, faceTipY)
                close()
            }

            // faceAndSide: cut off the bottom rim with a diagonal from the outer bottom corner
            // to the face's bottom-right corner.
            faceAndSidePath.reset()
            faceAndSidePath.apply {
                moveTo(faceTopX, faceTopY)
                lineTo(topChamferX, topChamferY)
                lineTo(outerBottomX, outerBottomY)
                lineTo(faceBottomX, faceBottomY)
                lineTo(faceTipX, faceTipY)
                close()
            }

            frontPath.reset()
            frontPath.apply {
                moveTo(faceTopX, faceTopY)
                lineTo(faceBottomX, faceBottomY)
                lineTo(faceTipX, faceTipY)
                close()
            }

            drawPath(silhouettePath, bottom)
            drawPath(faceAndSidePath, side)
            drawPath(frontPath, faceColor)
        }
    }
}
