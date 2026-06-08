package com.inspiredandroid.braincup.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.bungee
import org.jetbrains.compose.resources.Font

val Primary = Color(0xFFED7354)
private val OnPrimary = Color.White
private val Secondary = Color(0xFFC45C44)
private val OnSecondary = Color.White
private val Background = Color.White
private val OnBackground = Color.Black
private val Surface = Color.White
private val OnSurface = Color.Black
private val SurfaceVariant = Color(0xFFF5F5F5)
private val OnSurfaceVariant = Color(0xFF666666)

val SuccessGreen = Color(0xFF5C8E58)
val ErrorRed = Color(0xFFB00020)

// Brand-pinned container tones — used by banners and game-state visuals that should
// stay consistent regardless of Material You dynamic color. Values match M3 light defaults.
val PrimaryContainer = Color(0xFFEADDFF)
val OnPrimaryContainer = Color(0xFF21005D)

// Pre-baked alpha variants of brand colors that recur across screens.
val SuccessGreenSoft = Color(0xFF5C8E58).copy(alpha = 0.15f)
val OnPrimaryContainerSubtle = OnPrimaryContainer.copy(alpha = 0.15f)
val OnPrimaryContainerDisabled = OnPrimaryContainer.copy(alpha = 0.12f)

val MedalGold = Color(0xFFFFD700)
val MedalSilver = Color(0xFFC0C0C0)
val MedalBronze = Color(0xFFCD7F32)

val LightsOutOnColor = Color(0xFFFFC107)
val LightsOutOffColor = Color(0xFF424242)

// Wordle tile feedback. The three classic hues, kept constant across light/dark/OLED so the
// green/yellow/gray meaning never shifts; white text reads on all three. Empty/pending tiles use
// theme surface tones instead so they recede.
val WordleCorrect = Color(0xFF6AAA64)
val WordlePresent = Color(0xFFC9B458)
val WordleAbsent = Color(0xFF787C7E)

// Color Confusion cell faces. Tuned so toggle state stays unambiguous under bright-sunlight glare
// without the bright selected face looking harsh in normal viewing conditions. In dark mode the
// unselected face is also lifted off the very dark surface tone for the same reason; light mode
// keeps the theme's surfaceContainer since it's already bright enough.
val SelectedTileFaceLight = Color(0xFF6E6E6E)
val SelectedTileFaceDark = Color(0xFFA8A8A8)
val UnselectedTileFaceDark = Color(0xFF454545)

val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    secondary = Secondary,
    onSecondary = OnSecondary,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    error = ErrorRed,
    onError = Color.White,
)

// Warm dark: near-black tones biased toward the orange brand instead of neutral gray, so the
// dark theme feels warm rather than clinical. OLED below keeps the pure-black variant.
val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    secondary = Primary,
    onSecondary = Color.White,
    background = Color(0xFF1B1614),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1B1614),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF2E2622),
    onSurfaceVariant = Color(0xFFB0A8A2),
    surfaceContainer = Color(0xFF241D1A),
    surfaceContainerHigh = Color(0xFF332A25),
    error = ErrorRed,
    onError = Color.White,
)

// OLED: pure black background to save power on OLED panels. Container tones are lifted off pure
// black so the PrismTile bevel (which darkens the face color for its sides) stays visible on cards.
val OledColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    secondary = Primary,
    onSecondary = Color.White,
    background = Color.Black,
    onBackground = Color(0xFFE6E1E5),
    surface = Color.Black,
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF161616),
    onSurfaceVariant = Color(0xFFB0B0B0),
    surfaceContainer = Color(0xFF121212),
    surfaceContainerHigh = Color(0xFF1E1E1E),
    error = ErrorRed,
    onError = Color.White,
)

@Composable
private fun appTypography(): Typography {
    val bungee = FontFamily(Font(Res.font.bungee))
    return Typography(
        displayLarge = TextStyle(
            fontFamily = bungee,
            fontWeight = FontWeight.W100,
            fontSize = 57.sp,
            lineHeight = 64.sp,
        ),
        displayMedium = TextStyle(
            fontFamily = bungee,
            fontWeight = FontWeight.W100,
            fontSize = 45.sp,
            lineHeight = 52.sp,
        ),
        displaySmall = TextStyle(
            fontFamily = bungee,
            fontWeight = FontWeight.W500,
            fontSize = 36.sp,
            lineHeight = 44.sp,
        ),
        headlineLarge = TextStyle(
            fontFamily = bungee,
            fontWeight = FontWeight.W700,
            fontSize = 32.sp,
            lineHeight = 40.sp,
        ),
        headlineMedium = TextStyle(
            fontFamily = bungee,
            fontWeight = FontWeight.W700,
            fontSize = 28.sp,
            lineHeight = 36.sp,
        ),
        headlineSmall = TextStyle(
            fontFamily = bungee,
            fontWeight = FontWeight.W700,
            fontSize = 24.sp,
            lineHeight = 32.sp,
        ),
        titleLarge = TextStyle(
            fontFamily = bungee,
            fontWeight = FontWeight.W700,
            fontSize = 22.sp,
            lineHeight = 28.sp,
        ),
        titleMedium = TextStyle(
            fontFamily = bungee,
            fontWeight = FontWeight.W500,
            fontSize = 16.sp,
            lineHeight = 24.sp,
        ),
        titleSmall = TextStyle(
            fontFamily = bungee,
            fontWeight = FontWeight.W500,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        ),
        bodyLarge = TextStyle(
            fontFamily = bungee,
            fontWeight = FontWeight.W700,
            fontSize = 16.sp,
            lineHeight = 24.sp,
        ),
        bodyMedium = TextStyle(
            fontFamily = bungee,
            fontWeight = FontWeight.W200,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        ),
        bodySmall = TextStyle(
            fontFamily = bungee,
            fontWeight = FontWeight.W500,
            fontSize = 12.sp,
            lineHeight = 16.sp,
        ),
        labelLarge = TextStyle(
            fontFamily = bungee,
            fontWeight = FontWeight.W800,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        ),
        labelMedium = TextStyle(
            fontFamily = bungee,
            fontWeight = FontWeight.W500,
            fontSize = 12.sp,
            lineHeight = 16.sp,
        ),
        labelSmall = TextStyle(
            fontFamily = bungee,
            fontWeight = FontWeight.W500,
            fontSize = 11.sp,
            lineHeight = 16.sp,
        ),
    )
}

@Composable
fun BraincupTheme(
    colorScheme: ColorScheme = if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = colorScheme,
        typography = appTypography(),
        content = content,
    )
}
