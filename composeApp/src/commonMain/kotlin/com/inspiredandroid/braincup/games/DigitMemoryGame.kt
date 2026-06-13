package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.DigitMemoryUiState
import com.inspiredandroid.braincup.games.tools.Operator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds

/**
 * Digit Memory: a working-memory game played against the global 60s timer.
 *
 * Each round runs three phases:
 *  1. [Phase.SHOWING]  - a digit sequence is shown for a few seconds, then auto-hidden.
 *  2. [Phase.SOLVING]  - a simple distraction math problem must be solved (occupies working memory).
 *                        Getting it wrong forfeits the round: no recall, no point, and a fresh
 *                        sequence of the same length is shown.
 *  3. [Phase.RECALL]   - the player re-enters the original digit sequence.
 *
 * The sequence grows by one digit each round (round 1 -> 4 digits) and the math problem gets mildly
 * harder, but the math stays an easy distractor, not a hard calculation. Only the recall is scored.
 */
class DigitMemoryGame : Game() {
    // Always start fresh at length 4 so scores are comparable run-to-run.
    override val adaptiveDifficulty: Boolean = false

    enum class Phase { SHOWING, SOLVING, RECALL }

    enum class RecallResult { CORRECT, WRONG }

    var phase: Phase = Phase.SHOWING
        private set

    /** The digits to memorize, kept as a String so leading zeros survive comparison. */
    var sequence: String = ""
        private set

    /** Distraction problem text, e.g. "3 + 4". */
    var problem: String = ""
        private set

    /** Expected math answer as a String (for length-based auto-submit and comparison). */
    var problemAnswer: String = ""
        private set

    /** Non-null while briefly revealing the correct math answer after a wrong attempt. */
    var revealedMathAnswer: String? = null
        private set

    /** Non-null while revealing the recall result (drives the green/red reveal). */
    var recallResult: RecallResult? = null
        private set

    private var showJob: Job? = null
    private var showDeadlineMillis: Long = 0L
    private var showingPaused = false

    override fun generateRound() {
        phase = Phase.SHOWING
        revealedMathAnswer = null
        recallResult = null
        // round is still 0 here (Game.nextRound increments it after generateRound), so +4 => the
        // first round shows 4 digits, growing by one each round.
        val length = round + 4
        sequence = buildString {
            repeat(length) { append(Random.nextInt(0, 10)) }
        }
        generateProblem(round)
    }

    private fun generateProblem(round: Int) {
        val operators = if (round < 4) {
            listOf(Operator.PLUS, Operator.MINUS)
        } else {
            listOf(Operator.PLUS, Operator.MINUS, Operator.MULTIPLY)
        }
        val (text, answer) = when (operators.random()) {
            Operator.PLUS -> {
                val max = 9 + round * 2
                val a = Random.nextInt(2, max)
                val b = Random.nextInt(2, max)
                "$a + $b" to (a + b)
            }
            Operator.MINUS -> {
                val max = 9 + round * 2
                val a = Random.nextInt(5, max + 5)
                val b = Random.nextInt(2, a) // b < a -> non-negative result
                "$a - $b" to (a - b)
            }
            Operator.MULTIPLY, Operator.DIVIDE -> {
                val a = Random.nextInt(2, 6 + round / 2)
                val b = Random.nextInt(2, 6)
                "$a * $b" to (a * b)
            }
        }
        problem = text
        problemAnswer = answer.toString()
    }

    private fun showDurationMillis(length: Int): Long = (800L * length).coerceIn(2500L, 6000L)

    /** Show the sequence, then auto-advance to the solving phase after the memorize window. */
    fun startShowing(scope: CoroutineScope, onChange: () -> Unit) {
        showJob?.cancel()
        showingPaused = false
        phase = Phase.SHOWING
        onChange()
        val duration = showDurationMillis(sequence.length)
        showDeadlineMillis = Clock.System.now().toEpochMilliseconds() + duration
        showJob = scope.launch {
            delay(duration)
            phase = Phase.SOLVING
            onChange()
        }
    }

    fun cancelShowing() {
        showingPaused = false
        showJob?.cancel()
        showJob = null
    }

    fun pauseShowing() {
        if (showJob == null || phase != Phase.SHOWING) return
        showingPaused = true
        val remaining = (showDeadlineMillis - Clock.System.now().toEpochMilliseconds()).coerceAtLeast(0)
        showDeadlineMillis = Clock.System.now().toEpochMilliseconds() + remaining
        showJob?.cancel()
        showJob = null
    }

    fun resumeShowing(scope: CoroutineScope, onChange: () -> Unit) {
        if (!showingPaused) return
        showingPaused = false
        val remaining = (showDeadlineMillis - Clock.System.now().toEpochMilliseconds()).coerceAtLeast(0)
        showJob = scope.launch {
            delay(remaining.milliseconds)
            phase = Phase.SOLVING
            onChange()
        }
    }

    /**
     * Re-generate the round at the current difficulty (same sequence length) without advancing.
     * Used when a failed math problem forfeits the round.
     */
    fun repeatRound() {
        round -= 1
        nextRound()
    }

    /**
     * Submit the distraction math answer. Returns whether it was correct. On a wrong answer the
     * round is forfeited: the correct value is exposed via [revealedMathAnswer] so the UI can flash
     * it before a fresh round starts.
     */
    fun submitMath(input: String): Boolean {
        val ok = input == problemAnswer
        if (!ok) {
            revealedMathAnswer = problemAnswer
            answeredAllCorrect = false
        }
        return ok
    }

    fun advanceToRecall() {
        revealedMathAnswer = null
        phase = Phase.RECALL
    }

    /** Submit the recalled sequence. Returns whether it matched; exposes [recallResult] for reveal. */
    fun submitRecall(input: String): Boolean {
        val ok = input == sequence
        recallResult = if (ok) RecallResult.CORRECT else RecallResult.WRONG
        if (!ok) answeredAllCorrect = false
        return ok
    }

    override fun isCorrect(input: String): Boolean = input == sequence

    override fun solution(): String = sequence

    override fun hint(): String? = null

    override fun toUiState(): DigitMemoryUiState = DigitMemoryUiState(
        phase = phase,
        sequence = sequence,
        sequenceLength = sequence.length,
        problem = problem,
        answerLength = problemAnswer.length,
        revealedMathAnswer = revealedMathAnswer,
        recallResult = recallResult,
    )
}
