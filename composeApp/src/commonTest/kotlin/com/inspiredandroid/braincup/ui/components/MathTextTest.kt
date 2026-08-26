package com.inspiredandroid.braincup.ui.components

import kotlin.test.Test
import kotlin.test.assertEquals

class MathTextTest {

    @Test
    fun operatorsBecomeTheirMathGlyphs() {
        assertEquals("4 × 6 = 24", "4*6 = 24".formatMathSymbols())
        assertEquals("72 ÷ 9 = 8", "72/9 = 8".formatMathSymbols())
        assertEquals("2x + 3 ≥ 11", "2x+3 >= 11".formatMathSymbols())
        assertEquals("x ≤ 5", "x <= 5".formatMathSymbols())
    }

    /**
     * A tight slash is a fraction line in text about fractions and a division sign everywhere
     * else, so the same string has to come out differently depending on who is asking.
     */
    @Test
    fun onlyASpacedSlashDividesInFractionText() {
        assertEquals("3/4 of the bar", "3/4 of the bar".formatMathSymbols(fractionSlash = true))
        assertEquals("60 ÷ 5 = 12", "60 / 5 = 12".formatMathSymbols(fractionSlash = true))
        assertEquals("3 ÷ 4 of the bar", "3/4 of the bar".formatMathSymbols())
    }

    /**
     * The sign of a negative number belongs to the number. Spacing every hyphen alike rendered
     * "-4 - 6" as " - 4 - 6", which only showed up once the lessons went left of zero.
     */
    @Test
    fun aNegativeSignStaysAttachedToItsNumber() {
        assertEquals("-4 - 6 = ?", "-4 - 6 = ?".formatMathSymbols())
        assertEquals("5 - (-3) = 8", "5 - (-3) = 8".formatMathSymbols())
        assertEquals("-3 x (-4) = 12", "-3 x (-4) = 12".formatMathSymbols())
        assertEquals("-7 + 12 = ?", "-7 + 12 = ?".formatMathSymbols())
    }

    /** A minus that subtracts still gets its room, whatever sits in front of it. */
    @Test
    fun aSubtractingMinusKeepsItsSpacing() {
        assertEquals("15 - 8 = 15 - 5 - 3", "15-8 = 15-5-3".formatMathSymbols())
        assertEquals("x - 3 = 5", "x-3 = 5".formatMathSymbols())
        assertEquals("(x - 2)(x + 5)", "(x-2)(x+5)".formatMathSymbols())
        assertEquals("50% - 10% = 40%", "50%-10% = 40%".formatMathSymbols())
    }

    /** Group tags survive formatting, because the colouring pass runs after it. */
    @Test
    fun groupTagsAreLeftIntactForTheColouringPass() {
        assertEquals("{a:-4} - {b:6} = ?", "{a:-4} - {b:6} = ?".formatMathSymbols())
        assertEquals("37 = {a:30} + {b:7}", "37 = {a:30}+{b:7}".formatMathSymbols())
    }
}
