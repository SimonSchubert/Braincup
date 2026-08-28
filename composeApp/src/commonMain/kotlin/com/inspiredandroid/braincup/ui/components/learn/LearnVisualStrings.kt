package com.inspiredandroid.braincup.ui.components.learn

import androidx.compose.runtime.Composable
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.learn_fig_area
import braincup.composeapp.generated.resources.learn_fig_bar_after
import braincup.composeapp.generated.resources.learn_fig_bar_before
import braincup.composeapp.generated.resources.learn_fig_bar_score
import braincup.composeapp.generated.resources.learn_fig_bar_total
import braincup.composeapp.generated.resources.learn_fig_corners
import braincup.composeapp.generated.resources.learn_fig_cos
import braincup.composeapp.generated.resources.learn_fig_degrees
import braincup.composeapp.generated.resources.learn_fig_gradient
import braincup.composeapp.generated.resources.learn_fig_in_each
import braincup.composeapp.generated.resources.learn_fig_mean
import braincup.composeapp.generated.resources.learn_fig_mean_value
import braincup.composeapp.generated.resources.learn_fig_more
import braincup.composeapp.generated.resources.learn_fig_percent_of
import braincup.composeapp.generated.resources.learn_fig_perimeter
import braincup.composeapp.generated.resources.learn_fig_rows
import braincup.composeapp.generated.resources.learn_fig_sides
import braincup.composeapp.generated.resources.learn_fig_sides_corners
import braincup.composeapp.generated.resources.learn_fig_sin
import braincup.composeapp.generated.resources.learn_fig_solid_cone
import braincup.composeapp.generated.resources.learn_fig_solid_cone_counts
import braincup.composeapp.generated.resources.learn_fig_solid_cube
import braincup.composeapp.generated.resources.learn_fig_solid_cube_counts
import braincup.composeapp.generated.resources.learn_fig_solid_cylinder
import braincup.composeapp.generated.resources.learn_fig_solid_cylinder_counts
import braincup.composeapp.generated.resources.learn_fig_solid_prism
import braincup.composeapp.generated.resources.learn_fig_solid_prism_counts
import braincup.composeapp.generated.resources.learn_fig_solid_pyramid
import braincup.composeapp.generated.resources.learn_fig_solid_pyramid_counts
import braincup.composeapp.generated.resources.learn_fig_solid_sphere
import braincup.composeapp.generated.resources.learn_fig_solid_sphere_counts
import braincup.composeapp.generated.resources.learn_fig_solid_triangular_prism
import braincup.composeapp.generated.resources.learn_fig_solid_triangular_prism_counts
import braincup.composeapp.generated.resources.learn_fig_standard_deviations
import braincup.composeapp.generated.resources.learn_fig_symbol_key
import braincup.composeapp.generated.resources.learn_fig_symmetry_lines
import braincup.composeapp.generated.resources.learn_fig_take_from_both_sides
import com.inspiredandroid.braincup.learn.BarLabel
import com.inspiredandroid.braincup.learn.LearnVisual
import com.inspiredandroid.braincup.learn.SolidKind
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Every word a figure writes on itself.
 *
 * A figure captions itself while drawing, inside a `DrawScope`, where `stringResource` cannot be
 * called at all. So the captions are looked up once in [LearnVisualCanvas] and handed down to
 * [VisualScope] as this, exactly the way the two font families already are.
 *
 * The lesson prose these figures illustrate is authored in English in `learn/content`, on purpose
 * and for the reasons `LearnModels` gives. A caption is not that prose: it is a word the app
 * prints in its own voice, on a screen whose every other label is translated, so leaving "cube"
 * or "3 lines of symmetry" in English would put untranslated text inside a translated frame.
 *
 * A field whose name ends in `Template` still carries its `%1$s` placeholders, because what a
 * figure counts is only known once it has worked it out. Fill those with [fillIn] at draw time.
 * The rest arrive finished, including the four whose noun has to agree with a number and so come
 * from a `<plurals>` the figure's own count picks the form of.
 */
internal class LearnVisualStrings(
    /** "4 sides, 4 corners", the single-colour caption of a polygon that counts both at once. */
    val sidesAndCorners: String,
    /** The same caption split in two, for the figure that colours each count like what it counts. */
    val sides: String,
    val corners: String,
    /** What a solid is called, for the figure that names one. */
    val solidNames: Map<SolidKind, String>,
    /** What a solid is made of, for the figure that counts its faces instead of naming it. */
    val solidCounts: Map<SolidKind, String>,
    /** Already counted and already pluralised, since the count is authored on the figure. */
    val symmetryLines: String,
    val areaTemplate: String,
    val perimeterTemplate: String,
    val takeFromBothSidesTemplate: String,
    /** How many rows an array's first band holds, already agreeing with [LearnVisual.ArrayDots.bandRows]. */
    val rows: String,
    val moreTemplate: String,
    val inEachTemplate: String,
    val percentOfTemplate: String,
    val gradientTemplate: String,
    val cosTemplate: String,
    val sinTemplate: String,
    val degreesTemplate: String,
    /** What each bar of a chart stands for, in the words under it. */
    val barLabels: Map<BarLabel, String>,
    /** The mean with its reading, for the chip parked on a chart's mean line. */
    val meanValueTemplate: String,
    /** The word alone, for the curve that marks its centre without giving a number. */
    val mean: String,
    /** How far from the mean a bell curve's tick stands, signed: "+1 sd", "-1 sd". */
    val standardDeviationsTemplate: String,
    val symbolKeyTemplate: String,
)

