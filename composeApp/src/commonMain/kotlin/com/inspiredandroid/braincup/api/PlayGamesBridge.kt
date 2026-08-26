package com.inspiredandroid.braincup.api

import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.normalsudoku.SudokuDifficulty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class StorePlayerProfile(
    val playerId: String = "",
    val displayName: String,
    val avatarBytes: ByteArray? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StorePlayerProfile) return false
        return playerId == other.playerId &&
            displayName == other.displayName &&
            avatarBytes.contentEquals(other.avatarBytes)
    }

    override fun hashCode(): Int {
        var result = playerId.hashCode()
        result = 31 * result + displayName.hashCode()
        result = 31 * result + (avatarBytes?.contentHashCode() ?: 0)
        return result
    }
}

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

    private val _currentPlayer = MutableStateFlow<StorePlayerProfile?>(null)
    val currentPlayer: StateFlow<StorePlayerProfile?> = _currentPlayer.asStateFlow()

    fun updateCurrentPlayer(profile: StorePlayerProfile?) {
        if (_currentPlayer.value == profile) return
        _currentPlayer.value = profile
    }

    var hasPlayStoreAccount: Boolean = false
    var isGameCenterAccount: Boolean = false
    var onRefreshStoreProfile: (() -> Unit)? = null

    fun bindStorePlayer(playerId: String): Boolean = AccountStore().bindStorePlayer(playerId)

    /** Fired the first time English peg solitaire is finished with the last peg in the center. */
    var onPegSolitairePerfect: (() -> Unit)? = null

    /**
     * Fired when a Learn Math sub-topic certificate is earned, with the LearnUnit id. Each
     * certificate has its own store achievement; see `LearnStoreAchievements` for the id scheme.
     */
    var onLearnCertificate: ((String) -> Unit)? = null

    /**
     * Fired when an IQ test is finished, with the estimated IQ. The platform side unlocks the
     * completion achievement every time and the high-score one only past its threshold, so this
     * stays a single hook rather than one per achievement.
     */
    var onIqTestCompleted: ((Int) -> Unit)? = null

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

    /** Fired after Play Games / Game Center progress was written into local storage. */
    var onStoreProgressRestored: (() -> Unit)? = null
}
