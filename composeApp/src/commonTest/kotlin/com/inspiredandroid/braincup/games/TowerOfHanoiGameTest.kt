package com.inspiredandroid.braincup.games

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TowerOfHanoiGameTest {

    @Test
    fun adaptiveDifficultyIsDisabled() {
        assertFalse(TowerOfHanoiGame().adaptiveDifficulty)
    }

    @Test
    fun levelIsCoercedToAtLeastOne() {
        assertEquals(1, TowerOfHanoiGame(level = 0).level)
        assertEquals(1, TowerOfHanoiGame(level = -5).level)
        assertEquals(7, TowerOfHanoiGame(level = 7).level)
    }

    @Test
    fun disksForLevelIsUniquePerLevel() {
        // Level L → L+2 disks; each level strictly harder than the previous.
        assertEquals(3, TowerOfHanoiGame.disksForLevel(1))
        assertEquals(4, TowerOfHanoiGame.disksForLevel(2))
        assertEquals(5, TowerOfHanoiGame.disksForLevel(3))
        assertEquals(6, TowerOfHanoiGame.disksForLevel(4))
        assertEquals(7, TowerOfHanoiGame.disksForLevel(5))
        assertEquals(8, TowerOfHanoiGame.disksForLevel(6))
        assertEquals(12, TowerOfHanoiGame.disksForLevel(10))
        for (level in 1..12) {
            assertEquals(level + 2, TowerOfHanoiGame.disksForLevel(level))
            if (level > 1) {
                assertTrue(
                    TowerOfHanoiGame.disksForLevel(level) > TowerOfHanoiGame.disksForLevel(level - 1),
                )
            }
        }
        // Non-positive levels still produce the level-1 disk count.
        assertEquals(3, TowerOfHanoiGame.disksForLevel(0))
        assertEquals(3, TowerOfHanoiGame.disksForLevel(-3))
    }

    @Test
    fun startStateHasAllDisksOnLeftPeg() {
        val game = TowerOfHanoiGame(level = 1).apply { nextRound() }
        val state = game.toUiState()
        assertEquals(3, state.diskCount)
        assertEquals(listOf(3, 2, 1), state.pegs[0])
        assertTrue(state.pegs[1].isEmpty())
        assertTrue(state.pegs[2].isEmpty())
        assertNull(state.selectedPeg)
        assertEquals(0, state.moves)
    }

    @Test
    fun higherLevelUsesMoreDisks() {
        val game = TowerOfHanoiGame(level = 3).apply { nextRound() }
        assertEquals(5, game.diskCount)
        assertEquals(listOf(5, 4, 3, 2, 1), game.toUiState().pegs[0])
    }

    @Test
    fun consecutiveLevelsHaveDifferentDiskCounts() {
        val counts = (1..8).map { level ->
            TowerOfHanoiGame(level = level).apply { nextRound() }.diskCount
        }
        assertEquals(counts.distinct().size, counts.size)
        assertEquals(listOf(3, 4, 5, 6, 7, 8, 9, 10), counts)
    }

    @Test
    fun selectAndDeselectPeg() {
        val game = TowerOfHanoiGame(level = 1).apply { nextRound() }
        assertFalse(game.tapPeg(0))
        assertEquals(0, game.selectedPeg)
        assertFalse(game.tapPeg(0))
        assertNull(game.selectedPeg)
        assertEquals(0, game.moves)
    }

    @Test
    fun emptyPegCannotBeSelectedAsSource() {
        val game = TowerOfHanoiGame(level = 1).apply { nextRound() }
        assertFalse(game.tapPeg(1))
        assertNull(game.selectedPeg)
    }

    @Test
    fun legalMoveUpdatesPegsAndIncrementsMoves() {
        val game = TowerOfHanoiGame(level = 1).apply { nextRound() }
        game.tapPeg(0)
        assertFalse(game.tapPeg(2))
        val state = game.toUiState()
        assertEquals(listOf(3, 2), state.pegs[0])
        assertEquals(listOf(1), state.pegs[2])
        assertEquals(1, state.moves)
        assertNull(state.selectedPeg)
    }

    @Test
    fun illegalMoveDoesNotChangePegs() {
        val game = TowerOfHanoiGame(level = 1).apply { nextRound() }
        // Move small disk to peg 1, then try to put medium on it.
        game.tapPeg(0)
        game.tapPeg(1)
        game.tapPeg(0) // select medium (2) under 3
        val before = game.toUiState()
        assertFalse(game.tapPeg(1)) // illegal: 2 onto 1
        val after = game.toUiState()
        assertEquals(before.pegs, after.pegs)
        assertEquals(1, after.moves)
        // Selection is cleared after a wrong move.
        assertNull(after.selectedPeg)
        assertEquals(1, after.rejectedPeg)
        assertEquals(0, after.rejectFromPeg)
        assertEquals(1, after.rejectNonce)
    }

    @Test
    fun repeatedIllegalMovesBumpRejectNonce() {
        val game = TowerOfHanoiGame(level = 1).apply { nextRound() }
        game.tapPeg(0)
        game.tapPeg(1)
        game.tapPeg(0)
        game.tapPeg(1)
        assertEquals(1, game.rejectNonce)
        assertNull(game.selectedPeg)
        // Must re-select after a reject; selection is disabled until then.
        game.tapPeg(0)
        game.tapPeg(1)
        assertEquals(2, game.rejectNonce)
        assertNull(game.selectedPeg)
        assertEquals(1, game.rejectedPeg)
        assertEquals(0, game.rejectFromPeg)
    }

    @Test
    fun illegalMoveOntoEmptyIsImpossible_legalOntoEmptyWorks() {
        val game = TowerOfHanoiGame(level = 1).apply { nextRound() }
        game.tapPeg(0)
        assertFalse(game.tapPeg(1))
        assertEquals(listOf(1), game.toUiState().pegs[1])
    }

    @Test
    fun threeDiskOptimalSequenceSolves() {
        val game = TowerOfHanoiGame(level = 1).apply { nextRound() }
        // Classic optimal 3-disk solution (0=A, 1=B, 2=C).
        val moves = listOf(
            0 to 2,
            0 to 1,
            2 to 1,
            0 to 2,
            1 to 0,
            1 to 2,
            0 to 2,
        )
        var solved = false
        for ((from, to) in moves) {
            game.tapPeg(from)
            solved = game.tapPeg(to)
        }
        assertTrue(solved)
        assertTrue(game.isSolved())
        assertEquals(7, game.moves)
        val state = game.toUiState()
        assertEquals(listOf(3, 2, 1), state.pegs[2])
        assertTrue(state.pegs[0].isEmpty())
        assertTrue(state.pegs[1].isEmpty())
    }

    @Test
    fun outOfRangePegIsIgnored() {
        val game = TowerOfHanoiGame(level = 1).apply { nextRound() }
        assertFalse(game.tapPeg(-1))
        assertFalse(game.tapPeg(3))
        assertNull(game.selectedPeg)
        assertEquals(0, game.moves)
    }
}
