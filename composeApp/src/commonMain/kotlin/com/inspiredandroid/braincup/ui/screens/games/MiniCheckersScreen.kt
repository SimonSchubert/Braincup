package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.checkers.CheckersBoard
import com.inspiredandroid.braincup.checkers.CheckersSide
import com.inspiredandroid.braincup.games.MiniCheckersGame
import com.inspiredandroid.braincup.games.minicheckers.MINI_CHECKERS_DRAW_PLIES
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.theme.CheckersMustCaptureRing
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

private val MiniCheckersBoardSide = 300.dp

/** Warn once fewer than this many moves each remain before the quiet-move draw. */
private const val DRAW_WARNING_MOVES = 5

@Composable
internal fun ColumnScope.MiniCheckersContent(
    uiState: MiniCheckersUiState,
    onAnswer: (String) -> Unit,
) {
    var selectedPath by remember(uiState.board) { mutableStateOf<List<Int>>(emptyList()) }
    val interactive = uiState.outcome == null && !uiState.isAiThinking
    val mustCapture = uiState.legalMoves.firstOrNull()?.isCapture == true
    val movesUntilDraw = (uiState.drawPlies - uiState.quietPlies + 1) / 2

    val statusBox: @Composable () -> Unit = {
        Box(
            modifier = Modifier.height(20.dp),
            contentAlignment = Alignment.Center,
        ) {
            val (text, color) = when {
                uiState.outcome != null -> null to MaterialTheme.colorScheme.onSurface
                uiState.isAiThinking -> stringResource(Res.string.checkers_thinking) to MaterialTheme.colorScheme.onSurfaceVariant
                selectedPath.size > 1 -> stringResource(Res.string.checkers_keep_jumping) to CheckersMustCaptureRing
                // Stays up while the player is on the line and drops away once they leave it.
                uiState.combinationMoves != null ->
                    pluralStringResource(Res.plurals.mini_checkers_win_in, uiState.combinationMoves, uiState.combinationMoves) to SuccessGreen
                mustCapture -> stringResource(Res.string.checkers_must_capture_you) to CheckersMustCaptureRing
                movesUntilDraw <= DRAW_WARNING_MOVES ->
                    pluralStringResource(Res.plurals.mini_checkers_draw_in, movesUntilDraw, movesUntilDraw) to ChessWarning
                else -> stringResource(Res.string.checkers_turn_you) to MaterialTheme.colorScheme.onSurface
            }
            if (text != null) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = color,
                    fontWeight = if (color == MaterialTheme.colorScheme.onSurface) FontWeight.Normal else FontWeight.Bold,
                )
            }
        }
    }

    val board: @Composable () -> Unit = {
        CheckersBoardGrid(
            board = uiState.board,
            legalMoves = uiState.legalMoves,
            selectedPath = selectedPath,
            lastMove = uiState.lastMove,
            interactive = interactive,
            boardSide = MiniCheckersBoardSide,
            onSquareTapped = { index ->
                when (val tap = resolveCheckersTap(uiState.legalMoves, selectedPath, index)) {
                    is CheckersTap.Play -> {
                        selectedPath = emptyList()
                        onAnswer(MiniCheckersGame.encodeMove(tap.move.path))
                    }
                    is CheckersTap.Select -> selectedPath = tap.path
                }
            },
        )
    }

    val outcomeAndActions: @Composable ColumnScope.() -> Unit = {
        if (uiState.outcome != null) {
            Text(
                text = when (uiState.outcome) {
                    CpuRoundOutcome.PLAYER_WIN -> stringResource(Res.string.checkers_won)
                    CpuRoundOutcome.PLAYER_LOSS -> stringResource(Res.string.checkers_lost)
                    CpuRoundOutcome.DRAW -> stringResource(Res.string.mini_checkers_draw)
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (uiState.outcome == CpuRoundOutcome.PLAYER_WIN) SuccessGreen else MaterialTheme.colorScheme.onSurface,
            )
            if (uiState.outcome == CpuRoundOutcome.PLAYER_WIN) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(Res.string.mini_chess_xp_gained, uiState.pointsForWin),
                    style = MaterialTheme.typography.bodyMedium,
                    color = SuccessGreen,
                )
            }
            Spacer(Modifier.height(12.dp))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DefaultButton(
                onClick = { onAnswer(BoardCommand.RESET) },
                value = stringResource(Res.string.mini_chess_reset),
            )
            DefaultButton(
                onClick = { onAnswer(BoardCommand.RESTART) },
                value = stringResource(Res.string.mini_chess_restart),
            )
        }
    }

    if (LocalIsCompactHeight.current) {
        CompactGameRow {
            board()
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                statusBox()
                Spacer(Modifier.height(12.dp))
                outcomeAndActions()
            }
        }
    } else {
        Column(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(8.dp))
            statusBox()
            Spacer(Modifier.height(8.dp))
            board()
            Spacer(Modifier.height(12.dp))
            outcomeAndActions()
        }
    }
}

@DevicePreviews
@Composable
private fun MiniCheckersContentPreview() {
    GamePreviewHost {
        val board = CheckersBoard.fromRows(
            listOf(
                "...w.W",
                "......",
                "...B..",
                "b.....",
                ".b....",
                "....b.",
            ),
            CheckersSide.BLACK,
            drawPlies = MINI_CHECKERS_DRAW_PLIES,
        )
        MiniCheckersContent(
            uiState = MiniCheckersUiState(
                board = board,
                legalMoves = board.legalMoves().toImmutableList(),
                lastMove = null,
                isAiThinking = false,
                outcome = null,
                combinationMoves = 3,
                quietPlies = 0,
                drawPlies = MINI_CHECKERS_DRAW_PLIES,
                pointsForWin = 10,
            ),
            onAnswer = {},
        )
    }
}
