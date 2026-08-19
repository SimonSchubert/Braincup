package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.PatternSequenceUiState
import com.inspiredandroid.braincup.games.matrix.MAX_ENTITIES
import com.inspiredandroid.braincup.games.matrix.MatrixAttribute
import com.inspiredandroid.braincup.games.matrix.MatrixGenerator
import com.inspiredandroid.braincup.games.matrix.MatrixProblem
import com.inspiredandroid.braincup.games.matrix.MatrixRule
import com.inspiredandroid.braincup.games.matrix.SIZE_STEPS
import com.inspiredandroid.braincup.games.matrix.SPREAD_SIZE_STEPS
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PatternSequenceGameTest {

    @Test
    fun everyRowFollowsTheDeclaredRule() {
        forEachProblem { problem, label ->
            for ((attribute, rule) in problem.rules) {
                // Size can be drawn from a domain that skips a step, so read the rows as positions
                // in that domain: a progression is consecutive there, not in the raw value.
                val domain = if (attribute == MatrixAttribute.SIZE) sizeDomain(problem) else null
                val rows = problem.matrix
                    .map { panel ->
                        val value = panel.spec.valueOf(attribute)
                        domain?.indexOf(value) ?: value
                    }
                    .chunked(3)
                assertTrue(rowsFollow(rule, attribute, rows), "$label: $attribute does not follow $rule in $rows")
            }
        }
    }

    @Test
    fun ungovernedAttributesCarryNoInformation() {
        forEachProblem { problem, label ->
            // Positions follow from the count when a rule governs it, so they are determined
            // rather than free and are exempt from the constant check.
            val derived = if (MatrixAttribute.NUMBER in problem.governedAttributes) {
                setOf(MatrixAttribute.POSITION)
            } else {
                emptySet()
            }
            val free = MatrixAttribute.entries - problem.governedAttributes - derived
            for (attribute in free) {
                val values = problem.matrix.map { it.spec.valueOf(attribute) }.distinct()
                assertEquals(1, values.size, "$label: ungoverned $attribute varies across the matrix ($values)")
            }
        }
    }

    @Test
    fun exactlyOneOptionAnswersTheMatrix() {
        forEachProblem { problem, label ->
            val specs = problem.options.map { it.spec }
            assertEquals(specs.size, specs.distinct().size, "$label: duplicate options")
            assertEquals(1, specs.count { it == problem.answer.spec }, "$label: answer is not unique in the options")
            assertEquals(
                problem.answer.spec,
                specs[problem.correctOptionIndex],
                "$label: correctOptionIndex points at the wrong option",
            )
        }
    }

    /**
     * Building every distractor by perturbing one attribute of the answer would leave the answer
     * as the modal option, letting a solver skip the matrix entirely. No attribute value may be
     * held by more than half the options.
     */
    @Test
    fun optionSetIsNotSolvableWithoutTheMatrix() {
        forEachProblem { problem, label ->
            val answer = problem.answer.spec
            val limit = problem.options.size / 2
            for (attribute in problem.governedAttributes) {
                val matching = problem.options.count {
                    it.spec.valueOf(attribute) == answer.valueOf(attribute)
                }
                assertTrue(matching <= limit, "$label: $attribute is the answer's value in $matching options")
            }
        }
    }

    @Test
    fun optionCountFollowsTheDifficultyTier() {
        forEachProblem { problem, label, difficulty ->
            val expected = MatrixGenerator.optionCountFor(difficulty)
            assertEquals(expected, problem.options.size, "$label: wrong option count")
            assertEquals(MatrixGenerator.optionColumnsFor(expected), problem.optionColumns, "$label: wrong columns")
        }
    }

    @Test
    fun panelsStayWithinTheSubGrid() {
        forEachProblem { problem, label ->
            for (panel in problem.matrix + problem.options) {
                assertTrue(panel.entities.isNotEmpty(), "$label: empty panel")
                assertTrue(panel.entities.size <= MAX_ENTITIES, "$label: ${panel.entities.size} entities")
                assertEquals(panel.spec.number, panel.entities.size, "$label: entity count does not match the spec")
            }
        }
    }

    @Test
    fun sameSeedProducesIdenticalProblem() {
        for (difficulty in 0..MatrixGenerator.MAX_DIFFICULTY) {
            val first = MatrixGenerator(Random(1234L)).generate(difficulty)
            val second = MatrixGenerator(Random(1234L)).generate(difficulty)
            assertEquals(first, second, "difficulty $difficulty is not reproducible")
        }
    }

    @Test
    fun difficultyRisesWithTheRoundAndThenHolds() {
        assertEquals(0, PatternSequenceGame.difficultyFor(0))
        assertEquals(1, PatternSequenceGame.difficultyFor(2))
        assertEquals(3, PatternSequenceGame.difficultyFor(7))
        assertEquals(MatrixGenerator.MAX_DIFFICULTY, PatternSequenceGame.difficultyFor(12))
        assertEquals(MatrixGenerator.MAX_DIFFICULTY, PatternSequenceGame.difficultyFor(60))
    }

    /**
     * The screen turns a tap into `row * optionColumns + column`, so the row chunking in the ui
     * state has to lay the options out in the same flat order the answer index refers to.
     */
    @Test
    fun optionRowsPreserveTheFlatOptionOrder() {
        for (round in 0..14) {
            val game = PatternSequenceGame(Random(round.toLong())).apply {
                this.round = round
                nextRound()
            }
            val uiState = game.toUiState() as PatternSequenceUiState
            val flattened = uiState.optionRows.flatten().map { it.panel }
            assertEquals(game.problem.options, flattened, "round $round: option order changed")
            assertEquals(
                game.problem.options.size,
                uiState.optionRows.sumOf { it.size },
                "round $round: options lost in chunking",
            )
            assertTrue(
                uiState.optionRows.all { it.size == uiState.optionColumns },
                "round $round: ragged option grid",
            )
            assertEquals(9, uiState.matrix.size, "round $round: matrix is not 3x3")
            assertEquals(null, uiState.matrix[8], "round $round: the answer cell is not blank")
        }
    }

    @Test
    fun answerIsAcceptedOnlyAtItsOwnIndex() {
        val game = PatternSequenceGame(Random(9L)).apply { nextRound() }
        val correct = game.problem.correctOptionIndex
        assertTrue(game.isCorrect(correct.toString()))
        for (index in game.problem.options.indices) {
            if (index != correct) assertTrue(!game.isCorrect(index.toString()), "option $index was accepted")
        }
        assertTrue(!game.isCorrect(""))
    }

    private fun forEachProblem(check: (MatrixProblem, String, Int) -> Unit) {
        for (difficulty in 0..MatrixGenerator.MAX_DIFFICULTY) {
            for (seed in 0 until SEEDS) {
                val problem = MatrixGenerator(Random(seed.toLong())).generate(difficulty)
                check(problem, "difficulty $difficulty seed $seed", difficulty)
            }
        }
    }

    private fun forEachProblem(check: (MatrixProblem, String) -> Unit) = forEachProblem { problem, label, _ -> check(problem, label) }

    /** Which size steps this item draws from; the sub-grid halves every slot, so it skips one. */
    private fun sizeDomain(problem: MatrixProblem): List<Int> = if (MatrixAttribute.POSITION in problem.governedAttributes) {
        SPREAD_SIZE_STEPS
    } else {
        (0 until SIZE_STEPS).toList()
    }

    private fun rowsFollow(rule: MatrixRule, attribute: MatrixAttribute, rows: List<List<Int>>): Boolean = when (rule) {
        MatrixRule.CONSTANT -> rows.all { it.distinct().size == 1 }
        MatrixRule.DISTRIBUTE_THREE -> {
            val set = rows.first().toSet()
            set.size == 3 && rows.all { it.toSet() == set }
        }
        MatrixRule.PROGRESSION -> {
            val steps = rows.map { row -> stepOf(attribute, row) }
            steps.all { it != null && it == steps.first() && it != 0 }
        }
        MatrixRule.ARITHMETIC -> rows.all { it[0] + it[1] == it[2] } || rows.all { it[0] - it[1] == it[2] }
    }

    /** The shared step of a three-value row, or null when the two gaps disagree. */
    private fun stepOf(attribute: MatrixAttribute, row: List<Int>): Int? {
        if (attribute == MatrixAttribute.ROTATION) {
            val first = (row[1] - row[0]).mod(4)
            return if (first == (row[2] - row[1]).mod(4)) first else null
        }
        val first = row[1] - row[0]
        return if (first == row[2] - row[1] && abs(first) > 0) first else null
    }

    private companion object {
        const val SEEDS = 60
    }
}
