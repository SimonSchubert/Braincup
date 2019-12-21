package com.inspiredandroid.braincup.challenge

import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.games.getId
import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.Shape
import com.inspiredandroid.braincup.splitToIntList
import com.inspiredandroid.braincup.splitToStringList
import io.ktor.util.InternalAPI
import io.ktor.util.encodeBase64
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.*
import kotlinx.serialization.stringify

sealed class ChallengeUrlResult

data class ChallengeUrlError(val errorMessage: String) : ChallengeUrlResult()

data class ChallengeUrl(val url: String) : ChallengeUrlResult()

@UseExperimental(ImplicitReflectionSerializer::class)
object UrlController {

    @UseExperimental(InternalAPI::class)
    fun buildSherlockCalculationChallengeUrl(
        title: String,
        secret: String,
        goalInput: String,
        numbersInput: String
    ): ChallengeUrlResult {
        val goal = try {
            if(goalInput.isEmpty()) {
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

        val map = mutableMapOf<String, JsonElement>()
        if(title.isNotEmpty()) {
            map["title"] = JsonPrimitive(title)
        }
        if(secret.isNotEmpty()) {
            map["secret"] = JsonPrimitive(secret)
        }
        map["game"] = JsonPrimitive(GameType.SHERLOCK_CALCULATION.getId())
        map["goal"] = JsonPrimitive(goal)
        map["numbers"] = JsonPrimitive(numbers.joinToString(
            ","
        ))
        val json = Json.stringify(map) // .plain.toJson(map).content

        return ChallengeUrl(
            "https://braincup.app/challenge?data=${json.encodeBase64()}"
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