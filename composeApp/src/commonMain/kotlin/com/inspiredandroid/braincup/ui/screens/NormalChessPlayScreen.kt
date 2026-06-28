package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.ic_chess_bishop
import braincup.composeapp.generated.resources.ic_chess_king
import braincup.composeapp.generated.resources.ic_chess_knight
import braincup.composeapp.generated.resources.ic_chess_pawn
import braincup.composeapp.generated.resources.ic_chess_queen
import braincup.composeapp.generated.resources.ic_chess_rook
import braincup.composeapp.generated.resources.normal_chess_checkmate_black
import braincup.composeapp.generated.resources.normal_chess_checkmate_white
import braincup.composeapp.generated.resources.normal_chess_draw_50
import braincup.composeapp.generated.resources.normal_chess_draw_material
import braincup.composeapp.generated.resources.normal_chess_draw_warning
import braincup.composeapp.generated.resources.normal_chess_new_game
import braincup.composeapp.generated.resources.normal_chess_promotion_bishop
import braincup.composeapp.generated.resources.normal_chess_promotion_knight
import braincup.composeapp.generated.resources.normal_chess_promotion_queen
import braincup.composeapp.generated.resources.normal_chess_promotion_rook
import braincup.composeapp.generated.resources.normal_chess_promotion_title
import braincup.composeapp.generated.resources.normal_chess_quit
import braincup.composeapp.generated.resources.normal_chess_resign
import braincup.composeapp.generated.resources.normal_chess_stalemate
import braincup.composeapp.generated.resources.normal_chess_thinking
import braincup.composeapp.generated.resources.normal_chess_title
import braincup.composeapp.generated.resources.normal_chess_turn_black
import braincup.composeapp.generated.resources.normal_chess_turn_white
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.normalchess.GameResult
import com.inspiredandroid.braincup.normalchess.Move
import com.inspiredandroid.braincup.normalchess.NORMAL_CHESS_SIZE
import com.inspiredandroid.braincup.normalchess.NormalChessAi
import com.inspiredandroid.braincup.normalchess.NormalChessBoard
import com.inspiredandroid.braincup.normalchess.NormalChessDifficulty
import com.inspiredandroid.braincup.normalchess.NormalChessMode
import com.inspiredandroid.braincup.normalchess.Piece
import com.inspiredandroid.braincup.normalchess.PieceType
import com.inspiredandroid.braincup.normalchess.Square
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.ChessBoardFrame
import com.inspiredandroid.braincup.ui.components.ChessCaptureTint
import com.inspiredandroid.braincup.ui.components.ChessCheckTint
import com.inspiredandroid.braincup.ui.components.ChessDarkSquare
import com.inspiredandroid.braincup.ui.components.ChessDrawDot
import com.inspiredandroid.braincup.ui.components.ChessDrawTint
import com.inspiredandroid.braincup.ui.components.ChessLastMove
import com.inspiredandroid.braincup.ui.components.ChessLightSquare
import com.inspiredandroid.braincup.ui.components.ChessPieceIcon
import com.inspiredandroid.braincup.ui.components.ChessSelected
import com.inspiredandroid.braincup.ui.components.ChessWarning
import com.inspiredandroid.braincup.ui.components.DefaultButton
import com.inspiredandroid.braincup.ui.components.PrismCard
import com.inspiredandroid.braincup.ui.components.PrismTile
import com.inspiredandroid.braincup.ui.components.XpGainedChip
import com.inspiredandroid.braincup.ui.components.hoverHand
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock
import com.inspiredandroid.braincup.normalchess.Color as ChessColor

private const val MIN_AI_THINK_MILLIS = 500L

