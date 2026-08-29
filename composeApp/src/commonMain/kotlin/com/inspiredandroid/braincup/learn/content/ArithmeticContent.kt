package com.inspiredandroid.braincup.learn.content

import braincup.composeapp.generated.resources.*
import braincup.composeapp.generated.resources.Res
import com.inspiredandroid.braincup.learn.BarLabel
import com.inspiredandroid.braincup.learn.GradeLevel
import com.inspiredandroid.braincup.learn.LearnUnit
import com.inspiredandroid.braincup.learn.LearnVisual.AreaGrid
import com.inspiredandroid.braincup.learn.LearnVisual.ArrayDots
import com.inspiredandroid.braincup.learn.LearnVisual.BarChart
import com.inspiredandroid.braincup.learn.LearnVisual.DecimalGrid
import com.inspiredandroid.braincup.learn.LearnVisual.Fraction
import com.inspiredandroid.braincup.learn.LearnVisual.NumberLine
import com.inspiredandroid.braincup.learn.LearnVisual.PlaceValue
import com.inspiredandroid.braincup.learn.LearnVisual.RatioBar
import com.inspiredandroid.braincup.learn.LearnVisual.RightTriangle
import com.inspiredandroid.braincup.learn.LearnVisual.Steps
import com.inspiredandroid.braincup.learn.LearnVisual.TenFrame
import com.inspiredandroid.braincup.learn.LessonSpec
import com.inspiredandroid.braincup.learn.LessonStep.Choice
import com.inspiredandroid.braincup.learn.LessonStep.Concept
import com.inspiredandroid.braincup.learn.LessonStep.Numeric
import com.inspiredandroid.braincup.learn.LessonStep.Worked
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.learn.QuizQuestion
import com.inspiredandroid.braincup.learn.counted
import com.inspiredandroid.braincup.learn.filled
import com.inspiredandroid.braincup.learn.learnUnit
import com.inspiredandroid.braincup.learn.math
import com.inspiredandroid.braincup.learn.mathOptions
import com.inspiredandroid.braincup.learn.wordOptions
import com.inspiredandroid.braincup.learn.words

/**
 * Arithmetic: counting and first sums, then multiplication, fractions and decimals, then ratio and
 * percent, and finally the numbers that need rules of their own - standard form, surds and bounds.
 */
internal object ArithmeticContent {

    private val counting = learnUnit(
        topic = MathTopic.ARITHMETIC,
        urlSlug = "counting",
        title = Res.string.learn_unit_arithmetic_counting_title,
        summary = Res.string.learn_unit_arithmetic_counting_summary,
        level = GradeLevel.GRADES_1_2,
        lessons = listOf(
            LessonSpec(
                id = "g12-arithmetic-counting",
                title = Res.string.learn_g12_arithmetic_counting_title,
                summary = Res.string.learn_g12_arithmetic_counting_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_g12_arithmetic_counting_s1_body),
                        visual = NumberLine(from = 0, to = 10, start = 6, jump = 1, thenJump = -1),
                    ),
                    Concept(
                        body = words(Res.string.learn_g12_arithmetic_counting_s2_body),
                        formula = math("7 + {b:3} = 10"),
                        visual = NumberLine(from = 0, to = 12, start = 7, jump = 3, hops = 3),
                    ),
                    Choice(
                        question = words(Res.string.learn_g12_arithmetic_counting_s3_question),
                        formula = math("12 + {b:4} = ?"),
                        options = mathOptions("14", "15", "16", "17"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_g12_arithmetic_counting_s3_explanation),
                        visual = NumberLine(from = 10, to = 20, start = 12, jump = 4, hops = 4, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_g12_arithmetic_counting_s4_body),
                        formula = math("10, 20, 30, 40, 50"),
                        visual = Steps(terms = listOf(10, 20, 30, 40, 50)),
                    ),
                    Choice(
                        question = words(Res.string.learn_g12_arithmetic_counting_s5_question),
                        formula = math("30, 40, 50, ?"),
                        options = mathOptions("51", "55", "60", "70"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_g12_arithmetic_counting_s5_explanation),
                        visual = Steps(terms = listOf(30, 40, 50)),
                    ),
                    Choice(
                        question = words(Res.string.learn_g12_arithmetic_counting_s6_question),
                        formula = math("40 - {b:1} = ?"),
                        options = mathOptions("30", "39", "41", "44"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_g12_arithmetic_counting_s6_explanation),
                        visual = NumberLine(from = 35, to = 45, start = 40, jump = -1, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "g12-arithmetic-addsub",
                title = Res.string.learn_g12_arithmetic_addsub_title,
                summary = Res.string.learn_g12_arithmetic_addsub_summary,
                steps = listOf(
                    Worked(
                        problem = math("{a:6} + {b:7} = ?"),
                        lines = listOf(
                            words(Res.string.learn_g12_arithmetic_addsub_s1_l1),
                            words(Res.string.learn_g12_arithmetic_addsub_s1_l2),
                            words(Res.string.learn_g12_arithmetic_addsub_s1_l3),
                            math("{a:6} + {b:4} = 10"),
                            math("10 + {b:3} = 13"),
                        ),
                        result = math("13"),
                        visual = TenFrame(filled = 6, added = 7, reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_g12_arithmetic_addsub_s2_question),
                        formula = math("{a:7} + ? = 10"),
                        options = mathOptions("2", "3", "4", "5"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_g12_arithmetic_addsub_s2_explanation),
                        visual = TenFrame(filled = 7, added = 0, reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_g12_arithmetic_addsub_s3_question),
                        formula = math("{a:8} + {b:5} = ?"),
                        options = mathOptions("12", "13", "14", "15"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_g12_arithmetic_addsub_s3_explanation),
                        visual = TenFrame(filled = 8, added = 5, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_g12_arithmetic_addsub_s4_body),
                        formula = math("15 - {b:8} = 15 - {b:5} - {b:3}"),
                        visual = NumberLine(from = 0, to = 16, start = 15, hopSteps = listOf(-5, -3)),
                    ),
                    Choice(
                        question = words(Res.string.learn_g12_arithmetic_addsub_s5_question),
                        formula = math("13 - {b:5} = ?"),
                        options = mathOptions("6", "7", "8", "9"),
                        correctIndex = 2,
                        explanation = filled(Res.string.learn_t_lands_on_ten, "13", "3", "10", "2", "8"),
                        visual = NumberLine(from = 5, to = 15, start = 13, hopSteps = listOf(-3, -2), reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_g12_arithmetic_addsub_s6_question),
                        formula = math("15 - {b:9} = ?"),
                        options = mathOptions("5", "6", "7", "8"),
                        correctIndex = 1,
                        explanation = filled(Res.string.learn_t_lands_on_ten, "15", "5", "10", "4", "6"),
                        visual = NumberLine(from = 5, to = 16, start = 15, hopSteps = listOf(-5, -4), reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "g12-arithmetic-tens",
                title = Res.string.learn_g12_arithmetic_tens_title,
                summary = Res.string.learn_g12_arithmetic_tens_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_g12_arithmetic_tens_s1_body),
                        formula = math("37 = {a:30} + {b:7}"),
                        visual = PlaceValue(tens = 3, ones = 7),
                    ),
                    Choice(
                        question = words(Res.string.learn_g12_arithmetic_tens_s2_question),
                        options = mathOptions("6", "8", "60", "68"),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_g12_arithmetic_tens_s2_explanation),
                        visual = PlaceValue(tens = 6, ones = 8, reveal = false),
                    ),
                    Worked(
                        problem = math("{a:25} + {b:13} = ?"),
                        lines = listOf(
                            words(Res.string.learn_g12_arithmetic_tens_s3_l1),
                            words(Res.string.learn_g12_arithmetic_tens_s3_l2),
                            words(Res.string.learn_g12_arithmetic_tens_s3_l3),
                        ),
                        result = math("38"),
                        visual = PlaceValue(tens = 2, ones = 5, plus = 1 to 3),
                    ),
                    Choice(
                        question = words(Res.string.learn_g12_arithmetic_tens_s4_question),
                        options = mathOptions("6", "24", "42", "62"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_g12_arithmetic_tens_s4_explanation),
                        visual = PlaceValue(tens = 4, ones = 2, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_g12_arithmetic_tens_s5_body),
                        visual = PlaceValue(tens = 7, ones = 4, compare = 4 to 7),
                    ),
                    Choice(
                        question = words(Res.string.learn_g12_arithmetic_tens_s6_question),
                        options = listOf(
                            math("63"),
                            math("68"),
                            words(Res.string.learn_shared_they_equal),
                            words(Res.string.learn_shared_you_cannot_tell),
                        ),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_g12_arithmetic_tens_s6_explanation),
                        visual = PlaceValue(tens = 6, ones = 3, compare = 6 to 8, reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = words(Res.string.learn_unit_arithmetic_counting_q1_prompt),
                options = mathOptions("41", "45", "50", "60"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_arithmetic_counting_q1_explanation),
                visual = Steps(terms = listOf(20, 30, 40)),
            ),
            QuizQuestion(
                prompt = math("9 + {b:6} = ?"),
                options = mathOptions("14", "15", "16", "13"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_arithmetic_counting_q2_explanation),
                // The frame stops at the nine it is handed. Filled to fifteen it draws the
                // regrouping the question is asking for, ten dots and five, and the answer is
                // read off rather than worked out. One gap in the frame is the hint the method
                // needs; the six the learner has to place is on the card above.
                visual = TenFrame(filled = 9, added = 0, reveal = false),
            ),
            QuizQuestion(
                prompt = math("14 - {b:6} = ?"),
                options = mathOptions("6", "7", "8", "9"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_arithmetic_counting_q3_explanation),
                // Where the count starts, not where it finishes. The hops that bridge through ten
                // are the working, and a test figure that draws them lands on the answer itself.
                visual = NumberLine(from = 0, to = 16, start = 14, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_arithmetic_counting_q4_prompt),
                options = mathOptions("5", "3", "50", "53"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_arithmetic_counting_q4_explanation),
                visual = PlaceValue(tens = 5, ones = 3, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_arithmetic_counting_q5_prompt),
                options = listOf(
                    math("62"),
                    math("26"),
                    words(Res.string.learn_shared_they_equal),
                    words(Res.string.learn_shared_you_cannot_tell),
                ),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_arithmetic_counting_q5_explanation),
                visual = PlaceValue(tens = 6, ones = 2, compare = 2 to 6, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_arithmetic_counting_q6_prompt),
                options = mathOptions("26", "30", "35", "40"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_arithmetic_counting_q6_explanation),
                visual = Steps(terms = listOf(15, 20, 25)),
            ),
        ),
    )

