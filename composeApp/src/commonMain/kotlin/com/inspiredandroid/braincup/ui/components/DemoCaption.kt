package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Caption line for a self-playing demo whose text changes between phases. The demos sit in a
 * vertically-centered column, so a caption that grows or shrinks by a line as the text changes would
 * shift the whole board up and down ("jiggle"). This lays every candidate in [all] out invisibly to
 * reserve the height and width of the tallest one up front, and only draws [current] on top, so the
 * surrounding layout stays put no matter which caption is showing. [current] must be one of [all].
 */
@Composable
fun DemoCaption(
    current: StringResource,
    all: List<StringResource>,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        all.forEach { res ->
            Text(
                text = stringResource(res),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                // Only the active caption is drawn; the rest are invisible and exist to hold space.
                modifier = Modifier.alpha(if (res == current) 1f else 0f),
            )
        }
    }
}
