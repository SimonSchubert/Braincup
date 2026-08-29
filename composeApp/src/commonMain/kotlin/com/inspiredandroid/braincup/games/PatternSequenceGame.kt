package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.MatrixOptionCell
import com.inspiredandroid.braincup.app.PatternSequenceUiState
import com.inspiredandroid.braincup.games.matrix.MatrixGenerator
import com.inspiredandroid.braincup.games.matrix.MatrixProblem
import kotlinx.collections.immutable.toImmutableList
import kotlin.random.Random

/**
 * Matrix reasoning: read the rule running along the rows of a 3x3 grid, then pick the panel that
 * belongs in the empty corner. [MatrixGenerator] does the reasoning work; this class only maps
 * the round counter onto a difficulty tier.
 */
class PatternSequenceGame(random: Random = Random.Default) : Game() {

    private val generator = MatrixGenerator(random)

    lateinit var problem: MatrixProblem
        private set

    override fun generateRound() {
        problem = generator.generate(difficultyFor(round))
    }

    override fun isCorrect(input: String): Boolean = input.toIntOrNull() == problem.correctOptionIndex

    override fun solution(): String = problem.ruleSummary

    override fun hint(): String? = null

    override fun toUiState() = PatternSequenceUiState(
        matrix = problem.matrix
            .mapIndexed { index, panel -> panel.takeIf { index != MatrixProblem.ANSWER_INDEX } }
            .toImmutableList(),
        optionRows = problem.options
            .map { MatrixOptionCell(it) }
            .chunked(problem.optionColumns)
            .map { it.toImmutableList() }
            .toImmutableList(),
        optionColumns = problem.optionColumns,
    )

    companion object {

        /**
         * Derived from the round instead of stepped on exact round values, so a player resuming
         * mid-ramp lands on the tier their stored round earned rather than back at the easiest one.
         */
        fun difficultyFor(round: Int): Int = (round / 2).coerceIn(0, MatrixGenerator.MAX_DIFFICULTY)
    }
}
