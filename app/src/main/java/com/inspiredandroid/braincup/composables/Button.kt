package com.inspiredandroid.braincup.composables

import androidx.annotation.DrawableRes
import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.graphics.Color
import androidx.ui.graphics.vector.drawVector
import androidx.ui.layout.Container
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.preferredWidth
import androidx.ui.material.Button
import androidx.ui.material.MaterialTheme
import androidx.ui.res.vectorResource
import androidx.ui.unit.dp

@Composable
fun TextButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        text = { Text(text) }
    )
}

@Composable
fun TextImageButton(
    text: String,
    @DrawableRes drawableResource: Int,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.primary,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        backgroundColor = color,
        contentColor = Color.White,
        shape = MaterialTheme.shapes.medium,
        elevation = 2.dp
    ) {
        Row {
            val vectorAsset = vectorResource(drawableResource)
            Container(width = 24.dp, height = 24.dp, modifier = drawVector(vectorAsset)) {}
            Spacer(Modifier.preferredWidth(16.dp))
            Text(text = text, modifier = Modifier.gravity(align = Alignment.CenterVertically))
        }
    }
}

@Composable
fun ImageButton(
    @DrawableRes drawableResource: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(onClick = onClick, modifier = modifier) {
        val vectorAsset = vectorResource(drawableResource)
        Container(width = 24.dp, height = 24.dp, modifier = drawVector(vectorAsset)) {}
    }
}

