package com.inspiredandroid.braincup.games

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ShikakuGameTest {

    @Test
    fun adaptiveDifficultyIsDisabled() {
        assertFalse(ShikakuGame().adaptiveDifficulty)
    }

    @Test
    fun levelIsCoercedToAtLeastOne() {
        assertEquals(1, ShikakuGame(level = 0).level)
        assertEquals(1, ShikakuGame(level = -5).level)
        assertEquals(7, ShikakuGame(level = 7).level)
    }

    @Test
    fun gridGrowsWithLevel() {
        fun gridOf(level: Int): Pair<Int, Int> =
            ShikakuGame(level = level).apply { nextRound() }.let { it.rows to it.cols }

        assertEquals(4 to 4, gridOf(1))
        assertEquals(5 to 5, gridOf(5))
        assertEquals(6 to 6, gridOf(8))
        assertEquals(7 to 7, gridOf(12))
    }

    @Test
    fun generatedBoardsHaveUniqueSolution() {
        for (seed in 0L until 60L) {
            for (level in listOf(1, 4, 7, 10)) {
                val game = ShikakuGame(level = level, random = Random(seed)).apply { nextRound() }
                assertEquals(1, game.solutionCount(limit = 2), "non-unique board seed=$seed level=$level")
            }
        }
    }

    @Test
    fun generationProducesValidPartitionAcrossSeeds() {
        for (seed in 0L until 60L) {
            for (level in listOf(1, 4, 7, 10)) {
                val game = ShikakuGame(level = level, random = Random(seed)).apply { nextRound() }
                val partition = game.generatedSolution

                // Every clue belongs to exactly one leaf rectangle, so counts match.
                assertEquals(partition.size, game.clues.size, "clue/leaf mismatch seed=$seed level=$level")

                // The partition covers the whole grid exactly once (no gaps, no overlaps).
                val covered = BooleanArray(game.rows * game.cols)
                for (rect in partition) {
                    for (r in rect.top..rect.bottom) {
                        for (c in rect.left..rect.right) {
                            val index = r * game.cols + c
                            assertFalse(covered[index], "overlap at $index seed=$seed level=$level")
                            covered[index] = true
                        }
                    }
                }
                assertTrue(covered.all { it }, "gap in coverage seed=$seed level=$level")

                // No trivial single-cell clues; level 10+ uses a larger minimum.
                val minClue = game.clues.values.min()
                val expectedMin = if (level >= 10) 3 else 2
                assertTrue(minClue >= expectedMin, "clue $minClue < $expectedMin seed=$seed level=$level")
            }
        }
    }

    @Test
    fun sameSeedProducesIdenticalBoard() {
        val a = ShikakuGame(level = 6, random = Random(99L)).apply { nextRound() }
        val b = ShikakuGame(level = 6, random = Random(99L)).apply { nextRound() }
        assertEquals(a.rows, b.rows)
        assertEquals(a.cols, b.cols)
        assertEquals(a.clues, b.clues)
    }

    @Test
    fun commitStoresAndReplacesOverlappingRectangles() {
        val game = ShikakuGame(level = 1).apply { nextRound() } // 4x4

        game.commitRectangle(0, 0, 1, 1)
        assertEquals(1, game.rectangles.size)

        // Overlaps the first rectangle: it should be replaced, not added alongside.
        game.commitRectangle(1, 1, 2, 2)
        assertEquals(1, game.rectangles.size)
        assertEquals(ShikakuGame.Rect(top = 1, left = 1, bottom = 2, right = 2), game.rectangles.single())

        // Disjoint from the existing rectangle: now there are two.
        game.commitRectangle(0, 3, 0, 3)
        assertEquals(2, game.rectangles.size)
    }

    @Test
    fun commitNormalizesAndClampsCorners() {
        val game = ShikakuGame(level = 1).apply { nextRound() } // 4x4

        // Reversed and out-of-range corners normalize/clamp to the same in-bounds rectangle.
        game.commitRectangle(3, 2, 1, 0)
        assertEquals(ShikakuGame.Rect(top = 1, left = 0, bottom = 3, right = 2), game.rectangles.single())

        game.commitRectangle(-2, -2, 99, 99)
        assertEquals(ShikakuGame.Rect(top = 0, left = 0, bottom = 3, right = 3), game.rectangles.single())
    }

    @Test
    fun deleteRemovesCoveringRectangle() {
        val game = ShikakuGame(level = 1).apply { nextRound() } // 4x4
        game.commitRectangle(0, 0, 1, 1)

        game.deleteRectangleAt(3, 3) // empty cell: no-op
        assertEquals(1, game.rectangles.size)

        game.deleteRectangleAt(1, 0) // inside the rectangle
        assertTrue(game.rectangles.isEmpty())
    }

    @Test
    fun rebuildingGeneratedSolutionSolvesTheBoard() {
        for (seed in 0L until 100L) {
            val game = ShikakuGame(level = 4, random = Random(seed)).apply { nextRound() }
            for (rect in game.generatedSolution) {
                game.commitRectangle(rect.top, rect.left, rect.bottom, rect.right)
            }
            assertTrue(game.isCorrect(""), "generated solution not accepted, seed=$seed")
        }
    }

    @Test
    fun acceptsAnyValidPartitionNotJustTheGenerators() {
        // Four 2x2 quadrants, each with a clue of 2 at its top-left and bottom-right corner.
        // Every quadrant can be split into two 1x2 (horizontal) OR two 2x1 (vertical) rectangles,
        // so the same board has many valid partitions.
        fun fixture(): ShikakuGame = ShikakuGame(level = 1).apply {
            nextRound() // 4x4
            clues.clear()
            clues.putAll(
                mapOf(
                    0 * 4 + 0 to 2, 1 * 4 + 1 to 2, // top-left quadrant
                    0 * 4 + 2 to 2, 1 * 4 + 3 to 2, // top-right quadrant
                    2 * 4 + 0 to 2, 3 * 4 + 1 to 2, // bottom-left quadrant
                    2 * 4 + 2 to 2, 3 * 4 + 3 to 2, // bottom-right quadrant
                ),
            )
        }

        val horizontal = fixture()
        listOf(
            intArrayOf(0, 0, 0, 1), intArrayOf(1, 0, 1, 1),
            intArrayOf(0, 2, 0, 3), intArrayOf(1, 2, 1, 3),
            intArrayOf(2, 0, 2, 1), intArrayOf(3, 0, 3, 1),
            intArrayOf(2, 2, 2, 3), intArrayOf(3, 2, 3, 3),
        ).forEach { horizontal.commitRectangle(it[0], it[1], it[2], it[3]) }
        assertTrue(horizontal.isCorrect(""), "horizontal partition rejected")

        val vertical = fixture()
        listOf(
            intArrayOf(0, 0, 1, 0), intArrayOf(0, 1, 1, 1),
            intArrayOf(0, 2, 1, 2), intArrayOf(0, 3, 1, 3),
            intArrayOf(2, 0, 3, 0), intArrayOf(2, 1, 3, 1),
            intArrayOf(2, 2, 3, 2), intArrayOf(2, 3, 3, 3),
        ).forEach { vertical.commitRectangle(it[0], it[1], it[2], it[3]) }
        assertTrue(vertical.isCorrect(""), "vertical partition rejected")
    }

    @Test
    fun partialCoverageIsNotSolved() {
        val game = ShikakuGame(level = 4, random = Random(1L)).apply { nextRound() }
        val solution = game.generatedSolution
        // Commit all but the last rectangle: full clue match but a gap remains.
        solution.dropLast(1).forEach { game.commitRectangle(it.top, it.left, it.bottom, it.right) }
        assertFalse(game.isCorrect(""))
    }

    @Test
    fun fullCoverageWithRectangleSpanningMultipleCluesIsNotSolved() {
        val game = ShikakuGame(level = 1).apply { nextRound() } // 4x4, multiple clues
        // One rectangle covering the whole grid: area matches the grid but it holds every clue.
        game.commitRectangle(0, 0, 3, 3)
        assertFalse(game.isCorrect(""))
    }

    @Test
    fun fullCoverageWithWrongAreaIsNotSolved() {
        val game = ShikakuGame(level = 1).apply {
            nextRound() // 4x4
            clues.clear()
            clues.putAll(mapOf(0 * 4 + 0 to 8, 2 * 4 + 0 to 8)) // two horizontal halves
        }
        // Covers the whole grid with the correct total area, but the split is wrong:
        // a 12-cell rectangle holds the clue 8, and the 4-cell rectangle holds no clue.
        game.commitRectangle(0, 0, 2, 3)
        game.commitRectangle(3, 0, 3, 3)
        assertFalse(game.isCorrect(""))
    }

    @Test
    fun overlappingRectanglesAreNotSolved() {
        val game = ShikakuGame(level = 1).apply { nextRound() } // 4x4
        // Inject overlapping rectangles directly to exercise the double-cover guard:
        // total area is 16 but row 2 cols 0-1 are covered twice and row 3 cols 2-3 are uncovered.
        game.rectangles.clear()
        game.rectangles.add(ShikakuGame.Rect(top = 0, left = 0, bottom = 2, right = 3)) // area 12
        game.rectangles.add(ShikakuGame.Rect(top = 2, left = 0, bottom = 3, right = 1)) // area 4
        assertFalse(game.isCorrect(""))
    }
}
