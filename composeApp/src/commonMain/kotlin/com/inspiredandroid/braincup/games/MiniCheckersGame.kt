package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.CpuRoundOutcome
import com.inspiredandroid.braincup.app.GameUiState
import com.inspiredandroid.braincup.app.MiniCheckersUiState
import com.inspiredandroid.braincup.checkers.CheckersBoard
import com.inspiredandroid.braincup.checkers.CheckersMove
import com.inspiredandroid.braincup.checkers.CheckersResult
import com.inspiredandroid.braincup.games.minicheckers.MiniCheckersDifficulty
import com.inspiredandroid.braincup.games.minicheckers.MiniCheckersScenarioGenerator
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlin.random.Random

class MiniCheckersGame(
    val difficulty: MiniCheckersDifficulty = MiniCheckersDifficulty.NORMAL,
    private val random: Random = Random.Default,
) : Game() {
    override val adaptiveDifficulty: Boolean = false

    enum class Phase { PLAYER_TURN, AI_THINKING, ROUND_OVER }

    // Replaced by the first nextRound(); generating here too would pay for a search twice.
    var board: CheckersBoard = MiniCheckersScenarioGenerator.FALLBACK
        private set
    var phase: Phase = Phase.PLAYER_TURN
        private set
    var outcome: CpuRoundOutcome? = null
        private set
    var lastMove: CheckersMove? = null
        private set

    private var initialBoard: CheckersBoard = board

    override fun generateRound() {
        restartScenario()
    }

    /** A fresh scenario, for the in-game New button. */
    fun restartScenario() {
        board = MiniCheckersScenarioGenerator.generate(difficulty, random)
        initialBoard = board
        resetScenario()
    }

    /** The same scenario from its first move, for the in-game Reset button. */
    fun resetScenario() {
        board = initialBoard
        phase = Phase.PLAYER_TURN
        outcome = null
        lastMove = null
    }

    fun applyPlayerMove(move: CheckersMove) {
        require(move in board.legalMoves()) { "applyPlayerMove called with illegal move $move" }
        applyMove(move)
        if (phase != Phase.ROUND_OVER) phase = Phase.AI_THINKING
    }

    fun applyAiMove(move: CheckersMove) {
        require(move in board.legalMoves()) { "applyAiMove called with illegal move $move" }
        applyMove(move)
        if (phase != Phase.ROUND_OVER) phase = Phase.PLAYER_TURN
    }

    private fun applyMove(move: CheckersMove) {
        board = board.apply(move)
        lastMove = move
        outcome = when (board.result()) {
            CheckersResult.BLACK_WINS -> CpuRoundOutcome.PLAYER_WIN
            CheckersResult.WHITE_WINS -> CpuRoundOutcome.PLAYER_LOSS
            CheckersResult.DRAW -> CpuRoundOutcome.DRAW
            CheckersResult.ONGOING -> null
        }
        if (outcome != null) phase = Phase.ROUND_OVER
    }

    fun markGiveUp() {
        outcome = CpuRoundOutcome.PLAYER_LOSS
        phase = Phase.ROUND_OVER
    }

    /** A move arrives as its full path of square indices, `from>landing>landing`. */
    fun parseMove(input: String): CheckersMove? {
        val path = input.split(">").map { it.toIntOrNull() ?: return null }
        return board.legalMoves().firstOrNull { it.path == path }
    }

    override fun isCorrect(input: String): Boolean = parseMove(input) != null

    override fun solution(): String = ""

    override fun toUiState(): GameUiState = MiniCheckersUiState(
        board = board,
        legalMoves = if (phase == Phase.PLAYER_TURN) board.legalMoves().toImmutableList() else persistentListOf(),
        lastMove = lastMove,
        isAiThinking = phase == Phase.AI_THINKING,
        outcome = outcome,
        combinationMoves = if (phase == Phase.PLAYER_TURN) MiniCheckersScenarioGenerator.combinationLength(board) else null,
        quietPlies = board.quietPlies,
        drawPlies = board.drawPlies,
        pointsForWin = winPoints(),
    )

    fun winPoints(): Int = winPointsFor(difficulty)

    companion object {
        /** Normal and Hard wins map to the silver and gold thresholds on
         *  [GameType.MINI_CHECKERS]. */
        fun winPointsFor(difficulty: MiniCheckersDifficulty): Int = when (difficulty) {
            MiniCheckersDifficulty.NORMAL -> 10
            MiniCheckersDifficulty.HARD -> 15
        }

        fun encodeMove(path: List<Int>): String = path.joinToString(">")
    }
}
