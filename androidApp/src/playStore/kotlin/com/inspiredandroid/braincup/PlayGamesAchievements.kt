package com.inspiredandroid.braincup

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.activity.ComponentActivity
import com.google.android.gms.common.images.ImageManager
import com.google.android.gms.games.PlayGames
import com.google.android.gms.games.PlayGamesSdk
import com.google.android.gms.games.Player
import com.google.android.gms.games.achievement.Achievement
import com.google.android.gms.games.leaderboard.LeaderboardVariant
import com.inspiredandroid.braincup.api.PlayGamesBridge
import com.inspiredandroid.braincup.api.StorePlayerProfile
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.app.R
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.learn.LearnStoreAchievements
import com.inspiredandroid.braincup.matchstickriddles.MatchstickRiddles
import com.inspiredandroid.braincup.normalsudoku.SudokuDifficulty
import java.io.ByteArrayOutputStream
import java.lang.ref.WeakReference

private const val TAG = "PlayGamesBridge"

private var activityRef: WeakReference<ComponentActivity>? = null

/**
 * Runs [action] with a live activity and the non-blank achievement or leaderboard id [resId] names.
 *
 * Every Play Games call needs both and either can be absent: the activity is a weak reference a
 * rotation can clear, and the id resolves to an empty string in a build with no Play Games project
 * behind it. Both cases are a deliberate no-op rather than an error, which is why they were spelled
 * out at every call site before this.
 */
private inline fun withPlayGamesId(resId: Int?, action: (ComponentActivity, String) -> Unit) {
    val current = activityRef?.get() ?: return
    if (resId == null) return
    val id = current.getString(resId)
    if (id.isBlank()) return
    action(current, id)
}

fun initPlayGames(activity: ComponentActivity) {
    activityRef = WeakReference(activity)
    PlayGamesSdk.initialize(activity)

    PlayGamesBridge.hasPlayStoreAccount = true
    PlayGamesBridge.onRefreshStoreProfile = ::refreshStoreProfile

    val signInClient = PlayGames.getGamesSignInClient(activity)
    signInClient.isAuthenticated.addOnCompleteListener { task ->
        val authed = task.isSuccessful && task.result.isAuthenticated
        if (authed) {
            onPlayGamesAuthenticated(activity)
        } else {
            signInClient.signIn().addOnCompleteListener { signInTask ->
                val signedIn = signInTask.isSuccessful && signInTask.result?.isAuthenticated == true
                if (signedIn) {
                    onPlayGamesAuthenticated(activity)
                }
            }
        }
    }

    PlayGamesBridge.onGoldMedal = fun(gameType: GameType) {
        withPlayGamesId(achievementResIdFor(gameType)) { activity, id ->
            PlayGames.getAchievementsClient(activity).unlock(id)
        }
    }

    PlayGamesBridge.onTotalScore = fun(total: Int) {
        withPlayGamesId(R.string.achievementMindMarathoner) { activity, id ->
            PlayGames.getAchievementsClient(activity).setSteps(id, total.coerceAtMost(MIND_MARATHONER_TARGET))
        }
    }

    PlayGamesBridge.onStreak = fun(streak: Int) {
        if (streak < IRON_STREAK_TARGET) return
        withPlayGamesId(R.string.achievementIronStreak) { activity, id ->
            PlayGames.getAchievementsClient(activity).unlock(id)
        }
    }

    PlayGamesBridge.onPegSolitairePerfect = fun() {
        withPlayGamesId(R.string.achievementPegMaster) { activity, id ->
            PlayGames.getAchievementsClient(activity).unlock(id)
        }
    }

    PlayGamesBridge.onLearnCertificate = fun(unitId: String) {
        withPlayGamesId(learnCertificateResIdFor(unitId)) { activity, id ->
            PlayGames.getAchievementsClient(activity).unlock(id)
        }
    }

    PlayGamesBridge.onIqTestCompleted = fun(iq: Int) {
        val current = activityRef?.get() ?: return
        val client = PlayGames.getAchievementsClient(current)
        val completedId = current.getString(R.string.achievementMeasuredMind)
        if (completedId.isNotBlank()) client.unlock(completedId)
        if (iq < UserStorage.IQ_TEST_HIGH_ACHIEVEMENT_IQ) return
        val highId = current.getString(R.string.achievementPatternSavant)
        if (highId.isNotBlank()) client.unlock(highId)
    }

    PlayGamesBridge.onSudokuTierProgress = fun(difficulty: SudokuDifficulty, solved: Int) {
        withPlayGamesId(sudokuTierAchievementResIdFor(difficulty)) { activity, id ->
            PlayGames.getAchievementsClient(activity)
                .setSteps(id, solved.coerceAtMost(UserStorage.SUDOKU_TIER_TARGET))
        }
    }

    PlayGamesBridge.onMatchstickRiddlesProgress = fun(solved: Int) {
        // Steps toward storeProgressMax (50 in Play Console). In-app unlock is separate; no unlock() here.
        withPlayGamesId(R.string.achievementMatchstickMaster) { activity, id ->
            PlayGames.getAchievementsClient(activity).setSteps(id, solved.coerceAtMost(MatchstickRiddles.count))
        }
    }

    PlayGamesBridge.onSubmitScore = fun(gameType: GameType, score: Int) {
        withPlayGamesId(leaderboardResIdFor(gameType)) { activity, id ->
            PlayGames.getLeaderboardsClient(activity).submitScore(id, score.toLong())
        }
    }

    PlayGamesBridge.onSubmitTotalXp = fun(totalXp: Int) {
        withPlayGamesId(R.string.leaderboardBrainCup) { activity, id ->
            PlayGames.getLeaderboardsClient(activity).submitScore(id, totalXp.toLong())
        }
    }

    PlayGamesBridge.onShowBrainCup = fun() {
        withPlayGamesId(R.string.leaderboardBrainCup, ::ensureSignedInAndLaunch)
    }

    PlayGamesBridge.onShowLeaderboard = fun(gameType: GameType) {
        withPlayGamesId(leaderboardResIdFor(gameType), ::ensureSignedInAndLaunch)
    }
}

