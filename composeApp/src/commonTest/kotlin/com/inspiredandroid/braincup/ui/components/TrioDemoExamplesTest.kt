package com.inspiredandroid.braincup.ui.components

import com.inspiredandroid.braincup.games.TrioCard
import com.inspiredandroid.braincup.games.TrioTrait
import com.inspiredandroid.braincup.games.isTrioSet
import com.inspiredandroid.braincup.games.mixedTrioTraits
import com.inspiredandroid.braincup.games.sharesAnyTrait
import kotlin.test.Test
import kotlin.test.assertEquals

class TrioDemoExamplesTest {

    @Test
    fun theDemoShowsTriosThenNearMisses() {
        val judged = TrioExamples.map { (a, b, c) -> isTrioSet(a, b, c) }
        assertEquals(judged.sortedDescending(), judged, "a near miss is listed among the trios")
        TrioExamples.forEach { assertEquals(3, it.toSet().size, "not three distinct cards: $it") }
    }

    /** Every trait is the one held the same in some trio, and the one that breaks some near miss. */
    @Test
    fun everyTraitTakesATurnOnBothSides() {
        val (sets, notSets) = TrioExamples.partition { (a, b, c) -> isTrioSet(a, b, c) }
        val sharedSomewhere = sets.flatMap { cards ->
            TrioTrait.entries.filter { trait -> isSameOnAll(cards, trait) }
        }.toSet()
        val brokenSomewhere = notSets.flatMap { (a, b, c) -> mixedTrioTraits(a, b, c) }.toSet()
        assertEquals(TrioTrait.entries.toSet(), sharedSomewhere)
        assertEquals(TrioTrait.entries.toSet(), brokenSomewhere)
    }

    private fun isSameOnAll(cards: List<TrioCard>, trait: TrioTrait): Boolean = when (trait) {
        TrioTrait.SHAPE -> cards.map { it.shape }.toSet().size == 1
        TrioTrait.COUNT -> cards.map { it.count }.toSet().size == 1
        TrioTrait.FILL -> cards.map { it.fill }.toSet().size == 1
    }

    /** One caption per near miss, so each reads as one step away from a trio. */
    @Test
    fun eachNearMissFailsForOneReason() {
        TrioExamples.filterNot { (a, b, c) -> isTrioSet(a, b, c) }.forEach { (a, b, c) ->
            val reasons = mixedTrioTraits(a, b, c).size + if (sharesAnyTrait(a, b, c)) 0 else 1
            assertEquals(1, reasons, "$a, $b, $c")
        }
    }
}
