package com.inspiredandroid.braincup

import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.Shape

internal val ESCAPE by lazy { '\u001B' }
internal val RESET by lazy { "$ESCAPE[0m" }

fun String.color(color: Color): String {
    return when (color) {
        Color.RED -> getEscapedText(91)
        Color.GREEN -> getEscapedText(92)
        Color.BLUE -> getEscapedText(36)
        Color.PURPLE -> getEscapedText(35)
        Color.YELLOW -> getEscapedText(93)
        Color.ORANGE -> getEscapedText(33)
        Color.TURQUOISE -> getEscapedText(36)
        Color.ROSA -> getEscapedText(96)
        else -> this
    }
}

fun String.bold(): String {
    return getEscapedText(1)
}

private fun String.getEscapedText(code: Int): String {
    return "$ESCAPE[${code}m$this$RESET"
}

fun MutableList<String>.merge(data: List<String>) {
    data.forEachIndexed { index, line ->
        if (index >= this.size) {
            this.add("")
        }
        this[index] += line
    }
}

fun Figure.getLines(): List<String> {
    return this.shape.getLines(this.color, this.rotation)
}

fun Shape.getLines(color: Color, rotation: Int = 0): List<String> {
    return when (this) {
        Shape.SQUARE -> listOf(
            "  * * * * *  ".color(color),
            "  *       *  ".color(color),
            "  *       *  ".color(color),
            "  *       *  ".color(color),
            "  * * * * *  ".color(color)
        )
        Shape.CIRCLE -> listOf(
            "    *  *     ".color(color),
            "  *      *   ".color(color),
            " *        *  ".color(color),
            "  *      *   ".color(color),
            "    *  *     ".color(color)
        )
        Shape.HEART -> listOf(
            "   *     *   ".color(color),
            " *    *    * ".color(color),
            "  *       *  ".color(color),
            "    *   *    ".color(color),
            "      *      ".color(color)
        )
        Shape.STAR -> listOf(
            "  *   *    * ".color(color),
            "    * * *    ".color(color),
            "  * *   * *  ".color(color),
            "    * * *    ".color(color),
            "  *   *    * ".color(color)
        )
        Shape.T -> listOf(
            "    * * *    ".color(color),
            "    *   *    ".color(color),
            "  * *   * *  ".color(color),
            "  *       *  ".color(color),
            "  * * * * *  ".color(color)
        )
        Shape.DIAMOND -> listOf(
            "      * * *  ".color(color),
            "    *     *  ".color(color),
            "  *       *  ".color(color),
            "  *     *    ".color(color),
            "  * * *      ".color(color)
        )
        Shape.HOUSE -> listOf(
            "      *      ".color(color),
            "    *   *    ".color(color),
            "  *       *  ".color(color),
            "  *       *  ".color(color),
            "  * * * * *  ".color(color)
        )
        Shape.ABSTRACT_TRIANGLE -> listOf(
            "        * *  ".color(color),
            "      *   *  ".color(color),
            "    *     *  ".color(color),
            "  *       *  ".color(color),
            "  * * * * *  ".color(color)
        )
        Shape.TRIANGLE -> when (rotation) {
            0 -> listOf(
                "      *      ".color(color),
                "     * *     ".color(color),
                "    *   *    ".color(color),
                "   *     *   ".color(color),
                "  * * * * *  ".color(color)
            )
            90 -> listOf(
                "  *          ".color(color),
                "  *   *      ".color(color),
                "  *       *  ".color(color),
                "  *   *      ".color(color),
                "  *          ".color(color)
            )
            180 -> listOf(
                "  * * * * *  ".color(color),
                "   *     *   ".color(color),
                "    *   *    ".color(color),
                "     * *     ".color(color),
                "      *      ".color(color)
            )
            else -> listOf(
                "          *  ".color(color),
                "      *   *  ".color(color),
                "  *       *  ".color(color),
                "      *   *  ".color(color),
                "          *  ".color(color)
            )
        }
        Shape.L -> when (rotation) {
            90 -> listOf(
                "  * * * * *  ".color(color),
                "  *       *  ".color(color),
                "  *   * * *  ".color(color),
                "  *   *      ".color(color),
                "  * * *      ".color(color)
            )
            180 -> listOf(
                "  * * * * *  ".color(color),
                "  *       *  ".color(color),
                "  * * *   *  ".color(color),
                "      *   *  ".color(color),
                "      * * *  ".color(color)
            )
            270 -> listOf(
                "      * * *  ".color(color),
                "      *   *  ".color(color),
                "  * * *   *  ".color(color),
                "  *       *  ".color(color),
                "  * * * * *  ".color(color)
            )
            else -> listOf(
                "  * * *      ".color(color),
                "  *   *      ".color(color),
                "  *   * * *  ".color(color),
                "  *       *  ".color(color),
                "  * * * * *  ".color(color)
            )
        }
        Shape.ARROW -> when (rotation) {
            90 -> listOf(
                " → ".color(color)
            )
            180 -> listOf(
                " ↓ ".color(color)
            )
            270 -> listOf(
                " ← ".color(color)
            )
            else -> listOf(
                " ↑ ".color(color)
            )
        }
    }
}