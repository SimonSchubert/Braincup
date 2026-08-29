package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.games.tools.Figure
import kotlinx.collections.immutable.ImmutableList

/**
 * Which trait counts this round, stated by example rather than by name.
 *
 * The pair holds exactly one trait constant: one shape in two colors means shape is what matters,
 * two shapes in one color means color is. Reading it is the same relational step the board then
 * asks for, which is the point. A cue printing "SHAPE" would be a single lexical lookup, making a
 * switch round cost exactly what a repeat round costs, and the exercise would measure nothing.
 *
 * It also leaves the board with no text on it at all, which matters in an app shipping 44 locales.
 * An earlier attempt used arbitrary marks (Shape.T / Shape.L, then one pip against three); both
 * had to be memorised from the instructions first, and the glyph pair rendered as two
 * near-identical grey blobs at cue size.
 *
 * The figures are smaller than a board tile and sit inside a pill, and
 * [MentalFlexGame.cueExemplar][com.inspiredandroid.braincup.games.MentalFlexGame.cueExemplar]
 * never reuses the target's own shape or color, so the cue cannot be read as a hint about which
 * tile to tap.
 *
 * [label] spells the rule out in words. It is for the instructions only, which is where the idea is
 * introduced; the game board must never pass one.
 */
@Composable
fun MentalFlexRuleCue(
    exemplar: ImmutableList<Figure>,
    modifier: Modifier = Modifier,
    label: String? = null,
) {
    Row(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.surfaceContainerHigh,
                RoundedCornerShape(percent = 50),
            )
            .padding(horizontal = 18.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        exemplar.forEach { figure ->
            ShapeCanvas(figure = figure, modifier = Modifier.size(26.dp))
        }
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
