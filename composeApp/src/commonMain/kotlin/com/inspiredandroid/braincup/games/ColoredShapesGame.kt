package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.games.tools.Shape
import kotlin.random.Random

/**
 * Generates each round a shape and color with statements that are randomly true or false.
 *
 * Logic:
 * 1. Get a random shape(displayedShape) and color(displayedColor) which will be actually shown
 * 2. Get another random shape(answerShape) and color(answerColor) with a 50% chance of being identical to the shown shape and color
 * 3. Get a third random color(stringColor) which will be the color of the previous generated color string
 */
class ColoredShapesGame : Game() {
    lateinit var displayedColor: Color
    lateinit var answerColor: Color
    lateinit var stringColor: Color
    lateinit var displayedShape: Shape
    lateinit var answerShape: Shape
    var colorPoints = 0
    var shapePoints = 0
    var possibleAnswers: List<String> = emptyList()

    private val colors = listOf(Color.RED, Color.GREEN, Color.BLUE, Color.PURPLE)
    private val shapes = listOf(Shape.SQUARE, Shape.TRIANGLE, Shape.CIRCLE, Shape.HEART)

    override fun isCorrect(input: String): Boolean = points() == input

    override fun generateRound() {
        displayedShape = shapes.random()
        answerShape =
            if (Random.nextBoolean()) {
                shapes.filter { it != displayedShape }.random()
            } else {
                displayedShape
            }
        displayedColor = colors.random()
        answerColor =
            if (Random.nextBoolean()) {
                colors.filter { it != displayedColor }.random()
            } else {
                displayedColor
            }
        stringColor = colors.random()
        shapePoints = Random.nextInt(2, 5)
        colorPoints = Random.nextInt(2, 5)
        if (shapePoints == colorPoints) {
            shapePoints++
        }
        possibleAnswers = listOf(
            0,
            shapePoints,
            colorPoints,
            shapePoints + colorPoints,
        ).shuffled().map { it.toString() }
    }

    fun points(): String {
        var points = 0
        if (answerColor == displayedColor) {
            points += colorPoints
        }
        if (answerShape == displayedShape) {
            points += shapePoints
        }
        return points.toString()
    }

    override fun solution(): String = points()

    override fun hint(): String? = null

    override fun toUiState() = com.inspiredandroid.braincup.app.ColoredShapesUiState(
        displayedFigure = com.inspiredandroid.braincup.games.tools.Figure(displayedShape, displayedColor),
        answerShape = answerShape,
        answerColor = answerColor,
        stringColor = stringColor,
        shapePoints = shapePoints,
        colorPoints = colorPoints,
        possibleAnswers = possibleAnswers.map { com.inspiredandroid.braincup.app.AnswerButton(it) },
    )
}
