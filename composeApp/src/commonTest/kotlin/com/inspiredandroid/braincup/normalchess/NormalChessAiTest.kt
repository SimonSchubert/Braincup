package com.inspiredandroid.braincup.normalchess

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class NormalChessAiTest {
    @Test
    fun findsMateInOneAtAllDepths() {
        // White to play and mate in one with Rook to h8 (back-rank).
        val b = NormalChessBoard.fromMap(
            pieces = mapOf(
                Square(0, 0) to Piece(PieceType.KING, Color.WHITE),
                Square(0, 6) to Piece(PieceType.ROOK, Color.WHITE),
                Square(6, 7) to Piece(PieceType.KING, Color.BLACK),
                Square(5, 6) to Piece(PieceType.PAWN, Color.BLACK),
                Square(6, 6) to Piece(PieceType.PAWN, Color.BLACK),
                Square(7, 6) to Piece(PieceType.PAWN, Color.BLACK),
            ),
            sideToMove = Color.WHITE,
        )
        for (depth in intArrayOf(1, 2, 3)) {
            val ai = NormalChessAi(depth = depth, random = Random(42))
            val move = ai.bestMove(b)
            assertNotNull(move)
            val after = b.apply(move)
            assertTrue(after.isCheckmate(), "depth $depth did not find mate-in-1, played $move")
        }
    }

    @Test
    fun capturesHangingQueen() {
        // White rook on a1 can capture undefended black queen on a8.
        val b = NormalChessBoard.fromMap(
            pieces = mapOf(
                Square(4, 0) to Piece(PieceType.KING, Color.WHITE),
                Square(0, 0) to Piece(PieceType.ROOK, Color.WHITE),
                Square(4, 7) to Piece(PieceType.KING, Color.BLACK),
                Square(0, 7) to Piece(PieceType.QUEEN, Color.BLACK),
            ),
            sideToMove = Color.WHITE,
        )
        val ai = NormalChessAi(depth = 2, random = Random(7))
        val move = ai.bestMove(b)
        assertNotNull(move)
        assertEquals(Square(0, 0), move.from)
        assertEquals(Square(0, 7), move.to)
    }

    @Test
    fun doesNotBlunderFreeQueenAtHardDepth() {
        // White queen on d4 attacked by black knight on c6 — white must move queen.
        val b = NormalChessBoard.fromMap(
            pieces = mapOf(
                Square(4, 0) to Piece(PieceType.KING, Color.WHITE),
                Square(3, 3) to Piece(PieceType.QUEEN, Color.WHITE),
                Square(4, 7) to Piece(PieceType.KING, Color.BLACK),
                Square(2, 5) to Piece(PieceType.KNIGHT, Color.BLACK),
            ),
            sideToMove = Color.WHITE,
        )
        val ai = NormalChessAi(depth = 3, random = Random(7))
        val move = ai.bestMove(b)
        assertNotNull(move)
        // Either move the queen, or capture the knight. Both are fine.
        // What we must NOT do is leave the queen on d4 by moving a different piece
        // when the queen has safe squares.
        val movedQueen = move.from == Square(3, 3)
        val capturedAttacker = move.to == Square(2, 5)
        assertTrue(movedQueen || capturedAttacker, "AI left queen hanging: played $move")
    }
}
