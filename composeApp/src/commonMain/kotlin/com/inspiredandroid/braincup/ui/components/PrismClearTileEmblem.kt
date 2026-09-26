package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import com.inspiredandroid.braincup.games.PrismTileType

/**
 * The shape each Prism Clear tile type carries on its face, so a match can be spotted without
 * telling red from orange. Drawn lighter than a dark face and darker than a light one, so it keeps
 * its contrast under every palette.
 */
@Composable
fun PrismClearTileEmblem(type: PrismTileType, face: Color, modifier: Modifier = Modifier) {
    val ink = if (face.luminance() < 0.4f) lerp(face, Color.White, 0.6f) else lerp(face, Color.Black, 0.45f)
    PrismPolygon(
        points = type.shape.paths,
        face = ink,
        modifier = modifier.fillMaxSize(0.62f),
    )
}
