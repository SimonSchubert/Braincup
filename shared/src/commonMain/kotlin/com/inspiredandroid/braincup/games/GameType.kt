package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.api.UserStorage

enum class GameType {
    ANOMALY_PUZZLE,
    MENTAL_CALCULATION,
    COLOR_CONFUSION,
    SHERLOCK_CALCULATION,
    CHAIN_CALCULATION,
    FRACTION_CALCULATION,
    HEIGHT_COMPARISON,
    RIDDLE
}

fun GameType.getName(): String {
    return when (this) {
        GameType.MENTAL_CALCULATION -> "Mental calculation"
        GameType.COLOR_CONFUSION -> "Color confusion"
        GameType.SHERLOCK_CALCULATION -> "Sherlock calculation"
        GameType.CHAIN_CALCULATION -> "Chain calculation"
        GameType.FRACTION_CALCULATION -> "Fraction calculation"
        GameType.HEIGHT_COMPARISON -> "Height comparison"
        GameType.ANOMALY_PUZZLE -> "Anomaly puzzle"
        GameType.RIDDLE -> "Riddle"
    }
}

fun GameType.getId(): String {
    return when (this) {
        GameType.MENTAL_CALCULATION -> "0"
        GameType.COLOR_CONFUSION -> "1"
        GameType.SHERLOCK_CALCULATION -> "2"
        GameType.CHAIN_CALCULATION -> "3"
        GameType.FRACTION_CALCULATION -> "4"
        GameType.HEIGHT_COMPARISON -> "5"
        GameType.ANOMALY_PUZZLE -> "6"
        GameType.RIDDLE -> "7"
    }
}

fun UserStorage.Achievements.getDescription(): String {
    return when (this) {
        UserStorage.Achievements.MEDAL_BRONZE -> "Win bronze in all games"
        UserStorage.Achievements.MEDAL_SILVER -> "Win silver in all games"
        UserStorage.Achievements.MEDAL_GOLD -> "Win gold in all games"
        UserStorage.Achievements.SCORES_10 -> "Total points of 10"
        UserStorage.Achievements.SCORES_100 -> "Total points of 100"
        UserStorage.Achievements.SCORES_1000 -> "Total points of 1,000"
        UserStorage.Achievements.SCORES_10000 -> "Total points of 10,000"
        UserStorage.Achievements.APP_OPEN_3 -> "Train 3 days in a row"
        UserStorage.Achievements.APP_OPEN_7 -> "Train 7 days in a row"
        UserStorage.Achievements.APP_OPEN_30 -> "Train 30 days in a row"
    }
}

fun GameType.getScoreTable(): Array<Int> {
    return when (this) {
        GameType.MENTAL_CALCULATION -> arrayOf(16, 8)
        GameType.COLOR_CONFUSION -> arrayOf(15, 8)
        GameType.SHERLOCK_CALCULATION -> arrayOf(7, 3)
        GameType.CHAIN_CALCULATION -> arrayOf(8, 4)
        GameType.FRACTION_CALCULATION -> arrayOf(10, 4)
        GameType.HEIGHT_COMPARISON -> arrayOf(14, 4)
        GameType.ANOMALY_PUZZLE -> arrayOf(17, 8)
        GameType.RIDDLE -> arrayOf(0, 0)
    }
}

fun GameType.getMedalResource(score: Int): String {
    val scoreTable = this.getScoreTable()
    return when {
        score >= scoreTable[0] -> MEDAL_FIRST_RESOURCE
        score >= scoreTable[1] -> MEDAL_SECOND_RESOURCE
        else -> MEDAL_THIRD_RESOURCE
    }
}

const val MEDAL_FIRST_RESOURCE = "icons8-medal_first_place.svg"
const val MEDAL_SECOND_RESOURCE = "icons8-medal_second_place.svg"
const val MEDAL_THIRD_RESOURCE = "icons8-medal_third_place.svg"

fun GameType.getDescription(addTimeLimit: Boolean = true): String {
    val timeLimitString = " Time limit is 1 minute."
    return when (this) {
        GameType.MENTAL_CALCULATION -> "Follow the mathematical expressions. Use the result as the base for the next calculation."
        GameType.COLOR_CONFUSION -> "Sum up the points of the correct statements."
        GameType.SHERLOCK_CALCULATION -> "Find out how to get the result by only using the given numbers and the following operators: + - * / ( )."
        GameType.CHAIN_CALCULATION -> "Follow the mathematical expressions."
        GameType.FRACTION_CALCULATION -> "Solve the fractions."
        GameType.HEIGHT_COMPARISON -> "Pick the mathematical formal with the highest result."
        GameType.ANOMALY_PUZZLE -> "Find the outstanding figure. Take into account color and shape."
        GameType.RIDDLE -> "Solve the riddle."
    } + if (addTimeLimit) {
        timeLimitString
    } else {
        ""
    }
}

fun GameType.getImageResource(): String {
    return when (this) {
        GameType.MENTAL_CALCULATION -> "icons8-math.svg"
        GameType.COLOR_CONFUSION -> "icons8-fill_color.svg"
        GameType.SHERLOCK_CALCULATION -> "icons8-search.svg"
        GameType.CHAIN_CALCULATION -> "icons8-chain.svg"
        GameType.FRACTION_CALCULATION -> "icons8-divide.svg"
        GameType.HEIGHT_COMPARISON -> "icons8-height.svg"
        GameType.ANOMALY_PUZZLE -> "icons8-telescope.svg"
        GameType.RIDDLE -> "icons8-questions.svg"
    }
}