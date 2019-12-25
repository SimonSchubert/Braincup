package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.*
import androidx.ui.foundation.shape.border.Border
import androidx.ui.foundation.shape.border.DrawBorder
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.Container
import androidx.ui.layout.MinSize
import androidx.ui.layout.Spacing
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


@Composable
fun Input(
    title: String? = null,
    helperText: String? = null,
    modifier: Modifier = Modifier.None,
    onChange: ((String) -> Unit)
) {
    val state = +state { EditorModel("") }
    if (title != null) {
        Headline6(text = title)
    }
    Container(
        modifier = modifier wraps MinSize(250.dp, 48.dp)
    ) {
        DrawBorder(shape = RoundedCornerShape(4.dp), border = Border(Color.Gray, 1.dp))
        TextField(value = state.value, onValueChange = {
            state.value = it
            onChange(it.text)
        }, modifier = Spacing(8.dp))
    }
    if (helperText != null) {
        Subtitle2(text = helperText, modifier = modifier)
    }
}