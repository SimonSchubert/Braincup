package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.locale.supportedAppLanguages
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.PrismCard
import com.inspiredandroid.braincup.ui.components.hoverHand
import com.inspiredandroid.braincup.ui.components.noRippleClickable
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.ScreenPreviewHost
import com.inspiredandroid.braincup.ui.theme.ContentMaxWidth
import com.inspiredandroid.braincup.ui.theme.Primary
import org.jetbrains.compose.resources.stringResource

/**
 * Picks the UI language. [selectedTag] is a BCP 47 tag from [supportedAppLanguages], or null while
 * the app follows the device.
 */
private val CheckmarkSize = 24.dp

@Composable
fun LanguageScreen(
    selectedTag: String?,
    onSelect: (String?) -> Unit,
    onBack: () -> Unit,
) {
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    AppScaffold(
        title = stringResource(Res.string.settings_language),
        onBack = onBack,
        scrollable = false,
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = 16.dp + bottomInset,
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item(key = "system") {
                LanguageRow(
                    label = stringResource(Res.string.settings_language_system),
                    // The label is a translated app string, so it renders in the app's own face.
                    useSystemFont = false,
                    selected = selectedTag == null,
                    onClick = { onSelect(null) },
                )
            }
            items(supportedAppLanguages, key = { it.tag }) { language ->
                LanguageRow(
                    label = language.nativeName,
                    useSystemFont = true,
                    selected = selectedTag == language.tag,
                    onClick = { onSelect(language.tag) },
                )
            }
        }
    }
}

@Composable
private fun LanguageRow(
    label: String,
    useSystemFont: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
) {
    PrismCard(
        face = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier
            .widthIn(max = ContentMaxWidth)
            .fillMaxWidth()
            .noRippleClickable(onClickLabel = label, onClick = onClick)
            .hoverHand(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                // This is the one screen that draws twenty scripts at once: Bungee is Latin-only,
                // Tektur adds Greek and Cyrillic and Rubik stops at Hebrew and Arabic, so a brand
                // face would render the CJK, Thai and Indic endonyms as tofu. Hand those to the
                // platform's own fallback chain.
                fontFamily = if (useSystemFont) FontFamily.Default else null,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(16.dp))
            // The slot is always laid out, and smaller than a line of the label, so picking a
            // language cannot change the row's height or shift its text sideways.
            Box(
                modifier = Modifier.size(CheckmarkSize),
                contentAlignment = Alignment.Center,
            ) {
                if (selected) {
                    Text(
                        text = "✓",
                        color = Primary,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
    }
}

@DevicePreviews
@Composable
private fun LanguageScreenPreview() {
    ScreenPreviewHost {
        LanguageScreen(
            selectedTag = null,
            onSelect = {},
            onBack = {},
        )
    }
}

@DevicePreviews
@Composable
private fun LanguageScreenSelectedPreview() {
    ScreenPreviewHost {
        LanguageScreen(
            selectedTag = "de",
            onSelect = {},
            onBack = {},
        )
    }
}
