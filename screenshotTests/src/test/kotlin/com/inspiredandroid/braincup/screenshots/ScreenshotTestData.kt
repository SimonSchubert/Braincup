package com.inspiredandroid.braincup.screenshots

import com.inspiredandroid.braincup.app.GameUiState
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
    "11" to 6,
    "12" to 4,
    "13" to 7,
)

fun createColoredShapesGame(): ColoredShapesGame {
    val game = ColoredShapesGame()
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
    return game
}

fun createFractionCalculationGame(): FractionCalculationGame {
    val game = FractionCalculationGame()
    game.nextRound()
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

fun createPatternSequenceGame(): PatternSequenceGame {
    val game = PatternSequenceGame()
    game.sequence.clear()
    game.sequence.addAll(
        listOf(
            Figure(Shape.CIRCLE, Color.RED),
            Figure(Shape.SQUARE, Color.RED),
            Figure(Shape.CIRCLE, Color.RED),
            Figure(Shape.SQUARE, Color.RED),
        ),
    )
    game.options.clear()
    game.options.addAll(
        listOf(
            Figure(Shape.SQUARE, Color.RED),
            Figure(Shape.CIRCLE, Color.RED),
            Figure(Shape.TRIANGLE, Color.RED),
            Figure(Shape.CIRCLE, Color.BLUE),
        ),
    )
    game.correctOptionIndex = 1
    return game
}

fun createColorConfusionGame(): ColorConfusionGame {
    val game = ColorConfusionGame()
    game.cells = listOf(
        ColorConfusionGame.Cell(word = Color.RED, fontColor = Color.RED),
        ColorConfusionGame.Cell(word = Color.BLUE, fontColor = Color.GREEN),
        ColorConfusionGame.Cell(word = Color.GREEN, fontColor = Color.GREEN),
        ColorConfusionGame.Cell(word = Color.PURPLE, fontColor = Color.YELLOW),
        ColorConfusionGame.Cell(word = Color.ORANGE, fontColor = Color.ORANGE),
        ColorConfusionGame.Cell(word = Color.YELLOW, fontColor = Color.BLUE),
        ColorConfusionGame.Cell(word = Color.RED, fontColor = Color.PURPLE),
        ColorConfusionGame.Cell(word = Color.BLUE, fontColor = Color.BLUE),
        ColorConfusionGame.Cell(word = Color.GREEN, fontColor = Color.RED),
    )
    game.selectedIndices = mutableSetOf(0, 2, 4)
    game.feedbackState = List(9) { ColorConfusionGame.CellFeedback.NONE }
    game.isSubmitted = false
    return game
}

fun createGhostGridGame(): GhostGridGame {
    val game = GhostGridGame()
    game.nextRound()
    return game
}

fun createVisualMemoryGame(): VisualMemoryGame {
    val game = VisualMemoryGame()
    repeat(4) {
        game.nextRound()
    }
    return game
}

fun createColoredShapesUiState(): GameUiState = createColoredShapesGame().toUiState()
fun createAnomalyPuzzleUiState(): GameUiState = createAnomalyPuzzleGame().toUiState()
fun createMentalCalculationUiState(): GameUiState = createMentalCalculationGame().toUiState()
fun createSherlockCalculationUiState(): GameUiState = createSherlockCalculationGame().toUiState()
fun createChainCalculationUiState(): GameUiState = createChainCalculationGame().toUiState()
fun createFractionCalculationUiState(): GameUiState = createFractionCalculationGame().toUiState()
fun createValueComparisonUiState(): GameUiState = createValueComparisonGame().toUiState()
fun createPathFinderUiState(): GameUiState = createPathFinderGame().toUiState()
fun createGridSolverUiState(): GameUiState = createGridSolverGame().toUiState()
fun createPatternSequenceUiState(): GameUiState = createPatternSequenceGame().toUiState()

fun createColorConfusionUiState(): GameUiState = createColorConfusionGame().toUiState()

fun createGhostGridUiState(): com.inspiredandroid.braincup.app.GhostGridUiState {
    val game = createGhostGridGame()
    // Set phase to ANSWERING by simulating the show sequence completing
    // We create a game in round 1 (sequence of 3 on 4x4) and show it in answering state
    return game.toUiState().copy(
        phase = GhostGridGame.Phase.ANSWERING,
    )
}

fun createGhostGridGameOverUiState(): com.inspiredandroid.braincup.app.GhostGridUiState {
    val game = GhostGridGame()
    game.nextRound()
    // Submit a wrong answer to trigger game over
    game.submitAnswer("-1")
    return game.toUiState()
}

fun createVisualMemoryUiState(): com.inspiredandroid.braincup.app.VisualMemoryUiState {
    val game = createVisualMemoryGame()
    game.countdown = 3
    return game.toUiState()
}

fun createVisualMemoryGameOverUiState(): com.inspiredandroid.braincup.app.VisualMemoryUiState {
    val game = createVisualMemoryGame()
    game.startAnswerPhase()
    game.submitAnswer("-1") // triggers GAME_OVER
    return game.toUiState()
}
