package com.inspiredandroid.braincup.games.tools

class Figure(
    var shape: Shape,
    var color: Color,
    var rotation: Int = 0,
) {
    fun getRotationString(): String = when (rotation) {
        0 -> "up"
        90 -> "right"
        180 -> "down"
        270 -> "left"
        else -> ""
    }
}