@Composable
fun NormalChessPlayScreen(
    mode: NormalChessMode,
    difficulty: NormalChessDifficulty,
    storage: UserStorage,
    onBack: () -> Unit,
) {
    var board by remember { mutableStateOf(NormalChessBoard.startingPosition()) }
    var selected by remember { mutableStateOf<Square?>(null) }
    var pendingPromotion by remember { mutableStateOf<PendingPromotion?>(null) }
    var lastMoveFrom by remember { mutableStateOf<Square?>(null) }
    var lastMoveTo by remember { mutableStateOf<Square?>(null) }
    var aiThinking by remember { mutableStateOf(false) }
    var humanResigned by remember { mutableStateOf(false) }
    var xpGained by remember { mutableStateOf(0) }

    val ai = remember(difficulty) {
        NormalChessAi(
            depth = difficulty.depth,
            useQuiescence = difficulty.useQuiescence,
            blunderChance = difficulty.blunderChance,
        )
    }

    fun resetGame() {
        board = NormalChessBoard.startingPosition()
        selected = null
        pendingPromotion = null
        lastMoveFrom = null
        lastMoveTo = null
        aiThinking = false
        humanResigned = false
        xpGained = 0
    }

    fun applyMove(move: Move) {
        board = board.apply(move)
        lastMoveFrom = move.from
        lastMoveTo = move.to
        selected = null
        pendingPromotion = null
    }

    // Schedule AI move after every human move when in VS_CPU and it's black to move.
    LaunchedEffect(board, mode) {
        if (mode != NormalChessMode.VS_CPU) return@LaunchedEffect
        if (board.sideToMove != ChessColor.BLACK) return@LaunchedEffect
        if (board.result() != GameResult.ONGOING) return@LaunchedEffect
        aiThinking = true
        try {
            val move = withContext(Dispatchers.Default) {
                val start = Clock.System.now()
                val chosen = ai.bestMove(board)
                val elapsedMs = (Clock.System.now() - start).inWholeMilliseconds
                val remaining = MIN_AI_THINK_MILLIS - elapsedMs
                if (remaining > 0) delay(remaining)
                chosen
            }
            if (currentCoroutineContext().isActive && move != null) {
                applyMove(move)
            }
        } finally {
            aiThinking = false
        }
    }

    val result = board.result()

    // Award XP once per VS_CPU checkmate win. Resigning doesn't count — board.result()
    // stays ONGOING when humanResigned flips, so the gate is implicit.
    LaunchedEffect(result, mode) {
        if (xpGained == 0 && mode == NormalChessMode.VS_CPU && result == GameResult.WHITE_WINS) {
            xpGained = storage.awardNormalChessWinXp(difficulty).xpGained
        }
    }

    val humanCanInteract = !aiThinking &&
        pendingPromotion == null &&
        !humanResigned &&
        result == GameResult.ONGOING &&
        (
            mode == NormalChessMode.VS_HUMAN ||
                (mode == NormalChessMode.VS_CPU && board.sideToMove == ChessColor.WHITE)
            )
    val legalMoves = if (humanCanInteract) board.legalMoves() else emptyList()
    val legalByFrom: Map<Square, List<Move>> = legalMoves.groupBy { it.from }
    val highlightedTargets: Set<Square> = selected?.let { legalByFrom[it]?.map { m -> m.to }?.toSet() } ?: emptySet()
    // Moves from the selected piece that would draw the game (stalemate the opponent or trigger an
    // automatic draw). We warn the player so they don't accidentally stalemate a winning position.
    val stalematingTargets: Set<Square> = selected?.let { sel ->
        legalByFrom[sel].orEmpty()
            .filter { move ->
                val after = board.apply(move)
                val opponentMated = after.legalMoves().isEmpty() && !after.isInCheck(after.sideToMove)
                val drawByMaterial = after.isInsufficientMaterial()
                val drawByClock = after.halfmoveClock >= 100
                opponentMated || drawByMaterial || drawByClock
            }
            .map { it.to }
            .toSet()
    } ?: emptySet()
    val selectedHasDrawMove = stalematingTargets.isNotEmpty()

    fun onSquareTapped(square: Square) {
        // Recompute legality on every click against current board state so we never act on
        // a stale closure. The `humanCanInteract` gate is intentionally re-checked here.
        val canInteract = !aiThinking &&
            pendingPromotion == null &&
            !humanResigned &&
            board.result() == GameResult.ONGOING &&
            (
                mode == NormalChessMode.VS_HUMAN ||
                    (mode == NormalChessMode.VS_CPU && board.sideToMove == ChessColor.WHITE)
                )
        if (!canInteract) return
        val sel = selected
        if (sel != null) {
            val candidates = board.legalMoves().filter { it.from == sel && it.to == square }
            if (candidates.isNotEmpty()) {
                val needsPromotion = candidates.any { it.promotion != null }
                if (needsPromotion) {
                    pendingPromotion = PendingPromotion(from = sel, to = square)
                } else {
                    applyMove(candidates.first())
                }
                return
            }
        }
        val piece = board.pieceAt(square)
        if (piece != null && piece.color == board.sideToMove) {
            selected = square
        } else {
            selected = null
        }
    }

    AppScaffold(
        title = stringResource(Res.string.normal_chess_title),
        onBack = onBack,
        scrollable = true,
    ) {
        Spacer(Modifier.height(8.dp))

        TurnHeader(
            board = board,
            result = result,
            humanResigned = humanResigned,
            mode = mode,
            aiThinking = aiThinking,
            selectedHasDrawMove = selectedHasDrawMove,
        )

        Spacer(Modifier.height(12.dp))

        BoardView(
            board = board,
            selected = selected,
            highlightedTargets = highlightedTargets,
            stalematingTargets = stalematingTargets,
            lastMoveFrom = lastMoveFrom,
            lastMoveTo = lastMoveTo,
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

        if (result == GameResult.ONGOING && !humanResigned) {
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                DefaultButton(
                    onClick = { humanResigned = true },
                    value = stringResource(
                        if (mode == NormalChessMode.VS_CPU) {
                            Res.string.normal_chess_resign
                        } else {
                            Res.string.normal_chess_quit
                        },
                    ),
                )
                DefaultButton(
                    onClick = { resetGame() },
                    value = stringResource(Res.string.normal_chess_new_game),
                )
            }
        } else {
            DefaultButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = { resetGame() },
                value = stringResource(Res.string.normal_chess_new_game),
            )
        }

        Spacer(Modifier.height(16.dp))
    }

    val promotion = pendingPromotion
    if (promotion != null) {
        PromotionDialog(
            sideToMove = board.sideToMove,
            onPicked = { type ->
                val candidates = board.legalMoves().filter {
                    it.from == promotion.from && it.to == promotion.to && it.promotion == type
                }
                val chosen = candidates.firstOrNull()
                pendingPromotion = null
                if (chosen != null) applyMove(chosen)
            },
            onDismiss = {
                pendingPromotion = null
                selected = null
            },
        )
    }
}

