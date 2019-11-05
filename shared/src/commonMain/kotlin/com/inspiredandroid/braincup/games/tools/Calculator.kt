package com.inspiredandroid.braincup.games.tools

import com.inspiredandroid.braincup.addString
import com.inspiredandroid.braincup.removeWhitespaces

/**
 * Calculates the result of an expression given as a string.
 *
 * Supported tokens: + - * / ( )
 */
object Calculator {
    private val validCalculationRegex = Regex("^[0-9+\\-*/().]*\$")

    fun calc(input: String): Double {
        var expression = input.removeWhitespaces()
        if (!validCalculationRegex.matches(expression)) {
            return 0.0
        }
        expression = expression.replace("--", "+")
        expression = expression.replace("-", "+-")
        expression = calculateInnerBrackets(expression)
        return calculate(expression)
    }

    private fun calculate(expression: String, operator: String = "+"): Double {
        var sum = 0.0
        expression.split(operator).forEachIndexed { index, s ->
            when {
                s.contains("+") -> sum += calculate(
                    s,
                    "+"
                )
                s.contains("*") -> sum += calculate(
                    s,
                    "*"
                )
                s.contains("/") -> sum += calculate(
                    s,
                    "/"
                )
                else -> {
                    if (s.isNotEmpty()) {
                        if (index == 0) {
                            sum = s.toDouble()
                        } else {
                            when (operator) {
                                "+" -> sum += s.toFloat()
                                "*" -> sum *= s.toFloat()
                                "/" -> sum /= s.toFloat()
                            }
                        }
                    }
                }
            }
        }
        return sum
    }

    private fun calculateInnerBrackets(input: String): String {
        var result = input
        while (result.lastIndexOf("(") != -1) {
            val lastOpenBracketIndex = result.lastIndexOf("(")
            val lastCloseBracketIndex = result.indexOf(")", lastOpenBracketIndex)
            if (lastOpenBracketIndex + 1 < 0 || lastCloseBracketIndex < 0) {
                return ""
            }
            val innerBracket = result.substring(lastOpenBracketIndex + 1, lastCloseBracketIndex)

            val innerBracketValue =
                calculate(innerBracket)

            result = result.removeRange(lastOpenBracketIndex, lastCloseBracketIndex + 1)
            result = result.addString(innerBracketValue.toString(), lastOpenBracketIndex)
            result = result.replace("--", "+")
        }
        // abort calculation if result has unresolved brackets
        return if (result.indexOf("(") != -1 ||
            result.indexOf(")") != -1
        ) {
            ""
        } else {
            return result
        }
    }
}