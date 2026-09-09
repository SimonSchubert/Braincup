package com.inspiredandroid.braincup.reversi

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

private fun at(row: Int, col: Int) = row * REVERSI_SIZE + col

/** The best disc margin [me] can still force, by exhaustive search. Only usable on a board with
 *  few empties left; it is what the exact endgame solver is checked against. */
private fun bestFinalMargin(board: ReversiBoard, me: Disc): Int {
    if (board.isGameOver()) return board.count(me) - board.count(me.opponent)
    val moves = board.legalMoves()
    if (moves.isEmpty()) return bestFinalMargin(board.passTurn(), me)
    val outcomes = moves.map { bestFinalMargin(board.apply(it), me) }
    return if (board.sideToMove == me) outcomes.max() else outcomes.min()
}

private fun playOut(black: ReversiAi, white: ReversiAi): ReversiResult {
    var board = ReversiBoard.startingPosition()
    while (!board.isGameOver()) {
        if (board.legalMoves().isEmpty()) {
            board = board.passTurn()
            continue
        }
        val ai = if (board.sideToMove == Disc.BLACK) black else white
        board = board.apply(ai.bestMove(board)!!)
    }
    return board.result()
}

class ReversiAiTest {
    @Test
    fun everyDifficultyOnlyEverPlaysLegalMoves() {
        for (difficulty in ReversiDifficulty.entries) {
            val ai = ReversiAi(difficulty, random = Random(11))
            var board = ReversiBoard.startingPosition()
            while (!board.isGameOver()) {
                if (board.legalMoves().isEmpty()) {
                    board = board.passTurn()
                    continue
                }
                val move = ai.bestMove(board)
                assertNotNull(move, "$difficulty returned no move with ${board.legalMoves()} available")
                assertTrue(move in board.legalMoves(), "$difficulty played illegal move $move")
                board = board.apply(move)
            }
        }
    }

    @Test
    fun bestMoveIsNullWhenTheSideToMoveIsStuck() {
        val b = ReversiBoard.fromRows(
            listOf(
                "BBBBBB",
                "BBBBBB",
                "BBBBBW",
                "......",
                "......",
                "......",
            ),
            sideToMove = Disc.WHITE,
        )
        assertNull(ReversiAi(ReversiDifficulty.HARD, random = Random(1)).bestMove(b))
    }

    @Test
    fun thePositionalEvaluationPrefersACornerToABiggerFlip() {
        // Two legal moves: the corner (0,0), worth one disc, and (3,0), worth three. A corner can
        // never be flipped back, so the table has to rate it above the bigger haul.
        //
        // Searched one ply deep on purpose. This is a claim about the evaluation, not about the
        // shipped search: declining a corner to keep the opponent short of moves is real Reversi,
        // and HARD does it often enough that asserting otherwise would be asserting a weakness.
        val b = ReversiBoard.fromRows(
            listOf(
                ".WB...",
                "......",
                "......",
                ".WWWB.",
                "......",
                "......",
            ),
            sideToMove = Disc.BLACK,
        )
        assertEquals(setOf(at(0, 0), at(3, 0)), b.legalMoves().toSet())
        for (seed in 1..3) {
            val ai = ReversiAi(depth = 1, usePositionalEval = true, random = Random(seed))
            assertEquals(at(0, 0), ai.bestMove(b), "seed $seed")
        }
    }

    @Test
    fun theTwoEvaluationsDisagreeAboutAnXSquare() {
        // Two legal moves, no corner in play: (1,1) flips three discs but is the square diagonally
        // inside a corner, which is the one place you never want a disc — it is what lets the
        // opponent take the corner. (3,3) flips one and costs nothing.
        val b = ReversiBoard.fromRows(
            listOf(
                "......",
                "..WWWB",
                "......",
                "....WB",
                "......",
                "......",
            ),
            sideToMove = Disc.BLACK,
        )
        assertEquals(setOf(at(1, 1), at(3, 3)), b.legalMoves().toSet())
        assertEquals(3, b.flipsFor(at(1, 1)).size)
        assertEquals(1, b.flipsFor(at(3, 3)).size)

        val positional = ReversiAi(depth = 1, usePositionalEval = true, random = Random(3))
        assertEquals(at(3, 3), positional.bestMove(b), "the positional evaluation should refuse the X-square")

        val greedy = ReversiAi(depth = 1, usePositionalEval = false, random = Random(3))
        assertEquals(at(1, 1), greedy.bestMove(b), "the greedy evaluation should take the three discs")
    }

