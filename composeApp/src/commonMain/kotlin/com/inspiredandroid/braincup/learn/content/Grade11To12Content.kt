package com.inspiredandroid.braincup.learn.content

import com.inspiredandroid.braincup.learn.Curve
import com.inspiredandroid.braincup.learn.GradeLevel
import com.inspiredandroid.braincup.learn.LearnUnit
import com.inspiredandroid.braincup.learn.LearnVisual.BarChart
import com.inspiredandroid.braincup.learn.LearnVisual.NormalCurve
import com.inspiredandroid.braincup.learn.LearnVisual.PieChart
import com.inspiredandroid.braincup.learn.LearnVisual.Plot
import com.inspiredandroid.braincup.learn.LearnVisual.SetDiagram
import com.inspiredandroid.braincup.learn.LearnVisual.Steps
import com.inspiredandroid.braincup.learn.LearnVisual.UnitCircleFigure
import com.inspiredandroid.braincup.learn.LessonSpec
import com.inspiredandroid.braincup.learn.LessonStep.Choice
import com.inspiredandroid.braincup.learn.LessonStep.Concept
import com.inspiredandroid.braincup.learn.LessonStep.Numeric
import com.inspiredandroid.braincup.learn.LessonStep.Worked
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.learn.PlotPoint
import com.inspiredandroid.braincup.learn.QuizQuestion
import com.inspiredandroid.braincup.learn.learnUnit

/** Grades 11-12: exponentials and logs, the unit circle, calculus and inferential statistics. */
internal object Grade11To12Content {

    private val level = GradeLevel.GRADES_11_12

