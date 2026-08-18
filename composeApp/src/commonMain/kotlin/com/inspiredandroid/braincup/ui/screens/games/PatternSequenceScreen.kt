package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.FigureCellState
import com.inspiredandroid.braincup.app.MatrixOptionCell
import com.inspiredandroid.braincup.app.PatternSequenceUiState
import com.inspiredandroid.braincup.games.PatternSequenceGame
import com.inspiredandroid.braincup.games.matrix.MatrixPanel
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.theme.OnPrimaryContainer
import com.inspiredandroid.braincup.ui.theme.PrimaryContainer
import com.inspiredandroid.braincup.ui.theme.PrismFacet
import com.inspiredandroid.braincup.ui.theme.SuccessGreenSoft
import org.jetbrains.compose.resources.stringResource
import kotlin.random.Random

@Composable
internal fun ColumnScope.PatternSequenceContent(
    uiState: PatternSequenceUiState,
    onAnswer: (String) -> Unit,
) {
    val compact = LocalIsCompactHeight.current
    val matrixCell = if (compact) 50.dp else 62.dp
    val optionCell = if (compact) 44.dp else 54.dp

    val matrix: @Composable () -> Unit = { MatrixBoard(uiState, matrixCell) }
    val options: @Composable () -> Unit = { OptionBoard(uiState, optionCell, onAnswer) }

    if (compact) {
        CompactGameRow {
            matrix()
            options()
        }
        return
    }

    Text(
        text = stringResource(Res.string.pattern_sequence_prompt),
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier.align(Alignment.CenterHorizontally).padding(horizontal = 24.dp),
    )
    Spacer(Modifier.height(16.dp))
    Box(Modifier.align(Alignment.CenterHorizontally)) { matrix() }
    Spacer(Modifier.height(24.dp))
    Box(Modifier.align(Alignment.CenterHorizontally)) { options() }
}

@Composable
private fun MatrixBoard(
    uiState: PatternSequenceUiState,
    cellSize: Dp,
) {
    Column(verticalArrangement = Arrangement.spacedBy(MatrixGap)) {
        repeat(3) { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(MatrixGap)) {
                repeat(3) { column ->
                    MatrixCell(
                        panel = uiState.matrix[row * 3 + column],
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

@Composable
private fun OptionBoard(
    uiState: PatternSequenceUiState,
    cellSize: Dp,
    onAnswer: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(OptionGap)) {
        uiState.optionRows.forEachIndexed { row, cells ->
            Row(horizontalArrangement = Arrangement.spacedBy(OptionGap)) {
                cells.forEachIndexed { column, cell ->
                    val index = row * uiState.optionColumns + column
                    MatrixOptionTile(
                        cell = cell,
                        onClick = { onAnswer(index.toString()) },
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val face = when (cell.state) {
        FigureCellState.WRONG -> MaterialTheme.colorScheme.errorContainer
        FigureCellState.CORRECT -> SuccessGreenSoft
        else -> MaterialTheme.colorScheme.surfaceContainer
    }
    PrismTile(
        face = face,
        modifier = if (cell.state == FigureCellState.DIMMED) modifier.alpha(0.3f) else modifier,
        isClickable = cell.state == FigureCellState.NORMAL,
        isSelected = cell.state == FigureCellState.DIMMED,
        onClick = onClick,
    ) {
        MatrixPanelCanvas(panel = cell.panel, modifier = Modifier.fillMaxSize().padding(PanelInset))
    }
}

private val MatrixGap = 4.dp
private val OptionGap = 6.dp
private val PanelInset = 6.dp

@DevicePreviews
@Composable
private fun PatternSequenceContentPreview() {
    val uiState = remember {
        PatternSequenceGame(Random(7L)).apply {
            round = 6
            nextRound()
        }.toUiState() as PatternSequenceUiState
    }
    GamePreviewHost {
        PatternSequenceContent(uiState = uiState, onAnswer = {})
    }
}
