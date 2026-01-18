package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.games.tools.Operator
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

/**
 * Generates simple mathematical calculation up to 150
 *
 * Logic:
 * - Start with a number between 2 and 15
 * - When number < 10 only allow +*
 * - When number > 60 try division or minus if division doesn't work
 * - When number 10-60 try randomly +-*
 * - Result can't be negative
 * - Max result increases with each round up to 150
 */
class MentalCalculationGame : Game() {
    var calculation = ""
    private var number = 0
    private var division = -1
    private var maxNumber = 0
    private var resetNextRound = false
    private val allowedDivisions by lazy { mutableListOf(2, 3, 4, 5, 6, 7, 8, 9) }
    private val defaultOperators by lazy {
        listOf(
            Operator.PLUS,
            Operator.MULTIPLY,
            Operator.MINUS,
        )
    }
    private val increaseOperators by lazy { listOf(Operator.PLUS, Operator.MULTIPLY) }

    init {
        reset()
    }

    override fun isCorrect(input: String): Boolean {
        val isCorrect = number.toString() == input.trim()
        if (!isCorrect) {
            resetNextRound = true
        }
        return isCorrect
    }

    override fun nextRound() {
        if (resetNextRound) {
            reset()
            resetNextRound = false
        }
        calculation =
            if (round == 0) {
                "$number "
            } else {
                ""
            }

        calculation +=
            when (getNextOperator()) {
                Operator.PLUS -> {
                    val addition = Random.nextInt(3, max(maxNumber - number, 4))
                    number += addition
                    "+ $addition"
                }
                Operator.MINUS -> {
                    val subtraction = Random.nextInt(3, max(number - 3, 4))
                    number -= subtraction
                    "- $subtraction"
                }
                Operator.MULTIPLY -> {
                    val maxMulti = (maxNumber.toFloat() / number).toInt()
                    val multi = max(Random.nextInt(min(2, maxMulti - 1), maxMulti), 2)
                    number *= multi
                    "* $multi"
                }
                Operator.DIVIDE -> {
                    number /= division
                    "/ $division"
                }
            }

        updateMaxNumber()
    }

    override fun solution(): String = number.toString()

    override fun hint(): String? = if (round == 1) {
        "Remember $number"
    } else {
        null
    }

    override fun getGameType(): GameType = GameType.MENTAL_CALCULATION

    private fun reset() {
        round = 0
        number = Random.nextInt(2, 15)
        maxNumber = 30
    }

    private fun getNextOperator(): Operator {
        when {
            number < 10 -> return increaseOperators.random()
            number > 60 -> {
                if (Random.nextBoolean()) {
                    allowedDivisions.shuffle()
                    allowedDivisions.forEach {
                        if (number % it == 0) {
                            division = it
                            return Operator.DIVIDE
                        }
                    }
                }
                return Operator.MINUS
            }
            else -> return defaultOperators.random()
        }
    }

    private fun updateMaxNumber() {
        when (round) {
            5 -> maxNumber = 50
            9 -> maxNumber = 70
            13 -> maxNumber = 100
            19 -> maxNumber = 150
        }
    }

    fun getNumberLength(): Int = number.toString().length
}
