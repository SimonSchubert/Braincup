package com.inspiredandroid.braincup.challenge

import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.games.getId

sealed class ChallengeUrlResult

data class ChallengeUrlError(val errorMessage: String) : ChallengeUrlResult()

data class ChallengeUrl(val url: String) : ChallengeUrlResult()

object UrlController {

    fun buildSherlockCalculationChallengeUrl(
        goalInput: String,
        numbersInput: String
    ): ChallengeUrlResult {
        val goal = try {
            goalInput.trim().toInt()
        } catch (ignore: Exception) {
            return ChallengeUrlError("Goal is not properly formatted.")
        }

        val numbers = try {
            numbersInput.trim().split(" ").joinToString(separator = ",").split(",")
                .mapNotNull {
                    try {
                        it.trim().toInt()
                    } catch (ignore: Exception) {
                        null
                    }
                }
        } catch (ignore: Exception) {
            return ChallengeUrlError("Numbers are not properly formatted. Separation by comma and space are allowed.")
        }

        if (numbers.size < 2) {
            return ChallengeUrlError("Add more numbers.")
        }

        return ChallengeUrl(
            "https://braincup.app/challenge.html?game=${GameType.SHERLOCK_CALCULATION.getId()}&type=0&goal=$goal&numbers=${numbers.joinToString(
                ","
            )}"
        )
    }
}