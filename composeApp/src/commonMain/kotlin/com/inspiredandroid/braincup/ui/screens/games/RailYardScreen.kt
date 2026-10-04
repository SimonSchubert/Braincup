package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.game_rail_yard_howto
import com.inspiredandroid.braincup.app.BoardCommand
import com.inspiredandroid.braincup.app.RailYardUiState
import com.inspiredandroid.braincup.games.RailYardGame
import com.inspiredandroid.braincup.ui.components.LocalIsCompactHeight
import com.inspiredandroid.braincup.ui.components.LocalScaffoldBodyHeight
import com.inspiredandroid.braincup.ui.components.RailYardBoard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.stringResource

private val FallbackCompactBodyHeight = 260.dp

/** Widest and tallest the board may get relative to its other side, so a map never turns into a strip. */
private const val MAX_BOARD_ASPECT = 1.8f

/**
 * The yard fills the body. Without [liveTrains] (previews, screenshots) the trains frozen into
 * [uiState] are drawn instead.
 */
@Composable
internal fun ColumnScope.RailYardContent(
    uiState: RailYardUiState,
    onAnswer: (String) -> Unit,
    liveTrains: StateFlow<List<RailYardGame.TrainFrame>>? = null,
) {
    val compact = LocalIsCompactHeight.current
    val scaffoldBodyHeight = LocalScaffoldBodyHeight.current
    val fallbackTrains = remember(uiState.trains) { MutableStateFlow(uiState.trains) }
    val trains = (liveTrains ?: fallbackTrains).collectAsStateWithLifecycle()

    BoxWithConstraints(
        // Compact scaffolds scroll and measure with unbounded height, so the board takes the
        // viewport height the scaffold reports instead of filling.
        modifier = (if (compact) Modifier.fillMaxWidth() else Modifier.fillMaxWidth().weight(1f))
            .padding(horizontal = 16.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        val height = if (compact) (scaffoldBodyHeight ?: FallbackCompactBodyHeight) - 40.dp else maxHeight
        RailYardBoard(
            uiState = uiState,
            trains = { trains.value },
            onSwitchTap = { onAnswer(BoardCommand.tap(it)) },
            modifier = Modifier.size(boardSize(maxWidth, height)),
        )
    }

    Spacer(Modifier.height(if (compact) 4.dp else 8.dp))

    BoardInstructionLine(
        text = stringResource(Res.string.game_rail_yard_howto),
        isError = false,
        modifier = Modifier.align(Alignment.CenterHorizontally).padding(horizontal = 24.dp),
    )

    Spacer(Modifier.height(if (compact) 4.dp else 12.dp))
}

private fun boardSize(maxWidth: Dp, maxHeight: Dp): DpSize = if (maxWidth >= maxHeight) {
    DpSize(minOf(maxWidth, maxHeight * MAX_BOARD_ASPECT), maxHeight)
} else {
    DpSize(maxWidth, minOf(maxHeight, maxWidth * MAX_BOARD_ASPECT))
}
