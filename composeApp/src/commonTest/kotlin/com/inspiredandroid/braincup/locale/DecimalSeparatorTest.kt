package com.inspiredandroid.braincup.locale

import com.inspiredandroid.braincup.ui.components.formatMathSymbols
import com.inspiredandroid.braincup.ui.components.learn.FigureRole
import com.inspiredandroid.braincup.ui.components.learn.FigureRoles
import com.inspiredandroid.braincup.ui.components.withDecimalSeparator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The decimal separator, which is a correctness question rather than a cosmetic one.
 *
 * Every number in the Learn catalog is authored with a point and rendered once per language, so
 * this is the whole of what stands between a German learner and a formula card reading "0.35".
 */
class DecimalSeparatorTest {

    @Test
    fun theCommaLanguagesGetAComma() {
        listOf("de", "fr", "es", "it", "nl", "pt", "ru", "pl", "sv", "tr", "uk", "vi", "id")
            .forEach { assertEquals(',', decimalSeparatorFor(it), "$it writes a decimal comma") }
    }

    @Test
    fun thePointLanguagesKeepThePoint() {
        listOf("en", "ja", "zh", "ko", "he", "th", "hi", "bn", "ta", "ms", "ga", "fil", "ar", "fa")
            .forEach { assertEquals('.', decimalSeparatorFor(it), "$it writes a decimal point") }
    }

    /** Every language the app offers resolves to one of the two, and never to a glyph Rubik lacks. */
    @Test
    fun everySupportedLanguageResolves() {
        supportedAppLanguages.forEach { language ->
            val separator = decimalSeparatorFor(language.tag.substringBefore('-'))
            assertTrue(separator == '.' || separator == ',', "${language.tag} -> $separator")
        }
    }

    /** Only a point standing between two digits is a decimal point. */
    @Test
    fun onlyAPointBetweenDigitsMoves() {
        assertEquals("0,35 = 3 Zehntel", "0.35 = 3 Zehntel".withDecimalSeparator(','))
        assertEquals("Vier Spalten. 0,4 und nicht 0,04.", "Vier Spalten. 0.4 und nicht 0.04.".withDecimalSeparator(','))
        assertEquals("2,5 m = 250 cm", "2.5 m = 250 cm".withDecimalSeparator(','))
        assertEquals("Ende.", "Ende.".withDecimalSeparator(','))
        assertEquals("cm. 3", "cm. 3".withDecimalSeparator(','))
    }

    @Test
    fun theFormulaSeamCarriesIt() {
        assertEquals("0,5 × 4 = 2", "0.5*4 = 2".formatMathSymbols(decimalSeparator = ','))
        assertEquals("0.5 × 4 = 2", "0.5*4 = 2".formatMathSymbols())
    }

    /**
     * A figure states its values with a point whatever the language, because the catalog does, so
     * the role lookup has to accept the run the render seam actually produces.
     */
    @Test
    fun aFigureStillColoursACommaDecimal() {
        val roles = FigureRoles(given = setOf("0.35"), answer = setOf("0.75"))
        assertEquals(FigureRole.GIVEN, roles.roleOf("0,35"))
        assertEquals(FigureRole.ANSWER, roles.roleOf("0,75"))
        assertEquals(FigureRole.GIVEN, roles.roleOf("0.35"))
    }
}
