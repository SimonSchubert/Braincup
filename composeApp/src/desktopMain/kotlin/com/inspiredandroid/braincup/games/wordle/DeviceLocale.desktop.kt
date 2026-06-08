package com.inspiredandroid.braincup.games.wordle

import java.util.Locale

actual fun deviceLanguageTag(): String = Locale.getDefault().toLanguageTag().lowercase()
