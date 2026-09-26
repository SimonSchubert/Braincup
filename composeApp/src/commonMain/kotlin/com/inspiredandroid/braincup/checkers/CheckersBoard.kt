package com.inspiredandroid.braincup.checkers

const val CHECKERS_SIZE = 8

/** Forty moves each without a capture or a man moving. English draughts leaves the draw to the
 *  players; a fixed count is what stops two kings chasing each other forever. */
const val CHECKERS_DRAW_PLIES = 80

enum class CheckersSide { BLACK, WHITE }

val CheckersSide.opponent: CheckersSide get() = if (this == CheckersSide.BLACK) CheckersSide.WHITE else CheckersSide.BLACK

enum class CheckersResult { ONGOING, BLACK_WINS, WHITE_WINS, DRAW }

data class CheckersPiece(val side: CheckersSide, val isKing: Boolean)

/** [path] is every square the piece stands on, start and each landing, so a multi-jump reads hop
 *  by hop. [captured] lines up with the hops: `captured[i]` is jumped between `path[i]` and
 *  `path[i + 1]`. */
data class CheckersMove(val path: List<Int>, val captured: List<Int>) {
    val from: Int get() = path.first()
    val to: Int get() = path.last()
    val isCapture: Boolean get() = captured.isNotEmpty()
}

private const val EMPTY = 0
private const val BLACK_MAN = 1
private const val BLACK_KING = 2
private const val WHITE_MAN = 3
private const val WHITE_KING = 4

private fun sideOf(cell: Int): CheckersSide? = when (cell) {
    BLACK_MAN, BLACK_KING -> CheckersSide.BLACK
    WHITE_MAN, WHITE_KING -> CheckersSide.WHITE
    else -> null
}

private fun isKing(cell: Int) = cell == BLACK_KING || cell == WHITE_KING

/** Black starts at the bottom and moves up the board, so its forward is row - 1. */
private val CheckersSide.forward: Int get() = if (this == CheckersSide.BLACK) -1 else 1

private fun CheckersSide.crownRow(size: Int): Int = if (this == CheckersSide.BLACK) 0 else size - 1

/**
 * Immutable English draughts board, [size] squares a side. Cells are row-major flat indices,
 * `row * size + col`, top row first. Only dark squares, where `(row + col)` is odd, ever hold a
 * piece.
 */
