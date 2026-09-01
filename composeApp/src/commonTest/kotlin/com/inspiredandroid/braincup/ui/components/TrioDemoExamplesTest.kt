package com.inspiredandroid.braincup.ui.components

import com.inspiredandroid.braincup.games.isTrioSet
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TrioDemoExamplesTest {

    /** A row captioned as rejected must be one the game rejects, and vice versa. */
    @Test
    fun everyExampleIsJudgedTheWayItIsPresented() {
        TrioExamples.forEach { example ->
            assertEquals(3, example.cards.toSet().size, "not three distinct cards: ${example.cards}")
            val (a, b, c) = example.cards
            assertEquals(example.whyNot == null, isTrioSet(a, b, c), "misfiled: ${example.cards}")
        }
    }

    /** Each trait gets to be the shared one, so no reader concludes only shape or only fill can be. */
    @Test
    fun theAcceptedExamplesShareEachTraitAtLeastOnce() {
        val shared = TrioExamples.filter { it.whyNot == null }.flatMap { example ->
            traitVerdicts(example.cards).filter { it.second == TraitVerdict.SAME }.map { it.first }
        }
        assertEquals(3, shared.toSet().size, "not every trait is shared somewhere: $shared")
    }

    /**
     * The rejections carry captions naming why, so the rows have to break the rule where the
     * captions say: shape alone on the first, nothing shared at all on the second.
     */
    @Test
    fun theCaptionsMatchTheRowsTheySitUnder() {
        val rejected = TrioExamples.filter { it.whyNot != null }
        assertEquals(
            listOf(TraitVerdict.MIXED, TraitVerdict.DIFFERENT, TraitVerdict.SAME),
            traitVerdicts(rejected[0].cards).map { it.second },
        )
        assertTrue(traitVerdicts(rejected[1].cards).none { it.second == TraitVerdict.SAME })
    }
}
