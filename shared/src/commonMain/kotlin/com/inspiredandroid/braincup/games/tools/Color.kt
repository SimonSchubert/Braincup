package com.inspiredandroid.braincup.games.tools

enum class Color {
    RED,
    GREEN,
    BLUE,
    PURPLE
}

fun Color.getName(): String {
    return when (this) {
        Color.RED -> "red"
        Color.GREEN -> "green"
        Color.BLUE -> "blue"
        Color.PURPLE -> "purple"
    }
}

fun Color.getHex(): String {
    return when (this) {
        Color.RED -> "#e74c3c"
        Color.GREEN -> "#2ecc71"
        Color.BLUE -> "#3498db"
        Color.PURPLE -> "#9b59b6"
    }
}