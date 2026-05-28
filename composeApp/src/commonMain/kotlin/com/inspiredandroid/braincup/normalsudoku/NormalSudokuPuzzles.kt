package com.inspiredandroid.braincup.normalsudoku

import kotlin.random.Random

enum class SudokuDifficulty { BEGINNER, EASY, MEDIUM, HARD, EXPERT }

data class NormalSudokuPuzzle(
    val id: String,
    val difficulty: SudokuDifficulty,
    val clues: String,
    val solution: String,
)

/**
 * Curated set of 50 9x9 sudoku puzzles, 10 per difficulty tier. Puzzles are produced
 * deterministically from a fixed base solution by applying validity-preserving
 * transforms (digit relabel, row/column/band/stack swaps, optional transpose) and then
 * removing cells in a seeded order while the backtracking solver still reports a single
 * solution. This guarantees every puzzle is uniquely solvable; the matching solution
 * is precomputed and stored alongside the clue string.
 *
 * Generation runs once on first access via `lazy` and takes well under a second on
 * typical hardware. Seeds are stable, so the same 50 puzzles ship in every build.
 */
object NormalSudokuPuzzles {
    val all: List<NormalSudokuPuzzle> by lazy { generate() }

    fun byDifficulty(difficulty: SudokuDifficulty): List<NormalSudokuPuzzle> = all.filter { it.difficulty == difficulty }

    fun byId(id: String): NormalSudokuPuzzle? = all.firstOrNull { it.id == id }

    private val baseSolution: IntArray = intArrayOf(
        5, 3, 4, 6, 7, 8, 9, 1, 2,
        6, 7, 2, 1, 9, 5, 3, 4, 8,
        1, 9, 8, 3, 4, 2, 5, 6, 7,
        8, 5, 9, 7, 6, 1, 4, 2, 3,
        4, 2, 6, 8, 5, 3, 7, 9, 1,
        7, 1, 3, 9, 2, 4, 8, 5, 6,
        9, 6, 1, 5, 3, 7, 2, 8, 4,
        2, 8, 7, 4, 1, 9, 6, 3, 5,
        3, 4, 5, 2, 8, 6, 1, 7, 9,
    )

    private fun targetClues(difficulty: SudokuDifficulty): Int = when (difficulty) {
        SudokuDifficulty.BEGINNER -> 44
        SudokuDifficulty.EASY -> 38
        SudokuDifficulty.MEDIUM -> 32
        SudokuDifficulty.HARD -> 28
        SudokuDifficulty.EXPERT -> 25
    }

    private fun generate(): List<NormalSudokuPuzzle> {
        val result = mutableListOf<NormalSudokuPuzzle>()
        for (difficulty in SudokuDifficulty.entries) {
            val target = targetClues(difficulty)
            for (i in 1..10) {
                val seed = difficulty.ordinal * 1000L + i
                val solution = transform(baseSolution, Random(seed))
                val puzzle = removeCells(solution, target, Random(seed * 31 + 7))
                result += NormalSudokuPuzzle(
                    id = "${difficulty.name.lowercase()}-${i.toString().padStart(2, '0')}",
                    difficulty = difficulty,
                    clues = puzzle.toGridString(),
                    solution = solution.toGridString(),
                )
            }
        }
        return result
    }

    private fun transform(source: IntArray, rng: Random): IntArray {
        var grid = source.copyOf()

        val digitPerm = (1..9).shuffled(rng)
        grid = IntArray(81) { digitPerm[grid[it] - 1] }

        for (band in 0..2) {
            val order = (0..2).shuffled(rng)
            grid = permuteRowsInBand(grid, band, order)
        }
        grid = permuteBands(grid, (0..2).shuffled(rng))

        for (stack in 0..2) {
            val order = (0..2).shuffled(rng)
            grid = permuteColsInStack(grid, stack, order)
        }
        grid = permuteStacks(grid, (0..2).shuffled(rng))

        if (rng.nextBoolean()) {
            val transposed = IntArray(81)
            for (r in 0..8) for (c in 0..8) transposed[c * 9 + r] = grid[r * 9 + c]
            grid = transposed
        }
        return grid
    }

    private fun permuteRowsInBand(grid: IntArray, band: Int, order: List<Int>): IntArray {
        val out = grid.copyOf()
        for (r in 0..2) {
            val srcRow = band * 3 + order[r]
            val dstRow = band * 3 + r
            for (c in 0..8) out[dstRow * 9 + c] = grid[srcRow * 9 + c]
        }
        return out
    }

    private fun permuteBands(grid: IntArray, order: List<Int>): IntArray {
        val out = IntArray(81)
        for (band in 0..2) {
            for (r in 0..2) {
                for (c in 0..8) {
                    out[(band * 3 + r) * 9 + c] = grid[(order[band] * 3 + r) * 9 + c]
                }
            }
        }
        return out
    }

    private fun permuteColsInStack(grid: IntArray, stack: Int, order: List<Int>): IntArray {
        val out = grid.copyOf()
        for (c in 0..2) {
            val srcCol = stack * 3 + order[c]
            val dstCol = stack * 3 + c
            for (r in 0..8) out[r * 9 + dstCol] = grid[r * 9 + srcCol]
        }
        return out
    }

    private fun permuteStacks(grid: IntArray, order: List<Int>): IntArray {
        val out = IntArray(81)
        for (stack in 0..2) {
            for (c in 0..2) {
                for (r in 0..8) {
                    out[r * 9 + stack * 3 + c] = grid[r * 9 + order[stack] * 3 + c]
                }
            }
        }
        return out
    }

    private fun removeCells(solution: IntArray, targetClues: Int, rng: Random): IntArray {
        val puzzle = solution.copyOf()
        val positions = (0..80).shuffled(rng)
        var remaining = 81
        for (pos in positions) {
            if (remaining <= targetClues) break
            val saved = puzzle[pos]
            puzzle[pos] = 0
            if (countSolutions(puzzle.copyOf(), max = 2) == 1) {
                remaining--
            } else {
                puzzle[pos] = saved
            }
        }
        return puzzle
    }

    private fun countSolutions(grid: IntArray, max: Int): Int {
        var found = 0
        fun backtrack(): Boolean {
            var bestPos = -1
            var bestCount = 10
            for (i in 0..80) {
                if (grid[i] == 0) {
                    var c = 0
                    for (d in 1..9) if (canPlace(grid, i, d)) c++
                    if (c < bestCount) {
                        bestCount = c
                        bestPos = i
                        if (c <= 1) break
                    }
                }
            }
            if (bestPos == -1) {
                found++
                return found >= max
            }
            if (bestCount == 0) return false
            for (d in 1..9) {
                if (canPlace(grid, bestPos, d)) {
                    grid[bestPos] = d
                    if (backtrack()) {
                        grid[bestPos] = 0
                        return true
                    }
                    grid[bestPos] = 0
                }
            }
            return false
        }
        backtrack()
        return found
    }

    private fun canPlace(grid: IntArray, pos: Int, digit: Int): Boolean {
        val row = pos / 9
        val col = pos % 9
        for (i in 0..8) {
            if (grid[row * 9 + i] == digit) return false
            if (grid[i * 9 + col] == digit) return false
        }
        val br = (row / 3) * 3
        val bc = (col / 3) * 3
        for (r in br until br + 3) {
            for (c in bc until bc + 3) {
                if (grid[r * 9 + c] == digit) return false
            }
        }
        return true
    }

    private fun IntArray.toGridString(): String = buildString(81) {
        for (v in this@toGridString) append(v)
    }
}
