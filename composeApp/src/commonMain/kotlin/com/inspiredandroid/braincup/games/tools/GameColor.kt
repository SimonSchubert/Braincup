package com.inspiredandroid.braincup.games.tools

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.inspiredandroid.braincup.ui.theme.LocalAccessiblePalette

// Accessible palette uses Okabe & Ito 2008 / Wong 2011 — the empirically validated
// 8-color categorical set that stays distinguishable under protanopia and
// deuteranopia (together >95% of color-blind users). The 8 canonical colors fill
// the 8 chromatic slots; ROSA maps to black (the palette's 8th color) because no
// pale-pink hex stays separable from PURPLE under deuteranopia. ROSA's displayName
// is only rendered as text in Color Confusion, which is hidden while this palette
// is active, so the rose→black mismatch is invisible to the user.
enum class GameColor(
    val displayName: String,
    val standardColor: Color,
    val accessibleColor: Color,
) {
    RED(
        displayName = "red",
        standardColor = Color(0xFFE74C3C),
        accessibleColor = Color(0xFFD55E00), // Okabe-Ito vermillion
    ),
    GREEN(
        displayName = "green",
        standardColor = Color(0xFF2ECC71),
        accessibleColor = Color(0xFF009E73), // Okabe-Ito bluish green
    ),
    BLUE(
        displayName = "blue",
        standardColor = Color(0xFF3498DB),
        accessibleColor = Color(0xFF0072B2), // Okabe-Ito blue
    ),
    PURPLE(
        displayName = "purple",
        standardColor = Color(0xFF9B59B6),
        accessibleColor = Color(0xFFCC79A7), // Okabe-Ito reddish purple
    ),
    YELLOW(
        displayName = "yellow",
        standardColor = Color(0xFFF1C40F),
        accessibleColor = Color(0xFFF0E442), // Okabe-Ito yellow
    ),
    ORANGE(
        displayName = "orange",
        standardColor = Color(0xFFE67E22),
        accessibleColor = Color(0xFFE69F00), // Okabe-Ito orange
    ),
    TURQUOISE(
        displayName = "turquoise",
        standardColor = Color(0xFF12CBC4),
        accessibleColor = Color(0xFF56B4E9), // Okabe-Ito sky blue
    ),
    ROSA(
        displayName = "rosa",
        standardColor = Color(0xFFFDA7DF),
        accessibleColor = Color(0xFF000000), // Okabe-Ito black
    ),
    GREY_LIGHT(
        displayName = "light grey",
        standardColor = Color(0xFF999999),
        accessibleColor = Color(0xFF999999), // achromatic, already CVD-safe; lifted from 0x56 so it stays legible on dark cell backgrounds
    ),
    ;

    fun composeColor(accessible: Boolean): Color = if (accessible) accessibleColor else standardColor
}

@Composable
@ReadOnlyComposable
fun GameColor.composeColor(): Color = composeColor(LocalAccessiblePalette.current)
