package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.FeedbackMessage
import com.inspiredandroid.braincup.app.GameUiState

abstract class Game {
    var answeredAllCorrect = true
    var round = 0
    open val adaptiveDifficulty: Boolean = true

    fun nextRound() {
        generateRound()
        round++
    }

    /**
     * Re-generate the current round at the same difficulty. Games that ramp per round use this
     * after a wrong answer so the pace never runs ahead of the player, and so a re-shown round is
     * never the same puzzle twice.
     */
    fun repeatRound() {
        round -= 1
        nextRound()
    }

    protected abstract fun generateRound()

    abstract fun isCorrect(input: String): Boolean

    abstract fun solution(): String

    open fun solutionMessage(): FeedbackMessage = FeedbackMessage.Plain(solution())

    abstract fun hint(): String?

    /**
     * The hint as the feedback screen shows it, which is where a hint carrying words belongs.
     *
     * Same split as [solution] and [solutionMessage]: a plain [hint] is a string a game already
     * has, and anything that has to be said in the player's language becomes a [FeedbackMessage]
     * the screen resolves against `strings.xml` instead.
     */
    open fun hintMessage(): FeedbackMessage? = hint()?.let { FeedbackMessage.Plain(it) }

    abstract fun toUiState(): GameUiState
}
