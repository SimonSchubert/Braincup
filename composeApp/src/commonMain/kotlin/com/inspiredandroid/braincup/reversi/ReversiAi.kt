package com.inspiredandroid.braincup.reversi

import kotlin.random.Random

class ReversiAi(
    private val depth: Int,
    private val usePositionalEval: Boolean = true,
    private val blunderChance: Double = 0.0,
    private val exactSolveEmpties: Int = 0,
    private val random: Random = Random.Default,
) {
    constructor(difficulty: ReversiDifficulty, random: Random = Random.Default) : this(
        depth = difficulty.depth,
        usePositionalEval = difficulty.usePositionalEval,
        blunderChance = difficulty.blunderChance,
        exactSolveEmpties = difficulty.exactSolveEmpties,
        random = random,
    )

    /** Nodes searched by the last [bestMove]. The browser runs this on the UI thread, so the
     *  tests hold it to a budget rather than trusting the depth numbers to stay affordable. */
    var nodesVisited: Int = 0
        private set

    fun bestMove(board: ReversiBoard): Int? {
        nodesVisited = 0
        val moves = board.legalMoves().shuffled(random)
        if (moves.isEmpty()) return null
        if (blunderChance > 0 && moves.size > 1 && random.nextDouble() < blunderChance) {
            return moves.first()
        }
        // Decided once, here, and never re-tested inside the search. Every node at a given ply has
        // the same number of empty squares, so this is well defined — and a per-node test would
        // start a full endgame solve at every leaf of a depth-6 search instead of at the root.
        val exact = exactSolveEmpties > 0 && board.emptyCount() <= exactSolveEmpties
        var best = moves[0]
        var bestScore = -INF
        var alpha = -INF
        for (move in order(moves)) {
            val score = -search(board.apply(move), depth - 1, -INF, -alpha, exact)
            if (score > bestScore) {
                bestScore = score
                best = move
            }
            if (score > alpha) alpha = score
        }
        return best
    }

    private fun search(board: ReversiBoard, depth: Int, alphaIn: Int, beta: Int, exact: Boolean): Int {
        nodesVisited++
        val moves = board.legalMoves()
        if (moves.isEmpty()) {
            val passed = board.passTurn()
            // Two passes running is the end of the game, and the only end there is: a 6x6 game
            // regularly finishes with squares nobody can play.
            if (passed.legalMoves().isEmpty()) return finalScore(board)
            // A forced pass is not a loss, and it is not a ply either, so the depth is unchanged.
            // The recursion still terminates: the next pass ends the game, and every other move
            // adds a disc.
            return -search(passed, depth, -beta, -alphaIn, exact)
        }
        if (depth <= 0 && !exact) return evaluate(board)
        var alpha = alphaIn
        var best = -INF
        for (move in order(moves)) {
            val score = -search(board.apply(move), depth - 1, -beta, -alpha, exact)
            if (score > best) best = score
            if (best > alpha) alpha = best
            if (alpha >= beta) break
        }
        return best
    }

    /** Corners first, the squares that give a corner away last. Cheap, and it is most of what
     *  alpha-beta needs to prune well here. */
    private fun order(moves: List<Int>): List<Int> = if (moves.size <= 1) {
        moves
    } else {
        moves.sortedByDescending { SQUARE_WEIGHTS[it] }
    }

    /** A proven finish has to outrank any heuristic score, or the search trades a won endgame for
     *  a better-looking position one ply short of it. */
    private fun finalScore(board: ReversiBoard): Int {
        val margin = board.count(board.sideToMove) - board.count(board.sideToMove.opponent)
        return when {
            margin > 0 -> WIN_SCORE + margin
            margin < 0 -> -WIN_SCORE + margin
            else -> 0
        }
    }

    private fun evaluate(board: ReversiBoard): Int {
        val me = board.sideToMove
        val them = me.opponent
        if (!usePositionalEval) {
            var discs = 0
            var corners = 0
            for (index in 0 until ReversiBoard.CELL_COUNT) {
                val disc = board.discAt(index) ?: continue
                val sign = if (disc == me) 1 else -1
                discs += sign
                if (index in CORNERS) corners += sign
            }
            return discs + SIMPLE_CORNER_BONUS * corners
        }
        var positional = 0
        for (index in 0 until ReversiBoard.CELL_COUNT) {
            val disc = board.discAt(index) ?: continue
            positional += if (disc == me) SQUARE_WEIGHTS[index] else -SQUARE_WEIGHTS[index]
        }
        // Mobility is the term that actually wins Reversi, so it carries most of the weight.
        // Raising it further is not a free strength knob: past this the AI starts refusing good
        // corners to keep its move count up.
        val mobility = board.legalMoves(me).size - board.legalMoves(them).size
        return positional + MOBILITY_WEIGHT * mobility
    }

    companion object {
        private const val INF = 1_000_000
        private const val WIN_SCORE = 100_000
        private const val MOBILITY_WEIGHT = 50
        private const val SIMPLE_CORNER_BONUS = 10

        private val CORNERS = setOf(
            0,
            REVERSI_SIZE - 1,
            REVERSI_SIZE * (REVERSI_SIZE - 1),
            REVERSI_SIZE * REVERSI_SIZE - 1,
        )

        // A corner can never be flipped, so it is the one square worth paying for, and its diagonal
        // neighbour is the worst on the board because taking it opens that corner. 6x6 has no room
        // for the 8x8 edge geometry: an edge is corner, C-square, two middles, C-square, corner,
        // and nothing else. The table stays static on purpose — relieving the X and C penalties
        // once the corner is taken is standard 8x8 advice, and it loses here, because a third of a
        // 6x6 board is corner-adjacent and neutralising it throws away most of the discrimination.
        private val SQUARE_WEIGHTS = intArrayOf(
            100, -20, 10, 10, -20, 100,
            -20, -50, -2, -2, -50, -20,
            10, -2, 1, 1, -2, 10,
            10, -2, 1, 1, -2, 10,
            -20, -50, -2, -2, -50, -20,
            100, -20, 10, 10, -20, 100,
        )
    }
}
