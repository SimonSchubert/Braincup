package com.inspiredandroid.braincup.ui.screens.games

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.app.*
import com.inspiredandroid.braincup.ui.components.*
import com.inspiredandroid.braincup.ui.theme.FlashCrowdBlue
import com.inspiredandroid.braincup.ui.theme.FlashCrowdYellow
import com.inspiredandroid.braincup.ui.theme.FlashCrowdYellowBottom
import com.inspiredandroid.braincup.ui.theme.FlashCrowdYellowSide
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration.Companion.milliseconds
import androidx.compose.ui.graphics.Color as ComposeColor

@Composable
internal fun ColumnScope.FlashCrowdContent(
    uiState: FlashCrowdUiState,
    onAnswer: (String) -> Unit,
) {
    key(uiState.roundKey) {
        var showingDots by remember { mutableStateOf(true) }
        var visible by remember { mutableStateOf(true) }
        val alpha = animateFloatAsState(
            targetValue = if (visible) 1f else 0f,
            animationSpec = tween(200),
            label = "flashCrowdAlpha",
        )

        LaunchedEffect(Unit) {
            delay(750.milliseconds)
            visible = false
            delay(200.milliseconds)
            showingDots = false
            visible = true
        }

        if (showingDots) {
            FlashCrowdDotsRow(
                uiState,
                FlashCrowdBlue,
                FlashCrowdYellow,
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .graphicsLayer { this.alpha = alpha.value },
            )
        } else {
            Text(
                text = stringResource(Res.string.game_flash_crowd_which_more),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .graphicsLayer { this.alpha = alpha.value },
            )
            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .widthIn(max = 400.dp)
                    .align(Alignment.CenterHorizontally)
                    .graphicsLayer { this.alpha = alpha.value },
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                PrismTile(
                    face = FlashCrowdBlue,
                    onClick = { onAnswer("left") },
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .hoverHand(),
                ) {
                    Text(
                        text = stringResource(Res.string.game_flash_crowd_blue),
                        style = MaterialTheme.typography.titleLarge,
                        color = ComposeColor.White,
                    )
                }
                PrismTile(
                    face = FlashCrowdYellow,
                    side = FlashCrowdYellowSide,
                    bottom = FlashCrowdYellowBottom,
                    onClick = { onAnswer("right") },
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .hoverHand(),
                ) {
                    Text(
                        text = stringResource(Res.string.game_flash_crowd_yellow),
                        style = MaterialTheme.typography.titleLarge,
                        color = ComposeColor.Black,
                    )
                }
            }
        }
    }
}

@Composable
private fun FlashCrowdDotsRow(
    uiState: FlashCrowdUiState,
    blueColor: ComposeColor,
    yellowColor: ComposeColor,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .widthIn(max = 400.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Canvas(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f),
        ) {
            uiState.leftDots.forEach { dot ->
                drawPrismCircle(
                    center = Offset(dot.x * size.width, dot.y * size.height),
                    radius = dot.radius * size.width,
                    face = blueColor,
                )
            }
        }
        Canvas(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f),
        ) {
            uiState.rightDots.forEach { dot ->
                drawPrismCircle(
                    center = Offset(dot.x * size.width, dot.y * size.height),
                    radius = dot.radius * size.width,
                    face = yellowColor,
                    side = FlashCrowdYellowSide,
                    bottom = FlashCrowdYellowBottom,
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun FlashCrowdContentPreview() {
    GamePreviewHost {
        FlashCrowdContent(
            uiState = FlashCrowdUiState(
                roundKey = 1,
                leftDots = persistentListOf(
                    FlashCrowdUiState.Dot(0.3f, 0.4f, 0.08f),
                    FlashCrowdUiState.Dot(0.6f, 0.5f, 0.1f),
                ),
                rightDots = persistentListOf(
                    FlashCrowdUiState.Dot(0.4f, 0.3f, 0.09f),
                    FlashCrowdUiState.Dot(0.5f, 0.6f, 0.07f),
                    FlashCrowdUiState.Dot(0.7f, 0.4f, 0.08f),
                ),
            ),
            onAnswer = {},
        )
    }
}
