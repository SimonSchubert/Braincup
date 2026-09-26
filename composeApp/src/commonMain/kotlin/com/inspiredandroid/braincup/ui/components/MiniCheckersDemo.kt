package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import braincup.composeapp.generated.resources.checkers_howto
import com.inspiredandroid.braincup.checkers.CheckersBoard
import com.inspiredandroid.braincup.checkers.CheckersMove
import com.inspiredandroid.braincup.checkers.CheckersSide
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

private data class MiniCheckersDemoFrame(
    val board: CheckersBoard,
    val selectedPath: List<Int>,
    val lastMove: CheckersMove?,
    val holdMillis: Long,
)

// The black man takes both white men in one move, tapped a landing at a time as on the real board.
private val MiniCheckersDemoFrames: List<MiniCheckersDemoFrame> = run {
    val start = CheckersBoard.fromRows(
        listOf(
            "......",
            "......",
            "...w..",
            "......",
            ".w....",
            "b.....",
        ),
        CheckersSide.BLACK,
    )
    val doubleJump = start.legalMoves().single()
    listOf(
        MiniCheckersDemoFrame(start, emptyList(), null, 1000),
        MiniCheckersDemoFrame(start, doubleJump.path.take(1), null, 700),
        MiniCheckersDemoFrame(start, doubleJump.path.take(2), null, 700),
        MiniCheckersDemoFrame(start.apply(doubleJump), emptyList(), doubleJump, 1600),
    )
}

@Composable
fun MiniCheckersDemo(modifier: Modifier = Modifier) {
    var frame by remember { mutableStateOf(MiniCheckersDemoFrames.first()) }

    LaunchedEffect(Unit) {
        while (true) {
            for (next in MiniCheckersDemoFrames) {
                frame = next
                delay(next.holdMillis)
            }
        }
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        CheckersBoardGrid(
            board = frame.board,
            legalMoves = frame.board.legalMoves(),
            selectedPath = frame.selectedPath,
            lastMove = frame.lastMove,
            interactive = false,
            boardSide = 240.dp,
            onSquareTapped = {},
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(Res.string.checkers_howto),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}
