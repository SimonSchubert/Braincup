package com.inspiredandroid.braincup.games.tools

/** Wall clock in milliseconds, for animation loops and flash timers. */
fun currentTimeMillis(): Long = kotlin.time.Clock.System.now().toEpochMilliseconds()
