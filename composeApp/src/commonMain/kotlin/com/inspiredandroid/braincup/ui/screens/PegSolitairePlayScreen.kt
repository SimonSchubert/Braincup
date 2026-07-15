package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.peg_solitaire_howto
import braincup.composeapp.generated.resources.peg_solitaire_pegs_left
import braincup.composeapp.generated.resources.peg_solitaire_restart
import braincup.composeapp.generated.resources.peg_solitaire_stuck
import braincup.composeapp.generated.resources.peg_solitaire_title
import braincup.composeapp.generated.resources.peg_solitaire_undo
import braincup.composeapp.generated.resources.peg_solitaire_won
import braincup.composeapp.generated.resources.peg_solitaire_won_perfect
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.pegsolitaire.PEG_BOARD_SIZE
import com.inspiredandroid.braincup.pegsolitaire.PegCell
import com.inspiredandroid.braincup.pegsolitaire.PegJump
import com.inspiredandroid.braincup.pegsolitaire.PegSolitaireBoard
import com.inspiredandroid.braincup.pegsolitaire.PegSolitaireResult
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.ChessSelected
import com.inspiredandroid.braincup.ui.components.ColorPrismCell
import com.inspiredandroid.braincup.ui.components.DefaultButton
import com.inspiredandroid.braincup.ui.components.PrismCard
import com.inspiredandroid.braincup.ui.components.XpGainedChip
import com.inspiredandroid.braincup.ui.components.darken
import com.inspiredandroid.braincup.ui.components.hoverHand
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.ScreenPreviewHost
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.PrismFacet
import com.inspiredandroid.braincup.ui.theme.PrismShade
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import org.jetbrains.compose.resources.stringResource

private val PegBoardFrame = Color(0xFF5D4037)
private val PegBoardSurface = Color(0xFFD7CCC8)
private val PegHole = Color(0xFF8D6E63)
private val PegTarget = Color(0xFF5D4037)

