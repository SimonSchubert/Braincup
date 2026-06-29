package com.inspiredandroid.braincup.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.chess_move_bishop
import braincup.composeapp.generated.resources.chess_move_king
import braincup.composeapp.generated.resources.chess_move_knight
import braincup.composeapp.generated.resources.chess_move_pawn
import braincup.composeapp.generated.resources.chess_move_queen
import braincup.composeapp.generated.resources.chess_move_rook
import braincup.composeapp.generated.resources.chess_piece_bishop
import braincup.composeapp.generated.resources.chess_piece_king
import braincup.composeapp.generated.resources.chess_piece_knight
import braincup.composeapp.generated.resources.chess_piece_pawn
import braincup.composeapp.generated.resources.chess_piece_queen
import braincup.composeapp.generated.resources.chess_piece_rook
import braincup.composeapp.generated.resources.mini_chess_moves_title
import com.inspiredandroid.braincup.games.minichess.PieceType
import com.inspiredandroid.braincup.ui.theme.Primary
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.abs
import kotlin.math.roundToInt

private const val DemoBoardSize = 4
private val DemoCellSize = 52.dp
private val DemoDotSize = 16.dp

// How long the piece rests on a square (showing its legal moves) before flowing to the next one.
private const val RestMillis = 700L

// How long the promoted queen is shown before the pawn demo restarts.
private const val PromotionHoldMillis = 1200L

// One piece demos for this long before the board auto-advances to the next piece.
private const val AutoAdvanceMillis = 6000L

private data class DemoCell(val col: Int, val row: Int)

/**
 * What the demo shows for one piece: a [tour] of squares it visits in order. For the sliding/stepping
 * pieces every consecutive pair (including last -> first) is a legal move, so the animation loops
 * seamlessly without snapping back to the start. The pawn is choreographed separately: it captures an
 * enemy pawn on its first move, marches up the file, and promotes to a queen on the far rank.
 */
private data class DemoPlan(
    val type: PieceType,
    val tour: List<DemoCell>,
    val isKnight: Boolean = false,
)

private val RookDirs = listOf(1 to 0, -1 to 0, 0 to 1, 0 to -1)
private val BishopDirs = listOf(1 to 1, 1 to -1, -1 to 1, -1 to -1)
private val QueenDirs = RookDirs + BishopDirs
private val KingDeltas = QueenDirs
private val KnightDeltas = listOf(
    1 to 2,
    2 to 1,
    -1 to 2,
    -2 to 1,
    1 to -2,
    2 to -1,
    -1 to -2,
    -2 to -1,
)

// Pawn path: start, the enemy pawn it captures diagonally, then two forward steps onto the last rank.
private val PawnTour = listOf(DemoCell(1, 0), DemoCell(2, 1), DemoCell(2, 2), DemoCell(2, 3))
private val PawnEnemyCell = PawnTour[1]

private fun DemoCell.inBounds() = col in 0 until DemoBoardSize && row in 0 until DemoBoardSize
private fun DemoCell.isLastRank() = row == DemoBoardSize - 1

/** All squares reachable by stepping once per delta (king, knight). */
private fun steps(from: DemoCell, deltas: List<Pair<Int, Int>>): List<DemoCell> = deltas.map { (dc, dr) -> DemoCell(from.col + dc, from.row + dr) }.filter { it.inBounds() }

/** Every square along each ray until the board edge (rook, bishop, queen). */
private fun slide(from: DemoCell, dirs: List<Pair<Int, Int>>): List<DemoCell> {
    val out = mutableListOf<DemoCell>()
    for ((dc, dr) in dirs) {
        var cell = DemoCell(from.col + dc, from.row + dr)
        while (cell.inBounds()) {
            out += cell
            cell = DemoCell(cell.col + dc, cell.row + dr)
        }
    }
    return out
}

/** Squares the piece on [cell] could move to, used for the dots that follow the piece. */
private fun legalMoves(type: PieceType, cell: DemoCell): List<DemoCell> = when (type) {
    PieceType.KING -> steps(cell, KingDeltas)
    PieceType.QUEEN -> slide(cell, QueenDirs)
    PieceType.ROOK -> slide(cell, RookDirs)
    PieceType.BISHOP -> slide(cell, BishopDirs)
    PieceType.KNIGHT -> steps(cell, KnightDeltas)
    PieceType.PAWN -> listOf(DemoCell(cell.col, cell.row + 1)).filter { it.inBounds() }
}

// Hand-authored closed loops where each consecutive move (and the wrap from last back to first) is a
// legal move for the piece, so the tour flows continuously on the 4x4 board. The pawn uses [PawnTour].
private fun demoPlan(type: PieceType): DemoPlan {
    fun cells(vararg pairs: Pair<Int, Int>) = pairs.map { DemoCell(it.first, it.second) }
    return when (type) {
        PieceType.KING -> DemoPlan(type, cells(1 to 1, 2 to 1, 2 to 2, 1 to 2))
        PieceType.QUEEN -> DemoPlan(type, cells(1 to 1, 3 to 3, 3 to 1, 1 to 3))
        PieceType.ROOK -> DemoPlan(type, cells(1 to 1, 3 to 1, 3 to 3, 1 to 3))
        PieceType.BISHOP -> DemoPlan(type, cells(1 to 1, 0 to 2, 1 to 3, 2 to 2))
        PieceType.KNIGHT -> DemoPlan(type, cells(1 to 1, 0 to 3, 2 to 2, 3 to 0), isKnight = true)
        PieceType.PAWN -> DemoPlan(type, PawnTour)
    }
}

