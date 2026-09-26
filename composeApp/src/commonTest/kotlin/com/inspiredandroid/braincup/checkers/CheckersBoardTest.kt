package com.inspiredandroid.braincup.checkers

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private fun at(row: Int, col: Int) = row * CHECKERS_SIZE + col

class CheckersBoardTest {
    @Test
    fun theOpeningHasSevenMovesForBlack() {
        val board = CheckersBoard.startingPosition()
        assertEquals(CheckersSide.BLACK, board.sideToMove)
        assertEquals(12, board.count(CheckersSide.BLACK))
        assertEquals(12, board.count(CheckersSide.WHITE))
        assertEquals(7, board.legalMoves().size)
        assertTrue(board.legalMoves().all { it.to / CHECKERS_SIZE == 4 })
    }

    @Test
    fun aCaptureIsCompulsory() {
        val board = CheckersBoard.fromRows(
            listOf(
                "........",
                "........",
                "........",
                "..w.....",
                ".b......",
                "........",
                ".....b..",
                "........",
            ),
            CheckersSide.BLACK,
        )
        val moves = board.legalMoves()
        assertEquals(1, moves.size)
        assertEquals(listOf(at(4, 1), at(2, 3)), moves.single().path)
        assertEquals(listOf(at(3, 2)), moves.single().captured)
        val after = board.apply(moves.single())
        assertEquals(null, after.pieceAt(at(3, 2)))
        assertEquals(0, after.count(CheckersSide.WHITE))
    }

    @Test
    fun aMultiJumpMustBeTakenToTheEnd() {
        val board = CheckersBoard.fromRows(
            listOf(
                "........",
                "........",
                "........",
                "....w...",
                "........",
                "..w.....",
                ".b......",
                "........",
            ),
            CheckersSide.BLACK,
        )
        val move = board.legalMoves().single()
        assertEquals(listOf(at(6, 1), at(4, 3), at(2, 5)), move.path)
        assertEquals(listOf(at(5, 2), at(3, 4)), move.captured)
    }

    @Test
    fun theCaptureSequenceIsTheMoversChoiceNotTheLongest() {
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
        val lengths = board.legalMoves().map { it.captured.size }.sorted()
        assertEquals(listOf(1, 2), lengths)
    }

    @Test
    fun aManCannotCaptureBackwardsButAKingCan() {
        val rows = listOf(
            "........",
            "........",
            "........",
            "........",
            ".....?..",
            "....w...",
            "........",
            "........",
        )
        val man = CheckersBoard.fromRows(rows.map { it.replace('?', 'b') }, CheckersSide.BLACK)
        assertFalse(man.legalMoves().any { it.isCapture })
        val king = CheckersBoard.fromRows(rows.map { it.replace('?', 'B') }, CheckersSide.BLACK)
        assertEquals(listOf(at(4, 5), at(6, 3)), king.legalMoves().single().path)
    }

    @Test
    fun crowningEndsTheMove() {
        // After crowning on row 0 the new king could jump on over (1,4), but the move ends there.
        val board = CheckersBoard.fromRows(
            listOf(
                "........",
                "..w.w...",
                ".b......",
                "........",
                "........",
                "........",
                "........",
                "........",
            ),
            CheckersSide.BLACK,
        )
        val move = board.legalMoves().single()
        assertEquals(listOf(at(2, 1), at(0, 3)), move.path)
        val after = board.apply(move)
        assertEquals(CheckersPiece(CheckersSide.BLACK, isKing = true), after.pieceAt(at(0, 3)))
        assertEquals(CheckersSide.WHITE, after.sideToMove)
    }

    @Test
    fun aSideWithNoPiecesOrNoMovesLoses() {
        val noPieces = CheckersBoard.fromRows(
            listOf(
                "........",
                "........",
                "........",
                "........",
                "........",
                "........",
                ".b......",
                "........",
            ),
            CheckersSide.WHITE,
        )
        assertEquals(CheckersResult.BLACK_WINS, noPieces.result())

        val blocked = CheckersBoard.fromRows(
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
        assertEquals(CheckersResult.BLACK_WINS, blocked.result())
    }

    @Test
    fun fortyKingMovesEachWithoutACaptureIsADraw() {
        val board = CheckersBoard.fromRows(
            listOf(
                ".W......",
                "........",
                "........",
                "........",
                "........",
                "........",
                "........",
                "......B.",
            ),
            CheckersSide.BLACK,
            quietPlies = CHECKERS_DRAW_PLIES - 1,
        )
        assertEquals(CheckersResult.ONGOING, board.result())
        assertEquals(CheckersResult.DRAW, board.apply(board.legalMoves().first()).result())
    }

    @Test
    fun aManMoveResetsTheDrawCount() {
        val board = CheckersBoard.fromRows(
            listOf(
                ".W......",
                "........",
                "........",
                "........",
                "........",
                "........",
                ".b......",
                "........",
            ),
            CheckersSide.BLACK,
            quietPlies = 50,
        )
        assertEquals(0, board.apply(board.legalMoves().first()).quietPlies)
    }
}
