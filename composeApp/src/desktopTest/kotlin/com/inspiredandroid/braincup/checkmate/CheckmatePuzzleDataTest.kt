package com.inspiredandroid.braincup.checkmate

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail

class CheckmatePuzzleDataTest {
    private val puzzles = CheckmatePuzzle.parse(File("src/commonMain/composeResources/${CheckmatePuzzles.PATH}").readText())

    @Test
    fun countMatchesTheMenuConstant() {
        assertEquals(CheckmatePuzzles.COUNT, puzzles.size)
        assertEquals(puzzles.size, puzzles.map { it.id }.toSet().size)
    }

    @Test
    fun solutionLineMates() {
        for (puzzle in puzzles) {
            var attempt = puzzle.start()
            puzzle.solution.forEachIndexed { ply, code ->
                if (ply % 2 == 0) {
                    val move = assertNotNull(CheckmatePuzzle.decode(code))
                    assertTrue(move in attempt.board.legalMoves(), "${puzzle.id}: $code is not legal")
                    attempt = puzzle.play(attempt, move)
                } else {
                    assertTrue(attempt.awaitingReply, "${puzzle.id}: no reply expected at ply $ply")
                    assertEquals(code, puzzle.tabledReply(attempt.solverPath), "${puzzle.id} at ply $ply")
                    attempt = puzzle.answer(attempt)
                }
            }
            assertEquals(CheckmateOutcome.SOLVED, attempt.outcome, puzzle.id)
            assertEquals(puzzle.mateIn, attempt.solverMoves, puzzle.id)
        }
    }

    @Test
    fun aWrongLineEndsWithoutMate() {
        for (puzzle in puzzles) {
            var attempt = puzzle.start()
            while (attempt.outcome == null) {
                val wrong = attempt.board.legalMoves()
                    .filter { CheckmatePuzzle.encode(it) != puzzle.solution[attempt.solverMoves * 2] }
                    .firstOrNull { !attempt.board.apply(it).isCheckmate() }
                    ?: break
                attempt = puzzle.answer(puzzle.play(attempt, wrong))
            }
            assertTrue(attempt.outcome != CheckmateOutcome.SOLVED, puzzle.id)
            assertTrue(attempt.solverMoves <= puzzle.mateIn, puzzle.id)
        }
    }

    // Every line the solver can play has a tabled, legal answer, so the search fallback never runs.
    @Test
    fun everyDefenceIsTabledAndLegal() {
        for (puzzle in puzzles) walk(puzzle, puzzle.start())
    }

    private fun walk(puzzle: CheckmatePuzzle, attempt: CheckmateAttempt) {
        for (move in attempt.board.legalMoves()) {
            val played = puzzle.play(attempt, move)
            if (!played.awaitingReply) continue
            val code = puzzle.tabledReply(played.solverPath)
                ?: fail("${puzzle.id}: no reply after ${played.solverPath}")
            val reply = CheckmatePuzzle.decode(code)
            if (reply !in played.board.legalMoves()) fail("${puzzle.id}: illegal reply $code after ${played.solverPath}")
            val answered = puzzle.answer(played)
            if (answered.outcome == null) walk(puzzle, answered)
        }
    }
}
