package com.inspiredandroid.braincup.learn.content

import com.inspiredandroid.braincup.learn.LearnLesson
import com.inspiredandroid.braincup.learn.LearnQuiz
import com.inspiredandroid.braincup.learn.LearnVisual
import com.inspiredandroid.braincup.learn.LessonStep
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.learn.QuizQuestion

internal object AlgebraContent {

    val lessons: List<LearnLesson> = listOf(
        LearnLesson(
            id = "algebra-expressions",
            topic = MathTopic.ALGEBRA,
            title = "Variables & Expressions",
            summary = "Letters as numbers you do not know yet — and how to tidy them up.",
            steps = listOf(
                LessonStep.Concept(
                    body = "A variable is a letter standing in for a number. An expression is " +
                        "built from terms; the number in front of a variable is its coefficient.",
                    formula = "5x + 3   →   term 5x (coefficient 5), term 3",
                ),
                LessonStep.Concept(
                    body = "Only like terms — same letter, same power — can be combined. Add " +
                        "their coefficients and keep the variable part unchanged.",
                    formula = "3x + 5x = 8x    but    3x + 5y stays as it is",
                ),
                LessonStep.Choice(
                    question = "Simplify 4a + 7 − a + 2",
                    options = listOf("5a + 9", "3a + 9", "3a + 5", "4a + 9"),
                    correctIndex = 1,
                    explanation = "4a − a = 3a and 7 + 2 = 9, giving 3a + 9. Note that −a means −1a.",
                ),
                LessonStep.Worked(
                    problem = "Evaluate 3x² − 2x when x = 4.",
                    lines = listOf(
                        "Substitute: 3(4)² − 2(4)",
                        "Powers first: 3(16) − 8",
                        "48 − 8",
                    ),
                    result = "40",
                ),
                LessonStep.Numeric(
                    question = "Evaluate 5(x + 3) when x = 2.",
                    answer = "25",
                    explanation = "Inside the bracket first: 2 + 3 = 5, then 5 × 5 = 25.",
                ),
                LessonStep.Choice(
                    question = "Expand 2(3x − 4)",
                    options = listOf("6x − 4", "6x − 8", "5x − 8", "6x + 8"),
                    correctIndex = 1,
                    explanation = "Multiply both terms inside the bracket: 2 × 3x = 6x and 2 × (−4) = −8.",
                ),
            ),
        ),
        LearnLesson(
            id = "algebra-linear-equations",
            topic = MathTopic.ALGEBRA,
            title = "Solving Linear Equations",
            summary = "Keeping the balance while you peel away everything around x.",
            steps = listOf(
                LessonStep.Concept(
                    body = "An equation says two things weigh the same. Whatever you do to one " +
                        "side you must do to the other, and the balance holds.",
                    formula = "if a = b then a + c = b + c and a ÷ c = b ÷ c",
                    visual = LearnVisual.BALANCE_SCALE,
                ),
                LessonStep.Worked(
                    problem = "Solve 3x + 5 = 20",
                    lines = listOf(
                        "Undo the +5: subtract 5 from both sides → 3x = 15",
                        "Undo the ×3: divide both sides by 3",
                    ),
                    result = "x = 5",
                ),
                LessonStep.Numeric(
                    question = "Solve 4x − 7 = 21. What is x?",
                    answer = "7",
                    explanation = "Add 7 to both sides: 4x = 28. Divide by 4: x = 7.",
                ),
                LessonStep.Choice(
                    question = "Solve 2(x + 3) = 16",
                    options = listOf("x = 5", "x = 8", "x = 11", "x = 6.5"),
                    correctIndex = 0,
                    explanation = "Divide both sides by 2 first: x + 3 = 8, so x = 5. Expanding to " +
                        "2x + 6 = 16 gets you there too.",
                ),
                LessonStep.Concept(
                    body = "With variables on both sides, first gather them on whichever side " +
                        "keeps the coefficient positive, then finish as usual.",
                    formula = "5x − 4 = 2x + 8  →  3x − 4 = 8  →  3x = 12",
                ),
                LessonStep.Choice(
                    question = "Solve 5x − 4 = 2x + 8",
                    options = listOf("x = 4", "x = 2", "x = 12", "x = 6"),
                    correctIndex = 0,
                    explanation = "Subtract 2x: 3x − 4 = 8. Add 4: 3x = 12. Divide by 3: x = 4.",
                ),
            ),
        ),
        LearnLesson(
            id = "algebra-quadratics",
            topic = MathTopic.ALGEBRA,
            title = "Quadratics & Factoring",
            summary = "The x² equation, its U-shaped graph, and two ways to solve it.",
            steps = listOf(
                LessonStep.Concept(
                    body = "A quadratic has an x² term as its highest power. Its graph is a " +
                        "parabola — a U that opens upward when a is positive and downward when a is negative.",
                    formula = "ax² + bx + c = 0,  a ≠ 0",
                    visual = LearnVisual.PARABOLA,
                ),
                LessonStep.Concept(
                    body = "To factor x² + bx + c, look for two numbers that multiply to c and " +
                        "add to b. They become the numbers inside the two brackets.",
                    formula = "x² + bx + c = (x + p)(x + q),  p·q = c,  p + q = b",
                ),
                LessonStep.Worked(
                    problem = "Factor x² + 7x + 12",
                    lines = listOf(
                        "Need two numbers with product 12 and sum 7",
                        "Try the factor pairs of 12: 1·12, 2·6, 3·4",
                        "3 + 4 = 7 ✓",
                    ),
                    result = "(x + 3)(x + 4)",
                ),
                LessonStep.Choice(
                    question = "Solve (x − 2)(x + 5) = 0",
                    options = listOf("x = 2 or x = −5", "x = −2 or x = 5", "x = 2 or x = 5", "x = 10"),
                    correctIndex = 0,
                    explanation = "A product is zero only when a factor is zero: x − 2 = 0 gives " +
                        "x = 2, and x + 5 = 0 gives x = −5.",
                ),
                LessonStep.Concept(
                    body = "When no neat factors exist, the quadratic formula always works. The " +
                        "part under the root — the discriminant — tells you how many real " +
                        "solutions there are: two if positive, one if zero, none if negative.",
                    formula = "x = (−b ± √(b² − 4ac)) / 2a",
                ),
                LessonStep.Numeric(
                    question = "Solve x² = 49. Give the positive solution.",
                    answer = "7",
                    explanation = "x = ±√49, so x = 7 or x = −7. A quadratic normally has two roots.",
                ),
            ),
        ),
    )

