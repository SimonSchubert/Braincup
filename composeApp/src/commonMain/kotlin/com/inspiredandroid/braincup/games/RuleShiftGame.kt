package com.inspiredandroid.braincup.games

import androidx.compose.runtime.Immutable
import com.inspiredandroid.braincup.app.AnswerFeedbackState
import com.inspiredandroid.braincup.app.RuleShiftKeyCell
import com.inspiredandroid.braincup.app.RuleShiftUiState
import com.inspiredandroid.braincup.games.tools.GameColor
import com.inspiredandroid.braincup.games.tools.Shape
import kotlinx.collections.immutable.toImmutableList
import kotlin.random.Random

/** One card: a count of identically coloured shapes. */
@Immutable
data class RuleShiftCard(
    val count: Int,
    val color: GameColor,
    val shape: Shape,
)

/**
 * Card sorting against a rule the player is never told: they discover it from right/wrong feedback
 * alone, and once they have it, it silently changes.
 *
 * The four key cards are fixed for the whole run and aligned by position, so key card `i` carries
 * count `i`, colour `i` and shape `i`. A stimulus card therefore points at one key card by number,
 * one by colour and one by form, and [rule] alone decides which of the three is the answer.
 *
 * The run ends after [TRIALS] cards or [MAX_CATEGORIES] categories, whichever comes first.
 * Deliberately untimed, like the test - speed is not what it measures, and a clock would pay for
 * fast guessing over working the rule out.
 *
 * Three deliberate departures from the standard administration (see `docs/game-science.md`):
 *
 *  - Only the 24 cards whose count, colour and shape indices are *all different* are dealt. On the
 *    full deck a card can point at the same key card under two rules, which makes that trial's
 *    feedback ambiguous and the error unclassifiable. Restricting the deck means every response is
 *    fully informative.
 *  - The next rule is drawn at random from the two not in force, rather than cycling
 *    colour -> form -> number. The fixed order is an administration detail, and a player who
 *    learns it can skip the probe that the shift exists to force.
 *  - A category is [CATEGORY_CRITERION] consecutive correct rather than the standard ten, over a
 *    36-card run rather than 64. At ten, nine trials in ten only confirm a rule the player already
 *    holds, and the run is a chore; the shifts are what the paradigm is about, so the game packs
 *    them in. Four still demonstrates the rule - guessing a category is under 1% per attempt.
 *
 * Nothing on screen counts the streak or the categories. Both are derivable from the feedback
 * either way, but rendering either would announce the shift one trial early, and the surprise is
 * the manipulation. The cards left are shown, as the shrinking deck is visible in the real test.
 */
