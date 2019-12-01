package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.games.tools.Shape
import kotlin.random.Random

class ColorConfusionGame : Game() {

    lateinit var answerColor: Color
    lateinit var displayedColor: Color
    lateinit var stringColor: Color
    lateinit var answerShape: Shape
    lateinit var displayedShape: Shape
    var colorPoints = 0
    var shapePoints = 0

    override fun isCorrect(input: String): Boolean {
        return points() == input
    }

    /**
     * 50/50 chance that the shape is correct
     * 50/50 chance that the color is correct
     */
    override fun nextRound() {
        answerColor = colors.random()
        displayedColor = if (Random.nextBoolean()) {
            colors.filter { it != answerColor }.random()
        } else {
            answerColor
        }
        stringColor = colors.random()
        answerShape = shapes.random()
        displayedShape = if (Random.nextBoolean()) {
            shapes.filter { it != answerShape }.random()
        } else {
            answerShape
        }
        shapePoints = Random.nextInt(2, 5)
        colorPoints = Random.nextInt(2, 5)
        if(shapePoints == colorPoints) {
            shapePoints++
        }
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

    override fun solution(): String {
        return points()
    }

    override fun getGameType(): GameType {
        return GameType.COLOR_CONFUSION
    }

    companion object {
        val colors = listOf(Color.RED, Color.GREEN, Color.BLUE, Color.PURPLE)
        val shapes = listOf(Shape.SQUARE, Shape.TRIANGLE, Shape.CIRCLE, Shape.HEART)
    }
}