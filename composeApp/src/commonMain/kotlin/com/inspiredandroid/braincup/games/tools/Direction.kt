package com.inspiredandroid.braincup.games.tools

enum class Direction(
    val figure: Figure,
) {
    UP(figure = Figure(Shape.ARROW, GameColor.ORANGE)),
    RIGHT(figure = Figure(Shape.ARROW, GameColor.ORANGE, 90)),
    DOWN(figure = Figure(Shape.ARROW, GameColor.ORANGE, 180)),
    LEFT(figure = Figure(Shape.ARROW, GameColor.ORANGE, 270)),
}
