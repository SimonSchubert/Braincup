package com.inspiredandroid.braincup.ui.components.learn

import androidx.compose.runtime.Immutable
import com.inspiredandroid.braincup.learn.LearnVisual
import kotlin.math.abs

/**
 * What a figure paints, by role, as the text of each value.
 *
 * The section's colour code lives in two places that used to have no way of agreeing. A figure
 * knows perfectly well which of its numbers is the given, which is the working and which is the
 * answer - it has to, to draw them - while the line of text beside it was left guessing from
 * punctuation, and fell back to the given colour whenever the guess failed. That is why
 * "-4 is 4 left of 0" printed all three of its numbers orange under a number line that was already
 * drawing the -4 green.
 *
 * So the figure says, and the text reads it off. Which is the tie-break the section already
 * documented for the cases where the two disagree, applied before they can disagree.
 *
 * Values are held as **the text the figure prints**, so they can be matched against the runs of a
 * formula without either side parsing the other.
 *
 * An empty [FigureRoles] means "no help": the text falls back to its own inference and the guard
 * test skips the pair. Any figure whose values do not divide cleanly into these three roles should
 * return one, rather than a guess that would tint the wrong number.
 */
@Immutable
data class FigureRoles(
    val given: Set<String> = emptySet(),
    val working: Set<String> = emptySet(),
    val answer: Set<String> = emptySet(),
) {
    val isEmpty: Boolean get() = given.isEmpty() && working.isEmpty() && answer.isEmpty()

    /**
     * The role [value] carries, or null when the figure does not draw it.
     *
     * Answer beats working beats given, because a value can honestly be two things at once and the
     * later role is the more specific claim. A number line hopping -4 out of zero labels its arc
     * "-4" and marks where it lands "-4": the same text is the working and the answer, and green is
     * the one that says something the reader cannot get from anywhere else.
     */
    fun roleOf(value: String): FigureRole? = when {
        // The given and the working are two different quantities. When the figure prints the same
        // text for both, nothing here can tell which of them a line is naming, so it says nothing
        // and the line keeps its own judgement: "0.4 = 0.40" is two squares whose captions differ
        // only in a nought the figure was told to keep, and calling either one the working turned
        // the first square's own number blue.
        value in given && value in working -> null
        value in answer -> FigureRole.ANSWER
        value in working -> FigureRole.WORKING
        value in given -> FigureRole.GIVEN
        else -> null
    }
}

enum class FigureRole { GIVEN, WORKING, ANSWER }

/** Both ways a hop of [step] can be named: signed on the arc, bare in a sentence about it. */
private fun hopNames(step: Int): List<String> = listOf(step.toString(), abs(step).toString())

/**
 * The roles [this] figure draws, for the text beside it to colour itself by.
 *
 * Only the families whose numbers genuinely divide into given, working and answer are mapped. The
 * rest return an empty [FigureRoles] on purpose: a ratio bar's runs are equal partners with no
 * answer among them, a sequence has no given to step from, and a shape figure's labels are sides
 * rather than a sum. Guessing at those would tint a number to mean something the picture never
 * said, which is the failure this whole mechanism exists to stop.
 */
fun LearnVisual.roles(): FigureRoles = when (this) {
    // The start is what you count from, the hops are the movement, the tick you land on is the
    // answer. This mirrors `drawNumberLine`'s own `roleColor`, which reads from here.
    is LearnVisual.NumberLine -> {
        // The arcs carry the labels, so the roles have to be the hops the figure actually draws:
        // an even split into `hops` labels each one `jump / hops`, not the whole jump.
        val steps = when {
            hopSteps.isNotEmpty() -> hopSteps
            hops > 1 -> List(hops) { jump / hops }
            else -> listOf(jump)
        }
        val travel = steps.sum()
        FigureRoles(
            given = setOfNotNull(start?.toString()),
            working = steps.filter { it != 0 }.flatMap(::hopNames).toSet() +
                (if (steps.size > 1 && travel != 0) hopNames(travel) else emptyList()),
            answer = if (start != null && reveal) {
                setOfNotNull(
                    (start + travel).toString().takeIf { travel != 0 },
                    thenJump?.let { (start + it).toString() },
                )
            } else {
                emptySet()
            },
        )
    }

    // The first square, the one added to or measured against it, and the third square the sum
    // arrives on.
    //
    // A percentage's whole is left out on purpose. The figure prints "20% of 80" above the square
    // in the ordinary ink, as part of the question rather than as a value with a role, so claiming
    // the 80 would overrule the tag the author put on it with a colour the picture never used.
    is LearnVisual.DecimalGrid -> FigureRoles(
        given = setOf(formatDecimal(value)),
        working = setOfNotNull((plus ?: compare)?.let { formatDecimal(it) }),
        answer = buildSet {
            if (reveal) {
                plus?.let { add(formatDecimal(value + it)) }
                // What the percentage comes to, unless that is the same run of digits as the
                // percentage itself: "20% of 100 = 20" prints 20 for the rate and 20 for the
                // amount, and nothing in the text tells a matcher which one it is looking at.
                of?.let {
                    val amount = formatDecimal(value * it)
                    if (amount != formatDecimal(value * 100)) add(amount)
                }
            }
        },
    )

    // Dots already in the frame, dots arriving, and the total the caption states.
    is LearnVisual.TenFrame -> FigureRoles(
        given = setOf(filled.toString()),
        working = setOf(added.toString()),
        answer = if (added > 0 && reveal) setOf((filled + added).toString()) else emptySet(),
    )

    // The bar drawn first, the one under it, and the total on the third bar. Held as "3/5" text
    // because that is the run a sentence names a fraction with, and what `drawFraction` prints.
    is LearnVisual.Fraction -> {
        fun name(pair: Pair<Int, Int>) = "${pair.first}/${pair.second}"
        val second = plus ?: compare
        FigureRoles(
            given = setOf(name(numerator to denominator)),
            working = setOfNotNull(second?.let(::name)),
            answer = if (reveal) {
                setOfNotNull(plus?.let { name(numerator + it.first to denominator) })
            } else {
                emptySet()
            },
        )
    }

    // Rows are the given and what is in each row is the working, which is how the array labels
    // itself. A split array is a different figure: there the two row bands take the colours and the
    // column count runs through both, so it stays out of this.
    is LearnVisual.ArrayDots -> if (bandSplit != null) {
        FigureRoles()
    } else {
        FigureRoles(given = setOf(rows.toString()), working = setOf(cols.toString()))
    }

    // Both piles of a sum, and what they come to.
    is LearnVisual.PlaceValue -> {
        fun name(tens: Int, ones: Int) = (tens * 10 + ones).toString()
        val second = plus ?: compare
        FigureRoles(
            given = setOf(name(tens, ones)),
            working = setOfNotNull(second?.let { name(it.first, it.second) }),
            answer = if (reveal) {
                setOfNotNull(plus?.let { name(tens + it.first, ones + it.second) })
            } else {
                emptySet()
            },
        )
    }

    // Everything else says nothing. See the doc above: silence is the safe answer.
    else -> FigureRoles()
}
