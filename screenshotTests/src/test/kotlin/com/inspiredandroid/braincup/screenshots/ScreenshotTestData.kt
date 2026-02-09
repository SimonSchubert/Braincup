package com.inspiredandroid.braincup.screenshots

import com.inspiredandroid.braincup.games.*
import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.games.tools.Direction
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.Shape

val mainMenuHighscores = mapOf(
    "6" to 12,
    "0" to 9,
    "1" to 7,
    "2" to 4,
    "3" to 5,
    "4" to 6,
    "5" to 8,
    "8" to 10,
    "9" to 3,
    "10" to 5,
)

fun createColorConfusionGame(): ColorConfusionGame {
    val game = ColorConfusionGame()
    game.displayedShape = Shape.HEART
    game.displayedColor = Color.RED
    game.answerShape = Shape.HEART
    game.answerColor = Color.BLUE
    game.stringColor = Color.GREEN
    game.shapePoints = 3
    game.colorPoints = 4
    game.possibleAnswers = listOf("0", "3", "4", "7")
    return game
}

fun createAnomalyPuzzleGame(): AnomalyPuzzleGame {
    val game = AnomalyPuzzleGame()
    game.figures.clear()
    game.figures.addAll(
        listOf(
            Figure(Shape.STAR, Color.RED),
            Figure(Shape.STAR, Color.GREEN),
            Figure(Shape.STAR, Color.BLUE),
            Figure(Shape.STAR, Color.PURPLE),
            Figure(Shape.STAR, Color.YELLOW),
            Figure(Shape.STAR, Color.RED),
            Figure(Shape.STAR, Color.GREEN),
            Figure(Shape.STAR, Color.PURPLE),
            Figure(Shape.STAR, Color.BLUE),
        ),
    )
    game.resultIndex = 7
    return game
}

fun createMentalCalculationGame(): MentalCalculationGame {
    val game = MentalCalculationGame()
    game.calculation = "8 + 15"
    return game
}

fun createSherlockCalculationGame(): SherlockCalculationGame {
    val game = SherlockCalculationGame()
    game.result = 26
    game.numbers.clear()
    game.numbers.addAll(listOf(4, 9, 3, 7, 2))
    return game
}

fun createChainCalculationGame(): ChainCalculationGame {
    val game = ChainCalculationGame()
    game.nextRound()
    game.round++
    return game
}

fun createFractionCalculationGame(): FractionCalculationGame {
    val game = FractionCalculationGame()
    game.nextRound()
    game.round++
    return game
}

fun createValueComparisonGame(): ValueComparisonGame {
    val game = ValueComparisonGame()
    game.answers.clear()
    game.answers.addAll(listOf("3+8", "5+4"))
    return game
}

fun createPathFinderGame(): PathFinderGame {
    val game = PathFinderGame()
    game.directions.clear()
    game.directions.addAll(
        listOf(Direction.RIGHT, Direction.RIGHT, Direction.DOWN, Direction.LEFT, Direction.DOWN),
    )
    game.startX = 0
    game.currentX = 1
    game.currentY = 2
    return game
}

fun createGridSolverGame(): GridSolverGame {
    val game = GridSolverGame()
    game.entries.clear()
    game.entries.add(mutableListOf(3, 5))
    game.entries.add(mutableListOf(4, 2))
    game.resultsX.clear()
    game.resultsX.addAll(listOf(7, 7))
    game.resultsY.clear()
    game.resultsY.addAll(listOf(8, 6))
    return game
}

fun createVisualMemoryGame(): VisualMemoryGame {
    val game = VisualMemoryGame()
    repeat(4) {
        game.nextRound()
        game.round++
    }
    return game
}
