package com.inspiredandroid.braincup.games.minicheckers

import com.inspiredandroid.braincup.checkers.CheckersAi
import com.inspiredandroid.braincup.checkers.CheckersBoard
import com.inspiredandroid.braincup.checkers.CheckersDifficulty
import com.inspiredandroid.braincup.checkers.CheckersResult
import com.inspiredandroid.braincup.checkers.CheckersSide
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertTrue

class MiniCheckersScenarioGeneratorTest {
    @Test
    fun everyScenarioHoldsACombinationOfTheRightLength() {
        for (difficulty in MiniCheckersDifficulty.entries) {
            for (seed in 1..40) {
                val board = MiniCheckersScenarioGenerator.generate(difficulty, Random(seed))
                val label = "$difficulty seed $seed"
                assertNotSame(MiniCheckersScenarioGenerator.FALLBACK, board, "$label fell back")
                assertEquals(MINI_CHECKERS_SIZE, board.size, label)
                assertEquals(MINI_CHECKERS_DRAW_PLIES, board.drawPlies, label)
                assertEquals(CheckersSide.BLACK, board.sideToMove, label)
                assertEquals(CheckersResult.ONGOING, board.result(), label)
                val moves = assertNotNull(MiniCheckersScenarioGenerator.combinationLength(board), "$label has no combination")
                assertTrue(moves in difficulty.combinationMoves, "$label: combination of $moves")
                assertNoManOnItsCrownRow(board, label)
            }
        }
    }

    @Test
    fun fallbackHoldsAHardCombination() {
        val moves = assertNotNull(MiniCheckersScenarioGenerator.combinationLength(MiniCheckersScenarioGenerator.FALLBACK))
        assertTrue(moves in MiniCheckersDifficulty.HARD.combinationMoves, "fallback combination of $moves")
    }

    @Test
    fun theCpuHasNoChoiceAlongTheCombination() {
        // Walk the combination by always taking a player move that keeps one, and check the
        // CPU really is left with a single reply each time until it cannot move at all.
        for (seed in 1..10) {
            var board = MiniCheckersScenarioGenerator.generate(MiniCheckersDifficulty.HARD, Random(seed))
            var remaining = MiniCheckersScenarioGenerator.combinationLength(board)!!
            while (board.result() == CheckersResult.ONGOING) {
                val move = board.legalMoves().first { move ->
                    val after = board.apply(move)
                    val replies = after.legalMoves()
                    replies.isEmpty() ||
                        (replies.size == 1 && (MiniCheckersScenarioGenerator.combinationLength(after.apply(replies.single()), remaining - 1) != null))
                }
                board = board.apply(move)
                remaining--
                if (board.result() != CheckersResult.ONGOING) break
                assertEquals(1, board.legalMoves().size, "seed $seed")
                board = board.apply(board.legalMoves().single())
            }
            assertEquals(CheckersResult.BLACK_WINS, board.result(), "seed $seed")
        }
    }

    @Test
    fun hardCpuStaysWithinItsNodeBudget() {
        // The browser runs the search on the UI thread.
        val player = CheckersAi(depth = 3, random = Random(1))
        val cpu = CheckersAi(CheckersDifficulty.HARD, random = Random(2))
        var worst = 0
        for (seed in 1..10) {
            var board = MiniCheckersScenarioGenerator.generate(MiniCheckersDifficulty.HARD, Random(seed))
            while (board.result() == CheckersResult.ONGOING) {
                val ai = if (board.sideToMove == CheckersSide.BLACK) player else cpu
                board = board.apply(ai.bestMove(board)!!)
                if (ai === cpu && cpu.nodesVisited > worst) worst = cpu.nodesVisited
            }
        }
        assertTrue(worst < 120_000, "HARD search visited $worst nodes")
    }

    private fun assertNoManOnItsCrownRow(board: CheckersBoard, label: String) {
        for (index in 0 until board.cellCount) {
            val piece = board.pieceAt(index) ?: continue
            if (piece.isKing) continue
            val crownRow = if (piece.side == CheckersSide.BLACK) 0 else board.size - 1
            assertTrue(index / board.size != crownRow, "$label: man on its crown row at $index")
        }
    }
}
