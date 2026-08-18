package com.inspiredandroid.braincup.ui

import androidx.compose.runtime.Composable
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.games.tools.GameColor
import com.inspiredandroid.braincup.games.tools.Shape
import com.inspiredandroid.braincup.ui.theme.LocalAccessiblePalette
import com.inspiredandroid.braincup.ui.theme.isDarkColorScheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun GameColor.localizedName(): String = when (this) {
    GameColor.RED -> stringResource(Res.string.color_red)
    GameColor.GREEN -> stringResource(Res.string.color_green)
    GameColor.BLUE -> stringResource(Res.string.color_blue)
    GameColor.PURPLE -> stringResource(Res.string.color_purple)
    GameColor.YELLOW -> stringResource(Res.string.color_yellow)
    GameColor.ORANGE -> stringResource(Res.string.color_orange)
    GameColor.TURQUOISE -> stringResource(Res.string.color_turquoise)
    // The accessible palette repaints ROSA as the achromatic slot (black on light backgrounds,
    // near-white on dark), so calling it "pink" would contradict the shape on screen.
    GameColor.ROSA -> when {
        !LocalAccessiblePalette.current -> stringResource(Res.string.color_pink)
        isDarkColorScheme -> stringResource(Res.string.color_white)
        else -> stringResource(Res.string.color_black)
    }
    GameColor.GREY_LIGHT -> stringResource(Res.string.color_light_grey)
}.uppercase()

@Composable
fun Shape.localizedName(): String = when (this) {
    Shape.SQUARE -> stringResource(Res.string.shape_square)
    Shape.TRIANGLE, Shape.ABSTRACT_TRIANGLE -> stringResource(Res.string.shape_triangle)
    Shape.CIRCLE -> stringResource(Res.string.shape_circle)
    Shape.HEART -> stringResource(Res.string.shape_heart)
    Shape.STAR -> stringResource(Res.string.shape_star)
    Shape.T -> stringResource(Res.string.shape_t_shape)
    Shape.L -> stringResource(Res.string.shape_l_shape)
    Shape.DIAMOND -> stringResource(Res.string.shape_diamond)
    Shape.HOUSE -> stringResource(Res.string.shape_house)
    Shape.ARROW -> stringResource(Res.string.shape_arrow)
}.uppercase()
