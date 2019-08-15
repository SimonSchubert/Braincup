package com.inspiredandroid.braincup.games

abstract class GameMode {
    abstract fun nextRound()
    abstract fun isCorrect(input: String): Boolean
}

enum class Game {
    MENTAL_CALCULATION,
    COLOR_CONFUSION,
    SHERLOCK_CALCULATION
}

fun Game.getName(): String {
    return when (this) {
        Game.MENTAL_CALCULATION -> "Mental calculation"
        Game.COLOR_CONFUSION -> "Color confusion"
        Game.SHERLOCK_CALCULATION -> "Sherlock calculation"
    }
}

fun Game.getDescription(): String {
    return when (this) {
        Game.MENTAL_CALCULATION -> "Follow the mathematical expressions. Time limit is 2 minutes."
        Game.COLOR_CONFUSION -> "Sum up the points of the correct statements under the figure. Time limit is 2 minutes."
        Game.SHERLOCK_CALCULATION -> "Find out how to get the result by only using the given numbers and the following operators: + - * / ( ). Time limit is 2 minutes."
    }
}

fun Game.getImageResource(): String {
    return when (this) {
        Game.MENTAL_CALCULATION -> "icons8-math.svg"
        Game.COLOR_CONFUSION -> "icons8-fill_color.svg"
        Game.SHERLOCK_CALCULATION -> "icons8-search.svg"
    }
}