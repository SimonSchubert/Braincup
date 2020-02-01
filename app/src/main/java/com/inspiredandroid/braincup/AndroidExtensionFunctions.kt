package com.inspiredandroid.braincup

import androidx.ui.graphics.Color
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.games.getScoreTable
import com.inspiredandroid.braincup.games.tools.getHex

fun com.inspiredandroid.braincup.games.tools.Color.getComposeColor(): Color {
    return Color(android.graphics.Color.parseColor(this.getHex()))
}

fun getComposeColor(hex: String): Color {
    return Color(android.graphics.Color.parseColor(hex))
}

fun GameType.getAndroidDrawable(): Int {
    return when (this) {
        GameType.MENTAL_CALCULATION -> R.drawable.ic_icons8_math
        GameType.COLOR_CONFUSION -> R.drawable.ic_icons8_fill_color
        GameType.SHERLOCK_CALCULATION -> R.drawable.ic_icons8_search
        GameType.CHAIN_CALCULATION -> R.drawable.ic_icons8_chain
        GameType.FRACTION_CALCULATION -> R.drawable.ic_icons8_divide
        GameType.VALUE_COMPARISON -> R.drawable.ic_icons8_height
        GameType.ANOMALY_PUZZLE -> R.drawable.ic_icons8_telescope
        GameType.RIDDLE -> R.drawable.ic_icons8_questions
        GameType.PATH_FINDER -> R.drawable.ic_icons8_hard_to_find
    }
}

fun UserStorage.Achievements.getAndroidResource(): Int {
    return when (this) {
        UserStorage.Achievements.MEDAL_BRONZE -> R.drawable.ic_icons8_medal_third_place
        UserStorage.Achievements.MEDAL_SILVER -> R.drawable.ic_icons8_medal_second_place
        UserStorage.Achievements.MEDAL_GOLD -> R.drawable.ic_icons8_medal_first_place
        UserStorage.Achievements.SCORES_10 -> R.drawable.ic_icons8_counter
        UserStorage.Achievements.SCORES_100 -> R.drawable.ic_icons8_counter_bronze
        UserStorage.Achievements.SCORES_1000 -> R.drawable.ic_icons8_counter_silver
        UserStorage.Achievements.SCORES_10000 -> R.drawable.ic_icons8_counter_gold
        UserStorage.Achievements.APP_OPEN_3 -> R.drawable.ic_icons8_counter_bronze
        UserStorage.Achievements.APP_OPEN_7 -> R.drawable.ic_icons8_counter_bronze
        UserStorage.Achievements.APP_OPEN_30 -> R.drawable.ic_icons8_counter_bronze
    }
}

fun GameType.getAndroidMedalResource(score: Int): Int {
    val scoreTable = this.getScoreTable()
    return when {
        score >= scoreTable[0] -> R.drawable.ic_icons8_medal_first_place
        score >= scoreTable[1] -> R.drawable.ic_icons8_medal_second_place
        else -> R.drawable.ic_icons8_medal_third_place
    }
}