private data class PendingPromotion(val from: Square, val to: Square)

@Composable
private fun TurnHeader(
    board: NormalChessBoard,
    result: GameResult,
    humanResigned: Boolean,
    mode: NormalChessMode,
    aiThinking: Boolean,
    selectedHasDrawMove: Boolean,
) {
    val text: String
    val color: Color
    when {
        humanResigned && mode == NormalChessMode.VS_CPU -> {
            text = stringResource(Res.string.normal_chess_checkmate_black)
            color = MaterialTheme.colorScheme.error
        }
        humanResigned -> {
            text = stringResource(
                if (board.sideToMove == ChessColor.WHITE) {
                    Res.string.normal_chess_checkmate_black
                } else {
                    Res.string.normal_chess_checkmate_white
                },
            )
            color = MaterialTheme.colorScheme.error
        }
        result == GameResult.WHITE_WINS -> {
            text = stringResource(Res.string.normal_chess_checkmate_white)
            color = SuccessGreen
        }
        result == GameResult.BLACK_WINS -> {
            text = stringResource(Res.string.normal_chess_checkmate_black)
            color = SuccessGreen
        }
        result == GameResult.DRAW_STALEMATE -> {
            text = stringResource(Res.string.normal_chess_stalemate)
            color = MaterialTheme.colorScheme.onSurface
        }
        result == GameResult.DRAW_FIFTY_MOVE -> {
            text = stringResource(Res.string.normal_chess_draw_50)
            color = MaterialTheme.colorScheme.onSurface
        }
        result == GameResult.DRAW_INSUFFICIENT_MATERIAL -> {
            text = stringResource(Res.string.normal_chess_draw_material)
            color = MaterialTheme.colorScheme.onSurface
        }
        aiThinking -> {
            text = stringResource(Res.string.normal_chess_thinking)
            color = MaterialTheme.colorScheme.onSurfaceVariant
        }
        selectedHasDrawMove -> {
            text = stringResource(Res.string.normal_chess_draw_warning)
            color = ChessWarning
        }
        board.sideToMove == ChessColor.WHITE -> {
            text = stringResource(Res.string.normal_chess_turn_white)
            color = MaterialTheme.colorScheme.onSurface
        }
        else -> {
            text = stringResource(Res.string.normal_chess_turn_black)
            color = MaterialTheme.colorScheme.onSurface
        }
    }
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = color,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun BoardView(
    board: NormalChessBoard,
    selected: Square?,
    highlightedTargets: Set<Square>,
    stalematingTargets: Set<Square>,
    lastMoveFrom: Square?,
    lastMoveTo: Square?,
    interactive: Boolean,
    onSquareTapped: (Square) -> Unit,
) {
    val whiteInCheck = board.isInCheck(ChessColor.WHITE)
    val blackInCheck = board.isInCheck(ChessColor.BLACK)
    PrismCard(
        face = ChessBoardFrame,
        facet = com.inspiredandroid.braincup.ui.theme.PrismFacet.Board,
    ) {
        Column {
            for (row in NORMAL_CHESS_SIZE - 1 downTo 0) {
                Row {
                    for (col in 0 until NORMAL_CHESS_SIZE) {
                        val square = Square(col, row)
                        val piece = board.pieceAt(square)
                        val isLight = (row + col) % 2 == 0
                        val isSelected = selected == square
                        val isTarget = square in highlightedTargets
                        val isStalemateTarget = square in stalematingTargets
                        val isLastMove = square == lastMoveFrom || square == lastMoveTo
                        val showCheckRing = piece?.type == PieceType.KING &&
                            (
                                (piece.color == ChessColor.WHITE && whiteInCheck) ||
                                    (piece.color == ChessColor.BLACK && blackInCheck)
                                )
                        ChessSquareView(
                            piece = piece,
                            isLight = isLight,
                            isSelected = isSelected,
                            isTarget = isTarget,
                            isStalemateTarget = isStalemateTarget,
                            isLastMove = isLastMove,
                            showCheckRing = showCheckRing,
                            enabled = interactive,
                            onClick = { onSquareTapped(square) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChessSquareView(
    piece: Piece?,
    isLight: Boolean,
    isSelected: Boolean,
    isTarget: Boolean,
    isStalemateTarget: Boolean,
    isLastMove: Boolean,
    showCheckRing: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val baseColor = if (isLight) ChessLightSquare else ChessDarkSquare
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(if (isSelected) ChessSelected else baseColor)
            .clickable(enabled = enabled, onClick = onClick)
            .hoverHand(enabled),
        contentAlignment = Alignment.Center,
    ) {
        if (isLastMove && !isSelected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(ChessLastMove),
            )
        }
        if (showCheckRing) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(ChessCheckTint),
            )
        }
        if (isStalemateTarget) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(ChessDrawTint),
            )
        } else if (isTarget && piece != null) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(ChessCaptureTint),
            )
        }
        piece?.let {
            Box(
                modifier = Modifier.size(36.dp),
                contentAlignment = Alignment.Center,
            ) {
                ChessPieceIcon(resource = pieceResource(it.type), isWhite = it.color == ChessColor.WHITE)
            }
        }
        if (isTarget) {
            when {
                isStalemateTarget -> Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(ChessDrawDot),
                )
                piece == null -> Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color.Black.copy(alpha = 0.4f)),
                )
            }
        }
    }
}

