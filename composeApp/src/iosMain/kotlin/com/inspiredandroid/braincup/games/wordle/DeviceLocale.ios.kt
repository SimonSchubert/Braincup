package com.inspiredandroid.braincup.games.wordle

import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

// languageCode is already just the language part ("en" for an "en-US" device);
// WordleLanguages.resolve also trims any region defensively.
actual fun deviceLanguageTag(): String = NSLocale.currentLocale.languageCode.lowercase()
