package com.inspiredandroid.braincup.normalsudoku

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SudokuNotesTest {

    @Test
    fun noteMaskToggleAddsAndRemovesDigit() {
        var mask = 0
        mask = noteMaskToggle(mask, 5)
        assertTrue(noteMaskHas(mask, 5))
        assertFalse(noteMaskHas(mask, 4))
        mask = noteMaskToggle(mask, 5)
        assertFalse(noteMaskHas(mask, 5))
    }

    @Test
    fun noteMaskToTextListsDigitsInOrder() {
        var mask = 0
        mask = noteMaskToggle(mask, 3)
        mask = noteMaskToggle(mask, 1)
        mask = noteMaskToggle(mask, 7)
        assertEquals("137", noteMaskToText(mask))
    }

    @Test
    fun noteMaskRemoveClearsSingleDigit() {
        val mask = noteMaskToggle(0, 3)
        assertEquals(0, noteMaskRemove(mask, 3))
        assertEquals(mask, noteMaskRemove(mask, 7))
    }

    @Test
    fun autoEliminateRemovesDigitFromRowColumnAndBox() {
        val notes = emptySudokuNotes()
        val pos = 10 // row 1, col 1 — center of top-left box
        notes[pos] = noteMaskToggle(notes[pos], 7)
        notes[11] = noteMaskToggle(notes[11], 7) // same row
        notes[1] = noteMaskToggle(notes[1], 7) // same column
        notes[0] = noteMaskToggle(notes[0], 7) // same box
        notes[80] = noteMaskToggle(notes[80], 7) // unrelated corner

        autoEliminateNote(notes, 7, pos)

        assertFalse(noteMaskHas(notes[pos], 7))
        assertFalse(noteMaskHas(notes[11], 7))
        assertFalse(noteMaskHas(notes[1], 7))
        assertFalse(noteMaskHas(notes[0], 7))
        assertTrue(noteMaskHas(notes[80], 7))
    }

    @Test
    fun encodeDecodeRoundTrip() {
        val notes = emptySudokuNotes()
        notes[0] = noteMaskToggle(0, 1)
        notes[40] = noteMaskToggle(noteMaskToggle(0, 2), 9)
        notes[80] = noteMaskToggle(0, 5)

        val encoded = encodeSudokuNotes(notes)
        assertEquals(81, encoded.length)

        val decoded = decodeSudokuNotes(encoded)
        assertEquals(notes, decoded)
    }

    @Test
    fun decodeRejectsInvalidLength() {
        assertNull(decodeSudokuNotes("abc"))
    }

    @Test
    fun decodeRejectsOutOfRangeMask() {
        val invalid = buildString {
            repeat(80) { append((0 + 33).toChar()) }
            append((1100 + 33).toChar())
        }
        assertNull(decodeSudokuNotes(invalid))
    }
}
