package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.addOrIncrease
import com.inspiredandroid.braincup.splitToIntList
import kotlin.random.Random

class GridSolverGame : Game() {
    val entries = mutableListOf<MutableList<Int>>()
    val resultsX = mutableListOf<Int>()
    val resultsY = mutableListOf<Int>()
    val initialValues = mutableListOf<Int?>()

    fun size(): Int = when {
        round > 5 -> 4
        else -> 3
    }

    private fun maxEntryValue(): Int = when {
        round > 5 -> 9
        round > 3 -> 7
        else -> 6
    }

    override fun generateRound() {
        entries.clear()
        resultsX.clear()
        resultsY.clear()
        initialValues.clear()

        repeat(size()) { x ->
            entries.add(mutableListOf())
            repeat(size()) {
                val entry = Random.nextInt(2, maxEntryValue())
                entries[x].add(entry)
            }
        }

        repeat(size()) { x ->
            resultsY.add(entries[x].sum())
            repeat(size()) { y ->
                resultsX.addOrIncrease(x, entries[y][x])
            }
        }

        val gridSize = size()
        val totalCells = gridSize * gridSize
        repeat(totalCells) {
            initialValues.add(null)
        }

        val revealCount = if (gridSize == 3) 3 else 5
        // Reshuffle until clues span at least two rows AND at least two columns — a
        // single-row/column reveal makes the remaining cells trivially derivable from the sums.
        var indices: List<Int>
        do {
            indices = (0 until totalCells).shuffled().take(revealCount)
        } while (indices.map { it / gridSize }.toSet().size < 2 ||
            indices.map { it % gridSize }.toSet().size < 2
        )
        indices.forEach { index ->
            val row = index / gridSize
            val col = index % gridSize
            initialValues[index] = entries[row][col]
        }
    }

    override fun isCorrect(input: String): Boolean {
        val numbers = input.splitToIntList()
        if (numbers.count() < size().times(size())) {
            return false
        }
        resultsY.forEachIndexed { index, _ ->
            val sum = numbers.subList(index * size(), index * size() + size()).sum()
            if (sum != resultsY[index]) {
                return false
            }
        }
        resultsX.forEachIndexed { index, _ ->
            val sum =
                numbers.filterIndexed { numberIndex, _ -> numberIndex % size() == index }.sum()
            if (sum != resultsX[index]) {
                return false
            }
        }
        return true
    }

    override fun solution(): String {
        var solution = ""
        entries.forEach {
            it.forEach {
                solution += "$it "
            }
        }
        return solution
    }

    override fun hint(): String? = ""

    override fun toUiState() = com.inspiredandroid.braincup.app.GridSolverUiState(
        gridSize = size(),
        resultsX = resultsX.toList(),
        resultsY = resultsY.toList(),
        initialValues = initialValues.toList(),
    )
}
