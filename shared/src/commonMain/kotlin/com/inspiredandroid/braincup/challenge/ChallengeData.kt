package com.inspiredandroid.braincup.challenge

import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.games.getId
import com.inspiredandroid.braincup.games.getName
import io.ktor.util.InternalAPI
import io.ktor.util.decodeBase64String
import kotlinx.serialization.json.Json

sealed class ChallengeData(
    private val challengeTitle: String? = null,
    val challengeSecret: String,
    val gameType: GameType
) {
    companion object {
        @UseExperimental(InternalAPI::class)
        fun parse(url: String, data: String): ChallengeData {
            val json = Json.plain.parseJson(data.decodeBase64String()).jsonObject
            val gameType = json.getPrimitive("game").content
            val title = json.getPrimitive("title").contentOrNull ?: ""
            val secret = json.getPrimitive("secret").contentOrNull ?: ""

            return when (gameType) {
                GameType.SHERLOCK_CALCULATION.getId() -> {
                    return try {
                        val goal = json.getPrimitive("goal").content.toInt()
                        val numbers =
                            json.getPrimitive("numbers").content.split(",").map { it.toInt() }
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
                        val description = json.getPrimitive("description").content
                        val answers = json.getPrimitive("answers").content.split(",").map { it }
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
    val title: String,
    val secret: String,
    val goal: Int,
    val numbers: List<Int>
) : ChallengeData(title, secret, GameType.SHERLOCK_CALCULATION)

data class RiddleChallengeData(
    val url: String,
    val title: String,
    val secret: String,
    val description: String,
    val answers: List<String>
) : ChallengeData(title, secret, GameType.RIDDLE)

data class ChallengeDataParseError(val msg: String = "Parsing error") : ChallengeData(gameType = GameType.HEIGHT_COMPARISON, challengeSecret = "")