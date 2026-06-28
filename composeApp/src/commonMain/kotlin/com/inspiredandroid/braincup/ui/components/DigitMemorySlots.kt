package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inspiredandroid.braincup.ui.theme.RoundedSlot
import com.inspiredandroid.braincup.ui.theme.SlotBorderWidthDp
import com.inspiredandroid.braincup.ui.theme.numberFontFamily

/**
 * A single row of per-digit boxes shared by the Digit Memory game and its instruction demo.
 * Empty boxes act as input slots; the next slot to fill is highlighted with [accent].
 */
@Composable
fun DigitMemorySlots(
    length: Int,
    value: String,
    accent: Color,
    revealColor: Color?,
    modifier: Modifier = Modifier,
    slotWidth: Dp? = null,
    onRemoveAt: ((Int) -> Unit)? = null,
) {
    BoxWithConstraints(modifier = modifier) {
        val spacing = 6.dp
        val maxSlot = 48.dp
        val resolvedSlotWidth = slotWidth ?: ((maxWidth - spacing * (length - 1)) / length).coerceIn(18.dp, maxSlot)
        val slotHeight = resolvedSlotWidth * 1.3f
        val fontSize = (resolvedSlotWidth.value * 0.46f).sp
        Row(
            modifier = if (slotWidth == null) Modifier.fillMaxWidth() else Modifier,
            horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally),
        ) {
            repeat(length) { index ->
                val digit = value.getOrNull(index)?.toString() ?: ""
                val isActive = revealColor == null && index == value.length
                val borderColor = when {
                    revealColor != null -> revealColor
                    isActive -> accent
                    digit.isNotEmpty() -> accent.copy(alpha = 0.5f)
                    else -> MaterialTheme.colorScheme.outlineVariant
                }
                val textColor = revealColor ?: MaterialTheme.colorScheme.onSurface
                val clickable = onRemoveAt != null && digit.isNotEmpty()
                Box(
                    modifier = Modifier
                        .size(width = resolvedSlotWidth, height = slotHeight)
                        .clip(RoundedSlot)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .border(BorderStroke(SlotBorderWidthDp.dp, borderColor), RoundedSlot)
                        .then(
                            if (clickable) Modifier.clickable { onRemoveAt(index) } else Modifier,
                        )
                        .hoverHand(clickable),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = digit,
                        style = MaterialTheme.typography.headlineSmall,
                        fontSize = fontSize,
                        fontFamily = numberFontFamily(),
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                    )
                }
            }
        }
    }
}
