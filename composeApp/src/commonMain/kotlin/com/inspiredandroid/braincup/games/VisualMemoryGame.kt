package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.Shape

/**
 * Visual Memory game where players memorize shape-color combinations on a 3x3 grid.
 *
 * Each round reveals N shapes for memorization, then hides ALL shapes.
 * The player must identify ALL N shapes in a random order determined by the game.
 */
class VisualMemoryGame : Game() {
    enum class Phase {
        MEMORIZING,
        ANSWERING,
    }

    companion object {
        const val GRID_SIZE = 9
        const val MEMORIZE_DURATION_MILLIS = 5000L

        private val GAME_SHAPES = listOf(
            Shape.SQUARE,
            Shape.TRIANGLE,
            Shape.CIRCLE,
            Shape.HEART,
            Shape.STAR,
            Shape.T,
            Shape.L,
            Shape.DIAMOND,
            Shape.HOUSE,
        )

        private val GAME_COLORS = listOf(
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.PURPLE,
            Color.YELLOW,
            Color.ORANGE,
            Color.TURQUOISE,
            Color.ROSA,
            Color.GREY_LIGHT,
        )
    }

    var phase: Phase = Phase.MEMORIZING
        private set

    /** The 9 shuffled figures used for this game session */
    val availableFigures: List<Figure>

    /** The grid positions (0-8), null means empty cell */
    private val grid: Array<Int?> = arrayOfNulls(GRID_SIZE)

    /** Shuffled order of positions the player must guess this round */
    var guessOrder: List<Int> = emptyList()
        private set

    /** Current index into guessOrder (which shape the player is on) */
    var currentGuessIndex: Int = 0
        private set

    /** Positions that have been correctly identified and should be revealed */
    val revealedPositions: MutableSet<Int> = mutableSetOf()

    /** Shuffled answer options for display (shuffled once at game start) */
    val shuffledAnswerOptions: List<Figure>

    /** Shuffled grid positions - determines where each figure is placed */
    private val shuffledPositions: List<Int> = (0 until GRID_SIZE).shuffled()

    init {
        // Create 9 unique shape-color combinations
        val colors = GAME_COLORS.shuffled()
        val shapes = GAME_SHAPES.shuffled()
        availableFigures = shapes.zip(colors).map { (shape, color) ->
            Figure(shape, color)
        }
        // Shuffle answer options once for consistent display order throughout the game
        shuffledAnswerOptions = availableFigures.shuffled()
    }

    override fun nextRound() {
        phase = Phase.MEMORIZING
        val figureIndex = round  // Which figure (0, 1, 2...)
        if (figureIndex < GRID_SIZE) {
            val gridPosition = shuffledPositions[figureIndex]  // Random position
            grid[gridPosition] = figureIndex
        }
    }

    fun startAnswerPhase() {
        phase = Phase.ANSWERING
        // Use the shuffled positions for this round, then shuffle the guess order
        guessOrder = shuffledPositions.take(round).shuffled()
        currentGuessIndex = 0
        revealedPositions.clear()
    }

    /**
     * Returns the grid position of the current shape the player needs to find.
     */
    fun getCurrentTargetPosition(): Int = guessOrder[currentGuessIndex]

    /**
     * Returns the figure the player currently needs to identify.
     */
    fun getCurrentTargetFigure(): Figure {
        val position = getCurrentTargetPosition()
        val figureIndex = grid[position] ?: 0
        return availableFigures[figureIndex]
    }

    /**
     * Returns the figure at the given grid position during memorization phase.
     * During answer phase, returns null unless the position has been revealed.
     */
    fun getFigureAt(position: Int): Figure? {
        if (position < 0 || position >= GRID_SIZE) return null
        val figureIndex = grid[position] ?: return null

        // During answer phase, only show revealed positions
        if (phase == Phase.ANSWERING && position !in revealedPositions) {
            return null
        }

        return availableFigures.getOrNull(figureIndex)
    }

    /**
     * Check if the selected figure index matches the current target.
     * The input is the index of the figure in availableFigures.
     */
    override fun isCorrect(input: String): Boolean {
        val selectedFigureIndex = input.toIntOrNull() ?: return false
        val targetPosition = getCurrentTargetPosition()
        val targetFigureIndex = grid[targetPosition]
        return selectedFigureIndex == targetFigureIndex
    }

    /**
     * Advance to the next shape in the guess order.
     * Call this after a correct answer.
     */
    fun advanceGuess() {
        revealedPositions.add(getCurrentTargetPosition())
        currentGuessIndex++
    }

    /**
     * Check if all shapes in this round have been identified.
     */
    fun isRoundComplete(): Boolean = currentGuessIndex >= guessOrder.size

    override fun solution(): String {
        val target = getCurrentTargetFigure()
        return "${target.color.displayName} ${target.shape.displayName}"
    }

    override fun hint(): String? = null

    override fun getGameType(): GameType = GameType.VISUAL_MEMORY

    fun isGameComplete(): Boolean = round >= GRID_SIZE

    /**
     * Returns the grid position where a figure is placed.
     */
    fun getGridPositionForFigure(figureIndex: Int): Int = shuffledPositions[figureIndex]

    /**
     * Check if a figure has already been revealed (correctly guessed).
     */
    fun isFigureRevealed(figureIndex: Int): Boolean {
        val gridPosition = shuffledPositions[figureIndex]
        return gridPosition in revealedPositions
    }

    /**
     * Check if a position has a figure placed on it.
     */
    fun hasPlacedFigure(position: Int): Boolean = grid[position] != null
}
