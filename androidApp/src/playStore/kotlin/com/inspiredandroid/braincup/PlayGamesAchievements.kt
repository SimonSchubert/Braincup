package com.inspiredandroid.braincup

import android.util.Log
import androidx.activity.ComponentActivity
import com.google.android.gms.games.PlayGames
import com.google.android.gms.games.PlayGamesSdk
import com.google.android.gms.games.achievement.Achievement
import com.google.android.gms.games.leaderboard.LeaderboardVariant
import com.inspiredandroid.braincup.api.PlayGamesBridge
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.app.R
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.matchstickriddles.MatchstickRiddles
import com.inspiredandroid.braincup.normalsudoku.SudokuDifficulty
import java.lang.ref.WeakReference

private const val TAG = "PlayGamesBridge"

private var activityRef: WeakReference<ComponentActivity>? = null

fun initPlayGames(activity: ComponentActivity) {
    activityRef = WeakReference(activity)
    PlayGamesSdk.initialize(activity)

    val signInClient = PlayGames.getGamesSignInClient(activity)
    signInClient.isAuthenticated.addOnCompleteListener { task ->
        val authed = task.isSuccessful && task.result.isAuthenticated
        if (authed) {
            restoreAchievementsFromPlayGames(activity)
            syncTotalXpWithLeaderboard(activity)
            syncPerGameLeaderboards(activity)
        } else {
            signInClient.signIn().addOnCompleteListener { signInTask ->
                val signedIn = signInTask.isSuccessful && signInTask.result?.isAuthenticated == true
                if (signedIn) {
                    restoreAchievementsFromPlayGames(activity)
                    syncTotalXpWithLeaderboard(activity)
                    syncPerGameLeaderboards(activity)
                }
            }
        }
    }

    PlayGamesBridge.onGoldMedal = fun(gameType: GameType) {
        val current = activityRef?.get() ?: return
        val resId = achievementResIdFor(gameType) ?: return
        val id = current.getString(resId)
        if (id.isBlank()) return
        PlayGames.getAchievementsClient(current).unlock(id)
    }

    PlayGamesBridge.onTotalScore = fun(total: Int) {
        val current = activityRef?.get() ?: return
        val id = current.getString(R.string.achievementMindMarathoner)
        if (id.isBlank()) return
        PlayGames.getAchievementsClient(current).setSteps(id, total.coerceAtMost(MIND_MARATHONER_TARGET))
    }

    PlayGamesBridge.onStreak = fun(streak: Int) {
        if (streak < IRON_STREAK_TARGET) return
        val current = activityRef?.get() ?: return
        val id = current.getString(R.string.achievementIronStreak)
        if (id.isBlank()) return
        PlayGames.getAchievementsClient(current).unlock(id)
    }

    PlayGamesBridge.onPegSolitairePerfect = fun() {
        val current = activityRef?.get() ?: return
        val id = current.getString(R.string.achievementPegMaster)
        if (id.isBlank()) return
        PlayGames.getAchievementsClient(current).unlock(id)
    }

    PlayGamesBridge.onSudokuTierProgress = fun(difficulty: SudokuDifficulty, solved: Int) {
        val current = activityRef?.get() ?: return
        val id = current.getString(sudokuTierAchievementResIdFor(difficulty))
        if (id.isBlank()) return
        PlayGames.getAchievementsClient(current).setSteps(id, solved.coerceAtMost(UserStorage.SUDOKU_TIER_TARGET))
    }

    PlayGamesBridge.onMatchstickRiddlesProgress = fun(solved: Int) {
        val current = activityRef?.get() ?: return
        val id = current.getString(R.string.achievementMatchstickMaster)
        if (id.isBlank()) return
        // Steps toward storeProgressMax (50 in Play Console). In-app unlock is separate; no unlock() here.
        PlayGames.getAchievementsClient(current).setSteps(id, solved.coerceAtMost(MatchstickRiddles.count))
    }

    PlayGamesBridge.onSubmitScore = fun(gameType: GameType, score: Int) {
        val current = activityRef?.get() ?: return
        val resId = leaderboardResIdFor(gameType) ?: return
        val id = current.getString(resId)
        if (id.isBlank()) return
        PlayGames.getLeaderboardsClient(current).submitScore(id, score.toLong())
    }

    PlayGamesBridge.onSubmitTotalXp = fun(totalXp: Int) {
        val current = activityRef?.get() ?: return
        val id = current.getString(R.string.leaderboardBrainCup)
        if (id.isBlank()) return
        PlayGames.getLeaderboardsClient(current).submitScore(id, totalXp.toLong())
    }

    PlayGamesBridge.onShowBrainCup = fun() {
        val current = activityRef?.get() ?: return
        val id = current.getString(R.string.leaderboardBrainCup)
        if (id.isBlank()) return
        ensureSignedInAndLaunch(current, id)
    }

    PlayGamesBridge.onShowLeaderboard = fun(gameType: GameType) {
        val current = activityRef?.get() ?: return
        val resId = leaderboardResIdFor(gameType) ?: return
        val id = current.getString(resId)
        if (id.isBlank()) return
        ensureSignedInAndLaunch(current, id)
    }
}

