package com.inspiredandroid.braincup

import com.inspiredandroid.braincup.games.tools.Calculator
import kotlin.test.Test
import kotlin.test.assertEquals

class Tests {

    @Test
    fun calculator() {
        assertEquals(3.0, Calculator.calc("1+2"))
        assertEquals(3.9f, Calculator.calc("1.5+2.4").toFloat())
        assertEquals(3.0, Calculator.calc("2--1"))
        assertEquals(-1.0, Calculator.calc("-2+1"))
        assertEquals(-30.0, Calculator.calc("20-50"))
        assertEquals(25.0, Calculator.calc("(1+2)*5+12-4/(10/5)"))
        assertEquals(2.0, Calculator.calc("2*(10-(5+4))"))
        assertEquals(4.0, Calculator.calc(" 2 + 2 "))
        assertEquals(4.0, Calculator.calc("2+( 2 )"))
        assertEquals(0.0, Calculator.calc("2+("))
        assertEquals(0.0, Calculator.calc("2+)"))
        assertEquals(0.0, Calculator.calc("2+)("))
        assertEquals(0.0, Calculator.calc("2+())"))
        assertEquals(0.0, Calculator.calc("2+(()"))
        assertEquals(0.0, Calculator.calc("&#1-5@"))
    }
}
