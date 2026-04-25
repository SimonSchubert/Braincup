package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.splitToIntList

class MiniSudokuGame : Game() {
    var gridSize: Int = 4
        private set
    var blockRows: Int = 2
        private set
    var blockCols: Int = 2
        private set

    val solutionGrid = mutableListOf<MutableList<Int>>()
    val initialValues = mutableListOf<Int?>()

    private fun configureForRound() {
        if (round >= 8) {
            gridSize = 6
            blockRows = 2
            blockCols = 3
        } else {
            gridSize = 4
            blockRows = 2
            blockCols = 2
        }
    }

    private fun targetClueCount(): Int = when {
        gridSize == 6 -> when {
            round >= 12 -> 14
            round >= 10 -> 16
            else -> 18
        }
        round >= 5 -> 6
        round >= 3 -> 8
        else -> 10
    }

    override fun generateRound() {
        configureForRound()
        solutionGrid.clear()
        initialValues.clear()

        val full = generateFullGrid(gridSize, blockRows, blockCols)
        full.forEach { solutionGrid.add(it.toMutableList()) }

        val puzzle = removeClues(full, blockRows, blockCols, targetClueCount())
        repeat(gridSize) { r ->
            repeat(gridSize) { c ->
                val v = puzzle[r][c]
                initialValues.add(if (v == 0) null else v)
            }
        }
    }

    override fun isCorrect(input: String): Boolean {
        val numbers = input.splitToIntList()
        if (numbers.size != gridSize * gridSize) return false
        val n = gridSize
        for (r in 0 until n) {
            val seen = mutableSetOf<Int>()
            for (c in 0 until n) {
                val v = numbers[r * n + c]
                if (v < 1 || v > n || !seen.add(v)) return false
            }
        }
        for (c in 0 until n) {
            val seen = mutableSetOf<Int>()
            for (r in 0 until n) {
                if (!seen.add(numbers[r * n + c])) return false
            }
        }
        for (br in 0 until n step blockRows) {
            for (bc in 0 until n step blockCols) {
                val seen = mutableSetOf<Int>()
                for (r in br until br + blockRows) {
                    for (c in bc until bc + blockCols) {
                        if (!seen.add(numbers[r * n + c])) return false
                    }
                }
            }
        }
        return true
    }

    override fun solution(): String = solutionGrid.joinToString(" ") { row -> row.joinToString(" ") }

    override fun hint(): String? = ""

    override fun toUiState() = com.inspiredandroid.braincup.app.MiniSudokuUiState(
        gridSize = gridSize,
        blockRows = blockRows,
        blockCols = blockCols,
        initialValues = initialValues.toList(),
        solutionValues = null,
    )

    fun flatSolution(): List<Int> = solutionGrid.flatten()

    private fun generateFullGrid(
        n: Int,
        blockRows: Int,
        blockCols: Int,
    ): List<List<Int>> {
        // Base sudoku via the standard row-shift formula; valid for any n=blockRows*blockCols.
        val base = Array(n) { r ->
            val shift = (r % blockRows) * blockCols + (r / blockRows)
            IntArray(n) { c -> ((shift + c) % n) + 1 }
        }
        val digits = (1..n).shuffled()
        val mapped = Array(n) { r -> IntArray(n) { c -> digits[base[r][c] - 1] } }

        val rowOrder = shuffleWithinGroups(n, blockRows)
        val colOrder = shuffleWithinGroups(n, blockCols)

        return List(n) { r ->
            List(n) { c -> mapped[rowOrder[r]][colOrder[c]] }
        }
    }

    /** Permutation of `0 until n` that shuffles within each group of [groupSize] AND shuffles
     *  the groups. Preserves block validity for sudoku row/column reorderings. */
    private fun shuffleWithinGroups(n: Int, groupSize: Int): IntArray {
        val groupCount = n / groupSize
        val withinGroup = IntArray(n)
        for (group in 0 until groupCount) {
            val shuffled = (0 until groupSize).map { it + group * groupSize }.shuffled()
            for (i in 0 until groupSize) withinGroup[group * groupSize + i] = shuffled[i]
        }
        val groupOrder = (0 until groupCount).shuffled()
        return IntArray(n) { idx ->
            val group = idx / groupSize
            val within = idx % groupSize
            withinGroup[groupOrder[group] * groupSize + within]
        }
    }

    private fun removeClues(
        full: List<List<Int>>,
        blockRows: Int,
        blockCols: Int,
        targetClues: Int,
    ): Array<IntArray> {
        val n = full.size
        val grid = Array(n) { r -> IntArray(n) { c -> full[r][c] } }
        val totalCells = n * n
        val toBlank = totalCells - targetClues

        val positions = (0 until totalCells).shuffled()
        var blanked = 0
        for (pos in positions) {
            if (blanked >= toBlank) break
            val r = pos / n
            val c = pos % n
            val saved = grid[r][c]
            grid[r][c] = 0
            if (countSolutions(grid, blockRows, blockCols, fromIndex = 0, limit = 2) == 1) {
                blanked++
            } else {
                grid[r][c] = saved
            }
        }
        return grid
    }

    private fun countSolutions(
        grid: Array<IntArray>,
        blockRows: Int,
        blockCols: Int,
        fromIndex: Int,
        limit: Int,
    ): Int {
        val n = grid.size
        val total = n * n
        var nextEmpty = fromIndex
        while (nextEmpty < total && grid[nextEmpty / n][nextEmpty % n] != 0) nextEmpty++
        if (nextEmpty >= total) return 1

        val r = nextEmpty / n
        val c = nextEmpty % n
        var count = 0
        for (v in 1..n) {
            if (canPlace(grid, r, c, v, blockRows, blockCols)) {
                grid[r][c] = v
                count += countSolutions(grid, blockRows, blockCols, nextEmpty + 1, limit - count)
                grid[r][c] = 0
                if (count >= limit) return count
            }
        }
        return count
    }

    private fun canPlace(
        grid: Array<IntArray>,
        row: Int,
        col: Int,
        value: Int,
        blockRows: Int,
        blockCols: Int,
    ): Boolean {
        val n = grid.size
        for (i in 0 until n) {
            if (grid[row][i] == value) return false
            if (grid[i][col] == value) return false
        }
        val br = (row / blockRows) * blockRows
        val bc = (col / blockCols) * blockCols
        for (r in br until br + blockRows) {
            for (c in bc until bc + blockCols) {
                if (grid[r][c] == value) return false
            }
        }
        return true
    }
}
