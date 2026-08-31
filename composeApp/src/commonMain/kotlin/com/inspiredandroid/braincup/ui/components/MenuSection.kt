package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.ui.theme.OnPrimaryContainer
import com.inspiredandroid.braincup.ui.theme.PrismFacet

/**
 * The heading over a run of tiles on the main menu, used by every section there: each game
 * category, the untimed games and Learn.
 *
 * The title is a prism chip in [accentColor] rather than a line of coloured text. It is the same
 * accent the tiles below it are tinted with, so a section announces itself by colour before you
 * have read the word, and the chip carries the app's own bevelled surface instead of leaving the
 * one piece of chrome on the menu looking like plain text.
 *
 * Paints its own background because these are pinned as sticky headers: without it the tiles
 * scrolling underneath show straight through the title. Kept to a single line for the same
 * reason — it is on screen for the whole length of its section. [subtitle] is for the rare
 * section that has a real warning to carry.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MenuSectionHeader(
    title: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
    trailing: String? = null,
    subtitle: String? = null,
) {
    // The chip picks its own ink rather than making every caller state it. Every accent in use is
    // pale, so this reads as the dark branch in practice; the light branch is the guard that keeps
    // a future section from shipping a saturated accent with unreadable text on it.
    val onAccent = if (accentColor.luminance() > 0.5f) {
        OnPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            // Asymmetric, and tighter than it looks: the grid already spaces every item by 12dp,
            // so this is only what the heading adds on top. More above than below, because the
            // heading belongs to the tiles under it and should read as attached to them. The
            // bottom keeps a little, so a tile scrolling under a pinned heading is not shaved by
            // the chip's bevel.
            .padding(top = 6.dp, bottom = 4.dp),
    ) {
        // A flow rather than a row, because a trailing count is measured before the title and takes
        // its full width: on a narrow screen at a large font scale that left the title a column
        // four glyphs wide, which it then spelled out one letter to a line. Here the count drops to
        // its own line instead, and the title keeps the width.
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            itemVerticalAlignment = Alignment.CenterVertically,
        ) {
            PrismCard(face = accentColor, facet = PrismFacet.Preview) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = onAccent,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                )
            }
            if (trailing != null) {
                Text(
                    text = trailing,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (subtitle != null) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
