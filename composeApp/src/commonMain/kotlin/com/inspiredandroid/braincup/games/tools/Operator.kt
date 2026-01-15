package com.inspiredandroid.braincup.games.tools

enum class Operator {
    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE
}

fun Operator.toChar(): Char {
    return when (this) {
        Operator.PLUS -> '+'
        Operator.MINUS -> '-'
        Operator.MULTIPLY -> '*'
        Operator.DIVIDE -> '/'
    }
}