package com.inspiredandroid.braincup.app

import androidx.navigation.NavController
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.games.iqtest.IqScoring
import com.inspiredandroid.braincup.games.iqtest.IqTest
import com.inspiredandroid.braincup.games.iqtest.IqTestBlueprint
import com.inspiredandroid.braincup.games.matrix.MatrixProblem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds

/**
 * Drives the IQ test mode.
 *
 * Kept apart from [GameController] because almost nothing is shared: the test has no per-round
 * scoring, no medals, no adaptive round storage, and one clock spanning thirty items rather than a
 * sixty second run. Folding it into `finishCurrentGame` would mean a second mode flag on a class
 * that already branches on one.
 *
 * An attempt lives only in memory. A timed test that survived an app kill could not keep its clock
 * honest, so leaving abandons the run.
 */
class IqTestController(
    private val navController: NavController,
    private val storage: UserStorage,
    private val random: Random = Random.Default,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var test: IqTest? = null
    private var startedAtMillis = 0L
    private var timerJob: Job? = null
    private var advanceJob: Job? = null

    /**
     * Latches once an attempt has been scored. The clock expiring and the player pressing Finish can
     * land in the same frame, and scoring twice would write two history entries and pay XP twice.
     */
    private var scored = false

    private val _playState = MutableStateFlow<IqTestPlayUiState?>(null)
    val playState: StateFlow<IqTestPlayUiState?> = _playState.asStateFlow()

    private val _timeRemaining = MutableStateFlow(IqTestBlueprint.TIME_LIMIT_MILLIS)
    val timeRemaining: StateFlow<Long> = _timeRemaining.asStateFlow()

    private val _resultState = MutableStateFlow<IqTestResultUiState?>(null)
    val resultState: StateFlow<IqTestResultUiState?> = _resultState.asStateFlow()

    private val _history = MutableStateFlow<ImmutableList<UserStorage.IqTestRecord>>(
        storage.getIqTestResults().toImmutableList(),
    )
    val history: StateFlow<ImmutableList<UserStorage.IqTestRecord>> = _history.asStateFlow()

    private val _reviewItem = MutableStateFlow<IqTestReviewItemUiState?>(null)
    val reviewItem: StateFlow<IqTestReviewItemUiState?> = _reviewItem.asStateFlow()

    fun dispose() {
        scope.cancel()
    }

    fun refreshHistory() {
        _history.value = storage.getIqTestResults().toImmutableList()
    }

    fun navigateToIntro() {
        refreshHistory()
        navController.navigate(IqTestIntro)
    }

    fun start() {
        val fresh = IqTest(seed = random.nextLong())
        test = fresh
        scored = false
        startedAtMillis = Clock.System.now().toEpochMilliseconds()
        _resultState.value = null
        _timeRemaining.value = IqTestBlueprint.TIME_LIMIT_MILLIS
        emitPlayState(fresh)
        navController.navigate(IqTestPlay)
        startTimer()
    }

    fun select(optionIndex: Int) {
        val current = test ?: return
        current.select(optionIndex)
        emitPlayState(current)
        // A brief hold lets the pick register visually before the next item replaces it. Going
        // back is always allowed, so this never traps a player who wants to change their answer.
        advanceJob?.cancel()
        advanceJob = scope.launch {
            delay(AUTO_ADVANCE_DELAY)
            if (!current.next()) return@launch
            emitPlayState(current)
        }
    }

    fun goToNext() {
        val current = test ?: return
        advanceJob?.cancel()
        if (current.next()) emitPlayState(current)
    }

    fun goToPrevious() {
        val current = test ?: return
        advanceJob?.cancel()
        if (current.previous()) emitPlayState(current)
    }

    fun finish() {
        val current = test ?: return
        if (scored) return
        scored = true
        advanceJob?.cancel()
        timerJob?.cancel()
        timerJob = null

        val durationSeconds = ((Clock.System.now().toEpochMilliseconds() - startedAtMillis) / 1000).toInt()
        val rawScore = current.rawScore
        val award = storage.putIqTestResult(
            seed = current.seed,
            rawScore = rawScore,
            durationSeconds = durationSeconds,
        )
        refreshHistory()

        val iq = IqScoring.iqFor(rawScore)
        _resultState.value = IqTestResultUiState(
            rawScore = rawScore,
            itemCount = IqTestBlueprint.ITEM_COUNT,
            iq = iq,
            percentile = IqScoring.percentileFor(iq),
            band = IqScoring.bandFor(iq),
            isBelowMeasurableRange = IqScoring.isBelowMeasurableRange(rawScore),
            tierBreakdown = current.tierBreakdown().toImmutableList(),
            durationSeconds = durationSeconds,
            xpGained = award.xpGained,
            levelChange = award.levelChange,
            isPersonalBest = award.isPersonalBest,
        )
        _playState.value = null
        navController.navigate(IqTestResult) {
            popUpTo(IqTestIntro) { inclusive = false }
        }
    }

    fun abandon() {
        advanceJob?.cancel()
        timerJob?.cancel()
        timerJob = null
        test = null
        scored = false
        _playState.value = null
        navController.popBackStack(MainMenu, inclusive = false)
    }

    fun openReview() {
        val current = test ?: return
        current.goTo(0)
        emitReviewItem(current)
        navController.navigate(IqTestReview)
    }

    fun reviewNext() {
        val current = test ?: return
        if (current.next()) emitReviewItem(current)
    }

    fun reviewPrevious() {
        val current = test ?: return
        if (current.previous()) emitReviewItem(current)
    }

    fun leaveResult() {
        test = null
        _reviewItem.value = null
        navController.popBackStack(MainMenu, inclusive = false)
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = scope.launch {
            while (true) {
                val elapsed = Clock.System.now().toEpochMilliseconds() - startedAtMillis
                val remaining = (IqTestBlueprint.TIME_LIMIT_MILLIS - elapsed).coerceAtLeast(0)
                _timeRemaining.value = remaining
                if (remaining <= 0) break
                delay(TIMER_TICK)
            }
            // Clear the handle before finishing: finish() cancels the timer, and this coroutine is
            // that timer, so leaving it set would have it cancel itself mid-scoring.
            timerJob = null
            finish()
        }
    }

    private fun emitPlayState(current: IqTest) {
        val problem = current.problemAt(current.currentIndex)
        _playState.value = IqTestPlayUiState(
            itemIndex = current.currentIndex,
            itemCount = IqTestBlueprint.ITEM_COUNT,
            matrix = problem.blankedMatrix(),
            optionRows = problem.optionRows(),
            optionColumns = problem.optionColumns,
            selectedOption = current.responseAt(current.currentIndex),
            isOnLastItem = current.isOnLastItem,
        )
    }

    private fun emitReviewItem(current: IqTest) {
        val index = current.currentIndex
        val problem = current.problemAt(index)
        _reviewItem.value = IqTestReviewItemUiState(
            itemIndex = index,
            itemCount = IqTestBlueprint.ITEM_COUNT,
            matrix = problem.blankedMatrix(),
            optionRows = problem.optionRows(),
            optionColumns = problem.optionColumns,
            pickedOption = current.responseAt(index),
            correctOption = problem.correctOptionIndex,
            tier = IqTestBlueprint.tierFor(index),
        )
    }

    companion object {
        private val TIMER_TICK = 100.milliseconds
        private val AUTO_ADVANCE_DELAY = 350.milliseconds

        private fun MatrixProblem.blankedMatrix() = matrix
            .mapIndexed { index, panel -> panel.takeIf { index != MatrixProblem.ANSWER_INDEX } }
            .toImmutableList()

        private fun MatrixProblem.optionRows() = options
            .map { MatrixOptionCell(it) }
            .chunked(optionColumns)
            .map { it.toImmutableList() }
            .toImmutableList()
    }
}
