package com.inspiredandroid.braincup.learn.content

import com.inspiredandroid.braincup.learn.Curve
import com.inspiredandroid.braincup.learn.GradeLevel
import com.inspiredandroid.braincup.learn.LearnUnit
import com.inspiredandroid.braincup.learn.LearnVisual.BarChart
import com.inspiredandroid.braincup.learn.LearnVisual.Plot
import com.inspiredandroid.braincup.learn.LearnVisual.Steps
import com.inspiredandroid.braincup.learn.LessonSpec
import com.inspiredandroid.braincup.learn.LessonStep.Choice
import com.inspiredandroid.braincup.learn.LessonStep.Concept
import com.inspiredandroid.braincup.learn.LessonStep.Numeric
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.learn.PlotPoint
import com.inspiredandroid.braincup.learn.QuizQuestion
import com.inspiredandroid.braincup.learn.learnUnit

/** Pre-calculus: function notation and graphs, then exponentials, logs and sequences. */
internal object FunctionsContent {

    private val functionsAndGraphs = learnUnit(
        topic = MathTopic.FUNCTIONS,
        urlSlug = "functions-and-graphs",
        title = "Function notation and graphs",
        summary = "Function notation, linear and quadratic graphs, and transformations.",
        level = GradeLevel.GRADES_9_10,
        lessons = listOf(
            LessonSpec(
                id = "g910-functions-notation",
                title = "Function notation",
                summary = "One input, exactly one output.",
                steps = listOf(
                    Concept(
                        body = "A function is a machine: put a number in, one number comes out.",
                        formula = "f(x) = x + 1",
                        visual = Plot(
                            curve = Curve.Linear(m = 1f, c = 1f),
                            points = listOf(PlotPoint(x = 1f, y = 2f, label = "f(1) = 2")),
                        ),
                    ),
                    Concept(
                        body = "f(2) does not mean f times 2. It is the output at input 2.",
                        visual = Plot(
                            curve = Curve.Linear(m = 1f, c = 1f),
                            points = listOf(PlotPoint(x = 2f, y = 3f, label = "f(2) = 3")),
                        ),
                    ),
                    Numeric(
                        question = "If f(x) = x² - 1, what is f(4)?",
                        answer = "15",
                        explanation = "16 - 1.",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f, c = -1f)),
                    ),
                    Concept(
                        body = "The domain is the inputs allowed; the range is the outputs produced.",
                        visual = Plot(curve = Curve.Linear(m = 1f)),
                    ),
                    Choice(
                        question = "Which of these is NOT a function?",
                        options = listOf("y = x²", "y = 3x + 1", "x = y²", "y = 1/x"),
                        correctIndex = 2,
                        explanation = "x = y² gives two y values for most x.",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f)),
                    ),
                    Choice(
                        question = "For f(x) = 5 - x, what is f(-2)?",
                        options = listOf("3", "7", "-7", "-3"),
                        correctIndex = 1,
                        explanation = "5 - (-2).",
                        visual = Plot(curve = Curve.Linear(m = -1f, c = 2f)),
                    ),
                ),
            ),
            LessonSpec(
                id = "g910-functions-linear-quadratic",
                title = "Linear and quadratic graphs",
                summary = "Straight lines, parabolas, vertices and roots.",
                steps = listOf(
                    Concept(
                        body = "A line rises at a constant rate. A parabola's rate keeps changing.",
                        visual = Plot(curve = Curve.Linear(m = 1f), second = Curve.Quadratic(a = 0.5f, c = -2f)),
                    ),
                    Concept(
                        body = "The vertex sits on the line of symmetry.",
                        formula = "x = -b / 2a",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f, b = -1f, c = -1f), markVertex = true),
                    ),
                    Choice(
                        question = "Which way does this parabola open?",
                        options = listOf("upwards", "downwards", "sideways", "it is a straight line"),
                        correctIndex = 1,
                        explanation = "A negative x² coefficient flips it.",
                        visual = Plot(curve = Curve.Quadratic(a = -0.5f, c = 2f)),
                    ),
                    Numeric(
                        question = "Find the x-coordinate of this vertex.",
                        answer = "1",
                        explanation = "-b / 2a with a = 0.5 and b = -1.",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f, b = -1f, c = -1f)),
                    ),
                    Concept(
                        body = "Where a graph crosses the x-axis, y is zero. Those are the roots.",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f, c = -2f), markRoots = true),
                    ),
                    Choice(
                        question = "How many times does this curve cross the x-axis?",
                        options = listOf("never", "once", "twice", "infinitely often"),
                        correctIndex = 0,
                        explanation = "It sits entirely above the axis.",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f, c = 1f)),
                    ),
                ),
            ),
            LessonSpec(
                id = "g910-functions-transformations",
                title = "Transforming graphs",
                summary = "Four moves for every graph.",
                steps = listOf(
                    Concept(
                        body = "Change the formula and the graph moves in a predictable way.",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f)),
                    ),
                    Concept(
                        body = "Adding outside lifts the whole curve.",
                        formula = "f(x) + 1.5",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f), second = Curve.Quadratic(a = 0.5f, c = 1.5f)),
                    ),
                    Choice(
                        question = "The green curve is y = (x - 2)². How has it moved?",
                        options = listOf("2 to the left", "2 to the right", "2 up", "2 down"),
                        correctIndex = 1,
                        explanation = "Inside changes move the opposite way to their sign.",
                        visual = Plot(
                            curve = Curve.Quadratic(a = 0.5f),
                            second = Curve.Quadratic(a = 0.5f, b = -2f, c = 2f),
                        ),
                    ),
                    Concept(
                        body = "A minus outside flips the curve in the x-axis.",
                        formula = "-f(x)",
                        visual = Plot(
                            curve = Curve.Quadratic(a = 0.5f, c = -1f),
                            second = Curve.Quadratic(a = -0.5f, c = 1f),
                        ),
                    ),
                    Numeric(
                        question = "y = x² is shifted 1.5 up. What is y when x = 0?",
                        answer = "1.5",
                        explanation = "The vertex rises with the curve.",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f, c = 1.5f)),
                    ),
                    Choice(
                        question = "Compared with the orange curve, the green one is...",
                        options = listOf("shifted up", "three times taller", "shifted right", "three times wider"),
                        correctIndex = 1,
                        explanation = "Multiplying outside stretches every output.",
                        visual = Plot(curve = Curve.Quadratic(a = 0.2f), second = Curve.Quadratic(a = 0.6f)),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "If f(x) = 3x - 4, what is f(5)?",
                options = listOf("11", "15", "19", "-1"),
                correctIndex = 0,
                explanation = "15 - 4.",
                visual = Plot(curve = Curve.Linear(m = 1f, c = -1f)),
            ),
            QuizQuestion(
                prompt = "If f(x) = x² + 2, what is f(-3)?",
                options = listOf("-7", "7", "11", "-11"),
                correctIndex = 2,
                explanation = "9 + 2.",
                visual = Plot(curve = Curve.Quadratic(a = 0.5f, c = 1f)),
            ),
            QuizQuestion(
                prompt = "What is the x-coordinate of this vertex?",
                options = listOf("-4", "-2", "1", "4"),
                correctIndex = 2,
                explanation = "-b / 2a.",
                visual = Plot(curve = Curve.Quadratic(a = 0.5f, b = -1f, c = -1f)),
            ),
            QuizQuestion(
                prompt = "Which way does this parabola open?",
                options = listOf("upwards", "downwards", "sideways", "as a line"),
                correctIndex = 1,
                explanation = "The x² coefficient is negative.",
                visual = Plot(curve = Curve.Quadratic(a = -0.5f, c = 2f)),
            ),
            QuizQuestion(
                prompt = "The green curve is the orange one shifted how?",
                options = listOf("up", "down", "left", "right"),
                correctIndex = 0,
                explanation = "Adding outside the function lifts it.",
                visual = Plot(curve = Curve.Quadratic(a = 0.5f, c = -1.5f), second = Curve.Quadratic(a = 0.5f, c = 0.5f)),
            ),
            QuizQuestion(
                prompt = "Compared with y = f(x), the graph of y = f(x - 3) is...",
                options = listOf("3 left", "3 right", "3 up", "3 down"),
                correctIndex = 1,
                explanation = "Inside changes move the opposite way to their sign.",
                visual = Plot(curve = Curve.Quadratic(a = 0.5f), second = Curve.Quadratic(a = 0.5f, b = -2f, c = 2f)),
            ),
        ),
    )

    private val exponentialsAndLogs = learnUnit(
        topic = MathTopic.FUNCTIONS,
        urlSlug = "exponentials-and-logs",
        title = "Exponentials, logs and sequences",
        summary = "Exponential growth, logarithms, and arithmetic and geometric sequences.",
        level = GradeLevel.GRADES_11_12,
        lessons = listOf(
            LessonSpec(
                id = "g1112-functions-exponentials",
                title = "Exponential growth",
                summary = "Multiplying, over and over.",
                steps = listOf(
                    Concept(
                        body = "The variable sits in the exponent, so each step multiplies instead of adding.",
                        formula = "y = b^x",
                        visual = Plot(curve = Curve.Exponential(base = 2f)),
                    ),
                    Concept(
                        body = "Straight line against exponential: the curve loses at first, then wins by a mile.",
                        visual = Plot(curve = Curve.Exponential(base = 2f), second = Curve.Linear(m = 1f)),
                    ),
                    Choice(
                        question = "A population of 500 doubles every year. What is it after 3 years?",
                        options = listOf("1500", "3000", "4000", "6000"),
                        correctIndex = 2,
                        explanation = "500 x 2 x 2 x 2.",
                        visual = Steps(terms = listOf(500, 1000, 2000), multiply = true),
                    ),
                    Numeric(
                        question = "100 bacteria triple every hour. How many after 2 hours?",
                        answer = "900",
                        explanation = "100 x 3 x 3.",
                        visual = Steps(terms = listOf(100, 300, 900), multiply = true),
                    ),
                    Concept(
                        body = "Compound interest is exponential growth in a suit.",
                        formula = "A = P x (1 + r)^n",
                        visual = Plot(curve = Curve.Exponential(base = 1.6f)),
                    ),
                    Choice(
                        question = "Why does exponential growth always overtake a straight line eventually?",
                        options = listOf(
                            "it starts higher",
                            "each step multiplies what is already there",
                            "lines stop growing",
                            "it is linear too",
                        ),
                        correctIndex = 1,
                        explanation = "The increases themselves keep growing.",
                        visual = Plot(curve = Curve.Exponential(base = 2f), second = Curve.Linear(m = 1f)),
                    ),
                ),
            ),
            LessonSpec(
                id = "g1112-functions-logarithms",
                title = "Logarithms",
                summary = "The inverse of the exponential.",
                steps = listOf(
                    Concept(
                        body = "A logarithm answers 'what power?'. It undoes the exponential exactly.",
                        formula = "2^3 = 8 means log2(8) = 3",
                        visual = Plot(curve = Curve.Logarithm, second = Curve.Exponential(base = 2f)),
                    ),
                    Concept(
                        body = "The log curve climbs forever, but slower and slower.",
                        visual = Plot(curve = Curve.Logarithm),
                    ),
                    Numeric(
                        question = "What is the log of 1000 to base 10?",
                        answer = "3",
                        explanation = "10 x 10 x 10.",
                        visual = Steps(terms = listOf(1, 10, 100, 1000), multiply = true),
                    ),
                    Choice(
                        question = "Solve 2^x = 32",
                        options = listOf("x = 4", "x = 5", "x = 6", "x = 16"),
                        correctIndex = 1,
                        explanation = "Count the doublings from 1.",
                        visual = Steps(terms = listOf(2, 4, 8, 16, 32), multiply = true),
                    ),
                    Concept(
                        body = "Logs turn multiplying into adding, which is how they tame huge numbers.",
                        formula = "log(ab) = log a + log b",
                        visual = Plot(curve = Curve.Logarithm),
                    ),
                    Choice(
                        question = "log(a / b) equals...",
                        options = listOf("log a x log b", "log a - log b", "log a / log b", "log a + log b"),
                        correctIndex = 1,
                        explanation = "Dividing the numbers subtracts their logs.",
                        visual = Plot(curve = Curve.Logarithm),
                    ),
                ),
            ),
            LessonSpec(
                id = "g1112-functions-sequences",
                title = "Sequences and series",
                summary = "Fixed differences, fixed ratios and their sums.",
                steps = listOf(
                    Concept(
                        body = "Arithmetic sequences add the same amount every step.",
                        formula = "nth term = a + (n - 1)d",
                        visual = Steps(terms = listOf(4, 10, 16, 22, 28)),
                    ),
                    Concept(
                        body = "Geometric sequences multiply by the same ratio every step.",
                        visual = Steps(terms = listOf(3, 12, 48, 192), multiply = true),
                    ),
                    Numeric(
                        question = "This sequence starts at 4 and adds 6. What is the 10th term?",
                        answer = "58",
                        explanation = "4 + 9 x 6. Nine steps, not ten.",
                        visual = Steps(terms = listOf(4, 10, 16, 22)),
                    ),
                    Choice(
                        question = "What is the common ratio here?",
                        options = listOf("3", "4", "9", "16"),
                        correctIndex = 1,
                        explanation = "Each term is four times the one before.",
                        visual = Steps(terms = listOf(3, 12, 48, 192), multiply = true),
                    ),
                    Concept(
                        body = "Pair the first term with the last and every pair has the same total.",
                        formula = "sum = n x (first + last) / 2",
                        visual = BarChart(values = listOf(1, 2, 3, 4, 5, 6), showMean = true),
                    ),
                    Choice(
                        question = "What is 1 + 2 + 3 + ... + 100?",
                        options = listOf("1000", "5050", "5000", "10000"),
                        correctIndex = 1,
                        explanation = "Fifty pairs, each adding to 101.",
                        visual = BarChart(values = listOf(1, 2, 3, 4, 5, 6), showMean = true),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "A population of 200 doubles every year. What is it after 4 years?",
                options = listOf("800", "1600", "3200", "2400"),
                correctIndex = 2,
                explanation = "200 x 2^4.",
                visual = Steps(terms = listOf(200, 400, 800), multiply = true),
            ),
            QuizQuestion(
                prompt = "What is the log of 16 to base 2?",
                options = listOf("2", "4", "8", "16"),
                correctIndex = 1,
                explanation = "Count the doublings.",
                visual = Steps(terms = listOf(2, 4, 8, 16), multiply = true),
            ),
            QuizQuestion(
                prompt = "Which curve grows fastest in the long run?",
                options = listOf("the straight line", "the exponential", "they match", "neither grows"),
                correctIndex = 1,
                explanation = "Multiplying always beats adding eventually.",
                visual = Plot(curve = Curve.Exponential(base = 2f), second = Curve.Linear(m = 1f)),
            ),
            QuizQuestion(
                prompt = "What is the 10th term of 5, 8, 11, ...?",
                options = listOf("29", "32", "35", "38"),
                correctIndex = 1,
                explanation = "5 + 9 x 3.",
                visual = Steps(terms = listOf(5, 8, 11, 14)),
            ),
            QuizQuestion(
                prompt = "What is the common ratio here?",
                options = listOf("2", "5", "10", "25"),
                correctIndex = 1,
                explanation = "Each term is five times the previous.",
                visual = Steps(terms = listOf(2, 10, 50, 250), multiply = true),
            ),
            QuizQuestion(
                prompt = "log a + log b equals...",
                options = listOf("log(a + b)", "log(ab)", "log(a / b)", "(log a)(log b)"),
                correctIndex = 1,
                explanation = "Adding logs multiplies the numbers.",
                visual = Plot(curve = Curve.Logarithm),
            ),
        ),
    )

    val units: List<LearnUnit> = listOf(functionsAndGraphs, exponentialsAndLogs)
}
