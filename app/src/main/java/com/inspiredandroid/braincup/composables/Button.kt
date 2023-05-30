package com.inspiredandroid.braincup.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.elevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun TextButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        content = { Text(text) }
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
        colors = ButtonDefaults.buttonColors(
            backgroundColor = color,
            contentColor = Color.White
        ),
        shape = MaterialTheme.shapes.medium,
        elevation = elevation(2.dp)
    ) {
        Row {
            Icon(
                modifier = Modifier.requiredSize(24.dp),
                painter = painterResource(drawableResource),
                contentDescription = null
            )
            Spacer(Modifier.width(16.dp))
            Text(text = text, modifier = Modifier.align(Alignment.CenterVertically))
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
        Icon(
            modifier = Modifier.requiredSize(24.dp),
            painter=painterResource(drawableResource),
            contentDescription = null
        )
    }
}
