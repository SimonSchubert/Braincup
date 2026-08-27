package com.inspiredandroid.braincup.locale

import java.util.Locale

// Captured before the first override so "System default" is restorable without a relaunch.
private val systemLocale: Locale = Locale.getDefault()

internal actual fun setPlatformLanguage(tag: String?) {
    Locale.setDefault(if (tag == null) systemLocale else Locale.forLanguageTag(tag))
}
