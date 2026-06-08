package com.inspiredandroid.braincup.games.wordle

import android.os.Build
import java.util.Locale

// Per-app language (localeConfig) updates the app Configuration, not always Locale.getDefault().
actual fun deviceLanguageTag(): String = currentAppLocale().toLanguageTag().lowercase()

internal fun currentAppLocale(): Locale {
    WordleAppContext.applicationContext?.let { ctx ->
        val config = ctx.resources.configuration
        if (Build.VERSION.SDK_INT >= 24) {
            if (!config.locales.isEmpty) {
                return config.locales[0]
            }
        } else {
            @Suppress("DEPRECATION")
            return config.locale
        }
    }
    return Locale.getDefault()
}
