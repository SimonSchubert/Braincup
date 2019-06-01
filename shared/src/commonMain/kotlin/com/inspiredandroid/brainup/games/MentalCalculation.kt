package com.inspiredandroid.brainup

import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class MentalCalculation {

    var moveCount = 0
    var number = Random.nextInt(2,15)

    private val divisions = arrayListOf(2,3,4,5,6,7,8,9)
    private var division = -1
    private var maxNumber = 30

    fun nextCalculation(): String {
        var calculationString = ""

        when(getNextOperator()) {
            OPERATOR_PLUS -> {
                val addition = Random.nextInt(3, maxNumber - number)
                number += addition
                calculationString = "+ $addition"
            }
            OPERATOR_MINUS -> {
                val subtraction = Random.nextInt(3, number - 3)
                number -= subtraction
                calculationString = "- $subtraction"
            }
            OPERATOR_MULTIPLY -> {
                val maxMulti = (maxNumber.toFloat() / number).toInt()
                val multi = max(Random.nextInt(min(2, maxMulti-1),  maxMulti), 2)
                number *= multi
                calculationString = "* $multi"
            }
            OPERATOR_DIVIDE -> {
                number /= division
                calculationString = "/ $division"
            }
        }

        increaseMoveCount()
        return calculationString
    }

    private fun getNextOperator(): Int {
        when {
            number < 10 -> return intArrayOf(OPERATOR_PLUS, OPERATOR_MULTIPLY).random()
            number > 60 -> {
                if(Random.nextBoolean()) {
                    divisions.shuffle()
                    divisions.forEach {
                        if(number%it == 0) {
                            division = it
                            return OPERATOR_DIVIDE
                        }
                    }
                }
                return OPERATOR_MINUS
            }
            else -> return intArrayOf(OPERATOR_PLUS, OPERATOR_MULTIPLY, OPERATOR_MINUS).random()
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

    companion object {
        private const val OPERATOR_PLUS = 0
        private const val OPERATOR_MINUS = 1
        private const val OPERATOR_MULTIPLY = 2
        private const val OPERATOR_DIVIDE = 3
    }
}