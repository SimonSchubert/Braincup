package com.inspiredandroid.braincup.games.wordle

import com.inspiredandroid.braincup.app.WordleLetterState
import com.inspiredandroid.braincup.app.WordleUiState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class WordleGameTest {

    private val en = WordleLanguages.resolve("en")!!

    private val testWords = setOf("CRANE", "WRONG", "WORDS", "BREAD", "ABBEY")

    private fun game(target: String) = WordleGame(en, target, testWords)

    private fun states(guess: String, target: String) = WordleGame.evaluate(guess, target).map { it.state }

    @Test
    fun evaluateMarksGreenYellowGray() {
        // CRANE vs CARET: C green, R yellow, A yellow, N absent, E yellow.
        assertEquals(
            listOf(
                WordleLetterState.CORRECT, // C
                WordleLetterState.PRESENT, // R (in CARET, wrong spot)
                WordleLetterState.PRESENT, // A (in CARET, wrong spot)
                WordleLetterState.ABSENT, // N
                WordleLetterState.PRESENT, // E (in CARET, wrong spot)
            ),
            states("CRANE", "CARET"),
        )
    }

    @Test
    fun duplicateGuessLetterBeyondTargetCountGoesGray() {
        // LLAMA vs PARTY: target has one A and no L; no greens. The first A (index2) takes the
        // single available A as PRESENT, the second A (index4) has none left -> ABSENT.
        assertEquals(
            listOf(
                WordleLetterState.ABSENT, // L
                WordleLetterState.ABSENT, // L
                WordleLetterState.PRESENT, // A (consumes the only A)
                WordleLetterState.ABSENT, // M
                WordleLetterState.ABSENT, // A (none left)
            ),
            states("LLAMA", "PARTY"),
        )
    }

    @Test
    fun greenConsumesCountBeforeYellow() {
        // THREE vs OTHER: target has one E, at index3. The exact match (green) at index3 consumes
        // it, so the other E (index4) is ABSENT even though T/H/R are PRESENT.
        assertEquals(
            listOf(
                WordleLetterState.PRESENT, // T
                WordleLetterState.PRESENT, // H
                WordleLetterState.PRESENT, // R
                WordleLetterState.CORRECT, // E (exact position)
                WordleLetterState.ABSENT, // E (count already consumed by the green)
            ),
            states("THREE", "OTHER"),
        )
    }

    @Test
    fun winAfterCorrectGuessScoresByEfficiency() {
        val game = game("CRANE")
        "WRON".forEach { assertFalse(game.typeLetter(it)) }
        assertTrue(game.typeLetter('G'))
        assertFalse(game.finished)
        "CRAN".forEach { assertFalse(game.typeLetter(it)) }
        assertTrue(game.typeLetter('E'))
        assertTrue(game.solved)
        assertTrue(game.finished)
        assertEquals(2, game.guessesUsed)
        // 7 - 2 = 5.
        assertEquals(5, game.score)
    }

    @Test
    fun lossAfterSixWrongGuessesScoresZeroAndRevealsAnswer() {
        val game = game("CRANE")
        repeat(WordleGame.MAX_GUESSES) {
            "WORD".forEach { c -> assertFalse(game.typeLetter(c)) }
            assertTrue(game.typeLetter('S'))
        }
        assertTrue(game.finished)
        assertFalse(game.solved)
        assertEquals(0, game.score)
        val ui = game.toUiState() as WordleUiState
        assertEquals("CRANE", ui.answer)
    }

    @Test
    fun giveUpAfterInvalidWordShowsAnswerNotHint() {
        val game = game("CRANE")
        "ZZZZ".forEach { assertFalse(game.typeLetter(it)) }
        assertFalse(game.typeLetter('Z'))
        game.giveUp()
        val ui = game.toUiState() as WordleUiState
        assertFalse(ui.notInWordList)
        assertEquals("CRANE", ui.answer)
    }

    @Test
    fun invalidWordIsRejectedAndClearsInput() {
        val game = game("CRANE")
        "ZZZZ".forEach { assertFalse(game.typeLetter(it)) }
        assertFalse(game.typeLetter('Z'))
        assertEquals(0, game.guessesUsed)
        val ui = game.toUiState() as WordleUiState
        assertTrue(ui.notInWordList)
        assertEquals(' ', ui.rows[0][0].char)
    }

    @Test
    fun tooShortSubmitIsRejectedAndFlagged() {
        val game = game("CRANE")
        "CRA".forEach { game.typeLetter(it) }
        assertFalse(game.submitGuess())
        val ui = game.toUiState() as WordleUiState
        assertTrue(ui.notEnoughLetters)
        assertFalse(game.finished)
    }

    @Test
    fun typingIgnoresOutOfAlphabetAndOverflow() {
        val game = game("CRANE")
        assertFalse(game.typeLetter('1')) // not a letter
        "CRAN".forEach { assertFalse(game.typeLetter(it)) }
        assertTrue(game.typeLetter('E')) // auto-submits on the fifth letter
        assertTrue(game.solved)
        assertFalse(game.typeLetter('S')) // overflow after submit
    }

    @Test
    fun autoSubmitOnLastLetter() {
        val game = game("CRANE")
        "CRAN".forEach { assertFalse(game.typeLetter(it)) }
        assertTrue(game.typeLetter('E'))
        assertTrue(game.solved)
        assertEquals(1, game.guessesUsed)
    }

    @Test
    fun clearFromRemovesLetterAndTrailingLetters() {
        val game = game("CRANE")
        "HE".forEach { assertFalse(game.typeLetter(it)) }
        assertFalse(game.typeLetter('L'))
        game.clearFrom(2)
        var ui = game.toUiState() as WordleUiState
        assertEquals('H', ui.rows[0][0].char)
        assertEquals('E', ui.rows[0][1].char)
        assertEquals(' ', ui.rows[0][2].char)
        assertFalse(game.typeLetter('L'))
        ui = game.toUiState() as WordleUiState
        assertEquals('L', ui.rows[0][2].char)
    }

    @Test
    fun keyboardStatesNeverDowngrade() {
        val game = game("ABBEY")
        // First guess puts B as PRESENT somewhere, later guess makes it CORRECT; key must stay green.
        "BREA".forEach { assertFalse(game.typeLetter(it)) }
        assertTrue(game.typeLetter('D'))
        "ABBE".forEach { assertFalse(game.typeLetter(it)) }
        assertTrue(game.typeLetter('Y'))
        val ui = game.toUiState() as WordleUiState
        assertEquals(WordleLetterState.CORRECT, ui.keyStates['B'])
    }

    @Test
    fun resolveIsStrictAboutSupportedLanguages() {
        assertEquals("de", WordleLanguages.resolve("de")?.tag)
        assertEquals("en", WordleLanguages.resolve("en-US")?.tag) // region trimmed
        assertNull(WordleLanguages.resolve("ja")) // hidden script
        assertNull(WordleLanguages.resolve("es")) // not in first iteration
    }

    @Test
    fun germanUmlautTargetMatchesExactly() {
        val de = WordleLanguages.resolve("de")!!
        val words = setOf("KÄFIG", "KAFER")
        val game = WordleGame(de, "KÄFIG", words)
        "KÄFI".forEach { assertFalse(game.typeLetter(it)) }
        assertTrue(game.typeLetter('G'))
        assertTrue(game.solved)
        assertEquals(1, game.guessesUsed)
    }
}
