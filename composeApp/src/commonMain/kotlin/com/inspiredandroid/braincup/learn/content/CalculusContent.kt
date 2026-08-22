package com.inspiredandroid.braincup.learn.content

import com.inspiredandroid.braincup.learn.LearnLesson
import com.inspiredandroid.braincup.learn.LearnQuiz
import com.inspiredandroid.braincup.learn.LearnVisual
import com.inspiredandroid.braincup.learn.LessonStep
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.learn.QuizQuestion

internal object CalculusContent {

    val lessons: List<LearnLesson> = listOf(
        LearnLesson(
            id = "calculus-limits",
            topic = MathTopic.CALCULUS,
            title = "Limits & Continuity",
            summary = "What a function heads towards, even where it never arrives.",
            steps = listOf(
                LessonStep.Concept(
                    body = "A limit describes the value a function approaches as the input closes " +
                        "in on a point. It says nothing about the value at that point — which is " +
                        "exactly why it is useful where the function is undefined.",
                    formula = "lim(x→a) f(x) = L",
                ),
                LessonStep.Choice(
                    question = "What is lim(x→3) (2x + 1)?",
                    options = listOf("6", "7", "3", "does not exist"),
                    correctIndex = 1,
                    explanation = "The function is continuous there, so you can simply substitute: " +
                        "2(3) + 1 = 7.",
                ),
                LessonStep.Worked(
                    problem = "Evaluate lim(x→2) (x² − 4)/(x − 2)",
                    lines = listOf(
                        "Substituting gives 0/0 — indeterminate, so do not stop there",
                        "Factor the top: (x − 2)(x + 2)/(x − 2)",
                        "Cancel the common factor, valid because x ≠ 2 on the approach: x + 2",
                        "Now substitute: 2 + 2",
                    ),
                    result = "4",
                ),
                LessonStep.Concept(
                    body = "A function is continuous where its graph has no jump, hole or blow-up " +
                        "— the pen never leaves the paper. Formally, the limit exists and equals " +
                        "the actual value at the point.",
                    formula = "continuous at a  ⟺  lim(x→a) f(x) = f(a)",
                ),
                LessonStep.Numeric(
                    question = "What is lim(x→0) (sin x)/x?",
                    answer = "1",
                    explanation = "Substituting gives 0/0, but the ratio approaches 1 — the limit " +
                        "behind the derivative of sine.",
                ),
                LessonStep.Choice(
                    question = "A graph with a single hole at x = 1 is…",
                    options = listOf("continuous everywhere", "discontinuous at x = 1", "undefined everywhere", "not a function"),
                    correctIndex = 1,
                    explanation = "The limit may exist at x = 1, but the function has no value " +
                        "there, so continuity fails at that one point.",
                ),
            ),
        ),
        LearnLesson(
            id = "calculus-derivatives",
            topic = MathTopic.CALCULUS,
            title = "Derivatives",
            summary = "The slope of the tangent — how fast something is changing right now.",
            steps = listOf(
                LessonStep.Concept(
                    body = "The slope between two points on a curve is an average rate of change. " +
                        "Slide the second point towards the first and the chord becomes the " +
                        "tangent: the instantaneous rate of change, the derivative.",
                    formula = "f'(x) = lim(h→0) [f(x + h) − f(x)] / h",
                    visual = LearnVisual.TANGENT_LINE,
                ),
                LessonStep.Concept(
                    body = "You rarely use that limit directly. The power rule handles polynomials: " +
                        "bring the exponent down in front and reduce it by one. Constants have " +
                        "slope zero, and sums differentiate term by term.",
                    formula = "d/dx xⁿ = n·xⁿ⁻¹    d/dx c = 0",
                ),
                LessonStep.Worked(
                    problem = "Differentiate f(x) = 3x⁴ − 5x + 2",
                    lines = listOf(
                        "3x⁴ → 3 · 4x³ = 12x³",
                        "−5x → −5 (since x¹ becomes 1)",
                        "2 → 0, a constant never changes",
                    ),
                    result = "f'(x) = 12x³ − 5",
                ),
                LessonStep.Numeric(
                    question = "If f(x) = x³, what is f'(2)?",
                    answer = "12",
                    explanation = "f'(x) = 3x², so f'(2) = 3 × 4 = 12.",
                ),
                LessonStep.Choice(
                    question = "What is the derivative of f(x) = 7x² ?",
                    options = listOf("7x", "14x", "14x²", "7"),
                    correctIndex = 1,
                    explanation = "Bring down the 2: 7 · 2x¹ = 14x.",
                ),
                LessonStep.Choice(
                    question = "Where is the derivative of a smooth curve equal to zero?",
                    options = listOf("at the y-intercept", "at a maximum or minimum", "at every point", "only at x = 0"),
                    correctIndex = 1,
                    explanation = "At a peak or a trough the tangent is horizontal, so the slope " +
                        "is zero — which is how calculus finds optimal values.",
                ),
            ),
        ),
        LearnLesson(
            id = "calculus-integrals",
            topic = MathTopic.CALCULUS,
            title = "Integrals",
            summary = "Differentiation run backwards, and the area it measures.",
            steps = listOf(
                LessonStep.Concept(
                    body = "Integration undoes differentiation. Because every constant " +
                        "differentiates to zero, an indefinite integral always carries \"+ C\": " +
                        "there is a whole family of antiderivatives.",
                    formula = "∫xⁿ dx = xⁿ⁺¹/(n + 1) + C,  n ≠ −1",
                    visual = LearnVisual.AREA_UNDER_CURVE,
                ),
                LessonStep.Choice(
                    question = "What is ∫2x dx?",
                    options = listOf("x² + C", "2x² + C", "2 + C", "x²/2 + C"),
                    correctIndex = 0,
                    explanation = "Raise the power and divide: 2x²/2 = x². Check by differentiating: " +
                        "d/dx (x²) = 2x ✓.",
                ),
                LessonStep.Concept(
                    body = "A definite integral measures the signed area between the curve and the " +
                        "x-axis. The fundamental theorem of calculus turns that area into simple " +
                        "subtraction: find any antiderivative and evaluate it at both ends.",
                    formula = "∫ₐᵇ f(x) dx = F(b) − F(a)",
                ),
                LessonStep.Worked(
                    problem = "Evaluate ∫₀³ 2x dx",
                    lines = listOf(
                        "An antiderivative of 2x is x²",
                        "Evaluate at the ends: 3² − 0²",
                        "The C cancels, which is why definite integrals do not need it",
                    ),
                    result = "9",
                ),
                LessonStep.Numeric(
                    question = "Evaluate ∫₀² 3x² dx",
                    answer = "8",
                    explanation = "An antiderivative is x³, so the value is 2³ − 0³ = 8.",
                ),
                LessonStep.Choice(
                    question = "If s(t) is position and v(t) is velocity, then ∫v(t) dt gives…",
                    options = listOf("acceleration", "position", "speed at an instant", "the slope of v"),
                    correctIndex = 1,
                    explanation = "Velocity is the derivative of position, so integrating velocity " +
                        "returns position (up to the starting value, the + C).",
                ),
            ),
        ),
    )

