package com.inspiredandroid.braincup.games.matrix

import androidx.compose.runtime.Immutable
import com.inspiredandroid.braincup.games.tools.GameColor
import com.inspiredandroid.braincup.games.tools.Shape
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

enum class MatrixAttribute { TYPE, COLOR, ROTATION, SIZE, NUMBER, POSITION }

enum class MatrixRule { CONSTANT, PROGRESSION, ARITHMETIC, DISTRIBUTE_THREE }

/** Slot value for the lone entity of a panel whose position carries no information. */
const val CENTERED_SLOT = -1

/** Number of scale steps [MatrixEntity.sizeStep] can take. */
const val SIZE_STEPS = 4

/**
 * Scale per size step, as a fraction of the slot the entity is drawn in. Uniform sqrt(2) ratios,
 * so each step doubles in area and no pair is harder to tell apart than any other.
 */
val SIZE_SCALES = floatArrayOf(0.36f, 0.5f, 0.71f, 1f)

/**
 * Size steps a panel may use once it renders on the 2x2 sub-grid, where every slot is half as wide
 * and adjacent steps would land only a few dp apart. Skipping step 1 keeps the three values a rule
 * picks far enough apart to still read at that scale.
 */
val SPREAD_SIZE_STEPS = listOf(0, 2, 3)

/** Largest entity count a panel can hold, bounded by the 2x2 sub-grid. */
const val MAX_ENTITIES = 4

/**
 * Integer encoding of one panel, and the generator's source of truth.
 *
 * Every attribute a matrix governs is visually distinguishable by construction, so two specs
 * that differ always render differently. That is what lets the option set be validated on specs
 * alone instead of on rendered pixels. Size is the one attribute where that holds only because of
 * how far apart [SIZE_SCALES] spaces the steps, and because a panel on the sub-grid draws from
 * [SPREAD_SIZE_STEPS] rather than from every step; MatrixGeneratorTest pins both.
 */
@Immutable
data class MatrixPanelSpec(
    val typeIndex: Int,
    val colorIndex: Int,
    /** Quarter turns clockwise, 0..3. */
    val rotationStep: Int,
    val sizeStep: Int,
    val number: Int,
    /** Bits over the 2x2 sub-grid; its bit count always equals [number]. */
    val positionMask: Int,
) {
    fun valueOf(attribute: MatrixAttribute): Int = when (attribute) {
        MatrixAttribute.TYPE -> typeIndex
        MatrixAttribute.COLOR -> colorIndex
        MatrixAttribute.ROTATION -> rotationStep
        MatrixAttribute.SIZE -> sizeStep
        MatrixAttribute.NUMBER -> number
        MatrixAttribute.POSITION -> positionMask
    }

    fun withValue(attribute: MatrixAttribute, value: Int): MatrixPanelSpec = when (attribute) {
        MatrixAttribute.TYPE -> copy(typeIndex = value)
        MatrixAttribute.COLOR -> copy(colorIndex = value)
        MatrixAttribute.ROTATION -> copy(rotationStep = value)
        MatrixAttribute.SIZE -> copy(sizeStep = value)
        MatrixAttribute.NUMBER -> copy(number = value, positionMask = canonicalMask(value))
        MatrixAttribute.POSITION -> copy(positionMask = value, number = value.countOneBits())
    }

    companion object {
        /** Fill order used when a panel's positions carry no information: TL, TR, BL, BR. */
        fun canonicalMask(number: Int): Int = (1 shl number) - 1
    }
}

@Immutable
data class MatrixEntity(
    val shape: Shape,
    val color: GameColor,
    /** Degrees clockwise, always a multiple of 90. */
    val rotation: Int,
    val sizeStep: Int,
    /** Index into the 2x2 sub-grid (0 = top left, 3 = bottom right), or [CENTERED_SLOT]. */
    val slot: Int,
)

@Immutable
data class MatrixPanel(
    val entities: ImmutableList<MatrixEntity>,
    val spec: MatrixPanelSpec,
)

@Immutable
data class MatrixProblem(
    /** Nine panels in reading order. Index 8 is the answer and is never shown on the board. */
    val matrix: ImmutableList<MatrixPanel>,
    val options: ImmutableList<MatrixPanel>,
    val correctOptionIndex: Int,
    val optionColumns: Int,
    /** The rule each governed attribute follows along the rows. Anything absent is held constant. */
    val rules: ImmutableMap<MatrixAttribute, MatrixRule>,
    val ruleSummary: String,
) {
    val answer: MatrixPanel get() = matrix[8]

    val governedAttributes: Set<MatrixAttribute> get() = rules.keys
}
