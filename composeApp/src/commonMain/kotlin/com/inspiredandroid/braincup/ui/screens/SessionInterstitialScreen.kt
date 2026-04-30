package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.*
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
import com.inspiredandroid.braincup.ui.components.BrandedCard
import com.inspiredandroid.braincup.ui.components.PrimaryActionButton
import com.inspiredandroid.braincup.ui.components.ProgressDots
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
        ProgressDots(
            currentIndex = nextGameIndex,
            total = totalGames,
            currentColor = Color(nextGame.accentColor),
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

        BrandedCard(
            modifier = Modifier
                .widthIn(max = 420.dp)
                .padding(horizontal = 24.dp)
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            containerColor = Color(nextGame.accentColor),
            cornerRadius = 24,
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(Res.string.session_next_up).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = NextUpLabelColor,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(nextGame.displayNameRes),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = NextUpTitleColor,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(nextGame.descriptionRes),
                style = MaterialTheme.typography.bodyMedium,
                color = NextUpDescriptionColor,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(Modifier.height(32.dp))

        PrimaryActionButton(
            onClick = onContinue,
            value = stringResource(Res.string.session_continue),
        )
    }
}

private val NextUpLabelColor = Color.Black.copy(alpha = 0.55f)
private val NextUpTitleColor = Color.Black.copy(alpha = 0.9f)
private val NextUpDescriptionColor = Color.Black.copy(alpha = 0.7f)
