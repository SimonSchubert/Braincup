package com.inspiredandroid.braincup.games.minichess

import kotlin.random.Random

class ChessAi(
    val depth: Int,
    private val random: Random = Random.Default,
) {
    fun bestMove(board: ChessBoard): Move? {
        val moves = board.legalMoves().shuffled(random)
        if (moves.isEmpty()) return null
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

    private fun search(board: ChessBoard, depth: Int, alphaIn: Int, beta: Int, ply: Int): Int {
        if (depth == 0) return evaluate(board)
        val moves = board.legalMoves()
        if (moves.isEmpty()) {
            return if (board.isInCheck(board.sideToMove)) -(MATE - ply) else 0
        }
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

    private fun evaluate(board: ChessBoard): Int {
        var us = 0
        var them = 0
        val pieces = board.snapshot()
        for (i in pieces.indices) {
            val p = pieces[i] ?: continue
            val v = pieceValue(p.type) + centerBonus(i)
            if (p.color == board.sideToMove) us += v else them += v
        }
        return us - them
    }

    companion object {
        const val MATE = 100_000
        private const val INF = 1_000_000
        private val CENTER = intArrayOf(0, 1, 2, 1, 0)

        private fun centerBonus(flatIndex: Int): Int {
            val r = flatIndex / BOARD_SIZE
            val c = flatIndex % BOARD_SIZE
            return CENTER[r] + CENTER[c]
        }

        private fun pieceValue(t: PieceType): Int = when (t) {
            PieceType.PAWN -> 100
            PieceType.KNIGHT -> 300
            PieceType.BISHOP -> 320
            PieceType.ROOK -> 500
            PieceType.QUEEN -> 900
            PieceType.KING -> 0
        }
    }
}
