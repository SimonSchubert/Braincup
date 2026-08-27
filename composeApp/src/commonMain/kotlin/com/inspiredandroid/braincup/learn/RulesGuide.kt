package com.inspiredandroid.braincup.learn

/**
 * The rules guide: the rules and sign conventions of arithmetic, written out in one place.
 *
 * Arithmetic's counterpart to [ShapeGuide]. The sub-topics teach these where they first come up,
 * which is no help to someone who only wants to check which way round two minuses go. Each entry
 * leads with the rule as it would be written down, so the notation is the thing being looked up.
 *
 * Rules are authored here in English, exactly like the lesson prose in `learn/content`: this is
 * catalog content, not UI chrome. The screen around it goes through `strings.xml`.
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
        val rule: String,
        val meaning: String,
        val example: String? = null,
    )

    data class Section(
        val id: String,
        val title: String,
        val blurb: String,
        val rules: List<Entry>,
    )

    private val signs = Section(
        id = "signs",
        title = "Signs",
        blurb = "What happens when a minus meets a minus. The rules learners lose most marks on.",
        rules = listOf(
            Entry(
                id = "minus-of-minus",
                rule = "-(-a) = a",
                meaning = "A minus in front of a minus cancels it out",
                example = "-(-7) = 7",
            ),
            Entry(
                id = "subtract-negative",
                rule = "a - (-b) = a + b",
                meaning = "Subtracting a negative adds",
                example = "5 - (-3) = 8",
            ),
            Entry(
                id = "add-negative",
                rule = "a + (-b) = a - b",
                meaning = "Adding a negative subtracts",
                example = "5 + (-3) = 2",
            ),
            Entry(
                id = "times-two-negatives",
                rule = "- x - = +",
                meaning = "Two negatives multiply to a positive",
                example = "(-4) x (-3) = 12",
            ),
            Entry(
                id = "times-one-negative",
                rule = "- x + = -",
                meaning = "One negative flips the sign",
                example = "(-4) x 3 = -12",
            ),
            Entry(
                id = "divide-negatives",
                rule = "- / - = +",
                meaning = "Division follows the same sign rule as multiplication",
                example = "(-12) / (-3) = 4",
            ),
            Entry(
                id = "order-negatives",
                rule = "-5 < -2",
                meaning = "The further left on the line, the smaller the number",
                example = "0 > -2 > -5",
            ),
            Entry(
                id = "move-on-the-line",
                rule = "-3 + 5 = 2",
                meaning = "Adding moves right along the line, subtracting moves left",
                example = "2 - 5 = -3",
            ),
        ),
    )

    private val order = Section(
        id = "order",
        title = "Order of operations",
        blurb = "Which part of a long sum is worked out first.",
        rules = listOf(
            Entry(
                id = "times-before-plus",
                rule = "2 + 3 x 4 = 14",
                meaning = "Times and divide are done before plus and minus",
                example = "not 5 x 4",
            ),
            Entry(
                id = "brackets-first",
                rule = "3 x (4 + 2) = 18",
                meaning = "Brackets come before everything else",
            ),
            Entry(
                id = "left-to-right",
                rule = "20 / 5 x 2 = 8",
                meaning = "Same rank: work from left to right",
                example = "not 20 / 10",
            ),
            Entry(
                id = "powers-before-times",
                rule = "3 x 4² = 48",
                meaning = "Powers are worked out before times and divide",
                example = "not 12²",
            ),
        ),
    )

    private val zeroAndOne = Section(
        id = "zero-and-one",
        title = "Zero and one",
        blurb = "The two numbers that behave unlike any other.",
        rules = listOf(
            Entry(id = "add-zero", rule = "a + 0 = a", meaning = "Adding zero changes nothing", example = "9 + 0 = 9"),
            Entry(id = "times-one", rule = "a x 1 = a", meaning = "Multiplying by one changes nothing", example = "9 x 1 = 9"),
            Entry(id = "times-zero", rule = "a x 0 = 0", meaning = "Anything times zero is zero", example = "9 x 0 = 0"),
            Entry(
                id = "divide-by-itself",
                rule = "a / a = 1",
                meaning = "A number shared by itself is one",
                example = "9 / 9 = 1",
            ),
            Entry(id = "zero-shared", rule = "0 / a = 0", meaning = "Zero shared out is still zero", example = "0 / 9 = 0"),
            Entry(
                id = "divide-by-zero",
                rule = "a / 0",
                meaning = "Dividing by zero has no answer at all",
                example = "not zero, not one",
            ),
        ),
    )

    private val rearranging = Section(
        id = "rearranging",
        title = "Rearranging",
        blurb = "What a sum still comes to when the numbers are moved about.",
        rules = listOf(
            Entry(
                id = "swap-add",
                rule = "a + b = b + a",
                meaning = "Order does not matter when adding",
                example = "3 + 8 = 8 + 3",
            ),
            Entry(
                id = "swap-times",
                rule = "a x b = b x a",
                meaning = "Order does not matter when multiplying",
                example = "3 x 8 = 8 x 3",
            ),
            Entry(
                id = "no-swap",
                rule = "a - b ≠ b - a",
                meaning = "Order does matter when subtracting or dividing",
                example = "9 - 4 = 5 but 4 - 9 = -5",
            ),
            Entry(
                id = "regroup",
                rule = "(a + b) + c = a + (b + c)",
                meaning = "Group a chain of adds whichever way is easier",
                example = "7 + 3 + 8 = 10 + 8",
            ),
            Entry(
                id = "split-the-times",
                rule = "a x (b + c) = a x b + a x c",
                meaning = "Split one hard multiplication into two easy ones",
                example = "4 x 23 = 4 x 20 + 4 x 3",
            ),
        ),
    )

    private val fractions = Section(
        id = "fractions",
        title = "Fractions",
        blurb = "A tight slash is a fraction line: 3/4 is three quarters.",
        rules = listOf(
            Entry(
                id = "add-fractions",
                rule = "a/c + b/c = (a + b)/c",
                meaning = "Same bottom number: add the tops and keep the bottom",
                example = "1/5 + 2/5 = 3/5",
            ),
            Entry(
                id = "equivalent",
                rule = "a/b = (a x k)/(b x k)",
                meaning = "Times top and bottom by the same number and the value holds",
                example = "1/2 = 3/6",
            ),
            Entry(
                id = "times-fractions",
                rule = "a/b x c/d = (a x c)/(b x d)",
                meaning = "Multiply the tops together and the bottoms together",
                example = "2/3 x 3/4 = 6/12 = 1/2",
            ),
            Entry(
                id = "divide-fractions",
                rule = "a/b / (c/d) = a/b x d/c",
                meaning = "To divide by a fraction, turn it upside down and multiply",
                example = "1/2 / (1/4) = 2",
            ),
            Entry(
                id = "mixed-number",
                rule = "2 1/3 = 7/3",
                meaning = "Whole times the bottom, plus the top",
                example = "2 x 3 + 1 = 7",
            ),
        ),
    )

    private val decimals = Section(
        id = "decimals",
        title = "Decimals and percents",
        blurb = "The same amount written three ways.",
        rules = listOf(
            Entry(
                id = "three-ways",
                rule = "1/2 = 0.5 = 50%",
                meaning = "A fraction, a decimal and a percentage can be one number",
                example = "3/4 = 0.75 = 75%",
            ),
            Entry(
                id = "percent-meaning",
                rule = "x% = x/100",
                meaning = "Percent means out of a hundred",
                example = "25% = 25/100 = 1/4",
            ),
            Entry(
                id = "percent-of",
                rule = "x% of n = x/100 x n",
                meaning = "For a percentage of an amount, divide by 100 then times",
                example = "20% of 80 = 16",
            ),
            Entry(
                id = "times-ten",
                rule = "3.7 x 10 = 37",
                meaning = "Times ten moves the point one place right",
                example = "3.7 / 10 = 0.37",
            ),
        ),
    )

    private val powers = Section(
        id = "powers",
        title = "Powers and roots",
        blurb = "Shorthand for multiplying a number by itself, and how to undo it.",
        rules = listOf(
            Entry(id = "square", rule = "a² = a x a", meaning = "A square is a number times itself", example = "5² = 25"),
            Entry(id = "cube", rule = "a³ = a x a x a", meaning = "A cube is three of them multiplied", example = "2³ = 8"),
            Entry(
                id = "power-zero",
                rule = "a¹ = a, a⁰ = 1",
                meaning = "To the power one is itself, and to the power zero is one",
                example = "7⁰ = 1",
            ),
            Entry(
                id = "root",
                rule = "√25 = 5",
                meaning = "A square root undoes a square: what was multiplied by itself",
                example = "5 x 5 = 25",
            ),
            Entry(
                id = "powers-of-ten",
                rule = "10³ = 1000",
                meaning = "A power of ten is a one with that many zeros",
                example = "10⁶ = 1 million",
            ),
        ),
    )

    private val rounding = Section(
        id = "rounding",
        title = "Rounding",
        blurb = "Cutting a number down to the size the question asks for.",
        rules = listOf(
            Entry(
                id = "round-half-up",
                rule = "3.5 → 4",
                meaning = "Five or more rounds up, four or less rounds down",
                example = "3.4 → 3",
            ),
            Entry(
                id = "round-look-right",
                rule = "6749 → 6700",
                meaning = "Look only at the digit just right of where you are cutting",
                example = "to the nearest hundred",
            ),
            Entry(
                id = "significant-figures",
                rule = "0.0461 → 0.046",
                meaning = "Significant figures start counting at the first digit that is not zero",
                example = "to 2 significant figures",
            ),
        ),
    )

    val sections: List<Section> = listOf(signs, order, zeroAndOne, rearranging, fractions, decimals, powers, rounding)

    val ruleCount: Int = sections.sumOf { it.rules.size }
}
