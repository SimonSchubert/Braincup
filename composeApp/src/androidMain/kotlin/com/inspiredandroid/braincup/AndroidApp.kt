package com.inspiredandroid.braincup

import android.app.Activity
import android.os.Build
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.inspiredandroid.braincup.games.wordle.WordleAppContext
import com.inspiredandroid.braincup.ui.theme.DarkColorScheme
import com.inspiredandroid.braincup.ui.theme.LightColorScheme

@Composable
fun AndroidApp(
    useBuiltInSponsors: Boolean = false,
    mainMenuSponsorsSlot: @Composable () -> Unit = {},
) {
    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val context = LocalContext.current
    // Wordle reads the app Configuration locale; init before App() composes the main menu.
    WordleAppContext.applicationContext = context.applicationContext

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
        systemBarAppearance = { darkTheme ->
            val view = LocalView.current
            if (!view.isInEditMode) {
                SideEffect {
                    val window = (view.context as Activity).window
                    WindowCompat.getInsetsController(window, view).apply {
                        // Light icons on a dark theme, dark icons on a light theme.
                        isAppearanceLightStatusBars = !darkTheme
                        isAppearanceLightNavigationBars = !darkTheme
                    }
                }
            }
        },
        useBuiltInSponsors = useBuiltInSponsors,
        mainMenuSponsorsSlot = mainMenuSponsorsSlot,
    )
}
