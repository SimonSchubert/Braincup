package com.inspiredandroid.braincup.games.minichess

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ChessAiTest {
    private fun board(
        sideToMove: Color = Color.WHITE,
        vararg pieces: Pair<Square, Piece>,
    ): ChessBoard = ChessBoard.fromMap(pieces.toMap(), sideToMove)

    @Test
    fun returnsLegalMoveForVariousPositions() {
        val ai = ChessAi(depth = 2, random = Random(42))
        val positions = listOf(
            board(
                Color.WHITE,
                Square(0, 0) to Piece(PieceType.KING, Color.WHITE),
                Square(2, 2) to Piece(PieceType.KNIGHT, Color.WHITE),
                Square(4, 4) to Piece(PieceType.KING, Color.BLACK),
            ),
            board(
                Color.WHITE,
                Square(0, 0) to Piece(PieceType.KING, Color.WHITE),
                Square(0, 2) to Piece(PieceType.ROOK, Color.WHITE),
                Square(4, 0) to Piece(PieceType.QUEEN, Color.BLACK),
                Square(4, 4) to Piece(PieceType.KING, Color.BLACK),
            ),
            board(
                Color.WHITE,
                Square(0, 0) to Piece(PieceType.KING, Color.WHITE),
                Square(2, 1) to Piece(PieceType.PAWN, Color.WHITE),
                Square(4, 4) to Piece(PieceType.KING, Color.BLACK),
                Square(3, 3) to Piece(PieceType.BISHOP, Color.BLACK),
            ),
        )
        for (b in positions) {
            val move = ai.bestMove(b)
            assertNotNull(move)
            assertTrue(b.legalMoves().contains(move), "AI returned illegal move $move")
        }
    }

    @Test
    fun aiCapturesHangingQueen() {
        // White rook on file 3 attacks an undefended black queen on the same file.
        // Best move is to take the queen.
        val b = board(
            Color.WHITE,
            Square(0, 0) to Piece(PieceType.KING, Color.WHITE),
            Square(3, 0) to Piece(PieceType.ROOK, Color.WHITE),
            Square(3, 2) to Piece(PieceType.QUEEN, Color.BLACK),
            Square(0, 4) to Piece(PieceType.KING, Color.BLACK),
        )
        val ai = ChessAi(depth = 2, random = Random(1))
        val move = ai.bestMove(b)
        assertEquals(Move(Square(3, 0), Square(3, 2)), move)
    }

    @Test
    fun aiPlaysOnlyLegalMove() {
        // Queen on file 0 attacks white king; rook on rank 0 attacks rank 0.
        // (0,1) attacked by queen file; (1,0) attacked by rook rank; (1,1) safe.
        val b = board(
            Color.WHITE,
            Square(0, 0) to Piece(PieceType.KING, Color.WHITE),
            Square(0, 4) to Piece(PieceType.QUEEN, Color.BLACK),
            Square(4, 0) to Piece(PieceType.ROOK, Color.BLACK),
            Square(4, 4) to Piece(PieceType.KING, Color.BLACK),
        )
        val legal = b.legalMoves()
        assertEquals(1, legal.size, "expected exactly one legal move, got $legal")
        assertEquals(Move(Square(0, 0), Square(1, 1)), legal[0])

        val ai = ChessAi(depth = 3, random = Random(7))
        assertEquals(legal[0], ai.bestMove(b))
    }

    @Test
    fun aiFindsMateInOne() {
        // White king at (2,3), white queen at (1,2). Several queen moves give mate
        // (e.g. Q→(1,3) or Q→(1,4)). The AI should play any move that mates immediately.
        val b = board(
            Color.WHITE,
            Square(2, 3) to Piece(PieceType.KING, Color.WHITE),
            Square(1, 2) to Piece(PieceType.QUEEN, Color.WHITE),
            Square(0, 4) to Piece(PieceType.KING, Color.BLACK),
        )
        val ai = ChessAi(depth = 3, random = Random(0))
        val move = ai.bestMove(b)
        assertNotNull(move)
        val after = b.apply(move)
        assertTrue(after.isCheckmate(), "AI's chosen move $move did not deliver mate")
    }

    @Test
    fun aiReturnsNullWhenNoLegalMoves() {
        // Stalemate: black has no moves but isn't in check.
        val b = board(
            Color.BLACK,
            Square(0, 4) to Piece(PieceType.KING, Color.BLACK),
            Square(1, 2) to Piece(PieceType.QUEEN, Color.WHITE),
            Square(2, 2) to Piece(PieceType.KING, Color.WHITE),
        )
        val ai = ChessAi(depth = 2, random = Random(0))
        assertEquals(null, ai.bestMove(b))
    }
}
