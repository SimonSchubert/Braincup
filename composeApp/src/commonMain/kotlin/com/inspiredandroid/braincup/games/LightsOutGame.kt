package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.LightsOutUiState
import kotlin.random.Random

class LightsOutGame(
    level: Int = 1,
    private val random: Random = Random.Default,
) : Game() {
    override val adaptiveDifficulty: Boolean = false

    var level: Int = level.coerceAtLeast(1)
        private set

    var gridSize: Int = 3
        private set

    private var cells: BooleanArray = BooleanArray(0)
    var moves: Int = 0
        private set

    override fun generateRound() {
        gridSize = if (level <= 5) 3 else 4
        cells = BooleanArray(gridSize * gridSize)
        moves = 0
        // Each press is its own inverse, so scrambling from the solved (all-off) state
        // by N valid presses guarantees the puzzle is solvable in at most N presses.
        val scrambleMoves = if (level <= 5) 1 + level else level - 2
        repeat(scrambleMoves) { applyPress(random.nextInt(cells.size)) }
        if (cells.all { !it }) applyPress(random.nextInt(cells.size))
    }

    /** Returns true when the puzzle is now solved (all cells off). */
    fun press(index: Int): Boolean {
        if (index !in cells.indices) return false
        applyPress(index)
        moves++
        return cells.all { !it }
    }

    private fun applyPress(index: Int) {
        toggle(index)
        val r = index / gridSize
        val c = index % gridSize
        if (r > 0) toggle(index - gridSize)
        if (r < gridSize - 1) toggle(index + gridSize)
        if (c > 0) toggle(index - 1)
        if (c < gridSize - 1) toggle(index + 1)
    }

    private fun toggle(i: Int) {
        cells[i] = !cells[i]
    }

    override fun isCorrect(input: String): Boolean = cells.all { !it }

    override fun solution(): String = ""

    override fun hint(): String? = null

    override fun toUiState(): LightsOutUiState = LightsOutUiState(
        gridSize = gridSize,
        cells = cells.toList(),
        moves = moves,
        level = level,
    )
}
