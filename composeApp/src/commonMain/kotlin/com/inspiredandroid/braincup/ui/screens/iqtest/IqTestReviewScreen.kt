package com.inspiredandroid.braincup.ui.screens.iqtest

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.FigureCellState
import com.inspiredandroid.braincup.app.IqTestReviewItemUiState
import com.inspiredandroid.braincup.app.MatrixOptionCell
import com.inspiredandroid.braincup.games.iqtest.IqTest
import com.inspiredandroid.braincup.games.iqtest.IqTestBlueprint
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.LocalIsCompactHeight
import com.inspiredandroid.braincup.ui.components.MatrixBoard
import com.inspiredandroid.braincup.ui.components.OptionBoard
import com.inspiredandroid.braincup.ui.components.TextPrismButton
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.ScreenPreviewHost
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource

@Composable
fun IqTestReviewScreen(
    uiState: IqTestReviewItemUiState,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
) {
    val compact = LocalIsCompactHeight.current
    val cellSize = if (compact) 44.dp else 58.dp

    // Marking happens here rather than in the controller so the review board is built from the same
    // FeedbackCell states the mini-game uses after a wrong answer.
    val markedOptions = remember(uiState) {
        uiState.optionRows.mapIndexed { row, cells ->
            cells.mapIndexed { column, cell ->
                val index = row * uiState.optionColumns + column
                cell.withState(
                    when (index) {
                        uiState.correctOption -> FigureCellState.CORRECT
                        uiState.pickedOption -> FigureCellState.WRONG
                        else -> FigureCellState.DIMMED
                    },
                )
            }.toImmutableList()
        }.toImmutableList()
    }

    AppScaffold(
        title = stringResource(Res.string.iq_test_review_title),
        onBack = onBack,
        scrollable = true,
        provideCompactHeight = true,
    ) {
        Text(
            text = stringResource(Res.string.iq_test_item_counter, uiState.itemIndex + 1, uiState.itemCount) +
                "  ·  " +
                stringResource(Res.string.iq_test_difficulty_level, uiState.tier + 1),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = when {
                uiState.pickedOption == null -> stringResource(Res.string.iq_test_review_skipped)
                uiState.pickedOption == uiState.correctOption -> stringResource(Res.string.iq_test_review_correct)
                else -> stringResource(Res.string.iq_test_review_wrong)
            },
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 24.dp),
        )

        Spacer(Modifier.height(if (compact) 12.dp else 20.dp))

        Box(Modifier.align(Alignment.CenterHorizontally)) {
            MatrixBoard(uiState.matrix, cellSize)
        }

        Spacer(Modifier.height(20.dp))

        Box(Modifier.align(Alignment.CenterHorizontally)) {
            OptionBoard(
                optionRows = markedOptions,
                optionColumns = uiState.optionColumns,
                cellSize = cellSize,
                onSelect = {},
            )
        }

        Spacer(Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            TextPrismButton(
                onClick = onPrevious,
                value = stringResource(Res.string.iq_test_previous),
                isClickable = uiState.itemIndex > 0,
            )
            TextPrismButton(
                onClick = onNext,
                value = stringResource(Res.string.iq_test_next),
                isClickable = uiState.itemIndex < uiState.itemCount - 1,
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

@DevicePreviews
@Composable
private fun IqTestReviewScreenPreview() {
    val uiState = remember { previewReviewUiState(itemIndex = 14, pickedOption = 1) }
    ScreenPreviewHost {
        IqTestReviewScreen(uiState = uiState, onPrevious = {}, onNext = {}, onBack = {})
    }
}

internal fun previewReviewUiState(itemIndex: Int, pickedOption: Int?): IqTestReviewItemUiState {
    val test = IqTest(seed = 42L)
    val problem = test.problemAt(itemIndex)
    return IqTestReviewItemUiState(
        itemIndex = itemIndex,
        itemCount = IqTestBlueprint.ITEM_COUNT,
        matrix = problem.matrix
            .mapIndexed { index, panel -> panel.takeIf { index != 8 } }
            .toImmutableList(),
        optionRows = problem.options
            .map { MatrixOptionCell(it) }
            .chunked(problem.optionColumns)
            .map { it.toImmutableList() }
            .toImmutableList(),
        optionColumns = problem.optionColumns,
        pickedOption = pickedOption,
        correctOption = problem.correctOptionIndex,
        tier = IqTestBlueprint.tierFor(itemIndex),
    )
}
