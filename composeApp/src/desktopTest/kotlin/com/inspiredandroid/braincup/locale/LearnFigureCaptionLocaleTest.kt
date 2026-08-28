package com.inspiredandroid.braincup.locale

import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.learn_fig_rows
import braincup.composeapp.generated.resources.learn_fig_sides
import braincup.composeapp.generated.resources.learn_fig_solid_cube
import braincup.composeapp.generated.resources.learn_fig_symmetry_lines
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.PluralStringResource
import org.jetbrains.compose.resources.getPluralString
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.getSystemResourceEnvironment
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The words a Learn figure draws on itself, checked in the languages that stress them.
 *
 * A figure captions itself inside a `DrawScope`, so these go through `LearnVisualStrings` rather
 * than an ordinary `stringResource` call at a composable. Four of them are `<plurals>` - the only
 * ones in the project - because "4 sides" and "3 rows" need agreement in most languages here and
 * English never showed that. Nothing else exercises plural selection, so pin it: a Compose upgrade
 * that stopped honouring CLDR would otherwise show up as Russian quietly printing the wrong case.
 */
class LearnFigureCaptionLocaleTest {

    @AfterTest
    fun restoreSystemLanguage() {
        AppLocale.apply(null)
    }

    private fun plural(resource: PluralStringResource, count: Int): String = runBlocking {
        getPluralString(getSystemResourceEnvironment(), resource, count, count)
    }

    private fun cube(): String = runBlocking {
        getString(getSystemResourceEnvironment(), Res.string.learn_fig_solid_cube)
    }

    @Test
    fun aSolidIsNamedInTheAppLanguage() {
        AppLocale.apply("de")
        assertEquals("Würfel", cube())
        AppLocale.apply("fr")
        assertEquals("cube", cube())
        AppLocale.apply("ja")
        assertEquals("立方体", cube())
    }

    /** English distinguishes one from everything else, and a polygon never has one side. */
    @Test
    fun englishTakesThePluralForm() {
        AppLocale.apply("en")
        assertEquals("1 line of symmetry", plural(Res.plurals.learn_fig_symmetry_lines, 1))
        assertEquals("4 lines of symmetry", plural(Res.plurals.learn_fig_symmetry_lines, 4))
        assertEquals("5 sides,", plural(Res.plurals.learn_fig_sides, 5))
    }

    /**
     * Russian needs three forms across the range a figure can draw: 2-4 sides take one ending and
     * 5 and up another, which is the case a two-form plural gets wrong and nothing else would
     * catch.
     */
    @Test
    fun russianPicksFewAndManySeparately() {
        AppLocale.apply("ru")
        assertEquals("3 стороны,", plural(Res.plurals.learn_fig_sides, 3))
        assertEquals("7 сторон,", plural(Res.plurals.learn_fig_sides, 7))
        assertEquals("3 ряда", plural(Res.plurals.learn_fig_rows, 3))
        assertEquals("8 рядов", plural(Res.plurals.learn_fig_rows, 8))
    }

    /** Polish splits the same range differently again, so it is not just Russian's table. */
    @Test
    fun polishPicksItsOwnSplit() {
        AppLocale.apply("pl")
        assertEquals("3 boki,", plural(Res.plurals.learn_fig_sides, 3))
        assertEquals("7 boków,", plural(Res.plurals.learn_fig_sides, 7))
    }

    /** A language with no plural distinction should still resolve, from its one form. */
    @Test
    fun aLanguageWithoutPluralsStillResolves() {
        AppLocale.apply("ja")
        assertEquals("3 行", plural(Res.plurals.learn_fig_rows, 3))
        assertEquals("8 行", plural(Res.plurals.learn_fig_rows, 8))
    }

    /**
     * Every offered language resolves every figure plural over the whole range a figure can draw:
     * a polygon has 3 to 12 sides, an array band 2 to 10 rows, a shape 1 to 6 lines of symmetry.
     *
     * A form a language needs but does not carry is not an error - Compose falls back to `other` -
     * so this cannot catch a wrong ending. What it does catch is the thing that would be silent
     * and total: a locale file where a plural went missing, or a quantity Compose refuses.
     */
    @Test
    fun everyLanguageResolvesEveryFigurePluralOverItsWholeRange() {
        val ranges = listOf(
            Res.plurals.learn_fig_sides to 3..12,
            Res.plurals.learn_fig_rows to 2..10,
            Res.plurals.learn_fig_symmetry_lines to 1..6,
        )
        val blank = mutableListOf<String>()
        supportedAppLanguages.forEach { language ->
            AppLocale.apply(language.tag)
            ranges.forEach { (resource, range) ->
                range.forEach { count ->
                    val text = plural(resource, count)
                    if (text.isBlank() || !text.contains(count.toString())) {
                        blank += "${language.tag}/$count: '$text'"
                    }
                }
            }
        }
        assertEquals(emptyList(), blank)
    }
}
