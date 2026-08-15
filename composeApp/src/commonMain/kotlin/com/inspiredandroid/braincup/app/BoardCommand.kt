package com.inspiredandroid.braincup.app

/**
 * Board games reach [GameController] through the single `onAnswer(String)` channel every game
 * screen already has, so a move is encoded as `"<name>"`, `"<name>:<ints>"` or
 * `"<name>:<int>:<ints>"` — for example `"tap:12"`, `"del:2,3"`, `"paint:1:3,4,5"`, `"undo"`.
 *
 * Screens build those strings with the builders below and the controller reads them with the
 * matching parsers, so the wire format is written down in exactly one place. A parser returns
 * null when the input is not its command or the payload does not parse, which the controller
 * treats as "ignore this input".
 */
internal object BoardCommand {

    // Commands with no payload.
    const val UNDO = "undo"
    const val RESTART = "restart"
    const val RESET = "reset"
    const val SUBMIT = "submit"

    // Commands with one, so both ends name them the same thing.
    const val TAP = "tap"
    const val TOGGLE = "toggle"
    const val SWAP = "swap"
    const val DRAW = "draw"
    const val DELETE = "del"
    const val PAINT = "paint"
    const val PATH = "path"
    const val CLEAR = "clear"

    fun tap(index: Int): String = "$TAP:$index"

    fun toggle(index: Int): String = "$TOGGLE:$index"

    fun swap(from: Int, to: Int): String = "$SWAP:$from,$to"

    fun draw(top: Int, left: Int, bottom: Int, right: Int): String = "$DRAW:$top,$left,$bottom,$right"

    fun delete(row: Int, col: Int): String = "$DELETE:$row,$col"

    fun paint(fill: Boolean, cells: Collection<Int>): String = "$PAINT:${if (fill) 1 else 0}:${cells.joinToString(",")}"

    fun path(color: Int, cells: List<Int>): String = "$PATH:$color:${cells.joinToString(",")}"

    fun clearPath(color: Int): String = "$CLEAR:$color"

    /** `"tap:12"` as `intArg("tap")` → 12. */
    fun String.intArg(name: String): Int? = payloadOf(name)?.toIntOrNull()

    /** `"del:2,3"` as `intsArg("del")` → [2, 3]; null unless every part is a number. */
    fun String.intsArg(name: String): List<Int>? = payloadOf(name)?.splitInts()

    /** `"paint:1:3,4"` as `intAndIntsArg("paint")` → 1 to [3, 4]. */
    fun String.intAndIntsArg(name: String): Pair<Int, List<Int>>? {
        val payload = payloadOf(name) ?: return null
        val separator = payload.indexOf(':')
        if (separator < 0) return null
        val head = payload.substring(0, separator).toIntOrNull() ?: return null
        val tail = payload.substring(separator + 1).splitInts() ?: return null
        return head to tail
    }

    private fun String.payloadOf(name: String): String? {
        val prefix = "$name:"
        return if (startsWith(prefix)) substring(prefix.length) else null
    }

    private fun String.splitInts(): List<Int>? {
        if (isEmpty()) return null
        return split(',').map { it.toIntOrNull() ?: return null }
    }
}
