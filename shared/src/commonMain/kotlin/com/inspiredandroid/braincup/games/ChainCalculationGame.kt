package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.addString
import com.inspiredandroid.braincup.games.tools.Calculator
import kotlin.random.Random

/**
 * Generates each round a chain calculation e.g.: 1+(9-2)+4-3
 *
 * Logic:
 * - Enabled operators = +-*
 * - Generates operators randomly
 * - Multiplier max number is 6
 * - Plus and minus max number is 9
 * - Size of chain increases each round by 1
 * - Adds brackets after round 2
 * - No multiplications in a row
 * - Result is always positive
 */
class ChainCalculationGame : Game() {

    var calculation = ""
    private var numberCount = 2
    private var result = 0
    private var lastOperator = PLUS

    companion object {
        const val PLUS: Char = '+'
        const val MINUS: Char = '-'
        const val MULTIPLY: Char = '*'
    }

    override fun nextRound() {
        calculation = ""
        for (i in 0 until numberCount) {
            val maxNumber = if (lastOperator == MULTIPLY) {
                7
            } else {
                10
            }
            calculation += Random.nextInt(2, maxNumber)
            if (i != numberCount - 1) {
                lastOperator = getRandomOperator()
                calculation += lastOperator
            }
        }
        // add brackets
        if (numberCount > 4) {
            var bracketStart = Random.nextInt(calculation.length - 2)
            if (bracketStart % 2 != 0) {
                bracketStart -= 1
            }
            var bracketEnd = Random.nextInt(bracketStart + 2, calculation.length)
            if (bracketEnd % 2 == 0) {
                bracketEnd += 1
            }
            calculation = calculation.addString("(", bracketStart)
            calculation = calculation.addString(")", bracketEnd + 1)
        }

        // replace consecutive multiplications with '+'
        var previousOperator = PLUS
        calculation.forEachIndexed { index, c ->
            if (c == MULTIPLY || c == MINUS || c == PLUS) {
                previousOperator = if (c == MULTIPLY && previousOperator == MULTIPLY) {
                    calculation = calculation.replaceRange(index, index + 1, PLUS + "")
                    PLUS
                } else {
                    c
                }
            }
        }

        result = Calculator.calculate(calculation).toInt()
        // replace '-' with '+' until result is positive
        while (result < 0) {
            calculation = calculation.replaceFirst(MINUS, PLUS)
            result = Calculator.calculate(calculation).toInt()
        }
        numberCount++
    }

    override fun isCorrect(input: String): Boolean {
        return try {
            result == Calculator.calculate(input).toInt()
        } catch (ignore: NumberFormatException) {
            false
        }
    }

    override fun solution(): String {
        return result.toString()
    }

    override fun getGameType(): GameType {
        return GameType.CHAIN_CALCULATION
    }

    private fun getRandomOperator(): Char {
        return when (Random.nextInt(
            0, 3
        )) {
            0 -> PLUS
            1 -> MINUS
            else -> MULTIPLY
        }
    }
}