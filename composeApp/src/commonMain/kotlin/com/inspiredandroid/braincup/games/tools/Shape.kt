package com.inspiredandroid.braincup.games.tools

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class Shape(
    val displayName: String,
    val paths: ImmutableList<Pair<Float, Float>>,
) {
    SQUARE(displayName = "square", paths = squarePath),
    TRIANGLE(displayName = "triangle", paths = trianglePath),
    CIRCLE(displayName = "circle", paths = circlePath),
    HEART(displayName = "heart", paths = heartPath),
    STAR(displayName = "star", paths = starPath),
    T(displayName = "T shape", paths = tPath),
    L(displayName = "L shape", paths = lPath),
    DIAMOND(displayName = "diamond", paths = diamondPath),
    HOUSE(displayName = "house", paths = housePath),
    ABSTRACT_TRIANGLE(displayName = "triangle", paths = abstractTrianglePath),
    ARROW(displayName = "arrow", paths = arrowPath),
}

private val arrowPath by lazy {
    persistentListOf(
        0.3f to 1f,
        0.3f to 0.45f,
        0f to 0.45f,
        0.5f to 0f,
        1f to 0.45f,
        0.7f to 0.45f,
        0.7f to 1f,
    )
}

private val abstractTrianglePath by lazy {
    persistentListOf(0f to 1f, 0f to 0.75f, 0.75f to 0f, 1f to 0f, 1f to 1f)
}

private val housePath by lazy {
    persistentListOf(0f to 1f, 0f to 0.5f, 0.5f to 0f, 1f to 0.5f, 1f to 1f)
}

private val diamondPath by lazy {
    persistentListOf(0f to 1f, 0f to 0.5f, 0.5f to 0f, 1f to 0f, 1f to 0.5f, 0.5f to 1f)
}

private val lPath by lazy {
    persistentListOf(0f to 1f, 0f to 0.5f, 0.5f to 0.5f, 0.5f to 0f, 1f to 0f, 1f to 1f)
}

private val tPath by lazy {
    persistentListOf(
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

private val squarePath by lazy {
    persistentListOf(0f to 0f, 1f to 0f, 1f to 1f, 0f to 1f)
}

private val trianglePath by lazy {
    persistentListOf(0.5f to 0f, 1f to 1f, 0f to 1f)
}

private val circlePath by lazy {
    val path = mutableListOf<Pair<Float, Float>>()
    for (angle in 0 until 360 step 9) {
        val degree = angle * PI / 180
        val x = cos(degree) * 0.5 + 0.5
        val y = sin(degree) * 0.5 + 0.5
        path.add(Pair(x.toFloat(), y.toFloat()))
    }
    path.toImmutableList()
}

private val starPath by lazy {
    persistentListOf(
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

private val heartPath by lazy {
    persistentListOf(
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
    )
}
