package com.inspiredandroid.braincup.learn

import androidx.compose.runtime.Immutable

/**
 * The diagram shown with a lesson step, rendered and animated by `LearnVisualCanvas`.
 *
 * Every variant carries the numbers it is about rather than being a fixed stock sketch: a step
 * asking about a pentagon shows a real pentagon, and one about 4 + 3 shows four dots and three
 * dots sliding into a group of seven. That way the picture does the teaching and the body text
 * can stay short.
 */
@Immutable
sealed interface LearnVisual {

    /**
     * Whether the figure may caption itself with the value it works out. Steps that ask a question
     * set this false: the picture still shows the situation honestly, but the learner does the
     * counting instead of reading the total off the diagram.
     *
     * It stays false even once the question has been answered. The answer belongs to the screen
     * around the figure - the option that turns green, the question mark in the sum resolving -
     * and a diagram repeating it states the same fact twice. What the figure does do once
     * answered is mark where the learner's own value sits, through `VisualAnswer`.
     */
    val reveal: Boolean get() = true

    // --- Number ---------------------------------------------------------------------------

    /** Dot groups that count themselves, then slide together into one total. */
    data class Counters(
        val groups: List<Int>,
        val merge: Boolean = true,
        override val reveal: Boolean = true,
    ) : LearnVisual

    /** A ten-frame filling to 10, then spilling the remainder into a second frame. */
    data class TenFrame(val filled: Int, val added: Int, override val reveal: Boolean = true) : LearnVisual

    /**
     * Number line from [from] to [to]; when [jump] is set, a hop animates out of [start].
     *
     * [hopSteps] replaces that even split with hops of different sizes, taken in order, for the
     * figures that bridge through ten: 15 - 8 is drawn as -5 down to ten and then -3, which is
     * how the lesson teaches it. [jump] and [hops] are ignored when it is set.
     *
     * [thenJump] adds a second hop demonstrated after the first, holding between the two and
     * looping. It exists for the figures that teach a pair of opposite moves - one more and one
     * less - where showing only one direction teaches only half the idea.
     *
     * [compare] numbers and marks a set of values the question asks the learner to weigh against
     * each other. A step asking which of -10, -6, -1 and 0 is the largest cannot be answered off a
     * line that numbers only every fifth tick, and drawing a hop instead would work the question
     * out for them. These are candidates rather than roles, so they take the ordinary ink: the one
     * that turns out to be the answer is the option tile, not the axis.
     */
    data class NumberLine(
        val from: Int,
        val to: Int,
        val tickStep: Int = 1,
        val start: Int? = null,
        val jump: Int = 0,
        val hops: Int = 1,
        val hopSteps: List<Int> = emptyList(),
        val thenJump: Int? = null,
        val compare: List<Int> = emptyList(),
        override val reveal: Boolean = true,
    ) : LearnVisual

    /**
     * Base-ten rods and unit cubes, as `tens` to `ones`.
     *
     * [plus] sets a second number beside the first to be added to it, and [compare] one to be
     * measured against it. A step that asks which of two numbers is larger has to show both of
     * them, and one that adds two numbers has to show both piles going in.
     */
    data class PlaceValue(
        val tens: Int,
        val ones: Int,
        val plus: Pair<Int, Int>? = null,
        val compare: Pair<Int, Int>? = null,
        override val reveal: Boolean = true,
    ) : LearnVisual

    /**
     * Hundred-square shaded to a decimal, with a second one beside it to measure against with
     * [compare] or to add to it with [plus].
     *
     * A sum's total arrives as a third square once the figure may reveal it, so a learner watches
     * tenths land on tenths instead of being told to line the points up. Both parts of a [plus]
     * have to fit inside one square, which is the whole reason the sums are written under 1.
     *
     * [of] is the whole the shading is a percentage of, so "20% of 80" can put the 80 on the panel
     * and grow the answer out of the shaded squares, instead of leaving the grid to illustrate the
     * percentage and say nothing about the question. [percent] captions the square "35% = 0.35"
     * rather than "0.35", for the step whose whole point is that those are one number twice.
     *
     * [compareDecimals] writes the comparison's caption to that many places, so the square the step
     * calls 0.40 is captioned 0.40 and not 0.4. A Double cannot carry the nought that the step
     * about noughts on the end is entirely about, so the figure has to be told to keep it.
     */
    data class DecimalGrid(
        val value: Double,
        val compare: Double? = null,
        val compareDecimals: Int? = null,
        val plus: Double? = null,
        val of: Int? = null,
        val percent: Boolean = false,
        override val reveal: Boolean = true,
    ) : LearnVisual

