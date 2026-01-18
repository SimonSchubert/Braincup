package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.button_start
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.DefaultButton
import org.jetbrains.compose.resources.stringResource

@Composable
fun InstructionsScreen(
    gameType: GameType,
    onStart: () -> Unit,
    onBack: () -> Unit,
) {
    AppScaffold(
        title = stringResource(gameType.displayNameRes),
        onBack = onBack,
        scrollable = false,
    ) {
        Spacer(Modifier.height(32.dp))

        Text(
            text = stringResource(gameType.descriptionRes),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .align(Alignment.CenterHorizontally),
        )

        Spacer(Modifier.height(32.dp))

        DefaultButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = onStart,
            value = stringResource(Res.string.button_start),
        )
    }
}
