package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.games.SoloChessGame
import com.inspiredandroid.braincup.games.minichess.PieceType
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
                            SoloChessCellView(
                                type = uiState.pieces[index],
                                size = cellSize,
                                isLight = (row + col) % 2 == 0,
                                isKing = index == uiState.kingCell,
                                isSelected = uiState.selected == index,
                                isTarget = index in uiState.targets,
                                captures = uiState.capturesLeft[index] ?: 0,
                                onClick = { onAnswer("tap:$index") },
                            )
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
    val instructionColor = if (uiState.stuck) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val instructionWeight = if (uiState.stuck) FontWeight.Bold else FontWeight.Normal

    val actions: @Composable () -> Unit = {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            DefaultButton(
                onClick = { onAnswer("restart") },
                value = stringResource(Res.string.solo_chess_restart),
            )
            GiveUpButton(onGiveUp = onGiveUp)
        }
    }

    if (compact) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            board()
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(Res.string.level_label, uiState.level),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.height(6.dp))
                progress()
                Spacer(Modifier.height(6.dp))
                Text(
                    text = instruction,
                    style = MaterialTheme.typography.labelMedium,
                    color = instructionColor,
                    fontWeight = instructionWeight,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(8.dp))
                actions()
            }
        }
    } else {
        Text(
            text = stringResource(Res.string.level_label, uiState.level),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(6.dp))
        Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            progress()
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = instruction,
            style = MaterialTheme.typography.bodyMedium,
            color = instructionColor,
            fontWeight = instructionWeight,
            textAlign = TextAlign.Center,
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

@Composable
private fun SoloChessCellView(
    type: PieceType?,
    size: androidx.compose.ui.unit.Dp,
    isLight: Boolean,
    isKing: Boolean,
    isSelected: Boolean,
    isTarget: Boolean,
    captures: Int,
    onClick: () -> Unit,
) {
    val baseColor = if (isLight) ChessLightSquare else ChessDarkSquare
    // A piece that has used both captures is "spent": it can no longer move. The king is never spent
    // (it can't be captured and always remains), so it is never greyed.
    val spent = type != null && captures <= 0 && !isKing
    Box(
        modifier = Modifier
            .size(size)
            .background(if (isSelected) ChessSelected else baseColor)
            .clickable(onClick = onClick)
            .hoverHand(),
        contentAlignment = Alignment.Center,
    ) {
        // A translucent gold underlay marks the king: it can never be captured and must be the last
        // piece standing.
        if (isKing && !isSelected) {
            Box(modifier = Modifier.matchParentSize().background(ChessDrawTint))
        }
        if (isTarget) {
            Box(modifier = Modifier.matchParentSize().background(ChessCaptureTint))
        }
        type?.let {
            Box(
                modifier = Modifier.size(size * 0.82f),
                contentAlignment = Alignment.Center,
            ) {
                ChessPieceIcon(
                    resource = chessPieceResource(it),
                    isWhite = true,
                    figureSize = size * 0.78f,
                    tint = if (spent) SoloChessSpentTint else null,
                )
            }
            // Capture "charges": one amber pip per remaining capture (max two). This makes the
            // "no piece may capture more than twice" rule visible — a spent piece shows two empty pips.
            SoloChessCapturePips(
                remaining = captures.coerceIn(0, SoloChessGame.MAX_CAPTURES),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(size * 0.05f)
                    .size(width = size * 0.46f, height = size * 0.22f),
            )
        }
    }
}

@GameDevicePreviews
@Composable
private fun SoloChessContentPreview() {
    GamePreviewHost {
        SoloChessContent(
            uiState = SoloChessUiState(
                size = 4,
                pieces = persistentMapOf(0 to PieceType.ROOK, 5 to PieceType.KNIGHT, 15 to PieceType.KING),
                capturesLeft = persistentMapOf(0 to 1, 5 to 1, 15 to 0),
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
