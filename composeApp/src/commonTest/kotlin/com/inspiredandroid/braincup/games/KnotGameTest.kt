package com.inspiredandroid.braincup.games

import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KnotGameTest {

    @Test
    fun adaptiveDifficultyIsDisabled() {
        assertFalse(KnotGame().adaptiveDifficulty)
    }

    @Test
    fun levelIsCoercedToAtLeastOne() {
        assertEquals(1, KnotGame(level = 0).level)
        assertEquals(1, KnotGame(level = -5).level)
        assertEquals(7, KnotGame(level = 7).level)
    }

    @Test
    fun boardGrowsWithLevel() {
        fun build(level: Int) = KnotGame(level = level, random = Random(1L)).apply { nextRound() }
        // Size ramps slowly: three levels on each size before it grows.
        assertEquals(5, build(1).rows)
        assertEquals(5, build(3).rows)
        assertEquals(6, build(4).rows)
        assertEquals(7, build(7).rows)
        assertEquals(8, build(10).rows)
        assertEquals(9, build(13).rows)
        assertEquals(9, build(20).rows) // caps at 9x9
        // Pair density grows within each size, and the top tier is dense.
        assertEquals(3, build(1).endpoints.size)
        assertEquals(5, build(3).endpoints.size)
        assertEquals(8, build(12).endpoints.size)
        assertEquals(9, build(16).endpoints.size)
    }

    @Test
    fun generatedSolutionCoversGridWithValidDisjointPaths() {
        for (seed in 0L until 15L) {
            for (level in listOf(1, 3, 7, 12, 16)) {
                val game = KnotGame(level = level, random = Random(seed)).apply { nextRound() }
                val total = game.rows * game.cols
                val covered = BooleanArray(total)
                for (segment in game.generatedSolution) {
                    assertTrue(segment.size >= 3, "segment too short seed=$seed level=$level")
                    val seen = HashSet<Int>()
                    for (i in segment.indices) {
                        val cell = segment[i]
                        assertTrue(cell in 0 until total, "cell out of range seed=$seed level=$level")
                        assertTrue(seen.add(cell), "segment revisits a cell seed=$seed level=$level")
                        assertFalse(covered[cell], "paths overlap seed=$seed level=$level")
                        covered[cell] = true
                        if (i > 0) {
                            assertTrue(adjacent(segment[i - 1], cell, game.cols), "non-contiguous seed=$seed level=$level")
                        }
                    }
                }
                assertTrue(covered.all { it }, "grid not fully covered seed=$seed level=$level")
            }
        }
    }

    @Test
    fun endpointsMatchSolutionSegmentEnds() {
        for (seed in 0L until 20L) {
            val game = KnotGame(level = 4, random = Random(seed)).apply { nextRound() }
            assertEquals(game.generatedSolution.size, game.endpoints.size, "endpoint count seed=$seed")
            game.generatedSolution.forEachIndexed { color, segment ->
                val (a, b) = game.endpoints.getValue(color)
                assertEquals(setOf(segment.first(), segment.last()), setOf(a, b), "endpoint mismatch seed=$seed")
            }
        }
    }

    @Test
    fun drawingTheGeneratedSolutionSolvesTheBoard() {
        for (seed in 0L until 15L) {
            for (level in listOf(1, 3, 7, 12, 16)) {
                val game = KnotGame(level = level, random = Random(seed)).apply { nextRound() }
                game.generatedSolution.forEachIndexed { color, segment ->
                    game.setPath(color, segment)
                }
                assertTrue(game.isCorrect(""), "generated solution rejected seed=$seed level=$level")
            }
        }
    }

    @Test
    fun setPathStoresAndClearPathRemoves() {
        val game = KnotGame(level = 1, random = Random(0L)).apply { nextRound() }
        val segment = game.generatedSolution.first()
        assertFalse(game.setPath(0, segment)) // stored, board not yet solved
        assertEquals(segment, game.paths[0])
        game.clearPath(0)
        assertFalse(0 in game.paths)
    }

    @Test
    fun incompleteBoardIsNotSolved() {
        val game = KnotGame(level = 4, random = Random(7L)).apply { nextRound() }
        // Draw every path, then clear one: the board is no longer fully connected/covered.
        game.generatedSolution.forEachIndexed { color, segment -> game.setPath(color, segment) }
        game.clearPath(0)
        assertFalse(game.isCorrect(""))
    }

    @Test
    fun partialPathIsNotSolved() {
        val game = KnotGame(level = 4, random = Random(9L)).apply { nextRound() }
        game.generatedSolution.forEachIndexed { color, segment ->
            // Drop the last cell of the final path so it never reaches its endpoint.
            val cells = if (color == game.generatedSolution.lastIndex) segment.dropLast(1) else segment
            game.setPath(color, cells)
        }
        assertFalse(game.isCorrect(""))
    }

    @Test
    fun sanitizeRejectsAPathNotStartingAtAnEndpoint() {
        val game = KnotGame(level = 1, random = Random(2L)).apply { nextRound() }
        val segment = game.generatedSolution.first()
        // Starting from the middle of the segment is not a valid stroke; nothing is stored.
        game.setPath(0, segment.subList(1, segment.size))
        assertFalse(0 in game.paths)
    }

    @Test
    fun sameSeedProducesIdenticalBoard() {
        val a = KnotGame(level = 6, random = Random(99L)).apply { nextRound() }
        val b = KnotGame(level = 6, random = Random(99L)).apply { nextRound() }
        assertEquals(a.rows, b.rows)
        assertEquals(a.endpoints, b.endpoints)
        assertEquals(a.generatedSolution, b.generatedSolution)
    }

    private fun adjacent(a: Int, b: Int, cols: Int): Boolean {
        val ar = a / cols
        val ac = a % cols
        val br = b / cols
        val bc = b % cols
        return (ar == br && abs(ac - bc) == 1) || (ac == bc && abs(ar - br) == 1)
    }
}
