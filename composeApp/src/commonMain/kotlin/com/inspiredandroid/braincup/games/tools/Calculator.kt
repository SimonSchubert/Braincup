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
        var sum = 0.0
        expression.split(operator.toChar()).forEachIndexed { index, s ->
            when {
                s.contains(Operator.PLUS.toChar()) -> sum += solve(
                    s,
                    Operator.PLUS,
                )
                s.contains(Operator.MULTIPLY.toChar()) -> sum += solve(
                    s,
                    Operator.MULTIPLY,
                )
                s.contains(Operator.DIVIDE.toChar()) -> sum += solve(
                    s,
                    Operator.DIVIDE,
                )
                else -> {
                    if (s.isNotEmpty()) {
                        if (index == 0) {
                            sum = s.toDouble()
                        } else {
                            when (operator) {
                                Operator.PLUS -> sum += s.toFloat()
                                Operator.MULTIPLY -> sum *= s.toFloat()
                                Operator.DIVIDE -> sum /= s.toFloat()
                                Operator.MINUS -> sum -= s.toFloat()
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
            result = result.replace("--", Operator.PLUS.toChar().toString())
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
