package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.inspiredandroid.braincup.ui.theme.Primary

@Composable
fun PrismDialog(
    onDismissRequest: () -> Unit,
    title: String,
    message: String,
    primaryLabel: String,
    onPrimary: () -> Unit,
    secondaryLabel: String,
    onSecondary: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PrismDialogShell(onDismissRequest = onDismissRequest, modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(20.dp))
        PrismDialogButtonRow(
            primaryLabel = primaryLabel,
            onPrimary = onPrimary,
            secondaryLabel = secondaryLabel,
            onSecondary = onSecondary,
        )
    }
}

/**
 * The shell every prism dialog sits in: the platform [Dialog], the scrim behind it, and a centred
 * card whose taps are swallowed so only a tap on the scrim dismisses.
 *
 * Dialogs that are just a question with two answers use [PrismDialog]; this is for the ones that
 * put their own controls in the card, which would otherwise hand-roll the shell and drift from it.
 */
@Composable
fun PrismDialogShell(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = prismDialogProperties(),
    ) {
        DialogWindowEdgeToEdgeTweaks()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismissRequest,
                ),
            contentAlignment = Alignment.Center,
        ) {
            // The modifier lands on the card, not the scrim: the scrim always fills the window,
            // so the card is the only part a caller can meaningfully size.
            PrismCard(
                face = MaterialTheme.colorScheme.surface,
                modifier = modifier
                    .padding(horizontal = 32.dp)
                    .widthIn(max = 400.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {},
                    ),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    content = content,
                )
            }
        }
    }
}

/**
 * The cancel/confirm pair that closes a prism dialog, sized to split the card evenly - or stacked,
 * once half a card has stopped being enough for a label. At a large font scale the split row was
 * breaking four-letter answers across two lines mid-word ("Sta" / "y"), which reads as damage
 * rather than as a button.
 */
@Composable
fun PrismDialogButtonRow(
    primaryLabel: String,
    onPrimary: () -> Unit,
    secondaryLabel: String,
    onSecondary: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val primary: @Composable (Modifier) -> Unit = { buttonModifier ->
        PrismDialogButton(
            label = primaryLabel,
            onClick = onPrimary,
            face = MaterialTheme.colorScheme.surfaceVariant,
            textColor = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = buttonModifier,
        )
    }
    val secondary: @Composable (Modifier) -> Unit = { buttonModifier ->
        PrismDialogButton(
            label = secondaryLabel,
            face = Primary,
            textColor = Color.White,
            onClick = onSecondary,
            modifier = buttonModifier,
        )
    }
    if (isLargeFontScale()) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            primary(Modifier)
            secondary(Modifier)
        }
    } else {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            primary(Modifier.weight(1f))
            secondary(Modifier.weight(1f))
        }
    }
}

@Composable
fun PrismDialogButton(
    label: String,
    face: Color,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PrismTile(
        face = face,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .hoverHand(),
        onClick = onClick,
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
        )
    }
}
