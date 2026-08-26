package com.inspiredandroid.braincup.learn.content

import com.inspiredandroid.braincup.learn.Curve
import com.inspiredandroid.braincup.learn.GradeLevel
import com.inspiredandroid.braincup.learn.LearnUnit
import com.inspiredandroid.braincup.learn.LearnVisual.Plot
import com.inspiredandroid.braincup.learn.LessonSpec
import com.inspiredandroid.braincup.learn.LessonStep.Choice
import com.inspiredandroid.braincup.learn.LessonStep.Concept
import com.inspiredandroid.braincup.learn.LessonStep.Numeric
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.learn.PlotPoint
import com.inspiredandroid.braincup.learn.QuizQuestion
import com.inspiredandroid.braincup.learn.learnUnit

/** Calculus: limits, the derivative as a gradient and the integral as an area. */
internal object CalculusContent {

    private val limitsAndDerivatives = learnUnit(
        topic = MathTopic.CALCULUS,
        urlSlug = "limits-and-derivatives",
        title = "Limits, derivatives and integrals",
        summary = "Limits, derivatives as gradients, and integrals as area.",
        level = GradeLevel.GRADES_11_12,
        lessons = listOf(
            LessonSpec(
                id = "g1112-calculus-limits",
                title = "Limits",
                summary = "What a function approaches.",
                steps = listOf(
                    Concept(
                        body = "A limit says where a function is heading, even if it never arrives.",
                        formula = "(x² - 4)/(x - 2) approaches 4 as x approaches 2",
                        visual = Plot(
                            curve = Curve.Linear(m = 1f, c = 2f),
                            points = listOf(PlotPoint(x = 2f, y = 4f, label = "hole")),
                        ),
                    ),
                    Concept(
                        body = "At x = 2 the fraction reads 0/0, but cancelling leaves x + 2, which clearly heads for 4.",
                        visual = Plot(curve = Curve.Linear(m = 1f, c = 2f)),
                    ),
                    Choice(
                        question = "What does (x² - 9)/(x - 3) approach as x approaches 3?",
                        options = listOf("0", "3", "6", "undefined"),
                        correctIndex = 2,
                        explanation = "Cancel to x + 3.",
                        visual = Plot(curve = Curve.Linear(m = 1f, c = 3f)),
                    ),
                    Concept(
                        body = "Continuous means you could draw it without lifting the pen.",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f, c = -1f)),
                    ),
                    Numeric(
                        question = "What does 1/x approach as x grows without bound?",
                        answer = "0",
                        explanation = "Dividing by ever larger numbers shrinks towards zero.",
                        visual = Plot(curve = Curve.Exponential(base = 0.4f)),
                    ),
                    Choice(
                        question = "Why do limits matter in calculus?",
                        options = listOf(
                            "they replace algebra",
                            "both the derivative and the integral are defined as limits",
                            "they only apply to circles",
                            "they are quicker",
                        ),
                        correctIndex = 1,
                        explanation = "Everything else is built on top of them.",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f), tangentAt = 1.5f),
                    ),
                ),
            ),
            LessonSpec(
                id = "g1112-calculus-derivatives",
                title = "Derivatives",
                summary = "The gradient of the tangent.",
                steps = listOf(
                    Concept(
                        body = "The derivative is the gradient of the tangent touching the curve.",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f, c = -1f), tangentAt = 1.5f),
                    ),
                    Concept(
                        body = "Move along the curve and the tangent tilts: the gradient is itself a function.",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f, c = -1f), tangentAt = -2f),
                    ),
                    Concept(
                        body = "For powers, bring the index down and drop it by one.",
                        formula = "d/dx of x^n is n x^(n-1)",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f), second = Curve.Linear(m = 1f)),
                    ),
                    Choice(
                        question = "Differentiate y = x³",
                        options = listOf("3x", "x²", "3x²", "3x⁴"),
                        correctIndex = 2,
                        explanation = "Bring the 3 down, reduce the power to 2.",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f), tangentAt = 2f),
                    ),
                    Numeric(
                        question = "If f(x) = x², what is f'(3)?",
                        answer = "6",
                        explanation = "f'(x) = 2x.",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f), tangentAt = 2f),
                    ),
                    Choice(
                        question = "At the very bottom of this curve, the gradient is...",
                        options = listOf("at its largest", "zero", "undefined", "negative"),
                        correctIndex = 1,
                        explanation = "The tangent lies flat at a turning point.",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f, c = -2f), tangentAt = 0f),
                    ),
                ),
            ),
            LessonSpec(
                id = "g1112-calculus-integrals",
                title = "Integrals",
                summary = "Area, slice by slice.",
                steps = listOf(
                    Concept(
                        body = "Integration adds up infinitely thin slices - the area under the curve.",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f), areaTo = 2f),
                    ),
                    Concept(
                        body = "Integration undoes differentiation. That link is the Fundamental Theorem.",
                        formula = "integral of x^n is x^(n+1)/(n+1) + C",
                        visual = Plot(curve = Curve.Linear(m = 1f), areaTo = 2f),
                    ),
                    Choice(
                        question = "Integrate 2x",
                        options = listOf("2", "x² + C", "2x² + C", "x + C"),
                        correctIndex = 1,
                        explanation = "Differentiating x² gives 2x.",
                        visual = Plot(curve = Curve.Linear(m = 2f), areaTo = 1.5f),
                    ),
                    Concept(
                        body = "The + C appears because every constant differentiates to zero.",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f, c = -1f), second = Curve.Quadratic(a = 0.5f, c = 1f)),
                    ),
                    Numeric(
                        question = "The area under y = 2x from 0 to 3 is a triangle. What is it?",
                        answer = "9",
                        explanation = "Half of 3 x 6, and x² gives the same.",
                        visual = Plot(curve = Curve.Linear(m = 2f), areaTo = 1.5f),
                    ),
                    Choice(
                        question = "The area under a velocity-time graph represents...",
                        options = listOf("acceleration", "distance travelled", "speed", "elapsed time"),
                        correctIndex = 1,
                        explanation = "Velocity times time is distance, accumulated slice by slice.",
                        visual = Plot(curve = Curve.Linear(m = 1f, c = 1f), areaTo = 2.5f),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "Differentiate y = x⁴",
                options = listOf("4x", "x³", "4x³", "4x⁵"),
                correctIndex = 2,
                explanation = "Bring the power down, reduce by one.",
                visual = Plot(curve = Curve.Quadratic(a = 0.5f), tangentAt = 2f),
            ),
            QuizQuestion(
                prompt = "If f(x) = 3x², what is f'(x)?",
                options = listOf("3x", "6x", "6x²", "x³"),
                correctIndex = 1,
                explanation = "The 2 comes down and multiplies the 3.",
                visual = Plot(curve = Curve.Quadratic(a = 0.8f), tangentAt = 1.5f),
            ),
            QuizQuestion(
                prompt = "At the turning point of this curve, the derivative is...",
                options = listOf("at its largest", "zero", "negative", "undefined"),
                correctIndex = 1,
                explanation = "The tangent is horizontal there.",
                visual = Plot(curve = Curve.Quadratic(a = 0.5f, c = -2f), tangentAt = 0f),
            ),
            QuizQuestion(
                prompt = "Integrate 3x²",
                options = listOf("6x + C", "x³ + C", "3x³ + C", "x² + C"),
                correctIndex = 1,
                explanation = "Raise the power and divide by it.",
                visual = Plot(curve = Curve.Quadratic(a = 0.6f), areaTo = 2f),
            ),
            QuizQuestion(
                prompt = "What does (x² - 1)/(x - 1) approach as x approaches 1?",
                options = listOf("0", "1", "2", "undefined"),
                correctIndex = 2,
                explanation = "Cancel to x + 1.",
                visual = Plot(curve = Curve.Linear(m = 1f, c = 1f)),
            ),
            QuizQuestion(
                prompt = "The shaded area under a velocity-time graph gives...",
                options = listOf("acceleration", "distance", "speed", "force"),
                correctIndex = 1,
                explanation = "Velocity accumulated through time.",
                visual = Plot(curve = Curve.Linear(m = 1f, c = 1f), areaTo = 2.5f),
            ),
        ),
    )

    val units: List<LearnUnit> = listOf(limitsAndDerivatives)
}