class RuleShiftGame(
    private val random: Random = Random.Default,
) : Game() {
    enum class Rule { COLOR, SHAPE, COUNT }

    /** A completed sort, for the controller to score. Null when the tap was not a sort at all. */
    data class SortResult(val isCorrect: Boolean, val completedCategory: Boolean)

    /**
     * Trials are all the same difficulty, so there is no ramp to resume and no difficulty bonus to
     * earn: the task adapts itself by moving the rule whenever the player masters it.
     */
    override val adaptiveDifficulty: Boolean = false

    var rule: Rule = Rule.COLOR
        private set

    var stimulus: RuleShiftCard = keyCards[0]
        private set

    var categoriesCompleted: Int = 0
        private set

    /**
     * Errors that sort by the rule that was correct before the last shift. This is the WCST's
     * signature measure, and the reason the deck holds no ambiguous cards: on those, an error
     * cannot be told apart from a lucky guess.
     */
    var perseverativeErrors: Int = 0
        private set

    /** Cards dealt so far. The run is over when the deck runs out or every category is done. */
    var trialsUsed: Int = 0
        private set

    val cardsRemaining: Int get() = TRIALS - trialsUsed

    val isOver: Boolean get() = trialsUsed >= TRIALS || categoriesCompleted >= MAX_CATEGORIES

    private var consecutiveCorrect = 0
    private var perseveratedRule: Rule? = null

    /** The slot the player last tapped and whether it was right, held for the feedback beat. */
    private var feedbackSlot: Int? = null
    private var feedbackWasCorrect = false

    /**
     * True on the trial right after a silent shift, and on the first trial of the run. Neither can
     * be answered from anything the player has been told, so an error on one is the task working as
     * designed rather than a mistake, and must not cost the flawless-run bonus.
     */
    private var isFreeProbe = true

    override fun generateRound() {
        feedbackSlot = null
        stimulus = deck.random(random)
    }

    /**
     * Sort [slot] (1-based, matching the other tap-a-figure boards). Returns null while the
     * feedback beat is still up, so a double tap cannot sort the same card twice.
     */
    fun sort(slot: Int): SortResult? {
        if (feedbackSlot != null) return null
        if (slot !in 1..keyCards.size) return null

        val isCorrect = slot - 1 == correctIndex()
        feedbackSlot = slot
        feedbackWasCorrect = isCorrect
        trialsUsed++

        if (!isCorrect) {
            if (!isFreeProbe) answeredAllCorrect = false
            if (slot - 1 == perseveratedRule?.let { indexUnder(it) }) perseverativeErrors++
            consecutiveCorrect = 0
            isFreeProbe = false
            return SortResult(isCorrect = false, completedCategory = false)
        }

        isFreeProbe = false
        consecutiveCorrect++
        if (consecutiveCorrect < CATEGORY_CRITERION) {
            return SortResult(isCorrect = true, completedCategory = false)
        }

        categoriesCompleted++
        consecutiveCorrect = 0
        perseveratedRule = rule
        rule = Rule.entries.filter { it != rule }.random(random)
        isFreeProbe = true
        return SortResult(isCorrect = true, completedCategory = true)
    }

    override fun isCorrect(input: String): Boolean = input.toIntOrNull()?.minus(1) == correctIndex()

    override fun solution(): String = (correctIndex() + 1).toString()

    override fun toUiState(): RuleShiftUiState {
        val marked = feedbackSlot
        val state = if (feedbackWasCorrect) AnswerFeedbackState.CORRECT else AnswerFeedbackState.WRONG
        return RuleShiftUiState(
            keyCards = keyCards
                .mapIndexed { index, card ->
                    RuleShiftKeyCell(
                        card = card,
                        state = if (marked == index + 1) state else AnswerFeedbackState.NORMAL,
                    )
                }
                .toImmutableList(),
            stimulus = stimulus,
            isAwaitingNextCard = marked != null,
            cardsRemaining = cardsRemaining,
        )
    }

    /** Which key card [stimulus] belongs with under the rule now in force. */
    private fun correctIndex(): Int = indexUnder(rule)

    private fun indexUnder(rule: Rule): Int = when (rule) {
        Rule.COLOR -> keyCards.indexOfFirst { it.color == stimulus.color }
        Rule.SHAPE -> keyCards.indexOfFirst { it.shape == stimulus.shape }
        Rule.COUNT -> stimulus.count - 1
    }

    companion object {
        /** Consecutive correct sorts that complete a category and move the rule. */
        const val CATEGORY_CRITERION = 4

        /** Cards in a run, and the categories that end it early. */
        const val TRIALS = 36
        const val MAX_CATEGORIES = 6

        /** The standard four key cards, aligned so key card `i` holds count, colour and shape `i`. */
        val keyCards = listOf(
            RuleShiftCard(1, GameColor.RED, Shape.TRIANGLE),
            RuleShiftCard(2, GameColor.GREEN, Shape.STAR),
            RuleShiftCard(3, GameColor.YELLOW, Shape.CROSS),
            RuleShiftCard(4, GameColor.BLUE, Shape.CIRCLE),
        )

        /**
         * The 24 cards whose count, colour and shape each point at a *different* key card. Cards
         * where two of the three agree are left out: their feedback cannot separate the two rules.
         */
        val deck: List<RuleShiftCard> = buildList {
            val indices = keyCards.indices
            for (count in indices) {
                for (color in indices) {
                    for (shape in indices) {
                        if (count == color || color == shape || count == shape) continue
                        add(
                            RuleShiftCard(
                                count = count + 1,
                                color = keyCards[color].color,
                                shape = keyCards[shape].shape,
                            ),
                        )
                    }
                }
            }
        }
    }
}