private fun ensureSignedInAndLaunch(activity: ComponentActivity, id: String) {
    PlayGames.getGamesSignInClient(activity).isAuthenticated
        .addOnCompleteListener { authTask ->
            val signedIn = authTask.isSuccessful && authTask.result?.isAuthenticated == true
            if (signedIn) {
                launchLeaderboard(activity, id)
            } else {
                PlayGames.getGamesSignInClient(activity).signIn()
                    .addOnSuccessListener { launchLeaderboard(activity, id) }
                    .addOnFailureListener { e -> Log.w(TAG, "Play Games sign-in failed", e) }
            }
        }
}

private fun launchLeaderboard(activity: ComponentActivity, id: String) {
    PlayGames.getLeaderboardsClient(activity).getLeaderboardIntent(id)
        .addOnSuccessListener { intent ->
            if (intent == null) return@addOnSuccessListener
            try {
                // startActivityForResult (not startActivity): on Android 16 with intent
                // redirect hardening, the cross-package leaderboard intent is silently
                // dropped when launched without a result channel.
                @Suppress("DEPRECATION")
                activity.startActivityForResult(intent, LEADERBOARD_REQUEST_CODE)
            } catch (e: Exception) {
                Log.w(TAG, "startActivityForResult for leaderboard $id failed", e)
            }
        }
        .addOnFailureListener { e -> Log.w(TAG, "getLeaderboardIntent($id) failed", e) }
}

private const val MIND_MARATHONER_TARGET = 10_000
private const val IRON_STREAK_TARGET = 30
private const val LEADERBOARD_REQUEST_CODE = 9001

/**
 * Two-way sync of cumulative XP with the Brain Cup leaderboard:
 *  - If the player's remote score is higher than local (fresh install, reinstall, multi-device),
 *    restore local XP up to the remote value.
 *  - Submit the (possibly restored) local XP back. Play Games keeps the max, so a smaller submit
 *    can never overwrite a larger remote score.
 */
private fun syncTotalXpWithLeaderboard(activity: ComponentActivity) {
    val id = activity.getString(R.string.leaderboardBrainCup)
    if (id.isBlank()) return
    val storage = UserStorage()
    val leaderboardsClient = PlayGames.getLeaderboardsClient(activity)
    leaderboardsClient.loadCurrentPlayerLeaderboardScore(
        id,
        LeaderboardVariant.TIME_SPAN_ALL_TIME,
        LeaderboardVariant.COLLECTION_PUBLIC,
    ).addOnCompleteListener { task ->
        val remoteXp = if (task.isSuccessful) {
            task.result?.get()?.rawScore?.toInt() ?: 0
        } else {
            Log.w(TAG, "loadCurrentPlayerLeaderboardScore failed", task.exception)
            0
        }
        if (remoteXp > 0 && storage.restoreTotalXpIfHigher(remoteXp)) {
            PlayGamesBridge.onTotalXpRestored?.invoke(remoteXp)
        }
        val xp = storage.getTotalXp()
        if (xp > 0) {
            leaderboardsClient.submitScore(id, xp.toLong())
        }
    }
}

