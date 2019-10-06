package com.inspiredandroid.braincup.games.tools

import com.inspiredandroid.braincup.addString
import com.inspiredandroid.braincup.removeWhitespaces

/**
 * Calculates the result of an expression given as a string.
 *
 * Supported tokens: + - * / ( )
 */
object Calculator {

    fun calc(input: String): Double {
        var expression = input.removeWhitespaces()
        expression = expression.replace("--", "+")
        expression = expression.replace("-", "+-")
        expression =
            calculateInnerBrackets(expression)
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
            val innerBracket = result.substring(lastOpenBracketIndex + 1, lastCloseBracketIndex)

            val innerBracketValue =
                calculate(innerBracket)

            result = result.removeRange(lastOpenBracketIndex, lastCloseBracketIndex + 1)
            result = result.addString(innerBracketValue.toString(), lastOpenBracketIndex)
            result = result.replace("--", "+")
        }
        return result
    }
}