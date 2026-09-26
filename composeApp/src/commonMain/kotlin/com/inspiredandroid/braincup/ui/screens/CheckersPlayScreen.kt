package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.inspiredandroid.braincup.checkers.CheckersAi
import com.inspiredandroid.braincup.checkers.CheckersBoard
import com.inspiredandroid.braincup.checkers.CheckersDifficulty
import com.inspiredandroid.braincup.checkers.CheckersMode
import com.inspiredandroid.braincup.checkers.CheckersMove
import com.inspiredandroid.braincup.checkers.CheckersResult
import com.inspiredandroid.braincup.checkers.CheckersSide
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.CheckersBoardGrid
import com.inspiredandroid.braincup.ui.components.CheckersTap
import com.inspiredandroid.braincup.ui.components.DefaultButton
import com.inspiredandroid.braincup.ui.components.LocalScaffoldBodyHeight
import com.inspiredandroid.braincup.ui.components.XpGainedChip
import com.inspiredandroid.braincup.ui.components.resolveCheckersTap
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.ScreenPreviewHost
import com.inspiredandroid.braincup.ui.theme.CheckersMustCaptureRing
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
    val mustCapture = legalMoves.firstOrNull()?.isCapture == true

    fun onSquareTapped(index: Int) {
        // Re-checked against the live board rather than the captured flags: a tap can land while
        // the CPU reply is mid-flight.
        if (aiThinking || resignedBy != null) return
        if (board.result() != CheckersResult.ONGOING) return
        if (mode == CheckersMode.VS_CPU && board.sideToMove != HUMAN) return
        when (val tap = resolveCheckersTap(board.legalMoves(), selectedPath, index)) {
            is CheckersTap.Play -> applyMove(tap.move)
            is CheckersTap.Select -> selectedPath = tap.path
        }
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
            legalMoves = legalMoves,
            selectedPath = selectedPath,
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
    legalMoves: List<CheckersMove>,
    selectedPath: List<Int>,
    lastMove: CheckersMove?,
    interactive: Boolean,
    onSquareTapped: (Int) -> Unit,
) {
    val scaffoldBodyHeight = LocalScaffoldBodyHeight.current
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        // Header, spacers and the action buttons take roughly this much.
        val verticalChrome = 140.dp
        val heightBudget = ((scaffoldBodyHeight ?: maxHeight) - verticalChrome).coerceAtLeast(160.dp)
        CheckersBoardGrid(
            board = board,
            legalMoves = legalMoves,
            selectedPath = selectedPath,
            lastMove = lastMove,
            interactive = interactive,
            boardSide = minOf(maxWidth, heightBudget, 480.dp),
            onSquareTapped = onSquareTapped,
            modifier = Modifier.align(Alignment.Center),
        )
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
