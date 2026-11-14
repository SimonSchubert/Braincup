package com.inspiredandroid.braincup.checkmate

import com.inspiredandroid.braincup.chess.Move
import com.inspiredandroid.braincup.chess.Piece
import com.inspiredandroid.braincup.chess.PieceColor
import com.inspiredandroid.braincup.chess.PieceType
import com.inspiredandroid.braincup.chess.Square
import com.inspiredandroid.braincup.normalchess.NormalChessAi
import com.inspiredandroid.braincup.normalchess.NormalChessBoard

enum class CheckmateOutcome { SOLVED, NOT_MATE, MATED, STALEMATE }

data class CheckmateAttempt(
    val board: NormalChessBoard,
    val solverPath: String,
    val solverMoves: Int,
    val lastMove: Move?,
    val awaitingReply: Boolean,
    val outcome: CheckmateOutcome?,
)

/**
 * A mate-in-two or mate-in-three from the Lichess puzzle database (CC0), played from the moment the
 * solver is to move. The defence is not searched: [replies] holds the best answer to every legal
 * solver move, worked out offline with python-chess, so a wrong first move is refuted the way a
 * strong player would refute it.
 *
 * Squares are one letter of [SQUARE_LETTERS], a8 first and h1 last. A move is its two squares,
 * plus `:q` (or `:r`, `:b`, `:n`) after a promotion. A reply key is the solver's moves so far run
 * together, then either the move just played, `*` for any other move from that position, or the
 * lone `F` for anything both leave out.
 */
