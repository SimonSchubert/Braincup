package com.inspiredandroid.braincup.games

import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class MentalCalculation : GameMode() {

    var calculation = ""
    private var moveCount = 0
    private var number = 0
    private val divisions = arrayListOf(2, 3, 4, 5, 6, 7, 8, 9)
    private var division = -1
    private var maxNumber = 0

    init {
        reset()
    }

    override fun isCorrect(input: String): Boolean {
        val isCorrect = number.toString() == input
        if (!isCorrect) {
            reset()
        }
        return isCorrect
    }

    override fun nextRound() {
        calculation = if (moveCount == 0) {
            "$number "
        } else {
            ""
        }

        calculation += when (getNextOperator()) {
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

        increaseMoveCount()
    }

    private fun reset() {
        moveCount = 0
        number = Random.nextInt(2, 15)
        maxNumber = 30
    }

    private fun getNextOperator(): Operator {
        when {
            number < 10 -> return arrayListOf(Operator.PLUS, Operator.MULTIPLY).random()
            number > 60 -> {
                if (Random.nextBoolean()) {
                    divisions.shuffle()
                    divisions.forEach {
                        if (number % it == 0) {
                            division = it
                            return Operator.DIVIDE
                        }
                    }
                }
                return Operator.MINUS
            }
            else -> return arrayListOf(Operator.PLUS, Operator.MULTIPLY, Operator.MINUS).random()
        }
    }

    private fun increaseMoveCount() {
        moveCount++
        when (moveCount) {
            6 -> maxNumber = 50
            10 -> maxNumber = 70
            14 -> maxNumber = 100
            20 -> maxNumber = 150
        }
    }

    enum class Operator {
        PLUS,
        MINUS,
        MULTIPLY,
        DIVIDE
    }
}