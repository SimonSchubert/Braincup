package com.inspiredandroid.braincup.games

import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CatQueensGameTest {

    @Test
    fun adaptiveDifficultyIsDisabled() {
        assertFalse(CatQueensGame().adaptiveDifficulty)
    }

    @Test
    fun levelIsCoercedToAtLeastOne() {
        assertEquals(1, CatQueensGame(level = 0).level)
        assertEquals(1, CatQueensGame(level = -5).level)
        assertEquals(7, CatQueensGame(level = 7).level)
    }

    @Test
    fun boardGrowsWithLevel() {
        fun sizeOf(level: Int): Int = CatQueensGame(level = level).apply { nextRound() }.size
        assertEquals(4, sizeOf(1))
        assertEquals(5, sizeOf(3))
        assertEquals(6, sizeOf(5))
        assertEquals(7, sizeOf(8))
        assertEquals(7, sizeOf(12))
    }

    @Test
    fun generatedBoardsHaveUniqueSolution() {
        for (seed in 0L until 40L) {
            for (level in listOf(1, 4, 7, 10)) {
                val game = CatQueensGame(level = level, random = Random(seed)).apply { nextRound() }
                assertEquals(
                    1,
                    game.countSolutions(game.regions, game.size, limit = 2),
                    "non-unique board seed=$seed level=$level",
                )
            }
        }
    }

    @Test
    fun regionsAreConnectedAndComplete() {
        for (seed in 0L until 40L) {
            for (level in listOf(1, 4, 7, 10)) {
                val game = CatQueensGame(level = level, random = Random(seed)).apply { nextRound() }
                val n = game.size

                // Exactly n regions, every cell assigned to one of them.
                assertTrue(game.regions.all { it in 0 until n }, "bad region id seed=$seed level=$level")
                assertEquals(n, game.regions.toSet().size, "wrong region count seed=$seed level=$level")

                // Each region is a single connected component.
                for (region in 0 until n) {
                    val cells = game.regions.indices.filter { game.regions[it] == region }
                    val seen = mutableSetOf(cells.first())
                    val stack = ArrayDeque(seen)
                    while (stack.isNotEmpty()) {
                        val cell = stack.removeLast()
                        val r = cell / n
                        val c = cell % n
                        listOfNotNull(
                            if (r > 0) cell - n else null,
                            if (r < n - 1) cell + n else null,
                            if (c > 0) cell - 1 else null,
                            if (c < n - 1) cell + 1 else null,
                        ).forEach { nb ->
                            if (game.regions[nb] == region && seen.add(nb)) stack.addLast(nb)
                        }
                    }
                    assertEquals(cells.size, seen.size, "region $region disconnected seed=$seed level=$level")
                }
            }
        }
    }

    @Test
    fun generatedSolutionIsValidAndSolvesTheBoard() {
        for (seed in 0L until 40L) {
            val game = CatQueensGame(level = 4, random = Random(seed)).apply { nextRound() }
            val n = game.size
            val cells = game.solutionCats.toList()
            assertEquals(n, cells.size, "wrong cat count seed=$seed")

            // One per row, column and region, with no two cats touching.
            assertEquals(n, cells.map { it / n }.toSet().size, "row clash seed=$seed")
            assertEquals(n, cells.map { it % n }.toSet().size, "column clash seed=$seed")
            assertEquals(n, cells.map { game.regions[it] }.toSet().size, "region clash seed=$seed")
            for (i in cells.indices) {
                for (j in i + 1 until cells.size) {
                    val touching = abs(cells[i] / n - cells[j] / n) <= 1 && abs(cells[i] % n - cells[j] % n) <= 1
                    assertFalse(touching, "cats touch seed=$seed")
                }
            }

            // Placing exactly the solution cats solves the board.
            game.solutionCats.forEach { game.toggle(it) }
            assertTrue(game.isCorrect(""), "generated solution rejected seed=$seed")
        }
    }

    @Test
    fun togglePlacesAndRemovesCats() {
        val game = CatQueensGame(level = 1, random = Random(0L)).apply { nextRound() }
        val cell = game.solutionCats.first()
        assertFalse(game.toggle(cell)) // placed, board not yet solved
        assertTrue(cell in game.cats)
        game.toggle(cell) // remove
        assertFalse(cell in game.cats)
    }

    @Test
    fun partialPlacementIsNotSolved() {
        val game = CatQueensGame(level = 4, random = Random(7L)).apply { nextRound() }
        game.solutionCats.toList().dropLast(1).forEach { game.toggle(it) }
        assertFalse(game.isCorrect(""))
    }

    @Test
    fun conflictingPlacementIsNotSolved() {
        val game = CatQueensGame(level = 1, random = Random(3L)).apply { nextRound() }
        val n = game.size
        // Fill the first row completely: n cats placed, but they clash on row/column/adjacency.
        for (c in 0 until n) game.toggle(c)
        assertFalse(game.isCorrect(""))
    }

    @Test
    fun sameSeedProducesIdenticalBoard() {
        val a = CatQueensGame(level = 6, random = Random(99L)).apply { nextRound() }
        val b = CatQueensGame(level = 6, random = Random(99L)).apply { nextRound() }
        assertEquals(a.size, b.size)
        assertTrue(a.regions.contentEquals(b.regions))
        assertEquals(a.solutionCats, b.solutionCats)
    }
}