private fun refreshStoreProfile() {
    val current = activityRef?.get() ?: return
    loadCurrentPlayer(current, forceReload = true)
}

private fun onPlayGamesAuthenticated(activity: ComponentActivity) {
    loadCurrentPlayer(activity, forceReload = false)
    restoreAchievementsFromPlayGames(activity)
    syncTotalXpWithLeaderboard(activity)
    syncPerGameLeaderboards(activity)
}

private fun loadCurrentPlayer(activity: ComponentActivity, forceReload: Boolean) {
    PlayGames.getPlayersClient(activity)
        .getCurrentPlayer(forceReload)
        .addOnSuccessListener { annotated ->
            val player = annotated.get()
            if (player == null) {
                return@addOnSuccessListener
            }
            publishPlayer(activity, player)
        }
        .addOnFailureListener { e ->
            Log.w(TAG, "getCurrentPlayer failed", e)
        }
}

private fun publishPlayer(activity: ComponentActivity, player: Player) {
    val playerId = player.playerId
    val name = player.displayName.orEmpty()
    if (playerId.isNullOrBlank() || name.isBlank()) return
    val identityChanged = PlayGamesBridge.bindStorePlayer(playerId)
    if (identityChanged) {
        PlayGamesBridge.onStoreProgressRestored?.invoke()
        restoreAchievementsFromPlayGames(activity)
        syncTotalXpWithLeaderboard(activity)
        syncPerGameLeaderboards(activity)
    }
    val existing = PlayGamesBridge.currentPlayer.value
    val keepAvatar = if (existing?.playerId == playerId) existing.avatarBytes else null
    if (existing == null || existing.playerId != playerId || existing.displayName != name || existing.avatarBytes == null) {
        PlayGamesBridge.updateCurrentPlayer(
            StorePlayerProfile(playerId = playerId, displayName = name, avatarBytes = keepAvatar),
        )
    }
    val imageUri = player.hiResImageUri ?: player.iconImageUri
    if (imageUri == null || keepAvatar != null) return
    ImageManager.create(activity).loadImage(
        { _, drawable, isRequested ->
            if (PlayGamesBridge.currentPlayer.value?.playerId != playerId || !isRequested) return@loadImage
            val bytes = drawable?.let { drawableToPngBytes(it) } ?: return@loadImage
            PlayGamesBridge.updateCurrentPlayer(
                StorePlayerProfile(playerId = playerId, displayName = name, avatarBytes = bytes),
            )
        },
        imageUri,
    )
}

