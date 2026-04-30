package com.inspiredandroid.braincup

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.inspiredandroid.braincup.ui.theme.DarkColorScheme
import com.inspiredandroid.braincup.ui.theme.LightColorScheme

@Composable
fun AndroidApp() {
    val isDarkTheme = isSystemInDarkTheme()
    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && isDarkTheme -> dynamicDarkColorScheme(context)
        dynamicColor && !isDarkTheme -> dynamicLightColorScheme(context)
        isDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    App(colorScheme = colorScheme)
}
