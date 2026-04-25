package com.inspiredandroid.braincup

/**
 * Add string in between at specific position
 */
fun String.addString(
    part: String,
    position: Int,
): String = this.substring(0, position) + part + this.substring(position)

/**
 * Remove all whitespaces from String
 */
fun String.removeWhitespaces(): String = this.replace("\\s".toRegex(), "")

/**
 * Comma and space as separator is allowed
 */
fun String.splitToIntList(): List<Int> = this
    .trim()
    .split(" ")
    .joinToString(separator = ",")
    .split(",")
    .mapNotNull {
        try {
            it.trim().toInt()
        } catch (ignore: Exception) {
            null
        }
    }
