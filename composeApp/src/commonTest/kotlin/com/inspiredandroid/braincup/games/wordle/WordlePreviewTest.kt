package com.inspiredandroid.braincup.games.wordle

import com.inspiredandroid.braincup.app.WordleLetterState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class WordlePreviewTest {

    @Test
    fun everyLanguageHasPreviewPuzzle() {
        for (tag in listOf("en", "de", "fr", "nl")) {
            assertNotNull(WordlePreviewPuzzles.forTag(tag), "missing preview for $tag")
        }
    }

    @Test
    fun previewRowsUseAllFeedbackColorsBeforeWin() {
        val puzzle = WordlePreviewPuzzles.forTag("en")!!
        val early = puzzle.statesFor(puzzle.guesses[0]).toSet()
        assertTrue(WordleLetterState.PRESENT in early)
        assertTrue(WordleLetterState.CORRECT in early)
        val penultimate = puzzle.statesFor(puzzle.guesses[1]).toSet()
        assertTrue(WordleLetterState.ABSENT in penultimate)
        assertTrue(WordleLetterState.CORRECT in penultimate)
        assertEquals(
            listOf(WordleLetterState.CORRECT, WordleLetterState.CORRECT, WordleLetterState.CORRECT),
            puzzle.statesFor(puzzle.target),
        )
    }

    @Test
    fun germanPreviewMatchesExpectedColoring() {
        val puzzle = WordlePreviewPuzzles.forTag("de")!!
        assertEquals(
            listOf(WordleLetterState.PRESENT, WordleLetterState.CORRECT, WordleLetterState.PRESENT),
            puzzle.statesFor("GAT"),
        )
        assertEquals(
            listOf(WordleLetterState.CORRECT, WordleLetterState.CORRECT, WordleLetterState.ABSENT),
            puzzle.statesFor("TAL"),
        )
    }
}
