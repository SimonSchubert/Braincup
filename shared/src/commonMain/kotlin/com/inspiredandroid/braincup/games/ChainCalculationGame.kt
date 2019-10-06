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
 * - plus and minus max number is 9
 * - Size of chain increases each round by 1
 * - Adds brackets after round 2
 * - Result is always positive
 */
class ChainCalculationGame : Game() {

    var calculation = ""
    private var numberCount = 2
    private var result = 0
    private var lastOperator = ""

    override fun nextRound() {
        calculation = ""
        for (i in 0 until numberCount) {
            val maxNumber = if (lastOperator == "*") {
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

        result = Calculator.calc(calculation).toInt()
        // replace '-' with '+' until result is positive
        while (result < 0) {
            calculation = calculation.replaceFirst("-", "+")
            result = Calculator.calc(calculation).toInt()
        }
        numberCount++
    }

    override fun isCorrect(input: String): Boolean {
        return try {
            result == Calculator.calc(input).toInt()
        } catch (ignore: NumberFormatException) {
            false
        }
    }

    private fun getRandomOperator(): String {
        return when (Random.nextInt(
            0, 3
        )) {
            0 -> "+"
            1 -> "-"
            else -> "*"
        }
    }
}