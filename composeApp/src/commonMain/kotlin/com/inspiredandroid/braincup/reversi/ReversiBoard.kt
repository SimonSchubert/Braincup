package com.inspiredandroid.braincup.reversi

const val REVERSI_SIZE = 6

enum class Disc { BLACK, WHITE }

val Disc.opponent: Disc get() = if (this == Disc.BLACK) Disc.WHITE else Disc.BLACK

enum class ReversiResult { ONGOING, BLACK_WINS, WHITE_WINS, DRAW }

private const val EMPTY_CELL = 0
private const val BLACK_CELL = 1
private const val WHITE_CELL = 2

private val Disc.cell: Int get() = if (this == Disc.BLACK) BLACK_CELL else WHITE_CELL

private val DIRECTIONS = listOf(
    -1 to -1,
    -1 to 0,
    -1 to 1,
    0 to -1,
    0 to 1,
    1 to -1,
    1 to 0,
    1 to 1,
)

/**
 * Immutable 6x6 Reversi board. Cells are row-major flat indices, `row * REVERSI_SIZE + col`.
 *
 * [apply] always hands the turn to the opponent, even when the opponent is stuck. A pass is a
 * separate [passTurn] step so the caller can show it, and so the search counts it as a ply.
 */
class ReversiBoard private constructor(
    private val cells: IntArray,
    val sideToMove: Disc,
) {
    fun discAt(index: Int): Disc? = when (cells[index]) {
        BLACK_CELL -> Disc.BLACK
        WHITE_CELL -> Disc.WHITE
        else -> null
    }

    fun count(disc: Disc): Int = cells.count { it == disc.cell }

    fun emptyCount(): Int = cells.count { it == EMPTY_CELL }

    fun legalMoves(): List<Int> = legalMoves(sideToMove)

    fun legalMoves(disc: Disc): List<Int> = (0 until CELL_COUNT).filter { hasFlips(it, disc) }

    fun flipsFor(move: Int): List<Int> = flipsFor(move, sideToMove)

    fun flipsFor(move: Int, disc: Disc): List<Int> {
        if (move !in 0 until CELL_COUNT || cells[move] != EMPTY_CELL) return emptyList()
        val row = move / REVERSI_SIZE
        val col = move % REVERSI_SIZE
        val mine = disc.cell
        val theirs = disc.opponent.cell
        val flips = mutableListOf<Int>()
        for ((dRow, dCol) in DIRECTIONS) {
            var r = row + dRow
            var c = col + dCol
            val start = flips.size
            // Row and column are stepped and bounds-checked separately every square. Walking the
            // flat index by a single delta instead would leave one edge and reappear on the other.
            while (r in 0 until REVERSI_SIZE && c in 0 until REVERSI_SIZE && cells[r * REVERSI_SIZE + c] == theirs) {
                flips.add(r * REVERSI_SIZE + c)
                r += dRow
                c += dCol
            }
            val closed = flips.size > start &&
                r in 0 until REVERSI_SIZE &&
                c in 0 until REVERSI_SIZE &&
                cells[r * REVERSI_SIZE + c] == mine
            if (!closed) {
                while (flips.size > start) flips.removeAt(flips.size - 1)
            }
        }
        return flips
    }

    /** Apply a legal move. The move must come from [legalMoves] — undefined behaviour otherwise. */
    fun apply(move: Int): ReversiBoard {
        val flips = flipsFor(move)
        require(flips.isNotEmpty()) { "Illegal move: $move" }
        val next = cells.copyOf()
        next[move] = sideToMove.cell
        for (index in flips) next[index] = sideToMove.cell
        return ReversiBoard(next, sideToMove.opponent)
    }

    /** Hand the turn over without placing a disc. Only legal when the side to move is stuck. */
    fun passTurn(): ReversiBoard = ReversiBoard(cells, sideToMove.opponent)

    fun isGameOver(): Boolean = legalMoves(Disc.BLACK).isEmpty() && legalMoves(Disc.WHITE).isEmpty()

    fun result(): ReversiResult {
        if (!isGameOver()) return ReversiResult.ONGOING
        val black = count(Disc.BLACK)
        val white = count(Disc.WHITE)
        return when {
            black > white -> ReversiResult.BLACK_WINS
            white > black -> ReversiResult.WHITE_WINS
            else -> ReversiResult.DRAW
        }
    }

    fun snapshot(): List<Disc?> = (0 until CELL_COUNT).map { discAt(it) }

    private fun hasFlips(move: Int, disc: Disc): Boolean {
        if (cells[move] != EMPTY_CELL) return false
        val row = move / REVERSI_SIZE
        val col = move % REVERSI_SIZE
        val mine = disc.cell
        val theirs = disc.opponent.cell
        for ((dRow, dCol) in DIRECTIONS) {
            var r = row + dRow
            var c = col + dCol
            var seen = false
            while (r in 0 until REVERSI_SIZE && c in 0 until REVERSI_SIZE && cells[r * REVERSI_SIZE + c] == theirs) {
                seen = true
                r += dRow
                c += dCol
            }
            if (seen && r in 0 until REVERSI_SIZE && c in 0 until REVERSI_SIZE && cells[r * REVERSI_SIZE + c] == mine) {
                return true
            }
        }
        return false
    }

    companion object {
        const val CELL_COUNT = REVERSI_SIZE * REVERSI_SIZE

        fun startingPosition(): ReversiBoard {
            val cells = IntArray(CELL_COUNT)
            val low = REVERSI_SIZE / 2 - 1
            val high = REVERSI_SIZE / 2
            cells[low * REVERSI_SIZE + low] = WHITE_CELL
            cells[high * REVERSI_SIZE + high] = WHITE_CELL
            cells[low * REVERSI_SIZE + high] = BLACK_CELL
            cells[high * REVERSI_SIZE + low] = BLACK_CELL
            return ReversiBoard(cells, Disc.BLACK)
        }

        /** Build a board from six rows of `B`, `W` and `.`. Top row first. Used by tests. */
        fun fromRows(rows: List<String>, sideToMove: Disc): ReversiBoard {
            require(rows.size == REVERSI_SIZE) { "Expected $REVERSI_SIZE rows, got ${rows.size}" }
            val cells = IntArray(CELL_COUNT)
            rows.forEachIndexed { row, line ->
                require(line.length == REVERSI_SIZE) { "Row $row must be $REVERSI_SIZE wide: $line" }
                line.forEachIndexed { col, symbol ->
                    cells[row * REVERSI_SIZE + col] = when (symbol) {
                        'B' -> BLACK_CELL
                        'W' -> WHITE_CELL
                        '.' -> EMPTY_CELL
                        else -> error("Unexpected board symbol '$symbol'")
                    }
                }
            }
            return ReversiBoard(cells, sideToMove)
        }
    }
}
