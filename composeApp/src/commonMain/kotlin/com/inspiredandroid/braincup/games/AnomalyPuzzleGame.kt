package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.Shape
import com.inspiredandroid.braincup.games.tools.getName

/**
 * Generate 6,9 or 16 figures with one outstanding figure that doesn't match the other shapes and colors.
 *
 * Logic:
 * Basic shapes: Square, Triangle, Circle, Heart, Star
 * Basic colors: Green, Blue, Purple, Red, Yellow
 *
 * Puzzle types
 * RANDOM_COLOR_AND_SHAPE: Just random colors and shapes
 * SAME_COLOR: Figures have the same basic color but unique basic shapes
 * SAME_SHAPE: Figures have the same basic shape but unique basic colors
 * SAME_SHAPE_MAX_COLOR: Figures have the same basic shape but unique extended colors
 * TRIANGLE_ROTATION: All figures are in the same color and triangle shaped with a rotation of 0/90/180/270 degrees
 *
 * Figure pairs don't repeat except if no unique shapes or colors are available.
 *
 * Number of figures
 * Round 1-2: 6 figures
 * Round 3-8: 9 figures
 * Round 9+: 16 figures
 */
class AnomalyPuzzleGame : Game() {
    enum class Puzzle {
        RANDOM_COLOR_AND_SHAPE,
        SAME_COLOR,
        SAME_SHAPE,
        SAME_SHAPE_MAX_COLOR,
        TRIANGLE_ROTATION,
        RECTANGLE_VARIATION,
        L_ROTATION,
    }

    val figures = mutableListOf<Figure>()
    var resultIndex = 0
    private var puzzleType = Puzzle.RANDOM_COLOR_AND_SHAPE
    private val puzzleQueue = mutableListOf<Puzzle>()

    private val basicShapes =
        listOf(Shape.SQUARE, Shape.TRIANGLE, Shape.CIRCLE, Shape.HEART, Shape.STAR)
    private val basicColors = listOf(Color.GREEN, Color.BLUE, Color.PURPLE, Color.RED, Color.YELLOW)
    private val fallbackPuzzles by lazy {
        listOf(
            Puzzle.TRIANGLE_ROTATION,
            Puzzle.SAME_SHAPE_MAX_COLOR,
            Puzzle.RECTANGLE_VARIATION,
            Puzzle.L_ROTATION,
        ).shuffled()
    }

    init {
        initPuzzleQueue()
    }

    override fun isCorrect(input: String): Boolean = (resultIndex + 1).toString() == input

    override fun nextRound() {
        puzzleType = getCurrentPuzzleType()
        generateFigures(puzzleType)
    }

    private fun initPuzzleQueue() {
        puzzleQueue.addAll(
            mutableListOf(
                Puzzle.RANDOM_COLOR_AND_SHAPE,
                Puzzle.SAME_COLOR,
                Puzzle.SAME_SHAPE,
            ).shuffled(),
        )
        puzzleQueue.add(Puzzle.TRIANGLE_ROTATION)
        puzzleQueue.addAll(
            mutableListOf(
                Puzzle.RANDOM_COLOR_AND_SHAPE,
                Puzzle.SAME_COLOR,
                Puzzle.SAME_SHAPE,
            ).shuffled(),
        )
        puzzleQueue.addAll(
            mutableListOf(
                Puzzle.TRIANGLE_ROTATION,
                Puzzle.RECTANGLE_VARIATION,
                Puzzle.SAME_SHAPE_MAX_COLOR,
            ).shuffled(),
        )
        puzzleQueue.addAll(
            mutableListOf(
                Puzzle.TRIANGLE_ROTATION,
                Puzzle.SAME_SHAPE_MAX_COLOR,
                Puzzle.RECTANGLE_VARIATION,
            ).shuffled(),
        )
        puzzleQueue.add(Puzzle.L_ROTATION)
    }

    private fun getCurrentPuzzleType(): Puzzle {
        if (puzzleQueue.isEmpty()) {
            puzzleQueue.addAll(fallbackPuzzles)
        }
        return puzzleQueue.removeAt(0)
    }

    private fun generateFigures(type: Puzzle) {
        figures.clear()

        val maxFigures =
            when {
                round < 2 -> 6
                round < 8 -> 9
                else -> 16
            }

        val outstandingFigure =
            Figure(
                basicShapes.random(),
                basicColors.random(),
            )
        when (type) {
            Puzzle.RANDOM_COLOR_AND_SHAPE -> randomColorAndShapeRound(outstandingFigure, maxFigures)
            Puzzle.SAME_COLOR -> sameColorRound(outstandingFigure, maxFigures, basicShapes)
            Puzzle.SAME_SHAPE -> sameShapeRound(outstandingFigure, maxFigures, basicColors)
            Puzzle.SAME_SHAPE_MAX_COLOR -> sameShapeMaxColorRound(outstandingFigure, maxFigures)
            Puzzle.TRIANGLE_ROTATION ->
                sameShapeRotationRound(
                    Shape.TRIANGLE,
                    outstandingFigure,
                    maxFigures,
                )
            Puzzle.RECTANGLE_VARIATION -> rectangleVariationRound(outstandingFigure, maxFigures)
            Puzzle.L_ROTATION ->
                sameShapeRotationRound(
                    Shape.L,
                    outstandingFigure,
                    maxFigures,
                )
        }
        figures.add(outstandingFigure)
        figures.shuffle()

        resultIndex = figures.indexOf(outstandingFigure)
    }

