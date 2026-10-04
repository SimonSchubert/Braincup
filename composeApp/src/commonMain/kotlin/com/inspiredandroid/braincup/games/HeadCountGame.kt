package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.HeadCountUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

/**
 * Head Count: people walk into and out of a house at a machine-set pace, and when the traffic
 * stops the player enters how many are inside.
 *
 * Built like [QuickSumGame]: a [Phase.WATCHING] reveal the player cannot speed up, then a one-shot
 * [Phase.ANSWER]. The difference is that every step can push the tally either way, and higher up
 * people arrive from either side and go in and come out in the same step, which is what turns
 * counting into tracking.
 *
 * A correct count advances the ramp; a wrong one replays the same tier with fresh traffic. Unlike
 * Quick Sum the ramp carries over: the next run starts a few rounds below where this one ended.
 */
class HeadCountGame(private val random: Random = Random.Default) :
    Game(),
    RevealRoundGame {
    enum class Phase { WATCHING, ANSWER }

    data class Move(
        val entering: Int,
        val leaving: Int,
        val entersFromLeft: Boolean,
    )

    var phase: Phase = Phase.WATCHING
        private set

    override val isTimedPhaseActive: Boolean get() = phase == Phase.WATCHING

    var moves: List<Move> = emptyList()
        private set

    /** -1 during the lead-in, before the first move has started. */
    var currentMoveIndex: Int = -1
        private set

    /**
     * Bumped every time a move starts, so the UI restarts its walk even when a replayed round puts
     * the same move index on screen twice in a row.
     */
    var moveKey: Int = 0
        private set

    var answerResult: RevealResult? = null
        private set

    private var watchJob: Job? = null
    private var watchPaused = false

    private data class DifficultyConfig(
        val moveCount: Int,
        val maxBatch: Int,
        val isBothSides: Boolean,
        val simultaneousChance: Float,
        val stepMs: Long,
    )

    /**
     * One round per step, and each step changes as little as it can: a longer round, a bigger
     * batch, a second route, then people crossing in the same move, a little more often each time.
     * Several of those landing on one round is what made the old two-round tiers feel like a wall.
     */
    private fun configForRound(r: Int): DifficultyConfig = Ramp[r.coerceAtMost(Ramp.lastIndex)]

    companion object {
        private val Ramp = listOf(
            DifficultyConfig(moveCount = 4, maxBatch = 1, isBothSides = false, simultaneousChance = 0f, stepMs = 1400),
            DifficultyConfig(moveCount = 4, maxBatch = 1, isBothSides = false, simultaneousChance = 0f, stepMs = 1350),
            DifficultyConfig(moveCount = 5, maxBatch = 1, isBothSides = false, simultaneousChance = 0f, stepMs = 1300),
            DifficultyConfig(moveCount = 5, maxBatch = 2, isBothSides = false, simultaneousChance = 0f, stepMs = 1300),
            DifficultyConfig(moveCount = 5, maxBatch = 2, isBothSides = true, simultaneousChance = 0f, stepMs = 1250),
            DifficultyConfig(moveCount = 6, maxBatch = 2, isBothSides = true, simultaneousChance = 0f, stepMs = 1250),
            DifficultyConfig(moveCount = 6, maxBatch = 2, isBothSides = true, simultaneousChance = 0.15f, stepMs = 1200),
            DifficultyConfig(moveCount = 6, maxBatch = 2, isBothSides = true, simultaneousChance = 0.25f, stepMs = 1150),
            DifficultyConfig(moveCount = 7, maxBatch = 2, isBothSides = true, simultaneousChance = 0.3f, stepMs = 1100),
            DifficultyConfig(moveCount = 7, maxBatch = 3, isBothSides = true, simultaneousChance = 0.3f, stepMs = 1050),
            DifficultyConfig(moveCount = 7, maxBatch = 3, isBothSides = true, simultaneousChance = 0.35f, stepMs = 1000),
            DifficultyConfig(moveCount = 8, maxBatch = 3, isBothSides = true, simultaneousChance = 0.4f, stepMs = 950),
            DifficultyConfig(moveCount = MAX_MOVES, maxBatch = MAX_BATCH, isBothSides = true, simultaneousChance = 0.4f, stepMs = MIN_STEP_MS),
        )

        /** Keeps the answer a single digit, so the pad's submit-on-length reveals nothing. */
        const val MAX_OCCUPANTS = 9

        const val MAX_MOVES = 8

        /** People in a batch walk at one pace, so past this they no longer fit in a step. */
        const val MAX_BATCH = 3

        const val MIN_STEP_MS = 900L

        /**
         * Blank beat before the first move, for the same reason as Quick Sum's: the previous
         * round's revealed count must not run straight into the next round's traffic.
         */
        const val LEAD_IN_MS = 700L

        private const val FIRST_MOVE_MAX_ENTERING = 3
    }

    override fun generateRound() {
        val config = configForRound(round)
        phase = Phase.WATCHING
        answerResult = null
        currentMoveIndex = -1
        moves = generateMoves(config)
    }

    private fun generateMoves(config: DifficultyConfig): List<Move> {
        var occupants = 0
        return List(config.moveCount) { index ->
            val move = if (index == 0) {
                val maxEntering = minOf(config.maxBatch + 1, FIRST_MOVE_MAX_ENTERING)
                Move(entering = random.nextInt(1, maxEntering + 1), leaving = 0, entersFromLeft = true)
            } else {
                nextMove(occupants, config)
            }
            occupants += move.entering - move.leaving
            move
        }
    }

    private fun nextMove(occupants: Int, config: DifficultyConfig): Move {
        val entersFromLeft = !config.isBothSides || random.nextBoolean()
        val room = MAX_OCCUPANTS - occupants
        if (occupants > 0 && random.nextFloat() < config.simultaneousChance) {
            val leaving = random.nextInt(1, minOf(config.maxBatch, occupants) + 1)
            val entering = random.nextInt(1, minOf(config.maxBatch, room + leaving) + 1)
            return Move(entering = entering, leaving = leaving, entersFromLeft = entersFromLeft)
        }
        val goesIn = when {
            occupants == 0 -> true
            room == 0 -> false
            else -> random.nextBoolean()
        }
        return if (goesIn) {
            Move(entering = random.nextInt(1, minOf(config.maxBatch, room) + 1), leaving = 0, entersFromLeft = entersFromLeft)
        } else {
            Move(entering = 0, leaving = random.nextInt(1, minOf(config.maxBatch, occupants) + 1), entersFromLeft = entersFromLeft)
        }
    }

    /** Config for the round now in play, i.e. after [Game.nextRound] bumped [round]. */
    private fun activeConfig(): DifficultyConfig = configForRound(round.coerceAtLeast(1) - 1)

    fun stepDurationMs(): Long = activeConfig().stepMs

    fun occupants(): Int = moves.sumOf { it.entering - it.leaving }

    override fun startTimedPhase(scope: CoroutineScope, onChange: () -> Unit) {
        watchJob?.cancel()
        watchPaused = false
        phase = Phase.WATCHING
        currentMoveIndex = -1
        onChange()
        val step = stepDurationMs().milliseconds
        watchJob = scope.launch {
            delay(LEAD_IN_MS.milliseconds)
            for (i in moves.indices) {
                currentMoveIndex = i
                moveKey++
                onChange()
                delay(step)
            }
            phase = Phase.ANSWER
            onChange()
        }
    }

    override fun cancelTimedPhase() {
        watchPaused = false
        watchJob?.cancel()
        watchJob = null
    }

    override fun pauseTimedPhase() {
        if (watchJob == null || phase != Phase.WATCHING) return
        watchPaused = true
        watchJob?.cancel()
        watchJob = null
    }

    /**
     * Restart with fresh traffic rather than resuming mid-round, so the quit dialog cannot be
     * farmed for a second look at the same moves.
     */
    override fun resumeTimedPhase(scope: CoroutineScope, onChange: () -> Unit) {
        if (!watchPaused) return
        watchPaused = false
        repeatRound()
        startTimedPhase(scope, onChange)
    }

    fun submitCount(input: String): Boolean {
        val ok = isCorrect(input)
        answerResult = if (ok) RevealResult.CORRECT else RevealResult.WRONG
        if (!ok) answeredAllCorrect = false
        return ok
    }

    override fun isCorrect(input: String): Boolean = input.trim() == occupants().toString()

    override fun solution(): String = occupants().toString()

    override fun toUiState(): HeadCountUiState {
        val move = if (phase == Phase.WATCHING) moves.getOrNull(currentMoveIndex) else null
        return HeadCountUiState(
            phase = phase,
            moveKey = moveKey,
            entering = move?.entering ?: 0,
            leaving = move?.leaving ?: 0,
            entersFromLeft = move?.entersFromLeft ?: true,
            stepMillis = stepDurationMs(),
            revealedCount = if (answerResult != null) occupants() else null,
            answerResult = answerResult,
        )
    }
}
