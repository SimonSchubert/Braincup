package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.ui.theme.PrimaryContainer

@Composable
fun BrandedCard(
    modifier: Modifier = Modifier,
    containerColor: Color = PrimaryContainer,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    /**
     * Whether the card's content spans the width it is offered.
     *
     * True suits the banners this was written for, which fill their column and lay text out from
     * the left. A chip is the other shape: [XpGainedChip] wants to be as wide as "+15 XP" and no
     * wider, and filling instead turned it into a full-width bar with its text pinned to one end.
     */
    fillWidth: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    PrismCard(
        face = containerColor,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .then(if (fillWidth) Modifier.fillMaxWidth() else Modifier)
                .padding(contentPadding),
            horizontalAlignment = horizontalAlignment,
            content = content,
        )
    }
}
