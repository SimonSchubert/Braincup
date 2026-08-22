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

    /** Number line from [from] to [to]; when [jump] is set, a hop animates out of [start]. */
    data class NumberLine(
        val from: Int,
        val to: Int,
        val tickStep: Int = 1,
        val start: Int? = null,
        val jump: Int = 0,
        val hops: Int = 1,
        override val reveal: Boolean = true,
    ) : LearnVisual

    /** Base-ten rods and unit cubes. */
    data class PlaceValue(val tens: Int, val ones: Int, override val reveal: Boolean = true) : LearnVisual

    /** Hundred-square shaded to a decimal, optionally beside a second one to compare. */
    data class DecimalGrid(
        val value: Double,
        val compare: Double? = null,
        override val reveal: Boolean = true,
    ) : LearnVisual

    /** Rows x columns of dots, filled row by row: multiplication you can count. */
    data class ArrayDots(val rows: Int, val cols: Int, override val reveal: Boolean = true) : LearnVisual

    /** One fraction bar, or two stacked bars when [compare] is given. */
    data class Fraction(
        val numerator: Int,
        val denominator: Int,
        val compare: Pair<Int, Int>? = null,
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

    /** Terms with the step between them labelled, for sequences and skip counting. */
    data class Steps(val terms: List<Int>, val multiply: Boolean = false) : LearnVisual

    // --- Shape ----------------------------------------------------------------------------

    /** Regular polygon whose sides draw on one at a time and whose corners are counted. */
    data class Polygon(
        val sides: Int,
        val countCorners: Boolean = true,
        override val reveal: Boolean = true,
    ) : LearnVisual

    /** A named solid, with its face/edge/corner counts when [counts] is set. */
    data class Solid(val kind: SolidKind, val counts: Boolean = false, override val reveal: Boolean = true) : LearnVisual

    /** Regular polygon with its lines of symmetry folding in. */
    data class Symmetry(
        val sides: Int,
        val lines: Int,
        val rectangle: Boolean = false,
        override val reveal: Boolean = true,
    ) : LearnVisual

    /** Unit-square grid, labelled with its area, its perimeter, or both. */
    data class AreaGrid(
        val cols: Int,
        val rows: Int,
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

    /** An angle sweeping open to [degrees], optionally with its partner on a straight line. */
    data class AngleFigure(
        val degrees: Int,
        val supplement: Boolean = false,
        override val reveal: Boolean = true,
    ) : LearnVisual

    /** An equation as a balance: [leftX] x-blocks plus [leftOnes] against [rightOnes]. */
    data class Balance(val leftX: Int, val leftOnes: Int, val rightOnes: Int, val remove: Int = 0) : LearnVisual

    // --- Data -----------------------------------------------------------------------------

    /** Bars that grow to their values, with an optional mean line. */
    data class BarChart(
        val values: List<Int>,
        val labels: List<String> = emptyList(),
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

    /** A curve drawn on axes, with optional marked points, tangent, or shaded area. */
    data class Plot(
        val curve: Curve,
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

enum class SolidKind { CUBE, SPHERE, CYLINDER, CONE, PRISM }

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
