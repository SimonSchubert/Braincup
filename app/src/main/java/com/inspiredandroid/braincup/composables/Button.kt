package com.inspiredandroid.braincup.composables

import androidx.annotation.DrawableRes
import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.core.Text
import androidx.ui.graphics.Color
import androidx.ui.graphics.vector.DrawVector
import androidx.ui.layout.*
import androidx.ui.material.Button
import androidx.ui.material.MaterialTheme
import androidx.ui.res.vectorResource
import androidx.ui.unit.dp

@Composable
fun TextButton(
    text: String,
    modifier: Modifier = Modifier.None,
    onClick: (() -> Unit)? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        children = { Text(text) }
    )
}

@Composable
fun TextImageButton(
    text: String,
    @DrawableRes drawableResource: Int,
    modifier: Modifier = Modifier.None,
    color: Color = MaterialTheme.colors().primary,
    onClick: (() -> Unit)? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        backgroundColor = color,
        contentColor = Color.White,
        shape = MaterialTheme.shapes().button,
        elevation = 2.dp
    ) {
        Row {
            val vectorAsset = vectorResource(drawableResource)
            Container(width = 24.dp, height = 24.dp) {
                DrawVector(vectorAsset)
            }
            Spacer(LayoutWidth(16.dp))
            Text(text = text, modifier = LayoutGravity.Center)
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
        val vectorAsset = vectorResource(drawableResource)
        Container(width = 24.dp, height = 24.dp) {
            DrawVector(vectorAsset)
        }
    }
}

