package com.inspiredandroid.braincup.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.composeColor

@Composable
fun ShapeCanvas(
    figure: Figure,
    modifier: Modifier = Modifier,
) {
    PrismPolygon(
        points = figure.shape.paths,
        face = figure.color.composeColor(),
        rotationDegrees = figure.rotation.toFloat(),
        modifier = modifier,
    )
}
