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
)

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
data class LearnGradeDetail(val levelId: String)

@Serializable
data class LearnUnitDetail(val unitId: String)

@Serializable
data class LearnLessonPlay(val lessonId: String)

@Serializable
data class LearnTest(val unitId: String)

@Serializable
data class LearnCertificate(val unitId: String)
