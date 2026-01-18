package com.inspiredandroid.braincup.app

import com.inspiredandroid.braincup.games.Game
import com.inspiredandroid.braincup.games.GameType

/**
 * Represents the current state of an active game.
 * This is managed by GameController and not passed through navigation.
 */
sealed class GameState {
    data object Idle : GameState()

    data class Active(
        val gameType: GameType,
        val game: Game,
        val stateVersion: Long = 0,
    ) : GameState()

    data class Feedback(
        val gameType: GameType,
        val game: Game,
        val isCorrect: Boolean,
        val message: String?,
    ) : GameState()
}
