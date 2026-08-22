package com.inspiredandroid.braincup.learn.content

import com.inspiredandroid.braincup.learn.LearnLesson
import com.inspiredandroid.braincup.learn.LearnQuiz
import com.inspiredandroid.braincup.learn.LearnVisual
import com.inspiredandroid.braincup.learn.LessonStep
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.learn.QuizQuestion

internal object FunctionsContent {

    val lessons: List<LearnLesson> = listOf(
        LearnLesson(
            id = "functions-domain-range",
            topic = MathTopic.FUNCTIONS,
            title = "Functions, Domain & Range",
            summary = "A machine with one output per input — and what you are allowed to feed it.",
            steps = listOf(
                LessonStep.Concept(
                    body = "A function is a rule that turns each input into exactly one output. " +
                        "The notation f(x) names the rule f and the input x; f(3) means \"run the " +
                        "rule on 3\".",
                    formula = "f(x) = 2x + 1  →  f(3) = 2(3) + 1 = 7",
                ),
                LessonStep.Choice(
                    question = "For f(x) = 2x + 1, what is f(3)?",
                    options = listOf("5", "7", "6", "8"),
                    correctIndex = 1,
                    explanation = "Substitute 3 for x: 2(3) + 1 = 7.",
                ),
                LessonStep.Concept(
                    body = "The domain is every input the rule accepts; the range is every output " +
                        "it can produce. Two things break a rule: dividing by zero, and taking " +
                        "the square root of a negative number.",
                ),
                LessonStep.Choice(
                    question = "What is the domain of f(x) = 1/(x − 2)?",
                    options = listOf("all real x", "x ≠ 0", "x ≠ 2", "x > 2"),
                    correctIndex = 2,
                    explanation = "At x = 2 the denominator is zero, which is undefined. Every " +
                        "other real number is fine.",
                ),
                LessonStep.Worked(
                    problem = "Find the range of f(x) = x² over all real x.",
                    lines = listOf(
                        "Any real number squared is zero or positive",
                        "The value 0 is reached, at x = 0",
                        "Every positive number is reached too, by its square root",
                    ),
                    result = "range: y ≥ 0",
                ),
                LessonStep.Numeric(
                    question = "For f(x) = x² − 4, what is f(5)?",
                    answer = "21",
                    explanation = "25 − 4 = 21.",
                ),
            ),
        ),
        LearnLesson(
            id = "functions-transformations",
            topic = MathTopic.FUNCTIONS,
            title = "Transformations & Inverses",
            summary = "Sliding and stretching a graph, then running the rule backwards.",
            steps = listOf(
                LessonStep.Concept(
                    body = "Changing a function's formula moves its graph in predictable ways. " +
                        "Changes outside the function act on the output and move it vertically; " +
                        "changes inside act on the input and move it horizontally — the opposite " +
                        "way to the sign you see.",
                    formula = "f(x) + k: up k    f(x − k): right k    a·f(x): stretch    −f(x): flip",
                    visual = LearnVisual.PARABOLA,
                ),
                LessonStep.Choice(
                    question = "How does the graph of y = x² + 3 compare with y = x²?",
                    options = listOf("3 units right", "3 units up", "3 units down", "3 units left"),
                    correctIndex = 1,
                    explanation = "The +3 is applied after squaring, so every output rises by 3.",
                ),
                LessonStep.Choice(
                    question = "How does the graph of y = (x − 2)² compare with y = x²?",
                    options = listOf("2 units left", "2 units right", "2 units up", "2 units down"),
                    correctIndex = 1,
                    explanation = "The change is inside the bracket, so it shifts horizontally — " +
                        "and against the sign. The vertex moves from x = 0 to x = 2.",
                ),
                LessonStep.Concept(
                    body = "The inverse function undoes what the function did. Swap x and y, " +
                        "solve for y, and the new graph is the old one reflected in the line " +
                        "y = x. Only functions that never repeat an output can be inverted.",
                    formula = "f⁻¹(f(x)) = x",
                ),
                LessonStep.Worked(
                    problem = "Find the inverse of f(x) = 3x + 6.",
                    lines = listOf(
                        "Write it as y = 3x + 6",
                        "Swap the variables: x = 3y + 6",
                        "Solve for y: 3y = x − 6",
                    ),
                    result = "f⁻¹(x) = (x − 6)/3",
                ),
                LessonStep.Numeric(
                    question = "If f(x) = 2x + 1, what is f⁻¹(9)?",
                    answer = "4",
                    explanation = "Ask which input gives 9: 2x + 1 = 9, so x = 4.",
                ),
            ),
        ),
        LearnLesson(
            id = "functions-exponentials-logs",
            topic = MathTopic.FUNCTIONS,
            title = "Exponentials & Logarithms",
            summary = "Growth by repeated multiplying, and the function that reads the exponent back.",
            steps = listOf(
                LessonStep.Concept(
                    body = "A linear function adds the same amount each step; an exponential " +
                        "multiplies by the same factor each step. That is why it starts slowly " +
                        "and then runs away. A base above 1 grows, a base between 0 and 1 decays.",
                    formula = "y = a · bˣ",
                ),
                LessonStep.Concept(
                    body = "A logarithm answers the question \"what exponent do I need?\". It is " +
                        "the inverse of the exponential with the same base.",
                    formula = "log_b(y) = x  ⟺  bˣ = y",
                ),
                LessonStep.Choice(
                    question = "What is log₂ 8?",
                    options = listOf("2", "3", "4", "16"),
                    correctIndex = 1,
                    explanation = "It asks: 2 to what power is 8? Since 2³ = 8, the answer is 3.",
                ),
                LessonStep.Worked(
                    problem = "A colony of 100 bacteria doubles every hour. How many after 5 hours?",
                    lines = listOf(
                        "y = 100 × 2ˣ with x = 5",
                        "2⁵ = 32",
                        "100 × 32",
                    ),
                    result = "3200",
                ),
                LessonStep.Numeric(
                    question = "What is log₁₀ 1000?",
                    answer = "3",
                    explanation = "10³ = 1000.",
                ),
                LessonStep.Choice(
                    question = "Which rule is correct?",
                    options = listOf("log(ab) = log a · log b", "log(ab) = log a + log b", "log(ab) = log a − log b", "log(ab) = (log a)ᵇ"),
                    correctIndex = 1,
                    explanation = "Logs turn multiplication into addition — the property that made " +
                        "slide rules work. Division becomes subtraction.",
                ),
            ),
        ),
    )

