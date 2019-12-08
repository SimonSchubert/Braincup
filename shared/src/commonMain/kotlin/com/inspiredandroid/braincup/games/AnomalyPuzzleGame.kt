package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.games.tools.Shape
import com.inspiredandroid.braincup.games.tools.getName
import kotlin.random.Random

class AnomalyPuzzleGame : Game() {

    val figures = mutableListOf<Figure>()
    var resultIndex = 0
    private var round = 0

    private val shapes = listOf(Shape.SQUARE, Shape.TRIANGLE, Shape.CIRCLE, Shape.HEART, Shape.STAR)
    private val colors = listOf(Color.GREEN, Color.BLUE, Color.PURPLE, Color.RED, Color.YELLOW)

    class Figure(var shape: Shape, val color: Color, var rotation: Int = 0)

    override fun isCorrect(input: String): Boolean {
        return (resultIndex + 1).toString() == input
    }

    override fun nextRound() {
        figures.clear()

        val outstandingFigure = Figure(shapes.random(), colors.random())

        val maxFigures = if (round < 2) {
            6
        } else {
            9
        }
        when (round) {
            0 -> uniquePairRound(outstandingFigure, maxFigures)
            1 -> monotoneRound(outstandingFigure, maxFigures)
            2 -> sameShapeRound(outstandingFigure, maxFigures)
            3 -> uniquePairRound(outstandingFigure, maxFigures)
            4 -> monotoneRound(outstandingFigure, maxFigures)
            5 -> sameShapeRound(outstandingFigure, maxFigures)
            6 -> triangleRotationRound(outstandingFigure, maxFigures)
            else -> {
                when(Random.nextInt(0, 3)) {
                    0 -> triangleRotationRound(outstandingFigure, maxFigures)
                    1 -> monotoneRound(outstandingFigure, maxFigures)
                    2 -> uniquePairRound(outstandingFigure, maxFigures)
                    else -> triangleRotationRound(outstandingFigure, maxFigures)
                }
            }
        }

        figures.add(outstandingFigure)
        figures.shuffle()
        resultIndex = figures.indexOf(outstandingFigure)
        round++
    }

    /**
     * All triangles in the same color but different rotation
     */
    private fun triangleRotationRound(outstandingFigure: Figure, maxFigures: Int) {
        outstandingFigure.shape = Shape.TRIANGLE
        outstandingFigure.rotation = Random.nextInt(0, 3) * 90
        val shape = outstandingFigure.shape
        val color = outstandingFigure.color
        while (figures.size < maxFigures - 1) {
            val rotation = Random.nextInt(0, 3) * 90
            if (outstandingFigure.rotation != rotation) {
                figures.add(Figure(shape, color, rotation))
            }
        }
    }

    /**
     * All figures have the same shape
     */
    private fun sameShapeRound(outstandingFigure: Figure, maxFigures: Int) {
        val availableColors = colors.filter {
            outstandingFigure.color != it
        }
        val availableShapes = listOf(outstandingFigure.shape)
        while (figures.size < maxFigures - 2) {
            val shape = availableShapes.random()
            val color = availableColors.random()
            if (figures.count { it.color == color } == 0) {
                figures.add(Figure(shape, color))
                figures.add(Figure(shape, color))
                if (figures.size == maxFigures - 2) {
                    figures.add(Figure(shape, color))
                }
            }
        }
    }

    /**
     * All figures have the same color
     */
    private fun monotoneRound(outstandingFigure: Figure, maxFigures: Int) {
        val availableShapes = shapes.filter {
            outstandingFigure.shape != it
        }
        val availableColors = listOf(outstandingFigure.color)
        while (figures.size < maxFigures - 2) {
            val shape = availableShapes.random()
            val color = availableColors.random()
            if (figures.count { it.shape == shape } == 0) {
                figures.add(Figure(shape, color))
                figures.add(Figure(shape, color))
                if (figures.size == maxFigures - 2) {
                    figures.add(Figure(shape, color))
                }
            }
        }
    }

    /**
     * Figures can have different shapes and colors but at least on parameter doesn't match with the outstanding
     */
    private fun uniquePairRound(outstandingFigure: Figure, maxFigures: Int) {
        val availableShapes = shapes.filter {
            outstandingFigure.shape != it
        }
        val availableColors = colors.filter {
            outstandingFigure.color != it
        }
        while (figures.size < maxFigures - 2) {
            val shape = availableShapes.random()
            val color = availableColors.random()
            if (figures.count { it.color == color && it.shape == shape } == 0) {
                figures.add(Figure(shape, color))
                figures.add(Figure(shape, color))
                if (figures.size == maxFigures - 2) {
                    figures.add(Figure(shape, color))
                }
            }
        }
    }

    override fun solution(): String {
        val figure = figures[resultIndex]
        return "${figure.color.getName()} ${figure.shape.getName()}"
    }

    override fun hint(): String? {
        return null
    }

    override fun getGameType(): GameType {
        return GameType.ANOMALY_PUZZLE
    }
}