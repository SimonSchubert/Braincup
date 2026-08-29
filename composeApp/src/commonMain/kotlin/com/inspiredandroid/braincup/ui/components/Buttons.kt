package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.button_give_up
import com.inspiredandroid.braincup.ui.theme.ContentMaxWidth
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.numberFontFamily
import org.jetbrains.compose.resources.stringResource

@Composable
fun DefaultButton(
    onClick: () -> Unit,
    value: String,
    modifier: Modifier = Modifier,
    face: Color = Primary,
) {
    PrismTile(
        face = face,
        modifier = modifier
            .hoverHand()
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp),
        onClick = onClick,
    ) {
        Text(
            value,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
        )
    }
}

@Composable
fun TextPrismButton(
    onClick: () -> Unit,
    value: String,
    modifier: Modifier = Modifier,
    isClickable: Boolean = true,
) {
    PrismTile(
        face = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
            .hoverHand(isClickable)
            .defaultMinSize(minHeight = 36.dp),
        isClickable = isClickable,
        onClick = onClick,
    ) {
        Text(
            value,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}

@Composable
fun GiveUpButton(
    onGiveUp: () -> Unit,
    modifier: Modifier = Modifier,
    isClickable: Boolean = true,
) {
    TextPrismButton(
        onClick = onGiveUp,
        value = stringResource(Res.string.button_give_up),
        modifier = modifier,
        isClickable = isClickable,
    )
}

/**
 * A primary action button intended as the bottom CTA on result/interstitial screens.
 *
 * Caps width at [maxWidth] on wide layouts and adds 24.dp horizontal insets so the touch target
 * stays comfortable across phones, tablets, and desktop. The cap cannot be overridden by a caller;
 * a section reading at its own measure passes [maxWidth] instead.
 */
@Composable
fun PrimaryActionButton(
    onClick: () -> Unit,
    value: String,
    modifier: Modifier = Modifier,
    maxWidth: Dp = ContentMaxWidth,
) {
    DefaultButton(
        onClick = onClick,
        value = value,
        // The cap goes outside the caller's modifier on purpose. Appended after it instead, a
        // caller's fillMaxWidth() hands down constraints whose min equals their max, and the
        // Constraints.constrain inside widthIn coerces the ceiling back up into that fixed range,
        // so the cap silently disappears and the button spans the window. Outermost it always
        // holds, and fillMaxWidth() then means "fill the cap" rather than "fill the window".
        modifier = Modifier
            .widthIn(max = maxWidth)
            .padding(horizontal = 24.dp)
            .then(modifier),
    )
}

@Composable
fun CircleButton(
    onClick: () -> Unit,
    value: String,
    modifier: Modifier = Modifier,
) {
    PrismTile(
        face = Primary,
        modifier = modifier
            .sizeIn(56.dp, 56.dp)
            .hoverHand(),
        onClick = onClick,
    ) {
        val operatorIcon = OperatorIcons[value]
        if (operatorIcon != null) {
            Icon(
                imageVector = operatorIcon,
                contentDescription = value,
                tint = Color.White,
                modifier = Modifier.size(24.dp),
            )
        } else {
            Text(
                value,
                color = Color.White,
                fontFamily = numberFontFamily(),
                fontSize = 22.sp,
            )
        }
    }
}
