package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.AnswerButton
import com.inspiredandroid.braincup.app.FeedbackMessage
import com.inspiredandroid.braincup.app.MentalRotationsUiState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlin.random.Random

/** A lattice point. Figures live on integer coordinates so rotations stay exact. */
data class Cube(val x: Int, val y: Int, val z: Int)

/**
 * One of the 24 orientation-preserving rotations of the cubic lattice, as a 3x3 integer matrix
 * stored row-major. Composing only these keeps every coordinate an integer, which is what lets
 * [sameShape] compare figures by exact set equality instead of by a floating-point tolerance.
 */
data class LatticeRotation(private val m: IntArray) {
    init {
        require(m.size == 9)
    }

    fun apply(c: Cube) = Cube(
        x = m[0] * c.x + m[1] * c.y + m[2] * c.z,
        y = m[3] * c.x + m[4] * c.y + m[5] * c.z,
        z = m[6] * c.x + m[7] * c.y + m[8] * c.z,
    )

    // IntArray uses identity equals, so the data class defaults would be wrong.
    override fun equals(other: Any?) = other is LatticeRotation && m.contentEquals(other.m)

    override fun hashCode() = m.contentHashCode()

    companion object {
        /**
         * All 24 rotations, generated rather than written out: every one maps the x axis to one
         * of the 6 signed axes and then the y axis to one of the 4 remaining perpendicular ones,
         * with z fixed by the cross product so the result is a rotation and never a reflection.
         */
        val all: List<LatticeRotation> = buildList {
            val axes = listOf(
                intArrayOf(1, 0, 0),
                intArrayOf(-1, 0, 0),
                intArrayOf(0, 1, 0),
                intArrayOf(0, -1, 0),
                intArrayOf(0, 0, 1),
                intArrayOf(0, 0, -1),
            )
            for (col0 in axes) {
                for (col1 in axes) {
                    // Perpendicular means zero dot product; parallel or antiparallel is rejected.
                    if (col0[0] * col1[0] + col0[1] * col1[1] + col0[2] * col1[2] != 0) continue
                    val col2 = intArrayOf(
                        col0[1] * col1[2] - col0[2] * col1[1],
                        col0[2] * col1[0] - col0[0] * col1[2],
                        col0[0] * col1[1] - col0[1] * col1[0],
                    )
                    add(
                        LatticeRotation(
                            intArrayOf(
                                col0[0], col1[0], col2[0],
                                col0[1], col1[1], col2[1],
                                col0[2], col1[2], col2[2],
                            ),
                        ),
                    )
                }
            }
        }
    }
}

/** Translate so the lowest corner sits at the origin, making two figures comparable as sets. */
fun normalize(cubes: List<Cube>): Set<Cube> {
    if (cubes.isEmpty()) return emptySet()
    val minX = cubes.minOf { it.x }
    val minY = cubes.minOf { it.y }
    val minZ = cubes.minOf { it.z }
    return cubes.mapTo(mutableSetOf()) { Cube(it.x - minX, it.y - minY, it.z - minZ) }
}

/** True when [b] is [a] under some lattice rotation, i.e. the same solid, merely turned. */
fun sameShape(a: List<Cube>, b: List<Cube>): Boolean {
    if (a.size != b.size) return false
    val target = normalize(b)
    return LatticeRotation.all.any { rotation ->
        normalize(a.map(rotation::apply)) == target
    }
}

/** Reflect through the x axis. For a chiral figure this can never be undone by a rotation. */
fun mirror(cubes: List<Cube>): List<Cube> = cubes.map { Cube(-it.x, it.y, it.z) }

/**
 * A Shepard-Metzler figure: a chain of straight arms of unit cubes, each arm turning onto an axis
 * perpendicular to the one before it.
 */
