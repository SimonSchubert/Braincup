package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.games.minichess.PieceType
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SoloChessGameTest {

    @Test
    fun adaptiveDifficultyIsDisabled() {
        assertFalse(SoloChessGame().adaptiveDifficulty)
    }

    @Test
    fun levelIsCoercedToAtLeastOne() {
        assertEquals(1, SoloChessGame(level = 0).level)
        assertEquals(1, SoloChessGame(level = -3).level)
        assertEquals(8, SoloChessGame(level = 8).level)
    }

    @Test
    fun boardGrowsWithLevel() {
        fun build(level: Int) = SoloChessGame(level = level, random = Random(1L)).apply { nextRound() }
        // Board side steps up with level and caps at 6.
        assertEquals(4, build(1).size)
        assertEquals(4, build(3).size)
        assertEquals(5, build(4).size)
        assertEquals(6, build(8).size)
        assertEquals(6, build(20).size)
        // Piece density grows with level (level 1 is a gentle intro; gold-level boards are packed).
        assertTrue(build(10).pieces.size > build(1).pieces.size)
    }

    @Test
    fun everyGeneratedPieceStartsWithTwoCaptures() {
        val game = SoloChessGame(level = 6, random = Random(5L)).apply { nextRound() }
        assertTrue(game.capturesLeft.values.all { it == SoloChessGame.MAX_CAPTURES })
        assertEquals(game.pieces.keys, game.capturesLeft.keys)
    }

    @Test
    fun thereIsExactlyOneKing() {
        for (seed in 0L until 20L) {
            val game = SoloChessGame(level = 7, random = Random(seed)).apply { nextRound() }
            assertEquals(1, game.pieces.values.count { it == PieceType.KING }, "king count seed=$seed")
            assertEquals(PieceType.KING, game.pieces[game.kingCell], "kingCell mismatch seed=$seed")
        }
    }

    @Test
    fun generatedBoardIsSolvableAndLeavesTheKing() {
        for (seed in 0L until 25L) {
            for (level in listOf(1, 3, 5, 8, 10, 14)) {
                val game = SoloChessGame(level = level, random = Random(seed)).apply { nextRound() }
                val pieceCount = game.pieces.size
                assertTrue(pieceCount >= 3, "too few pieces seed=$seed level=$level")
                // The recorded forward solution has one capture per non-final piece.
                assertEquals(pieceCount - 1, game.generatedSolution.size, "solution length seed=$seed level=$level")

                val solved = replaySolution(game)
                assertTrue(solved, "generated solution did not solve seed=$seed level=$level")
                assertTrue(game.isCorrect(""), "isCorrect false after solving seed=$seed level=$level")
                assertEquals(1, game.pieces.size, "more than one piece left seed=$seed level=$level")
                // The king can never be captured, so it must be the survivor.
                assertEquals(PieceType.KING, game.pieces.values.single(), "survivor is not the king seed=$seed level=$level")
            }
        }
    }

    @Test
    fun kingIsNeverACaptureTarget() {
        for (seed in 0L until 20L) {
            val game = SoloChessGame(level = 9, random = Random(seed)).apply { nextRound() }
            game.pieces.keys.forEach { from ->
                assertFalse(game.kingCell in game.captureTargets(from), "king is a target seed=$seed")
            }
        }
    }

    @Test
    fun boardIsNotDeadOnArrival() {
        for (seed in 0L until 25L) {
            for (level in listOf(1, 5, 10)) {
                val game = SoloChessGame(level = level, random = Random(seed)).apply { nextRound() }
                val anyMove = game.pieces.keys.any { game.captureTargets(it).isNotEmpty() }
                assertTrue(anyMove, "no legal capture on a fresh board seed=$seed level=$level")
            }
        }
    }

    @Test
    fun restartRestoresTheInitialBoard() {
        val game = SoloChessGame(level = 6, random = Random(3L)).apply { nextRound() }
        val initialPieces = game.pieces.toMap()
        val initialKing = game.kingCell
        // Play the first solution move, then restart.
        val (from, to) = game.generatedSolution.first()
        game.tap(from)
        game.tap(to)
        assertTrue(game.pieces.size < initialPieces.size)
        game.restart()
        assertEquals(initialPieces, game.pieces)
        assertEquals(initialKing, game.kingCell)
        assertTrue(game.capturesLeft.values.all { it == SoloChessGame.MAX_CAPTURES })
    }

    @Test
    fun tappingAnEmptyCellDoesNothing() {
        val game = SoloChessGame(level = 1, random = Random(0L)).apply { nextRound() }
        val empty = (0 until game.size * game.size).first { it !in game.pieces }
        assertFalse(game.tap(empty))
        assertEquals(null, game.selected)
    }

    @Test
    fun sameSeedProducesIdenticalBoard() {
        val a = SoloChessGame(level = 8, random = Random(42L)).apply { nextRound() }
        val b = SoloChessGame(level = 8, random = Random(42L)).apply { nextRound() }
        assertEquals(a.size, b.size)
        assertEquals(a.pieces, b.pieces)
        assertEquals(a.kingCell, b.kingCell)
        assertEquals(a.generatedSolution, b.generatedSolution)
    }

    /** Replays the recorded forward solution through the public tap API; returns whether it solved. */
    private fun replaySolution(game: SoloChessGame): Boolean {
        var solved = false
        for ((from, to) in game.generatedSolution) {
            game.tap(from) // select the mover
            solved = game.tap(to) // capture
        }
        return solved
    }
}
