package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.game_tower_of_hanoi_desc
import com.inspiredandroid.braincup.ui.theme.HanoiBaseColor
import com.inspiredandroid.braincup.ui.theme.HanoiDiskColors
import com.inspiredandroid.braincup.ui.theme.HanoiPegColor
import com.inspiredandroid.braincup.ui.theme.Primary
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

private const val DemoDiskCount = 3

// Optimal 3-disk solution: (from, to) peg indices.
private val OptimalMoves = listOf(
    0 to 2,
    0 to 1,
    2 to 1,
    0 to 2,
    1 to 0,
    1 to 2,
    0 to 2,
)

private const val SelectMillis = 400L
private const val MoveMillis = 350L
private const val SolvedHoldMillis = 1300L
private const val ResetPauseMillis = 500L

/**
 * Animated tutorial for Tower of Hanoi: a 3-disk stack that solves itself with the classic
 * optimal sequence, highlighting the source peg then the destination on each step.
 */
@Composable
fun TowerOfHanoiDemo(modifier: Modifier = Modifier) {
    var pegs by remember {
        mutableStateOf(listOf(listOf(3, 2, 1), emptyList(), emptyList()))
    }
    var selectedPeg by remember { mutableIntStateOf(-1) }

    LaunchedEffect(Unit) {
        while (true) {
            pegs = listOf(listOf(3, 2, 1), emptyList(), emptyList())
            selectedPeg = -1
            delay(ResetPauseMillis)

            for ((from, to) in OptimalMoves) {
                selectedPeg = from
                delay(SelectMillis)
                val next = pegs.map { it.toMutableList() }
                if (next[from].isNotEmpty()) {
                    val disk = next[from].removeAt(next[from].lastIndex)
                    next[to].add(disk)
                }
                pegs = next.map { it.toList() }
                selectedPeg = to
                delay(MoveMillis)
                selectedPeg = -1
                delay(120L)
            }
            delay(SolvedHoldMillis)
        }
    }

    val compact = LocalIsCompactHeight.current
    val boardHeight = if (compact) 120.dp else 160.dp
    val pegWidth = if (compact) 64.dp else 80.dp

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            for (pegIndex in 0 until 3) {
                DemoPeg(
                    disks = pegs[pegIndex],
                    selected = selectedPeg == pegIndex,
                    width = pegWidth,
                    height = boardHeight,
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(Res.string.game_tower_of_hanoi_desc),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}

@Composable
private fun DemoPeg(
    disks: List<Int>,
    selected: Boolean,
    width: androidx.compose.ui.unit.Dp,
    height: androidx.compose.ui.unit.Dp,
) {
    val diskHeight = 14.dp
    val poleWidth = 6.dp
    val maxDiskWidth = width - 6.dp
    val minDiskWidth = width * 0.38f
    val face = if (selected) Primary else MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.35f)
    val poleColor = if (selected) Primary else HanoiPegColor

    Column(
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(10.dp))
            .background(face)
            .padding(horizontal = 3.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Box(
                modifier = Modifier
                    .width(poleWidth)
                    .fillMaxHeight(0.9f)
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(poleWidth / 2))
                    .background(poleColor),
            )
            Column(
                modifier = Modifier.align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                disks.asReversed().forEach { size ->
                    val fraction = (size - 1).toFloat() / (DemoDiskCount - 1).toFloat()
                    val diskWidth = minDiskWidth + (maxDiskWidth - minDiskWidth) * fraction
                    val color = HanoiDiskColors[(size - 1).coerceIn(0, HanoiDiskColors.lastIndex)]
                    Box(
                        modifier = Modifier
                            .width(diskWidth)
                            .height(diskHeight)
                            .clip(RoundedCornerShape(diskHeight / 2))
                            .background(color),
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(if (selected) Primary else HanoiBaseColor),
        )
    }
}
