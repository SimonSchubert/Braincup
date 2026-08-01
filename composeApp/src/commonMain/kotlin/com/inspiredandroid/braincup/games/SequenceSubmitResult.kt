package com.inspiredandroid.braincup.games

/**
 * Shared submit outcome for sequence / multi-select survival games that advance mid-round
 * (Ghost Grid, Simon Says, Orbit Tracker).
 *
 * - [CorrectContinue]: correct input; stay in the same round for more taps/selections
 * - [RoundComplete]: correct input that finished the round
 * - [Wrong]: incorrect input; the game is over
 */
sealed interface SequenceSubmitResult {
    data object CorrectContinue : SequenceSubmitResult
    data object RoundComplete : SequenceSubmitResult
    data object Wrong : SequenceSubmitResult
}