fun generateFigure(armCount: Int, armLength: IntRange, random: Random): List<Cube>? {
    val directions = listOf(
        Cube(1, 0, 0),
        Cube(-1, 0, 0),
        Cube(0, 1, 0),
        Cube(0, -1, 0),
        Cube(0, 0, 1),
        Cube(0, 0, -1),
    )
    val occupied = mutableSetOf(Cube(0, 0, 0))
    val order = mutableListOf(Cube(0, 0, 0))
    var head = Cube(0, 0, 0)
    var previous: Cube? = null

    repeat(armCount) {
        // Perpendicular to the previous arm, so the figure actually turns at every joint.
        val prev = previous
        val candidates = directions.filter { d ->
            prev == null || d.x * prev.x + d.y * prev.y + d.z * prev.z == 0
        }.shuffled(random)

        var placed = false
        for (direction in candidates) {
            val length = armLength.random(random)
            val arm = (1..length).map { step ->
                Cube(head.x + direction.x * step, head.y + direction.y * step, head.z + direction.z * step)
            }
            if (arm.any { it in occupied }) continue
            occupied.addAll(arm)
            order.addAll(arm)
            head = arm.last()
            previous = direction
            placed = true
            break
        }
        // Every perpendicular direction collided; the chain has boxed itself in.
        if (!placed) return null
    }
    return order
}

/**
 * Decide whether a reference/candidate pair is fair to ask.
 *
 * A mirrored pair is only answerable if the figure is genuinely chiral: for an achiral figure the
 * mirror *is* a rotation, so both answers would be right. A rotated pair is only interesting if
 * the candidate is not sitting at the same orientation as the reference, which would make it a
 * free point. Both checks are exact, because everything is on the integer lattice.
 */
fun isFairPair(reference: List<Cube>, candidate: List<Cube>, expectMirrored: Boolean): Boolean = if (expectMirrored) {
    !sameShape(reference, candidate)
} else {
    sameShape(reference, candidate) && normalize(reference) != normalize(candidate)
}

/**
 * Is the candidate the same solid as the reference, or its mirror image?
 *
 * Both figures are drawn as isometric cube stacks; the candidate is always turned, so the only
 * way through is to rotate it mentally and compare. Difficulty ramps on arm count, which grows
 * the figure, and on how far the candidate is turned away from the reference.
 *
 * Answers are [ANSWER_SAME] and [ANSWER_MIRRORED].
 */
