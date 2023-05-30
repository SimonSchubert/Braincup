package com.inspiredandroid.braincup.challenge

import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.games.getId
import com.inspiredandroid.braincup.splitToIntList
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

sealed class ChallengeUrlResult

data class ChallengeUrlError(val errorMessage: String) : ChallengeUrlResult()

data class ChallengeUrl(val url: String) : ChallengeUrlResult()

@OptIn(ExperimentalEncodingApi::class)
object UrlBuilder {

    fun buildSherlockCalculationChallengeUrl(
        title: String,
        secret: String,
        goalInput: String,
        numbersInput: String
    ): ChallengeUrlResult {
        val goal = try {
            if (goalInput.isEmpty()) {
                return ChallengeUrlError("Goal is empty.")
            } else {
                goalInput.trim().toInt()
            }
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

        val map = getBaseMap(title, secret, GameType.SHERLOCK_CALCULATION)
        map["goal"] = JsonPrimitive(goal)
        map["numbers"] = JsonPrimitive(
            numbers.joinToString(
                ","
            )
        )
        val json = Json.encodeToString(map)

        return ChallengeUrl(
            "https://braincup.app/challenge?data=${Base64.encode(json.toByteArray())}"
        )
    }

    private fun getBaseMap(
        title: String,
        secret: String,
        gameType: GameType
    ): MutableMap<String, JsonElement> {
        val map = mutableMapOf<String, JsonElement>()
        if (title.isNotEmpty()) {
            map["title"] = JsonPrimitive(title)
        }
        if (secret.isNotEmpty()) {
            map["secret"] = JsonPrimitive(secret)
        }
        map["game"] = JsonPrimitive(gameType.getId())
        return map
    }

    fun buildRiddleChallengeUrl(
        title: String,
        secret: String,
        description: String,
        answersInput: String
    ): ChallengeUrlResult {
        if (description.trim().isEmpty()) {
            return ChallengeUrlError("Description is missing.")
        }

        val answers = try {
            answersInput.split(",")
        } catch (ignore: Exception) {
            return ChallengeUrlError("Numbers are not properly formatted. Separation by comma and space are allowed.")
        }

        if (answers.isEmpty()) {
            return ChallengeUrlError("Add more answers.")
        }

        val map = getBaseMap(title, secret, GameType.RIDDLE)
        map["description"] = JsonPrimitive(description)
        map["answers"] = JsonPrimitive(
            answers.joinToString(separator = ",") {
                it.trim()
            }
        )
        val json = Json.encodeToString(map)

        return ChallengeUrl(
            "https://braincup.app/challenge?data=${Base64.encode(json.toByteArray())}"
        )
    }
}