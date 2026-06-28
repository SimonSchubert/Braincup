package com.inspiredandroid.braincup.ui.theme

import androidx.compose.ui.graphics.Color

object MatchstickColors {
    val WoodBody = Color(0xFFE0A85B)
    val WoodSide = Color(0xFFB07E3C)
    val WoodHead = Color(0xFF4C9A52)
    val WoodHeadSolved = SuccessGreen

    // Sticks that can no longer be picked up stay readable but lose the vivid green head.
    val LockedBody = Color(0xFFC9A877)
    val LockedSide = Color(0xFF9B8052)
    val LockedHead = Color(0xFF8F8A79)

    // Move-budget pips in the play screen header.
    val PipSpentBody = Color(0xFFCDC4B6)
    val PipSpentSide = Color(0xFFAFA591)
    val PipSpentHead = Color(0xFF9E978B)

    /** Category tile accent on the overview screen. */
    const val TileAccentArgb = 0xFFCB8A43L

    fun woodHead(solved: Boolean): Color = if (solved) WoodHeadSolved else WoodHead
}
