package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.game_solo_chess_desc
import com.inspiredandroid.braincup.games.SoloChessGame
import com.inspiredandroid.braincup.games.minichess.PieceType
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

private const val SoloChessDemoSize = 3
private val SoloChessDemoCell = 56.dp

private data class SoloChessFrame(
    val pieces: Map<Int, PieceType>,
    /** cell -> captures the piece still has (out of two); drives the charge pips. */
    val captures: Map<Int, Int>,
    val selected: Int?,
    val target: Int?,
    val holdMillis: Long,
)

// A fixed 3x3 puzzle that solves itself on a loop: the queen captures the knight, then the king
// captures the queen, leaving only the king (which can never be captured). The charge pips tick down
// as pieces capture, demonstrating the "no piece may capture more than twice" rule.
private val SoloChessDemoFrames = listOf(
    SoloChessFrame(mapOf(0 to PieceType.QUEEN, 2 to PieceType.KNIGHT, 4 to PieceType.KING), mapOf(0 to 2, 2 to 2, 4 to 2), null, null, 900),
    SoloChessFrame(mapOf(0 to PieceType.QUEEN, 2 to PieceType.KNIGHT, 4 to PieceType.KING), mapOf(0 to 2, 2 to 2, 4 to 2), 0, 2, 800),
    SoloChessFrame(mapOf(2 to PieceType.QUEEN, 4 to PieceType.KING), mapOf(2 to 1, 4 to 2), null, null, 700),
    SoloChessFrame(mapOf(2 to PieceType.QUEEN, 4 to PieceType.KING), mapOf(2 to 1, 4 to 2), 4, 2, 800),
    SoloChessFrame(mapOf(2 to PieceType.KING), mapOf(2 to 1), null, null, 1500),
)

/**
 * Animated tutorial board for Solo Chess: a fixed 3x3 puzzle that solves itself on a loop. Each step
 * highlights the moving piece and the piece it captures, mirroring the real board's select-then-tap
 * flow, then settles to a single king.
 */
@Composable
fun SoloChessDemo(modifier: Modifier = Modifier) {
    var frame by remember { mutableStateOf(SoloChessDemoFrames.first()) }

    LaunchedEffect(Unit) {
        while (true) {
            for (next in SoloChessDemoFrames) {
                frame = next
                delay(next.holdMillis)
            }
            delay(500)
        }
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        PrismCard(face = ChessBoardFrame, facet = com.inspiredandroid.braincup.ui.theme.PrismFacet.Board) {
            Column {
                for (row in 0 until SoloChessDemoSize) {
                    Row {
                        for (col in 0 until SoloChessDemoSize) {
                            val index = row * SoloChessDemoSize + col
                            val type = frame.pieces[index]
                            Box(
                                modifier = Modifier
                                    .size(SoloChessDemoCell)
                                    .background(
                                        if (frame.selected == index) {
                                            ChessSelected
                                        } else if ((row + col) % 2 == 0) {
                                            ChessLightSquare
                                        } else {
                                            ChessDarkSquare
                                        },
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (type == PieceType.KING && frame.selected != index) {
                                    Box(modifier = Modifier.matchParentSize().background(ChessDrawTint))
                                }
                                if (frame.target == index) {
                                    Box(modifier = Modifier.matchParentSize().background(ChessCaptureTint))
                                }
                                if (type != null) {
                                    ChessPieceIcon(
                                        resource = chessPieceResource(type),
                                        isWhite = true,
                                        figureSize = 42.dp,
                                    )
                                    SoloChessCapturePips(
                                        remaining = (frame.captures[index] ?: 0).coerceIn(0, SoloChessGame.MAX_CAPTURES),
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(3.dp)
                                            .size(width = 24.dp, height = 11.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(Res.string.game_solo_chess_desc),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}
