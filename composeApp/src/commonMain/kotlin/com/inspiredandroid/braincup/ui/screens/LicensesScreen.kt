package com.inspiredandroid.braincup.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.licenses.Attribution
import com.inspiredandroid.braincup.licenses.AttributionCategory
import com.inspiredandroid.braincup.licenses.BRAINCUP_SOURCE_URL
import com.inspiredandroid.braincup.licenses.LicenseTexts
import com.inspiredandroid.braincup.licenses.SoftwareLicense
import com.inspiredandroid.braincup.licenses.attributionSections
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.ChunkyChevron
import com.inspiredandroid.braincup.ui.components.PrismCard
import com.inspiredandroid.braincup.ui.components.hoverHand
import com.inspiredandroid.braincup.ui.components.noRippleClickable
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.ScreenPreviewHost
import com.inspiredandroid.braincup.ui.theme.ContentMaxWidth
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.PrismSlot
import com.inspiredandroid.braincup.ui.theme.readable
import org.jetbrains.compose.resources.stringResource

@Composable
fun LicensesScreen(onBack: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    // Merged from the common, per-target and per-flavor lists on each read, so hoist it out of the
    // LazyColumn builder.
    val sections = remember { attributionSections }
    AppScaffold(
        title = stringResource(Res.string.licenses_title),
        onBack = onBack,
        scrollable = false,
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        ) {
            sections.forEach { section ->
                item(key = "section-${section.category.name}") {
                    SectionHeader(
                        title = stringResource(section.category.titleResource),
                        modifier = Modifier
                            .widthIn(max = ContentMaxWidth)
                            .fillMaxWidth(),
                    )
                }
                items(section.entries, key = { "${section.category.name}-${it.name}" }) { attribution ->
                    AttributionCard(
                        attribution = attribution,
                        onOpenUrl = { uriHandler.openUri(attribution.url) },
                        modifier = Modifier
                            .widthIn(max = ContentMaxWidth)
                            .fillMaxWidth(),
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
            item(key = "app-notice") {
                Text(
                    text = stringResource(Res.string.licenses_app_notice),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .widthIn(max = ContentMaxWidth)
                        .fillMaxWidth()
                        .noRippleClickable { uriHandler.openUri(BRAINCUP_SOURCE_URL) }
                        .hoverHand()
                        .padding(vertical = 16.dp),
                )
            }
        }
    }
}

private val AttributionCategory.titleResource
    get() = when (this) {
        AttributionCategory.LIBRARIES -> Res.string.licenses_section_libraries
        AttributionCategory.FONTS -> Res.string.licenses_section_fonts
        AttributionCategory.SOUND -> Res.string.licenses_section_sound
        AttributionCategory.ARTWORK -> Res.string.licenses_section_artwork
        AttributionCategory.WORD_LISTS -> Res.string.licenses_section_word_lists
    }

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = Primary,
        modifier = modifier.padding(start = 4.dp, top = 16.dp, bottom = 8.dp),
    )
}

@Composable
private fun AttributionCard(
    attribution: Attribution,
    onOpenUrl: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val license = attribution.license
    val uriHandler = LocalUriHandler.current
    var expanded by rememberSaveable { mutableStateOf(false) }
    val chevronRotation by animateFloatAsState(if (expanded) 90f else 0f)

    PrismCard(
        face = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier
            .noRippleClickable(onClickLabel = attribution.name) {
                if (license != null) expanded = !expanded else onOpenUrl()
            }
            .hoverHand(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = attribution.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    val subtitle = attribution.holder ?: stringResource(Res.string.licenses_view_source)
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (license != null) {
                        Text(
                            text = license.label,
                            style = MaterialTheme.typography.labelMedium,
                            color = Primary,
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                ChunkyChevron(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    rotationDegrees = if (license != null) chevronRotation else 0f,
                    modifier = Modifier.size(16.dp),
                )
            }
            if (license != null) {
                AnimatedVisibility(visible = expanded) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        LinkText(
                            text = stringResource(Res.string.licenses_view_source),
                            onClick = onOpenUrl,
                        )
                        if (license.infoUrl != null) {
                            LinkText(
                                text = stringResource(Res.string.licenses_view_license),
                                onClick = { uriHandler.openUri(license.infoUrl) },
                            )
                        }
                        if (license.textPath != null) {
                            LicenseText(license)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LinkText(
    text: String,
    onClick: () -> Unit,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = Primary,
        textDecoration = TextDecoration.Underline,
        modifier = Modifier
            .noRippleClickable(onClick = onClick)
            .hoverHand(),
    )
}

@Composable
private fun LicenseText(license: SoftwareLicense) {
    val text = rememberLicenseText(license)
    Text(
        text = text.orEmpty(),
        style = MaterialTheme.typography.bodySmall.readable().copy(lineHeight = 18.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .clip(PrismSlot)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp),
    )
}

@Composable
private fun rememberLicenseText(license: SoftwareLicense): String? {
    var text by remember(license) { mutableStateOf(LicenseTexts.cached(license)) }
    LaunchedEffect(license) {
        if (text == null) text = LicenseTexts.load(license)
    }
    return text
}

@DevicePreviews
@Composable
private fun LicensesScreenPreview() {
    ScreenPreviewHost {
        LicensesScreen(onBack = {})
    }
}
