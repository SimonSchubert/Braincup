package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inspiredandroid.braincup.ui.theme.Primary

@Composable
fun DefaultButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    value: String,
) {
    Button(
        onClick = { onClick() },
        modifier = modifier
            .pointerHoverIcon(PointerIcon.Hand)
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp),
    ) {
        Text(value)
    }
}

@Composable
fun CircleButton(
    onClick: () -> Unit,
    value: String,
) {
    Box(
        modifier = Modifier
            .sizeIn(56.dp, 56.dp)
            .clip(CircleShape)
            .background(Primary)
            .pointerHoverIcon(PointerIcon.Hand)
            .clickable(onClick = onClick),
        contentAlignment = androidx.compose.ui.Alignment.Center,
    ) {
        Text(
            value,
            color = androidx.compose.ui.graphics.Color.White,
            fontSize = 22.sp,
        )
    }
}
