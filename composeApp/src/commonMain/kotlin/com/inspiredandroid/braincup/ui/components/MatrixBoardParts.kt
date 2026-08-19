package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.app.FigureCellState
import com.inspiredandroid.braincup.app.MatrixOptionCell
import com.inspiredandroid.braincup.games.matrix.MatrixPanel
import com.inspiredandroid.braincup.ui.theme.OnPrimaryContainer
import com.inspiredandroid.braincup.ui.theme.PrimaryContainer
import com.inspiredandroid.braincup.ui.theme.PrismFacet
import com.inspiredandroid.braincup.ui.theme.SuccessGreenSoft
import kotlinx.collections.immutable.ImmutableList

/**
 * Rendering for a 3x3 matrix item and its option set, shared by the Pattern Sequence mini-game and
 * the IQ test. Both draw the same artwork from the same [MatrixPanel]s, so the parts take plain
 * panel data rather than either mode's UI state.
 */
/**
 * Measures the width both boards have to share and hands down the one cell size they must agree on.
 *
 * A rule can govern figure size, so an option only reads correctly when it is drawn at exactly the
 * scale the matrix draws it at. That rules out sizing the two boards independently, even though the
 * option board can want more columns than the matrix's three.
 */
@Composable
internal fun MatrixBoardLayout(
    optionColumns: Int,
    content: @Composable ColumnScope.(cellSize: Dp) -> Unit,
) {
    val compact = LocalIsCompactHeight.current
    val maxCell = gridCellMaxSize
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val cellSize = if (compact) {
            // CompactGameRow sets the two boards side by side, so every column is on one line.
            val usable = maxWidth - CompactRowPadding * 2 - CompactRowSpacing
            val gaps = MatrixGap * 2 + OptionGap * (optionColumns - 1)
            (usable - gaps) / (3 + optionColumns)
        } else {
            val usable = maxWidth - BoardMargin * 2
            minOf(
                (usable - MatrixGap * 2) / 3,
                (usable - OptionGap * (optionColumns - 1)) / optionColumns,
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            content(cellSize.coerceIn(MinCellSize, maxCell))
        }
    }
}

@Composable
internal fun MatrixBoard(
    matrix: ImmutableList<MatrixPanel?>,
    cellSize: Dp,
) {
    Column(verticalArrangement = Arrangement.spacedBy(MatrixGap)) {
        repeat(3) { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(MatrixGap)) {
                repeat(3) { column ->
                    MatrixCell(
                        panel = matrix[row * 3 + column],
                        modifier = Modifier.size(cellSize),
                    )
                }
            }
        }
    }
}

@Composable
private fun MatrixCell(
    panel: MatrixPanel?,
    modifier: Modifier = Modifier,
) {
    if (panel == null) {
        PrismCard(face = PrimaryContainer, facet = PrismFacet.Cell, modifier = modifier) {
            Text(
                text = "?",
                style = MaterialTheme.typography.headlineMedium,
                color = OnPrimaryContainer,
            )
        }
        return
    }
    PrismCard(
        face = MaterialTheme.colorScheme.surfaceContainer,
        facet = PrismFacet.Cell,
        modifier = modifier,
    ) {
        MatrixPanelCanvas(panel = panel, modifier = Modifier.fillMaxSize().padding(PanelInset))
    }
}

/**
 * [selectedIndex] marks a pick that has not been judged yet, which only the IQ test needs: it
 * withholds correctness until the whole test is scored, where the mini-game answers immediately.
 */
@Composable
internal fun OptionBoard(
    optionRows: ImmutableList<ImmutableList<MatrixOptionCell>>,
    optionColumns: Int,
    cellSize: Dp,
    onSelect: (Int) -> Unit,
    selectedIndex: Int? = null,
) {
    Column(verticalArrangement = Arrangement.spacedBy(OptionGap)) {
        optionRows.forEachIndexed { row, cells ->
            Row(horizontalArrangement = Arrangement.spacedBy(OptionGap)) {
                cells.forEachIndexed { column, cell ->
                    val index = row * optionColumns + column
                    MatrixOptionTile(
                        cell = cell,
                        isSelected = index == selectedIndex,
                        onClick = { onSelect(index) },
                        modifier = Modifier.size(cellSize),
                    )
                }
            }
        }
    }
}

@Composable
private fun MatrixOptionTile(
    cell: MatrixOptionCell,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val face = when {
        cell.state == FigureCellState.WRONG -> MaterialTheme.colorScheme.errorContainer
        cell.state == FigureCellState.CORRECT -> SuccessGreenSoft
        isSelected -> PrimaryContainer
        else -> MaterialTheme.colorScheme.surfaceContainer
    }
    PrismTile(
        face = face,
        // A sunken tile normally paints itself in its own side colour, which would mute the pick to
        // grey. Pinning the side to the face keeps it recognisably the colour of the empty slot it
        // is destined for, while the tile still presses in.
        side = if (isSelected) face else null,
        modifier = if (cell.state == FigureCellState.DIMMED) modifier.alpha(0.3f) else modifier,
        isClickable = cell.state == FigureCellState.NORMAL,
        isSelected = cell.state == FigureCellState.DIMMED || isSelected,
        onClick = onClick,
    ) {
        MatrixPanelCanvas(panel = cell.panel, modifier = Modifier.fillMaxSize().padding(OptionPanelInset))
    }
}

private val MatrixGap = 4.dp
private val OptionGap = 6.dp
private val PanelInset = 6.dp

/** Breathing room left either side of a stacked board, matching the other game screens. */
private val BoardMargin = 24.dp

/** Never shrink past this; the screens scroll, so a board may run wider than a very narrow phone. */
private val MinCellSize = 36.dp

// Mirrors what CompactGameRow puts around and between its children.
private val CompactRowPadding = 16.dp
private val CompactRowSpacing = 24.dp

/**
 * An option is a [PrismTile] while a matrix entry is a [PrismCard], and the tile's bevel eats more
 * of its box, so the option pads less to leave a drawing area of exactly the matrix cell's size.
 */
private val OptionPanelInset = PanelInset - (PrismFacet.Tile - PrismFacet.Cell) / 2
