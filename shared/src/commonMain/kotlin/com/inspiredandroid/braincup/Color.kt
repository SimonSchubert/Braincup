package com.inspiredandroid.braincup

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