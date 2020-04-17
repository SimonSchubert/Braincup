package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.core.Modifier
import androidx.ui.foundation.*
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.LayoutSize
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.material.MaterialTheme
import androidx.ui.text.ParagraphStyle
import androidx.ui.text.style.TextAlign
import androidx.ui.unit.dp

@Composable
fun Subtitle1(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text,
        style = MaterialTheme.typography.subtitle1.merge(ParagraphStyle(textAlign = TextAlign.Center)),
        modifier = modifier
    )
}

@Composable
fun Subtitle2(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text,
        style = MaterialTheme.typography.subtitle2.merge(ParagraphStyle(textAlign = TextAlign.Center)),
        modifier = modifier
    )
}

@Composable
fun Headline3(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text,
        style = MaterialTheme.typography.h3,
        modifier = modifier
    )
}

@Composable
fun Headline4(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text,
        style = MaterialTheme.typography.h4,
        modifier = modifier
    )
}

@Composable
fun Headline5(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text,
        style = MaterialTheme.typography.h5,
        modifier = modifier
    )
}

@Composable
fun Headline6(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text,
        style = MaterialTheme.typography.h6,
        modifier = modifier
    )
}

@Composable
fun Input(
    title: String? = null,
    helperText: String? = null,
    onChange: ((String) -> Unit)
) {
    val state = state { "" }
    if (title != null) {
        Headline6(text = title)
    }
    Box(
        shape = RoundedCornerShape(4.dp),
        border = Border(1.dp, Color.Gray),
        modifier = LayoutSize.Min(250.dp, 48.dp)
    ) {
        TextField(value = TextFieldValue(state.value), onValueChange = {
            state.value = it.text
            onChange(it.text)
        }, modifier = Modifier.padding(8.dp))
    }
    if (helperText != null) {
        Subtitle2(text = helperText, modifier = Modifier.fillMaxWidth())
    }
}