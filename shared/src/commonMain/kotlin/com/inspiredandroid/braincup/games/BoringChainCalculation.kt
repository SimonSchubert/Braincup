package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.addString
import com.inspiredandroid.braincup.games.tools.Calculator
import kotlin.random.Random

class BoringChainCalculation : Game() {

    var numberCount = 4
    var result = 0
    var calculation = ""
    var lastOperator = ""

    override fun nextRound() {
        calculation = ""
        for (i in 0 until numberCount) {
            val maxNumber = if(lastOperator == "*") {
                7
            } else {
                10
            }
            calculation += Random.nextInt(2, maxNumber)
            if(i != numberCount-1) {
                lastOperator = getRandomOperator()
                calculation += lastOperator
            }
        }
        // add brakets
        if(numberCount > 6) {
            var bracketStart = Random.nextInt(calculation.length-2)
            if (bracketStart % 2 != 0) {
                bracketStart -= 1
            }
            var bracketEnd = Random.nextInt(bracketStart+2, calculation.length)
            if (bracketEnd % 2 == 0) {
                bracketEnd += 1
            }
            calculation = calculation.addString("(", bracketStart)
            calculation = calculation.addString(")", bracketEnd+1)
        }

        result = Calculator.calc(calculation).toInt()
        // replace - with + until result is positive
        while(result < 0) {
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