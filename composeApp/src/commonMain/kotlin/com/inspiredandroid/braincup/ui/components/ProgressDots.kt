package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.ui.theme.Primary

/**
 * A horizontal row of dots tracking progress through a stepped flow.
 *
 * Dots before [currentIndex] render as completed, the dot at [currentIndex] as the active step,
 * and dots after as pending. Pass [currentColor] equal to [mutedColor] (and equal sizes) to render
 * a flat completed/pending look without a distinct active dot.
 */
@Composable
fun ProgressDots(
    currentIndex: Int,
    total: Int,
    modifier: Modifier = Modifier,
    completedColor: Color = Primary,
    currentColor: Color = completedColor,
    mutedColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
    activeSize: Dp = 12.dp,
    inactiveSize: Dp = 8.dp,
    completedBorder: Color = Color.Unspecified,
    mutedBorder: Color = Color.Unspecified,
    borderWidth: Dp = 1.5.dp,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(total) { index ->
            val isCurrent = index == currentIndex
            val isCompleted = index < currentIndex
            val fill = when {
                isCompleted -> completedColor
                isCurrent -> currentColor
                else -> mutedColor
            }
            val borderColor = when {
                isCompleted -> completedBorder
                else -> mutedBorder
            }
            val size = if (isCurrent) activeSize else inactiveSize
            val base = Modifier
                .size(size)
                .background(fill, CircleShape)
            val withBorder = if (borderColor.isSpecified()) {
                base.border(borderWidth, borderColor, CircleShape)
            } else {
                base
            }
            Box(modifier = withBorder)
        }
    }
}

private fun Color.isSpecified(): Boolean = this != Color.Unspecified
