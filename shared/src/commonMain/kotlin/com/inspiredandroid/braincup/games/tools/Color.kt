package com.inspiredandroid.braincup.games.tools

enum class Color {
    RED,
    GREEN,
    BLUE,
    PURPLE,
    YELLOW,
    ORANGE,
    TURKIES,
    ROSA
}

fun Color.getName(): String {
    return when (this) {
        Color.RED -> "red"
        Color.GREEN -> "green"
        Color.BLUE -> "blue"
        Color.PURPLE -> "purple"
        Color.YELLOW -> "yellow"
        Color.ORANGE -> "orange"
        Color.TURKIES -> "turkies"
        Color.ROSA -> "rosa"
    }
}

fun Color.getHex(): String {
    return when (this) {
        Color.RED -> "#e74c3c"
        Color.GREEN -> "#2ecc71"
        Color.BLUE -> "#3498db"
        Color.PURPLE -> "#9b59b6"
        Color.YELLOW -> "#f1c40f"
        Color.ORANGE -> "#e67e22"
        Color.TURKIES -> "#12CBC4"
        Color.ROSA -> "#FDA7DF"
    }
}