package com.inspiredandroid.braincup.normalchess

import kotlin.random.Random

class NormalChessAi(
    val depth: Int,
    private val useQuiescence: Boolean = true,
    private val blunderChance: Double = 0.0,
    private val random: Random = Random.Default,
) {
    fun bestMove(board: NormalChessBoard): Move? {
        val moves = board.legalMoves().shuffled(random)
        if (moves.isEmpty()) return null
        // Beginner blunder: occasionally pick a random move instead of the calculated best.
        // Skip when there's only one legal move (would be the same) and when in check (the
        // random move might be the only saving move — picking a wrong one looks broken,
        // not casual).
        if (blunderChance > 0 && moves.size > 1 && !board.isInCheck(board.sideToMove) &&
            random.nextDouble() < blunderChance
        ) {
            return moves.first()
        }
        var best = moves[0]
        var bestScore = -INF
        var alpha = -INF
        val beta = INF
        for (m in moves) {
            val score = -search(board.apply(m), depth - 1, -beta, -alpha, ply = 1)
            if (score > bestScore) {
                bestScore = score
                best = m
            }
            if (score > alpha) alpha = score
        }
        return best
    }

    private fun search(board: NormalChessBoard, depth: Int, alphaIn: Int, beta: Int, ply: Int): Int {
        val moves = order(board, board.legalMoves())
        if (moves.isEmpty()) {
            return if (board.isInCheck(board.sideToMove)) -(MATE - ply) else 0
        }
        // Drop into a captures-only search at the depth horizon. This avoids the classic
        // alpha-beta horizon effect where the AI happily trades into a position that's
        // about to lose material — the static eval at depth 0 doesn't see the recapture.
        // Easy mode disables quiescence so it plays like a beginner who doesn't read
        // recaptures.
        if (depth == 0) return if (useQuiescence) quiescence(board, alphaIn, beta) else evaluate(board)
        if (board.halfmoveClock >= 100 || board.isInsufficientMaterial()) return 0
        var alpha = alphaIn
        var best = -INF
        for (m in moves) {
            val score = -search(board.apply(m), depth - 1, -beta, -alpha, ply + 1)
            if (score > best) best = score
            if (best > alpha) alpha = best
            if (alpha >= beta) break
        }
        return best
    }

    /** Quiescence search — keeps following capture chains past the depth limit so we
     *  evaluate quiet positions only. Stand-pat lets the side to move decline to capture
     *  if doing so would worsen its score, which is required for soundness. */
    private fun quiescence(board: NormalChessBoard, alphaIn: Int, beta: Int): Int {
        val standPat = evaluate(board)
        if (standPat >= beta) return beta
        var alpha = if (standPat > alphaIn) standPat else alphaIn

        val captures = board.legalMoves().filter { move ->
            board.pieceAt(move.to) != null ||
                // En passant: pawn moves diagonally to enPassantTarget (an empty square).
                (move.to == board.enPassantTarget && board.pieceAt(move.from)?.type == PieceType.PAWN) ||
                // Queen promotion changes material drastically; treat as tactical.
                move.promotion == PieceType.QUEEN
        }
        val ordered = order(board, captures)
        for (m in ordered) {
            val score = -quiescence(board.apply(m), -beta, -alpha)
            if (score >= beta) return beta
            if (score > alpha) alpha = score
        }
        return alpha
    }

    /** Order: captures first by MVV/LVA, then quiet moves. Stable. */
    private fun order(board: NormalChessBoard, moves: List<Move>): List<Move> {
        if (moves.size <= 1) return moves
        val scored = moves.map { m ->
            val attacker = board.pieceAt(m.from)
            val victim = board.pieceAt(m.to)
            val score = if (victim != null && attacker != null) {
                10 * pieceValue(victim.type) - pieceValue(attacker.type)
            } else {
                -1
            }
            m to score
        }
        return scored.sortedByDescending { it.second }.map { it.first }
    }

    private fun evaluate(board: NormalChessBoard): Int {
        var us = 0
        var them = 0
        val snapshot = board.snapshot()
        for (i in snapshot.indices) {
            val p = snapshot[i] ?: continue
            val v = pieceValue(p.type) + pst(p, i)
            if (p.color == board.sideToMove) us += v else them += v
        }
        return us - them
    }

    companion object {
        const val MATE = 100_000
        private const val INF = 1_000_000

        private fun pieceValue(t: PieceType): Int = when (t) {
            PieceType.PAWN -> 100
            PieceType.KNIGHT -> 300
            PieceType.BISHOP -> 320
            PieceType.ROOK -> 500
            PieceType.QUEEN -> 900
            PieceType.KING -> 0
        }

        // Piece-square tables from white's POV, row 0 = white's first rank.
        // Mirrored for black at access time. Values are deliberately small so they
        // refine choices without overwhelming material.
        private val PAWN_PST = intArrayOf(
            0, 0, 0, 0, 0, 0, 0, 0,
            5, 10, 10, -20, -20, 10, 10, 5,
            5, -5, -10, 0, 0, -10, -5, 5,
            0, 0, 0, 20, 20, 0, 0, 0,
            5, 5, 10, 25, 25, 10, 5, 5,
            10, 10, 20, 30, 30, 20, 10, 10,
            50, 50, 50, 50, 50, 50, 50, 50,
            0, 0, 0, 0, 0, 0, 0, 0,
        )
        private val KNIGHT_PST = intArrayOf(
            -50, -40, -30, -30, -30, -30, -40, -50,
            -40, -20, 0, 5, 5, 0, -20, -40,
            -30, 5, 10, 15, 15, 10, 5, -30,
            -30, 0, 15, 20, 20, 15, 0, -30,
            -30, 5, 15, 20, 20, 15, 5, -30,
            -30, 0, 10, 15, 15, 10, 0, -30,
            -40, -20, 0, 0, 0, 0, -20, -40,
            -50, -40, -30, -30, -30, -30, -40, -50,
        )
        private val BISHOP_PST = intArrayOf(
            -20, -10, -10, -10, -10, -10, -10, -20,
            -10, 5, 0, 0, 0, 0, 5, -10,
            -10, 10, 10, 10, 10, 10, 10, -10,
            -10, 0, 10, 10, 10, 10, 0, -10,
            -10, 5, 5, 10, 10, 5, 5, -10,
            -10, 0, 5, 10, 10, 5, 0, -10,
            -10, 0, 0, 0, 0, 0, 0, -10,
            -20, -10, -10, -10, -10, -10, -10, -20,
        )
        private val ROOK_PST = intArrayOf(
            0, 0, 0, 5, 5, 0, 0, 0,
            -5, 0, 0, 0, 0, 0, 0, -5,
            -5, 0, 0, 0, 0, 0, 0, -5,
            -5, 0, 0, 0, 0, 0, 0, -5,
            -5, 0, 0, 0, 0, 0, 0, -5,
            -5, 0, 0, 0, 0, 0, 0, -5,
            5, 10, 10, 10, 10, 10, 10, 5,
            0, 0, 0, 0, 0, 0, 0, 0,
        )
        private val QUEEN_PST = intArrayOf(
            -20, -10, -10, -5, -5, -10, -10, -20,
            -10, 0, 0, 0, 0, 0, 0, -10,
            -10, 5, 5, 5, 5, 5, 0, -10,
            0, 0, 5, 5, 5, 5, 0, -5,
            -5, 0, 5, 5, 5, 5, 0, -5,
            -10, 0, 5, 5, 5, 5, 0, -10,
            -10, 0, 0, 0, 0, 0, 0, -10,
            -20, -10, -10, -5, -5, -10, -10, -20,
        )
        private val KING_PST = intArrayOf(
            20, 30, 10, 0, 0, 10, 30, 20,
            20, 20, 0, 0, 0, 0, 20, 20,
            -10, -20, -20, -20, -20, -20, -20, -10,
            -20, -30, -30, -40, -40, -30, -30, -20,
            -30, -40, -40, -50, -50, -40, -40, -30,
            -30, -40, -40, -50, -50, -40, -40, -30,
            -30, -40, -40, -50, -50, -40, -40, -30,
            -30, -40, -40, -50, -50, -40, -40, -30,
        )

        private fun pst(piece: Piece, flatIndex: Int): Int {
            // flatIndex was built as row * 8 + file in snapshot(); mirror row for black.
            val row = flatIndex / NORMAL_CHESS_SIZE
            val file = flatIndex % NORMAL_CHESS_SIZE
            val effectiveRow = if (piece.color == Color.WHITE) row else NORMAL_CHESS_SIZE - 1 - row
            val mirrored = effectiveRow * NORMAL_CHESS_SIZE + file
            return when (piece.type) {
                PieceType.PAWN -> PAWN_PST[mirrored]
                PieceType.KNIGHT -> KNIGHT_PST[mirrored]
                PieceType.BISHOP -> BISHOP_PST[mirrored]
                PieceType.ROOK -> ROOK_PST[mirrored]
                PieceType.QUEEN -> QUEEN_PST[mirrored]
                PieceType.KING -> KING_PST[mirrored]
            }
        }
    }
}
