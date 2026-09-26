package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.checkers_black_wins
import braincup.composeapp.generated.resources.checkers_draw
import braincup.composeapp.generated.resources.checkers_keep_jumping
import braincup.composeapp.generated.resources.checkers_lost
import braincup.composeapp.generated.resources.checkers_must_capture_black
import braincup.composeapp.generated.resources.checkers_must_capture_white
import braincup.composeapp.generated.resources.checkers_must_capture_you
import braincup.composeapp.generated.resources.checkers_new_game
import braincup.composeapp.generated.resources.checkers_quit
import braincup.composeapp.generated.resources.checkers_resign
import braincup.composeapp.generated.resources.checkers_resigned
import braincup.composeapp.generated.resources.checkers_resigned_black
import braincup.composeapp.generated.resources.checkers_resigned_white
import braincup.composeapp.generated.resources.checkers_thinking
import braincup.composeapp.generated.resources.checkers_title
import braincup.composeapp.generated.resources.checkers_turn_black
import braincup.composeapp.generated.resources.checkers_turn_white
import braincup.composeapp.generated.resources.checkers_turn_you
import braincup.composeapp.generated.resources.checkers_white_wins
import braincup.composeapp.generated.resources.checkers_won
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.checkers.CHECKERS_SIZE
import com.inspiredandroid.braincup.checkers.CheckersAi
import com.inspiredandroid.braincup.checkers.CheckersBoard
import com.inspiredandroid.braincup.checkers.CheckersDifficulty
import com.inspiredandroid.braincup.checkers.CheckersMode
import com.inspiredandroid.braincup.checkers.CheckersMove
import com.inspiredandroid.braincup.checkers.CheckersPiece
import com.inspiredandroid.braincup.checkers.CheckersResult
import com.inspiredandroid.braincup.checkers.CheckersSide
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.CheckersDisc
import com.inspiredandroid.braincup.ui.components.DefaultButton
import com.inspiredandroid.braincup.ui.components.LocalScaffoldBodyHeight
import com.inspiredandroid.braincup.ui.components.PrismCard
import com.inspiredandroid.braincup.ui.components.XpGainedChip
import com.inspiredandroid.braincup.ui.components.hoverHand
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.ScreenPreviewHost
import com.inspiredandroid.braincup.ui.theme.CheckersBoardFrame
import com.inspiredandroid.braincup.ui.theme.CheckersDarkSquare
import com.inspiredandroid.braincup.ui.theme.CheckersLastMove
import com.inspiredandroid.braincup.ui.theme.CheckersLegalDot
import com.inspiredandroid.braincup.ui.theme.CheckersLightSquare
import com.inspiredandroid.braincup.ui.theme.CheckersMustCaptureRing
import com.inspiredandroid.braincup.ui.theme.CheckersSelected
import com.inspiredandroid.braincup.ui.theme.PrismFacet
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock

private const val MIN_AI_THINK_MILLIS = 500L

/** In VS_CPU the human is black and so always moves first. In VS_HUMAN both sides are human and
 *  these only name the colours. */
private val HUMAN = CheckersSide.BLACK
private val CPU = CheckersSide.WHITE

