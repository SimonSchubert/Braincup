package com.inspiredandroid.braincup.games.wordle

import java.util.Locale

// Per-app language (localeConfig) updates the app Configuration, not always Locale.getDefault().
actual fun deviceLanguageTag(): String = currentAppLocale().toLanguageTag().lowercase()

internal fun currentAppLocale(): Locale {
    WordleAppContext.applicationContext?.let { ctx ->
        val config = ctx.resources.configuration
        if (!config.locales.isEmpty) {
            return config.locales[0]
        }
    }
    return Locale.getDefault()
}
