package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.ShikakuUiState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlin.random.Random

/**
 * Shikaku: divide the whole grid into rectangles so that each rectangle contains exactly one
 * number, equal to its area. Level-based stateful puzzle modeled on [LightsOutGame]: one puzzle
 * per attempt, no timer, score = highest level reached.
 *
 * The player draws rectangles (drag-to-draw in the UI). [commitRectangle] adds a rectangle,
 * replacing any it overlaps; [deleteRectangleAt] removes one. The board is solved when the
 * rectangles form a valid partition (full coverage, no overlap, each rect holds exactly one
 * clue whose value equals the rect's area). Solve detection accepts ANY valid partition, which
 * is the correct Shikaku semantics.
 */
class ShikakuGame(
    level: Int = 1,
    private val random: Random = Random.Default,
) : Game() {
    override val adaptiveDifficulty: Boolean = false

    var level: Int = level.coerceAtLeast(1)
        private set

    var rows: Int = 4
        private set

    var cols: Int = 4
        private set

    /** cellIndex (row*cols+col) -> required area. */
    internal val clues: MutableMap<Int, Int> = mutableMapOf()

    /** Player-drawn rectangles with inclusive grid bounds. */
    internal val rectangles: MutableList<Rect> = mutableListOf()

    /** The generator's own partition, kept for tests/regression; not used during play. */
    internal var generatedSolution: List<Rect> = emptyList()
        private set

    /** Inclusive grid bounds. area = (bottom-top+1)*(right-left+1). */
    data class Rect(val top: Int, val left: Int, val bottom: Int, val right: Int) {
        val area: Int get() = (bottom - top + 1) * (right - left + 1)

        fun contains(r: Int, c: Int): Boolean = r in top..bottom && c in left..right

        fun intersects(other: Rect): Boolean =
            top <= other.bottom && bottom >= other.top && left <= other.right && right >= other.left
    }

    private data class Difficulty(
        val rows: Int,
        val cols: Int,
        val minArea: Int,
        val maxLeafArea: Int,
        val stopProb: Double,
    )

    private fun difficultyFor(level: Int): Difficulty = when {
        level <= 3 -> Difficulty(rows = 4, cols = 4, minArea = 2, maxLeafArea = 6, stopProb = 0.6)
        level <= 6 -> Difficulty(rows = 5, cols = 5, minArea = 2, maxLeafArea = 6, stopProb = 0.5)
        level <= 9 -> Difficulty(rows = 6, cols = 6, minArea = 2, maxLeafArea = 6, stopProb = 0.45)
        else -> Difficulty(rows = 7, cols = 7, minArea = 3, maxLeafArea = 8, stopProb = 0.4)
    }

    override fun generateRound() {
        val difficulty = difficultyFor(level)
        rows = difficulty.rows
        cols = difficulty.cols
        rectangles.clear()

        // Rejection sampling for quality: a good Shikaku has exactly one solution. Generate
        // candidate boards (each guaranteed solvable by construction) until one is unique, or
        // give up after a cap and keep the last solvable board so generation always terminates.
        var lastPartition = generatePartition(difficulty).also { assignClues(it) }
        var attempts = 1
        while (solutionCount(limit = 2) != 1 && attempts < MAX_GENERATION_ATTEMPTS) {
            lastPartition = generatePartition(difficulty).also { assignClues(it) }
            attempts++
        }
        generatedSolution = lastPartition
    }

    /**
     * Guillotine recursive split: always yields a valid partition by construction, and is
     * deterministic given the injected [random]. Recursively cuts the grid, only choosing cuts
     * that keep both children at or above [Difficulty.minArea], and stops randomly on small
     * rectangles to vary their sizes.
     *
     * Known tradeoff: this may admit boards with more than one valid partition, so a puzzle is
     * not guaranteed to have a unique solution. That is acceptable here because validation
     * accepts any valid partition (the correct Shikaku rule), and checking uniqueness would
     * require a full solver, which is out of scope for this casual game.
     */
    private fun generatePartition(difficulty: Difficulty): List<Rect> {
        val out = mutableListOf<Rect>()
        splitRect(Rect(0, 0, difficulty.rows - 1, difficulty.cols - 1), difficulty, out)
        return out
    }

    private fun splitRect(rect: Rect, difficulty: Difficulty, out: MutableList<Rect>) {
        val height = rect.bottom - rect.top + 1
        val width = rect.right - rect.left + 1
        val minArea = difficulty.minArea

        // Feasible cuts keep BOTH children at or above minArea.
        val verticalCuts = (1 until width).filter { k ->
            height * k >= minArea && height * (width - k) >= minArea
        }
        val horizontalCuts = (1 until height).filter { k ->
            width * k >= minArea && width * (height - k) >= minArea
        }

        val canSplit = verticalCuts.isNotEmpty() || horizontalCuts.isNotEmpty()
        if (!canSplit || (rect.area <= difficulty.maxLeafArea && random.nextDouble() < difficulty.stopProb)) {
            out.add(rect)
            return
        }

        val splitVertical = when {
            verticalCuts.isEmpty() -> false
            horizontalCuts.isEmpty() -> true
            else -> random.nextBoolean()
        }

        if (splitVertical) {
            val k = verticalCuts.random(random)
            splitRect(Rect(rect.top, rect.left, rect.bottom, rect.left + k - 1), difficulty, out)
            splitRect(Rect(rect.top, rect.left + k, rect.bottom, rect.right), difficulty, out)
        } else {
            val k = horizontalCuts.random(random)
            splitRect(Rect(rect.top, rect.left, rect.top + k - 1, rect.right), difficulty, out)
            splitRect(Rect(rect.top + k, rect.left, rect.bottom, rect.right), difficulty, out)
        }
    }

    private fun assignClues(partition: List<Rect>) {
        clues.clear()
        for (rect in partition) {
            val r = random.nextInt(rect.top, rect.bottom + 1)
            val c = random.nextInt(rect.left, rect.right + 1)
            clues[r * cols + c] = rect.area
        }
    }

    /**
     * Commits a rectangle between two (possibly out-of-range, unnormalized) corners. Removes any
     * existing rectangles it overlaps so re-dragging cleanly replaces them. Returns whether the
     * board is now solved.
     */
    fun commitRectangle(r1: Int, c1: Int, r2: Int, c2: Int): Boolean {
        val top = minOf(r1, r2).coerceIn(0, rows - 1)
        val bottom = maxOf(r1, r2).coerceIn(0, rows - 1)
        val left = minOf(c1, c2).coerceIn(0, cols - 1)
        val right = maxOf(c1, c2).coerceIn(0, cols - 1)
        val rect = Rect(top, left, bottom, right)
        rectangles.removeAll { it.intersects(rect) }
        rectangles.add(rect)
        return isSolved()
    }

    /** Removes the rectangle covering cell (r, c), if any. Returns whether the board is solved. */
    fun deleteRectangleAt(r: Int, c: Int): Boolean {
        rectangles.removeAll { it.contains(r, c) }
        return isSolved()
    }

    private fun isSolved(): Boolean {
        if (rectangles.sumOf { it.area } != rows * cols) return false
        val covered = BooleanArray(rows * cols)
        for (rect in rectangles) {
            for (r in rect.top..rect.bottom) {
                for (c in rect.left..rect.right) {
                    val index = r * cols + c
                    if (covered[index]) return false
                    covered[index] = true
                }
            }
        }
        if (covered.any { !it }) return false
        return rectangles.all { rectIsValid(it) }
    }

    /** A rectangle is valid when it contains exactly one clue and that clue equals its area. */
    private fun rectIsValid(rect: Rect): Boolean {
        var count = 0
        var clueValue = -1
        for ((index, value) in clues) {
            if (rect.contains(index / cols, index % cols)) {
                count++
                clueValue = value
            }
        }
        return count == 1 && clueValue == rect.area
    }

    /**
     * Counts how many ways the current clues can partition the grid, stopping once [limit] is
     * reached (callers pass 2 to merely test uniqueness). Each clue is assigned one of its
     * candidate rectangles; a complete set of pairwise-disjoint choices is a solution. Full
     * coverage follows automatically because the clue values sum to the grid area.
     */
    internal fun solutionCount(limit: Int): Int {
        val clueList = clues.entries.toList()
        val candidates = ArrayList<List<Rect>>(clueList.size)
        for (entry in clueList) {
            val rects = candidateRects(entry.key / cols, entry.key % cols, entry.value)
            if (rects.isEmpty()) return 0
            candidates.add(rects)
        }
        // Assign the most constrained clues first to prune the search early.
        val order = clueList.indices.sortedBy { candidates[it].size }
        val covered = BooleanArray(rows * cols)
        var count = 0

        fun backtrack(depth: Int) {
            if (count >= limit) return
            if (depth == order.size) {
                count++
                return
            }
            for (rect in candidates[order[depth]]) {
                var overlaps = false
                outer@ for (r in rect.top..rect.bottom) {
                    for (c in rect.left..rect.right) {
                        if (covered[r * cols + c]) {
                            overlaps = true
                            break@outer
                        }
                    }
                }
                if (overlaps) continue
                for (r in rect.top..rect.bottom) for (c in rect.left..rect.right) covered[r * cols + c] = true
                backtrack(depth + 1)
                for (r in rect.top..rect.bottom) for (c in rect.left..rect.right) covered[r * cols + c] = false
                if (count >= limit) return
            }
        }
        backtrack(0)
        return count
    }

    /** All rectangles of the given [area] that contain (clueRow, clueCol) and no other clue. */
    private fun candidateRects(clueRow: Int, clueCol: Int, area: Int): List<Rect> {
        val result = mutableListOf<Rect>()
        for (h in 1..rows) {
            if (area % h != 0) continue
            val w = area / h
            if (w > cols) continue
            for (top in maxOf(0, clueRow - h + 1)..minOf(clueRow, rows - h)) {
                for (left in maxOf(0, clueCol - w + 1)..minOf(clueCol, cols - w)) {
                    val rect = Rect(top, left, top + h - 1, left + w - 1)
                    if (containsOnlyClue(rect, clueRow, clueCol)) result.add(rect)
                }
            }
        }
        return result
    }

    private fun containsOnlyClue(rect: Rect, clueRow: Int, clueCol: Int): Boolean {
        for ((index, _) in clues) {
            val r = index / cols
            val c = index % cols
            if ((r != clueRow || c != clueCol) && rect.contains(r, c)) return false
        }
        return true
    }

    override fun isCorrect(input: String): Boolean = isSolved()

    override fun solution(): String = ""

    override fun hint(): String? = null

    override fun toUiState(): ShikakuUiState = ShikakuUiState(
        rows = rows,
        cols = cols,
        clues = clues.toImmutableMap(),
        rectangles = rectangles.map {
            ShikakuUiState.RectState(it.top, it.left, it.bottom, it.right, rectIsValid(it))
        }.toImmutableList(),
        level = level,
    )

    companion object {
        /** Upper bound on board regenerations while searching for a unique-solution puzzle. */
        private const val MAX_GENERATION_ATTEMPTS = 400
    }
}
