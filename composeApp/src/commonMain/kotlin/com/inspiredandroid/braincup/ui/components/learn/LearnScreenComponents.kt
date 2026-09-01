package com.inspiredandroid.braincup.ui.components.learn

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.learn.LearnVisual
import com.inspiredandroid.braincup.ui.components.MathText
import com.inspiredandroid.braincup.ui.components.PrimaryActionButton
import com.inspiredandroid.braincup.ui.components.PrismCard
import com.inspiredandroid.braincup.ui.components.PrismTile
import com.inspiredandroid.braincup.ui.components.formatMathSymbols
import com.inspiredandroid.braincup.ui.components.hoverHand
import com.inspiredandroid.braincup.ui.components.readsAsNotation
import com.inspiredandroid.braincup.ui.components.withFormulaColors
import com.inspiredandroid.braincup.ui.components.withGroupColors
import com.inspiredandroid.braincup.ui.theme.LearnCorrectFace
import com.inspiredandroid.braincup.ui.theme.LearnWrongFace
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import com.inspiredandroid.braincup.ui.theme.numeric

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
 * The Learn section's primary action, on the section's own [LearnContentWidth] measure.
 *
 * The bar or column around it spans the window, the button inside it does not: stretched edge to
 * edge on a desktop window it stops reading as a button at all. Filled rather than wrapped so a
 * CTA lines up with the option tiles, formula cards and review cards stacked above it, which all
 * read at the same width.
 */
@Composable
internal fun LearnPrimaryButton(
    onClick: () -> Unit,
    value: String,
    modifier: Modifier = Modifier,
) {
    PrimaryActionButton(
        onClick = onClick,
        value = value,
        maxWidth = LearnContentWidth,
        modifier = Modifier.fillMaxWidth().then(modifier),
    )
}

/**
 * How an answer option is standing at the moment it is drawn.
 *
 * A test leaves every option [NORMAL] from first to last: answers are revealed only at the end, so
 * nothing on the screen may hint at which one was right.
 */
internal enum class LearnOptionState { NORMAL, CORRECT, WRONG, DIMMED }

/**
 * Notation in the number face, prose in the display face.
 *
 * A test question, its options and its review line are a mix: "9 + 6 = ?" is notation and belongs
 * in Rubik, "Which number is smaller, 62 or 26?" is a sentence and belongs in the display face the
 * rest of the section's teaching prose is set in. Routing all of them through [MathText] set the
 * sentences in the number face as well, so the same wording read one way in a lesson and another
 * in a test.
 */
@Composable
internal fun LearnText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
    /**
     * Colour the notation by role - given, working, answer - the way a lesson's formula card does.
     * A test asks the same kind of question a lesson asks, so it should read the same way. Off for
     * an answer option, which is a choice rather than a given and says what it is by turning green.
     */
    roleColors: Boolean = false,
    /** The figure beside this run, whose values say which role each number carries. */
    roles: FigureRoles? = null,
    /**
     * Which face this run takes. A caller holding the [CatalogText][com.inspiredandroid.braincup
     * .learn.CatalogText] it came from passes `isNotation`, which is what the catalog declared;
     * [readsAsNotation] is the fallback for a run that arrives as a bare string, and it can only
     * go on the characters, so a hyphen in "cross-section x length" reads to it as a minus sign.
     */
    notation: Boolean = text.readsAsNotation(),
) {
    if (roleColors && notation) {
        Text(
            text = text.formatMathSymbols(fractionSlash = true)
                .withFormulaColors(structure = MaterialTheme.colorScheme.onSurfaceVariant, roles = roles),
            style = style.numeric(),
            modifier = modifier,
            textAlign = textAlign,
        )
    } else if (notation) {
        MathText(
            text = text,
            style = style,
            modifier = modifier,
            color = color,
            textAlign = textAlign,
            fractionSlash = true,
        )
    } else {
        Text(
            text = text.withGroupColors(),
            style = style,
            modifier = modifier,
            color = color,
            textAlign = textAlign,
        )
    }
}

