package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import braincup.composeapp.generated.resources.checkmate_black_answers
import braincup.composeapp.generated.resources.checkmate_black_king_escapes
import braincup.composeapp.generated.resources.checkmate_black_mates_you
import braincup.composeapp.generated.resources.checkmate_find_the_mate
import braincup.composeapp.generated.resources.checkmate_hint
import braincup.composeapp.generated.resources.checkmate_mate_in
import braincup.composeapp.generated.resources.checkmate_mated_detail
import braincup.composeapp.generated.resources.checkmate_next
import braincup.composeapp.generated.resources.checkmate_not_mate
import braincup.composeapp.generated.resources.checkmate_puzzle_number
import braincup.composeapp.generated.resources.checkmate_rating
import braincup.composeapp.generated.resources.checkmate_restart
import braincup.composeapp.generated.resources.checkmate_solved
import braincup.composeapp.generated.resources.checkmate_stalemate
import braincup.composeapp.generated.resources.checkmate_stalemate_detail
import braincup.composeapp.generated.resources.checkmate_stats
import braincup.composeapp.generated.resources.checkmate_title
import braincup.composeapp.generated.resources.checkmate_white_answers
import braincup.composeapp.generated.resources.checkmate_white_king_escapes
import braincup.composeapp.generated.resources.checkmate_white_mates_you
import braincup.composeapp.generated.resources.checkmate_your_move
import braincup.composeapp.generated.resources.normal_chess_turn_black
import braincup.composeapp.generated.resources.normal_chess_turn_white
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.checkmate.CheckmateAttempt
import com.inspiredandroid.braincup.checkmate.CheckmateOutcome
import com.inspiredandroid.braincup.checkmate.CheckmatePuzzle
import com.inspiredandroid.braincup.checkmate.CheckmatePuzzles
import com.inspiredandroid.braincup.chess.PieceColor
import com.inspiredandroid.braincup.chess.Square
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.ChunkyCheck
import com.inspiredandroid.braincup.ui.components.ChunkyCross
import com.inspiredandroid.braincup.ui.components.DefaultButton
import com.inspiredandroid.braincup.ui.components.NormalChessBoardView
import com.inspiredandroid.braincup.ui.components.PrismCard
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.ScreenPreviewHost
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

private const val REPLY_DELAY_MILLIS = 900L

@Composable
fun CheckmatePlayScreen(
    puzzleId: String,
    storage: UserStorage,
    onSolved: () -> Unit,
    onNext: (puzzleId: String) -> Unit,
    onBack: () -> Unit,
) {
    val puzzles by produceState(CheckmatePuzzles.cached) { value = CheckmatePuzzles.load() }
    val all = puzzles ?: return
    val index = all.indexOfFirst { it.id == puzzleId }
    if (index < 0) {
        LaunchedEffect(Unit) { onBack() }
        return
    }
    CheckmatePlayContent(
        puzzle = all[index],
        total = all.size,
        storage = storage,
        onSolved = onSolved,
        onNext = all.getOrNull(index + 1)?.let { next -> { onNext(next.id) } },
        onBack = onBack,
    )
}

@Composable
fun CheckmatePlayContent(
    puzzle: CheckmatePuzzle,
    total: Int,
    storage: UserStorage,
    onSolved: () -> Unit,
    onNext: (() -> Unit)?,
    onBack: () -> Unit,
    initialAttempt: CheckmateAttempt = puzzle.start(),
) {
    var attempt by remember(puzzle) { mutableStateOf(initialAttempt) }
    var selected by remember(puzzle) { mutableStateOf<Square?>(null) }
    var restarted by remember(puzzle) { mutableStateOf(false) }
    var stats by remember { mutableStateOf(CheckmateStats.read(storage)) }

    LaunchedEffect(attempt) {
        if (attempt.awaitingReply) {
            delay(REPLY_DELAY_MILLIS)
            attempt = puzzle.answer(attempt)
        }
    }

    val outcome = attempt.outcome
    LaunchedEffect(outcome) {
        if (outcome == null) return@LaunchedEffect
        val solved = outcome == CheckmateOutcome.SOLVED
        storage.recordCheckmateResult(puzzle.id, solved = solved, firstGo = !restarted)
        stats = CheckmateStats.read(storage)
        if (solved) onSolved()
    }

    val interactive = outcome == null && !attempt.awaitingReply
    val legalFromSelected = remember(attempt, selected) {
        selected
            ?.takeIf { interactive }
            ?.let { from -> attempt.board.legalMoves().filter { it.from == from }.map { it.to }.toImmutableSet() }
            ?: persistentSetOf()
    }

    val onSquareTapped: (Square) -> Unit = remember(puzzle) {
        { square ->
            val current = attempt
            if (current.outcome == null && !current.awaitingReply) {
                val from = selected
                val move = from?.let { puzzle.moveFor(current, it, square) }
                if (move != null) {
                    selected = null
                    attempt = puzzle.play(current, move)
                } else {
                    val piece = current.board.pieceAt(square)
                    selected = if (piece != null && piece.color == puzzle.solverColor && square != from) square else null
                }
            }
        }
    }

    AppScaffold(
        title = stringResource(Res.string.checkmate_title),
        onBack = onBack,
        provideCompactHeight = true,
    ) {
        Spacer(Modifier.height(8.dp))

        PromptHeader(puzzle = puzzle, attempt = attempt)

        Spacer(Modifier.height(12.dp))

        NormalChessBoardView(
            board = attempt.board,
            selected = selected,
            highlightedTargets = legalFromSelected,
            lastMoveFrom = attempt.lastMove?.from,
            lastMoveTo = attempt.lastMove?.to,
            interactive = interactive,
            onSquareTapped = onSquareTapped,
            flipped = puzzle.solverColor == PieceColor.BLACK,
            verticalChrome = 250.dp,
        )

        Spacer(Modifier.height(16.dp))

        StatusLine(puzzle = puzzle, attempt = attempt)

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (attempt.solverMoves > 0) {
                DefaultButton(
                    onClick = {
                        attempt = puzzle.start()
                        selected = null
                        restarted = true
                    },
                    value = stringResource(Res.string.checkmate_restart),
                )
            }
            if (onNext != null) {
                DefaultButton(
                    onClick = onNext,
                    value = stringResource(Res.string.checkmate_next),
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(Res.string.checkmate_puzzle_number, puzzle.number, total) +
                " · " + stringResource(Res.string.checkmate_rating, puzzle.rating),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Text(
            text = stringResource(Res.string.checkmate_stats, stats.solved, stats.streak, stats.best),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(Modifier.height(16.dp))
    }
}

private data class CheckmateStats(val solved: Int, val streak: Int, val best: Int) {
    companion object {
        fun read(storage: UserStorage) = CheckmateStats(
            solved = storage.getSolvedCheckmateIds().size,
            streak = storage.getCheckmateStreak(),
            best = storage.getCheckmateBestStreak(),
        )
    }
}

@Composable
private fun PromptHeader(puzzle: CheckmatePuzzle, attempt: CheckmateAttempt) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(
                    if (puzzle.solverColor == PieceColor.WHITE) {
                        Res.string.normal_chess_turn_white
                    } else {
                        Res.string.normal_chess_turn_black
                    },
                ),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = pluralStringResource(Res.plurals.checkmate_mate_in, puzzle.mateIn, puzzle.mateIn),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Primary,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for (k in 0 until puzzle.mateIn) {
                MovePip(
                    number = k + 1,
                    played = k < attempt.solverMoves,
                    current = k == attempt.solverMoves && attempt.outcome == null,
                )
            }
        }
    }
}

