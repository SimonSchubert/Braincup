package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.normal_sudoku_erase
import braincup.composeapp.generated.resources.normal_sudoku_not_solved
import braincup.composeapp.generated.resources.normal_sudoku_solved
import braincup.composeapp.generated.resources.normal_sudoku_title
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.normalsudoku.NormalSudokuPuzzles
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.PrismTile
import com.inspiredandroid.braincup.ui.components.XpGainedChip
import com.inspiredandroid.braincup.ui.components.hoverHand
import com.inspiredandroid.braincup.ui.theme.OnPrimaryContainer
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.PrimaryContainer
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import com.inspiredandroid.braincup.ui.theme.numberFontFamily
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

private const val GRID = 9
private const val BLOCK = 3
private val CellSeparator = 1.dp
private val PadGap = 6.dp

@Composable
fun NormalSudokuPlayScreen(
    puzzleId: String,
    storage: UserStorage,
    onCompleted: () -> Unit,
    onBack: () -> Unit,
) {
    val puzzle = remember(puzzleId) { NormalSudokuPuzzles.byId(puzzleId) }
    if (puzzle == null) {
        LaunchedEffect(Unit) { onBack() }
        return
    }

    val clueDigits = remember(puzzle) { puzzle.clues.map { it.digitToInt() } }
    val initial = remember(puzzle) {
        val saved = storage.getNormalSudokuProgress(puzzle.id)
        val source = saved ?: puzzle.clues
        MutableList(81) { source[it].digitToInt() }
    }
    val board = remember(puzzle) { mutableStateListOf<Int>().apply { addAll(initial) } }
    var selectedIndex by remember(puzzle) {
        val firstEmpty = clueDigits.indexOfFirst { it == 0 }
        mutableStateOf(if (firstEmpty == -1) 0 else firstEmpty)
    }
    var lastWrongFlash by remember(puzzle) { mutableStateOf(0L) }
    var solved by remember(puzzle) { mutableStateOf(false) }
    var xpGained by remember(puzzle) { mutableStateOf(0) }

    fun persist() {
        if (solved) return
        storage.saveNormalSudokuProgress(puzzle.id, board.joinToString("") { it.toString() })
    }

    fun applyDigit(digit: Int) {
        if (solved) return
        val pos = selectedIndex
        if (pos !in board.indices) return
        if (clueDigits[pos] != 0) return
        board[pos] = digit
        persist()
        if (board.none { it == 0 }) {
            val asString = board.joinToString("") { it.toString() }
            if (asString == puzzle.solution) {
                // Award XP first; awardNormalSudokuCompletionXp dedupes by checking the
                // completed set BEFORE we add to it via markNormalSudokuCompleted.
                xpGained = storage.awardNormalSudokuCompletionXp(puzzle.id, puzzle.difficulty).xpGained
                solved = true
                storage.markNormalSudokuCompleted(puzzle.id)
            } else {
                lastWrongFlash = currentTimeMillis()
            }
        } else {
            selectedIndex = nextEmpty(pos, board, clueDigits)
        }
    }

    LaunchedEffect(solved) {
        if (solved) {
            // Give the XP chip animation time to land before we navigate away.
            delay(if (xpGained > 0) 2200 else 1200)
            onCompleted()
        }
    }

    AppScaffold(
        title = stringResource(Res.string.normal_sudoku_title),
        onBack = onBack,
        scrollable = false,
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            val landscape = maxWidth > maxHeight && maxHeight < 600.dp
            val compact = maxWidth < 400.dp || maxHeight < 600.dp
            val blockSep = if (compact) 8.dp else 12.dp
            val outerFrame = if (compact) 6.dp else 8.dp
            val screenPadding = if (compact) 6.dp else 8.dp

            if (landscape) {
                val boardSize = minOf(
                    maxHeight - screenPadding * 2,
                    maxWidth * 0.55f - screenPadding * 2,
                )
                val padWidth = minOf(
                    maxWidth - boardSize - screenPadding * 2 - 16.dp,
                    boardSize * 0.55f,
                )
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(screenPadding),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SudokuBoard9x9(
                        board = board,
                        clueDigits = clueDigits,
                        selectedIndex = selectedIndex,
                        solved = solved,
                        outerFrame = outerFrame,
                        blockSep = blockSep,
                        modifier = Modifier.size(boardSize),
                        onCellClick = { idx -> if (!solved && clueDigits[idx] == 0) selectedIndex = idx },
                    )
                    DigitPad(
                        columns = 3,
                        enabled = !solved,
                        modifier = Modifier.width(padWidth),
                        onDigit = ::applyDigit,
                        onErase = { applyDigit(0) },
                    )
                }
            } else {
                val boardSize = minOf(
                    maxWidth - screenPadding * 2,
                    maxHeight * 0.62f,
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(screenPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
                ) {
                    SudokuBoard9x9(
                        board = board,
                        clueDigits = clueDigits,
                        selectedIndex = selectedIndex,
                        solved = solved,
                        outerFrame = outerFrame,
                        blockSep = blockSep,
                        modifier = Modifier.size(boardSize),
                        onCellClick = { idx -> if (!solved && clueDigits[idx] == 0) selectedIndex = idx },
                    )
                    DigitPad(
                        columns = 9,
                        enabled = !solved,
                        modifier = Modifier.width(boardSize),
                        onDigit = ::applyDigit,
                        onErase = { applyDigit(0) },
                    )
                }
            }
        }

        StatusBanner(solved = solved, wrongFlashKey = lastWrongFlash, xpGained = xpGained)
    }
}

