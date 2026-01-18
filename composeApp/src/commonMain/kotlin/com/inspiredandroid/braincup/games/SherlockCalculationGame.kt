package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.games.tools.Calculator
import com.inspiredandroid.braincup.games.tools.Operator
import com.inspiredandroid.braincup.games.tools.toChar
import com.inspiredandroid.braincup.numbersRegex
import kotlin.random.Random

/**
 * Generates a goal number and a bunch of random numbers which makes it possible to build a expression
 * that will result in the goal number.
 *
 * Logic:
 * - Allowed operators = +-*
 * - Result of subtraction can't be under 0
 * - Result of multiplication can't be over 140
 * - Each round increases the count of available numbers by 1
 * - Available numbers can't contain the result number
 */
class SherlockCalculationGame : Game() {
    var result = 0
    val numbers = mutableListOf<Int>()
    private var calculation = ""
    private var minNumbersNeeded = 2
    private var maxNumbersNeeded = 3
    private val availableOperators = listOf(Operator.MINUS, Operator.PLUS, Operator.MULTIPLY)

    override fun isCorrect(input: String): Boolean {
        val matches = numbersRegex.findAll(input)
        val userNumbers = matches.map { it.value.toIntOrNull() }.toList()
        if (!numbers.containsAll(userNumbers)) {
            return false
        }

        return try {
            result == Calculator.calculate(input).toInt()
        } catch (ignore: NumberFormatException) {
            false
        }
    }

    override fun nextRound() {
        calculation = ""
        numbers.clear()
        numbers.add(Random.nextInt(2, 5))
        numbers.add(Random.nextInt(2, 10))
        for (i in 0 until maxNumbersNeeded - 3) {
            numbers.add(Random.nextInt(10, 40))
        }

        numbers.shuffle()

        numbers
            .subList(0, Random.nextInt(minNumbersNeeded, maxNumbersNeeded))
            .forEachIndexed { index, i ->
                if (index > 0) {
                    val excludeMinus = Calculator.calculate(calculation) - i < 0
                    val excludeMultiply = Calculator.calculate(calculation) * i > 140
                    calculation += getRandomOperator(excludeMinus, excludeMultiply)
                }
                calculation += i.toString()
            }
        result = Calculator.calculate(calculation).toInt()

        while (numbers.contains(result)) {
            calculation += Operator.MULTIPLY.toChar()
            val number = Random.nextInt(2, 4)
            calculation += "$number"
            numbers.add(number)
            result = Calculator.calculate(calculation).toInt()
        }
        numbers.shuffle()

        updateNumberBounds()
    }

    override fun solution(): String = calculation

    override fun hint(): String? = null

    override fun getGameType(): GameType = GameType.SHERLOCK_CALCULATION

    fun getNumbersString(): String = numbers.joinToString()

    private fun updateNumberBounds() {
        maxNumbersNeeded = round + 3
        when (round) {
            2 -> minNumbersNeeded = 3
            5 -> minNumbersNeeded = 4
        }
    }

    private fun getRandomOperator(
        excludeMinus: Boolean = false,
        excludeMultiply: Boolean = false,
    ): Char {
        var operator = availableOperators.random()
        if (excludeMinus && operator == Operator.DIVIDE) {
            operator = Operator.PLUS
        }
        if (excludeMultiply && operator == Operator.MULTIPLY) {
            operator = Operator.PLUS
        }
        return operator.toChar()
    }
}
