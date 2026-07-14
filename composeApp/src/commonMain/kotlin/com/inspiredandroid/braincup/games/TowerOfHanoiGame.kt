package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.TowerOfHanoiUiState
import kotlinx.collections.immutable.toImmutableList

/**
 * Classic Tower of Hanoi. Level scales the disk count; score is the level solved.
 * Pegs are indexed 0..2 (left → right). Disks are sized 1..diskCount where larger numbers
 * are larger disks. Start: all disks on peg 0; goal: all on peg 2.
 */
class TowerOfHanoiGame(
    level: Int = 1,
) : Game() {
    override val adaptiveDifficulty: Boolean = false

    var level: Int = level.coerceAtLeast(1)
        private set

    var diskCount: Int = 3
        private set

    /** Disks bottom→top on each peg. Larger size values are physically larger disks. */
    private var pegs: Array<MutableList<Int>> = Array(PEG_COUNT) { mutableListOf() }

    var selectedPeg: Int? = null
        private set

    var moves: Int = 0
        private set

    /**
     * Peg that was the target of the most recent illegal drop (larger on smaller), or null.
     * Paired with [rejectNonce] so the UI can re-fire feedback for repeated rejects.
     */
    var rejectedPeg: Int? = null
        private set

    /** Source peg of the most recent illegal drop (disk that failed to move). */
    var rejectFromPeg: Int? = null
        private set

    /** Monotonic counter bumped on every illegal drop; 0 until the first reject this round. */
    var rejectNonce: Int = 0
        private set

    override fun generateRound() {
        diskCount = disksForLevel(level)
        pegs = Array(PEG_COUNT) { mutableListOf() }
        // Largest disk at bottom (diskCount), smallest on top (1).
        pegs[START_PEG].addAll(diskCount downTo 1)
        selectedPeg = null
        moves = 0
        rejectedPeg = null
        rejectFromPeg = null
        rejectNonce = 0
    }

    /**
     * Tap a peg to select a source, deselect, or complete a move.
     * @return true when the puzzle is solved after this tap.
     */
    fun tapPeg(peg: Int): Boolean {
        if (peg !in 0 until PEG_COUNT) return false
        val current = selectedPeg
        when {
            current == null -> {
                rejectedPeg = null
                rejectFromPeg = null
                if (pegs[peg].isNotEmpty()) selectedPeg = peg
            }
            current == peg -> {
                selectedPeg = null
                rejectedPeg = null
                rejectFromPeg = null
            }
            canMove(current, peg) -> {
                val disk = pegs[current].removeAt(pegs[current].lastIndex)
                pegs[peg].add(disk)
                moves++
                selectedPeg = null
                rejectedPeg = null
                rejectFromPeg = null
                return isSolved()
            }
            else -> {
                // Illegal drop (larger on smaller): clear selection and signal the rejected
                // target (and source) so the UI can flash/shake without leaving a held disk.
                rejectedPeg = peg
                rejectFromPeg = current
                selectedPeg = null
                rejectNonce++
            }
        }
        return false
    }

    private fun canMove(from: Int, to: Int): Boolean {
        val source = pegs[from]
        if (source.isEmpty()) return false
        val moving = source.last()
        val target = pegs[to]
        return target.isEmpty() || target.last() > moving
    }

    fun isSolved(): Boolean = pegs[GOAL_PEG].size == diskCount

    override fun isCorrect(input: String): Boolean = isSolved()

    override fun solution(): String = ""

    override fun hint(): String? = null

    override fun toUiState(): TowerOfHanoiUiState = TowerOfHanoiUiState(
        diskCount = diskCount,
        pegs = pegs.map { it.toImmutableList() }.toImmutableList(),
        selectedPeg = selectedPeg,
        rejectedPeg = rejectedPeg,
        rejectFromPeg = rejectFromPeg,
        rejectNonce = rejectNonce,
        moves = moves,
        level = level,
    )

    companion object {
        const val PEG_COUNT = 3
        const val START_PEG = 0
        const val GOAL_PEG = 2

        /**
         * One disk more per level so every level is a unique difficulty.
         * Level 1 → 3 disks (optimal 7 moves), level 2 → 4 (15), …, level n → n+2.
         */
        fun disksForLevel(level: Int): Int = level.coerceAtLeast(1) + 2
    }
}