    val quiz = LearnQuiz(
        topic = MathTopic.CALCULUS,
        questions = listOf(
            QuizQuestion(
                prompt = "What is lim(x→4) (3x − 2)?",
                options = listOf("10", "12", "4", "does not exist"),
                correctIndex = 0,
                explanation = "Continuous there, so substitute: 3(4) − 2 = 10.",
            ),
            QuizQuestion(
                prompt = "What is the derivative of f(x) = x⁵?",
                options = listOf("5x⁴", "x⁴", "5x⁶", "4x⁵"),
                correctIndex = 0,
                explanation = "Power rule: bring down the 5, reduce the exponent to 4.",
            ),
            QuizQuestion(
                prompt = "What is the derivative of f(x) = 9?",
                options = listOf("9", "9x", "0", "1"),
                correctIndex = 2,
                explanation = "A constant function never changes, so its slope is 0.",
            ),
            QuizQuestion(
                prompt = "If f(x) = 2x² + 3x, what is f'(1)?",
                options = listOf("5", "7", "4", "3"),
                correctIndex = 1,
                explanation = "f'(x) = 4x + 3, so f'(1) = 7.",
            ),
            QuizQuestion(
                prompt = "What is ∫3x² dx?",
                options = listOf("6x + C", "x³ + C", "3x³ + C", "x³/3 + C"),
                correctIndex = 1,
                explanation = "3x³/3 = x³. Differentiating x³ gives 3x² ✓.",
            ),
            QuizQuestion(
                prompt = "What is ∫₀¹ 2x dx?",
                options = listOf("1", "2", "0.5", "0"),
                correctIndex = 0,
                explanation = "The antiderivative x² evaluated from 0 to 1 gives 1 − 0 = 1.",
            ),
            QuizQuestion(
                prompt = "A definite integral of a positive function measures…",
                options = listOf("the slope at a point", "the area under the curve", "the maximum value", "the average of the endpoints"),
                correctIndex = 1,
                explanation = "That is the geometric meaning of the definite integral.",
            ),
            QuizQuestion(
                prompt = "lim(x→1) (x² − 1)/(x − 1) equals…",
                options = listOf("0", "1", "2", "undefined"),
                correctIndex = 2,
                explanation = "Factor to (x + 1)(x − 1)/(x − 1) = x + 1, then substitute: 2.",
            ),
        ),
    )
}
