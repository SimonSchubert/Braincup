package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.checkers.CheckersBoard
import com.inspiredandroid.braincup.checkers.CheckersMove
import com.inspiredandroid.braincup.checkers.CheckersPiece
import com.inspiredandroid.braincup.ui.theme.CheckersBoardFrame
import com.inspiredandroid.braincup.ui.theme.CheckersDarkSquare
import com.inspiredandroid.braincup.ui.theme.CheckersLastMove
import com.inspiredandroid.braincup.ui.theme.CheckersLegalDot
import com.inspiredandroid.braincup.ui.theme.CheckersLightSquare
import com.inspiredandroid.braincup.ui.theme.CheckersMustCaptureRing
import com.inspiredandroid.braincup.ui.theme.CheckersSelected
import com.inspiredandroid.braincup.ui.theme.PrismFacet

sealed interface CheckersTap {
    data class Select(val path: List<Int>) : CheckersTap
    data class Play(val move: CheckersMove) : CheckersTap
}

/**
 * [selectedPath] is the selected piece's square followed by every landing tapped so far. A
 * multi-jump is entered one landing at a time, so the player sees each capture rather than picking
 * from a list of paths.
 */
fun resolveCheckersTap(legalMoves: List<CheckersMove>, selectedPath: List<Int>, index: Int): CheckersTap {
    if (selectedPath.isNotEmpty()) {
        val next = selectedPath + index
        val continuing = legalMoves.filter { it.path.startsWith(next) }
        if (continuing.isNotEmpty()) {
            // English rules never let one move be the start of another, so a full match is final.
            val complete = continuing.firstOrNull { it.path.size == next.size }
            return if (complete != null) CheckersTap.Play(complete) else CheckersTap.Select(next)
        }
    }
    return CheckersTap.Select(if (legalMoves.any { it.from == index }) listOf(index) else emptyList())
}

private fun List<Int>.startsWith(prefix: List<Int>): Boolean = size >= prefix.size && subList(0, prefix.size) == prefix

@Composable
fun CheckersBoardGrid(
    board: CheckersBoard,
    legalMoves: List<CheckersMove>,
    selectedPath: List<Int>,
    lastMove: CheckersMove?,
    interactive: Boolean,
    boardSide: Dp,
    onSquareTapped: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val movesAlongPath = legalMoves.filter { it.path.size > selectedPath.size && it.path.startsWith(selectedPath) }
    val targets = if (selectedPath.isEmpty()) emptySet() else movesAlongPath.map { it.path[selectedPath.size] }.toSet()
    val mustCapture = legalMoves.firstOrNull()?.isCapture == true
    val capturingPieces = if (mustCapture && selectedPath.isEmpty()) legalMoves.map { it.from }.toSet() else emptySet()
    val jumpedSoFar = if (selectedPath.size > 1) {
        movesAlongPath.firstOrNull()?.captured?.take(selectedPath.size - 1)?.toSet().orEmpty()
    } else {
        emptySet()
    }
    val lastMoveSquares = lastMove?.path?.toSet().orEmpty()
    // Mid-jump, the piece is drawn where it has got to rather than where it started.
    val origin = selectedPath.firstOrNull()
    val hop = selectedPath.lastOrNull()
    val movingPiece = origin?.let { board.pieceAt(it) }
    val cellSize = boardSide / board.size

    PrismCard(
        face = CheckersBoardFrame,
        facet = PrismFacet.Board,
        modifier = modifier,
    ) {
        Column {
            for (row in 0 until board.size) {
                Row {
                    for (col in 0 until board.size) {
                        val index = row * board.size + col
                        val piece = when (index) {
                            hop -> movingPiece
                            origin -> null
                            else -> board.pieceAt(index)
                        }
                        CheckersSquare(
                            size = cellSize,
                            isDark = board.isDarkSquare(index),
                            piece = piece,
                            isSelected = index == hop,
                            isTarget = index in targets,
                            mustCapture = index in capturingPieces,
                            isJumped = index in jumpedSoFar,
                            isLastMove = index in lastMoveSquares,
                            enabled = interactive,
                            onClick = { onSquareTapped(index) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CheckersSquare(
    size: Dp,
    isDark: Boolean,
    piece: CheckersPiece?,
    isSelected: Boolean,
    isTarget: Boolean,
    mustCapture: Boolean,
    isJumped: Boolean,
    isLastMove: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val clickable = enabled && isDark
    Box(
        modifier = Modifier
            .size(size)
            .background(if (isDark) CheckersDarkSquare else CheckersLightSquare)
            .clickable(enabled = clickable, onClick = onClick)
            .hoverHand(clickable),
        contentAlignment = Alignment.Center,
    ) {
        when {
            isSelected -> Box(Modifier.matchParentSize().background(CheckersSelected))
            isLastMove -> Box(Modifier.matchParentSize().background(CheckersLastMove))
        }
        if (piece != null) {
            CheckersDisc(
                piece = piece,
                modifier = Modifier
                    .size(size * 0.8f)
                    .alpha(if (isJumped) 0.35f else 1f),
            )
            if (mustCapture) {
                Box(
                    Modifier
                        .size(size * 0.92f)
                        .border(2.dp, CheckersMustCaptureRing),
                )
            }
        } else if (isTarget) {
            Box(
                Modifier
                    .size(size * 0.28f)
                    .clip(CircleShape)
                    .background(CheckersLegalDot),
            )
        }
    }
}
