package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.GhostGridUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Ghost Grid game. A sequence of tiles lights up
 * one at a time on a grid, and the player must tap them back in the same order.
 */
class GhostGridGame : Game() {
    sealed class SubmitResult {
        data object CorrectContinue : SubmitResult()
        data object RoundComplete : SubmitResult()
        data object Wrong : SubmitResult()
    }

    enum class Phase {
        SHOWING,
        ANSWERING,
        GAME_OVER,
    }

    var gridSize: Int = 4
        private set

    var sequence: List<Int> = emptyList()
        private set

    var phase: Phase = Phase.SHOWING
        private set

    var currentShowIndex: Int = -1
        private set

    var currentTapIndex: Int = 0
        private set

    var wrongPosition: Int? = null
        private set

    var flashDurationMillis: Long = 600L
        private set

    private var showJob: Job? = null

    override fun generateRound() {
        val sequenceLength = 3 + round
        gridSize = if (sequenceLength >= 7) 5 else 4
        flashDurationMillis = if (sequenceLength >= 9) 450L else 600L

        val totalCells = gridSize * gridSize
        sequence = (0 until totalCells).shuffled().take(sequenceLength)

        phase = Phase.SHOWING
        currentShowIndex = -1
        currentTapIndex = 0
        wrongPosition = null
    }

    fun startShowSequence(scope: CoroutineScope, onStateChanged: () -> Unit) {
        showJob?.cancel()
        showJob = scope.launch {
            for (i in sequence.indices) {
                currentShowIndex = i
                onStateChanged()
                delay(flashDurationMillis)
                currentShowIndex = -1
                onStateChanged()
                delay(200L)
            }
            phase = Phase.ANSWERING
            onStateChanged()
        }
    }

    fun cancelShowSequence() {
        showJob?.cancel()
        showJob = null
    }

    fun submitAnswer(position: String): SubmitResult {
        val pos = position.toIntOrNull() ?: return SubmitResult.Wrong

        val expected = sequence[currentTapIndex]
        if (pos != expected) {
            answeredAllCorrect = false
            wrongPosition = pos
            phase = Phase.GAME_OVER
            return SubmitResult.Wrong
        }

        currentTapIndex++
        if (currentTapIndex >= sequence.size) {
            nextRound()
            return SubmitResult.RoundComplete
        }
        return SubmitResult.CorrectContinue
    }

    override fun isCorrect(input: String): Boolean {
        val pos = input.toIntOrNull() ?: return false
        return currentTapIndex < sequence.size && pos == sequence[currentTapIndex]
    }

    override fun solution(): String = sequence.joinToString(", ")

    override fun hint(): String? = null

    override fun toUiState(): GhostGridUiState {
        val totalCells = gridSize * gridSize
        val tappedPositions = if (phase == Phase.ANSWERING || phase == Phase.GAME_OVER) {
            sequence.take(currentTapIndex).toSet()
        } else {
            emptySet()
        }

        val cells = (0 until totalCells).map { position ->
            when {
                phase == Phase.SHOWING && currentShowIndex >= 0 && position == sequence[currentShowIndex] ->
                    GhostGridUiState.CellState(GhostGridUiState.CellType.ACTIVE)

                phase == Phase.GAME_OVER && position == wrongPosition ->
                    GhostGridUiState.CellState(GhostGridUiState.CellType.WRONG)

                phase == Phase.GAME_OVER && position == sequence[currentTapIndex] ->
                    GhostGridUiState.CellState(GhostGridUiState.CellType.MISSED)

                position in tappedPositions ->
                    GhostGridUiState.CellState(GhostGridUiState.CellType.TAPPED)

                else ->
                    GhostGridUiState.CellState(GhostGridUiState.CellType.INACTIVE)
            }
        }

        return GhostGridUiState(
            gridSize = gridSize,
            round = round,
            phase = phase,
            cells = cells,
            sequenceLength = sequence.size,
            tappedCount = currentTapIndex,
        )
    }
}