@Composable
fun PegSolitairePlayScreen(
    storage: UserStorage,
    onBack: () -> Unit,
) {
    var board by remember { mutableStateOf(PegSolitaireBoard.englishStarting()) }
    var history by remember { mutableStateOf<List<PegSolitaireBoard>>(emptyList()) }
    var selected by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var xpGained by remember { mutableIntStateOf(0) }
    var awardedThisGame by remember { mutableStateOf(false) }

    fun resetGame() {
        board = PegSolitaireBoard.englishStarting()
        history = emptyList()
        selected = null
        awardedThisGame = false
        // Keep xpGained so the chip can still show if they won then restarted; clear on restart
        // for a clean new session.
        xpGained = 0
    }

    fun applyJump(jump: PegJump) {
        history = history + board
        board = board.apply(jump)
        selected = null
    }

    fun undo() {
        val previous = history.lastOrNull() ?: return
        history = history.dropLast(1)
        board = previous
        selected = null
    }

    val result = board.result()
    val interactive = result == PegSolitaireResult.ONGOING
    // Highlight targets from the live board; the click handler re-queries legality too so a jump
    // never depends on a stale composition-time list.
    val targetHoles: Set<Pair<Int, Int>> = selected?.let { (r, c) ->
        if (interactive) {
            board.legalJumpsFrom(r, c).map { it.toRow to it.toCol }.toSet()
        } else {
            emptySet()
        }
    } ?: emptySet()

    LaunchedEffect(result) {
        if (awardedThisGame) return@LaunchedEffect
        when (result) {
            PegSolitaireResult.WON, PegSolitaireResult.WON_PERFECT -> {
                xpGained = storage.awardPegSolitaireWinXp().xpGained
                if (result == PegSolitaireResult.WON_PERFECT) {
                    storage.markPegSolitairePerfect()
                }
                awardedThisGame = true
            }
            else -> Unit
        }
    }

    fun onHoleTapped(row: Int, col: Int) {
        // Re-read state on every tap (same pattern as NormalChessPlayScreen) so we never act on
        // a stale legal-move list from a previous composition.
        if (board.result() != PegSolitaireResult.ONGOING) return
        val cell = board.cellAt(row, col)
        if (cell == PegCell.INVALID) return

        val sel = selected
        if (sel != null) {
            val jump = board.legalJumpsFrom(sel.first, sel.second)
                .firstOrNull { it.toRow == row && it.toCol == col }
            if (jump != null) {
                applyJump(jump)
                return
            }
            if (sel.first == row && sel.second == col) {
                selected = null
                return
            }
        }
        if (cell == PegCell.PEG) {
            selected = row to col
        } else {
            selected = null
        }
    }

    AppScaffold(
        title = stringResource(Res.string.peg_solitaire_title),
        onBack = onBack,
        scrollable = true,
    ) {
        Spacer(Modifier.height(8.dp))

        StatusHeader(board = board, result = result)

        Spacer(Modifier.height(12.dp))

        PegBoardView(
            board = board,
            selected = selected,
            targets = targetHoles,
            interactive = interactive,
            onHoleTapped = ::onHoleTapped,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(Modifier.height(16.dp))

        if (xpGained > 0) {
            XpGainedChip(
                xpGained = xpGained,
                modifier = Modifier.align(Alignment.CenterHorizontally).widthIn(max = 200.dp),
            )
            Spacer(Modifier.height(16.dp))
        }

        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (history.isNotEmpty()) {
                DefaultButton(
                    onClick = { undo() },
                    value = stringResource(Res.string.peg_solitaire_undo),
                )
            }
            DefaultButton(
                onClick = { resetGame() },
                value = stringResource(Res.string.peg_solitaire_restart),
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun StatusHeader(board: PegSolitaireBoard, result: PegSolitaireResult) {
    val (text, color) = when (result) {
        PegSolitaireResult.WON_PERFECT ->
            stringResource(Res.string.peg_solitaire_won_perfect) to SuccessGreen
        PegSolitaireResult.WON ->
            stringResource(Res.string.peg_solitaire_won) to SuccessGreen
        PegSolitaireResult.STUCK ->
            stringResource(Res.string.peg_solitaire_stuck) to MaterialTheme.colorScheme.error
        PegSolitaireResult.ONGOING ->
            stringResource(Res.string.peg_solitaire_howto) to MaterialTheme.colorScheme.onSurfaceVariant
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.peg_solitaire_pegs_left, board.pegCount()),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = color,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
    }
}

@Composable
private fun PegBoardView(
    board: PegSolitaireBoard,
    selected: Pair<Int, Int>?,
    targets: Set<Pair<Int, Int>>,
    interactive: Boolean,
    onHoleTapped: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        val boardSize = maxWidth.coerceAtMost(360.dp)
        PrismCard(
            face = PegBoardFrame,
            facet = PrismFacet.Board,
            modifier = Modifier
                .size(boardSize)
                .align(Alignment.Center),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PegBoardSurface),
            ) {
                for (row in 0 until PEG_BOARD_SIZE) {
                    Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        for (col in 0 until PEG_BOARD_SIZE) {
                            val cellType = board.cellAt(row, col)
                            if (cellType == PegCell.INVALID) {
                                Spacer(Modifier.weight(1f).fillMaxSize())
                            } else {
                                // Full cell is the hit target. Empty holes need a filled background
                                // so Compose registers taps reliably.
                                val isSelected = selected?.first == row && selected.second == col
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxSize()
                                        .background(
                                            if (isSelected) ChessSelected else PegBoardSurface,
                                        )
                                        .clickable(enabled = interactive) { onHoleTapped(row, col) }
                                        .hoverHand(interactive),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    PegCellContent(
                                        cell = cellType,
                                        isTarget = (row to col) in targets,
                                        isSelected = isSelected,
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

/**
 * Recessed hole (small dark prism) with a Primary prism peg on top when occupied — same
 * chamfered [ColorPrismCell] look as Lights Out / sliding-puzzle pieces.
 */
@Composable
private fun PegCellContent(
    cell: PegCell,
    isTarget: Boolean,
    isSelected: Boolean,
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize().padding(3.dp),
        contentAlignment = Alignment.Center,
    ) {
        val side = minOf(maxWidth, maxHeight)
        val holeSize = side * 0.78f
        val pegSize = side * 0.72f
        val targetSize = side * 0.28f

        // Board socket under the peg (slightly darker prism, sunken look).
        ColorPrismCell(
            face = PegHole.darken(PrismShade.Side),
            side = PegHole.darken(PrismShade.Bottom),
            bottom = PegHole.darken(0.35f),
            facet = PrismFacet.Dot,
            modifier = Modifier.size(holeSize),
        )

        when {
            cell == PegCell.PEG -> {
                ColorPrismCell(
                    face = if (isSelected) Primary.darken(PrismShade.Side) else Primary,
                    facet = PrismFacet.Cell,
                    modifier = Modifier.size(pegSize),
                )
            }
            isTarget -> {
                // Legal landing: small dark prism marker in the empty socket.
                ColorPrismCell(
                    face = PegTarget,
                    facet = PrismFacet.Dot,
                    modifier = Modifier.size(targetSize),
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun PegSolitairePlayScreenPreview() {
    ScreenPreviewHost {
        PegSolitairePlayScreen(
            storage = UserStorage.forPreview(),
            onBack = {},
        )
    }
}
