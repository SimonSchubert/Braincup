package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
            .hoverHand()
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp),
    ) {
        Text(value)
    }
}

/**
 * A primary action button intended as the bottom CTA on result/interstitial screens.
 * Caps width at 420.dp on wide layouts and adds 24.dp horizontal insets so the touch
 * target stays comfortable across phones, tablets, and desktop.
 */
@Composable
fun PrimaryActionButton(
    onClick: () -> Unit,
    value: String,
    modifier: Modifier = Modifier,
) {
    DefaultButton(
        onClick = onClick,
        value = value,
        modifier = modifier
            .widthIn(max = 420.dp)
            .padding(horizontal = 24.dp),
    )
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
            .hoverHand()
            .clickable(onClick = onClick),
        contentAlignment = androidx.compose.ui.Alignment.Center,
    ) {
        val operatorIcon = OperatorIcons[value]
        if (operatorIcon != null) {
            androidx.compose.material3.Icon(
                imageVector = operatorIcon,
                contentDescription = value,
                tint = Color.White,
                modifier = Modifier.size(24.dp),
            )
        } else {
            Text(
                value,
                color = Color.White,
                fontSize = 22.sp,
            )
        }
    }
}
