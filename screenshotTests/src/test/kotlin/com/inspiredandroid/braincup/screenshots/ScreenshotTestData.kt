package com.inspiredandroid.braincup.screenshots

import com.inspiredandroid.braincup.api.UserStorage
import com.russhwolf.settings.MapSettings
import com.inspiredandroid.braincup.app.DigitMemoryUiState
import com.inspiredandroid.braincup.app.IqTestPlayUiState
import com.inspiredandroid.braincup.app.IqTestResultUiState
import com.inspiredandroid.braincup.app.IqTestReviewItemUiState
import com.inspiredandroid.braincup.app.MatrixOptionCell
import com.inspiredandroid.braincup.app.FlashCrowdUiState
import com.inspiredandroid.braincup.app.GameUiState
import com.inspiredandroid.braincup.app.KnotUiState
import com.inspiredandroid.braincup.app.NBackUiState
import com.inspiredandroid.braincup.app.QuickSumUiState
import com.inspiredandroid.braincup.app.SequenceCellType
import com.inspiredandroid.braincup.app.SimonSaysUiState
import com.inspiredandroid.braincup.app.VisualMemoryUiState
import com.inspiredandroid.braincup.app.VisualMemoryUiState.CellState
import com.inspiredandroid.braincup.app.VisualMemoryUiState.CellType
import com.inspiredandroid.braincup.games.*
import com.inspiredandroid.braincup.games.iqtest.IqScoring
import com.inspiredandroid.braincup.games.iqtest.IqTest
import com.inspiredandroid.braincup.games.iqtest.IqTestBlueprint
import com.inspiredandroid.braincup.games.iqtest.TierResult
import com.inspiredandroid.braincup.games.matrix.MatrixGenerator
import com.inspiredandroid.braincup.games.matrix.MatrixProblem
import com.inspiredandroid.braincup.games.tools.GameColor
import com.inspiredandroid.braincup.games.tools.Direction
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.Shape
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import kotlin.random.Random

val mainMenuHighscores = persistentMapOf(
    "6" to 12,
    "0" to 9,
    "1" to 7,
    "2" to 4,
    "3" to 5,
    "4" to 6,
    "5" to 8,
    "8" to 10,
    "16" to 3,
    "17" to 120, // 12.0s
    "10" to 5,
    "11" to 6, // Pattern Sequence
    "12" to 4, // Ghost Grid
    "13" to 7,
    "15" to 8,
    "18" to 10, // Mini Chess (medium win)
    "22" to 5, // Digit Memory
    "23" to 8, // Spot the New
    "27" to 6, // Cat Queens
)

