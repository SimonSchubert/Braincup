package com.inspiredandroid.braincup.challenge

sealed class ChallengeData

data class SherlockCalculationChallengeData(val goal: Int, val numbers: List<Int>) :
    ChallengeData() {
    fun getUrl(): String {
        val result =
            UrlController.buildSherlockCalculationChallengeUrl(
                goal.toString(),
                numbers.joinToString(",")
            )
        return when (result) {
            is ChallengeUrl -> result.url
            else -> ""
        }
    }
}

data class ChallengeDataParseError(val msg: String = "Parsing error") : ChallengeData()