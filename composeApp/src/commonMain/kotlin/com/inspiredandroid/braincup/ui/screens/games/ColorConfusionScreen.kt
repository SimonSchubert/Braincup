package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.games.tools.composeColor
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.localizedName
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.SelectedTileFaceDark
import com.inspiredandroid.braincup.ui.theme.SelectedTileFaceLight
import com.inspiredandroid.braincup.ui.theme.SuccessGreenSoft
import com.inspiredandroid.braincup.ui.theme.UnselectedTileFaceDark
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.graphics.Color as ComposeColor

@Composable
internal fun ColumnScope.ColorConfusionContent(
    uiState: ColorConfusionUiState,
    onAnswer: (String) -> Unit,
) {
    val compact = LocalIsCompactHeight.current
    val cellMax = if (compact) 72.dp else 100.dp

    // 3x3 Grid
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .widthIn(max = cellMax * 3)
            .align(Alignment.CenterHorizontally),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        uiState.cells.chunked(3).forEachIndexed { y, rowCells ->
            Row {
                rowCells.forEachIndexed { x, cell ->
                    val index = y * 3 + x
                    ColorConfusionCell(
                        cell = cell,
                        onClick = {
                            if (!uiState.isSubmitted) {
                                onAnswer(index.toString())
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(4.dp),
                    )
                }
            }
        }
    }

    Spacer(Modifier.height(if (compact) 8.dp else 16.dp))

    PrismTile(
        face = Primary,
        isClickable = !uiState.isSubmitted,
        onClick = { onAnswer("submit") },
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .defaultMinSize(minWidth = 96.dp, minHeight = 48.dp)
            .alpha(if (uiState.isSubmitted) 0f else 1f)
            .hoverHand(),
    ) {
        Text(
            stringResource(Res.string.button_done),
            color = ComposeColor.White,
        )
    }
}

@Composable
private fun ColorConfusionCell(
    cell: ColorConfusionUiState.Cell,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDark = isSystemInDarkTheme()
    val selectedFace = if (isDark) SelectedTileFaceDark else SelectedTileFaceLight
    val unselectedFace = if (isDark) UnselectedTileFaceDark else MaterialTheme.colorScheme.surfaceContainer
    val targetContainerColor = when {
        cell.feedback == ColorConfusionUiState.CellFeedback.CORRECT_SELECTED -> SuccessGreenSoft
        cell.feedback == ColorConfusionUiState.CellFeedback.WRONG_SELECTED -> MaterialTheme.colorScheme.errorContainer
        cell.feedback == ColorConfusionUiState.CellFeedback.MISSED -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
        cell.isSelected -> selectedFace
        else -> unselectedFace
    }
    val containerColor by animateColorAsState(
        targetValue = targetContainerColor,
        animationSpec = tween(250),
        label = "colorConfusionContainer",
    )

    val isInteractive = cell.feedback == ColorConfusionUiState.CellFeedback.NONE
    // Only sink the tile for finalized feedback states. Sinking the mid-play selection would dim
    // the bright selected face to its side color and erase the very contrast that distinguishes
    // selected from unselected in bright sunlight.
    val isLockedIn = cell.feedback == ColorConfusionUiState.CellFeedback.CORRECT_SELECTED ||
        cell.feedback == ColorConfusionUiState.CellFeedback.WRONG_SELECTED
    PrismTile(
        face = containerColor,
        modifier = modifier.hoverHand(isInteractive),
        isClickable = isInteractive,
        isSelected = isLockedIn,
        onClick = onClick,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                text = cell.word.localizedName(),
                style = MaterialTheme.typography.titleMedium,
                color = cell.fontColor.composeColor(),
            )
        }
    }
}

@GameDevicePreviews
@Composable
private fun ColorConfusionContentPreview() {
    GamePreviewHost {
        ColorConfusionContent(
            uiState = ColorConfusionUiState(
                cells = persistentListOf(
                    ColorConfusionUiState.Cell(Color.RED, Color.BLUE, false, ColorConfusionUiState.CellFeedback.NONE),
                    ColorConfusionUiState.Cell(Color.GREEN, Color.GREEN, true, ColorConfusionUiState.CellFeedback.NONE),
                    ColorConfusionUiState.Cell(Color.BLUE, Color.RED, false, ColorConfusionUiState.CellFeedback.NONE),
                    ColorConfusionUiState.Cell(Color.YELLOW, Color.YELLOW, false, ColorConfusionUiState.CellFeedback.NONE),
                    ColorConfusionUiState.Cell(Color.BLUE, Color.RED, false, ColorConfusionUiState.CellFeedback.NONE),
                    ColorConfusionUiState.Cell(Color.BLUE, Color.RED, false, ColorConfusionUiState.CellFeedback.NONE),
                    ColorConfusionUiState.Cell(Color.YELLOW, Color.YELLOW, false, ColorConfusionUiState.CellFeedback.NONE),
                    ColorConfusionUiState.Cell(Color.BLUE, Color.RED, false, ColorConfusionUiState.CellFeedback.NONE),
                    ColorConfusionUiState.Cell(Color.BLUE, Color.RED, false, ColorConfusionUiState.CellFeedback.NONE),
                ),
                isSubmitted = false,
            ),
            onAnswer = {},
        )
    }
}
