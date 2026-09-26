package com.inspiredandroid.braincup.checkers

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

private fun at(row: Int, col: Int) = row * CHECKERS_SIZE + col

private fun playOut(black: CheckersAi, white: CheckersAi): CheckersResult {
    var board = CheckersBoard.startingPosition()
    while (board.result() == CheckersResult.ONGOING) {
        val ai = if (board.sideToMove == CheckersSide.BLACK) black else white
        board = board.apply(ai.bestMove(board)!!)
    }
    return board.result()
}

class CheckersAiTest {
    @Test
    fun everyDifficultyOnlyEverPlaysLegalMoves() {
        for (difficulty in CheckersDifficulty.entries) {
            val ai = CheckersAi(difficulty, random = Random(11))
            var board = CheckersBoard.startingPosition()
            while (board.result() == CheckersResult.ONGOING) {
                val move = ai.bestMove(board)
                assertNotNull(move, "$difficulty returned no move")
                assertTrue(move in board.legalMoves(), "$difficulty played illegal move $move")
                board = board.apply(move)
            }
        }
    }

    @Test
    fun bestMoveIsNullWhenTheSideToMoveIsStuck() {
        val board = CheckersBoard.fromRows(
            listOf(
                "........",
                "........",
                "........",
                "........",
                "........",
                "........",
                ".......w",
                "......b.",
            ),
            CheckersSide.WHITE,
        )
        assertNull(CheckersAi(CheckersDifficulty.HARD, random = Random(1)).bestMove(board))
    }

    @Test
    fun itDoesNotStepIntoACapture() {
        // (5,2) to (4,1) or (4,3) walks into the white man's jump; (5,6) to (4,5) or (4,7) is safe.
        val board = CheckersBoard.fromRows(
            listOf(
                "........",
                "........",
                "........",
                "..w.....",
                "........",
                "..b...b.",
                "........",
                "........",
            ),
            CheckersSide.BLACK,
        )
        for (seed in 1..5) {
            val move = CheckersAi(CheckersDifficulty.MEDIUM, random = Random(seed)).bestMove(board)
            assertEquals(at(5, 6), move?.from, "seed $seed")
        }
    }

    @Test
    fun itTakesTheDoubleJumpOverTheSingle() {
        // Both captures are legal; going left takes (4,3) and then (2,1), going right takes one.
        val board = CheckersBoard.fromRows(
            listOf(
                "........",
                "........",
                ".w......",
                "........",
                "...w.w..",
                "....b...",
                "........",
                "........",
            ),
            CheckersSide.BLACK,
        )
        for (seed in 1..3) {
            val move = CheckersAi(CheckersDifficulty.MEDIUM, random = Random(seed)).bestMove(board)
            assertEquals(listOf(at(5, 4), at(3, 2), at(1, 0)), move?.path, "seed $seed")
        }
    }

    @Test
    fun hardBeatsEasy() {
        var hardWins = 0
        var easyWins = 0
        for (seed in 1..3) {
            val hard = CheckersAi(CheckersDifficulty.HARD, random = Random(seed))
            val easy = CheckersAi(CheckersDifficulty.EASY, random = Random(seed))
            when (playOut(black = hard, white = easy)) {
                CheckersResult.BLACK_WINS -> hardWins++
                CheckersResult.WHITE_WINS -> easyWins++
                else -> Unit
            }
            when (playOut(black = easy, white = hard)) {
                CheckersResult.WHITE_WINS -> hardWins++
                CheckersResult.BLACK_WINS -> easyWins++
                else -> Unit
            }
        }
        assertTrue(hardWins > easyWins, "hard won $hardWins, easy won $easyWins")
    }

    @Test
    fun hardStaysWithinItsNodeBudget() {
        // The browser runs the search on the UI thread, so cost is a shipping constraint rather
        // than a nicety.
        var worst = 0
        for (seed in 1..2) {
            val hard = CheckersAi(CheckersDifficulty.HARD, random = Random(seed))
            val medium = CheckersAi(CheckersDifficulty.MEDIUM, random = Random(seed))
            var board = CheckersBoard.startingPosition()
            while (board.result() == CheckersResult.ONGOING) {
                val ai = if (board.sideToMove == CheckersSide.BLACK) hard else medium
                board = board.apply(ai.bestMove(board)!!)
                if (ai === hard && hard.nodesVisited > worst) worst = hard.nodesVisited
            }
        }
        // Worst observed across these games is under 70k nodes.
        assertTrue(worst < 120_000, "HARD search visited $worst nodes")
    }
}
