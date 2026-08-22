package com.inspiredandroid.braincup.learn.content

import com.inspiredandroid.braincup.learn.GradeLevel
import com.inspiredandroid.braincup.learn.LearnUnit
import com.inspiredandroid.braincup.learn.LearnVisual
import com.inspiredandroid.braincup.learn.LessonSpec
import com.inspiredandroid.braincup.learn.LessonStep.Choice
import com.inspiredandroid.braincup.learn.LessonStep.Concept
import com.inspiredandroid.braincup.learn.LessonStep.Numeric
import com.inspiredandroid.braincup.learn.LessonStep.Worked
import com.inspiredandroid.braincup.learn.MathTopic
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
                summary = "Multiplying by a fixed factor, over and over.",
                steps = listOf(
                    Concept(
                        body = "An exponential function keeps the variable in the exponent. Instead of adding a fixed amount each step, it multiplies by a fixed factor.",
                        formula = "y = a x b^x",
                        visual = LearnVisual.EXPONENTIAL_CURVE,
                    ),
                    Concept(
                        body = "With b above 1 the curve grows, and grows faster the higher it gets. With b between 0 and 1 it decays towards zero without ever arriving.",
                        visual = LearnVisual.EXPONENTIAL_CURVE,
                    ),
                    Choice(
                        question = "A population of 500 doubles every year. What is it after 3 years?",
                        options = listOf("1500", "3000", "4000", "6000"),
                        correctIndex = 2,
                        explanation = "500 x 2³ = 500 x 8 = 4000. Doubling three times, not tripling once.",
                    ),
                    Numeric(
                        question = "A colony of 100 bacteria triples every hour. How many are there after 2 hours?",
                        answer = "900",
                        explanation = "100 x 3² = 900.",
                    ),
                    Concept(
                        body = "Compound interest is exponential growth wearing a suit: 1000 euro at 5% a year becomes 1000 x 1.05 to the power n after n years.",
                        formula = "A = P x (1 + r)^n",
                    ),
                    Choice(
                        question = "Exponential growth eventually overtakes any polynomial growth because...",
                        options = listOf("it starts higher", "the factor applies to an ever-larger amount", "polynomials stop growing", "it is linear"),
                        correctIndex = 1,
                        explanation = "Each step multiplies whatever you already have, so the increases themselves keep growing.",
                        visual = LearnVisual.EXPONENTIAL_CURVE,
                    ),
                ),
            ),
            LessonSpec(
                id = "g1112-functions-logarithms",
                title = "Logarithms",
                summary = "The question 'what power?' has its own function.",
                steps = listOf(
                    Concept(
                        body = "A logarithm answers the question 'what power?'. The log of 8 to base 2 is 3, because 2 cubed is 8. It is the exact inverse of the exponential.",
                        formula = "b^y = x means log base b of x = y",
                    ),
                    Concept(
                        body = "Logs turn multiplication into addition, which is exactly why they tame enormous numbers.",
                        formula = "log(ab) = log a + log b",
                    ),
                    Numeric(
                        question = "What is the log of 1000 to base 10?",
                        answer = "3",
                        explanation = "10³ = 1000.",
                    ),
                    Choice(
                        question = "Solve 2^x = 32",
                        options = listOf("x = 4", "x = 5", "x = 6", "x = 16"),
                        correctIndex = 1,
                        explanation = "2⁵ = 32.",
                    ),
                    Concept(
                        body = "Decibels, pH and earthquake magnitude are all logarithmic scales: every step up multiplies the underlying quantity by a fixed factor.",
                    ),
                    Choice(
                        question = "log(a / b) is equal to...",
                        options = listOf("log a x log b", "log a - log b", "log a / log b", "log a + log b"),
                        correctIndex = 1,
                        explanation = "Dividing the numbers subtracts their logs.",
                    ),
                ),
            ),
            LessonSpec(
                id = "g1112-functions-sequences",
                title = "Sequences and series",
                summary = "Fixed differences, fixed ratios and their sums.",
                steps = listOf(
                    Concept(
                        body = "A sequence is an ordered list built by a rule. Arithmetic sequences add a fixed difference each step; geometric sequences multiply by a fixed ratio.",
                        visual = LearnVisual.NUMBER_LINE,
                    ),
                    Concept(
                        body = "Any arithmetic sequence has a closed form, so you can jump straight to the hundredth term without listing the first ninety-nine.",
                        formula = "nth term = first term + (n - 1) x d",
                    ),
                    Numeric(
                        question = "A sequence starts at 4 and adds 6 each time. What is the 10th term?",
                        answer = "58",
                        explanation = "4 + 9 x 6 = 4 + 54 = 58. Nine steps, not ten.",
                    ),
                    Choice(
                        question = "What is the common ratio of 3, 12, 48, 192?",
                        options = listOf("3", "4", "9", "16"),
                        correctIndex = 1,
                        explanation = "Each term is 4 times the one before it.",
                    ),
                    Concept(
                        body = "The sum of an arithmetic series pairs the first term with the last, the second with the second-last, and so on - every pair has the same total.",
                        formula = "sum = n x (first + last) / 2",
                    ),
                    Choice(
                        question = "What is 1 + 2 + 3 + ... + 100?",
                        options = listOf("1000", "5050", "5000", "10000"),
                        correctIndex = 1,
                        explanation = "Fifty pairs each summing to 101: 50 x 101 = 5050.",
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "A population of 200 doubles every year. What is it after 4 years?",
                options = listOf("800", "1600", "3200", "2400"),
                correctIndex = 2,
                explanation = "200 x 2^4 = 200 x 16.",
            ),
            QuizQuestion(
                prompt = "What is the log of 16 to base 2?",
                options = listOf("2", "4", "8", "16"),
                correctIndex = 1,
                explanation = "2^4 = 16.",
            ),
            QuizQuestion(
                prompt = "Solve 3^x = 81",
                options = listOf("x = 3", "x = 4", "x = 9", "x = 27"),
                correctIndex = 1,
                explanation = "3^4 = 81.",
            ),
            QuizQuestion(
                prompt = "What is the 10th term of 5, 8, 11, ...?",
                options = listOf("29", "32", "35", "38"),
                correctIndex = 1,
                explanation = "5 + 9 x 3 = 32.",
            ),
            QuizQuestion(
                prompt = "What is the common ratio of 2, 10, 50, 250?",
                options = listOf("2", "5", "10", "25"),
                correctIndex = 1,
                explanation = "Each term is five times the previous one.",
            ),
            QuizQuestion(
                prompt = "log a + log b is equal to...",
                options = listOf("log(a + b)", "log(ab)", "log(a / b)", "(log a)(log b)"),
                correctIndex = 1,
                explanation = "Adding logs corresponds to multiplying the numbers.",
            ),
        ),
    )

    private val trigonometry = learnUnit(
        level = level,
        topic = MathTopic.TRIGONOMETRY,
        summary = "Radians and the unit circle, wave graphs and trigonometric identities.",
        lessons = listOf(
            LessonSpec(
                id = "g1112-trigonometry-unit-circle",
                title = "Radians and the unit circle",
                summary = "Trig for every angle, not just acute ones.",
                steps = listOf(
                    Concept(
                        body = "The unit circle has radius 1 and sits at the origin. The point at angle x has coordinates (cos x, sin x) - a definition that works for any angle at all, not only those under 90 degrees.",
                        visual = LearnVisual.UNIT_CIRCLE,
                    ),
                    Concept(
                        body = "Radians measure an angle by the arc length it cuts on the unit circle. A full turn is 2 pi radians, so 180 degrees is pi radians.",
                        formula = "180 degrees = pi radians",
                        visual = LearnVisual.UNIT_CIRCLE,
                    ),
                    Choice(
                        question = "How many degrees is pi/2 radians?",
                        options = listOf("45", "90", "180", "360"),
                        correctIndex = 1,
                        explanation = "pi radians is 180 degrees, so half of that is 90.",
                    ),
                    Concept(
                        body = "The signs of sine and cosine follow the quadrant the point lands in. In the second quadrant the point is up and to the left, so sine is positive while cosine is negative.",
                        visual = LearnVisual.UNIT_CIRCLE,
                    ),
                    Numeric(
                        question = "What is cos 0?",
                        answer = "1",
                        explanation = "At angle 0 the point sits at (1, 0), and cosine is the x-coordinate.",
                    ),
                    Choice(
                        question = "What is sin 150 degrees?",
                        options = listOf("-0.5", "0.5", "1", "-1"),
                        correctIndex = 1,
                        explanation = "150 degrees mirrors 30 degrees across the vertical axis, and sine stays positive in the second quadrant.",
                    ),
                ),
            ),
            LessonSpec(
                id = "g1112-trigonometry-graphs",
                title = "Graphs of sine and cosine",
                summary = "Amplitude, period and why the wave repeats.",
                steps = listOf(
                    Concept(
                        body = "Walking around the unit circle and plotting the height against the angle traces the sine wave. Because you keep going round, the graph repeats for ever.",
                        visual = LearnVisual.SINE_WAVE,
                    ),
                    Concept(
                        body = "The cosine graph has exactly the same shape, shifted a quarter turn to the left.",
                        formula = "cos x = sin(x + 90 degrees)",
                        visual = LearnVisual.SINE_WAVE,
                    ),
                    Choice(
                        question = "What is the period of y = sin x, in degrees?",
                        options = listOf("90", "180", "360", "720"),
                        correctIndex = 2,
                        explanation = "It repeats after one complete turn of the circle.",
                    ),
                    Concept(
                        body = "In y = a sin(bx), a is the amplitude - how tall the wave is - and b squeezes it horizontally, so the period becomes 360 degrees divided by b.",
                        formula = "period = 360 / b",
                        visual = LearnVisual.SINE_WAVE,
                    ),
                    Numeric(
                        question = "What is the amplitude of y = 4 sin x?",
                        answer = "4",
                        explanation = "The wave runs from -4 up to 4.",
                    ),
                    Choice(
                        question = "What is the period of y = sin(2x), in degrees?",
                        options = listOf("90", "180", "360", "720"),
                        correctIndex = 1,
                        explanation = "Doubling the input makes the wave complete twice as fast, halving the period.",
                    ),
                ),
            ),
            LessonSpec(
                id = "g1112-trigonometry-identities",
                title = "Identities and equations",
                summary = "One identity that comes straight from Pythagoras.",
                steps = listOf(
                    Concept(
                        body = "An identity is true for every angle. An equation is true only for particular ones - and solving trig equations usually means finding all of them in a given range.",
                        visual = LearnVisual.UNIT_CIRCLE,
                    ),
                    Concept(
                        body = "The Pythagorean identity is nothing more than Pythagoras applied to the right triangle inside the unit circle, whose hypotenuse is 1.",
                        formula = "sin²x + cos²x = 1",
                        visual = LearnVisual.UNIT_CIRCLE,
                    ),
                    Choice(
                        question = "If sin x = 0.6, what is cos²x?",
                        options = listOf("0.36", "0.4", "0.64", "0.8"),
                        correctIndex = 2,
                        explanation = "cos²x = 1 - 0.36 = 0.64.",
                    ),
                    Concept(
                        body = "Tangent is simply the ratio of the other two, which is why it blows up wherever cosine is zero.",
                        formula = "tan x = sin x / cos x",
                    ),
                    Numeric(
                        question = "What is sin²30 + cos²30?",
                        answer = "1",
                        explanation = "The identity holds for every angle, so there is nothing to calculate.",
                    ),
                    Choice(
                        question = "How many solutions does sin x = 0.5 have between 0 and 360 degrees?",
                        options = listOf("none", "one", "two", "four"),
                        correctIndex = 2,
                        explanation = "30 degrees and 150 degrees - sine is positive in the first two quadrants.",
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "How many radians are in a full turn?",
                options = listOf("pi", "2 pi", "180", "360"),
                correctIndex = 1,
                explanation = "A full turn is 2 pi radians, matching 360 degrees.",
            ),
            QuizQuestion(
                prompt = "What is cos 180 degrees?",
                options = listOf("1", "0", "-1", "0.5"),
                correctIndex = 2,
                explanation = "At 180 degrees the point sits at (-1, 0).",
            ),
            QuizQuestion(
                prompt = "What is the period of y = sin x?",
                options = listOf("90 degrees", "180 degrees", "360 degrees", "720 degrees"),
                correctIndex = 2,
                explanation = "One full circuit of the unit circle.",
            ),
            QuizQuestion(
                prompt = "What is the amplitude of y = 3 sin 2x?",
                options = listOf("2", "3", "6", "360"),
                correctIndex = 1,
                explanation = "The amplitude is the factor outside the sine.",
            ),
            QuizQuestion(
                prompt = "sin²x + cos²x equals...",
                options = listOf("0", "1", "2", "x"),
                correctIndex = 1,
                explanation = "The Pythagorean identity, true for every angle.",
            ),
            QuizQuestion(
                prompt = "How many degrees is pi/3 radians?",
                options = listOf("30", "45", "60", "90"),
                correctIndex = 2,
                explanation = "180 / 3 = 60.",
            ),
        ),
    )

    private val calculus = learnUnit(
        level = level,
        topic = MathTopic.CALCULUS,
        summary = "Limits, derivatives as gradients, and integrals as accumulated area.",
        lessons = listOf(
            LessonSpec(
                id = "g1112-calculus-limits",
                title = "Limits",
                summary = "What a function approaches, not what it reaches.",
                steps = listOf(
                    Concept(
                        body = "A limit describes what a function approaches as the input closes in on some value - even when the function is undefined at that exact value.",
                        formula = "(x² - 4)/(x - 2) approaches 4 as x approaches 2",
                        visual = LearnVisual.TANGENT_LINE,
                    ),
                    Concept(
                        body = "At x = 2 that expression reads 0/0, which means nothing. But cancelling the common factor leaves x + 2, which clearly heads for 4. The limit describes the journey, not the destination.",
                    ),
                    Choice(
                        question = "What is the limit of (x² - 9)/(x - 3) as x approaches 3?",
                        options = listOf("0", "3", "6", "undefined"),
                        correctIndex = 2,
                        explanation = "Factorise the top as (x - 3)(x + 3) and cancel: x + 3 approaches 6.",
                    ),
                    Concept(
                        body = "A function is continuous wherever you could draw it without lifting the pen: the limit exists and matches the actual value there.",
                    ),
                    Numeric(
                        question = "What does 1/x approach as x grows without bound?",
                        answer = "0",
                        explanation = "Dividing by ever larger numbers shrinks the result towards zero.",
                    ),
                    Choice(
                        question = "Limits matter in calculus because...",
                        options = listOf("they replace algebra", "both the derivative and the integral are defined as limits", "they only apply to circles", "they are quicker to compute"),
                        correctIndex = 1,
                        explanation = "Everything else in calculus is built on top of them.",
                    ),
                ),
            ),
            LessonSpec(
                id = "g1112-calculus-derivatives",
                title = "Derivatives",
                summary = "The gradient of the tangent, at every point.",
                steps = listOf(
                    Concept(
                        body = "The derivative is the instantaneous rate of change: the gradient of the tangent line touching the curve at a point.",
                        visual = LearnVisual.TANGENT_LINE,
                    ),
                    Concept(
                        body = "It comes straight from a limit. Take the gradient between two nearby points on the curve, then slide them together until the gap vanishes.",
                        formula = "f'(x) = limit of (f(x+h) - f(x)) / h as h approaches 0",
                        visual = LearnVisual.TANGENT_LINE,
                    ),
                    Concept(
                        body = "For powers the shortcut is quick: bring the power down in front and reduce it by one.",
                        formula = "derivative of x^n is n x^(n-1)",
                    ),
                    Choice(
                        question = "Differentiate y = x³",
                        options = listOf("3x", "x²", "3x²", "3x⁴"),
                        correctIndex = 2,
                        explanation = "Bring the 3 down and reduce the power to 2.",
                    ),
                    Numeric(
                        question = "If f(x) = x², what is f'(3)?",
                        answer = "6",
                        explanation = "f'(x) = 2x, so f'(3) = 6.",
                    ),
                    Choice(
                        question = "Where a smooth curve has a maximum, its derivative is...",
                        options = listOf("at its largest", "zero", "undefined", "negative"),
                        correctIndex = 1,
                        explanation = "The tangent is horizontal at the top of a hill, so its gradient is zero.",
                        visual = LearnVisual.TANGENT_LINE,
                    ),
                ),
            ),
            LessonSpec(
                id = "g1112-calculus-integrals",
                title = "Integrals",
                summary = "Adding up infinitely many thin slices.",
                steps = listOf(
                    Concept(
                        body = "Integration adds up infinitely many infinitely thin slices. Geometrically that total is the area under the curve.",
                        visual = LearnVisual.AREA_UNDER_CURVE,
                    ),
                    Concept(
                        body = "Integration undoes differentiation. That surprising link between area and gradient is the Fundamental Theorem of Calculus.",
                        formula = "integral of x^n is x^(n+1)/(n+1) + C",
                        visual = LearnVisual.AREA_UNDER_CURVE,
                    ),
                    Choice(
                        question = "Integrate 2x",
                        options = listOf("2", "x² + C", "2x² + C", "x + C"),
                        correctIndex = 1,
                        explanation = "Differentiating x² gives 2x, so integrating 2x returns x² plus a constant.",
                    ),
                    Concept(
                        body = "The '+ C' appears because every constant differentiates to zero, so the original one cannot be recovered. In a definite integral you subtract two values and the C cancels itself out.",
                    ),
                    Numeric(
                        question = "Evaluate the integral of 2x from 0 to 3.",
                        answer = "9",
                        explanation = "The antiderivative is x², so the answer is 3² - 0² = 9.",
                    ),
                    Choice(
                        question = "The area under a velocity-time graph represents...",
                        options = listOf("acceleration", "distance travelled", "speed", "elapsed time"),
                        correctIndex = 1,
                        explanation = "Velocity multiplied by time is distance, and the integral accumulates it slice by slice.",
                        visual = LearnVisual.AREA_UNDER_CURVE,
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "Differentiate y = x⁴",
                options = listOf("4x", "x³", "4x³", "4x⁵"),
                correctIndex = 2,
                explanation = "Bring the power down and reduce it by one.",
            ),
            QuizQuestion(
                prompt = "If f(x) = 3x², what is f'(x)?",
                options = listOf("3x", "6x", "6x²", "x³"),
                correctIndex = 1,
                explanation = "The 2 comes down and multiplies the 3.",
            ),
            QuizQuestion(
                prompt = "At a maximum of a smooth curve the derivative is...",
                options = listOf("at its largest", "zero", "negative", "undefined"),
                correctIndex = 1,
                explanation = "The tangent there is horizontal.",
            ),
            QuizQuestion(
                prompt = "Integrate 3x²",
                options = listOf("6x + C", "x³ + C", "3x³ + C", "x² + C"),
                correctIndex = 1,
                explanation = "Raise the power to 3 and divide by 3.",
            ),
            QuizQuestion(
                prompt = "What is the limit of (x² - 1)/(x - 1) as x approaches 1?",
                options = listOf("0", "1", "2", "undefined"),
                correctIndex = 2,
                explanation = "Cancel to x + 1, which approaches 2.",
            ),
            QuizQuestion(
                prompt = "The definite integral of velocity over time gives...",
                options = listOf("acceleration", "distance", "speed", "force"),
                correctIndex = 1,
                explanation = "Accumulating velocity through time produces displacement.",
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
                summary = "Two data sets can share a mean and share nothing else.",
                steps = listOf(
                    Concept(
                        body = "Two data sets can share a mean and be completely different. Standard deviation measures the typical distance of a value from that mean.",
                        visual = LearnVisual.NORMAL_CURVE,
                    ),
                    Concept(
                        body = "A small standard deviation means the data clusters tightly around the mean; a large one means it is spread far and wide.",
                        visual = LearnVisual.NORMAL_CURVE,
                    ),
                    Choice(
                        question = "Two classes both average 70%. Class A has a standard deviation of 3, class B of 15. Which class is more consistent?",
                        options = listOf("Class A", "Class B", "They are equally consistent", "You cannot tell"),
                        correctIndex = 0,
                        explanation = "A smaller standard deviation means results sit closer to the mean.",
                    ),
                    Concept(
                        body = "A z-score says how many standard deviations a value sits from the mean, which lets you compare results measured on completely different scales.",
                        formula = "z = (value - mean) / standard deviation",
                    ),
                    Numeric(
                        question = "A test has mean 60 and standard deviation 10. What is the z-score of a mark of 80?",
                        answer = "2",
                        explanation = "(80 - 60) / 10 = 2.",
                    ),
                    Choice(
                        question = "A z-score of -1.5 means the value is...",
                        options = listOf("1.5 above the mean", "1.5 standard deviations below the mean", "1.5% of the data", "impossible"),
                        correctIndex = 1,
                        explanation = "A negative z-score always sits below the mean.",
                    ),
                ),
            ),
            LessonSpec(
                id = "g1112-data-normal",
                title = "The normal distribution",
                summary = "The bell curve and the 68-95-99.7 rule.",
                steps = listOf(
                    Concept(
                        body = "Many natural measurements pile up in a symmetric bell shape around the mean. That shape is the normal distribution.",
                        visual = LearnVisual.NORMAL_CURVE,
                    ),
                    Concept(
                        body = "The empirical rule: about 68% of values lie within one standard deviation of the mean, 95% within two, and 99.7% within three.",
                        formula = "68 - 95 - 99.7",
                        visual = LearnVisual.NORMAL_CURVE,
                    ),
                    Choice(
                        question = "Heights are normal with mean 170 cm and standard deviation 10 cm. Roughly what percentage lies between 160 and 180 cm?",
                        options = listOf("50%", "68%", "95%", "99.7%"),
                        correctIndex = 1,
                        explanation = "That range is exactly one standard deviation either side of the mean.",
                        visual = LearnVisual.NORMAL_CURVE,
                    ),
                    Numeric(
                        question = "What percentage of a normal distribution lies within two standard deviations of the mean?",
                        answer = "95",
                        explanation = "The 68-95-99.7 rule gives 95%.",
                    ),
                    Concept(
                        body = "Because the curve is perfectly symmetric, the mean, the median and the mode all sit together at the centre.",
                        visual = LearnVisual.NORMAL_CURVE,
                    ),
                    Choice(
                        question = "In a normal distribution, values more than three standard deviations from the mean are...",
                        options = listOf("common", "about a third of the data", "very rare", "impossible"),
                        correctIndex = 2,
                        explanation = "Only about 0.3% of the data falls that far out.",
                    ),
                ),
            ),
            LessonSpec(
                id = "g1112-data-conditional",
                title = "Conditional probability",
                summary = "What changes once you know something.",
                steps = listOf(
                    Concept(
                        body = "Conditional probability is the chance of A given that B has already happened. Knowing B shrinks the pool of possibilities you are choosing from.",
                        formula = "P(A given B) = P(A and B) / P(B)",
                        visual = LearnVisual.PIE_CHART,
                    ),
                    Worked(
                        problem = "A class of 20 has 12 students taking French, 8 of whom also take German. A French student is picked at random. What is the chance they take German?",
                        lines = listOf(
                            "The pool is no longer 20 - it is only the 12 French students.",
                            "Of those 12, exactly 8 also take German.",
                            "So the probability is 8/12.",
                        ),
                        result = "2/3",
                    ),
                    Choice(
                        question = "Two events are independent when...",
                        options = listOf("they cannot both happen", "knowing one tells you nothing about the other", "they have equal probability", "they are opposites"),
                        correctIndex = 1,
                        explanation = "For independent events the conditional probability equals the plain one.",
                    ),
                    Concept(
                        body = "For independent events the probabilities simply multiply - which is why long runs of luck are so unlikely.",
                        formula = "P(A and B) = P(A) x P(B)",
                    ),
                    Numeric(
                        question = "Two fair coins are tossed. What is the probability of two heads, as a decimal?",
                        answer = "0.25",
                        explanation = "0.5 x 0.5 = 0.25.",
                    ),
                    Choice(
                        question = "Drawing two cards without replacing the first makes the second draw...",
                        options = listOf("independent", "dependent on the first", "impossible", "always identical"),
                        correctIndex = 1,
                        explanation = "The first card changes what is left in the deck, so the odds shift.",
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "A test has mean 50 and standard deviation 5. What is the z-score of 60?",
                options = listOf("1", "2", "10", "0.5"),
                correctIndex = 1,
                explanation = "(60 - 50) / 5 = 2.",
            ),
            QuizQuestion(
                prompt = "Roughly what percentage of a normal distribution lies within one standard deviation of the mean?",
                options = listOf("50%", "68%", "95%", "99.7%"),
                correctIndex = 1,
                explanation = "The first step of the 68-95-99.7 rule.",
            ),
            QuizQuestion(
                prompt = "A smaller standard deviation means the data is...",
                options = listOf("higher on average", "more spread out", "more consistent", "smaller in count"),
                correctIndex = 2,
                explanation = "Values sit closer to the mean.",
            ),
            QuizQuestion(
                prompt = "For independent events, P(A and B) equals...",
                options = listOf("P(A) + P(B)", "P(A) x P(B)", "P(A) - P(B)", "P(A) / P(B)"),
                correctIndex = 1,
                explanation = "Independent probabilities multiply.",
            ),
            QuizQuestion(
                prompt = "Two fair dice are rolled. What is the probability of two sixes?",
                options = listOf("1/6", "1/12", "1/36", "2/6"),
                correctIndex = 2,
                explanation = "1/6 x 1/6 = 1/36.",
            ),
            QuizQuestion(
                prompt = "Of 12 French students, 8 also take German. What is P(German given French)?",
                options = listOf("8/20", "8/12", "12/20", "4/12"),
                correctIndex = 1,
                explanation = "The condition restricts the pool to the 12 French students.",
            ),
        ),
    )

    val units: List<LearnUnit> = listOf(functions, trigonometry, calculus, data)
}
