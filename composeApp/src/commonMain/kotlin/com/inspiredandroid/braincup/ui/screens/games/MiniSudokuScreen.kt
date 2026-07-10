package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import com.inspiredandroid.braincup.ui.theme.numberFontFamily
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource

private val SudokuOuterFrame = 4.dp

private val SudokuBlockSeparator = 4.dp

private val SudokuCellSeparator = 2.dp

private val SudokuCellSize = 48.dp

private val SudokuDigitPadButtonSize = 56.dp

@Composable
internal fun ColumnScope.MiniSudokuContent(
    uiState: MiniSudokuUiState,
    onAnswer: (String) -> Unit,
) {
    val showingSolution = uiState.solutionValues != null

    // Reset state each round — grid size can grow 4→6 mid-session, so previous inputs
    // would index out of bounds on the new grid.
    var inputs by remember(uiState) { mutableStateOf(uiState.initialValues) }
    var selectedIndex by remember(uiState) {
        mutableStateOf(uiState.initialValues.indexOfFirst { it == null }.coerceAtLeast(0))
    }

    val onDigit: (Int) -> Unit = { digit ->
        if (selectedIndex in inputs.indices && uiState.initialValues[selectedIndex] == null) {
            val updated = inputs.toMutableList().apply { this[selectedIndex] = digit }
            inputs = updated.toImmutableList()
            if (updated.all { it != null }) {
                onAnswer(updated.joinToString(",") { it.toString() })
            } else {
                selectedIndex = nextEmptyCell(selectedIndex, updated, uiState.initialValues)
            }
        }
    }

    if (LocalIsCompactHeight.current) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SudokuGrid(
                uiState = uiState,
                inputs = inputs,
                selectedIndex = selectedIndex,
                showingSolution = showingSolution,
                onCellClick = { selectedIndex = it },
            )
            SudokuDigitPadGrid(
                gridSize = uiState.gridSize,
                columns = 2,
                enabled = !showingSolution,
                onDigit = onDigit,
            )
        }
    } else {
        Text(
            text = stringResource(Res.string.game_sudoku_instruction),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(16.dp))

        SudokuGrid(
            uiState = uiState,
            inputs = inputs,
            selectedIndex = selectedIndex,
            showingSolution = showingSolution,
            onCellClick = { selectedIndex = it },
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(Modifier.height(24.dp))

        SudokuDigitPad(
            gridSize = uiState.gridSize,
            enabled = !showingSolution,
            onDigit = onDigit,
        )
    }
}

@Composable
private fun SudokuDigitPadGrid(
    gridSize: Int,
    columns: Int,
    enabled: Boolean,
    onDigit: (Int) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        (1..gridSize).chunked(columns).forEach { rowDigits ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                rowDigits.forEach { digit ->
                    PrismTile(
                        face = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier
                            .size(SudokuDigitPadButtonSize)
                            .hoverHand(enabled)
                            .alpha(if (enabled) 1f else 0.6f),
                        isClickable = enabled,
                        onClick = { onDigit(digit) },
                    ) {
                        Text(
                            text = digit.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontFamily = numberFontFamily(),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            }
        }
    }
}

private fun nextEmptyCell(from: Int, inputs: List<Int?>, initialValues: List<Int?>): Int {
    val total = inputs.size
    for (step in 1..total) {
        val idx = (from + step) % total
        if (initialValues[idx] == null && inputs[idx] == null) return idx
    }
    return from
}

@Composable
private fun SudokuGrid(
    uiState: MiniSudokuUiState,
    inputs: ImmutableList<Int?>,
    selectedIndex: Int,
    showingSolution: Boolean,
    onCellClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val n = uiState.gridSize
    val gridLineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)

    Box(modifier = modifier.background(gridLineColor)) {
        Column(modifier = Modifier.padding(SudokuOuterFrame)) {
            for (row in 0 until n) {
                Row {
                    for (col in 0 until n) {
                        val index = row * n + col
                        val isClue = uiState.initialValues[index] != null
                        val isSolution = showingSolution && !isClue
                        val value = when {
                            isSolution -> uiState.solutionValues!![index]
                            else -> inputs[index]
                        }
                        SudokuCell(
                            value = value?.toString().orEmpty(),
                            isClue = isClue,
                            isSelected = index == selectedIndex && !showingSolution,
                            isSolution = isSolution,
                            onClick = { onCellClick(index) },
                            modifier = Modifier.padding(
                                end = gapAfter(col, n, uiState.blockCols),
                                bottom = gapAfter(row, n, uiState.blockRows),
                            ),
                        )
                    }
                }
            }
        }
    }
}

private fun gapAfter(index: Int, total: Int, blockSize: Int): androidx.compose.ui.unit.Dp = when {
    index == total - 1 -> 0.dp
    (index + 1) % blockSize == 0 -> SudokuBlockSeparator
    else -> SudokuCellSeparator
}

@Composable
private fun SudokuCell(
    value: String,
    isClue: Boolean,
    isSelected: Boolean,
    isSolution: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isInteractive = !isClue && !isSolution
    val containerColor = when {
        isSolution -> SuccessGreen.copy(alpha = 0.22f)
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }
    PrismTile(
        face = containerColor,
        isClickable = isInteractive,
        isSelected = isSelected,
        modifier = modifier
            .size(SudokuCellSize)
            .hoverHand(isInteractive),
        onClick = onClick,
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontFamily = numberFontFamily(),
            fontWeight = FontWeight.Bold,
            color = if (isSolution) SuccessGreen else MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ColumnScope.SudokuDigitPad(
    gridSize: Int,
    enabled: Boolean,
    onDigit: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.align(Alignment.CenterHorizontally),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (digit in 1..gridSize) {
            PrismTile(
                face = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .size(SudokuDigitPadButtonSize)
                    .hoverHand(enabled)
                    .alpha(if (enabled) 1f else 0.6f),
                isClickable = enabled,
                onClick = { onDigit(digit) },
            ) {
                Text(
                    text = digit.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = numberFontFamily(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun MiniSudokuContentPreview() {
    GamePreviewHost {
        MiniSudokuContent(
            uiState = MiniSudokuUiState(
                gridSize = 4,
                blockRows = 2,
                blockCols = 2,
                initialValues = persistentListOf(
                    1, null, null, 4,
                    null, 3, 2, null,
                    null, 4, 1, null,
                    2, null, null, 3,
                ),
            ),
            onAnswer = {},
        )
    }
}
