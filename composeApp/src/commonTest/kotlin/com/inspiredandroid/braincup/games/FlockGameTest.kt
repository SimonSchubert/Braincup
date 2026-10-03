package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.AnswerFeedbackState
import com.inspiredandroid.braincup.games.FlockGame.Direction
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FlockGameTest {

    private fun wrongDirectionFor(trial: FlockGame.Trial): Direction = Direction.entries.first { it != trial.target }

    private fun dealCorrectly(game: FlockGame, count: Int): List<FlockGame.Trial> = buildList {
        repeat(count) {
            game.nextRound()
            add(game.trial)
            game.answer(game.trial.target)
        }
    }

    @Test
    fun `incongruent flankers always point the opposite way`() {
        val game = FlockGame(Random(1L))
        dealCorrectly(game, 300).forEach { trial ->
            if (trial.isCongruent) {
                assertEquals(trial.target, trial.flankers)
            } else {
                assertEquals(trial.target.opposite, trial.flankers, "sideways flankers on $trial")
            }
        }
    }

    @Test
    fun `every target direction comes up`() {
        val game = FlockGame(Random(2L))
        val targets = dealCorrectly(game, 200).map { it.target }.toSet()
        assertEquals(Direction.entries.toSet(), targets)
    }

    @Test
    fun `the congruent share holds at the scheduled rate`() {
        val game = FlockGame(Random(3L))
        val trials = dealCorrectly(game, 1000)
        val congruent = trials.count { it.isCongruent }
        val expected = trials.size * FlockGame.CONGRUENT_PER_BAG / FlockGame.BAG_SIZE
        assertTrue(
            abs(congruent - expected) <= FlockGame.BAG_SIZE,
            "expected about $expected congruent trials in ${trials.size}, got $congruent",
        )
    }

    @Test
    fun `every bag holds its share, so neither condition comes out in a streak`() {
        val game = FlockGame(Random(4L))
        dealCorrectly(game, 500)
            .chunked(FlockGame.BAG_SIZE)
            .filter { it.size == FlockGame.BAG_SIZE }
            .forEachIndexed { index, bag ->
                assertEquals(FlockGame.CONGRUENT_PER_BAG, bag.count { it.isCongruent }, "bag $index is off its share")
            }
    }

    @Test
    fun `the target never repeats on consecutive trials`() {
        val game = FlockGame(Random(5L))
        dealCorrectly(game, 300).zipWithNext { previous, next ->
            assertNotEquals(previous.target, next.target)
        }
    }

    @Test
    fun `only the middle bird's direction is correct`() {
        val game = FlockGame(Random(6L))
        repeat(100) {
            game.nextRound()
            val trial = game.trial
            Direction.entries.forEach { direction ->
                assertEquals(direction == trial.target, game.isCorrect(direction.name), "$direction on $trial")
            }
            assertEquals(trial.target.name, game.solution())
            game.answer(trial.target)
        }
    }

    @Test
    fun `a second swipe on the same trial is ignored`() {
        val game = FlockGame(Random(7L))
        game.nextRound()
        assertEquals(true, game.answer(game.trial.target))
        assertNull(game.answer(game.trial.target))
    }

    @Test
    fun `following the flankers on an incongruent trial is wrong`() {
        val game = FlockGame(Random(8L))
        do game.nextRound() while (game.trial.isCongruent)
        assertEquals(false, game.answer(game.trial.flankers))
        assertFalse(game.answeredAllCorrect)
    }

    @Test
    fun `the congruency effect needs enough correct trials of both kinds`() {
        val game = FlockGame(Random(10L))
        assertNull(game.congruencyEffectMillis())
        dealCorrectly(game, FlockGame.BAG_SIZE)
        assertNull(game.congruencyEffectMillis())
    }

    @Test
    fun `errors are left out of the congruency effect`() {
        val game = FlockGame(Random(11L))
        repeat(FlockGame.MIN_TRIALS_PER_CONDITION * FlockGame.BAG_SIZE * 4) {
            game.nextRound()
            game.answer(wrongDirectionFor(game.trial))
        }
        assertNull(game.congruencyEffectMillis())
    }

    @Test
    fun `a long clean run reports an effect`() {
        val game = FlockGame(Random(12L))
        dealCorrectly(game, FlockGame.MIN_TRIALS_PER_CONDITION * FlockGame.BAG_SIZE)
        assertNotNull(game.congruencyEffectMillis())
    }

    @Test
    fun `the ui state carries the verdict until the next trial`() {
        val game = FlockGame(Random(13L))
        game.nextRound()
        assertEquals(AnswerFeedbackState.NORMAL, game.toUiState().feedback)
        game.answer(wrongDirectionFor(game.trial))
        assertEquals(AnswerFeedbackState.WRONG, game.toUiState().feedback)
        game.nextRound()
        assertEquals(AnswerFeedbackState.NORMAL, game.toUiState().feedback)
    }

    @Test
    fun `the list does not ramp, so there is no difficulty to resume`() {
        assertFalse(FlockGame().adaptiveDifficulty)
    }
}
