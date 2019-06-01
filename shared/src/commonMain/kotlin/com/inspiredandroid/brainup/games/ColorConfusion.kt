package com.inspiredandroid.brainup

import kotlin.random.Random

class ColorConfusion {

    data class  Round(val answerColor: Color, val displayedColor: Color, val stringColor: Color, val answerShape: Shape, val displayedShape: Shape) {

        val shapePoints: Int = Random.nextInt(2,7)
        val colorPoints: Int

        init {
            colorPoints = Random.nextInt(2,10 - shapePoints)
        }

        fun isCorrect(value: String): Boolean {
            return points() == value
        }

        fun points(): String {
            var points = 0
            if(answerColor == displayedColor) {
                points += colorPoints
            }
            if(answerShape == displayedShape) {
                points += shapePoints
            }
            return points.toString()
        }
    }

    fun nextRound(): Round {
        return Round(colors.random(), colors.random(), colors.random(), shapes.random(), shapes.random())
    }

    companion object {
        val colors = listOf(Color.RED, Color.GREEN, Color.BLUE, Color.PURPLE)
        val shapes = listOf(Shape.SQUARE, Shape.TRIANGLE, Shape.CIRCLE, Shape.HEART)
    }
}