class CheckmatePuzzle(
    val id: String,
    val number: Int,
    val cells: String,
    val solverColor: PieceColor,
    val mateIn: Int,
    val solution: List<String>,
    val enPassantIndex: Int,
    val rating: Int,
    private val replies: Map<String, String>,
) {
    fun start(): CheckmateAttempt = CheckmateAttempt(
        board = startingBoard(),
        solverPath = "",
        solverMoves = 0,
        lastMove = null,
        awaitingReply = false,
        outcome = null,
    )

    fun startingBoard(): NormalChessBoard {
        val pieces = buildMap {
            cells.forEachIndexed { index, c -> pieceFor(c)?.let { put(squareAt(index), it) } }
        }
        return NormalChessBoard.fromMap(
            pieces = pieces,
            sideToMove = solverColor,
            enPassantTarget = enPassantIndex.takeIf { it >= 0 }?.let(::squareAt),
        )
    }

    /**
     * The move a tap from [from] to [to] plays, or null when it is not legal. Promotions take no
     * dialog: on the last move the piece that mates, else the one the solution promotes to, else a
     * queen.
     */
    fun moveFor(attempt: CheckmateAttempt, from: Square, to: Square): Move? {
        val candidates = attempt.board.legalMoves().filter { it.from == from && it.to == to }
        if (candidates.size <= 1) return candidates.firstOrNull()
        if (attempt.solverMoves == mateIn - 1) {
            candidates.firstOrNull { attempt.board.apply(it).isCheckmate() }?.let { return it }
        }
        val line = solution.getOrNull(attempt.solverMoves * 2)
        return candidates.firstOrNull { encode(it) == line }
            ?: candidates.first { it.promotion == PieceType.QUEEN }
    }

    fun play(attempt: CheckmateAttempt, move: Move): CheckmateAttempt {
        val board = attempt.board.apply(move)
        val outcome = when {
            board.isCheckmate() -> CheckmateOutcome.SOLVED
            board.isStalemate() -> CheckmateOutcome.STALEMATE
            else -> null
        }
        return attempt.copy(
            board = board,
            solverPath = attempt.solverPath + encode(move),
            solverMoves = attempt.solverMoves + 1,
            lastMove = move,
            awaitingReply = outcome == null,
            outcome = outcome,
        )
    }

    fun answer(attempt: CheckmateAttempt): CheckmateAttempt {
        val board = attempt.board
        val move = defence(attempt) ?: return attempt.copy(awaitingReply = false)
        val after = board.apply(move)
        val outcome = when {
            after.isCheckmate() -> CheckmateOutcome.MATED
            after.isStalemate() -> CheckmateOutcome.STALEMATE
            attempt.solverMoves >= mateIn -> CheckmateOutcome.NOT_MATE
            else -> null
        }
        return attempt.copy(board = after, lastMove = move, awaitingReply = false, outcome = outcome)
    }

    internal fun defence(attempt: CheckmateAttempt): Move? {
        val legal = attempt.board.legalMoves()
        if (legal.isEmpty()) return null
        return tabledReply(attempt.solverPath)?.let { decode(it) }?.takeIf { it in legal }
            ?: NormalChessAi(depth = 2, useQuiescence = false).bestMove(attempt.board)
    }

    internal fun tabledReply(solverPath: String): String? {
        val lastMoveLength = if (solverPath.length >= 4 && solverPath[solverPath.length - 2] == ':') 4 else 2
        val before = solverPath.dropLast(lastMoveLength)
        val raw = replies[solverPath] ?: replies["$before*"] ?: replies["F"] ?: return null
        return raw.trimEnd('!', '~').takeUnless { it == "#" || it == "=" }
    }

    companion object {
        const val SQUARE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_"

        fun squareAt(index: Int): Square = Square(file = index % 8, row = 7 - index / 8)

        fun indexOf(square: Square): Int = (7 - square.row) * 8 + square.file

        fun encode(move: Move): String {
            val squares = "${SQUARE_LETTERS[indexOf(move.from)]}${SQUARE_LETTERS[indexOf(move.to)]}"
            val promotion = when (move.promotion) {
                PieceType.QUEEN -> ":q"
                PieceType.ROOK -> ":r"
                PieceType.BISHOP -> ":b"
                PieceType.KNIGHT -> ":n"
                else -> ""
            }
            return squares + promotion
        }

        fun decode(code: String): Move? {
            if (code.length < 2) return null
            val from = SQUARE_LETTERS.indexOf(code[0]).takeIf { it >= 0 } ?: return null
            val to = SQUARE_LETTERS.indexOf(code[1]).takeIf { it >= 0 } ?: return null
            val promotion = when (code.getOrNull(3)) {
                'q' -> PieceType.QUEEN
                'r' -> PieceType.ROOK
                'b' -> PieceType.BISHOP
                'n' -> PieceType.KNIGHT
                else -> null
            }
            return Move(squareAt(from), squareAt(to), promotion)
        }

        fun pieceFor(c: Char): Piece? {
            val type = when (c.lowercaseChar()) {
                'k' -> PieceType.KING
                'q' -> PieceType.QUEEN
                'r' -> PieceType.ROOK
                'b' -> PieceType.BISHOP
                'n' -> PieceType.KNIGHT
                'p' -> PieceType.PAWN
                else -> return null
            }
            return Piece(type, if (c.isUpperCase()) PieceColor.WHITE else PieceColor.BLACK)
        }

        /** One line per puzzle: `id|fen placement|w or b|mate in|solution|en passant|rating|replies`. */
        fun parse(text: String): List<CheckmatePuzzle> = text.lineSequence()
            .filter { it.isNotBlank() }
            .mapIndexed { index, line ->
                val f = line.split('|')
                CheckmatePuzzle(
                    id = f[0],
                    number = index + 1,
                    cells = expandFen(f[1]),
                    solverColor = if (f[2] == "w") PieceColor.WHITE else PieceColor.BLACK,
                    mateIn = f[3].toInt(),
                    solution = f[4].split(' '),
                    enPassantIndex = f[5].toInt(),
                    rating = f[6].toInt(),
                    replies = f[7].split(' ')
                        .filter { '>' in it }
                        .associate { it.substringBefore('>') to it.substringAfter('>') },
                )
            }
            .toList()

        private fun expandFen(placement: String): String = buildString {
            for (c in placement) {
                when {
                    c == '/' -> Unit
                    c.isDigit() -> repeat(c.digitToInt()) { append('.') }
                    else -> append(c)
                }
            }
        }
    }
}
