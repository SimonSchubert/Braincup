package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Replace * with × and / with ÷ for better math display.
 */
fun String.formatMathSymbols(): String = this.replace("*", " \u00D7 ")
    .replace("/", " \u00F7 ")
    .replace("+", " + ")
    .replace("-", " - ")
    .replace("  ", " ")
    .trim()

@Composable
fun MathText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    textAlign: TextAlign? = null,
) {
    Text(
        text = text.formatMathSymbols(),
        modifier = modifier,
        style = style,
        textAlign = textAlign,
    )
}

@Composable
fun FractionText(
    numerator: String,
    denominator: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
) {
    val barColor = if (color != Color.Unspecified) {
        color
    } else if (style.color != Color.Unspecified) {
        style.color
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Column(
        modifier = modifier.width(IntrinsicSize.Min),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = numerator,
            style = style,
            color = color,
            maxLines = 1,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(barColor),
        )
        Text(
            text = denominator,
            style = style,
            color = color,
            maxLines = 1,
        )
    }
}
