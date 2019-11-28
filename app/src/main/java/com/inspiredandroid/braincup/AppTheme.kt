package com.inspiredandroid.braincup

import androidx.compose.Composable
import androidx.ui.core.sp
import androidx.ui.graphics.Color
import androidx.ui.material.MaterialColors
import androidx.ui.material.MaterialTheme
import androidx.ui.material.MaterialTypography
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontFamily
import androidx.ui.text.font.FontWeight

@Composable
fun AppTheme(children: @Composable() () -> Unit) {
    val colors = MaterialColors(
        primary = Color(0xFFED7354),
        onPrimary = Color.White,
        background = Color.White,
        onBackground = Color.Black,
        surface = Color.White,
        onSurface = Color.Black
    )
    val typography = MaterialTypography(
        h1 = TextStyle(
            fontFamily = FontFamily("RobotoCondensed"),
            fontWeight = FontWeight.W100,
            fontSize = 96.sp
        ),
        h2 = TextStyle(
            fontFamily = FontFamily("RobotoCondensed"),
            fontWeight = FontWeight.W100,
            fontSize = 60.sp
        ),
        h3 = TextStyle(
            fontFamily = FontFamily("Eczar"),
            fontWeight = FontWeight.W500,
            fontSize = 48.sp
        ),
        h4 = TextStyle(
            fontFamily = FontFamily("RobotoCondensed"),
            fontWeight = FontWeight.W700,
            fontSize = 34.sp
        ),
        h5 = TextStyle(
            fontFamily = FontFamily("RobotoCondensed"),
            fontWeight = FontWeight.W700,
            fontSize = 24.sp
        ),
        h6 = TextStyle(
            fontFamily = FontFamily("RobotoCondensed"),
            fontWeight = FontWeight.W700,
            fontSize = 20.sp
        ),
        subtitle1 = TextStyle(
            fontFamily = FontFamily("RobotoCondensed"),
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        ),
        subtitle2 = TextStyle(
            fontFamily = FontFamily("RobotoCondensed"),
            fontWeight = FontWeight.W500,
            fontSize = 14.sp
        ),
        body1 = TextStyle(
            fontFamily = FontFamily("Eczar"),
            fontWeight = FontWeight.W700,
            fontSize = 16.sp
        ),
        body2 = TextStyle(
            fontFamily = FontFamily("RobotoCondensed"),
            fontWeight = FontWeight.W200,
            fontSize = 14.sp
        ),
        button = TextStyle(
            fontFamily = FontFamily("RobotoCondensed"),
            fontWeight = FontWeight.W800,
            fontSize = 14.sp
        ),
        caption = TextStyle(
            fontFamily = FontFamily("RobotoCondensed"),
            fontWeight = FontWeight.W500,
            fontSize = 12.sp
        ),
        overline = TextStyle(
            fontFamily = FontFamily("RobotoCondensed"),
            fontWeight = FontWeight.W500,
            fontSize = 10.sp
        )
    )
    MaterialTheme(colors = colors, typography = typography) {
        children()
    }
}