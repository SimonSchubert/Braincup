package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
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
    all: ImmutableList<StringResource>,
    modifier: Modifier = Modifier,
    emphasis: ImmutableSet<StringResource> = persistentSetOf(),
) {
    val defaultColor = MaterialTheme.colorScheme.onSurfaceVariant
    val emphasisColor = MaterialTheme.colorScheme.error
    Box(
        modifier = modifier.padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        all.forEach { res ->
            val isCurrent = res == current
            val isEmphasis = res in emphasis
            Text(
                text = stringResource(res),
                style = MaterialTheme.typography.bodyMedium,
                color = if (isCurrent && isEmphasis) emphasisColor else defaultColor,
                fontWeight = if (isCurrent && isEmphasis) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                // Only the active caption is drawn; the rest are invisible and exist to hold space.
                modifier = Modifier.alpha(if (isCurrent) 1f else 0f),
            )
        }
    }
}

/**
 * Like [DemoCaption] for captions that need format args or other runtime text. [reserveTexts] lists
 * every variant (use the widest formatting up front) so the box keeps the tallest line count.
 */
@Composable
fun DemoCaptionText(
    text: String,
    reserveTexts: ImmutableList<String>,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    fontWeight: FontWeight = FontWeight.Normal,
) {
    Box(
        modifier = modifier.padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        reserveTexts.forEach { reserve ->
            Text(
                text = reserve,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(0f),
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = color,
            fontWeight = fontWeight,
            textAlign = TextAlign.Center,
        )
    }
}
