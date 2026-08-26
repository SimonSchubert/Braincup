package com.inspiredandroid.braincup.ui.components.learn

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.learn.LearnVisual
import com.inspiredandroid.braincup.ui.components.MathText
import com.inspiredandroid.braincup.ui.components.PrismCard
import com.inspiredandroid.braincup.ui.components.PrismTile
import com.inspiredandroid.braincup.ui.components.hoverHand
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.SuccessGreen

/**
 * The measures the Learn section lays its screens out on.
 *
 * Everything on a step shares one width so a desktop window does not stretch the prose, the
 * options and the diagram to three different measures. The diagram is capped tighter still,
 * because its figures are laid out from the canvas width and a very wide canvas draws counters
 * and hops far larger than the text beside them.
 *
 * These live here rather than in `PrismTokens` because they are the Learn section's own measure,
 * wider than the app-wide `ContentMaxWidth` a list screen reads at.
 */
internal val LearnContentWidth = 480.dp
internal val LearnFigureWidth = 420.dp
internal val LearnFigureHeight = 180.dp

/**
 * How an answer option is standing at the moment it is drawn.
 *
 * A test leaves every option [IDLE] from first to last: answers are revealed only at the end, so
 * nothing on the screen may hint at which one was right.
 */
internal enum class LearnOptionState { IDLE, CORRECT, WRONG, MUTED }

/** One tappable answer option, in a lesson step or a test question. */
@Composable
internal fun LearnOptionTile(
    label: String,
    state: LearnOptionState,
    onClick: () -> Unit,
) {
    val face = when (state) {
        LearnOptionState.IDLE -> MaterialTheme.colorScheme.surfaceVariant
        LearnOptionState.CORRECT -> SuccessGreen
        LearnOptionState.WRONG -> MaterialTheme.colorScheme.error
        LearnOptionState.MUTED -> MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = when (state) {
        LearnOptionState.CORRECT, LearnOptionState.WRONG -> Color.White
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    PrismTile(
        face = face,
        isClickable = state == LearnOptionState.IDLE,
        modifier = Modifier
            .widthIn(max = LearnContentWidth)
            .fillMaxWidth()
            .hoverHand(state == LearnOptionState.IDLE),
        onClick = { if (state == LearnOptionState.IDLE) onClick() },
    ) {
        MathText(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (state == LearnOptionState.MUTED) contentColor.copy(alpha = 0.6f) else contentColor,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            fractionSlash = true,
        )
    }
}

/**
 * A step or question's diagram, on a panel of its own so it reads as a figure rather than as
 * marks floating on the page, and so the tap-to-replay target has a visible edge.
 */
@Composable
internal fun LearnFigurePanel(
    visual: LearnVisual,
    modifier: Modifier = Modifier,
    answer: VisualAnswer? = null,
) {
    PrismCard(
        face = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
            .widthIn(max = LearnFigureWidth)
            .fillMaxWidth()
            .height(LearnFigureHeight),
    ) {
        LearnVisualCanvas(
            visual = visual,
            modifier = Modifier.fillMaxSize().padding(12.dp),
            answer = answer,
        )
    }
}

/**
 * The scrolling body a lesson step and a test question are both laid out in, under whatever
 * progress indicator the screen shows above it.
 */
@Composable
internal fun ColumnScope.LearnStepColumn(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content,
    )
}

/**
 * The screen a lesson or a test ends on: what was finished, how it went, and whatever each has to
 * offer next. Both scroll, because a result can grow a review list or a certificate under it.
 */
@Composable
internal fun LearnResultColumn(
    title: String,
    score: String,
    scoreStyle: TextStyle,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = verticalArrangement,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = Primary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(text = score, style = scoreStyle, textAlign = TextAlign.Center)
        content()
    }
}

/**
 * The face and ink of a card that is either called out or left plain.
 *
 * Ink follows face deliberately: on Android these containers are resolved from the wallpaper, so
 * text left to inherit the ambient content colour can end up near-white on a pale card.
 */
@Composable
internal fun learnContainerColors(highlighted: Boolean): Pair<Color, Color> = if (highlighted) {
    MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
} else {
    MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
}
