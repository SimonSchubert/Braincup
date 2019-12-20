package com.inspiredandroid.braincup.challenge

import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.games.getName

sealed class ChallengeData()

data class SherlockCalculationChallengeData(
    val title: String,
    val goal: Int,
    val numbers: List<Int>
) :
    ChallengeData() {
    fun getUrl(): String {
        val result =
            UrlController.buildSherlockCalculationChallengeUrl(
                title,
                goal.toString(),
                numbers.joinToString(",")
            )
        return when (result) {
            is ChallengeUrl -> result.url
            else -> ""
        }
    }

    fun getTitle(): String {
        return if (title.isNotEmpty()) {
            title
        } else {
            GameType.SHERLOCK_CALCULATION.getName()
        }
    }
}

data class RiddleChallengeData(
    val title: String,
    val description: String,
    val answers: List<String>
) :
    ChallengeData() {
    fun getUrl(): String {
        return ""
    }

    fun getTitle(): String {
        return if (title.isNotEmpty()) {
            title
        } else {
            GameType.RIDDLE.getName()
        }
    }
}

data class ChallengeDataParseError(val msg: String = "Parsing error") : ChallengeData()