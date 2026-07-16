package com.inspiredandroid.braincup.api

import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.normalsudoku.SudokuDifficulty

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

    /** Fired the first time English peg solitaire is finished with the last peg in the center. */
    var onPegSolitairePerfect: (() -> Unit)? = null

    /**
     * Report the number of solved puzzles in a Normal Sudoku difficulty tier (0..10) to the
     * tier's incremental store achievement. Wired to `setSteps` (Play Games) /
     * `percentComplete` (Game Center); null off-store so completion stays a no-op.
     */
    var onSudokuTierProgress: ((SudokuDifficulty, Int) -> Unit)? = null

    /**
     * Report the number of solved Matchstick Riddles to the store incremental achievement.
     * Wired to `setSteps` (Play Games) / `percentComplete` (Game Center); null off-store so it stays
     * a no-op. Progress is reported toward [MatchstickRiddles.storeProgressMax]; in-app completion
     * is handled separately in [UserStorage].
     */
    var onMatchstickRiddlesProgress: ((Int) -> Unit)? = null

    /** Submit a final score to the per-game leaderboard. No-op if the game has none. */
    var onSubmitScore: ((GameType, Int) -> Unit)? = null

    /** Launch the per-game leaderboard UI. No-op if the game has none. */
    var onShowLeaderboard: ((GameType) -> Unit)? = null

    /** Submit cumulative XP to the cross-game Brain Cup leaderboard. */
    var onSubmitTotalXp: ((Int) -> Unit)? = null

    /** Launch the Brain Cup (cross-game XP) leaderboard UI. */
    var onShowBrainCup: (() -> Unit)? = null

    /** Fired when local XP was raised to match a higher value from the Brain Cup leaderboard. */
    var onTotalXpRestored: ((Int) -> Unit)? = null
}
