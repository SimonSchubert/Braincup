package com.inspiredandroid.braincup.composables

import androidx.annotation.DrawableRes
import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Clickable
import androidx.ui.graphics.Color
import androidx.ui.graphics.vector.drawVector
import androidx.ui.layout.Container
import androidx.ui.layout.LayoutSize
import androidx.ui.material.ripple.ripple
import androidx.ui.res.vectorResource

@Composable
fun VectorImageButton(@DrawableRes id: Int, onClick: () -> Unit) {
    Clickable(onClick = onClick, modifier = Modifier.ripple()) {
        VectorImage(id = id)
    }
}

@Composable
fun VectorImage(
    modifier: Modifier = Modifier, @DrawableRes id: Int,
    tint: Color = Color.Transparent
) {
    val vector = vectorResource(id)
    Container(
        modifier = modifier + LayoutSize(vector.defaultWidth, vector.defaultHeight) + drawVector(
            vector,
            tint
        )
    ) {

    }
}