    private fun sameShapeMaxColorRound(
        outstandingFigure: Figure,
        maxFigures: Int,
    ) {
        val allColors = basicColors + Color.ROSA + Color.TURQUOISE + Color.ORANGE
        outstandingFigure.color = allColors.random()
        sameShapeRound(outstandingFigure, maxFigures, allColors)
    }

    private fun rectangleVariationRound(
        outstandingFigure: Figure,
        maxFigures: Int,
    ) {
        val rectangleShapes =
            listOf(
                Shape.T,
                Shape.L,
                Shape.DIAMOND,
                Shape.HOUSE,
                Shape.ABSTRACT_TRIANGLE,
            )
        outstandingFigure.shape = rectangleShapes.random()
        sameColorRound(outstandingFigure, maxFigures, rectangleShapes)
    }

    private fun sameShapeRotationRound(
        shape: Shape,
        outstandingFigure: Figure,
        maxFigures: Int,
    ) {
        val rotations = mutableListOf(0, 90, 180, 270)
        outstandingFigure.shape = shape
        outstandingFigure.rotation = rotations.random()
        rotations.remove(outstandingFigure.rotation)
        val color = outstandingFigure.color
        while (figures.size < maxFigures - 2) {
            val rotation =
                if (rotations.isEmpty()) {
                    figures.random().rotation
                } else {
                    rotations.random()
                }
            rotations.remove(rotation)
            figures.add(
                Figure(
                    shape,
                    color,
                    rotation,
                ),
            )
            figures.add(
                Figure(
                    shape,
                    color,
                    rotation,
                ),
            )
            if (figures.size == maxFigures - 2) {
                figures.add(
                    Figure(
                        shape,
                        color,
                        rotation,
                    ),
                )
            }
        }
    }

    private fun sameShapeRound(
        outstandingFigure: Figure,
        maxFigures: Int,
        colors: List<Color>,
    ) {
        val availableColors =
            colors
                .filter {
                    outstandingFigure.color != it
                }.toMutableList()
        while (figures.size < maxFigures - 2) {
            val shape = outstandingFigure.shape
            val color =
                if (availableColors.isNotEmpty()) {
                    availableColors.removeAt(0)
                } else {
                    figures.random().color
                }
            figures.add(
                Figure(
                    shape,
                    color,
                ),
            )
            figures.add(
                Figure(
                    shape,
                    color,
                ),
            )
            if (figures.size == maxFigures - 2) {
                figures.add(
                    Figure(
                        shape,
                        color,
                    ),
                )
            }
        }
    }

    private fun sameColorRound(
        outstandingFigure: Figure,
        maxFigures: Int,
        shapes: List<Shape>,
    ) {
        val availableShapes =
            shapes
                .filter {
                    outstandingFigure.shape != it
                }.toMutableList()
        while (figures.size < maxFigures - 2) {
            val shape =
                if (availableShapes.isNotEmpty()) {
                    availableShapes.removeAt(0)
                } else {
                    figures.random().shape
                }
            val color = outstandingFigure.color
            figures.add(
                Figure(
                    shape,
                    color,
                ),
            )
            figures.add(
                Figure(
                    shape,
                    color,
                ),
            )
            if (figures.size == maxFigures - 2) {
                figures.add(
                    Figure(
                        shape,
                        color,
                    ),
                )
            }
        }
    }

    private fun randomColorAndShapeRound(
        outstandingFigure: Figure,
        maxFigures: Int,
    ) {
        val availableShapes =
            basicShapes.filter {
                outstandingFigure.shape != it
            }
        val availableColors =
            basicColors.filter {
                outstandingFigure.color != it
            }
        while (figures.size < maxFigures - 2) {
            val shape = availableShapes.random()
            val color = availableColors.random()
            if (figures.count { it.color == color && it.shape == shape } == 0 ||
                figures.size == (availableColors.size * availableShapes.size) * 2
            ) {
                figures.add(
                    Figure(
                        shape,
                        color,
                    ),
                )
                figures.add(
                    Figure(
                        shape,
                        color,
                    ),
                )
                if (figures.size == maxFigures - 2) {
                    figures.add(
                        Figure(
                            shape,
                            color,
                        ),
                    )
                }
            }
        }
    }

    override fun solution(): String {
        val figure = figures[resultIndex]
        val extraInfo =
            if (puzzleType == Puzzle.TRIANGLE_ROTATION || puzzleType == Puzzle.L_ROTATION) {
                "pointing ${figure.getRotationString()}"
            } else {
                ""
            }
        return "${figure.color.getName()} ${figure.shape.getName()} $extraInfo".trim()
    }

    override fun hint(): String? = null

    override fun getGameType(): GameType = GameType.ANOMALY_PUZZLE
}
