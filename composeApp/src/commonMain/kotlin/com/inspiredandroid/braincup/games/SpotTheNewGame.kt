package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.FeedbackMessage
import com.inspiredandroid.braincup.app.SpotTheNewUiState
import com.inspiredandroid.braincup.games.tools.Animal
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds

/**
 * Recognition-memory survival game.
 *
 * The game opens with a short memorize phase: [SEED_COUNT] animals are shown for [MEMORIZE_MILLIS]
 * with no interaction. After the countdown the first answering round appears, adding exactly one
 * brand-new animal to those already seen; the player must tap the new one.
 *
 * Each correct tap adds the new animal to the seen pool, advances the round, and grows the
 * displayed count by 1 every 2 rounds (capped at [MAX_DISPLAY]). There is no timer; a single wrong
 * tap ends the game. When every animal has been seen the game ends with the maximum score. The
 * score is the number of rounds survived.
 */
class SpotTheNewGame : Game() {
    // Survival game: never resume mid-difficulty, always start fresh.
    override val adaptiveDifficulty: Boolean = false

    sealed class SubmitResult {
        /** Correct tap; the game advanced to the next round. */
        data object Correct : SubmitResult()

        /** Wrong tap; the game is over. */
        data object Wrong : SubmitResult()

        /** Correct tap that exhausted every animal; finish the game with the maximum score. */
        data object PoolExhausted : SubmitResult()
    }

    enum class Phase { MEMORIZING, ANSWERING, GAME_OVER }

    enum class CellType { NORMAL, WRONG, CORRECT }

    companion object {
        const val MAX_DISPLAY = 16

        /** Animals shown (and memorized) before the first answering round. */
        const val SEED_COUNT = 3

        /** Duration of the opening memorize phase. */
        const val MEMORIZE_MILLIS = 2000L

        /** Every available animal; the game ends once all of these have been seen. */
        val ALL_ANIMALS: List<Animal> = Animal.entries.toList()

        /**
         * Total displayed animals for a 0-indexed answering round, before clamping to the seen pool.
         * Starts at [SEED_COUNT] + 1 (the seed plus one new animal) and grows by 1 every 2 rounds.
         */
        fun targetDisplayCount(round: Int): Int = (SEED_COUNT + 1 + round / 2).coerceAtMost(MAX_DISPLAY)
    }

    var phase: Phase = Phase.MEMORIZING
        private set

    /** Cumulative pool of every animal shown so far (seed + each tapped new one). */
    private val seen = mutableSetOf<Animal>()

    /** Number of animals the player has already seen. Exposed for tests. */
    val seenCount: Int get() = seen.size

    /** The new animal for the current round (the correct answer); null during the memorize phase. */
    var newAnimal: Animal? = null
        private set

    /** Animals shown this round in display order (memorize seed, or old subset + the new animal). */
    var displayed: List<Animal> = emptyList()
        private set

    /** The animal the player wrongly tapped, used to mark it on the game-over reveal. */
    private var wrongAnimal: Animal? = null

    /** True when every animal has been seen, i.e. the player beat the whole set. */
    var poolExhausted: Boolean = false
        private set

    private var countdownJob: Job? = null
    private var memorizeDeadlineMillis: Long = 0L
    private var countdownPaused = false

    /** Seeds the initial animals and enters the memorize phase. */
    fun startMemorizing() {
        phase = Phase.MEMORIZING
        val seed = ALL_ANIMALS.shuffled().take(SEED_COUNT)
        seen.addAll(seed)
        displayed = seed.shuffled()
    }

    /** Runs the memorize countdown, then transitions to the first answering round. */
    fun startMemorizeCountdown(scope: CoroutineScope, onStateChanged: () -> Unit) {
        countdownJob?.cancel()
        countdownPaused = false
        memorizeDeadlineMillis = Clock.System.now().toEpochMilliseconds() + MEMORIZE_MILLIS
        countdownJob = scope.launch {
            delay(MEMORIZE_MILLIS)
            startAnswering()
            onStateChanged()
        }
    }

    fun cancelCountdown() {
        countdownPaused = false
        countdownJob?.cancel()
        countdownJob = null
    }

    fun pauseCountdown() {
        if (countdownJob == null) return
        countdownPaused = true
        val remaining = (memorizeDeadlineMillis - Clock.System.now().toEpochMilliseconds()).coerceAtLeast(0)
        memorizeDeadlineMillis = Clock.System.now().toEpochMilliseconds() + remaining
        countdownJob?.cancel()
        countdownJob = null
    }

    fun resumeCountdown(scope: CoroutineScope, onStateChanged: () -> Unit) {
        if (!countdownPaused) return
        countdownPaused = false
        val remaining = (memorizeDeadlineMillis - Clock.System.now().toEpochMilliseconds()).coerceAtLeast(0)
        countdownJob = scope.launch {
            delay(remaining.milliseconds)
            startAnswering()
            onStateChanged()
        }
    }

    /** Transitions from the memorize phase into the first answering round. */
    fun startAnswering() {
        phase = Phase.ANSWERING
        nextRound()
    }

    override fun generateRound() {
        val unseen = ALL_ANIMALS.filterNot { it in seen }
        if (unseen.isEmpty()) {
            poolExhausted = true
            newAnimal = null
            displayed = emptyList()
            return
        }
        val target = targetDisplayCount(round)
        val oldCount = minOf(target - 1, seen.size)
        val oldAnimals = seen.shuffled().take(oldCount)
        val newOne = unseen.random()
        newAnimal = newOne
        displayed = (oldAnimals + newOne).shuffled()
    }

    /** Handles a tap, advancing the game or ending it. The input is the displayed-list index. */
    fun submitAnswer(answer: String): SubmitResult {
        if (phase != Phase.ANSWERING) return SubmitResult.Wrong
        val tapped = answer.toIntOrNull()?.let { displayed.getOrNull(it) }
        if (tapped == null || tapped != newAnimal) {
            answeredAllCorrect = false
            wrongAnimal = tapped
            phase = Phase.GAME_OVER
            return SubmitResult.Wrong
        }
        newAnimal?.let { seen.add(it) }
        nextRound()
        return if (poolExhausted) SubmitResult.PoolExhausted else SubmitResult.Correct
    }

    override fun isCorrect(input: String): Boolean {
        val index = input.toIntOrNull() ?: return false
        return displayed.getOrNull(index) == newAnimal
    }

    override fun solution(): String = newAnimal?.displayName ?: ""

    override fun solutionMessage(): FeedbackMessage = FeedbackMessage.Plain(solution())

    override fun hint(): String? = null

    override fun toUiState(): SpotTheNewUiState {
        val cells = displayed.mapIndexed { index, animal ->
            val type = when {
                phase == Phase.GAME_OVER && animal == newAnimal -> CellType.CORRECT
                phase == Phase.GAME_OVER && animal == wrongAnimal -> CellType.WRONG
                else -> CellType.NORMAL
            }
            SpotTheNewUiState.CellState(animal = animal, index = index, type = type)
        }
        return SpotTheNewUiState(
            round = round,
            phase = phase,
            displayedCount = displayed.size,
            cells = cells.toImmutableList(),
        )
    }
}
