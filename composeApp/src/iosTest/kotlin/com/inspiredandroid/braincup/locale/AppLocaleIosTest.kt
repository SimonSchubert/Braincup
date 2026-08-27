package com.inspiredandroid.braincup.locale

import platform.Foundation.NSLocale
import platform.Foundation.preferredLanguages
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The iOS override writes AppleLanguages into NSUserDefaults, which only helps if
 * NSLocale.preferredLanguages re-reads it in the same process; both Compose's darwin locale
 * delegate and the resources library's iOS environment go through that property. If Foundation
 * ever starts caching it, the language changer would silently need an app restart, so pin it here.
 */
class AppLocaleIosTest {
    @AfterTest
    fun restoreSystemLanguage() {
        AppLocale.apply(null)
    }

    private fun preferredLanguage(): String? = NSLocale.preferredLanguages.firstOrNull() as String?

    @Test
    fun overrideIsVisibleImmediately() {
        AppLocale.apply("de")
        assertEquals("de", preferredLanguage())
    }

    @Test
    fun clearingTheOverrideRestoresTheSystemLanguage() {
        val system = preferredLanguage()
        AppLocale.apply("ja")
        assertEquals("ja", preferredLanguage())
        AppLocale.apply(null)
        assertEquals(system, preferredLanguage())
    }
}
