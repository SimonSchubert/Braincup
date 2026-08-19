package com.inspiredandroid.braincup.ui.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

object PrismFacet {
    val Tile: Dp = 8.dp
    val Card: Dp = 3.dp
    val Board: Dp = 6.dp
    val Preview: Dp = 4.dp
    val Cell: Dp = 2.dp
    val Dot: Dp = 1.5.dp
}

object PrismPress {
    val TileShift: Dp = 4.dp
    val BackShift: Dp = 3.dp
}

object PrismShade {
    const val Side = 0.7f
    const val Bottom = 0.5f
}

/**
 * The prism silhouette as a shape, for surfaces reached through `clip` / `border` / [Shapes]
 * rather than through [com.inspiredandroid.braincup.ui.components.PrismCard].
 *
 * Same outline the prism composables paint, minus the side and bottom facets: use this where a
 * surface is an outline or a tint and has no depth to extrude, and a PrismCard where it does.
 * [CornerBasedShape] rather than a plain Shape so it can also stand in for the Material3 shape
 * scale, which only accepts corner-based shapes.
 */
@Immutable
class PrismChamferShape(private val facet: CornerSize) :
    CornerBasedShape(
        topStart = ZeroCornerSize,
        topEnd = facet,
        bottomEnd = ZeroCornerSize,
        bottomStart = facet,
    ) {
    override fun createOutline(
        size: Size,
        topStart: Float,
        topEnd: Float,
        bottomEnd: Float,
        bottomStart: Float,
        layoutDirection: LayoutDirection,
    ): Outline {
        // layoutDirection is ignored on purpose: every prism composable forces LTR for its
        // geometry, so the chamfer has to stay top-right/bottom-left in RTL too, or a bordered
        // slot would bevel the opposite way from the card beside it.
        val f = maxOf(topEnd, bottomStart).coerceAtMost(size.minDimension / 2f)
        if (f <= 0f) return Outline.Rectangle(Rect(Offset.Zero, size))
        return Outline.Generic(
            Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width - f, 0f)
                lineTo(size.width, f)
                lineTo(size.width, size.height)
                lineTo(f, size.height)
                lineTo(0f, size.height - f)
                close()
            },
        )
    }

    override fun copy(
        topStart: CornerSize,
        topEnd: CornerSize,
        bottomEnd: CornerSize,
        bottomStart: CornerSize,
    ): CornerBasedShape = PrismChamferShape(topEnd)

    override fun equals(other: Any?): Boolean = other is PrismChamferShape && other.facet == facet

    override fun hashCode(): Int = facet.hashCode()

    override fun toString(): String = "PrismChamferShape(facet=$facet)"
}

fun PrismChamferShape(facet: Dp): PrismChamferShape = PrismChamferShape(CornerSize(facet))

val PrismSlot = PrismChamferShape(PrismFacet.Board)

/**
 * Material3 shape scale rebuilt on the prism chamfer, so the few components that still read
 * `MaterialTheme.shapes` (and the stock AlertDialog) bevel instead of rounding.
 */
val PrismShapes = Shapes(
    extraSmall = PrismChamferShape(PrismFacet.Cell),
    small = PrismChamferShape(PrismFacet.Card),
    medium = PrismChamferShape(PrismFacet.Preview),
    large = PrismChamferShape(PrismFacet.Board),
    extraLarge = PrismChamferShape(PrismFacet.Tile),
)

const val SlotBorderWidthDp = 2
