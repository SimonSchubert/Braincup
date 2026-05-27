package com.inspiredandroid.braincup.games.tools

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import com.inspiredandroid.braincup.ui.theme.LocalAccessiblePalette
import androidx.compose.ui.graphics.Color as ComposeColor

// Accessible palette uses Okabe & Ito 2008 / Wong 2011 — the empirically validated
// 8-color categorical set that stays distinguishable under protanopia and
// deuteranopia (together >95% of color-blind users). The 8 canonical colors fill
// the 8 chromatic slots; ROSA maps to black (the palette's 8th color) because no
// pale-pink hex stays separable from PURPLE under deuteranopia. ROSA's displayName
// is only rendered as text in Color Confusion, which is hidden while this palette
// is active, so the rose→black mismatch is invisible to the user.
enum class Color(
    val displayName: String,
    val standardColor: ComposeColor,
    val accessibleColor: ComposeColor,
) {
    RED(
        displayName = "red",
        standardColor = ComposeColor(0xFFE74C3C),
        accessibleColor = ComposeColor(0xFFD55E00), // Okabe-Ito vermillion
    ),
    GREEN(
        displayName = "green",
        standardColor = ComposeColor(0xFF2ECC71),
        accessibleColor = ComposeColor(0xFF009E73), // Okabe-Ito bluish green
    ),
    BLUE(
        displayName = "blue",
        standardColor = ComposeColor(0xFF3498DB),
        accessibleColor = ComposeColor(0xFF0072B2), // Okabe-Ito blue
    ),
    PURPLE(
        displayName = "purple",
        standardColor = ComposeColor(0xFF9B59B6),
        accessibleColor = ComposeColor(0xFFCC79A7), // Okabe-Ito reddish purple
    ),
    YELLOW(
        displayName = "yellow",
        standardColor = ComposeColor(0xFFF1C40F),
        accessibleColor = ComposeColor(0xFFF0E442), // Okabe-Ito yellow
    ),
    ORANGE(
        displayName = "orange",
        standardColor = ComposeColor(0xFFE67E22),
        accessibleColor = ComposeColor(0xFFE69F00), // Okabe-Ito orange
    ),
    TURQUOISE(
        displayName = "turquoise",
        standardColor = ComposeColor(0xFF12CBC4),
        accessibleColor = ComposeColor(0xFF56B4E9), // Okabe-Ito sky blue
    ),
    ROSA(
        displayName = "rosa",
        standardColor = ComposeColor(0xFFFDA7DF),
        accessibleColor = ComposeColor(0xFF000000), // Okabe-Ito black
    ),
    GREY_LIGHT(
        displayName = "light grey",
        standardColor = ComposeColor(0xFF999999),
        accessibleColor = ComposeColor(0xFF999999), // achromatic, already CVD-safe; lifted from 0x56 so it stays legible on dark cell backgrounds
    ),
    ;

    fun composeColor(accessible: Boolean): ComposeColor = if (accessible) accessibleColor else standardColor
}

@Composable
@ReadOnlyComposable
fun Color.composeColor(): ComposeColor = composeColor(LocalAccessiblePalette.current)
