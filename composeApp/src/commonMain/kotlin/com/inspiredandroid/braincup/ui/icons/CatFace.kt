package com.inspiredandroid.braincup.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * A cute tuxedo cat face, hand-built as a scalable [ImageVector] so it needs no per-platform asset
 * files and renders crisply at any size. Used as the playing piece in Cat Queens and on its menu
 * tile. Original art.
 */
private val CatBlack = Color(0xFF2C2A33)
private val CatWhite = Color(0xFFFDFBF7)
private val CatEye = Color(0xFF8BC34A)
private val CatNose = Color(0xFFE6899E)

private fun PathBuilder.circle(cx: Float, cy: Float, r: Float) {
    moveTo(cx - r, cy)
    arcToRelative(r, r, 0f, isMoreThanHalf = false, isPositiveArc = true, 2 * r, 0f)
    arcToRelative(r, r, 0f, isMoreThanHalf = false, isPositiveArc = true, -2 * r, 0f)
    close()
}

private fun PathBuilder.triangle(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float) {
    moveTo(x1, y1)
    lineTo(x2, y2)
    lineTo(x3, y3)
    close()
}

val CatFace: ImageVector by lazy {
    ImageVector.Builder(
        name = "CatFace",
        defaultWidth = 48.dp,
        defaultHeight = 48.dp,
        viewportWidth = 48f,
        viewportHeight = 48f,
    ).apply {
        // Ears and head in one black shape.
        path(fill = SolidColor(CatBlack)) {
            triangle(13f, 4f, 10f, 20f, 24f, 13f)
            triangle(35f, 4f, 38f, 20f, 24f, 13f)
            circle(24f, 26f, 15f)
        }
        // White muzzle: two overlapping lobes.
        path(fill = SolidColor(CatWhite)) {
            circle(19f, 33f, 7.5f)
            circle(29f, 33f, 7.5f)
        }
        // Eyes: white sclera, green iris, dark pupil.
        path(fill = SolidColor(CatWhite)) {
            circle(18f, 24f, 4.3f)
            circle(30f, 24f, 4.3f)
        }
        path(fill = SolidColor(CatEye)) {
            circle(18f, 24f, 2.7f)
            circle(30f, 24f, 2.7f)
        }
        path(fill = SolidColor(CatBlack)) {
            circle(18f, 24f, 1.2f)
            circle(30f, 24f, 1.2f)
        }
        // Pink nose.
        path(fill = SolidColor(CatNose)) {
            triangle(24f, 33f, 21.3f, 30f, 26.7f, 30f)
        }
    }.build()
}
