package com.inspiredandroid.braincup.learn.content

import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.learn.Curve
import com.inspiredandroid.braincup.learn.GradeLevel
import com.inspiredandroid.braincup.learn.LearnUnit
import com.inspiredandroid.braincup.learn.LearnVisual.AlgebraRect
import com.inspiredandroid.braincup.learn.LearnVisual.AreaGrid
import com.inspiredandroid.braincup.learn.LearnVisual.ArrayDots
import com.inspiredandroid.braincup.learn.LearnVisual.Balance
import com.inspiredandroid.braincup.learn.LearnVisual.Counters
import com.inspiredandroid.braincup.learn.LearnVisual.Inequality
import com.inspiredandroid.braincup.learn.LearnVisual.NumberLine
import com.inspiredandroid.braincup.learn.LearnVisual.Plot
import com.inspiredandroid.braincup.learn.LearnVisual.Solid
import com.inspiredandroid.braincup.learn.LearnVisual.Steps
import com.inspiredandroid.braincup.learn.LessonSpec
import com.inspiredandroid.braincup.learn.LessonStep.Choice
import com.inspiredandroid.braincup.learn.LessonStep.Concept
import com.inspiredandroid.braincup.learn.LessonStep.Numeric
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.learn.PlotPoint
import com.inspiredandroid.braincup.learn.QuizQuestion
import com.inspiredandroid.braincup.learn.SolidKind
import com.inspiredandroid.braincup.learn.filled
import com.inspiredandroid.braincup.learn.learnUnit
import com.inspiredandroid.braincup.learn.math
import com.inspiredandroid.braincup.learn.mathOptions
import com.inspiredandroid.braincup.learn.wordOptions
import com.inspiredandroid.braincup.learn.words

/**
 * Algebra: what a letter is doing in a sum, then equations, graphs and inequalities, and finally
 * the three that need a method of their own - simultaneous equations, quadratics, and indices.
 *
 * The area model ([AlgebraRect]) carries the bracket work rather than a borrowed [AreaGrid]: a
 * side of length x has no unit-square count, and an area grid that captions itself in cm² is the
 * exact confusion expanding and factorising exist to clear up.
 */
internal object AlgebraContent {

