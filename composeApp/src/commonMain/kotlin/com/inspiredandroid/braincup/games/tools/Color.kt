package com.inspiredandroid.braincup.games.tools

import androidx.compose.ui.graphics.Color as ComposeColor

enum class Color(
    val displayName: String,
    val composeColor: ComposeColor,
) {
    RED(displayName = "red", composeColor = ComposeColor(0xFFE74C3C)),
    GREEN(displayName = "green", composeColor = ComposeColor(0xFF2ECC71)),
    BLUE(displayName = "blue", composeColor = ComposeColor(0xFF3498DB)),
    PURPLE(displayName = "purple", composeColor = ComposeColor(0xFF9B59B6)),
    YELLOW(displayName = "yellow", composeColor = ComposeColor(0xFFF1C40F)),
    ORANGE(displayName = "orange", composeColor = ComposeColor(0xFFE67E22)),
    TURQUOISE(displayName = "turquoise", composeColor = ComposeColor(0xFF12CBC4)),
    ROSA(displayName = "rosa", composeColor = ComposeColor(0xFFFDA7DF)),
    GREY_LIGHT(displayName = "light grey", composeColor = ComposeColor(0xFF565656)),
}
