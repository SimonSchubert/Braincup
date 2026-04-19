package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.DefaultButton
import com.inspiredandroid.braincup.ui.theme.Primary
import org.jetbrains.compose.resources.stringResource

@Composable
fun SessionInterstitialScreen(
    nextGame: GameType,
    nextGameIndex: Int,
    totalGames: Int,
    runningTotal: Int,
    onContinue: () -> Unit,
    onExit: () -> Unit,
) {
    AppScaffold(
        title = stringResource(Res.string.daily_challenge_title),
        onBack = onExit,
        scrollable = false,
    ) {
        SessionProgressDots(
            currentIndex = nextGameIndex,
            total = totalGames,
            accentColor = Color(nextGame.accentColor),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = stringResource(Res.string.session_progress, nextGameIndex + 1, totalGames),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        if (nextGameIndex > 0) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(Res.string.session_running_total, runningTotal),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }

        Spacer(Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(nextGame.accentColor),
            ),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .widthIn(max = 420.dp)
                .padding(horizontal = 24.dp)
                .align(Alignment.CenterHorizontally),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(Res.string.session_next_up).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black.copy(alpha = 0.55f),
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(nextGame.displayNameRes),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = stringResource(nextGame.descriptionRes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        DefaultButton(
            onClick = onContinue,
            modifier = Modifier
                .widthIn(max = 420.dp)
                .padding(horizontal = 24.dp),
            value = stringResource(Res.string.session_continue),
        )
    }
}

@Composable
private fun SessionProgressDots(
    currentIndex: Int,
    total: Int,
    accentColor: Color,
    modifier: Modifier = Modifier,
) {
    val mutedColor = MaterialTheme.colorScheme.surfaceContainerHighest
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(total) { index ->
            val isCurrent = index == currentIndex
            val isCompleted = index < currentIndex
            val size = if (isCurrent) 12.dp else 8.dp
            val color = when {
                isCurrent -> accentColor
                isCompleted -> Primary
                else -> mutedColor
            }
            Box(
                modifier = Modifier
                    .size(size)
                    .background(color, CircleShape),
            )
        }
    }
}
