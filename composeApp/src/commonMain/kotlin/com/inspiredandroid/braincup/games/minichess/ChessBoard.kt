package com.inspiredandroid.braincup.games.minichess

import kotlin.math.abs

const val BOARD_SIZE = 5

enum class PieceType { KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN }

enum class Color { WHITE, BLACK }

fun Color.opposite(): Color = if (this == Color.WHITE) Color.BLACK else Color.WHITE

data class Piece(val type: PieceType, val color: Color)

data class Square(val file: Int, val row: Int) {
    fun inBounds(): Boolean = file in 0 until BOARD_SIZE && row in 0 until BOARD_SIZE
}

data class Move(
    val from: Square,
    val to: Square,
    val promotion: PieceType? = null,
)

class ChessBoard private constructor(
    private val grid: Array<Array<Piece?>>,
    val sideToMove: Color,
) {
    fun pieceAt(square: Square): Piece? = grid[square.row][square.file]
    fun pieceAt(file: Int, row: Int): Piece? = grid[row][file]

    fun apply(move: Move): ChessBoard {
        val piece = pieceAt(move.from)
            ?: error("apply: no piece on ${move.from}")
        val newGrid = Array(BOARD_SIZE) { r -> Array<Piece?>(BOARD_SIZE) { c -> grid[r][c] } }
        newGrid[move.from.row][move.from.file] = null
        val placedType = move.promotion ?: piece.type
        newGrid[move.to.row][move.to.file] = Piece(placedType, piece.color)
        return ChessBoard(newGrid, sideToMove.opposite())
    }

    fun findKing(color: Color): Square? {
        for (r in 0 until BOARD_SIZE) {
            for (c in 0 until BOARD_SIZE) {
                val p = grid[r][c]
                if (p != null && p.type == PieceType.KING && p.color == color) return Square(c, r)
            }
        }
        return null
    }

    fun isInCheck(color: Color): Boolean {
        val kingSq = findKing(color) ?: return false
        return isSquareAttackedBy(kingSq, color.opposite())
    }

    private fun isSquareAttackedBy(target: Square, color: Color): Boolean {
        for (r in 0 until BOARD_SIZE) {
            for (c in 0 until BOARD_SIZE) {
                val piece = grid[r][c] ?: continue
                if (piece.color != color) continue
                if (pieceAttacks(piece, Square(c, r), target)) return true
            }
        }
        return false
    }

    private fun pieceAttacks(piece: Piece, from: Square, target: Square): Boolean = when (piece.type) {
        PieceType.PAWN -> pawnAttacks(piece.color, from, target)
        PieceType.KNIGHT -> knightAttacks(from, target)
        PieceType.BISHOP -> slideAttacks(from, target, BISHOP_DELTAS)
        PieceType.ROOK -> slideAttacks(from, target, ROOK_DELTAS)
        PieceType.QUEEN -> slideAttacks(from, target, ROOK_DELTAS) || slideAttacks(from, target, BISHOP_DELTAS)
        PieceType.KING -> kingAttacks(from, target)
    }

    private fun pawnAttacks(color: Color, from: Square, target: Square): Boolean {
        val dir = if (color == Color.WHITE) 1 else -1
        return target.row == from.row + dir && abs(target.file - from.file) == 1
    }

    private fun knightAttacks(from: Square, target: Square): Boolean {
        val df = abs(target.file - from.file)
        val dr = abs(target.row - from.row)
        return (df == 1 && dr == 2) || (df == 2 && dr == 1)
    }

    private fun kingAttacks(from: Square, target: Square): Boolean {
        if (from == target) return false
        return abs(target.file - from.file) <= 1 && abs(target.row - from.row) <= 1
    }

    private fun slideAttacks(from: Square, target: Square, deltas: Array<IntArray>): Boolean {
        for (d in deltas) {
            var f = from.file + d[0]
            var r = from.row + d[1]
            while (f in 0 until BOARD_SIZE && r in 0 until BOARD_SIZE) {
                if (f == target.file && r == target.row) return true
                if (grid[r][f] != null) break
                f += d[0]
                r += d[1]
            }
        }
        return false
    }

    fun legalMoves(): List<Move> {
        val pseudo = pseudoLegalMoves(sideToMove)
        val out = ArrayList<Move>(pseudo.size)
        for (m in pseudo) {
            val next = apply(m)
            if (!next.isInCheck(sideToMove)) out.add(m)
        }
        return out
    }

    fun isCheckmate(): Boolean = isInCheck(sideToMove) && legalMoves().isEmpty()
    fun isStalemate(): Boolean = !isInCheck(sideToMove) && legalMoves().isEmpty()

    private fun pseudoLegalMoves(color: Color): List<Move> {
        val moves = ArrayList<Move>()
        for (r in 0 until BOARD_SIZE) {
            for (c in 0 until BOARD_SIZE) {
                val piece = grid[r][c] ?: continue
                if (piece.color != color) continue
                val from = Square(c, r)
                when (piece.type) {
                    PieceType.PAWN -> generatePawnMoves(piece.color, from, moves)
                    PieceType.KNIGHT -> generateStepMoves(piece.color, from, KNIGHT_DELTAS, moves)
                    PieceType.BISHOP -> generateSlidingMoves(piece.color, from, BISHOP_DELTAS, moves)
                    PieceType.ROOK -> generateSlidingMoves(piece.color, from, ROOK_DELTAS, moves)
                    PieceType.QUEEN -> {
                        generateSlidingMoves(piece.color, from, ROOK_DELTAS, moves)
                        generateSlidingMoves(piece.color, from, BISHOP_DELTAS, moves)
                    }
                    PieceType.KING -> generateStepMoves(piece.color, from, KING_DELTAS, moves)
                }
            }
        }
        return moves
    }

    private fun generatePawnMoves(color: Color, from: Square, out: MutableList<Move>) {
        val dir = if (color == Color.WHITE) 1 else -1
        val promotionRow = if (color == Color.WHITE) BOARD_SIZE - 1 else 0
        val forwardRow = from.row + dir
        if (forwardRow !in 0 until BOARD_SIZE) return

        if (grid[forwardRow][from.file] == null) {
            val to = Square(from.file, forwardRow)
            out.add(if (forwardRow == promotionRow) Move(from, to, PieceType.QUEEN) else Move(from, to))
        }
        for (df in intArrayOf(-1, 1)) {
            val tf = from.file + df
            if (tf !in 0 until BOARD_SIZE) continue
            val target = grid[forwardRow][tf]
            if (target != null && target.color != color) {
                val to = Square(tf, forwardRow)
                out.add(if (forwardRow == promotionRow) Move(from, to, PieceType.QUEEN) else Move(from, to))
            }
        }
    }

    private fun generateStepMoves(
        color: Color,
        from: Square,
        deltas: Array<IntArray>,
        out: MutableList<Move>,
    ) {
        for (d in deltas) {
            val tf = from.file + d[0]
            val tr = from.row + d[1]
            if (tf !in 0 until BOARD_SIZE || tr !in 0 until BOARD_SIZE) continue
            val target = grid[tr][tf]
            if (target == null || target.color != color) {
                out.add(Move(from, Square(tf, tr)))
            }
        }
    }

    private fun generateSlidingMoves(
        color: Color,
        from: Square,
        deltas: Array<IntArray>,
        out: MutableList<Move>,
    ) {
        for (d in deltas) {
            var f = from.file + d[0]
            var r = from.row + d[1]
            while (f in 0 until BOARD_SIZE && r in 0 until BOARD_SIZE) {
                val target = grid[r][f]
                if (target == null) {
                    out.add(Move(from, Square(f, r)))
                } else {
                    if (target.color != color) out.add(Move(from, Square(f, r)))
                    break
                }
                f += d[0]
                r += d[1]
            }
        }
    }

    fun snapshot(): List<Piece?> = buildList(BOARD_SIZE * BOARD_SIZE) {
        for (r in 0 until BOARD_SIZE) for (c in 0 until BOARD_SIZE) add(grid[r][c])
    }

    fun pieceCount(color: Color): Int {
        var n = 0
        for (r in 0 until BOARD_SIZE) {
            for (c in 0 until BOARD_SIZE) {
                if (grid[r][c]?.color == color) n++
            }
        }
        return n
    }

    companion object {
        private val KNIGHT_DELTAS = arrayOf(
            intArrayOf(1, 2),
            intArrayOf(2, 1),
            intArrayOf(2, -1),
            intArrayOf(1, -2),
            intArrayOf(-1, -2),
            intArrayOf(-2, -1),
            intArrayOf(-2, 1),
            intArrayOf(-1, 2),
        )
        private val KING_DELTAS = arrayOf(
            intArrayOf(1, 0),
            intArrayOf(-1, 0),
            intArrayOf(0, 1),
            intArrayOf(0, -1),
            intArrayOf(1, 1),
            intArrayOf(-1, 1),
            intArrayOf(1, -1),
            intArrayOf(-1, -1),
        )
        private val ROOK_DELTAS = arrayOf(
            intArrayOf(1, 0),
            intArrayOf(-1, 0),
            intArrayOf(0, 1),
            intArrayOf(0, -1),
        )
        private val BISHOP_DELTAS = arrayOf(
            intArrayOf(1, 1),
            intArrayOf(-1, 1),
            intArrayOf(1, -1),
            intArrayOf(-1, -1),
        )

        fun fromMap(pieces: Map<Square, Piece>, sideToMove: Color = Color.WHITE): ChessBoard {
            val grid = Array(BOARD_SIZE) { Array<Piece?>(BOARD_SIZE) { null } }
            for ((sq, p) in pieces) grid[sq.row][sq.file] = p
            return ChessBoard(grid, sideToMove)
        }
    }
}
