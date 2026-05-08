package com.inspiredandroid.braincup.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Color.darken(darkenBy: Float = 0.3f): Color = copy(
    red = red * darkenBy,
    green = green * darkenBy,
    blue = blue * darkenBy,
    alpha = alpha,
)

private val PrismFacet = 8.dp
private val PrismCardFacet = 3.dp
private val PressShift = 4.dp

@Composable
fun PrismTile(
    face: Color,
    side: Color? = null,
    bottom: Color? = null,
    modifier: Modifier = Modifier,
    isClickable: Boolean = true,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    val resolvedSide = remember(face, side) { side ?: face.darken(0.7f) }
    val resolvedBottom = remember(face, bottom) { bottom ?: face.darken(0.5f) }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val hapticFeedback = LocalHapticFeedback.current

    val sunken = isPressed || isSelected
    val shift by animateDpAsState(
        targetValue = if (sunken) PressShift else 0.dp,
        animationSpec = tween(durationMillis = 120),
    )
    val faceColor = if (sunken) resolvedSide else face

    Box(
        modifier = modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = isClickable,
        ) {
            onClick()
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        },
        contentAlignment = Alignment.Center,
    ) {
        PrismShape(
            face = faceColor,
            side = resolvedSide,
            bottom = resolvedBottom,
            shift = shift,
            facet = PrismFacet,
            modifier = Modifier.matchParentSize(),
        )
        Box(
            modifier = Modifier.padding(
                start = shift,
                top = shift,
                end = PrismFacet - shift,
                bottom = PrismFacet - shift,
            ),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}

@Composable
fun PrismCard(
    face: Color,
    side: Color? = null,
    bottom: Color? = null,
    modifier: Modifier = Modifier,
    facet: Dp = PrismCardFacet,
    content: @Composable () -> Unit = {},
) {
    val resolvedSide = remember(face, side) { side ?: face.darken(0.7f) }
    val resolvedBottom = remember(face, bottom) { bottom ?: face.darken(0.5f) }
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        PrismShape(
            face = face,
            side = resolvedSide,
            bottom = resolvedBottom,
            shift = 0.dp,
            facet = facet,
            modifier = Modifier.matchParentSize(),
        )
        Box(
            modifier = Modifier.padding(end = facet, bottom = facet),
        ) {
            content()
        }
    }
}

/** A purely decorative colored prism cell with no inner content (used for grid cells, dots, accents). */
@Composable
fun ColorPrismCell(
    face: Color,
    modifier: Modifier = Modifier,
    facet: Dp = 2.dp,
    side: Color? = null,
    bottom: Color? = null,
) {
    PrismCard(
        face = face,
        side = side,
        bottom = bottom,
        facet = facet,
        modifier = modifier,
    )
}

@Composable
private fun PrismShape(
    face: Color,
    side: Color,
    bottom: Color,
    shift: Dp,
    facet: Dp,
    modifier: Modifier = Modifier,
) {
    val silhouettePath = remember { Path() }
    val faceAndSidePath = remember { Path() }
    val frontPath = remember { Path() }
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val f = facet.toPx()
        val s = shift.toPx()
        val d = f - s

        silhouettePath.reset()
        silhouettePath.apply {
            moveTo(w - d, s)
            lineTo(w, s + d)
            lineTo(w, h)
            lineTo(s + d, h)
            lineTo(s, h - d)
            lineTo(s, s)
            close()
        }
        faceAndSidePath.reset()
        faceAndSidePath.apply {
            moveTo(w - d, s)
            lineTo(w, s + d)
            lineTo(w, h)
            lineTo(w - d, h - d)
            lineTo(s, h - d)
            lineTo(s, s)
            close()
        }
        frontPath.reset()
        frontPath.apply {
            moveTo(s, s)
            lineTo(w - d, s)
            lineTo(w - d, h - d)
            lineTo(s, h - d)
            close()
        }

        drawPath(silhouettePath, bottom)
        drawPath(faceAndSidePath, side)
        drawPath(frontPath, face)
    }
}
