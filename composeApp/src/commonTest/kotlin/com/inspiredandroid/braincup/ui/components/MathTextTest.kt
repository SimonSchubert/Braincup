package com.inspiredandroid.braincup.ui.components

import androidx.compose.ui.graphics.Color
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import com.inspiredandroid.braincup.ui.theme.WorkingBlue
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

    /** The colour each run of a formula card is printed in, in order. */
    private fun String.runColors(): List<Pair<String, Color>> {
        val colored = formatMathSymbols(fractionSlash = true).withFormulaColors(structure = Structure)
        return colored.spanStyles.map { colored.text.substring(it.start, it.end) to it.item.color }
    }

    private fun String.colorOf(value: String): Color? = runColors().firstOrNull { it.first.trim() == value }?.second

    /**
     * A formula that states its result prints that result in the answer green.
     *
     * It is the same number the figure beside the card marks in green, so printing it in the
     * given orange had the card and the picture disagreeing about which number was the answer.
     */
    @Test
    fun aStatedResultTakesTheAnswerColour() {
        assertEquals(SuccessGreen, "7 + {b:3} = 10".colorOf("10"))
        assertEquals(Primary, "7 + {b:3} = 10".colorOf("7"))
        assertEquals(WorkingBlue, "7 + {b:3} = 10".colorOf("3"))

        assertEquals(SuccessGreen, "360 - 170 = 190".colorOf("190"))
        assertEquals(SuccessGreen, "-4 * 10^3 = -4000".colorOf("-4000"))
        assertEquals(SuccessGreen, "35% = 0.35".colorOf("0.35"))
        assertEquals(SuccessGreen, "3/5 + {b:1/5} = 4/5".colorOf("4"))
    }

    /**
     * A number after an equals sign is only sometimes an answer, and each of these would be wrong.
     *
     * The `?` cases matter most: there the unknown is the question mark and the number after the
     * equals is a given, so colouring it green states the opposite of what the step asks.
     */
    @Test
    fun aTrailingNumberThatIsNotAnAnswerStaysGiven() {
        assertEquals(Primary, "65 + ? = 180".colorOf("180"))
        assertEquals(Primary, "90 + 90 + 100 + ? = 360".colorOf("360"))
        assertEquals(Primary, "k = √9 = 3".colorOf("3"))
        assertEquals(Primary, "17 / 5 = 3 r 2".colorOf("2"))
        assertEquals(Primary, "full turn = 360 degrees".colorOf("360"))
    }

    /** A question that has resolved keeps colouring its answer through the `{c:}` it was given. */
    @Test
    fun aResolvedQuestionStillAnswersThroughItsTag() {
        assertEquals(SuccessGreen, "12 + {b:4} = {c:16}".colorOf("16"))
    }

    private companion object {
        val Structure = Color(0xFF666666)
    }
}
