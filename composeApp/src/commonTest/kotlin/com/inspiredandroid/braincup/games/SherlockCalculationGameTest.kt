package com.inspiredandroid.braincup.games

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The tile pool is what the player has to search, so it is the difficulty knob. It used to grow by
 * one every round with nothing to stop it, and every give-up stepped the ramp, so a run of skips
 * banked rounds the player had never solved and came back to a board of thirty tiles.
 */
class SherlockCalculationGameTest {

    private fun poolSizesAt(startRound: Int, draws: Int = 300): List<Int> = List(draws) {
        SherlockCalculationGame().apply {
            round = startRound
            nextRound()
        }.numbers.size
    }

    /** [SherlockCalculationGame.MAX_NUMBERS_NEEDED] is exclusive, and the goal-collision top-up can
     *  append a couple of small numbers on top of the pool the ramp asked for. */
    private val cap = SherlockCalculationGame.MAX_NUMBERS_NEEDED - 1
    private val topUpSlack = 4

    @Test
    fun firstRoundIsTheSmallestBoard() {
        val sizes = poolSizesAt(0)
        assertEquals(2, sizes.min())
        assertTrue(sizes.max() <= 2 + topUpSlack, "first round dealt ${sizes.max()} numbers")
    }

    @Test
    fun poolStopsGrowingAtTheCap() {
        listOf(6, 10, 30, 200).forEach { startRound ->
            val sizes = poolSizesAt(startRound)
            assertEquals(cap, sizes.min(), "round $startRound")
            assertTrue(sizes.max() <= cap + topUpSlack, "round $startRound dealt ${sizes.max()}")
        }
    }

    /**
     * A resumed run used to deal its first round at the starting difficulty and its second at the
     * stored one, which is the jump from `12=9+3` to a wall of tiles the bug report showed.
     */
    @Test
    fun resumedRunOpensAtTheDifficultyItStoppedAt() {
        assertEquals(cap, poolSizesAt(12).min())
    }

    @Test
    fun givingUpDealsAnotherRoundAtTheSameDifficulty() {
        val game = SherlockCalculationGame()
        repeat(3) { game.nextRound() }
        val round = game.round
        repeat(20) { game.repeatRound() }
        assertEquals(round, game.round)
        assertTrue(game.numbers.size <= 4 + topUpSlack, "skipping grew the board")
    }

    @Test
    fun generatedRoundIsSolvableFromItsOwnNumbers() {
        repeat(300) {
            val game = SherlockCalculationGame().apply {
                round = (0..20).random()
                nextRound()
            }
            assertTrue(
                game.isCorrect(game.solution()),
                "unsolvable: ${game.solution()} != ${game.result} from ${game.numbers}",
            )
            assertTrue(game.result !in game.numbers, "goal ${game.result} sits on a tile")
        }
    }
}