class MentalRotationsGame(
    private val random: Random = Random.Default,
) : Game() {

    var referenceCubes: List<Cube> = emptyList()
        private set

    var candidateCubes: List<Cube> = emptyList()
        private set

    /** True when the candidate is the mirror image, so [ANSWER_MIRRORED] is the correct answer. */
    var isMirrored: Boolean = false
        private set

    var roundKey: Int = 0
        private set

    override fun generateRound() {
        val armCount = armCountForRound()
        val mirrored = random.nextBoolean()

        repeat(GENERATE_ATTEMPTS) {
            val figure = generateFigure(armCount, ARM_LENGTH, random) ?: return@repeat
            val candidate = turnedCandidate(figure, mirrored) ?: return@repeat
            show(figure, candidate, mirrored)
            return
        }

        // Every attempt was rejected: a boxed-in chain, an achiral figure whose mirror is just a
        // rotation, or a figure that hid one of its own cubes. FALLBACK_FIGURE is chiral and stays
        // fully in sight under all 24 rotations, so this path always has a round to show. Should
        // even that turn up nothing, the untouched figure is still a correct round, only an easy
        // one, which beats leaving the screen empty.
        val base = if (mirrored) mirror(FALLBACK_FIGURE) else FALLBACK_FIGURE
        show(FALLBACK_FIGURE, turnedCandidate(FALLBACK_FIGURE, mirrored) ?: base, mirrored)
    }

    private fun show(figure: List<Cube>, candidate: List<Cube>, mirrored: Boolean) {
        referenceCubes = figure
        candidateCubes = candidate
        isMirrored = mirrored
        roundKey++
    }

    /**
     * A turned copy of [figure], or of its mirror, that is fair to ask; null when no turn is.
     *
     * Both panels must also show every cube they contain, or the pair reads as two different
     * solids rather than one solid turned: see [allCubesVisible]. Rotations are tried in random
     * order so the candidate's orientation stays unpredictable instead of settling on whichever
     * rotation happens to come first in the list.
     */
    private fun turnedCandidate(figure: List<Cube>, mirrored: Boolean): List<Cube>? {
        if (!allCubesVisible(figure)) return null
        val base = if (mirrored) mirror(figure) else figure
        return LatticeRotation.all.shuffled(random).firstNotNullOfOrNull { rotation ->
            base.map(rotation::apply).takeIf {
                allCubesVisible(it) && isFairPair(figure, it, expectMirrored = mirrored)
            }
        }
    }

    override fun isCorrect(input: String): Boolean = input == if (isMirrored) ANSWER_MIRRORED else ANSWER_SAME

    /** The raw answer token, for the controller's board feedback. Never shown: see [solutionMessage]. */
    override fun solution(): String = if (isMirrored) ANSWER_MIRRORED else ANSWER_SAME

    /**
     * [ANSWER_SAME] and [ANSWER_MIRRORED] are wire tokens on the `onAnswer(String)` channel, not
     * English the player should ever read, so the feedback screen resolves the answer against
     * `strings.xml` instead of echoing [solution].
     */
    override fun solutionMessage(): FeedbackMessage = FeedbackMessage.MirrorAnswer(isMirrored)

    override fun hint(): String? = null

    override fun toUiState() = MentalRotationsUiState(
        roundKey = roundKey,
        reference = referenceCubes.toProjection(),
        candidate = candidateCubes.toProjection(),
        answers = ANSWERS.map { AnswerButton(it) }.toImmutableList(),
    )

    /**
     * Arm count grows with the ramp, then holds: past 5 arms the figure stops fitting a phone.
     *
     * Three arms is the floor, not two. Two perpendicular arms always lie in one plane, and a
     * planar figure is its own mirror image, so a two-arm round could never be a mirrored one.
     */
    private fun armCountForRound(): Int = when {
        round <= 5 -> 3
        round <= 9 -> 4
        else -> 5
    }

    companion object {
        const val ANSWER_SAME = "same"
        const val ANSWER_MIRRORED = "mirrored"
        val ANSWERS = listOf(ANSWER_SAME, ANSWER_MIRRORED)

        private val ARM_LENGTH = 2..3
        private const val GENERATE_ATTEMPTS = 60

        /**
         * A chiral 3-arm staircase, used only when generation keeps failing. Chosen so that every
         * one of the 24 rotations leaves all 8 cubes in sight, for the figure and for its mirror
         * alike, which makes the fallback safe to turn at random.
         */
        private val FALLBACK_FIGURE = listOf(
            Cube(0, 0, 0),
            Cube(1, 0, 0),
            Cube(2, 0, 0),
            Cube(2, 0, 1),
            Cube(2, 0, 2),
            Cube(2, 0, 3),
            Cube(2, 1, 3),
            Cube(2, 2, 3),
        )
    }
}

/**
 * True when every cube of [cubes] paints at least one pixel of the isometric view.
 *
 * The camera looks down the (1,1,1) diagonal, so a cube exactly one such step in front of another
 * projects onto the very same hexagon and hides it outright. Less obviously, a cube can also be
 * lost when its three viewer-facing faces are each covered by other cubes. Either way the drawing
 * holds fewer cubes than the figure does, and the two panels then read as two different solids
 * instead of one solid turned, which is the one thing the game must never show.
 *
 * The test is exact rather than a rasterisation, because on the lattice the blockers of a face are
 * a short fixed list, read straight off the projection: the cube sharing that face covers it
 * whole, and the two cubes sitting diagonally in front of it cover half each. Any blocker may sit
 * any number of (1,1,1) steps further forward and still land on the same place on screen, so each
 * one is followed along the view axis until it leaves the figure.
 */
fun allCubesVisible(cubes: List<Cube>): Boolean {
    if (cubes.size < 2) return true
    val occupied = cubes.toSet()
    // Far enough that a blocker walked this many steps is guaranteed to be outside the figure.
    val reach = maxOf(
        cubes.maxOf { it.x } - cubes.minOf { it.x },
        cubes.maxOf { it.y } - cubes.minOf { it.y },
        cubes.maxOf { it.z } - cubes.minOf { it.z },
    ) + 1

    fun blocked(cube: Cube, offset: Cube) = (0..reach).any { step ->
        Cube(cube.x + offset.x + step, cube.y + offset.y + step, cube.z + offset.z + step) in occupied
    }

    return cubes.none { cube ->
        blocked(cube, VIEW_STEP) ||
            FACE_BLOCKERS.all { (whole, half, otherHalf) ->
                blocked(cube, whole) || (blocked(cube, half) && blocked(cube, otherHalf))
            }
    }
}