@Composable
private fun PromotionDialog(
    sideToMove: ChessColor,
    onPicked: (PieceType) -> Unit,
    onDismiss: () -> Unit,
) {
    val isWhite = sideToMove == ChessColor.WHITE
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("✕")
            }
        },
        title = { Text(stringResource(Res.string.normal_chess_promotion_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PromotionRow(PieceType.QUEEN, Res.string.normal_chess_promotion_queen, isWhite, onPicked)
                PromotionRow(PieceType.ROOK, Res.string.normal_chess_promotion_rook, isWhite, onPicked)
                PromotionRow(PieceType.BISHOP, Res.string.normal_chess_promotion_bishop, isWhite, onPicked)
                PromotionRow(PieceType.KNIGHT, Res.string.normal_chess_promotion_knight, isWhite, onPicked)
            }
        },
    )
}

@Composable
private fun PromotionRow(
    type: PieceType,
    labelRes: org.jetbrains.compose.resources.StringResource,
    isWhite: Boolean,
    onPicked: (PieceType) -> Unit,
) {
    PrismTile(
        face = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .hoverHand()
            .fillMaxWidth(),
        onClick = { onPicked(type) },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                ChessPieceIcon(resource = pieceResource(type), isWhite = isWhite)
            }
            Spacer(Modifier.width(12.dp))
            Text(stringResource(labelRes))
        }
    }
}

private fun pieceResource(type: PieceType): DrawableResource = when (type) {
    PieceType.KING -> Res.drawable.ic_chess_king
    PieceType.QUEEN -> Res.drawable.ic_chess_queen
    PieceType.ROOK -> Res.drawable.ic_chess_rook
    PieceType.BISHOP -> Res.drawable.ic_chess_bishop
    PieceType.KNIGHT -> Res.drawable.ic_chess_knight
    PieceType.PAWN -> Res.drawable.ic_chess_pawn
}
