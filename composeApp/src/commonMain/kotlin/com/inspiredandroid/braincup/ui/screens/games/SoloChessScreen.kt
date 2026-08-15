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
import com.inspiredandroid.braincup.games.SoloChessGame
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.theme.PrismFacet
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ColumnScope.SoloChessContent(
    uiState: SoloChessUiState,
    onAnswer: (String) -> Unit,
    onGiveUp: () -> Unit,
) {
    val n = uiState.size
    val compact = LocalIsCompactHeight.current
    // Fit the whole board into a fixed target so 4x4 isn't tiny and 6x6 isn't oversized.
    val cellSize = ((if (compact) 248f else 312f) / n).dp

    val board: @Composable () -> Unit = {
        PrismCard(face = ChessBoardFrame, facet = PrismFacet.Board) {
            Column {
                for (row in 0 until n) {
                    Row {
                        for (col in 0 until n) {
                            val index = row * n + col
                            val type = uiState.pieces[index]
                            val isKing = index == uiState.kingCell
                            val captures = uiState.remainingCapturesByCell[index] ?: 0
                            // A piece that has used both captures is "spent": it can no longer move.
                            // The king is never spent (it can't be captured and always remains).
                            val spent = type != null && captures <= 0 && !isKing
                            ChessSquare(
                                size = cellSize,
                                isLight = (row + col) % 2 == 0,
                                isSelected = uiState.selected == index,
                                target = if (index in uiState.targets) {
                                    ChessSquareTarget.Capture
                                } else {
                                    ChessSquareTarget.None
                                },
                                showKingHighlight = isKing,
                                onClick = { onAnswer(BoardCommand.tap(index)) },
                            ) {
                                type?.let {
                                    Box(
                                        modifier = Modifier.size(cellSize * 0.82f),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        ChessPieceIcon(
                                            resource = chessPieceResource(it),
                                            isWhite = true,
                                            figureSize = cellSize * 0.78f,
                                            tint = if (spent) SoloChessSpentTint else null,
                                        )
                                    }
                                    // Capture "charges": one amber pip per remaining capture (max two).
                                    SoloChessCapturePips(
                                        remaining = captures.coerceIn(0, SoloChessGame.MAX_CAPTURES),
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(cellSize * 0.05f)
                                            .size(width = cellSize * 0.46f, height = cellSize * 0.22f),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    val progress: @Composable () -> Unit = {
        Text(
            text = stringResource(Res.string.solo_chess_pieces_left, uiState.pieces.size),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }

    // The how-to line is replaced by a restart nudge when no capture is possible (a dead-end line).
    val instruction = if (uiState.stuck) {
        stringResource(Res.string.solo_chess_stuck)
    } else {
        stringResource(Res.string.game_solo_chess_howto)
    }
    val isError = uiState.stuck

    val actions: @Composable () -> Unit = {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            DefaultButton(
                onClick = { onAnswer(BoardCommand.RESTART) },
                value = stringResource(Res.string.solo_chess_restart),
            )
            GiveUpButton(onGiveUp = onGiveUp)
        }
    }

    if (compact) {
        CompactGameRow {
            board()
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LevelHeader(uiState.level)
                Spacer(Modifier.height(6.dp))
                progress()
                Spacer(Modifier.height(6.dp))
                BoardInstructionLine(
                    text = instruction,
                    isError = isError,
                    style = MaterialTheme.typography.labelMedium,
                )
                Spacer(Modifier.height(8.dp))
                actions()
            }
        }
    } else {
        LevelHeader(uiState.level, Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(6.dp))
        Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            progress()
        }
        Spacer(Modifier.height(6.dp))
        BoardInstructionLine(
            text = instruction,
            isError = isError,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 24.dp),
        )
        Spacer(Modifier.height(16.dp))
        Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            board()
        }
        Spacer(Modifier.height(16.dp))
        Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            actions()
        }
    }
}

@DevicePreviews
@Composable
private fun SoloChessContentPreview() {
    GamePreviewHost {
        SoloChessContent(
            uiState = SoloChessUiState(
                size = 4,
                pieces = persistentMapOf(0 to PieceType.ROOK, 5 to PieceType.KNIGHT, 15 to PieceType.KING),
                remainingCapturesByCell = persistentMapOf(0 to 1, 5 to 1, 15 to 0),
                kingCell = 15,
                selected = null,
                targets = persistentSetOf(),
                level = 1,
                stuck = false,
            ),
            onAnswer = {},
            onGiveUp = {},
        )
    }
}
