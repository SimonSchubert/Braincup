package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.reversi_black_wins
import braincup.composeapp.generated.resources.reversi_draw
import braincup.composeapp.generated.resources.reversi_lost
import braincup.composeapp.generated.resources.reversi_new_game
import braincup.composeapp.generated.resources.reversi_pass_black
import braincup.composeapp.generated.resources.reversi_pass_cpu
import braincup.composeapp.generated.resources.reversi_pass_white
import braincup.composeapp.generated.resources.reversi_pass_you
import braincup.composeapp.generated.resources.reversi_quit
import braincup.composeapp.generated.resources.reversi_resign
import braincup.composeapp.generated.resources.reversi_resigned
import braincup.composeapp.generated.resources.reversi_resigned_black
import braincup.composeapp.generated.resources.reversi_resigned_white
import braincup.composeapp.generated.resources.reversi_thinking
import braincup.composeapp.generated.resources.reversi_title
import braincup.composeapp.generated.resources.reversi_turn_black
import braincup.composeapp.generated.resources.reversi_turn_cpu
import braincup.composeapp.generated.resources.reversi_turn_white
import braincup.composeapp.generated.resources.reversi_turn_you
import braincup.composeapp.generated.resources.reversi_white_wins
import braincup.composeapp.generated.resources.reversi_won
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.reversi.Disc
import com.inspiredandroid.braincup.reversi.REVERSI_SIZE
import com.inspiredandroid.braincup.reversi.ReversiAi
import com.inspiredandroid.braincup.reversi.ReversiBoard
import com.inspiredandroid.braincup.reversi.ReversiDifficulty
import com.inspiredandroid.braincup.reversi.ReversiMode
import com.inspiredandroid.braincup.reversi.ReversiResult
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.ColorPrismCell
import com.inspiredandroid.braincup.ui.components.DefaultButton
import com.inspiredandroid.braincup.ui.components.LocalScaffoldBodyHeight
import com.inspiredandroid.braincup.ui.components.PrismCard
import com.inspiredandroid.braincup.ui.components.XpGainedChip
import com.inspiredandroid.braincup.ui.components.hoverHand
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.ScreenPreviewHost
import com.inspiredandroid.braincup.ui.theme.PrismFacet
import com.inspiredandroid.braincup.ui.theme.ReversiBlackDisc
import com.inspiredandroid.braincup.ui.theme.ReversiBoardFrame
import com.inspiredandroid.braincup.ui.theme.ReversiFelt
import com.inspiredandroid.braincup.ui.theme.ReversiGridLine
import com.inspiredandroid.braincup.ui.theme.ReversiLastMove
import com.inspiredandroid.braincup.ui.theme.ReversiLegalDot
import com.inspiredandroid.braincup.ui.theme.ReversiWhiteDisc
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock

private const val MIN_AI_THINK_MILLIS = 500L

/** Long enough to read the notice before the board moves on without you. */
private const val PASS_NOTICE_MILLIS = 1100L

/** In VS_CPU the human is black and so always moves first. In VS_HUMAN both sides are human and
 *  these only name the colours. */
private val HUMAN = Disc.BLACK
private val CPU = Disc.WHITE

