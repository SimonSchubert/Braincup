package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.TrioUiState
import kotlinx.collections.immutable.toImmutableList
import kotlin.random.Random

enum class TrioShape { CIRCLE, SQUARE, TRIANGLE }

enum class TrioFill { SOLID, STRIPED, OUTLINE }

enum class TrioTrait { SHAPE, COUNT, FILL }

data class TrioCard(
    val shape: TrioShape,
    val count: Int,
    val fill: TrioFill,
) {
    init {
        require(count in 1..3)
    }
}

fun isTrioSet(a: TrioCard, b: TrioCard, c: TrioCard): Boolean = mixedTrioTraits(a, b, c).isEmpty() && sharesAnyTrait(a, b, c)

fun sharesAnyTrait(a: TrioCard, b: TrioCard, c: TrioCard): Boolean = TrioTrait.entries.any { trait ->
    a.attribute(trait) == b.attribute(trait) && b.attribute(trait) == c.attribute(trait)
}

/** The traits that are on two of the cards but not the third, which is what disqualifies a trio. */
fun mixedTrioTraits(a: TrioCard, b: TrioCard, c: TrioCard): List<TrioTrait> = TrioTrait.entries.filter { trait ->
    val values = setOf(a.attribute(trait), b.attribute(trait), c.attribute(trait))
    values.size == 2
}

fun completingTrioCard(a: TrioCard, b: TrioCard): TrioCard = TrioCard(
    shape = TrioShape.entries[completeAttribute(a.shape.ordinal, b.shape.ordinal)],
    count = completeAttribute(a.count - 1, b.count - 1) + 1,
    fill = TrioFill.entries[completeAttribute(a.fill.ordinal, b.fill.ordinal)],
)

/**
 * Number of attributes that differ across the three cards (1 = easiest, 3 = hardest).
 *
 * The third card is determined by the first two, so it cannot change the count: where [a] and [b]
 * differ it differs from both, and where they agree it agrees too.
 */
fun trioSetHardness(a: TrioCard, b: TrioCard): Int = TrioTrait.entries.count { a.attribute(it) != b.attribute(it) }

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

private fun TrioCard.attribute(trait: TrioTrait): Int = when (trait) {
    TrioTrait.SHAPE -> shape.ordinal
    TrioTrait.COUNT -> count - 1
    TrioTrait.FILL -> fill.ordinal
}

private fun completeAttribute(x: Int, y: Int): Int = if (x == y) x else 3 - x - y

/**
 * Find three cards that share at least one trait, with every other trait all different. Unlike
 * the card game Set, a trio that differs in everything does not count: there is nothing shared to
 * spot, and it was the example players found hardest to accept.
 *
 * 12 unique cards are dealt each round. Tapping toggles a card; the third tap is judged in place.
 * A wrong trio flashes and deselects so the same board can be searched again, and [mixedTraits] or
 * [sharesNothing] says what broke it until the next tap, since the rule is otherwise only learnt by
 * guessing. Difficulty is the hardness of the guaranteed set, derived from [round] so adaptive
 * resume stays honest.
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

    var mixedTraits: List<TrioTrait> = emptyList()
        private set

    var sharesNothing: Boolean = false
        private set

    override fun generateRound() {
        selected = linkedSetOf()
        feedback = CardFeedback.NONE
        mixedTraits = emptyList()
        sharesNothing = false
        cards = dealBoard()
    }

    fun tap(index: Int): TapResult {
        if (feedback == CardFeedback.CORRECT || feedback == CardFeedback.WRONG) {
            return TapResult.Ignored
        }
        if (index !in cards.indices) return TapResult.Ignored
        mixedTraits = emptyList()
        sharesNothing = false
        if (index in selected) {
            selected.remove(index)
            return TapResult.Toggled
        }
        if (selected.size >= 3) return TapResult.Ignored
        selected.add(index)
        if (selected.size < 3) return TapResult.Toggled

        val picks = selected.toList()
        val (a, b, c) = picks.map { cards[it] }
        mixedTraits = mixedTrioTraits(a, b, c)
        sharesNothing = mixedTraits.isEmpty() && !sharesAnyTrait(a, b, c)
        val isSet = isTrioSet(a, b, c)
        feedback = if (isSet) CardFeedback.CORRECT else CardFeedback.WRONG
        if (!isSet) answeredAllCorrect = false
        return if (isSet) TapResult.Correct else TapResult.Wrong
    }

    fun clearSelection() {
        selected = linkedSetOf()
        feedback = CardFeedback.NONE
    }

    /** Marks a set the board holds. Reached from give up, which awards no point for it. */
    fun revealSolution(): Boolean {
        if (feedback == CardFeedback.CORRECT || feedback == CardFeedback.WRONG) return false
        val set = findTrioSets(cards).firstOrNull() ?: return false
        selected = LinkedHashSet(set)
        feedback = CardFeedback.CORRECT
        return true
    }

    internal fun loadBoard(board: List<TrioCard>) {
        require(board.size == BOARD_SIZE)
        selected = linkedSetOf()
        feedback = CardFeedback.NONE
        mixedTraits = emptyList()
        sharesNothing = false
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
        mixedTraits = mixedTraits.toImmutableList(),
        sharesNothing = sharesNothing,
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
            if (c != a && c != b && trioSetHardness(a, b) == hardness) {
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