@Composable
private fun SudokuBoard9x9(
    board: List<Int>,
    clueDigits: List<Int>,
    selectedIndex: Int,
    solved: Boolean,
    outerFrame: Dp,
    blockSep: Dp,
    modifier: Modifier = Modifier,
    onCellClick: (Int) -> Unit,
) {
    val gridLineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)

    Box(modifier = modifier.background(gridLineColor)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(outerFrame),
        ) {
            for (row in 0 until GRID) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                ) {
                    for (col in 0 until GRID) {
                        val index = row * GRID + col
                        val isClue = clueDigits[index] != 0
                        val value = board[index]
                        SudokuCell(
                            value = if (value == 0) "" else value.toString(),
                            isClue = isClue,
                            isSelected = !solved && index == selectedIndex,
                            isSolved = solved,
                            onClick = { onCellClick(index) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                        )
                        if (col < GRID - 1) {
                            Spacer(
                                Modifier.width(
                                    if ((col + 1) % BLOCK == 0) blockSep else CellSeparator,
                                ),
                            )
                        }
                    }
                }
                if (row < GRID - 1) {
                    Spacer(
                        Modifier.height(
                            if ((row + 1) % BLOCK == 0) blockSep else CellSeparator,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun SudokuCell(
    value: String,
    isClue: Boolean,
    isSelected: Boolean,
    isSolved: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor = when {
        isSolved -> SuccessGreen.copy(alpha = 0.22f)
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        isClue -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }
    val textColor = when {
        isSolved -> SuccessGreen
        isClue -> MaterialTheme.colorScheme.onSurface
        else -> Primary
    }
    BoxWithConstraints(modifier = modifier) {
        val size = minOf(maxWidth, maxHeight)
        PrismTile(
            face = containerColor,
            isClickable = !isClue && !isSolved,
            isSelected = isSelected,
            modifier = Modifier
                .size(size)
                .hoverHand(!isClue && !isSolved),
            onClick = onClick,
        ) {
            if (value.isNotEmpty()) {
                Text(
                    text = value,
                    fontSize = (size.value * 0.45f).sp,
                    fontFamily = numberFontFamily(),
                    fontWeight = if (isClue) FontWeight.Bold else FontWeight.SemiBold,
                    color = textColor,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun DigitPad(
    columns: Int,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onDigit: (Int) -> Unit,
    onErase: () -> Unit,
) {
    val digits = (1..9).toList()
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PadGap),
    ) {
        digits.chunked(columns).forEach { rowDigits ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(PadGap),
            ) {
                rowDigits.forEach { digit ->
                    DigitTile(
                        label = digit.toString(),
                        enabled = enabled,
                        onClick = { onDigit(digit) },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                    )
                }
                repeat(columns - rowDigits.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
        EraseTile(
            enabled = enabled,
            onClick = onErase,
            modifier = Modifier.fillMaxWidth(0.7f),
        )
    }
}

@Composable
private fun DigitTile(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
        val size = minOf(maxWidth, maxHeight)
        PrismTile(
            // Brand-pinned light-purple face paired with the dark-purple OnPrimaryContainer text so
            // the digits stay high-contrast in every theme. Using colorScheme.primaryContainer here
            // resolved to a dark purple in dark mode, leaving dark text on a dark tile.
            face = PrimaryContainer,
            modifier = Modifier
                .size(size)
                .hoverHand(enabled)
                .alpha(if (enabled) 1f else 0.6f),
            isClickable = enabled,
            onClick = onClick,
        ) {
            Text(
                text = label,
                fontSize = (size.value * 0.5f).sp,
                fontFamily = numberFontFamily(),
                fontWeight = FontWeight.Bold,
                color = OnPrimaryContainer,
            )
        }
    }
}

@Composable
private fun EraseTile(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PrismTile(
        face = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
            .heightIn(min = 40.dp)
            .hoverHand(enabled)
            .alpha(if (enabled) 1f else 0.6f),
        isClickable = enabled,
        onClick = onClick,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            Text(
                text = stringResource(Res.string.normal_sudoku_erase),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun StatusBanner(solved: Boolean, wrongFlashKey: Long, xpGained: Int) {
    var showWrong by remember { mutableStateOf(false) }
    LaunchedEffect(wrongFlashKey) {
        if (wrongFlashKey > 0L) {
            showWrong = true
            delay(1400)
            showWrong = false
        }
    }
    val message: String? = when {
        solved -> stringResource(Res.string.normal_sudoku_solved)
        showWrong -> stringResource(Res.string.normal_sudoku_not_solved)
        else -> null
    }
    val color: Color = if (solved) SuccessGreen else Primary
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (message != null) {
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                color = color,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
        }
        if (solved && xpGained > 0) {
            Spacer(Modifier.height(8.dp))
            XpGainedChip(xpGained = xpGained)
        }
    }
}

private fun nextEmpty(from: Int, board: List<Int>, clueDigits: List<Int>): Int {
    val total = board.size
    for (step in 1..total) {
        val idx = (from + step) % total
        if (clueDigits[idx] == 0 && board[idx] == 0) return idx
    }
    return from
}

private fun currentTimeMillis(): Long = kotlin.time.Clock.System.now().toEpochMilliseconds()