/** One step towards the viewer. A cube here covers the one behind it exactly. */
private val VIEW_STEP = Cube(1, 1, 1)

/**
 * Per viewer-facing face, the neighbour that covers it whole and the two that cover half each.
 * Listed for the top (+z), right (+x) and left (+y) faces, which are the only three a cube shows.
 */
private val FACE_BLOCKERS = listOf(
    Triple(Cube(0, 0, 1), Cube(0, 1, 1), Cube(1, 0, 1)),
    Triple(Cube(1, 0, 0), Cube(1, 0, 1), Cube(1, 1, 0)),
    Triple(Cube(0, 1, 0), Cube(0, 1, 1), Cube(1, 1, 0)),
)

/**
 * Project the lattice onto the screen for drawing.
 *
 * The projection lives here rather than in the composable so the UI state stays a plain immutable
 * value that a screenshot test can build from a public constructor. Cubes come out sorted
 * back-to-front, so painting them in order gives correct occlusion without a depth buffer.
 */
fun List<Cube>.toProjection(): MentalRotationsUiState.Figure {
    if (isEmpty()) return MentalRotationsUiState.Figure(persistentListOf(), 1f, 1f)

    // Standard isometric axes: x and y go diagonally, z straight up the screen.
    val positions = map { cube ->
        val px = (cube.x - cube.y) * ISO_X
        val py = (cube.x + cube.y) * ISO_Y - cube.z * ISO_Z
        Triple(cube, px, py)
    }

    // A cube is drawn from its top vertex downwards, so it reaches CUBE_HALF_WIDTH to either side
    // and CUBE_TOP_HEIGHT + CUBE_SIDE_HEIGHT below. The topmost drawn pixel is the highest top
    // vertex itself, with nothing above it.
    val minX = positions.minOf { it.second } - CUBE_HALF_WIDTH
    val maxX = positions.maxOf { it.second } + CUBE_HALF_WIDTH
    val minY = positions.minOf { it.third }
    val maxY = positions.maxOf { it.third } + CUBE_TOP_HEIGHT + CUBE_SIDE_HEIGHT

    // Painter's algorithm: a cube is in front of another when it is further along every axis that
    // points towards the viewer, and x + y + z orders exactly that for an isometric view.
    val sorted = positions.sortedBy { (cube, _, _) -> cube.x + cube.y + cube.z }

    return MentalRotationsUiState.Figure(
        cubes = sorted.map { (_, px, py) ->
            MentalRotationsUiState.ProjectedCube(x = px - minX, y = py - minY)
        }.toImmutableList(),
        width = maxX - minX,
        height = maxY - minY,
    )
}

// Unit-cube geometry, in the same arbitrary units the projection reports its bounds in. The screen
// scales the whole figure to fit, so only the ratios between these matter.
//
// In a true isometric view all three axes foreshorten equally, so each unit edge projects to the
// same screen length and a lone cube draws as a regular hexagon. Three equalities keep neighbouring
// cubes tiling seamlessly, and `MentalRotationsGameTest` pins all three:
//
//   CUBE_HALF_WIDTH  == ISO_X      the top face's half-diagonal is one x step across
//   CUBE_TOP_HEIGHT  == 2 * ISO_Y  the top face spans one x step plus one y step down
//   CUBE_SIDE_HEIGHT == ISO_Z      the vertical edge is exactly what stacking in z moves by
//
// The last one is the one that bites: draw the side taller than the z step and every cube eats into
// the one below, so cubes end up with visibly different heights depending on what overlaps them.
internal const val ISO_X = 0.866f
internal const val ISO_Y = 0.5f
internal const val ISO_Z = 1.0f
internal const val CUBE_HALF_WIDTH = ISO_X
internal const val CUBE_TOP_HEIGHT = 2f * ISO_Y
internal const val CUBE_SIDE_HEIGHT = ISO_Z
