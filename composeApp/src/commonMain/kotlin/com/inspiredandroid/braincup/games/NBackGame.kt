package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.NBackUiState
import com.inspiredandroid.braincup.games.tools.Shape
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

/**
 * A sequence-memory game: a short run of shapes flashes by one at a time, then the player is asked
 * for the shape at a single position ("which shape was at position 3?") and taps it from a fixed
 * palette of every shape.
 *
 * Each round runs two phases:
 *  1. [Phase.MEMORIZE] - the shapes flash in order, each vanishing before the next, with a position
 *                        counter so the player can bind each shape to its slot.
 *  2. [Phase.RECALL]   - one position is asked and the tapped shape is revealed green/red.
 *
 * A correct answer lengthens the next sequence (up to [MAX_LENGTH]); a wrong one replays the same
 * length with a fresh sequence, so the pace never runs ahead of the player. Answering is untimed and
 * a blind guess is right 1 in [Shape] count of the time, so there is nothing to farm; the score is
 * the number of correct recalls within the global 60s timer.
 */
class NBackGame : Game() {
    // Always start at the shortest sequence so scores are comparable run-to-run.
    override val adaptiveDifficulty: Boolean = false

    enum class Phase { MEMORIZE, RECALL }

    enum class RecallResult { CORRECT, WRONG }

    var phase: Phase = Phase.MEMORIZE
        private set

    /** The shapes to memorize, in flash order; distinct within a round so a position is unambiguous. */
    var sequence: List<Shape> = emptyList()
        private set

    /** Index of the shape currently flashing, or -1 before the first / during the gap bookkeeping. */
    var showIndex: Int = -1
        private set

    /** False during the blank gap between two shapes. */
    var showing: Boolean = false
        private set

    /** The position (0-based) the player is asked to recall this round. */
    var askPosition: Int = 0
        private set

    /** Non-null while revealing the answer after a tap (drives the green/red reveal). */
    var recallResult: RecallResult? = null
        private set

    private var showJob: Job? = null
    private var showPaused = false

    companion object {
        const val MIN_LENGTH = 3
        const val MAX_LENGTH = 6

        /**
         * Shape identity carries the whole game, so the palette is deliberately small and every
         * shape is unmistakable. ABSTRACT_TRIANGLE is excluded on purpose: it shares the display
         * name "triangle" with TRIANGLE and would read as the same shape.
         */
        val PALETTE = listOf(
            Shape.SQUARE,
            Shape.TRIANGLE,
            Shape.CIRCLE,
            Shape.HEART,
            Shape.STAR,
            Shape.DIAMOND,
        )

        private const val VISIBLE_MS = 800L
        private const val GAP_MS = 250L

        /** Blank beat before the first shape so a new round does not run into the last reveal. */
        private const val LEAD_IN_MS = 500L
    }

    private fun lengthForRound(r: Int): Int = (r + MIN_LENGTH).coerceAtMost(MAX_LENGTH)

    override fun generateRound() {
        // round is still the previous value here (Game.nextRound increments after generateRound),
        // so the first round (round 0) shows MIN_LENGTH shapes.
        val length = lengthForRound(round)
        sequence = PALETTE.shuffled().take(length)
        askPosition = Random.nextInt(length)
        phase = Phase.MEMORIZE
        showIndex = -1
        showing = false
        recallResult = null
    }

    val sequenceLength: Int get() = sequence.size

    /** The correct answer this round: the shape at the asked position. */
    fun answerShape(): Shape = sequence[askPosition]

    /** Flash the sequence in order, then open the recall phase. */
    fun startShowing(scope: CoroutineScope, onChange: () -> Unit) {
        showJob?.cancel()
        showPaused = false
        phase = Phase.MEMORIZE
        showIndex = -1
        showing = false
        recallResult = null
        onChange()
        showJob = scope.launch {
            delay(LEAD_IN_MS.milliseconds)
            for (i in sequence.indices) {
                showIndex = i
                showing = true
                onChange()
                delay(VISIBLE_MS.milliseconds)
                showing = false
                onChange()
                delay(GAP_MS.milliseconds)
            }
            phase = Phase.RECALL
            onChange()
        }
    }

    fun cancelShowing() {
        showPaused = false
        showJob?.cancel()
        showJob = null
    }

    fun pauseShowing() {
        if (showJob == null || phase != Phase.MEMORIZE) return
        showPaused = true
        showJob?.cancel()
        showJob = null
    }

    fun wasPaused(): Boolean = showPaused

    /**
     * Restart the round with a fresh sequence rather than resuming mid-flash. Half a shown sequence
     * has no meaningful resume point, and replaying the same shapes would turn the quit dialog into a
     * free second look that could be farmed by reopening it.
     */
    fun resumeShowing(scope: CoroutineScope, onChange: () -> Unit) {
        if (!showPaused) return
        showPaused = false
        repeatRound()
        startShowing(scope, onChange)
    }

    /** Re-generate the round at the current length with a fresh sequence, without advancing. */
    fun repeatRound() {
        round -= 1
        nextRound()
    }

    /** Submit the tapped shape (by [Shape.name]). Returns whether it was the shape at [askPosition]. */
    fun submitRecall(input: String): Boolean {
        val correct = input == answerShape().name
        recallResult = if (correct) RecallResult.CORRECT else RecallResult.WRONG
        if (!correct) answeredAllCorrect = false
        return correct
    }

    override fun isCorrect(input: String): Boolean = input == answerShape().name

    override fun solution(): String = answerShape().displayName

    override fun hint(): String? = null

    override fun toUiState(): NBackUiState = NBackUiState(
        phase = phase,
        currentShape = if (phase == Phase.MEMORIZE && showing) sequence.getOrNull(showIndex) else null,
        showIndex = showIndex,
        sequenceLength = sequence.size,
        askPosition = askPosition,
        options = PALETTE.toImmutableList(),
        revealAnswer = if (recallResult != null) answerShape() else null,
        recallResult = recallResult,
    )
}
