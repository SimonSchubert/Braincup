/*
// * Copyright 2019 The Android Open Source Project
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package androidx.ui.material.studies.rally
//
//import androidx.compose.Composable
//import androidx.compose.composer
//import androidx.ui.core.sp
//import androidx.ui.text.font.FontWeight
//import androidx.ui.text.font.FontFamily
//import androidx.ui.graphics.Color
//import androidx.ui.material.MaterialColors
//import androidx.ui.material.MaterialTheme
//import androidx.ui.material.MaterialTypography
//import androidx.ui.text.TextStyle
//
//@Composable
//fun AppTheme(children: @Composable() () -> Unit) {
//    val colors = MaterialColors(
//        primary = Color(0xFFED7354),
//        surface = Color(0xFF26282F),
//        onSurface = Color.White
//    )
//    val typography = MaterialTypography(
//        h1 = TextStyle(fontFamily = FontFamily("RobotoCondensed"),
//            fontWeight = FontWeight.W100,
//            fontSize = 96.sp),
//        h2 = TextStyle(fontFamily = FontFamily("RobotoCondensed"),
//            fontWeight = FontWeight.W100,
//            fontSize = 60.sp),
//        h3 = TextStyle(fontFamily = FontFamily("Eczar"),
//            fontWeight = FontWeight.W500,
//            fontSize = 48.sp),
//        h4 = TextStyle(fontFamily = FontFamily("RobotoCondensed"),
//            fontWeight = FontWeight.W700,
//            fontSize = 34.sp),
//        h5 = TextStyle(fontFamily = FontFamily("RobotoCondensed"),
//            fontWeight = FontWeight.W700,
//            fontSize = 24.sp),
//        h6 = TextStyle(fontFamily = FontFamily("RobotoCondensed"),
//            fontWeight = FontWeight.W700,
//            fontSize = 20.sp),
//        subtitle1 = TextStyle(fontFamily = FontFamily("RobotoCondensed"),
//            fontWeight = FontWeight.W700,
//            fontSize = 16.sp),
//        subtitle2 = TextStyle(fontFamily = FontFamily("RobotoCondensed"),
//            fontWeight = FontWeight.W500,
//            fontSize = 14.sp),
//        body1 = TextStyle(fontFamily = FontFamily("Eczar"),
//            fontWeight = FontWeight.W700,
//            fontSize = 16.sp),
//        body2 = TextStyle(fontFamily = FontFamily("RobotoCondensed"),
//            fontWeight = FontWeight.W200,
//            fontSize = 14.sp),
//        button = TextStyle(fontFamily = FontFamily("RobotoCondensed"),
//            fontWeight = FontWeight.W800,
//            fontSize = 14.sp),
//        caption = TextStyle(fontFamily = FontFamily("RobotoCondensed"),
//            fontWeight = FontWeight.W500,
//            fontSize = 12.sp),
//        overline = TextStyle(fontFamily = FontFamily("RobotoCondensed"),
//            fontWeight = FontWeight.W500,
//            fontSize = 10.sp)
//
//    )
//    MaterialTheme(colors = colors, typography = typography) {
//        children()
//    }
//}