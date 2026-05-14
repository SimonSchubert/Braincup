package com.inspiredandroid.braincup.api

import com.inspiredandroid.braincup.games.GameType

/**
 * Platform-agnostic hook fired when a game run earns a Gold-tier result.
 * The playStore Android flavor wires this to Play Games achievement unlocks;
 * other platforms/flavors leave it null (no-op).
 *
 * Play Games unlocks are idempotent, so this fires every gold-tier run.
 */
object PlayGamesBridge {
    var onGoldMedal: ((GameType) -> Unit)? = null
    var onTotalScore: ((Int) -> Unit)? = null
    var onStreak: ((Int) -> Unit)? = null

    /** Submit a final score to the per-game leaderboard. No-op if the game has none. */
    var onSubmitScore: ((GameType, Int) -> Unit)? = null

    /** Launch the per-game leaderboard UI. No-op if the game has none. */
    var onShowLeaderboard: ((GameType) -> Unit)? = null

    /** Submit cumulative XP to the cross-game Brain Cup leaderboard. */
    var onSubmitTotalXp: ((Int) -> Unit)? = null

    /** Launch the Brain Cup (cross-game XP) leaderboard UI. */
    var onShowBrainCup: (() -> Unit)? = null
}