/**
 * Resolves every figure caption for the current locale.
 *
 * Takes [visual] for the captions whose noun has to agree with a number. English gets away with
 * one form for every count above one, so the figures used to build these by hand; most of the
 * languages here do not, and which form a count takes is the resource's business rather than an
 * `if` at the draw site. The counts come off the figure as the properties it draws from, so the
 * caption cannot end up counting something other than what is on screen.
 */
@Composable
internal fun learnVisualStrings(visual: LearnVisual): LearnVisualStrings {
    val sides = (visual as? LearnVisual.Polygon)?.drawnSides ?: 1
    val symmetryLines = (visual as? LearnVisual.Symmetry)?.lines ?: 1
    val rows = (visual as? LearnVisual.ArrayDots)?.bandRows ?: 1
    return LearnVisualStrings(
        sidesAndCorners = pluralStringResource(Res.plurals.learn_fig_sides_corners, sides, sides),
        sides = pluralStringResource(Res.plurals.learn_fig_sides, sides, sides),
        corners = pluralStringResource(Res.plurals.learn_fig_corners, sides, sides),
        solidNames = mapOf(
            SolidKind.CUBE to stringResource(Res.string.learn_fig_solid_cube),
            SolidKind.SPHERE to stringResource(Res.string.learn_fig_solid_sphere),
            SolidKind.CYLINDER to stringResource(Res.string.learn_fig_solid_cylinder),
            SolidKind.CONE to stringResource(Res.string.learn_fig_solid_cone),
            SolidKind.PRISM to stringResource(Res.string.learn_fig_solid_prism),
            SolidKind.TRIANGULAR_PRISM to stringResource(Res.string.learn_fig_solid_triangular_prism),
            SolidKind.PYRAMID to stringResource(Res.string.learn_fig_solid_pyramid),
        ),
        solidCounts = mapOf(
            SolidKind.CUBE to stringResource(Res.string.learn_fig_solid_cube_counts),
            SolidKind.SPHERE to stringResource(Res.string.learn_fig_solid_sphere_counts),
            SolidKind.CYLINDER to stringResource(Res.string.learn_fig_solid_cylinder_counts),
            SolidKind.CONE to stringResource(Res.string.learn_fig_solid_cone_counts),
            SolidKind.PRISM to stringResource(Res.string.learn_fig_solid_prism_counts),
            SolidKind.TRIANGULAR_PRISM to stringResource(Res.string.learn_fig_solid_triangular_prism_counts),
            SolidKind.PYRAMID to stringResource(Res.string.learn_fig_solid_pyramid_counts),
        ),
        symmetryLines = pluralStringResource(Res.plurals.learn_fig_symmetry_lines, symmetryLines, symmetryLines),
        areaTemplate = stringResource(Res.string.learn_fig_area),
        perimeterTemplate = stringResource(Res.string.learn_fig_perimeter),
        takeFromBothSidesTemplate = stringResource(Res.string.learn_fig_take_from_both_sides),
        rows = pluralStringResource(Res.plurals.learn_fig_rows, rows, rows),
        moreTemplate = stringResource(Res.string.learn_fig_more),
        inEachTemplate = stringResource(Res.string.learn_fig_in_each),
        percentOfTemplate = stringResource(Res.string.learn_fig_percent_of),
        gradientTemplate = stringResource(Res.string.learn_fig_gradient),
        cosTemplate = stringResource(Res.string.learn_fig_cos),
        sinTemplate = stringResource(Res.string.learn_fig_sin),
        degreesTemplate = stringResource(Res.string.learn_fig_degrees),
        barLabels = mapOf(
            BarLabel.BEFORE to stringResource(Res.string.learn_fig_bar_before),
            BarLabel.AFTER to stringResource(Res.string.learn_fig_bar_after),
            BarLabel.SCORE to stringResource(Res.string.learn_fig_bar_score),
            BarLabel.TOTAL to stringResource(Res.string.learn_fig_bar_total),
        ),
        meanValueTemplate = stringResource(Res.string.learn_fig_mean_value),
        mean = stringResource(Res.string.learn_fig_mean),
        standardDeviationsTemplate = stringResource(Res.string.learn_fig_standard_deviations),
        symbolKeyTemplate = stringResource(Res.string.learn_fig_symbol_key),
    )
}

/**
 * Substitutes [args] into a caption template's positional placeholders.
 *
 * The formatting overload of `stringResource` is a composable and the figures caption themselves
 * while drawing, so the templates arrive here unsubstituted and are filled in by hand.
 *
 * Only `%1$s` and `%1$d` style tokens are recognised, and a placeholder may repeat. Anything else
 * beginning with `%` is left exactly as written, which is what lets a caption print a literal
 * percent sign - "25% of 60" - without an escape a translator would have to know about.
 */
internal fun String.fillIn(vararg args: Any): String {
    val out = StringBuilder(length)
    var from = 0
    while (from < length) {
        val token = placeholderAt(from)
        if (token == null) {
            out.append(this[from])
            from++
        } else {
            out.append(args.getOrNull(token.first - 1) ?: "")
            from = token.second
        }
    }
    return out.toString()
}

/**
 * The placeholder starting at [start], as its 1-based argument number and the index just past it,
 * or null if no placeholder starts there.
 */
private fun String.placeholderAt(start: Int): Pair<Int, Int>? {
    if (this[start] != '%') return null
    var at = start + 1
    var index = 0
    while (at < length && this[at].isDigit()) {
        index = index * 10 + (this[at] - '0')
        at++
    }
    // No digits is not a placeholder, and neither is one that runs off the end of the string.
    if (at == start + 1 || at + 1 >= length) return null
    if (this[at] != '$' || (this[at + 1] != 's' && this[at + 1] != 'd')) return null
    return index to at + 2
}
