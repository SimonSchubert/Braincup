package com.inspiredandroid.braincup.games.minicheckers

import com.inspiredandroid.braincup.checkers.CheckersBoard
import com.inspiredandroid.braincup.checkers.CheckersResult
import com.inspiredandroid.braincup.checkers.CheckersSide
import kotlin.random.Random

const val MINI_CHECKERS_SIZE = 6

/** Fifteen moves each without a capture or a man moving. A scenario is won by a short combination,
 *  so this only ends a round the player has already let slip into kings chasing kings. */
const val MINI_CHECKERS_DRAW_PLIES = 30

/** Longest combination the generator and the in-game counter look for. */
const val MINI_CHECKERS_MAX_COMBINATION = 5

/**
 * Random positions, kept only when they hold a forcing combination: a line of player moves after
 * each of which the CPU has exactly one legal reply, ending with the CPU unable to move. The whole
 * win can be worked out in the head, and the CPU cannot wriggle out once the player is on it.
 * Difficulty sets how long the shortest such line is. The player is black, at the bottom and
 * moving first, as in the full Checkers mode.
 */
object MiniCheckersScenarioGenerator {
    // Only player moves branch in the combination search, so a candidate costs about seventy
    // nodes. Hard finds one in roughly two hundred candidates; the cap is for a pathological seed.
    private const val MAX_ATTEMPTS = 5_000

    // A one-move win ends the round before it starts, and a single legal move is no choice.
    private const val MIN_LEGAL_MOVES = 2

    fun generate(difficulty: MiniCheckersDifficulty, random: Random = Random.Default): CheckersBoard {
        repeat(MAX_ATTEMPTS) {
            val board = tryGenerate(difficulty, random)
            if (board != null) return board
        }
        return FALLBACK
    }

    internal fun tryGenerate(difficulty: MiniCheckersDifficulty, random: Random): CheckersBoard? {
        val board = randomCandidate(difficulty.playerPieces, difficulty.cpuPieces, random)
        if (board.result() != CheckersResult.ONGOING) return null
        if (board.legalMoves().size < MIN_LEGAL_MOVES) return null
        val moves = combinationLength(board, MINI_CHECKERS_MAX_COMBINATION) ?: return null
        return board.takeIf { moves in difficulty.combinationMoves }
    }

    /** Player moves in the shortest forcing combination from [board], or null when none of at most
     *  [maxMoves] exists. */
    fun combinationLength(board: CheckersBoard, maxMoves: Int = MINI_CHECKERS_MAX_COMBINATION): Int? = (1..maxMoves).firstOrNull { forcesWinWithin(board, it) }

    private fun forcesWinWithin(board: CheckersBoard, moves: Int): Boolean {
        if (moves == 0) return false
        return board.legalMoves().any { move ->
            val after = board.apply(move)
            val replies = after.legalMoves()
            replies.isEmpty() || (replies.size == 1 && forcesWinWithin(after.apply(replies.single()), moves - 1))
        }
    }

    internal fun randomCandidate(playerCount: IntRange, cpuCount: IntRange, random: Random): CheckersBoard {
        val rows = Array(MINI_CHECKERS_SIZE) { CharArray(MINI_CHECKERS_SIZE) { '.' } }
        val free = DARK_SQUARES.shuffled(random).toMutableList()

        fun place(side: CheckersSide, count: Int) {
            repeat(count) {
                val king = random.nextInt(KING_ONE_IN) == 0
                // A man never stands on the row it would crown on.
                val crownRow = if (side == CheckersSide.BLACK) 0 else MINI_CHECKERS_SIZE - 1
                val square = free.firstOrNull { king || it / MINI_CHECKERS_SIZE != crownRow } ?: return
                free.remove(square)
                val symbol = when (side) {
                    CheckersSide.BLACK -> if (king) 'B' else 'b'
                    CheckersSide.WHITE -> if (king) 'W' else 'w'
                }
                rows[square / MINI_CHECKERS_SIZE][square % MINI_CHECKERS_SIZE] = symbol
            }
        }

        place(CheckersSide.BLACK, playerCount.random(random))
        place(CheckersSide.WHITE, cpuCount.random(random))
        return CheckersBoard.fromRows(
            rows.map { it.concatToString() },
            CheckersSide.BLACK,
            drawPlies = MINI_CHECKERS_DRAW_PLIES,
        )
    }

    /** A generated Hard scenario, won by a five-move combination. */
    internal val FALLBACK: CheckersBoard = CheckersBoard.fromRows(
        listOf(
            ".w.B..",
            "b.....",
            ".w.b..",
            "w.....",
            ".b....",
            "..b...",
        ),
        CheckersSide.BLACK,
        drawPlies = MINI_CHECKERS_DRAW_PLIES,
    )

    private const val KING_ONE_IN = 5

    private val DARK_SQUARES: List<Int> = (0 until MINI_CHECKERS_SIZE * MINI_CHECKERS_SIZE)
        .filter { CheckersBoard.isDarkSquare(it, MINI_CHECKERS_SIZE) }
}
