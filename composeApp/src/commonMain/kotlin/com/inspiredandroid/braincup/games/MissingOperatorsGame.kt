package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.GameUiState
import com.inspiredandroid.braincup.app.MissingOperatorsUiState
import com.inspiredandroid.braincup.games.tools.Operator
import kotlinx.collections.immutable.toImmutableList
import kotlin.random.Random

class MissingOperatorsGame : Game() {
    var numbers: List<Int> = emptyList()
        internal set
    var correctOperators: List<Operator> = emptyList()
        internal set
    var targetResult: Int = 0
        internal set

    init {
        nextRound()
    }

    fun getNumbersCountForRound(round: Int): Int = when {
        round >= 10 -> 5
        round >= 5 -> 4
        else -> 3
    }

    fun getAvailableOperatorsForRound(round: Int): List<Operator> = if (round <= 2) {
        listOf(Operator.PLUS, Operator.MINUS)
    } else {
        listOf(Operator.PLUS, Operator.MINUS, Operator.MULTIPLY, Operator.DIVIDE)
    }

    override fun generateRound() {
        val count = getNumbersCountForRound(round)
        val ops = getAvailableOperatorsForRound(round)

        while (true) {
            val generatedNumbers = List(count) {
                val maxNum = if (count <= 3) 20 else 12
                Random.nextInt(2, maxNum)
            }
            val generatedOperators = List(count - 1) {
                ops.random()
            }

            val result = evaluateTokens(generatedNumbers, generatedOperators)
            if (result != null && result > 0 && result <= 200) {
                // When more than one operator, never allow all-plus as a valid answer
                // (too easy and occurs too often by chance).
                val operatorCount = generatedOperators.size
                if (operatorCount > 1) {
                    val allPlus = List(operatorCount) { Operator.PLUS }
                    if (evaluateTokens(generatedNumbers, allPlus) == result) {
                        continue
                    }
                }
                this.numbers = generatedNumbers
                this.correctOperators = generatedOperators
                this.targetResult = result
                break
            }
        }
    }

    /** Generated puzzles stay within whole numbers from 0 to 200 at every step. */
    fun evaluateTokens(
        numbers: List<Int>,
        operators: List<Operator>,
    ): Int? {
        if (numbers.isEmpty()) return null
        if (operators.size != numbers.size - 1) return null

        val tokens = mutableListOf<Any>()
        for (i in numbers.indices) {
            tokens.add(numbers[i])
            if (i < operators.size) {
                tokens.add(operators[i])
            }
        }

        // Step 1: Evaluate * and / left-to-right
        var i = 1
        while (i < tokens.size) {
            val op = tokens[i] as? Operator
            if (op == Operator.MULTIPLY || op == Operator.DIVIDE) {
                val left = tokens[i - 1] as Int
                val right = tokens[i + 1] as Int
                val res = when (op) {
                    Operator.MULTIPLY -> {
                        val r = left * right
                        if (r > 200) return null
                        r
                    }
                    Operator.DIVIDE -> {
                        if (right == 0 || left % right != 0) return null
                        left / right
                    }
                }
                tokens[i - 1] = res
                tokens.removeAt(i) // remove operator
                tokens.removeAt(i) // remove right operand
            } else {
                i += 2
            }
        }

        // Step 2: Evaluate + and - left-to-right
        i = 1
        while (i < tokens.size) {
            val op = tokens[i] as? Operator
            if (op == Operator.PLUS || op == Operator.MINUS) {
                val left = tokens[i - 1] as Int
                val right = tokens[i + 1] as Int
                val res = when (op) {
                    Operator.PLUS -> {
                        val r = left + right
                        if (r > 200) return null
                        r
                    }
                    Operator.MINUS -> {
                        val r = left - right
                        if (r < 0) return null
                        r
                    }
                }
                tokens[i - 1] = res
                tokens.removeAt(i)
                tokens.removeAt(i)
            } else {
                i += 2
            }
        }

        return tokens[0] as Int
    }

    fun parseOperators(input: String): List<Operator>? {
        val trimmed = input.trim().replace(" ", "")
        if (trimmed.length != numbers.size - 1) return null
        val userOperators = trimmed.mapNotNull { char ->
            Operator.entries.find { it.char == char }
        }
        return userOperators.takeIf { it.size == numbers.size - 1 }
    }

    override fun isCorrect(input: String): Boolean {
        val userOperators = parseOperators(input) ?: return false
        return equalsExactly(numbers, userOperators, targetResult)
    }

    /**
     * Player answers are held to plain arithmetic only, not to the generator's limits, so
     * `8 - 3 + 3`, `8 / 3 * 3` and `17 * 12 / 12` all count when the target is what they make.
     * Fractions are kept exact; no puzzle is long enough for them to overflow a Long.
     */
    private fun equalsExactly(numbers: List<Int>, operators: List<Operator>, target: Int): Boolean {
        if (operators.size != numbers.size - 1) return false
        var sumNumerator = 0L
        var sumDenominator = 1L
        var termNumerator = numbers[0].toLong()
        var termDenominator = 1L
        var termSign = 1L

        fun addTerm() {
            sumNumerator = sumNumerator * termDenominator + termSign * termNumerator * sumDenominator
            sumDenominator *= termDenominator
            val divisor = gcd(sumNumerator, sumDenominator)
            sumNumerator /= divisor
            sumDenominator /= divisor
        }

        operators.forEachIndexed { index, operator ->
            val next = numbers[index + 1].toLong()
            when (operator) {
                Operator.MULTIPLY -> termNumerator *= next
                Operator.DIVIDE -> {
                    if (next == 0L) return false
                    termDenominator *= next
                }
                Operator.PLUS, Operator.MINUS -> {
                    addTerm()
                    termSign = if (operator == Operator.PLUS) 1L else -1L
                    termNumerator = next
                    termDenominator = 1L
                }
            }
        }
        addTerm()
        return sumNumerator == target.toLong() * sumDenominator
    }

    private fun gcd(a: Long, b: Long): Long {
        var x = if (a < 0) -a else a
        var y = if (b < 0) -b else b
        while (y != 0L) {
            val t = x % y
            x = y
            y = t
        }
        return if (x == 0L) 1L else x
    }

    override fun solution(): String {
        val sb = StringBuilder()
        for (i in numbers.indices) {
            sb.append(numbers[i])
            if (i < correctOperators.size) {
                sb.append(" ").append(correctOperators[i].char).append(" ")
            }
        }
        sb.append(" = ").append(targetResult)
        return sb.toString()
    }

    override fun toUiState(): GameUiState = MissingOperatorsUiState(
        numbers = numbers.toImmutableList(),
        targetResult = targetResult,
        operatorsCount = numbers.size - 1,
    )
}
