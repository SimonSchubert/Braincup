package com.inspiredandroid.braincup.games

import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.category_logic
import braincup.composeapp.generated.resources.category_math
import braincup.composeapp.generated.resources.category_memory
import braincup.composeapp.generated.resources.category_perception
import org.jetbrains.compose.resources.StringResource

/**
 * The four skills the mini games train. Beyond tinting a tile, this is what the main menu is
 * sectioned by: the enum order here is the order the sections appear in.
 */
enum class GameCategory(
    val accentColor: Long,
    val displayNameRes: StringResource,
) {
    MEMORY(
        accentColor = 0xFFD1FAE5,
        displayNameRes = Res.string.category_memory,
    ),
    LOGIC(
        accentColor = 0xFFEDE9FE,
        displayNameRes = Res.string.category_logic,
    ),
    PERCEPTION(
        accentColor = 0xFFFFEDD5,
        displayNameRes = Res.string.category_perception,
    ),
    MATH(
        accentColor = 0xFFDBEAFE,
        displayNameRes = Res.string.category_math,
    ),
}
