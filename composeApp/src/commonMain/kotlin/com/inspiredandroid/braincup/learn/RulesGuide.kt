package com.inspiredandroid.braincup.learn

import braincup.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource

/**
 * The rules guide: the rules and sign conventions of arithmetic, written out in one place.
 *
 * Arithmetic's counterpart to [ShapeGuide]. The sub-topics teach these where they first come up,
 * which is no help to someone who only wants to check which way round two minuses go. Each entry
 * leads with the rule as it would be written down, so the notation is the thing being looked up.
 *
 * Rules go through `strings.xml` like the lessons themselves, keyed by the entry ids below:
 * `learn_rule_minus_of_minus_rule`, `..._meaning`, `..._example`. Only the ids stay here.
 *
 * Notation follows the lessons: `x` multiplies, a spaced `/` divides, and a tight one is a
 * fraction line, because `MathText` is what renders these. Negatives are written in brackets,
 * `(-3)`, since a minus after a letter or digit is spaced out as a subtraction.
 */
object RulesGuide {

    /**
     * One rule: the line to remember, what it means in words, and an instance of it at work.
     *
     * [rule] and [example] are notation and render through `MathText`; [meaning] is prose and must
     * not carry operators, which that formatter would re-space.
     */
    data class Entry(
        val id: String,
        val rule: CatalogText,
        val meaning: StringResource,
        val example: CatalogText? = null,
    )

    private val signs = GuideSection(
        id = "signs",
        title = Res.string.learn_rulesguide_signs_title,
        blurb = Res.string.learn_rulesguide_signs_blurb,
        entries = listOf(
            Entry(
                id = "minus-of-minus",
                rule = math("-(-a) = a"),
                meaning = Res.string.learn_rule_minus_of_minus_meaning,
                example = math("-(-7) = 7"),
            ),
            Entry(
                id = "subtract-negative",
                rule = math("a - (-b) = a + b"),
                meaning = Res.string.learn_rule_subtract_negative_meaning,
                example = math("5 - {b:(-3)} = 8"),
            ),
            Entry(
                id = "add-negative",
                rule = math("a + (-b) = a - b"),
                meaning = Res.string.learn_rule_add_negative_meaning,
                example = math("5 + {b:(-3)} = 2"),
            ),
            Entry(
                id = "times-two-negatives",
                rule = math("- x - = +"),
                meaning = Res.string.learn_rule_times_two_negatives_meaning,
                example = math("(-4) x {b:(-3)} = 12"),
            ),
            Entry(
                id = "times-one-negative",
                rule = math("- x + = -"),
                meaning = Res.string.learn_rule_times_one_negative_meaning,
                example = math("(-4) x {b:3} = -12"),
            ),
            Entry(
                id = "divide-negatives",
                rule = math("- / - = +"),
                meaning = Res.string.learn_rule_divide_negatives_meaning,
                example = math("(-12) / {b:(-3)} = 4"),
            ),
            Entry(
                id = "order-negatives",
                rule = math("-5 < -2"),
                meaning = Res.string.learn_rule_order_negatives_meaning,
                example = math("0 > -2 > -5"),
            ),
            Entry(
                id = "move-on-the-line",
                rule = math("-3 + 5 = 2"),
                meaning = Res.string.learn_rule_move_on_the_line_meaning,
                example = math("2 - {b:5} = -3"),
            ),
        ),
    )

    private val order = GuideSection(
        id = "order",
        title = Res.string.learn_rulesguide_order_title,
        blurb = Res.string.learn_rulesguide_order_blurb,
        entries = listOf(
            Entry(
                id = "times-before-plus",
                rule = math("2 + 3 x 4 = 14"),
                meaning = Res.string.learn_rule_times_before_plus_meaning,
                example = words(Res.string.learn_rule_times_before_plus_example),
            ),
            Entry(
                id = "brackets-first",
                rule = math("3 x (4 + 2) = 18"),
                meaning = Res.string.learn_rule_brackets_first_meaning,
                example = math("3 x {b:6} = 18"),
            ),
            Entry(
                id = "left-to-right",
                rule = math("20 / 5 x 2 = 8"),
                meaning = Res.string.learn_rule_left_to_right_meaning,
                example = words(Res.string.learn_rule_left_to_right_example),
            ),
            Entry(
                id = "powers-before-times",
                rule = math("3 x 4² = 48"),
                meaning = Res.string.learn_rule_powers_before_times_meaning,
                example = words(Res.string.learn_rule_powers_before_times_example),
            ),
        ),
    )

