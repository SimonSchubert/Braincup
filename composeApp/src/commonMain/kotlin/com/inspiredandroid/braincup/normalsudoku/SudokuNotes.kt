package com.inspiredandroid.braincup.normalsudoku

private const val GRID = 9
private const val BLOCK = 3
private const val NOTE_ENCODE_OFFSET = 33

/** Candidate bitmask per cell; bit [digit] set means [digit] is a pencil mark (1..9). */
typealias NoteMask = Int

private const val MAX_NOTE_MASK = (1 shl 10) - 2 // bits 1..9 all set

fun noteMaskHas(mask: NoteMask, digit: Int): Boolean = digit in 1..9 && (mask shr digit) and 1 == 1

fun noteMaskToggle(mask: NoteMask, digit: Int): NoteMask = if (digit in 1..9) mask xor (1 shl digit) else mask

fun noteMaskRemove(mask: NoteMask, digit: Int): NoteMask = if (digit in 1..9) mask and (1 shl digit).inv() else mask

fun noteMaskToText(mask: NoteMask): String = buildString {
    for (digit in 1..9) {
        if (noteMaskHas(mask, digit)) append(digit)
    }
}

/** Remove [digit] from pencil marks in the same row, column, and 3×3 box as [pos]. */
fun autoEliminateNote(notes: MutableList<NoteMask>, digit: Int, pos: Int) {
    if (digit !in 1..9 || pos !in notes.indices) return
    val row = pos / GRID
    val col = pos % GRID
    val boxRow = (row / BLOCK) * BLOCK
    val boxCol = (col / BLOCK) * BLOCK
    for (index in notes.indices) {
        val r = index / GRID
        val c = index % GRID
        val inPeers = r == row ||
            c == col ||
            (r in boxRow until boxRow + BLOCK && c in boxCol until boxCol + BLOCK)
        if (inPeers) notes[index] = noteMaskRemove(notes[index], digit)
    }
}

fun encodeSudokuNotes(notes: List<NoteMask>): String = notes.joinToString("") { (it + NOTE_ENCODE_OFFSET).toChar().toString() }

fun decodeSudokuNotes(encoded: String): List<NoteMask>? {
    if (encoded.length != GRID * GRID) return null
    return encoded.map { char ->
        val value = char.code - NOTE_ENCODE_OFFSET
        if (value !in 0..MAX_NOTE_MASK) return null
        value
    }
}

fun emptySudokuNotes(): MutableList<NoteMask> = MutableList(GRID * GRID) { 0 }