    val quiz = LearnQuiz(
        topic = MathTopic.ALGEBRA,
        questions = listOf(
            QuizQuestion(
                prompt = "Simplify 7y − 2y + 4",
                options = listOf("9y + 4", "5y + 4", "5y", "9y"),
                correctIndex = 1,
                explanation = "7y − 2y = 5y, and the 4 has no like term.",
            ),
            QuizQuestion(
                prompt = "Evaluate 2x² + 1 when x = 3.",
                options = listOf("19", "37", "13", "12"),
                correctIndex = 0,
                explanation = "2(9) + 1 = 19. Square before multiplying.",
            ),
            QuizQuestion(
                prompt = "Solve 6x + 4 = 34",
                options = listOf("x = 5", "x = 6", "x = 30", "x = 4"),
                correctIndex = 0,
                explanation = "6x = 30, so x = 5.",
            ),
            QuizQuestion(
                prompt = "Expand 3(2x + 5)",
                options = listOf("6x + 5", "5x + 8", "6x + 15", "6x + 8"),
                correctIndex = 2,
                explanation = "3 × 2x = 6x and 3 × 5 = 15.",
            ),
            QuizQuestion(
                prompt = "Solve 4x + 3 = x + 18",
                options = listOf("x = 3", "x = 5", "x = 7", "x = 15"),
                correctIndex = 1,
                explanation = "Subtract x: 3x + 3 = 18. Then 3x = 15 and x = 5.",
            ),
            QuizQuestion(
                prompt = "Factor x² + 5x + 6",
                options = listOf("(x + 1)(x + 6)", "(x + 2)(x + 3)", "(x − 2)(x − 3)", "(x + 5)(x + 1)"),
                correctIndex = 1,
                explanation = "2 × 3 = 6 and 2 + 3 = 5.",
            ),
            QuizQuestion(
                prompt = "What are the solutions of (x + 4)(x − 1) = 0?",
                options = listOf("x = 4 or x = −1", "x = −4 or x = 1", "x = −4 or x = −1", "x = 3"),
                correctIndex = 1,
                explanation = "Set each factor to zero: x = −4 and x = 1.",
            ),
            QuizQuestion(
                prompt = "In x² − 4x + 4 = 0, the discriminant b² − 4ac equals…",
                options = listOf("0", "8", "−16", "32"),
                correctIndex = 0,
                explanation = "16 − 16 = 0, so the equation has exactly one repeated root, x = 2.",
            ),
        ),
    )
}
