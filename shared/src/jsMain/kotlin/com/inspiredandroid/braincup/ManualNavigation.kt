package com.inspiredandroid.braincup

import com.inspiredandroid.braincup.app.AppState
import com.inspiredandroid.braincup.challenge.ChallengeData
import com.inspiredandroid.braincup.challenge.ChallengeDataParseError
import com.inspiredandroid.braincup.challenge.RiddleChallengeData
import com.inspiredandroid.braincup.challenge.SherlockCalculationChallengeData
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.games.getId
import org.w3c.dom.url.URLSearchParams
import kotlin.browser.window

fun referenceFunctions() {
    startMenu()
    startAnomalyPuzzle()
    startColorConfusion()
    startChainCalculation()
    startFractionCalculation()
    startMentalCalculation()
    startSherlockCalculation()
    startHeightComparison()
    startAnomalyPuzzleScoreboard()
    startChainCalculationScoreboard()
    startColorConfusionScoreboard()
    startFractionCalculationScoreboard()
    startHeightComparisonScoreboard()
    startMentalCalculationScoreboard()
    startSherlockCalculationScoreboard()
    startCreateChallenge()
    startChallenge()
}

fun startMenu() {
    JsMain(state = AppState.START)
}

fun startChallenge() {
    val urlParams = URLSearchParams(window.location.search)
    val data = parseChallenge(urlParams)
    JsMain(
        state = AppState.CHALLENGE,
        gameType = GameType.SHERLOCK_CALCULATION,
        challengeData = data
    )
}

fun parseChallenge(urlParams: URLSearchParams): ChallengeData {
    val game = urlParams.get("game")

    return when (game) {
        GameType.SHERLOCK_CALCULATION.getId() -> {
            val title = try {
                urlParams.get("title")!!.toString()
            } catch (ignore: Exception) {
                return ChallengeDataParseError()
            }
            val goal = try {
                urlParams.get("goal")!!.toInt()
            } catch (ignore: Exception) {
                return ChallengeDataParseError()
            }
            val numbers = try {
                urlParams.get("numbers")!!.split(",").map { it.toInt() }
            } catch (ignore: Exception) {
                return ChallengeDataParseError()
            }
            SherlockCalculationChallengeData(
                title,
                goal,
                numbers
            )
        }
        GameType.RIDDLE.getId() -> {
            val title = try {
                urlParams.get("title")!!.toString()
            } catch (ignore: Exception) {
                return ChallengeDataParseError()
            }
            val description = try {
                urlParams.get("description")!!
            } catch (ignore: Exception) {
                return ChallengeDataParseError()
            }
            val answers = try {
                urlParams.get("answers")!!.split(",").map { it }
            } catch (ignore: Exception) {
                return ChallengeDataParseError()
            }
            RiddleChallengeData(
                title,
                description,
                answers
            )
        }
        else -> {
            ChallengeDataParseError()
        }
    }
}

// http://localhost:63343/-c2aydfbf79txhqs0kyws3mn3t8s38b7b16im9/Braincup/build/webDebug/challenge.html?game=7&type=0&question=dfsdf&answers=213

fun startCreateChallenge() {
    JsMain(state = AppState.CREATE_CHALLENGE)
}

fun startSherlockCalculation() {
    JsMain(AppState.INSTRUCTIONS, GameType.SHERLOCK_CALCULATION)
}

fun startHeightComparison() {
    JsMain(AppState.INSTRUCTIONS, GameType.HEIGHT_COMPARISON)
}

fun startMentalCalculation() {
    JsMain(AppState.INSTRUCTIONS, GameType.MENTAL_CALCULATION)
}

fun startFractionCalculation() {
    JsMain(AppState.INSTRUCTIONS, GameType.FRACTION_CALCULATION)
}

fun startChainCalculation() {
    JsMain(AppState.INSTRUCTIONS, GameType.CHAIN_CALCULATION)
}

fun startAnomalyPuzzle() {
    JsMain(AppState.INSTRUCTIONS, GameType.ANOMALY_PUZZLE)
}

fun startColorConfusion() {
    JsMain(AppState.INSTRUCTIONS, GameType.COLOR_CONFUSION)
}

fun startAnomalyPuzzleScoreboard() {
    JsMain(AppState.SCOREBOARD, GameType.ANOMALY_PUZZLE)
}

fun startColorConfusionScoreboard() {
    JsMain(AppState.SCOREBOARD, GameType.COLOR_CONFUSION)
}

fun startChainCalculationScoreboard() {
    JsMain(AppState.SCOREBOARD, GameType.CHAIN_CALCULATION)
}

fun startSherlockCalculationScoreboard() {
    JsMain(AppState.SCOREBOARD, GameType.SHERLOCK_CALCULATION)
}

fun startMentalCalculationScoreboard() {
    JsMain(AppState.SCOREBOARD, GameType.MENTAL_CALCULATION)
}

fun startFractionCalculationScoreboard() {
    JsMain(AppState.SCOREBOARD, GameType.FRACTION_CALCULATION)
}

fun startHeightComparisonScoreboard() {
    JsMain(AppState.SCOREBOARD, GameType.HEIGHT_COMPARISON)
}