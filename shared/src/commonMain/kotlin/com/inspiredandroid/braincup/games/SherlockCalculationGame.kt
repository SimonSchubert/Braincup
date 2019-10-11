package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.games.tools.Calculator
import kotlin.random.Random

class SherlockCalculationGame : Game() {

    var result = 0
    private val numbers = mutableListOf<Int>()
    private var moveCount = 0
    private var calculation = ""
    private var minNumbersNeeded = 2
    private var maxNumbersNeeded = 3
    private val numbersRegex = Regex("(\\d+)")

    override fun isCorrect(input: String): Boolean {
        val matches = numbersRegex.findAll(input)
        val userNumbers = matches.map { it.value.toIntOrNull() }.toList()
        if (!numbers.containsAll(userNumbers)) {
            return false
        }

        return try {
            result == Calculator.calc(input).toInt()
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

        numbers.subList(0, Random.nextInt(minNumbersNeeded, maxNumbersNeeded))
            .forEachIndexed { index, i ->
                if (index > 0) {
                    val excludeMinus = Calculator.calc(calculation) - i < 0
                    val excludeMultiply = Calculator.calc(calculation) * i > 140
                    calculation += getRandomOperator(excludeMinus, excludeMultiply)
                }
                calculation += i.toString()
            }
        result = Calculator.calc(calculation).toInt()

        while (numbers.contains(result)) {
            calculation += "*"
            val number = Random.nextInt(2, 4)
            calculation += "$number"
            numbers.add(number)
            result = Calculator.calc(calculation).toInt()
        }
        numbers.shuffle()

        increaseMoveCount()
    }

    override fun solution(): String {
        return calculation
    }

    override fun getGameType(): GameType {
        return GameType.SHERLOCK_CALCULATION
    }

    fun getNumbersString(): String {
        return numbers.joinToString()
    }

    private fun increaseMoveCount() {
        moveCount++
        maxNumbersNeeded = moveCount + 3
        when (moveCount) {
            2 -> {
                minNumbersNeeded = 3
            }
            5 -> {
                minNumbersNeeded = 4
            }
        }
    }

    private fun getRandomOperator(
        excludeMinus: Boolean = false,
        excludeMultiply: Boolean = false
    ): String {
        var operator = when (Random.nextInt(
            0, 2
        )) {
            0 -> "+"
            1 -> "-"
            2 -> "*"
            else -> "+"
        }
        if (excludeMinus && operator == "-") {
            operator = "+"
        }
        if (excludeMultiply && operator == "*") {
            operator = "+"
        }
        return operator
    }
}