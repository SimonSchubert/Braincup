package com.inspiredandroid.braincup.matchstickriddles

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MatchstickRiddlesTest {

    @Test
    fun everyRiddleSolutionIsAValidStickMove() {
        for (riddle in MatchstickRiddles.all) {
            assertTrue(riddle.solutions.isNotEmpty(), "${riddle.id} has no solutions")
            for (solution in riddle.solutions) {
                assertEquals(riddle.initial.size, solution.size, "${riddle.id}: matchstick count must be conserved")
                val removed = riddle.initial - solution
                val added = solution - riddle.initial
                assertTrue(removed.isNotEmpty(), "${riddle.id}: a solution must differ from the start")
                assertEquals(removed.size, added.size, "${riddle.id}: every lifted stick must land somewhere")
                assertTrue(removed.all { it in riddle.initial }, "${riddle.id}: moved sticks must start occupied")
                assertTrue(added.all { it !in riddle.initial }, "${riddle.id}: destinations must start empty")
            }
        }
    }

    @Test
    fun theStartingBoardIsNotSolved() {
        for (riddle in MatchstickRiddles.all) {
            assertFalse(riddle.isSolved(riddle.initial), "${riddle.id}: the initial (false) equation must not count")
        }
    }

    @Test
    fun theIntendedSolutionIsAccepted() {
        for (riddle in MatchstickRiddles.all) {
            assertTrue(riddle.isSolved(riddle.solutions.first()), "${riddle.id}: the intended answer must count")
        }
    }

    @Test
    fun aMalformedBoardIsNotSolved() {
        for (riddle in MatchstickRiddles.all) {
            // An empty board leaves every digit blank, so it cannot decode to an equation.
            assertFalse(riddle.isSolved(emptySet()), "${riddle.id}: a blank board must not count")
        }
    }

    @Test
    fun anOpenLoopNineDoesNotCountAsNine() {
        // Regression for issue #32: "9-3=0" has a one-stick move that wins only because a nine
        // missing its bottom bar ("4 with a top bar") used to decode as a nine. Lift the left nine's
        // bottom stick onto the middle three's top-left slot: the three becomes a full nine and the
        // left nine is left malformed, reading "9-9=0". That must no longer count as solved.
        val riddle = MatchstickRiddles.byId("nine_minus_three")
        assertNotNull(riddle)
        val destination = (occupiedForEquation("9-9=0") - riddle.initial).single()
        val leftNineBottom = riddle.slots.indices
            .filter { riddle.slots[it].midX < 1f && riddle.slots[it].ay == riddle.slots[it].by }
            .maxByOrNull { riddle.slots[it].midY }
        assertNotNull(leftNineBottom)
        val malformed = riddle.initial - leftNineBottom + destination
        assertEquals(riddle.initial.size, malformed.size, "the reproduction must be a real one-stick move")
        assertFalse(riddle.isSolved(malformed), "an open-bottom nine (issue #32) must not be accepted")
        assertTrue(riddle.isSolved(riddle.solutions.first()), "the intended 9-3=6 answer must still count")
    }

    @Test
    fun anAlternativeTrueEquationIsAlsoAccepted() {
        // "9+3=0" is intended to become "9-9=0", but "6-6=0" is another true equation with the same
        // matchstick count, so it must count too (the player reached a valid answer, not THE answer).
        val riddle = MatchstickRiddles.byId("nine_plus_three")
        assertNotNull(riddle)
        val alternative = occupiedForEquation("6-6=0")
        assertEquals(riddle.initial.size, alternative.size, "the alternative must use the same number of sticks")
        assertTrue(alternative != riddle.solutions.first(), "the alternative must differ from the intended answer")
        assertTrue(riddle.isSolved(alternative), "any true equation must be accepted")
    }

    @Test
    fun draggingEachMovedStickOntoItsTargetSlotSolves() {
        for (riddle in MatchstickRiddles.all) {
            val solution = riddle.solutions.first()
            val destinations = solution - riddle.initial
            val occupied = riddle.initial.toMutableSet()

            // Reproduce the play screen's snap selection one drag at a time: grab a stick that still
            // needs to move and aim at an empty destination slot's midpoint; it should snap there.
            for (target in destinations) {
                val origin = occupied.first { it !in solution }
                val slot = riddle.slots[target]
                val picked = nearestSnapTarget(riddle, origin, occupied, slot.midX, slot.midY)
                assertNotNull(picked, "${riddle.id}: nothing snapped near destination $target")
                assertEquals(target, picked, "${riddle.id}: snap chose the wrong slot")
                occupied.remove(origin)
                occupied.add(picked)
            }
            assertTrue(riddle.isSolved(occupied), "${riddle.id}: committing the moves did not solve")
        }
    }

    @Test
    fun stickLockingEnforcesTheMoveBudget() {
        // A one-move riddle: nothing is locked at the start, but after relocating one stick the
        // remaining start-slot sticks lock while the moved stick stays adjustable.
        val riddle = MatchstickRiddles.byId("nine_minus_four")
        assertNotNull(riddle)
        assertEquals(1, riddle.moves)
        assertTrue(riddle.lockedSticks(riddle.initial).isEmpty(), "nothing is locked before a move")

        val origin = riddle.initial.first { it !in riddle.fixedSlots }
        val destination = riddle.slots.indices.first { it !in riddle.initial }
        val afterMove = riddle.initial - origin + destination
        val locked = riddle.lockedSticks(afterMove)
        assertTrue(destination !in locked, "the moved stick must stay adjustable")
        assertTrue(locked.isNotEmpty() && locked.all { it in riddle.initial }, "start-slot sticks lock")
        assertTrue(origin !in locked, "a stick that left its slot is not in the locked set")
        assertTrue(locked.none { it in riddle.fixedSlots }, "the equals sign is never budget-locked")
    }

    @Test
    fun theEqualsSignIsFixedAndNeverMoves() {
        for (riddle in MatchstickRiddles.all) {
            assertEquals(2, riddle.fixedSlots.size, "${riddle.id}: the equals sign has two fixed bars")
            assertTrue(riddle.fixedSlots.all { it in riddle.initial }, "${riddle.id}: fixed bars start lit")
            // No solution may relocate an equals bar.
            val moved = riddle.initial - riddle.solutions.first()
            assertTrue(moved.none { it in riddle.fixedSlots }, "${riddle.id}: a solution must not move the equals sign")
        }
    }

    @Test
    fun noRiddleCanBeSolvedInFewerMovesThanDeclared() {
        // The declared move count is the puzzle's difficulty promise. Brute-force every shorter
        // relocation count and confirm none reaches a true equation, then confirm the declared
        // count does. This guarantees each riddle is genuinely as hard as it claims (no shortcut).
        for (riddle in MatchstickRiddles.all) {
            for (shorter in 1 until riddle.moves) {
                assertFalse(
                    solvableInExactly(riddle, shorter),
                    "${riddle.id}: declared ${riddle.moves} moves but solvable in $shorter",
                )
            }
            assertTrue(
                solvableInExactly(riddle, riddle.moves),
                "${riddle.id}: not solvable in its declared ${riddle.moves} moves",
            )
        }
    }

    @Test
    fun multiTermEquationsAreEvaluatedLeftToRight() {
        // A three-addend riddle must accept any true chain and reject a false one with the same
        // matchstick count, proving the additive-chain evaluator (not just single-operator) is live.
        val riddle = MatchstickRiddles.byId("triple_seven")
        assertNotNull(riddle)
        assertTrue(riddle.isSolved(occupiedForEquation("7+7+7=21")), "1+1+1=23 -> 7+7+7=21 must count")
        assertFalse(riddle.isSolved(occupiedForEquation("1+7+7=21")), "a false chain must not count")
    }

    @Test
    fun storeProgressMaxLeavesHeadroomBeyondTheLiveCatalog() {
        assertTrue(
            MatchstickRiddles.storeProgressMax > MatchstickRiddles.count,
            "store progress must exceed the catalog so Play/GC never auto-complete when the set is cleared",
        )
    }

    @Test
    fun pointToSegmentDistanceMeasuresPerpendicularAndEndpoints() {
        val horizontal = Stick(0f, 0f, 2f, 0f)
        assertEquals(0f, pointToSegmentDistance(1f, 0f, horizontal), 1e-4f) // on the segment
        assertEquals(1f, pointToSegmentDistance(1f, 1f, horizontal), 1e-4f) // perpendicular
        assertEquals(1f, pointToSegmentDistance(3f, 0f, horizontal), 1e-4f) // past endpoint B
    }
}

