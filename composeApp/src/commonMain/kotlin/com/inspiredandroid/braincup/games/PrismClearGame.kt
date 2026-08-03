package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.PrismClearClearWave
import com.inspiredandroid.braincup.app.PrismClearUiState
import com.inspiredandroid.braincup.games.tools.GameColor
import com.inspiredandroid.braincup.games.tools.Shape
import com.inspiredandroid.braincup.games.tools.orthogonalNeighbors
import kotlinx.collections.immutable.toImmutableList

/**
 * Prism Clear: match-3 clear-the-board puzzle.
 *
 * Swap two orthogonally adjacent occupied tiles to form lines of ≥3. Matches clear (entire run),
 * gravity packs toward the bottom, cascades free; **no refill**. Win by emptying the board.
 * There is no move limit. [undo] reverts the last successful swap; [restart] restores the level.
 *
 * Boards come from the hand-authored catalog [PrismClearLevels].
 */
enum class PrismTileType(val color: GameColor, val shape: Shape) {
    RUBY(GameColor.RED, Shape.CIRCLE),
    EMERALD(GameColor.GREEN, Shape.SQUARE),
    SAPPHIRE(GameColor.BLUE, Shape.TRIANGLE),
    AMETHYST(GameColor.PURPLE, Shape.DIAMOND),
    TOPAZ(GameColor.ORANGE, Shape.STAR),
}

