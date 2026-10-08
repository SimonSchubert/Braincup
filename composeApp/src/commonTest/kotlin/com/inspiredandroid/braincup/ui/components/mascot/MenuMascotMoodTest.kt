package com.inspiredandroid.braincup.ui.components.mascot

import kotlin.test.Test
import kotlin.test.assertEquals

class MenuMascotMoodTest {
    @Test
    fun daytimeIsNeutral() {
        assertEquals(MascotMood.NEUTRAL, menuMascotMood(localHour = 12, streakAtRisk = false))
    }

    @Test
    fun streakAtRiskOnlyWorriesFromTheEvening() {
        assertEquals(MascotMood.NEUTRAL, menuMascotMood(localHour = 17, streakAtRisk = true))
        assertEquals(MascotMood.WORRIED, menuMascotMood(localHour = 18, streakAtRisk = true))
    }

    @Test
    fun nightIsSleepy() {
        assertEquals(MascotMood.SLEEPY, menuMascotMood(localHour = 22, streakAtRisk = false))
        assertEquals(MascotMood.SLEEPY, menuMascotMood(localHour = 3, streakAtRisk = false))
        assertEquals(MascotMood.NEUTRAL, menuMascotMood(localHour = 5, streakAtRisk = false))
    }

    @Test
    fun aStreakAtRiskKeepsItAwakeAtBedtime() {
        assertEquals(MascotMood.WORRIED, menuMascotMood(localHour = 23, streakAtRisk = true))
    }

    @Test
    fun afterMidnightTheNewDayHasOnlyJustStarted() {
        assertEquals(MascotMood.SLEEPY, menuMascotMood(localHour = 1, streakAtRisk = true))
    }
}
