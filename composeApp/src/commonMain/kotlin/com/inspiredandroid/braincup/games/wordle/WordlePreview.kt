package com.inspiredandroid.braincup.games.wordle

import com.inspiredandroid.braincup.app.WordleLetterState

/**
 * Mini 3×3 Wordle board for the main-menu tile. Each puzzle is a believable solve path:
 * two narrowing guesses, then the target word all green.
 *
 * Words are common in their language and chosen so [WordleGame.evaluate] produces a mix of
 * gray (absent), yellow (present), and green (correct) before the win row.
 */
data class WordlePreviewPuzzle(
    val target: String,
    /** Three guesses in order; the last entry must equal [target]. */
    val guesses: List<String>,
) {
    init {
        require(target.length == WORD_LENGTH)
        require(guesses.size == ROW_COUNT)
        require(guesses.last() == target)
    }

    fun statesFor(guess: String): List<WordleLetterState> = WordleGame.evaluate(guess, target).map { it.state }

    companion object {
        const val WORD_LENGTH = 3
        const val ROW_COUNT = 3
    }
}

object WordlePreviewPuzzles {
    /**
     * en — CAT: ACT (reordered letters), BAT (right vowel + ending), CAT (solved).
     * de — TAG: GAT (day letters scrambled), TAL (valley; two greens), TAG (solved).
     * fr — MER: PRE (near miss), FER (iron; two greens), MER (sea, solved).
     * nl — DEK: BED (bed; two hits), DEN (den; two greens), DEK (cover, solved).
     */
    private val byTag = mapOf(
        "en" to WordlePreviewPuzzle(target = "CAT", guesses = listOf("ACT", "BAT", "CAT")),
        "de" to WordlePreviewPuzzle(target = "TAG", guesses = listOf("GAT", "TAL", "TAG")),
        "fr" to WordlePreviewPuzzle(target = "MER", guesses = listOf("PRE", "FER", "MER")),
        "nl" to WordlePreviewPuzzle(target = "DEK", guesses = listOf("BED", "DEN", "DEK")),
    )

    fun forTag(tag: String): WordlePreviewPuzzle? = byTag[normalizeLanguageTag(tag)]
}
