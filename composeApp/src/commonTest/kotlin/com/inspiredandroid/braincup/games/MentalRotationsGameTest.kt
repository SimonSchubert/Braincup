package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.FeedbackMessage
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MentalRotationsGameTest {

    @Test
    fun `there are exactly 24 lattice rotations and they are distinct`() {
        assertEquals(24, LatticeRotation.all.size)
        assertEquals(24, LatticeRotation.all.toSet().size)
    }

    @Test
    fun `every lattice rotation preserves the shape it is applied to`() {
        val figure = staircase()
        LatticeRotation.all.forEach { rotation ->
            assertTrue(
                sameShape(figure, figure.map(rotation::apply)),
                "a rotation produced a figure that no longer matches the original",
            )
        }
    }

    @Test
    fun `the mirror of a chiral figure is never reachable by rotation`() {
        val figure = staircase()
        assertFalse(sameShape(figure, mirror(figure)))
    }

    @Test
    fun `the mirror of an achiral figure is reachable by rotation`() {
        // A straight bar is symmetric, so reflecting it is the same as turning it round.
        val bar = listOf(Cube(0, 0, 0), Cube(1, 0, 0), Cube(2, 0, 0))
        assertTrue(sameShape(bar, mirror(bar)))
    }

    @Test
    fun `generated figures never self-intersect and have the expected cube count`() {
        val random = Random(20260828)
        repeat(300) {
            val armCount = 2 + (it % 4)
            val figure = generateFigure(armCount, 2..3, random) ?: return@repeat
            assertEquals(
                figure.size,
                figure.toSet().size,
                "the chain visited the same lattice cell twice",
            )
            // One origin cube, plus 2 or 3 per arm.
            assertTrue(figure.size in (1 + armCount * 2)..(1 + armCount * 3))
        }
    }

    @Test
    fun `every generated round has exactly one correct answer`() {
        val game = MentalRotationsGame(Random(4711))
        repeat(400) {
            game.nextRound()
            val reference = game.referenceCubes
            val candidate = game.candidateCubes

            assertEquals(reference.size, candidate.size, "the pair must be built from one figure")

            // The whole fairness claim: a "mirrored" round must be unreachable by any rotation, and
            // a "same" round must be reachable by one. Either way only one button can be right.
            val reachable = sameShape(reference, candidate)
            if (game.isMirrored) {
                assertFalse(reachable, "a mirrored round was solvable as 'same'")
                assertTrue(game.isCorrect(MentalRotationsGame.ANSWER_MIRRORED))
                assertFalse(game.isCorrect(MentalRotationsGame.ANSWER_SAME))
            } else {
                assertTrue(reachable, "a 'same' round was not actually the same figure")
                assertTrue(game.isCorrect(MentalRotationsGame.ANSWER_SAME))
                assertFalse(game.isCorrect(MentalRotationsGame.ANSWER_MIRRORED))
            }
        }
    }

    @Test
    fun `a same round is always visibly turned, never an identical copy`() {
        val game = MentalRotationsGame(Random(99))
        var sameRounds = 0
        repeat(300) {
            game.nextRound()
            if (game.isMirrored) return@repeat
            sameRounds++
            assertTrue(
                normalize(game.referenceCubes) != normalize(game.candidateCubes),
                "the candidate was shown at the reference's own orientation, which is a free point",
            )
        }
        assertTrue(sameRounds > 0, "the sample produced no non-mirrored rounds to check")
    }

    @Test
    fun `both answers come up over a run`() {
        val game = MentalRotationsGame(Random(123))
        var mirrored = 0
        repeat(200) {
            game.nextRound()
            if (game.isMirrored) mirrored++
        }
        assertTrue(mirrored in 60..140, "answers were lopsided: $mirrored mirrored out of 200")
    }

    @Test
    fun `rounds are varied, so the fallback figure is not carrying the game`() {
        val game = MentalRotationsGame(Random(2024))
        val shapes = mutableSetOf<Set<Cube>>()
        repeat(100) {
            game.nextRound()
            shapes.add(normalize(game.referenceCubes))
        }
        // Generation only falls back after 60 failed attempts, which should effectively never
        // happen; if it started happening, every round would show the same staircase.
        assertTrue(shapes.size > 40, "only ${shapes.size} distinct figures in 100 rounds")
    }

    @Test
    fun `figures grow with the ramp`() {
        val game = MentalRotationsGame(Random(7))
        game.nextRound()
        val early = game.referenceCubes.size

        game.round = 20
        game.nextRound()
        val late = game.referenceCubes.size

        assertTrue(late > early, "round 20 was not bigger than round 1 ($late vs $early)")
    }

    @Test
    fun `an unsolved round reports the answer it expected`() {
        val game = MentalRotationsGame(Random(31337))
        repeat(20) {
            game.nextRound()
            val expected =
                if (game.isMirrored) MentalRotationsGame.ANSWER_MIRRORED else MentalRotationsGame.ANSWER_SAME
            assertEquals(expected, game.solution())
        }
    }

    @Test
    fun `a lone cube projects to a regular hexagon`() {
        // Width 2*sqrt(3)/2 and height 2 for a unit edge. Anything else and the cube is stretched.
        val box = listOf(Cube(0, 0, 0)).toProjection()
        assertEquals(2f * CUBE_HALF_WIDTH, box.width, 0.001f)
        assertEquals(CUBE_TOP_HEIGHT + CUBE_SIDE_HEIGHT, box.height, 0.001f)
        assertEquals(2f, box.height, 0.001f)
    }

    @Test
    fun `the drawn cube matches the lattice steps, so neighbours tile seamlessly`() {
        assertEquals(ISO_X, CUBE_HALF_WIDTH, 0.0001f)
        assertEquals(2f * ISO_Y, CUBE_TOP_HEIGHT, 0.0001f)
        // The one that produced visibly uneven cubes when it drifted: a side drawn taller than the
        // z step makes every cube overpaint the one beneath it.
        assertEquals(ISO_Z, CUBE_SIDE_HEIGHT, 0.0001f)
    }

    @Test
    fun `stacking in z moves a cube by exactly its own height`() {
        val stacked = listOf(Cube(0, 0, 0), Cube(0, 0, 1)).toProjection()
        val (upper, lower) = stacked.cubes.sortedBy { it.y }
        assertEquals(CUBE_SIDE_HEIGHT, lower.y - upper.y, 0.001f)
        assertEquals(upper.x, lower.x, 0.001f)
        // Two cubes tall: one cube's box plus one more z step.
        assertEquals(CUBE_TOP_HEIGHT + 2f * CUBE_SIDE_HEIGHT, stacked.height, 0.001f)
    }

    @Test
    fun `a neighbour in x shares an edge with its predecessor`() {
        val pair = listOf(Cube(0, 0, 0), Cube(1, 0, 0)).toProjection()
        val (left, right) = pair.cubes.sortedBy { it.x }
        assertEquals(ISO_X, right.x - left.x, 0.001f)
        assertEquals(ISO_Y, right.y - left.y, 0.001f)
    }

    @Test
    fun `the box hugs the figure, with no dead margin above or below`() {
        val column = listOf(Cube(0, 0, 0), Cube(0, 0, 1), Cube(0, 0, 2)).toProjection()
        // Topmost cube's top vertex sits on the box's top edge.
        assertEquals(0f, column.cubes.minOf { it.y }, 0.001f)
        // Bottom cube's lowest drawn point sits on the box's bottom edge.
        val lowest = column.cubes.maxOf { it.y } + CUBE_TOP_HEIGHT + CUBE_SIDE_HEIGHT
        assertEquals(column.height, lowest, 0.001f)
    }

    @Test
    fun `the shown answer is a localizable message, never the raw wire token`() {
        val game = MentalRotationsGame(Random(555))
        repeat(20) {
            game.nextRound()
            val message = game.solutionMessage()
            // Plain would put the English token "same"/"mirrored" straight on the feedback screen.
            assertTrue(
                message is FeedbackMessage.MirrorAnswer,
                "solutionMessage must resolve against strings.xml, got $message",
            )
            assertEquals(game.isMirrored, message.isMirrored)
        }
    }

    @Test
    fun `the game contributes no hint text of its own`() {
        val game = MentalRotationsGame(Random(556))
        game.nextRound()
        assertEquals(null, game.hint())
        assertEquals(null, game.hintMessage())
    }

    @Test
    fun `projection reports a positive box and one entry per cube`() {
        val figure = staircase()
        val projected = figure.toProjection()
        assertEquals(figure.size, projected.cubes.size)
        assertTrue(projected.width > 0f)
        assertTrue(projected.height > 0f)
        // Everything sits inside the reported box, which is what the screen scales to.
        assertTrue(projected.cubes.all { it.x >= -0.001f && it.y >= -0.001f })
    }

    @Test
    fun `an empty figure projects to an empty box without throwing`() {
        val projected = emptyList<Cube>().toProjection()
        assertTrue(projected.cubes.isEmpty())
    }

    /** A chiral 3-arm staircase, the same shape the game falls back to. */
    private fun staircase() = listOf(
        Cube(0, 0, 0),
        Cube(1, 0, 0),
        Cube(2, 0, 0),
        Cube(2, 1, 0),
        Cube(2, 2, 0),
        Cube(2, 2, 1),
        Cube(2, 2, 2),
    )
}
