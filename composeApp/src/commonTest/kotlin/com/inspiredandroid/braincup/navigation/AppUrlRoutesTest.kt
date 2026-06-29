package com.inspiredandroid.braincup.navigation

import com.inspiredandroid.braincup.app.Instructions
import com.inspiredandroid.braincup.app.MainMenu
import com.inspiredandroid.braincup.app.NormalSudokuPlay
import com.inspiredandroid.braincup.app.Playing
import com.inspiredandroid.braincup.app.Scoreboard
import com.inspiredandroid.braincup.app.Settings
import com.inspiredandroid.braincup.games.GameType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AppUrlRoutesTest {

    @Test
    fun gameTypeUrlSlug_catQueens() {
        assertEquals("CatQueens", GameType.CAT_QUEENS.urlSlug)
    }

    @Test
    fun gameTypeFromUrlSlug_roundTrip() {
        assertEquals(GameType.CAT_QUEENS, GameType.fromUrlSlug("CatQueens"))
        assertNull(GameType.fromUrlSlug("NotAGame"))
    }

    @Test
    fun navRouteToPathSuffix_mainMenu() {
        assertEquals("", navRouteToPathSuffix(MainMenu))
    }

    @Test
    fun navRouteToPathSuffix_catQueensInstructions() {
        assertEquals(
            "CatQueens",
            navRouteToPathSuffix(Instructions(GameType.CAT_QUEENS.id)),
        )
        assertEquals(
            "CatQueens",
            navRouteToPathSuffix(Playing(GameType.CAT_QUEENS.id)),
        )
    }

    @Test
    fun navRouteToPathSuffix_settings() {
        assertEquals("settings", navRouteToPathSuffix(Settings))
    }

    @Test
    fun navRouteToPathSuffix_sudokuPlay() {
        assertEquals("sudoku/easy-1", navRouteToPathSuffix(NormalSudokuPlay("easy-1")))
    }

    @Test
    fun navRouteToPathSuffix_scoreboard() {
        assertEquals(
            "CatQueens/scores",
            navRouteToPathSuffix(Scoreboard(GameType.CAT_QUEENS.id)),
        )
    }

    @Test
    fun pathSuffixToNavRoute_catQueensDeepLink() {
        assertEquals(
            Instructions(GameType.CAT_QUEENS.id),
            pathSuffixToNavRoute("CatQueens"),
        )
    }

    @Test
    fun pathSuffixToNavRoute_settings() {
        assertEquals(Settings, pathSuffixToNavRoute("settings"))
    }

    @Test
    fun pathSuffixToNavRoute_sudokuPlay() {
        assertEquals(NormalSudokuPlay("easy-1"), pathSuffixToNavRoute("sudoku/easy-1"))
    }

    @Test
    fun pathSuffixToNavRoute_unknownPath() {
        assertNull(pathSuffixToNavRoute("UnknownGame"))
        assertNull(pathSuffixToNavRoute(""))
    }

    @Test
    fun detectWebBasePath_githubPages() {
        assertEquals("/Braincup", detectWebBasePath("/Braincup/CatQueens"))
        assertEquals("/Braincup", detectWebBasePath("/Braincup/"))
    }

    @Test
    fun detectWebBasePath_localDev() {
        assertEquals("", detectWebBasePath("/"))
        assertEquals("", detectWebBasePath("/CatQueens"))
    }
}