    private val zeroAndOne = GuideSection(
        id = "zero-and-one",
        title = Res.string.learn_rulesguide_zero_and_one_title,
        blurb = Res.string.learn_rulesguide_zero_and_one_blurb,
        entries = listOf(
            Entry(id = "add-zero", rule = math("a + 0 = a"), meaning = Res.string.learn_rule_add_zero_meaning, example = math("9 + {b:0} = 9")),
            Entry(id = "times-one", rule = math("a x 1 = a"), meaning = Res.string.learn_rule_times_one_meaning, example = math("9 x {b:1} = 9")),
            Entry(id = "times-zero", rule = math("a x 0 = 0"), meaning = Res.string.learn_rule_times_zero_meaning, example = math("9 x {b:0} = 0")),
            Entry(
                id = "divide-by-itself",
                rule = math("a / a = 1"),
                meaning = Res.string.learn_rule_divide_by_itself_meaning,
                example = math("9 / {b:9} = 1"),
            ),
            Entry(id = "zero-shared", rule = math("0 / a = 0"), meaning = Res.string.learn_rule_zero_shared_meaning, example = math("0 / {b:9} = 0")),
            Entry(
                id = "divide-by-zero",
                rule = math("a / 0"),
                meaning = Res.string.learn_rule_divide_by_zero_meaning,
                example = words(Res.string.learn_rule_divide_by_zero_example),
            ),
        ),
    )

    private val rearranging = GuideSection(
        id = "rearranging",
        title = Res.string.learn_rulesguide_rearranging_title,
        blurb = Res.string.learn_rulesguide_rearranging_blurb,
        entries = listOf(
            Entry(
                id = "swap-add",
                rule = math("a + b = b + a"),
                meaning = Res.string.learn_rule_swap_add_meaning,
                example = math("3 + 8 = 8 + 3"),
            ),
            Entry(
                id = "swap-times",
                rule = math("a x b = b x a"),
                meaning = Res.string.learn_rule_swap_times_meaning,
                example = math("3 x 8 = 8 x 3"),
            ),
            Entry(
                id = "no-swap",
                rule = math("a - b ≠ b - a"),
                meaning = Res.string.learn_rule_no_swap_meaning,
                example = words(Res.string.learn_rule_no_swap_example),
            ),
            Entry(
                id = "regroup",
                rule = math("(a + b) + c = a + (b + c)"),
                meaning = Res.string.learn_rule_regroup_meaning,
                example = math("7 + 3 + 8 = 10 + 8"),
            ),
            Entry(
                id = "split-the-times",
                rule = math("a x (b + c) = a x b + a x c"),
                meaning = Res.string.learn_rule_split_the_times_meaning,
                example = math("4 x 23 = 4 x 20 + 4 x 3"),
            ),
        ),
    )

    private val fractions = GuideSection(
        id = "fractions",
        title = Res.string.learn_rulesguide_fractions_title,
        blurb = Res.string.learn_rulesguide_fractions_blurb,
        entries = listOf(
            Entry(
                id = "add-fractions",
                rule = math("a/c + b/c = (a + b)/c"),
                meaning = Res.string.learn_rule_add_fractions_meaning,
                example = math("1/5 + {b:2/5} = 3/5"),
            ),
            Entry(
                id = "equivalent",
                rule = math("a/b = (a x k)/(b x k)"),
                meaning = Res.string.learn_rule_equivalent_meaning,
                example = math("1/2 = 3/6"),
            ),
            Entry(
                id = "times-fractions",
                rule = math("a/b x c/d = (a x c)/(b x d)"),
                meaning = Res.string.learn_rule_times_fractions_meaning,
                example = math("2/3 x 3/4 = 6/12 = 1/2"),
            ),
            Entry(
                id = "divide-fractions",
                rule = math("a/b / (c/d) = a/b x d/c"),
                meaning = Res.string.learn_rule_divide_fractions_meaning,
                example = math("1/2 / {b:(1/4)} = 2"),
            ),
            Entry(
                id = "mixed-number",
                rule = math("2 1/3 = 7/3"),
                meaning = Res.string.learn_rule_mixed_number_meaning,
                example = math("2 x 3 + 1 = 7"),
            ),
        ),
    )