    val quiz = LearnQuiz(
        topic = MathTopic.FUNCTIONS,
        questions = listOf(
            QuizQuestion(
                prompt = "For f(x) = 3x − 4, what is f(6)?",
                options = listOf("14", "18", "22", "10"),
                correctIndex = 0,
                explanation = "3(6) − 4 = 18 − 4 = 14.",
            ),
            QuizQuestion(
                prompt = "What is the domain of f(x) = √x over the real numbers?",
                options = listOf("all real x", "x ≥ 0", "x > 0", "x ≠ 0"),
                correctIndex = 1,
                explanation = "Negative numbers have no real square root, but √0 = 0 is fine.",
            ),
            QuizQuestion(
                prompt = "The graph of y = x² − 5 is the graph of y = x²…",
                options = listOf("5 up", "5 down", "5 left", "5 right"),
                correctIndex = 1,
                explanation = "Subtracting outside the function lowers every output by 5.",
            ),
            QuizQuestion(
                prompt = "The graph of y = (x + 3)² is the graph of y = x²…",
                options = listOf("3 left", "3 right", "3 up", "3 down"),
                correctIndex = 0,
                explanation = "Inside the bracket, so horizontal — and against the sign: 3 to the left.",
            ),
            QuizQuestion(
                prompt = "If f(x) = x/2 + 1, what is f⁻¹(5)?",
                options = listOf("3.5", "8", "11", "6"),
                correctIndex = 1,
                explanation = "Solve x/2 + 1 = 5 → x/2 = 4 → x = 8.",
            ),
            QuizQuestion(
                prompt = "What is log₃ 81?",
                options = listOf("3", "4", "9", "27"),
                correctIndex = 1,
                explanation = "3⁴ = 81.",
            ),
            QuizQuestion(
                prompt = "An investment of 500 grows by 10% a year. Which formula gives its value after x years?",
                options = listOf("500 + 10x", "500 · 1.1ˣ", "500 · 0.1ˣ", "500ˣ · 1.1"),
                correctIndex = 1,
                explanation = "Each year multiplies by 1.1, so the growth is exponential, not linear.",
            ),
            QuizQuestion(
                prompt = "Which pair of functions are inverses?",
                options = listOf("x² and 2x", "x + 5 and x − 5", "x + 5 and 5 − x", "2x and x²"),
                correctIndex = 1,
                explanation = "Adding 5 and then subtracting 5 returns the original input.",
            ),
        ),
    )
}