fun createColoredShapesGame(): ColoredShapesGame {
    val game = ColoredShapesGame()
    game.displayedShape = Shape.HEART
    game.displayedColor = GameColor.RED
    game.answerShape = Shape.HEART
    game.answerColor = GameColor.BLUE
    game.stringColor = GameColor.GREEN
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
            Figure(Shape.STAR, GameColor.RED),
            Figure(Shape.STAR, GameColor.GREEN),
            Figure(Shape.STAR, GameColor.BLUE),
            Figure(Shape.STAR, GameColor.PURPLE),
            Figure(Shape.STAR, GameColor.YELLOW),
            Figure(Shape.STAR, GameColor.RED),
            Figure(Shape.STAR, GameColor.GREEN),
            Figure(Shape.STAR, GameColor.PURPLE),
            Figure(Shape.STAR, GameColor.BLUE),
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

fun createBubbleSumUiState(): GameUiState {
    val game = BubbleSumGame()
    game.nextRound()
    return game.toUiState()
}

/** Built directly rather than from a game: the terms are random, and a snapshot needs a fixed one. */
fun createQuickSumUiState(): GameUiState = QuickSumUiState(
    phase = QuickSumGame.Phase.FLASHING,
    currentTerm = 7,
    termIndex = 1,
    termCount = 4,
    answerLength = 2,
    revealedSum = null,
    answerResult = null,
)

// Built directly rather than from a game: the sequence is random, and a snapshot needs a fixed one.
fun createNBackUiState(): GameUiState = NBackUiState(
    phase = NBackGame.Phase.RECALL,
    currentShape = null,
    showIndex = 3,
    sequenceLength = 4,
    askIndex = 2,
    options = NBackGame.PALETTE.toImmutableList(),
    revealAnswer = null,
    recallResult = null,
)

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

fun createSchulteTableGame(): SchulteTableGame {
    val game = SchulteTableGame()
    game.nextRound() // 4×4 populated with random numbers
    game.numbers.clear()
    game.numbers.addAll(
        listOf(
            7, 12, 3, 16,
            1, 9, 14, 5,
            11, 6, 8, 2,
            4, 15, 13, 10,
        ),
    )
    listOf(1, 2, 3, 4).forEach { value ->
        game.tapCell(game.numbers.indexOf(value))
    }
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

fun createColorConfusionGame(): ColorConfusionGame {
    val game = ColorConfusionGame()
    game.cells = listOf(
        ColorConfusionGame.Cell(word = GameColor.RED, fontColor = GameColor.RED),
        ColorConfusionGame.Cell(word = GameColor.BLUE, fontColor = GameColor.GREEN),
        ColorConfusionGame.Cell(word = GameColor.GREEN, fontColor = GameColor.GREEN),
        ColorConfusionGame.Cell(word = GameColor.PURPLE, fontColor = GameColor.YELLOW),
        ColorConfusionGame.Cell(word = GameColor.ORANGE, fontColor = GameColor.ORANGE),
        ColorConfusionGame.Cell(word = GameColor.YELLOW, fontColor = GameColor.BLUE),
        ColorConfusionGame.Cell(word = GameColor.RED, fontColor = GameColor.PURPLE),
        ColorConfusionGame.Cell(word = GameColor.BLUE, fontColor = GameColor.BLUE),
        ColorConfusionGame.Cell(word = GameColor.GREEN, fontColor = GameColor.RED),
    )
    game.selectedIndices = mutableSetOf(0, 2, 4)
    game.feedbackState = List(9) { ColorConfusionGame.CellFeedback.NONE }
    game.isSubmitted = false
    return game
}

fun createGhostGridGame(): GhostGridGame {
    val game = GhostGridGame(random = Random(42L))
    game.nextRound()
    return game
}

private val visualMemoryFigures = listOf(
    Figure(Shape.HEART, GameColor.PURPLE),
    Figure(Shape.SQUARE, GameColor.GREY_LIGHT),
    Figure(Shape.STAR, GameColor.BLUE),
    Figure(Shape.CIRCLE, GameColor.TURQUOISE),
    Figure(Shape.L, GameColor.ROSA),
    Figure(Shape.TRIANGLE, GameColor.YELLOW),
    Figure(Shape.HOUSE, GameColor.ORANGE),
    Figure(Shape.T, GameColor.GREEN),
    Figure(Shape.DIAMOND, GameColor.RED),
)

private val visualMemoryAnswerOptions = visualMemoryFigures.mapIndexed { index, figure ->
    VisualMemoryUiState.AnswerOption(
        figure = figure,
        figureIndex = index,
        enabled = true,
    )
}.toImmutableList()

fun createColoredShapesUiState(): GameUiState = createColoredShapesGame().toUiState()
fun createAnomalyPuzzleUiState(): GameUiState = createAnomalyPuzzleGame().toUiState()
fun createMentalCalculationUiState(): GameUiState = createMentalCalculationGame().toUiState()
fun createSherlockCalculationUiState(): GameUiState = createSherlockCalculationGame().toUiState()
fun createChainCalculationUiState(): GameUiState = createChainCalculationGame().toUiState()
fun createFractionCalculationUiState(): GameUiState = createFractionCalculationGame().toUiState()
fun createValueComparisonUiState(): GameUiState = createValueComparisonGame().toUiState()
fun createPathFinderUiState(): GameUiState = createPathFinderGame().toUiState()
fun createMiniSudokuUiState(): GameUiState = createMiniSudokuGame().toUiState()
fun createLightsOutUiState(): GameUiState = LightsOutGame(level = 3, random = Random(42L))
    .apply { nextRound() }
    .toUiState()
fun createSlidingPuzzleUiState(): GameUiState = SlidingPuzzleGame(level = 3, random = Random(42L))
    .apply { nextRound() }
    .toUiState()
fun createShikakuUiState(): GameUiState = ShikakuGame(level = 3, random = Random(42L))
    .apply { nextRound() }
    .toUiState()
fun createNurikabeUiState(): GameUiState = NurikabeGame(level = 3, random = Random(42L))
    .apply { nextRound() }
    .toUiState()
fun createCatQueensUiState(): GameUiState = CatQueensGame(level = 3, random = Random(42L))
    .apply {
        nextRound()
        // A non-conflicting trio on this fixed 5x5 board, so the snapshot exercises cat rendering
        // and the green "valid" feedback rings.
        toggle(0)
        toggle(12)
        toggle(24)
    }
    .toUiState()
// A fixed 4x4 Knot board: two pairs connected, one pair (color 2) still just dots, so the snapshot
// exercises both the drawn-path rendering and the bare endpoints.
fun createKnotUiState(): GameUiState = KnotUiState(
    rows = 4,
    cols = 4,
    endpoints = listOf(
        KnotUiState.Endpoint(color = 0, a = 0, b = 11),
        KnotUiState.Endpoint(color = 1, a = 1, b = 10),
        KnotUiState.Endpoint(color = 2, a = 2, b = 6),
    ).toImmutableList(),
    paths = persistentMapOf(
        0 to listOf(0, 4, 8, 12, 13, 14, 15, 11).toImmutableList(),
        1 to listOf(1, 5, 9, 10).toImmutableList(),
    ),
    level = 3,
)

// A deterministic Solo Chess board with a piece selected so the snapshot shows a capture highlight.
// Built from the public API only (the screenshot module can't see internal members).
fun createSoloChessUiState(): GameUiState {
    val game = SoloChessGame(level = 3, random = Random(42L)).apply { nextRound() }
    for (cell in game.toUiState().pieces.keys) {
        game.tap(cell)
        if (game.toUiState().targets.isNotEmpty()) break
        game.tap(cell) // nothing to capture from here; deselect and try the next piece
    }
    return game.toUiState()
}

fun createSchulteTableUiState(): GameUiState = createSchulteTableGame().toUiState()
// Round 6 lands on difficulty tier 3: two governed attributes and six options, which is the
// most representative board the ladder produces.
fun createPatternSequenceUiState(): GameUiState = PatternSequenceGame(Random(42L))
    .apply {
        round = 6
        nextRound()
    }.toUiState()

fun createColorConfusionUiState(): GameUiState = createColorConfusionGame().toUiState()

fun createTrioUiState(): GameUiState = TrioGame(Random(42L)).apply {
    nextRound()
    tap(0)
    tap(1)
}.toUiState()

/** A storage holding one banked run, so the scoreboard has both a high score and a history row. */
fun scoreboardStorage(): UserStorage = UserStorage(MapSettings()).apply {
    putScore(GameType.MENTAL_ROTATIONS.id, 11)
}

// A fixed seed so the pair of figures, and therefore the snapshot, never drifts.
fun createMentalRotationsUiState(): GameUiState = MentalRotationsGame(Random(9L)).apply {
    round = 6
    nextRound()
}.toUiState()

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
    ).toImmutableList(),
    rightDots = listOf(
        FlashCrowdUiState.Dot(0.20f, 0.15f, 0.050f),
        FlashCrowdUiState.Dot(0.65f, 0.12f, 0.045f),
        FlashCrowdUiState.Dot(0.40f, 0.38f, 0.055f),
        FlashCrowdUiState.Dot(0.80f, 0.40f, 0.048f),
        FlashCrowdUiState.Dot(0.15f, 0.60f, 0.052f),
        FlashCrowdUiState.Dot(0.55f, 0.65f, 0.045f),
        FlashCrowdUiState.Dot(0.30f, 0.85f, 0.050f),
        FlashCrowdUiState.Dot(0.75f, 0.80f, 0.048f),
    ).toImmutableList(),
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
    val game = GhostGridGame(random = Random(42L))
    game.nextRound()
    // Submit a wrong answer to trigger game over
    game.submitAnswer("-1")
    return game.toUiState()
}

