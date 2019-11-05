package com.inspiredandroid.braincup

import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.games.*
import com.inspiredandroid.braincup.games.tools.Calculator
import kotlin.test.Test
import kotlin.test.assertEquals

class Tests {

    @Test
    fun calculator() {
        assertEquals(3.0, Calculator.calculate("1+2"))
        assertEquals(3.9f, Calculator.calculate("1.5+2.4").toFloat())
        assertEquals(3.0, Calculator.calculate("2--1"))
        assertEquals(-1.0, Calculator.calculate("-2+1"))
        assertEquals(-30.0, Calculator.calculate("20-50"))
        assertEquals(25.0, Calculator.calculate("(1+2)*5+12-4/(10/5)"))
        assertEquals(2.0, Calculator.calculate("2*(10-(5+4))"))
        assertEquals(4.0, Calculator.calculate(" 2 + 2 "))
        assertEquals(4.0, Calculator.calculate("2+( 2 )"))
        assertEquals(0.0, Calculator.calculate("2+("))
        assertEquals(0.0, Calculator.calculate("2+)"))
        assertEquals(0.0, Calculator.calculate("2+)("))
        assertEquals(0.0, Calculator.calculate("2+())"))
        assertEquals(0.0, Calculator.calculate("2+(()"))
        assertEquals(0.0, Calculator.calculate("&#1-5@"))
    }

    @Test
    fun extensionFunctions() {
        assertEquals("", "   ".removeWhitespaces())
        assertEquals("123", " 1 2 3  ".removeWhitespaces())
        assertEquals("Hello Kotlin World!", "Hello World!".addString(" Kotlin", 5))
        assertEquals("121", "11".addString("2", 1))
    }

    @Test
    fun storage() {
        val storage = UserStorage()
        storage.putScore(GameType.CHAIN_CALCULATION.getId(), 99)

        // assertEquals(99, storage.getHighScore(GameType.CHAIN_CALCULATION.getId()))
    }

    @Test
    fun fractionCalculation() {
        val game = FractionCalculationGame()
        game.nextRound()
        assertEquals(game.solution(), Calculator.calculate(game.calculation).toInt().toString())
    }

    @Test
    fun chainCalculation() {
        val game = ChainCalculationGame()
        game.nextRound()
        assertEquals(game.solution(), Calculator.calculate(game.calculation).toInt().toString())
    }

    @Test
    fun mentalCalculation() {
        val game = MentalCalculationGame()
        game.nextRound()
        assertEquals(game.solution(), Calculator.calculate(game.calculation).toInt().toString())
    }

    @Test
    fun heightComparison() {
        val game = HeightComparisonGame()
        game.nextRound()
        var highestResult = -1
        var highestIndex = -1
        game.answers.forEachIndexed { index, s ->
            if(Calculator.calculate(s) > highestResult) {
                highestIndex = index+1
                highestResult = Calculator.calculate(s).toInt()
            }
        }
        assertEquals(game.solution(), highestIndex.toString())
    }

    @Test
    fun colorConfusion() {
        val game = ColorConfusionGame()
        game.nextRound()
        var points = 0
        if(game.answerShape == game.displayedShape) {
            points += game.shapePoints
        }
        if(game.answerColor == game.displayedColor) {
            points += game.colorPoints
        }
        assertEquals(game.points(), points.toString())
    }

    @Test
    fun sherlockCalculation() {
        val game = SherlockCalculationGame()
        game.nextRound()
    }
}