    /**
     * Rows x columns of dots, filled row by row: multiplication you can count.
     *
     * [split] cuts the rows into two blocks, the first [split] and the rest, drawn in the two group
     * colours. That is how a hard fact is actually worked out - 6 eights is 5 eights and one more
     * eight - and it lets the prose colour its numbers to match the blocks.
     *
     * [leftover] adds the dots that could not fill another whole row, which is what turns the same
     * picture from building up into sharing out.
     *
     * The figure draws the situation and never the sum it comes to, so it has no [reveal] of its
     * own: the step's formula is what says "4 x 6 = 24", and the array poses a question honestly
     * whichever way round it is read.
     */
    data class ArrayDots(
        val rows: Int,
        val cols: Int,
        val split: Int? = null,
        val leftover: Int = 0,
    ) : LearnVisual {
        /**
         * The split the figure draws a lane for, or null for one unbroken block.
         *
         * A split that takes every row or none of them is not a split, and drawing the lane
         * anyway would promise a second block that never comes.
         */
        val bandSplit: Int? get() = split?.takeIf { it in 1 until rows.coerceAtLeast(1) }

        /** How many rows the first band holds, which is what its label has to agree with. */
        val bandRows: Int get() = bandSplit ?: rows.coerceAtLeast(1)
    }

    /**
     * One fraction bar, a second stacked under it to measure against with [compare], or the two
     * parts of a sum with [plus].
     *
     * A [plus] closes with the total on a third bar, keeping each half in the colour it arrived in,
     * so "the pieces are the same size, add the tops" is something to look at rather than a rule.
     * Both bars must be cut into the same number of pieces: pieces only add when they match.
     */
    data class Fraction(
        val numerator: Int,
        val denominator: Int,
        val compare: Pair<Int, Int>? = null,
        val plus: Pair<Int, Int>? = null,
        override val reveal: Boolean = true,
    ) : LearnVisual

    /**
     * A ratio as one bar cut into its parts, each run in its own colour: 2 : 3 is a run of two
     * beside a run of three, not a fraction bar shaded 2 of 5.
     *
     * It is a separate figure from [Fraction] because reading a ratio as a fraction of the whole
     * is the one mistake this corner of the curriculum exists to head off, and a fraction bar
     * captions itself "2/5". Here the two runs are equal partners and the counts sit over their
     * own colours.
     *
     * [scale] cuts every part again once the bar is out, so the same bar reads as 3 : 5 and then
     * as 12 : 20 and equivalence is a finer cut rather than a second diagram. [total] shares that
     * amount over the parts and says underneath what each run is worth.
     */
    data class RatioBar(
        val parts: List<Int>,
        val scale: Int = 1,
        val total: Int? = null,
        override val reveal: Boolean = true,
    ) : LearnVisual

    /** Coins with their values, counted on one at a time to a running total. */
    data class Coins(
        val values: List<Int>,
        val currency: String = "c",
        override val reveal: Boolean = true,
    ) : LearnVisual

    /** A ruler with an object laid along it from zero. */
    data class Ruler(
        val length: Int,
        val span: Int = 10,
        val unit: String = "cm",
        override val reveal: Boolean = true,
    ) : LearnVisual

    /** Clock face with both hands. */
    data class Clock(val hour: Int, val minute: Int) : LearnVisual

    /**
     * A solution set on a number line: every value on one side of [value], drawn as a ray. The end
     * is hollow for a strict inequality and solid for [orEqual], which is the whole difference
     * between `x > 3` and `x >= 3` and the thing learners most often lose a mark on.
     */
    data class Inequality(
        val from: Int,
        val to: Int,
        val value: Int,
        val greater: Boolean,
        val orEqual: Boolean = false,
        override val reveal: Boolean = true,
    ) : LearnVisual

    /**
     * Terms with the step between them labelled, for sequences and skip counting.
     *
     * The terms are [Number] rather than [Int] because a chain that divides by ten leaves the
     * whole numbers behind: 8200, 820, 82 continues to 8.2, and rounding that last term to 8 made
     * the figure label a hop x0.1 that was nothing of the kind.
     */
    data class Steps(val terms: List<Number>, val multiply: Boolean = false) : LearnVisual

    // --- Shape ----------------------------------------------------------------------------

    /** Regular polygon whose sides draw on one at a time and whose corners are counted. */
    data class Polygon(
        val sides: Int,
        val countCorners: Boolean = true,
        override val reveal: Boolean = true,
    ) : LearnVisual {
        /** Fewer than three sides is not a polygon, so the figure draws and counts three. */
        val drawnSides: Int get() = sides.coerceAtLeast(3)
    }

    /**
     * A triangle of the named kind, with tick marks on sides of equal length.
     *
     * [Polygon] builds a regular shape, so a triangle asked for there is always equilateral - the
     * wrong picture for an isosceles question, and worse for one whose angles are given as
     * unequal. [RightTriangle] is the fourth kind and stays separate: it carries side lengths and
     * the squares of Pythagoras, which these do not.
     */
    data class Triangle(
        val kind: TriKind,
        val marks: Boolean = true,
        override val reveal: Boolean = true,
    ) : LearnVisual