// Built from the public constructor rather than game.toUiState().copy(phase = ANSWERING): the
// game only leaves SHOWING from inside startShowSequence's coroutine, so a copy() after the fact
// relabels the phase but leaves every pad INACTIVE, giving an all-dark golden that can never
// catch a regression in how a tapped pad renders.
fun createSimonSaysUiState(): SimonSaysUiState {
    val tapped = SimonSaysGame.PADS.first()
    return SimonSaysUiState(
        round = 1,
        phase = SimonSaysGame.Phase.ANSWERING,
        pads = SimonSaysGame.PADS.map { color ->
            SimonSaysUiState.PadState(
                color = color,
                type = if (color == tapped) {
                    SequenceCellType.TAPPED
                } else {
                    SequenceCellType.INACTIVE
                },
            )
        }.toImmutableList(),
        sequenceLength = 3,
        tappedCount = 1,
    )
}

fun createSimonSaysGameOverUiState(): SimonSaysUiState {
    val game = SimonSaysGame(random = Random(42L))
    game.nextRound()
    // Submit a color that is guaranteed to mismatch the actual first pad, whatever the seed
    // produced, so this stays deterministic without hardcoding the RNG's output.
    val wrongColorName = SimonSaysGame.PADS.first { it != game.sequence[0] }.name
    game.submitAnswer(wrongColorName)
    return game.toUiState()
}

fun createDigitMemoryShowingUiState(): DigitMemoryUiState = DigitMemoryUiState(
    phase = DigitMemoryGame.Phase.SHOWING,
    sequence = "5441754",
    sequenceLength = 7,
    problem = "3 + 4",
    answerLength = 1,
    revealedMathAnswer = null,
    recallResult = null,
)

