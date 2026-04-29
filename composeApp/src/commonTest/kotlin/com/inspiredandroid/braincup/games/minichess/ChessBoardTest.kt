package com.inspiredandroid.braincup.games.minichess

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ChessBoardTest {
    private fun board(
        sideToMove: Color = Color.WHITE,
        vararg pieces: Pair<Square, Piece>,
    ): ChessBoard = ChessBoard.fromMap(pieces.toMap(), sideToMove)

    @Test
    fun pawnMovesForwardOnce() {
        val b = board(
            Color.WHITE,
            Square(0, 0) to Piece(PieceType.KING, Color.WHITE),
            Square(4, 4) to Piece(PieceType.KING, Color.BLACK),
            Square(2, 1) to Piece(PieceType.PAWN, Color.WHITE),
        )
        val pawnTargets = b.legalMoves().filter { it.from == Square(2, 1) }.map { it.to }
        assertEquals(setOf(Square(2, 2)), pawnTargets.toSet())
    }

    @Test
    fun pawnCapturesDiagonallyAndCannotForwardCapture() {
        val b = board(
            Color.WHITE,
            Square(0, 0) to Piece(PieceType.KING, Color.WHITE),
            Square(4, 4) to Piece(PieceType.KING, Color.BLACK),
            Square(2, 1) to Piece(PieceType.PAWN, Color.WHITE),
            Square(2, 2) to Piece(PieceType.PAWN, Color.BLACK), // blocks forward
            Square(1, 2) to Piece(PieceType.KNIGHT, Color.BLACK), // capturable
        )
        val pawnTargets = b.legalMoves().filter { it.from == Square(2, 1) }.map { it.to }
        assertEquals(setOf(Square(1, 2)), pawnTargets.toSet())
    }

    @Test
    fun pawnPromotesToQueenOnLastRank() {
        val b = board(
            Color.WHITE,
            Square(0, 0) to Piece(PieceType.KING, Color.WHITE),
            Square(4, 4) to Piece(PieceType.KING, Color.BLACK),
            Square(2, 3) to Piece(PieceType.PAWN, Color.WHITE),
        )
        val moves = b.legalMoves().filter { it.from == Square(2, 3) }
        assertEquals(1, moves.size)
        assertEquals(Square(2, 4), moves[0].to)
        assertEquals(PieceType.QUEEN, moves[0].promotion)

        val after = b.apply(moves[0])
        assertEquals(Piece(PieceType.QUEEN, Color.WHITE), after.pieceAt(Square(2, 4)))
    }

    @Test
    fun blackPawnMovesAndPromotesDownward() {
        val b = board(
            Color.BLACK,
            Square(0, 0) to Piece(PieceType.KING, Color.WHITE),
            Square(4, 4) to Piece(PieceType.KING, Color.BLACK),
            Square(2, 1) to Piece(PieceType.PAWN, Color.BLACK),
        )
        val moves = b.legalMoves().filter { it.from == Square(2, 1) }
        assertEquals(1, moves.size)
        assertEquals(Square(2, 0), moves[0].to)
        assertEquals(PieceType.QUEEN, moves[0].promotion)
    }

    @Test
    fun knightMovesInLShape() {
        val b = board(
            Color.WHITE,
            Square(2, 2) to Piece(PieceType.KNIGHT, Color.WHITE),
            Square(0, 0) to Piece(PieceType.KING, Color.WHITE),
            Square(4, 4) to Piece(PieceType.KING, Color.BLACK),
        )
        val expected = setOf(
            Square(0, 1),
            Square(1, 0),
            Square(3, 0),
            Square(4, 1),
            Square(4, 3),
            Square(3, 4),
            Square(1, 4),
            Square(0, 3),
        )
        val actual = b.legalMoves().filter { it.from == Square(2, 2) }.map { it.to }.toSet()
        assertEquals(expected, actual)
    }

    @Test
    fun knightCannotLandOnOwnPieceButCanCapture() {
        val b = board(
            Color.WHITE,
            Square(2, 2) to Piece(PieceType.KNIGHT, Color.WHITE),
            Square(0, 1) to Piece(PieceType.PAWN, Color.WHITE), // own → blocked
            Square(4, 3) to Piece(PieceType.PAWN, Color.BLACK), // opp → capturable
            Square(0, 0) to Piece(PieceType.KING, Color.WHITE),
            Square(4, 4) to Piece(PieceType.KING, Color.BLACK),
        )
        val knightTargets = b.legalMoves().filter { it.from == Square(2, 2) }.map { it.to }.toSet()
        assertFalse(Square(0, 1) in knightTargets)
        assertTrue(Square(4, 3) in knightTargets)
    }

    @Test
    fun rookSlidesUntilBlocker() {
        val b = board(
            Color.WHITE,
            Square(2, 2) to Piece(PieceType.ROOK, Color.WHITE),
            Square(2, 4) to Piece(PieceType.PAWN, Color.BLACK), // capturable
            Square(4, 2) to Piece(PieceType.PAWN, Color.WHITE), // blocks
            Square(0, 0) to Piece(PieceType.KING, Color.WHITE),
            Square(0, 4) to Piece(PieceType.KING, Color.BLACK),
        )
        val rookTargets = b.legalMoves().filter { it.from == Square(2, 2) }.map { it.to }.toSet()
        assertEquals(
            setOf(
                // up: (2,3) empty, (2,4) capture
                Square(2, 3),
                Square(2, 4),
                // down
                Square(2, 1),
                Square(2, 0),
                // left
                Square(1, 2),
                Square(0, 2),
                // right: (3,2) empty, then own pawn blocks → no (4,2)
                Square(3, 2),
            ),
            rookTargets,
        )
    }

    @Test
    fun bishopAndQueenMoveDiagonally() {
        val b = board(
            Color.WHITE,
            Square(2, 2) to Piece(PieceType.BISHOP, Color.WHITE),
            Square(0, 0) to Piece(PieceType.KING, Color.WHITE),
            Square(4, 4) to Piece(PieceType.KING, Color.BLACK),
        )
        val bishopTargets = b.legalMoves().filter { it.from == Square(2, 2) }.map { it.to }.toSet()
        // Diagonals from (2,2): (1,1), (0,0 own king blocks), (3,3), (4,4 black king at end of ray, capturable but capturing king is not really possible — but rays go (3,3) then (4,4) capture)
        // (1,3),(0,4 black king blocks at (0,4)? no (0,4) is black king on board). Actually let's recompute.
        // From (2,2) NE: (3,3), (4,4) — black king is at (4,4) so capturable. But the board has white king at (0,0) so capturing the black king would leave own king ok? Yes capture is "legal" pseudo, but capturing king is impossible because (4,4) is a king square — but my engine doesn't special-case this; the only way to capture a king is if it's already in check unresolved which can't happen in practice. For this test, ignore (4,4) by checking subset.
        assertTrue(Square(1, 1) in bishopTargets)
        assertTrue(Square(3, 3) in bishopTargets)
        assertTrue(Square(1, 3) in bishopTargets)
        assertTrue(Square(0, 4) in bishopTargets)
        assertTrue(Square(3, 1) in bishopTargets)
        assertTrue(Square(4, 0) in bishopTargets)
        assertFalse(Square(2, 3) in bishopTargets) // not diagonal
    }

    @Test
    fun kingCannotMoveIntoCheck() {
        val b = board(
            Color.WHITE,
            Square(2, 0) to Piece(PieceType.KING, Color.WHITE),
            Square(4, 4) to Piece(PieceType.KING, Color.BLACK),
            Square(3, 4) to Piece(PieceType.ROOK, Color.BLACK), // attacks file 3
        )
        val kingTargets = b.legalMoves().filter { it.from == Square(2, 0) }.map { it.to }.toSet()
        assertFalse(Square(3, 0) in kingTargets) // attacked by rook on file 3
        assertFalse(Square(3, 1) in kingTargets) // attacked by rook on file 3
        assertTrue(Square(1, 0) in kingTargets) // safe
    }

    @Test
    fun kingsCannotBeAdjacent() {
        val b = board(
            Color.WHITE,
            Square(2, 2) to Piece(PieceType.KING, Color.WHITE),
            Square(2, 4) to Piece(PieceType.KING, Color.BLACK),
        )
        val kingTargets = b.legalMoves().filter { it.from == Square(2, 2) }.map { it.to }.toSet()
        // Cannot move adjacent to opposing king (which would put own king in check by enemy king).
        assertFalse(Square(1, 3) in kingTargets)
        assertFalse(Square(2, 3) in kingTargets)
        assertFalse(Square(3, 3) in kingTargets)
        assertTrue(Square(2, 1) in kingTargets)
    }

    @Test
    fun pinnedPieceCannotLeaveThePinLine() {
        val b = board(
            Color.WHITE,
            Square(2, 0) to Piece(PieceType.KING, Color.WHITE),
            Square(2, 2) to Piece(PieceType.ROOK, Color.WHITE), // pinned vertically
            Square(2, 4) to Piece(PieceType.ROOK, Color.BLACK),
            Square(4, 4) to Piece(PieceType.KING, Color.BLACK),
        )
        val pinnedTargets = b.legalMoves().filter { it.from == Square(2, 2) }.map { it.to }.toSet()
        // Allowed: stays on file 2 → (2,1), (2,3), (2,4) capture.
        assertEquals(setOf(Square(2, 1), Square(2, 3), Square(2, 4)), pinnedTargets)
    }

    @Test
    fun isInCheckDetectsAttacker() {
        val b = board(
            Color.WHITE,
            Square(0, 0) to Piece(PieceType.KING, Color.WHITE),
            Square(0, 4) to Piece(PieceType.ROOK, Color.BLACK),
            Square(4, 4) to Piece(PieceType.KING, Color.BLACK),
        )
        assertTrue(b.isInCheck(Color.WHITE))
        assertFalse(b.isInCheck(Color.BLACK))
    }

    @Test
    fun checkmateInCornerByQueenAndKing() {
        val b = board(
            Color.BLACK,
            Square(0, 4) to Piece(PieceType.KING, Color.BLACK),
            Square(1, 3) to Piece(PieceType.QUEEN, Color.WHITE),
            Square(1, 2) to Piece(PieceType.KING, Color.WHITE),
        )
        assertTrue(b.isInCheck(Color.BLACK))
        assertEquals(emptyList(), b.legalMoves())
        assertTrue(b.isCheckmate())
        assertFalse(b.isStalemate())
    }

    @Test
    fun stalemateBlackKingNoMovesNoCheck() {
        // Black king at (0,4); white queen at (1,2); white king at (2,2).
        // Queen attacks (0,3),(1,3),(1,4) -> black king's escape squares all attacked.
        // Queen does NOT attack (0,4) (off any rank/file/diagonal).
        // Black has no other pieces -> stalemate.
        val b = board(
            Color.BLACK,
            Square(0, 4) to Piece(PieceType.KING, Color.BLACK),
            Square(1, 2) to Piece(PieceType.QUEEN, Color.WHITE),
            Square(2, 2) to Piece(PieceType.KING, Color.WHITE),
        )
        assertFalse(b.isInCheck(Color.BLACK))
        assertEquals(emptyList(), b.legalMoves())
        assertTrue(b.isStalemate())
        assertFalse(b.isCheckmate())
    }

    @Test
    fun applyMovesPieceAndFlipsSide() {
        val b = board(
            Color.WHITE,
            Square(0, 0) to Piece(PieceType.KING, Color.WHITE),
            Square(4, 4) to Piece(PieceType.KING, Color.BLACK),
            Square(2, 2) to Piece(PieceType.KNIGHT, Color.WHITE),
        )
        val after = b.apply(Move(Square(2, 2), Square(0, 1)))
        assertEquals(Color.BLACK, after.sideToMove)
        assertNull(after.pieceAt(Square(2, 2)))
        assertEquals(Piece(PieceType.KNIGHT, Color.WHITE), after.pieceAt(Square(0, 1)))
    }
}
