package com.inspiredandroid.braincup.checkers

import kotlin.random.Random

class CheckersAi(
    private val depth: Int,
    private val blunderChance: Double = 0.0,
    private val random: Random = Random.Default,
) {
    constructor(difficulty: CheckersDifficulty, random: Random = Random.Default) : this(
        depth = difficulty.depth,
        blunderChance = difficulty.blunderChance,
        random = random,
    )

    /** Nodes searched by the last [bestMove]. The browser runs this on the UI thread, so the
     *  tests hold it to a budget rather than trusting the depth numbers to stay affordable. */
    var nodesVisited: Int = 0
        private set

    fun bestMove(board: CheckersBoard): CheckersMove? {
        nodesVisited = 0
        val moves = board.legalMoves().shuffled(random)
        if (moves.size <= 1) return moves.firstOrNull()
        if (blunderChance > 0 && !moves.first().isCapture && random.nextDouble() < blunderChance) {
            return moves.first()
        }
        var best = moves[0]
        var bestScore = -INF
        var alpha = -INF
        for (move in order(moves)) {
            val score = -search(board.apply(move), depth - 1, -INF, -alpha, ply = 1)
            if (score > bestScore) {
                bestScore = score
                best = move
            }
            if (score > alpha) alpha = score
        }
        return best
    }

    private fun search(board: CheckersBoard, depth: Int, alphaIn: Int, beta: Int, ply: Int): Int {
        nodesVisited++
        val moves = board.legalMoves()
        if (moves.isEmpty()) return -(WIN_SCORE - ply)
        if (board.quietPlies >= CHECKERS_DRAW_PLIES) return 0
        // A forced capture at the horizon is searched through rather than scored: the static
        // evaluation would count a piece that is about to be taken back. Captures always remove
        // material, so this ends on its own; the ply cap only bounds the worst case.
        val forcedCapture = moves.first().isCapture
        if (depth <= 0 && (!forcedCapture || ply >= this.depth + MAX_CAPTURE_EXTENSION)) return evaluate(board)
        var alpha = alphaIn
        var best = -INF
        for (move in order(moves)) {
            val score = -search(board.apply(move), depth - 1, -beta, -alpha, ply + 1)
            if (score > best) best = score
            if (best > alpha) alpha = best
            if (alpha >= beta) break
        }
        return best
    }

    /** Longest captures first, then moves that crown. Enough for alpha-beta to cut well. */
    private fun order(moves: List<CheckersMove>): List<CheckersMove> = if (moves.size <= 1) {
        moves
    } else {
        moves.sortedByDescending { move ->
            val reachesBackRow = move.to / CHECKERS_SIZE == 0 || move.to / CHECKERS_SIZE == CHECKERS_SIZE - 1
            move.captured.size * 2 + if (reachesBackRow) 1 else 0
        }
    }

    private fun evaluate(board: CheckersBoard): Int {
        val me = board.sideToMove
        var myMaterial = 0
        var theirMaterial = 0
        var positional = 0
        var pieces = 0
        for (index in 0 until CheckersBoard.CELL_COUNT) {
            val piece = board.pieceAt(index) ?: continue
            pieces++
            val row = index / CHECKERS_SIZE
            val col = index % CHECKERS_SIZE
            var value: Int
            var bonus = 0
            if (piece.isKing) {
                value = KING_VALUE
                if (row in 2..5 && col in 2..5) bonus += CENTRE_BONUS
            } else {
                value = MAN_VALUE
                val advanced = if (piece.side == CheckersSide.BLACK) CHECKERS_SIZE - 1 - row else row
                bonus += advanced * ADVANCE_BONUS
                // A man left on the home row keeps the opponent from crowning there.
                if (advanced == 0) bonus += BACK_ROW_BONUS
                if (col in 2..5) bonus += CENTRE_BONUS
            }
            if (piece.side == me) {
                myMaterial += value
                positional += bonus
            } else {
                theirMaterial += value
                positional -= bonus
            }
        }
        val lead = myMaterial - theirMaterial
        // Trading down while ahead is how a won game is actually converted; without this the
        // search is happy to shuffle kings into the draw count.
        val tradeBonus = when {
            lead > 0 -> (24 - pieces) * TRADE_WEIGHT
            lead < 0 -> -(24 - pieces) * TRADE_WEIGHT
            else -> 0
        }
        return lead + positional + tradeBonus
    }

    companion object {
        private const val INF = 1_000_000
        private const val WIN_SCORE = 100_000
        private const val MAN_VALUE = 100
        private const val KING_VALUE = 160
        private const val ADVANCE_BONUS = 3
        private const val BACK_ROW_BONUS = 8
        private const val CENTRE_BONUS = 4
        private const val TRADE_WEIGHT = 3
        private const val MAX_CAPTURE_EXTENSION = 8
    }
}
