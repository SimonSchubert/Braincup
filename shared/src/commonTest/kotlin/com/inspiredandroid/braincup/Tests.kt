package com.inspiredandroid.braincup

import com.inspiredandroid.braincup.games.tools.Calculator
import kotlin.test.Test
import kotlin.test.assertTrue

class Tests {

    @Test
    fun calculator() {
        assertTrue {
            Calculator.calc("1+2") == 3.0
        }
    }
}

@Test
fun calculator() {
    assertTrue {
        Calculator.calc("1+2") == 3.0
    }
}