@Composable
fun CheckersPlayScreen(
    mode: CheckersMode,
    difficulty: CheckersDifficulty,
    storage: UserStorage,
    onBack: () -> Unit,
) {
    var board by remember { mutableStateOf(CheckersBoard.startingPosition()) }
    // The selected piece's square followed by every hop tapped so far. A multi-jump is entered one
    // landing at a time, so the player sees each capture rather than picking from a list of paths.
    var selectedPath by remember { mutableStateOf<List<Int>>(emptyList()) }
    var lastMove by remember { mutableStateOf<CheckersMove?>(null) }
    var aiThinking by remember { mutableStateOf(false) }
    var resignedBy by remember { mutableStateOf<CheckersSide?>(null) }
    var xpGained by remember { mutableIntStateOf(0) }

    val ai = remember(difficulty) { CheckersAi(difficulty) }

    fun resetGame() {
        board = CheckersBoard.startingPosition()
        selectedPath = emptyList()
        lastMove = null
        aiThinking = false
        resignedBy = null
        xpGained = 0
    }

    fun applyMove(move: CheckersMove) {
        board = board.apply(move)
        lastMove = move
        selectedPath = emptyList()
    }

    LaunchedEffect(board, resignedBy, mode) {
        if (mode != CheckersMode.VS_CPU || resignedBy != null) return@LaunchedEffect
        if (board.sideToMove != CPU || board.result() != CheckersResult.ONGOING) return@LaunchedEffect
        aiThinking = true
        try {
            val move = withContext(Dispatchers.Default) {
                val start = Clock.System.now()
                val chosen = ai.bestMove(board)
                // A reply that lands instantly reads as a board glitch rather than as a move.
                val elapsed = (Clock.System.now() - start).inWholeMilliseconds
                val remaining = MIN_AI_THINK_MILLIS - elapsed
                if (remaining > 0) delay(remaining)
                chosen
            }
            if (currentCoroutineContext().isActive && move != null) applyMove(move)
        } finally {
            aiThinking = false
        }
    }

    val result = board.result()

    // Only a win over the CPU earns XP. Resigning leaves the result ONGOING, so it never counts.
    LaunchedEffect(result, mode) {
        if (mode == CheckersMode.VS_CPU && xpGained == 0 && result == CheckersResult.BLACK_WINS) {
            xpGained = storage.awardCheckersWinXp(difficulty).xpGained
        }
    }

    val humanCanInteract = !aiThinking &&
        resignedBy == null &&
        result == CheckersResult.ONGOING &&
        (mode == CheckersMode.VS_HUMAN || board.sideToMove == HUMAN)
    val legalMoves = if (humanCanInteract) board.legalMoves() else emptyList()
    val movesAlongPath = legalMoves.filter { it.path.size > selectedPath.size && it.path.startsWith(selectedPath) }
    val targets: Set<Int> = if (selectedPath.isEmpty()) emptySet() else movesAlongPath.map { it.path[selectedPath.size] }.toSet()
    val mustCapture = legalMoves.firstOrNull()?.isCapture == true
    val capturingPieces: Set<Int> = if (mustCapture && selectedPath.isEmpty()) legalMoves.map { it.from }.toSet() else emptySet()
    val jumpedSoFar: Set<Int> = if (selectedPath.size > 1) {
        movesAlongPath.firstOrNull()?.captured?.take(selectedPath.size - 1)?.toSet().orEmpty()
    } else {
        emptySet()
    }

    fun onSquareTapped(index: Int) {
        // Re-checked against the live board rather than the captured flags: a tap can land while
        // the CPU reply is mid-flight.
        if (aiThinking || resignedBy != null) return
        if (board.result() != CheckersResult.ONGOING) return
        if (mode == CheckersMode.VS_CPU && board.sideToMove != HUMAN) return
        val moves = board.legalMoves()
        if (selectedPath.isNotEmpty()) {
            val next = selectedPath + index
            val continuing = moves.filter { it.path.size >= next.size && it.path.startsWith(next) }
            if (continuing.isNotEmpty()) {
                // English rules never let one move be the start of another, so a full match is final.
                val complete = continuing.firstOrNull { it.path.size == next.size }
                if (complete != null) applyMove(complete) else selectedPath = next
                return
            }
        }
        selectedPath = if (moves.any { it.from == index }) listOf(index) else emptyList()
    }

    AppScaffold(
        title = stringResource(Res.string.checkers_title),
        onBack = onBack,
        // Lets the board shrink in short landscape windows; a scrollable body is measured with
        // unbounded height otherwise.
        provideCompactHeight = true,
    ) {
        Spacer(Modifier.height(8.dp))

        StatusHeader(
            mode = mode,
            board = board,
            result = result,
            resignedBy = resignedBy,
            aiThinking = aiThinking,
            mustCapture = mustCapture,
            midJump = selectedPath.size > 1,
        )

        Spacer(Modifier.height(12.dp))

        BoardView(
            board = board,
            selectedPath = selectedPath,
            targets = targets,
            capturingPieces = capturingPieces,
            jumpedSoFar = jumpedSoFar,
            lastMove = lastMove,
            interactive = humanCanInteract,
            onSquareTapped = ::onSquareTapped,
        )

        Spacer(Modifier.height(16.dp))

        if (xpGained > 0) {
            XpGainedChip(
                xpGained = xpGained,
                modifier = Modifier.align(Alignment.CenterHorizontally).widthIn(max = 200.dp),
            )
            Spacer(Modifier.height(16.dp))
        }

        if (result == CheckersResult.ONGOING && resignedBy == null) {
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                DefaultButton(
                    // Vs the CPU the human concedes whoever's turn it is, including mid-search.
                    // Pass-and-play has no such asymmetry, so the side to move is the one giving up.
                    onClick = {
                        resignedBy = if (mode == CheckersMode.VS_CPU) HUMAN else board.sideToMove
                    },
                    value = stringResource(
                        if (mode == CheckersMode.VS_CPU) Res.string.checkers_resign else Res.string.checkers_quit,
                    ),
                )
                DefaultButton(
                    onClick = { resetGame() },
                    value = stringResource(Res.string.checkers_new_game),
                )
            }
        } else {
            DefaultButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = { resetGame() },
                value = stringResource(Res.string.checkers_new_game),
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

private fun List<Int>.startsWith(prefix: List<Int>): Boolean = size >= prefix.size && subList(0, prefix.size) == prefix

@Composable
private fun StatusHeader(
    mode: CheckersMode,
    board: CheckersBoard,
    result: CheckersResult,
    resignedBy: CheckersSide?,
    aiThinking: Boolean,
    mustCapture: Boolean,
    midJump: Boolean,
) {
    val vsCpu = mode == CheckersMode.VS_CPU
    val text: String
    val color: Color
    when {
        resignedBy != null -> {
            text = when {
                vsCpu -> stringResource(Res.string.checkers_resigned)
                resignedBy == CheckersSide.BLACK -> stringResource(Res.string.checkers_resigned_black)
                else -> stringResource(Res.string.checkers_resigned_white)
            }
            color = if (vsCpu) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        }
        result == CheckersResult.BLACK_WINS -> {
            text = stringResource(if (vsCpu) Res.string.checkers_won else Res.string.checkers_black_wins)
            color = if (vsCpu) SuccessGreen else MaterialTheme.colorScheme.onSurface
        }
        result == CheckersResult.WHITE_WINS -> {
            text = stringResource(if (vsCpu) Res.string.checkers_lost else Res.string.checkers_white_wins)
            color = if (vsCpu) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        }
        result == CheckersResult.DRAW -> {
            text = stringResource(Res.string.checkers_draw)
            color = MaterialTheme.colorScheme.onSurface
        }
        aiThinking || (vsCpu && board.sideToMove == CPU) -> {
            text = stringResource(Res.string.checkers_thinking)
            color = MaterialTheme.colorScheme.onSurfaceVariant
        }
        midJump -> {
            text = stringResource(Res.string.checkers_keep_jumping)
            color = CheckersMustCaptureRing
        }
        // A piece that will not move is baffling unless the forced capture is spelled out.
        mustCapture -> {
            text = when {
                vsCpu -> stringResource(Res.string.checkers_must_capture_you)
                board.sideToMove == CheckersSide.BLACK -> stringResource(Res.string.checkers_must_capture_black)
                else -> stringResource(Res.string.checkers_must_capture_white)
            }
            color = CheckersMustCaptureRing
        }
        else -> {
            text = when {
                vsCpu -> stringResource(Res.string.checkers_turn_you)
                board.sideToMove == CheckersSide.BLACK -> stringResource(Res.string.checkers_turn_black)
                else -> stringResource(Res.string.checkers_turn_white)
            }
            color = MaterialTheme.colorScheme.onSurface
        }
    }
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = color,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
    }
}

@Composable
private fun BoardView(
    board: CheckersBoard,
    selectedPath: List<Int>,
    targets: Set<Int>,
    capturingPieces: Set<Int>,
    jumpedSoFar: Set<Int>,
    lastMove: CheckersMove?,
    interactive: Boolean,
    onSquareTapped: (Int) -> Unit,
) {
    val scaffoldBodyHeight = LocalScaffoldBodyHeight.current
    val lastMoveSquares = lastMove?.path?.toSet().orEmpty()
    // Mid-jump, the piece is drawn where it has got to rather than where it started.
    val origin = selectedPath.firstOrNull()
    val hop = selectedPath.lastOrNull()
    val movingPiece = origin?.let { board.pieceAt(it) }
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        // Header, spacers and the action buttons take roughly this much.
        val verticalChrome = 140.dp
        val heightBudget = ((scaffoldBodyHeight ?: maxHeight) - verticalChrome).coerceAtLeast(160.dp)
        val boardSide = minOf(maxWidth, heightBudget, 480.dp)
        val cellSize = boardSide / CHECKERS_SIZE

        PrismCard(
            face = CheckersBoardFrame,
            facet = PrismFacet.Board,
            modifier = Modifier.align(Alignment.Center),
        ) {
            Column {
                for (row in 0 until CHECKERS_SIZE) {
                    Row {
                        for (col in 0 until CHECKERS_SIZE) {
                            val index = row * CHECKERS_SIZE + col
                            val piece = when (index) {
                                hop -> movingPiece
                                origin -> null
                                else -> board.pieceAt(index)
                            }
                            BoardSquare(
                                size = cellSize,
                                isDark = CheckersBoard.isDarkSquare(index),
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
}

@Composable
private fun BoardSquare(
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

@DevicePreviews
@Composable
private fun CheckersPlayScreenPreview() {
    ScreenPreviewHost {
        val storage = remember { UserStorage.forPreview() }
        CheckersPlayScreen(
            mode = CheckersMode.VS_CPU,
            difficulty = CheckersDifficulty.EASY,
            storage = storage,
            onBack = {},
        )
    }
}
