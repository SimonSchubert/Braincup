package com.inspiredandroid.braincup

import android.util.Log
import androidx.activity.ComponentActivity
import com.google.android.gms.games.PlayGames
import com.google.android.gms.games.PlayGamesSdk
import com.google.android.gms.games.achievement.Achievement
import com.inspiredandroid.braincup.api.PlayGamesBridge
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.app.R
import com.inspiredandroid.braincup.games.GameType
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
        } else {
            signInClient.signIn().addOnCompleteListener { signInTask ->
                val signedIn = signInTask.isSuccessful && signInTask.result?.isAuthenticated == true
                if (signedIn) {
                    restoreAchievementsFromPlayGames(activity)
                }
            }
        }
    }

    PlayGamesBridge.onGoldMedal = fun(gameType: GameType) {
        val current = activityRef?.get() ?: return
        val resId = achievementResIdFor(gameType) ?: return
        PlayGames.getAchievementsClient(current).unlock(current.getString(resId))
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

    PlayGamesBridge.onSubmitScore = fun(gameType: GameType, score: Int) {
        val current = activityRef?.get() ?: return
        val resId = leaderboardResIdFor(gameType) ?: return
        val id = current.getString(resId)
        if (id.isBlank()) return
        PlayGames.getLeaderboardsClient(current).submitScore(id, score.toLong())
    }

    PlayGamesBridge.onShowLeaderboard = fun(gameType: GameType) {
        val current = activityRef?.get() ?: run {
            Log.w(TAG, "showLeaderboard: activity ref is null")
            return
        }
        val resId = leaderboardResIdFor(gameType) ?: run {
            Log.w(TAG, "showLeaderboard: no leaderboard mapping for $gameType")
            return
        }
        val id = current.getString(resId)
        if (id.isBlank()) {
            Log.w(TAG, "showLeaderboard: leaderboard id resource is blank")
            return
        }
        // Re-check sign-in: on FOSS-less builds the initial sign-in can still fail
        // (no Play Games account on the device, parental controls, etc.), and the
        // leaderboard intent only resolves when signed in.
        PlayGames.getGamesSignInClient(current).isAuthenticated
            .addOnCompleteListener { authTask ->
                val signedIn = authTask.isSuccessful && authTask.result?.isAuthenticated == true
                if (!signedIn) {
                    Log.w(TAG, "showLeaderboard: not signed in to Play Games; attempting sign-in")
                    PlayGames.getGamesSignInClient(current).signIn()
                        .addOnSuccessListener { launchLeaderboard(current, id) }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "showLeaderboard: sign-in failed", e)
                        }
                } else {
                    launchLeaderboard(current, id)
                }
            }
    }
}

private fun launchLeaderboard(activity: ComponentActivity, id: String) {
    PlayGames.getLeaderboardsClient(activity).getLeaderboardIntent(id)
        .addOnSuccessListener { intent ->
            try {
                activity.startActivity(intent)
            } catch (e: Exception) {
                Log.e(TAG, "startActivity for leaderboard $id failed", e)
            }
        }
        .addOnFailureListener { e ->
            Log.e(TAG, "getLeaderboardIntent($id) failed", e)
        }
}

private const val MIND_MARATHONER_TARGET = 10_000
private const val IRON_STREAK_TARGET = 30

private fun restoreAchievementsFromPlayGames(activity: ComponentActivity) {
    PlayGames.getAchievementsClient(activity).load(true).addOnSuccessListener { annotatedData ->
        val buffer = annotatedData.get() ?: return@addOnSuccessListener
        try {
            val unlockedIds = mutableSetOf<String>()
            for (ach in buffer) {
                if (ach.state == Achievement.STATE_UNLOCKED) {
                    unlockedIds.add(ach.achievementId)
                }
            }
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
            UserStorage().restoreUnlockedAchievements(toRestore)
        } finally {
            buffer.release()
        }
    }
}

private fun achievementResIdFor(gameType: GameType): Int? = when (gameType) {
    GameType.MINI_SUDOKU -> R.string.achievementSudokuSage
    GameType.MINI_CHESS -> R.string.achievementEndgameVirtuoso
    GameType.LIGHTS_OUT -> R.string.achievementTotalBlackout
    GameType.SLIDING_PUZZLE -> R.string.achievementSmoothOperator
    GameType.PATH_FINDER -> R.string.achievementTrailblazer
    GameType.ANOMALY_PUZZLE -> R.string.achievementOddOneSpotted
    GameType.COLORED_SHAPES -> R.string.achievementShapeShifter
    GameType.SHERLOCK_CALCULATION -> R.string.achievementElementaryMyDear
    GameType.MENTAL_CALCULATION -> R.string.achievementHumanCalculator
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
    GameType.FLAGS -> null
}

private fun leaderboardResIdFor(gameType: GameType): Int? = when (gameType) {
    GameType.FLAGS -> R.string.leaderboardFlagMaster
    else -> null
}