    private val functions = learnUnit(
        level = level,
        topic = MathTopic.FUNCTIONS,
        summary = "Exponential growth, logarithms, and arithmetic and geometric sequences.",
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

    private val trigonometry = learnUnit(
        level = level,
        topic = MathTopic.TRIGONOMETRY,
        summary = "Radians and the unit circle, wave graphs and identities.",
        lessons = listOf(
            LessonSpec(
                id = "g1112-trigonometry-unit-circle",
                title = "Radians and the unit circle",
                summary = "Trig for every angle, not just acute ones.",
                steps = listOf(
                    Concept(
                        body = "The point at angle x sits at (cos x, sin x) - and this works for any angle at all.",
                        visual = UnitCircleFigure(degrees = 52),
                    ),
                    Concept(
                        body = "Radians measure the arc itself, so a full turn is 2 pi.",
                        formula = "180 degrees = pi radians",
                        visual = UnitCircleFigure(degrees = 180, label = "pi"),
                    ),
                    Choice(
                        question = "How many degrees is this angle?",
                        options = listOf("45", "90", "180", "360"),
                        correctIndex = 1,
                        explanation = "pi/2 radians is a quarter turn.",
                        visual = UnitCircleFigure(degrees = 90, label = "pi/2", reveal = false),
                    ),
                    Concept(
                        body = "In the second quadrant the point is up and left, so sine is positive and cosine negative.",
                        visual = UnitCircleFigure(degrees = 140),
                    ),
                    Numeric(
                        question = "What is cos 0?",
                        answer = "1",
                        explanation = "The point sits at (1, 0), and cosine is the x-coordinate.",
                        visual = UnitCircleFigure(degrees = 0, reveal = false),
                    ),
                    Choice(
                        question = "What is sin 150?",
                        options = listOf("-0.5", "0.5", "1", "-1"),
                        correctIndex = 1,
                        explanation = "It mirrors 30 degrees, and sine stays positive up there.",
                        visual = UnitCircleFigure(degrees = 150, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "g1112-trigonometry-graphs",
                title = "Graphs of sine and cosine",
                summary = "Amplitude, period and why it repeats.",
                steps = listOf(
                    Concept(
                        body = "Plot the height as you walk round the circle and you trace the sine wave.",
                        visual = Plot(curve = Curve.Sine(amplitude = 2f)),
                    ),
                    Concept(
                        body = "Cosine is the same wave, shifted a quarter turn.",
                        formula = "cos x = sin(x + 90 degrees)",
                        visual = Plot(
                            curve = Curve.Sine(amplitude = 2f),
                            second = Curve.Sine(amplitude = 2f, cosine = true),
                        ),
                    ),
                    Choice(
                        question = "What is the period of y = sin x, in degrees?",
                        options = listOf("90", "180", "360", "720"),
                        correctIndex = 2,
                        explanation = "One complete circuit of the circle.",
                        visual = Plot(curve = Curve.Sine(amplitude = 2f)),
                    ),
                    Concept(
                        body = "The number in front sets the amplitude; the number inside squeezes the period.",
                        formula = "y = a sin(bx)",
                        visual = Plot(curve = Curve.Sine(amplitude = 1f), second = Curve.Sine(amplitude = 2.5f)),
                    ),
                    Numeric(
                        question = "What is the amplitude of the taller wave?",
                        answer = "2.5",
                        explanation = "It runs from -2.5 up to 2.5.",
                        visual = Plot(curve = Curve.Sine(amplitude = 1f), second = Curve.Sine(amplitude = 2.5f)),
                    ),
                    Choice(
                        question = "The green wave has double the frequency. Its period is...",
                        options = listOf("doubled", "halved", "unchanged", "zero"),
                        correctIndex = 1,
                        explanation = "Twice as many waves in the same space.",
                        visual = Plot(
                            curve = Curve.Sine(amplitude = 2f),
                            second = Curve.Sine(amplitude = 2f, frequency = 2f),
                        ),
                    ),
                ),
            ),
            LessonSpec(
                id = "g1112-trigonometry-identities",
                title = "Identities and equations",
                summary = "Pythagoras on the unit circle.",
                steps = listOf(
                    Concept(
                        body = "An identity holds for every angle; an equation only for particular ones.",
                        visual = UnitCircleFigure(degrees = 52),
                    ),
                    Concept(
                        body = "The sine and cosine legs form a right triangle with hypotenuse 1.",
                        formula = "sin²x + cos²x = 1",
                        visual = UnitCircleFigure(degrees = 37),
                    ),
                    Choice(
                        question = "If sin x = 0.6, what is cos²x?",
                        options = listOf("0.36", "0.4", "0.64", "0.8"),
                        correctIndex = 2,
                        explanation = "1 - 0.36.",
                        visual = UnitCircleFigure(degrees = 37, reveal = false),
                    ),
                    Concept(
                        body = "Tangent is the ratio of the two legs, so it blows up where cosine is zero.",
                        formula = "tan x = sin x / cos x",
                        visual = UnitCircleFigure(degrees = 80),
                    ),
                    Numeric(
                        question = "What is sin²30 + cos²30?",
                        answer = "1",
                        explanation = "The identity holds for every angle.",
                        visual = UnitCircleFigure(degrees = 30, reveal = false),
                    ),
                    Choice(
                        question = "How many solutions does sin x = 0.5 have between 0 and 360?",
                        options = listOf("none", "one", "two", "four"),
                        correctIndex = 2,
                        explanation = "Sine is positive in the first two quadrants: 30 and 150.",
                        visual = Plot(curve = Curve.Sine(amplitude = 2f)),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "How many radians are in a full turn?",
                options = listOf("pi", "2 pi", "180", "360"),
                correctIndex = 1,
                explanation = "One full circuit of the unit circle.",
                visual = UnitCircleFigure(degrees = 300, reveal = false),
            ),
            QuizQuestion(
                prompt = "What is cos 180?",
                options = listOf("1", "0", "-1", "0.5"),
                correctIndex = 2,
                explanation = "The point sits at (-1, 0).",
                visual = UnitCircleFigure(degrees = 180, reveal = false),
            ),
            QuizQuestion(
                prompt = "What is the period of this wave, in degrees?",
                options = listOf("90", "180", "360", "720"),
                correctIndex = 2,
                explanation = "It repeats after one full turn.",
                visual = Plot(curve = Curve.Sine(amplitude = 2f)),
            ),
            QuizQuestion(
                prompt = "What is the amplitude of the taller wave?",
                options = listOf("1", "2", "2.5", "360"),
                correctIndex = 2,
                explanation = "The amplitude is the factor outside the sine.",
                visual = Plot(curve = Curve.Sine(amplitude = 1f), second = Curve.Sine(amplitude = 2.5f)),
            ),
            QuizQuestion(
                prompt = "sin²x + cos²x equals...",
                options = listOf("0", "1", "2", "x"),
                correctIndex = 1,
                explanation = "Pythagoras with hypotenuse 1.",
                visual = UnitCircleFigure(degrees = 37),
            ),
            QuizQuestion(
                prompt = "How many degrees is pi/3 radians?",
                options = listOf("30", "45", "60", "90"),
                correctIndex = 2,
                explanation = "180 / 3.",
                visual = UnitCircleFigure(degrees = 60, label = "pi/3", reveal = false),
            ),
        ),
    )

    private val calculus = learnUnit(
        level = level,
        topic = MathTopic.CALCULUS,
        summary = "Limits, derivatives as gradients, and integrals as area.",
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

    private val data = learnUnit(
        level = level,
        topic = MathTopic.DATA,
        summary = "Standard deviation, the normal distribution and conditional probability.",
        lessons = listOf(
            LessonSpec(
                id = "g1112-data-distributions",
                title = "Spread and z-scores",
                summary = "Same mean, different worlds.",
                steps = listOf(
                    Concept(
                        body = "Standard deviation measures the typical distance from the mean.",
                        visual = NormalCurve(shadeSd = 1),
                    ),
                    Concept(
                        body = "Two sets can share a mean and still look nothing alike.",
                        visual = BarChart(values = listOf(48, 52, 50, 51, 49), showMean = true),
                    ),
                    Choice(
                        question = "Two classes both average 70%. One has sd 3, the other sd 15. Which is more consistent?",
                        options = listOf("sd 3", "sd 15", "equally", "cannot tell"),
                        correctIndex = 0,
                        explanation = "Smaller spread means results cluster near the mean.",
                        visual = NormalCurve(shadeSd = 1),
                    ),
                    Concept(
                        body = "A z-score counts standard deviations from the mean, so any two scales become comparable.",
                        formula = "z = (value - mean) / sd",
                        visual = NormalCurve(shadeSd = 2),
                    ),
                    Numeric(
                        question = "Mean 60, sd 10. What is the z-score of 80?",
                        answer = "2",
                        explanation = "20 above the mean is two standard deviations.",
                        visual = NormalCurve(shadeSd = 2),
                    ),
                    Choice(
                        question = "A z-score of -1.5 means the value is...",
                        options = listOf("1.5 above the mean", "1.5 sd below the mean", "1.5% of the data", "impossible"),
                        correctIndex = 1,
                        explanation = "Negative z-scores sit below the mean.",
                        visual = NormalCurve(shadeSd = 2),
                    ),
                ),
            ),
            LessonSpec(
                id = "g1112-data-normal",
                title = "The normal distribution",
                summary = "The bell curve and the 68-95-99.7 rule.",
                steps = listOf(
                    Concept(
                        body = "Many natural measurements pile up symmetrically around the mean.",
                        visual = NormalCurve(shadeSd = 1, percent = "68%"),
                    ),
                    Concept(
                        body = "Two standard deviations either side already covers almost everything.",
                        visual = NormalCurve(shadeSd = 2, percent = "95%"),
                    ),
                    Choice(
                        question = "Heights are normal, mean 170 cm, sd 10 cm. What percent lie between 160 and 180?",
                        options = listOf("50%", "68%", "95%", "99.7%"),
                        correctIndex = 1,
                        explanation = "That band is one standard deviation either side.",
                        visual = NormalCurve(shadeSd = 1),
                    ),
                    Numeric(
                        question = "What percent lies within two standard deviations of the mean?",
                        answer = "95",
                        explanation = "The middle step of the 68-95-99.7 rule.",
                        visual = NormalCurve(shadeSd = 2),
                    ),
                    Concept(
                        body = "The curve is symmetric, so mean, median and mode all sit together.",
                        visual = NormalCurve(shadeSd = 1),
                    ),
                    Choice(
                        question = "Values more than three standard deviations out are...",
                        options = listOf("common", "about a third of the data", "very rare", "impossible"),
                        correctIndex = 2,
                        explanation = "About 0.3% of the data.",
                        visual = NormalCurve(shadeSd = 3),
                    ),
                ),
            ),
            LessonSpec(
                id = "g1112-data-conditional",
                title = "Conditional probability",
                summary = "What changes once you know something.",
                steps = listOf(
                    Concept(
                        body = "Knowing B shrinks the pool you are choosing from.",
                        formula = "P(A given B) = P(A and B) / P(B)",
                        visual = SetDiagram(aOnly = 4, both = 8, bOnly = 5, aLabel = "French", bLabel = "German"),
                    ),
                    Worked(
                        problem = "12 take French, 8 of those also take German. A French student is picked.",
                        lines = listOf(
                            "The pool is now only the 12 French students.",
                            "Of those, 8 also take German.",
                            "So 8 out of 12.",
                        ),
                        result = "2/3",
                        visual = SetDiagram(aOnly = 4, both = 8, bOnly = 5, aLabel = "French", bLabel = "German"),
                    ),
                    Choice(
                        question = "Two events are independent when...",
                        options = listOf(
                            "they cannot both happen",
                            "knowing one tells you nothing about the other",
                            "they are equally likely",
                            "they are opposites",
                        ),
                        correctIndex = 1,
                        explanation = "The conditional probability equals the plain one.",
                        visual = SetDiagram(aOnly = 6, both = 3, bOnly = 6, aLabel = "A", bLabel = "B"),
                    ),
                    Concept(
                        body = "Independent probabilities multiply, which is why long lucky runs are so rare.",
                        formula = "P(A and B) = P(A) x P(B)",
                        visual = PieChart(shares = listOf(1, 3), labels = listOf("both heads", "anything else")),
                    ),
                    Numeric(
                        question = "Two fair coins. What is the probability of two heads, as a decimal?",
                        answer = "0.25",
                        explanation = "0.5 x 0.5.",
                        visual = PieChart(shares = listOf(1, 3), reveal = false),
                    ),
                    Choice(
                        question = "Drawing a second card without replacing the first makes it...",
                        options = listOf("independent", "dependent on the first", "impossible", "identical"),
                        correctIndex = 1,
                        explanation = "The deck has changed.",
                        visual = SetDiagram(aOnly = 12, both = 1, bOnly = 12, aLabel = "1st", bLabel = "2nd"),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "Mean 50, sd 5. What is the z-score of 60?",
                options = listOf("1", "2", "10", "0.5"),
                correctIndex = 1,
                explanation = "10 above the mean is two standard deviations.",
                visual = NormalCurve(shadeSd = 2),
            ),
            QuizQuestion(
                prompt = "What percent of a normal distribution lies in the shaded band?",
                options = listOf("50%", "68%", "95%", "99.7%"),
                correctIndex = 1,
                explanation = "One standard deviation either side.",
                visual = NormalCurve(shadeSd = 1),
            ),
            QuizQuestion(
                prompt = "A smaller standard deviation means the data is...",
                options = listOf("higher on average", "more spread out", "more consistent", "smaller in count"),
                correctIndex = 2,
                explanation = "Values cluster near the mean.",
                visual = NormalCurve(shadeSd = 1),
            ),
            QuizQuestion(
                prompt = "For independent events, P(A and B) equals...",
                options = listOf("P(A) + P(B)", "P(A) x P(B)", "P(A) - P(B)", "P(A) / P(B)"),
                correctIndex = 1,
                explanation = "Independent probabilities multiply.",
                visual = PieChart(shares = listOf(1, 3), reveal = false),
            ),
            QuizQuestion(
                prompt = "Two fair dice. What is the probability of two sixes?",
                options = listOf("1/6", "1/12", "1/36", "2/6"),
                correctIndex = 2,
                explanation = "1/6 x 1/6.",
                visual = PieChart(shares = listOf(1, 35), reveal = false),
            ),
            QuizQuestion(
                prompt = "A French student is picked. What is the chance they also take German?",
                options = listOf("8/20", "8/12", "12/20", "4/12"),
                correctIndex = 1,
                explanation = "The condition restricts the pool to the 12 French students.",
                visual = SetDiagram(aOnly = 4, both = 8, bOnly = 5, aLabel = "French", bLabel = "German"),
            ),
        ),
    )

    val units: List<LearnUnit> = listOf(functions, trigonometry, calculus, data)
}
