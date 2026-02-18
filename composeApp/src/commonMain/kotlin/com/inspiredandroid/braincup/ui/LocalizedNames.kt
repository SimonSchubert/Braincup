package com.inspiredandroid.braincup.ui

import androidx.compose.runtime.Composable
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.games.tools.Shape
import org.jetbrains.compose.resources.stringResource

@Composable
fun Color.localizedName(): String = when (this) {
    Color.RED -> stringResource(Res.string.color_red)
    Color.GREEN -> stringResource(Res.string.color_green)
    Color.BLUE -> stringResource(Res.string.color_blue)
    Color.PURPLE -> stringResource(Res.string.color_purple)
    Color.YELLOW -> stringResource(Res.string.color_yellow)
    Color.ORANGE -> stringResource(Res.string.color_orange)
    Color.TURQUOISE -> stringResource(Res.string.color_turquoise)
    Color.ROSA -> stringResource(Res.string.color_pink)
    Color.GREY_LIGHT -> stringResource(Res.string.color_light_grey)
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
