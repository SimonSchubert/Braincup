package com.inspiredandroid.braincup.ui.components

import androidx.compose.ui.graphics.Color
import com.inspiredandroid.braincup.games.tools.getHex
import com.inspiredandroid.braincup.games.tools.Color as GameColor

fun GameColor.toComposeColor(): Color {
    return parseHexColor(this.getHex())
}

fun parseHexColor(hex: String): Color {
    val cleanHex = hex.removePrefix("#")
    val colorLong = cleanHex.toLong(16)
    return Color(
        red = ((colorLong shr 16) and 0xFF) / 255f,
        green = ((colorLong shr 8) and 0xFF) / 255f,
        blue = (colorLong and 0xFF) / 255f
    )
}
