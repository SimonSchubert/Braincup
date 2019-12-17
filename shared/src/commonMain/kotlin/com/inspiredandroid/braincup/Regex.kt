package com.inspiredandroid.braincup

val numbersRegex by lazy { Regex("(\\d+)") }
val validCalculationRegex by lazy { Regex("^[0-9+\\-*/().]*\$") }
