package com.inspiredandroid.braincup.games.tools

import androidx.compose.runtime.Immutable

@Immutable
data class Figure(
    val shape: Shape,
    val color: Color,
    val rotation: Int = 0,
) {
    fun getRotationString(): String = when (rotation) {
        0 -> "up"
        90 -> "right"
        180 -> "down"
        270 -> "left"
        else -> ""
    }
}