/**
 * Restore per-game high scores from their Play Games leaderboards. Mirrors
 * [syncTotalXpWithLeaderboard] but for games with a dedicated leaderboard (currently FLAGS).
 * Play Games keeps the max, so a smaller resubmit can never overwrite a larger remote score.
 */
private fun syncPerGameLeaderboards(activity: ComponentActivity) {
    val storage = UserStorage()
    val leaderboardsClient = PlayGames.getLeaderboardsClient(activity)
    for (gameType in GameType.entries) {
        val resId = leaderboardResIdFor(gameType) ?: continue
        val leaderboardId = activity.getString(resId)
        if (leaderboardId.isBlank()) continue
        leaderboardsClient.loadCurrentPlayerLeaderboardScore(
            leaderboardId,
            LeaderboardVariant.TIME_SPAN_ALL_TIME,
            LeaderboardVariant.COLLECTION_PUBLIC,
        ).addOnCompleteListener { task ->
            val remoteScore = if (task.isSuccessful) {
                task.result?.get()?.rawScore?.toInt() ?: 0
            } else {
                Log.w(TAG, "loadCurrentPlayerLeaderboardScore($leaderboardId) failed", task.exception)
                0
            }
            if (remoteScore > 0) {
                storage.restoreHighScoreIfHigher(gameType.id, remoteScore)
            }
        }
    }
}

private fun restoreAchievementsFromPlayGames(activity: ComponentActivity) {
    PlayGames.getAchievementsClient(activity).load(true).addOnSuccessListener { annotatedData ->
        val buffer = annotatedData.get() ?: return@addOnSuccessListener
        try {
            val unlockedIds = mutableSetOf<String>()
            // Current step count of incremental achievements (e.g. the Sudoku tiers), so we can
            // restore partial progress, not just the all-or-nothing unlocked state.
            val incrementalSteps = mutableMapOf<String, Int>()
            for (ach in buffer) {
                if (ach.state == Achievement.STATE_UNLOCKED) {
                    unlockedIds.add(ach.achievementId)
                }
                if (ach.type == Achievement.TYPE_INCREMENTAL) {
                    incrementalSteps[ach.achievementId] = ach.currentSteps
                }
            }
            val storage = UserStorage()
            val toRestore = mutableSetOf<UserStorage.Achievements>()
            for (gameType in GameType.entries) {
                val resId = achievementResIdFor(gameType) ?: continue
                if (activity.getString(resId) in unlockedIds) {
                    UserStorage.Achievements.forGameGold(gameType)?.let { toRestore.add(it) }
                }
            }
            val marathonerId = activity.getString(R.string.achievementMindMarathoner)
            if (marathonerId.isNotBlank() && marathonerId in unlockedIds) {
                toRestore.add(UserStorage.Achievements.TOTAL_SCORE_10K)
            }
            val streakId = activity.getString(R.string.achievementIronStreak)
            if (streakId.isNotBlank() && streakId in unlockedIds) {
                toRestore.add(UserStorage.Achievements.STREAK_30)
            }
            val pegMasterId = activity.getString(R.string.achievementPegMaster)
            if (pegMasterId.isNotBlank() && pegMasterId in unlockedIds) {
                toRestore.add(UserStorage.Achievements.PEG_SOLITAIRE_PERFECT)
            }
            storage.restoreUnlockedAchievements(toRestore)

            for (difficulty in SudokuDifficulty.entries) {
                val tierId = activity.getString(sudokuTierAchievementResIdFor(difficulty))
                if (tierId.isBlank()) continue
                val steps = incrementalSteps[tierId] ?: continue
                if (steps > 0) {
                    storage.restoreSudokuTierProgressIfHigher(difficulty, steps)
                }
            }

            val matchstickId = activity.getString(R.string.achievementMatchstickMaster)
            if (matchstickId.isNotBlank()) {
                val steps = incrementalSteps[matchstickId] ?: 0
                if (steps > 0) {
                    storage.restoreMatchstickRiddlesProgressIfHigher(steps)
                }
            }
        } finally {
            buffer.release()
        }
    }
}

