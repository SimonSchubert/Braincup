package com.inspiredandroid.brainup

enum class Game {
    MENTAL_CALCULATION,
    COLOR_CONFUSION
}

fun Game.getName(): String {
    return when (this) {
        Game.MENTAL_CALCULATION -> "Mental calculation"
        Game.COLOR_CONFUSION -> "Color confusion"
    }
}

fun Game.getDescription(): String {
    return when (this) {
        Game.MENTAL_CALCULATION -> "Follow the mathematical instructions. Time limit is 2 minutes."
        Game.COLOR_CONFUSION -> "Sum up the points of the correct statements under the figure. Time limit is 2 minutes."
    }
}

fun Game.getImageResource(): String {
    return when (this) {
        Game.MENTAL_CALCULATION -> "icons8-math.svg"
        Game.COLOR_CONFUSION -> "icons8-fill_color.svg"
    }
}