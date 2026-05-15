package com.inspiredandroid.braincup.games.minichess

import kotlin.math.abs
import kotlin.math.max
import kotlin.random.Random

object ScenarioGenerator {
    private const val MAX_ATTEMPTS = 400
    private const val MIN_LEGAL_MOVES = 3

    /** Depth values that count as "Easy" for piece-count purposes (matches the
     *  InstructionsScreen difficulty selector). */
    private const val EASY_DEPTH = 1
    private const val MEDIUM_DEPTH = 3

    /** Negamax depth used by the quality gate. Deep enough to spot short forced mates
     *  on a 5×5 board without making generation feel sluggish. */
    private const val VALIDATION_DEPTH_EASY = 3
    private const val VALIDATION_DEPTH = 4

    fun generate(difficultyDepth: Int, random: Random = Random.Default): ChessBoard {
        repeat(MAX_ATTEMPTS) { attempt ->
            val board = tryGenerate(difficultyDepth, random, attempt)
            if (board != null) return board
        }
        return fallback()
    }

    private fun tryGenerate(difficultyDepth: Int, random: Random, attempt: Int): ChessBoard? {
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

        // Without a queen, rook, or promotable pawn, white can't force checkmate
        // against a lone king — these positions waste the player's time.
        if (!hasMatingPotential(placed, Color.WHITE)) return null

        // Quality gate: short negamax search must place the eval in a
        // difficulty-tuned window. Skip for Easy (current behavior is fine).
        if (difficultyDepth > EASY_DEPTH) {
            val window = windowFor(difficultyDepth, attempt)
            val validationDepth = if (difficultyDepth <= MEDIUM_DEPTH) VALIDATION_DEPTH_EASY else VALIDATION_DEPTH
            val score = ChessAi(depth = validationDepth).scorePosition(board, validationDepth)
            if (score < window.minEval) return null
            if (score > window.maxEval) return null
            // Mate-in-N detection: scores near MATE mean a forced mate within (MATE - score) plies.
            if (score >= ChessAi.MATE - window.minMatePlies) return null
        }

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

    private fun hasMatingPotential(placed: Map<Square, Piece>, color: Color): Boolean = placed.values.any { p ->
        p.color == color && (p.type == PieceType.QUEEN || p.type == PieceType.ROOK || p.type == PieceType.PAWN)
    }

    /** Eval window the quality gate accepts for the given difficulty. After half the
     *  attempts have been spent the window relaxes by 50cp per step, so an unlucky
     *  random seed doesn't fall through to [fallback]. */
    private fun windowFor(difficultyDepth: Int, attempt: Int): QualityWindow {
        val relaxSteps = max(0, attempt - MAX_ATTEMPTS / 2) / 50
        val slack = relaxSteps * 50
        return when {
            difficultyDepth <= MEDIUM_DEPTH -> QualityWindow(
                minEval = 150 - slack,
                maxEval = 900 + slack,
                minMatePlies = if (relaxSteps > 0) 2 else 4,
            )
            else -> QualityWindow(
                minEval = 120 - slack,
                maxEval = 500 + slack,
                minMatePlies = if (relaxSteps > 0) 4 else 6,
            )
        }
    }

    private data class QualityWindow(val minEval: Int, val maxEval: Int, val minMatePlies: Int)

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