fun createDigitMemorySolvingUiState(): DigitMemoryUiState = DigitMemoryUiState(
    phase = DigitMemoryGame.Phase.SOLVING,
    sequence = "5441754",
    sequenceLength = 7,
    problem = "6 * 7",
    answerLength = 2,
    revealedMathAnswer = null,
    recallResult = null,
)

fun createDigitMemoryRecallUiState(): DigitMemoryUiState = DigitMemoryUiState(
    phase = DigitMemoryGame.Phase.RECALL,
    sequence = "5441754",
    sequenceLength = 7,
    problem = "6 * 7",
    answerLength = 2,
    revealedMathAnswer = null,
    recallResult = null,
)

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
        ).toImmutableList(),
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
        ).toImmutableList(),
        answerOptions = visualMemoryAnswerOptions.mapIndexed { index, option ->
            option.copy(
                enabled = false,
                isWrong = index == 4,
            )
        }.toImmutableList(),
        currentTargetFigure = visualMemoryFigures[3],
    )
}

/**
 * IQ test fixtures. Built from the public [IqTest] API rather than the screens' own preview
 * helpers, which are `internal` to composeApp and so invisible from this module.
 */
private const val IQ_TEST_SEED = 42L

private fun iqTestProblem(itemIndex: Int) = IqTest(seed = IQ_TEST_SEED).problemAt(itemIndex)

private fun MatrixProblem.blankedMatrix() = matrix
    .mapIndexed { index, panel -> panel.takeIf { index != 8 } }
    .toImmutableList()

private fun MatrixProblem.optionCellRows() = options
    .map { MatrixOptionCell(it) }
    .chunked(optionColumns)
    .map { it.toImmutableList() }
    .toImmutableList()

fun createIqTestPlayUiState(itemIndex: Int = 17, selectedOption: Int? = 2): IqTestPlayUiState {
    val problem = iqTestProblem(itemIndex)
    return IqTestPlayUiState(
        itemIndex = itemIndex,
        itemCount = IqTestBlueprint.ITEM_COUNT,
        matrix = problem.blankedMatrix(),
        optionRows = problem.optionCellRows(),
        optionColumns = problem.optionColumns,
        selectedOption = selectedOption,
        isOnLastItem = itemIndex == IqTestBlueprint.ITEM_COUNT - 1,
    )
}

fun createIqTestReviewUiState(itemIndex: Int = 14, pickedOption: Int? = 1): IqTestReviewItemUiState {
    val problem = iqTestProblem(itemIndex)
    return IqTestReviewItemUiState(
        itemIndex = itemIndex,
        itemCount = IqTestBlueprint.ITEM_COUNT,
        matrix = problem.blankedMatrix(),
        optionRows = problem.optionCellRows(),
        optionColumns = problem.optionColumns,
        pickedOption = pickedOption,
        correctOption = problem.correctOptionIndex,
        tier = IqTestBlueprint.tierFor(itemIndex),
    )
}

fun createIqTestResultUiState(
    rawScore: Int,
    isPersonalBest: Boolean = true,
    levelChange: UserStorage.LevelChange? = null,
    durationSeconds: Int = 11 * 60 + 42,
    /**
     * Correct answers per tier. Defaults to filling the easy tiers first, which is fine for a
     * regression fixture but reads as a cliff. Pass a spread for the store shots, where a run that
     * drops a few items mid-ladder looks like someone actually played it.
     */
    tierCorrect: List<Int>? = null,
): IqTestResultUiState {
    val iq = IqScoring.iqFor(rawScore)
    var remaining = rawScore
    val breakdown = (0..MatrixGenerator.MAX_DIFFICULTY).map { tier ->
        val total = IqTestBlueprint.itemCountForTier(tier)
        val correct = tierCorrect?.get(tier) ?: remaining.coerceIn(0, total).also { remaining -= it }
        TierResult(tier = tier, correct = correct, total = total)
    }
    require(breakdown.sumOf { it.correct } == rawScore) {
        "tierCorrect sums to ${breakdown.sumOf { it.correct }}, expected rawScore $rawScore"
    }
    require(breakdown.all { it.correct <= it.total }) { "a tier cannot score above its item count" }
    return IqTestResultUiState(
        rawScore = rawScore,
        itemCount = IqTestBlueprint.ITEM_COUNT,
        iq = iq,
        percentile = IqScoring.percentileFor(iq),
        band = IqScoring.bandFor(iq),
        isBelowMeasurableRange = IqScoring.isBelowMeasurableRange(rawScore),
        tierBreakdown = breakdown.toImmutableList(),
        durationSeconds = durationSeconds,
        xpGained = UserStorage.IQ_TEST_COMPLETION_XP,
        levelChange = levelChange,
        isPersonalBest = isPersonalBest,
    )
}
