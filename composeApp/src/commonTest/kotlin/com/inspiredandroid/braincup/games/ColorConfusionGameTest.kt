package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.AnswerFeedbackState
import com.inspiredandroid.braincup.games.tools.GameColor
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ColorConfusionGameTest {

    private val colors = ColorConfusionGame.RESPONSE_COLORS

    /** The 1-based swatch that answers [trial] correctly, derived without asking the game. */
    private fun slotFor(trial: ColorConfusionGame.Trial): Int = colors.indexOf(trial.ink) + 1

    private fun wrongSlotFor(trial: ColorConfusionGame.Trial): Int = colors.indices.first { colors[it] != trial.ink } + 1

    /** Deal [count] trials, answering each one correctly, and hand back what was dealt. */
    private fun dealCorrectly(game: ColorConfusionGame, count: Int): List<ColorConfusionGame.Trial> = buildList {
        repeat(count) {
            game.nextRound()
            add(game.trial)
            game.answer(slotFor(game.trial))
        }
    }

    @Test
    fun `the word and the ink are always answerable from the response row`() {
        val game = ColorConfusionGame(Random(1L))
        dealCorrectly(game, 200).forEach { trial ->
            assertTrue(trial.ink in colors, "ink ${trial.ink} is not on the response row")
            assertTrue(trial.word in colors, "word ${trial.word} is not on the response row")
        }
    }

    @Test
    fun `congruent trials agree and incongruent trials disagree`() {
        val game = ColorConfusionGame(Random(2L))
        dealCorrectly(game, 200).forEach { trial ->
            assertEquals(trial.word == trial.ink, trial.isCongruent)
        }
    }

    @Test
    fun `the congruent share holds at the scheduled rate`() {
        val game = ColorConfusionGame(Random(3L))
        val trials = dealCorrectly(game, 1000)
        val congruent = trials.count { it.isCongruent }
        val expected = trials.size * ColorConfusionGame.CONGRUENT_PER_BAG / ColorConfusionGame.BAG_SIZE

        // Dealt from a shuffled bag, so this is not an average that settles down over a long run:
        // it is exact but for the part-dealt bag at the end.
        assertTrue(
            abs(congruent - expected) <= ColorConfusionGame.BAG_SIZE,
            "expected about $expected congruent trials in ${trials.size}, got $congruent",
        )
    }

    @Test
    fun `every bag holds its share, so neither condition comes out in a streak`() {
        val game = ColorConfusionGame(Random(4L))
        dealCorrectly(game, 500)
            .chunked(ColorConfusionGame.BAG_SIZE)
            .filter { it.size == ColorConfusionGame.BAG_SIZE }
            .forEachIndexed { index, bag ->
                assertEquals(
                    ColorConfusionGame.CONGRUENT_PER_BAG,
                    bag.count { it.isCongruent },
                    "bag $index is off its share",
                )
            }
    }

    @Test
    fun `the ink never repeats, so one swatch cannot be farmed`() {
        val game = ColorConfusionGame(Random(5L))
        dealCorrectly(game, 300).zipWithNext { previous, next ->
            assertNotEquals(previous.ink, next.ink, "the ink repeated on consecutive trials")
        }
    }

    @Test
    fun `only the ink swatch is correct`() {
        val game = ColorConfusionGame(Random(6L))
        repeat(100) {
            game.nextRound()
            val trial = game.trial
            colors.indices.forEach { index ->
                val expected = colors[index] == trial.ink
                assertEquals(expected, game.isCorrect("${index + 1}"), "slot ${index + 1} on $trial")
            }
            assertEquals(slotFor(trial).toString(), game.solution())
            game.answer(slotFor(trial))
        }
    }

    @Test
    fun `a second tap on the same trial is ignored`() {
        val game = ColorConfusionGame(Random(7L))
        game.nextRound()
        assertEquals(true, game.answer(slotFor(game.trial)))
        assertNull(game.answer(slotFor(game.trial)))
    }

    @Test
    fun `a tap outside the response row is ignored`() {
        val game = ColorConfusionGame(Random(8L))
        game.nextRound()
        assertNull(game.answer(0))
        assertNull(game.answer(colors.size + 1))
    }

    @Test
    fun `a wrong answer costs the flawless run`() {
        val game = ColorConfusionGame(Random(9L))
        game.nextRound()
        assertTrue(game.answeredAllCorrect)
        assertEquals(false, game.answer(wrongSlotFor(game.trial)))
        assertFalse(game.answeredAllCorrect)
    }

    @Test
    fun `the congruency effect needs enough correct trials of both kinds`() {
        val game = ColorConfusionGame(Random(10L))
        assertNull(game.congruencyEffectMillis())

        // A bag holds two congruent trials, so a run this short cannot reach the minimum on the
        // congruent side however it is answered.
        dealCorrectly(game, ColorConfusionGame.BAG_SIZE)
        assertNull(game.congruencyEffectMillis())
    }

    @Test
    fun `errors are left out of the congruency effect`() {
        val game = ColorConfusionGame(Random(11L))
        val needed = ColorConfusionGame.MIN_TRIALS_PER_CONDITION

        // Answer every trial wrongly, well past the point where a correct run would report.
        repeat(needed * ColorConfusionGame.BAG_SIZE * 4) {
            game.nextRound()
            game.answer(wrongSlotFor(game.trial))
        }
        assertNull(game.congruencyEffectMillis())
    }

    @Test
    fun `a long clean run reports an effect`() {
        val game = ColorConfusionGame(Random(12L))
        dealCorrectly(game, ColorConfusionGame.MIN_TRIALS_PER_CONDITION * ColorConfusionGame.BAG_SIZE)
        // Machine-fast answers make the medians themselves near zero, so the value is not worth
        // asserting; that it is reported at all is.
        assertNotNull(game.congruencyEffectMillis())
    }

    @Test
    fun `the ui state marks only the tapped swatch`() {
        val game = ColorConfusionGame(Random(13L))
        game.nextRound()
        val correctSlot = slotFor(game.trial)
        game.answer(correctSlot)

        val state = game.toUiState()
        assertTrue(state.isAwaitingNextTrial)
        assertEquals(colors, state.swatches.map { it.color })
        state.swatches.forEachIndexed { index, swatch ->
            val marked = index + 1 == correctSlot
            assertEquals(marked, swatch.state != AnswerFeedbackState.NORMAL)
        }
    }

    @Test
    fun `the response row is the classic four colours`() {
        assertEquals(
            listOf(GameColor.RED, GameColor.GREEN, GameColor.BLUE, GameColor.YELLOW),
            colors,
        )
    }

    @Test
    fun `the list does not ramp, so there is no difficulty to resume`() {
        assertFalse(ColorConfusionGame().adaptiveDifficulty)
    }
}