    @Test
    fun theExactSolverPlaysTheEndgamePerfectly() {
        val b = ReversiBoard.fromRows(
            listOf(
                "WWWWWW",
                "WBBBBW",
                "WBWWBW",
                ".BWWB.",
                "..BB..",
                "WWWWWW",
            ),
            sideToMove = Disc.BLACK,
        )
        assertTrue(b.emptyCount() <= ReversiDifficulty.HARD.exactSolveEmpties)
        assertTrue(b.legalMoves().size > 1, "the position needs a real choice to be worth testing")
        val move = ReversiAi(ReversiDifficulty.HARD, random = Random(5)).bestMove(b)
        assertNotNull(move)
        assertEquals(
            bestFinalMargin(b, Disc.BLACK),
            bestFinalMargin(b.apply(move), Disc.BLACK),
            "the exact solver gave up margin by playing $move",
        )
    }

    @Test
    fun hardBeatsNormal() {
        var hardWins = 0
        var normalWins = 0
        for (seed in 1..4) {
            val hard = ReversiAi(ReversiDifficulty.HARD, random = Random(seed))
            val normal = ReversiAi(ReversiDifficulty.NORMAL, random = Random(seed))
            when (playOut(black = hard, white = normal)) {
                ReversiResult.BLACK_WINS -> hardWins++
                ReversiResult.WHITE_WINS -> normalWins++
                else -> Unit
            }
            when (playOut(black = normal, white = hard)) {
                ReversiResult.WHITE_WINS -> hardWins++
                ReversiResult.BLACK_WINS -> normalWins++
                else -> Unit
            }
        }
        assertTrue(hardWins > normalWins, "hard won $hardWins, normal won $normalWins")
    }

    @Test
    fun theMidgameSearchStaysWithinItsNodeBudget() {
        // The browser runs the search on the UI thread, so cost is a shipping constraint rather
        // than a nicety. Worst observed across a full HARD game is a few thousand nodes; this
        // catches the exact solve being triggered per node instead of once at the root, which
        // costs four orders of magnitude.
        val ai = ReversiAi(ReversiDifficulty.HARD, random = Random(9))
        var board = ReversiBoard.startingPosition()
        var worst = 0
        while (!board.isGameOver() && board.emptyCount() > ReversiDifficulty.HARD.exactSolveEmpties) {
            if (board.legalMoves().isEmpty()) {
                board = board.passTurn()
                continue
            }
            board = board.apply(ai.bestMove(board)!!)
            if (ai.nodesVisited > worst) worst = ai.nodesVisited
        }
        assertTrue(worst < 50_000, "midgame search visited $worst nodes")
    }

    @Test
    fun theEndgameSolveStaysWithinItsNodeBudget() {
        var worst = 0
        for (seed in 1..12) {
            val rolls = Random(seed * 31)
            var board = ReversiBoard.startingPosition()
            while (!board.isGameOver() && board.emptyCount() > ReversiDifficulty.HARD.exactSolveEmpties) {
                if (board.legalMoves().isEmpty()) {
                    board = board.passTurn()
                    continue
                }
                val moves = board.legalMoves()
                board = board.apply(moves[rolls.nextInt(moves.size)])
            }
            if (board.isGameOver() || board.legalMoves().isEmpty()) continue
            val ai = ReversiAi(ReversiDifficulty.HARD, random = Random(2))
            ai.bestMove(board)
            if (ai.nodesVisited > worst) worst = ai.nodesVisited
        }
        assertTrue(worst in 1 until 400_000, "endgame solve visited $worst nodes")
    }
}
