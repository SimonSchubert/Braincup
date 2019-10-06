package com.inspiredandroid.braincup

import com.inspiredandroid.braincup.games.tools.Calculator
import kotlin.test.assertTrue
import kotlin.test.Test

class Tests {

    @Test
    fun calculator() {
        assertTrue {
            Calculator.calc("1+2") == 3.0
        }
        assertTrue {
            Calculator.calc("2--1") == 3.0
        }
        assertTrue {
            Calculator.calc("-2+1") == -1.0
        }
        assertTrue {
            Calculator.calc("20-50") == -30.0
        }
        assertTrue {
            Calculator.calc("(1+2)*5+12-4/(10/5)") == 25.0
        }
        assertTrue {
            Calculator.calc("2*(10-(5+4))") == 2.0
        }
        assertTrue {
            Calculator.calc(" 2 + 2 ") == 4.0
        }
    }
}
