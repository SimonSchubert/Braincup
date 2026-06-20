package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.CatQueensUiState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlin.math.abs
import kotlin.random.Random

/**
 * Cat Queens: a Star-Battle / N-Queens style puzzle. Place exactly one cat in every row, every
 * column and every colored zone, with no two cats touching (not even diagonally). The mechanic is
 * a centuries-old public-domain puzzle; the theme and art are original.
 *
 * Level-based stateful puzzle modeled on [ShikakuGame] / [NurikabeGame]: one puzzle per attempt,
 * no timer, score = highest level reached. The player taps a cell to place or remove a cat
 * ([toggle]); the board is solved when the placed cats satisfy every rule.
 *
 * Because the puzzle has exactly one cat per row, two cats can only ever touch diagonally between
 * adjacent rows, so a valid placement is a column permutation in which consecutive rows differ by
 * at least two columns, with one cat falling in each region.
 */
class CatQueensGame(
    level: Int = 1,
    private val random: Random = Random.Default,
) : Game() {
    override val adaptiveDifficulty: Boolean = false

    var level: Int = level.coerceAtLeast(1)
        private set

    /** Side length of the square board; also the number of cats, columns and color regions. */
    var size: Int = 5
        private set

    /** region id (0 until size) for each cell, indexed by row*size+col. */
    internal var regions: IntArray = IntArray(0)
        private set

    /** Cells (row*size+col) the player has placed a cat on. */
    internal val cats: MutableSet<Int> = mutableSetOf()

    /** The generator's own solution, kept for tests; not used during play. */
    internal var solutionCats: Set<Int> = emptySet()
        private set

    // Level 1 is a gentle 4x4 intro; capped at 7x7 because random region growth almost never yields
    // a uniquely-solvable 8x8 board and larger boards add no new mechanic (matches ShikakuGame).
    private fun sizeForLevel(level: Int): Int = when {
        level <= 1 -> 4
        level <= 4 -> 5
        level <= 7 -> 6
        else -> 7
    }

    override fun generateRound() {
        size = sizeForLevel(level)
        cats.clear()

        // Rejection sampling for quality: keep generating placement+regions until the puzzle has a
        // unique solution, falling back to the last solvable board after a cap so we always finish.
        var placement = randomPlacement(size)
        var board = growRegions(size, placement)
        var attempts = 1
        while (countSolutions(board, size, limit = 2) != 1 && attempts < MAX_GENERATION_ATTEMPTS) {
            placement = randomPlacement(size)
            board = growRegions(size, placement)
            attempts++
        }
        regions = board
        solutionCats = placement.withIndex().map { (r, c) -> r * size + c }.toSet()
    }

    /** Places or removes a cat at [index]. Returns whether the board is now solved. */
    fun toggle(index: Int): Boolean {
        if (index !in regions.indices) return false
        if (!cats.add(index)) cats.remove(index)
        return isSolved()
    }

    /** The offending cats plus the single rule to surface (highest priority when several break). */
    private data class Conflicts(val cells: Set<Int>, val violation: CatQueensUiState.Violation?)

    /** Cats that break a rule: sharing a row, column or zone with another cat, or touching one. */
    private fun conflicts(): Conflicts {
        val bad = mutableSetOf<Int>()
        var row = false
        var column = false
        var zone = false
        var touching = false
        val list = cats.toList()
        for (i in list.indices) {
            for (j in i + 1 until list.size) {
                val a = list[i]
                val b = list[j]
                val ar = a / size
                val ac = a % size
                val br = b / size
                val bc = b % size
                val sameRow = ar == br
                val sameColumn = ac == bc
                val sameZone = regions[a] == regions[b]
                val touch = abs(ar - br) <= 1 && abs(ac - bc) <= 1
                if (sameRow || sameColumn || sameZone || touch) {
                    bad.add(a)
                    bad.add(b)
                    if (sameRow) row = true
                    if (sameColumn) column = true
                    if (sameZone) zone = true
                    if (touch) touching = true
                }
            }
        }
        val violation = when {
            row -> CatQueensUiState.Violation.ROW
            column -> CatQueensUiState.Violation.COLUMN
            zone -> CatQueensUiState.Violation.ZONE
            touching -> CatQueensUiState.Violation.TOUCHING
            else -> null
        }
        return Conflicts(bad, violation)
    }

    /** Solved when all [size] cats are placed and none breaks a rule: with no shared row/column,
     *  that forces exactly one per row and column, and the zone check forces one per zone. */
    private fun isSolved(): Boolean = cats.size == size && conflicts().cells.isEmpty()

    override fun isCorrect(input: String): Boolean = isSolved()

    override fun solution(): String = ""

    override fun hint(): String? = null

    override fun toUiState(): CatQueensUiState {
        val conflicts = conflicts()
        return CatQueensUiState(
            size = size,
            regions = regions.toList().toImmutableList(),
            cats = cats.toImmutableSet(),
            invalidCats = conflicts.cells.toImmutableSet(),
            level = level,
            violation = conflicts.violation,
        )
    }

    /**
     * A random column permutation where consecutive rows differ by at least two columns (so no two
     * cats touch). Greedy with restart: for boards of size >= 5 valid placements are plentiful, so
     * this converges immediately.
     */
    private fun randomPlacement(n: Int): IntArray {
        while (true) {
            val cols = IntArray(n) { -1 }
            val used = BooleanArray(n)
            var ok = true
            for (r in 0 until n) {
                val candidates = (0 until n)
                    .filter { c -> !used[c] && (r == 0 || abs(c - cols[r - 1]) >= 2) }
                    .shuffled(random)
                if (candidates.isEmpty()) {
                    ok = false
                    break
                }
                val c = candidates.first()
                cols[r] = c
                used[c] = true
            }
            if (ok) return cols
        }
    }

    /**
     * Grows [n] connected color regions outward from the [placement] cats via randomized
     * multi-source flood fill, so every region is connected and contains exactly one cat. The
     * region id for the cat in row r is r.
     */
    private fun growRegions(n: Int, placement: IntArray): IntArray {
        val region = IntArray(n * n) { -1 }
        val frontierCell = ArrayList<Int>()
        val frontierRegion = ArrayList<Int>()

        fun addNeighbors(cell: Int, reg: Int) {
            val r = cell / n
            val c = cell % n
            if (r > 0 && region[cell - n] == -1) { frontierCell.add(cell - n); frontierRegion.add(reg) }
            if (r < n - 1 && region[cell + n] == -1) { frontierCell.add(cell + n); frontierRegion.add(reg) }
            if (c > 0 && region[cell - 1] == -1) { frontierCell.add(cell - 1); frontierRegion.add(reg) }
            if (c < n - 1 && region[cell + 1] == -1) { frontierCell.add(cell + 1); frontierRegion.add(reg) }
        }

        for (r in 0 until n) {
            val cell = r * n + placement[r]
            region[cell] = r
            addNeighbors(cell, r)
        }

        while (frontierCell.isNotEmpty()) {
            val idx = random.nextInt(frontierCell.size)
            val cell = frontierCell[idx]
            val reg = frontierRegion[idx]
            val last = frontierCell.size - 1
            frontierCell[idx] = frontierCell[last]
            frontierRegion[idx] = frontierRegion[last]
            frontierCell.removeAt(last)
            frontierRegion.removeAt(last)
            if (region[cell] != -1) continue
            region[cell] = reg
            addNeighbors(cell, reg)
        }
        return region
    }

    /**
     * Counts valid solutions for the given [board], stopping once [limit] is reached (callers pass
     * 2 to test uniqueness). Assigns one cat per row by column, enforcing unused column, unused
     * region and a >= 2 column gap from the previous row (the only way two cats could touch).
     */
    internal fun countSolutions(board: IntArray, n: Int, limit: Int): Int {
        val usedCols = BooleanArray(n)
        val usedRegions = BooleanArray(n)
        var count = 0

        fun backtrack(row: Int, prevCol: Int) {
            if (count >= limit) return
            if (row == n) {
                count++
                return
            }
            for (c in 0 until n) {
                if (usedCols[c]) continue
                if (prevCol >= 0 && abs(c - prevCol) < 2) continue
                val region = board[row * n + c]
                if (usedRegions[region]) continue
                usedCols[c] = true
                usedRegions[region] = true
                backtrack(row + 1, c)
                usedCols[c] = false
                usedRegions[region] = false
                if (count >= limit) return
            }
        }
        backtrack(0, -1)
        return count
    }

    companion object {
        /** Upper bound on board regenerations while searching for a unique-solution puzzle. Unique
         *  5x5/6x6 boards turn up within a few draws; 7x7 needs ~170 on average, so the ceiling is
         *  generous. It is only a safety net: in practice a unique board is found far sooner. */
        private const val MAX_GENERATION_ATTEMPTS = 4000
    }
}
