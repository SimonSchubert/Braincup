package com.inspiredandroid.braincup.games.tools

import com.inspiredandroid.braincup.addString
import com.inspiredandroid.braincup.removeWhitespaces
import com.inspiredandroid.braincup.validCalculationRegex

/**
 * Calculates the result of an expression given as a string.
 *
 * Supported tokens: + - * / ( )
 */
object Calculator {

    private const val bracketLeft = "("
    private const val bracketRight = ")"

    fun calculate(input: String): Double {
        var expression = input.removeWhitespaces()
        if (!validCalculationRegex.matches(expression)) {
            return 0.0
        }
        expression = expression.replace("--", "+")
        expression = expression.replace("-", "+-")
        expression = calculateInnerBrackets(expression)
        return solve(expression)
    }

    private fun solve(expression: String, operator: Operator = Operator.PLUS): Double {
        if (expression.isEmpty()) {
            return 0.0
        }
        return when (operator) {
            Operator.PLUS -> {
                var sum = 0.0
                expression.split(Operator.PLUS.char).forEachIndexed { index, term ->
                    val value = solve(term, Operator.MULTIPLY)
                    sum = if (index == 0) value else sum + value
                }
                sum
            }
            Operator.MULTIPLY -> {
                var product = 0.0
                expression.split(Operator.MULTIPLY.char).forEachIndexed { index, factor ->
                    val value = solve(factor, Operator.DIVIDE)
                    product = if (index == 0) value else product * value
                }
                product
            }
            Operator.DIVIDE -> {
                var quotient = 0.0
                expression.split(Operator.DIVIDE.char).forEachIndexed { index, divisor ->
                    val value = divisor.toDouble()
                    quotient = if (index == 0) value else quotient / value
                }
                quotient
            }
            Operator.MINUS -> expression.toDouble()
        }
    }

    private fun calculateInnerBrackets(input: String): String {
        var result = input
        while (result.lastIndexOf(bracketLeft) != -1) {
            val lastOpenBracketIndex = result.lastIndexOf(bracketLeft)
            val lastCloseBracketIndex = result.indexOf(bracketRight, lastOpenBracketIndex)
            if (lastOpenBracketIndex + 1 < 0 || lastCloseBracketIndex < 0) {
                return ""
            }
            val innerBracket = result.substring(lastOpenBracketIndex + 1, lastCloseBracketIndex)

            val innerBracketValue =
                solve(innerBracket)

            result = result.removeRange(lastOpenBracketIndex, lastCloseBracketIndex + 1)
            result = result.addString(innerBracketValue.toString(), lastOpenBracketIndex)
            result = result.replace("--", Operator.PLUS.char.toString())
        }
        // abort calculation if result has unresolved brackets
        return if (result.indexOf(bracketLeft) != -1 ||
            result.indexOf(bracketRight) != -1
        ) {
            ""
        } else {
            return result
        }
    }
}
