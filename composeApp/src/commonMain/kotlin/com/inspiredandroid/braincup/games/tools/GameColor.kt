package com.inspiredandroid.braincup.games.tools

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.inspiredandroid.braincup.ui.theme.LocalAccessiblePalette
import com.inspiredandroid.braincup.ui.theme.isDarkColorScheme

// Accessible palette uses Okabe & Ito 2008 / Wong 2011 — the empirically validated
// 8-color categorical set that stays distinguishable under protanopia and
// deuteranopia (together >95% of color-blind users). Seven of the eight canonical
// colors are chromatic; the eighth is achromatic, and ROSA fills it because no
// pale-pink hex stays separable from PURPLE under deuteranopia.
//
// That achromatic slot has to flip with the background: Okabe-Ito specifies black,
// which is invisible on the dark and OLED schemes (OLED's background is pure black),
// so [accessibleColorOnDark] carries the inverted near-white and [composeColor]
// picks between them. Pattern Sequence, Visual Memory and Anomaly Puzzle all draw
// ROSA figures and none of them is gated by GameType.requiresColorVision, so both
// the fill and the localized name (see LocalizedNames.localizedName) are user-visible
// while this palette is active.
//
// Hue alone is still not enough (tritanopia, low vision, a dim screen), so the palette also
// textures every figure with [pattern]. YELLOW stays plain because it is far lighter than the
// rest; GREY_LIGHT stays plain because it is only ever Path Finder's background.
enum class GameColor(
    val pattern: ColorPattern,
    val displayName: String,
    val standardColor: Color,
    val accessibleColor: Color,
    val accessibleColorOnDark: Color = accessibleColor,
) {
    RED(
        pattern = ColorPattern.HORIZONTAL_STRIPES,
        displayName = "red",
        standardColor = Color(0xFFE74C3C),
        accessibleColor = Color(0xFFD55E00), // Okabe-Ito vermillion
    ),
    GREEN(
        pattern = ColorPattern.DOTS,
        displayName = "green",
        standardColor = Color(0xFF2ECC71),
        accessibleColor = Color(0xFF009E73), // Okabe-Ito bluish green
    ),
    BLUE(
        pattern = ColorPattern.VERTICAL_STRIPES,
        displayName = "blue",
        standardColor = Color(0xFF3498DB),
        accessibleColor = Color(0xFF0072B2), // Okabe-Ito blue
    ),
    PURPLE(
        pattern = ColorPattern.DIAGONAL_STRIPES,
        displayName = "purple",
        standardColor = Color(0xFF9B59B6),
        accessibleColor = Color(0xFFCC79A7), // Okabe-Ito reddish purple
    ),
    YELLOW(
        pattern = ColorPattern.PLAIN,
        displayName = "yellow",
        standardColor = Color(0xFFF1C40F),
        accessibleColor = Color(0xFFF0E442), // Okabe-Ito yellow
    ),
    ORANGE(
        pattern = ColorPattern.CROSSHATCH,
        displayName = "orange",
        standardColor = Color(0xFFE67E22),
        accessibleColor = Color(0xFFE69F00), // Okabe-Ito orange
    ),
    TURQUOISE(
        pattern = ColorPattern.CHECKER,
        displayName = "turquoise",
        standardColor = Color(0xFF12CBC4),
        accessibleColor = Color(0xFF56B4E9), // Okabe-Ito sky blue
    ),
    ROSA(
        pattern = ColorPattern.RINGS,
        displayName = "rosa",
        standardColor = Color(0xFFFDA7DF),
        accessibleColor = Color(0xFF000000), // Okabe-Ito black, for light backgrounds
        // Near-white rather than pure white so the prism bevel, which darkens the face for its
        // sides, still has somewhere to go.
        accessibleColorOnDark = Color(0xFFF2F2F2),
    ),
    GREY_LIGHT(
        pattern = ColorPattern.PLAIN,
        displayName = "light grey",
        standardColor = Color(0xFF999999),
        accessibleColor = Color(0xFF999999), // achromatic, already CVD-safe; lifted from 0x56 so it stays legible on dark cell backgrounds
    ),
    ;

    fun composeColor(accessible: Boolean, onDark: Boolean = false): Color = when {
        !accessible -> standardColor
        onDark -> accessibleColorOnDark
        else -> accessibleColor
    }
}

@Composable
@ReadOnlyComposable
fun GameColor.composeColor(): Color = composeColor(
    accessible = LocalAccessiblePalette.current,
    onDark = isDarkColorScheme,
)

/** The texture this colour carries, which is only drawn while the colour-blind palette is on. */
@Composable
@ReadOnlyComposable
fun GameColor.visiblePattern(): ColorPattern = if (LocalAccessiblePalette.current) pattern else ColorPattern.PLAIN
