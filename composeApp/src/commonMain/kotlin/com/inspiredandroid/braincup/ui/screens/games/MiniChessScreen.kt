package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.chess.PieceType
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.theme.PrismFacet
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ColumnScope.MiniChessContent(
    uiState: MiniChessUiState,
    onAnswer: (String) -> Unit,
) {
    var selectedFrom by remember(uiState.cells, uiState.legalMovesByFrom) {
        mutableStateOf<Int?>(null)
    }
    val highlights = uiState.legalMovesByFrom[selectedFrom].orEmpty()
    val drawHighlights = uiState.stalematingMovesByFrom[selectedFrom].orEmpty()
    val interactive = uiState.outcome == null && !uiState.isAiThinking
    val selectedHasDrawMove = drawHighlights.isNotEmpty()
    val movesLeft = uiState.halfMoveCap - uiState.halfMoveCount
    val movesNearCap = uiState.outcome == null && movesLeft in 1..6

    val statusBox: @Composable () -> Unit = {
        Box(
            modifier = Modifier.height(20.dp),
            contentAlignment = Alignment.Center,
        ) {
            when {
                uiState.outcome != null -> Unit
                uiState.isAiThinking -> Text(
                    text = stringResource(Res.string.mini_chess_thinking),
                    style = MaterialTheme.typography.bodyMedium,
                )
                selectedHasDrawMove -> Row(verticalAlignment = Alignment.CenterVertically) {
                    ColorPrismCell(
                        face = ChessDrawDot,
                        facet = PrismFacet.Dot,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(Res.string.mini_chess_draw_warning),
                        style = MaterialTheme.typography.bodyMedium,
                        color = ChessWarning,
                        fontWeight = FontWeight.Bold,
                    )
                }
                else -> Text(
                    text = stringResource(
                        Res.string.mini_chess_move_counter,
                        uiState.halfMoveCount,
                        uiState.halfMoveCap,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (movesNearCap) ChessWarning else MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (movesNearCap) FontWeight.Bold else FontWeight.Normal,
                )
            }
        }
    }

    val board: @Composable () -> Unit = {
        PrismCard(
            face = ChessBoardFrame,
            facet = PrismFacet.Board,
        ) {
            Column {
                for (row in 4 downTo 0) {
                    Row {
                        for (col in 0..4) {
                            val index = row * 5 + col
                            val cell = uiState.cells[index]
                            val isLegalTarget = index in highlights
                            val isStalemateTarget = index in drawHighlights
                            val showCheckRing = cell.pieceType == PieceType.KING &&
                                (
                                    (cell.isWhite && uiState.whiteInCheck) ||
                                        (!cell.isWhite && uiState.blackInCheck)
                                    )
                            val target = when {
                                !isLegalTarget -> ChessSquareTarget.None
                                isStalemateTarget -> ChessSquareTarget.Stalemate
                                cell.pieceType != null -> ChessSquareTarget.Capture
                                else -> ChessSquareTarget.LegalEmpty
                            }
                            ChessSquare(
                                size = 56.dp,
                                isLight = (row + col) % 2 == 0,
                                isSelected = selectedFrom == index,
                                isLastMove = index == uiState.lastMoveFromIndex ||
                                    index == uiState.lastMoveToIndex,
                                showCheckRing = showCheckRing,
                                target = target,
                                enabled = interactive,
                                onClick = {
                                    val from = selectedFrom
                                    if (from != null && uiState.legalMovesByFrom[from]?.contains(index) == true) {
                                        onAnswer("$from>$index")
                                        selectedFrom = null
                                    } else if (uiState.legalMovesByFrom.containsKey(index)) {
                                        selectedFrom = index
                                    } else {
                                        selectedFrom = null
                                    }
                                },
                            ) {
                                cell.pieceType?.let { type ->
                                    Box(
                                        modifier = Modifier.size(52.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        ChessPieceIcon(
                                            resource = chessPieceResource(type),
                                            isWhite = cell.isWhite,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    val outcomeAndActions: @Composable ColumnScope.() -> Unit = {
        if (uiState.outcome == null) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DefaultButton(
                    onClick = { onAnswer("reset") },
                    value = stringResource(Res.string.mini_chess_reset),
                )
                DefaultButton(
                    onClick = { onAnswer("restart") },
                    value = stringResource(Res.string.mini_chess_restart),
                )
            }
        } else {
            Text(
                text = when (uiState.outcome) {
                    MiniChessOutcome.PLAYER_WIN -> stringResource(Res.string.mini_chess_round_won)
                    MiniChessOutcome.PLAYER_LOSS -> stringResource(Res.string.mini_chess_round_lost)
                    MiniChessOutcome.DRAW -> stringResource(Res.string.mini_chess_round_draw)
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = when (uiState.outcome) {
                    MiniChessOutcome.PLAYER_WIN -> SuccessGreen
                    else -> MaterialTheme.colorScheme.onSurface
                },
            )
            if (uiState.outcome == MiniChessOutcome.PLAYER_WIN) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(Res.string.mini_chess_xp_gained, uiState.pointsForWin),
                    style = MaterialTheme.typography.bodyMedium,
                    color = SuccessGreen,
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DefaultButton(
                    onClick = { onAnswer("reset") },
                    value = stringResource(Res.string.mini_chess_reset),
                )
                DefaultButton(
                    onClick = { onAnswer("restart") },
                    value = stringResource(Res.string.mini_chess_restart),
                )
            }
        }
    }

    if (LocalIsCompactHeight.current) {
        CompactGameRow {
            board()
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
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
private fun MiniChessContentPreview() {
    GamePreviewHost {
        MiniChessContent(
            uiState = MiniChessUiState(
                cells = persistentListOf(
                    MiniChessCell(PieceType.ROOK, true),
                    MiniChessCell(null, true),
                    MiniChessCell(null, true),
                    MiniChessCell(null, true),
                    MiniChessCell(PieceType.KING, true),
                    MiniChessCell(null, true),
                    MiniChessCell(null, true),
                    MiniChessCell(null, true),
                    MiniChessCell(null, true),
                    MiniChessCell(null, true),
                    MiniChessCell(null, true),
                    MiniChessCell(null, true),
                    MiniChessCell(null, false),
                    MiniChessCell(null, false),
                    MiniChessCell(null, false),
                    MiniChessCell(null, false),
                    MiniChessCell(null, false),
                    MiniChessCell(null, false),
                    MiniChessCell(null, false),
                    MiniChessCell(null, false),
                    MiniChessCell(PieceType.KING, false),
                    MiniChessCell(null, false),
                    MiniChessCell(null, false),
                    MiniChessCell(null, false),
                    MiniChessCell(PieceType.ROOK, false),
                ),
                legalMovesByFrom = persistentMapOf(),
                stalematingMovesByFrom = persistentMapOf(),
                lastMoveFromIndex = null,
                lastMoveToIndex = null,
                whiteInCheck = false,
                blackInCheck = false,
                isAiThinking = false,
                outcome = null,
                halfMoveCount = 0,
                halfMoveCap = 40,
                pointsForWin = 10,
            ),
            onAnswer = {},
        )
    }
}
