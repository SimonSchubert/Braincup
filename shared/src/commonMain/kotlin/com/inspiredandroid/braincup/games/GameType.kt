package com.inspiredandroid.braincup.games

enum class GameType {
    MENTAL_CALCULATION,
    COLOR_CONFUSION,
    SHERLOCK_CALCULATION,
    CHAIN_CALCULATION
}

fun GameType.getName(): String {
    return when (this) {
        GameType.MENTAL_CALCULATION -> "Mental calculation"
        GameType.COLOR_CONFUSION -> "Color confusion"
        GameType.SHERLOCK_CALCULATION -> "Sherlock calculation"
        GameType.CHAIN_CALCULATION -> "Chain calculation"
    }
}

fun GameType.getDescription(): String {
    return when (this) {
        GameType.MENTAL_CALCULATION -> "Follow the mathematical expressions. Time limit is 1 minute."
        GameType.COLOR_CONFUSION -> "Sum up the points of the correct statements under the figure. Time limit is 1 minute."
        GameType.SHERLOCK_CALCULATION -> "Find out how to get the result by only using the given numbers and the following operators: + - * / ( ). Time limit is 1 minute."
        GameType.CHAIN_CALCULATION -> "Follow the mathematical expressions. Time limit is 1 minute."
    }
}

fun GameType.getImageResource(): String {
    return when (this) {
        GameType.MENTAL_CALCULATION -> "icons8-math.svg"
        GameType.COLOR_CONFUSION -> "icons8-fill_color.svg"
        GameType.SHERLOCK_CALCULATION -> "icons8-search.svg"
        GameType.CHAIN_CALCULATION -> "icons8-chain.svg"
    }
}