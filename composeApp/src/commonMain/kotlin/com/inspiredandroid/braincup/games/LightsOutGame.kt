package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.LightsOutUiState
import com.inspiredandroid.braincup.games.tools.orthogonalNeighbors
import kotlinx.collections.immutable.toImmutableList
import kotlin.random.Random

class LightsOutGame(
    level: Int = 1,
    private val random: Random = Random.Default,
) : LevelGame(level) {

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
        orthogonalNeighbors(index, gridSize, gridSize).forEach { toggle(it) }
    }

    private fun toggle(i: Int) {
        cells[i] = !cells[i]
    }

    override fun isSolved(): Boolean = cells.all { !it }

    override fun toUiState(): LightsOutUiState = LightsOutUiState(
        gridSize = gridSize,
        cells = cells.toList().toImmutableList(),
        moves = moves,
        level = level,
    )
}
