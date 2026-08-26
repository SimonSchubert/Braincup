package com.inspiredandroid.braincup.navigation

import com.inspiredandroid.braincup.app.Accounts
import com.inspiredandroid.braincup.app.Instructions
import com.inspiredandroid.braincup.app.LearnCertificate
import com.inspiredandroid.braincup.app.LearnLessonPlay
import com.inspiredandroid.braincup.app.LearnMenu
import com.inspiredandroid.braincup.app.LearnTest
import com.inspiredandroid.braincup.app.LearnTopicDetail
import com.inspiredandroid.braincup.app.LearnUnitDetail
import com.inspiredandroid.braincup.app.MainMenu
import com.inspiredandroid.braincup.app.NormalSudokuPlay
import com.inspiredandroid.braincup.app.PegSolitaire
import com.inspiredandroid.braincup.app.Playing
import com.inspiredandroid.braincup.app.Scoreboard
import com.inspiredandroid.braincup.app.Settings
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.learn.LearnCatalog
import com.inspiredandroid.braincup.learn.MathTopic
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
    fun navRouteToPathSuffix_accounts() {
        assertEquals("accounts", navRouteToPathSuffix(Accounts))
    }

    @Test
    fun pathSuffixToNavRoute_accounts() {
        assertEquals(Accounts, pathSuffixToNavRoute("accounts"))
    }

    @Test
    fun pathSuffixToNavRoute_sudokuPlay() {
        assertEquals(NormalSudokuPlay("easy-1"), pathSuffixToNavRoute("sudoku/easy-1"))
    }

    @Test
    fun navRouteToPathSuffix_pegSolitaire() {
        assertEquals("peg-solitaire", navRouteToPathSuffix(PegSolitaire))
    }

    @Test
    fun pathSuffixToNavRoute_pegSolitaire() {
        assertEquals(PegSolitaire, pathSuffixToNavRoute("peg-solitaire"))
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

    @Test
    fun navRouteToPathSuffix_learn() {
        val fractions = requireNotNull(LearnCatalog.unitBySlug(MathTopic.ARITHMETIC, "fractions"))
        assertEquals("learn", navRouteToPathSuffix(LearnMenu))
        assertEquals("learn/arithmetic", navRouteToPathSuffix(LearnTopicDetail(MathTopic.ARITHMETIC.id)))
        assertEquals("learn/arithmetic/fractions", navRouteToPathSuffix(LearnUnitDetail(fractions.id)))
        assertEquals("learn/arithmetic/fractions/test", navRouteToPathSuffix(LearnTest(fractions.id)))
        assertEquals(
            "learn/arithmetic/fractions/certificate",
            navRouteToPathSuffix(LearnCertificate(fractions.id)),
        )
    }

    @Test
    fun pathSuffixToNavRoute_learnRoundTrip() {
        val shapes = requireNotNull(LearnCatalog.unitBySlug(MathTopic.GEOMETRY, "shapes"))
        assertEquals(LearnMenu, pathSuffixToNavRoute("learn"))
        assertEquals(
            LearnTopicDetail(MathTopic.GEOMETRY.id),
            pathSuffixToNavRoute("learn/geometry"),
        )
        assertEquals(
            LearnUnitDetail(shapes.id),
            pathSuffixToNavRoute("learn/geometry/shapes"),
        )
        assertEquals(
            LearnTest(shapes.id),
            pathSuffixToNavRoute("learn/geometry/shapes/test"),
        )
        assertEquals(
            LearnCertificate(shapes.id),
            pathSuffixToNavRoute("learn/geometry/shapes/certificate"),
        )
    }

    @Test
    fun learnLessonPathRoundTrips() {
        val lessonId = LearnCatalog.allLessons.first().id
        assertEquals("learn/lesson/$lessonId", navRouteToPathSuffix(LearnLessonPlay(lessonId)))
        assertEquals(LearnLessonPlay(lessonId), pathSuffixToNavRoute("learn/lesson/$lessonId"))
    }

    @Test
    fun unknownLearnPathsAreRejected() {
        assertNull(pathSuffixToNavRoute("learn/grade-99"))
        assertNull(pathSuffixToNavRoute("learn/grade-9-10/not-a-topic"))
        assertNull(pathSuffixToNavRoute("learn/grade-9-10/algebra/nope"))
        assertNull(pathSuffixToNavRoute("learn/lesson/not-a-lesson"))
    }

    /** Calculus is not taught in grade 1-2, so that pairing must not resolve to anything. */
    @Test
    fun learnTopicsNotTaughtAtALevelAreRejected() {
        assertNull(pathSuffixToNavRoute("learn/grade-1-2/calculus"))
        assertNull(pathSuffixToNavRoute("learn/grade-1-2/calculus/test"))
    }
}
