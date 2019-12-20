package com.inspiredandroid.braincup.challenge

import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.games.getId
import com.inspiredandroid.braincup.splitToIntList
import com.inspiredandroid.braincup.splitToStringList

sealed class ChallengeUrlResult

data class ChallengeUrlError(val errorMessage: String) : ChallengeUrlResult()

data class ChallengeUrl(val url: String) : ChallengeUrlResult()

object UrlController {

    fun buildSherlockCalculationChallengeUrl(
        title: String,
        goalInput: String,
        numbersInput: String
    ): ChallengeUrlResult {
        val goal = try {
            goalInput.trim().toInt()
        } catch (ignore: Exception) {
            return ChallengeUrlError("Goal is not properly formatted.")
        }

        val numbers = try {
            numbersInput.splitToIntList()
        } catch (ignore: Exception) {
            return ChallengeUrlError("Numbers are not properly formatted. Separation by comma and space are allowed.")
        }

        if (numbers.size < 2) {
            return ChallengeUrlError("Add more numbers.")
        }

        return ChallengeUrl(
            "https://braincup.app/challenge.html?game=${GameType.SHERLOCK_CALCULATION.getId()}&type=0&title=$title&goal=$goal&numbers=${numbers.joinToString(
                ","
            )}"
        )
    }

    fun buildRiddleChallengeUrl(
        title: String,
        description: String,
        answersInput: String
    ): ChallengeUrlResult {

        if (description.trim().isEmpty()) {
            return ChallengeUrlError("Description is missing.")
        }

        val answers = try {
            answersInput.splitToStringList()
        } catch (ignore: Exception) {
            return ChallengeUrlError("Numbers are not properly formatted. Separation by comma and space are allowed.")
        }

        if (answers.isEmpty()) {
            return ChallengeUrlError("Add more answers.")
        }

        return ChallengeUrl(
            "https://braincup.app/challenge.html?game=${GameType.RIDDLE.getId()}&type=0&title=$title&description=$description&answers=${answers.joinToString(
                ","
            )}"
        )
    }
}