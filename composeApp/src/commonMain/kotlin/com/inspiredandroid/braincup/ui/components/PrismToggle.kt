package com.inspiredandroid.braincup.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.ui.theme.Primary

private val TrackWidth = 52.dp
private val TrackHeight = 30.dp
private val ThumbSize = 22.dp
private val ThumbInset = 4.dp

/**
 * On/off toggle drawn in the app's "Prism" design language: a chamfered 3D [PrismCard] track with a
 * smaller faceted thumb that slides left (off) to right (on). The track crossfades between
 * [androidx.compose.material3.ColorScheme.surfaceVariant] and [Primary], matching the rest of the
 * prism-styled surfaces instead of the default Material `Switch`.
 *
 * Display-only: it reflects [checked] and animates between states, but expects the surrounding row
 * to handle taps (see `SettingsToggleRow`).
 */
@Composable
fun PrismToggle(
    checked: Boolean,
    modifier: Modifier = Modifier,
) {
    val trackColor by animateColorAsState(
        targetValue = if (checked) Primary else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(durationMillis = 120),
    )
    val thumbColor =
        if (checked) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) TrackWidth - ThumbSize - ThumbInset else ThumbInset,
        animationSpec = tween(durationMillis = 120),
    )

    // Keep the chamfer orientation and the left-to-right slide consistent regardless of the system
    // layout direction, matching the convention used by PrismShape / PrismProgressBar.
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Box(
            modifier = modifier.size(width = TrackWidth, height = TrackHeight),
            contentAlignment = Alignment.CenterStart,
        ) {
            PrismCard(
                face = trackColor,
                modifier = Modifier.size(width = TrackWidth, height = TrackHeight),
            )
            ColorPrismCell(
                face = thumbColor,
                modifier = Modifier
                    .offset(x = thumbOffset)
                    .size(ThumbSize),
            )
        }
    }
}
