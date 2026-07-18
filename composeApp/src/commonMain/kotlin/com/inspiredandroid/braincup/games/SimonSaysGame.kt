package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.SimonSaysUiState
import com.inspiredandroid.braincup.games.tools.Color
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

/**
 * Simon Says. Each round appends one random pad to the SAME accumulating sequence, but only that
 * one new pad is flashed -- the player still has to tap the whole sequence back from the start,
 * carrying every earlier pad from memory. Ends on the first wrong tap. Score = rounds (sequence
 * length) survived.
 */
class SimonSaysGame(private val random: Random = Random.Default) : Game() {
    sealed class SubmitResult {
        data object CorrectContinue : SubmitResult()
        data object RoundComplete : SubmitResult()
        data object Wrong : SubmitResult()
    }

    enum class Phase { SHOWING, ANSWERING, GAME_OVER }

    companion object {
        // Classic physical-toy layout: green top-left, red top-right, yellow bottom-left, blue
        // bottom-right. Order here is also the fixed pad order in SimonSaysUiState.pads.
        val PADS = listOf(Color.GREEN, Color.RED, Color.YELLOW, Color.BLUE)

        private const val LEAD_IN_MILLIS = 400L
        private const val FLASH_MILLIS = 600L
    }

    // Survival game: never resume mid-difficulty, always start fresh.
    override val adaptiveDifficulty: Boolean = false

    var sequence: List<Color> = emptyList()
        private set
    var phase: Phase = Phase.SHOWING
        private set
    var currentShowIndex: Int = -1
        private set
    var currentTapIndex: Int = 0
        private set
    var wrongPad: Color? = null
        private set

    private var showJob: Job? = null

    override fun generateRound() {
        // Append, never replace: this cumulative-memory property is the whole point of the game.
        sequence = sequence + PADS.random(random)
        phase = Phase.SHOWING
        currentShowIndex = -1
        currentTapIndex = 0
        wrongPad = null
    }

    /**
     * Flashes only the pad [generateRound] just appended. Ghost Grid replays its whole sequence
     * every round and tiers the flash faster to keep that replay bounded; here the showing phase
     * is one pad however long the sequence is, so the duration stays constant and the difficulty
     * ramp comes entirely from how much the player has to hold in memory.
     */
    fun startShowNewPad(scope: CoroutineScope, onStateChanged: () -> Unit) {
        showJob?.cancel()
        showJob = scope.launch {
            delay(LEAD_IN_MILLIS.milliseconds) // beat before the flash, so it reads as "this one is new"
            currentShowIndex = sequence.lastIndex
            onStateChanged()
            delay(FLASH_MILLIS.milliseconds)
            currentShowIndex = -1
            onStateChanged()
            delay(200.milliseconds)
            phase = Phase.ANSWERING
            onStateChanged()
        }
    }

    fun cancelShowNewPad() {
        showJob?.cancel()
        showJob = null
    }

    fun submitAnswer(colorName: String): SubmitResult {
        val color = Color.entries.find { it.name == colorName }
        val expected = sequence[currentTapIndex]
        if (color == null || color != expected) {
            answeredAllCorrect = false
            wrongPad = color
            phase = Phase.GAME_OVER
            return SubmitResult.Wrong
        }
        currentTapIndex++
        if (currentTapIndex >= sequence.size) return SubmitResult.RoundComplete
        return SubmitResult.CorrectContinue
    }

    override fun isCorrect(input: String): Boolean {
        val color = Color.entries.find { it.name == input } ?: return false
        return currentTapIndex < sequence.size && color == sequence[currentTapIndex]
    }

    override fun solution(): String = sequence.joinToString(", ") { it.displayName }

    override fun hint(): String? = null

    override fun toUiState(): SimonSaysUiState {
        val pads = PADS.map { color ->
            val type = when {
                phase == Phase.SHOWING && currentShowIndex >= 0 && sequence[currentShowIndex] == color ->
                    SimonSaysUiState.CellType.ACTIVE
                phase == Phase.GAME_OVER && wrongPad == color ->
                    SimonSaysUiState.CellType.WRONG
                phase == Phase.GAME_OVER && sequence[currentTapIndex] == color ->
                    SimonSaysUiState.CellType.MISSED
                // Repeat-safe: highlight only the single most recently correctly-tapped pad, not
                // "any pad tapped so far" -- colors repeat within a round (e.g. GREEN, RED, GREEN),
                // so a Set-membership check (Ghost Grid's approach) would wrongly keep GREEN "done"
                // after step 1 even though it's needed again at step 3.
                phase == Phase.ANSWERING && currentTapIndex > 0 && sequence[currentTapIndex - 1] == color ->
                    SimonSaysUiState.CellType.TAPPED
                else -> SimonSaysUiState.CellType.INACTIVE
            }
            SimonSaysUiState.PadState(color, type)
        }
        return SimonSaysUiState(
            round = round,
            phase = phase,
            pads = pads.toImmutableList(),
            sequenceLength = sequence.size,
            tappedCount = currentTapIndex,
        )
    }
}
