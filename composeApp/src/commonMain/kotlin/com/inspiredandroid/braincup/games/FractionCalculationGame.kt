package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.games.tools.Calculator
import kotlin.random.Random

/**
 * Solve the fraction calculation.
 *
 * Logic:
 * - Allowed operator = *
 * - Result can't be negative
 */
class FractionCalculationGame : Game() {

    private var result = 0
    private var fractions = mutableListOf<String>()
    var calculation = ""

    override fun nextRound() {
        fractions.clear()
        val down = Random.nextInt(2, 8)
        val up = down * Random.nextInt(3, 7)
        val upDivision = getRandomDivisionIntegers(up)
        val downDivision = getRandomDivisionIntegers(down)
        fractions.add("$upDivision/$downDivision")
        fractions.add("${up / upDivision}/${down / downDivision}")
        result = Calculator.calculate("$up/$down").toInt()
        calculation = fractions.joinToString(") * (", "(", ")")
    }

    private fun getRandomDivisionIntegers(value: Int): Int {
        val possibleUpperDivisions = mutableListOf<Int>()
        for (i in 2 until value) {
            if (value % i == 0) {
                possibleUpperDivisions.add(i)
            }
        }
        return if (possibleUpperDivisions.isEmpty()) {
            1
        } else {
            possibleUpperDivisions.random()
        }
    }

    override fun isCorrect(input: String): Boolean {
        return input == result.toString()
    }

    override fun solution(): String {
        return result.toString()
    }

    override fun hint(): String? {
        return null
    }

    override fun getGameType(): GameType {
        return GameType.FRACTION_CALCULATION
    }
}