package com.inspiredandroid.braincup.games.wordle

import kotlin.js.JsString

@JsFun("() => (navigator.language || 'en')")
private external fun navigatorLanguage(): JsString

// navigator.language is a BCP-47 tag like "en-US"; WordleLanguages.resolve trims the region.
actual fun deviceLanguageTag(): String = navigatorLanguage().toString().lowercase()
