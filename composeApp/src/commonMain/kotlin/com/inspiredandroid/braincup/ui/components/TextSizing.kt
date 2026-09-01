package com.inspiredandroid.braincup.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

/**
 * A text size pinned to a box measured in dp.
 *
 * Board cells, keyboard caps and digit tiles are all laid out in dp to fit a fixed number across
 * the screen, so they cannot grow with the font scale. A glyph asked for in sp spills straight out
 * of its own square as soon as the system font size goes up, which sliced every digit on the
 * Sudoku grid in half and pushed Wordle letters over their neighbours. Converting the dp back to
 * sp keeps the glyph inside the box it belongs to.
 */
@Composable
internal fun boxedTextSize(box: Dp, fraction: Float): TextUnit = with(LocalDensity.current) { (box * fraction).toSp() }

/**
 * Line metrics for a single glyph that has to sit centred in a box measured in dp.
 *
 * A parent Box centres the line box, not the ink inside it, and a line box carries whatever
 * leading the style asks for. Digit tiles pinned their line height to the whole tile, which is
 * taller than the prism face the glyph sits on, so the leading pushed every digit down towards
 * the bottom edge; dropping the pin instead inherits a line height in sp, which at a large system
 * font scale grows past the face and slides the digit off it. Trimming the leading off the first
 * and last line leaves the line box the size of the glyph's own metrics, so the box centres what
 * the eye sees at every font scale.
 */
internal val BoxedGlyphLineHeight = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.Both,
)
