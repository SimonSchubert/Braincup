package com.inspiredandroid.braincup.ui.components

import androidx.compose.runtime.Composable
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.button_quit_game
import braincup.composeapp.generated.resources.button_stay
import braincup.composeapp.generated.resources.quit_game_message
import braincup.composeapp.generated.resources.quit_game_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun QuitGameDialog(
    onDismiss: () -> Unit,
    onQuit: () -> Unit,
) {
    PrismDialog(
        onDismissRequest = onDismiss,
        title = stringResource(Res.string.quit_game_title),
        message = stringResource(Res.string.quit_game_message),
        primaryLabel = stringResource(Res.string.button_stay),
        onPrimary = onDismiss,
        secondaryLabel = stringResource(Res.string.button_quit_game),
        onSecondary = onQuit,
    )
}
