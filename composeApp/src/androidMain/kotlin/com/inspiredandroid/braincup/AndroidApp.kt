package com.inspiredandroid.braincup

import android.os.Build
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.inspiredandroid.braincup.ui.theme.DarkColorScheme
import com.inspiredandroid.braincup.ui.theme.LightColorScheme

@Composable
fun AndroidApp(
    mainMenuSponsorsSlot: @Composable () -> Unit = {},
) {
    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val context = LocalContext.current

    // Material You dynamic colors apply only to the "System" theme; explicit Light/Dark/OLED choices
    // resolve to the fixed brand schemes inside App().
    App(
        systemColorSchemeProvider = { dark ->
            when {
                dynamicColor && dark -> dynamicDarkColorScheme(context)
                dynamicColor -> dynamicLightColorScheme(context)
                dark -> DarkColorScheme
                else -> LightColorScheme
            }
        },
        mainMenuSponsorsSlot = mainMenuSponsorsSlot,
    )
}
