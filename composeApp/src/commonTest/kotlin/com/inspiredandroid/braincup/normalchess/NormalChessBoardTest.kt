package com.inspiredandroid.braincup.normalchess

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NormalChessBoardTest {
    @Test
    fun startingPositionHas32PiecesAndWhiteToMove() {
        val b = NormalChessBoard.startingPosition()
        assertEquals(Color.WHITE, b.sideToMove)
        assertEquals(32, b.snapshot().count { it != null })
        // White has 20 opening moves (16 pawn moves + 4 knight moves)
        assertEquals(20, b.legalMoves().size)
    }

    @Test
    fun startingPositionPlacesQueensOnDFileAndKingsOnEFile() {
        // Standard chess: queen on d, king on e. Do not "swap" them to fix square colors;
        // square coloring is a UI concern (a1 must be dark so queens sit on their own color).
        val b = NormalChessBoard.startingPosition()
        assertEquals(Piece(PieceType.QUEEN, Color.WHITE), b.pieceAt(Square(3, 0)))
        assertEquals(Piece(PieceType.KING, Color.WHITE), b.pieceAt(Square(4, 0)))
        assertEquals(Piece(PieceType.QUEEN, Color.BLACK), b.pieceAt(Square(3, 7)))
        assertEquals(Piece(PieceType.KING, Color.BLACK), b.pieceAt(Square(4, 7)))
    }

    @Test
    fun pawnDoubleAdvanceFromStartRankOnly() {
        val b = NormalChessBoard.startingPosition()
        val pawnMoves = b.legalMoves().filter { it.from == Square(4, 1) }
        assertEquals(setOf(Square(4, 2), Square(4, 3)), pawnMoves.map { it.to }.toSet())
        val after = b.apply(Move(Square(4, 1), Square(4, 3)))
        val afterBlack = after.apply(after.legalMoves().first { it.from == Square(0, 6) && it.to == Square(0, 5) })
        val secondPawnMoves = afterBlack.legalMoves().filter { it.from == Square(4, 3) }
        assertEquals(setOf(Square(4, 4)), secondPawnMoves.map { it.to }.toSet())
    }

    @Test
    fun enPassantCaptureWorks() {
        val b = NormalChessBoard.fromMap(
            pieces = mapOf(
                Square(4, 0) to Piece(PieceType.KING, Color.WHITE),
                Square(4, 7) to Piece(PieceType.KING, Color.BLACK),
                Square(4, 1) to Piece(PieceType.PAWN, Color.WHITE),
                Square(5, 3) to Piece(PieceType.PAWN, Color.BLACK),
            ),
            sideToMove = Color.WHITE,
        )
        val after = b.apply(Move(Square(4, 1), Square(4, 3)))
        assertEquals(Square(4, 2), after.enPassantTarget)
        val epMove = after.legalMoves().firstOrNull { it.from == Square(5, 3) && it.to == Square(4, 2) }
        assertNotNull(epMove, "en passant capture should be legal")
        val final = after.apply(epMove)
        assertNull(final.pieceAt(Square(4, 3)))
        assertEquals(Piece(PieceType.PAWN, Color.BLACK), final.pieceAt(Square(4, 2)))
        assertNull(final.enPassantTarget)
    }

    @Test
    fun pawnPromotionOffersAllFourTypes() {
        val b = NormalChessBoard.fromMap(
            pieces = mapOf(
                Square(0, 0) to Piece(PieceType.KING, Color.WHITE),
                Square(0, 7) to Piece(PieceType.KING, Color.BLACK),
                Square(4, 6) to Piece(PieceType.PAWN, Color.WHITE),
            ),
            sideToMove = Color.WHITE,
        )
        val promotions = b.legalMoves().filter { it.from == Square(4, 6) && it.to == Square(4, 7) }
        assertEquals(
            setOf(PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT),
            promotions.mapNotNull { it.promotion }.toSet(),
        )
        val asKnight = promotions.first { it.promotion == PieceType.KNIGHT }
        val after = b.apply(asKnight)
        assertEquals(Piece(PieceType.KNIGHT, Color.WHITE), after.pieceAt(Square(4, 7)))
    }

    @Test
    fun kingsideCastleWorksAndMovesRook() {
        val b = NormalChessBoard.fromMap(
            pieces = mapOf(
                Square(4, 0) to Piece(PieceType.KING, Color.WHITE),
                Square(7, 0) to Piece(PieceType.ROOK, Color.WHITE),
                Square(4, 7) to Piece(PieceType.KING, Color.BLACK),
            ),
            sideToMove = Color.WHITE,
            castling = CastlingRights(whiteKingside = true, whiteQueenside = false, blackKingside = false, blackQueenside = false),
        )
        val castle = b.legalMoves().firstOrNull { it.from == Square(4, 0) && it.to == Square(6, 0) }
        assertNotNull(castle, "kingside castle should be legal")
        val after = b.apply(castle)
        assertEquals(Piece(PieceType.KING, Color.WHITE), after.pieceAt(Square(6, 0)))
        assertEquals(Piece(PieceType.ROOK, Color.WHITE), after.pieceAt(Square(5, 0)))
        assertNull(after.pieceAt(Square(7, 0)))
        assertFalse(after.castling.whiteKingside)
        assertFalse(after.castling.whiteQueenside)
    }

    @Test
    fun queensideCastleWorksAndMovesRook() {
        val b = NormalChessBoard.fromMap(
            pieces = mapOf(
                Square(4, 0) to Piece(PieceType.KING, Color.WHITE),
                Square(0, 0) to Piece(PieceType.ROOK, Color.WHITE),
                Square(4, 7) to Piece(PieceType.KING, Color.BLACK),
            ),
            sideToMove = Color.WHITE,
            castling = CastlingRights(whiteKingside = false, whiteQueenside = true, blackKingside = false, blackQueenside = false),
        )
        val castle = b.legalMoves().firstOrNull { it.from == Square(4, 0) && it.to == Square(2, 0) }
        assertNotNull(castle)
        val after = b.apply(castle)
        assertEquals(Piece(PieceType.KING, Color.WHITE), after.pieceAt(Square(2, 0)))
        assertEquals(Piece(PieceType.ROOK, Color.WHITE), after.pieceAt(Square(3, 0)))
        assertNull(after.pieceAt(Square(0, 0)))
    }

    @Test
    fun cannotCastleThroughCheck() {
        val b = NormalChessBoard.fromMap(
            pieces = mapOf(
                Square(4, 0) to Piece(PieceType.KING, Color.WHITE),
                Square(7, 0) to Piece(PieceType.ROOK, Color.WHITE),
                Square(4, 7) to Piece(PieceType.KING, Color.BLACK),
                Square(5, 7) to Piece(PieceType.ROOK, Color.BLACK),
            ),
            sideToMove = Color.WHITE,
            castling = CastlingRights(whiteKingside = true, whiteQueenside = false, blackKingside = false, blackQueenside = false),
        )
        val castle = b.legalMoves().firstOrNull { it.from == Square(4, 0) && it.to == Square(6, 0) }
        assertNull(castle, "should not be allowed to castle through attacked f1")
    }

    @Test
    fun cannotCastleWhenInCheck() {
        val b = NormalChessBoard.fromMap(
            pieces = mapOf(
                Square(4, 0) to Piece(PieceType.KING, Color.WHITE),
                Square(7, 0) to Piece(PieceType.ROOK, Color.WHITE),
                Square(4, 7) to Piece(PieceType.KING, Color.BLACK),
                Square(4, 5) to Piece(PieceType.ROOK, Color.BLACK),
            ),
            sideToMove = Color.WHITE,
            castling = CastlingRights(whiteKingside = true, whiteQueenside = false, blackKingside = false, blackQueenside = false),
        )
        val castle = b.legalMoves().firstOrNull { it.from == Square(4, 0) && it.to == Square(6, 0) }
        assertNull(castle)
    }

    @Test
    fun cannotCastleAfterKingMoves() {
        val b = NormalChessBoard.fromMap(
            pieces = mapOf(
                Square(4, 0) to Piece(PieceType.KING, Color.WHITE),
                Square(7, 0) to Piece(PieceType.ROOK, Color.WHITE),
                Square(4, 7) to Piece(PieceType.KING, Color.BLACK),
            ),
            sideToMove = Color.WHITE,
            castling = CastlingRights(whiteKingside = false, whiteQueenside = false, blackKingside = false, blackQueenside = false),
        )
        val castle = b.legalMoves().firstOrNull { it.from == Square(4, 0) && it.to == Square(6, 0) }
        assertNull(castle)
    }

    @Test
    fun rookMoveRevokesMatchingCastlingRight() {
        val b = NormalChessBoard.fromMap(
            pieces = mapOf(
                Square(4, 0) to Piece(PieceType.KING, Color.WHITE),
                Square(0, 0) to Piece(PieceType.ROOK, Color.WHITE),
                Square(7, 0) to Piece(PieceType.ROOK, Color.WHITE),
                Square(4, 7) to Piece(PieceType.KING, Color.BLACK),
            ),
            sideToMove = Color.WHITE,
            castling = CastlingRights(whiteKingside = true, whiteQueenside = true, blackKingside = false, blackQueenside = false),
        )
        val after = b.apply(Move(Square(0, 0), Square(0, 4)))
        assertFalse(after.castling.whiteQueenside)
        assertTrue(after.castling.whiteKingside)
    }

    @Test
    fun backRankMateIsCheckmate() {
        val b = NormalChessBoard.fromMap(
            pieces = mapOf(
                Square(6, 7) to Piece(PieceType.KING, Color.BLACK),
                Square(5, 6) to Piece(PieceType.PAWN, Color.BLACK),
                Square(6, 6) to Piece(PieceType.PAWN, Color.BLACK),
                Square(7, 6) to Piece(PieceType.PAWN, Color.BLACK),
                Square(0, 7) to Piece(PieceType.ROOK, Color.WHITE),
                Square(0, 0) to Piece(PieceType.KING, Color.WHITE),
            ),
            sideToMove = Color.BLACK,
        )
        assertTrue(b.isCheckmate())
        assertEquals(GameResult.WHITE_WINS, b.result())
    }

    @Test
    fun stalemateDetected() {
        val b = NormalChessBoard.fromMap(
            pieces = mapOf(
                Square(7, 7) to Piece(PieceType.KING, Color.BLACK),
                Square(6, 5) to Piece(PieceType.QUEEN, Color.WHITE),
                Square(5, 5) to Piece(PieceType.KING, Color.WHITE),
            ),
            sideToMove = Color.BLACK,
        )
        assertTrue(b.isStalemate())
        assertEquals(GameResult.DRAW_STALEMATE, b.result())
    }

    @Test
    fun fiftyMoveRuleDrawAtHundredHalfmoves() {
        val b = NormalChessBoard.fromMap(
            pieces = mapOf(
                Square(4, 0) to Piece(PieceType.KING, Color.WHITE),
                Square(4, 7) to Piece(PieceType.KING, Color.BLACK),
                Square(0, 0) to Piece(PieceType.ROOK, Color.WHITE),
            ),
            sideToMove = Color.WHITE,
            halfmoveClock = 100,
        )
        assertEquals(GameResult.DRAW_FIFTY_MOVE, b.result())
    }

    @Test
    fun pawnMoveResetsHalfmoveClock() {
        val b = NormalChessBoard.fromMap(
            pieces = mapOf(
                Square(4, 0) to Piece(PieceType.KING, Color.WHITE),
                Square(4, 7) to Piece(PieceType.KING, Color.BLACK),
                Square(0, 1) to Piece(PieceType.PAWN, Color.WHITE),
            ),
            sideToMove = Color.WHITE,
            halfmoveClock = 40,
        )
        val after = b.apply(Move(Square(0, 1), Square(0, 2)))
        assertEquals(0, after.halfmoveClock)
    }

    @Test
    fun captureResetsHalfmoveClock() {
        val b = NormalChessBoard.fromMap(
            pieces = mapOf(
                Square(4, 0) to Piece(PieceType.KING, Color.WHITE),
                Square(4, 7) to Piece(PieceType.KING, Color.BLACK),
                Square(0, 0) to Piece(PieceType.ROOK, Color.WHITE),
                Square(0, 4) to Piece(PieceType.ROOK, Color.BLACK),
            ),
            sideToMove = Color.WHITE,
            halfmoveClock = 30,
        )
        val after = b.apply(Move(Square(0, 0), Square(0, 4)))
        assertEquals(0, after.halfmoveClock)
    }

    @Test
    fun insufficientMaterialKvK() {
        val b = NormalChessBoard.fromMap(
            pieces = mapOf(
                Square(4, 0) to Piece(PieceType.KING, Color.WHITE),
                Square(4, 7) to Piece(PieceType.KING, Color.BLACK),
            ),
            sideToMove = Color.WHITE,
        )
        assertTrue(b.isInsufficientMaterial())
        assertEquals(GameResult.DRAW_INSUFFICIENT_MATERIAL, b.result())
    }

    @Test
    fun insufficientMaterialKvKB() {
        val b = NormalChessBoard.fromMap(
            pieces = mapOf(
                Square(4, 0) to Piece(PieceType.KING, Color.WHITE),
                Square(4, 7) to Piece(PieceType.KING, Color.BLACK),
                Square(2, 5) to Piece(PieceType.BISHOP, Color.BLACK),
            ),
            sideToMove = Color.WHITE,
        )
        assertTrue(b.isInsufficientMaterial())
    }

    @Test
    fun queenIsSufficientMaterial() {
        val b = NormalChessBoard.fromMap(
            pieces = mapOf(
                Square(4, 0) to Piece(PieceType.KING, Color.WHITE),
                Square(4, 7) to Piece(PieceType.KING, Color.BLACK),
                Square(0, 0) to Piece(PieceType.QUEEN, Color.WHITE),
            ),
            sideToMove = Color.WHITE,
        )
        assertFalse(b.isInsufficientMaterial())
    }

    @Test
    fun bishopCapturingCornerRookKillsCastlingRight() {
        val b = NormalChessBoard.fromMap(
            pieces = mapOf(
                Square(4, 0) to Piece(PieceType.KING, Color.WHITE),
                Square(4, 7) to Piece(PieceType.KING, Color.BLACK),
                Square(7, 0) to Piece(PieceType.ROOK, Color.WHITE),
                Square(6, 1) to Piece(PieceType.BISHOP, Color.BLACK),
            ),
            sideToMove = Color.BLACK,
            castling = CastlingRights(whiteKingside = true, whiteQueenside = true, blackKingside = false, blackQueenside = false),
        )
        val capture = b.legalMoves().first { it.from == Square(6, 1) && it.to == Square(7, 0) }
        val after = b.apply(capture)
        assertFalse(after.castling.whiteKingside)
        assertTrue(after.castling.whiteQueenside)
    }

    @Test
    fun twofoldRepetitionIsNotYetADraw() {
        // Knight shuffle once back to the start: second occurrence, still ongoing.
        var b = NormalChessBoard.startingPosition()
        b = b.apply(Move(Square(6, 0), Square(5, 2))) // Nf3
        b = b.apply(Move(Square(6, 7), Square(5, 5))) // Nf6
        b = b.apply(Move(Square(5, 2), Square(6, 0))) // Ng1
        b = b.apply(Move(Square(5, 5), Square(6, 7))) // Ng8
        assertEquals(Color.WHITE, b.sideToMove)
        assertFalse(b.isThreefoldRepetition())
        assertEquals(GameResult.ONGOING, b.result())
    }

    @Test
    fun threefoldRepetitionIsAnAutomaticDraw() {
        // Knight shuffle twice back to the start: third occurrence of the initial position.
        var b = NormalChessBoard.startingPosition()
        repeat(2) {
            b = b.apply(Move(Square(6, 0), Square(5, 2))) // Nf3
            b = b.apply(Move(Square(6, 7), Square(5, 5))) // Nf6
            b = b.apply(Move(Square(5, 2), Square(6, 0))) // Ng1
            b = b.apply(Move(Square(5, 5), Square(6, 7))) // Ng8
        }
        assertEquals(Color.WHITE, b.sideToMove)
        assertTrue(b.isThreefoldRepetition())
        assertEquals(GameResult.DRAW_REPETITION, b.result())
    }
}