    /**
     * A named quadrilateral drawn as the shape it really is, with tick marks on sides of equal
     * length and chevrons on parallel ones.
     *
     * [Polygon] can only build a regular shape, so a rhombus asked for there arrives as a square
     * and a parallelogram as a rectangle - which is the exact confusion these lessons exist to
     * clear up. Set [marks] to false for a step that wants the bare outline.
     */
    data class Quadrilateral(
        val kind: QuadKind,
        val marks: Boolean = true,
        override val reveal: Boolean = true,
    ) : LearnVisual

    /**
     * A quadrilateral with all four corners sitting on the circle, drawn deliberately lopsided so
     * that "opposite angles add to 180" reads as a claim about any such shape rather than about
     * the square a regular polygon would have given.
     *
     * [angles] labels the four corners in order; an empty string leaves one bare and "?" marks the
     * one being asked for. [highlightPair] picks out one opposite pair, 0 for the first and third
     * corners and 1 for the second and fourth.
     */
    data class CyclicQuad(
        val angles: List<String> = emptyList(),
        val highlightPair: Int? = null,
        override val reveal: Boolean = true,
    ) : LearnVisual

    /** A named solid, with its face/edge/corner counts when [counts] is set. */
    data class Solid(val kind: SolidKind, val counts: Boolean = false, override val reveal: Boolean = true) : LearnVisual

    /**
     * A flat shape none of the polygon figures can build: the two curved ones, and the star.
     *
     * [Polygon] draws a convex regular shape from a side count, which leaves the oval and the
     * half-circle with no corners to count and the star with the wrong ones. Each of these is a
     * shape a learner is expected to know by name, so the shape guide needs them drawn properly
     * rather than approximated by a many-sided polygon.
     */
    data class FlatShape(val kind: FlatShapeKind) : LearnVisual

    /** Regular polygon with its lines of symmetry folding in. */
    data class Symmetry(
        val sides: Int,
        val lines: Int,
        val rectangle: Boolean = false,
        override val reveal: Boolean = true,
    ) : LearnVisual

    /** Unit-square grid, labelled with its area, its perimeter, or both. */
    data class AreaGrid(
        val rows: Int,
        val cols: Int,
        val showArea: Boolean = true,
        val showPerimeter: Boolean = false,
        val unit: String = "cm",
        override val reveal: Boolean = true,
    ) : LearnVisual

    /**
     * Right triangle on legs [a] and [b]. [showSquares] grows the three squares of Pythagoras,
     * and [unknown] marks the side being solved for.
     */
    data class RightTriangle(
        val a: Int,
        val b: Int,
        val showSquares: Boolean = false,
        val angle: Int? = null,
        val unknown: Side? = null,
        val labels: Boolean = true,
    ) : LearnVisual

    /** Circle with whichever of its measurements the step is about. */
    data class CircleFigure(
        val radius: Int? = null,
        val showRadius: Boolean = true,
        val showDiameter: Boolean = false,
        val sweepCircumference: Boolean = false,
        val fillArea: Boolean = false,
        val centreAngle: Int? = null,
        override val reveal: Boolean = true,
    ) : LearnVisual

    /**
     * An angle sweeping open to [degrees], optionally with the partner that completes it.
     *
     * [supplement] draws the partner that makes a straight line and [wholeTurn] the one that makes
     * a full turn. Both arrive on [reveal], because on a question the partner is the answer.
     */
    data class AngleFigure(
        val degrees: Int,
        val supplement: Boolean = false,
        /**
         * Draw the turn the angle is part of as a complete circle behind it.
         *
         * A step asking what is left of a full turn has to show the whole turn. With only the
         * angle's own arc on the panel there is nothing on the screen for "the rest of it" to be
         * the rest of, and a 170 degree sweep looks like a half circle that has overshot.
         */
        val wholeTurn: Boolean = false,
        /**
         * Whether the angle writes its own reading beside itself.
         *
         * Off for a question that asks for exactly that number. "How many degrees is a right
         * angle?" was printing its own answer on the figure above the number pad: the reading is
         * a given on every other step, so no [reveal] setting could tell the two apart. The angle
         * is still drawn to its true size, which is the part the learner reads it off.
         */
        val labels: Boolean = true,
        override val reveal: Boolean = true,
    ) : LearnVisual

    /** An equation as a balance: [leftX] x-blocks plus [leftOnes] against [rightOnes]. */
    data class Balance(val leftX: Int, val leftOnes: Int, val rightOnes: Int, val remove: Int = 0) : LearnVisual

    // --- Data -----------------------------------------------------------------------------

