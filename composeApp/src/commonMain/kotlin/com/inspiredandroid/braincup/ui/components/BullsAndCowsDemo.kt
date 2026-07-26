package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.game_bulls_and_cows_howto
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.GamePreviewHost
import org.jetbrains.compose.resources.stringResource

@Composable
fun BullsAndCowsDemo(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.game_bulls_and_cows_howto),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(20.dp))

        // Show a brief demonstration row representing a guess
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DigitMemorySlots(
                length = 4,
                value = "1234",
                accent = MaterialTheme.colorScheme.primary,
                revealColor = null,
                modifier = Modifier.width(180.dp),
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = com.inspiredandroid.braincup.ui.theme.SuccessGreen.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, com.inspiredandroid.braincup.ui.theme.SuccessGreen.copy(alpha = 0.5f)),
                ) {
                    Text(
                        text = "1 Bull",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = com.inspiredandroid.braincup.ui.theme.SuccessGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }

                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                ) {
                    Text(
                        text = "2 Cows",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }
        }
    }
}

@DevicePreviews
@Composable
private fun BullsAndCowsDemoPreview() {
    GamePreviewHost {
        BullsAndCowsDemo()
    }
}
