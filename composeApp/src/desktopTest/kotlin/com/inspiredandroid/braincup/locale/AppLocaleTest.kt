package com.inspiredandroid.braincup.locale

import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.settings_title
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.getSystemResourceEnvironment
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Compose Multiplatform exposes no public API for the resource locale, so the language changer
 * works by mutating the platform default that `Locale.current` reads. That is an internal contract
 * of the resources library, so pin it: if a Compose upgrade stops honouring the JVM default, these
 * fail instead of the app silently going back to rendering in the device language.
 */
class AppLocaleTest {
    @AfterTest
    fun restoreSystemLanguage() {
        AppLocale.apply(null)
    }

    private fun settingsTitle(): String = runBlocking {
        getString(getSystemResourceEnvironment(), Res.string.settings_title)
    }

    @Test
    fun overrideChangesTheResolvedString() {
        AppLocale.apply("de")
        assertEquals("Einstellungen", settingsTitle())
    }

    @Test
    fun regionSubtagPicksTheRegionalResource() {
        // zh-TW must land on values-zh-rTW, not values-zh: Compose matches the region qualifier
        // separately, so a tag that loses its region silently falls back to Simplified.
        AppLocale.apply("zh-TW")
        assertEquals("設定", settingsTitle())
        AppLocale.apply("zh")
        assertEquals("设置", settingsTitle())
    }

    @Test
    fun everyOfferedLanguageResolvesToItsOwnTranslation() {
        // English is the base resource, so it has no values-en to distinguish it.
        val translated = supportedAppLanguages.filter { it.tag != "en" }
        val fallingBackToEnglish = translated.filter { language ->
            AppLocale.apply(language.tag)
            settingsTitle() == "Settings"
        }
        assertEquals(emptyList(), fallingBackToEnglish.map { it.tag })
    }

    @Test
    fun clearingTheOverrideRestoresTheSystemLanguage() {
        val system = settingsTitle()
        AppLocale.apply("de")
        AppLocale.apply(null)
        assertEquals(system, settingsTitle())
    }
}
