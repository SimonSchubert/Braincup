package com.inspiredandroid.braincup.games.tools

enum class Color {
    RED,
    GREEN,
    BLUE,
    PURPLE,
    YELLOW,
    ORANGE,
    TURQUOISE,
    ROSA,
    GREY_DARK,
    GREY_LIGHT,
}

fun Color.getName(): String = when (this) {
    Color.RED -> "red"
    Color.GREEN -> "green"
    Color.BLUE -> "blue"
    Color.PURPLE -> "purple"
    Color.YELLOW -> "yellow"
    Color.ORANGE -> "orange"
    Color.TURQUOISE -> "turquoise"
    Color.ROSA -> "rosa"
    Color.GREY_DARK -> "dark grey"
    Color.GREY_LIGHT -> "light grey"
}

fun Color.getHex(): String = when (this) {
    Color.RED -> "#e74c3c"
    Color.GREEN -> "#2ecc71"
    Color.BLUE -> "#3498db"
    Color.PURPLE -> "#9b59b6"
    Color.YELLOW -> "#f1c40f"
    Color.ORANGE -> "#e67e22"
    Color.TURQUOISE -> "#12CBC4"
    Color.ROSA -> "#FDA7DF"
    Color.GREY_DARK -> "#262626"
    Color.GREY_LIGHT -> "#565656"
}
