package com.inspiredandroid.braincup.screenshots

import com.inspiredandroid.braincup.app.FlashCrowdUiState
import com.inspiredandroid.braincup.app.GameUiState
import com.inspiredandroid.braincup.app.VisualMemoryUiState
import com.inspiredandroid.braincup.app.VisualMemoryUiState.CellState
import com.inspiredandroid.braincup.app.VisualMemoryUiState.CellType
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
    "16" to 3,
    "10" to 5,
    "11" to 6,
    "12" to 4,
    "13" to 7,
    "15" to 8,
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
    game.calculation = "8-4"
    return game
}

fun createFractionCalculationGame(): FractionCalculationGame {
    val game = FractionCalculationGame()
    game.calculation = "(2/2) * (12/3)"
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

fun createMiniSudokuGame(): MiniSudokuGame {
    val game = MiniSudokuGame()
    game.solutionGrid.clear()
    game.solutionGrid.add(mutableListOf(1, 2, 3, 4))
    game.solutionGrid.add(mutableListOf(3, 4, 1, 2))
    game.solutionGrid.add(mutableListOf(2, 1, 4, 3))
    game.solutionGrid.add(mutableListOf(4, 3, 2, 1))
    game.initialValues.clear()
    game.initialValues.addAll(
        listOf(
            1, null, null, 4,
            null, 4, 1, null,
            null, 1, 4, null,
            4, null, null, 1,
        ),
    )
    return game
}

fun createPatternSequenceGame(): PatternSequenceGame {
    val game = PatternSequenceGame()
    game.sequence.clear()
    game.sequence.addAll(
        listOf(
            Figure(Shape.CIRCLE, Color.BLUE),
            Figure(Shape.HEART, Color.ROSA),
            Figure(Shape.CIRCLE, Color.BLUE),
            Figure(Shape.HEART, Color.ROSA),
        ),
    )
    game.options.clear()
    game.options.addAll(
        listOf(
            Figure(Shape.HEART, Color.BLUE),
            Figure(Shape.CIRCLE, Color.ROSA),
            Figure(Shape.HEART, Color.ROSA),
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

private val visualMemoryFigures = listOf(
    Figure(Shape.HEART, Color.PURPLE),
    Figure(Shape.SQUARE, Color.GREY_LIGHT),
    Figure(Shape.STAR, Color.BLUE),
    Figure(Shape.CIRCLE, Color.TURQUOISE),
    Figure(Shape.L, Color.ROSA),
    Figure(Shape.TRIANGLE, Color.YELLOW),
    Figure(Shape.HOUSE, Color.ORANGE),
    Figure(Shape.T, Color.GREEN),
    Figure(Shape.DIAMOND, Color.RED),
)

private val visualMemoryAnswerOptions = visualMemoryFigures.mapIndexed { index, figure ->
    VisualMemoryUiState.AnswerOption(
        figure = figure,
        figureIndex = index,
        enabled = true,
    )
}

fun createColoredShapesUiState(): GameUiState = createColoredShapesGame().toUiState()
fun createAnomalyPuzzleUiState(): GameUiState = createAnomalyPuzzleGame().toUiState()
fun createMentalCalculationUiState(): GameUiState = createMentalCalculationGame().toUiState()
fun createSherlockCalculationUiState(): GameUiState = createSherlockCalculationGame().toUiState()
fun createChainCalculationUiState(): GameUiState = createChainCalculationGame().toUiState()
fun createFractionCalculationUiState(): GameUiState = createFractionCalculationGame().toUiState()
fun createValueComparisonUiState(): GameUiState = createValueComparisonGame().toUiState()
fun createPathFinderUiState(): GameUiState = createPathFinderGame().toUiState()
fun createMiniSudokuUiState(): GameUiState = createMiniSudokuGame().toUiState()
fun createPatternSequenceUiState(): GameUiState = createPatternSequenceGame().toUiState()

fun createColorConfusionUiState(): GameUiState = createColorConfusionGame().toUiState()

fun createFlashCrowdUiState(): FlashCrowdUiState = FlashCrowdUiState(
    roundKey = 1,
    leftDots = listOf(
        FlashCrowdUiState.Dot(0.15f, 0.12f, 0.025f),
        FlashCrowdUiState.Dot(0.42f, 0.08f, 0.030f),
        FlashCrowdUiState.Dot(0.78f, 0.18f, 0.022f),
        FlashCrowdUiState.Dot(0.25f, 0.35f, 0.028f),
        FlashCrowdUiState.Dot(0.60f, 0.30f, 0.035f),
        FlashCrowdUiState.Dot(0.88f, 0.42f, 0.025f),
        FlashCrowdUiState.Dot(0.10f, 0.55f, 0.032f),
        FlashCrowdUiState.Dot(0.50f, 0.52f, 0.027f),
        FlashCrowdUiState.Dot(0.75f, 0.60f, 0.030f),
        FlashCrowdUiState.Dot(0.35f, 0.72f, 0.025f),
        FlashCrowdUiState.Dot(0.65f, 0.78f, 0.033f),
        FlashCrowdUiState.Dot(0.20f, 0.88f, 0.028f),
        FlashCrowdUiState.Dot(0.80f, 0.85f, 0.022f),
        FlashCrowdUiState.Dot(0.45f, 0.92f, 0.030f),
        FlashCrowdUiState.Dot(0.90f, 0.15f, 0.025f),
    ),
    rightDots = listOf(
        FlashCrowdUiState.Dot(0.20f, 0.15f, 0.050f),
        FlashCrowdUiState.Dot(0.65f, 0.12f, 0.045f),
        FlashCrowdUiState.Dot(0.40f, 0.38f, 0.055f),
        FlashCrowdUiState.Dot(0.80f, 0.40f, 0.048f),
        FlashCrowdUiState.Dot(0.15f, 0.60f, 0.052f),
        FlashCrowdUiState.Dot(0.55f, 0.65f, 0.045f),
        FlashCrowdUiState.Dot(0.30f, 0.85f, 0.050f),
        FlashCrowdUiState.Dot(0.75f, 0.80f, 0.048f),
    ),
)

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

fun createVisualMemoryUiState(): VisualMemoryUiState {
    return VisualMemoryUiState(
        round = 4,
        phase = VisualMemoryGame.Phase.MEMORIZING,
        countdown = 3,
        cells = listOf(
            CellState(CellType.MEMORIZING, visualMemoryFigures[0]),
            CellState(CellType.EMPTY, null),
            CellState(CellType.MEMORIZING, visualMemoryFigures[2]),
            CellState(CellType.EMPTY, null),
            CellState(CellType.EMPTY, null),
            CellState(CellType.EMPTY, null),
            CellState(CellType.MEMORIZING, visualMemoryFigures[4]),
            CellState(CellType.EMPTY, null),
            CellState(CellType.MEMORIZING, visualMemoryFigures[3]),
        ),
        answerOptions = visualMemoryAnswerOptions,
        currentTargetFigure = null,
    )
}

fun createVisualMemoryGameOverUiState(): VisualMemoryUiState {
    return VisualMemoryUiState(
        round = 4,
        phase = VisualMemoryGame.Phase.GAME_OVER,
        countdown = 0,
        cells = listOf(
            CellState(CellType.REVEALED, visualMemoryFigures[0]),
            CellState(CellType.EMPTY, null),
            CellState(CellType.REVEALED, visualMemoryFigures[2]),
            CellState(CellType.EMPTY, null),
            CellState(CellType.EMPTY, null),
            CellState(CellType.EMPTY, null),
            CellState(CellType.WRONG, visualMemoryFigures[4]),
            CellState(CellType.EMPTY, null),
            CellState(CellType.REVEALED, visualMemoryFigures[3]),
        ),
        answerOptions = visualMemoryAnswerOptions.mapIndexed { index, option ->
            option.copy(
                enabled = false,
                isWrong = index == 4,
            )
        },
        currentTargetFigure = visualMemoryFigures[3],
    )
}