@Composable
fun ReversiPlayScreen(
    mode: ReversiMode,
    difficulty: ReversiDifficulty,
    storage: UserStorage,
    onBack: () -> Unit,
) {
    var board by remember { mutableStateOf(ReversiBoard.startingPosition()) }
    var lastMove by remember { mutableStateOf<Int?>(null) }
    var passedSide by remember { mutableStateOf<Disc?>(null) }
    var aiThinking by remember { mutableStateOf(false) }
    var resignedBy by remember { mutableStateOf<Disc?>(null) }
    var xpGained by remember { mutableIntStateOf(0) }

    val ai = remember(difficulty) { ReversiAi(difficulty) }

    fun resetGame() {
        board = ReversiBoard.startingPosition()
        lastMove = null
        passedSide = null
        aiThinking = false
        resignedBy = null
        xpGained = 0
    }

    // Drives everything that is not a human tap: the automatic pass when a side is stuck, and the
    // CPU's reply. Re-keying on `board` is what makes consecutive CPU moves work — after a pass
    // the board changed but it is still the CPU to move, so the effect simply runs again.
    LaunchedEffect(board, resignedBy, mode) {
        if (resignedBy != null || board.isGameOver()) return@LaunchedEffect
        if (board.legalMoves().isEmpty()) {
            // Both modes need this: a stuck human has nothing to tap, so the board would sit there.
            passedSide = board.sideToMove
            delay(PASS_NOTICE_MILLIS)
            if (currentCoroutineContext().isActive) board = board.passTurn()
            return@LaunchedEffect
        }
        if (mode != ReversiMode.VS_CPU) return@LaunchedEffect
        if (board.sideToMove != CPU) return@LaunchedEffect
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
            if (currentCoroutineContext().isActive && move != null) {
                lastMove = move
                passedSide = null
                board = board.apply(move)
            }
        } finally {
            aiThinking = false
        }
    }

    val result = board.result()

    // Pass-and-play earns nothing: there is no opponent to have beaten, and it would be the
    // easiest XP in the app.
    LaunchedEffect(result, mode) {
        if (mode == ReversiMode.VS_CPU && xpGained == 0 && result != ReversiResult.ONGOING) {
            xpGained = storage.awardReversiXp(difficulty, result).xpGained
        }
    }

    val humanCanInteract = !aiThinking &&
        resignedBy == null &&
        result == ReversiResult.ONGOING &&
        (mode == ReversiMode.VS_HUMAN || board.sideToMove == HUMAN)
    val legalMoves: ImmutableSet<Int> = if (humanCanInteract) {
        board.legalMoves().toImmutableSet()
    } else {
        persistentSetOf()
    }

    fun onCellTapped(index: Int) {
        // Re-checked against the live board rather than the captured flags: a tap can land while
        // the pass or the CPU reply is mid-flight.
        if (aiThinking || resignedBy != null) return
        if (board.result() != ReversiResult.ONGOING) return
        if (mode == ReversiMode.VS_CPU && board.sideToMove != HUMAN) return
        if (index !in board.legalMoves()) return
        lastMove = index
        passedSide = null
        board = board.apply(index)
    }

    AppScaffold(
        title = stringResource(Res.string.reversi_title),
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
            passedSide = passedSide,
        )

        Spacer(Modifier.height(8.dp))

        ScoreRow(black = board.count(Disc.BLACK), white = board.count(Disc.WHITE))

        Spacer(Modifier.height(12.dp))

        BoardView(
            board = board,
            legalMoves = legalMoves,
            lastMove = lastMove,
            interactive = humanCanInteract,
            onCellTapped = ::onCellTapped,
        )

        Spacer(Modifier.height(16.dp))

        if (xpGained > 0) {
            XpGainedChip(
                xpGained = xpGained,
                modifier = Modifier.align(Alignment.CenterHorizontally).widthIn(max = 200.dp),
            )
            Spacer(Modifier.height(16.dp))
        }

        if (result == ReversiResult.ONGOING && resignedBy == null) {
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                DefaultButton(
                    // Vs the CPU the human concedes whoever's clock it is, including mid-search.
                    // Pass-and-play has no such asymmetry, so the side to move is the one giving up.
                    onClick = {
                        resignedBy = if (mode == ReversiMode.VS_CPU) HUMAN else board.sideToMove
                    },
                    value = stringResource(
                        if (mode == ReversiMode.VS_CPU) Res.string.reversi_resign else Res.string.reversi_quit,
                    ),
                )
                DefaultButton(
                    onClick = { resetGame() },
                    value = stringResource(Res.string.reversi_new_game),
                )
            }
        } else {
            DefaultButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = { resetGame() },
                value = stringResource(Res.string.reversi_new_game),
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun StatusHeader(
    mode: ReversiMode,
    board: ReversiBoard,
    result: ReversiResult,
    resignedBy: Disc?,
    aiThinking: Boolean,
    passedSide: Disc?,
) {
    val black = board.count(Disc.BLACK)
    val white = board.count(Disc.WHITE)
    val vsCpu = mode == ReversiMode.VS_CPU
    val winner = when (result) {
        ReversiResult.BLACK_WINS -> Disc.BLACK
        ReversiResult.WHITE_WINS -> Disc.WHITE
        else -> null
    }
    val text: String
    val color: Color
    when {
        // Resigning gets its own wording rather than the disc counts. You can resign while ahead,
        // and "White wins 10 to 20" is nonsense.
        resignedBy != null -> {
            text = when {
                vsCpu -> stringResource(Res.string.reversi_resigned)
                resignedBy == Disc.BLACK -> stringResource(Res.string.reversi_resigned_black)
                else -> stringResource(Res.string.reversi_resigned_white)
            }
            color = if (vsCpu) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        }
        winner == Disc.BLACK -> {
            text = if (vsCpu) {
                stringResource(Res.string.reversi_won, black, white)
            } else {
                stringResource(Res.string.reversi_black_wins, black, white)
            }
            color = if (vsCpu) SuccessGreen else MaterialTheme.colorScheme.onSurface
        }
        winner == Disc.WHITE -> {
            text = if (vsCpu) {
                stringResource(Res.string.reversi_lost, white, black)
            } else {
                stringResource(Res.string.reversi_white_wins, white, black)
            }
            color = if (vsCpu) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        }
        result == ReversiResult.DRAW -> {
            text = stringResource(Res.string.reversi_draw, black)
            color = MaterialTheme.colorScheme.onSurface
        }
        aiThinking -> {
            text = stringResource(Res.string.reversi_thinking)
            color = MaterialTheme.colorScheme.onSurfaceVariant
        }
        // A pass leaves the turn indicator apparently unchanged, so it has to say so outright.
        passedSide != null -> {
            text = when {
                !vsCpu && passedSide == Disc.BLACK -> stringResource(Res.string.reversi_pass_black)
                !vsCpu -> stringResource(Res.string.reversi_pass_white)
                passedSide == CPU -> stringResource(Res.string.reversi_pass_cpu)
                else -> stringResource(Res.string.reversi_pass_you)
            }
            color = MaterialTheme.colorScheme.onSurfaceVariant
        }
        else -> {
            text = when {
                !vsCpu && board.sideToMove == Disc.BLACK -> stringResource(Res.string.reversi_turn_black)
                !vsCpu -> stringResource(Res.string.reversi_turn_white)
                board.sideToMove == HUMAN -> stringResource(Res.string.reversi_turn_you)
                else -> stringResource(Res.string.reversi_turn_cpu)
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
private fun ScoreRow(black: Int, white: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ScoreChip(face = ReversiBlackDisc, count = black)
        Spacer(Modifier.width(24.dp))
        ScoreChip(face = ReversiWhiteDisc, count = white)
    }
}

@Composable
private fun ScoreChip(face: Color, count: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        ColorPrismCell(face = face, facet = PrismFacet.Dot, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun BoardView(
    board: ReversiBoard,
    legalMoves: ImmutableSet<Int>,
    lastMove: Int?,
    interactive: Boolean,
    onCellTapped: (Int) -> Unit,
) {
    val scaffoldBodyHeight = LocalScaffoldBodyHeight.current
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        // Header, score row, spacers and the action buttons take roughly this much.
        val verticalChrome = 180.dp
        val heightBudget = ((scaffoldBodyHeight ?: maxHeight) - verticalChrome).coerceAtLeast(160.dp)
        val boardSide = minOf(maxWidth, heightBudget, 420.dp)
        val cellSize = boardSide / REVERSI_SIZE

        PrismCard(
            face = ReversiBoardFrame,
            facet = PrismFacet.Board,
            modifier = Modifier.align(Alignment.Center),
        ) {
            Column {
                for (row in 0 until REVERSI_SIZE) {
                    Row {
                        for (col in 0 until REVERSI_SIZE) {
                            val index = row * REVERSI_SIZE + col
                            BoardCell(
                                size = cellSize,
                                disc = board.discAt(index),
                                isLegal = index in legalMoves,
                                isLastMove = index == lastMove,
                                enabled = interactive,
                                onClick = { onCellTapped(index) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BoardCell(
    size: Dp,
    disc: Disc?,
    isLegal: Boolean,
    isLastMove: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val clickable = enabled && isLegal
    Box(
        modifier = Modifier
            .size(size)
            .background(ReversiGridLine)
            .clickable(enabled = clickable, onClick = onClick)
            .hoverHand(clickable),
        contentAlignment = Alignment.Center,
    ) {
        Box(Modifier.matchParentSize().padding(1.dp).background(ReversiFelt))
        if (isLastMove) {
            Box(Modifier.matchParentSize().padding(1.dp).background(ReversiLastMove))
        }
        when {
            disc != null -> ColorPrismCell(
                face = if (disc == Disc.BLACK) ReversiBlackDisc else ReversiWhiteDisc,
                facet = PrismFacet.Dot,
                modifier = Modifier.size(size * 0.78f),
            )
            isLegal -> Box(
                Modifier
                    .size(size * 0.26f)
                    .clip(CircleShape)
                    .background(ReversiLegalDot),
            )
        }
    }
}

@DevicePreviews
@Composable
private fun ReversiPlayScreenPreview() {
    ScreenPreviewHost {
        val storage = remember { UserStorage.forPreview() }
        ReversiPlayScreen(
            mode = ReversiMode.VS_CPU,
            difficulty = ReversiDifficulty.NORMAL,
            storage = storage,
            onBack = {},
        )
    }
}
