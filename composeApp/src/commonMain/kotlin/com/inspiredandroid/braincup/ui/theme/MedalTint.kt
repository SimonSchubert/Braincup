package com.inspiredandroid.braincup.ui.theme

import androidx.compose.ui.graphics.Color
import com.inspiredandroid.braincup.games.GameType

/** The medal [score] has earned in this game, or null when it has not earned one. */
fun GameType.medalTint(score: Int): Color? = when {
    meetsScore(score, goldScore) -> MedalGold
    meetsScore(score, silverScore) -> MedalSilver
    meetsScore(score, bronzeScore) -> MedalBronze
    else -> null
}
