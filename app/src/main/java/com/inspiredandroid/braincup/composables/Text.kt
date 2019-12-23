package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.core.Modifier
import androidx.ui.core.Text
import androidx.ui.material.MaterialTheme
import androidx.ui.text.ParagraphStyle
import androidx.ui.text.style.TextAlign

@Composable
fun Subtitle1(
    text: String,
    modifier: Modifier = Modifier.None
) {
    Text(
        text,
        style = (+MaterialTheme.typography()).subtitle1,
        paragraphStyle = ParagraphStyle(textAlign = TextAlign.Center),
        modifier = modifier
    )
}

@Composable
fun Subtitle2(
    text: String,
    modifier: Modifier = Modifier.None
) {
    Text(
        text,
        style = (+MaterialTheme.typography()).subtitle2,
        paragraphStyle = ParagraphStyle(textAlign = TextAlign.Center),
        modifier = modifier
    )
}

@Composable
fun Headline3(
    text: String,
    modifier: Modifier = Modifier.None
) {
    Text(
        text,
        style = (+MaterialTheme.typography()).h3,
        modifier = modifier
    )
}

@Composable
fun Headline4(
    text: String,
    modifier: Modifier = Modifier.None
) {
    Text(
        text,
        style = (+MaterialTheme.typography()).h4,
        modifier = modifier
    )
}

@Composable
fun Headline5(
    text: String,
    modifier: Modifier = Modifier.None
) {
    Text(
        text,
        style = (+MaterialTheme.typography()).h5,
        modifier = modifier
    )
}

@Composable
fun Headline6(
    text: String,
    modifier: Modifier = Modifier.None
) {
    Text(
        text,
        style = (+MaterialTheme.typography()).h6,
        modifier = modifier
    )
}