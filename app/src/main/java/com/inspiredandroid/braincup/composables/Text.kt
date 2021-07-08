package com.inspiredandroid.braincup.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

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
    var state by remember { mutableStateOf(TextFieldValue()) }

    if (title != null) {
        Headline6(text = title)
    }

    OutlinedTextField(
        value = state,
        onValueChange = { newText ->
            state = newText
            onChange(newText.text)
        },
        modifier = Modifier
            .sizeIn(minWidth = 250.dp, minHeight = 48.dp)
    )

    if (helperText != null) {
        Subtitle2(text = helperText, modifier = Modifier.fillMaxWidth())
    }
}