class PrismClearGame(
    level: Int = 1,
) : LevelGame(level, maxLevel = PrismClearLevels.COUNT) {

    var rows: Int = 2
        private set

    var cols: Int = 6
        private set

    var movesUsed: Int = 0
        private set

    var selectedIndex: Int? = null
        private set

    var rejectFeedbackKey: Int = 0
        private set

    var rejectedFrom: Int? = null
        private set

    var rejectedTo: Int? = null
        private set

    /** Row-major board; row 0 = top; gravity increases row. */
    internal var cells: Array<PrismTileType?> = emptyArray()
        private set

    private var initialCells: Array<PrismTileType?> = emptyArray()

    /** Snapshots of the board before each successful swap (for [undo]). */
    private val undoStack: ArrayDeque<Array<PrismTileType?>> = ArrayDeque()

    /** Catalog solution for tests. */
    internal var generatedSolution: List<Pair<Int, Int>> = emptyList()
        private set

    /** Cascade waves from the latest successful swap (for clear/fall animation). */
    private var lastClearWaves: List<PrismClearClearWave> = emptyList()

    /** Pre-swap board + indices for the swap slide animation. */
    private var lastCellsBeforeSwap: List<Int?> = emptyList()
    private var lastSwapFrom: Int? = null
    private var lastSwapTo: Int? = null

    private var boardAnimationKey: Int = 0

    enum class PrismClearResult {
        Updated,
        Solved,
        Rejected,
    }

    val canUndo: Boolean get() = undoStack.isNotEmpty()

    override fun generateRound() {
        val def = PrismClearLevels.forLevel(level)
        level = def.id
        rows = def.rows
        cols = def.cols
        selectedIndex = null
        rejectedFrom = null
        rejectedTo = null
        movesUsed = 0
        undoStack.clear()
        lastClearWaves = emptyList()
        lastCellsBeforeSwap = emptyList()
        lastSwapFrom = null
        lastSwapTo = null
        boardAnimationKey++

        cells = def.parseCells()
        generatedSolution = def.solution
        initialCells = cells.copyOf()
    }

    fun tap(index: Int): PrismClearResult {
        if (index !in cells.indices) return PrismClearResult.Updated
        val sel = selectedIndex
        val tile = cells[index]
        return when {
            tile == null -> {
                selectedIndex = null
                PrismClearResult.Updated
            }
            sel == null -> {
                selectedIndex = index
                PrismClearResult.Updated
            }
            sel == index -> {
                selectedIndex = null
                PrismClearResult.Updated
            }
            isOrthogonallyAdjacent(sel, index) && cells[sel] != null -> trySwap(sel, index)
            else -> {
                selectedIndex = index
                PrismClearResult.Updated
            }
        }
    }

    fun trySwap(a: Int, b: Int): PrismClearResult {
        if (!isLegalMatchingSwap(cells, rows, cols, a, b)) {
            rejectFeedbackKey++
            rejectedFrom = a
            rejectedTo = b
            selectedIndex = null
            return PrismClearResult.Rejected
        }
        undoStack.addLast(cells.copyOf())
        lastCellsBeforeSwap = cells.map { it?.ordinal }
        lastSwapFrom = a
        lastSwapTo = b
        swapCells(cells, a, b)
        lastClearWaves = captureCascadeWaves(cells, rows, cols)
        // Apply final settled board (waves already mutated [cells] through clear+gravity).
        movesUsed++
        selectedIndex = null
        rejectedFrom = null
        rejectedTo = null
        boardAnimationKey++
        if (boardIsEmpty()) return PrismClearResult.Solved
        return PrismClearResult.Updated
    }

    /** Reverts the last successful swap. No-op when nothing has been played. */
    fun undo(): Boolean {
        if (undoStack.isEmpty()) return false
        cells = undoStack.removeLast()
        movesUsed = (movesUsed - 1).coerceAtLeast(0)
        selectedIndex = null
        rejectedFrom = null
        rejectedTo = null
        lastClearWaves = emptyList()
        lastCellsBeforeSwap = emptyList()
        lastSwapFrom = null
        lastSwapTo = null
        boardAnimationKey++
        return true
    }

    fun restart() {
        cells = initialCells.copyOf()
        movesUsed = 0
        undoStack.clear()
        selectedIndex = null
        rejectedFrom = null
        rejectedTo = null
        lastClearWaves = emptyList()
        lastCellsBeforeSwap = emptyList()
        lastSwapFrom = null
        lastSwapTo = null
        boardAnimationKey++
    }

    fun hasAnyValidSwap(): Boolean = hasAnyValidSwap(cells, rows, cols)

    fun isStuck(): Boolean = !boardIsEmpty() && !hasAnyValidSwap()

    fun boardIsEmpty(): Boolean = cells.all { it == null }

    override fun isCorrect(input: String): Boolean = boardIsEmpty()

    override fun solution(): String = ""

    override fun hint(): String? = null

    override fun toUiState(): PrismClearUiState = PrismClearUiState(
        rows = rows,
        cols = cols,
        tileOrdinals = cells.map { it?.ordinal }.toImmutableList(),
        selectedIndex = selectedIndex,
        movesUsed = movesUsed,
        level = level,
        stuck = isStuck(),
        canUndo = canUndo,
        rejectedFrom = rejectedFrom,
        rejectedTo = rejectedTo,
        rejectFeedbackKey = rejectFeedbackKey,
        clearWaves = lastClearWaves.toImmutableList(),
        tileOrdinalsBeforeSwap = lastCellsBeforeSwap.toImmutableList(),
        swapFromIndex = lastSwapFrom,
        swapToIndex = lastSwapTo,
        boardAnimationKey = boardAnimationKey,
    )

    private fun isOrthogonallyAdjacent(a: Int, b: Int): Boolean {
        if (a !in cells.indices || b !in cells.indices) return false
        val ra = a / cols
        val ca = a % cols
        val rb = b / cols
        val cb = b % cols
        return kotlin.math.abs(ra - rb) + kotlin.math.abs(ca - cb) == 1
    }

    companion object {
        /** Builds a ready-to-render UI state for [level] (clamped to the catalog). */
        fun uiStateForLevel(level: Int): PrismClearUiState = PrismClearGame(level = level).apply { nextRound() }.toUiState()
    }
}

// ── Pure board utilities (play + tests) ──

internal fun isLegalMatchingSwap(
    cells: Array<PrismTileType?>,
    rows: Int,
    cols: Int,
    a: Int,
    b: Int,
): Boolean {
    if (a == b) return false
    if (a !in cells.indices || b !in cells.indices) return false
    val ra = a / cols
    val ca = a % cols
    val rb = b / cols
    val cb = b % cols
    if (kotlin.math.abs(ra - rb) + kotlin.math.abs(ca - cb) != 1) return false
    if (cells[a] == null || cells[b] == null) return false
    val copy = cells.copyOf()
    swapCells(copy, a, b)
    return findMatches(copy, rows, cols).isNotEmpty()
}

