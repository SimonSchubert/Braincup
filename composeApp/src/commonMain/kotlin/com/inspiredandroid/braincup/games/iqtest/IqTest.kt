package com.inspiredandroid.braincup.games.iqtest

import androidx.compose.runtime.Immutable
import com.inspiredandroid.braincup.games.matrix.MatrixGenerator
import com.inspiredandroid.braincup.games.matrix.MatrixProblem
import kotlin.random.Random

@Immutable
data class TierResult(val tier: Int, val correct: Int, val total: Int)

/**
 * One attempt at the test: the items, the player's picks, and where they currently are.
 *
 * Items are generated on demand and kept, rather than all thirty up front. Generation is cheap but
 * not free, and wasmJs has no worker thread to hide a burst of it behind, so paying for one item at
 * a time keeps the first frame instant. Because the generator is always driven in item order and
 * never re-run for an item already built, [seed] still reproduces an attempt exactly, which is what
 * lets a finished result be replayed on the review screen.
 */
class IqTest(val seed: Long) {

    private val generator = MatrixGenerator(Random(seed))
    private val items = mutableListOf<MatrixProblem>()
    private val responses = arrayOfNulls<Int>(IqTestBlueprint.ITEM_COUNT)

    var currentIndex: Int = 0
        private set

    fun problemAt(index: Int): MatrixProblem {
        require(index in 0 until IqTestBlueprint.ITEM_COUNT) { "item $index out of range" }
        while (items.size <= index) {
            items.add(generator.generate(IqTestBlueprint.tierFor(items.size)))
        }
        return items[index]
    }

    fun responseAt(index: Int): Int? = responses[index]

    fun isCorrectAt(index: Int): Boolean = responses[index] == problemAt(index).correctOptionIndex

    fun select(optionIndex: Int) {
        responses[currentIndex] = optionIndex
    }

    fun goTo(index: Int) {
        currentIndex = index.coerceIn(0, IqTestBlueprint.ITEM_COUNT - 1)
    }

    fun next(): Boolean {
        if (currentIndex >= IqTestBlueprint.ITEM_COUNT - 1) return false
        currentIndex++
        return true
    }

    fun previous(): Boolean {
        if (currentIndex <= 0) return false
        currentIndex--
        return true
    }

    val answeredCount: Int get() = responses.count { it != null }

    val isOnLastItem: Boolean get() = currentIndex == IqTestBlueprint.ITEM_COUNT - 1

    /** Unanswered items count as wrong, the same way a timed-out item does. */
    val rawScore: Int
        get() = (0 until IqTestBlueprint.ITEM_COUNT).count { responses[it] != null && isCorrectAt(it) }

    fun tierBreakdown(): List<TierResult> = IqScoring.TIER_DIFFICULTY_LOGITS.indices.map { tier ->
        val itemsInTier = (0 until IqTestBlueprint.ITEM_COUNT).filter { IqTestBlueprint.tierFor(it) == tier }
        TierResult(
            tier = tier,
            correct = itemsInTier.count { responses[it] != null && isCorrectAt(it) },
            total = itemsInTier.size,
        )
    }
}
