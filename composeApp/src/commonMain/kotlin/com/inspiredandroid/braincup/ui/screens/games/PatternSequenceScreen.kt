package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.PatternSequenceUiState
import com.inspiredandroid.braincup.games.PatternSequenceGame
import com.inspiredandroid.braincup.ui.components.*
import org.jetbrains.compose.resources.stringResource
import kotlin.random.Random

@Composable
internal fun ColumnScope.PatternSequenceContent(
    uiState: PatternSequenceUiState,
    onAnswer: (String) -> Unit,
) {
    val compact = LocalIsCompactHeight.current
    // A rule can govern figure size, so an option only reads correctly when it is drawn at the
    // exact scale the matrix draws it at: same tile size, and an inset that cancels the wider
    // tile bevel (see MatrixBoardParts).
    val cellSize = if (compact) 50.dp else 62.dp

    val matrix: @Composable () -> Unit = { MatrixBoard(uiState.matrix, cellSize) }
    val options: @Composable () -> Unit = {
        OptionBoard(
            optionRows = uiState.optionRows,
            optionColumns = uiState.optionColumns,
            cellSize = cellSize,
            onSelect = { index -> onAnswer(index.toString()) },
        )
    }

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
