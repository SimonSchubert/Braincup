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
}
