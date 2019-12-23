package com.inspiredandroid.braincup.composables

import androidx.annotation.DrawableRes
import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.core.Modifier
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.graphics.vector.DrawVector
import androidx.ui.layout.Container
import androidx.ui.layout.Row
import androidx.ui.layout.WidthSpacer
import androidx.ui.material.Button
import androidx.ui.res.vectorResource

@Composable
fun TextImageButton(
    text: String,
    @DrawableRes drawableResource: Int,
    modifier: Modifier = Modifier.None,
    onClick: (() -> Unit)? = null
) {
    Button(onClick = onClick, modifier = modifier) {
        Row {
            val vectorAsset =
                +vectorResource(drawableResource)
            Container(width = 24.dp, height = 24.dp) {
                DrawVector(vectorAsset)
            }
            WidthSpacer(16.dp)
            Text(text = text)
        }
    }
}

@Composable
fun ImageButton(
    @DrawableRes drawableResource: Int,
    modifier: Modifier = Modifier.None,
    onClick: (() -> Unit)? = null
) {
    Button(onClick = onClick, modifier = modifier) {
        val vectorAsset = +vectorResource(
            drawableResource
        )
        Container(width = 24.dp, height = 24.dp) {
            DrawVector(vectorAsset)
        }
    }
}

