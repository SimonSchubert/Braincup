package com.inspiredandroid.braincup.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
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
