package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.GameUiState
import com.inspiredandroid.braincup.app.MiniChessCell
import com.inspiredandroid.braincup.app.MiniChessOutcome
import com.inspiredandroid.braincup.app.MiniChessUiState
import com.inspiredandroid.braincup.games.minichess.BOARD_SIZE
import com.inspiredandroid.braincup.games.minichess.ChessBoard
import com.inspiredandroid.braincup.games.minichess.Color
import com.inspiredandroid.braincup.games.minichess.Move
import com.inspiredandroid.braincup.games.minichess.ScenarioGenerator
import com.inspiredandroid.braincup.games.minichess.Square

class MiniChessGame(
    /** AI search depth. Driven by the user's selection on the instructions screen.
     *  1 = easy (no opponent-response prediction), 2 = medium, 3 = hard. */
    private val difficultyDepth: Int = DEFAULT_DEPTH,
) : Game() {
    override val adaptiveDifficulty: Boolean = false

    enum class Phase { PLAYER_TURN, AI_THINKING, ROUND_OVER }

    var board: ChessBoard = ScenarioGenerator.generate(difficultyDepth = difficultyDepth)
        private set
    var phase: Phase = Phase.PLAYER_TURN
        private set
    var outcome: MiniChessOutcome? = null
        private set
    var lastMoveFrom: Square? = null
        private set
    var lastMoveTo: Square? = null
        private set

    private var halfMoveCount: Int = 0

    /** Snapshot of the board taken when this scenario was generated. Used by [resetScenario]
     *  so the player can retry the same starting position without rolling a fresh one. */
    private var initialBoard: ChessBoard = board

    override fun generateRound() {
        board = ScenarioGenerator.generate(difficultyDepth = difficultyDepth)
        initialBoard = board
        phase = Phase.PLAYER_TURN
        outcome = null
        lastMoveFrom = null
        lastMoveTo = null
        halfMoveCount = 0
    }

    fun aiDepth(): Int = difficultyDepth

    /** Abandon the current scenario and generate a fresh one without changing [round].
     *  Used by the in-game New Game button. */
    fun restartScenario() {
        board = ScenarioGenerator.generate(difficultyDepth = difficultyDepth)
        initialBoard = board
        phase = Phase.PLAYER_TURN
        outcome = null
        lastMoveFrom = null
        lastMoveTo = null
        halfMoveCount = 0
    }

    /** Restore the current scenario to its initial position (same board, move 0).
     *  Used by the in-game Reset button. */
    fun resetScenario() {
        board = initialBoard
        phase = Phase.PLAYER_TURN
        outcome = null
        lastMoveFrom = null
        lastMoveTo = null
        halfMoveCount = 0
    }

    /** Find a legal move matching the from/to squares (promotion handled by the engine). */
    fun findLegalMove(from: Square, to: Square): Move? = board.legalMoves().firstOrNull { it.from == from && it.to == to }

    /** Apply a player's move. Returns the new phase the game is in. Caller must verify the move
     *  was obtained via [findLegalMove] (defense in depth). */
    fun applyPlayerMove(move: Move): PlayerMoveResult {
        require(move in board.legalMoves()) { "applyPlayerMove called with illegal move $move" }
        board = board.apply(move)
        lastMoveFrom = move.from
        lastMoveTo = move.to
        halfMoveCount++

        // After player's move, evaluate position from AI (black) perspective.
        if (board.legalMoves().isEmpty()) {
            outcome = if (board.isInCheck(Color.BLACK)) {
                MiniChessOutcome.PLAYER_WIN
            } else {
                MiniChessOutcome.DRAW
            }
            phase = Phase.ROUND_OVER
            return PlayerMoveResult.RoundOver
        }
        if (halfMoveCount >= MAX_HALF_MOVES) {
            outcome = MiniChessOutcome.DRAW
            phase = Phase.ROUND_OVER
            return PlayerMoveResult.RoundOver
        }
        phase = Phase.AI_THINKING
        return PlayerMoveResult.AiToMove
    }

    /** Apply the AI's chosen move. */
    fun applyAiMove(move: Move) {
        require(move in board.legalMoves()) { "applyAiMove called with illegal move $move" }
        board = board.apply(move)
        lastMoveFrom = move.from
        lastMoveTo = move.to
        halfMoveCount++

        // After AI's move, evaluate position from player (white) perspective.
        if (board.legalMoves().isEmpty()) {
            outcome = if (board.isInCheck(Color.WHITE)) {
                MiniChessOutcome.PLAYER_LOSS
            } else {
                MiniChessOutcome.DRAW
            }
            phase = Phase.ROUND_OVER
            return
        }
        if (halfMoveCount >= MAX_HALF_MOVES) {
            outcome = MiniChessOutcome.DRAW
            phase = Phase.ROUND_OVER
            return
        }
        phase = Phase.PLAYER_TURN
    }

    fun markGiveUp() {
        outcome = MiniChessOutcome.PLAYER_LOSS
        phase = Phase.ROUND_OVER
    }

    override fun isCorrect(input: String): Boolean = parseMove(input) != null

    override fun solution(): String = "" // not user-facing for chess

    override fun hint(): String? = null

    override fun toUiState(): GameUiState {
        val snapshot = board.snapshot()
        val cells = snapshot.map { piece ->
            MiniChessCell(pieceType = piece?.type, isWhite = piece?.color == Color.WHITE)
        }
        val legalByFrom: Map<Int, Set<Int>>
        val stalematingByFrom: Map<Int, Set<Int>>
        if (phase == Phase.PLAYER_TURN) {
            val legalMoves = board.legalMoves()
            legalByFrom = legalMoves
                .groupBy({ it.from.toIndex() }, { it.to.toIndex() })
                .mapValues { it.value.toSet() }
            stalematingByFrom = legalMoves
                .filter { move ->
                    val after = board.apply(move)
                    after.legalMoves().isEmpty() && !after.isInCheck(Color.BLACK)
                }
                .groupBy({ it.from.toIndex() }, { it.to.toIndex() })
                .mapValues { it.value.toSet() }
        } else {
            legalByFrom = emptyMap()
            stalematingByFrom = emptyMap()
        }
        return MiniChessUiState(
            cells = cells,
            legalMovesByFrom = legalByFrom,
            stalematingMovesByFrom = stalematingByFrom,
            lastMoveFromIndex = lastMoveFrom?.toIndex(),
            lastMoveToIndex = lastMoveTo?.toIndex(),
            whiteInCheck = board.isInCheck(Color.WHITE),
            blackInCheck = board.isInCheck(Color.BLACK),
            isAiThinking = phase == Phase.AI_THINKING,
            outcome = outcome,
            halfMoveCount = halfMoveCount,
            halfMoveCap = MAX_HALF_MOVES,
            pointsForWin = winPoints(),
        )
    }

    fun parseMove(input: String): Move? {
        val parts = input.split(">")
        if (parts.size != 2) return null
        val from = parts[0].toIntOrNull() ?: return null
        val to = parts[1].toIntOrNull() ?: return null
        if (from !in 0 until BOARD_SIZE * BOARD_SIZE) return null
        if (to !in 0 until BOARD_SIZE * BOARD_SIZE) return null
        val fromSq = Square(from % BOARD_SIZE, from / BOARD_SIZE)
        val toSq = Square(to % BOARD_SIZE, to / BOARD_SIZE)
        return findLegalMove(fromSq, toSq)
    }

    sealed class PlayerMoveResult {
        data object AiToMove : PlayerMoveResult()
        data object RoundOver : PlayerMoveResult()
    }

    /** Score awarded for a checkmate win at the current difficulty. Drives both the
     *  highscore (bronze/silver/gold tier) and XP gained via UserStorage. */
    fun winPoints(): Int = winPointsForDepth(difficultyDepth)

    companion object {
        /** Cap each scenario at 50 ply (25 full moves) to avoid endless games. */
        const val MAX_HALF_MOVES = 50

        /** Default AI depth when none has been chosen. Matches Difficulty.Medium. */
        const val DEFAULT_DEPTH = 3

        /** Maps AI depth (the persisted difficulty key) to win points. The thresholds
         *  here align with [com.inspiredandroid.braincup.games.GameType.MINI_CHESS]'s
         *  silver/gold scores so winning at Easy = bronze, Medium = silver, Hard = gold. */
        fun winPointsForDepth(depth: Int): Int = when (depth) {
            1 -> 5 // Easy → bronze
            3 -> 10 // Medium → silver
            5 -> 15 // Hard → gold
            else -> 10
        }

        fun encodeMove(from: Square, to: Square): String = "${from.toIndex()}>${to.toIndex()}"

        fun Square.toIndex(): Int = row * BOARD_SIZE + file
    }
}