    private val expressions = learnUnit(
        topic = MathTopic.ALGEBRA,
        urlSlug = "expressions",
        title = Res.string.learn_unit_algebra_expressions_title,
        summary = Res.string.learn_unit_algebra_expressions_summary,
        level = GradeLevel.GRADES_6_8,
        lessons = listOf(
            LessonSpec(
                id = "algebra-expressions-letters",
                title = Res.string.learn_algebra_expressions_letters_title,
                summary = Res.string.learn_algebra_expressions_letters_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_algebra_expressions_letters_s1_body),
                        formula = math("x + 3 = 8"),
                        visual = Balance(leftX = 1, leftOnes = 3, rightOnes = 8),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_expressions_letters_s2_body),
                        formula = math("3x = x + x + x"),
                        visual = AlgebraRect(leftOnes = 3, topX = 1),
                    ),
                    Choice(
                        question = words(Res.string.learn_algebra_expressions_letters_s3_question),
                        options = mathOptions("n + 5", "5n", "n - 5", "n ÷ 5"),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_algebra_expressions_letters_s3_explanation),
                        // One counter and five beside it, never merged: "n, and five more" is the
                        // whole question, and the distractor 5n would be five groups instead.
                        visual = Counters(groups = listOf(1, 5), merge = false, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_expressions_letters_s4_body),
                        formula = math("3 * 4 = 12"),
                        visual = ArrayDots(rows = 3, cols = 4),
                    ),
                    Numeric(
                        question = words(Res.string.learn_algebra_expressions_letters_s5_question),
                        formula = math("2 * {b:5} + 7 = ?"),
                        answer = "17",
                        explanation = words(Res.string.learn_algebra_expressions_letters_s5_explanation),
                        visual = ArrayDots(rows = 2, cols = 5),
                    ),
                    Choice(
                        question = words(Res.string.learn_algebra_expressions_letters_s6_question),
                        formula = math("6² - 6 = ?"),
                        options = mathOptions("36", "12", "30", "0"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_algebra_expressions_letters_s6_explanation),
                        // Six sixes with one row lifted off: the method, not the total.
                        visual = ArrayDots(rows = 6, cols = 6, split = 5),
                    ),
                ),
            ),
            LessonSpec(
                id = "algebra-expressions-collecting",
                title = Res.string.learn_algebra_expressions_collecting_title,
                summary = Res.string.learn_algebra_expressions_collecting_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_algebra_expressions_collecting_s1_body),
                        formula = math("3x + {b:5}x = 8x"),
                        visual = Counters(groups = listOf(3, 5)),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_expressions_collecting_s2_body),
                        formula = math("3a + 2b"),
                        visual = Counters(groups = listOf(3, 2), merge = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_algebra_expressions_collecting_s3_question),
                        options = mathOptions("6x + 4", "9x", "5x + 2", "6x + 2"),
                        correctIndex = 3,
                        explanation = words(Res.string.learn_algebra_expressions_collecting_s3_explanation),
                        visual = Counters(groups = listOf(4, 2), merge = false, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_expressions_collecting_s4_body),
                        formula = math("7x - 2x = 5x"),
                        visual = ArrayDots(rows = 7, cols = 1, split = 5),
                    ),
                    Numeric(
                        question = words(Res.string.learn_algebra_expressions_collecting_s5_question),
                        formula = math("9 - {b:4} = ?"),
                        answer = "5",
                        explanation = words(Res.string.learn_algebra_expressions_collecting_s5_explanation),
                        visual = ArrayDots(rows = 9, cols = 1, split = 5),
                    ),
                    Choice(
                        question = words(Res.string.learn_algebra_expressions_collecting_s6_question),
                        options = mathOptions("4x + 2x", "2x + 3y", "5a - a", "3b + 6b"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_algebra_expressions_collecting_s6_explanation),
                        visual = Counters(groups = listOf(2, 3), merge = false, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "algebra-expressions-brackets",
                title = Res.string.learn_algebra_expressions_brackets_title,
                summary = Res.string.learn_algebra_expressions_brackets_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_algebra_expressions_brackets_s1_body),
                        formula = math("3(x + 4)"),
                        visual = AlgebraRect(leftOnes = 3, topX = 1, topOnes = 4),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_expressions_brackets_s2_body),
                        formula = math("3(x + 4) = 3x + 12"),
                        visual = AlgebraRect(leftOnes = 3, topX = 1, topOnes = 4),
                    ),
                    Choice(
                        question = words(Res.string.learn_algebra_expressions_brackets_s3_question),
                        options = mathOptions("2x + 5", "x + 10", "2x + 10", "2x + 7"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_algebra_expressions_brackets_s3_explanation),
                        visual = AlgebraRect(leftOnes = 2, topX = 1, topOnes = 5, reveal = false, revealSides = true),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_expressions_brackets_s4_body),
                        formula = math("6x + 15 = 3(2x + 5)"),
                        visual = AlgebraRect(leftOnes = 3, topX = 2, topOnes = 5),
                    ),
                    Numeric(
                        question = words(Res.string.learn_algebra_expressions_brackets_s5_question),
                        // No leading formula: every way of writing this one carries the 15, and
                        // "5" is a substring of "15", which is exactly what the ask-rather-than-
                        // tell guard forbids. The question is prose, so it leads on its own.
                        answer = "5",
                        explanation = words(Res.string.learn_algebra_expressions_brackets_s5_explanation),
                        visual = AlgebraRect(leftOnes = 3, topX = 2, topOnes = 5, reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_algebra_expressions_brackets_s6_question),
                        options = mathOptions("4", "2", "8", "12"),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_algebra_expressions_brackets_s6_explanation),
                        visual = AlgebraRect(leftOnes = 4, topX = 2, topOnes = 3, reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = words(Res.string.learn_unit_algebra_expressions_q1_prompt),
                options = mathOptions("3m", "m - 3", "m + 3", "m ÷ 3"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_algebra_expressions_q1_explanation),
                visual = Counters(groups = listOf(3, 1), merge = false, reveal = false),
            ),
            QuizQuestion(
                prompt = math("5x + 2 + {b:3}x = ?"),
                options = mathOptions("10x", "8x + 5", "5x + 5", "8x + 2"),
                correctIndex = 3,
                explanation = words(Res.string.learn_unit_algebra_expressions_q2_explanation),
                visual = Counters(groups = listOf(5, 3), merge = false, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_algebra_expressions_q3_question),
                options = mathOptions("11", "21", "25", "17"),
                correctIndex = 3,
                explanation = words(Res.string.learn_unit_algebra_expressions_q3_explanation),
                visual = ArrayDots(rows = 3, cols = 7),
            ),
            QuizQuestion(
                prompt = math("4(x + 3) = ?"),
                options = mathOptions("4x + 3", "4x + 12", "x + 12", "4x + 7"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_algebra_expressions_q4_explanation),
                visual = AlgebraRect(leftOnes = 4, topX = 1, topOnes = 3, reveal = false, revealSides = true),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_algebra_expressions_q5_prompt),
                options = mathOptions("4a + 5b", "2x + 6x", "7y - 3y", "a + 4a"),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_algebra_expressions_q5_explanation),
                visual = Counters(groups = listOf(4, 5), merge = false, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_algebra_expressions_q6_prompt),
                options = mathOptions("2", "10", "5", "25"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_algebra_expressions_q6_explanation),
                visual = AlgebraRect(leftOnes = 5, topX = 2, topOnes = 5, reveal = false),
            ),
        ),
    )

    private val linearEquations = learnUnit(
        topic = MathTopic.ALGEBRA,
        urlSlug = "linear-equations",
        title = Res.string.learn_unit_algebra_linear_equations_title,
        summary = Res.string.learn_unit_algebra_linear_equations_summary,
        level = GradeLevel.GRADES_6_8,
        lessons = listOf(
            LessonSpec(
                id = "algebra-equations-balance",
                title = Res.string.learn_algebra_equations_balance_title,
                summary = Res.string.learn_algebra_equations_balance_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_algebra_equations_balance_s1_body),
                        formula = math("x + 4 = 9"),
                        visual = Balance(leftX = 1, leftOnes = 4, rightOnes = 9),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_equations_balance_s2_body),
                        formula = math("x = 5"),
                        visual = Balance(leftX = 1, leftOnes = 4, rightOnes = 9, remove = 4),
                    ),
                    Choice(
                        question = words(Res.string.learn_algebra_equations_balance_s3_question),
                        options = mathOptions("10", "6", "4", "16"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_algebra_equations_balance_s3_explanation),
                        visual = Balance(leftX = 1, leftOnes = 6, rightOnes = 10),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_equations_balance_s4_body),
                        formula = math("3x = 12"),
                        visual = Balance(leftX = 3, leftOnes = 0, rightOnes = 12),
                    ),
                    Numeric(
                        question = filled(Res.string.learn_t_solve_ax, "3", "12"),
                        formula = math("12 ÷ 3 = ?"),
                        answer = "4",
                        explanation = words(Res.string.learn_algebra_equations_balance_s5_explanation),
                        visual = Balance(leftX = 3, leftOnes = 0, rightOnes = 12),
                    ),
                    Choice(
                        question = filled(Res.string.learn_t_solve_ax_plus_b, "2", "3", "11"),
                        formula = math("2x + 3 = 11, x = ?"),
                        options = mathOptions("7", "5", "8", "4"),
                        correctIndex = 3,
                        explanation = words(Res.string.learn_algebra_equations_balance_s6_explanation),
                        visual = Balance(leftX = 2, leftOnes = 3, rightOnes = 11),
                    ),
                ),
            ),
            LessonSpec(
                id = "algebra-equations-twostep",
                title = Res.string.learn_algebra_equations_twostep_title,
                summary = Res.string.learn_algebra_equations_twostep_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_algebra_equations_twostep_s1_body),
                        formula = math("4x + 5 = 17"),
                        visual = Balance(leftX = 4, leftOnes = 5, rightOnes = 17),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_equations_twostep_s2_body),
                        formula = math("4x = 12"),
                        visual = Balance(leftX = 4, leftOnes = 5, rightOnes = 17, remove = 5),
                    ),
                    Numeric(
                        question = filled(Res.string.learn_t_solve_ax, "4", "12"),
                        formula = math("12 ÷ 4 = ?"),
                        answer = "3",
                        explanation = words(Res.string.learn_algebra_equations_twostep_s3_explanation),
                        visual = Balance(leftX = 4, leftOnes = 0, rightOnes = 12),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_equations_twostep_s4_body),
                        formula = math("4x + 5 = 17"),
                        visual = Balance(leftX = 4, leftOnes = 5, rightOnes = 17),
                    ),
                    Choice(
                        question = filled(Res.string.learn_t_solve_ax_plus_b, "5", "2", "22"),
                        formula = math("5x + 2 = 22, x = ?"),
                        options = mathOptions("5", "20", "4", "24"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_algebra_equations_twostep_s5_explanation),
                        visual = Balance(leftX = 5, leftOnes = 2, rightOnes = 22),
                    ),
                    Choice(
                        question = words(Res.string.learn_shared_which_equation_scale),
                        options = mathOptions("3x + 2 = 14", "3x = 14", "2x + 3 = 14", "3x + 14 = 2"),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_algebra_equations_twostep_s6_explanation),
                        visual = Balance(leftX = 3, leftOnes = 2, rightOnes = 14),
                    ),
                ),
            ),
            LessonSpec(
                id = "algebra-equations-bothsides",
                title = Res.string.learn_algebra_equations_bothsides_title,
                summary = Res.string.learn_algebra_equations_bothsides_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_algebra_equations_bothsides_s1_body),
                        formula = math("5x + 2 = 2x + 17"),
                        visual = Balance(leftX = 5, leftOnes = 2, rightX = 2, rightOnes = 17),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_equations_bothsides_s2_body),
                        formula = math("3x + 2 = 17"),
                        visual = Balance(leftX = 5, leftOnes = 2, rightX = 2, rightOnes = 17, removeX = 2),
                    ),
                    Numeric(
                        question = filled(Res.string.learn_t_solve_ax_plus_b, "3", "2", "17"),
                        formula = math("3x + 2 = 17, x = ?"),
                        answer = "5",
                        explanation = words(Res.string.learn_algebra_equations_bothsides_s3_explanation),
                        visual = Balance(leftX = 3, leftOnes = 2, rightOnes = 17),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_equations_bothsides_s4_body),
                        formula = math("2x + 9 = 5x"),
                        visual = Balance(leftX = 2, leftOnes = 9, rightX = 5, rightOnes = 0),
                    ),
                    Choice(
                        question = words(Res.string.learn_algebra_equations_bothsides_s5_question),
                        formula = math("2x + 9 = 5x, x = ?"),
                        options = mathOptions("2", "9", "3", "5"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_algebra_equations_bothsides_s5_explanation),
                        visual = Balance(leftX = 2, leftOnes = 9, rightX = 5, rightOnes = 0),
                    ),
                    Choice(
                        question = filled(Res.string.learn_t_solve_both_sides, "7", "3", "16"),
                        formula = math("7x = 3x + 16, x = ?"),
                        options = mathOptions("2", "4", "16", "5"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_algebra_equations_bothsides_s6_explanation),
                        visual = Balance(leftX = 7, leftOnes = 0, rightX = 3, rightOnes = 16),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = words(Res.string.learn_unit_algebra_linear_equations_q1_prompt),
                options = mathOptions("23", "8", "6", "7"),
                correctIndex = 3,
                explanation = words(Res.string.learn_unit_algebra_linear_equations_q1_explanation),
                visual = Balance(leftX = 1, leftOnes = 8, rightOnes = 15),
            ),
            QuizQuestion(
                prompt = math("4x = 20, x = ?"),
                options = mathOptions("5", "4", "16", "24"),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_algebra_linear_equations_q2_explanation),
                visual = Balance(leftX = 4, leftOnes = 0, rightOnes = 20),
            ),
            QuizQuestion(
                prompt = filled(Res.string.learn_t_solve_ax_plus_b, "3", "4", "19"),
                options = mathOptions("5", "7", "15", "23"),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_algebra_linear_equations_q3_explanation),
                visual = Balance(leftX = 3, leftOnes = 4, rightOnes = 19),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_shared_which_equation_scale),
                options = mathOptions("5x + 2 = 13", "2x + 13 = 5", "2x = 13", "2x + 5 = 13"),
                correctIndex = 3,
                explanation = words(Res.string.learn_unit_algebra_linear_equations_q4_explanation),
                visual = Balance(leftX = 2, leftOnes = 5, rightOnes = 13),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_algebra_linear_equations_q5_prompt),
                options = mathOptions("5", "4", "2", "10"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_algebra_linear_equations_q5_explanation),
                visual = Balance(leftX = 6, leftOnes = 1, rightX = 4, rightOnes = 9),
            ),
            QuizQuestion(
                prompt = filled(Res.string.learn_t_solve_both_sides, "5", "2", "12"),
                options = mathOptions("3", "12", "4", "6"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_algebra_linear_equations_q6_explanation),
                visual = Balance(leftX = 5, leftOnes = 0, rightX = 2, rightOnes = 12),
            ),
        ),
    )

    /**
     * Straight-line graphs. Every plot here stays inside the -3..3 window `drawPlot` draws, which
     * is what keeps the gradients small: a line of gradient 4 leaves the panel within one square
     * and the picture stops being about the line.
     */
    private val straightLineGraphs = learnUnit(
        topic = MathTopic.ALGEBRA,
        urlSlug = "straight-line-graphs",
        title = Res.string.learn_unit_algebra_straight_line_graphs_title,
        summary = Res.string.learn_unit_algebra_straight_line_graphs_summary,
        level = GradeLevel.GRADES_6_8,
        lessons = listOf(
            LessonSpec(
                id = "algebra-graphs-coordinates",
                title = Res.string.learn_algebra_graphs_coordinates_title,
                summary = Res.string.learn_algebra_graphs_coordinates_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_algebra_graphs_coordinates_s1_body),
                        formula = math("(2, 1)"),
                        // Bare axes: the step is about where the point sits, and a line through the
                        // origin would invite reading the point off the line instead of the grid.
                        visual = Plot(points = listOf(PlotPoint(x = 2f, y = 1f, label = "(2, 1)"))),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_graphs_coordinates_s2_body),
                        formula = math("y = x"),
                        visual = Plot(
                            curve = Curve.Linear(m = 1f),
                            points = listOf(PlotPoint(x = 2f, y = 2f, label = "(2, 2)")),
                        ),
                    ),
                    Choice(
                        question = words(Res.string.learn_algebra_graphs_coordinates_s3_question),
                        options = mathOptions("(3, 1)", "(1, 3)", "(0, 2)", "(3, 3)"),
                        correctIndex = 3,
                        explanation = words(Res.string.learn_algebra_graphs_coordinates_s3_explanation),
                        visual = Plot(curve = Curve.Linear(m = 1f), reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_graphs_coordinates_s4_body),
                        formula = math("y = x + 2"),
                        visual = Plot(
                            curve = Curve.Linear(m = 1f, c = 2f),
                            points = listOf(PlotPoint(x = 0f, y = 2f, label = "(0, 2)")),
                        ),
                    ),
                    Numeric(
                        question = words(Res.string.learn_algebra_graphs_coordinates_s5_question),
                        formula = math("1 + 2 = ?"),
                        answer = "3",
                        explanation = words(Res.string.learn_algebra_graphs_coordinates_s5_explanation),
                        visual = Plot(
                            curve = Curve.Linear(m = 1f, c = 2f),
                            points = listOf(PlotPoint(x = 1f, y = 3f)),
                            reveal = false,
                        ),
                    ),
                    Choice(
                        question = words(Res.string.learn_algebra_graphs_coordinates_s6_question),
                        options = mathOptions("(0, 2)", "(2, 0)", "(0, 0)", "(0, 1)"),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_algebra_graphs_coordinates_s6_explanation),
                        visual = Plot(curve = Curve.Linear(m = 1f, c = 2f), reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "algebra-graphs-gradient",
                title = Res.string.learn_algebra_graphs_gradient_title,
                summary = Res.string.learn_algebra_graphs_gradient_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_algebra_graphs_gradient_s1_body),
                        formula = math("y = mx + c"),
                        visual = Plot(curve = Curve.Linear(m = 1f, c = 1f)),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_graphs_gradient_s2_body),
                        formula = math("y = 2x"),
                        visual = Plot(curve = Curve.Linear(m = 2f)),
                    ),
                    Choice(
                        question = words(Res.string.learn_algebra_graphs_gradient_s3_question),
                        options = mathOptions("1", "3", "2", "4"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_algebra_graphs_gradient_s3_explanation),
                        visual = Plot(curve = Curve.Linear(m = 2f), reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_graphs_gradient_s4_body),
                        formula = math("y = x + 1"),
                        visual = Plot(
                            curve = Curve.Linear(m = 1f, c = 1f),
                            points = listOf(PlotPoint(x = 0f, y = 1f, label = "(0, 1)")),
                        ),
                    ),
                    Numeric(
                        question = filled(Res.string.learn_t_line_value, "2", "1", "1"),
                        formula = math("2 * 1 + 1 = ?"),
                        answer = "3",
                        explanation = words(Res.string.learn_algebra_graphs_gradient_s5_explanation),
                        visual = Plot(
                            curve = Curve.Linear(m = 2f, c = 1f),
                            points = listOf(PlotPoint(x = 1f, y = 3f)),
                            reveal = false,
                        ),
                    ),
                    Choice(
                        question = words(Res.string.learn_algebra_graphs_gradient_s6_question),
                        options = mathOptions("y = -x + 2", "y = 2x - 1", "y = x - 2", "y = 2x + 1"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_algebra_graphs_gradient_s6_explanation),
                        visual = Plot(curve = Curve.Linear(m = 2f, c = -1f), reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "algebra-graphs-writing",
                title = Res.string.learn_algebra_graphs_writing_title,
                summary = Res.string.learn_algebra_graphs_writing_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_algebra_graphs_writing_s1_body),
                        formula = math("y = 2x - 1"),
                        visual = Plot(
                            curve = Curve.Linear(m = 2f, c = -1f),
                            points = listOf(PlotPoint(x = 0f, y = -1f, label = "(0, -1)")),
                        ),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_graphs_writing_s2_body),
                        formula = math("y = -x + 2"),
                        visual = Plot(curve = Curve.Linear(m = -1f, c = 2f)),
                    ),
                    Choice(
                        question = words(Res.string.learn_shared_gradient_of_this_line),
                        options = mathOptions("1", "-1", "-2", "2"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_algebra_graphs_writing_s3_explanation),
                        visual = Plot(curve = Curve.Linear(m = -1f, c = 2f), reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_graphs_writing_s4_body),
                        formula = math("y = 2"),
                        visual = Plot(curve = Curve.Linear(m = 0f, c = 2f)),
                    ),
                    Choice(
                        question = words(Res.string.learn_algebra_graphs_writing_s5_question),
                        options = mathOptions("y = x - 1", "y = -x + 1", "y = x + 1", "y = 2x + 1"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_algebra_graphs_writing_s5_explanation),
                        visual = Plot(curve = Curve.Linear(m = 1f, c = 1f), reveal = false),
                    ),
                    Numeric(
                        question = words(Res.string.learn_algebra_graphs_writing_s6_question),
                        answer = "3",
                        explanation = words(Res.string.learn_algebra_graphs_writing_s6_explanation),
                        visual = Plot(
                            curve = Curve.Linear(m = 3f),
                            points = listOf(PlotPoint(x = 1f, y = 3f)),
                            reveal = false,
                        ),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = words(Res.string.learn_unit_algebra_straight_line_graphs_q1_prompt),
                options = mathOptions("(2, 1)", "(1, 2)", "(2, 2)", "(1, 1)"),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_algebra_straight_line_graphs_q1_explanation),
                visual = Plot(points = listOf(PlotPoint(x = 2f, y = 1f)), reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_algebra_straight_line_graphs_q2_prompt),
                options = mathOptions("-1", "1", "-2", "2"),
                correctIndex = 3,
                explanation = words(Res.string.learn_unit_algebra_straight_line_graphs_q2_explanation),
                visual = Plot(curve = Curve.Linear(m = 2f, c = -1f), reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_algebra_straight_line_graphs_q3_prompt),
                options = mathOptions("(0, -2)", "(-2, 0)", "(0, 2)", "(2, 0)"),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_algebra_straight_line_graphs_q3_explanation),
                visual = Plot(curve = Curve.Linear(m = 1f, c = -2f), reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_algebra_straight_line_graphs_q4_prompt),
                options = mathOptions("y = 2x", "y = x + 2", "y = 2", "y = -2x"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_algebra_straight_line_graphs_q4_explanation),
                visual = Plot(curve = Curve.Linear(m = 0f, c = 2f), reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_shared_gradient_of_this_line),
                options = mathOptions("2", "0", "1", "-2"),
                correctIndex = 3,
                explanation = words(Res.string.learn_unit_algebra_straight_line_graphs_q5_explanation),
                visual = Plot(curve = Curve.Linear(m = -2f, c = 1f), reveal = false),
            ),
            QuizQuestion(
                prompt = filled(Res.string.learn_t_line_value_minus, "2", "1", "2"),
                options = mathOptions("2", "3", "1", "4"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_algebra_straight_line_graphs_q6_explanation),
                visual = Plot(curve = Curve.Linear(m = 2f, c = -1f), reveal = false),
            ),
        ),
    )

    private val inequalities = learnUnit(
        topic = MathTopic.ALGEBRA,
        urlSlug = "inequalities",
        title = Res.string.learn_unit_algebra_inequalities_title,
        summary = Res.string.learn_unit_algebra_inequalities_summary,
        level = GradeLevel.GRADES_6_8,
        lessons = listOf(
            LessonSpec(
                id = "algebra-inequalities-reading",
                title = Res.string.learn_algebra_inequalities_reading_title,
                summary = Res.string.learn_algebra_inequalities_reading_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_algebra_inequalities_reading_s1_body),
                        formula = math("x > 3"),
                        visual = Inequality(from = -1, to = 8, value = 3, greater = true),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_inequalities_reading_s2_body),
                        formula = math("x ≥ 3"),
                        visual = Inequality(from = -1, to = 8, value = 3, greater = true, orEqual = true),
                    ),
                    Choice(
                        question = words(Res.string.learn_shared_which_inequality_line),
                        options = mathOptions("x > 2", "x ≥ 2", "x < 2", "x ≤ 2"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_algebra_inequalities_reading_s3_explanation),
                        visual = Inequality(from = -3, to = 6, value = 2, greater = false, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_inequalities_reading_s4_body),
                        formula = math("x < 5"),
                        visual = Inequality(from = 0, to = 9, value = 5, greater = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_algebra_inequalities_reading_s5_question),
                        options = mathOptions("4", "3", "2", "0"),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_algebra_inequalities_reading_s5_explanation),
                        visual = Inequality(from = 0, to = 8, value = 4, greater = true, orEqual = true, reveal = false),
                    ),
                    Numeric(
                        question = words(Res.string.learn_algebra_inequalities_reading_s6_question),
                        answer = "7",
                        explanation = words(Res.string.learn_algebra_inequalities_reading_s6_explanation),
                        visual = Inequality(from = 2, to = 11, value = 6, greater = true, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "algebra-inequalities-solving",
                title = Res.string.learn_algebra_inequalities_solving_title,
                summary = Res.string.learn_algebra_inequalities_solving_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_algebra_inequalities_solving_s1_body),
                        formula = math("x + 3 > 7"),
                        visual = Inequality(from = 0, to = 9, value = 4, greater = true),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_inequalities_solving_s2_body),
                        formula = math("x > 4"),
                        visual = Inequality(from = 1, to = 10, value = 4, greater = true),
                    ),
                    Numeric(
                        question = words(Res.string.learn_algebra_inequalities_solving_s3_question),
                        formula = math("12 - 5 = ?"),
                        answer = "7",
                        explanation = words(Res.string.learn_algebra_inequalities_solving_s3_explanation),
                        visual = Inequality(from = 2, to = 11, value = 7, greater = false, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_inequalities_solving_s4_body),
                        formula = math("2x < 10"),
                        visual = Inequality(from = 0, to = 9, value = 5, greater = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_algebra_inequalities_solving_s5_question),
                        options = mathOptions("x ≤ 4", "x ≥ 4", "x > 4", "x ≥ 36"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_algebra_inequalities_solving_s5_explanation),
                        visual = Inequality(from = 0, to = 8, value = 4, greater = true, orEqual = true, reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_algebra_inequalities_solving_s6_question),
                        options = mathOptions("1", "2", "-1", "3"),
                        correctIndex = 3,
                        explanation = words(Res.string.learn_algebra_inequalities_solving_s6_explanation),
                        visual = Inequality(from = -1, to = 8, value = 3, greater = true, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "algebra-inequalities-flipping",
                title = Res.string.learn_algebra_inequalities_flipping_title,
                summary = Res.string.learn_algebra_inequalities_flipping_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_algebra_inequalities_flipping_s1_body),
                        formula = math("-2 < 3"),
                        // The two numbers on one line, so the swap in the next step is something to
                        // watch rather than a rule handed over.
                        visual = NumberLine(from = -5, to = 5, compare = listOf(-2, 3)),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_inequalities_flipping_s2_body),
                        formula = math("2 > -3"),
                        visual = NumberLine(from = -5, to = 5, compare = listOf(-3, 2)),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_inequalities_flipping_s3_body),
                        formula = math("-2x < 6"),
                        visual = Inequality(from = -6, to = 3, value = -3, greater = true),
                    ),
                    Choice(
                        question = filled(Res.string.learn_t_solve_ineq_gt, "-3", "9"),
                        options = mathOptions("x > -3", "x < 3", "x > 3", "x < -3"),
                        correctIndex = 3,
                        explanation = words(Res.string.learn_algebra_inequalities_flipping_s4_explanation),
                        visual = Inequality(from = -7, to = 2, value = -3, greater = false, reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_algebra_inequalities_flipping_s5_question),
                        options = mathOptions("x < -4", "x > -4", "x > 4", "x < 4"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_algebra_inequalities_flipping_s5_explanation),
                        visual = Inequality(from = -8, to = 2, value = -4, greater = true, reveal = false),
                    ),
                    Numeric(
                        question = words(Res.string.learn_algebra_inequalities_flipping_s6_question),
                        answer = "4",
                        explanation = words(Res.string.learn_algebra_inequalities_flipping_s6_explanation),
                        visual = Inequality(from = 0, to = 8, value = 4, greater = false, orEqual = true, reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = words(Res.string.learn_shared_which_inequality_line),
                options = mathOptions("x > 2", "x ≥ 2", "x < 2", "x ≤ 2"),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_algebra_inequalities_q1_explanation),
                visual = Inequality(from = -2, to = 7, value = 2, greater = true, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_algebra_inequalities_q2_prompt),
                options = mathOptions("13", "4", "5", "9"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_algebra_inequalities_q2_explanation),
                visual = Inequality(from = 1, to = 10, value = 5, greater = true, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_algebra_inequalities_q3_prompt),
                options = mathOptions("x ≥ 5", "x ≤ 5", "x ≤ 45", "x < 5"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_algebra_inequalities_q3_explanation),
                visual = Inequality(from = 0, to = 9, value = 5, greater = false, orEqual = true, reveal = false),
            ),
            QuizQuestion(
                prompt = filled(Res.string.learn_t_solve_ineq_gt, "-2", "6"),
                options = mathOptions("x > -3", "x < 3", "x > 3", "x < -3"),
                correctIndex = 3,
                explanation = words(Res.string.learn_unit_algebra_inequalities_q4_explanation),
                visual = Inequality(from = -7, to = 2, value = -3, greater = false, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_algebra_inequalities_q5_prompt),
                options = mathOptions("5", "6", "4", "10"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_algebra_inequalities_q5_explanation),
                visual = Inequality(from = 1, to = 10, value = 5, greater = true, orEqual = true, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_algebra_inequalities_q6_prompt),
                options = mathOptions("2", "3", "4", "1"),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_algebra_inequalities_q6_explanation),
                visual = Inequality(from = -1, to = 8, value = 3, greater = false, reveal = false),
            ),
        ),
    )

    private val simultaneousEquations = learnUnit(
        topic = MathTopic.ALGEBRA,
        urlSlug = "simultaneous-equations",
        title = Res.string.learn_unit_algebra_simultaneous_equations_title,
        summary = Res.string.learn_unit_algebra_simultaneous_equations_summary,
        level = GradeLevel.GRADES_9_10,
        lessons = listOf(
            LessonSpec(
                id = "algebra-systems-crossing",
                title = Res.string.learn_algebra_systems_crossing_title,
                summary = Res.string.learn_algebra_systems_crossing_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_algebra_systems_crossing_s1_body),
                        formula = math("y = x + 1"),
                        visual = Plot(curve = Curve.Linear(m = 1f, c = 1f)),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_systems_crossing_s2_body),
                        formula = math("y = 3 - x"),
                        visual = Plot(
                            curve = Curve.Linear(m = 1f, c = 1f),
                            second = Curve.Linear(m = -1f, c = 3f),
                            points = listOf(PlotPoint(x = 1f, y = 2f, label = "(1, 2)")),
                        ),
                    ),
                    Choice(
                        question = words(Res.string.learn_shared_where_lines_cross),
                        options = mathOptions("(2, 1)", "(0, 1)", "(1, 2)", "(3, 0)"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_algebra_systems_crossing_s3_explanation),
                        visual = Plot(
                            curve = Curve.Linear(m = 1f, c = 1f),
                            second = Curve.Linear(m = -1f, c = 3f),
                            reveal = false,
                        ),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_systems_crossing_s4_body),
                        formula = math("y = x + 1"),
                        visual = Plot(
                            curve = Curve.Linear(m = 1f, c = 1f),
                            second = Curve.Linear(m = 1f, c = -1f),
                        ),
                    ),
                    Numeric(
                        question = words(Res.string.learn_algebra_systems_crossing_s5_question),
                        answer = "1",
                        explanation = words(Res.string.learn_algebra_systems_crossing_s5_explanation),
                        visual = Plot(
                            curve = Curve.Linear(m = 1f),
                            second = Curve.Linear(m = -1f, c = 2f),
                            reveal = false,
                        ),
                    ),
                    Choice(
                        question = words(Res.string.learn_algebra_systems_crossing_s6_question),
                        options = mathOptions("x = 1, y = 2", "x = 2, y = 1", "x = 3, y = 0", "x = 0, y = 3"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_algebra_systems_crossing_s6_explanation),
                        visual = Plot(
                            curve = Curve.Linear(m = -1f, c = 3f),
                            second = Curve.Linear(m = 1f, c = -1f),
                            reveal = false,
                        ),
                    ),
                ),
            ),
            LessonSpec(
                id = "algebra-systems-elimination",
                title = Res.string.learn_algebra_systems_elimination_title,
                summary = Res.string.learn_algebra_systems_elimination_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_algebra_systems_elimination_s1_body),
                        formula = math("2x + y = 7"),
                        visual = Balance(leftX = 2, leftY = 1, rightOnes = 7),
                    ),
                    // The second rule, drawn. The step after this one asks the learner to subtract
                    // it from the first, and it used to arrive for the first time in that
                    // question's own wording - a subtraction of something never put on screen.
                    Concept(
                        body = words(Res.string.learn_algebra_systems_elimination_s2_body),
                        formula = math("x + y = 4"),
                        visual = Balance(leftX = 1, leftY = 1, rightOnes = 4),
                    ),
                    Numeric(
                        question = filled(Res.string.learn_t_eliminate, "2", "7", "4"),
                        answer = "3",
                        explanation = words(Res.string.learn_algebra_systems_elimination_s3_explanation),
                        visual = Balance(leftX = 2, leftY = 1, rightOnes = 7),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_systems_elimination_s4_body),
                        formula = math("3 + y = 4"),
                        visual = Balance(leftX = 0, leftY = 1, leftOnes = 3, rightOnes = 4),
                    ),
                    Numeric(
                        question = filled(Res.string.learn_t_substitute_y, "3", "4"),
                        formula = math("4 - 3 = ?"),
                        answer = "1",
                        explanation = words(Res.string.learn_algebra_systems_elimination_s5_explanation),
                        visual = Balance(leftX = 0, leftY = 1, leftOnes = 3, rightOnes = 4),
                    ),
                    Choice(
                        question = words(Res.string.learn_algebra_systems_elimination_s6_question),
                        options = wordOptions(
                            Res.string.learn_opt_subtract_the_two_rules,
                            Res.string.learn_opt_multiply_the_two_rules,
                            Res.string.learn_opt_add_the_two_rules,
                            Res.string.learn_opt_divide_the_two_rules,
                        ),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_algebra_systems_elimination_s6_explanation),
                        // No figure: the step asks which move to make, and a scale showing the
                        // equation that move would leave was answering a question nobody asked.
                        visual = null,
                    ),
                ),
            ),
            LessonSpec(
                id = "algebra-systems-substitution",
                title = Res.string.learn_algebra_systems_substitution_title,
                summary = Res.string.learn_algebra_systems_substitution_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_algebra_systems_substitution_s1_body),
                        formula = math("y = x + 1"),
                        visual = Plot(
                            curve = Curve.Linear(m = 1f, c = 1f),
                            second = Curve.Linear(m = -1f, c = 3f),
                        ),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_systems_substitution_s2_body),
                        formula = math("x + x + 1 = 3"),
                        visual = Balance(leftX = 2, leftOnes = 1, rightOnes = 3),
                    ),
                    Numeric(
                        question = words(Res.string.learn_algebra_systems_substitution_s3_question),
                        answer = "1",
                        explanation = words(Res.string.learn_algebra_systems_substitution_s3_explanation),
                        visual = Balance(leftX = 2, leftOnes = 1, rightOnes = 3),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_systems_substitution_s4_body),
                        formula = math("y = 2x"),
                        visual = Plot(
                            curve = Curve.Linear(m = 2f),
                            second = Curve.Linear(m = -1f, c = 3f),
                        ),
                    ),
                    Numeric(
                        question = filled(Res.string.learn_t_substitute_x, "2", "6"),
                        answer = "2",
                        explanation = words(Res.string.learn_algebra_systems_substitution_s5_explanation),
                        visual = Balance(leftX = 3, leftOnes = 0, rightOnes = 6),
                    ),
                    Numeric(
                        question = words(Res.string.learn_algebra_systems_substitution_s6_question),
                        formula = math("2 + 1 = ?"),
                        answer = "3",
                        explanation = words(Res.string.learn_algebra_systems_substitution_s6_explanation),
                        visual = Balance(leftX = 1, leftOnes = 0, rightOnes = 3),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = words(Res.string.learn_shared_where_lines_cross),
                options = mathOptions("(1, 2)", "(0, 3)", "(3, 0)", "(2, 1)"),
                correctIndex = 3,
                explanation = words(Res.string.learn_unit_algebra_simultaneous_equations_q1_explanation),
                visual = Plot(
                    curve = Curve.Linear(m = -1f, c = 3f),
                    second = Curve.Linear(m = 1f, c = -1f),
                    reveal = false,
                ),
            ),
            QuizQuestion(
                prompt = filled(Res.string.learn_t_eliminate, "3", "10", "6"),
                options = mathOptions("2", "4", "6", "3"),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_algebra_simultaneous_equations_q2_explanation),
                visual = Balance(leftX = 3, leftY = 1, rightOnes = 10),
            ),
            QuizQuestion(
                prompt = filled(Res.string.learn_t_substitute_x, "3", "8"),
                options = mathOptions("2", "4", "6", "8"),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_algebra_simultaneous_equations_q3_explanation),
                visual = Balance(leftX = 1, leftY = 1, rightOnes = 8),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_algebra_simultaneous_equations_q4_prompt),
                options = wordOptions(
                    Res.string.learn_opt_one_solution,
                    Res.string.learn_opt_no_solutions,
                    Res.string.learn_opt_two_solutions,
                    Res.string.learn_opt_infinitely_many,
                ),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_algebra_simultaneous_equations_q4_explanation),
                visual = Plot(
                    curve = Curve.Linear(m = 1f, c = 1f),
                    second = Curve.Linear(m = 1f, c = -1f),
                    reveal = false,
                ),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_algebra_simultaneous_equations_q5_prompt),
                options = mathOptions("2", "5", "1", "3"),
                correctIndex = 3,
                explanation = words(Res.string.learn_unit_algebra_simultaneous_equations_q5_explanation),
                visual = Balance(leftX = 1, leftY = 1, rightOnes = 5),
            ),
            QuizQuestion(
                prompt = filled(Res.string.learn_t_substitute_y, "4", "7"),
                options = mathOptions("4", "3", "7", "11"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_algebra_simultaneous_equations_q6_explanation),
                visual = Balance(leftX = 0, leftY = 1, leftOnes = 4, rightOnes = 7),
            ),
        ),
    )

    /**
     * Quadratics. Every curve is chosen so both roots and the vertex land inside the -3..3 window
     * `drawPlot` draws: `y = x² - 4` has its vertex a whole unit below the floor and renders as two
     * disconnected arms, which is not a parabola as far as the reader is concerned.
     */
    private val quadratics = learnUnit(
        topic = MathTopic.ALGEBRA,
        urlSlug = "quadratics",
        title = Res.string.learn_unit_algebra_quadratics_title,
        summary = Res.string.learn_unit_algebra_quadratics_summary,
        level = GradeLevel.GRADES_9_10,
        lessons = listOf(
            LessonSpec(
                id = "algebra-quadratics-parabola",
                title = Res.string.learn_algebra_quadratics_parabola_title,
                summary = Res.string.learn_algebra_quadratics_parabola_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_algebra_quadratics_parabola_s1_body),
                        formula = math("y = x²"),
                        visual = Plot(curve = Curve.Quadratic(a = 1f)),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_quadratics_parabola_s2_body),
                        formula = math("y = x² - 2"),
                        visual = Plot(curve = Curve.Quadratic(a = 1f, c = -2f), markVertex = true),
                    ),
                    Choice(
                        question = words(Res.string.learn_algebra_quadratics_parabola_s3_question),
                        options = mathOptions("1", "0", "2", "3"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_algebra_quadratics_parabola_s3_explanation),
                        visual = Plot(curve = Curve.Quadratic(a = 1f, c = -2f), reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_quadratics_parabola_s4_body),
                        formula = math("y = x² - 1"),
                        visual = Plot(curve = Curve.Quadratic(a = 1f, c = -1f), markRoots = true),
                    ),
                    Numeric(
                        question = words(Res.string.learn_algebra_quadratics_parabola_s5_question),
                        answer = "1",
                        explanation = words(Res.string.learn_shared_crossings_either_side),
                        visual = Plot(curve = Curve.Quadratic(a = 1f, c = -1f), reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_algebra_quadratics_parabola_s6_question),
                        options = mathOptions("y = x²", "y = x² + 1", "y = x² - 1", "y = -x²"),
                        correctIndex = 3,
                        explanation = words(Res.string.learn_algebra_quadratics_parabola_s6_explanation),
                        visual = Plot(curve = Curve.Quadratic(a = -1f, c = 1f), reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "algebra-quadratics-factorising",
                title = Res.string.learn_algebra_quadratics_factorising_title,
                summary = Res.string.learn_algebra_quadratics_factorising_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_algebra_quadratics_factorising_s1_body),
                        formula = math("(x + 2)(x + 3)"),
                        visual = AlgebraRect(leftX = 1, leftOnes = 2, topX = 1, topOnes = 3),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_quadratics_factorising_s2_body),
                        formula = math("x² + 5x + 6"),
                        visual = AlgebraRect(leftX = 1, leftOnes = 2, topX = 1, topOnes = 3),
                    ),
                    Choice(
                        question = filled(Res.string.learn_t_add_and_multiply, "5", "6"),
                        options = mathOptions("2 and 3", "1 and 5", "2 and 4", "1 and 6"),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_algebra_quadratics_factorising_s3_explanation),
                        visual = AlgebraRect(leftX = 1, leftOnes = 2, topX = 1, topOnes = 3, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_quadratics_factorising_s4_body),
                        formula = math("x² + 7x + 12 = (x + 3)(x + 4)"),
                        visual = AlgebraRect(leftX = 1, leftOnes = 3, topX = 1, topOnes = 4),
                    ),
                    Choice(
                        question = filled(Res.string.learn_t_factorise, "6", "8"),
                        options = mathOptions(
                            "(x + 1)(x + 8)",
                            "(x + 3)(x + 5)",
                            "(x + 2)(x + 4)",
                            "(x + 2)(x + 6)",
                        ),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_algebra_quadratics_factorising_s5_explanation),
                        visual = AlgebraRect(leftX = 1, leftOnes = 2, topX = 1, topOnes = 4, reveal = false),
                    ),
                    Numeric(
                        question = words(Res.string.learn_algebra_quadratics_factorising_s6_question),
                        answer = "5",
                        explanation = words(Res.string.learn_algebra_quadratics_factorising_s6_explanation),
                        visual = AlgebraRect(leftX = 1, leftOnes = 4, topX = 1, topOnes = 5, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "algebra-quadratics-solving",
                title = Res.string.learn_algebra_quadratics_solving_title,
                summary = Res.string.learn_algebra_quadratics_solving_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_algebra_quadratics_solving_s1_body),
                        formula = math("(x - 2)(x + 1) = 0"),
                        visual = Plot(curve = Curve.Quadratic(a = 1f, b = -1f, c = -2f)),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_quadratics_solving_s2_body),
                        formula = math("x = 2 or x = -1"),
                        visual = Plot(curve = Curve.Quadratic(a = 1f, b = -1f, c = -2f), markRoots = true),
                    ),
                    Choice(
                        question = filled(Res.string.learn_t_roots_of_brackets, "2", "1"),
                        options = mathOptions("-2 and 1", "2 and -1", "2 and 1", "-2 and -1"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_algebra_quadratics_solving_s3_explanation),
                        visual = Plot(curve = Curve.Quadratic(a = 1f, b = -1f, c = -2f), reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_quadratics_solving_s4_body),
                        formula = math("x² - x - 2 = 0"),
                        visual = Plot(curve = Curve.Quadratic(a = 1f, b = -1f, c = -2f), markRoots = true),
                    ),
                    Numeric(
                        question = words(Res.string.learn_algebra_quadratics_solving_s5_question),
                        answer = "2",
                        explanation = words(Res.string.learn_algebra_quadratics_solving_s5_explanation),
                        visual = Plot(curve = Curve.Quadratic(a = 1f, b = -1f, c = -2f), reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_algebra_quadratics_solving_s6_question),
                        options = mathOptions("1 and 0", "1 and -1", "-1 and 0", "2 and -2"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_algebra_quadratics_solving_s6_explanation),
                        visual = Plot(curve = Curve.Quadratic(a = 1f, c = -1f), reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = words(Res.string.learn_unit_algebra_quadratics_q1_prompt),
                options = mathOptions("1", "0", "2", "3"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_algebra_quadratics_q1_explanation),
                visual = Plot(curve = Curve.Quadratic(a = 1f, c = -1f), reveal = false),
            ),
            QuizQuestion(
                prompt = filled(Res.string.learn_t_factorise, "5", "6"),
                options = mathOptions(
                    "(x + 2)(x + 3)",
                    "(x + 1)(x + 6)",
                    "(x + 2)(x + 4)",
                    "(x + 5)(x + 1)",
                ),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_algebra_quadratics_q2_explanation),
                visual = AlgebraRect(leftX = 1, leftOnes = 2, topX = 1, topOnes = 3, reveal = false),
            ),
            QuizQuestion(
                prompt = filled(Res.string.learn_t_roots_of_brackets, "1", "2"),
                options = mathOptions("-1 and 2", "1 and 2", "-1 and -2", "1 and -2"),
                correctIndex = 3,
                explanation = words(Res.string.learn_unit_algebra_quadratics_q3_explanation),
                visual = Plot(curve = Curve.Quadratic(a = 1f, b = 1f, c = -2f), reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_algebra_quadratics_q4_prompt),
                options = wordOptions(
                    Res.string.learn_opt_makes_it_steeper,
                    Res.string.learn_opt_moves_it_up,
                    Res.string.learn_opt_moves_it_right,
                    Res.string.learn_opt_turns_it_over,
                ),
                correctIndex = 3,
                explanation = words(Res.string.learn_unit_algebra_quadratics_q4_explanation),
                visual = Plot(curve = Curve.Quadratic(a = -1f, c = 1f), reveal = false),
            ),
            QuizQuestion(
                prompt = filled(Res.string.learn_t_add_and_multiply, "7", "12"),
                options = mathOptions("3 and 4", "2 and 6", "1 and 12", "5 and 2"),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_algebra_quadratics_q5_explanation),
                visual = AlgebraRect(leftX = 1, leftOnes = 3, topX = 1, topOnes = 4, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_algebra_quadratics_q6_prompt),
                options = mathOptions("4", "1", "2", "3"),
                correctIndex = 2,
                explanation = words(Res.string.learn_shared_crossings_either_side),
                visual = Plot(curve = Curve.Quadratic(a = 0.5f, c = -2f), reveal = false),
            ),
        ),
    )

    private val powersAndRoots = learnUnit(
        topic = MathTopic.ALGEBRA,
        urlSlug = "powers-and-roots",
        title = Res.string.learn_unit_algebra_powers_and_roots_title,
        summary = Res.string.learn_unit_algebra_powers_and_roots_summary,
        level = GradeLevel.GRADES_9_10,
        lessons = listOf(
            LessonSpec(
                id = "algebra-indices-notation",
                title = Res.string.learn_algebra_indices_notation_title,
                summary = Res.string.learn_algebra_indices_notation_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_algebra_indices_notation_s1_body),
                        formula = math("2^4 = 2 * 2 * 2 * 2"),
                        // Ascending, because a Steps ladder lays its terms out in list order and
                        // the hop arrows point right whichever way the numbers go.
                        visual = Steps(terms = listOf(2, 4, 8, 16), multiply = true),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_indices_notation_s2_body),
                        formula = math("x²"),
                        visual = AlgebraRect(leftX = 1, leftOnes = 0, topX = 1, topOnes = 0),
                    ),
                    Numeric(
                        question = words(Res.string.learn_algebra_indices_notation_s3_question),
                        formula = math("3 * 3 = ?"),
                        answer = "9",
                        explanation = words(Res.string.learn_algebra_indices_notation_s3_explanation),
                        visual = AreaGrid(cols = 3, rows = 3, showArea = false, unit = ""),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_indices_notation_s4_body),
                        formula = math("x^2 * x^3 = x^5"),
                        visual = Steps(terms = listOf(2, 4, 8, 16, 32), multiply = true),
                    ),
                    Choice(
                        question = words(Res.string.learn_algebra_indices_notation_s5_question),
                        formula = math("x^4 * x^3 = ?"),
                        options = mathOptions("x^12", "x^1", "x^43", "x^7"),
                        correctIndex = 3,
                        explanation = words(Res.string.learn_algebra_indices_notation_s5_explanation),
                        visual = Steps(terms = listOf(3, 9, 27, 81), multiply = true),
                    ),
                    Choice(
                        question = words(Res.string.learn_algebra_indices_notation_s6_question),
                        formula = math("y^7 ÷ y^4 = ?"),
                        options = mathOptions("y^11", "y^3", "y^28", "y^2"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_shared_dividing_subtracts_counts),
                        visual = Steps(terms = listOf(2, 4, 8, 16), multiply = true),
                    ),
                ),
            ),
            LessonSpec(
                id = "algebra-indices-special",
                title = Res.string.learn_algebra_indices_special_title,
                summary = Res.string.learn_algebra_indices_special_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_algebra_indices_special_s1_body),
                        formula = math("2^3 = 8"),
                        // Descending, because every sentence in this lesson is about stepping *down*
                        // the ladder. The same terms climbing told the learner the opposite.
                        visual = Steps(terms = listOf(8, 4, 2), multiply = true),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_indices_special_s2_body),
                        formula = math("2^0 = 1"),
                        visual = Steps(terms = listOf(8, 4, 2, 1), multiply = true),
                    ),
                    Choice(
                        question = filled(Res.string.learn_t_zero_power, "7"),
                        options = mathOptions("0", "7", "1", "70"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_algebra_indices_special_s3_explanation),
                        visual = Steps(terms = listOf(343, 49, 7), multiply = true),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_indices_special_s4_body),
                        formula = math("2^-1 = 1/2"),
                        visual = Steps(terms = listOf<Number>(4, 2, 1, 0.5), multiply = true),
                    ),
                    Choice(
                        question = filled(Res.string.learn_t_negative_power, "2", "3"),
                        options = mathOptions("1/8", "-8", "-6", "8"),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_algebra_indices_special_s5_explanation),
                        visual = Steps(terms = listOf<Number>(2, 1, 0.5, 0.25), multiply = true),
                    ),
                    Numeric(
                        question = filled(Res.string.learn_t_zero_power, "5"),
                        answer = "1",
                        explanation = words(Res.string.learn_algebra_indices_special_s6_explanation),
                        visual = Steps(terms = listOf(125, 25, 5), multiply = true),
                    ),
                ),
            ),
            LessonSpec(
                id = "algebra-indices-roots",
                title = Res.string.learn_algebra_indices_roots_title,
                summary = Res.string.learn_algebra_indices_roots_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_algebra_indices_roots_s1_body),
                        formula = math("√16 = 4"),
                        visual = AreaGrid(cols = 4, rows = 4, showArea = false, unit = ""),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_indices_roots_s2_body),
                        formula = math("√(x²) = x"),
                        visual = AlgebraRect(leftX = 1, leftOnes = 0, topX = 1, topOnes = 0),
                    ),
                    Numeric(
                        question = filled(Res.string.learn_t_square_root, "25"),
                        answer = "5",
                        explanation = words(Res.string.learn_algebra_indices_roots_s3_explanation),
                        visual = AreaGrid(cols = 5, rows = 5, showArea = false, unit = "", showSides = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_algebra_indices_roots_s4_body),
                        formula = math("3^3 = 27"),
                        // The sentence is about a cube, so the figure is one. A power ladder said
                        // nothing about the shape the word "cube root" is named after.
                        visual = Solid(kind = SolidKind.CUBE),
                    ),
                    Choice(
                        question = filled(Res.string.learn_t_square_root, "49"),
                        options = mathOptions("24.5", "7", "98", "6"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_algebra_indices_roots_s5_explanation),
                        visual = AreaGrid(cols = 7, rows = 7, showArea = false, unit = "", showSides = false),
                    ),
                    Choice(
                        question = filled(Res.string.learn_t_square_root, "36"),
                        options = mathOptions("18", "72", "9", "6"),
                        correctIndex = 3,
                        explanation = words(Res.string.learn_algebra_indices_roots_s6_explanation),
                        visual = AreaGrid(cols = 6, rows = 6, showArea = false, unit = "", showSides = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = math("a^5 * a^4 = ?"),
                options = mathOptions("a^20", "a^9", "a^54", "a^1"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_algebra_powers_and_roots_q1_explanation),
                visual = Steps(terms = listOf(2, 4, 8, 16), multiply = true),
            ),
            QuizQuestion(
                prompt = filled(Res.string.learn_t_zero_power, "9"),
                options = mathOptions("0", "1", "9", "90"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_algebra_powers_and_roots_q2_explanation),
                visual = Steps(terms = listOf(1, 9, 81), multiply = true),
            ),
            QuizQuestion(
                prompt = math("y^8 ÷ y^5 = ?"),
                options = mathOptions("y^3", "y^13", "y^40", "y^2"),
                correctIndex = 0,
                explanation = words(Res.string.learn_shared_dividing_subtracts_counts),
                visual = Steps(terms = listOf(2, 4, 8, 16), multiply = true),
            ),
            QuizQuestion(
                prompt = filled(Res.string.learn_t_square_root, "64"),
                options = mathOptions("32", "16", "8", "6"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_algebra_powers_and_roots_q4_explanation),
                visual = AreaGrid(cols = 8, rows = 8, showArea = false, unit = "", showSides = false),
            ),
            QuizQuestion(
                prompt = filled(Res.string.learn_t_negative_power, "3", "2"),
                options = mathOptions("-9", "1/9", "-6", "9"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_algebra_powers_and_roots_q5_explanation),
                visual = Steps(terms = listOf(1, 3, 9, 27), multiply = true),
            ),
            QuizQuestion(
                prompt = filled(Res.string.learn_t_square_root, "100"),
                options = mathOptions("50", "1000", "20", "10"),
                correctIndex = 3,
                explanation = words(Res.string.learn_unit_algebra_powers_and_roots_q6_explanation),
                visual = AreaGrid(cols = 10, rows = 10, showArea = false, unit = "", showSides = false),
            ),
        ),
    )

    val units: List<LearnUnit> = listOf(
        expressions,
        linearEquations,
        straightLineGraphs,
        inequalities,
        simultaneousEquations,
        quadratics,
        powersAndRoots,
    )
}
