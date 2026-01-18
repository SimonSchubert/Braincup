package com.inspiredandroid.braincup.games.tools

enum class Direction {
    UP,
    RIGHT,
    DOWN,
    LEFT,
}

fun Direction.getFigure(): Figure = when (this) {
    Direction.UP -> Figure(Shape.ARROW, Color.ORANGE)
    Direction.RIGHT -> Figure(Shape.ARROW, Color.ORANGE, 90)
    Direction.DOWN -> Figure(Shape.ARROW, Color.ORANGE, 180)
    Direction.LEFT -> Figure(Shape.ARROW, Color.ORANGE, 270)
}