@Composable
private fun MovePip(number: Int, played: Boolean, current: Boolean) {
    val muted = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    Box(
        modifier = Modifier
            .size(30.dp)
            .background(if (played) Primary else Color.Transparent, CircleShape)
            .border(1.5.dp, if (played || current) Primary else muted, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = number.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = when {
                played -> Color.White
                current -> Primary
                else -> muted
            },
        )
    }
}

@Composable
private fun StatusLine(puzzle: CheckmatePuzzle, attempt: CheckmateAttempt) {
    val whiteSolves = puzzle.solverColor == PieceColor.WHITE
    val outcome = attempt.outcome
    if (outcome == null) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(
                    when {
                        attempt.awaitingReply && whiteSolves -> Res.string.checkmate_black_answers
                        attempt.awaitingReply -> Res.string.checkmate_white_answers
                        attempt.solverMoves > 0 -> Res.string.checkmate_your_move
                        else -> Res.string.checkmate_find_the_mate
                    },
                ),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(Res.string.checkmate_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
        return
    }

    val solved = outcome == CheckmateOutcome.SOLVED
    val tone = if (solved) SuccessGreen else MaterialTheme.colorScheme.error
    val verdict = stringResource(
        when (outcome) {
            CheckmateOutcome.SOLVED -> Res.string.checkmate_solved
            CheckmateOutcome.NOT_MATE -> Res.string.checkmate_not_mate
            CheckmateOutcome.MATED -> if (whiteSolves) Res.string.checkmate_black_mates_you else Res.string.checkmate_white_mates_you
            CheckmateOutcome.STALEMATE -> Res.string.checkmate_stalemate
        },
    )
    val detail = when (outcome) {
        CheckmateOutcome.SOLVED -> null
        CheckmateOutcome.NOT_MATE -> stringResource(
            if (whiteSolves) Res.string.checkmate_black_king_escapes else Res.string.checkmate_white_king_escapes,
        )
        CheckmateOutcome.MATED -> stringResource(Res.string.checkmate_mated_detail)
        CheckmateOutcome.STALEMATE -> stringResource(Res.string.checkmate_stalemate_detail)
    }
    PrismCard(
        face = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .widthIn(max = 480.dp)
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(tone, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                if (solved) {
                    ChunkyCheck(color = Color.White, modifier = Modifier.size(18.dp))
                } else {
                    ChunkyCross(color = Color.White, modifier = Modifier.size(16.dp))
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = verdict,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = tone,
                )
                if (detail != null) {
                    Text(
                        text = detail,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@DevicePreviews
@Composable
private fun CheckmatePlayScreenPreview() {
    ScreenPreviewHost {
        val storage = remember { UserStorage.forPreview() }
        val puzzle = remember {
            CheckmatePuzzle.parse("UMLhc|6k1/1r3ppp/p1q5/3p4/P1n5/6QP/5PP1/1R4K1|w|3|uB JB 5B SC BC|-1|1308|F>JB")
                .first()
        }
        CheckmatePlayContent(
            puzzle = puzzle,
            total = 60,
            storage = storage,
            onSolved = {},
            onNext = {},
            onBack = {},
        )
    }
}