class CheckersBoard private constructor(
    private val cells: IntArray,
    val size: Int,
    val sideToMove: CheckersSide,
    /** Plies since the last capture or man move. */
    val quietPlies: Int,
    val drawPlies: Int,
) {
    val cellCount: Int get() = cells.size

    private val moves: List<CheckersMove> by lazy { generateMoves() }

    fun pieceAt(index: Int): CheckersPiece? {
        val cell = cells[index]
        val side = sideOf(cell) ?: return null
        return CheckersPiece(side, isKing(cell))
    }

    fun count(side: CheckersSide): Int = cells.count { sideOf(it) == side }

    fun legalMoves(): List<CheckersMove> = moves

    fun apply(move: CheckersMove): CheckersBoard {
        val next = cells.copyOf()
        val piece = next[move.from]
        next[move.from] = EMPTY
        for (index in move.captured) next[index] = EMPTY
        val crowned = !isKing(piece) && move.to / size == sideToMove.crownRow(size)
        next[move.to] = when {
            !crowned -> piece
            sideToMove == CheckersSide.BLACK -> BLACK_KING
            else -> WHITE_KING
        }
        val resetsClock = move.isCapture || !isKing(piece)
        return CheckersBoard(next, size, sideToMove.opponent, if (resetsClock) 0 else quietPlies + 1, drawPlies)
    }

    fun result(): CheckersResult = when {
        moves.isEmpty() -> if (sideToMove == CheckersSide.BLACK) CheckersResult.WHITE_WINS else CheckersResult.BLACK_WINS
        quietPlies >= drawPlies -> CheckersResult.DRAW
        else -> CheckersResult.ONGOING
    }

    private fun generateMoves(): List<CheckersMove> {
        val captures = mutableListOf<CheckersMove>()
        for (index in cells.indices) {
            if (sideOf(cells[index]) == sideToMove) {
                collectJumps(index, isKing(cells[index]), mutableListOf(index), mutableListOf(), captures)
            }
        }
        if (captures.isNotEmpty()) return captures
        val steps = mutableListOf<CheckersMove>()
        for (index in cells.indices) {
            if (sideOf(cells[index]) != sideToMove) continue
            val row = index / size
            val col = index % size
            for (dRow in directionsFor(isKing(cells[index]))) {
                for (dCol in SIDEWAYS) {
                    val r = row + dRow
                    val c = col + dCol
                    if (!onBoard(r, c)) continue
                    val target = r * size + c
                    if (cells[target] == EMPTY) steps.add(CheckersMove(listOf(index, target), emptyList()))
                }
            }
        }
        return steps
    }

    /** Depth-first over every jump sequence. Jumping on is compulsory, so only sequences that
     *  cannot be extended are moves, except that a man crowned mid-sequence stops there. */
    private fun collectJumps(
        at: Int,
        king: Boolean,
        path: MutableList<Int>,
        captured: MutableList<Int>,
        out: MutableList<CheckersMove>,
    ) {
        val origin = path.first()
        val row = at / size
        val col = at % size
        var extended = false
        for (dRow in directionsFor(king)) {
            for (dCol in SIDEWAYS) {
                val landRow = row + 2 * dRow
                val landCol = col + 2 * dCol
                if (!onBoard(landRow, landCol)) continue
                val over = (row + dRow) * size + col + dCol
                val land = landRow * size + landCol
                if (sideOf(cells[over]) != sideToMove.opponent || over in captured) continue
                // The moving piece has left its square, so a king may land back on it.
                if (cells[land] != EMPTY && land != origin) continue
                extended = true
                path.add(land)
                captured.add(over)
                if (!king && landRow == sideToMove.crownRow(size)) {
                    out.add(CheckersMove(path.toList(), captured.toList()))
                } else {
                    collectJumps(land, king, path, captured, out)
                }
                path.removeAt(path.size - 1)
                captured.removeAt(captured.size - 1)
            }
        }
        if (!extended && captured.isNotEmpty()) out.add(CheckersMove(path.toList(), captured.toList()))
    }

    private fun directionsFor(king: Boolean): IntArray = if (king) BOTH_WAYS else intArrayOf(sideToMove.forward)

    private fun onBoard(row: Int, col: Int) = row in 0 until size && col in 0 until size

    fun isDarkSquare(index: Int): Boolean = isDarkSquare(index, size)

    companion object {
        private val SIDEWAYS = intArrayOf(-1, 1)
        private val BOTH_WAYS = intArrayOf(-1, 1)

        fun isDarkSquare(index: Int, size: Int): Boolean = (index / size + index % size) % 2 == 1

        fun startingPosition(): CheckersBoard {
            val cells = IntArray(CHECKERS_SIZE * CHECKERS_SIZE)
            for (index in cells.indices) {
                if (!isDarkSquare(index, CHECKERS_SIZE)) continue
                when (index / CHECKERS_SIZE) {
                    in 0..2 -> cells[index] = WHITE_MAN
                    in 5..7 -> cells[index] = BLACK_MAN
                }
            }
            return CheckersBoard(cells, CHECKERS_SIZE, CheckersSide.BLACK, 0, CHECKERS_DRAW_PLIES)
        }

        /** Build a square board from rows of `b`/`w` (men), `B`/`W` (kings) and `.`. Top row
         *  first; the number of rows sets the size. */
        fun fromRows(
            rows: List<String>,
            sideToMove: CheckersSide,
            quietPlies: Int = 0,
            drawPlies: Int = CHECKERS_DRAW_PLIES,
        ): CheckersBoard {
            val size = rows.size
            val cells = IntArray(size * size)
            rows.forEachIndexed { row, line ->
                require(line.length == size) { "Row $row must be $size wide: $line" }
                line.forEachIndexed { col, symbol ->
                    val index = row * size + col
                    cells[index] = when (symbol) {
                        'b' -> BLACK_MAN
                        'B' -> BLACK_KING
                        'w' -> WHITE_MAN
                        'W' -> WHITE_KING
                        '.' -> EMPTY
                        else -> error("Unexpected board symbol '$symbol'")
                    }
                    require(cells[index] == EMPTY || isDarkSquare(index, size)) { "Piece on a light square at $row,$col" }
                }
            }
            return CheckersBoard(cells, size, sideToMove, quietPlies, drawPlies)
        }
    }
}
