package com.inspiredandroid.braincup.normalchess

import kotlin.math.abs

const val NORMAL_CHESS_SIZE = 8

enum class PieceType { KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN }

enum class Color { WHITE, BLACK }

fun Color.opposite(): Color = if (this == Color.WHITE) Color.BLACK else Color.WHITE

data class Piece(val type: PieceType, val color: Color)

data class Square(val file: Int, val row: Int) {
    fun inBounds(): Boolean = file in 0 until NORMAL_CHESS_SIZE && row in 0 until NORMAL_CHESS_SIZE
}

data class Move(
    val from: Square,
    val to: Square,
    val promotion: PieceType? = null,
)

data class CastlingRights(
    val whiteKingside: Boolean = true,
    val whiteQueenside: Boolean = true,
    val blackKingside: Boolean = true,
    val blackQueenside: Boolean = true,
)

enum class GameResult {
    ONGOING,
    WHITE_WINS,
    BLACK_WINS,
    DRAW_STALEMATE,
    DRAW_FIFTY_MOVE,
    DRAW_INSUFFICIENT_MATERIAL,
    DRAW_REPETITION,
}

class NormalChessBoard private constructor(
    private val grid: Array<Array<Piece?>>,
    val sideToMove: Color,
    val castling: CastlingRights,
    val enPassantTarget: Square?,
    val halfmoveClock: Int,
    val fullmoveNumber: Int,
    /** Keys of every position since the game started, including the current one. Used for
     *  threefold-repetition detection (auto-draw on the third occurrence). */
    private val positionKeys: List<Long>,
) {
    fun pieceAt(square: Square): Piece? = grid[square.row][square.file]
    fun pieceAt(file: Int, row: Int): Piece? = grid[row][file]

    /** Apply a legal move and return the resulting board. The move must come from
     *  [legalMoves] — undefined behaviour otherwise. */
    fun apply(move: Move): NormalChessBoard {
        val piece = pieceAt(move.from) ?: error("apply: no piece on ${move.from}")
        val newGrid = Array(NORMAL_CHESS_SIZE) { r -> Array<Piece?>(NORMAL_CHESS_SIZE) { c -> grid[r][c] } }

        val isPawn = piece.type == PieceType.PAWN
        val isCapture = pieceAt(move.to) != null
        val isCastle = piece.type == PieceType.KING && abs(move.to.file - move.from.file) == 2
        val isEnPassant = isPawn && move.to == enPassantTarget && pieceAt(move.to) == null && move.from.file != move.to.file

        newGrid[move.from.row][move.from.file] = null
        val placedType = move.promotion ?: piece.type
        newGrid[move.to.row][move.to.file] = Piece(placedType, piece.color)

        if (isEnPassant) {
            newGrid[move.from.row][move.to.file] = null
        }

        if (isCastle) {
            val row = move.from.row
            val (rookFromFile, rookToFile) = if (move.to.file > move.from.file) 7 to 5 else 0 to 3
            val rook = newGrid[row][rookFromFile]
            newGrid[row][rookFromFile] = null
            newGrid[row][rookToFile] = rook
        }

        val newEnPassant: Square? = if (isPawn && abs(move.to.row - move.from.row) == 2) {
            val midRow = (move.from.row + move.to.row) / 2
            Square(move.from.file, midRow)
        } else {
            null
        }

        val newCastling = updateCastlingRights(piece, move)
        val newHalfmove = if (isPawn || isCapture || isEnPassant) 0 else halfmoveClock + 1
        val newFullmove = if (sideToMove == Color.BLACK) fullmoveNumber + 1 else fullmoveNumber
        val newSide = sideToMove.opposite()
        val nextKey = positionKey(
            grid = newGrid,
            sideToMove = newSide,
            castling = newCastling,
            enPassantTarget = newEnPassant,
        )

        return NormalChessBoard(
            grid = newGrid,
            sideToMove = newSide,
            castling = newCastling,
            enPassantTarget = newEnPassant,
            halfmoveClock = newHalfmove,
            fullmoveNumber = newFullmove,
            positionKeys = positionKeys + nextKey,
        )
    }

    private fun updateCastlingRights(piece: Piece, move: Move): CastlingRights {
        var c = castling
        if (piece.type == PieceType.KING) {
            c = if (piece.color == Color.WHITE) {
                c.copy(whiteKingside = false, whiteQueenside = false)
            } else {
                c.copy(blackKingside = false, blackQueenside = false)
            }
        }
        if (piece.type == PieceType.ROOK) {
            if (piece.color == Color.WHITE && move.from == Square(0, 0)) c = c.copy(whiteQueenside = false)
            if (piece.color == Color.WHITE && move.from == Square(7, 0)) c = c.copy(whiteKingside = false)
            if (piece.color == Color.BLACK && move.from == Square(0, 7)) c = c.copy(blackQueenside = false)
            if (piece.color == Color.BLACK && move.from == Square(7, 7)) c = c.copy(blackKingside = false)
        }
        // Capturing a corner rook also kills the matching castling right.
        if (move.to == Square(0, 0)) c = c.copy(whiteQueenside = false)
        if (move.to == Square(7, 0)) c = c.copy(whiteKingside = false)
        if (move.to == Square(0, 7)) c = c.copy(blackQueenside = false)
        if (move.to == Square(7, 7)) c = c.copy(blackKingside = false)
        return c
    }

    fun findKing(color: Color): Square? {
        for (r in 0 until NORMAL_CHESS_SIZE) {
            for (c in 0 until NORMAL_CHESS_SIZE) {
                val p = grid[r][c]
                if (p != null && p.type == PieceType.KING && p.color == color) return Square(c, r)
            }
        }
        return null
    }

    fun isInCheck(color: Color): Boolean {
        val king = findKing(color) ?: return false
        return isSquareAttackedBy(king, color.opposite())
    }

    private fun isSquareAttackedBy(target: Square, color: Color): Boolean {
        for (r in 0 until NORMAL_CHESS_SIZE) {
            for (c in 0 until NORMAL_CHESS_SIZE) {
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
            while (f in 0 until NORMAL_CHESS_SIZE && r in 0 until NORMAL_CHESS_SIZE) {
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

    /** Current position has occurred at least three times (FIDE threefold; auto-draw here). */
    fun isThreefoldRepetition(): Boolean {
        val key = positionKeys.lastOrNull() ?: return false
        return positionKeys.count { it == key } >= 3
    }

    fun isInsufficientMaterial(): Boolean {
        val pieces = snapshot().filterNotNull()
        if (pieces.any { it.type == PieceType.QUEEN || it.type == PieceType.ROOK || it.type == PieceType.PAWN }) return false
        val whiteMinors = pieces.count { it.color == Color.WHITE && (it.type == PieceType.KNIGHT || it.type == PieceType.BISHOP) }
        val blackMinors = pieces.count { it.color == Color.BLACK && (it.type == PieceType.KNIGHT || it.type == PieceType.BISHOP) }
        // KvK
        if (whiteMinors == 0 && blackMinors == 0) return true
        // KvK+N or KvK+B
        if (whiteMinors == 0 && blackMinors == 1) return true
        if (blackMinors == 0 && whiteMinors == 1) return true
        // KB vs KB with bishops on same color complex (theoretical draw)
        if (whiteMinors == 1 && blackMinors == 1) {
            val whiteBishop = squareOf(PieceType.BISHOP, Color.WHITE)
            val blackBishop = squareOf(PieceType.BISHOP, Color.BLACK)
            if (whiteBishop != null && blackBishop != null) {
                val whiteParity = (whiteBishop.file + whiteBishop.row) and 1
                val blackParity = (blackBishop.file + blackBishop.row) and 1
                if (whiteParity == blackParity) return true
            }
        }
        return false
    }

    private fun squareOf(type: PieceType, color: Color): Square? {
        for (r in 0 until NORMAL_CHESS_SIZE) {
            for (c in 0 until NORMAL_CHESS_SIZE) {
                val p = grid[r][c]
                if (p != null && p.type == type && p.color == color) return Square(c, r)
            }
        }
        return null
    }

    fun result(): GameResult {
        if (legalMoves().isEmpty()) {
            return if (isInCheck(sideToMove)) {
                if (sideToMove == Color.WHITE) GameResult.BLACK_WINS else GameResult.WHITE_WINS
            } else {
                GameResult.DRAW_STALEMATE
            }
        }
        if (halfmoveClock >= 100) return GameResult.DRAW_FIFTY_MOVE
        if (isInsufficientMaterial()) return GameResult.DRAW_INSUFFICIENT_MATERIAL
        if (isThreefoldRepetition()) return GameResult.DRAW_REPETITION
        return GameResult.ONGOING
    }

    private fun pseudoLegalMoves(color: Color): List<Move> {
        val moves = ArrayList<Move>(48)
        for (r in 0 until NORMAL_CHESS_SIZE) {
            for (c in 0 until NORMAL_CHESS_SIZE) {
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
                    PieceType.KING -> {
                        generateStepMoves(piece.color, from, KING_DELTAS, moves)
                        generateCastleMoves(piece.color, from, moves)
                    }
                }
            }
        }
        return moves
    }

    private fun generatePawnMoves(color: Color, from: Square, out: MutableList<Move>) {
        val dir = if (color == Color.WHITE) 1 else -1
        val startRow = if (color == Color.WHITE) 1 else 6
        val promotionRow = if (color == Color.WHITE) NORMAL_CHESS_SIZE - 1 else 0
        val oneForwardRow = from.row + dir
        if (oneForwardRow !in 0 until NORMAL_CHESS_SIZE) return

        // Single push
        if (grid[oneForwardRow][from.file] == null) {
            addPawnPushOrPromotion(from, Square(from.file, oneForwardRow), promotionRow, out)
            // Double push
            if (from.row == startRow) {
                val twoForwardRow = from.row + 2 * dir
                if (grid[twoForwardRow][from.file] == null) {
                    out.add(Move(from, Square(from.file, twoForwardRow)))
                }
            }
        }
        // Captures
        for (df in intArrayOf(-1, 1)) {
            val tf = from.file + df
            if (tf !in 0 until NORMAL_CHESS_SIZE) continue
            val target = grid[oneForwardRow][tf]
            if (target != null && target.color != color) {
                addPawnPushOrPromotion(from, Square(tf, oneForwardRow), promotionRow, out)
            }
        }
        // En passant
        val ep = enPassantTarget
        if (ep != null && ep.row == oneForwardRow && abs(ep.file - from.file) == 1) {
            out.add(Move(from, ep))
        }
    }

    private fun addPawnPushOrPromotion(from: Square, to: Square, promotionRow: Int, out: MutableList<Move>) {
        if (to.row == promotionRow) {
            out.add(Move(from, to, PieceType.QUEEN))
            out.add(Move(from, to, PieceType.ROOK))
            out.add(Move(from, to, PieceType.BISHOP))
            out.add(Move(from, to, PieceType.KNIGHT))
        } else {
            out.add(Move(from, to))
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
            if (tf !in 0 until NORMAL_CHESS_SIZE || tr !in 0 until NORMAL_CHESS_SIZE) continue
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
            while (f in 0 until NORMAL_CHESS_SIZE && r in 0 until NORMAL_CHESS_SIZE) {
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

    private fun generateCastleMoves(color: Color, from: Square, out: MutableList<Move>) {
        val homeRow = if (color == Color.WHITE) 0 else 7
        if (from != Square(4, homeRow)) return
        if (isInCheck(color)) return

        val kingside = if (color == Color.WHITE) castling.whiteKingside else castling.blackKingside
        val queenside = if (color == Color.WHITE) castling.whiteQueenside else castling.blackQueenside
        val enemy = color.opposite()

        if (kingside &&
            grid[homeRow][5] == null &&
            grid[homeRow][6] == null &&
            grid[homeRow][7] == Piece(PieceType.ROOK, color) &&
            !isSquareAttackedBy(Square(5, homeRow), enemy) &&
            !isSquareAttackedBy(Square(6, homeRow), enemy)
        ) {
            out.add(Move(from, Square(6, homeRow)))
        }
        if (queenside &&
            grid[homeRow][1] == null &&
            grid[homeRow][2] == null &&
            grid[homeRow][3] == null &&
            grid[homeRow][0] == Piece(PieceType.ROOK, color) &&
            !isSquareAttackedBy(Square(3, homeRow), enemy) &&
            !isSquareAttackedBy(Square(2, homeRow), enemy)
        ) {
            out.add(Move(from, Square(2, homeRow)))
        }
    }

    fun snapshot(): List<Piece?> = buildList(NORMAL_CHESS_SIZE * NORMAL_CHESS_SIZE) {
        for (r in 0 until NORMAL_CHESS_SIZE) for (c in 0 until NORMAL_CHESS_SIZE) add(grid[r][c])
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

        /** Hash of piece placement + side to move + castling + en passant (FIDE position identity). */
        fun positionKey(
            grid: Array<Array<Piece?>>,
            sideToMove: Color,
            castling: CastlingRights,
            enPassantTarget: Square?,
        ): Long {
            var h = 0L
            for (r in 0 until NORMAL_CHESS_SIZE) {
                for (c in 0 until NORMAL_CHESS_SIZE) {
                    val p = grid[r][c]
                    val code = if (p == null) {
                        0
                    } else {
                        (p.type.ordinal + 1) * 2 + p.color.ordinal + 1
                    }
                    h = h * 31 + code
                }
            }
            h = h * 31 + sideToMove.ordinal
            h = h * 31 + castlingBits(castling)
            h = h * 31 + (
                enPassantTarget?.let { 1 + it.file + it.row * NORMAL_CHESS_SIZE } ?: 0
                )
            return h
        }

        private fun castlingBits(c: CastlingRights): Int {
            var bits = 0
            if (c.whiteKingside) bits = bits or 1
            if (c.whiteQueenside) bits = bits or 2
            if (c.blackKingside) bits = bits or 4
            if (c.blackQueenside) bits = bits or 8
            return bits
        }

        private fun create(
            grid: Array<Array<Piece?>>,
            sideToMove: Color,
            castling: CastlingRights,
            enPassantTarget: Square?,
            halfmoveClock: Int,
            fullmoveNumber: Int,
        ): NormalChessBoard {
            val key = positionKey(grid, sideToMove, castling, enPassantTarget)
            return NormalChessBoard(
                grid = grid,
                sideToMove = sideToMove,
                castling = castling,
                enPassantTarget = enPassantTarget,
                halfmoveClock = halfmoveClock,
                fullmoveNumber = fullmoveNumber,
                positionKeys = listOf(key),
            )
        }

        fun startingPosition(): NormalChessBoard {
            val grid = Array(NORMAL_CHESS_SIZE) { Array<Piece?>(NORMAL_CHESS_SIZE) { null } }
            val backRank = arrayOf(
                PieceType.ROOK,
                PieceType.KNIGHT,
                PieceType.BISHOP,
                PieceType.QUEEN,
                PieceType.KING,
                PieceType.BISHOP,
                PieceType.KNIGHT,
                PieceType.ROOK,
            )
            for (c in 0 until NORMAL_CHESS_SIZE) {
                grid[0][c] = Piece(backRank[c], Color.WHITE)
                grid[1][c] = Piece(PieceType.PAWN, Color.WHITE)
                grid[6][c] = Piece(PieceType.PAWN, Color.BLACK)
                grid[7][c] = Piece(backRank[c], Color.BLACK)
            }
            return create(
                grid = grid,
                sideToMove = Color.WHITE,
                castling = CastlingRights(),
                enPassantTarget = null,
                halfmoveClock = 0,
                fullmoveNumber = 1,
            )
        }

        fun fromMap(
            pieces: Map<Square, Piece>,
            sideToMove: Color = Color.WHITE,
            castling: CastlingRights = CastlingRights(false, false, false, false),
            enPassantTarget: Square? = null,
            halfmoveClock: Int = 0,
            fullmoveNumber: Int = 1,
        ): NormalChessBoard {
            val grid = Array(NORMAL_CHESS_SIZE) { Array<Piece?>(NORMAL_CHESS_SIZE) { null } }
            for ((sq, p) in pieces) grid[sq.row][sq.file] = p
            return create(grid, sideToMove, castling, enPassantTarget, halfmoveClock, fullmoveNumber)
        }
    }
}
