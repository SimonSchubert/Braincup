package com.inspiredandroid.braincup.games.matrix

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Guards the one thing the spec-level checks in PatternSequenceGameTest cannot see: that a size
 * difference the generator treats as meaningful is still a size difference a player can see.
 *
 * The generator reasons in integer steps and never reads [SIZE_SCALES], so nothing else ties the
 * two together. An item is drawn either as a single centred figure filling the panel, or on the
 * 2x2 sub-grid where every slot is half as wide and every size gap halves with it.
 */
class MatrixGeneratorTest {

    @Test
    fun theSizeLadderRisesInEvenAndVisibleSteps() {
        assertEquals(SIZE_STEPS, SIZE_SCALES.size, "the ladder must have one scale per size step")
        assertEquals(1f, SIZE_SCALES.last(), "the largest step should fill its slot")
        for (step in 1 until SIZE_SCALES.size) {
            val ratio = SIZE_SCALES[step] / SIZE_SCALES[step - 1]
            assertTrue(
                ratio >= MIN_VISIBLE_RATIO,
                "steps ${step - 1} and $step are only ${ratio}x apart, below $MIN_VISIBLE_RATIO",
            )
        }
    }

    @Test
    fun theSubGridStepsStaySpacedOnceEverySlotHalves() {
        assertEquals(3, SPREAD_SIZE_STEPS.size, "a rule picks three values, so three is all it needs")
        assertTrue(
            SPREAD_SIZE_STEPS.all { it in SIZE_SCALES.indices },
            "every spread step must index the ladder: $SPREAD_SIZE_STEPS",
        )
        assertEquals(SPREAD_SIZE_STEPS.sorted(), SPREAD_SIZE_STEPS, "keep the steps ordered")
        assertEquals(
            SIZE_STEPS - 1,
            SPREAD_SIZE_STEPS.last(),
            "ungoverned size sits at the top step, so the top step has to stay available",
        )
        for (index in 1 until SPREAD_SIZE_STEPS.size) {
            val ratio = SIZE_SCALES[SPREAD_SIZE_STEPS[index]] / SIZE_SCALES[SPREAD_SIZE_STEPS[index - 1]]
            assertTrue(
                ratio >= MIN_SUB_GRID_RATIO,
                "spread steps ${SPREAD_SIZE_STEPS[index - 1]} and ${SPREAD_SIZE_STEPS[index]} " +
                    "are only ${ratio}x apart, below $MIN_SUB_GRID_RATIO",
            )
        }
    }

    @Test
    fun panelsDrawnOnTheSubGridOnlyUseTheSpreadSteps() {
        forEachProblem { problem, label ->
            val panels = problem.matrix + problem.options
            if (panels.none { panel -> panel.entities.any { it.slot != CENTERED_SLOT } }) return@forEachProblem
            val steps = panels.flatMap { panel -> panel.entities.map { it.sizeStep } }.distinct().sorted()
            assertTrue(
                steps.all { it in SPREAD_SIZE_STEPS },
                "$label: sub-grid item uses steps $steps, outside $SPREAD_SIZE_STEPS",
            )
        }
    }

    @Test
    fun everySizeStepComesFromTheDomainTheItemDrawsFrom() {
        forEachProblem { problem, label ->
            val domain = sizeDomain(problem)
            val steps = (problem.matrix + problem.options).map { it.spec.sizeStep }.distinct().sorted()
            assertTrue(steps.all { it in domain }, "$label: size steps $steps stray outside domain $domain")
        }
    }

    /**
     * The bug this test exists for: an option that differed from the answer by one size step, drawn
     * on the sub-grid where that step was under 4dp wide. The distractor picker prefers the option
     * that changes the fewest attributes, so a size-only near-miss is the common case, not a rare one.
     */
    @Test
    fun noTwoPanelsDifferByASizeStepTooSmallToSee() {
        forEachProblem { problem, label ->
            val specs = (problem.matrix + problem.options).map { it.spec }.distinct()
            val halved = problem.matrix.any { panel -> panel.entities.any { it.slot != CENTERED_SLOT } }
            val floor = if (halved) MIN_SUB_GRID_RATIO else MIN_VISIBLE_RATIO
            for (first in specs.indices) {
                for (second in first + 1 until specs.size) {
                    val a = specs[first]
                    val b = specs[second]
                    if (a.sizeStep == b.sizeStep) continue
                    if (a.withValue(MatrixAttribute.SIZE, b.sizeStep) != b) continue
                    val larger = maxOf(SIZE_SCALES[a.sizeStep], SIZE_SCALES[b.sizeStep])
                    val smaller = minOf(SIZE_SCALES[a.sizeStep], SIZE_SCALES[b.sizeStep])
                    assertTrue(
                        larger / smaller >= floor,
                        "$label: panels differ only by size, steps ${a.sizeStep} vs ${b.sizeStep}, " +
                            "${larger / smaller}x apart, below $floor",
                    )
                }
            }
        }
    }

    /** Which size steps an item draws from; mirrors the branch in MatrixGenerator.domainOf. */
    private fun sizeDomain(problem: MatrixProblem): List<Int> = if (MatrixAttribute.POSITION in problem.governedAttributes) {
        SPREAD_SIZE_STEPS
    } else {
        (0 until SIZE_STEPS).toList()
    }

    private fun forEachProblem(check: (MatrixProblem, String) -> Unit) {
        for (difficulty in 0..MatrixGenerator.MAX_DIFFICULTY) {
            for (seed in 0 until SEEDS) {
                val problem = MatrixGenerator(Random(seed.toLong())).generate(difficulty)
                check(problem, "difficulty $difficulty seed $seed")
            }
        }
    }

    private companion object {
        const val SEEDS = 200

        /** Smallest ratio between adjacent steps that still reads as a size change when centred. */
        const val MIN_VISIBLE_RATIO = 1.35f

        /** The sub-grid halves every slot, so the values a rule picks there have to be wider apart. */
        const val MIN_SUB_GRID_RATIO = 1.4f
    }
}
