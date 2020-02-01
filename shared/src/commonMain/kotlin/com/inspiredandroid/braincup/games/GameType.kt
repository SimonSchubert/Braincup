package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.api.UserStorage

enum class GameType {
    ANOMALY_PUZZLE,
    MENTAL_CALCULATION,
    COLOR_CONFUSION,
    SHERLOCK_CALCULATION,
    CHAIN_CALCULATION,
    FRACTION_CALCULATION,
    VALUE_COMPARISON,
    RIDDLE,
    PATH_FINDER
}

fun GameType.getName(): String {
    return when (this) {
        GameType.MENTAL_CALCULATION -> "Mental calculation"
        GameType.COLOR_CONFUSION -> "Color confusion"
        GameType.SHERLOCK_CALCULATION -> "Sherlock calculation"
        GameType.CHAIN_CALCULATION -> "Chain calculation"
        GameType.FRACTION_CALCULATION -> "Fraction calculation"
        GameType.VALUE_COMPARISON -> "Value comparison"
        GameType.ANOMALY_PUZZLE -> "Anomaly puzzle"
        GameType.RIDDLE -> "Riddle"
        GameType.PATH_FINDER -> "Path finder"
    }
}

fun GameType.getId(): String {
    return when (this) {
        GameType.MENTAL_CALCULATION -> "0"
        GameType.COLOR_CONFUSION -> "1"
        GameType.SHERLOCK_CALCULATION -> "2"
        GameType.CHAIN_CALCULATION -> "3"
        GameType.FRACTION_CALCULATION -> "4"
        GameType.VALUE_COMPARISON -> "5"
        GameType.ANOMALY_PUZZLE -> "6"
        GameType.RIDDLE -> "7"
        GameType.PATH_FINDER -> "8"
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
        GameType.VALUE_COMPARISON -> arrayOf(14, 4)
        GameType.ANOMALY_PUZZLE -> arrayOf(17, 8)
        GameType.RIDDLE -> arrayOf(0, 0)
        GameType.PATH_FINDER -> arrayOf(14, 7)
    }
}

fun GameType.getDescription(addTimeLimit: Boolean = true): String {
    val timeLimitString = " Time limit is 1 minute."
    return when (this) {
        GameType.MENTAL_CALCULATION -> "Follow the mathematical expressions. Use the result as the base for the next calculation."
        GameType.COLOR_CONFUSION -> "Sum up the points of the correct statements."
        GameType.SHERLOCK_CALCULATION -> "Find out how to get the goal by only using the given numbers and the following operators: + - * / ( )."
        GameType.CHAIN_CALCULATION -> "Follow the mathematical expressions."
        GameType.FRACTION_CALCULATION -> "Solve the fractions."
        GameType.VALUE_COMPARISON -> "Pick the mathematical formula with the highest result."
        GameType.ANOMALY_PUZZLE -> "Find the outstanding figure. Take the color and shape of the figure into account."
        GameType.RIDDLE -> "Solve the riddle."
        GameType.PATH_FINDER -> "Start at the marked position and follow the arrow instructions to find the destination."
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
        GameType.VALUE_COMPARISON -> "icons8-height.svg"
        GameType.ANOMALY_PUZZLE -> "icons8-telescope.svg"
        GameType.RIDDLE -> "icons8-questions.svg"
        GameType.PATH_FINDER -> "icons8-hard_to_find.svg"
    }
}