package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.KnotUiState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlin.math.abs
import kotlin.random.Random

/**
 * Knot: a Numberlink / "Flow Free" puzzle. The board has several colored pairs of dots; the player
 * draws a path connecting each pair so that the paths never cross AND every cell is covered (the
 * full-coverage rule is what keeps the puzzle from being trivial). Colored dots only, no numbers.
 *
 * Level-based stateful puzzle modeled on [CatQueensGame] / [NurikabeGame] / [ShikakuGame]: one
 * puzzle per attempt, no timer, score = highest level reached. The player drags from an endpoint to
 * draw that color's path ([setPath]) and taps an endpoint/path to clear it ([clearPath]); the board
 * is solved when every pair is connected, no two paths overlap, and all cells are filled.
 *
 * Generation builds a board that is solvable by construction (decompose the whole grid into K
 * vertex-disjoint simple paths via a random Hamiltonian path cut into segments) and then
 * rejection-samples for a unique solution within a bounded solver budget. As with the sibling
 * puzzles, if uniqueness can't be cheaply confirmed it accepts any valid board: solve detection
 * accepts ANY full-coverage connection, so a non-unique board is still a fair puzzle.
 */
class KnotGame(
    level: Int = 1,
    private val random: Random = Random.Default,
) : Game() {
    override val adaptiveDifficulty: Boolean = false

    var level: Int = level.coerceAtLeast(1)
        private set

    var rows: Int = 5
        private set

    var cols: Int = 5
        private set

    /** color id (0 until pairCount) -> its two endpoint cell indices (row*cols+col). */
    internal val endpoints: MutableMap<Int, Pair<Int, Int>> = mutableMapOf()

    /** color id -> the ordered cells the player has drawn so far (first cell is always an endpoint). */
    internal val paths: MutableMap<Int, List<Int>> = mutableMapOf()

    /** The generator's own solution paths, kept for tests/regression; not used during play. */
    internal var generatedSolution: List<List<Int>> = emptyList()
        private set

    private data class Difficulty(val size: Int, val pairs: Int)

    // The board size ramps slowly: three levels are spent on each size, getting denser (more pairs,
    // so shorter, more tightly interwoven paths) before the grid grows. The top tier reaches 9x9 with
    // many pairs for a genuinely hard end game.
    private fun difficultyFor(level: Int): Difficulty = when (level) {
        1 -> Difficulty(size = 5, pairs = 3)
        2 -> Difficulty(size = 5, pairs = 4)
        3 -> Difficulty(size = 5, pairs = 5)
        4 -> Difficulty(size = 6, pairs = 4)
        5 -> Difficulty(size = 6, pairs = 5)
        6 -> Difficulty(size = 6, pairs = 6)
        7 -> Difficulty(size = 7, pairs = 5)
        8 -> Difficulty(size = 7, pairs = 6)
        9 -> Difficulty(size = 7, pairs = 7)
        10 -> Difficulty(size = 8, pairs = 6)
        11 -> Difficulty(size = 8, pairs = 7)
        12 -> Difficulty(size = 8, pairs = 8)
        13 -> Difficulty(size = 9, pairs = 7)
        14 -> Difficulty(size = 9, pairs = 8)
        else -> Difficulty(size = 9, pairs = 9)
    }

    override fun generateRound() {
        val difficulty = difficultyFor(level)
        rows = difficulty.size
        cols = difficulty.size
        paths.clear()

        // Rejection sampling for quality: prefer a uniquely-solvable board, but only spend the solver
        // on a bounded number of candidates so generation stays fast. Every candidate is solvable by
        // construction, and solve detection accepts any valid board, so the fallback is still fair.
        var lastSolution = buildSolution(difficulty).also { applyEndpoints(it) }
        var attempts = 1
        while (solutionCount(limit = 2) != 1 && attempts < MAX_GENERATION_ATTEMPTS) {
            lastSolution = buildSolution(difficulty).also { applyEndpoints(it) }
            attempts++
        }
        generatedSolution = lastSolution
    }

    /**
     * Decomposes the whole grid into [Difficulty.pairs] vertex-disjoint simple paths that cover every
     * cell: take a random Hamiltonian path and cut it into that many contiguous segments. Full
     * coverage and validity are guaranteed by construction; the segment ends become the endpoint
     * pairs.
     */
    private fun buildSolution(difficulty: Difficulty): List<List<Int>> {
        val hamiltonian = randomHamiltonianPath()
        return cutIntoSegments(hamiltonian, difficulty.pairs)
    }

    private fun applyEndpoints(solution: List<List<Int>>) {
        endpoints.clear()
        solution.forEachIndexed { color, segment ->
            endpoints[color] = segment.first() to segment.last()
        }
    }

    /**
     * A random Hamiltonian path over all cells, produced by repeatedly applying "backbite" moves to a
     * boustrophedon snake. A backbite picks a random grid-neighbour q of the path's head p0; if q sits
     * at position k in the path, reversing the prefix p0..p(k-1) yields another Hamiltonian path with a
     * new head. This preserves the Hamiltonian invariant every step, so it can never fail, and mixes
     * quickly for the small boards used here.
     */
    private fun randomHamiltonianPath(): MutableList<Int> {
        val path = snakePath().toMutableList()
        // A handful of backbite passes is plenty of variety; n^2 was needlessly expensive on big
        // boards (it dominated generation time without improving the puzzles).
        val iterations = path.size * BACKBITE_PASSES
        repeat(iterations) {
            // Occasionally flip the whole path so backbites act on the other end too.
            if (random.nextBoolean()) path.reverse()
            val head = path[0]
            val neighbors = neighbors(head)
            val q = neighbors[random.nextInt(neighbors.size)]
            val k = path.indexOf(q)
            if (k > 1) {
                var i = 0
                var j = k - 1
                while (i < j) {
                    val tmp = path[i]
                    path[i] = path[j]
                    path[j] = tmp
                    i++
                    j--
                }
            }
        }
        return path
    }

    /** Boustrophedon ("snake") Hamiltonian path: always valid, used as the backbite seed. */
    private fun snakePath(): List<Int> {
        val out = ArrayList<Int>(rows * cols)
        for (r in 0 until rows) {
            if (r % 2 == 0) {
                for (c in 0 until cols) out.add(r * cols + c)
            } else {
                for (c in cols - 1 downTo 0) out.add(r * cols + c)
            }
        }
        return out
    }

    /**
     * Cuts [path] into [k] contiguous segments, each at least [MIN_SEGMENT_LENGTH] long so a pair's
     * endpoints are never trivially adjacent. The spare length beyond the minimums is scattered
     * randomly so segment sizes vary.
     */
    private fun cutIntoSegments(path: List<Int>, k: Int): List<List<Int>> {
        val sizes = IntArray(k) { MIN_SEGMENT_LENGTH }
        var extra = path.size - k * MIN_SEGMENT_LENGTH
        while (extra > 0) {
            sizes[random.nextInt(k)]++
            extra--
        }
        val segments = ArrayList<List<Int>>(k)
        var index = 0
        for (size in sizes) {
            segments.add(path.subList(index, index + size).toList())
            index += size
        }
        return segments
    }

    /**
     * Stores the path the player drew for [color] from the raw drag cells, sanitising it into a valid
     * contiguous chain that starts at one of the color's endpoints, and truncating any other color's
     * path at cells this one now claims (Flow Free overwrite). Returns whether the board is now solved.
     */
    fun setPath(color: Int, cells: List<Int>): Boolean {
        val endpointPair = endpoints[color] ?: return isSolved()
        val sanitized = sanitizePath(color, cells, endpointPair)
        if (sanitized.size < 2) {
            paths.remove(color)
        } else {
            paths[color] = sanitized
            truncateOthers(color, sanitized.toHashSet())
        }
        return isSolved()
    }

    /** Removes the path drawn for [color]. Returns whether the board is now solved. */
    fun clearPath(color: Int): Boolean {
        paths.remove(color)
        return isSolved()
    }

    /**
     * Turns a raw sequence of dragged cells into a valid path: it must start on one of [color]'s
     * endpoints, advance to orthogonally-adjacent cells, never reuse a cell (dragging back over the
     * path trims it), never pass through another color's endpoint, and it stops once it reaches the
     * partner endpoint. Gaps from a fast drag are bridged with a short L-shaped run when every bridged
     * cell is free.
     */
    private fun sanitizePath(color: Int, raw: List<Int>, endpointPair: Pair<Int, Int>): List<Int> {
        if (raw.isEmpty()) return emptyList()
        val start = raw.first()
        val (a, b) = endpointPair
        if (start != a && start != b) return emptyList()
        val other = if (start == a) b else a

        val result = ArrayList<Int>()
        result.add(start)
        if (start == other) return result // degenerate single-cell endpoint pair, shouldn't happen

        for (i in 1 until raw.size) {
            if (result.last() == other) break
            val cell = raw[i]
            if (cell !in 0 until rows * cols) continue
            if (cell == result.last()) continue

            val existingIndex = result.indexOf(cell)
            if (existingIndex >= 0) {
                // Dragged back over our own path: trim back to the revisited cell.
                while (result.size > existingIndex + 1) result.removeAt(result.size - 1)
                continue
            }

            val steps = if (areAdjacent(result.last(), cell)) {
                listOf(cell)
            } else {
                bridge(result.last(), cell)
            }
            if (steps == null) break

            var blocked = false
            for (step in steps) {
                if (step in result) { blocked = true; break }
                if (step != other && isForeignEndpoint(color, step)) { blocked = true; break }
                result.add(step)
                if (step == other) break
            }
            if (blocked) break
        }
        return result
    }

    /**
     * The cells of a short horizontal-then-vertical L-run from [from] to [to] (excluding [from],
     * including [to]). Used only to bridge a small gap left by a fast drag; the caller validates each
     * bridged cell and stops if one is blocked, and longer detours are left to the player.
     */
    private fun bridge(from: Int, to: Int): List<Int>? {
        val fr = from / cols
        val fc = from % cols
        val tr = to / cols
        val tc = to % cols
        if (abs(fr - tr) + abs(fc - tc) > MAX_BRIDGE_DISTANCE) return null

        val cells = ArrayList<Int>()
        var r = fr
        var c = fc
        while (c != tc) { c += if (tc > c) 1 else -1; cells.add(r * cols + c) }
        while (r != tr) { r += if (tr > r) 1 else -1; cells.add(r * cols + c) }
        return cells
    }

    private fun truncateOthers(color: Int, claimed: Set<Int>) {
        for ((otherColor, otherPath) in paths.toMap()) {
            if (otherColor == color) continue
            val cut = otherPath.indexOfFirst { it in claimed }
            if (cut >= 0) {
                val trimmed = otherPath.subList(0, cut)
                if (trimmed.size < 2) paths.remove(otherColor) else paths[otherColor] = trimmed.toList()
            }
        }
    }

    private fun isForeignEndpoint(color: Int, cell: Int): Boolean {
        for ((other, pair) in endpoints) {
            if (other == color) continue
            if (cell == pair.first || cell == pair.second) return true
        }
        return false
    }

    /** Solved when every pair is connected by a valid path, no two paths overlap, and all cells are filled. */
    private fun isSolved(): Boolean {
        if (paths.size != endpoints.size) return false
        val covered = BooleanArray(rows * cols)
        for ((color, endpointPair) in endpoints) {
            val path = paths[color] ?: return false
            if (!isCompletePath(path, endpointPair)) return false
            for (cell in path) {
                if (covered[cell]) return false // two paths cross
                covered[cell] = true
            }
        }
        return covered.all { it } // full coverage
    }

    /** True when [path] is a simple orthogonal chain whose two ends are exactly the color's endpoints. */
    private fun isCompletePath(path: List<Int>, endpointPair: Pair<Int, Int>): Boolean {
        if (path.size < 2) return false
        val ends = setOf(path.first(), path.last())
        if (ends != setOf(endpointPair.first, endpointPair.second)) return false
        val seen = HashSet<Int>(path.size)
        for (i in path.indices) {
            if (!seen.add(path[i])) return false
            if (i > 0 && !areAdjacent(path[i - 1], path[i])) return false
        }
        return true
    }

    private fun areAdjacent(a: Int, b: Int): Boolean {
        val ar = a / cols
        val ac = a % cols
        val br = b / cols
        val bc = b % cols
        return (ar == br && abs(ac - bc) == 1) || (ac == bc && abs(ar - br) == 1)
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
     * Counts full-coverage solutions for the current endpoints, stopping once [limit] is reached
     * (callers pass 2 to test uniqueness). Grows each color's path in turn from its first endpoint to
     * its second through still-empty cells; a complete assignment that fills every cell is a solution.
     * A frozen-stranded-cell prune and a node budget keep the search bounded, mirroring
     * [NurikabeGame.solutionCount]; hitting the budget reports "not unique" so generation simply keeps
     * looking rather than accepting an unverified board.
     */
    internal fun solutionCount(limit: Int): Int {
        val total = rows * cols
        val colors = endpoints.keys.sorted()
        val owner = IntArray(total) { UNASSIGNED }
        for (color in colors) {
            val (a, b) = endpoints.getValue(color)
            owner[a] = color
            owner[b] = color
        }
        var count = 0
        var budget = SOLUTION_NODE_BUDGET

        // An empty cell whose every neighbour is owned by an already-completed color can never be
        // filled (completed colors never move, future paths only grow through empty cells), so the
        // current partial assignment is dead. Safe: it ignores the in-progress and future colors.
        fun hasFrozenStrandedEmpty(completedBelow: Int): Boolean {
            for (cell in 0 until total) {
                if (owner[cell] != UNASSIGNED) continue
                var reachable = false
                for (nb in neighbors(cell)) {
                    val o = owner[nb]
                    if (o == UNASSIGNED || o >= completedBelow) { reachable = true; break }
                }
                if (!reachable) return true
            }
            return false
        }

        // Mutually recursive: extend grows the current color, then hands off to solveColor for the
        // next. solveColor is a captured var so extend can call it before it is assigned below.
        var solveColor: (Int) -> Unit = {}

        fun extend(colorIndex: Int, color: Int, target: Int, head: Int) {
            if (count >= limit) return
            if (budget-- <= 0) { count = limit; return }
            for (nb in neighbors(head)) {
                if (nb == target) {
                    solveColor(colorIndex + 1)
                } else if (owner[nb] == UNASSIGNED) {
                    owner[nb] = color
                    extend(colorIndex, color, target, nb)
                    owner[nb] = UNASSIGNED
                }
                if (count >= limit) return
            }
        }

        solveColor = solveColor@{ colorIndex ->
            if (count >= limit) return@solveColor
            if (budget-- <= 0) { count = limit; return@solveColor }
            if (colorIndex > 0 && hasFrozenStrandedEmpty(colorIndex)) return@solveColor
            if (colorIndex == colors.size) {
                if (owner.none { it == UNASSIGNED }) count++
                return@solveColor
            }
            val color = colors[colorIndex]
            val (a, b) = endpoints.getValue(color)
            extend(colorIndex, color, b, a)
        }

        solveColor(0)
        return count
    }

    override fun isCorrect(input: String): Boolean = isSolved()

    override fun solution(): String = ""

    override fun hint(): String? = null

    override fun toUiState(): KnotUiState = KnotUiState(
        rows = rows,
        cols = cols,
        endpoints = endpoints.entries
            .sortedBy { it.key }
            .map { KnotUiState.Endpoint(it.key, it.value.first, it.value.second) }
            .toImmutableList(),
        paths = paths.mapValues { it.value.toImmutableList() }.toImmutableMap(),
        level = level,
    )

    companion object {
        /** Shortest allowed path between a pair's endpoints, so no pair is trivially adjacent. */
        private const val MIN_SEGMENT_LENGTH = 3

        /** Largest gap (in cells) a fast drag may leave before the path stops growing. */
        private const val MAX_BRIDGE_DISTANCE = 3

        private const val UNASSIGNED = -1

        /** Backbite passes used to shuffle the seed snake into a varied Hamiltonian path. */
        private const val BACKBITE_PASSES = 8

        /** Upper bound on board regenerations while searching for a unique-solution puzzle. Kept small
         *  because large boards rarely verify as unique with this generator, so extra attempts mostly
         *  burn time; solve detection accepts any valid board, so a non-unique fallback is still fair. */
        private const val MAX_GENERATION_ATTEMPTS = 60

        /** Upper bound on solver nodes per uniqueness check, so generation can never stall. Small
         *  boards verify in well under this; large boards exceed it (and are accepted as-is), so this
         *  mainly caps the time spent failing to verify a hard board. */
        private const val SOLUTION_NODE_BUDGET = 12_000
    }
}
