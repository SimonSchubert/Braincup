package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.Calculator
import kotlin.random.Random

class SherlockCalculation : GameMode() {

    private var moveCount = 0
    internal var result = 0
    private var calculation = ""
    internal val numbers = mutableListOf<Int>()
    var minNumbersNeeded = 2
    var maxNumbersNeeded = 3
    private val numbersRegex = Regex("(\\d+)")

    override fun isCorrect(input: String): Boolean {
        val matches = numbersRegex.findAll(input)
        val userNumbers = matches.map { it.value.toInt() }.toList()
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
        numbers.add(Random.nextInt(10, 20))
        numbers.add(Random.nextInt(10, 40))
        numbers.add(Random.nextInt(10, 40))
        numbers.add(Random.nextInt(20, 50))

        numbers.shuffle()

        numbers.subList(0, Random.nextInt(minNumbersNeeded, maxNumbersNeeded)).forEachIndexed { index, i ->
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

    private fun increaseMoveCount() {
        moveCount++
        when (moveCount) {
            2 -> {
                minNumbersNeeded = 3
                maxNumbersNeeded = 4
            }
            5 -> {
                minNumbersNeeded = 3
                maxNumbersNeeded = 5
            }
        }
    }

    private fun getRandomOperator(excludeMinus: Boolean = false, excludeMultiply: Boolean = false): String {
        var operator = when (Random.nextInt(
            0, 2)) {
            0 -> "+"
            1 -> "-"
            2 -> "*"
            else -> "+"
        }
        if(excludeMinus && operator == "-") {
            operator = "*"
        }
        if(excludeMultiply && operator == "*") {
            operator = "+"
        }
        return operator
    }
}