package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.NurikabeUiState
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toImmutableSet
import kotlin.random.Random

/**
 * Nurikabe: paint the "sea" (walls) around numbered "islands" so that
 *  - every numbered clue belongs to one island of exactly that many connected (orthogonal) cells,
 *  - each island contains exactly one clue,
 *  - islands never touch each other orthogonally (the sea separates them),
 *  - the sea is a single orthogonally-connected region, and
 *  - the sea contains no 2x2 block of cells.
 *
 * Level-based stateful puzzle modeled on [ShikakuGame] and [LightsOutGame]: one puzzle per attempt,
 * no timer, score = highest level reached. The player paints/erases sea cells; [toggleWall] flips a
 * single cell, [setWalls] paints a stroke. The board is solved when the painted cells satisfy every
 * rule above. Solve detection accepts ANY valid sea, which is the correct Nurikabe semantics.
 *
 * Generation prefers a uniquely-solvable board but only within a small bounded solver budget; if it
 * can't cheaply confirm uniqueness it accepts any structurally valid (always solvable) board, the
 * same tradeoff [ShikakuGame] documents. Because solve detection accepts any valid sea, a non-unique
 * board is still a fair puzzle.
 */
class NurikabeGame(
    level: Int = 1,
    private val random: Random = Random.Default,
) : Game() {
    override val adaptiveDifficulty: Boolean = false

    var level: Int = level.coerceAtLeast(1)
        private set

    var rows: Int = 5
        internal set

    var cols: Int = 5
        internal set

    /** cellIndex (row*cols+col) -> island size. Each clue cell is white and anchors one island. */
    internal val clues: MutableMap<Int, Int> = mutableMapOf()

    /** Player-painted sea (wall) cells, by cellIndex. Clue cells are never paintable. */
    internal val walls: MutableSet<Int> = mutableSetOf()

    /** The generator's own sea, kept for tests/regression; not used during play. */
    internal var generatedSea: Set<Int> = emptySet()
        private set

    /** The generator's own islands (each a set of cellIndexes), kept for tests; not used in play. */
    internal var generatedIslands: List<Set<Int>> = emptyList()
        private set

    private data class Difficulty(
        val rows: Int,
        val cols: Int,
        /** Preferred number of islands; boards outside the range are kept only as a fallback. */
        val islandCount: IntRange,
        /** Boards with a larger island are rejected, keeping puzzles clean and the solver bounded. */
        val maxIslandSize: Int,
        /** Target proportion of cells painted as sea before growth stops. */
        val seaFraction: Double,
    )

    private fun difficultyFor(level: Int): Difficulty = when {
        level <= 3 -> Difficulty(rows = 5, cols = 5, islandCount = 3..6, maxIslandSize = 5, seaFraction = 0.5)
        level <= 6 -> Difficulty(rows = 6, cols = 6, islandCount = 4..9, maxIslandSize = 6, seaFraction = 0.5)
        level <= 9 -> Difficulty(rows = 7, cols = 7, islandCount = 5..11, maxIslandSize = 6, seaFraction = 0.5)
        else -> Difficulty(rows = 7, cols = 7, islandCount = 4..10, maxIslandSize = 7, seaFraction = 0.46)
    }

    private data class Solution(
        val islands: List<Set<Int>>,
        val sea: Set<Int>,
        val clues: Map<Int, Int>,
    )

    override fun generateRound() {
        val difficulty = difficultyFor(level)
        rows = difficulty.rows
        cols = difficulty.cols
        walls.clear()

        // Rejection sampling for quality: prefer a uniquely-solvable board with the desired island
        // count, but only spend the solver on the first few candidates so generation stays fast. If
        // none of those are unique we accept the next valid board anyway: every candidate is solvable
        // by construction and solve detection accepts any valid sea, so a non-unique board is still a
        // fair puzzle (the same tradeoff [ShikakuGame] documents). [fallback] keeps the best valid
        // board seen so a board whose island count is merely outside the preferred range still beats
        // the degenerate fallback.
        var fallback: Solution? = null
        var chosen: Solution? = null
        var uniquenessTries = 0
        var attempts = 0
        while (chosen == null && attempts < MAX_GENERATION_ATTEMPTS) {
            attempts++
            val solution = buildSolution(difficulty) ?: continue
            val preferred = solution.islands.size in difficulty.islandCount
            // Keep the first valid board, then upgrade to one with a preferred island count.
            if (fallback == null || (preferred && fallback!!.islands.size !in difficulty.islandCount)) {
                fallback = solution
            }
            if (!preferred) continue
            if (uniquenessTries >= UNIQUENESS_TRIES) {
                chosen = solution // stop paying for the solver; this valid board is good enough
                break
            }
            uniquenessTries++
            applySolution(solution)
            if (solutionCount(limit = 2) == 1) chosen = solution
        }

        val solution = chosen ?: fallback ?: degenerateFallback()
        applySolution(solution)
        generatedSea = solution.sea
        generatedIslands = solution.islands
    }

    private fun applySolution(solution: Solution) {
        clues.clear()
        clues.putAll(solution.clues)
    }

    /**
     * Grows the sea (wall) outward from a random seed, adding one frontier cell at a time and only
     * when doing so would not complete a 2x2 sea block, until it reaches the target sea fraction or
     * can grow no further. Because the sea starts from one cell and only ever absorbs cells adjacent
     * to it, the result is automatically connected and pool-free; the white cells left behind form
     * the islands, which are pairwise non-adjacent precisely because the sea separates them. This is
     * far more reliable than growing islands first and hoping the leftover sea happens to be legal.
     *
     * Returns null only for a board with fewer than two islands or one island larger than
     * [Difficulty.maxIslandSize] (kept small so puzzles stay clean and the uniqueness solver stays
     * bounded), in which case the caller resamples.
     */
    private fun buildSolution(difficulty: Difficulty): Solution? {
        val total = rows * cols
        val isSea = BooleanArray(total)
        val inFrontier = BooleanArray(total)
        // A cell that would complete a 2x2 pool can never become addable, since the sea only grows.
        val blocked = BooleanArray(total)
        val frontier = ArrayList<Int>()

        fun pushFrontier(cell: Int) {
            if (!isSea[cell] && !inFrontier[cell] && !blocked[cell]) {
                inFrontier[cell] = true
                frontier.add(cell)
            }
        }

        val seed = random.nextInt(total)
        isSea[seed] = true
        var seaCount = 1
        for (n in neighbors(seed)) pushFrontier(n)

        val targetSea = (total * difficulty.seaFraction).toInt().coerceIn(1, total - 1)
        while (seaCount < targetSea && frontier.isNotEmpty()) {
            val pick = random.nextInt(frontier.size)
            val cell = frontier[pick]
            frontier[pick] = frontier[frontier.size - 1]
            frontier.removeAt(frontier.size - 1)
            inFrontier[cell] = false
            if (wouldCompletePool(cell, isSea)) {
                blocked[cell] = true
                continue
            }
            isSea[cell] = true
            seaCount++
            for (n in neighbors(cell)) pushFrontier(n)
        }

        val sea = (0 until total).filter { isSea[it] }.toSet()
        val islands = whiteComponents(isSea)
        if (islands.size < 2) return null
        if (islands.any { it.size > difficulty.maxIslandSize }) return null

        val clueMap = HashMap<Int, Int>(islands.size)
        for (island in islands) clueMap[island.random(random)] = island.size
        return Solution(islands, sea, clueMap)
    }

    /** True when painting [cell] as sea would complete a 2x2 all-sea block in any of its corners. */
    private fun wouldCompletePool(cell: Int, isSea: BooleanArray): Boolean {
        val r = cell / cols
        val c = cell % cols
        for (topRow in (r - 1)..r) {
            for (leftCol in (c - 1)..c) {
                if (topRow < 0 || leftCol < 0 || topRow + 1 >= rows || leftCol + 1 >= cols) continue
                val topLeft = topRow * cols + leftCol
                val others = intArrayOf(topLeft, topLeft + 1, topLeft + cols, topLeft + cols + 1)
                if (others.all { it == cell || isSea[it] }) return true
            }
        }
        return false
    }

    /** Connected components of the non-sea (white) cells; each becomes one island. */
    private fun whiteComponents(isSea: BooleanArray): List<Set<Int>> {
        val total = rows * cols
        val visited = BooleanArray(total)
        val components = ArrayList<Set<Int>>()
        for (start in 0 until total) {
            if (isSea[start] || visited[start]) continue
            val component = HashSet<Int>()
            val stack = ArrayDeque<Int>()
            stack.addLast(start)
            visited[start] = true
            while (stack.isNotEmpty()) {
                val cell = stack.removeLast()
                component.add(cell)
                for (n in neighbors(cell)) {
                    if (!isSea[n] && !visited[n]) {
                        visited[n] = true
                        stack.addLast(n)
                    }
                }
            }
            components.add(component)
        }
        return components
    }

    /** Single-island, sea-less board used only if sampling never yields a structurally valid board. */
    private fun degenerateFallback(): Solution {
        val all = (0 until rows * cols).toSet()
        return Solution(listOf(all), emptySet(), mapOf(0 to all.size))
    }

    /**
     * Toggles the sea cell at [index]. Clue cells and out-of-range indices are ignored. Returns
     * whether the board is now solved.
     */
    fun toggleWall(index: Int): Boolean {
        if (index in 0 until rows * cols && index !in clues) {
            if (!walls.add(index)) walls.remove(index)
        }
        return isSolved()
    }

    /**
     * Paints (or erases) sea over a set of cells in one stroke. Clue cells and out-of-range indices
     * are skipped. Returns whether the board is now solved.
     */
    fun setWalls(indices: List<Int>, wall: Boolean): Boolean {
        for (index in indices) {
            if (index !in 0 until rows * cols || index in clues) continue
            if (wall) walls.add(index) else walls.remove(index)
        }
        return isSolved()
    }

    private fun isSolved(): Boolean {
        val total = rows * cols
        if (clues.keys.any { it in walls }) return false

        // Every white (non-sea) component must hold exactly one clue equal to its cell count.
        val visited = BooleanArray(total)
        for (start in 0 until total) {
            if (start in walls || visited[start]) continue
            var size = 0
            var clueCount = 0
            var clueValue = -1
            val stack = ArrayDeque<Int>()
            stack.addLast(start)
            visited[start] = true
            while (stack.isNotEmpty()) {
                val cell = stack.removeLast()
                size++
                clues[cell]?.let {
                    clueCount++
                    clueValue = it
                }
                for (n in neighbors(cell)) {
                    if (n !in walls && !visited[n]) {
                        visited[n] = true
                        stack.addLast(n)
                    }
                }
            }
            if (clueCount != 1 || clueValue != size) return false
        }

        if (!isConnected(walls)) return false
        if (hasSeaPool(walls)) return false
        return true
    }

    /** True when [cells] form a single orthogonally-connected region (empty counts as connected). */
    private fun isConnected(cells: Set<Int>): Boolean {
        if (cells.isEmpty()) return true
        val start = cells.first()
        val seen = HashSet<Int>(cells.size)
        val stack = ArrayDeque<Int>()
        stack.addLast(start)
        seen.add(start)
        while (stack.isNotEmpty()) {
            val cell = stack.removeLast()
            for (n in neighbors(cell)) {
                if (n in cells && seen.add(n)) stack.addLast(n)
            }
        }
        return seen.size == cells.size
    }

    /** True when [sea] fully covers any 2x2 block, which Nurikabe forbids. */
    private fun hasSeaPool(sea: Set<Int>): Boolean {
        for (r in 0 until rows - 1) {
            for (c in 0 until cols - 1) {
                val topLeft = r * cols + c
                if (topLeft in sea &&
                    topLeft + 1 in sea &&
                    topLeft + cols in sea &&
                    topLeft + cols + 1 in sea
                ) {
                    return true
                }
            }
        }
        return false
    }

    private fun neighbors(index: Int): List<Int> {
        val r = index / cols
        val c = index % cols
        val out = ArrayList<Int>(4)
        if (r > 0) out.add(index - cols)
        if (r < rows - 1) out.add(index + cols)
        if (c > 0) out.add(index - 1)
        if (c < cols - 1) out.add(index + 1)
        return out
    }

    /**
     * Counts how many ways the current clues can be solved, stopping once [limit] is reached (callers
     * pass 2 to merely test uniqueness). Mirrors [ShikakuGame.solutionCount]: each clue is assigned one
     * of its candidate islands; a complete set of pairwise non-adjacent, non-overlapping choices whose
     * complement (the sea) is connected with no 2x2 pool is a solution.
     */
    internal fun solutionCount(limit: Int): Int {
        val total = rows * cols
        val clueList = clues.entries.toList()
        val candidates = ArrayList<List<Set<Int>>>(clueList.size)
        for (entry in clueList) {
            val islands = islandCandidates(entry.key, entry.value)
            if (islands.isEmpty()) return 0
            candidates.add(islands)
        }
        // Assign the most constrained clues first to prune the search early.
        val order = clueList.indices.sortedBy { candidates[it].size }
        val owner = IntArray(total) { UNASSIGNED }
        var count = 0
        // Bound the search so a pathological board can never stall generation. Hitting the budget
        // reports "not unique" (count = limit), so the generator simply keeps looking for a board it
        // can verify cheaply rather than accepting an unverified one.
        var budget = SOLUTION_NODE_BUDGET

        fun conflicts(island: Set<Int>): Boolean {
            for (cell in island) {
                if (owner[cell] != UNASSIGNED) return true
                for (n in neighbors(cell)) {
                    if (owner[n] != UNASSIGNED && n !in island) return true
                }
            }
            return false
        }

        fun seaIsValid(): Boolean {
            val sea = (0 until total).filter { owner[it] == UNASSIGNED }.toSet()
            return isConnected(sea) && !hasSeaPool(sea)
        }

        fun backtrack(depth: Int) {
            if (count >= limit) return
            if (budget-- <= 0) {
                count = limit // treat an over-budget board as not uniquely verifiable
                return
            }
            if (depth == order.size) {
                if (seaIsValid()) count++
                return
            }
            for (island in candidates[order[depth]]) {
                if (conflicts(island)) continue
                for (cell in island) owner[cell] = order[depth]
                backtrack(depth + 1)
                for (cell in island) owner[cell] = UNASSIGNED
                if (count >= limit) return
            }
        }
        backtrack(0)
        return count
    }

    /**
     * All islands of the given [size] that contain [clueIndex], hold no other clue, and never touch
     * another clue cell (which would force two islands to be adjacent). Duplicate-free connected-set
     * enumeration so larger clues stay tractable.
     */
    private fun islandCandidates(clueIndex: Int, size: Int): List<Set<Int>> {
        val total = rows * cols
        val forbidden = BooleanArray(total)
        for ((index, _) in clues) {
            if (index == clueIndex) continue
            forbidden[index] = true
            for (n in neighbors(index)) forbidden[n] = true
        }
        if (forbidden[clueIndex]) return emptyList()

        val results = ArrayList<Set<Int>>()
        val current = HashSet<Int>()
        current.add(clueIndex)
        val excluded = HashSet<Int>()
        val extension = neighbors(clueIndex).filter { !forbidden[it] }
        enumerateConnected(current, extension, excluded, size, forbidden, results)
        return results
    }

    /**
     * Emits every connected set of [size] cells reachable from [current] using the standard
     * include/exclude branching over an ordered [extension] frontier, which visits each set once.
     */
    private fun enumerateConnected(
        current: HashSet<Int>,
        extension: List<Int>,
        excluded: HashSet<Int>,
        size: Int,
        forbidden: BooleanArray,
        results: MutableList<Set<Int>>,
    ) {
        if (current.size == size) {
            results.add(HashSet(current))
            return
        }
        if (extension.isEmpty()) return

        val cell = extension[0]
        val rest = extension.subList(1, extension.size)

        // Branch 1: include cell, extending the frontier with its fresh neighbours.
        current.add(cell)
        val grown = ArrayList(rest)
        for (n in neighbors(cell)) {
            if (!forbidden[n] && n !in current && n !in excluded && n !in grown) grown.add(n)
        }
        enumerateConnected(current, grown, excluded, size, forbidden, results)
        current.remove(cell)

        // Branch 2: exclude cell for the remainder of this subtree.
        excluded.add(cell)
        enumerateConnected(current, rest, excluded, size, forbidden, results)
        excluded.remove(cell)
    }

    override fun isCorrect(input: String): Boolean = isSolved()

    override fun solution(): String = ""

    override fun hint(): String? = null

    override fun toUiState(): NurikabeUiState {
        val satisfied = HashSet<Int>()
        val invalid = HashSet<Int>()
        val visited = BooleanArray(rows * cols)
        for (start in 0 until rows * cols) {
            if (start in walls || visited[start]) continue
            val region = ArrayList<Int>()
            var clueCount = 0
            var clueValue = -1
            val stack = ArrayDeque<Int>()
            stack.addLast(start)
            visited[start] = true
            while (stack.isNotEmpty()) {
                val cell = stack.removeLast()
                region.add(cell)
                clues[cell]?.let {
                    clueCount++
                    clueValue = it
                }
                for (n in neighbors(cell)) {
                    if (n !in walls && !visited[n]) {
                        visited[n] = true
                        stack.addLast(n)
                    }
                }
            }
            // Only single-clue regions get a verdict; multi-clue regions are still in progress.
            if (clueCount == 1) {
                if (region.size == clueValue) {
                    satisfied.addAll(region)
                } else if (region.size > clueValue) {
                    invalid.addAll(region)
                }
            }
        }

        val pool = HashSet<Int>()
        for (r in 0 until rows - 1) {
            for (c in 0 until cols - 1) {
                val topLeft = r * cols + c
                if (topLeft in walls &&
                    topLeft + 1 in walls &&
                    topLeft + cols in walls &&
                    topLeft + cols + 1 in walls
                ) {
                    pool.add(topLeft)
                    pool.add(topLeft + 1)
                    pool.add(topLeft + cols)
                    pool.add(topLeft + cols + 1)
                }
            }
        }

        return NurikabeUiState(
            rows = rows,
            cols = cols,
            clues = clues.toImmutableMap(),
            walls = walls.toImmutableSet(),
            satisfiedCells = satisfied.toImmutableSet(),
            invalidCells = invalid.toImmutableSet(),
            poolCells = pool.toImmutableSet(),
            level = level,
        )
    }

    companion object {
        private const val UNASSIGNED = -1

        /** Upper bound on board samples while searching for a structurally valid puzzle. */
        private const val MAX_GENERATION_ATTEMPTS = 400

        /** How many valid candidates to run the uniqueness solver on before accepting any valid one. */
        private const val UNIQUENESS_TRIES = 8

        /** Upper bound on solver nodes per uniqueness check, so generation can never stall. */
        private const val SOLUTION_NODE_BUDGET = 20_000
    }
}
