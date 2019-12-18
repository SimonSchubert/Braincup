package com.inspiredandroid.braincup

import com.inspiredandroid.braincup.app.AppState
import com.inspiredandroid.braincup.games.GameType

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
}

fun startMenu() {
    JsMain()
}

fun startSherlockCalculation() {
    JsMain(GameType.SHERLOCK_CALCULATION, AppState.INSTRUCTIONS)
}

fun startHeightComparison() {
    JsMain(GameType.HEIGHT_COMPARISON, AppState.INSTRUCTIONS)
}

fun startMentalCalculation() {
    JsMain(GameType.MENTAL_CALCULATION, AppState.INSTRUCTIONS)
}

fun startFractionCalculation() {
    JsMain(GameType.FRACTION_CALCULATION, AppState.INSTRUCTIONS)
}

fun startChainCalculation() {
    JsMain(GameType.CHAIN_CALCULATION, AppState.INSTRUCTIONS)
}

fun startAnomalyPuzzle() {
    JsMain(GameType.ANOMALY_PUZZLE, AppState.INSTRUCTIONS)
}

fun startColorConfusion() {
    JsMain(GameType.COLOR_CONFUSION, AppState.INSTRUCTIONS)
}

fun startAnomalyPuzzleScoreboard() {
    JsMain(GameType.ANOMALY_PUZZLE, AppState.SCOREBOARD)
}

fun startColorConfusionScoreboard() {
    JsMain(GameType.COLOR_CONFUSION, AppState.SCOREBOARD)
}

fun startChainCalculationScoreboard() {
    JsMain(GameType.CHAIN_CALCULATION, AppState.SCOREBOARD)
}

fun startSherlockCalculationScoreboard() {
    JsMain(GameType.SHERLOCK_CALCULATION, AppState.SCOREBOARD)
}

fun startMentalCalculationScoreboard() {
    JsMain(GameType.MENTAL_CALCULATION, AppState.SCOREBOARD)
}

fun startFractionCalculationScoreboard() {
    JsMain(GameType.FRACTION_CALCULATION, AppState.SCOREBOARD)
}

fun startHeightComparisonScoreboard() {
    JsMain(GameType.HEIGHT_COMPARISON, AppState.SCOREBOARD)
}