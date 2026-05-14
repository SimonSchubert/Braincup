package com.inspiredandroid.braincup.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

private val TrophyPath = listOf(
    0.25f to 0.00f,
    0.75f to 0.00f,
    0.78f to 0.10f,
    0.95f to 0.15f,
    1.00f to 0.30f,
    0.95f to 0.42f,
    0.78f to 0.45f,
    0.72f to 0.55f,
    0.60f to 0.62f,
    0.58f to 0.75f,
    0.85f to 0.80f,
    0.85f to 0.92f,
    0.95f to 1.00f,
    0.05f to 1.00f,
    0.15f to 0.92f,
    0.15f to 0.80f,
    0.42f to 0.75f,
    0.40f to 0.62f,
    0.28f to 0.55f,
    0.22f to 0.45f,
    0.05f to 0.42f,
    0.00f to 0.30f,
    0.05f to 0.15f,
    0.22f to 0.10f,
)

@Composable
fun PrismTrophy(
    tint: Color,
    modifier: Modifier = Modifier,
) {
    PrismPolygon(
        points = TrophyPath,
        face = tint,
        modifier = modifier,
    )
}
