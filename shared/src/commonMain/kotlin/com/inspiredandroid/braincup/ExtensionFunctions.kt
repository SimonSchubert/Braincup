package com.inspiredandroid.braincup

/**
 * Add string in between at specific position
 */
fun String.addString(part: String, position: Int): String {
    return this.substring(0, position) + part + this.substring(position)
}

/**
 * Remove all whitespaces from String
 */
fun String.removeWhitespaces(): String {
    return this.replace("\\s".toRegex(), "")
}

/**
 * Comma and space as separator is allowed
 */
fun String.splitToIntList(): List<Int> {
    return this.trim().split(" ").joinToString(separator = ",").split(",")
        .mapNotNull {
            try {
                it.trim().toInt()
            } catch (ignore: Exception) {
                null
            }
        }
}

/**
 * Comma and space as separator is allowed
 */
fun String.splitToStringList(): List<String> {
    return this.trim().split(" ").joinToString(separator = ",").split(",")
        .map {
            it.trim()
        }
}

/**
 * Add ne entry or increase existing
 */
fun MutableList<Int>.addOrIncrease(index: Int, number: Int) {
    if (size > index) {
        set(index, get(index) + number)
    } else {
        add(number)
    }
}