/**
 * The card an equation is set in, wherever one appears.
 *
 * A lesson step and a test question ask the same kind of thing, so they present it the same way:
 * the notation in a card of its own, and any prose that goes with it plainly underneath. A test
 * that printed its equation as loose text under the figure read as a caption rather than as the
 * question.
 *
 * Every number in it carries its role - the given in the brand colour, the working in blue, the
 * answer in green once it arrives - while the operators holding them together take the muted tone,
 * so a colour on this card always means something.
 */
@Composable
internal fun LearnFormulaCard(formula: String, roles: FigureRoles? = null) {
    PrismCard(
        face = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.widthIn(max = LearnContentWidth).fillMaxWidth(),
    ) {
        Text(
            text = formula.formatMathSymbols(fractionSlash = true)
                .withFormulaColors(structure = MaterialTheme.colorScheme.onSurfaceVariant, roles = roles),
            style = MaterialTheme.typography.titleLarge.numeric(),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        )
    }
}

/**
 * A result read out on a card of its own, for the steps whose question has nowhere to resolve.
 *
 * A step asked as an equation finishes where it was asked - the answer lands on the question mark
 * in [LearnFormulaCard] - so it needs no readout at all. A step asked in words has no question
 * mark to land on, and that answer goes here rather than floating between the cards around it.
 */
@Composable
internal fun LearnAnswerCard(label: String, value: String) {
    PrismCard(
        face = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.widthIn(max = LearnContentWidth).fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = SuccessGreen,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

/** One tappable answer option, in a lesson step or a test question. */
@Composable
internal fun LearnOptionTile(
    label: String,
    state: LearnOptionState,
    onClick: () -> Unit,
    /** See [LearnText]: whether the label is notation, as the catalog declared it. */
    notation: Boolean = label.readsAsNotation(),
) {
    // Both answered faces are brand-pinned: left on the scheme's roles they are resolved from the
    // wallpaper by Material You, and a wrong answer was landing on a pale pink that carries white
    // text at 1.71:1. See LearnCorrectFace / LearnWrongFace.
    val face = when (state) {
        LearnOptionState.NORMAL -> MaterialTheme.colorScheme.surfaceVariant
        LearnOptionState.CORRECT -> LearnCorrectFace
        LearnOptionState.WRONG -> LearnWrongFace
        LearnOptionState.DIMMED -> MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = when (state) {
        LearnOptionState.CORRECT, LearnOptionState.WRONG -> Color.White
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    PrismTile(
        face = face,
        isClickable = state == LearnOptionState.NORMAL,
        modifier = Modifier
            .widthIn(max = LearnContentWidth)
            .fillMaxWidth()
            .hoverHand(state == LearnOptionState.NORMAL),
        onClick = { if (state == LearnOptionState.NORMAL) onClick() },
    ) {
        LearnText(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (state == LearnOptionState.DIMMED) contentColor.copy(alpha = 0.6f) else contentColor,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            notation = notation,
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
 *
 * Centred in the space it is given rather than stacked at the top. A teaching step is often one
 * figure and one sentence, and top-aligned under a bottom-pinned button that left more than half
 * the screen empty between the two - a gap so large the button stopped reading as the end of the
 * step. The minimum height is the viewport, so a step that overflows still grows downwards and
 * scrolls exactly as it did.
 */
@Composable
internal fun ColumnScope.LearnStepColumn(content: @Composable ColumnScope.() -> Unit) {
    BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
        val viewport = maxHeight
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .heightIn(min = viewport)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            content = content,
        )
    }
}

/**
 * The screen a lesson or a test ends on: what was finished, how it went, and whatever each has to
 * offer next. Both scroll, because a result can grow a review list or a certificate under it.
 *
 * Centred in the space it is given, exactly as [LearnStepColumn] is. A result is a handful of
 * lines and two buttons, and stacked at the top it left the bottom half of the screen empty. A
 * result that outgrows the viewport, such as a test with its review list unfolded, is taller than
 * the space it is centred in and so keeps scrolling from the top exactly as it did.
 */
@Composable
internal fun LearnResultColumn(
    title: String,
    score: String,
    scoreStyle: TextStyle,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
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
