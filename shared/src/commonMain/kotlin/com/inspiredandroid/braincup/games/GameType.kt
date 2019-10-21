package com.inspiredandroid.braincup.games

enum class GameType {
    MENTAL_CALCULATION,
    COLOR_CONFUSION,
    SHERLOCK_CALCULATION,
    CHAIN_CALCULATION,
    FRACTION_CALCULATION,
    HEIGHT_COMPARISON
}

fun GameType.getName(): String {
    return when (this) {
        GameType.MENTAL_CALCULATION -> "Mental calculation"
        GameType.COLOR_CONFUSION -> "Color confusion"
        GameType.SHERLOCK_CALCULATION -> "Sherlock calculation"
        GameType.CHAIN_CALCULATION -> "Chain calculation"
        GameType.FRACTION_CALCULATION -> "Fraction calculation"
        GameType.HEIGHT_COMPARISON -> "Height comparison"
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
    }
}

fun GameType.getScoreTable(): Array<Int> {
    return when (this) {
        GameType.MENTAL_CALCULATION -> arrayOf(16, 8)
        GameType.COLOR_CONFUSION -> arrayOf(16, 8)
        GameType.SHERLOCK_CALCULATION -> arrayOf(7, 3)
        GameType.CHAIN_CALCULATION -> arrayOf(8, 4)
        GameType.FRACTION_CALCULATION -> arrayOf(8, 4)
        GameType.HEIGHT_COMPARISON -> arrayOf(10, 4)
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

fun GameType.getDescription(): String {
    return when (this) {
        GameType.MENTAL_CALCULATION -> "Follow the mathematical expressions. Time limit is 1 minute."
        GameType.COLOR_CONFUSION -> "Sum up the points of the correct statements under the figure. Time limit is 1 minute."
        GameType.SHERLOCK_CALCULATION -> "Find out how to get the result by only using the given numbers and the following operators: + - * / ( ). Time limit is 1 minute."
        GameType.CHAIN_CALCULATION -> "Follow the mathematical expressions. Time limit is 1 minute."
        GameType.FRACTION_CALCULATION -> "Solve the fractions. Time limit is 1 minute."
        GameType.HEIGHT_COMPARISON -> "Pick the mathematical formal with the highest result. Time limit is 1 minute."
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
    }
}