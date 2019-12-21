package com.inspiredandroid.braincup

import com.inspiredandroid.braincup.app.AppState
import com.inspiredandroid.braincup.challenge.*
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
    val url = window.location.search
    val urlParams = URLSearchParams(url)
    urlParams.get("data")?.let {
        val data = ChallengeData.parse(url, it)
        JsMain(
            state = AppState.CHALLENGE,
            gameType = GameType.SHERLOCK_CALCULATION,
            challengeData = data
        )
    }
}

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