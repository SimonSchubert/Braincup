package com.inspiredandroid.braincup.games

abstract class Game {
    abstract fun nextRound()
    abstract fun isCorrect(input: String): Boolean

    enum class Type {
        MENTAL_CALCULATION,
        COLOR_CONFUSION,
        SHERLOCK_CALCULATION,
        BORING_CHAIN_CALCULAITON
    }
}

fun Game.Type.getName(): String {
    return when (this) {
        Game.Type.MENTAL_CALCULATION -> "Mental calculation"
        Game.Type.COLOR_CONFUSION -> "Color confusion"
        Game.Type.SHERLOCK_CALCULATION -> "Sherlock calculation"
        Game.Type.BORING_CHAIN_CALCULAITON -> "Boring chain calculation"
    }
}

fun Game.Type.getDescription(): String {
    return when (this) {
        Game.Type.MENTAL_CALCULATION -> "Follow the mathematical expressions. Time limit is 2 minutes."
        Game.Type.COLOR_CONFUSION -> "Sum up the points of the correct statements under the figure. Time limit is 2 minutes."
        Game.Type.SHERLOCK_CALCULATION -> "Find out how to get the result by only using the given numbers and the following operators: + - * / ( ). Time limit is 2 minutes."
        Game.Type.BORING_CHAIN_CALCULAITON -> "Follow the mathematical expressions. Time limit is 2 minutes."
    }
}

fun Game.Type.getImageResource(): String {
    return when (this) {
        Game.Type.MENTAL_CALCULATION -> "icons8-math.svg"
        Game.Type.COLOR_CONFUSION -> "icons8-fill_color.svg"
        Game.Type.SHERLOCK_CALCULATION -> "icons8-search.svg"
        Game.Type.BORING_CHAIN_CALCULAITON -> "icons8-search.svg"
    }
}