package com.inspiredandroid.braincup.app

import kotlinx.serialization.Serializable

// Navigation routes (serializable)
@Serializable
object MainMenu

@Serializable
data class Instructions(val gameTypeId: String)

@Serializable
data class Playing(val gameTypeId: String)

@Serializable
data class Finish(
    val gameTypeId: String,
    val score: Int,
    val isNewHighscore: Boolean,
    val answeredAllCorrect: Boolean,
    val highscore: Int,
    val xpGained: Int,
    val totalXpAfter: Int,
    val adaptiveStartRoundCredit: Int = 0,
    val isFinalCatalogLevel: Boolean = false,
    /**
     * The block summary for games scored out of hitting a set of targets, so the result and the
     * buttons that act on it are the same screen. -1 when the game has no such summary.
     */
    val targetsFound: Int = -1,
    val targetsTotal: Int = -1,
    val mistakes: Int = -1,
    /**
     * Color Confusion's congruency effect in milliseconds, or [NO_CONGRUENCY_EFFECT] when the run
     * held too few correct trials of either kind to take a median from. The absent value cannot be
     * -1 like the fields above: a real effect can come out negative, and a run that came out that
     * way should say so rather than be hidden.
     */
    val congruencyEffectMs: Int = NO_CONGRUENCY_EFFECT,
)

/** [Finish.congruencyEffectMs] when the game did not measure one. */
const val NO_CONGRUENCY_EFFECT = Int.MIN_VALUE

@Serializable
data class Scoreboard(val gameTypeId: String)

@Serializable
object Achievements

@Serializable
object Settings

@Serializable
object Accounts

@Serializable
object Licenses

@Serializable
object Language

@Serializable
object SessionInterstitial

@Serializable
object SessionComplete

@Serializable
object NormalSudokuMenu

@Serializable
data class NormalSudokuPlay(val puzzleId: String)

@Serializable
object NormalChessMenu

@Serializable
data class NormalChessPlay(val mode: String, val difficulty: String)

@Serializable
object MatchstickRiddlesMenu

@Serializable
data class MatchstickRiddlesPlay(val riddleId: String)

@Serializable
object PegSolitaire

@Serializable
object IqTestIntro

@Serializable
object IqTestPlay

@Serializable
object IqTestResult

@Serializable
object IqTestReview

@Serializable
object LearnMenu

@Serializable
data class LearnTopicDetail(val topicId: String)

@Serializable
data class LearnUnitDetail(val unitId: String)

@Serializable
object LearnShapeGuide

@Serializable
object LearnRulesGuide

@Serializable
data class LearnLessonPlay(val lessonId: String)

@Serializable
data class LearnTest(val unitId: String)

@Serializable
data class LearnCertificate(val unitId: String)