private fun achievementResIdFor(gameType: GameType): Int? = when (gameType) {
    GameType.MINI_SUDOKU -> R.string.achievementSudokuSage
    GameType.MINI_CHESS -> R.string.achievementEndgameVirtuoso
    GameType.SOLO_CHESS -> R.string.achievementLastPieceStanding
    GameType.LIGHTS_OUT -> R.string.achievementTotalBlackout
    GameType.SLIDING_PUZZLE -> R.string.achievementSmoothOperator
    GameType.TOWER_OF_HANOI -> R.string.achievementTowerMaster
    GameType.SHIKAKU -> R.string.achievementRectangleMaster
    GameType.NURIKABE -> R.string.achievementWallBuilder
    GameType.CAT_QUEENS -> R.string.achievementCatHerder
    GameType.KNOT -> R.string.achievementUntangler
    GameType.PATH_FINDER -> R.string.achievementTrailblazer
    GameType.ANOMALY_PUZZLE -> R.string.achievementOddOneSpotted
    GameType.COLORED_SHAPES -> R.string.achievementShapeShifter
    GameType.SHERLOCK_CALCULATION -> R.string.achievementElementaryMyDear
    GameType.MENTAL_CALCULATION -> R.string.achievementHumanCalculator
    GameType.BUBBLE_SUM -> R.string.achievementBubbleAccountant
    GameType.QUICK_SUM -> R.string.achievementFlashAbacus
    GameType.CHAIN_CALCULATION -> R.string.achievementUnbrokenChain
    GameType.FRACTION_CALCULATION -> R.string.achievementFractionBoss
    GameType.VALUE_COMPARISON -> R.string.achievementGreaterThanTheRest
    GameType.GHOST_GRID -> R.string.achievementGhostWhisperer
    GameType.VISUAL_MEMORY -> R.string.achievementPhotographicMind
    GameType.ORBIT_TRACKER -> R.string.achievementAstronomer
    GameType.PATTERN_SEQUENCE -> R.string.achievementPatternProphet
    GameType.COLOR_CONFUSION -> R.string.achievementTrueColors
    GameType.FLASH_CROWD -> R.string.achievementCrowdCounter
    GameType.SCHULTE_TABLE -> R.string.achievementLightningGaze
    GameType.DIGIT_MEMORY -> R.string.achievementDigitSavant
    GameType.SPOT_THE_NEW -> R.string.achievementFreshEyes
    GameType.FLAGS -> R.string.achievementFlagBearer
    GameType.WORDLE -> R.string.achievementWordsmith
    GameType.N_BACK -> null // GPGS achievement ID pending console setup
    GameType.SIMON_SAYS -> null // GPGS achievement ID pending console setup
}

private fun sudokuTierAchievementResIdFor(difficulty: SudokuDifficulty): Int = when (difficulty) {
    SudokuDifficulty.BEGINNER -> R.string.achievementSudokuBeginner
    SudokuDifficulty.EASY -> R.string.achievementSudokuEasy
    SudokuDifficulty.MEDIUM -> R.string.achievementSudokuMedium
    SudokuDifficulty.HARD -> R.string.achievementSudokuHard
    SudokuDifficulty.EXPERT -> R.string.achievementSudokuExpert
}

private fun leaderboardResIdFor(gameType: GameType): Int? = when (gameType) {
    GameType.FLAGS -> R.string.leaderboardFlagMaster
    else -> null
}
