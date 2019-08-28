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