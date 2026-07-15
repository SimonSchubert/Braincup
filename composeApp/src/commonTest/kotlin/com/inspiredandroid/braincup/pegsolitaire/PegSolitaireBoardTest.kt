package com.inspiredandroid.braincup.pegsolitaire

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PegSolitaireBoardTest {

    @Test
    fun englishStarting_has33Holes32PegsAndEmptyCenter() {
        val b = PegSolitaireBoard.englishStarting()
        assertEquals(33, b.holeCount())
        assertEquals(32, b.pegCount())
        assertEquals(PegCell.EMPTY, b.cellAt(PEG_CENTER, PEG_CENTER))
        assertEquals(PegSolitaireResult.ONGOING, b.result())
    }

    @Test
    fun englishShape_cornersInvalid() {
        val b = PegSolitaireBoard.englishStarting()
        // Four corner 2×2 blocks
        for (row in 0..1) {
            for (col in 0..1) {
                assertEquals(PegCell.INVALID, b.cellAt(row, col))
                assertEquals(PegCell.INVALID, b.cellAt(row, col + 5))
                assertEquals(PegCell.INVALID, b.cellAt(row + 5, col))
                assertEquals(PegCell.INVALID, b.cellAt(row + 5, col + 5))
            }
        }
        assertFalse(PegSolitaireBoard.isValidHole(0, 0))
        assertTrue(PegSolitaireBoard.isValidHole(3, 3))
        assertTrue(PegSolitaireBoard.isValidHole(0, 3))
        assertTrue(PegSolitaireBoard.isValidHole(3, 0))
    }

    @Test
    fun startingPosition_hasLegalJumpsIntoCenter() {
        val b = PegSolitaireBoard.englishStarting()
        // Classic first moves: jump into center from the four orthogonal directions.
        val expected = setOf(
            PegJump(1, 3, 3, 3),
            PegJump(5, 3, 3, 3),
            PegJump(3, 1, 3, 3),
            PegJump(3, 5, 3, 3),
        )
        val intoCenter = b.allLegalJumps().filter { it.toRow == 3 && it.toCol == 3 }.toSet()
        assertEquals(expected, intoCenter)
    }

    @Test
    fun apply_removesMidPegAndMovesJumper() {
        val b = PegSolitaireBoard.englishStarting()
        val jump = PegJump(3, 1, 3, 3)
        assertTrue(b.isLegalJump(jump))
        val next = b.apply(jump)
        assertEquals(31, next.pegCount())
        assertEquals(PegCell.EMPTY, next.cellAt(3, 1))
        assertEquals(PegCell.EMPTY, next.cellAt(3, 2))
        assertEquals(PegCell.PEG, next.cellAt(3, 3))
    }

    @Test
    fun illegalJumps_rejected() {
        val b = PegSolitaireBoard.englishStarting()
        // Destination occupied
        assertFalse(b.isLegalJump(PegJump(0, 2, 0, 4)))
        // No mid peg (would jump over empty center path incorrectly)
        assertFalse(b.isLegalJump(PegJump(3, 3, 3, 5))) // from empty
        // Off-board / invalid landing
        assertFalse(b.isLegalJump(PegJump(0, 3, -2, 3)))
        // Diagonal not allowed
        assertFalse(b.isLegalJump(PegJump(2, 2, 4, 4)))
        // Non-jump distance
        assertFalse(b.isLegalJump(PegJump(3, 0, 3, 1)))
    }

    @Test
    fun result_wonAndPerfectAndStuck() {
        val perfect = PegSolitaireBoard.fromPegs(setOf(3 to 3))
        assertEquals(PegSolitaireResult.WON_PERFECT, perfect.result())

        val wonOffCenter = PegSolitaireBoard.fromPegs(setOf(0 to 3))
        assertEquals(PegSolitaireResult.WON, wonOffCenter.result())

        // Two pegs far apart: no jump possible
        val stuck = PegSolitaireBoard.fromPegs(setOf(0 to 2, 0 to 4))
        assertEquals(2, stuck.pegCount())
        assertTrue(stuck.allLegalJumps().isEmpty())
        assertEquals(PegSolitaireResult.STUCK, stuck.result())
    }

    @Test
    fun multiJump_reducesPegCount() {
        // Three pegs in a row with room to jump: (3,1) PEG, (3,2) PEG, (3,3) EMPTY
        var b = PegSolitaireBoard.fromPegs(setOf(3 to 1, 3 to 2))
        assertEquals(2, b.pegCount())
        val jump = PegJump(3, 1, 3, 3)
        assertTrue(b.isLegalJump(jump))
        b = b.apply(jump)
        assertEquals(1, b.pegCount())
        assertEquals(PegCell.PEG, b.cellAt(3, 3))
        assertEquals(PegSolitaireResult.WON_PERFECT, b.result())
    }

    @Test
    fun legalJumpsFrom_onlyFromPegsWithLanding() {
        val b = PegSolitaireBoard.englishStarting()
        assertTrue(b.legalJumpsFrom(3, 1).isNotEmpty())
        assertTrue(b.legalJumpsFrom(3, 3).isEmpty()) // empty center
        assertTrue(b.legalJumpsFrom(0, 0).isEmpty()) // invalid
    }
}