    private val multiplication = learnUnit(
        topic = MathTopic.ARITHMETIC,
        urlSlug = "multiplication",
        title = Res.string.learn_unit_arithmetic_multiplication_title,
        summary = Res.string.learn_unit_arithmetic_multiplication_summary,
        level = GradeLevel.GRADES_3_5,
        lessons = listOf(
            LessonSpec(
                id = "g35-arithmetic-multiplication",
                title = Res.string.learn_g35_arithmetic_multiplication_title,
                summary = Res.string.learn_g35_arithmetic_multiplication_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_g35_arithmetic_multiplication_s1_body),
                        formula = math("4 x {b:6} = 24"),
                        visual = ArrayDots(rows = 4, cols = 6),
                    ),
                    Concept(
                        body = words(Res.string.learn_g35_arithmetic_multiplication_s2_body),
                        formula = math("{a:6} x {b:4} = {b:4} x {a:6}"),
                        visual = ArrayDots(rows = 6, cols = 4),
                    ),
                    Choice(
                        question = words(Res.string.learn_g35_arithmetic_multiplication_s3_question),
                        formula = math("7 x {b:3} = ?"),
                        options = mathOptions("18", "21", "24", "27"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_g35_arithmetic_multiplication_s3_explanation),
                        visual = ArrayDots(rows = 7, cols = 3),
                    ),
                    Concept(
                        body = words(Res.string.learn_g35_arithmetic_multiplication_s4_body),
                        formula = math("3, 6, 9, 12"),
                        visual = Steps(terms = listOf(3, 6, 9, 12)),
                    ),
                    Concept(
                        body = words(Res.string.learn_g35_arithmetic_multiplication_s5_body),
                        formula = words(Res.string.learn_g35_arithmetic_multiplication_s5_formula),
                        visual = ArrayDots(rows = 6, cols = 6, split = 3),
                    ),
                    Numeric(
                        question = words(Res.string.learn_g35_arithmetic_multiplication_s6_question),
                        formula = math("4 x {b:7} = ?"),
                        answer = "28",
                        explanation = words(Res.string.learn_g35_arithmetic_multiplication_s6_explanation),
                        visual = ArrayDots(rows = 4, cols = 7),
                    ),
                ),
            ),
            LessonSpec(
                id = "arithmetic-multiplication-facts",
                title = Res.string.learn_arithmetic_multiplication_facts_title,
                summary = Res.string.learn_arithmetic_multiplication_facts_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_arithmetic_multiplication_facts_s1_body),
                        formula = math("7 x 4 = {a:5} x 4 + {b:2} x 4"),
                        visual = ArrayDots(rows = 7, cols = 4, split = 5),
                    ),
                    Worked(
                        problem = math("{a:6} x 8 = ?"),
                        lines = listOf(
                            words(Res.string.learn_arithmetic_multiplication_facts_s2_l1),
                            math("{a:5} x 8 = 40"),
                            words(Res.string.learn_arithmetic_multiplication_facts_s2_l3),
                            math("40 + {b:8}"),
                        ),
                        result = math("48"),
                        visual = ArrayDots(rows = 6, cols = 8, split = 5),
                    ),
                    Concept(
                        body = words(Res.string.learn_arithmetic_multiplication_facts_s3_body),
                        formula = math("10 x {b:6} = 60"),
                        visual = ArrayDots(rows = 10, cols = 6),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_multiplication_facts_s4_question),
                        formula = math("9 x {b:6} = ?"),
                        options = mathOptions("45", "48", "54", "56"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_arithmetic_multiplication_facts_s4_explanation),
                        visual = ArrayDots(rows = 9, cols = 6),
                    ),
                    Numeric(
                        question = words(Res.string.learn_arithmetic_multiplication_facts_s5_question),
                        formula = math("7 x {b:8} = ?"),
                        answer = "56",
                        explanation = words(Res.string.learn_arithmetic_multiplication_facts_s5_explanation),
                        visual = ArrayDots(rows = 7, cols = 8, split = 5),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_multiplication_facts_s6_question),
                        formula = math("4 x {b:9} = ?"),
                        options = mathOptions("13", "18", "27", "36"),
                        correctIndex = 3,
                        explanation = words(Res.string.learn_arithmetic_multiplication_facts_s6_explanation),
                        visual = ArrayDots(rows = 4, cols = 9, split = 2),
                    ),
                ),
            ),
            LessonSpec(
                id = "arithmetic-multiplication-division",
                title = Res.string.learn_arithmetic_multiplication_division_title,
                summary = Res.string.learn_arithmetic_multiplication_division_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_arithmetic_multiplication_division_s1_body),
                        formula = math("28 / {b:7} = 4"),
                        visual = ArrayDots(rows = 4, cols = 7),
                    ),
                    Concept(
                        body = words(Res.string.learn_arithmetic_multiplication_division_s2_body),
                        formula = words(Res.string.learn_arithmetic_multiplication_division_s2_formula),
                        visual = ArrayDots(rows = 6, cols = 5),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_multiplication_division_s3_question),
                        formula = math("56 / {b:8} = ?"),
                        options = mathOptions("6", "7", "8", "9"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_arithmetic_multiplication_division_s3_explanation),
                        visual = ArrayDots(rows = 7, cols = 8),
                    ),
                    Concept(
                        body = words(Res.string.learn_arithmetic_multiplication_division_s4_body),
                        formula = math("17 / {b:5} = 3 r {b:2}"),
                        visual = ArrayDots(rows = 3, cols = 5, leftover = 2),
                    ),
                    Worked(
                        problem = words(Res.string.learn_arithmetic_multiplication_division_s5_problem),
                        lines = listOf(
                            words(Res.string.learn_arithmetic_multiplication_division_s5_l1),
                            words(Res.string.learn_arithmetic_multiplication_division_s5_l2),
                            words(Res.string.learn_arithmetic_multiplication_division_s5_l3),
                        ),
                        result = words(Res.string.learn_arithmetic_multiplication_division_s5_result),
                        visual = ArrayDots(rows = 7, cols = 4, leftover = 1),
                    ),
                    Numeric(
                        question = words(Res.string.learn_arithmetic_multiplication_division_s6_question),
                        answer = "7",
                        explanation = words(Res.string.learn_arithmetic_multiplication_division_s6_explanation),
                        visual = ArrayDots(rows = 7, cols = 5, leftover = 3),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = words(Res.string.learn_unit_arithmetic_multiplication_q1_prompt),
                options = mathOptions("54", "56", "48", "64"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_arithmetic_multiplication_q1_explanation),
                visual = ArrayDots(rows = 8, cols = 7),
            ),
            QuizQuestion(
                prompt = math("6 x {b:9} = ?"),
                options = mathOptions("45", "54", "56", "63"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_arithmetic_multiplication_q2_explanation),
                // The nines set off, three of the six. A 6 x 9 array draws 54 dots to count and
                // captions itself "5 rows" and "1 more", which is not the question posed but the
                // method and the answer together. The ladder stops well short of any option, so
                // reading it is counting on rather than reading off.
                visual = Steps(terms = listOf(0, 9, 18, 27)),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_arithmetic_multiplication_q3_prompt),
                options = mathOptions("7 + 4", "4 x 7", "7 x 7", "4 + 4 + 4"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_arithmetic_multiplication_q3_explanation),
                visual = ArrayDots(rows = 4, cols = 7),
            ),
            QuizQuestion(
                prompt = math("72 / {b:9} = ?"),
                options = mathOptions("6", "7", "8", "9"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_arithmetic_multiplication_q4_explanation),
                // A division answer is the row count, and an array prints its row count: drawn as
                // 8 x 9 this question captioned itself "8 rows". The ladder climbs the nines and
                // stops short of 72, so the eight has to be counted rather than read.
                visual = Steps(terms = listOf(18, 27, 36, 45)),
            ),
            QuizQuestion(
                prompt = math("23 / {b:4} = ?"),
                options = mathOptions("5 r 1", "5 r 3", "6 r 1", "4 r 7"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_arithmetic_multiplication_q5_explanation),
                // Drawn as five rows of four with three dots underneath, the figure spelled the
                // whole answer out: "5 rows" in words and the remainder alongside it.
                visual = Steps(terms = listOf(0, 4, 8, 12)),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_arithmetic_multiplication_q6_prompt),
                options = mathOptions("14", "42", "48", "54"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_arithmetic_multiplication_q6_explanation),
                visual = ArrayDots(rows = 8, cols = 6, split = 5),
            ),
        ),
    )

    private val fractions = learnUnit(
        topic = MathTopic.ARITHMETIC,
        urlSlug = "fractions",
        title = Res.string.learn_unit_arithmetic_fractions_title,
        summary = Res.string.learn_unit_arithmetic_fractions_summary,
        level = GradeLevel.GRADES_3_5,
        lessons = listOf(
            LessonSpec(
                id = "g35-arithmetic-fractions",
                title = Res.string.learn_g35_arithmetic_fractions_title,
                summary = Res.string.learn_g35_arithmetic_fractions_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_g35_arithmetic_fractions_s1_body),
                        formula = math("3/4"),
                        visual = Fraction(numerator = 3, denominator = 4),
                    ),
                    Choice(
                        question = words(Res.string.learn_shared_how_much_bar_shaded),
                        options = mathOptions("2/5", "3/5", "2/3", "5/2"),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_g35_arithmetic_fractions_s2_explanation),
                        visual = Fraction(numerator = 2, denominator = 5, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_g35_arithmetic_fractions_s3_body),
                        visual = Fraction(numerator = 1, denominator = 4, compare = 1 to 8),
                    ),
                    Choice(
                        question = words(Res.string.learn_shared_which_bar_shaded_more),
                        options = listOf(
                            math("3/8"),
                            math("5/8"),
                            words(Res.string.learn_shared_they_equal),
                            words(Res.string.learn_shared_you_cannot_tell),
                        ),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_g35_arithmetic_fractions_s4_explanation),
                        visual = Fraction(numerator = 3, denominator = 8, compare = 5 to 8, reveal = false),
                    ),
                    Numeric(
                        question = words(Res.string.learn_g35_arithmetic_fractions_s5_question),
                        formula = math("8/8 - {b:3/8} = ?/8"),
                        answer = "5",
                        explanation = words(Res.string.learn_g35_arithmetic_fractions_s5_explanation),
                        visual = Fraction(numerator = 8, denominator = 8, compare = 3 to 8, reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_g35_arithmetic_fractions_s6_question),
                        options = mathOptions("4/4", "3/4", "4/3", "1/4"),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_g35_arithmetic_fractions_s6_explanation),
                        visual = Fraction(numerator = 4, denominator = 4, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "arithmetic-fractions-equivalent",
                title = Res.string.learn_arithmetic_fractions_equivalent_title,
                summary = Res.string.learn_arithmetic_fractions_equivalent_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_arithmetic_fractions_equivalent_s1_body),
                        formula = math("1/2 = {b:4/8}"),
                        visual = Fraction(numerator = 1, denominator = 2, compare = 4 to 8),
                    ),
                    Concept(
                        body = words(Res.string.learn_arithmetic_fractions_equivalent_s2_body),
                        formula = math("2/3 = {b:4/6}"),
                        visual = Fraction(numerator = 2, denominator = 3, compare = 4 to 6),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_fractions_equivalent_s3_question),
                        options = mathOptions("3/4", "4/6", "2/6", "6/3"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_arithmetic_fractions_equivalent_s3_explanation),
                        visual = Fraction(numerator = 2, denominator = 3, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_arithmetic_fractions_equivalent_s4_body),
                        formula = math("6/8 = {b:3/4}"),
                        visual = Fraction(numerator = 6, denominator = 8, compare = 3 to 4),
                    ),
                    Choice(
                        question = words(Res.string.learn_shared_which_bar_shaded_more),
                        options = listOf(
                            math("2/3"),
                            math("3/4"),
                            words(Res.string.learn_shared_they_equal),
                            words(Res.string.learn_arithmetic_fractions_equivalent_s5_o4),
                        ),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_arithmetic_fractions_equivalent_s5_explanation),
                        visual = Fraction(numerator = 2, denominator = 3, compare = 3 to 4, reveal = false),
                    ),
                    Numeric(
                        question = words(Res.string.learn_arithmetic_fractions_equivalent_s6_question),
                        formula = math("3/5 = ?/10"),
                        answer = "6",
                        explanation = words(Res.string.learn_arithmetic_fractions_equivalent_s6_explanation),
                        visual = Fraction(numerator = 3, denominator = 5, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "arithmetic-fractions-add",
                title = Res.string.learn_arithmetic_fractions_add_title,
                summary = Res.string.learn_arithmetic_fractions_add_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_arithmetic_fractions_add_s1_body),
                        formula = math("3/5 + {b:1/5} = 4/5"),
                        visual = Fraction(numerator = 3, denominator = 5, plus = 1 to 5),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_fractions_add_s2_question),
                        formula = math("2/7 + {b:3/7} = ?"),
                        options = mathOptions("5/7", "5/14", "6/7", "1/7"),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_arithmetic_fractions_add_s2_explanation),
                        visual = Fraction(numerator = 2, denominator = 7, plus = 3 to 7, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_arithmetic_fractions_add_s3_body),
                        formula = math("5/6 - {b:2/6} = 3/6"),
                        visual = Fraction(numerator = 5, denominator = 6, compare = 2 to 6),
                    ),
                    Concept(
                        body = words(Res.string.learn_arithmetic_fractions_add_s4_body),
                        formula = math("1/2 = {b:2/4}"),
                        visual = Fraction(numerator = 1, denominator = 2, compare = 2 to 4),
                    ),
                    Worked(
                        problem = math("{a:1}/2 + {b:1}/4 = ?"),
                        lines = listOf(
                            words(Res.string.learn_arithmetic_fractions_add_s5_l1),
                            words(Res.string.learn_arithmetic_fractions_add_s5_l2),
                            words(Res.string.learn_arithmetic_fractions_add_s5_l3),
                            math("{a:2}/4 + {b:1}/4"),
                        ),
                        result = math("3/4"),
                        visual = Fraction(numerator = 2, denominator = 4, plus = 1 to 4),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_fractions_add_s6_question),
                        formula = math("1/6 + {b:1/3} = ?"),
                        options = mathOptions("2/9", "1/2", "2/6", "1/3"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_arithmetic_fractions_add_s6_explanation),
                        visual = Fraction(numerator = 1, denominator = 6, plus = 1 to 3, reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = words(Res.string.learn_shared_how_much_bar_shaded),
                options = mathOptions("2/5", "3/5", "2/3", "5/2"),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_arithmetic_fractions_q1_explanation),
                visual = Fraction(numerator = 2, denominator = 5, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_arithmetic_fractions_q2_prompt),
                options = mathOptions("3/5", "4/8", "2/5", "5/12"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_arithmetic_fractions_q2_explanation),
                visual = Fraction(numerator = 1, denominator = 2, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_shared_which_bar_shaded_more),
                options = listOf(
                    math("3/8"),
                    math("1/2"),
                    words(Res.string.learn_shared_they_equal),
                    words(Res.string.learn_shared_you_cannot_tell),
                ),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_arithmetic_fractions_q3_explanation),
                visual = Fraction(numerator = 3, denominator = 8, compare = 1 to 2, reveal = false),
            ),
            QuizQuestion(
                prompt = math("3/5 + {b:1/5} = ?"),
                options = mathOptions("4/10", "4/5", "3/10", "4/25"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_arithmetic_fractions_q4_explanation),
                visual = Fraction(numerator = 3, denominator = 5, plus = 1 to 5, reveal = false),
            ),
            QuizQuestion(
                prompt = math("5/6 - {b:2/6} = ?"),
                options = mathOptions("3/6", "3/12", "7/6", "1/6"),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_arithmetic_fractions_q5_explanation),
                visual = Fraction(numerator = 5, denominator = 6, compare = 2 to 6, reveal = false),
            ),
            QuizQuestion(
                prompt = math("1/2 + {b:1/4} = ?"),
                options = mathOptions("2/6", "3/4", "1/6", "2/4"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_arithmetic_fractions_q6_explanation),
                visual = Fraction(numerator = 1, denominator = 2, plus = 1 to 4, reveal = false),
            ),
        ),
    )

    private val decimals = learnUnit(
        topic = MathTopic.ARITHMETIC,
        urlSlug = "decimals",
        title = Res.string.learn_unit_arithmetic_decimals_title,
        summary = Res.string.learn_unit_arithmetic_decimals_summary,
        level = GradeLevel.GRADES_3_5,
        lessons = listOf(
            LessonSpec(
                id = "g35-arithmetic-decimals",
                title = Res.string.learn_g35_arithmetic_decimals_title,
                summary = Res.string.learn_g35_arithmetic_decimals_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_g35_arithmetic_decimals_s1_body),
                        formula = words(Res.string.learn_g35_arithmetic_decimals_s1_formula),
                        visual = DecimalGrid(value = 0.35),
                    ),
                    Concept(
                        body = words(Res.string.learn_g35_arithmetic_decimals_s2_body),
                        formula = math("1/4 = 0.25"),
                        visual = DecimalGrid(value = 0.25),
                    ),
                    Choice(
                        question = words(Res.string.learn_shared_write_shaded_amount_as_decimal),
                        options = mathOptions("0.075", "0.7", "0.75", "7.5"),
                        correctIndex = 2,
                        explanation = filled(Res.string.learn_t_cells_out_hundred_hundredths, "75", "75", "0.75"),
                        visual = DecimalGrid(value = 0.75, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_g35_arithmetic_decimals_s4_body),
                        formula = words(Res.string.learn_g35_arithmetic_decimals_s4_formula),
                        visual = DecimalGrid(value = 0.05),
                    ),
                    Choice(
                        question = words(Res.string.learn_g35_arithmetic_decimals_s5_question),
                        options = listOf(
                            filled(Res.string.learn_opt_euro_cents, "3", "50"),
                            filled(Res.string.learn_opt_euro_cents, "3", "5"),
                            filled(Res.string.learn_opt_euro_cents, "30", "5"),
                            filled(Res.string.learn_opt_euro_cents, "3", "500"),
                        ),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_g35_arithmetic_decimals_s5_explanation),
                        visual = DecimalGrid(value = 0.05, reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_g35_arithmetic_decimals_s6_question),
                        options = mathOptions("0.4", "0.04", "4", "0.44"),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_g35_arithmetic_decimals_s6_explanation),
                        visual = DecimalGrid(value = 0.4, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "arithmetic-decimals-compare",
                title = Res.string.learn_arithmetic_decimals_compare_title,
                summary = Res.string.learn_arithmetic_decimals_compare_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_arithmetic_decimals_compare_s1_body),
                        formula = math("0.4 > {b:0.35}"),
                        visual = DecimalGrid(value = 0.4, compare = 0.35),
                    ),
                    Choice(
                        question = words(Res.string.learn_shared_which_square_shaded_more),
                        options = listOf(
                            math("0.4"),
                            math("0.35"),
                            words(Res.string.learn_shared_they_equal),
                            words(Res.string.learn_shared_you_cannot_tell),
                        ),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_arithmetic_decimals_compare_s2_explanation),
                        visual = DecimalGrid(value = 0.4, compare = 0.35, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_arithmetic_decimals_compare_s3_body),
                        formula = math("0.4 = {b:0.40}"),
                        visual = DecimalGrid(value = 0.4, compare = 0.40, compareDecimals = 2),
                    ),
                    Concept(
                        body = words(Res.string.learn_arithmetic_decimals_compare_s4_body),
                        formula = words(Res.string.learn_arithmetic_decimals_compare_s4_formula),
                        visual = DecimalGrid(value = 0.5),
                    ),
                    Numeric(
                        question = words(Res.string.learn_arithmetic_decimals_compare_s5_question),
                        formula = words(Res.string.learn_arithmetic_decimals_compare_s5_formula),
                        answer = "30",
                        explanation = words(Res.string.learn_arithmetic_decimals_compare_s5_explanation),
                        visual = DecimalGrid(value = 0.3, reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_decimals_compare_s6_question),
                        options = mathOptions(
                            "0.09, 0.7, 0.71",
                            "0.7, 0.09, 0.71",
                            "0.71, 0.7, 0.09",
                            "0.09, 0.71, 0.7",
                        ),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_arithmetic_decimals_compare_s6_explanation),
                        visual = DecimalGrid(value = 0.09, compare = 0.7, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "arithmetic-decimals-add",
                title = Res.string.learn_arithmetic_decimals_add_title,
                summary = Res.string.learn_arithmetic_decimals_add_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_arithmetic_decimals_add_s1_body),
                        formula = math("0.4 + {b:0.35} = 0.75"),
                        visual = DecimalGrid(value = 0.4, plus = 0.35),
                    ),
                    Worked(
                        problem = math("{a:0.6} + {b:0.25} = ?"),
                        lines = listOf(
                            words(Res.string.learn_arithmetic_decimals_add_s2_l1),
                            words(Res.string.learn_arithmetic_decimals_add_s2_l2),
                            words(Res.string.learn_arithmetic_decimals_add_s2_l3),
                        ),
                        result = math("0.85"),
                        visual = DecimalGrid(value = 0.6, plus = 0.25),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_decimals_add_s3_question),
                        formula = math("0.3 + {b:0.45} = ?"),
                        options = mathOptions("0.48", "0.75", "0.78", "0.15"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_arithmetic_decimals_add_s3_explanation),
                        visual = DecimalGrid(value = 0.3, plus = 0.45, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_arithmetic_decimals_add_s4_body),
                        formula = math("0.75 - {b:0.4} = 0.35"),
                        visual = DecimalGrid(value = 0.35, plus = 0.4),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_decimals_add_s5_question),
                        formula = math("0.45 + {b:0.3} = ?"),
                        options = mathOptions("0.15", "0.48", "0.75", "7.5"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_arithmetic_decimals_add_s5_explanation),
                        visual = DecimalGrid(value = 0.45, plus = 0.3, reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_decimals_add_s6_question),
                        formula = math("0.35 + ? = 1"),
                        options = mathOptions("0.65", "0.75", "0.6", "0.7"),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_arithmetic_decimals_add_s6_explanation),
                        visual = DecimalGrid(value = 0.35, reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = words(Res.string.learn_shared_write_shaded_amount_as_decimal),
                options = mathOptions("0.25", "0.4", "0.75", "0.025"),
                correctIndex = 0,
                explanation = filled(Res.string.learn_t_cells_out_hundred_hundredths, "25", "25", "0.25"),
                visual = DecimalGrid(value = 0.25, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_shared_which_square_shaded_more),
                options = listOf(
                    math("0.5"),
                    math("0.45"),
                    words(Res.string.learn_unit_arithmetic_decimals_q2_o3),
                    words(Res.string.learn_shared_you_cannot_tell),
                ),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_arithmetic_decimals_q2_explanation),
                visual = DecimalGrid(value = 0.5, compare = 0.45, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_shared_which_these_smallest),
                options = mathOptions("0.08", "0.6", "0.61", "0.7"),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_arithmetic_decimals_q3_explanation),
                visual = DecimalGrid(value = 0.08, compare = 0.6, reveal = false),
            ),
            QuizQuestion(
                prompt = math("0.4 + {b:0.25} = ?"),
                options = mathOptions("0.29", "0.65", "0.6", "0.425"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_arithmetic_decimals_q4_explanation),
                visual = DecimalGrid(value = 0.4, plus = 0.25, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_arithmetic_decimals_q5_prompt),
                options = mathOptions("1/2", "1/4", "2/5", "1/5"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_arithmetic_decimals_q5_explanation),
                visual = DecimalGrid(value = 0.25, reveal = false),
            ),
            QuizQuestion(
                prompt = math("0.7 - {b:0.35} = ?"),
                options = mathOptions("0.35", "0.42", "0.45", "0.28"),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_arithmetic_decimals_q6_explanation),
                visual = DecimalGrid(value = 0.7, reveal = false),
            ),
        ),
    )

    private val negatives = learnUnit(
        topic = MathTopic.ARITHMETIC,
        urlSlug = "negatives",
        title = Res.string.learn_unit_arithmetic_negatives_title,
        summary = Res.string.learn_unit_arithmetic_negatives_summary,
        level = GradeLevel.GRADES_6_8,
        lessons = listOf(
            LessonSpec(
                id = "g68-arithmetic-negatives",
                title = Res.string.learn_g68_arithmetic_negatives_title,
                summary = Res.string.learn_g68_arithmetic_negatives_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_g68_arithmetic_negatives_s1_body),
                        visual = NumberLine(from = -6, to = 6, start = 2, jump = 3, thenJump = -7),
                    ),
                    Concept(
                        body = words(Res.string.learn_g68_arithmetic_negatives_s2_body),
                        formula = words(Res.string.learn_g68_arithmetic_negatives_s2_formula),
                        visual = NumberLine(from = -6, to = 6, start = 0, jump = -4),
                    ),
                    Choice(
                        question = words(Res.string.learn_shared_which_these_largest),
                        options = mathOptions("-10", "-6", "-1", "0"),
                        correctIndex = 3,
                        explanation = words(Res.string.learn_g68_arithmetic_negatives_s3_explanation),
                        visual = NumberLine(from = -10, to = 0, compare = listOf(-10, -6, -1, 0), reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_g68_arithmetic_negatives_s4_body),
                        formula = math("-10 < -1"),
                        visual = NumberLine(from = -12, to = 2, start = -10, jump = 9),
                    ),
                    Choice(
                        question = words(Res.string.learn_g68_arithmetic_negatives_s5_question),
                        options = wordOptions(
                            Res.string.learn_g68_arithmetic_negatives_s5_o1,
                            Res.string.learn_g68_arithmetic_negatives_s5_o2,
                            Res.string.learn_g68_arithmetic_negatives_s5_o3,
                            Res.string.learn_shared_you_cannot_tell_2,
                        ),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_g68_arithmetic_negatives_s5_explanation),
                        visual = NumberLine(from = -20, to = 2, start = -18, tickStep = 2, reveal = false),
                    ),
                    Numeric(
                        question = words(Res.string.learn_g68_arithmetic_negatives_s6_question),
                        formula = math("-2 - {b:(-18)} = ?"),
                        answer = "16",
                        explanation = words(Res.string.learn_g68_arithmetic_negatives_s6_explanation),
                        visual = NumberLine(from = -20, to = 2, start = -18, tickStep = 2, hopSteps = listOf(16), reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "arithmetic-negatives-addsub",
                title = Res.string.learn_arithmetic_negatives_addsub_title,
                summary = Res.string.learn_arithmetic_negatives_addsub_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_arithmetic_negatives_addsub_s1_body),
                        visual = NumberLine(from = -6, to = 6, start = -4, jump = 3),
                    ),
                    Numeric(
                        question = words(Res.string.learn_arithmetic_negatives_addsub_s2_question),
                        formula = math("{a:-7} + {b:12} = ?"),
                        answer = "5",
                        explanation = words(Res.string.learn_arithmetic_negatives_addsub_s2_explanation),
                        visual = NumberLine(from = -10, to = 5, start = -7, hopSteps = listOf(7, 5), reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_negatives_addsub_s3_question),
                        formula = math("{a:-4} - {b:6} = ?"),
                        options = mathOptions("2", "-2", "-10", "10"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_arithmetic_negatives_addsub_s3_explanation),
                        visual = NumberLine(from = -10, to = 5, start = -4, hopSteps = listOf(-6), reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_arithmetic_negatives_addsub_s4_body),
                        formula = math("{a:5} - ({b:-3}) = 8"),
                        visual = NumberLine(from = -4, to = 10, start = 5, jump = 3),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_negatives_addsub_s5_question),
                        formula = math("{a:-2} - ({b:-9}) = ?"),
                        options = mathOptions("-11", "-7", "7", "11"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_arithmetic_negatives_addsub_s5_explanation),
                        visual = NumberLine(from = -4, to = 10, start = -2, hopSteps = listOf(9), reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_arithmetic_negatives_addsub_s6_body),
                        formula = math("6 + {b:(-4)} = 6 - {b:4}"),
                        visual = NumberLine(from = -2, to = 8, start = 6, jump = -4),
                    ),
                ),
            ),
            LessonSpec(
                id = "arithmetic-negatives-multiply",
                title = Res.string.learn_arithmetic_negatives_multiply_title,
                summary = Res.string.learn_arithmetic_negatives_multiply_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_arithmetic_negatives_multiply_s1_body),
                        formula = math("3 x {b:(-5)} = -15"),
                        visual = Steps(terms = listOf(-15, -10, -5, 0)),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_negatives_multiply_s2_question),
                        formula = math("3 x {b:(-6)} = ?"),
                        options = mathOptions("-18", "-9", "9", "18"),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_arithmetic_negatives_multiply_s2_explanation),
                        visual = Steps(terms = listOf(-18, -12, -6, 0)),
                    ),
                    Concept(
                        body = words(Res.string.learn_arithmetic_negatives_multiply_s3_body),
                        formula = math("{a:-3} x (-4) = {b:12}"),
                        visual = Steps(terms = listOf(-12, -8, -4, 0, 4, 8, 12)),
                    ),
                    Concept(
                        body = words(Res.string.learn_arithmetic_negatives_multiply_s4_body),
                        formula = math("(-4) x {b:(-5)} = 20"),
                        visual = Steps(terms = listOf(-10, -5, 0, 5, 10, 15, 20)),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_negatives_multiply_s5_question),
                        formula = math("(-30) / {b:(-6)} = ?"),
                        options = mathOptions("-5", "-36", "5", "36"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_arithmetic_negatives_multiply_s5_explanation),
                        visual = Steps(terms = listOf(-30, -24, -18, -12, -6, 0)),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_negatives_multiply_s6_question),
                        formula = math("{a:-45} / {b:9} = ?"),
                        options = mathOptions("-6", "-5", "5", "6"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_arithmetic_negatives_multiply_s6_explanation),
                        visual = Steps(terms = listOf(-45, -36, -27, -18, -9, 0)),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = math("-6 + {b:9} = ?"),
                options = mathOptions("-15", "-3", "3", "15"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_arithmetic_negatives_q1_explanation),
                visual = NumberLine(from = -10, to = 5, start = -6, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_shared_which_these_smallest),
                options = mathOptions("-9", "-2", "0", "3"),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_arithmetic_negatives_q2_explanation),
                visual = NumberLine(from = -10, to = 5, compare = listOf(-9, -2, 0, 3), reveal = false),
            ),
            QuizQuestion(
                prompt = math("-3 - {b:8} = ?"),
                options = mathOptions("-11", "-5", "5", "11"),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_arithmetic_negatives_q3_explanation),
                visual = NumberLine(from = -12, to = 2, start = -3, reveal = false),
            ),
            QuizQuestion(
                prompt = math("7 - {b:(-5)} = ?"),
                options = mathOptions("-12", "-2", "2", "12"),
                correctIndex = 3,
                explanation = words(Res.string.learn_unit_arithmetic_negatives_q4_explanation),
                visual = NumberLine(from = -2, to = 14, start = 7, reveal = false),
            ),
            QuizQuestion(
                prompt = math("(-4) x (-5) = ?"),
                options = mathOptions("-20", "-9", "9", "20"),
                correctIndex = 3,
                explanation = words(Res.string.learn_unit_arithmetic_negatives_q5_explanation),
                visual = Steps(terms = listOf(-10, -5, 0, 5)),
            ),
            QuizQuestion(
                prompt = math("(-36) / 9 = ?"),
                options = mathOptions("-4", "4", "-27", "27"),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_arithmetic_negatives_q6_explanation),
                visual = Steps(terms = listOf(-36, -27, -18, -9, 0)),
            ),
        ),
    )

    private val ratio = learnUnit(
        topic = MathTopic.ARITHMETIC,
        urlSlug = "ratio",
        title = Res.string.learn_unit_arithmetic_ratio_title,
        summary = Res.string.learn_unit_arithmetic_ratio_summary,
        level = GradeLevel.GRADES_6_8,
        lessons = listOf(
            LessonSpec(
                id = "g68-arithmetic-ratio",
                title = Res.string.learn_g68_arithmetic_ratio_title,
                summary = Res.string.learn_g68_arithmetic_ratio_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_g68_arithmetic_ratio_s1_body),
                        formula = math("{a:1} : {b:4}"),
                        visual = RatioBar(parts = listOf(1, 4)),
                    ),
                    Concept(
                        body = words(Res.string.learn_g68_arithmetic_ratio_s2_body),
                        formula = math("{a:3} : {b:5} = {a:12} : {b:20}"),
                        visual = RatioBar(parts = listOf(3, 5), scale = 4),
                    ),
                    Choice(
                        question = words(Res.string.learn_shared_which_ratio_matches_bar),
                        options = mathOptions("2 : 3", "3 : 2", "6 : 3", "1 : 2"),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_g68_arithmetic_ratio_s3_explanation),
                        visual = RatioBar(parts = listOf(6, 9), reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_g68_arithmetic_ratio_s4_body),
                        visual = RatioBar(parts = listOf(2, 3)),
                    ),
                    Choice(
                        question = words(Res.string.learn_g68_arithmetic_ratio_s5_question),
                        options = mathOptions("1/3", "1/4", "3/4", "1/2"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_g68_arithmetic_ratio_s5_explanation),
                        visual = RatioBar(parts = listOf(1, 3), reveal = false),
                    ),
                    Numeric(
                        question = words(Res.string.learn_g68_arithmetic_ratio_s6_question),
                        formula = math("{a:2} + {b:5} = ?"),
                        answer = "7",
                        explanation = words(Res.string.learn_g68_arithmetic_ratio_s6_explanation),
                        visual = RatioBar(parts = listOf(2, 5), reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "arithmetic-ratio-sharing",
                title = Res.string.learn_arithmetic_ratio_sharing_title,
                summary = Res.string.learn_arithmetic_ratio_sharing_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_arithmetic_ratio_sharing_s1_body),
                        formula = words(Res.string.learn_arithmetic_ratio_sharing_s1_formula),
                        visual = RatioBar(parts = listOf(2, 3), total = 60),
                    ),
                    Worked(
                        problem = words(Res.string.learn_arithmetic_ratio_sharing_s2_problem),
                        lines = listOf(
                            words(Res.string.learn_arithmetic_ratio_sharing_s2_l1),
                            words(Res.string.learn_arithmetic_ratio_sharing_s2_l2),
                            words(Res.string.learn_arithmetic_ratio_sharing_s2_l3),
                        ),
                        result = words(Res.string.learn_arithmetic_ratio_sharing_s2_result),
                        visual = RatioBar(parts = listOf(2, 3), total = 60),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_ratio_sharing_s3_question),
                        options = listOf(
                            counted(Res.plurals.learn_opt_euro, 20),
                            counted(Res.plurals.learn_opt_euro, 40),
                            counted(Res.plurals.learn_opt_euro, 60),
                            counted(Res.plurals.learn_opt_euro, 75),
                        ),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_arithmetic_ratio_sharing_s3_explanation),
                        visual = RatioBar(parts = listOf(3, 1), total = 80, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_arithmetic_ratio_sharing_s4_body),
                        visual = RatioBar(parts = listOf(3, 2, 1), total = 180),
                    ),
                    Numeric(
                        question = words(Res.string.learn_arithmetic_ratio_sharing_s5_question),
                        formula = math("180 / 6 x {a:3} = ?"),
                        answer = "90",
                        explanation = words(Res.string.learn_arithmetic_ratio_sharing_s5_explanation),
                        visual = RatioBar(parts = listOf(3, 2, 1), total = 180, reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_ratio_sharing_s6_question),
                        options = listOf(
                            counted(Res.plurals.learn_opt_litres, 8),
                            counted(Res.plurals.learn_opt_litres, 15),
                            counted(Res.plurals.learn_opt_litres, 18),
                            counted(Res.plurals.learn_opt_litres, 20),
                        ),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_arithmetic_ratio_sharing_s6_explanation),
                        visual = RatioBar(parts = listOf(2, 3), reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "arithmetic-ratio-scaling",
                title = Res.string.learn_arithmetic_ratio_scaling_title,
                summary = Res.string.learn_arithmetic_ratio_scaling_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_arithmetic_ratio_scaling_s1_body),
                        formula = words(Res.string.learn_arithmetic_ratio_scaling_s1_formula),
                        visual = Steps(terms = listOf(75, 150, 225, 300)),
                    ),
                    Concept(
                        body = words(Res.string.learn_arithmetic_ratio_scaling_s2_body),
                        formula = words(Res.string.learn_arithmetic_ratio_scaling_s2_formula),
                        visual = Steps(terms = listOf(75, 150, 225, 300)),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_ratio_scaling_s3_question),
                        formula = words(Res.string.learn_arithmetic_ratio_scaling_s3_formula),
                        options = mathOptions("350 g", "400 g", "450 g", "600 g"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_arithmetic_ratio_scaling_s3_explanation),
                        visual = Steps(terms = listOf(75, 150, 225, 300)),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_ratio_scaling_s4_question),
                        options = listOf(
                            counted(Res.plurals.learn_opt_euro, 8),
                            counted(Res.plurals.learn_opt_euro, 9),
                            counted(Res.plurals.learn_opt_euro, 10),
                            counted(Res.plurals.learn_opt_euro, 12),
                        ),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_arithmetic_ratio_scaling_s4_explanation),
                        visual = Steps(terms = listOf(3, 6, 9)),
                    ),
                    Numeric(
                        question = words(Res.string.learn_arithmetic_ratio_scaling_s5_question),
                        formula = math("{a:4} cm x {b:50000} = ? cm"),
                        answer = "200000",
                        explanation = words(Res.string.learn_arithmetic_ratio_scaling_s5_explanation),
                        visual = Steps(terms = listOf(1, 50000), multiply = true),
                    ),
                    Concept(
                        body = words(Res.string.learn_arithmetic_ratio_scaling_s6_body),
                        formula = words(Res.string.learn_arithmetic_ratio_scaling_s6_formula),
                        visual = Steps(terms = listOf(12, 6, 4, 3)),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = words(Res.string.learn_shared_which_ratio_matches_bar),
                options = mathOptions("1 : 2", "3 : 5", "5 : 3", "15 : 5"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_arithmetic_ratio_q1_explanation),
                visual = RatioBar(parts = listOf(3, 5), reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_arithmetic_ratio_q2_prompt),
                options = mathOptions("2/7", "2/9", "7/9", "1/2"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_arithmetic_ratio_q2_explanation),
                visual = RatioBar(parts = listOf(2, 7), reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_arithmetic_ratio_q3_prompt),
                options = listOf(
                    counted(Res.plurals.learn_opt_euro, 20),
                    counted(Res.plurals.learn_opt_euro, 25),
                    counted(Res.plurals.learn_opt_euro, 40),
                    counted(Res.plurals.learn_opt_euro, 80),
                ),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_arithmetic_ratio_q3_explanation),
                visual = RatioBar(parts = listOf(1, 4), total = 100, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_arithmetic_ratio_q4_prompt),
                options = mathOptions("3 : 5", "5 : 3", "1 : 2", "15 : 5"),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_arithmetic_ratio_q4_explanation),
                visual = RatioBar(parts = listOf(3, 5), reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_arithmetic_ratio_q5_prompt),
                options = mathOptions("300 g", "320 g", "360 g", "400 g"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_arithmetic_ratio_q5_explanation),
                visual = Steps(terms = listOf(40, 80, 120, 160, 200, 240)),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_arithmetic_ratio_q6_prompt),
                options = mathOptions("10 cm", "15 cm", "20 cm", "150 cm"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_arithmetic_ratio_q6_explanation),
                visual = Steps(terms = listOf(1, 200), multiply = true),
            ),
        ),
    )

    private val percent = learnUnit(
        topic = MathTopic.ARITHMETIC,
        urlSlug = "percent",
        title = Res.string.learn_unit_arithmetic_percent_title,
        summary = Res.string.learn_unit_arithmetic_percent_summary,
        level = GradeLevel.GRADES_6_8,
        lessons = listOf(
            LessonSpec(
                id = "g68-arithmetic-percent",
                title = Res.string.learn_g68_arithmetic_percent_title,
                summary = Res.string.learn_g68_arithmetic_percent_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_g68_arithmetic_percent_s1_body),
                        formula = math("35% = 0.35"),
                        visual = DecimalGrid(value = 0.35, percent = true),
                    ),
                    Concept(
                        body = words(Res.string.learn_g68_arithmetic_percent_s2_body),
                        formula = math("25% = 0.25 = 1/4"),
                        visual = DecimalGrid(value = 0.25, percent = true),
                    ),
                    Concept(
                        body = words(Res.string.learn_g68_arithmetic_percent_s3_body),
                        formula = words(Res.string.learn_g68_arithmetic_percent_s3_formula),
                        visual = DecimalGrid(value = 0.2, of = 80),
                    ),
                    Choice(
                        question = words(Res.string.learn_g68_arithmetic_percent_s4_question),
                        formula = words(Res.string.learn_g68_arithmetic_percent_s4_formula),
                        options = mathOptions("16", "32", "3.2", "128"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_g68_arithmetic_percent_s4_explanation),
                        visual = DecimalGrid(value = 0.5, of = 64, reveal = false),
                    ),
                    Numeric(
                        question = words(Res.string.learn_g68_arithmetic_percent_s5_question),
                        formula = words(Res.string.learn_g68_arithmetic_percent_s5_formula),
                        answer = "30",
                        explanation = words(Res.string.learn_g68_arithmetic_percent_s5_explanation),
                        visual = DecimalGrid(value = 0.15, of = 200, reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_shared_which_these_largest),
                        options = mathOptions("0.4", "35%", "1/3", "30%"),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_g68_arithmetic_percent_s6_explanation),
                        visual = DecimalGrid(value = 0.4, percent = true, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "arithmetic-percent-change",
                title = Res.string.learn_arithmetic_percent_change_title,
                summary = Res.string.learn_arithmetic_percent_change_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_arithmetic_percent_change_s1_body),
                        formula = math("{a:100} + {b:20}% = 120"),
                        visual = DecimalGrid(value = 0.2, of = 100),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_percent_change_s2_question),
                        options = listOf(
                            counted(Res.plurals.learn_opt_euro, 10),
                            counted(Res.plurals.learn_opt_euro, 15),
                            counted(Res.plurals.learn_opt_euro, 30),
                            counted(Res.plurals.learn_opt_euro, 35),
                        ),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_arithmetic_percent_change_s2_explanation),
                        visual = DecimalGrid(value = 0.25, of = 40, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_arithmetic_percent_change_s3_body),
                        formula = math("x 0.8"),
                        visual = DecimalGrid(value = 0.8, of = 40),
                    ),
                    Numeric(
                        question = words(Res.string.learn_arithmetic_percent_change_s4_question),
                        formula = math("{a:60} x {b:1.15} = ?"),
                        answer = "69",
                        explanation = words(Res.string.learn_arithmetic_percent_change_s4_explanation),
                        visual = DecimalGrid(value = 0.15, of = 60, reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_percent_change_s5_question),
                        options = wordOptions(
                            Res.string.learn_arithmetic_percent_change_s5_o1,
                            Res.string.learn_arithmetic_percent_change_s5_o2,
                            Res.string.learn_arithmetic_percent_change_s5_o3,
                            Res.string.learn_arithmetic_percent_change_s5_o4,
                        ),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_arithmetic_percent_change_s5_explanation),
                        visual = DecimalGrid(value = 0.1, of = 110, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_arithmetic_percent_change_s6_body),
                        formula = math("110 x {b:0.9} = 99"),
                        visual = DecimalGrid(value = 0.1, of = 110),
                    ),
                ),
            ),
            LessonSpec(
                id = "arithmetic-percent-finding",
                title = Res.string.learn_arithmetic_percent_finding_title,
                summary = Res.string.learn_arithmetic_percent_finding_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_arithmetic_percent_finding_s1_body),
                        formula = math("{b:15} / {a:50} x 100 = 30"),
                        visual = BarChart(values = listOf(50, 65), labels = listOf(BarLabel.BEFORE, BarLabel.AFTER), gridStep = 10),
                    ),
                    Worked(
                        problem = words(Res.string.learn_arithmetic_percent_finding_s2_problem),
                        lines = listOf(
                            words(Res.string.learn_arithmetic_percent_finding_s2_l1),
                            words(Res.string.learn_arithmetic_percent_finding_s2_l2),
                            math("{b:15} / {a:50} = 0.3"),
                            math("x 100"),
                        ),
                        result = words(Res.string.learn_arithmetic_percent_finding_s2_result),
                        visual = BarChart(values = listOf(50, 65), labels = listOf(BarLabel.BEFORE, BarLabel.AFTER), gridStep = 10),
                    ),
                    Concept(
                        body = words(Res.string.learn_arithmetic_percent_finding_s3_body),
                        visual = BarChart(values = listOf(80, 60), labels = listOf(BarLabel.BEFORE, BarLabel.AFTER), gridStep = 20),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_percent_finding_s4_question),
                        options = mathOptions("20%", "25%", "30%", "33%"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_arithmetic_percent_finding_s4_explanation),
                        visual = BarChart(values = listOf(80, 60), labels = listOf(BarLabel.BEFORE, BarLabel.AFTER), gridStep = 20, reveal = false),
                    ),
                    Numeric(
                        question = words(Res.string.learn_arithmetic_percent_finding_s5_question),
                        formula = math("{a:18} / {b:24} x 100 = ?"),
                        answer = "75",
                        explanation = words(Res.string.learn_arithmetic_percent_finding_s5_explanation),
                        visual = BarChart(values = listOf(18, 24), labels = listOf(BarLabel.SCORE, BarLabel.TOTAL), gridStep = 6, reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_percent_finding_s6_question),
                        options = mathOptions("20%", "25%", "50%", "80%"),
                        correctIndex = 1,
                        explanation = filled(Res.string.learn_t_change_against_original_quarter, "50", "50", "200"),
                        visual = BarChart(values = listOf(200, 250), labels = listOf(BarLabel.BEFORE, BarLabel.AFTER), gridStep = 50, reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = filled(Res.string.learn_t_what_is_of, "12", "150"),
                options = mathOptions("12", "15", "18", "24"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_arithmetic_percent_q1_explanation),
                visual = DecimalGrid(value = 0.12, of = 150, reveal = false),
            ),
            QuizQuestion(
                prompt = filled(Res.string.learn_t_what_is_of, "75", "40"),
                options = mathOptions("25", "30", "35", "3"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_arithmetic_percent_q2_explanation),
                visual = DecimalGrid(value = 0.75, of = 40, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_arithmetic_percent_q3_prompt),
                options = mathOptions("4.5", "0.45", "0.045", "45"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_arithmetic_percent_q3_explanation),
                visual = DecimalGrid(value = 0.45, percent = true, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_arithmetic_percent_q4_prompt),
                options = listOf(
                    counted(Res.plurals.learn_opt_euro, 27),
                    counted(Res.plurals.learn_opt_euro, 60),
                    counted(Res.plurals.learn_opt_euro, 63),
                    counted(Res.plurals.learn_opt_euro, 70),
                ),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_arithmetic_percent_q4_explanation),
                visual = DecimalGrid(value = 0.3, of = 90, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_arithmetic_percent_q5_prompt),
                options = mathOptions("10%", "20%", "25%", "50%"),
                correctIndex = 2,
                explanation = filled(Res.string.learn_t_change_against_original_quarter, "10", "10", "40"),
                visual = BarChart(values = listOf(40, 50), labels = listOf(BarLabel.BEFORE, BarLabel.AFTER), gridStep = 10, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_arithmetic_percent_q6_prompt),
                options = listOf(
                    counted(Res.plurals.learn_opt_euro, 96),
                    counted(Res.plurals.learn_opt_euro, 100),
                    counted(Res.plurals.learn_opt_euro, 104),
                    counted(Res.plurals.learn_opt_euro, 80),
                ),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_arithmetic_percent_q6_explanation),
                visual = DecimalGrid(value = 0.2, of = 120, reveal = false),
            ),
        ),
    )

    private val standardForm = learnUnit(
        topic = MathTopic.ARITHMETIC,
        urlSlug = "standard-form",
        title = Res.string.learn_unit_arithmetic_standard_form_title,
        summary = Res.string.learn_unit_arithmetic_standard_form_summary,
        level = GradeLevel.GRADES_9_10,
        lessons = listOf(
            LessonSpec(
                id = "arithmetic-standard-form",
                title = Res.string.learn_arithmetic_standard_form_title,
                summary = Res.string.learn_arithmetic_standard_form_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_arithmetic_standard_form_s1_body),
                        formula = math("8 000 = 8 * 10^3"),
                        visual = Steps(terms = listOf(8, 80, 800, 8000), multiply = true),
                    ),
                    Concept(
                        body = words(Res.string.learn_arithmetic_standard_form_s2_body),
                        formula = math("52 000 = 5.2 * 10^4"),
                        visual = Steps(terms = listOf(52, 520, 5200, 52000), multiply = true),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_standard_form_s3_question),
                        formula = math("720 000 = ?"),
                        options = mathOptions("7.2 * 10^5", "72 * 10^4", "7.2 * 10^6", "0.72 * 10^6"),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_arithmetic_standard_form_s3_explanation),
                        visual = Steps(terms = listOf(72, 720, 7200), multiply = true),
                    ),
                    Concept(
                        body = words(Res.string.learn_arithmetic_standard_form_s4_body),
                        formula = math("3 * 10^8 > 9 * 10^7"),
                        visual = Steps(terms = listOf(3, 30, 300, 3000), multiply = true),
                    ),
                    Numeric(
                        question = words(Res.string.learn_arithmetic_standard_form_s5_question),
                        formula = math("4.7 * 10^3 = ?"),
                        answer = "4700",
                        explanation = words(Res.string.learn_arithmetic_standard_form_s5_explanation),
                        visual = Steps(terms = listOf(1, 10, 100, 1000), multiply = true),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_standard_form_s6_question),
                        options = mathOptions("12 * 10^3", "1.2 * 10^4", "0.12 * 10^5", "1.2 * 10^3"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_arithmetic_standard_form_s6_explanation),
                        visual = Steps(terms = listOf(12, 120, 1200, 12000), multiply = true),
                    ),
                ),
            ),
            LessonSpec(
                id = "arithmetic-standard-form-small",
                title = Res.string.learn_arithmetic_standard_form_small_title,
                summary = Res.string.learn_arithmetic_standard_form_small_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_arithmetic_standard_form_small_s1_body),
                        formula = math("0.006 = 6 * 10^-3"),
                        visual = Steps(terms = listOf(6000, 600, 60, 6), multiply = true),
                    ),
                    Concept(
                        body = words(Res.string.learn_arithmetic_standard_form_small_s2_body),
                        formula = math("3 * 10^-4 = 0.0003"),
                        visual = Steps(terms = listOf(3000, 300, 30, 3), multiply = true),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_standard_form_small_s3_question),
                        formula = math("0.00082 = ?"),
                        options = mathOptions("8.2 * 10^-4", "8.2 * 10^-3", "82 * 10^-5", "8.2 * 10^4"),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_arithmetic_standard_form_small_s3_explanation),
                        visual = Steps(terms = listOf(8200, 820, 82, 8.2), multiply = true),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_standard_form_small_s4_question),
                        formula = math("5.6 * 10^-4 = ?"),
                        options = mathOptions("0.000056", "0.00056", "0.0056", "56000"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_arithmetic_standard_form_small_s4_explanation),
                        visual = Steps(terms = listOf(1, 10, 100, 1000), multiply = true),
                    ),
                    Concept(
                        body = words(Res.string.learn_arithmetic_standard_form_small_s5_body),
                        formula = math("-4 * 10^3 = -4000"),
                        visual = Steps(terms = listOf(4, 40, 400, 4000), multiply = true),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_standard_form_small_s6_question),
                        options = mathOptions("2 * 10^-3", "9 * 10^-4", "5 * 10^-3", "1 * 10^-2"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_arithmetic_standard_form_small_s6_explanation),
                        visual = Steps(terms = listOf(9000, 900, 90, 9), multiply = true),
                    ),
                ),
            ),
            LessonSpec(
                id = "arithmetic-standard-form-calculating",
                title = Res.string.learn_arithmetic_standard_form_calculating_title,
                summary = Res.string.learn_arithmetic_standard_form_calculating_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_arithmetic_standard_form_calculating_s1_body),
                        formula = math("10^4 x 10^3 = 10^7"),
                        visual = Steps(terms = listOf(10, 100, 1000, 10000), multiply = true),
                    ),
                    Worked(
                        problem = math("(3 * 10^4) * (2 * 10^3) = ?"),
                        lines = listOf(
                            words(Res.string.learn_arithmetic_standard_form_calculating_s2_l1),
                            words(Res.string.learn_arithmetic_standard_form_calculating_s2_l2),
                            words(Res.string.learn_arithmetic_standard_form_calculating_s2_l3),
                            words(Res.string.learn_arithmetic_standard_form_calculating_s2_l4),
                        ),
                        result = math("6 * 10^7"),
                        visual = Steps(terms = listOf(3, 30, 300, 3000), multiply = true),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_standard_form_calculating_s3_question),
                        formula = math("(4 * 10^6) / (2 * 10^2) = ?"),
                        options = mathOptions("2 * 10^4", "2 * 10^3", "8 * 10^4", "2 * 10^8"),
                        correctIndex = 0,
                        explanation = filled(Res.string.learn_t_front_numbers_give_powers, "4", "2", "2", "6", "2", "4"),
                        visual = Steps(terms = listOf(4, 40, 400, 4000), multiply = true),
                    ),
                    Concept(
                        body = words(Res.string.learn_arithmetic_standard_form_calculating_s4_body),
                        formula = math("60 * 10^5 = 6 * 10^6"),
                        visual = Steps(terms = listOf(6, 60, 600, 6000), multiply = true),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_standard_form_calculating_s5_question),
                        formula = math("(5 * 10^3) x (4 * 10^2) = ?"),
                        options = mathOptions("20 * 10^5", "2 * 10^6", "2 * 10^5", "9 * 10^5"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_arithmetic_standard_form_calculating_s5_explanation),
                        visual = Steps(terms = listOf(5, 50, 500, 5000), multiply = true),
                    ),
                    Numeric(
                        question = words(Res.string.learn_arithmetic_standard_form_calculating_s6_question),
                        formula = math("10^7 / 10^4 = ?"),
                        answer = "1000",
                        explanation = words(Res.string.learn_arithmetic_standard_form_calculating_s6_explanation),
                        visual = Steps(terms = listOf(1, 10, 100, 1000), multiply = true),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = words(Res.string.learn_unit_arithmetic_standard_form_q1_prompt),
                options = mathOptions("4.5 * 10^4", "45 * 10^3", "4.5 * 10^5", "0.45 * 10^5"),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_arithmetic_standard_form_q1_explanation),
                visual = Steps(terms = listOf(45, 450, 4500), multiply = true),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_arithmetic_standard_form_q2_prompt),
                options = mathOptions("3.2 * 10^5", "1.0 * 10^-3", "12 * 10^4", "9.9 * 10^2"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_arithmetic_standard_form_q2_explanation),
                visual = Steps(terms = listOf(12, 120, 1200), multiply = true),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_arithmetic_standard_form_q3_prompt),
                options = mathOptions("4 * 10^-4", "4 * 10^-3", "4 * 10^4", "0.4 * 10^-3"),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_arithmetic_standard_form_q3_explanation),
                visual = Steps(terms = listOf(4000, 400, 40, 4), multiply = true),
            ),
            QuizQuestion(
                prompt = math("(2 x 10^5) x (4 x 10^2) = ?"),
                options = mathOptions("8 * 10^7", "8 * 10^10", "6 * 10^7", "8 * 10^3"),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_arithmetic_standard_form_q4_explanation),
                visual = Steps(terms = listOf(2, 20, 200), multiply = true),
            ),
            QuizQuestion(
                prompt = math("(9 * 10^8) / (3 * 10^5) = ?"),
                options = mathOptions("3 * 10^3", "3 * 10^13", "6 * 10^3", "3 * 10^4"),
                correctIndex = 0,
                explanation = filled(Res.string.learn_t_front_numbers_give_powers, "9", "3", "3", "8", "5", "3"),
                visual = Steps(terms = listOf(9, 90, 900), multiply = true),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_arithmetic_standard_form_q6_prompt),
                options = listOf(
                    math("7 * 10^5"),
                    math("2 * 10^6"),
                    words(Res.string.learn_unit_arithmetic_standard_form_q6_o3),
                    words(Res.string.learn_shared_you_cannot_tell_2),
                ),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_arithmetic_standard_form_q6_explanation),
                visual = Steps(terms = listOf(2, 20, 200, 2000), multiply = true),
            ),
        ),
    )

    private val surds = learnUnit(
        topic = MathTopic.ARITHMETIC,
        urlSlug = "surds",
        title = Res.string.learn_unit_arithmetic_surds_title,
        summary = Res.string.learn_unit_arithmetic_surds_summary,
        level = GradeLevel.GRADES_9_10,
        lessons = listOf(
            LessonSpec(
                id = "arithmetic-surds",
                title = Res.string.learn_arithmetic_surds_title,
                summary = Res.string.learn_arithmetic_surds_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_arithmetic_surds_s1_body),
                        formula = math("√36 = 6"),
                        visual = AreaGrid(cols = 6, rows = 6, showArea = false, unit = ""),
                    ),
                    Concept(
                        body = words(Res.string.learn_arithmetic_surds_s2_body),
                        formula = math("5² + 5² = 50"),
                        visual = RightTriangle(a = 5, b = 5),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_surds_s3_question),
                        options = mathOptions("√49", "√64", "√70", "√81"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_arithmetic_surds_s3_explanation),
                    ),
                    Concept(
                        body = words(Res.string.learn_arithmetic_surds_s4_body),
                        formula = words(Res.string.learn_arithmetic_surds_s4_formula),
                    ),
                    Choice(
                        question = filled(Res.string.learn_t_between_whole_numbers_sit, "30"),
                        options = listOf(
                            filled(Res.string.learn_opt_between, "3", "4"),
                            filled(Res.string.learn_opt_between, "4", "5"),
                            filled(Res.string.learn_opt_between, "5", "6"),
                            filled(Res.string.learn_opt_between, "6", "7"),
                        ),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_arithmetic_surds_s5_explanation),
                        visual = AreaGrid(cols = 5, rows = 5, showArea = false, unit = ""),
                    ),
                    Numeric(
                        question = math("√7 x √7 = ?"),
                        answer = "7",
                        explanation = words(Res.string.learn_arithmetic_surds_s6_explanation),
                    ),
                ),
            ),
            LessonSpec(
                id = "arithmetic-surds-simplify",
                title = Res.string.learn_arithmetic_surds_simplify_title,
                summary = Res.string.learn_arithmetic_surds_simplify_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_arithmetic_surds_simplify_s1_body),
                        formula = words(Res.string.learn_arithmetic_surds_simplify_s1_formula),
                    ),
                    Worked(
                        problem = math("√50 = ?"),
                        lines = listOf(
                            words(Res.string.learn_arithmetic_surds_simplify_s2_l1),
                            words(Res.string.learn_arithmetic_surds_simplify_s2_l2),
                            words(Res.string.learn_arithmetic_surds_simplify_s2_l3),
                            words(Res.string.learn_arithmetic_surds_simplify_s2_l4),
                        ),
                        result = math("5√2"),
                        visual = AreaGrid(cols = 5, rows = 5, showArea = false, unit = ""),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_surds_simplify_s3_question),
                        formula = math("√18 = ?"),
                        options = mathOptions("3√2", "2√3", "9√2", "6√3"),
                        correctIndex = 0,
                        explanation = filled(Res.string.learn_t_x_so_comes_out, "18", "9", "2", "9", "3", "2"),
                        visual = AreaGrid(cols = 3, rows = 3, showArea = false, unit = ""),
                    ),
                    Concept(
                        body = words(Res.string.learn_arithmetic_surds_simplify_s4_body),
                        formula = math("√72 = 6√2"),
                        visual = AreaGrid(cols = 6, rows = 6, showArea = false, unit = ""),
                    ),
                    Numeric(
                        question = words(Res.string.learn_arithmetic_surds_simplify_s5_question),
                        answer = "2",
                        explanation = words(Res.string.learn_arithmetic_surds_simplify_s5_explanation),
                        visual = AreaGrid(cols = 2, rows = 2, showArea = false, unit = ""),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_surds_simplify_s6_question),
                        options = mathOptions("√8", "√12", "√15", "√20"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_arithmetic_surds_simplify_s6_explanation),
                        visual = AreaGrid(cols = 4, rows = 4, showArea = false, unit = ""),
                    ),
                ),
            ),
            LessonSpec(
                id = "arithmetic-surds-arithmetic",
                title = Res.string.learn_arithmetic_surds_arithmetic_title,
                summary = Res.string.learn_arithmetic_surds_arithmetic_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_arithmetic_surds_arithmetic_s1_body),
                        formula = math("2√3 + 4√3 = 6√3"),
                    ),
                    Choice(
                        question = words(Res.string.learn_shared_roots_match_so_only_numbers),
                        formula = math("3√5 + 2√5 = ?"),
                        options = mathOptions("5√5", "5√10", "6√5", "5"),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_arithmetic_surds_arithmetic_s2_explanation),
                    ),
                    Concept(
                        body = words(Res.string.learn_arithmetic_surds_arithmetic_s3_body),
                        formula = words(Res.string.learn_arithmetic_surds_arithmetic_s3_formula),
                    ),
                    Worked(
                        problem = math("√3 x √12 = ?"),
                        lines = listOf(
                            words(Res.string.learn_arithmetic_surds_arithmetic_s4_l1),
                            math("√3 x √12 = √36"),
                            words(Res.string.learn_arithmetic_surds_arithmetic_s4_l3),
                        ),
                        result = math("6"),
                        visual = AreaGrid(cols = 6, rows = 6, showArea = false, unit = ""),
                    ),
                    Numeric(
                        question = math("√5 x √20 = ?"),
                        answer = "10",
                        explanation = words(Res.string.learn_arithmetic_surds_arithmetic_s5_explanation),
                        visual = AreaGrid(cols = 10, rows = 10, showArea = false, unit = ""),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_surds_arithmetic_s6_question),
                        options = mathOptions("7", "14", "49", "√7"),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_arithmetic_surds_arithmetic_s6_explanation),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = words(Res.string.learn_unit_arithmetic_surds_q1_prompt),
                options = mathOptions("√16", "√25", "√30", "√36"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_arithmetic_surds_q1_explanation),
            ),
            QuizQuestion(
                prompt = filled(Res.string.learn_t_simplify, "32"),
                options = mathOptions("4√2", "8√2", "2√16", "16√2"),
                correctIndex = 0,
                explanation = filled(Res.string.learn_t_x_so_comes_out, "32", "16", "2", "16", "4", "2"),
                visual = Steps(terms = listOf(2, 4, 8, 16, 32), multiply = true),
            ),
            QuizQuestion(
                prompt = filled(Res.string.learn_t_simplify, "45"),
                options = mathOptions("3√5", "5√3", "9√5", "15√3"),
                correctIndex = 0,
                explanation = filled(Res.string.learn_t_x_so_comes_out, "45", "9", "5", "9", "3", "5"),
                visual = AreaGrid(cols = 3, rows = 3, showArea = false, unit = ""),
            ),
            QuizQuestion(
                prompt = math("√6 x √6 = ?"),
                options = mathOptions("6", "12", "36", "√12"),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_arithmetic_surds_q4_explanation),
            ),
            QuizQuestion(
                prompt = math("5√2 + 3√2 = ?"),
                options = mathOptions("8√2", "8√4", "15√2", "8"),
                correctIndex = 0,
                explanation = words(Res.string.learn_shared_roots_match_so_only_numbers),
            ),
            QuizQuestion(
                prompt = filled(Res.string.learn_t_between_whole_numbers_sit, "50"),
                options = listOf(
                    filled(Res.string.learn_opt_between, "5", "6"),
                    filled(Res.string.learn_opt_between, "6", "7"),
                    filled(Res.string.learn_opt_between, "7", "8"),
                    filled(Res.string.learn_opt_between, "8", "9"),
                ),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_arithmetic_surds_q6_explanation),
                visual = AreaGrid(cols = 7, rows = 7, showArea = false, unit = ""),
            ),
        ),
    )

    private val bounds = learnUnit(
        topic = MathTopic.ARITHMETIC,
        urlSlug = "bounds",
        title = Res.string.learn_unit_arithmetic_bounds_title,
        summary = Res.string.learn_unit_arithmetic_bounds_summary,
        level = GradeLevel.GRADES_9_10,
        lessons = listOf(
            LessonSpec(
                id = "arithmetic-bounds",
                title = Res.string.learn_arithmetic_bounds_title,
                summary = Res.string.learn_arithmetic_bounds_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_arithmetic_bounds_s1_body),
                        formula = words(Res.string.learn_arithmetic_bounds_s1_formula),
                        visual = NumberLine(from = 30, to = 50, tickStep = 5, start = 40, jump = -5, thenJump = 5),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_bounds_s2_question),
                        options = mathOptions("400", "450", "495", "499"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_arithmetic_bounds_s2_explanation),
                        visual = NumberLine(from = 300, to = 700, tickStep = 100, start = 500, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_arithmetic_bounds_s3_body),
                        formula = words(Res.string.learn_arithmetic_bounds_s3_formula),
                        visual = NumberLine(from = 400, to = 600, tickStep = 50, start = 500, jump = -50, thenJump = 50),
                    ),
                    Concept(
                        body = words(Res.string.learn_arithmetic_bounds_s4_body),
                        formula = words(Res.string.learn_arithmetic_bounds_s4_formula),
                        visual = NumberLine(from = 30, to = 50, tickStep = 5, start = 35, jump = 10),
                    ),
                    Numeric(
                        question = words(Res.string.learn_arithmetic_bounds_s5_question),
                        answer = "55",
                        explanation = words(Res.string.learn_arithmetic_bounds_s5_explanation),
                        visual = NumberLine(from = 50, to = 70, tickStep = 5, start = 60, reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_bounds_s6_question),
                        options = mathOptions("250 m", "275 m", "295 m", "299 m"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_arithmetic_bounds_s6_explanation),
                        visual = NumberLine(from = 200, to = 400, tickStep = 25, start = 300, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "arithmetic-bounds-significant",
                title = Res.string.learn_arithmetic_bounds_significant_title,
                summary = Res.string.learn_arithmetic_bounds_significant_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_arithmetic_bounds_significant_s1_body),
                        formula = words(Res.string.learn_arithmetic_bounds_significant_s1_formula),
                        visual = Steps(terms = listOf(427, 4270, 42700), multiply = true),
                    ),
                    Concept(
                        body = words(Res.string.learn_arithmetic_bounds_significant_s2_body),
                        formula = words(Res.string.learn_arithmetic_bounds_significant_s2_formula),
                        visual = Steps(terms = listOf(39, 390, 3900), multiply = true),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_bounds_significant_s3_question),
                        options = mathOptions("0.046", "0.05", "0.0462", "0.04"),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_arithmetic_bounds_significant_s3_explanation),
                        visual = Steps(terms = listOf(46, 460, 4600), multiply = true),
                    ),
                    Numeric(
                        question = filled(Res.string.learn_t_round_significant_figures, "27", "486", "3"),
                        answer = "27500",
                        explanation = words(Res.string.learn_arithmetic_bounds_significant_s4_explanation),
                        visual = Steps(terms = listOf(275, 2750, 27500), multiply = true),
                    ),
                    Concept(
                        body = words(Res.string.learn_arithmetic_bounds_significant_s5_body),
                        formula = words(Res.string.learn_arithmetic_bounds_significant_s5_formula),
                        visual = Steps(terms = listOf(308, 3080, 30800), multiply = true),
                    ),
                    Choice(
                        question = filled(Res.string.learn_t_many_significant_figures, "0.0250"),
                        options = mathOptions("1", "2", "3", "4"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_arithmetic_bounds_significant_s6_explanation),
                        visual = Steps(terms = listOf(250, 2500, 25000), multiply = true),
                    ),
                ),
            ),
            LessonSpec(
                id = "arithmetic-bounds-calculating",
                title = Res.string.learn_arithmetic_bounds_calculating_title,
                summary = Res.string.learn_arithmetic_bounds_calculating_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_arithmetic_bounds_calculating_s1_body),
                        visual = NumberLine(from = 30, to = 50, tickStep = 5, start = 40, jump = -5, thenJump = 5),
                    ),
                    Worked(
                        problem = words(Res.string.learn_arithmetic_bounds_calculating_s2_problem),
                        lines = listOf(
                            words(Res.string.learn_arithmetic_bounds_calculating_s2_l1),
                            words(Res.string.learn_arithmetic_bounds_calculating_s2_l2),
                            words(Res.string.learn_arithmetic_bounds_calculating_s2_l3),
                            words(Res.string.learn_arithmetic_bounds_calculating_s2_l4),
                        ),
                        result = words(Res.string.learn_arithmetic_bounds_calculating_s2_result),
                        visual = NumberLine(from = 60, to = 100, tickStep = 10, start = 80, jump = -10, thenJump = 10),
                    ),
                    Concept(
                        body = words(Res.string.learn_arithmetic_bounds_calculating_s3_body),
                        visual = NumberLine(from = 80, to = 120, tickStep = 10, start = 90, jump = 20),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_bounds_calculating_s4_question),
                        options = listOf(
                            math("100 cm"),
                            math("105 cm"),
                            math("110 cm"),
                            filled(Res.string.learn_opt_just_under_cm, "110"),
                        ),
                        correctIndex = 3,
                        explanation = words(Res.string.learn_arithmetic_bounds_calculating_s4_explanation),
                        visual = NumberLine(from = 80, to = 120, tickStep = 10, start = 100, reveal = false),
                    ),
                    Numeric(
                        question = words(Res.string.learn_arithmetic_bounds_calculating_s5_question),
                        answer = "115",
                        explanation = words(Res.string.learn_arithmetic_bounds_calculating_s5_explanation),
                        visual = NumberLine(from = 100, to = 140, tickStep = 5, start = 120, reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_arithmetic_bounds_calculating_s6_question),
                        options = wordOptions(
                            Res.string.learn_arithmetic_bounds_calculating_s6_o1,
                            Res.string.learn_arithmetic_bounds_calculating_s6_o2,
                            Res.string.learn_arithmetic_bounds_calculating_s6_o3,
                            Res.string.learn_arithmetic_bounds_calculating_s6_o4,
                        ),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_arithmetic_bounds_calculating_s6_explanation),
                        visual = NumberLine(from = 0, to = 40, tickStep = 5, start = 30, jump = -10, reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = words(Res.string.learn_unit_arithmetic_bounds_q1_prompt),
                options = mathOptions("7 kg", "7.5 kg", "7.9 kg", "8 kg"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_arithmetic_bounds_q1_explanation),
                visual = NumberLine(from = 6, to = 10, tickStep = 1, start = 8, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_arithmetic_bounds_q2_prompt),
                options = listOf(
                    filled(Res.string.learn_opt_up_to_just_under, "195", "205"),
                    filled(Res.string.learn_opt_up_to_just_under, "190", "200"),
                    filled(Res.string.learn_opt_up_to, "199", "201"),
                    filled(Res.string.learn_opt_up_to, "195", "210"),
                ),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_arithmetic_bounds_q2_explanation),
                visual = NumberLine(from = 180, to = 220, tickStep = 5, start = 200, reveal = false),
            ),
            QuizQuestion(
                prompt = filled(Res.string.learn_t_round_significant_figures, "5", "748", "2"),
                options = mathOptions("5 700", "5 800", "5 750", "6 000"),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_arithmetic_bounds_q3_explanation),
                visual = Steps(terms = listOf(57, 570, 5700), multiply = true),
            ),
            QuizQuestion(
                prompt = filled(Res.string.learn_t_many_significant_figures, "0.00680"),
                options = mathOptions("2", "3", "4", "5"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_arithmetic_bounds_q4_explanation),
                visual = Steps(terms = listOf(68, 680, 6800), multiply = true),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_arithmetic_bounds_q5_prompt),
                options = mathOptions("46.5", "46", "47", "45.5"),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_arithmetic_bounds_q5_explanation),
                visual = NumberLine(from = 44, to = 48, tickStep = 1, start = 46, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_arithmetic_bounds_q6_prompt),
                options = mathOptions("38", "39", "39.5", "40"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_arithmetic_bounds_q6_explanation),
                visual = NumberLine(from = 36, to = 44, tickStep = 2, start = 40, reveal = false),
            ),
        ),
    )

    val units: List<LearnUnit> = listOf(
        counting,
        multiplication,
        fractions,
        decimals,
        negatives,
        ratio,
        percent,
        standardForm,
        surds,
        bounds,
    )
}
