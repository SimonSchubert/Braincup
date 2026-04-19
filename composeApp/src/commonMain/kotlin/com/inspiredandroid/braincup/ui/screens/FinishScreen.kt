package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.DefaultButton
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.PrimaryContainer
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun FinishScreen(
    gameType: GameType,
    score: Int,
    isNewHighscore: Boolean,
    answeredAllCorrect: Boolean,
    highscore: Int,
    onPlayRandom: () -> Unit,
    onPlayAgain: () -> Unit,
    onMenu: () -> Unit,
) {
    AppScaffold(
        title = stringResource(gameType.displayNameRes),
        onBack = onMenu,
        scrollable = false,
    ) {
        val medalTint = when {
            score >= gameType.goldScore -> Color(0xFFFFD700)
            score >= gameType.silverScore -> Color(0xFFC0C0C0)
            score > 0 -> Color(0xFFCD7F32)
            else -> null
        }

        if (medalTint != null) {
            Icon(
                painterResource(Res.drawable.ic_icons8_counter_gold),
                contentDescription = null,
                tint = medalTint,
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.CenterHorizontally),
            )
            Spacer(Modifier.height(8.dp))
        }

        Text(
            text = stringResource(Res.string.finish_score, score),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(Modifier.height(16.dp))

        if (answeredAllCorrect) {
            Text(
                text = stringResource(Res.string.finish_bonus_point),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = Primary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 24.dp),
            )
        }

        if (isNewHighscore) {
            Spacer(Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = PrimaryContainer,
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text(
                    text = stringResource(Res.string.finish_new_highscore),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
        } else if (highscore > 0) {
            Text(
                text = stringResource(Res.string.finish_highscore, highscore),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }

        Spacer(Modifier.height(32.dp))

        DefaultButton(
            onClick = onPlayRandom,
            modifier = Modifier
                .widthIn(max = 420.dp)
                .padding(horizontal = 24.dp),
            value = stringResource(Res.string.button_play_random),
        )

        Spacer(Modifier.height(8.dp))

        DefaultButton(
            onClick = onPlayAgain,
            modifier = Modifier
                .widthIn(max = 420.dp)
                .padding(horizontal = 24.dp),
            value = stringResource(Res.string.button_play_again),
        )
    }
}
