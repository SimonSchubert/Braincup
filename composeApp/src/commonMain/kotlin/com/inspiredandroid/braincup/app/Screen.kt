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
)

@Serializable
data class Scoreboard(val gameTypeId: String)

@Serializable
object Achievements
