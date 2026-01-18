package com.inspiredandroid.braincup.games.tools

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class Shape {
    SQUARE,
    TRIANGLE,
    CIRCLE,
    HEART,
    STAR,
    T,
    L,
    DIAMOND,
    HOUSE,
    ABSTRACT_TRIANGLE,
    ARROW,
}

fun Shape.getName(): String = when (this) {
    Shape.SQUARE -> "square"
    Shape.TRIANGLE -> "triangle"
    Shape.CIRCLE -> "circle"
    Shape.HEART -> "heart"
    Shape.STAR -> "star"
    Shape.T -> "T shape"
    Shape.L -> "L shape"
    Shape.DIAMOND -> "diamond"
    Shape.HOUSE -> "house"
    Shape.ABSTRACT_TRIANGLE -> "triangle"
    Shape.ARROW -> "arrow"
}

fun Shape.getPaths(): List<Pair<Float, Float>> = when (this) {
    Shape.SQUARE -> squarePath
    Shape.TRIANGLE -> trianglePath
    Shape.HEART -> heartPath
    Shape.STAR -> starPath
    Shape.CIRCLE -> circlePath
    Shape.T -> tPath
    Shape.L -> lPath
    Shape.DIAMOND -> diamondPath
    Shape.HOUSE -> housePath
    Shape.ABSTRACT_TRIANGLE -> abstractTrianglePath
    Shape.ARROW -> arrowPath
}

val arrowPath by lazy {
    listOf(
        0.3f to 1f,
        0.3f to 0.45f,
        0f to 0.45f,
        0.5f to 0f,
        1f to 0.45f,
        0.7f to 0.45f,
        0.7f to 1f,
    )
}

val abstractTrianglePath by lazy {
    listOf(0f to 1f, 0f to 0.75f, 0.75f to 0f, 1f to 0f, 1f to 1f)
}

val housePath by lazy {
    listOf(0f to 1f, 0f to 0.5f, 0.5f to 0f, 1f to 0.5f, 1f to 1f)
}

val diamondPath by lazy {
    listOf(0f to 1f, 0f to 0.5f, 0.5f to 0f, 1f to 0f, 1f to 0.5f, 0.5f to 1f)
}

val lPath by lazy {
    listOf(0f to 1f, 0f to 0.5f, 0.5f to 0.5f, 0.5f to 0f, 1f to 0f, 1f to 1f)
}

val tPath by lazy {
    listOf(
        0f to 1f,
        0f to 0.5f,
        0.25f to 0.5f,
        0.25f to 0f,
        0.75f to 0f,
        0.75f to 0.5f,
        1f to 0.5f,
        1f to 1f,
    )
}

val squarePath by lazy {
    listOf(0f to 0f, 1f to 0f, 1f to 1f, 0f to 1f)
}

val trianglePath by lazy {
    listOf(0.5f to 0f, 1f to 1f, 0f to 1f, 0.5f to 0f)
}

val circlePath by lazy {
    val path = mutableListOf<Pair<Float, Float>>()
    for (angle in 0..360 step 9) {
        val degree = angle * PI / 180
        val x = cos(degree) * 0.5 + 0.5
        val y = sin(degree) * 0.5 + 0.5
        path.add(Pair(x.toFloat(), y.toFloat()))
    }
    path
}

val starPath by lazy {
    listOf(
        0.5f to 0f,
        0.65f to 0.32f,
        1f to 0.38f,
        0.75f to 0.62f,
        0.82f to 1f,
        0.5f to 0.82f,
        0.18f to 1f,
        0.25f to 0.62f,
        0f to 0.38f,
        0.35f to 0.32f,
    )
}

val heartPath by lazy {
    listOf(
        0.5f to 0.135f,
        0.4f to 0.035f,
        0.25f to 0f,
        0.11f to 0.05f,
        0.015f to 0.2f,
        0f to 0.41f,
        0.08f to 0.62f,
        0.21f to 0.79f,
        0.5f to 1f,
        0.79f to 0.79f,
        0.92f to 0.62f,
        1f to 0.41f,
        0.985f to 0.2f,
        0.89f to 0.05f,
        0.75f to 0f,
        0.6f to 0.035f,
        0.5f to 0.135f,
    )
}