    /** Bars that grow to their values, with an optional mean line. */
    data class BarChart(
        val values: List<Int>,
        val labels: List<BarLabel> = emptyList(),
        val highlight: Set<Int> = emptySet(),
        val showMean: Boolean = false,
        val gridStep: Int = 0,
        override val reveal: Boolean = true,
    ) : LearnVisual

    /** Pie slices sweeping out in order, labelled with their share. */
    data class PieChart(
        val shares: List<Int>,
        val labels: List<String> = emptyList(),
        override val reveal: Boolean = true,
    ) : LearnVisual

    /** Rows of symbols, halves included, each symbol worth [unitValue]. */
    data class Pictogram(
        val rows: List<Float>,
        val unitValue: Int,
        override val reveal: Boolean = true,
    ) : LearnVisual

    /** Bell curve with the band [shadeSd] standard deviations either side shaded. */
    data class NormalCurve(val shadeSd: Int = 1, val percent: String? = null) : LearnVisual

    /** Two overlapping sets, for conditional probability. */
    data class SetDiagram(
        val aOnly: Int,
        val both: Int,
        val bOnly: Int,
        val aLabel: String,
        val bLabel: String,
    ) : LearnVisual

    /** Tally marks in gates of five. */
    data class Tally(val count: Int, override val reveal: Boolean = true) : LearnVisual

    // --- Graphs ---------------------------------------------------------------------------

    /**
     * A curve drawn on axes, with optional marked points, tangent, or shaded area.
     *
     * [curve] is optional: a figure that is only about where its [points] sit - a translation, a
     * reflection - wants bare axes, because any line drawn through the origin reads as the mirror
     * the question is asking about.
     */
    data class Plot(
        val curve: Curve? = null,
        val second: Curve? = null,
        val points: List<PlotPoint> = emptyList(),
        val tangentAt: Float? = null,
        val areaTo: Float? = null,
        val markRoots: Boolean = false,
        val markVertex: Boolean = false,
    ) : LearnVisual

    /** Unit circle with the radius at [degrees] and its sine and cosine legs. */
    data class UnitCircleFigure(
        val degrees: Int,
        val showSin: Boolean = true,
        val showCos: Boolean = true,
        val label: String? = null,
        override val reveal: Boolean = true,
    ) : LearnVisual
}

/**
 * How many phases this figure plays through before it repeats. Almost every figure is a single
 * animation; the exceptions demonstrate a move and then its opposite.
 */
val LearnVisual.phaseCount: Int
    get() = when {
        this is LearnVisual.NumberLine && thenJump != null -> 2
        else -> 1
    }

/** The triangles a [LearnVisual.Triangle] can draw, sorted the way their lesson sorts them. */
enum class TriKind { EQUILATERAL, ISOSCELES, SCALENE }

/**
 * The quadrilaterals a [LearnVisual.Quadrilateral] can draw, each as its own shape rather than as
 * the special case a regular polygon would give.
 */
enum class QuadKind { SQUARE, RECTANGLE, RHOMBUS, PARALLELOGRAM, TRAPEZIUM, KITE }

/**
 * The solids a [LearnVisual.Solid] can draw.
 *
 * [PRISM] is drawn as a box, so it doubles as the cuboid; [TRIANGULAR_PRISM] is the one with
 * triangle ends, which is the shape "prism" is meant to call to mind once a learner has met both.
 */
enum class SolidKind { CUBE, SPHERE, CYLINDER, CONE, PRISM, TRIANGULAR_PRISM, PYRAMID }

/** The flat shapes a [LearnVisual.FlatShape] can draw. */
enum class FlatShapeKind { OVAL, SEMICIRCLE, STAR }

/**
 * What a bar on a [LearnVisual.BarChart] stands for.
 *
 * Named rather than written out, because the word under a bar is a word the app prints in its own
 * voice on a screen whose every other label is translated - so it comes out of the same table the
 * rest of the figure captions do rather than being authored in English on the figure.
 */
enum class BarLabel { BEFORE, AFTER, SCORE, TOTAL }

/** Which side of a right triangle a step is solving for. */
enum class Side { A, B, HYPOTENUSE }

/** The function a [LearnVisual.Plot] draws, over x in -3..3. */
@Immutable
sealed interface Curve {
    data class Linear(val m: Float, val c: Float = 0f) : Curve
    data class Quadratic(val a: Float = 1f, val b: Float = 0f, val c: Float = 0f) : Curve
    data class Exponential(val base: Float = 2f) : Curve
    data class Sine(val amplitude: Float = 1f, val frequency: Float = 1f, val cosine: Boolean = false) : Curve
    data object Logarithm : Curve
}

/** A point called out on a [LearnVisual.Plot]. */
@Immutable
data class PlotPoint(val x: Float, val y: Float, val label: String? = null)