internal fun hasAnyValidSwap(cells: Array<PrismTileType?>, rows: Int, cols: Int): Boolean {
    for (i in cells.indices) {
        if (cells[i] == null) continue
        for (n in orthogonalNeighbors(i, rows, cols)) {
            if (n < i) continue
            if (cells[n] == null) continue
            if (isLegalMatchingSwap(cells, rows, cols, i, n)) return true
        }
    }
    return false
}

internal fun findMatches(cells: Array<PrismTileType?>, rows: Int, cols: Int): Set<Int> {
    val matched = mutableSetOf<Int>()
    for (r in 0 until rows) {
        var c = 0
        while (c < cols) {
            val t = cells[r * cols + c]
            if (t == null) {
                c++
                continue
            }
            var end = c + 1
            while (end < cols && cells[r * cols + end] == t) end++
            if (end - c >= 3) {
                for (x in c until end) matched.add(r * cols + x)
            }
            c = end
        }
    }
    for (c in 0 until cols) {
        var r = 0
        while (r < rows) {
            val t = cells[r * cols + c]
            if (t == null) {
                r++
                continue
            }
            var end = r + 1
            while (end < rows && cells[end * cols + c] == t) end++
            if (end - r >= 3) {
                for (y in r until end) matched.add(y * cols + c)
            }
            r = end
        }
    }
    return matched
}

internal fun applyGravity(cells: Array<PrismTileType?>, rows: Int, cols: Int) {
    for (c in 0 until cols) {
        val column = ArrayList<PrismTileType>(rows)
        for (r in 0 until rows) {
            cells[r * cols + c]?.let { column.add(it) }
        }
        val empty = rows - column.size
        for (r in 0 until rows) {
            cells[r * cols + c] = if (r < empty) null else column[r - empty]
        }
    }
}

internal fun resolveAllCascades(cells: Array<PrismTileType?>, rows: Int, cols: Int) {
    while (true) {
        val matches = findMatches(cells, rows, cols)
        if (matches.isEmpty()) break
        for (i in matches) cells[i] = null
        applyGravity(cells, rows, cols)
    }
}

/**
 * Clears matches and applies gravity wave-by-wave, recording each wave for UI animation.
 * Mutates [cells] to the fully settled board (same result as [resolveAllCascades]).
 */
internal fun captureCascadeWaves(
    cells: Array<PrismTileType?>,
    rows: Int,
    cols: Int,
): List<PrismClearClearWave> {
    val waves = ArrayList<PrismClearClearWave>()
    while (true) {
        val matches = findMatches(cells, rows, cols)
        if (matches.isEmpty()) break
        val before = cells.map { it?.ordinal }.toImmutableList()
        val cleared = matches.sorted()
        for (i in cleared) cells[i] = null
        applyGravity(cells, rows, cols)
        waves.add(
            PrismClearClearWave(
                cellsBeforeClear = before,
                clearedIndices = cleared.toImmutableList(),
                cellsAfterGravity = cells.map { it?.ordinal }.toImmutableList(),
            ),
        )
    }
    return waves
}

internal fun verifySolves(
    start: Array<PrismTileType?>,
    rows: Int,
    cols: Int,
    swaps: List<Pair<Int, Int>>,
): Boolean {
    val sim = start.copyOf()
    for ((a, b) in swaps) {
        if (sim.all { it == null }) return true
        if (!isLegalMatchingSwap(sim, rows, cols, a, b)) return false
        swapCells(sim, a, b)
        resolveAllCascades(sim, rows, cols)
    }
    return sim.all { it == null }
}

internal fun swapCells(cells: Array<PrismTileType?>, a: Int, b: Int) {
    val tmp = cells[a]
    cells[a] = cells[b]
    cells[b] = tmp
}

internal fun typeCountsAreMultiplesOfThree(cells: Array<PrismTileType?>): Boolean {
    val counts = IntArray(PrismTileType.entries.size)
    for (cell in cells) {
        if (cell != null) counts[cell.ordinal]++
    }
    return counts.all { it % 3 == 0 }
}
