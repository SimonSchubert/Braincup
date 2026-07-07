package com.inspiredandroid.braincup.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale as ComposeLocale
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.bungee
import braincup.composeapp.generated.resources.rubik_bold
import braincup.composeapp.generated.resources.rubik_medium
import braincup.composeapp.generated.resources.rubik_regular
import braincup.composeapp.generated.resources.rubik_semibold
import braincup.composeapp.generated.resources.tektur
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

// Start CTA on the instructions screen. A cool teal that contrasts the warm orange brand and is
// never used by demo tiles, so the real "Start" action never blends into the orange animation.
// Brand-pinned (constant across light/dark/OLED) and dark enough for white bold text to read.
val StartAccent = Color(0xFF14857A)

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

/** Vibrant pastel zone colors for Cat Queens, by region id (boards use up to 8 regions). The board
 *  also draws bold region borders, so the puzzle stays solvable when hues are hard to tell apart. */
val CatRegionColors = listOf(
    Color(0xFF6EC6E6), // sky blue
    Color(0xFFB39DDB), // lavender
    Color(0xFFFFD54F), // sunflower
    Color(0xFFE57399), // rose
    Color(0xFF4DB6AC), // teal
    Color(0xFFAED581), // leaf green
    Color(0xFFFFB074), // peach
    Color(0xFFF48FB1), // blossom pink
)

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

// Locales whose scripts Bungee cannot render. Bungee only partially covers Greek (just
// μ, π, Δ, Ω) which produces a jarring mix, and has no Cyrillic/CJK/Thai/Arabic/Indic
// glyphs at all. These locales use Tektur instead (full Greek + Cyrillic, a heavy blocky
// look close to Bungee); Latin locales keep the Bungee brand, which covers their accents.
private val nonLatinDisplayLanguages =
    setOf("el", "ru", "uk", "ja", "ko", "zh", "th", "hi", "bn", "ar")

@Composable
private fun displayFontFamily(): FontFamily =
    if (ComposeLocale.current.language in nonLatinDisplayLanguages) {
        FontFamily(Font(Res.font.tektur))
    } else {
        FontFamily(Font(Res.font.bungee))
    }

@Composable
private fun appTypography(): Typography {
    val display = displayFontFamily()
    return Typography(
        displayLarge = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.W100,
            fontSize = 57.sp,
            lineHeight = 64.sp,
        ),
        displayMedium = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.W100,
            fontSize = 45.sp,
            lineHeight = 52.sp,
        ),
        displaySmall = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.W500,
            fontSize = 36.sp,
            lineHeight = 44.sp,
        ),
        headlineLarge = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.W700,
            fontSize = 32.sp,
            lineHeight = 40.sp,
        ),
        headlineMedium = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.W700,
            fontSize = 28.sp,
            lineHeight = 36.sp,
        ),
        headlineSmall = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.W700,
            fontSize = 24.sp,
            lineHeight = 32.sp,
        ),
        titleLarge = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.W700,
            fontSize = 22.sp,
            lineHeight = 28.sp,
        ),
        titleMedium = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.W500,
            fontSize = 16.sp,
            lineHeight = 24.sp,
        ),
        titleSmall = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.W500,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        ),
        bodyLarge = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.W700,
            fontSize = 16.sp,
            lineHeight = 24.sp,
        ),
        bodyMedium = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.W200,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        ),
        bodySmall = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.W500,
            fontSize = 12.sp,
            lineHeight = 16.sp,
        ),
        labelLarge = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.W800,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        ),
        labelMedium = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.W500,
            fontSize = 12.sp,
            lineHeight = 16.sp,
        ),
        labelSmall = TextStyle(
            fontFamily = display,
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

/**
 * Rubik: a geometric sans used for number-heavy gameplay content (math problems, grids,
 * scores, timers). Far more legible than the Bungee display font while still fitting the
 * playful look. Bundles the weights the number widgets use so existing FontWeight values
 * resolve correctly.
 */
@Composable
fun numberFontFamily(): FontFamily = FontFamily(
    Font(Res.font.rubik_regular, FontWeight.W400),
    Font(Res.font.rubik_medium, FontWeight.W500),
    Font(Res.font.rubik_semibold, FontWeight.W600),
    Font(Res.font.rubik_bold, FontWeight.W700),
)

/** Swap a theme TextStyle's font to the readable number font for gameplay numbers. */
@Composable
fun TextStyle.numeric(): TextStyle = copy(fontFamily = numberFontFamily())

/**
 * Render only the digit runs of [text] in the readable number font, leaving words and other
 * characters in the surrounding (Bungee) style. Use for mixed label+number strings like
 * "Level 4" or "Score: 12" so the word stays on-brand while the number is legible.
 *
 * Only ASCII digits 0-9 (with internal grouping/decimal separators) are restyled, since that is
 * the range the number font ships; localized non-Latin numerals fall through to the base style.
 */
@Composable
fun annotateNumbers(text: String): AnnotatedString {
    val family = numberFontFamily()
    return buildAnnotatedString {
        var i = 0
        while (i < text.length) {
            if (text[i] in '0'..'9') {
                val start = i
                while (i < text.length &&
                    (
                        text[i] in '0'..'9' ||
                            ((text[i] == '.' || text[i] == ',') && i + 1 < text.length && text[i + 1] in '0'..'9')
                        )
                ) {
                    i++
                }
                withStyle(SpanStyle(fontFamily = family)) {
                    append(text.substring(start, i))
                }
            } else {
                append(text[i])
                i++
            }
        }
    }
}
