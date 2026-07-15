package com.inspiredandroid.braincup.pegsolitaire

/** Side length of the English board grid (including invalid corner cells). */
const val PEG_BOARD_SIZE = 7

/** Row/column of the center hole on an English board. */
const val PEG_CENTER = 3

enum class PegCell {
    /** Outside the English cross — not a playable hole. */
    INVALID,

    /** Valid empty hole. */
    EMPTY,

    /** Valid hole with a peg. */
    PEG,
}

data class PegJump(
    val fromRow: Int,
    val fromCol: Int,
    val toRow: Int,
    val toCol: Int,
) {
    val midRow: Int get() = (fromRow + toRow) / 2
    val midCol: Int get() = (fromCol + toCol) / 2
}

enum class PegSolitaireResult {
    ONGOING,

    /** Exactly one peg left, not in the center. */
    WON,

    /** Exactly one peg left, in the center. */
    WON_PERFECT,

    /** No legal jumps and more than one peg remains. */
    STUCK,
}

/**
 * Immutable English peg-solitaire board (cross on a 7×7 grid, 33 holes).
 *
 * Cells are stored row-major, length [PEG_BOARD_SIZE]². Use [englishStarting] for the classic
 * setup (all holes filled except the center empty).
 */
class PegSolitaireBoard private constructor(
    private val cells: List<PegCell>,
) {
    init {
        require(cells.size == CELL_COUNT) { "Board must have $CELL_COUNT cells" }
    }

    fun cellAt(row: Int, col: Int): PegCell {
        if (row !in 0 until PEG_BOARD_SIZE || col !in 0 until PEG_BOARD_SIZE) return PegCell.INVALID
        return cells[index(row, col)]
    }

    fun pegCount(): Int = cells.count { it == PegCell.PEG }

    fun holeCount(): Int = cells.count { it != PegCell.INVALID }

    fun legalJumpsFrom(row: Int, col: Int): List<PegJump> {
        if (cellAt(row, col) != PegCell.PEG) return emptyList()
        return JUMP_DELTAS.mapNotNull { (dRow, dCol) ->
            val toRow = row + dRow
            val toCol = col + dCol
            val jump = PegJump(row, col, toRow, toCol)
            jump.takeIf { isLegalJump(it) }
        }
    }

    fun allLegalJumps(): List<PegJump> = buildList {
        for (row in 0 until PEG_BOARD_SIZE) {
            for (col in 0 until PEG_BOARD_SIZE) {
                addAll(legalJumpsFrom(row, col))
            }
        }
    }

    fun isLegalJump(jump: PegJump): Boolean {
        if (cellAt(jump.fromRow, jump.fromCol) != PegCell.PEG) return false
        if (cellAt(jump.midRow, jump.midCol) != PegCell.PEG) return false
        if (cellAt(jump.toRow, jump.toCol) != PegCell.EMPTY) return false
        val dRow = jump.toRow - jump.fromRow
        val dCol = jump.toCol - jump.fromCol
        return (dRow == 0 && (dCol == 2 || dCol == -2)) ||
            (dCol == 0 && (dRow == 2 || dRow == -2))
    }

    /**
     * Apply a legal jump. The jump must come from [legalJumpsFrom] / [allLegalJumps] —
     * undefined behaviour otherwise.
     */
    fun apply(jump: PegJump): PegSolitaireBoard {
        require(isLegalJump(jump)) { "Illegal jump: $jump" }
        val next = cells.toMutableList()
        next[index(jump.fromRow, jump.fromCol)] = PegCell.EMPTY
        next[index(jump.midRow, jump.midCol)] = PegCell.EMPTY
        next[index(jump.toRow, jump.toCol)] = PegCell.PEG
        return PegSolitaireBoard(next)
    }

    fun result(): PegSolitaireResult {
        val pegs = pegCount()
        if (pegs == 1) {
            return if (cellAt(PEG_CENTER, PEG_CENTER) == PegCell.PEG) {
                PegSolitaireResult.WON_PERFECT
            } else {
                PegSolitaireResult.WON
            }
        }
        if (allLegalJumps().isEmpty()) return PegSolitaireResult.STUCK
        return PegSolitaireResult.ONGOING
    }

    companion object {
        private const val CELL_COUNT = PEG_BOARD_SIZE * PEG_BOARD_SIZE

        private val JUMP_DELTAS = listOf(
            -2 to 0,
            2 to 0,
            0 to -2,
            0 to 2,
        )

        /** English cross: center 3×7 plus vertical arms (rows 0–1 and 5–6, cols 2–4). */
        fun isValidHole(row: Int, col: Int): Boolean {
            if (row !in 0 until PEG_BOARD_SIZE || col !in 0 until PEG_BOARD_SIZE) return false
            return row in 2..4 || col in 2..4
        }

        fun englishStarting(): PegSolitaireBoard {
            val cells = MutableList(CELL_COUNT) { PegCell.INVALID }
            for (row in 0 until PEG_BOARD_SIZE) {
                for (col in 0 until PEG_BOARD_SIZE) {
                    if (!isValidHole(row, col)) continue
                    cells[index(row, col)] = if (row == PEG_CENTER && col == PEG_CENTER) {
                        PegCell.EMPTY
                    } else {
                        PegCell.PEG
                    }
                }
            }
            return PegSolitaireBoard(cells)
        }

        /**
         * Build a board from a map of peg positions (row to col). All valid holes default to empty;
         * invalid cells stay invalid. Useful for tests.
         */
        fun fromPegs(pegs: Set<Pair<Int, Int>>): PegSolitaireBoard {
            val cells = MutableList(CELL_COUNT) { PegCell.INVALID }
            for (row in 0 until PEG_BOARD_SIZE) {
                for (col in 0 until PEG_BOARD_SIZE) {
                    if (!isValidHole(row, col)) continue
                    cells[index(row, col)] = if (row to col in pegs) PegCell.PEG else PegCell.EMPTY
                }
            }
            return PegSolitaireBoard(cells)
        }

        private fun index(row: Int, col: Int): Int = row * PEG_BOARD_SIZE + col
    }
}
