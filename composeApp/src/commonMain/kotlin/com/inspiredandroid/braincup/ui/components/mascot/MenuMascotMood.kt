package com.inspiredandroid.braincup.ui.components.mascot

private const val STREAK_WORRY_HOUR = 18
private const val BEDTIME_HOUR = 22
private const val WAKE_HOUR = 5

/**
 * The mood the main menu mascot holds for the player's situation. A streak that breaks tonight
 * outranks bedtime, since it is the one the player can still do something about.
 */
internal fun menuMascotMood(localHour: Int, streakAtRisk: Boolean): MascotMood = when {
    streakAtRisk && localHour >= STREAK_WORRY_HOUR -> MascotMood.WORRIED
    localHour >= BEDTIME_HOUR || localHour < WAKE_HOUR -> MascotMood.SLEEPY
    else -> MascotMood.NEUTRAL
}
