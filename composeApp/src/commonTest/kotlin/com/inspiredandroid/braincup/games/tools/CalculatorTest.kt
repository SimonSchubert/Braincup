package com.inspiredandroid.braincup.games.tools

import kotlin.test.Test
import kotlin.test.assertEquals

class CalculatorTest {
    @Test
    fun addition() {
        assertEquals(3.0, Calculator.calculate("1+2"))
    }

    @Test
    fun doubleMinus() {
        assertEquals(3.0, Calculator.calculate("2--1"))
    }

    @Test
    fun negativeOperand() {
        assertEquals(-1.0, Calculator.calculate("-2+1"))
    }

    @Test
    fun subtraction() {
        assertEquals(-30.0, Calculator.calculate("20-50"))
    }

    @Test
    fun bracketsWithDivision() {
        assertEquals(25.0, Calculator.calculate("(1+2)*5+12-4/(10/5)"))
    }

    @Test
    fun nestedBrackets() {
        assertEquals(2.0, Calculator.calculate("2*(10-(5+4))"))
    }

    @Test
    fun ignoresWhitespace() {
        assertEquals(4.0, Calculator.calculate(" 2 + 2 "))
    }

    @Test
    fun mixedMultiplyDivide() {
        assertEquals(11.0, Calculator.calculate("22*5/10"))
    }

    @Test
    fun issue22Expression() {
        assertEquals(65.0, Calculator.calculate("38*2-(22*5/10)"))
    }
}
