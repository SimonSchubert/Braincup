package com.inspiredandroid.braincup.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

private val PodiumPath = listOf(
    0.00f to 0.35f,
    0.30f to 0.35f,
    0.30f to 0.00f,
    0.70f to 0.00f,
    0.70f to 0.35f,
    1.00f to 0.35f,
    1.00f to 1.00f,
    0.00f to 1.00f,
)

@Composable
fun PrismPodium(
    tint: Color,
    modifier: Modifier = Modifier,
) {
    PrismPolygon(
        points = PodiumPath,
        face = tint,
        modifier = modifier,
    )
}
