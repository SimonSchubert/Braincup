package com.inspiredandroid.braincup.navigation

import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.app.PegSolitaire
import com.inspiredandroid.braincup.games.GameCategory
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.locale.AppLocale
import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.runBlocking
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Launcher shortcuts are built outside any composition, so their labels come from the non-composable
 * `getString` path. It reads the platform default that [AppLocale] mutates, which is what lets a
 * shortcut follow the in-app language rather than the device one.
 */
class LauncherShortcutLabelsTest {

    @AfterTest
    fun restoreSystemLanguage() {
        AppLocale.apply(null)
    }

    private fun shortcuts(configure: UserStorage.() -> Unit = {}) = runBlocking {
        launcherShortcuts(UserStorage(MapSettings()).apply(configure))
    }

    @Test
    fun freshPlayerGetsOnlyTheDailyChallenge() {
        AppLocale.apply("en")
        val entries = shortcuts()
        assertEquals(1, entries.size)
        assertEquals("Daily Challenge", entries.single().label)
        assertEquals(null, entries.single().category)
    }

    @Test
    fun labelsFollowTheInAppLanguage() {
        AppLocale.apply("de")
        assertEquals("Tägliche Herausforderung", shortcuts().single().label)
    }

    @Test
    fun recentGamesFollowTheDailyChallengeNewestFirst() {
        AppLocale.apply("en")
        val entries = shortcuts {
            putRecentGame(GameType.MENTAL_CALCULATION.urlSlug)
            putRecentGame(navRouteToPathSuffix(PegSolitaire))
        }
        assertEquals(
            listOf("Daily Challenge", "Peg Solitaire", "Mental Calculation"),
            entries.map { it.label },
        )
        assertEquals(
            listOf(null, GameCategory.LOGIC, GameCategory.MATH),
            entries.map { it.category },
        )
    }

    @Test
    fun onlyTheThreeMostRecentGamesAreOffered() {
        AppLocale.apply("en")
        val entries = shortcuts {
            GameType.entries.take(UserStorage.RECENT_GAMES_LIMIT).forEach { putRecentGame(it.urlSlug) }
        }
        assertEquals(1 + MAX_RECENT_SHORTCUTS, entries.size)
    }

    @Test
    fun aRetiredGameIsSkippedWithoutDroppingTheRest() {
        AppLocale.apply("en")
        val entries = shortcuts {
            putRecentGame(GameType.MENTAL_CALCULATION.urlSlug)
            putRecentGame("NotAGameAnyMore")
        }
        assertEquals(listOf("Daily Challenge", "Mental Calculation"), entries.map { it.label })
    }

    @Test
    fun colorVisionGamesAreHiddenWhileTheColorblindPaletteIsOn() {
        AppLocale.apply("en")
        val entries = shortcuts {
            setColorblindPaletteEnabled(true)
            putRecentGame(GameType.COLOR_CONFUSION.urlSlug)
            putRecentGame(GameType.MENTAL_CALCULATION.urlSlug)
        }
        assertEquals(listOf("Daily Challenge", "Mental Calculation"), entries.map { it.label })
    }
}