/** True when some sequence of exactly [k] stick relocations turns [riddle] into a true equation. */
private fun solvableInExactly(riddle: MatchstickRiddle, k: Int): Boolean {
    val movable = riddle.initial.filter { it !in riddle.fixedSlots }
    val empty = riddle.slots.indices.filter { it !in riddle.initial }
    for (remove in combinations(movable, k)) {
        val base = riddle.initial - remove.toSet()
        for (add in combinations(empty, k)) {
            if (riddle.isSolved(base + add.toSet())) return true
        }
    }
    return false
}

/** Lazily yields every size-[k] combination of [items], preserving order. */
private fun <T> combinations(items: List<T>, k: Int): Sequence<List<T>> = sequence {
    when {
        k == 0 -> yield(emptyList())
        k <= items.size -> for (i in items.indices) {
            for (tail in combinations(items.subList(i + 1, items.size), k - 1)) {
                yield(listOf(items[i]) + tail)
            }
        }
    }
}

private const val SNAP_RADIUS = 0.7f

/** Mirrors MatchstickRiddlesPlayScreen's snap selection so the logic is covered without a gesture. */
private fun nearestSnapTarget(
    riddle: MatchstickRiddle,
    origin: Int,
    occupied: Set<Int>,
    nx: Float,
    ny: Float,
): Int? = riddle.slots.indices
    .filter { it == origin || it !in occupied }
    .minByOrNull { pointToSegmentDistance(nx, ny, riddle.slots[it]) }
    ?.takeIf { pointToSegmentDistance(nx, ny, riddle.slots[it]) <= SNAP_RADIUS }
