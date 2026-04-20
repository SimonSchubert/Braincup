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

    override fun generateRound() {
        fractions.clear()
        val (maxDown, maxMultiplier) = when {
            round >= 10 -> 14 to 12
            round >= 5 -> 10 to 9
            round >= 2 -> 8 to 7
            else -> 7 to 6
        }
        val down = Random.nextInt(2, maxDown + 1)
        val up = down * Random.nextInt(3, maxMultiplier + 1)
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

    override fun isCorrect(input: String): Boolean = input == result.toString()

    override fun solution(): String = result.toString()

    override fun hint(): String? = null

    override fun toUiState() = com.inspiredandroid.braincup.app.FractionCalculationUiState(
        calculation = calculation,
        answerString = result.toString(),
    )
}
