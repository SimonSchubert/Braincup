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
import com.inspiredandroid.braincup.ui.theme.WorkingBlue
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
 * Tags that tint a number to match what the figure beside it draws in the same colour.
 *
 * `{a:6}` is a given - a number the question hands you - and `{b:4}` is the working, the step you
 * take with it, so "{a:6} needs {b:4} more" can be read straight off the picture. `{c:}` is the
 * answer, and the section spends it nowhere else: it is the one green on the screen, matching the
 * option tile that turns green and the value the figure marks once the learner has it right.
 *
 * `{c:}` is not authored into the content. It is substituted at render time, when a question
 * resolves and its formula finishes in front of the learner.
 */
// Every brace is escaped, inside the character class too: Android's ICU engine rejects bare
// braces that the JVM regex accepts.
private val GroupTag = Regex("""\{([abc]):([^\}]*)\}""")

/**
 * Resolves [GroupTag] markup into coloured, bold runs. Everything outside a tag is left exactly as
 * it is and inherits the caller's colour, so text that carries no tags renders unchanged.
 *
 * [groupA] matches the figure's own accent, which is also the colour a formula card is already
 * printed in: inside one, only the working and the answer visibly shift, which is precisely the
 * split the figure draws.
 */
fun String.withGroupColors(
    groupA: Color = Primary,
    groupB: Color = WorkingBlue,
    groupC: Color = SuccessGreen,
): AnnotatedString = buildAnnotatedString {
    var cursor = 0
    GroupTag.findAll(this@withGroupColors).forEach { match ->
        append(this@withGroupColors.substring(cursor, match.range.first))
        val color = when (match.groupValues[1]) {
            "a" -> groupA
            "b" -> groupB
            else -> groupC
        }
        withStyle(SpanStyle(color = color, fontWeight = FontWeight.Bold)) {
            append(match.groupValues[2])
        }
        cursor = match.range.last + 1
    }
    append(this@withGroupColors.substring(cursor))
}

/**
 * A formula's colours: the values carry a role, the scaffolding between them does not.
 *
 * Operators and the equals sign are structure, not content. While they were printed in the same
 * orange as an untagged number, orange read as "the colour a formula card is printed in" rather
 * than "the number the question handed you" - so they drop to [structure] and orange is left
 * meaning one thing. What survives in orange is exactly what the figure marks in orange.
 *
 * A minus is part of the number when it signs one and structure when it subtracts, which is the
 * same distinction [spaceSubtraction] draws: after [formatMathSymbols] a subtracting minus always
 * has space around it and a sign never does.
 */
fun String.withFormulaColors(
    structure: Color,
    given: Color = Primary,
    working: Color = WorkingBlue,
    answer: Color = SuccessGreen,
): AnnotatedString = buildAnnotatedString {
    val answerStart = this@withFormulaColors.answerTailStart()

    // [base] is where [run] starts in the whole formula, which is what tells a number after the
    // equals sign apart from one before it.
    fun appendPlain(run: String, base: Int) {
        var i = 0
        while (i < run.length) {
            val char = run[i]
            val signed = char == '-' &&
                i + 1 < run.length &&
                run[i + 1].isDigit() &&
                run.take(i).lastOrNull { !it.isWhitespace() }?.endsAValue() != true
            if (char.isDigit() || signed) {
                val start = i
                if (run[i] == '-') i++
                while (i < run.length && (run[i].isDigit() || run[i] == '.' || run[i] == ',')) i++
                if (i < run.length && run[i] == '%') i++
                val role = if (answerStart != null && base + start >= answerStart) answer else given
                withStyle(SpanStyle(color = role, fontWeight = FontWeight.Bold)) {
                    append(run.substring(start, i))
                }
            } else {
                val start = i
                while (i < run.length &&
                    !run[i].isDigit() &&
                    !(run[i] == '-' && i + 1 < run.length && run[i + 1].isDigit())
                ) {
                    i++
                }
                withStyle(SpanStyle(color = structure)) { append(run.substring(start, i)) }
            }
        }
    }

    var cursor = 0
    GroupTag.findAll(this@withFormulaColors).forEach { match ->
        appendPlain(this@withFormulaColors.substring(cursor, match.range.first), cursor)
        val color = when (match.groupValues[1]) {
            "a" -> given
            "b" -> working
            else -> answer
        }
        withStyle(SpanStyle(color = color, fontWeight = FontWeight.Bold)) {
            append(match.groupValues[2])
        }
        cursor = match.range.last + 1
    }
    appendPlain(this@withFormulaColors.substring(cursor), cursor)
}

/** A lone value: what a formula that works out to something has on the right of its equals sign. */
private val AnswerValue = Regex("""[-+]?\d+(?:[.,]\d+)?(?:/\d+)?[%\u00B2\u00B3]?""")

/**
 * Where the answer begins in a formula that ends in one, or null when nothing on the card is one.
 *
 * A taught formula states its result - "7 + 3 = 10" - and that result is the same number the
 * figure beside it marks in green. Without this every untagged number fell through to [given], so
 * the card printed its answer in the colour meaning "what the question handed you" while the
 * picture marked it in the colour meaning "the answer". `{c:}` already covers the questions,
 * because those resolve at render time; this covers the ones written out in the content.
 *
 * Deliberately narrow, because a number after an equals sign is only sometimes an answer:
 *
 * - **A `?` anywhere disqualifies the line.** In "65 + ? = 180" the unknown is the `?` and the 180
 *   is given, so colouring the tail would say the exact opposite of what the step asks.
 * - **Exactly one equals sign.** "k = √9 = 3" and "3 x 6 = 18, so 6 x 6 = 36" are chains, and
 *   which of their values is the answer is not something the punctuation says.
 * - **The tail is one bare value**, so "3 r 2", "360 degrees" and "2 x (length + width)" are left
 *   alone. A tight-slash fraction counts as one value: "3/5 + 1/5 = 4/5" answers with 4/5.
 * - **An already-tagged tail is left to its tag**, which is how a resolved `{c:}` keeps working.
 */
private fun String.answerTailStart(): Int? {
    if (contains('?')) return null
    val equals = indexOf('=')
    if (equals < 0 || indexOf('=', equals + 1) >= 0) return null
    if (substring(0, equals).isBlank()) return null
    val tail = substring(equals + 1)
    if (tail.contains('{') || !AnswerValue.matches(tail.trim())) return null
    val offset = tail.indexOfFirst { !it.isWhitespace() }
    return if (offset < 0) null else equals + 1 + offset
}

/** Operators and relations. A string carrying one of these is notation, whatever else is in it. */
private val NotationChar = charArrayOf(
    '+', '-', '*', '/', '=', '<', '>', '\u00D7', '\u00F7', '\u2264', '\u2265', '^', '%',
)

/**
 * Whether a string reads as notation rather than as a sentence, and so belongs in the number face.
 *
 * Two things qualify: carrying an operator ("9 + 6 = ?", "4.5 x 10^4"), or containing no word at
 * all, which is what a bare option like "26" looks like. Anything else is prose that happens to
 * mention numbers - "Which number is smaller, 62 or 26?" - and takes the display face the rest of
 * the section's prose is set in. The rule is the whole-string form of the per-run split
 * `VisualScope.annotate` applies to figure captions.
 */
fun String.readsAsNotation(): Boolean {
    if (any { it in NotationChar }) return true
    var letters = 0
    forEach { char ->
        if (char.isLetter()) {
            letters++
            if (letters >= 2) return false
        } else {
            letters = 0
        }
    }
    return true
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
