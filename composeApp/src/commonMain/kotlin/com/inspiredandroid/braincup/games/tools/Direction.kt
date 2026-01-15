package com.inspiredandroid.braincup.games.tools

enum class Direction {
    UP,
    RIGHT,
    DOWN,
    LEFT
}

fun Direction.getName(): String {
    return when (this) {
        Direction.UP -> "\u2191"
        Direction.RIGHT -> "\u2192"
        Direction.DOWN -> "\u2193"
        Direction.LEFT -> "\u2190"
    }
}

fun Direction.getFigure(): Figure {
    return when (this) {
        Direction.UP -> Figure(Shape.ARROW, Color.ORANGE)
        Direction.RIGHT -> Figure(Shape.ARROW, Color.ORANGE, 90)
        Direction.DOWN -> Figure(Shape.ARROW, Color.ORANGE, 180)
        Direction.LEFT -> Figure(Shape.ARROW, Color.ORANGE, 270)
    }
}