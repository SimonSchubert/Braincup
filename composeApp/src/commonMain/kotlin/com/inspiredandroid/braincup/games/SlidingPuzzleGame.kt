package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.SlidingPuzzleUiState
import kotlin.random.Random

class SlidingPuzzleGame(
    level: Int = 1,
    private val random: Random = Random.Default,
) : Game() {
    override val adaptiveDifficulty: Boolean = false

    var level: Int = level.coerceAtLeast(1)
        private set

    var gridSize: Int = 3
        private set

    /** 0 marks the empty cell; non-zero values are tile labels 1..n²-1. */
    private var tiles: IntArray = IntArray(0)
    private var emptyIndex: Int = 0
    var moves: Int = 0
        private set

    override fun generateRound() {
        gridSize = if (level <= 5) 3 else 4
        val n = gridSize * gridSize
        // Solved state: 1, 2, ..., n²-1, 0 (empty in bottom-right).
        tiles = IntArray(n) { i -> if (i == n - 1) 0 else i + 1 }
        emptyIndex = n - 1
        moves = 0

        val shuffleMoves = if (level <= 5) 4 + level * 2 else 6 + level * 2
        var lastEmpty = -1
        repeat(shuffleMoves) {
            // Pick a random neighbor of the empty cell, but avoid immediately undoing
            // the previous move so the puzzle doesn't trivially unwind itself.
            val candidates = neighborsOf(emptyIndex).filter { it != lastEmpty }
            val pick = candidates.random(random)
            val previousEmpty = emptyIndex
            slideInternal(pick)
            lastEmpty = previousEmpty
        }
        if (isSolved()) slideInternal(neighborsOf(emptyIndex).random(random))
    }

    /** Returns true if the slide produced the solved state. Invalid taps return false silently. */
    fun slideTile(index: Int): Boolean {
        if (index !in tiles.indices) return false
        if (index !in neighborsOf(emptyIndex)) return false
        slideInternal(index)
        moves++
        return isSolved()
    }

    private fun slideInternal(index: Int) {
        tiles[emptyIndex] = tiles[index]
        tiles[index] = 0
        emptyIndex = index
    }

    private fun neighborsOf(i: Int): List<Int> {
        val r = i / gridSize
        val c = i % gridSize
        return buildList {
            if (r > 0) add(i - gridSize)
            if (r < gridSize - 1) add(i + gridSize)
            if (c > 0) add(i - 1)
            if (c < gridSize - 1) add(i + 1)
        }
    }

    private fun isSolved(): Boolean {
        val n = tiles.size
        for (i in 0 until n - 1) if (tiles[i] != i + 1) return false
        return tiles[n - 1] == 0
    }

    override fun isCorrect(input: String): Boolean = isSolved()

    override fun solution(): String = ""

    override fun hint(): String? = null

    override fun toUiState(): SlidingPuzzleUiState = SlidingPuzzleUiState(
        gridSize = gridSize,
        tiles = tiles.toList(),
        moves = moves,
        level = level,
    )
}
