package com.inspiredandroid.braincup.screenshots.learn

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import app.cash.paparazzi.DeviceConfig
import com.android.resources.ScreenOrientation
import app.cash.paparazzi.Paparazzi
import com.inspiredandroid.braincup.learn.LearnCatalog
import com.inspiredandroid.braincup.learn.CatalogText
import com.inspiredandroid.braincup.learn.LessonStep
import com.inspiredandroid.braincup.ui.theme.BraincupTheme
import com.inspiredandroid.braincup.ui.theme.DarkColorScheme
import com.inspiredandroid.braincup.ui.theme.LightColorScheme
import com.inspiredandroid.braincup.ui.theme.OledColorScheme
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getPluralString
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.getSystemResourceEnvironment

/**
 * Shared rig for the Learn section's render check.
 *
 * The section's readiness bar (`docs/learn-release-status.md` §3, item 9) asks for every sub-topic
 * to be *seen*, on a narrow screen, before release. These renders are that pass: they walk the
 * whole catalog rather than guarding a golden, so they are recorded with
 * `:screenshotTests:renderLearnScreens` and reviewed by eye, not diffed.
 *
 * Two things make them different from the store and regression sets:
 *
 * * **Device resolution.** [LearnPhone] renders at the Pixel 9a's real 1080x2424 rather than the
 *   445x1000 dp grid the goldens use, because this is a check on teaching prose and hand-drawn
 *   figures and 445px body text cannot be read.
 * * **Seeded screen state.** `LessonScreenState` and `QuizScreenState` open a lesson or a test
 *   part-way through, which is the only way a still frame can reach the answered states: the
 *   solved formula, the feedback card, the retry note, the result and the review list.
 */
val LearnPhone: DeviceConfig = DeviceConfig.PIXEL_9A.copy(softButtons = false)

/**
 * Viewports deep enough to hold a whole unfolded review list, or the whole shape guide, in one
 * frame. `renderLearnScreens` slices anything taller than [LearnPhone] back into phone-height
 * pages, so a `_p2.png` existing is itself the report that the screen scrolls.
 */
const val LearnTallPx: Int = 7_000

const val LearnGuidePx: Int = 13_000

/**
 * A viewport the width of the phone but only deep enough for one figure panel.
 *
 * Declared landscape on purpose: layoutlib enforces height >= width on a portrait config, so a
 * short portrait device silently comes back rotated, and a figure laid out from a 560px canvas
 * instead of a 1080px one is not the figure a lesson draws.
 */
val LearnFigureStrip: DeviceConfig = LearnPhone.copy(orientation = ScreenOrientation.LANDSCAPE)

/** The three schemes the sweep covers. `BraincupTheme` takes the scheme, layoutlib takes the name. */
enum class LearnTheme(
    val scheme: ColorScheme,
    val platformTheme: String,
) {
    LIGHT(LightColorScheme, "android:Theme.Material.Light.NoActionBar"),
    DARK(DarkColorScheme, "android:Theme.Material.NoActionBar"),
    OLED(OledColorScheme, "android:Theme.Material.NoActionBar"),
}

/**
 * The rule every Learn render class uses. `useDeviceResolution` is what buys the readable 1080px
 * frame; `showSystemUi` is off because a status bar only steals height from the content under
 * review.
 */
fun learnPaparazzi(): Paparazzi = Paparazzi(
    deviceConfig = LearnPhone,
    showSystemUi = false,
    useDeviceResolution = true,
)

/**
 * One frame. [name] is the file's identity inside its sub-topic folder; keep it sortable, because
 * the review walks a directory listing in order.
 */
fun Paparazzi.learnSnap(
    name: String,
    theme: LearnTheme = LearnTheme.LIGHT,
    heightPx: Int = LearnPhone.screenHeight,
    content: @Composable () -> Unit,
) {
    unsafeUpdateConfig(
        deviceConfig = when {
            heightPx == LearnPhone.screenHeight -> LearnPhone
            heightPx < LearnPhone.screenHeight -> LearnFigureStrip.copy(screenHeight = heightPx)
            else -> LearnPhone.copy(screenHeight = heightPx)
        },
        theme = theme.platformTheme,
    )
    snapshot(name = name) {
        CompositionLocalProvider(LocalInspectionMode provides true) {
            BraincupTheme(colorScheme = theme.scheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    content()
                }
            }
        }
    }
}

/** Short tag naming what kind of step a frame shows, so a listing reads without opening anything. */
fun LessonStep.kindTag(): String = when (this) {
    is LessonStep.Concept -> "concept"
    is LessonStep.Worked -> "worked"
    is LessonStep.Choice -> "choice"
    is LessonStep.Numeric -> "numeric"
}

/**
 * An option this step rejects, as the text the miss state records. Every authored step has at
 * least two.
 *
 * Resolved here rather than handed back as a key: `LessonAnswer.Missed` holds what the learner
 * tapped, and the option tiles are matched against it by text.
 */
fun LessonStep.Choice.wrongOption(): String =
    options.filterIndexed { index, _ -> index != correctIndex }.first().render()

/** A catalog run in the render locale, outside composition. */
fun CatalogText.render(): String = when (this) {
    is CatalogText.Value -> text
    is CatalogText.Words -> runBlocking { getString(getSystemResourceEnvironment(), res) }
    is CatalogText.Counted -> runBlocking {
        getPluralString(getSystemResourceEnvironment(), res, count, count)
    }
    is CatalogText.Formatted -> runBlocking {
        getString(getSystemResourceEnvironment(), res, *args.toTypedArray())
    }
}

/** An index this step rejects, for seeding a test that was not passed. */
fun wrongIndexFor(correctIndex: Int, optionCount: Int): Int =
    (correctIndex + 1) % optionCount

/**
 * A typed value this step rejects. Appending a digit changes any number the pad can produce, so
 * the fallback is only there for an answer that somehow normalises back to the original.
 */
fun LessonStep.Numeric.wrongTyped(): String {
    val candidate = answer.trim() + "1"
    return if (LearnCatalog.matchesNumericAnswer(candidate, answer)) answer.trim() + "7" else candidate
}
