package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.NurikabeUiState
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NurikabeGameTest {

    @Test
    fun adaptiveDifficultyIsDisabled() {
        assertFalse(NurikabeGame().adaptiveDifficulty)
    }

    @Test
    fun levelIsCoercedToAtLeastOne() {
        assertEquals(1, NurikabeGame(level = 0).level)
        assertEquals(1, NurikabeGame(level = -5).level)
        assertEquals(7, NurikabeGame(level = 7).level)
    }

    @Test
    fun gridGrowsWithLevel() {
        fun gridOf(level: Int): Pair<Int, Int> =
            NurikabeGame(level = level).apply { nextRound() }.let { it.rows to it.cols }

        assertEquals(5 to 5, gridOf(1))
        assertEquals(6 to 6, gridOf(4))
        assertEquals(7 to 7, gridOf(7))
        assertEquals(7 to 7, gridOf(12))
    }

    @Test
    fun generatedBoardsAreStructurallyValid() {
        for (seed in 0L until 25L) {
            for (level in listOf(1, 4, 7, 10)) {
                val game = NurikabeGame(level = level, random = Random(seed)).apply { nextRound() }
                val cols = game.cols
                val islands = game.generatedIslands
                val sea = game.generatedSea

                // Islands and sea partition the whole grid exactly once.
                val covered = BooleanArray(game.rows * cols)
                for (island in islands) {
                    for (cell in island) {
                        assertFalse(covered[cell], "overlap at $cell seed=$seed level=$level")
                        covered[cell] = true
                    }
                }
                for (cell in sea) {
                    assertFalse(covered[cell], "sea overlaps island at $cell seed=$seed level=$level")
                    covered[cell] = true
                }
                assertTrue(covered.all { it }, "gap in coverage seed=$seed level=$level")

                // The sea is real, and every island holds exactly one clue equal to its size.
                assertTrue(sea.isNotEmpty(), "empty sea seed=$seed level=$level")
                assertEquals(islands.size, game.clues.size, "clue/island mismatch seed=$seed level=$level")
                for (island in islands) {
                    assertTrue(island.isNotEmpty(), "empty island seed=$seed level=$level")
                    val clueCells = island.filter { it in game.clues }
                    assertEquals(1, clueCells.size, "island clue count seed=$seed level=$level")
                    assertEquals(island.size, game.clues[clueCells.single()], "clue value seed=$seed level=$level")
                }

                // Islands never touch each other orthogonally.
                for (island in islands) {
                    for (cell in island) {
                        val r = cell / cols
                        val c = cell % cols
                        val neighbours = buildList {
                            if (r > 0) add(cell - cols)
                            if (r < game.rows - 1) add(cell + cols)
                            if (c > 0) add(cell - 1)
                            if (c < cols - 1) add(cell + 1)
                        }
                        for (n in neighbours) {
                            if (n !in island) {
                                assertFalse(
                                    islands.any { it !== island && n in it },
                                    "islands touch near $cell seed=$seed level=$level",
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun rebuildingGeneratedSolutionSolvesTheBoard() {
        for (seed in 0L until 50L) {
            for (level in listOf(1, 4, 7, 10)) {
                val game = NurikabeGame(level = level, random = Random(seed)).apply { nextRound() }
                game.setWalls(game.generatedSea.toList(), true)
                assertTrue(game.isCorrect(""), "generated solution not accepted seed=$seed level=$level")
            }
        }
    }

    @Test
    fun generatedBoardsAreSolvable() {
        // Generation does not guarantee a unique solution (see NurikabeGame KDoc), but every board
        // must have at least one, which the generator's own solution provides.
        for (seed in 0L until 20L) {
            for (level in listOf(1, 4, 7, 10)) {
                val game = NurikabeGame(level = level, random = Random(seed)).apply { nextRound() }
                assertTrue(game.solutionCount(limit = 1) >= 1, "unsolvable board seed=$seed level=$level")
            }
        }
    }

    @Test
    fun sameSeedProducesIdenticalBoard() {
        val a = NurikabeGame(level = 6, random = Random(99L)).apply { nextRound() }
        val b = NurikabeGame(level = 6, random = Random(99L)).apply { nextRound() }
        assertEquals(a.rows, b.rows)
        assertEquals(a.cols, b.cols)
        assertEquals(a.clues, b.clues)
        assertEquals(a.generatedSea, b.generatedSea)
    }

    @Test
    fun toggleWallPaintsErasesAndIgnoresClues() {
        val game = NurikabeGame(level = 1, random = Random(1L)).apply { nextRound() }
        val clueCell = game.clues.keys.first()
        val empty = (0 until game.rows * game.cols).first { it !in game.clues }

        game.toggleWall(empty)
        assertTrue(empty in game.walls)
        game.toggleWall(empty)
        assertFalse(empty in game.walls)

        game.toggleWall(clueCell)
        assertFalse(clueCell in game.walls, "clue cells must not become sea")

        // Out-of-range indices are ignored without crashing.
        game.toggleWall(-1)
        game.toggleWall(game.rows * game.cols)
    }

    @Test
    fun setWallsPaintsAndErasesSkippingClues() {
        val game = NurikabeGame(level = 1, random = Random(2L)).apply { nextRound() }
        val clueCell = game.clues.keys.first()
        val empties = (0 until game.rows * game.cols).filter { it !in game.clues }.take(4)
        val stroke = empties + clueCell

        game.setWalls(stroke, true)
        assertTrue(empties.all { it in game.walls })
        assertFalse(clueCell in game.walls, "clue cells must not become sea")

        game.setWalls(stroke, false)
        assertTrue(stroke.none { it in game.walls })
    }

    @Test
    fun whiteRegionWithoutClueIsNotSolved() {
        val game = NurikabeGame(level = 4, random = Random(3L)).apply { nextRound() }
        // Leave one sea cell unpainted: it becomes a stray white cell that breaks the solution.
        game.setWalls(game.generatedSea.toList().dropLast(1), true)
        assertFalse(game.isCorrect(""))
    }

    @Test
    fun isolatingEveryClueIsNotSolved() {
        val game = NurikabeGame(level = 4, random = Random(4L)).apply { nextRound() }
        // Paint every non-clue cell: each clue becomes a size-1 island, smaller than its value.
        game.setWalls((0 until game.rows * game.cols).filter { it !in game.clues }, true)
        assertFalse(game.isCorrect(""))
    }

    @Test
    fun islandWithWrongSizeIsNotSolved() {
        // 3x3, clue "2" at the centre but the island is left at size 1.
        val game = NurikabeGame().apply {
            rows = 3
            cols = 3
            clues.clear()
            walls.clear()
            clues[4] = 2
        }
        game.setWalls((0 until 9).filter { it != 4 }, true)
        assertFalse(game.isCorrect(""))
    }

    @Test
    fun seaPoolIsNotSolved() {
        // 4x4 with the top and bottom rows as size-4 islands and the two middle rows as sea.
        // Every rule holds except the sea contains 2x2 pools, which Nurikabe forbids.
        val game = NurikabeGame().apply {
            rows = 4
            cols = 4
            clues.clear()
            walls.clear()
            clues[0] = 4
            clues[12] = 4
        }
        game.setWalls((4..11).toList(), true)
        assertFalse(game.isCorrect(""))
    }

    @Test
    fun disconnectedSeaIsNotSolved() {
        // 3x3 with the middle column as a size-3 island, splitting the sea into two strips.
        val game = NurikabeGame().apply {
            rows = 3
            cols = 3
            clues.clear()
            walls.clear()
            clues[4] = 3
        }
        game.setWalls(listOf(0, 3, 6, 2, 5, 8), true)
        assertFalse(game.isCorrect(""))
    }

    @Test
    fun uiStateReportsLiveFeedback() {
        // 3x3, clue "2" at the centre. A correct 2-cell island reads satisfied; an over-grown one
        // reads invalid; the untouched board reports neither.
        fun fixture(): NurikabeGame = NurikabeGame().apply {
            rows = 3
            cols = 3
            clues.clear()
            walls.clear()
            clues[4] = 2
        }

        val fresh = fixture().toUiState()
        assertTrue(fresh.satisfiedCells.isEmpty())
        assertTrue(fresh.invalidCells.isEmpty())

        val correct = fixture().apply { setWalls((0 until 9).filter { it != 4 && it != 1 }, true) }
            .toUiState()
        assertEquals(setOf(1, 4), correct.satisfiedCells.toSet())
        assertTrue(correct.invalidCells.isEmpty())

        val overgrown = fixture().apply { setWalls(listOf(0, 2, 3, 5, 6, 8), true) }
            .toUiState()
        assertEquals(setOf(1, 4, 7), overgrown.invalidCells.toSet())
        assertTrue(overgrown.satisfiedCells.isEmpty())
    }

    @Test
    fun uiStateReportsSeaPools() {
        val game = NurikabeGame().apply {
            rows = 4
            cols = 4
            clues.clear()
            walls.clear()
            clues[0] = 4
            clues[12] = 4
        }
        game.setWalls((4..11).toList(), true)
        val ui = game.toUiState()
        assertEquals((4..11).toSet(), ui.poolCells.toSet())
    }

    @Test
    fun acceptsAnyValidSolutionNotJustOne() {
        // 3x3 with a single clue "2" at the centre: the island may grow up, down, left or right,
        // so the puzzle has four valid solutions and isSolved must accept each of them.
        fun fixture(): NurikabeGame = NurikabeGame().apply {
            rows = 3
            cols = 3
            clues.clear()
            walls.clear()
            clues[4] = 2
        }

        val up = fixture().apply { setWalls((0 until 9).filter { it != 4 && it != 1 }, true) }
        assertTrue(up.isCorrect(""), "upward island rejected")

        val left = fixture().apply { setWalls((0 until 9).filter { it != 4 && it != 3 }, true) }
        assertTrue(left.isCorrect(""), "leftward island rejected")

        assertEquals(4, fixture().solutionCount(limit = 5))
    }
}