private fun drawableToPngBytes(drawable: Drawable): ByteArray? {
    val bitmap = when (drawable) {
        is BitmapDrawable -> drawable.bitmap
        else -> {
            val width = drawable.intrinsicWidth.coerceAtLeast(1)
            val height = drawable.intrinsicHeight.coerceAtLeast(1)
            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).also { bmp ->
                val canvas = Canvas(bmp)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
            }
        }
    }
    return try {
        ByteArrayOutputStream().use { out ->
            if (!bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)) return null
            out.toByteArray()
        }
    } catch (e: Exception) {
        Log.w(TAG, "Failed to encode Play Games avatar", e)
        null
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
    val storage = UserStorage(playSlotProgress = true)
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
    val storage = UserStorage(playSlotProgress = true)
    val leaderboardsClient = PlayGames.getLeaderboardsClient(activity)
    val boards = GameType.entries.mapNotNull { gameType ->
        val resId = leaderboardResIdFor(gameType) ?: return@mapNotNull null
        val leaderboardId = activity.getString(resId)
        if (leaderboardId.isBlank()) null else gameType to leaderboardId
    }
    if (boards.isEmpty()) return
    var remaining = boards.size
    for ((gameType, leaderboardId) in boards) {
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
            remaining -= 1
            if (remaining == 0) {
                PlayGamesBridge.onStoreProgressRestored?.invoke()
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
            val storage = UserStorage(playSlotProgress = true)
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
            val measuredMindId = activity.getString(R.string.achievementMeasuredMind)
            if (measuredMindId.isNotBlank() && measuredMindId in unlockedIds) {
                toRestore.add(UserStorage.Achievements.IQ_TEST_COMPLETED)
            }
            val patternSavantId = activity.getString(R.string.achievementPatternSavant)
            if (patternSavantId.isNotBlank() && patternSavantId in unlockedIds) {
                toRestore.add(UserStorage.Achievements.IQ_TEST_HIGH)
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

            // Certificates the store has but this install does not: the only way a certificate
            // survives a reinstall, since nothing else records one off-device.
            val certifiedUnitIds = mutableSetOf<String>()
            for (unitId in LearnStoreAchievements.certifiedUnitIds) {
                val resId = learnCertificateResIdFor(unitId) ?: continue
                val certificateId = activity.getString(resId)
                if (certificateId.isNotBlank() && certificateId in unlockedIds) {
                    certifiedUnitIds.add(unitId)
                }
            }
            storage.restoreLearnCertificates(certifiedUnitIds)

            val matchstickId = activity.getString(R.string.achievementMatchstickMaster)
            if (matchstickId.isNotBlank()) {
                val steps = incrementalSteps[matchstickId] ?: 0
                if (steps > 0) {
                    storage.restoreMatchstickRiddlesProgressIfHigher(steps)
                }
            }
            PlayGamesBridge.onStoreProgressRestored?.invoke()
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
    GameType.MISSING_OPERATORS -> R.string.achievementOperatorMaster
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
    GameType.N_BACK -> R.string.achievementRollingRecall
    GameType.SIMON_SAYS -> R.string.achievementPatternKeeper
    GameType.PRISM_CLEAR -> R.string.achievementCrystalClarity
    GameType.BULLS_AND_COWS -> R.string.achievementCodeCracker
    GameType.TRIO -> R.string.achievementTripleVision
    GameType.MENTAL_ROTATIONS -> R.string.achievementSpinDoctor
    GameType.MENTAL_FLEX -> R.string.achievementQuickChange
    GameType.RULE_SHIFT -> R.string.achievementRuleBreaker
}

/**
 * Store achievement per Learn Math sub-topic certificate. Unlike [achievementResIdFor] this `when`
 * is not exhaustive, so a new sub-topic will not fail the build here; `LearnStoreAchievementsTest`
 * is what catches it.
 */
private fun learnCertificateResIdFor(unitId: String): Int? = when (unitId) {
    "algebra-expressions" -> R.string.achievementCertAlgebraExpressions
    "algebra-linear-equations" -> R.string.achievementCertAlgebraLinearEquations
    "algebra-straight-line-graphs" -> R.string.achievementCertAlgebraStraightLineGraphs
    "algebra-inequalities" -> R.string.achievementCertAlgebraInequalities
    "algebra-simultaneous-equations" -> R.string.achievementCertAlgebraSimultaneousEquations
    "algebra-quadratics" -> R.string.achievementCertAlgebraQuadratics
    "algebra-powers-and-roots" -> R.string.achievementCertAlgebraPowersAndRoots
    "arithmetic-counting"        -> R.string.achievementCertArithmeticCounting
    "arithmetic-multiplication"  -> R.string.achievementCertArithmeticMultiplication
    "arithmetic-fractions"       -> R.string.achievementCertArithmeticFractions
    "arithmetic-decimals"        -> R.string.achievementCertArithmeticDecimals
    "arithmetic-negatives"       -> R.string.achievementCertArithmeticNegatives
    "arithmetic-ratio"           -> R.string.achievementCertArithmeticRatio
    "arithmetic-percent"         -> R.string.achievementCertArithmeticPercent
    "arithmetic-standard-form"   -> R.string.achievementCertArithmeticStandardForm
    "arithmetic-surds"           -> R.string.achievementCertArithmeticSurds
    "arithmetic-bounds"          -> R.string.achievementCertArithmeticBounds
    "geometry-flat-shapes"       -> R.string.achievementCertGeometryFlatShapes
    "geometry-solid-shapes"      -> R.string.achievementCertGeometrySolidShapes
    "geometry-angles"            -> R.string.achievementCertGeometryAngles
    "geometry-quadrilaterals"    -> R.string.achievementCertGeometryQuadrilaterals
    "geometry-symmetry"          -> R.string.achievementCertGeometrySymmetry
    "geometry-perimeter-and-area"-> R.string.achievementCertGeometryPerimeterAndArea
    "geometry-pythagoras"        -> R.string.achievementCertGeometryPythagoras
    "geometry-circles"           -> R.string.achievementCertGeometryCircles
    "geometry-volume"            -> R.string.achievementCertGeometryVolume
    "geometry-similarity"        -> R.string.achievementCertGeometrySimilarity
    "geometry-transformations"   -> R.string.achievementCertGeometryTransformations
    "geometry-circle-theorems"   -> R.string.achievementCertGeometryCircleTheorems
    else -> null
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
