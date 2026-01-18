package com.inspiredandroid.braincup.games.tools

enum class Direction(
    val figure: Figure,
) {
    UP(figure = Figure(Shape.ARROW, Color.ORANGE)),
    RIGHT(figure = Figure(Shape.ARROW, Color.ORANGE, 90)),
    DOWN(figure = Figure(Shape.ARROW, Color.ORANGE, 180)),
    LEFT(figure = Figure(Shape.ARROW, Color.ORANGE, 270)),
}
