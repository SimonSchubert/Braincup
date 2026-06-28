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
)

@Serializable
data class Scoreboard(val gameTypeId: String)

@Serializable
object Achievements

@Serializable
object Settings

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
