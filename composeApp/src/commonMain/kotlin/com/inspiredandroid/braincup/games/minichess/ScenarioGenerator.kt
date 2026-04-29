package com.inspiredandroid.braincup.games.minichess

import kotlin.math.abs
import kotlin.math.max
import kotlin.random.Random

object ScenarioGenerator {
    private const val MAX_ATTEMPTS = 200
    private const val MIN_LEGAL_MOVES = 3

    /** Depth values that count as "Easy" for piece-count purposes (matches the
     *  InstructionsScreen difficulty selector). */
    private const val EASY_DEPTH = 1

    fun generate(difficultyDepth: Int, random: Random = Random.Default): ChessBoard {
        repeat(MAX_ATTEMPTS) {
            val attempt = tryGenerate(difficultyDepth, random)
            if (attempt != null) return attempt
        }
        return fallback()
    }

    private fun tryGenerate(difficultyDepth: Int, random: Random): ChessBoard? {
        val placed = HashMap<Square, Piece>()

        val whiteKing = ALL_SQUARES.random(random)
        placed[whiteKing] = Piece(PieceType.KING, Color.WHITE)

        val blackKingCandidates = ALL_SQUARES.filter { it != whiteKing && chebyshev(it, whiteKing) >= 2 }
        val blackKing = blackKingCandidates.randomOrNull(random) ?: return null
        placed[blackKing] = Piece(PieceType.KING, Color.BLACK)

        val whiteCount = whitePieceCount(random)
        repeat(whiteCount) {
            val piece = placeRandom(placed, Color.WHITE, random) ?: return null
            placed[piece.first] = piece.second
        }

        val blackCount = blackPieceCount(difficultyDepth, random)
        repeat(blackCount) {
            val piece = placeRandom(placed, Color.BLACK, random) ?: return null
            placed[piece.first] = piece.second
        }

        val board = ChessBoard.fromMap(placed, Color.WHITE)

        // Reject illegal/impossible/uninteresting positions.
        if (board.isInCheck(Color.WHITE)) return null
        if (board.isInCheck(Color.BLACK)) return null
        if (board.legalMoves().size < MIN_LEGAL_MOVES) return null

        return board
    }

    private fun placeRandom(
        occupied: Map<Square, Piece>,
        color: Color,
        random: Random,
    ): Pair<Square, Piece>? {
        val type = PIECE_BAG.random(random)
        val candidates = ALL_SQUARES.filter { sq ->
            sq !in occupied && validForPawn(sq, type)
        }
        val sq = candidates.randomOrNull(random) ?: return null
        return sq to Piece(type, color)
    }

    private fun validForPawn(sq: Square, type: PieceType): Boolean = type != PieceType.PAWN || sq.row in 1 until BOARD_SIZE - 1

    /** Player always gets a meaty army: K + 3..4 non-king pieces. */
    private fun whitePieceCount(random: Random): Int = (3..4).random(random)

    /** CPU army size depends on difficulty so Easy gives the player a clear material edge. */
    private fun blackPieceCount(difficultyDepth: Int, random: Random): Int = when {
        difficultyDepth <= EASY_DEPTH -> (2..3).random(random) // Easy: fewer defenders
        else -> (3..4).random(random)
    }

    private fun chebyshev(a: Square, b: Square): Int = max(abs(a.file - b.file), abs(a.row - b.row))

    private fun fallback(): ChessBoard = ChessBoard.fromMap(
        mapOf(
            Square(0, 0) to Piece(PieceType.KING, Color.WHITE),
            Square(2, 1) to Piece(PieceType.ROOK, Color.WHITE),
            Square(4, 4) to Piece(PieceType.KING, Color.BLACK),
        ),
        Color.WHITE,
    )

    private val ALL_SQUARES: List<Square> = buildList {
        for (r in 0 until BOARD_SIZE) for (c in 0 until BOARD_SIZE) add(Square(c, r))
    }

    // Weighted bag: pawns and minor pieces common, queen rare.
    private val PIECE_BAG: List<PieceType> = listOf(
        PieceType.PAWN, PieceType.PAWN, PieceType.PAWN,
        PieceType.KNIGHT, PieceType.KNIGHT,
        PieceType.BISHOP, PieceType.BISHOP,
        PieceType.ROOK, PieceType.ROOK,
        PieceType.QUEEN,
    )
}
