package com.inspiredandroid.braincup.navigation

import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.app.Instructions
import com.inspiredandroid.braincup.app.IqTestIntro
import com.inspiredandroid.braincup.app.MatchstickRiddlesMenu
import com.inspiredandroid.braincup.app.NormalChessMenu
import com.inspiredandroid.braincup.app.NormalSudokuMenu
import com.inspiredandroid.braincup.app.PegSolitaire
import com.inspiredandroid.braincup.app.SessionInterstitial
import com.inspiredandroid.braincup.games.GameType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * A launcher shortcut stores nothing but the route path suffix, so every game a shortcut can point
 * at has to survive the round trip through [navRouteToPathSuffix] and [pathSuffixToNavRoute].
 */
class LauncherShortcutsTest {

    @Test
    fun everyGameTypeRoundTripsToItsInstructions() {
        GameType.entries.forEach { gameType ->
            val route = Instructions(gameType.id)
            val suffix = navRouteToPathSuffix(route)
            assertTrue(suffix.isNotEmpty(), "${gameType.name} has no path suffix")
            assertEquals(route, pathSuffixToNavRoute(suffix), "${gameType.name} did not round trip")
        }
    }

    @Test
    fun everyUntimedGameRoundTripsToItsMenu() {
        listOf(NormalSudokuMenu, NormalChessMenu, MatchstickRiddlesMenu, PegSolitaire, IqTestIntro)
            .forEach { route ->
                val suffix = navRouteToPathSuffix(route)
                assertTrue(suffix.isNotEmpty(), "$route has no path suffix")
                assertEquals(route, pathSuffixToNavRoute(suffix), "$route did not round trip")
            }
    }

    @Test
    fun dailyChallengeRoundTrips() {
        val suffix = navRouteToPathSuffix(SessionInterstitial)
        assertEquals("session", suffix)
        assertEquals(SessionInterstitial, pathSuffixToNavRoute(suffix))
    }

    @Test
    fun aRetiredGameResolvesToNothing() {
        assertNull(pathSuffixToNavRoute("NotAGameAnyMore"))
    }

    @Test
    fun theLauncherShowsFewerGamesThanStorageKeeps() {
        assertTrue(MAX_RECENT_SHORTCUTS < UserStorage.RECENT_GAMES_LIMIT)
    }
}
