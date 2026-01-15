package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.addString
import com.inspiredandroid.braincup.games.tools.Calculator
import com.inspiredandroid.braincup.games.tools.Operator
import com.inspiredandroid.braincup.games.tools.toChar
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
    private var lastOperator = Operator.PLUS

    val availableOperators by lazy {
        listOf(Operator.PLUS, Operator.MINUS, Operator.MULTIPLY)
    }

    override fun nextRound() {
        calculation = ""
        for (i in 0 until numberCount) {
            val maxNumber = if (lastOperator == Operator.MULTIPLY) {
                7
            } else {
                10
            }
            calculation += Random.nextInt(2, maxNumber)
            if (i != numberCount - 1) {
                lastOperator = availableOperators.random()
                calculation += lastOperator.toChar()
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
        var previousOperator = ' '
        calculation.forEachIndexed { index, c ->
            if (c == Operator.MULTIPLY.toChar() || c == Operator.MINUS.toChar() || c == Operator.PLUS.toChar()) {
                previousOperator =
                    if (c == Operator.MULTIPLY.toChar() && previousOperator == Operator.MULTIPLY.toChar()) {
                        calculation =
                            calculation.replaceRange(index, index + 1, Operator.PLUS.toChar() + "")
                        Operator.PLUS.toChar()
                    } else {
                        c
                    }
            }
        }

        result = Calculator.calculate(calculation).toInt()
        // replace '-' with '+' until result is positive
        while (result < 0) {
            calculation = calculation.replaceFirst(Operator.MINUS.toChar(), Operator.PLUS.toChar())
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

    override fun hint(): String? {
        return null
    }

    override fun getGameType(): GameType {
        return GameType.CHAIN_CALCULATION
    }
}