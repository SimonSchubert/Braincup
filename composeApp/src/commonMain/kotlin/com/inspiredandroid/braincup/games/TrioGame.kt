package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.TrioUiState
import kotlinx.collections.immutable.toImmutableList
import kotlin.random.Random

enum class TrioShape { CIRCLE, SQUARE, TRIANGLE }

enum class TrioFill { SOLID, STRIPED, OUTLINE }

data class TrioCard(
    val shape: TrioShape,
    val count: Int,
    val fill: TrioFill,
) {
    init {
        require(count in 1..3)
    }
}

/** True when no trait is mixed and at least one trait is shared. */
fun isTrioSet(a: TrioCard, b: TrioCard, c: TrioCard): Boolean {
    var shared = false
    for (i in 0..2) {
        val x = a.attribute(i)
        val y = b.attribute(i)
        val z = c.attribute(i)
        val allSame = x == y && y == z
        val allDifferent = x != y && y != z && x != z
        if (!allSame && !allDifferent) return false
        if (allSame) shared = true
    }
    return shared
}

fun completingTrioCard(a: TrioCard, b: TrioCard): TrioCard = TrioCard(
    shape = TrioShape.entries[completeAttribute(a.shape.ordinal, b.shape.ordinal)],
    count = completeAttribute(a.count - 1, b.count - 1) + 1,
    fill = TrioFill.entries[completeAttribute(a.fill.ordinal, b.fill.ordinal)],
)

/** Number of attributes that differ across the three cards (1 = easiest, 3 = hardest). */
fun trioSetHardness(a: TrioCard, b: TrioCard, c: TrioCard): Int {
    var different = 0
    for (i in 0..2) {
        if (a.attribute(i) != b.attribute(i)) different++
    }
    return different
}

fun findTrioSets(cards: List<TrioCard>): List<List<Int>> {
    val result = mutableListOf<List<Int>>()
    for (i in cards.indices) {
        for (j in i + 1 until cards.size) {
            for (k in j + 1 until cards.size) {
                if (isTrioSet(cards[i], cards[j], cards[k])) {
                    result.add(listOf(i, j, k))
                }
            }
        }
    }
    return result
}

fun allTrioCards(): List<TrioCard> = buildList {
    for (shape in TrioShape.entries) {
        for (count in 1..3) {
            for (fill in TrioFill.entries) {
                add(TrioCard(shape, count, fill))
            }
        }
    }
}

private fun TrioCard.attribute(index: Int): Int = when (index) {
    0 -> shape.ordinal
    1 -> count - 1
    else -> fill.ordinal
}

private fun completeAttribute(x: Int, y: Int): Int = if (x == y) x else 3 - x - y

/**
 * Find three cards that share a trait; every other trait is all-same or all-different.
 *
 * 12 unique cards are dealt each round. Tapping toggles a card; the third tap is judged in place.
 * A wrong trio flashes and deselects so the same board can be searched again. Difficulty is the
 * hardness of the guaranteed set, derived from [round] so adaptive resume stays honest.
 */
class TrioGame(
    private val random: Random = Random.Default,
) : Game() {
    enum class CardFeedback { NONE, SELECTED, CORRECT, WRONG, DIMMED }

    enum class TapResult { Toggled, Correct, Wrong, Ignored }

    var cards: List<TrioCard> = emptyList()
        private set

    var selected: LinkedHashSet<Int> = linkedSetOf()
        private set

    var feedback: CardFeedback = CardFeedback.NONE
        private set

    override fun generateRound() {
        selected = linkedSetOf()
        feedback = CardFeedback.NONE
        cards = dealBoard()
    }

    fun tap(index: Int): TapResult {
        if (feedback == CardFeedback.CORRECT || feedback == CardFeedback.WRONG) {
            return TapResult.Ignored
        }
        if (index !in cards.indices) return TapResult.Ignored
        if (index in selected) {
            selected.remove(index)
            return TapResult.Toggled
        }
        if (selected.size >= 3) return TapResult.Ignored
        selected.add(index)
        if (selected.size < 3) return TapResult.Toggled

        val picks = selected.toList()
        val isSet = isTrioSet(cards[picks[0]], cards[picks[1]], cards[picks[2]])
        feedback = if (isSet) CardFeedback.CORRECT else CardFeedback.WRONG
        if (!isSet) answeredAllCorrect = false
        return if (isSet) TapResult.Correct else TapResult.Wrong
    }

    fun clearSelection() {
        selected = linkedSetOf()
        feedback = CardFeedback.NONE
    }

    internal fun loadBoard(board: List<TrioCard>) {
        require(board.size == BOARD_SIZE)
        selected = linkedSetOf()
        feedback = CardFeedback.NONE
        cards = board
    }

    override fun isCorrect(input: String): Boolean {
        val indices = input.split(",").mapNotNull { it.trim().toIntOrNull() }
        if (indices.size != 3) return false
        if (indices.any { it !in cards.indices }) return false
        if (indices.toSet().size != 3) return false
        return isTrioSet(cards[indices[0]], cards[indices[1]], cards[indices[2]])
    }

    override fun solution(): String {
        val set = findTrioSets(cards).firstOrNull() ?: return ""
        return set.joinToString(", ") { (it + 1).toString() }
    }

    override fun hint(): String? = null

    override fun toUiState() = TrioUiState(
        cards = cards.mapIndexed { index, card ->
            val isSelected = index in selected
            val cellFeedback = when {
                feedback == CardFeedback.CORRECT && isSelected -> CardFeedback.CORRECT
                feedback == CardFeedback.CORRECT && !isSelected -> CardFeedback.DIMMED
                feedback == CardFeedback.WRONG && isSelected -> CardFeedback.WRONG
                isSelected -> CardFeedback.SELECTED
                else -> CardFeedback.NONE
            }
            TrioUiState.Card(
                shape = card.shape,
                count = card.count,
                fill = card.fill,
                feedback = cellFeedback,
            )
        }.toImmutableList(),
        columns = COLUMNS,
    )

    private fun dealBoard(): List<TrioCard> {
        val hardness = if (round < 4) 1 else 2
        val seed = randomSetOfHardness(hardness)
        val leftover = allTrioCards().filterNot { it in seed }.shuffled(random)
        return (seed + leftover.take(BOARD_SIZE - seed.size)).shuffled(random)
    }

    private fun randomSetOfHardness(hardness: Int): List<TrioCard> {
        val deck = allTrioCards()
        repeat(DEAL_ATTEMPTS) {
            val a = deck.random(random)
            val b = deck.filter { it != a }.random(random)
            val c = completingTrioCard(a, b)
            if (c != a && c != b && trioSetHardness(a, b, c) == hardness) {
                return listOf(a, b, c)
            }
        }
        return listOf(
            TrioCard(TrioShape.CIRCLE, 1, TrioFill.SOLID),
            TrioCard(TrioShape.CIRCLE, 2, TrioFill.SOLID),
            TrioCard(TrioShape.CIRCLE, 3, TrioFill.SOLID),
        )
    }

    companion object {
        const val BOARD_SIZE = 12
        const val COLUMNS = 3
        private const val DEAL_ATTEMPTS = 200
    }
}
