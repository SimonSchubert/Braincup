package com.inspiredandroid.braincup.games.wordle

import android.content.Context

/** Holds the application [Context] so Wordle can read the app locale outside composables. */
internal object WordleAppContext {
    var applicationContext: Context? = null
}
