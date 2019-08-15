package com.inspiredandroid.braincup

fun String.addString(part: String, position: Int): String {
    return this.substring(0, position) + part + this.substring(position)
}

fun String.removeWhitespaces(): String {
    return this.replace("\\s".toRegex(), "")
}