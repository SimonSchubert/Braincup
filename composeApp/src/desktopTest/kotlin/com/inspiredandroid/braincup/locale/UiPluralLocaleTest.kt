package com.inspiredandroid.braincup.locale

import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.daily_challenge_subtitle_start
import braincup.composeapp.generated.resources.finish_best_tries
import braincup.composeapp.generated.resources.session_streak_current
import braincup.composeapp.generated.resources.solution_column_row
import braincup.composeapp.generated.resources.solution_figure
import braincup.composeapp.generated.resources.solution_pointing_right
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.PluralStringResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getPluralString
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.getSystemResourceEnvironment
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Counted UI lines and figure-feedback phrases that English can concatenate and other
 * languages cannot. Same [AppLocale] harness as [LearnFigureCaptionLocaleTest].
 */
class UiPluralLocaleTest {

    @AfterTest
    fun restoreSystemLanguage() {
        AppLocale.apply(null)
    }

    private fun plural(resource: PluralStringResource, count: Int): String = runBlocking {
        getPluralString(getSystemResourceEnvironment(), resource, count, count)
    }

    private fun string(resource: StringResource, vararg args: Any): String = runBlocking {
        getString(getSystemResourceEnvironment(), resource, *args)
    }

    @Test
    fun englishSingularAndPlural() {
        AppLocale.apply("en")
        assertEquals("Current streak: 1 day", plural(Res.plurals.session_streak_current, 1))
        assertEquals("Current streak: 2 days", plural(Res.plurals.session_streak_current, 2))
        assertEquals("Best: 1 try", plural(Res.plurals.finish_best_tries, 1))
        assertEquals("Best: 2 tries", plural(Res.plurals.finish_best_tries, 2))
        assertEquals("Play 4 games to keep your streak", plural(Res.plurals.daily_challenge_subtitle_start, 4))
    }

    @Test
    fun polishUsesFewForFourGames() {
        AppLocale.apply("pl")
        val four = plural(Res.plurals.daily_challenge_subtitle_start, 4)
        val five = plural(Res.plurals.daily_challenge_subtitle_start, 5)
        assertTrue(four.contains("gry"), four)
        assertFalse(four.contains("gier"), four)
        assertTrue(five.contains("gier"), five)
        assertEquals("Obecna seria: 1 dzień", plural(Res.plurals.session_streak_current, 1))
        assertEquals("Obecna seria: 2 dni", plural(Res.plurals.session_streak_current, 2))
        assertEquals("Obecna seria: 5 dni", plural(Res.plurals.session_streak_current, 5))
    }

    @Test
    fun germanNamesTheShapeThenTheColour() {
        AppLocale.apply("de")
        assertEquals("Herz (Rot)", string(Res.string.solution_figure, "Rot", "Herz"))
    }

    @Test
    fun frenchPointingUsesTheFeminineArticle() {
        AppLocale.apply("fr")
        val right = string(Res.string.solution_pointing_right, "Rouge", "Cœur")
        assertTrue(right.contains("vers la droite"), right)
        assertFalse(right.contains("vers le droite"), right)
    }

    /**
     * Traditional Chinese swaps 行 and 列 versus Mainland. PathFinder passes column then row;
     * copying the Simplified wording would name the transposed cell.
     */
    @Test
    fun traditionalChineseDoesNotTransposeThePathFinderCell() {
        AppLocale.apply("zh-TW")
        assertEquals("第3行第2列", string(Res.string.solution_column_row, 3, 2))
        AppLocale.apply("zh")
        assertEquals("第3列第2行", string(Res.string.solution_column_row, 3, 2))
    }
}
