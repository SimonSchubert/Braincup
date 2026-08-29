package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.FeedbackMessage
import com.inspiredandroid.braincup.app.MentalFlexUiState
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MentalFlexGameTest {

    private fun playedGame(seed: Long, rounds: Int = 200): List<MentalFlexGame> {
        val game = MentalFlexGame(Random(seed))
        return List(rounds) {
            game.nextRound()
            game
        }
    }

    @Test
    fun `exactly one candidate matches the target on the active dimension`() {
        playedGame(seed = 1L).forEach { game ->
            val matches = game.candidates.count { candidate ->
                when (game.rule) {
                    MentalFlexGame.Rule.SHAPE -> candidate.shape == game.target.shape
                    MentalFlexGame.Rule.COLOR -> candidate.color == game.target.color
                }
            }
            assertEquals(1, matches, "round ${game.round} under ${game.rule} had $matches answers")
        }
    }

    @Test
    fun `correctIndex points at the candidate matching on the active dimension`() {
        playedGame(seed = 2L).forEach { game ->
            val correct = game.candidates[game.correctIndex]
            when (game.rule) {
                MentalFlexGame.Rule.SHAPE -> {
                    assertEquals(game.target.shape, correct.shape)
                    // Matching on both dimensions would make the round answerable without the cue.
                    assertFalse(correct.color == game.target.color)
                }
                MentalFlexGame.Rule.COLOR -> {
                    assertEquals(game.target.color, correct.color)
                    assertFalse(correct.shape == game.target.shape)
                }
            }
        }
    }

    @Test
    fun `every board carries a lure matching on the ignored dimension`() {
        // Without it a player could answer by "which tile shares anything with the target" and
        // never read the cue at all, which is the one thing this game is measuring.
        playedGame(seed = 3L).forEach { game ->
            val lures = game.candidates.count { candidate ->
                when (game.rule) {
                    MentalFlexGame.Rule.SHAPE -> candidate.color == game.target.color
                    MentalFlexGame.Rule.COLOR -> candidate.shape == game.target.shape
                }
            }
            assertEquals(1, lures, "round ${game.round} under ${game.rule} had $lures lures")
        }
    }

    @Test
    fun `distractors share neither dimension with the target`() {
        playedGame(seed = 4L).forEach { game ->
            val sharing = game.candidates.count {
                it.shape == game.target.shape || it.color == game.target.color
            }
            // Exactly the answer and the lure, never a third ambiguous tile.
            assertEquals(2, sharing, "round ${game.round} had $sharing tiles sharing with the target")
        }
    }

    @Test
    fun `the same board would have a different answer under the other rule`() {
        playedGame(seed = 5L).forEach { game ->
            val underOther = game.candidates.indexOfFirst { candidate ->
                when (game.rule) {
                    MentalFlexGame.Rule.SHAPE -> candidate.color == game.target.color
                    MentalFlexGame.Rule.COLOR -> candidate.shape == game.target.shape
                }
            }
            assertFalse(
                underOther == game.correctIndex,
                "round ${game.round}: both rules pointed at the same tile, so the switch costs nothing",
            )
        }
    }

    @Test
    fun `isCorrect accepts only the one-based position of the answer`() {
        val game = MentalFlexGame(Random(6L))
        game.nextRound()
        assertTrue(game.isCorrect("${game.correctIndex + 1}"))
        game.candidates.indices
            .filter { it != game.correctIndex }
            .forEach { assertFalse(game.isCorrect("${it + 1}")) }
        assertFalse(game.isCorrect(""))
        assertFalse(game.isCorrect("not a number"))
        assertFalse(game.isCorrect("0"))
    }

    @Test
    fun `the rule switches over a run without alternating every round`() {
        val game = MentalFlexGame(Random(7L))
        var switches = 0
        var previous: MentalFlexGame.Rule? = null
        repeat(200) {
            game.nextRound()
            if (previous != null && game.rule != previous) switches++
            previous = game.rule
        }
        assertTrue(switches > 20, "the rule barely ever switched ($switches in 200 rounds)")
        assertTrue(switches < 180, "the rule alternated almost every round ($switches in 200)")
    }

    @Test
    fun `isSwitch is false on the first round and tracks rule changes after it`() {
        val game = MentalFlexGame(Random(8L))
        game.nextRound()
        assertFalse(game.isSwitch, "the first round has no previous rule to switch from")

        var previous = game.rule
        repeat(50) {
            game.nextRound()
            assertEquals(game.rule != previous, game.isSwitch)
            previous = game.rule
        }
    }

    @Test
    fun `board size is derived from the round so a resumed run stays hard`() {
        fun candidatesAtStartRound(startRound: Int): Int = MentalFlexGame(Random(9L))
            .apply {
                round = startRound
                nextRound()
            }.candidates.size

        assertEquals(4, candidatesAtStartRound(0))
        assertEquals(4, candidatesAtStartRound(2))
        assertEquals(6, candidatesAtStartRound(3))
        // Six is the cap, so a long run stays a six-tile board rather than growing into a search.
        assertEquals(6, candidatesAtStartRound(40))
    }

    @Test
    fun `the board always fills whole rows of the reported column count`() {
        playedGame(seed = 12L).forEach { game ->
            val ui = game.toUiState()
            assertEquals(game.candidates.size, ui.rows.sumOf { it.size })
            assertEquals(game.candidates, ui.rows.flatMap { row -> row.map { it.figure } })
            // withFeedbackStates maps a tap through y * columnsPerRow + x, so a row wider than
            // columnsPerRow would colour the wrong tile on a wrong answer.
            assertTrue(ui.rows.all { it.size <= ui.columnsPerRow })
        }
    }

    @Test
    fun `the cue exemplar holds the active trait constant and the other one varying`() {
        // This pair IS the rule statement. If it ever held both traits constant, or neither, it
        // would say nothing and the round would be unanswerable.
        playedGame(seed = 15L).forEach { game ->
            val (a, b) = game.cueExemplar
            when (game.rule) {
                MentalFlexGame.Rule.SHAPE -> {
                    assertEquals(a.shape, b.shape)
                    assertFalse(a.color == b.color)
                }
                MentalFlexGame.Rule.COLOR -> {
                    assertEquals(a.color, b.color)
                    assertFalse(a.shape == b.shape)
                }
            }
        }
    }

    @Test
    fun `the cue exemplar never reuses the target's own shape or color`() {
        // Otherwise the cue reads as a pointer at one particular tile rather than as a rule.
        playedGame(seed = 16L).forEach { game ->
            game.cueExemplar.forEach { figure ->
                assertFalse(figure.shape == game.target.shape, "round ${game.round}: cue echoed the target shape")
                assertFalse(figure.color == game.target.color, "round ${game.round}: cue echoed the target color")
            }
        }
    }

    @Test
    fun `the give-up description picks out exactly one tile`() {
        // solutionMessage names the answer by color and shape displayName. Shape.ABSTRACT_TRIANGLE
        // shares the name "triangle" with Shape.TRIANGLE, so a pool holding both could describe two
        // different tiles identically and send a player who gave up to the wrong one.
        playedGame(seed = 13L).forEach { game ->
            val named = game.candidates.map { "${it.color.displayName} ${it.shape.displayName}" }
            assertEquals(named.size, named.toSet().size, "round ${game.round}: two tiles share a description")
            assertEquals(named[game.correctIndex], game.solution())
        }
    }

    @Test
    fun `every tile on a board is a distinct figure`() {
        // Two identical tiles would make a round ambiguous to look at even when only one is the
        // scored answer.
        playedGame(seed = 14L).forEach { game ->
            assertEquals(game.candidates.size, game.candidates.toSet().size)
        }
    }

    @Test
    fun `the ui state exposes the candidates as one feedback-ready row`() {
        val game = MentalFlexGame(Random(10L))
        game.nextRound()
        val ui = game.toUiState() as MentalFlexUiState

        assertEquals(game.rule, ui.rule)
        assertEquals(game.cueExemplar, ui.cueExemplar)
        assertEquals(game.target, ui.target)
        assertEquals(game.candidates, ui.rows.flatMap { row -> row.map { it.figure } })
    }

    @Test
    fun `giving up describes the figure that should have been tapped`() {
        val game = MentalFlexGame(Random(11L))
        game.nextRound()
        val correct = game.candidates[game.correctIndex]

        val message = game.solutionMessage()
        assertTrue(message is FeedbackMessage.FigureDescription)
        assertEquals(correct.color, message.color)
        assertEquals(correct.shape, message.shape)
        // Nothing in this game is rotated, so there is no direction to report.
        assertEquals(null, message.directionDegrees)
    }
}
