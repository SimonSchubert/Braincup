package com.inspiredandroid.braincup.games

import kotlinx.coroutines.CoroutineScope

/**
 * A game that drives part of a round from its own coroutine: a memorize countdown, a flashing
 * sequence, an animation loop or a chess search. The controller stops that work through this
 * interface on every exit path, so leaving a game never depends on knowing which class it was.
 */
interface TimedPhaseGame {
    /** Stops any coroutine this game started. Safe to call when nothing is running. */
    fun cancelTimedPhase()
}

/**
 * A [TimedPhaseGame] whose running phase has to be held while a modal covers the board, so the
 * player cannot buy extra memorize time by opening the quit dialog.
 */
interface PausableTimedPhaseGame : TimedPhaseGame {
    /** True while the phase that has to be held is the one on screen. */
    val isTimedPhaseActive: Boolean

    fun pauseTimedPhase()

    fun resumeTimedPhase(scope: CoroutineScope, onChange: () -> Unit)
}

/**
 * A timed game whose every round opens with a coroutine-driven reveal (digits, terms, shapes)
 * before the player may answer. Correct answers advance the ramp through [Game.nextRound], wrong
 * ones replay the same difficulty through [Game.repeatRound]; either way the next round starts by
 * calling [startTimedPhase] again.
 */
interface RevealRoundGame : PausableTimedPhaseGame {
    fun startTimedPhase(scope: CoroutineScope, onChange: () -> Unit)
}