    private val decimals = GuideSection(
        id = "decimals",
        title = Res.string.learn_rulesguide_decimals_title,
        blurb = Res.string.learn_rulesguide_decimals_blurb,
        entries = listOf(
            Entry(
                id = "three-ways",
                rule = math("1/2 = 0.5 = 50%"),
                meaning = Res.string.learn_rule_three_ways_meaning,
                example = math("3/4 = 0.75 = 75%"),
            ),
            Entry(
                id = "percent-meaning",
                rule = math("x% = x/100"),
                meaning = Res.string.learn_rule_percent_meaning_meaning,
                example = math("25% = 25/100 = 1/4"),
            ),
            Entry(
                id = "percent-of",
                rule = words(Res.string.learn_rule_percent_of_rule),
                meaning = Res.string.learn_rule_percent_of_meaning,
                example = words(Res.string.learn_rule_percent_of_example),
            ),
            Entry(
                id = "times-ten",
                rule = math("3.7 x 10 = 37"),
                meaning = Res.string.learn_rule_times_ten_meaning,
                example = math("3.7 / {b:10} = 0.37"),
            ),
        ),
    )

    private val powers = GuideSection(
        id = "powers",
        title = Res.string.learn_rulesguide_powers_title,
        blurb = Res.string.learn_rulesguide_powers_blurb,
        entries = listOf(
            Entry(id = "square", rule = math("a² = a x a"), meaning = Res.string.learn_rule_square_meaning, example = math("5² = 25")),
            Entry(id = "cube", rule = math("a³ = a x a x a"), meaning = Res.string.learn_rule_cube_meaning, example = math("2³ = 8")),
            Entry(
                id = "power-zero",
                rule = math("a¹ = a, a⁰ = 1"),
                meaning = Res.string.learn_rule_power_zero_meaning,
                example = math("7⁰ = 1"),
            ),
            Entry(
                id = "root",
                rule = math("√25 = 5"),
                meaning = Res.string.learn_rule_root_meaning,
                example = math("5 x {b:5} = 25"),
            ),
            Entry(
                id = "powers-of-ten",
                rule = math("10³ = 1000"),
                meaning = Res.string.learn_rule_powers_of_ten_meaning,
                example = words(Res.string.learn_rule_powers_of_ten_example),
            ),
        ),
    )

    private val rounding = GuideSection(
        id = "rounding",
        title = Res.string.learn_rulesguide_rounding_title,
        blurb = Res.string.learn_rulesguide_rounding_blurb,
        entries = listOf(
            Entry(
                id = "round-half-up",
                rule = math("3.5 → 4"),
                meaning = Res.string.learn_rule_round_half_up_meaning,
                example = math("3.4 → 3"),
            ),
            Entry(
                id = "round-look-right",
                rule = math("6749 → 6700"),
                meaning = Res.string.learn_rule_round_look_right_meaning,
                example = words(Res.string.learn_rule_round_look_right_example),
            ),
            Entry(
                id = "significant-figures",
                rule = math("0.0461 → 0.046"),
                meaning = Res.string.learn_rule_significant_figures_meaning,
                example = words(Res.string.learn_rule_significant_figures_example),
            ),
        ),
    )

    val sections: List<GuideSection<Entry>> = listOf(signs, order, zeroAndOne, rearranging, fractions, decimals, powers, rounding)

    val ruleCount: Int = sections.sumOf { it.entries.size }
}
