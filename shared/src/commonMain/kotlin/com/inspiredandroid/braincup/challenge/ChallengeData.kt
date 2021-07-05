package com.inspiredandroid.braincup.challenge

import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.games.getId
import com.inspiredandroid.braincup.games.getName
import io.ktor.util.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

sealed class ChallengeData(
    private val challengeTitle: String? = null,
    val challengeSecret: String,
    val gameType: GameType
) {
    companion object {
        @OptIn(InternalAPI::class)
        fun parse(url: String, data: String): ChallengeData {
            val json: JsonObject = Json.parseToJsonElement(data.decodeBase64String()) as? JsonObject ?: throw Exception("Expected JsonObject")
            val gameType = json["game"]?.jsonPrimitive?.content
            val title = json["title"]?.jsonPrimitive?.content ?: ""
            val secret = json["secret"]?.jsonPrimitive?.content ?: ""

            return when (gameType) {
                GameType.SHERLOCK_CALCULATION.getId() -> {
                    return try {
                        val goal = json.getValue("goal").jsonPrimitive.content.toInt()
                        val numbers = json.getValue("numbers").jsonPrimitive.content.split(",").map { it.toInt() }
                        SherlockCalculationChallengeData(
                            url,
                            title,
                            secret,
                            goal,
                            numbers
                        )
                    } catch (ignore: Exception) {
                        return ChallengeDataParseError()
                    }
                }
                GameType.RIDDLE.getId() -> {
                    return try {
                        val description = json.getValue("description").jsonPrimitive.content
                        val answers = json.getValue("answers").jsonPrimitive.content.split(",").map { it }
                        RiddleChallengeData(
                            url,
                            title,
                            secret,
                            description,
                            answers
                        )
                    } catch (ignore: Exception) {
                        ChallengeDataParseError()
                    }
                }
                else -> {
                    ChallengeDataParseError()
                }
            }
        }
    }

    fun getTitle(): String {
        return if (challengeTitle != null && challengeTitle.isNotEmpty()) {
            challengeTitle
        } else {
            gameType.getName()
        }
    }
}

data class SherlockCalculationChallengeData(
    val url: String,
    val t: String,
    val secret: String,
    val goal: Int,
    val numbers: List<Int>
) : ChallengeData(t, secret, GameType.SHERLOCK_CALCULATION)

data class RiddleChallengeData(
    val url: String,
    val t: String,
    val secret: String,
    val description: String,
    val answers: List<String>
) : ChallengeData(t, secret, GameType.RIDDLE)

data class ChallengeDataParseError(val msg: String = "Parsing error") :
    ChallengeData(gameType = GameType.VALUE_COMPARISON, challengeSecret = "")