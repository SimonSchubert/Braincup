package com.inspiredandroid.braincup.ui.screens.iqtest

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.IqTestPlayUiState
import com.inspiredandroid.braincup.app.MatrixOptionCell
import com.inspiredandroid.braincup.games.iqtest.IqTest
import com.inspiredandroid.braincup.games.iqtest.IqTestBlueprint
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.screens.games.CompactGameRow
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.ScreenPreviewHost
import com.inspiredandroid.braincup.ui.theme.ContentMaxWidth
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.numeric
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource

@Composable
fun IqTestPlayScreen(
    uiState: IqTestPlayUiState,
    timeRemainingMillis: Long,
    onSelect: (Int) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onFinish: () -> Unit,
    onRequestQuit: () -> Unit,
) {
    AppScaffold(
        title = null,
        onBack = onRequestQuit,
        scrollable = true,
        provideCompactHeight = true,
        actions = {
            Text(
                text = formatDuration((timeRemainingMillis / 1000).toInt()),
                style = MaterialTheme.typography.titleMedium.numeric(),
                color = if (timeRemainingMillis <= LowTimeMillis) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.padding(end = 16.dp),
            )
        },
    ) {
        // Read inside the scaffold body: AppScaffold only provides this around its content.
        val compact = LocalIsCompactHeight.current

        Text(
            text = stringResource(
                Res.string.iq_test_item_counter,
                uiState.itemIndex + 1,
                uiState.itemCount,
            ),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(Modifier.height(8.dp))

        PrismProgressBar(
            progress = (uiState.itemIndex + 1) / uiState.itemCount.toFloat(),
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            fillColor = Primary,
            modifier = Modifier
                .widthIn(max = ContentMaxWidth)
                .padding(horizontal = 24.dp)
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .height(8.dp),
        )

        Spacer(Modifier.height(if (compact) 12.dp else 20.dp))

        MatrixBoardLayout(uiState.optionColumns) { cellSize ->
            val matrix: @Composable () -> Unit = { MatrixBoard(uiState.matrix, cellSize) }
            val options: @Composable () -> Unit = {
                OptionBoard(
                    optionRows = uiState.optionRows,
                    optionColumns = uiState.optionColumns,
                    cellSize = cellSize,
                    onSelect = onSelect,
                    selectedIndex = uiState.selectedOption,
                )
            }

            if (compact) {
                CompactGameRow {
                    matrix()
                    options()
                }
            } else {
                Box(Modifier.align(Alignment.CenterHorizontally)) { matrix() }
                Spacer(Modifier.height(20.dp))
                Box(Modifier.align(Alignment.CenterHorizontally)) { options() }
            }
        }

        Spacer(Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            TextPrismButton(
                onClick = onPrevious,
                value = stringResource(Res.string.iq_test_previous),
                isClickable = uiState.itemIndex > 0,
            )
            if (uiState.isOnLastItem) {
                DefaultButton(
                    onClick = onFinish,
                    value = stringResource(Res.string.iq_test_finish),
                )
            } else {
                TextPrismButton(
                    onClick = onNext,
                    value = if (uiState.selectedOption == null) {
                        stringResource(Res.string.iq_test_skip)
                    } else {
                        stringResource(Res.string.iq_test_next)
                    },
                )
            }
        }

        if (!uiState.isOnLastItem) {
            Spacer(Modifier.height(12.dp))
            TextPrismButton(
                onClick = onFinish,
                value = stringResource(Res.string.iq_test_finish_now),
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

private const val LowTimeMillis = 60_000L

@DevicePreviews
@Composable
private fun IqTestPlayScreenPreview() {
    val uiState = remember { previewPlayUiState(itemIndex = 17, selectedOption = 2) }
    ScreenPreviewHost {
        IqTestPlayScreen(
            uiState = uiState,
            timeRemainingMillis = 9 * 60_000L + 12_000L,
            onSelect = {},
            onPrevious = {},
            onNext = {},
            onFinish = {},
            onRequestQuit = {},
        )
    }
}

internal fun previewPlayUiState(itemIndex: Int, selectedOption: Int?): IqTestPlayUiState {
    val test = IqTest(seed = 42L)
    test.goTo(itemIndex)
    val problem = test.problemAt(itemIndex)
    return IqTestPlayUiState(
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
        selectedOption = selectedOption,
        isOnLastItem = itemIndex == IqTestBlueprint.ITEM_COUNT - 1,
    )
}
