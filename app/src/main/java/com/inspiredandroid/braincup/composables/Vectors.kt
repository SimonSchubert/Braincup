package com.inspiredandroid.braincup.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource

@Composable
fun VectorImageButton(@DrawableRes id: Int, onClick: () -> Unit) {
    Box(modifier = Modifier
        .clickable(onClick = onClick,
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(bounded = false)
        )) {
        VectorImage(id = id)
    }
}

@Composable
fun VectorImage(
    modifier: Modifier = Modifier, @DrawableRes id: Int,
    tint: Color = Color.Transparent
) {
    Image(
        painter = painterResource(id),
        modifier = modifier,
        colorFilter = if (tint != Color.Transparent) ColorFilter.tint(tint) else null,
        contentDescription = null
    )
}