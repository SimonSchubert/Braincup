package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import com.inspiredandroid.braincup.ui.theme.numeric

/** A slash with room around it, the one kind that still means "divided by" in fraction text. */
private val SpacedSlash = Regex(" +/ +")

/**
 * Replace typed operators with their proper math glyphs: * with ×, / with ÷, and the two-character
 * inequality signs with ≥ and ≤. The inequality pair runs first so the later rules do not split
 * ">=" apart at its own characters.
 *
 * [fractionSlash] is for text that talks about fractions, where a slash written tight between its
 * neighbours - 3/4, d/dx - is a fraction line and only a spaced one divides. Nothing in the text
 * itself settles which is meant, because the mental-arithmetic games write "12/4" for a division
 * and a fractions lesson writes "3/4" for three quarters, so the caller says which language it is
 * writing in and the default stays with division.
 */
fun String.formatMathSymbols(fractionSlash: Boolean = false): String = this.replace(">=", " \u2265 ")
    .replace("<=", " \u2264 ")
    .replace("*", " \u00D7 ")
    .let { if (fractionSlash) it.replace(SpacedSlash, " \u00F7 ") else it.replace("/", " \u00F7 ") }
    .replace("+", " + ")
    .spaceSubtraction()
    .replace("  ", " ")
    .trim()

/** Characters a minus can follow and still be subtracting: something has to come before it. */
private fun Char.endsAValue(): Boolean = isDigit() || isLetter() || this == ')' || this == '%'

/**
 * Pads out the minuses that subtract, and leaves the sign of a negative number attached to it.
 *
 * Spacing every hyphen alike turned "-4 - 6" into " - 4 - 6" and "5 - (-3)" into "5 - ( - 3)".
 * Nothing showed it up while the lessons stayed to the right of zero.
 */
private fun String.spaceSubtraction(): String {
    val out = StringBuilder(length + 8)
    var previous: Char? = null
    forEach { char ->
        val before = previous
        if (char == '-' && before != null && before.endsAValue()) out.append(" - ") else out.append(char)
        if (!char.isWhitespace()) previous = char
    }
    return out.toString()
}

/**
 * Tags that tint a number to match the dots it stands for in the figure beside it: `{a:6}` takes
 * the first group's colour and `{b:4}` the second, the same two colours the ten-frames and counter
 * piles are drawn in. So "{a:6} needs {b:4} more" can be read straight off the picture.
 */
// Every brace is escaped, inside the character class too: Android's ICU engine rejects bare
// braces that the JVM regex accepts.
private val GroupTag = Regex("""\{([ab]):([^\}]*)\}""")

/**
 * Resolves [GroupTag] markup into coloured, bold runs. Everything outside a tag is left exactly as
 * it is and inherits the caller's colour, so text that carries no tags renders unchanged.
 *
 * [groupA] matches the figure's own accent, which is also the colour a formula card is already
 * printed in: inside one, only the second group visibly shifts, which is precisely the split the
 * dots show.
 */
fun String.withGroupColors(
    groupA: Color = Primary,
    groupB: Color = SuccessGreen,
): AnnotatedString = buildAnnotatedString {
    var cursor = 0
    GroupTag.findAll(this@withGroupColors).forEach { match ->
        append(this@withGroupColors.substring(cursor, match.range.first))
        withStyle(SpanStyle(color = if (match.groupValues[1] == "a") groupA else groupB, fontWeight = FontWeight.Bold)) {
            append(match.groupValues[2])
        }
        cursor = match.range.last + 1
    }
    append(this@withGroupColors.substring(cursor))
}

/** [fractionSlash] keeps a tight `3/4` a fraction; see [formatMathSymbols]. */
@Composable
fun MathText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    textAlign: TextAlign? = null,
    color: Color = Color.Unspecified,
    fractionSlash: Boolean = false,
) {
    Text(
        text = text.formatMathSymbols(fractionSlash).withGroupColors(),
        modifier = modifier,
        style = style.numeric(),
        textAlign = textAlign,
        color = color,
    )
}

@Composable
fun FractionText(
    numerator: String,
    denominator: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
) {
    val numericStyle = style.numeric()
    val barColor = if (color != Color.Unspecified) {
        color
    } else if (style.color != Color.Unspecified) {
        style.color
    } else {
        LocalContentColor.current
    }

    Column(
        modifier = modifier.width(IntrinsicSize.Min),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = numerator,
            style = numericStyle,
            color = color,
            maxLines = 1,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(barColor),
        )
        Text(
            text = denominator,
            style = numericStyle,
            color = color,
            maxLines = 1,
        )
    }
}
