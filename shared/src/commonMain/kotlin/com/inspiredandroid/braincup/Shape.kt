package com.inspiredandroid.braincup

enum class Shape {
    SQUARE,
    TRIANGLE,
    CIRCLE,
    HEART
}

fun Shape.getName(): String {
    return when (this) {
        Shape.SQUARE -> "square"
        Shape.TRIANGLE -> "triangle"
        Shape.CIRCLE -> "circle"
        Shape.HEART -> "heart"
    }
}