private fun pieceNameRes(type: PieceType): StringResource = when (type) {
    PieceType.KING -> Res.string.chess_piece_king
    PieceType.QUEEN -> Res.string.chess_piece_queen
    PieceType.ROOK -> Res.string.chess_piece_rook
    PieceType.BISHOP -> Res.string.chess_piece_bishop
    PieceType.KNIGHT -> Res.string.chess_piece_knight
    PieceType.PAWN -> Res.string.chess_piece_pawn
}

private fun moveDescRes(type: PieceType): StringResource = when (type) {
    PieceType.KING -> Res.string.chess_move_king
    PieceType.QUEEN -> Res.string.chess_move_queen
    PieceType.ROOK -> Res.string.chess_move_rook
    PieceType.BISHOP -> Res.string.chess_move_bishop
    PieceType.KNIGHT -> Res.string.chess_move_knight
    PieceType.PAWN -> Res.string.chess_move_pawn
}

private val MoveDemoCaptions = persistentListOf(
    Res.string.chess_move_king,
    Res.string.chess_move_queen,
    Res.string.chess_move_rook,
    Res.string.chess_move_bishop,
    Res.string.chess_move_knight,
    Res.string.chess_move_pawn,
)

private fun glideDuration(from: DemoCell, to: DemoCell): Int {
    val distance = maxOf(abs(from.col - to.col), abs(from.row - to.row))
    return (170 * distance).coerceIn(320, 760)
}

/**
 * Animated tutorial board: a 4x4 chessboard that cycles through every piece type. Each piece flows
 * through a few legal moves in one continuous animation, dotting the squares it can reach from its
 * current position. The pawn captures an enemy pawn and promotes to a queen. Pieces auto-advance every
 * few seconds; tapping a chip jumps to that piece.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChessMoveDemo(modifier: Modifier = Modifier) {
    val pieces = remember { listOf(PieceType.KING, PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT, PieceType.PAWN) }
    var selectedIndex by remember { mutableStateOf(0) }
    val type = pieces[selectedIndex]
    val plan = remember(type) { demoPlan(type) }

    val cellPx = with(LocalDensity.current) { DemoCellSize.toPx() }
    fun offsetOf(cell: DemoCell) = Offset(cell.col * cellPx, (DemoBoardSize - 1 - cell.row) * cellPx)

    // The piece rests on currentCell between glides; the dots are derived from it so they follow along.
    // displayType lets the pawn turn into a queen on promotion; enemyVisible hides the captured pawn.
    var currentCell by remember(plan) { mutableStateOf(plan.tour.first()) }
    var displayType by remember(plan) { mutableStateOf(type) }
    var enemyVisible by remember(plan) { mutableStateOf(type == PieceType.PAWN) }
    val moveDots = remember(currentCell, type) { legalMoves(type, currentCell) }

    // The moving piece is one overlay that linearly interpolates from legFrom to legTo as progress
    // runs 0 -> 1, so straight, diagonal and knight legs all reuse the same path. Seeded on the home
    // square so the first painted frame already shows the piece in place.
    val legFrom = remember(plan, cellPx) { mutableStateOf(offsetOf(plan.tour.first())) }
    val legTo = remember(plan, cellPx) { mutableStateOf(offsetOf(plan.tour.first())) }
    val progress = remember(plan) { Animatable(1f) }
    val dotsAlpha = remember(plan) { Animatable(0f) }
    val pieceAlpha = remember(plan) { Animatable(1f) }

    // Auto-advance. Keyed on selectedIndex so a manual chip tap resets the timer (pause-then-resume).
    LaunchedEffect(selectedIndex) {
        delay(AutoAdvanceMillis)
        selectedIndex = (selectedIndex + 1) % pieces.size
    }

    LaunchedEffect(plan, cellPx) {
        suspend fun glide(from: DemoCell, to: DemoCell, durationMillis: Int) {
            legFrom.value = offsetOf(from)
            legTo.value = offsetOf(to)
            progress.snapTo(0f)
            progress.animateTo(1f, tween(durationMillis))
        }

        suspend fun showDotsThenMove(from: DemoCell, body: suspend () -> Unit) {
            currentCell = from
            dotsAlpha.animateTo(1f, tween(220))
            delay(RestMillis)
            dotsAlpha.animateTo(0f, tween(160))
            body()
        }

        if (type == PieceType.PAWN) {
            val tour = plan.tour
            while (true) {
                // Reset to the start: pawn on its square, an enemy pawn waiting to be captured.
                displayType = PieceType.PAWN
                enemyVisible = true
                currentCell = tour.first()
                legFrom.value = offsetOf(tour.first())
                legTo.value = offsetOf(tour.first())
                progress.snapTo(1f)
                pieceAlpha.snapTo(0f)
                pieceAlpha.animateTo(1f, tween(220))

                for (i in 0 until tour.size - 1) {
                    val from = tour[i]
                    val to = tour[i + 1]
                    showDotsThenMove(from) {
                        glide(from, to, glideDuration(from, to))
                    }
                    currentCell = to
                    if (i == 0) enemyVisible = false // first move captured the enemy pawn
                    if (to.isLastRank()) displayType = PieceType.QUEEN // promotion
                }

                delay(PromotionHoldMillis)
                pieceAlpha.animateTo(0f, tween(220)) // fade out; the loop fades a fresh pawn back in
            }
        } else {
            displayType = type
            enemyVisible = false
            currentCell = plan.tour.first()
            legFrom.value = offsetOf(plan.tour.first())
            legTo.value = offsetOf(plan.tour.first())
            progress.snapTo(1f)
            pieceAlpha.snapTo(1f)

            var index = 0
            while (true) {
                val from = plan.tour[index]
                val nextIndex = (index + 1) % plan.tour.size
                val to = plan.tour[nextIndex]
                showDotsThenMove(from) {
                    if (plan.isKnight) {
                        // Trace the L: long leg first, then the short turn, so the jump reads clearly.
                        val corner = DemoCell(to.col, from.row)
                        glide(from, corner, 280)
                        glide(corner, to, 280)
                    } else {
                        glide(from, to, glideDuration(from, to))
                    }
                }
                currentCell = to
                index = nextIndex
            }
        }
    }

    val pieceOffset = Offset(
        legFrom.value.x + (legTo.value.x - legFrom.value.x) * progress.value,
        legFrom.value.y + (legTo.value.y - legFrom.value.y) * progress.value,
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.mini_chess_moves_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(16.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            pieces.forEachIndexed { index, pieceType ->
                val isSelected = index == selectedIndex
                PrismTile(
                    face = if (isSelected) Primary else MaterialTheme.colorScheme.surfaceVariant,
                    isSelected = isSelected,
                    onClick = { selectedIndex = index },
                    modifier = Modifier.size(52.dp).hoverHand(),
                ) {
                    ChessPieceIcon(resource = chessPieceResource(pieceType), isWhite = true, figureSize = 34.dp)
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        PrismCard(face = ChessBoardFrame, facet = com.inspiredandroid.braincup.ui.theme.PrismFacet.Board) {
            Box(Modifier.size(DemoCellSize * DemoBoardSize)) {
                Column {
                    for (row in (DemoBoardSize - 1) downTo 0) {
                        Row {
                            for (col in 0 until DemoBoardSize) {
                                val isLight = (row + col) % 2 == 0
                                Box(
                                    Modifier
                                        .size(DemoCellSize)
                                        .background(if (isLight) ChessLightSquare else ChessDarkSquare),
                                )
                            }
                        }
                    }
                }
                Box(Modifier.fillMaxSize().alpha(dotsAlpha.value)) {
                    moveDots.forEach { cell -> MoveDot(cell, cellPx) }
                }
                if (type == PieceType.PAWN && enemyVisible) {
                    EnemyPawn(PawnEnemyCell, cellPx)
                }
                Box(
                    modifier = Modifier
                        .size(DemoCellSize)
                        .offset { IntOffset(pieceOffset.x.roundToInt(), pieceOffset.y.roundToInt()) }
                        .alpha(pieceAlpha.value),
                    contentAlignment = Alignment.Center,
                ) {
                    ChessPieceIcon(resource = chessPieceResource(displayType), isWhite = true)
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(pieceNameRes(type)),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        DemoCaption(current = moveDescRes(type), all = MoveDemoCaptions)
    }
}

private fun cellOffset(cell: DemoCell, cellPx: Float) = IntOffset((cell.col * cellPx).roundToInt(), ((DemoBoardSize - 1 - cell.row) * cellPx).roundToInt())

@Composable
private fun BoxScope.MoveDot(cell: DemoCell, cellPx: Float) {
    Box(
        modifier = Modifier
            .size(DemoCellSize)
            .offset { cellOffset(cell, cellPx) },
        contentAlignment = Alignment.Center,
    ) {
        ColorPrismCell(face = ChessLegalDot, facet = com.inspiredandroid.braincup.ui.theme.PrismFacet.Dot, modifier = Modifier.size(DemoDotSize))
    }
}

@Composable
private fun BoxScope.EnemyPawn(cell: DemoCell, cellPx: Float) {
    Box(
        modifier = Modifier
            .size(DemoCellSize)
            .offset { cellOffset(cell, cellPx) },
    ) {
        Box(Modifier.fillMaxSize().background(ChessCaptureTint))
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            ChessPieceIcon(resource = chessPieceResource(PieceType.PAWN), isWhite = false)
        }
    }
}
